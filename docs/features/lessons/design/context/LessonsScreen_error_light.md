# SỐ LIỆU THIẾT KẾ: LessonsScreen_error_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-3454
- Frame: `23:3454`, giao diện sáng
- Ngày lấy: 2026-11-20

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| instance ghi đè component | Icon | màu nền |
| khoảng cách | 10 | ngoài hệ thống |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 16 | md |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 24 | lg |
| khoảng cách | 4 | xs |
| khoảng cách | 8 | sm |
| kiểu chữ | 12/16, đậm 500 | labelMedium |
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 400 | bodyLarge |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | ngoài hệ thống |
| kiểu chữ | 48/48, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | ngoài hệ thống |
| màu chữ | #1C2C05 | ngoài hệ thống |
| màu chữ | #5F6675 | ngoài hệ thống |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest |
| màu nền | #3D6A00 | ngoài hệ thống |
| màu nền | #DDF5B8 | ngoài hệ thống |
| màu nền | #ECEEF1 | ngoài hệ thống |
| màu nền | #F7F8FA | ngoài hệ thống |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "LessonsScreen_error_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "Lessons", vị trí 20,44, cỡ 95 x 32, chữ "Lessons", kiểu ngoài hệ thống (24/32, đậm 700), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 119,60, cỡ 284 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Content", vị trí 0,92, cỡ 411 x 711, xếp dọc, cách nhau 16 (md), lề trên/phải/dưới/trái 0/24 (lg)/0/24 (lg), căn CENTER/CENTER
    - Instance "Icon", vị trí 182,336, cỡ 48 x 48, CHÚ Ý: ghi đè màu nền của component gốc
      - Text "glyph", vị trí 182,336, cỡ 48 x 48, chữ "cloud_off", kiểu ngoài hệ thống (48/48, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Text "Lessons didn't load", vị trí 24,400, cỡ 363 x 32, chữ "Lessons didn't load", kiểu ngoài hệ thống (24/32, đậm 700), màu #12141A (ngoài hệ thống)
    - Text "The lesson server is not", vị trí 24,448, cỡ 363 x 48, chữ "The lesson server is not responding. Your projects still work.", kiểu bodyLarge (16/24, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "Button", vị trí 138,512, cỡ 135 x 48, nền #3D6A00 (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 158,526, cỡ 20 x 20
        - Text "glyph", vị trí 158,526, cỡ 20 x 20, chữ "refresh", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
      - Text "Label", vị trí 186,526, cỡ 63 x 20, chữ "Try again", kiểu ngoài hệ thống (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
  - Frame "BottomNav", vị trí 0,803, cỡ 411 x 88, nền #ECEEF1 (ngoài hệ thống), xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/0/12 (ngoài hệ thống)/0, căn MIN/MIN
    - Instance "NavItem", vị trí 0,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 71,817, cỡ 64 x 32, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 91,821, cỡ 24 x 24
          - Text "glyph", vị trí 91,821, cỡ 24 x 24, chữ "animation", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Label", vị trí 79,853, cỡ 47 x 16, chữ "Projects", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (ngoài hệ thống)
    - Instance "NavItem", vị trí 206,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 276,817, cỡ 64 x 32, nền #DDF5B8 (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 296,821, cỡ 24 x 24
          - Text "glyph", vị trí 296,821, cỡ 24 x 24, chữ "school", kiểu ngoài hệ thống (24/24, đậm 400), màu #1C2C05 (ngoài hệ thống)
      - Text "Label", vị trí 284,853, cỡ 48 x 16, chữ "Lessons", kiểu labelMedium (12/16, đậm 500), màu #12141A (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
