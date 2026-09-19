# Thomas AI Studio

AI creative studio for Android — generate images, edit photos, turn images into video, and automatically produce advertisements. Created by **Thomas T. Chingwaru**.

## Architecture

```
Android app (Kotlin/Compose) → Internet → FastAPI backend → AI providers → generated media → back to app
```

- `android/` — Kotlin + Jetpack Compose app (`com.thomas.aistudio`)
- `backend/` — Python FastAPI backend with a provider abstraction (text/image/video/audio/TTS)
- `.github/workflows/android-build.yml` — builds the debug APK automatically on GitHub Actions

The app ships in **DEMO MODE** until you configure real AI provider keys — it will not claim demo output is real AI generation.

## Backend setup

```bash
cd backend
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env   # fill in provider keys, or leave as demo
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

### Configuring AI providers

Edit `backend/.env`. Each capability has its own provider + key:

```
IMAGE_PROVIDER=demo   # change to a real provider name once implemented
IMAGE_API_KEY=...
```

To wire a real provider, implement the matching interface in `backend/app/providers/base.py`
(see `backend/app/providers/example_http_provider.py` for a template) and register it in
`backend/app/providers/factory.py`. Never commit real keys — `.env` is gitignored.

## Connecting the Android app

1. Deploy the backend somewhere internet-accessible (Render, Railway, Fly.io, a VPS, etc.), or run it locally and use a tunnel (e.g. ngrok) for testing on a device.
2. Open the app → **Settings** → set **Backend URL** → **Save** → **Test connection**.

## Building the Android app

**Locally:**
```bash
cd android
./gradlew assembleDebug   # or: gradle assembleDebug if no wrapper jar is present
```
APK output: `android/app/build/outputs/apk/debug/app-debug.apk`

**Via GitHub Actions (recommended):**
1. Push this project to your GitHub repository.
2. The `android-build.yml` workflow runs automatically and uploads the built APK as a workflow artifact — no secrets required for a debug build.

## Uploading to GitHub

```bash
git init
git add .
git commit -m "Thomas AI Studio"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

GitHub Actions will pick up the workflow automatically. Check the **Actions** tab for the build and download the APK from the run's artifacts.

## Core features

- **Home** — dashboard with quick actions and recent projects
- **Image Generator** — text-to-image with style/aspect-ratio presets
- **Image Editor** — natural-language photo editing (before/after)
- **Image to Video** — animate a still image with a motion prompt
- **Text to Video** — describe a video, generate it
- **Ad Studio** (primary feature) — upload a product image, describe the ad, and the pipeline generates a concept, storyboard, scenes, voiceover, and a final assembled advertisement in 9:16, 1:1, or 16:9
- **Projects** — save, rename, delete, and revisit past creations
- No artificial coins/credits/limits in the app itself — only whatever limits your chosen AI providers impose

## Limitations (V1)

- Job queue is in-memory (per backend process) — fine for a single-instance deployment/demo, not for horizontal scaling
- Media assembly (`backend/app/services/media.py`) includes an FFmpeg integration point but ships a simplified fallback when FFmpeg isn't available on the host
- Only demo providers are wired by default; real AI vendors must be implemented per `example_http_provider.py`
- Project storage is a local JSON file, suitable for a single backend instance
