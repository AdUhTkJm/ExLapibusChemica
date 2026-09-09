#!/usr/bin/env python3
"""Generate the creative chunk heater texture from the creative heat block texture.

Shifts the hue of the orange parts to red so the two creative blocks are visually
distinguishable. Run from the repository root:

    python scripts/creative_chunk_heater_texture.py
"""

from __future__ import annotations

import colorsys

from PIL import Image

PREFIX = "src/main/resources/assets/mekanismheated/textures/block"
SRC = f"{PREFIX}/creative_heat_block.png"
DST = f"{PREFIX}/creative_chunk_heater.png"

# Hue range (in degrees) considered "orange"
ORANGE_MIN, ORANGE_MAX = 0, 60
# Orange (~24 degrees) maps to red (~4 degrees)
HUE_SCALE = 0.2


def shift_orange_to_red(r: int, g: int, b: int, a: int) -> tuple[int, int, int, int]:
    h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
    hue_deg = h * 360
    if s > 0.2 and ORANGE_MIN < hue_deg < ORANGE_MAX:
        h = hue_deg * HUE_SCALE / 360
        r, g, b = (round(c * 255) for c in colorsys.hsv_to_rgb(h, s, v))
    return r, g, b, a


def main() -> None:
    img = Image.open(SRC).convert("RGBA")
    out = Image.new("RGBA", img.size)
    out.putdata([shift_orange_to_red(*px) for px in img.getdata()])
    out.save(DST, "PNG")
    print(f"wrote {DST}")


if __name__ == "__main__":
    main()
