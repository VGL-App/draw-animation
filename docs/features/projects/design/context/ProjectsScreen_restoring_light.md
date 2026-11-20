# SỐ LIỆU THIẾT KẾ: ProjectsScreen_restoring_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-2434
- Frame: `23:2434`, giao diện sáng
- Ngày lấy: 2026-11-20

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| instance ghi đè component | Icon | màu nền |
| khoảng cách | 10 | ngoài hệ thống |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 14 | ngoài hệ thống |
| khoảng cách | 16 | md |
| khoảng cách | 18 | ngoài hệ thống |
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
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | ngoài hệ thống |
| kiểu chữ | 28/28, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | ngoài hệ thống |
| màu chữ | #1C2C05 | ngoài hệ thống |
| màu chữ | #2F6FE0 | ngoài hệ thống |
| màu chữ | #3D6A00 | ngoài hệ thống |
| màu chữ | #5F6675 | ngoài hệ thống |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest |
| màu nền | #000000 | ngoài hệ thống |
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
| độ mờ của lớp | 0.55 | - |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "ProjectsScreen_restoring_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/4 (xs)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Frame "Wordmark", vị trí 20,44, cỡ 144 x 32, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
      - Ellipse "Ellipse", vị trí 20,53, cỡ 14 x 14, nền #3D6A00 (ngoài hệ thống)
      - Text "Frameloop", vị trí 42,44, cỡ 122 x 32, chữ "Frameloop", kiểu ngoài hệ thống (24/32, đậm 700), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 164,60, cỡ 147 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "IconButton", vị trí 311,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 323,48, cỡ 24 x 24
        - Text "glyph", vị trí 323,48, cỡ 24 x 24, chữ "upload_file", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "IconButton", vị trí 359,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 371,48, cỡ 24 x 24
        - Text "glyph", vị trí 371,48, cỡ 24 x 24, chữ "settings", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
  - Frame "Content", vị trí 0,92, cỡ 411 x 639, xếp dọc, cách nhau 20 (ngoài hệ thống), lề trên/phải/dưới/trái 8 (sm)/16 (md)/0/16 (md), căn MIN/MIN
    - Instance "Button", vị trí 16,100, cỡ 379 x 50, nền #3D6A00 (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 18 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 139,118, cỡ 20 x 20
        - Text "glyph", vị trí 139,118, cỡ 20 x 20, chữ "add", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
      - Text "Label", vị trí 167,118, cỡ 101 x 20, chữ "New animation", kiểu ngoài hệ thống (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
    - Frame "SectionHeader", vị trí 16,170, cỡ 379 x 28, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
      - Text "Your projects", vị trí 16,170, cỡ 142 x 28, chữ "Your projects", kiểu ngoài hệ thống (22/28, đậm 500), màu #12141A (ngoài hệ thống)
      - Frame "spacer", vị trí 158,184, cỡ 189 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Recent", vị trí 347,174, cỡ 48 x 20, chữ "Recent", kiểu ngoài hệ thống (14/20, đậm 600), màu #5F6675 (ngoài hệ thống)
    - Frame "Grid", vị trí 16,218, cỡ 379 x 344, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Frame "Row 1", vị trí 16,218, cỡ 379 x 166, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Instance "ProjectCard", vị trí 16,218, cỡ 184 x 166, nền #ECEEF1 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
          - Frame "Thumbnail", vị trí 24,226, cỡ 168 x 94, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
            - Rectangle "ground", vị trí 41,299, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
            - Ellipse "ball", vị trí 116,279, cỡ 21 x 21, nền #2F6FE0 (ngoài hệ thống)
          - Text "Title", vị trí 24,328, cỡ 91 x 20, chữ "Bouncing ball", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
          - Text "Meta", vị trí 24,356, cỡ 102 x 16, chữ "48 frames · 12 fps", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
        - Instance "ProjectCard", vị trí 212,218, cỡ 184 x 166, nền #ECEEF1 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
          - Frame "Thumbnail", vị trí 220,226, cỡ 168 x 94, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
            - Rectangle "ground", vị trí 236,299, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
            - Ellipse "ball", vị trí 312,279, cỡ 21 x 21, nền #2F6FE0 (ngoài hệ thống)
          - Text "Title", vị trí 220,328, cỡ 72 x 20, chữ "Walk cycle", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
          - Text "Meta", vị trí 220,356, cỡ 102 x 16, chữ "24 frames · 12 fps", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
      - Frame "Row 2", vị trí 16,396, cỡ 379 x 166, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Instance "ProjectCard", vị trí 16,396, cỡ 184 x 166, nền #ECEEF1 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
          - Frame "Thumbnail", vị trí 24,404, cỡ 168 x 94, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
            - Rectangle "ground", vị trí 41,477, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
            - Ellipse "ball", vị trí 116,457, cỡ 21 x 21, nền #2F6FE0 (ngoài hệ thống)
          - Text "Title", vị trí 24,506, cỡ 71 x 20, chữ "Logo sting", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
          - Text "Meta", vị trí 24,534, cỡ 104 x 16, chữ "90 frames · 24 fps", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
        - Instance "ProjectCard", vị trí 212,396, cỡ 184 x 166, nền #ECEEF1 (ngoài hệ thống), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
          - Frame "Thumbnail", vị trí 220,404, cỡ 168 x 94, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 8
            - Rectangle "ground", vị trí 236,477, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
            - Ellipse "ball", vị trí 312,457, cỡ 21 x 21, nền #2F6FE0 (ngoài hệ thống)
          - Text "Title", vị trí 220,506, cỡ 63 x 20, chữ "Blink test", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
          - Text "Meta", vị trí 220,534, cỡ 95 x 16, chữ "16 frames · 8 fps", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
  - Instance "projects_bottom (NativeAdBar)", vị trí 0,731, cỡ 411 x 72, nền #F1F2F5 (ngoài hệ thống), viền #D2D6DC (ngoài hệ thống), dày 1.0, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/16 (md)/10 (ngoài hệ thống)/16 (md), căn MIN/CENTER
    - Rectangle "Ad media", vị trí 16,744, cỡ 48 x 48, nền #D9DDE2 (ngoài hệ thống), bo góc 8
    - Frame "Text", vị trí 76,748, cỡ 236 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Frame "Headline row", vị trí 76,748, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Frame "Ad badge", vị trí 76,748, cỡ 27 x 20, nền #D9DDE2 (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
          - Text "Ad", vị trí 82,750, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (ngoài hệ thống)
        - Text "Headline", vị trí 109,748, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
      - Text "Body", vị trí 76,770, cỡ 236 x 16, chữ "Native ad, bottom bar", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Frame "Ad CTA", vị trí 324,750, cỡ 71 x 36, viền #2F6FE0 (ngoài hệ thống), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
      - Text "Install", vị trí 339,758, cỡ 41 x 20, chữ "Install", kiểu ngoài hệ thống (14/20, đậm 600), màu #2F6FE0 (ngoài hệ thống)
  - Frame "BottomNav", vị trí 0,803, cỡ 411 x 88, nền #ECEEF1 (ngoài hệ thống), xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/0/12 (ngoài hệ thống)/0, căn SPACE_BETWEEN/MIN
    - Instance "NavItem", vị trí 0,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 71,817, cỡ 64 x 32, nền #DDF5B8 (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 91,821, cỡ 24 x 24
          - Text "glyph", vị trí 91,821, cỡ 24 x 24, chữ "animation", kiểu ngoài hệ thống (24/24, đậm 400), màu #1C2C05 (ngoài hệ thống)
      - Text "Label", vị trí 79,853, cỡ 47 x 16, chữ "Projects", kiểu labelMedium (12/16, đậm 500), màu #12141A (ngoài hệ thống)
    - Instance "NavItem", vị trí 206,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 276,817, cỡ 64 x 32, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 296,821, cỡ 24 x 24
          - Text "glyph", vị trí 296,821, cỡ 24 x 24, chữ "school", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Label", vị trí 284,853, cỡ 48 x 16, chữ "Lessons", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (ngoài hệ thống)
  - Rectangle "Scrim", vị trí 0,0, cỡ 411 x 891, nền #000000 (ngoài hệ thống), độ mờ của lớp 0.55
  - Frame "RestoreProgress", vị trí 32,197, cỡ 348 x 498, nền #E3E6EA (ngoài hệ thống), bo góc 20, xếp dọc, cách nhau 16 (md), lề trên/phải/dưới/trái 24 (lg)/24 (lg)/24 (lg)/24 (lg), căn MIN/MIN
    - Instance "Icon", vị trí 56,221, cỡ 28 x 28, CHÚ Ý: ghi đè màu nền của component gốc
      - Text "glyph", vị trí 56,221, cỡ 28 x 28, chữ "restore", kiểu ngoài hệ thống (28/28, đậm 400), màu #3D6A00 (ngoài hệ thống)
    - Text "Restoring “Walk cycle”", vị trí 56,265, cỡ 300 x 28, chữ "Restoring “Walk cycle”", kiểu ngoài hệ thống (22/28, đậm 500), màu #12141A (ngoài hệ thống)
    - Text "Keep the app open. This ", vị trí 56,309, cỡ 300 x 40, chữ "Keep the app open. This takes a few seconds.", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "ProgressBar", vị trí 56,365, cỡ 300 x 8, nền #E3E6EA (ngoài hệ thống), bo góc 999
      - Rectangle "Value", vị trí 56,365, cỡ 168 x 8, nền #3D6A00 (ngoài hệ thống), bo góc 4
    - Frame "Meta", vị trí 56,389, cỡ 300 x 16, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "48 of 120 frames", vị trí 56,389, cỡ 98 x 16, chữ "48 of 120 frames", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (ngoài hệ thống)
      - Frame "spacer", vị trí 154,389, cỡ 176 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "40%", vị trí 330,389, cỡ 26 x 16, chữ "40%", kiểu labelMedium (12/16, đậm 500), màu #12141A (ngoài hệ thống)
    - Frame "restore_mrec", vị trí 56,421, cỡ 300 x 250, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/MIN
      - Instance "MrecSlot", vị trí 56,421, cỡ 300 x 250, nền #F1F2F5 (ngoài hệ thống), viền #D2D6DC (ngoài hệ thống), dày 1.0, bo góc 4
        - Text "Label", vị trí 135,538, cỡ 142 x 16, chữ "Banner MREC 300 × 250", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (ngoài hệ thống)
        - Frame "Ad badge", vị trí 62,427, cỡ 27 x 20, nền #D9DDE2 (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
          - Text "Ad", vị trí 68,429, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
