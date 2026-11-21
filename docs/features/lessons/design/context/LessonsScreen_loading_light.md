# SỐ LIỆU THIẾT KẾ: LessonsScreen_loading_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-3415
- Frame: `23:3415`, giao diện sáng
- Ngày lấy: 2026-11-22

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| khoảng cách | 10 | ngoài hệ thống |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 16 | md |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 4 | xs |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 12/16, đậm 500 | labelMedium |
| kiểu chữ | 14/20, đậm 600 | labelLarge |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | headlineSmall |
| màu chữ | #12141A | onBackground, onSurface |
| màu chữ | #1C2C05 | onPrimaryContainer |
| màu chữ | #5F6675 | onSurfaceVariant |
| màu nền | #DDF5B8 | primaryContainer |
| màu nền | #E3E6EA | surfaceVariant, surfaceContainerHigh |
| màu nền | #ECEEF1 | surfaceContainer |
| màu nền | #F7F8FA | background, surface, surfaceBright |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "LessonsScreen_loading_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (background, surface, surfaceBright), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "Lessons", vị trí 20,44, cỡ 95 x 32, chữ "Lessons", kiểu headlineSmall (24/32, đậm 700), màu #12141A (onBackground, onSurface)
    - Frame "spacer", vị trí 119,60, cỡ 284 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Filters", vị trí 0,92, cỡ 411 x 36, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/16 (md)/8 (sm)/16 (md), căn MIN/MIN
    - Instance "Chip", vị trí 16,92, cỡ 39 x 28, nền #DDF5B8 (primaryContainer), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Text "Label", vị trí 28,98, cỡ 15 x 16, chữ "All", kiểu labelMedium (12/16, đậm 500), màu #1C2C05 (onPrimaryContainer)
    - Instance "Chip", vị trí 63,92, cỡ 62 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Text "Label", vị trí 75,98, cỡ 38 x 16, chữ "Basics", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
    - Instance "Chip", vị trí 133,92, cỡ 64 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Text "Label", vị trí 145,98, cỡ 40 x 16, chữ "Motion", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
    - Instance "Chip", vị trí 205,92, cỡ 88 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Text "Label", vị trí 217,98, cỡ 64 x 16, chữ "Characters", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
    - Instance "Chip", vị trí 301,92, cỡ 65 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Text "Label", vị trí 313,98, cỡ 41 x 16, chữ "Effects", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
  - Frame "Content", vị trí 0,128, cỡ 411 x 675, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 8 (sm)/16 (md)/0/16 (md), căn MIN/MIN
    - Rectangle "Skeleton title", vị trí 16,136, cỡ 120 x 24, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 6
    - Frame "Skeleton row", vị trí 16,172, cỡ 379 x 172, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Frame "Skeleton card", vị trí 16,172, cỡ 184 x 172, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
        - Rectangle "Rectangle", vị trí 24,180, cỡ 168 x 94, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 24,282, cỡ 60 x 10, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 24,300, cỡ 120 x 14, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 24,322, cỡ 90 x 10, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
      - Frame "Skeleton card", vị trí 212,172, cỡ 184 x 172, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
        - Rectangle "Rectangle", vị trí 220,180, cỡ 168 x 94, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 220,282, cỡ 60 x 10, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 220,300, cỡ 120 x 14, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 220,322, cỡ 90 x 10, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
    - Rectangle "Skeleton title", vị trí 16,356, cỡ 120 x 24, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 6
    - Frame "Skeleton row", vị trí 16,392, cỡ 379 x 172, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Frame "Skeleton card", vị trí 16,392, cỡ 184 x 172, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
        - Rectangle "Rectangle", vị trí 24,400, cỡ 168 x 94, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 24,502, cỡ 60 x 10, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 24,520, cỡ 120 x 14, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 24,542, cỡ 90 x 10, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
      - Frame "Skeleton card", vị trí 212,392, cỡ 184 x 172, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
        - Rectangle "Rectangle", vị trí 220,400, cỡ 168 x 94, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 220,502, cỡ 60 x 10, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 220,520, cỡ 120 x 14, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
        - Rectangle "Rectangle", vị trí 220,542, cỡ 90 x 10, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 4
  - Frame "BottomNav", vị trí 0,803, cỡ 411 x 88, nền #ECEEF1 (surfaceContainer), xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/0/12 (ngoài hệ thống)/0, căn MIN/MIN
    - Instance "NavItem", vị trí 0,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 71,817, cỡ 64 x 32, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 91,821, cỡ 24 x 24
          - Text "glyph", vị trí 91,821, cỡ 24 x 24, chữ "animation", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Text "Label", vị trí 79,853, cỡ 47 x 16, chữ "Projects", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (onSurfaceVariant)
    - Instance "NavItem", vị trí 206,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 276,817, cỡ 64 x 32, nền #DDF5B8 (primaryContainer), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 296,821, cỡ 24 x 24
          - Text "glyph", vị trí 296,821, cỡ 24 x 24, chữ "school", kiểu ngoài hệ thống (24/24, đậm 400), màu #1C2C05 (onPrimaryContainer)
      - Text "Label", vị trí 284,853, cỡ 48 x 16, chữ "Lessons", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
