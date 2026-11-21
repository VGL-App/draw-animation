# SỐ LIỆU THIẾT KẾ: LessonsScreen_list_dark

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=10-823
- Frame: `10:823`, giao diện tối
- Ngày lấy: 2026-11-21

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| khoảng cách | 10 | ngoài hệ thống |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 14 | ngoài hệ thống |
| khoảng cách | 16 | md |
| khoảng cách | 2 | ngoài hệ thống |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 4 | xs |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 11/16, đậm 500 | labelSmall |
| kiểu chữ | 12/16, đậm 400 | bodySmall |
| kiểu chữ | 12/16, đậm 500 | labelMedium |
| kiểu chữ | 14/14, đậm 400 | ngoài hệ thống |
| kiểu chữ | 14/20, đậm 500 | titleSmall, labelLarge |
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | ngoài hệ thống |
| màu chữ | #10140A | ngoài hệ thống |
| màu chữ | #6EA8FF | ngoài hệ thống |
| màu chữ | #8C93A3 | ngoài hệ thống |
| màu chữ | #B6F35C | ngoài hệ thống |
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
| màu nền | #FAFAF7 | ngoài hệ thống |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "LessonsScreen_list_dark", vị trí 0,0, cỡ 411 x 891, nền #0E1014 (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #EEF0F4 (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "Lessons", vị trí 20,44, cỡ 95 x 32, chữ "Lessons", kiểu ngoài hệ thống (24/32, đậm 700), màu #EEF0F4 (ngoài hệ thống)
    - Frame "spacer", vị trí 119,60, cỡ 284 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Filters", vị trí 0,92, cỡ 411 x 36, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/16 (md)/8 (sm)/16 (md), căn MIN/MIN
    - Instance "Chip", vị trí 16,92, cỡ 39 x 28, nền #2E3D17 (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Text "Label", vị trí 28,98, cỡ 15 x 16, chữ "All", kiểu labelMedium (12/16, đậm 500), màu #D8FA9E (ngoài hệ thống)
    - Instance "Chip", vị trí 63,92, cỡ 62 x 28, nền #22262E (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Text "Label", vị trí 75,98, cỡ 38 x 16, chữ "Basics", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Chip", vị trí 133,92, cỡ 64 x 28, nền #22262E (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Text "Label", vị trí 145,98, cỡ 40 x 16, chữ "Motion", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Chip", vị trí 205,92, cỡ 88 x 28, nền #22262E (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Text "Label", vị trí 217,98, cỡ 64 x 16, chữ "Characters", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Chip", vị trí 301,92, cỡ 65 x 28, nền #22262E (ngoài hệ thống), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
      - Text "Label", vị trí 313,98, cỡ 41 x 16, chữ "Effects", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (ngoài hệ thống)
  - Frame "Content", vị trí 0,128, cỡ 411 x 603, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 8 (sm)/16 (md)/0/16 (md), căn MIN/MIN
    - Frame "SectionHeader", vị trí 16,136, cỡ 379 x 28, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
      - Text "Basics", vị trí 16,136, cỡ 69 x 28, chữ "Basics", kiểu ngoài hệ thống (22/28, đậm 500), màu #EEF0F4 (ngoài hệ thống)
      - Frame "spacer", vị trí 85,150, cỡ 273 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "1 of 5", vị trí 358,140, cỡ 37 x 20, chữ "1 of 5", kiểu ngoài hệ thống (14/20, đậm 600), màu #8C93A3 (ngoài hệ thống)
    - Frame "Basics row", vị trí 16,176, cỡ 379 x 194, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "LessonCard", vị trí 16,176, cỡ 184 x 184, nền #171A20 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
        - Frame "Thumbnail", vị trí 24,184, cỡ 168 x 94, nền #FAFAF7 (ngoài hệ thống), bo góc 8
          - Rectangle "ground", vị trí 41,257, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
          - Ellipse "ball", vị trí 74,237, cỡ 21 x 21, nền #6EA8FF (ngoài hệ thống)
          - Frame "Done badge", vị trí 32,192, cỡ 58 x 20, nền #B6F35C (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 2 (ngoài hệ thống)/8 (sm)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/CENTER
            - Instance "Icon", vị trí 38,195, cỡ 14 x 14
              - Text "glyph", vị trí 38,195, cỡ 14 x 14, chữ "check", kiểu ngoài hệ thống (14/14, đậm 400), màu #10140A (ngoài hệ thống)
            - Text "Done", vị trí 54,194, cỡ 28 x 16, chữ "Done", kiểu labelSmall (11/16, đậm 500), màu #10140A (ngoài hệ thống)
        - Text "Level", vị trí 24,284, cỡ 49 x 16, chữ "Basics · 1", kiểu labelSmall (11/16, đậm 500), màu #B6F35C (ngoài hệ thống)
        - Text "Title", vị trí 24,306, cỡ 91 x 20, chữ "Bouncing ball", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
        - Text "Meta", vị trí 24,332, cỡ 97 x 16, chữ "5 min · 12 frames", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
      - Instance "LessonCard", vị trí 212,176, cỡ 184 x 194, nền #171A20 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
        - Frame "Thumbnail", vị trí 220,184, cỡ 168 x 94, nền #FAFAF7 (ngoài hệ thống), bo góc 8
          - Rectangle "ground", vị trí 236,257, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
          - Ellipse "ball", vị trí 270,237, cỡ 21 x 21, nền #6EA8FF (ngoài hệ thống)
        - Text "Level", vị trí 220,284, cỡ 50 x 16, chữ "Basics · 2", kiểu labelSmall (11/16, đậm 500), màu #B6F35C (ngoài hệ thống)
        - Text "Title", vị trí 220,306, cỡ 130 x 20, chữ "Squash and stretch", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
        - Text "Meta", vị trí 220,332, cỡ 97 x 16, chữ "6 min · 16 frames", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
        - Instance "Progress", vị trí 220,354, cỡ 168 x 4, nền #22262E (ngoài hệ thống), bo góc 999
          - Rectangle "Value", vị trí 220,354, cỡ 168 x 8, nền #B6F35C (ngoài hệ thống), bo góc 4
    - Frame "SectionHeader", vị trí 16,382, cỡ 379 x 28, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
      - Text "Motion", vị trí 16,382, cỡ 76 x 28, chữ "Motion", kiểu ngoài hệ thống (22/28, đậm 500), màu #EEF0F4 (ngoài hệ thống)
      - Frame "spacer", vị trí 92,396, cỡ 263 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "0 of 4", vị trí 355,386, cỡ 40 x 20, chữ "0 of 4", kiểu ngoài hệ thống (14/20, đậm 600), màu #8C93A3 (ngoài hệ thống)
    - Frame "Motion row", vị trí 16,422, cỡ 379 x 184, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "LessonCard", vị trí 16,422, cỡ 184 x 184, nền #171A20 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
        - Frame "Thumbnail", vị trí 24,430, cỡ 168 x 94, nền #FAFAF7 (ngoài hệ thống), bo góc 8
          - Rectangle "ground", vị trí 41,503, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
          - Ellipse "ball", vị trí 74,483, cỡ 21 x 21, nền #6EA8FF (ngoài hệ thống)
        - Text "Level", vị trí 24,530, cỡ 51 x 16, chữ "Motion · 1", kiểu labelSmall (11/16, đậm 500), màu #B6F35C (ngoài hệ thống)
        - Text "Title", vị trí 24,552, cỡ 111 x 20, chữ "Stick figure walk", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
        - Text "Meta", vị trí 24,578, cỡ 99 x 16, chữ "8 min · 24 frames", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
      - Instance "LessonCard", vị trí 212,422, cỡ 184 x 184, nền #171A20 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
        - Frame "Thumbnail", vị trí 220,430, cỡ 168 x 94, nền #FAFAF7 (ngoài hệ thống), bo góc 8
          - Rectangle "ground", vị trí 236,503, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
          - Ellipse "ball", vị trí 270,483, cỡ 21 x 21, nền #6EA8FF (ngoài hệ thống)
        - Text "Level", vị trí 220,530, cỡ 53 x 16, chữ "Motion · 2", kiểu labelSmall (11/16, đậm 500), màu #B6F35C (ngoài hệ thống)
        - Text "Title", vị trí 220,552, cỡ 93 x 20, chữ "Blink and look", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
        - Text "Meta", vị trí 220,578, cỡ 98 x 16, chữ "4 min · 10 frames", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
  - Instance "lessons_bottom (NativeAdBar)", vị trí 0,731, cỡ 411 x 72, nền #13161B (ngoài hệ thống), viền #2C313B (ngoài hệ thống), dày 1.0, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/16 (md)/10 (ngoài hệ thống)/16 (md), căn MIN/CENTER
    - Rectangle "Ad media", vị trí 16,744, cỡ 48 x 48, nền #2B303A (ngoài hệ thống), bo góc 8
    - Frame "Text", vị trí 76,748, cỡ 236 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Frame "Headline row", vị trí 76,748, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Frame "Ad badge", vị trí 76,748, cỡ 27 x 20, nền #2B303A (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
          - Text "Ad", vị trí 82,750, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #8C93A3 (ngoài hệ thống)
        - Text "Headline", vị trí 109,748, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
      - Text "Body", vị trí 76,770, cỡ 236 x 16, chữ "Native ad, bottom bar", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
    - Frame "Ad CTA", vị trí 324,750, cỡ 71 x 36, viền #6EA8FF (ngoài hệ thống), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
      - Text "Install", vị trí 339,758, cỡ 41 x 20, chữ "Install", kiểu ngoài hệ thống (14/20, đậm 600), màu #6EA8FF (ngoài hệ thống)
  - Frame "BottomNav", vị trí 0,803, cỡ 411 x 88, nền #171A20 (ngoài hệ thống), xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/0/12 (ngoài hệ thống)/0, căn MIN/MIN
    - Instance "NavItem", vị trí 0,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 71,817, cỡ 64 x 32, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 91,821, cỡ 24 x 24
          - Text "glyph", vị trí 91,821, cỡ 24 x 24, chữ "animation", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Text "Label", vị trí 79,853, cỡ 47 x 16, chữ "Projects", kiểu labelMedium (12/16, đậm 500), màu #8C93A3 (ngoài hệ thống)
    - Instance "NavItem", vị trí 206,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 276,817, cỡ 64 x 32, nền #2E3D17 (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 296,821, cỡ 24 x 24
          - Text "glyph", vị trí 296,821, cỡ 24 x 24, chữ "school", kiểu ngoài hệ thống (24/24, đậm 400), màu #D8FA9E (ngoài hệ thống)
      - Text "Label", vị trí 284,853, cỡ 48 x 16, chữ "Lessons", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
