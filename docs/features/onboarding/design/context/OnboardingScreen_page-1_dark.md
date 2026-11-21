# SỐ LIỆU THIẾT KẾ: OnboardingScreen_page-1_dark

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=20-37
- Frame: `20:37`, giao diện tối
- Ngày lấy: 2026-11-22

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
| kiểu chữ | 14/20, đậm 500 | titleSmall |
| kiểu chữ | 14/20, đậm 600 | labelLarge |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 400 | bodyLarge |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | headlineSmall |
| màu chữ | #10140A | onPrimary |
| màu chữ | #6EA8FF | secondary |
| màu chữ | #8C93A3 | onSurfaceVariant |
| màu chữ | #EEF0F4 | onBackground, onSurface, inverseSurface |
| màu nền | #0E1014 | background, surface, surfaceDim |
| màu nền | #171A20 | surfaceContainer |
| màu nền | #2B303A | surfaceBright, surfaceContainerHighest |
| màu nền | #6EA8FF | secondary |
| màu nền | #B6F35C | primary, surfaceTint |
| màu nền | #FAFAF7 | canvas |
| độ mờ của lớp | 0.50 | - |
| độ mờ của lớp | 0.75 | - |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "OnboardingScreen_page-1_dark", vị trí 0,0, cỡ 411 x 891, nền #0E1014 (background, surface, surfaceDim), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu labelLarge (14/20, đậm 600), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 48, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/8 (sm)/0/24 (lg), căn SPACE_BETWEEN/CENTER
    - Text "1 / 4", vị trí 24,42, cỡ 29 x 20, chữ "1 / 4", kiểu labelLarge (14/20, đậm 600), màu #8C93A3 (onSurfaceVariant)
  - Frame "Page (pager)", vị trí 0,76, cỡ 411 x 318, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 4 (xs)/24 (lg)/0/24 (lg), căn MIN/CENTER
    - Frame "Illustration", vị trí 56,80, cỡ 300 x 190, nền #171A20 (surfaceContainer), bo góc 20
      - Rectangle "Rectangle", vị trí 96,110, cỡ 70 x 124, nền #FAFAF7 (canvas), bo góc 8, độ mờ của lớp 0.50
      - Rectangle "Rectangle", vị trí 170,110, cỡ 70 x 124, nền #FAFAF7 (canvas), bo góc 8, độ mờ của lớp 0.75
      - Rectangle "Rectangle", vị trí 246,110, cỡ 70 x 124, nền #FAFAF7 (canvas), bo góc 8
      - Ellipse "Ellipse", vị trí 116,140, cỡ 30 x 30, nền #6EA8FF (secondary)
      - Ellipse "Ellipse", vị trí 190,170, cỡ 30 x 30, nền #6EA8FF (secondary)
      - Ellipse "Ellipse", vị trí 266,200, cỡ 30 x 30, nền #6EA8FF (secondary)
    - Text "Draw frame by frame", vị trí 24,282, cỡ 363 x 32, chữ "Draw frame by frame", kiểu headlineSmall (24/32, đậm 700), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Text "Sketch each moment, then", vị trí 24,326, cỡ 363 x 48, chữ "Sketch each moment, then flip through the frames to see it move.", kiểu bodyLarge (16/24, đậm 400), màu #8C93A3 (onSurfaceVariant)
    - Frame "Page dots", vị trí 174,386, cỡ 62 x 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
      - Rectangle "Rectangle", vị trí 174,386, cỡ 20 x 8, nền #B6F35C (primary, surfaceTint), bo góc 4
      - Rectangle "Rectangle", vị trí 200,386, cỡ 8 x 8, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 4
      - Rectangle "Rectangle", vị trí 214,386, cỡ 8 x 8, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 4
      - Rectangle "Rectangle", vị trí 228,386, cỡ 8 x 8, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 4
  - Frame "spacer", vị trí 0,394, cỡ 1 x 151, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "native_onboarding (NativeStepAd)", vị trí 0,545, cỡ 411 x 250, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/MIN
    - Instance "NativeAdMedium", vị trí 16,545, cỡ 379 x 250, nền #171A20 (surfaceContainer), viền #2C313B (outlineVariant), dày 1.0, bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/MIN
      - Frame "Header", vị trí 29,558, cỡ 353 x 36, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Rectangle "Ad icon", vị trí 29,558, cỡ 36 x 36, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 8
        - Frame "Text", vị trí 75,558, cỡ 307 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Frame "Headline row", vị trí 75,558, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
            - Frame "Ad badge", vị trí 75,558, cỡ 27 x 20, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
              - Text "Ad", vị trí 81,560, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #8C93A3 (onSurfaceVariant)
            - Text "Headline", vị trí 108,558, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall (14/20, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
          - Text "Advertiser", vị trí 75,578, cỡ 59 x 16, chữ "Advertiser", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (onSurfaceVariant)
      - Rectangle "Ad media", vị trí 29,602, cỡ 353 x 136, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 8
      - Frame "Ad CTA", vị trí 29,746, cỡ 353 x 36, viền #6EA8FF (secondary), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Install", vị trí 185,754, cỡ 41 x 20, chữ "Install", kiểu labelLarge (14/20, đậm 600), màu #6EA8FF (secondary)
  - Frame "Footer", vị trí 0,795, cỡ 411 x 96, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 24 (lg)/16 (md)/24 (lg)/16 (md), căn MIN/MIN
    - Instance "Button", vị trí 16,819, cỡ 379 x 48, nền #B6F35C (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 174,833, cỡ 20 x 20
        - Text "glyph", vị trí 174,833, cỡ 20 x 20, chữ "arrow_forward", kiểu ngoài hệ thống (20/20, đậm 400), màu #10140A (onPrimary)
      - Text "Label", vị trí 202,833, cỡ 32 x 20, chữ "Next", kiểu labelLarge (14/20, đậm 600), màu #10140A (onPrimary)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
