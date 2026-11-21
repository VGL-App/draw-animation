# SỐ LIỆU THIẾT KẾ: EditorScreen_timeline-collapsed_dark

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=33-4118
- Frame: `33:4118`, giao diện tối
- Ngày lấy: 2026-11-21

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| instance ghi đè component | ColorSwatch | màu nền |
| khoảng cách | 1 | ngoài hệ thống |
| khoảng cách | 10 | ngoài hệ thống |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 14 | ngoài hệ thống |
| khoảng cách | 16 | md |
| khoảng cách | 2 | ngoài hệ thống |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 4 | xs |
| khoảng cách | 5 | ngoài hệ thống |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 11/16, đậm 500 | labelSmall |
| kiểu chữ | 12/16, đậm 400 | bodySmall |
| kiểu chữ | 12/16, đậm 500 | labelMedium |
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 600 | titleMedium |
| kiểu chữ | 18/18, đậm 400 | ngoài hệ thống |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/22, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| màu chữ | #10140A | ngoài hệ thống |
| màu chữ | #6EA8FF | ngoài hệ thống |
| màu chữ | #8C93A3 | ngoài hệ thống |
| màu chữ | #D8FA9E | ngoài hệ thống |
| màu chữ | #EEF0F4 | ngoài hệ thống |
| màu nền | #0E1014 | ngoài hệ thống |
| màu nền | #13161B | ngoài hệ thống |
| màu nền | #171A20 | ngoài hệ thống |
| màu nền | #1A1C22 | ngoài hệ thống |
| màu nền | #22262E | ngoài hệ thống |
| màu nền | #2B303A | ngoài hệ thống |
| màu nền | #2E3D17 | ngoài hệ thống |
| màu nền | #6EA8FF | ngoài hệ thống |
| màu nền | #B6F35C | ngoài hệ thống |
| màu nền | #EEF0F4 | ngoài hệ thống |
| màu nền | #FAFAF7 | ngoài hệ thống |
| độ mờ của lớp | 0.45 | - |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "EditorScreen_timeline-collapsed_dark", vị trí 0,0, cỡ 411 x 891, nền #0E1014 (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #EEF0F4 (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 56, xếp ngang, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,44, cỡ 24 x 24
        - Text "glyph", vị trí 16,44, cỡ 24 x 24, chữ "arrow_back", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Frame "Title", vị trí 54,36, cỡ 144 x 40, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Bouncing ball", vị trí 54,36, cỡ 144 x 24, chữ "Bouncing ball", kiểu titleMedium (16/24, đậm 600), màu #EEF0F4 (ngoài hệ thống)
      - Text "Frame 12 / 48 · saved", vị trí 54,60, cỡ 144 x 16, chữ "Frame 12 / 48 · saved", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
    - Instance "IconButton", vị trí 200,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 212,44, cỡ 24 x 24
        - Text "glyph", vị trí 212,44, cỡ 24 x 24, chữ "undo", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Instance "IconButton", vị trí 250,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 262,44, cỡ 24 x 24
        - Text "glyph", vị trí 262,44, cỡ 24 x 24, chữ "redo", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Button", vị trí 300,32, cỡ 99 x 48, nền #B6F35C (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/14 (ngoài hệ thống)/8 (sm)/12 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 312,46, cỡ 20 x 20
        - Text "glyph", vị trí 312,46, cỡ 20 x 20, chữ "ios_share", kiểu ngoài hệ thống (20/20, đậm 400), màu #10140A (ngoài hệ thống)
      - Text "Label", vị trí 340,46, cỡ 45 x 20, chữ "Export", kiểu ngoài hệ thống (14/20, đậm 600), màu #10140A (ngoài hệ thống)
  - Frame "editor_top_native", vị trí 0,84, cỡ 411 x 72, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 16 (md)/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn MIN/MIN
    - Instance "NativeAdStrip", vị trí 12,100, cỡ 387 x 56, nền #13161B (ngoài hệ thống), viền #2C313B (ngoài hệ thống), dày 1.0, bo góc 10, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/8 (sm)/0/8 (sm), căn MIN/CENTER
      - Rectangle "Ad media", vị trí 21,108, cỡ 40 x 40, nền #2B303A (ngoài hệ thống), bo góc 8
      - Frame "Text", vị trí 71,110, cỡ 252 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Frame "Headline row", vị trí 71,110, cỡ 252 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
          - Frame "Ad badge", vị trí 71,111, cỡ 25 x 18, nền #2B303A (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 1 (ngoài hệ thống)/5 (ngoài hệ thống)/1 (ngoài hệ thống)/5 (ngoài hệ thống), căn MIN/MIN
            - Text "Ad", vị trí 76,112, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #8C93A3 (ngoài hệ thống)
          - Text "Headline", vị trí 102,110, cỡ 221 x 20, chữ "Sponsored app name", kiểu ngoài hệ thống (14/20, đậm 600), màu #EEF0F4 (ngoài hệ thống)
        - Text "Body", vị trí 71,130, cỡ 252 x 16, chữ "Native ad, editor top strip", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
      - Frame "Ad CTA", vị trí 333,112, cỡ 57 x 32, viền #6EA8FF (ngoài hệ thống), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/12 (ngoài hệ thống)/0/12 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Open", vị trí 346,120, cỡ 31 x 16, chữ "Open", kiểu labelMedium (12/16, đậm 500), màu #6EA8FF (ngoài hệ thống)
  - Frame "Workspace", vị trí 0,156, cỡ 411 x 679, nền #0E1014 (ngoài hệ thống)
    - Frame "Canvas 9:16", vị trí 62,232, cỡ 296 x 526, nền #FAFAF7 (ngoài hệ thống)
      - Rectangle "Ground stroke", vị trí 86,664, cỡ 249 x 3, nền #1A1C22 (ngoài hệ thống)
      - Ellipse "Onion previous", vị trí 179,485, cỡ 62 x 62, viền #FF5A5F (ngoài hệ thống), dày 4.111111164093018, độ mờ của lớp 0.45
      - Ellipse "Onion next", vị trí 179,379, cỡ 62 x 62, viền #3D8BFF (ngoài hệ thống), dày 4.111111164093018, độ mờ của lớp 0.45
      - Ellipse "Current frame stroke", vị trí 179,432, cỡ 62 x 62, viền #1A1C22 (ngoài hệ thống), dày 4.111111164093018
    - Frame "Toolbar", vị trí 6,300, cỡ 52 x 392, nền #22262E (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/4 (xs)/6 (ngoài hệ thống)/4 (xs), căn MIN/MIN
      - Instance "ToolButton", vị trí 10,306, cỡ 44 x 44, nền #B6F35C (ngoài hệ thống), bo góc 8
        - Instance "Glyph", vị trí 20,316, cỡ 24 x 24
          - Text "glyph", vị trí 20,316, cỡ 24 x 24, chữ "brush", kiểu ngoài hệ thống (24/24, đậm 400), màu #10140A (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,354, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,364, cỡ 24 x 24
          - Text "glyph", vị trí 20,364, cỡ 24 x 24, chữ "ink_eraser", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,402, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,412, cỡ 24 x 24
          - Text "glyph", vị trí 20,412, cỡ 24 x 24, chữ "format_color_fill", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,450, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,460, cỡ 24 x 24
          - Text "glyph", vị trí 20,460, cỡ 24 x 24, chữ "lasso_select", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,498, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,508, cỡ 24 x 24
          - Text "glyph", vị trí 20,508, cỡ 24 x 24, chữ "shapes", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,546, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,556, cỡ 24 x 24
          - Text "glyph", vị trí 20,556, cỡ 24 x 24, chữ "title", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,594, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,604, cỡ 24 x 24
          - Text "glyph", vị trí 20,604, cỡ 24 x 24, chữ "videocam", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Instance "ToolButton", vị trí 10,642, cỡ 44 x 44, bo góc 8
        - Instance "Glyph", vị trí 20,652, cỡ 24 x 24
          - Text "glyph", vị trí 20,652, cỡ 24 x 24, chữ "more_horiz", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Frame "ToolOptions", vị trí 361,329, cỡ 44 x 174, nền #22262E (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/4 (xs)/10 (ngoài hệ thống)/4 (xs), căn MIN/CENTER
      - Instance "ColorSwatch", vị trí 365,339, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
        - Ellipse "Color", vị trí 369,343, cỡ 28 x 28, nền #6EA8FF (ngoài hệ thống), viền #2C313B (ngoài hệ thống), dày 1.0
        - Ellipse "Ring", vị trí 365,339, cỡ 36 x 36, viền #EEF0F4 (ngoài hệ thống), dày 2.0
      - Ellipse "Brush size", vị trí 378,387, cỡ 10 x 10, nền #EEF0F4 (ngoài hệ thống)
      - Text "8", vị trí 380,409, cỡ 7 x 16, chữ "8", kiểu labelSmall (11/16, đậm 500), màu #8C93A3 (ngoài hệ thống)
      - Instance "Icon", vị trí 372,437, cỡ 22 x 22
        - Text "glyph", vị trí 372,437, cỡ 22 x 22, chữ "opacity", kiểu ngoài hệ thống (22/22, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Instance "Icon", vị trí 372,471, cỡ 22 x 22
        - Text "glyph", vị trí 372,471, cỡ 22 x 22, chữ "layers", kiểu ngoài hệ thống (22/22, đậm 400), màu #EEF0F4 (ngoài hệ thống)
  - Frame "Timeline", vị trí 0,835, cỡ 411 x 56, nền #171A20 (ngoài hệ thống), xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 2 (ngoài hệ thống)/0/2 (ngoài hệ thống)/0, căn MIN/MIN
    - Frame "Controls", vị trí 0,837, cỡ 411 x 52, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/8 (sm), căn MIN/CENTER
      - Instance "Chip", vị trí 8,848, cỡ 108 x 30, nền #2E3D17 (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Leading icon", vị trí 20,854, cỡ 18 x 18
          - Text "glyph", vị trí 20,854, cỡ 18 x 18, chữ "animation", kiểu ngoài hệ thống (18/18, đậm 400), màu #D8FA9E (ngoài hệ thống)
        - Text "Label", vị trí 44,855, cỡ 60 x 16, chữ "Onion 2 · 1", kiểu labelMedium (12/16, đậm 500), màu #D8FA9E (ngoài hệ thống)
      - Text "12 / 48", vị trí 120,855, cỡ 40 x 16, chữ "12 / 48", kiểu labelMedium (12/16, đậm 500), màu #8C93A3 (ngoài hệ thống)
      - Frame "spacer", vị trí 164,862, cỡ 34 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "IconButton", vị trí 202,839, cỡ 48 x 48, nền #B6F35C (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 214,851, cỡ 24 x 24
          - Text "glyph", vị trí 214,851, cỡ 24 x 24, chữ "play_arrow", kiểu ngoài hệ thống (24/24, đậm 400), màu #10140A (ngoài hệ thống)
      - Frame "spacer", vị trí 254,862, cỡ 34 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "Chip", vị trí 292,849, cỡ 59 x 28, nền #22262E (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Text "Label", vị trí 304,855, cỡ 35 x 16, chữ "12 fps", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (ngoài hệ thống)
      - Instance "Expand timeline", vị trí 355,839, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 367,851, cỡ 24 x 24
          - Text "glyph", vị trí 367,851, cỡ 24 x 24, chữ "expand_less", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
