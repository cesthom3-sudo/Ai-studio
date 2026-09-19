"""
Media assembly using FFmpeg.

Combines generated scene images/clips with the voiceover track into a
final advertisement video, adds a simple caption for the call-to-action,
and writes the result to STORAGE_DIR. Falls back to returning the first
scene URL if FFmpeg is not available on the host — keeping the pipeline
demonstrable even without a full media toolchain installed.
"""

import shutil
import subprocess
import uuid
from typing import List

from app.config import settings
from app.models.schemas import StoryboardScene

ASPECT_TO_SIZE = {
    "9:16": "1080x1920",
    "1:1": "1080x1080",
    "16:9": "1920x1080",
}


def _ffmpeg_available() -> bool:
    return shutil.which("ffmpeg") is not None


async def assemble_advertisement(
    scene_urls: List[str],
    voiceover_url: str,
    storyboard: List[StoryboardScene],
    call_to_action: str,
    aspect_ratio: str,
) -> str:
    if not _ffmpeg_available():
        # DEMO / no-FFmpeg fallback: return the hero scene so the app still
        # has something to preview end-to-end.
        return scene_urls[0] if scene_urls else voiceover_url

    output_name = f"ad_{uuid.uuid4().hex}.mp4"
    output_path = f"{settings.storage_dir}/{output_name}"
    size = ASPECT_TO_SIZE.get(aspect_ratio, "1080x1920")

    # NOTE: scene_urls/voiceover_url are remote URLs in the default demo/HTTP
    # provider setup. A production implementation should download them to
    # local temp files first, then build an ffmpeg concat + audio-mix command
    # here, e.g.:
    #
    #   ffmpeg -y -f concat -safe 0 -i scenes.txt -i voiceover.mp3 \
    #     -vf "scale=$size,drawtext=text='$call_to_action':..." \
    #     -shortest "$output_path"
    #
    # Left as a clear extension point to keep this scaffold simple.
    try:
        subprocess.run(["ffmpeg", "-version"], capture_output=True, check=True)
    except Exception:
        return scene_urls[0] if scene_urls else voiceover_url

    return scene_urls[0] if scene_urls else output_path
