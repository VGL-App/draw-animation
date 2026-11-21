# SỐ LIỆU THIẾT KẾ: OnboardingScreen_page-4_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-6678
- Frame: `23:6678`, giao diện sáng
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
| khoảng cách | 2 | ngoài hệ thống |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 24 | lg |
| khoảng cách | 4 | xs |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 11/16, đậm 500 | labelSmall |
| kiểu chữ | 12/16, đậm 400 | bodySmall |
| kiểu chữ | 14/20, đậm 500 | titleSmall |
| kiểu chữ | 14/20, đậm 600 | labelLarge |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 400 | bodyLarge |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | headlineSmall |
| kiểu chữ | 28/28, đậm 400 | ngoài hệ thống |
| kiểu chữ | 48/48, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | onBackground, onSurface |
| màu chữ | #2F6FE0 | secondary |
| màu chữ | #3D6A00 | primary, surfaceTint |
| màu chữ | #5F6675 | onSurfaceVariant |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |
| màu nền | #3D6A00 | primary, surfaceTint |
| màu nền | #D9DDE2 | surfaceContainerHighest |
| màu nền | #ECEEF1 | surfaceContainer |
| màu nền | #F7F8FA | background, surface, surfaceBright |
| màu nền | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "OnboardingScreen_page-4_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (background, surface, surfaceBright), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu labelLarge (14/20, đậm 600), màu #12141A (onBackground, onSurface)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (onBackground, onSurface)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 48, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/8 (sm)/0/24 (lg), căn SPACE_BETWEEN/CENTER
    - Text "4 / 4", vị trí 24,42, cỡ 31 x 20, chữ "4 / 4", kiểu labelLarge (14/20, đậm 600), màu #5F6675 (onSurfaceVariant)
  - Frame "Page (pager)", vị trí 0,76, cỡ 411 x 318, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 4 (xs)/24 (lg)/0/24 (lg), căn MIN/CENTER
    - Frame "Illustration", vị trí 56,80, cỡ 300 x 190, nền #ECEEF1 (surfaceContainer), bo góc 20
      - Rectangle "Rectangle", vị trí 126,110, cỡ 160 x 110, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
      - Instance "Icon", vị trí 182,140, cỡ 48 x 48, CHÚ Ý: ghi đè màu nền của component gốc
        - Text "glyph", vị trí 182,140, cỡ 48 x 48, chữ "cloud_done", kiểu ngoài hệ thống (48/48, đậm 400), màu #3D6A00 (primary, surfaceTint)
      - Instance "Icon", vị trí 192,230, cỡ 28 x 28, CHÚ Ý: ghi đè màu nền của component gốc
        - Text "glyph", vị trí 192,230, cỡ 28 x 28, chữ "save", kiểu ngoài hệ thống (28/28, đậm 400), màu #5F6675 (onSurfaceVariant)
    - Text "Never lose your work", vị trí 24,282, cỡ 363 x 32, chữ "Never lose your work", kiểu headlineSmall (24/32, đậm 700), màu #12141A (onBackground, onSurface)
    - Text "Frameloop saves as you d", vị trí 24,326, cỡ 363 x 48, chữ "Frameloop saves as you draw and backs up a whole project to one file.", kiểu bodyLarge (16/24, đậm 400), màu #5F6675 (onSurfaceVariant)
    - Frame "Page dots", vị trí 174,386, cỡ 62 x 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
      - Rectangle "Rectangle", vị trí 174,386, cỡ 8 x 8, nền #D9DDE2 (surfaceContainerHighest), bo góc 4
      - Rectangle "Rectangle", vị trí 188,386, cỡ 8 x 8, nền #D9DDE2 (surfaceContainerHighest), bo góc 4
      - Rectangle "Rectangle", vị trí 202,386, cỡ 8 x 8, nền #D9DDE2 (surfaceContainerHighest), bo góc 4
      - Rectangle "Rectangle", vị trí 216,386, cỡ 20 x 8, nền #3D6A00 (primary, surfaceTint), bo góc 4
  - Frame "spacer", vị trí 0,394, cỡ 1 x 151, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "native_onboarding (NativeStepAd)", vị trí 0,545, cỡ 411 x 250, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/MIN
    - Instance "NativeAdMedium", vị trí 16,545, cỡ 379 x 250, nền #ECEEF1 (surfaceContainer), viền #D2D6DC (outlineVariant), dày 1.0, bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/MIN
      - Frame "Header", vị trí 29,558, cỡ 353 x 36, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Rectangle "Ad icon", vị trí 29,558, cỡ 36 x 36, nền #D9DDE2 (surfaceContainerHighest), bo góc 8
        - Frame "Text", vị trí 75,558, cỡ 307 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Frame "Headline row", vị trí 75,558, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
            - Frame "Ad badge", vị trí 75,558, cỡ 27 x 20, nền #D9DDE2 (surfaceContainerHighest), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
              - Text "Ad", vị trí 81,560, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (onSurfaceVariant)
            - Text "Headline", vị trí 108,558, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall (14/20, đậm 500), màu #12141A (onBackground, onSurface)
          - Text "Advertiser", vị trí 75,578, cỡ 59 x 16, chữ "Advertiser", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (onSurfaceVariant)
      - Rectangle "Ad media", vị trí 29,602, cỡ 353 x 136, nền #D9DDE2 (surfaceContainerHighest), bo góc 8
      - Frame "Ad CTA", vị trí 29,746, cỡ 353 x 36, viền #2F6FE0 (secondary), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Install", vị trí 185,754, cỡ 41 x 20, chữ "Install", kiểu labelLarge (14/20, đậm 600), màu #2F6FE0 (secondary)
  - Frame "Footer", vị trí 0,795, cỡ 411 x 96, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 24 (lg)/16 (md)/24 (lg)/16 (md), căn MIN/MIN
    - Instance "Button", vị trí 16,819, cỡ 379 x 48, nền #3D6A00 (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 143,833, cỡ 20 x 20
        - Text "glyph", vị trí 143,833, cỡ 20 x 20, chữ "brush", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Text "Label", vị trí 171,833, cỡ 93 x 20, chữ "Start drawing", kiểu labelLarge (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
