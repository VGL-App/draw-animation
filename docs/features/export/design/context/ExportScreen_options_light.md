# SỐ LIỆU THIẾT KẾ: ExportScreen_options_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-3298
- Frame: `23:3298`, giao diện sáng
- Ngày lấy: 2026-11-21

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
| kiểu chữ | 14/20, đậm 500 | titleSmall, labelLarge |
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 600 | titleMedium |
| kiểu chữ | 2/3, đậm 600 | ngoài hệ thống |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 3/3, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | ngoài hệ thống |
| màu chữ | #1C2C05 | ngoài hệ thống |
| màu chữ | #3D6A00 | ngoài hệ thống |
| màu chữ | #5F6675 | ngoài hệ thống |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest |
| màu chữ | #FFFFFF (độ mờ 0.90, lớp tô 0.90) | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest |
| màu nền | #1A1C22 | ngoài hệ thống |
| màu nền | #2F6FE0 | ngoài hệ thống |
| màu nền | #3D6A00 | ngoài hệ thống |
| màu nền | #D9DDE2 | ngoài hệ thống |
| màu nền | #DDF5B8 | ngoài hệ thống |
| màu nền | #E3E6EA | ngoài hệ thống |
| màu nền | #ECEEF1 | ngoài hệ thống |
| màu nền | #F1F2F5 | ngoài hệ thống |
| màu nền | #F7F8FA | ngoài hệ thống |
| màu nền | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "ExportScreen_options_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,48, cỡ 24 x 24
        - Text "glyph", vị trí 16,48, cỡ 24 x 24, chữ "close", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Text "Export", vị trí 56,46, cỡ 72 x 28, chữ "Export", kiểu ngoài hệ thống (22/28, đậm 500), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 132,60, cỡ 271 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Content", vị trí 0,92, cỡ 411 x 703, xếp dọc, cách nhau 16 (md), lề trên/phải/dưới/trái 4 (xs)/16 (md)/0/16 (md), căn MIN/MIN
    - Frame "ProjectSummary", vị trí 16,96, cỡ 379 x 120, nền #ECEEF1 (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Frame "Preview", vị trí 28,108, cỡ 54 x 96, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
        - Rectangle "Rectangle", vị trí 32,187, cỡ 45 x 2, nền #1A1C22 (ngoài hệ thống)
        - Ellipse "Ellipse", vị trí 49,174, cỡ 13 x 13, nền #2F6FE0 (ngoài hệ thống)
        - Frame "Watermark", vị trí 65,199, cỡ 16 x 3, xếp ngang, cách nhau 1 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
          - Instance "Icon", vị trí 65,200, cỡ 3 x 3, CHÚ Ý: ghi đè màu nền, viền của component gốc
            - Text "glyph", vị trí 65,200, cỡ 3 x 3, chữ "animation", kiểu ngoài hệ thống (3/3, đậm 400), màu #FFFFFF (độ mờ 0.90, lớp tô 0.90) (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
          - Text "Frameloop", vị trí 68,199, cỡ 12 x 3, chữ "Frameloop", kiểu ngoài hệ thống (2/3, đậm 600), màu #FFFFFF (độ mờ 0.90, lớp tô 0.90) (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
      - Frame "Text", vị trí 98,124, cỡ 285 x 64, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Text "Bouncing ball", vị trí 98,124, cỡ 105 x 24, chữ "Bouncing ball", kiểu titleMedium (16/24, đậm 600), màu #12141A (ngoài hệ thống)
        - Text "48 frames · 12 fps · 4.0", vị trí 98,150, cỡ 163 x 20, chữ "48 frames · 12 fps · 4.0 s", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
        - Text "9:16 · 1080 × 1920", vị trí 98,172, cỡ 105 x 16, chữ "9:16 · 1080 × 1920", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Frame "export_options_mrec", vị trí 16,232, cỡ 379 x 250, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/MIN
      - Instance "MrecSlot", vị trí 56,232, cỡ 300 x 250, nền #F1F2F5 (ngoài hệ thống), viền #D2D6DC (ngoài hệ thống), dày 1.0, bo góc 4
        - Text "Label", vị trí 134,349, cỡ 142 x 16, chữ "Banner MREC 300 × 250", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (ngoài hệ thống)
        - Frame "Ad badge", vị trí 62,238, cỡ 27 x 20, nền #D9DDE2 (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
          - Text "Ad", vị trí 68,240, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (ngoài hệ thống)
    - Text "Format", vị trí 16,498, cỡ 48 x 20, chữ "Format", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
    - Frame "Formats", vị trí 16,534, cỡ 379 x 282, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "FormatOption", vị trí 16,534, cỡ 379 x 66, nền #ECEEF1 (ngoài hệ thống), viền #3D6A00 (ngoài hệ thống), dày 2.0, bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/16 (md)/12 (ngoài hệ thống)/16 (md), căn MIN/CENTER
        - Instance "Icon", vị trí 34,555, cỡ 24 x 24
          - Text "glyph", vị trí 34,555, cỡ 24 x 24, chữ "radio_button_checked", kiểu ngoài hệ thống (24/24, đậm 400), màu #3D6A00 (ngoài hệ thống)
        - Frame "Text", vị trí 70,548, cỡ 307 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Title", vị trí 70,548, cỡ 72 x 20, chữ "MP4 video", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
          - Text "Description", vị trí 70,570, cỡ 174 x 16, chữ "With sound · plays everywhere", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
      - Instance "FormatOption", vị trí 16,608, cỡ 379 x 64, nền #ECEEF1 (ngoài hệ thống), viền #D2D6DC (ngoài hệ thống), dày 1.0, bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/16 (md)/12 (ngoài hệ thống)/16 (md), căn MIN/CENTER
        - Instance "Icon", vị trí 33,628, cỡ 24 x 24
          - Text "glyph", vị trí 33,628, cỡ 24 x 24, chữ "radio_button_unchecked", kiểu ngoài hệ thống (24/24, đậm 400), màu #5F6675 (ngoài hệ thống)
        - Frame "Text", vị trí 69,621, cỡ 309 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Title", vị trí 69,621, cỡ 23 x 20, chữ "GIF", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
          - Text "Description", vị trí 69,643, cỡ 187 x 16, chữ "Loops, no sound · good for chats", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
      - Instance "FormatOption", vị trí 16,680, cỡ 379 x 64, nền #ECEEF1 (ngoài hệ thống), viền #D2D6DC (ngoài hệ thống), dày 1.0, bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/16 (md)/12 (ngoài hệ thống)/16 (md), căn MIN/CENTER
        - Instance "Icon", vị trí 33,700, cỡ 24 x 24
          - Text "glyph", vị trí 33,700, cỡ 24 x 24, chữ "radio_button_unchecked", kiểu ngoài hệ thống (24/24, đậm 400), màu #5F6675 (ngoài hệ thống)
        - Frame "Text", vị trí 69,693, cỡ 243 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Title", vị trí 69,693, cỡ 100 x 20, chữ "PNG sequence", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
          - Text "Description", vị trí 69,715, cỡ 219 x 16, chữ "One image per frame · for editing apps", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
        - Frame "Reward badge", vị trí 324,700, cỡ 54 x 24, nền #E3E6EA (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 4 (xs)/10 (ngoài hệ thống)/4 (xs)/8 (sm), căn MIN/CENTER
          - Instance "Icon", vị trí 332,704, cỡ 16 x 16
            - Text "glyph", vị trí 332,704, cỡ 16 x 16, chữ "smart_display", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
          - Text "Ad", vị trí 352,704, cỡ 16 x 16, chữ "Ad", kiểu labelMedium (12/16, đậm 500), màu #12141A (ngoài hệ thống)
      - Instance "FormatOption", vị trí 16,752, cỡ 379 x 64, nền #ECEEF1 (ngoài hệ thống), viền #D2D6DC (ngoài hệ thống), dày 1.0, bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/16 (md)/12 (ngoài hệ thống)/16 (md), căn MIN/CENTER
        - Instance "Icon", vị trí 33,772, cỡ 24 x 24
          - Text "glyph", vị trí 33,772, cỡ 24 x 24, chữ "radio_button_unchecked", kiểu ngoài hệ thống (24/24, đậm 400), màu #5F6675 (ngoài hệ thống)
        - Frame "Text", vị trí 69,765, cỡ 243 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Text "Title", vị trí 69,765, cỡ 108 x 20, chữ "Transparent GIF", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
          - Text "Description", vị trí 69,787, cỡ 142 x 16, chữ "No background · stickers", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
        - Frame "Reward badge", vị trí 324,772, cỡ 54 x 24, nền #E3E6EA (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 4 (xs)/10 (ngoài hệ thống)/4 (xs)/8 (sm), căn MIN/CENTER
          - Instance "Icon", vị trí 332,776, cỡ 16 x 16
            - Text "glyph", vị trí 332,776, cỡ 16 x 16, chữ "smart_display", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
          - Text "Ad", vị trí 352,776, cỡ 16 x 16, chữ "Ad", kiểu labelMedium (12/16, đậm 500), màu #12141A (ngoài hệ thống)
    - Text "Quality", vị trí 16,832, cỡ 48 x 20, chữ "Quality", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
    - Frame "Quality", vị trí 16,868, cỡ 123 x 28, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "Chip", vị trí 16,868, cỡ 54 x 28, nền #E3E6EA (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Text "Label", vị trí 28,874, cỡ 30 x 16, chữ "720p", kiểu labelMedium (12/16, đậm 500), màu #12141A (ngoài hệ thống)
      - Instance "Chip", vị trí 78,868, cỡ 61 x 28, nền #DDF5B8 (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
        - Text "Label", vị trí 90,874, cỡ 37 x 16, chữ "1080p", kiểu labelMedium (12/16, đậm 500), màu #1C2C05 (ngoài hệ thống)
    - Frame "Watermark", vị trí 16,912, cỡ 379 x 58, nền #ECEEF1 (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/12 (ngoài hệ thống)/10 (ngoài hệ thống)/16 (md), căn MIN/CENTER
      - Frame "Text", vị trí 32,922, cỡ 287 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Text "Frameloop watermark", vị trí 32,922, cỡ 147 x 20, chữ "Frameloop watermark", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
        - Text "Turn off by watching one", vị trí 32,944, cỡ 287 x 16, chữ "Turn off by watching one ad", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
      - Instance "Switch", vị trí 331,925, cỡ 52 x 32, nền #3D6A00 (ngoài hệ thống), bo góc 999
        - Ellipse "Knob", vị trí 355,929, cỡ 24 x 24, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
    - Frame "spacer", vị trí 16,986, cỡ 1 x 1, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Footer", vị trí 0,795, cỡ 411 x 96, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 24 (lg)/16 (md)/24 (lg)/16 (md), căn MIN/MIN
    - Instance "Button", vị trí 16,819, cỡ 379 x 48, nền #3D6A00 (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 150,833, cỡ 20 x 20
        - Text "glyph", vị trí 150,833, cỡ 20 x 20, chữ "ios_share", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
      - Text "Label", vị trí 178,833, cỡ 80 x 20, chữ "Export MP4", kiểu ngoài hệ thống (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
