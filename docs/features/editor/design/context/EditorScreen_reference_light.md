# SỐ LIỆU THIẾT KẾ: EditorScreen_reference_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=33-4765
- Frame: `33:4765`, giao diện sáng
- Ngày lấy: 2026-11-21

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
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 400 | bodyLarge |
| kiểu chữ | 16/24, đậm 600 | titleMedium |
| kiểu chữ | 18/18, đậm 400 | ngoài hệ thống |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/22, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | ngoài hệ thống |
| màu chữ | #1C2C05 | ngoài hệ thống |
| màu chữ | #2F6FE0 | ngoài hệ thống |
| màu chữ | #5F6675 | ngoài hệ thống |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest |
| màu nền | #12141A | ngoài hệ thống |
| màu nền | #1A1C22 | ngoài hệ thống |
| màu nền | #2F6FE0 | ngoài hệ thống |
| màu nền | #3D6A00 | ngoài hệ thống |
| màu nền | #5C854D | ngoài hệ thống |
| màu nền | #B9BEC8 | ngoài hệ thống |
| màu nền | #D9DDE2 | ngoài hệ thống |
| màu nền | #DDF5B8 | ngoài hệ thống |
| màu nền | #E3E6EA | ngoài hệ thống |
| màu nền | #ECEEF1 | ngoài hệ thống |
| màu nền | #F1F2F5 | ngoài hệ thống |
| màu nền | #F7F8FA | ngoài hệ thống |
| màu nền | #FAD166 | ngoài hệ thống |
| màu nền | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest |
| độ mờ của lớp | 0.40 | - |
| độ mờ của lớp | 0.45 | - |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "EditorScreen_reference_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 56, xếp ngang, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,44, cỡ 24 x 24
        - Text "glyph", vị trí 16,44, cỡ 24 x 24, chữ "arrow_back", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Frame "Title", vị trí 54,36, cỡ 144 x 40, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Bouncing ball", vị trí 54,36, cỡ 144 x 24, chữ "Bouncing ball", kiểu titleMedium (16/24, đậm 600), màu #12141A (ngoài hệ thống)
      - Text "Frame 12 / 48 · saved", vị trí 54,60, cỡ 144 x 16, chữ "Frame 12 / 48 · saved", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "IconButton", vị trí 200,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 212,44, cỡ 24 x 24
        - Text "glyph", vị trí 212,44, cỡ 24 x 24, chữ "undo", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "IconButton", vị trí 250,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 262,44, cỡ 24 x 24
        - Text "glyph", vị trí 262,44, cỡ 24 x 24, chữ "redo", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Button", vị trí 300,32, cỡ 99 x 48, nền #3D6A00 (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/14 (ngoài hệ thống)/8 (sm)/12 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 312,46, cỡ 20 x 20
        - Text "glyph", vị trí 312,46, cỡ 20 x 20, chữ "ios_share", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
      - Text "Label", vị trí 340,46, cỡ 45 x 20, chữ "Export", kiểu ngoài hệ thống (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
  - Frame "editor_top_native", vị trí 0,84, cỡ 411 x 72, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 16 (md)/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn MIN/MIN
    - Instance "NativeAdStrip", vị trí 12,100, cỡ 387 x 56, nền #F1F2F5 (ngoài hệ thống), viền #D2D6DC (ngoài hệ thống), dày 1.0, bo góc 10, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/8 (sm)/0/8 (sm), căn MIN/CENTER
      - Rectangle "Ad media", vị trí 21,108, cỡ 40 x 40, nền #D9DDE2 (ngoài hệ thống), bo góc 8
      - Frame "Text", vị trí 71,110, cỡ 252 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Frame "Headline row", vị trí 71,110, cỡ 252 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
          - Frame "Ad badge", vị trí 71,111, cỡ 25 x 18, nền #D9DDE2 (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 1 (ngoài hệ thống)/5 (ngoài hệ thống)/1 (ngoài hệ thống)/5 (ngoài hệ thống), căn MIN/MIN
            - Text "Ad", vị trí 76,112, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (ngoài hệ thống)
          - Text "Headline", vị trí 102,110, cỡ 221 x 20, chữ "Sponsored app name", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)
        - Text "Body", vị trí 71,130, cỡ 252 x 16, chữ "Native ad, editor top strip", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
      - Frame "Ad CTA", vị trí 333,112, cỡ 57 x 32, viền #2F6FE0 (ngoài hệ thống), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Open", vị trí 346,120, cỡ 31 x 16, chữ "Open", kiểu labelMedium (12/16, đậm 500), màu #2F6FE0 (ngoài hệ thống)
  - Frame "Workspace", vị trí 0,156, cỡ 411 x 595, nền #F7F8FA (ngoài hệ thống)
    - Frame "Canvas 9:16", vị trí 62,198, cỡ 288 x 512, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
      - Frame "Reference photo (40%)", vị trí 62,198, cỡ 288 x 512, độ mờ của lớp 0.40
        - Ellipse "Ellipse", vị trí 2,578, cỡ 420 x 220, nền #5C854D (ngoài hệ thống)
        - Ellipse "Ellipse", vị trí 252,258, cỡ 70 x 70, nền #FAD166 (ngoài hệ thống)
      - Rectangle "Ground stroke", vị trí 85,618, cỡ 242 x 3, nền #1A1C22 (ngoài hệ thống)
      - Ellipse "Onion previous", vị trí 176,444, cỡ 60 x 60, viền #FF5A5F (ngoài hệ thống), dày 4.0, độ mờ của lớp 0.45
      - Ellipse "Onion next", vị trí 176,341, cỡ 60 x 60, viền #3D8BFF (ngoài hệ thống), dày 4.0, độ mờ của lớp 0.45
      - Ellipse "Current frame stroke", vị trí 176,393, cỡ 60 x 60, nền #2F6FE0 (ngoài hệ thống), viền #1A1C22 (ngoài hệ thống), dày 4.0
    - Frame "Toolbar", vị trí 6,222, cỡ 52 x 392, nền #E3E6EA (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/4 (xs)/6 (ngoài hệ thống)/4 (xs), căn MIN/MIN
      - Instance "ToolButton", vị trí 10,228, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,238, cỡ 24 x 24
          - Text "glyph", vị trí 20,238, cỡ 24 x 24, chữ "brush", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,276, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,286, cỡ 24 x 24
          - Text "glyph", vị trí 20,286, cỡ 24 x 24, chữ "ink_eraser", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,324, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,334, cỡ 24 x 24
          - Text "glyph", vị trí 20,334, cỡ 24 x 24, chữ "format_color_fill", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,372, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,382, cỡ 24 x 24
          - Text "glyph", vị trí 20,382, cỡ 24 x 24, chữ "lasso_select", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,420, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,430, cỡ 24 x 24
          - Text "glyph", vị trí 20,430, cỡ 24 x 24, chữ "shapes", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,468, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,478, cỡ 24 x 24
          - Text "glyph", vị trí 20,478, cỡ 24 x 24, chữ "title", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,516, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,526, cỡ 24 x 24
          - Text "glyph", vị trí 20,526, cỡ 24 x 24, chữ "videocam", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,564, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,574, cỡ 24 x 24
          - Text "glyph", vị trí 20,574, cỡ 24 x 24, chữ "more_horiz", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Frame "ToolOptions", vị trí 361,262, cỡ 44 x 174, nền #E3E6EA (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/4 (xs)/10 (ngoài hệ thống)/4 (xs), căn MIN/CENTER
      - Instance "ColorSwatch", vị trí 365,272, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
        - Ellipse "Color", vị trí 369,276, cỡ 28 x 28, nền #2F6FE0 (ngoài hệ thống), viền #D2D6DC (ngoài hệ thống), dày 1.0
        - Ellipse "Ring", vị trí 365,272, cỡ 36 x 36, viền #12141A (ngoài hệ thống), dày 2.0
      - Ellipse "Brush size", vị trí 378,320, cỡ 10 x 10, nền #12141A (ngoài hệ thống)
      - Text "8", vị trí 380,342, cỡ 7 x 16, chữ "8", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (ngoài hệ thống)
      - Instance "Icon", vị trí 372,370, cỡ 22 x 22
        - Text "glyph", vị trí 372,370, cỡ 22 x 22, chữ "opacity", kiểu ngoài hệ thống (22/22, đậm 400), màu #12141A (ngoài hệ thống)
      - Instance "Icon", vị trí 372,404, cỡ 22 x 22
        - Text "glyph", vị trí 372,404, cỡ 22 x 22, chữ "layers", kiểu ngoài hệ thống (22/22, đậm 400), màu #12141A (ngoài hệ thống)
  - Frame "Timeline", vị trí 0,751, cỡ 411 x 140, nền #ECEEF1 (ngoài hệ thống), xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 4 (xs)/0/16 (md)/0, căn MIN/MIN
    - Frame "Controls", vị trí 0,755, cỡ 411 x 52, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/8 (sm), căn MIN/CENTER
      - Instance "Chip", vị trí 8,766, cỡ 108 x 30, nền #DDF5B8 (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Leading icon", vị trí 20,772, cỡ 18 x 18
          - Text "glyph", vị trí 20,772, cỡ 18 x 18, chữ "animation", kiểu ngoài hệ thống (18/18, đậm 400), màu #1C2C05 (ngoài hệ thống)
        - Text "Label", vị trí 44,773, cỡ 60 x 16, chữ "Onion 2 · 1", kiểu labelMedium (12/16, đậm 500), màu #1C2C05 (ngoài hệ thống)
      - Text "12 / 48", vị trí 120,773, cỡ 40 x 16, chữ "12 / 48", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (ngoài hệ thống)
      - Frame "spacer", vị trí 164,780, cỡ 34 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "IconButton", vị trí 202,757, cỡ 48 x 48, nền #3D6A00 (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 214,769, cỡ 24 x 24
          - Text "glyph", vị trí 214,769, cỡ 24 x 24, chữ "play_arrow", kiểu ngoài hệ thống (24/24, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
      - Frame "spacer", vị trí 254,780, cỡ 34 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "Chip", vị trí 292,767, cỡ 59 x 28, nền #E3E6EA (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Text "Label", vị trí 304,773, cỡ 35 x 16, chữ "12 fps", kiểu labelMedium (12/16, đậm 500), màu #12141A (ngoài hệ thống)
      - Instance "IconButton", vị trí 355,757, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 367,769, cỡ 24 x 24
          - Text "glyph", vị trí 367,769, cỡ 24 x 24, chữ "library_add", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Frame "Frames", vị trí 0,811, cỡ 411 x 64, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn MIN/CENTER
      - Instance "FrameThumb", vị trí 12,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
        - Rectangle "ground", vị trí 16,861, cỡ 29 x 1, nền #1A1C22 (ngoài hệ thống)
        - Ellipse "ball", vị trí 23,853, cỡ 8 x 8, nền #2F6FE0 (ngoài hệ thống)
      - Instance "FrameThumb", vị trí 54,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
        - Rectangle "ground", vị trí 58,861, cỡ 29 x 1, nền #1A1C22 (ngoài hệ thống)
        - Ellipse "ball", vị trí 65,853, cỡ 8 x 8, nền #2F6FE0 (ngoài hệ thống)
      - Instance "FrameThumb", vị trí 96,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
        - Rectangle "ground", vị trí 100,861, cỡ 29 x 1, nền #1A1C22 (ngoài hệ thống)
        - Ellipse "ball", vị trí 107,853, cỡ 8 x 8, nền #2F6FE0 (ngoài hệ thống)
      - Instance "FrameThumb", vị trí 138,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), viền #3D6A00 (ngoài hệ thống), dày 2.0, bo góc 8
        - Rectangle "ground", vị trí 142,861, cỡ 29 x 1, nền #1A1C22 (ngoài hệ thống)
        - Ellipse "ball", vị trí 149,853, cỡ 8 x 8, nền #2F6FE0 (ngoài hệ thống)
      - Instance "FrameThumb", vị trí 180,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
        - Rectangle "ground", vị trí 184,861, cỡ 29 x 1, nền #1A1C22 (ngoài hệ thống)
        - Ellipse "ball", vị trí 191,853, cỡ 8 x 8, nền #2F6FE0 (ngoài hệ thống)
      - Instance "FrameThumb", vị trí 222,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
        - Rectangle "ground", vị trí 226,861, cỡ 29 x 1, nền #1A1C22 (ngoài hệ thống)
        - Ellipse "ball", vị trí 233,853, cỡ 8 x 8, nền #2F6FE0 (ngoài hệ thống)
      - Instance "FrameThumb", vị trí 264,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
        - Rectangle "ground", vị trí 268,861, cỡ 29 x 1, nền #1A1C22 (ngoài hệ thống)
        - Ellipse "ball", vị trí 275,853, cỡ 8 x 8, nền #2F6FE0 (ngoài hệ thống)
      - Instance "FrameThumb", vị trí 306,811, cỡ 36 x 64, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
        - Rectangle "ground", vị trí 310,861, cỡ 29 x 1, nền #1A1C22 (ngoài hệ thống)
        - Ellipse "ball", vị trí 317,853, cỡ 8 x 8, nền #2F6FE0 (ngoài hệ thống)
      - Frame "Add frame", vị trí 348,811, cỡ 36 x 64, viền #B9BEC8 (ngoài hệ thống), dày 1.0, bo góc 8, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Icon", vị trí 355,832, cỡ 22 x 22, CHÚ Ý: ghi đè màu nền của component gốc
          - Text "glyph", vị trí 355,832, cỡ 22 x 22, chữ "add", kiểu ngoài hệ thống (22/22, đậm 400), màu #5F6675 (ngoài hệ thống)
  - Frame "Reference sheet", vị trí 0,601, cỡ 411 x 290, nền #E3E6EA (ngoài hệ thống), xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/24 (lg)/0, căn MIN/MIN
    - Instance "SheetHandle", vị trí 0,601, cỡ 411 x 24, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Rectangle "Rectangle", vị trí 190,611, cỡ 32 x 4, nền #B9BEC8 (ngoài hệ thống), bo góc 2
    - Frame "Header", vị trí 0,637, cỡ 411 x 48, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/24 (lg), căn MIN/CENTER
      - Text "Reference", vị trí 24,647, cỡ 109 x 28, chữ "Reference", kiểu ngoài hệ thống (22/28, đậm 500), màu #12141A (ngoài hệ thống)
      - Frame "spacer", vị trí 137,660, cỡ 210 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "IconButton", vị trí 351,637, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 363,649, cỡ 24 x 24
          - Text "glyph", vị trí 363,649, cỡ 24 x 24, chữ "close", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Frame "Body", vị trí 0,697, cỡ 411 x 170, xếp dọc, cách nhau 16 (md), lề trên/phải/dưới/trái 0/24 (lg)/0/24 (lg), căn MIN/MIN
      - Frame "Slider · Opacity", vị trí 24,697, cỡ 363 x 48, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Frame "Header", vị trí 24,697, cỡ 363 x 20, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Label", vị trí 24,697, cỡ 53 x 20, chữ "Opacity", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)
          - Frame "spacer", vị trí 77,697, cỡ 279 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Value", vị trí 356,697, cỡ 31 x 20, chữ "40%", kiểu ngoài hệ thống (14/20, đậm 600), màu #5F6675 (ngoài hệ thống)
        - Frame "Track", vị trí 24,725, cỡ 363 x 20
          - Rectangle "Rectangle", vị trí 24,733, cỡ 363 x 4, nền #D9DDE2 (ngoài hệ thống), bo góc 2
          - Rectangle "Rectangle", vị trí 24,733, cỡ 145 x 4, nền #3D6A00 (ngoài hệ thống), bo góc 2
          - Ellipse "Thumb", vị trí 159,725, cỡ 20 x 20, nền #3D6A00 (ngoài hệ thống)
      - Frame "Show reference", vị trí 24,761, cỡ 363 x 42, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Frame "Text", vị trí 24,761, cỡ 299 x 42, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Show reference", vị trí 24,761, cỡ 119 x 24, chữ "Show reference", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
          - Text "Trace over a photo or vi", vị trí 24,787, cỡ 299 x 16, chữ "Trace over a photo or video. It is not exported.", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
        - Instance "Switch", vị trí 335,766, cỡ 52 x 32, nền #3D6A00 (ngoài hệ thống), bo góc 999
          - Ellipse "Knob", vị trí 359,770, cỡ 24 x 24, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
      - Instance "Remove reference", vị trí 24,819, cỡ 169 x 48, viền #B9BEC8 (ngoài hệ thống), dày 1.0, bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Label", vị trí 45,833, cỡ 123 x 20, chữ "Remove reference", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
