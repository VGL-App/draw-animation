# SỐ LIỆU THIẾT KẾ: ProjectsScreen_list_dark_unfolded

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=10-541
- Frame: `10:541`, giao diện tối
- Ngày lấy: 2026-11-20

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
| khoảng cách | 24 | lg |
| khoảng cách | 4 | xs |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 11/16, đậm 500 | labelSmall |
| kiểu chữ | 12/16, đậm 400 | bodySmall |
| kiểu chữ | 12/16, đậm 500 | labelMedium |
| kiểu chữ | 14/20, đậm 500 | titleSmall, labelLarge |
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | ngoài hệ thống |
| màu chữ | #10140A | ngoài hệ thống |
| màu chữ | #6EA8FF | ngoài hệ thống |
| màu chữ | #8C93A3 | ngoài hệ thống |
| màu chữ | #D8FA9E | ngoài hệ thống |
| màu chữ | #EEF0F4 | ngoài hệ thống |
| màu nền | #0E1014 | ngoài hệ thống |
| màu nền | #13161B | ngoài hệ thống |
| màu nền | #171A20 | ngoài hệ thống |
| màu nền | #1A1C22 | ngoài hệ thống |
| màu nền | #2B303A | ngoài hệ thống |
| màu nền | #2E3D17 | ngoài hệ thống |
| màu nền | #6EA8FF | ngoài hệ thống |
| màu nền | #B6F35C | ngoài hệ thống |
| màu nền | #FAFAF7 | ngoài hệ thống |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "ProjectsScreen_list_dark_unfolded", vị trí 0,0, cỡ 673 x 841, nền #0E1014 (ngoài hệ thống), xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "NavRail", vị trí 0,0, cỡ 96 x 841, nền #171A20 (ngoài hệ thống), xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 24 (lg)/0/0/0, căn MIN/CENTER
    - Ellipse "Ellipse", vị trí 40,24, cỡ 16 x 16, nền #B6F35C (ngoài hệ thống)
    - Instance "NavItem", vị trí 0,52, cỡ 96 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 16,62, cỡ 64 x 32, nền #2E3D17 (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 36,66, cỡ 24 x 24
          - Text "glyph", vị trí 36,66, cỡ 24 x 24, chữ "animation", kiểu ngoài hệ thống (24/24, đậm 400), màu #D8FA9E (ngoài hệ thống)
      - Text "Label", vị trí 24,98, cỡ 47 x 16, chữ "Projects", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (ngoài hệ thống)
    - Instance "NavItem", vị trí 0,136, cỡ 96 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 16,146, cỡ 64 x 32, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 36,150, cỡ 24 x 24
          - Text "glyph", vị trí 36,150, cỡ 24 x 24, chữ "school", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Text "Label", vị trí 24,182, cỡ 48 x 16, chữ "Lessons", kiểu labelMedium (12/16, đậm 500), màu #8C93A3 (ngoài hệ thống)
  - Frame "Main", vị trí 96,0, cỡ 577 x 841, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "StatusBar", vị trí 96,0, cỡ 577 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
      - Text "9:41", vị trí 116,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #EEF0F4 (ngoài hệ thống)
      - Frame "spacer", vị trí 150,14, cỡ 443 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "Icon", vị trí 597,6, cỡ 16 x 16
        - Text "glyph", vị trí 597,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Instance "Icon", vị trí 617,6, cỡ 16 x 16
        - Text "glyph", vị trí 617,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Instance "Icon", vị trí 637,6, cỡ 16 x 16
        - Text "glyph", vị trí 637,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Frame "TopBar", vị trí 96,28, cỡ 577 x 64, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/8 (sm)/0/24 (lg), căn MIN/CENTER
      - Text "Frameloop", vị trí 120,44, cỡ 122 x 32, chữ "Frameloop", kiểu ngoài hệ thống (24/32, đậm 700), màu #EEF0F4 (ngoài hệ thống)
      - Frame "spacer", vị trí 242,60, cỡ 154 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Instance "Button", vị trí 396,36, cỡ 173 x 48, nền #B6F35C (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
        - Instance "Leading icon", vị trí 416,50, cỡ 20 x 20
          - Text "glyph", vị trí 416,50, cỡ 20 x 20, chữ "add", kiểu ngoài hệ thống (20/20, đậm 400), màu #10140A (ngoài hệ thống)
        - Text "Label", vị trí 444,50, cỡ 101 x 20, chữ "New animation", kiểu ngoài hệ thống (14/20, đậm 600), màu #10140A (ngoài hệ thống)
      - Instance "IconButton", vị trí 569,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 581,48, cỡ 24 x 24
          - Text "glyph", vị trí 581,48, cỡ 24 x 24, chữ "upload_file", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Instance "IconButton", vị trí 617,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
        - Instance "Glyph", vị trí 629,48, cỡ 24 x 24
          - Text "glyph", vị trí 629,48, cỡ 24 x 24, chữ "settings", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Frame "Content", vị trí 96,92, cỡ 577 x 677, xếp dọc, cách nhau 16 (md), lề trên/phải/dưới/trái 8 (sm)/24 (lg)/0/24 (lg), căn MIN/MIN
      - Text "Your projects", vị trí 120,100, cỡ 142 x 28, chữ "Your projects", kiểu ngoài hệ thống (22/28, đậm 500), màu #EEF0F4 (ngoài hệ thống)
      - Frame "Grid", vị trí 120,144, cỡ 529 x 344, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Frame "Row 1", vị trí 120,144, cỡ 529 x 166, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Instance "ProjectCard", vị trí 120,144, cỡ 168 x 166, nền #171A20 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
            - Frame "Thumbnail", vị trí 128,152, cỡ 152 x 94, nền #FAFAF7 (ngoài hệ thống), bo góc 8
              - Rectangle "ground", vị trí 145,225, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
              - Ellipse "ball", vị trí 220,205, cỡ 21 x 21, nền #6EA8FF (ngoài hệ thống)
            - Text "Title", vị trí 128,254, cỡ 91 x 20, chữ "Bouncing ball", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
            - Text "Meta", vị trí 128,282, cỡ 102 x 16, chữ "48 frames · 12 fps", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
          - Instance "ProjectCard", vị trí 300,144, cỡ 168 x 166, nền #171A20 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
            - Frame "Thumbnail", vị trí 308,152, cỡ 152 x 94, nền #FAFAF7 (ngoài hệ thống), bo góc 8
              - Rectangle "ground", vị trí 325,225, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
              - Ellipse "ball", vị trí 401,205, cỡ 21 x 21, nền #6EA8FF (ngoài hệ thống)
            - Text "Title", vị trí 308,254, cỡ 72 x 20, chữ "Walk cycle", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
            - Text "Meta", vị trí 308,282, cỡ 102 x 16, chữ "24 frames · 12 fps", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
          - Instance "ProjectCard", vị trí 481,144, cỡ 168 x 166, nền #171A20 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
            - Frame "Thumbnail", vị trí 489,152, cỡ 152 x 94, nền #FAFAF7 (ngoài hệ thống), bo góc 8
              - Rectangle "ground", vị trí 505,225, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
              - Ellipse "ball", vị trí 581,205, cỡ 21 x 21, nền #6EA8FF (ngoài hệ thống)
            - Text "Title", vị trí 489,254, cỡ 71 x 20, chữ "Logo sting", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
            - Text "Meta", vị trí 489,282, cỡ 104 x 16, chữ "90 frames · 24 fps", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
        - Frame "Row 2", vị trí 120,322, cỡ 529 x 166, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Instance "ProjectCard", vị trí 120,322, cỡ 168 x 166, nền #171A20 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
            - Frame "Thumbnail", vị trí 128,330, cỡ 152 x 94, nền #FAFAF7 (ngoài hệ thống), bo góc 8
              - Rectangle "ground", vị trí 145,403, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
              - Ellipse "ball", vị trí 220,383, cỡ 21 x 21, nền #6EA8FF (ngoài hệ thống)
            - Text "Title", vị trí 128,432, cỡ 63 x 20, chữ "Blink test", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
            - Text "Meta", vị trí 128,460, cỡ 95 x 16, chữ "16 frames · 8 fps", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
          - Instance "ProjectCard", vị trí 300,322, cỡ 168 x 166, nền #171A20 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
            - Frame "Thumbnail", vị trí 308,330, cỡ 152 x 94, nền #FAFAF7 (ngoài hệ thống), bo góc 8
              - Rectangle "ground", vị trí 325,403, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
              - Ellipse "ball", vị trí 401,383, cỡ 21 x 21, nền #6EA8FF (ngoài hệ thống)
            - Text "Title", vị trí 308,432, cỡ 62 x 20, chữ "Rain loop", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
            - Text "Meta", vị trí 308,460, cỡ 102 x 16, chữ "32 frames · 12 fps", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
          - Instance "ProjectCard", vị trí 481,322, cỡ 168 x 166, nền #171A20 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
            - Frame "Thumbnail", vị trí 489,330, cỡ 152 x 94, nền #FAFAF7 (ngoài hệ thống), bo góc 8
              - Rectangle "ground", vị trí 505,403, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
              - Ellipse "ball", vị trí 581,383, cỡ 21 x 21, nền #6EA8FF (ngoài hệ thống)
            - Text "Title", vị trí 489,432, cỡ 38 x 20, chữ "Jump", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
            - Text "Meta", vị trí 489,460, cỡ 102 x 16, chữ "20 frames · 12 fps", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
    - Instance "projects_bottom (NativeAdBar)", vị trí 96,769, cỡ 577 x 72, nền #13161B (ngoài hệ thống), viền #2C313B (ngoài hệ thống), dày 1.0, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/16 (md)/10 (ngoài hệ thống)/16 (md), căn MIN/CENTER
      - Rectangle "Ad media", vị trí 112,782, cỡ 48 x 48, nền #2B303A (ngoài hệ thống), bo góc 8
      - Frame "Text", vị trí 172,786, cỡ 402 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Frame "Headline row", vị trí 172,786, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
          - Frame "Ad badge", vị trí 172,786, cỡ 27 x 20, nền #2B303A (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
            - Text "Ad", vị trí 178,788, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #8C93A3 (ngoài hệ thống)
          - Text "Headline", vị trí 205,786, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
        - Text "Body", vị trí 172,808, cỡ 402 x 16, chữ "Native ad, bottom bar", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
      - Frame "Ad CTA", vị trí 586,788, cỡ 71 x 36, viền #6EA8FF (ngoài hệ thống), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Install", vị trí 601,796, cỡ 41 x 20, chữ "Install", kiểu ngoài hệ thống (14/20, đậm 600), màu #6EA8FF (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
