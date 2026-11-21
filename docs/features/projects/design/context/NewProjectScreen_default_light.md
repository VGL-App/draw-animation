# SỐ LIỆU THIẾT KẾ: NewProjectScreen_default_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-2500
- Frame: `23:2500`, giao diện sáng
- Ngày lấy: 2026-11-22

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| instance ghi đè component | ColorSwatch | màu nền |
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
| kiểu chữ | 14/20, đậm 500 | titleSmall |
| kiểu chữ | 14/20, đậm 600 | labelLarge |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 400 | bodyLarge |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | titleLarge |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | onBackground, onSurface |
| màu chữ | #1C2C05 | onPrimaryContainer |
| màu chữ | #2F6FE0 | secondary |
| màu chữ | #5F6675 | onSurfaceVariant |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |
| màu nền | #2F6FE0 | secondary |
| màu nền | #3D6A00 | primary, surfaceTint |
| màu nền | #D9DDE2 | surfaceContainerHighest |
| màu nền | #DDF5B8 | primaryContainer |
| màu nền | #E3E6EA | surfaceVariant, surfaceContainerHigh |
| màu nền | #ECEEF1 | surfaceContainer |
| màu nền | #F7F8FA | background, surface, surfaceBright |
| màu nền | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "NewProjectScreen_default_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (background, surface, surfaceBright), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/16 (md)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,48, cỡ 24 x 24
        - Text "glyph", vị trí 16,48, cỡ 24 x 24, chữ "close", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Text "New animation", vị trí 56,46, cỡ 156 x 28, chữ "New animation", kiểu titleLarge (22/28, đậm 500), màu #12141A (onBackground, onSurface)
  - Frame "Content", vị trí 0,92, cỡ 411 x 703, xếp dọc, cách nhau 24 (lg), lề trên/phải/dưới/trái 8 (sm)/16 (md)/0/16 (md), căn MIN/MIN
    - Frame "NameField", vị trí 16,100, cỡ 379 x 62, viền #B9BEC8 (outline), dày 1.0, bo góc 8, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 8 (sm)/16 (md)/8 (sm)/16 (md), căn MIN/MIN
      - Text "Name", vị trí 33,109, cỡ 31 x 16, chữ "Name", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (onSurfaceVariant)
      - Text "My animation 5", vị trí 33,129, cỡ 116 x 24, chữ "My animation 5", kiểu bodyLarge (16/24, đậm 400), màu #12141A (onBackground, onSurface)
    - Frame "Canvas", vị trí 16,186, cỡ 379 x 114, xếp dọc, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Canvas", vị trí 16,186, cỡ 50 x 20, chữ "Canvas", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
      - Frame "Ratios", vị trí 16,216, cỡ 379 x 84, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Frame "Ratio 9:16", vị trí 16,216, cỡ 124 x 84, nền #DDF5B8 (primaryContainer), viền #3D6A00 (primary, surfaceTint), dày 2.0, bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MAX/CENTER
          - Rectangle "Rectangle", vị trí 69,228, cỡ 18 x 32, viền #1C2C05 (onPrimaryContainer), dày 2.0, bo góc 3
          - Text "9:16", vị trí 63,268, cỡ 29 x 20, chữ "9:16", kiểu labelLarge (14/20, đậm 600), màu #1C2C05 (onPrimaryContainer)
        - Frame "Ratio 16:9", vị trí 148,216, cỡ 120 x 84, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MAX/CENTER
          - Rectangle "Rectangle", vị trí 192,244, cỡ 32 x 18, viền #5F6675 (onSurfaceVariant), dày 2.0, bo góc 3
          - Text "16:9", vị trí 193,270, cỡ 29 x 20, chữ "16:9", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
        - Frame "Ratio 1:1", vị trí 275,216, cỡ 120 x 84, nền #ECEEF1 (surfaceContainer), bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MAX/CENTER
          - Rectangle "Rectangle", vị trí 322,236, cỡ 26 x 26, viền #5F6675 (onSurfaceVariant), dày 2.0, bo góc 3
          - Text "1:1", vị trí 326,270, cỡ 18 x 20, chữ "1:1", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
    - Frame "Size", vị trí 16,324, cỡ 379 x 58, xếp dọc, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Size", vị trí 16,324, cỡ 29 x 20, chữ "Size", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
      - Frame "Sizes", vị trí 16,354, cỡ 123 x 28, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Instance "Chip", vị trí 16,354, cỡ 54 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Text "Label", vị trí 28,360, cỡ 30 x 16, chữ "720p", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
        - Instance "Chip", vị trí 78,354, cỡ 61 x 28, nền #DDF5B8 (primaryContainer), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Text "Label", vị trí 90,360, cỡ 37 x 16, chữ "1080p", kiểu labelMedium (12/16, đậm 500), màu #1C2C05 (onPrimaryContainer)
    - Frame "Frame rate", vị trí 16,406, cỡ 379 x 58, xếp dọc, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Frame rate", vị trí 16,406, cỡ 73 x 20, chữ "Frame rate", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
      - Frame "Rates", vị trí 16,436, cỡ 281 x 28, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
        - Instance "Chip", vị trí 16,436, cỡ 32 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Text "Label", vị trí 28,442, cỡ 8 x 16, chữ "6", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
        - Instance "Chip", vị trí 56,436, cỡ 32 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Text "Label", vị trí 68,442, cỡ 8 x 16, chữ "8", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
        - Instance "Chip", vị trí 96,436, cỡ 59 x 28, nền #DDF5B8 (primaryContainer), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Text "Label", vị trí 108,442, cỡ 35 x 16, chữ "12 fps", kiểu labelMedium (12/16, đậm 500), màu #1C2C05 (onPrimaryContainer)
        - Instance "Chip", vị trí 163,436, cỡ 38 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Text "Label", vị trí 175,442, cỡ 14 x 16, chữ "15", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
        - Instance "Chip", vị trí 209,436, cỡ 40 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Text "Label", vị trí 221,442, cỡ 16 x 16, chữ "24", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
        - Instance "Chip", vị trí 257,436, cỡ 40 x 28, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 6 (ngoài hệ thống)/12 (ngoài hệ thống)/6 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/CENTER
          - Text "Label", vị trí 269,442, cỡ 16 x 16, chữ "30", kiểu labelMedium (12/16, đậm 500), màu #12141A (onBackground, onSurface)
    - Instance "new_project_native (NativeAdMedium)", vị trí 16,488, cỡ 379 x 250, nền #ECEEF1 (surfaceContainer), viền #D2D6DC (outlineVariant), dày 1.0, bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/MIN
      - Frame "Header", vị trí 29,501, cỡ 353 x 36, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Rectangle "Ad icon", vị trí 29,501, cỡ 36 x 36, nền #D9DDE2 (surfaceContainerHighest), bo góc 8
        - Frame "Text", vị trí 75,501, cỡ 307 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Frame "Headline row", vị trí 75,501, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
            - Frame "Ad badge", vị trí 75,501, cỡ 27 x 20, nền #D9DDE2 (surfaceContainerHighest), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
              - Text "Ad", vị trí 81,503, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (onSurfaceVariant)
            - Text "Headline", vị trí 108,501, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
          - Text "Advertiser", vị trí 75,521, cỡ 59 x 16, chữ "Advertiser", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
      - Rectangle "Ad media", vị trí 29,545, cỡ 353 x 136, nền #D9DDE2 (surfaceContainerHighest), bo góc 8
      - Frame "Ad CTA", vị trí 29,689, cỡ 353 x 36, viền #2F6FE0 (secondary), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Install", vị trí 185,697, cỡ 41 x 20, chữ "Install", kiểu labelLarge (14/20, đậm 600), màu #2F6FE0 (secondary)
    - Frame "Background", vị trí 16,762, cỡ 379 x 78, xếp dọc, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Background", vị trí 16,762, cỡ 81 x 20, chữ "Background", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
      - Frame "Backgrounds", vị trí 16,792, cỡ 244 x 48, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Instance "ColorSwatch", vị trí 16,798, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
          - Ellipse "Color", vị trí 20,802, cỡ 28 x 28, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), viền #D2D6DC (outlineVariant), dày 1.0
          - Ellipse "Ring", vị trí 16,798, cỡ 36 x 36, viền #12141A (onBackground, onSurface), dày 2.0
        - Instance "ColorSwatch", vị trí 64,798, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
          - Ellipse "Color", vị trí 66,800, cỡ 32 x 32, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), viền #D2D6DC (outlineVariant), dày 1.0
        - Instance "ColorSwatch", vị trí 112,798, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
          - Ellipse "Color", vị trí 114,800, cỡ 32 x 32, nền #2F6FE0 (secondary), viền #D2D6DC (outlineVariant), dày 1.0
        - Instance "IconButton", vị trí 160,792, cỡ 48 x 48, nền #E3E6EA (surfaceVariant, surfaceContainerHigh), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
          - Instance "Glyph", vị trí 172,804, cỡ 24 x 24
            - Text "glyph", vị trí 172,804, cỡ 24 x 24, chữ "image", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (onBackground, onSurface)
        - Text "Photo", vị trí 220,806, cỡ 40 x 20, chữ "Photo", kiểu labelLarge (14/20, đậm 600), màu #5F6675 (onSurfaceVariant)
    - Frame "spacer", vị trí 16,864, cỡ 1 x 1, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Footer", vị trí 0,795, cỡ 411 x 96, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 24 (lg)/16 (md)/24 (lg)/16 (md), căn MIN/MIN
    - Instance "Button", vị trí 16,819, cỡ 379 x 48, nền #3D6A00 (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 166,833, cỡ 20 x 20
        - Text "glyph", vị trí 166,833, cỡ 20 x 20, chữ "check", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Text "Label", vị trí 194,833, cỡ 46 x 20, chữ "Create", kiểu labelLarge (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
