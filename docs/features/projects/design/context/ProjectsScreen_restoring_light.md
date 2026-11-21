# SỐ LIỆU THIẾT KẾ: ProjectsScreen_restoring_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-2434
- Frame: `23:2434`, giao diện sáng
- Ngày lấy: 2026-11-22

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
| kiểu chữ | 14/20, đậm 500 | titleSmall |
| kiểu chữ | 14/20, đậm 600 | labelLarge |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | titleLarge |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | headlineSmall |
| kiểu chữ | 28/28, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | onBackground, onSurface |
| màu chữ | #1C2C05 | onPrimaryContainer |
| màu chữ | #2F6FE0 | secondary |
| màu chữ | #3D6A00 | primary, surfaceTint |
| màu chữ | #5F6675 | onSurfaceVariant |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |
| màu nền | #000000 | ngoài hệ thống |
| màu nền | #1A1C22 | onCanvas |
| màu nền | #2F6FE0 | secondary |
| màu nền | #3D6A00 | primary, surfaceTint |
| màu nền | #D9DDE2 | surfaceContainerHighest |
| màu nền | #DDF5B8 | primaryContainer |
| màu nền | #E3E6EA | surfaceVariant, surfaceContainerHigh |
| màu nền | #ECEEF1 | surfaceContainer |
| màu nền | #F1F2F5 | surfaceContainerLow |
| màu nền | #F7F8FA | background, surface, surfaceBright |
| màu nền | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |
| độ mờ của lớp | 0.55 | - |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "ProjectsScreen_restoring_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (background, surface, surfaceBright), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/4 (xs)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Frame "Wordmark", vị trí 20,44, cỡ 144 x 32, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
      - Ellipse "Ellipse", vị trí 20,53, cỡ 14 x 14, nền #3D6A00 (primary, surfaceTint)
      - Text "Frameloop", vị trí 42,44, cỡ 122 x 32, chữ "Frameloop", kiểu headlineSmall (24/32, đậm 700), màu #12141A (onBackground, onSurface)
    - Frame "spacer", vị trí 164,60, cỡ 147 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "IconButton", vị trí 311,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 323,48, cỡ 24 x 24
        - Text "glyph", vị trí 323,48, cỡ 24 x 24, chữ "upload_file", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "IconButton", vị trí 359,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 371,48, cỡ 24 x 24
        - Text "glyph", vị trí 371,48, cỡ 24 x 24, chữ "settings", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
  - Frame "Content", vị trí 0,92, cỡ 411 x 639, xếp dọc, cách nhau 20 (ngoài hệ thống), lề trên/phải/dưới/trái 8 (sm)/16 (md)/0/16 (md), căn MIN/MIN
    - Instance "Button", vị trí 16,100, cỡ 379 x 50, nền #3D6A00 (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 18 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 139,118, cỡ 20 x 20
        - Text "glyph", vị trí 139,118, cỡ 20 x 20, chữ "add", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Text "Label", vị trí 167,118, cỡ 101 x 20, chữ "New animation", kiểu labelLarge (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
    - Frame "SectionHeader", vị trí 16,170, cỡ 379 x 28, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
      - Text "Your projects", vị trí 16,170, cỡ 142 x 28, chữ "Your projects", kiểu titleLarge (22/28, đậm 500), màu #12141A (onBackground, onSurface)
      - Frame "spacer", vị trí 158,184, cỡ 189 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Recent", vị trí 347,174, cỡ 48 x 20, chữ "Recent", kiểu labelLarge (14/20, đậm 600), màu #5F6675 (onSurfaceVariant)
    - Frame "Grid", vị trí 16,218, cỡ 379 x 344, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Frame "Row 1", vị trí 16,218, cỡ 379 x 166, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Instance "ProjectCard", vị trí 16,218, cỡ 184 x 166, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
          - Frame "Thumbnail", vị trí 24,226, cỡ 168 x 94, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
            - Rectangle "ground", vị trí 41,299, cỡ 134 x 2, nền #1A1C22 (onCanvas)
            - Ellipse "ball", vị trí 116,279, cỡ 21 x 21, nền #2F6FE0 (secondary)
          - Text "Title", vị trí 24,328, cỡ 91 x 20, chữ "Bouncing ball", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
          - Text "Meta", vị trí 24,356, cỡ 102 x 16, chữ "48 frames · 12 fps", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
        - Instance "ProjectCard", vị trí 212,218, cỡ 184 x 166, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
          - Frame "Thumbnail", vị trí 220,226, cỡ 168 x 94, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
            - Rectangle "ground", vị trí 236,299, cỡ 134 x 2, nền #1A1C22 (onCanvas)
            - Ellipse "ball", vị trí 312,279, cỡ 21 x 21, nền #2F6FE0 (secondary)
          - Text "Title", vị trí 220,328, cỡ 72 x 20, chữ "Walk cycle", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
          - Text "Meta", vị trí 220,356, cỡ 102 x 16, chữ "24 frames · 12 fps", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
      - Frame "Row 2", vị trí 16,396, cỡ 379 x 166, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Instance "ProjectCard", vị trí 16,396, cỡ 184 x 166, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
          - Frame "Thumbnail", vị trí 24,404, cỡ 168 x 94, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
            - Rectangle "ground", vị trí 41,477, cỡ 134 x 2, nền #1A1C22 (onCanvas)
            - Ellipse "ball", vị trí 116,457, cỡ 21 x 21, nền #2F6FE0 (secondary)
          - Text "Title", vị trí 24,506, cỡ 71 x 20, chữ "Logo sting", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
          - Text "Meta", vị trí 24,534, cỡ 104 x 16, chữ "90 frames · 24 fps", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
        - Instance "ProjectCard", vị trí 212,396, cỡ 184 x 166, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 8 (sm)/8 (sm)/12 (ngoài hệ thống)/8 (sm), căn MIN/MIN
          - Frame "Thumbnail", vị trí 220,404, cỡ 168 x 94, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
            - Rectangle "ground", vị trí 236,477, cỡ 134 x 2, nền #1A1C22 (onCanvas)
            - Ellipse "ball", vị trí 312,457, cỡ 21 x 21, nền #2F6FE0 (secondary)
          - Text "Title", vị trí 220,506, cỡ 63 x 20, chữ "Blink test", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
          - Text "Meta", vị trí 220,534, cỡ 95 x 16, chữ "16 frames · 8 fps", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
  - Instance "projects_bottom (NativeAdBar)", vị trí 0,731, cỡ 411 x 72, nền #F1F2F5 (surfaceContainerLow), viền #D2D6DC (outlineVariant), dày 1.0, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/16 (md)/10 (ngoài hệ thống)/16 (md), căn MIN/CENTER
    - Rectangle "Ad media", vị trí 16,744, cỡ 48 x 48, nền #D9DDE2 (surfaceContainerHighest), bo góc 8
    - Frame "Text", vị trí 76,748, cỡ 236 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Frame "Headline row", vị trí 76,748, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Frame "Ad badge", vị trí 76,748, cỡ 27 x 20, nền #D9DDE2 (surfaceContainerHighest), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
          - Text "Ad", vị trí 82,750, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (onSurfaceVariant)
        - Text "Headline", vị trí 109,748, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
      - Text "Body", vị trí 76,770, cỡ 236 x 16, chữ "Native ad, bottom bar", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
    - Frame "Ad CTA", vị trí 324,750, cỡ 71 x 36, viền #2F6FE0 (secondary), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
      - Text "Install", vị trí 339,758, cỡ 41 x 20, chữ "Install", kiểu labelLarge (14/20, đậm 600), màu #2F6FE0 (secondary)
  - Frame "BottomNav", vị trí 0,803, cỡ 411 x 88, nền #ECEEF1 (surfaceContainer), xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/0/12 (ngoài hệ thống)/0, căn SPACE_BETWEEN/MIN
    - Instance "NavItem", vị trí 0,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 71,817, cỡ 64 x 32, nền #DDF5B8 (primaryContainer), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 91,821, cỡ 24 x 24
          - Text "glyph", vị trí 91,821, cỡ 24 x 24, chữ "animation", kiểu ngoài hệ thống (24/24, đậm 400), màu #1C2C05 (onPrimaryContainer)
      - Text "Label", vị trí 79,853, cỡ 47 x 16, chữ "Projects", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
    - Instance "NavItem", vị trí 206,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 276,817, cỡ 64 x 32, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 296,821, cỡ 24 x 24
          - Text "glyph", vị trí 296,821, cỡ 24 x 24, chữ "school", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
      - Text "Label", vị trí 284,853, cỡ 48 x 16, chữ "Lessons", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (onSurfaceVariant)
  - Rectangle "Scrim", vị trí 0,0, cỡ 411 x 891, nền #000000 (ngoài hệ thống), độ mờ của lớp 0.55
  - Frame "RestoreProgress", vị trí 32,165, cỡ 348 x 562, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 20, xếp dọc, cách nhau 16 (md), lề trên/phải/dưới/trái 24 (lg)/24 (lg)/16 (md)/24 (lg), căn MIN/MIN
    - Instance "Icon", vị trí 56,189, cỡ 28 x 28, CHÚ Ý: ghi đè màu nền của component gốc
      - Text "glyph", vị trí 56,189, cỡ 28 x 28, chữ "restore", kiểu ngoài hệ thống (28/28, đậm 400), màu #3D6A00 (primary, surfaceTint)
    - Text "Restoring “Walk cycle”", vị trí 56,233, cỡ 300 x 28, chữ "Restoring “Walk cycle”", kiểu titleLarge (22/28, đậm 500), màu #12141A (onBackground, onSurface)
    - Text "Keep the app open. This ", vị trí 56,277, cỡ 300 x 40, chữ "Keep the app open. This takes a few seconds.", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (onSurfaceVariant)
    - Instance "ProgressBar", vị trí 56,333, cỡ 300 x 8, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 999
      - Rectangle "Value", vị trí 56,333, cỡ 180 x 8, nền #3D6A00 (primary, surfaceTint), bo góc 4
    - Frame "Meta", vị trí 56,357, cỡ 300 x 16, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "72 of 120 frames", vị trí 56,357, cỡ 97 x 16, chữ "72 of 120 frames", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (onSurfaceVariant)
      - Frame "spacer", vị trí 153,357, cỡ 177 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "60%", vị trí 330,357, cỡ 26 x 16, chữ "60%", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
    - Frame "restore_mrec", vị trí 56,389, cỡ 300 x 250, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/MIN
      - Instance "MrecSlot", vị trí 56,389, cỡ 300 x 250, nền #F1F2F5 (surfaceContainerLow), viền #D2D6DC (outlineVariant), dày 1.0, bo góc 4
        - Text "Label", vị trí 135,506, cỡ 142 x 16, chữ "Banner MREC 300 × 250", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (onSurfaceVariant)
        - Frame "Ad badge", vị trí 62,395, cỡ 27 x 20, nền #D9DDE2 (surfaceContainerHighest), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
          - Text "Ad", vị trí 68,397, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (onSurfaceVariant)
    - Frame "Actions", vị trí 56,655, cỡ 300 x 56, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 8 (sm)/0/0/0, căn MAX/MIN
      - Instance "Button", vị trí 264,663, cỡ 92 x 48, bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Label", vị trí 284,677, cỡ 48 x 20, chữ "Cancel", kiểu labelLarge (14/20, đậm 600), màu #3D6A00 (primary, surfaceTint)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
