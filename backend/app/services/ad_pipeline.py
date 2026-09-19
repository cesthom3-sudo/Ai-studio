"""
Automatic advertisement pipeline (the app's primary feature).

Steps: analyze product -> concept -> script -> storyboard -> scene prompts
-> generate visual scenes -> voiceover text -> voiceover audio -> sound
effects -> assemble media -> add captions/CTA -> final advertisement.

Media assembly is delegated to services/media.py (ffmpeg). Each step
reports storyboard progress back through the job so the Android app can
show the storyboard before the final render completes.
"""

from app.models.schemas import AdCreateRequest, StoryboardScene
from app.providers.factory import (
    get_image_provider,
    get_text_provider,
    get_tts_provider,
    get_video_provider,
)
from app.services.media import assemble_advertisement
from app.workers.jobs import job_manager


def _build_storyboard(req: AdCreateRequest) -> list[StoryboardScene]:
    total = req.durationSeconds
    name = req.productName or "the product"
    beats = [
        f"Hero shot of {name}.",
        f"Close-up highlighting {name}'s key feature.",
        f"{name} shown in use.",
        f"Lifestyle scene connecting with {req.audience or 'the target audience'}.",
        f"{name} with call-to-action: \"{req.callToAction}\".",
    ]
    scene_count = len(beats)
    step = max(1, total // scene_count)
    scenes = []
    for i, beat in enumerate(beats):
        start = i * step
        end = total if i == scene_count - 1 else min(total, start + step)
        scenes.append(StoryboardScene(index=i + 1, startSeconds=start, endSeconds=end, description=beat))
    return scenes


async def run_ad_create(job_id: str, req: AdCreateRequest):
    text_provider = get_text_provider()
    image_provider = get_image_provider()
    video_provider = get_video_provider()
    tts_provider = get_tts_provider()

    # 1-2. Analyze product & generate advertising concept
    job_manager.set_progress(job_id, 10)
    concept_prompt = req.freeformPrompt or (
        f"Advertisement concept for {req.productName}, objective: {req.objective}, "
        f"audience: {req.audience}, tone: {req.tone}."
    )
    concept = await text_provider.generate_text(concept_prompt)

    # 3-6. Script, storyboard, scene descriptions, prompts
    storyboard = _build_storyboard(req)
    job_manager.set_progress(job_id, 25, storyboard=storyboard)

    # 7. Generate visual scenes (one image per scene as a practical V1 approach)
    scene_urls = []
    for scene in storyboard:
        urls = await image_provider.generate_image(
            f"{concept}. {scene.description}", req.aspectRatio, "advertisement", 1
        )
        scene_urls.append(urls[0])
    job_manager.set_progress(job_id, 55, storyboard=storyboard)

    # 8-9. Voiceover text + voiceover audio
    voiceover_text = f"{concept} {req.callToAction}"
    voice_url = await tts_provider.synthesize_speech(voiceover_text, "default")
    job_manager.set_progress(job_id, 70, storyboard=storyboard)

    # 10. Sound effects / background audio (best-effort, optional)
    # Skipped by default provider; wire an AIAudioProvider call here if configured.

    # 11-14. Assemble media, captions, transitions, CTA
    job_manager.set_progress(job_id, 85, storyboard=storyboard)
    final_url = await assemble_advertisement(
        scene_urls=scene_urls,
        voiceover_url=voice_url,
        storyboard=storyboard,
        call_to_action=req.callToAction,
        aspect_ratio=req.aspectRatio,
    )

    # 15. Final advertisement ready
    job_manager.complete(job_id, final_url, storyboard=storyboard)
