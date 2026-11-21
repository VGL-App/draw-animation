# SỐ LIỆU THIẾT KẾ: ExportScreen_options_dark

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=10-626
- Frame: `10:626`, giao diện tối
- Ngày lấy: 2026-11-22

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| instance ghi đè component | Icon | màu nền, viền |
| khoảng cách | 1 | ngoài hệ thống |
| khoảng cách | 10 | ngoài hệ thống |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 16 | md |
| khoảng cách | 2 | ngoài hệ thống |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 24 | lg |
| khoảng cách | 4 | xs |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 11/16, đậm 500 | labelSmall |
| kiểu chữ | 12/16, đậm 400 | bodySmall |
| kiểu chữ | 12/16, đậm 500 | labelMedium |
| kiểu chữ | 14/20, đậm 400 | bodyMedium |
| kiểu chữ | 14/20, đậm 500 | titleSmall |
| kiểu chữ | 14/20, đậm 600 | labelLarge |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 600 | titleMedium |
| kiểu chữ | 2/2, đậm 400 | ngoài hệ thống |
| kiểu chữ | 2/2, đậm 600 | ngoài hệ thống |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | titleLarge |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| màu chữ | #10140A | onPrimary |
| màu chữ | #8C93A3 | onSurfaceVariant |
| màu chữ | #B6F35C | primary, surfaceTint |
| màu chữ | #D8FA9E | onPrimaryContainer |
| màu chữ | #EEF0F4 | onBackground, onSurface, inverseSurface |
| màu chữ | #FFFFFF | ngoài hệ thống |
| màu nền | #000000 (độ mờ 0.35, lớp tô 0.35) | ngoài hệ thống |
| màu nền | #0E1014 | background, surface, surfaceDim |
| màu nền | #10140A | onPrimary |
| màu nền | #13161B | surfaceContainerLow |
| màu nền | #171A20 | surfaceContainer |
| màu nền | #1A1C22 | onCanvas |
| màu nền | #22262E | surfaceVariant, surfaceContainerHigh |
| màu nền | #2B303A | surfaceBright, surfaceContainerHighest |
| màu nền | #2E3D17 | primaryContainer |
| màu nền | #6EA8FF | secondary |
| màu nền | #B6F35C | primary, surfaceTint |
| màu nền | #FAFAF7 | canvas |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "ExportScreen_options_dark", vị trí 0,0, cỡ 411 x 891, nền #0E1014 (background, surface, surfaceDim), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu labelLarge (14/20, đậm 600), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,48, cỡ 24 x 24
        - Text "glyph", vị trí 16,48, cỡ 24 x 24, chữ "close", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Text "Export", vị trí 56,46, cỡ 72 x 28, chữ "Export", kiểu titleLarge (22/28, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Frame "spacer", vị trí 132,60, cỡ 271 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Content", vị trí 0,92, cỡ 411 x 703, xếp dọc, cách nhau 16 (md), lề trên/phải/dưới/trái 4 (xs)/16 (md)/0/16 (md), căn MIN/MIN
    - Frame "ProjectSummary", vị trí 16,96, cỡ 379 x 120, nền #171A20 (surfaceContainer), bo góc 14, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Frame "Preview", vị trí 28,108, cỡ 54 x 96, nền #FAFAF7 (canvas), bo góc 8
        - Rectangle "Rectangle", vị trí 32,187, cỡ 45 x 2, nền #1A1C22 (onCanvas)
        - Ellipse "Ellipse", vị trí 49,174, cỡ 13 x 13, nền #6EA8FF (secondary)
        - Frame "PreviewWatermark", vị trí 67,199, cỡ 14 x 4, nền #000000 (độ mờ 0.35, lớp tô 0.35) (ngoài hệ thống), bo góc 1, xếp ngang, cách nhau 1 (ngoài hệ thống), lề trên/phải/dưới/trái 0/1 (ngoài hệ thống)/0/1 (ngoài hệ thống), căn MIN/CENTER
          - Instance "Icon", vị trí 68,199, cỡ 2 x 2, CHÚ Ý: ghi đè màu nền, viền của component gốc
            - Text "glyph", vị trí 68,199, cỡ 2 x 2, chữ "animation", kiểu ngoài hệ thống (2/2, đậm 400), màu #FFFFFF (ngoài hệ thống)
          - Text "Frameloop", vị trí 70,199, cỡ 9 x 3, chữ "Frameloop", kiểu ngoài hệ thống (2/2, đậm 600), màu #FFFFFF (ngoài hệ thống)
      - Frame "Text", vị trí 98,124, cỡ 285 x 64, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Text "Bouncing ball", vị trí 98,124, cỡ 105 x 24, chữ "Bouncing ball", kiểu titleMedium (16/24, đậm 600), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
        - Text "48 frames · 12 fps · 4.0", vị trí 98,150, cỡ 163 x 20, chữ "48 frames · 12 fps · 4.0 s", kiểu bodyMedium (14/20, đậm 400), màu #8C93A3 (onSurfaceVariant)
        - Text "9:16 · 1080 × 1920", vị trí 98,172, cỡ 105 x 16, chữ "9:16 · 1080 × 1920", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (onSurfaceVariant)
    - Frame "export_options_mrec", vị trí 16,232, cỡ 379 x 250, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/MIN
      - Instance "MrecSlot", vị trí 56,232, cỡ 300 x 250, nền #13161B (surfaceContainerLow), viền #2C313B (outlineVariant), dày 1.0, bo góc 4
        - Text "Label", vị trí 134,349, cỡ 142 x 16, chữ "Banner MREC 300 × 250", kiểu labelMedium (12/16, đậm 500), màu #8C93A3 (onSurfaceVariant)
        - Frame "Ad badge", vị trí 62,238, cỡ 27 x 20, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
          - Text "Ad", vị trí 68,240, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #8C93A3 (onSurfaceVariant)
    - Text "Format", vị trí 16,498, cỡ 48 x 20, chữ "Format", kiểu titleSmall (14/20, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Frame "Formats", vị trí 16,534, cỡ 379 x 282, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "FormatOption", vị trí 16,534, cỡ 379 x 66, nền #171A20 (surfaceContainer), viền #B6F35C (primary, surfaceTint), dày 2.0, bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/16 (md)/12 (ngoài hệ thống)/16 (md), căn MIN/CENTER
        - Instance "Icon", vị trí 34,555, cỡ 24 x 24
          - Text "glyph", vị trí 34,555, cỡ 24 x 24, chữ "radio_button_checked", kiểu ngoài hệ thống (24/24, đậm 400), màu #B6F35C (primary, surfaceTint)
        - Frame "Text", vị trí 70,548, cỡ 307 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Title", vị trí 70,548, cỡ 72 x 20, chữ "MP4 video", kiểu titleSmall (14/20, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
          - Text "Description", vị trí 70,570, cỡ 174 x 16, chữ "With sound · plays everywhere", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (onSurfaceVariant)
      - Instance "FormatOption", vị trí 16,608, cỡ 379 x 64, nền #171A20 (surfaceContainer), viền #2C313B (outlineVariant), dày 1.0, bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/16 (md)/12 (ngoài hệ thống)/16 (md), căn MIN/CENTER
        - Instance "Icon", vị trí 33,628, cỡ 24 x 24
          - Text "glyph", vị trí 33,628, cỡ 24 x 24, chữ "radio_button_unchecked", kiểu ngoài hệ thống (24/24, đậm 400), màu #8C93A3 (onSurfaceVariant)
        - Frame "Text", vị trí 69,621, cỡ 309 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Title", vị trí 69,621, cỡ 23 x 20, chữ "GIF", kiểu titleSmall (14/20, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
          - Text "Description", vị trí 69,643, cỡ 187 x 16, chữ "Loops, no sound · good for chats", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (onSurfaceVariant)
      - Instance "FormatOption", vị trí 16,680, cỡ 379 x 64, nền #171A20 (surfaceContainer), viền #2C313B (outlineVariant), dày 1.0, bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/16 (md)/12 (ngoài hệ thống)/16 (md), căn MIN/CENTER
        - Instance "Icon", vị trí 33,700, cỡ 24 x 24
          - Text "glyph", vị trí 33,700, cỡ 24 x 24, chữ "radio_button_unchecked", kiểu ngoài hệ thống (24/24, đậm 400), màu #8C93A3 (onSurfaceVariant)
        - Frame "Text", vị trí 69,693, cỡ 243 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Title", vị trí 69,693, cỡ 100 x 20, chữ "PNG sequence", kiểu titleSmall (14/20, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
          - Text "Description", vị trí 69,715, cỡ 219 x 16, chữ "One image per frame · for editing apps", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (onSurfaceVariant)
        - Frame "Reward badge", vị trí 324,700, cỡ 54 x 24, nền #22262E (surfaceVariant, surfaceContainerHigh), bo góc 999, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 4 (xs)/10 (ngoài hệ thống)/4 (xs)/8 (sm), căn MIN/CENTER
          - Instance "Icon", vị trí 332,704, cỡ 16 x 16
            - Text "glyph", vị trí 332,704, cỡ 16 x 16, chữ "smart_display", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
          - Text "Ad", vị trí 352,704, cỡ 16 x 16, chữ "Ad", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
      - Instance "FormatOption", vị trí 16,752, cỡ 379 x 64, nền #171A20 (surfaceContainer), viền #2C313B (outlineVariant), dày 1.0, bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/16 (md)/12 (ngoài hệ thống)/16 (md), căn MIN/CENTER
        - Instance "Icon", vị trí 33,772, cỡ 24 x 24
          - Text "glyph", vị trí 33,772, cỡ 24 x 24, chữ "radio_button_unchecked", kiểu ngoài hệ thống (24/24, đậm 400), màu #8C93A3 (onSurfaceVariant)
        - Frame "Text", vị trí 69,765, cỡ 243 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Title", vị trí 69,765, cỡ 108 x 20, chữ "Transparent GIF", kiểu titleSmall (14/20, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
          - Text "Description", vị trí 69,787, cỡ 142 x 16, chữ "No background · stickers", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (onSurfaceVariant)
        - Frame "Reward badge", vị trí 324,772, cỡ 54 x 24, nền #22262E (surfaceVariant, surfaceContainerHigh), bo góc 999, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 4 (xs)/10 (ngoài hệ thống)/4 (xs)/8 (sm), căn MIN/CENTER
          - Instance "Icon", vị trí 332,776, cỡ 16 x 16
            - Text "glyph", vị trí 332,776, cỡ 16 x 16, chữ "smart_display", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
          - Text "Ad", vị trí 352,776, cỡ 16 x 16, chữ "Ad", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Text "Quality", vị trí 16,832, cỡ 48 x 20, chữ "Quality", kiểu titleSmall (14/20, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Frame "Quality", vị trí 16,868, cỡ 123 x 28, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "Chip", vị trí 16,868, cỡ 54 x 28, nền #22262E (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Text "Label", vị trí 28,874, cỡ 30 x 16, chữ "720p", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
      - Instance "Chip", vị trí 78,868, cỡ 61 x 28, nền #2E3D17 (primaryContainer), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Text "Label", vị trí 90,874, cỡ 37 x 16, chữ "1080p", kiểu labelMedium (12/16, đậm 500), màu #D8FA9E (onPrimaryContainer)
    - Frame "Watermark", vị trí 16,912, cỡ 379 x 58, nền #171A20 (surfaceContainer), bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/12 (ngoài hệ thống)/10 (ngoài hệ thống)/16 (md), căn MIN/CENTER
      - Frame "Text", vị trí 32,922, cỡ 287 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Text "Frameloop watermark", vị trí 32,922, cỡ 147 x 20, chữ "Frameloop watermark", kiểu titleSmall (14/20, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
        - Text "Turn off by watching one", vị trí 32,944, cỡ 287 x 16, chữ "Turn off by watching one ad", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (onSurfaceVariant)
      - Instance "Switch", vị trí 331,925, cỡ 52 x 32, nền #B6F35C (primary, surfaceTint), bo góc 999
        - Ellipse "Knob", vị trí 355,929, cỡ 24 x 24, nền #10140A (onPrimary)
    - Frame "spacer", vị trí 16,986, cỡ 1 x 1, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Footer", vị trí 0,795, cỡ 411 x 96, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 24 (lg)/16 (md)/24 (lg)/16 (md), căn MIN/MIN
    - Instance "Button", vị trí 16,819, cỡ 379 x 48, nền #B6F35C (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 150,833, cỡ 20 x 20
        - Text "glyph", vị trí 150,833, cỡ 20 x 20, chữ "ios_share", kiểu ngoài hệ thống (20/20, đậm 400), màu #10140A (onPrimary)
      - Text "Label", vị trí 178,833, cỡ 80 x 20, chữ "Export MP4", kiểu labelLarge (14/20, đậm 600), màu #10140A (onPrimary)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
