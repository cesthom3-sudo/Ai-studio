import base64
import httpx

from app.config import settings
from app.providers.base import AIImageProvider


class OpenAIImageProvider(AIImageProvider):
    def __init__(self):
        self.api_key = settings.image_api_key
        self.base_url = "https://api.openai.com/v1"

    async def generate_image(
        self,
        prompt: str,
        aspect_ratio: str,
        style: str,
        variations: int,
    ):
        if not self.api_key:
            raise RuntimeError("IMAGE_API_KEY is not configured.")

        # Map the app's aspect ratio to supported image sizes.
        size = "1536x1024"

        if aspect_ratio in ("1:1", "square"):
            size = "1024x1024"
        elif aspect_ratio in ("16:9", "landscape"):
            size = "1536x1024"
        elif aspect_ratio in ("9:16", "portrait"):
            size = "1024x1536"

        full_prompt = prompt

        if style:
            full_prompt += f"\n\nStyle: {style}"

        async with httpx.AsyncClient(timeout=180) as client:
            response = await client.post(
                f"{self.base_url}/images/generations",
                headers={
                    "Authorization": f"Bearer {self.api_key}",
                    "Content-Type": "application/json",
                },
                json={
                    "model": "gpt-image-2",
                    "prompt": full_prompt,
                    "size": size,
                    "n": max(1, min(variations, 4)),
                },
            )

            response.raise_for_status()
            data = response.json()

        results = []

        for item in data.get("data", []):
            if item.get("b64_json"):
                image_bytes = base64.b64decode(item["b64_json"])

                # Return a data URL so the existing app can display it.
                results.append(
                    "data:image/png;base64,"
                    + base64.b64encode(image_bytes).decode("utf-8")
                )
            elif item.get("url"):
                results.append(item["url"])

        if not results:
            raise RuntimeError("The image API returned no images.")

        return results

    async def edit_image(
        self,
        image_base64: str,
        instruction: str,
    ) -> str:
        raise NotImplementedError(
            "Image editing will be wired separately."
                              )
