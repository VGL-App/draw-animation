# SỐ LIỆU THIẾT KẾ: EditorScreen_drawing_light_unfolded

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-4619
- Frame: `23:4619`, giao diện sáng
- Ngày lấy: 2026-11-22

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| instance ghi đè component | ColorSwatch | màu nền |
| khoảng cách | 1 | ngoài hệ thống |
| khoảng cách | 10 | ngoài hệ thống |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 16 | md |
| khoảng cách | 2 | ngoài hệ thống |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 24 | lg |
| khoảng cách | 4 | xs |
| khoảng cách | 5 | ngoài hệ thống |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 11/16, đậm 500 | labelSmall |
| kiểu chữ | 12/16, đậm 400 | bodySmall |
| kiểu chữ | 12/16, đậm 500 | labelMedium |
| kiểu chữ | 14/20, đậm 600 | labelLarge |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 400 | bodyLarge |
| kiểu chữ | 16/24, đậm 600 | titleMedium |
| kiểu chữ | 18/18, đậm 400 | ngoài hệ thống |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/22, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | onBackground, onSurface |
| màu chữ | #1C2C05 | onPrimaryContainer |
| màu chữ | #2F6FE0 | secondary |
| màu chữ | #5F6675 | onSurfaceVariant |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |
| màu nền | #1A1C22 | onCanvas |
| màu nền | #2F6FE0 | secondary |
| màu nền | #3D6A00 | primary, surfaceTint |
| màu nền | #D9DDE2 | surfaceContainerHighest |
| màu nền | #DDF5B8 | primaryContainer |
| màu nền | #E3E6EA | surfaceVariant, surfaceContainerHigh |
| màu nền | #ECEEF1 | surfaceContainer |
| màu nền | #F1F2F5 | surfaceContainerLow |
| màu nền | #F7F8FA | background, surface, surfaceBright |
| màu nền | #FF5A5F | onionPrevious |
| màu nền | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |
| độ mờ của lớp | 0.45 | - |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "EditorScreen_drawing_light_unfolded", vị trí 0,0, cỡ 673 x 841, nền #F7F8FA (background, surface, surfaceBright), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 673 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
    - Frame "spacer", vị trí 54,14, cỡ 539 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 597,6, cỡ 16 x 16
      - Text "glyph", vị trí 597,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 617,6, cỡ 16 x 16
      - Text "glyph", vị trí 617,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 637,6, cỡ 16 x 16
      - Text "glyph", vị trí 637,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
  - Frame "TopBar", vị trí 0,28, cỡ 673 x 56, xếp ngang, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,44, cỡ 24 x 24
        - Text "glyph", vị trí 16,44, cỡ 24 x 24, chữ "arrow_back", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Frame "Title", vị trí 54,36, cỡ 388 x 40, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Bouncing ball", vị trí 54,36, cỡ 105 x 24, chữ "Bouncing ball", kiểu titleMedium (16/24, đậm 600), màu #12141A (onBackground, onSurface)
      - Text "Frame 12 / 48 · saved", vị trí 54,60, cỡ 122 x 16, chữ "Frame 12 / 48 · saved", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
    - Instance "IconButton", vị trí 444,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 456,44, cỡ 24 x 24
        - Text "glyph", vị trí 456,44, cỡ 24 x 24, chữ "undo", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "IconButton", vị trí 494,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 506,44, cỡ 24 x 24
        - Text "glyph", vị trí 506,44, cỡ 24 x 24, chữ "redo", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Button", vị trí 544,32, cỡ 117 x 48, nền #3D6A00 (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/24 (lg)/8 (sm)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 564,46, cỡ 20 x 20
        - Text "glyph", vị trí 564,46, cỡ 20 x 20, chữ "ios_share", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Text "Label", vị trí 592,46, cỡ 45 x 20, chữ "Export", kiểu labelLarge (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
  - Frame "editor_top_native", vị trí 0,84, cỡ 673 x 72, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 16 (md)/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn MIN/MIN
    - Instance "NativeAdStrip", vị trí 12,100, cỡ 649 x 56, nền #F1F2F5 (surfaceContainerLow), viền #D2D6DC (outlineVariant), dày 1.0, bo góc 10, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/8 (sm)/0/8 (sm), căn MIN/CENTER
      - Rectangle "Ad media", vị trí 21,108, cỡ 40 x 40, nền #D9DDE2 (surfaceContainerHighest), bo góc 8
      - Frame "Text", vị trí 71,110, cỡ 514 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Frame "Headline row", vị trí 71,110, cỡ 514 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
          - Frame "Ad badge", vị trí 71,111, cỡ 25 x 18, nền #D9DDE2 (surfaceContainerHighest), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 1 (ngoài hệ thống)/5 (ngoài hệ thống)/1 (ngoài hệ thống)/5 (ngoài hệ thống), căn MIN/MIN
            - Text "Ad", vị trí 76,112, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (onSurfaceVariant)
          - Text "Headline", vị trí 102,110, cỡ 483 x 20, chữ "Sponsored app name", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
        - Text "Body", vị trí 71,130, cỡ 514 x 16, chữ "Native ad, editor top strip", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
      - Frame "Ad CTA", vị trí 595,112, cỡ 57 x 32, viền #2F6FE0 (secondary), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Open", vị trí 608,120, cỡ 31 x 16, chữ "Open", kiểu labelMedium (12/16, đậm 500), màu #2F6FE0 (secondary)
  - Frame "Middle", vị trí 0,156, cỡ 673 x 545, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Frame "Toolbar", vị trí 0,156, cỡ 60 x 545, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 16 (md)/8 (sm)/0/8 (sm), căn MIN/MIN
      - Instance "ToolButton", vị trí 8,172, cỡ 44 x 44, nền #3D6A00 (primary, surfaceTint), bo góc 8
        - Instance "Glyph", vị trí 18,182, cỡ 24 x 24
          - Text "glyph", vị trí 18,182, cỡ 24 x 24, chữ "brush", kiểu ngoài hệ thống (24/24, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Instance "ToolButton", vị trí 8,220, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 18,230, cỡ 24 x 24
          - Text "glyph", vị trí 18,230, cỡ 24 x 24, chữ "ink_eraser", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 8,268, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 18,278, cỡ 24 x 24
          - Text "glyph", vị trí 18,278, cỡ 24 x 24, chữ "format_color_fill", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 8,316, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 18,326, cỡ 24 x 24
          - Text "glyph", vị trí 18,326, cỡ 24 x 24, chữ "lasso_select", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 8,364, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 18,374, cỡ 24 x 24
          - Text "glyph", vị trí 18,374, cỡ 24 x 24, chữ "shapes", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 8,412, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 18,422, cỡ 24 x 24
          - Text "glyph", vị trí 18,422, cỡ 24 x 24, chữ "title", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 8,460, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 18,470, cỡ 24 x 24
          - Text "glyph", vị trí 18,470, cỡ 24 x 24, chữ "videocam", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 8,508, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 18,518, cỡ 24 x 24
          - Text "glyph", vị trí 18,518, cỡ 24 x 24, chữ "more_horiz", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Frame "Workspace", vị trí 60,156, cỡ 373 x 545, nền #F7F8FA (background, surface, surfaceBright)
      - Frame "Canvas 9:16", vị trí 102,172, cỡ 289 x 513, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
        - Rectangle "Ground stroke", vị trí 128,639, cỡ 269 x 3, nền #1A1C22 (onCanvas)
        - Ellipse "Onion previous", vị trí 232,445, cỡ 60 x 60, viền #FF5A5F (onionPrevious), dày 4.0, độ mờ của lớp 0.45
        - Ellipse "Onion next", vị trí 232,331, cỡ 60 x 60, viền #3D8BFF (onionNext), dày 4.0, độ mờ của lớp 0.45
        - Ellipse "Current frame stroke", vị trí 232,388, cỡ 60 x 60, viền #1A1C22 (onCanvas), dày 4.0
    - Frame "SidePanel", vị trí 433,156, cỡ 240 x 545, nền #ECEEF1 (surfaceContainer), xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 16 (md)/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn MIN/MIN
      - Frame "Layers header", vị trí 445,172, cỡ 216 x 48, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Text "Layers", vị trí 445,184, cỡ 52 x 24, chữ "Layers", kiểu titleMedium (16/24, đậm 600), màu #12141A (onBackground, onSurface)
        - Frame "spacer", vị trí 497,196, cỡ 116 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Instance "IconButton", vị trí 613,172, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
          - Instance "Glyph", vị trí 625,184, cỡ 24 x 24
            - Text "glyph", vị trí 625,184, cỡ 24 x 24, chữ "add", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "LayerRow", vị trí 445,232, cỡ 216 x 56, nền #DDF5B8 (primaryContainer), bo góc 8, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/4 (xs)/0/12 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Icon", vị trí 457,250, cỡ 20 x 20
          - Text "glyph", vị trí 457,250, cỡ 20 x 20, chữ "drag_indicator", kiểu ngoài hệ thống (20/20, đậm 400), màu #5F6675 (onSurfaceVariant)
        - Frame "Thumbnail", vị trí 489,240, cỡ 40 x 40, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
          - Rectangle "ground", vị trí 493,271, cỡ 32 x 1, nền #1A1C22 (onCanvas)
          - Ellipse "ball", vị trí 503,262, cỡ 9 x 9, nền #2F6FE0 (secondary)
        - Text "Name", vị trí 541,248, cỡ 56 x 24, chữ "Ball", kiểu bodyLarge (16/24, đậm 400), màu #1C2C05 (onPrimaryContainer)
        - Frame "Visibility button", vị trí 609,236, cỡ 48 x 48, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
          - Instance "Visibility", vị trí 622,249, cỡ 22 x 22
            - Text "glyph", vị trí 622,249, cỡ 22 x 22, chữ "visibility", kiểu ngoài hệ thống (22/22, đậm 400), màu #5F6675 (onSurfaceVariant)
      - Instance "LayerRow", vị trí 445,300, cỡ 216 x 56, bo góc 8, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/4 (xs)/0/12 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Icon", vị trí 457,318, cỡ 20 x 20
          - Text "glyph", vị trí 457,318, cỡ 20 x 20, chữ "drag_indicator", kiểu ngoài hệ thống (20/20, đậm 400), màu #5F6675 (onSurfaceVariant)
        - Frame "Thumbnail", vị trí 489,308, cỡ 40 x 40, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
          - Rectangle "ground", vị trí 493,339, cỡ 32 x 1, nền #1A1C22 (onCanvas)
          - Ellipse "ball", vị trí 503,330, cỡ 9 x 9, nền #2F6FE0 (secondary)
        - Text "Name", vị trí 541,316, cỡ 56 x 24, chữ "Shadow", kiểu bodyLarge (16/24, đậm 400), màu #12141A (onBackground, onSurface)
        - Frame "Visibility button", vị trí 609,304, cỡ 48 x 48, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
          - Instance "Visibility", vị trí 622,317, cỡ 22 x 22
            - Text "glyph", vị trí 622,317, cỡ 22 x 22, chữ "visibility", kiểu ngoài hệ thống (22/22, đậm 400), màu #5F6675 (onSurfaceVariant)
      - Instance "LayerRow", vị trí 445,368, cỡ 216 x 56, bo góc 8, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/4 (xs)/0/12 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Icon", vị trí 457,386, cỡ 20 x 20
          - Text "glyph", vị trí 457,386, cỡ 20 x 20, chữ "drag_indicator", kiểu ngoài hệ thống (20/20, đậm 400), màu #5F6675 (onSurfaceVariant)
        - Frame "Thumbnail", vị trí 489,376, cỡ 40 x 40, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
          - Rectangle "ground", vị trí 493,407, cỡ 32 x 1, nền #1A1C22 (onCanvas)
          - Ellipse "ball", vị trí 503,398, cỡ 9 x 9, nền #2F6FE0 (secondary)
        - Text "Name", vị trí 541,384, cỡ 56 x 24, chữ "Background", kiểu bodyLarge (16/24, đậm 400), màu #12141A (onBackground, onSurface)
        - Frame "Visibility button", vị trí 609,372, cỡ 48 x 48, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
          - Instance "Visibility", vị trí 622,385, cỡ 22 x 22
            - Text "glyph", vị trí 622,385, cỡ 22 x 22, chữ "visibility", kiểu ngoài hệ thống (22/22, đậm 400), màu #5F6675 (onSurfaceVariant)
      - Text "Brush", vị trí 445,436, cỡ 46 x 24, chữ "Brush", kiểu titleMedium (16/24, đậm 600), màu #12141A (onBackground, onSurface)
      - Instance "Slider", vị trí 445,472, cỡ 216 x 48, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Frame "Header", vị trí 445,472, cỡ 216 x 20, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Label", vị trí 445,472, cỡ 29 x 20, chữ "Size", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
          - Frame "spacer", vị trí 474,472, cỡ 158 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Value", vị trí 632,472, cỡ 29 x 20, chữ "8 px", kiểu labelLarge (14/20, đậm 600), màu #5F6675 (onSurfaceVariant)
        - Frame "Track", vị trí 445,500, cỡ 216 x 20
          - Rectangle "Rectangle", vị trí 445,508, cỡ 216 x 4, nền #D9DDE2 (surfaceContainerHighest), bo góc 2
          - Rectangle "Rectangle", vị trí 445,508, cỡ 110 x 4, nền #3D6A00 (primary, surfaceTint), bo góc 2
          - Ellipse "Thumb", vị trí 545,500, cỡ 20 x 20, nền #3D6A00 (primary, surfaceTint)
      - Instance "Slider", vị trí 445,532, cỡ 216 x 48, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Frame "Header", vị trí 445,532, cỡ 216 x 20, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Label", vị trí 445,532, cỡ 53 x 20, chữ "Opacity", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
          - Frame "spacer", vị trí 498,532, cỡ 125 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Value", vị trí 623,532, cỡ 38 x 20, chữ "100%", kiểu labelLarge (14/20, đậm 600), màu #5F6675 (onSurfaceVariant)
        - Frame "Track", vị trí 445,560, cỡ 216 x 20
          - Rectangle "Rectangle", vị trí 445,568, cỡ 216 x 4, nền #D9DDE2 (surfaceContainerHighest), bo góc 2
          - Rectangle "Rectangle", vị trí 445,568, cỡ 110 x 4, nền #3D6A00 (primary, surfaceTint), bo góc 2
          - Ellipse "Thumb", vị trí 545,560, cỡ 20 x 20, nền #3D6A00 (primary, surfaceTint)
      - Frame "Colors", vị trí 445,592, cỡ 162 x 36, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Instance "ColorSwatch", vị trí 445,592, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
          - Ellipse "Color", vị trí 449,596, cỡ 28 x 28, nền #2F6FE0 (secondary), viền #D2D6DC (outlineVariant), dày 1.0
          - Ellipse "Ring", vị trí 445,592, cỡ 36 x 36, viền #12141A (onBackground, onSurface), dày 2.0
        - Instance "ColorSwatch", vị trí 487,592, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
          - Ellipse "Color", vị trí 489,594, cỡ 32 x 32, nền #1A1C22 (onCanvas), viền #D2D6DC (outlineVariant), dày 1.0
        - Instance "ColorSwatch", vị trí 529,592, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
          - Ellipse "Color", vị trí 531,594, cỡ 32 x 32, nền #FF5A5F (onionPrevious), viền #D2D6DC (outlineVariant), dày 1.0
        - Instance "ColorSwatch", vị trí 571,592, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
          - Ellipse "Color", vị trí 573,594, cỡ 32 x 32, nền #3D6A00 (primary, surfaceTint), viền #D2D6DC (outlineVariant), dày 1.0
  - Frame "Timeline", vị trí 0,701, cỡ 673 x 140, nền #ECEEF1 (surfaceContainer), xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 4 (xs)/0/16 (md)/0, căn MIN/MIN
    - Frame "Controls", vị trí 0,705, cỡ 673 x 52, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn MIN/CENTER
      - Instance "Chip", vị trí 12,716, cỡ 108 x 30, nền #DDF5B8 (primaryContainer), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Leading icon", vị trí 24,722, cỡ 18 x 18
          - Text "glyph", vị trí 24,722, cỡ 18 x 18, chữ "animation", kiểu ngoài hệ thống (18/18, đậm 400), màu #1C2C05 (onPrimaryContainer)
        - Text "Label", vị trí 48,723, cỡ 60 x 16, chữ "Onion 2 · 1", kiểu labelMedium (12/16, đậm 500), màu #1C2C05 (onPrimaryContainer)
      - Text "12 / 48", vị trí 128,723, cỡ 40 x 16, chữ "12 / 48", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (onSurfaceVariant)
      - Frame "spacer", vị trí 176,730, cỡ 149 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "IconButton", vị trí 333,707, cỡ 48 x 48, nền #3D6A00 (primary, surfaceTint), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 345,719, cỡ 24 x 24
          - Text "glyph", vị trí 345,719, cỡ 24 x 24, chữ "play_arrow", kiểu ngoài hệ thống (24/24, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Frame "spacer", vị trí 389,730, cỡ 149 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "Chip", vị trí 546,717, cỡ 59 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Text "Label", vị trí 558,723, cỡ 35 x 16, chữ "12 fps", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
      - Instance "IconButton", vị trí 613,707, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 625,719, cỡ 24 x 24
          - Text "glyph", vị trí 625,719, cỡ 24 x 24, chữ "library_add", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Frame "Frames", vị trí 0,761, cỡ 673 x 64, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn MIN/MIN
      - Instance "FrameThumb", vị trí 12,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 16,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 23,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 54,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 58,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 65,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 96,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 100,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 107,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 138,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), viền #3D6A00 (primary, surfaceTint), dày 2.0, bo góc 8
        - Rectangle "ground", vị trí 142,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 149,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 180,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 184,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 191,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 222,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 226,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 233,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 264,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 268,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 275,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 306,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 310,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 317,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 348,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 352,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 359,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 390,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 394,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 401,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 432,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 436,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 443,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 474,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 478,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 485,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 516,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 520,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 527,803, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 558,761, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 562,811, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 569,803, cỡ 8 x 8, nền #2F6FE0 (secondary)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
