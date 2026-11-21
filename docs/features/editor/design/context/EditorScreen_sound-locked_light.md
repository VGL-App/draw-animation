# SỐ LIỆU THIẾT KẾ: EditorScreen_sound-locked_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-4806
- Frame: `23:4806`, giao diện sáng
- Ngày lấy: 2026-11-22

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| instance ghi đè component | ColorSwatch | màu nền |
| instance ghi đè component | Icon | màu nền |
| khoảng cách | 1 | ngoài hệ thống |
| khoảng cách | 10 | ngoài hệ thống |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 14 | ngoài hệ thống |
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
| kiểu chữ | 14/20, đậm 500 | titleSmall |
| kiểu chữ | 14/20, đậm 600 | labelLarge |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 600 | titleMedium |
| kiểu chữ | 18/18, đậm 400 | ngoài hệ thống |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/22, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | titleLarge |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | onBackground, onSurface |
| màu chữ | #1C2C05 | onPrimaryContainer |
| màu chữ | #2F6FE0 | secondary |
| màu chữ | #5F6675 | onSurfaceVariant |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |
| màu nền | #12141A | onBackground, onSurface |
| màu nền | #1A1C22 | onCanvas |
| màu nền | #2F6FE0 | secondary |
| màu nền | #3D6A00 | primary, surfaceTint |
| màu nền | #5F6675 | onSurfaceVariant |
| màu nền | #B9BEC8 | outline |
| màu nền | #D9DDE2 | surfaceContainerHighest |
| màu nền | #DDF5B8 | primaryContainer |
| màu nền | #E3E6EA | surfaceVariant, surfaceContainerHigh |
| màu nền | #ECEEF1 | surfaceContainer |
| màu nền | #F1F2F5 | surfaceContainerLow |
| màu nền | #F7F8FA | background, surface, surfaceBright |
| màu nền | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |
| độ mờ của lớp | 0.45 | - |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "EditorScreen_sound-locked_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (background, surface, surfaceBright), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 56, xếp ngang, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,44, cỡ 24 x 24
        - Text "glyph", vị trí 16,44, cỡ 24 x 24, chữ "arrow_back", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Frame "Title", vị trí 54,36, cỡ 144 x 40, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Bouncing ball", vị trí 54,36, cỡ 144 x 24, chữ "Bouncing ball", kiểu titleMedium (16/24, đậm 600), màu #12141A (onBackground, onSurface)
      - Text "Frame 12 / 48 · saved", vị trí 54,60, cỡ 144 x 16, chữ "Frame 12 / 48 · saved", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
    - Instance "IconButton", vị trí 200,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 212,44, cỡ 24 x 24
        - Text "glyph", vị trí 212,44, cỡ 24 x 24, chữ "undo", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "IconButton", vị trí 250,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 262,44, cỡ 24 x 24
        - Text "glyph", vị trí 262,44, cỡ 24 x 24, chữ "redo", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Button", vị trí 300,32, cỡ 99 x 48, nền #3D6A00 (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/14 (ngoài hệ thống)/8 (sm)/12 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 312,46, cỡ 20 x 20
        - Text "glyph", vị trí 312,46, cỡ 20 x 20, chữ "ios_share", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Text "Label", vị trí 340,46, cỡ 45 x 20, chữ "Export", kiểu labelLarge (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
  - Frame "editor_top_native", vị trí 0,84, cỡ 411 x 72, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 16 (md)/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn MIN/MIN
    - Instance "NativeAdStrip", vị trí 12,100, cỡ 387 x 56, nền #F1F2F5 (surfaceContainerLow), viền #D2D6DC (outlineVariant), dày 1.0, bo góc 10, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/8 (sm)/0/8 (sm), căn MIN/CENTER
      - Rectangle "Ad media", vị trí 21,108, cỡ 40 x 40, nền #D9DDE2 (surfaceContainerHighest), bo góc 8
      - Frame "Text", vị trí 71,110, cỡ 252 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Frame "Headline row", vị trí 71,110, cỡ 252 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
          - Frame "Ad badge", vị trí 71,111, cỡ 25 x 18, nền #D9DDE2 (surfaceContainerHighest), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 1 (ngoài hệ thống)/5 (ngoài hệ thống)/1 (ngoài hệ thống)/5 (ngoài hệ thống), căn MIN/MIN
            - Text "Ad", vị trí 76,112, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (onSurfaceVariant)
          - Text "Headline", vị trí 102,110, cỡ 221 x 20, chữ "Sponsored app name", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
        - Text "Body", vị trí 71,130, cỡ 252 x 16, chữ "Native ad, editor top strip", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
      - Frame "Ad CTA", vị trí 333,112, cỡ 57 x 32, viền #2F6FE0 (secondary), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Open", vị trí 346,120, cỡ 31 x 16, chữ "Open", kiểu labelMedium (12/16, đậm 500), màu #2F6FE0 (secondary)
  - Frame "Workspace", vị trí 0,156, cỡ 411 x 595, nền #F7F8FA (background, surface, surfaceBright)
    - Frame "Canvas 9:16", vị trí 62,198, cỡ 288 x 512, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Rectangle "Ground stroke", vị trí 85,618, cỡ 242 x 3, nền #1A1C22 (onCanvas)
      - Ellipse "Onion previous", vị trí 176,444, cỡ 60 x 60, viền #FF5A5F (onionPrevious), dày 4.0, độ mờ của lớp 0.45
      - Ellipse "Onion next", vị trí 176,341, cỡ 60 x 60, viền #3D8BFF (onionNext), dày 4.0, độ mờ của lớp 0.45
      - Ellipse "Current frame stroke", vị trí 176,393, cỡ 60 x 60, viền #1A1C22 (onCanvas), dày 4.0
    - Frame "Toolbar", vị trí 6,222, cỡ 52 x 392, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 14, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/4 (xs)/6 (ngoài hệ thống)/4 (xs), căn MIN/MIN
      - Instance "ToolButton", vị trí 10,228, cỡ 44 x 44, nền #3D6A00 (primary, surfaceTint), bo góc 8
        - Instance "Glyph", vị trí 20,238, cỡ 24 x 24
          - Text "glyph", vị trí 20,238, cỡ 24 x 24, chữ "brush", kiểu ngoài hệ thống (24/24, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Instance "ToolButton", vị trí 10,276, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,286, cỡ 24 x 24
          - Text "glyph", vị trí 20,286, cỡ 24 x 24, chữ "ink_eraser", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 10,324, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,334, cỡ 24 x 24
          - Text "glyph", vị trí 20,334, cỡ 24 x 24, chữ "format_color_fill", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 10,372, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,382, cỡ 24 x 24
          - Text "glyph", vị trí 20,382, cỡ 24 x 24, chữ "lasso_select", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 10,420, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,430, cỡ 24 x 24
          - Text "glyph", vị trí 20,430, cỡ 24 x 24, chữ "shapes", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 10,468, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,478, cỡ 24 x 24
          - Text "glyph", vị trí 20,478, cỡ 24 x 24, chữ "title", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 10,516, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,526, cỡ 24 x 24
          - Text "glyph", vị trí 20,526, cỡ 24 x 24, chữ "videocam", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "ToolButton", vị trí 10,564, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,574, cỡ 24 x 24
          - Text "glyph", vị trí 20,574, cỡ 24 x 24, chữ "more_horiz", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Frame "ToolOptions", vị trí 361,262, cỡ 44 x 174, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 14, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/4 (xs)/10 (ngoài hệ thống)/4 (xs), căn MIN/CENTER
      - Instance "ColorSwatch", vị trí 365,272, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
        - Ellipse "Color", vị trí 369,276, cỡ 28 x 28, nền #2F6FE0 (secondary), viền #D2D6DC (outlineVariant), dày 1.0
        - Ellipse "Ring", vị trí 365,272, cỡ 36 x 36, viền #12141A (onBackground, onSurface), dày 2.0
      - Ellipse "Brush size", vị trí 378,320, cỡ 10 x 10, nền #12141A (onBackground, onSurface)
      - Text "8", vị trí 380,342, cỡ 7 x 16, chữ "8", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (onSurfaceVariant)
      - Instance "Icon", vị trí 372,370, cỡ 22 x 22
        - Text "glyph", vị trí 372,370, cỡ 22 x 22, chữ "opacity", kiểu ngoài hệ thống (22/22, đậm 400), màu #12141A (onBackground, onSurface)
      - Instance "Icon", vị trí 372,404, cỡ 22 x 22
        - Text "glyph", vị trí 372,404, cỡ 22 x 22, chữ "layers", kiểu ngoài hệ thống (22/22, đậm 400), màu #12141A (onBackground, onSurface)
  - Frame "Timeline", vị trí 0,751, cỡ 411 x 140, nền #ECEEF1 (surfaceContainer), xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 4 (xs)/0/16 (md)/0, căn MIN/MIN
    - Frame "Controls", vị trí 0,755, cỡ 411 x 52, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/8 (sm), căn MIN/CENTER
      - Instance "Chip", vị trí 8,766, cỡ 108 x 30, nền #DDF5B8 (primaryContainer), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Leading icon", vị trí 20,772, cỡ 18 x 18
          - Text "glyph", vị trí 20,772, cỡ 18 x 18, chữ "animation", kiểu ngoài hệ thống (18/18, đậm 400), màu #1C2C05 (onPrimaryContainer)
        - Text "Label", vị trí 44,773, cỡ 60 x 16, chữ "Onion 2 · 1", kiểu labelMedium (12/16, đậm 500), màu #1C2C05 (onPrimaryContainer)
      - Text "12 / 48", vị trí 120,773, cỡ 40 x 16, chữ "12 / 48", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (onSurfaceVariant)
      - Frame "spacer", vị trí 164,780, cỡ 34 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "IconButton", vị trí 202,757, cỡ 48 x 48, nền #3D6A00 (primary, surfaceTint), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 214,769, cỡ 24 x 24
          - Text "glyph", vị trí 214,769, cỡ 24 x 24, chữ "play_arrow", kiểu ngoài hệ thống (24/24, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Frame "spacer", vị trí 254,780, cỡ 34 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "Chip", vị trí 292,767, cỡ 59 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Text "Label", vị trí 304,773, cỡ 35 x 16, chữ "12 fps", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
      - Instance "IconButton", vị trí 355,757, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 367,769, cỡ 24 x 24
          - Text "glyph", vị trí 367,769, cỡ 24 x 24, chữ "library_add", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Frame "Frames", vị trí 0,811, cỡ 411 x 64, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn MIN/CENTER
      - Instance "FrameThumb", vị trí 12,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 16,861, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 23,853, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 54,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 58,861, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 65,853, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 96,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 100,861, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 107,853, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 138,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), viền #3D6A00 (primary, surfaceTint), dày 2.0, bo góc 8
        - Rectangle "ground", vị trí 142,861, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 149,853, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 180,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 184,861, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 191,853, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 222,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 226,861, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 233,853, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 264,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 268,861, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 275,853, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Instance "FrameThumb", vị trí 306,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
        - Rectangle "ground", vị trí 310,861, cỡ 29 x 1, nền #1A1C22 (onCanvas)
        - Ellipse "ball", vị trí 317,853, cỡ 8 x 8, nền #2F6FE0 (secondary)
      - Frame "Add frame", vị trí 348,811, cỡ 36 x 64, viền #B9BEC8 (outline), dày 1.0, bo góc 8, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Icon", vị trí 355,832, cỡ 22 x 22, CHÚ Ý: ghi đè màu nền của component gốc
          - Text "glyph", vị trí 355,832, cỡ 22 x 22, chữ "add", kiểu ngoài hệ thống (22/22, đậm 400), màu #5F6675 (onSurfaceVariant)
  - Frame "Sound sheet", vị trí 0,293, cỡ 411 x 598, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/24 (lg)/0, căn MIN/MIN
    - Instance "SheetHandle", vị trí 0,293, cỡ 411 x 24, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Rectangle "Rectangle", vị trí 190,303, cỡ 32 x 4, nền #B9BEC8 (outline), bo góc 2
    - Frame "Header", vị trí 0,329, cỡ 411 x 48, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/24 (lg), căn MIN/CENTER
      - Text "Sound", vị trí 24,339, cỡ 69 x 28, chữ "Sound", kiểu titleLarge (22/28, đậm 500), màu #12141A (onBackground, onSurface)
      - Frame "spacer", vị trí 97,352, cỡ 250 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "IconButton", vị trí 351,329, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 363,341, cỡ 24 x 24
          - Text "glyph", vị trí 363,341, cỡ 24 x 24, chữ "close", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Frame "Body", vị trí 0,389, cỡ 411 x 478, xếp dọc, cách nhau 16 (md), lề trên/phải/dưới/trái 0/24 (lg)/0/24 (lg), căn MIN/MIN
      - Frame "Lofi beat.mp3", vị trí 24,389, cỡ 363 x 58, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/12 (ngoài hệ thống)/10 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Icon", vị trí 36,406, cỡ 24 x 24, CHÚ Ý: ghi đè màu nền của component gốc
          - Text "glyph", vị trí 36,406, cỡ 24 x 24, chữ "music_note", kiểu ngoài hệ thống (24/24, đậm 400), màu #2F6FE0 (secondary)
        - Frame "Text", vị trí 72,399, cỡ 243 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Lofi beat.mp3", vị trí 72,399, cỡ 93 x 20, chữ "Lofi beat.mp3", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
          - Text "Music · from 0:00 · 4.0 ", vị trí 72,421, cỡ 137 x 16, chữ "Music · from 0:00 · 4.0 s", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
        - Frame "Waveform", vị trí 327,408, cỡ 48 x 20, xếp ngang, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
          - Rectangle "Rectangle", vị trí 327,415, cỡ 3 x 6, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 332,411, cỡ 3 x 14, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 337,413, cỡ 3 x 10, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 342,408, cỡ 3 x 20, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 347,414, cỡ 3 x 8, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 352,410, cỡ 3 x 16, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 357,412, cỡ 3 x 12, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 362,415, cỡ 3 x 6, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 367,409, cỡ 3 x 18, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 372,413, cỡ 3 x 10, nền #5F6675 (onSurfaceVariant), bo góc 2
      - Frame "Boing", vị trí 24,463, cỡ 363 x 58, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/12 (ngoài hệ thống)/10 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Icon", vị trí 36,480, cỡ 24 x 24, CHÚ Ý: ghi đè màu nền của component gốc
          - Text "glyph", vị trí 36,480, cỡ 24 x 24, chữ "graphic_eq", kiểu ngoài hệ thống (24/24, đậm 400), màu #2F6FE0 (secondary)
        - Frame "Text", vị trí 72,473, cỡ 243 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Boing", vị trí 72,473, cỡ 39 x 20, chữ "Boing", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
          - Text "Sound effect · frame 12", vị trí 72,495, cỡ 133 x 16, chữ "Sound effect · frame 12", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
        - Frame "Waveform", vị trí 327,482, cỡ 48 x 20, xếp ngang, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
          - Rectangle "Rectangle", vị trí 327,489, cỡ 3 x 6, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 332,485, cỡ 3 x 14, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 337,487, cỡ 3 x 10, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 342,482, cỡ 3 x 20, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 347,488, cỡ 3 x 8, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 352,484, cỡ 3 x 16, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 357,486, cỡ 3 x 12, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 362,489, cỡ 3 x 6, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 367,483, cỡ 3 x 18, nền #5F6675 (onSurfaceVariant), bo góc 2
          - Rectangle "Rectangle", vị trí 372,487, cỡ 3 x 10, nền #5F6675 (onSurfaceVariant), bo góc 2
      - Frame "Effect packs", vị trí 24,537, cỡ 363 x 266, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Text "Effect packs", vị trí 24,537, cỡ 84 x 20, chữ "Effect packs", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
        - Frame "Cartoon pack (locked)", vị trí 24,565, cỡ 363 x 58, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/12 (ngoài hệ thống)/10 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Instance "Icon", vị trí 36,582, cỡ 24 x 24, CHÚ Ý: ghi đè màu nền của component gốc
            - Text "glyph", vị trí 36,582, cỡ 24 x 24, chữ "library_music", kiểu ngoài hệ thống (24/24, đậm 400), màu #5F6675 (onSurfaceVariant)
          - Frame "Text", vị trí 72,575, cỡ 271 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
            - Text "Cartoon", vị trí 72,575, cỡ 55 x 20, chữ "Cartoon", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
            - Text "24 sounds · boing, pop, ", vị trí 72,597, cỡ 180 x 16, chữ "24 sounds · boing, pop, swoosh", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
          - Instance "Icon", vị trí 355,584, cỡ 20 x 20, CHÚ Ý: ghi đè màu nền của component gốc
            - Text "glyph", vị trí 355,584, cỡ 20 x 20, chữ "lock", kiểu ngoài hệ thống (20/20, đậm 400), màu #5F6675 (onSurfaceVariant)
        - Frame "Ambience pack (locked)", vị trí 24,631, cỡ 363 x 58, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/12 (ngoài hệ thống)/10 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Instance "Icon", vị trí 36,648, cỡ 24 x 24, CHÚ Ý: ghi đè màu nền của component gốc
            - Text "glyph", vị trí 36,648, cỡ 24 x 24, chữ "library_music", kiểu ngoài hệ thống (24/24, đậm 400), màu #5F6675 (onSurfaceVariant)
          - Frame "Text", vị trí 72,641, cỡ 271 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
            - Text "Ambience", vị trí 72,641, cỡ 68 x 20, chữ "Ambience", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
            - Text "16 sounds · rain, city, ", vị trí 72,663, cỡ 156 x 16, chữ "16 sounds · rain, city, forest", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
          - Instance "Icon", vị trí 355,650, cỡ 20 x 20, CHÚ Ý: ghi đè màu nền của component gốc
            - Text "glyph", vị trí 355,650, cỡ 20 x 20, chữ "lock", kiểu ngoài hệ thống (20/20, đậm 400), màu #5F6675 (onSurfaceVariant)
        - Frame "RewardedInvite", vị trí 24,697, cỡ 363 x 106, nền #DDF5B8 (primaryContainer), bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/12 (ngoài hệ thống)/10 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Instance "Icon", vị trí 36,738, cỡ 24 x 24, CHÚ Ý: ghi đè màu nền của component gốc
            - Text "glyph", vị trí 36,738, cỡ 24 x 24, chữ "lock_open", kiểu ngoài hệ thống (24/24, đậm 400), màu #1C2C05 (onPrimaryContainer)
          - Frame "Text", vị trí 72,707, cỡ 155 x 86, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
            - Text "All effect packs", vị trí 72,707, cỡ 104 x 20, chữ "All effect packs", kiểu titleSmall (14/20, đậm 500), màu #1C2C05 (onPrimaryContainer)
            - Text "Watch one ad to use ever", vị trí 72,729, cỡ 155 x 64, chữ "Watch one ad to use every effect pack for 24 hours. Your music and voice stay free.", kiểu bodySmall (12/16, đậm 400), màu #1C2C05 (onPrimaryContainer)
          - Instance "Button", vị trí 239,726, cỡ 136 x 48, nền #3D6A00 (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
            - Instance "Leading icon", vị trí 259,740, cỡ 20 x 20
              - Text "glyph", vị trí 259,740, cỡ 20 x 20, chữ "smart_display", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
            - Text "Label", vị trí 287,740, cỡ 64 x 20, chữ "Watch ad", kiểu labelLarge (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Frame "Add sound", vị trí 24,819, cỡ 363 x 48, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Instance "Button", vị trí 24,819, cỡ 116 x 48, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
          - Instance "Leading icon", vị trí 45,833, cỡ 20 x 20
            - Text "glyph", vị trí 45,833, cỡ 20 x 20, chữ "library_music", kiểu ngoài hệ thống (20/20, đậm 400), màu #12141A (onBackground, onSurface)
          - Text "Label", vị trí 73,833, cỡ 41 x 20, chữ "Music", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
        - Instance "Button", vị trí 148,819, cỡ 116 x 48, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
          - Instance "Leading icon", vị trí 166,833, cỡ 20 x 20
            - Text "glyph", vị trí 166,833, cỡ 20 x 20, chữ "mic", kiểu ngoài hệ thống (20/20, đậm 400), màu #12141A (onBackground, onSurface)
          - Text "Label", vị trí 194,833, cỡ 48 x 20, chữ "Record", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
        - Instance "Button", vị trí 271,819, cỡ 116 x 48, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
          - Instance "Leading icon", vị trí 289,833, cỡ 20 x 20
            - Text "glyph", vị trí 289,833, cỡ 20 x 20, chữ "graphic_eq", kiểu ngoài hệ thống (20/20, đậm 400), màu #12141A (onBackground, onSurface)
          - Text "Label", vị trí 317,833, cỡ 49 x 20, chữ "Effects", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
