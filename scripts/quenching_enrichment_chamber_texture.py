#!/usr/bin/env python3
"""Generate the Quenching Enrichment Chamber textures from Mekanism's Enrichment Chamber.

The new machine reuses the Enrichment Chamber's block shape, so its textures are derived from
Mekanism's own assets in two steps:

1. Every static face gets a very subtle blue tint. Mekanism's machine textures are essentially
   grayscale, so shifting the red channel down slightly and the blue channel up slightly is
   enough to read as "blue" without leaving the familiar machine look.
2. The active front face is built by taking Mekanism's animated active front, computing the
   per-pixel difference against the (upscaled, tinted) static front, swapping the red and blue
   channels of that difference, and adding it back onto the static front. The Enrichment
   Chamber's active glow is warm (red), so swapping the difference turns it into a cool (blue)
   glow while keeping the surrounding face identical.

Mekanism's active front is 32x32 pixels per frame (higher resolution than the 16x16 static
face), so the static face is nearest-neighbour upscaled before diffing; the output keeps the
original 32x224 layout and frame count, so `front_active.png.mcmeta` is copied unchanged.

Run from the repository root:

    python scripts/quenching_enrichment_chamber_texture.py
"""

from __future__ import annotations

import pathlib
import shutil

from PIL import Image

MEKANISM = pathlib.Path("mek/Mekanism/src/main/resources/assets/mekanism/textures/block/enrichment_chamber")
OUT = pathlib.Path("src/main/resources/assets/mekanismheated/textures/block/quenching_enrichment_chamber")

# Static faces to copy and tint. `front` is also the baseline the active front is derived from.
STATIC_FACES = ("back", "bottom", "front", "left", "right", "top")

# Very subtle blue tint: red down 5%, blue up 8%, green untouched.
RED_SCALE = 0.95
BLUE_SCALE = 1.08


def clamp_channel(value: float) -> int:
    return max(0, min(255, round(value)))


def blue_tint(image: Image.Image) -> Image.Image:
    """Return a copy of `image` with a very subtle blue tint applied to its RGB channels."""
    rgba = image.convert("RGBA")
    tinted = Image.new("RGBA", rgba.size)
    tinted.putdata([
        (clamp_channel(r * RED_SCALE), g, clamp_channel(b * BLUE_SCALE), a)
        for r, g, b, a in rgba.getdata()
    ])
    return tinted


def cool_active_front(active: Image.Image, static_front: Image.Image) -> Image.Image:
    """Turn the warm animated glow of `active` into a blue glow on top of `static_front`.

    Each frame of `active` is diffed against `static_front` (nearest-neighbour upscaled to the
    frame size), the difference's red and blue channels are swapped, and the swapped difference
    is added back onto `static_front`.
    """
    active = active.convert("RGBA")
    frame_width = active.width
    frame_height = frame_width  # square frames, stacked vertically
    frame_count = active.height // frame_height
    baseline = static_front.resize((frame_width, frame_height), Image.NEAREST)

    result = Image.new("RGBA", active.size)
    for index in range(frame_count):
        frame = active.crop((0, index * frame_height, frame_width, (index + 1) * frame_height))
        out = Image.new("RGBA", frame.size)
        out.putdata([
            (
                clamp_channel(br + (fb - bb)),  # swapped-difference red <- original blue difference
                fg,  # the green channel is not swapped
                clamp_channel(bb + (fr - br)),  # swapped-difference blue <- original red difference
                fa,
            )
            for (fr, fg, fb, fa), (br, bg, bb, _) in zip(frame.getdata(), baseline.getdata())
        ])
        result.paste(out, (0, index * frame_height))
    return result


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)

    tinted: dict[str, Image.Image] = {}
    for face in STATIC_FACES:
        tinted[face] = blue_tint(Image.open(MEKANISM / f"{face}.png"))
        tinted[face].save(OUT / f"{face}.png", "PNG")
        print(f"wrote {OUT / f'{face}.png'}")

    active = Image.open(MEKANISM / "front_active.png")
    cool_active_front(active, tinted["front"]).save(OUT / "front_active.png", "PNG")
    print(f"wrote {OUT / 'front_active.png'}")

    # The animation metadata (frame time and frame order) is unchanged.
    shutil.copyfile(MEKANISM / "front_active.png.mcmeta", OUT / "front_active.png.mcmeta")
    print(f"wrote {OUT / 'front_active.png.mcmeta'}")


if __name__ == "__main__":
    main()
