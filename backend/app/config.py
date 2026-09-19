import os
from dotenv import load_dotenv

load_dotenv()


class Settings:
    port: int = int(os.getenv("PORT", "8000"))
    storage_dir: str = os.getenv("STORAGE_DIR", "./storage")

    text_provider: str = os.getenv("TEXT_PROVIDER", "demo")
    text_api_key: str = os.getenv("TEXT_API_KEY", "")

    image_provider: str = os.getenv("IMAGE_PROVIDER", "demo")
    image_api_key: str = os.getenv("IMAGE_API_KEY", "")

    video_provider: str = os.getenv("VIDEO_PROVIDER", "demo")
    video_api_key: str = os.getenv("VIDEO_API_KEY", "")

    tts_provider: str = os.getenv("TTS_PROVIDER", "demo")
    tts_api_key: str = os.getenv("TTS_API_KEY", "")

    audio_provider: str = os.getenv("AUDIO_PROVIDER", "demo")
    audio_api_key: str = os.getenv("AUDIO_API_KEY", "")

    @property
    def demo_mode(self) -> bool:
        return "demo" in (
            self.text_provider,
            self.image_provider,
            self.video_provider,
            self.tts_provider,
            self.audio_provider,
        )


settings = Settings()
os.makedirs(settings.storage_dir, exist_ok=True)
