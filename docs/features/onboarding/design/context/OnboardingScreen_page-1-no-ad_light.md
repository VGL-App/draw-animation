# SỐ LIỆU THIẾT KẾ: OnboardingScreen_page-1-no-ad_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=33-8488
- Frame: `33:8488`, giao diện sáng
- Ngày lấy: 2026-11-22

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 16 | md |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 24 | lg |
| khoảng cách | 4 | xs |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 14/20, đậm 600 | labelLarge |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 400 | bodyLarge |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | headlineSmall |
| màu chữ | #12141A | onBackground, onSurface |
| màu chữ | #5F6675 | onSurfaceVariant |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |
| màu nền | #2F6FE0 | secondary |
| màu nền | #3D6A00 | primary, surfaceTint |
| màu nền | #D9DDE2 | surfaceContainerHighest |
| màu nền | #ECEEF1 | surfaceContainer |
| màu nền | #F7F8FA | background, surface, surfaceBright |
| màu nền | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas |
| độ mờ của lớp | 0.50 | - |
| độ mờ của lớp | 0.75 | - |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "OnboardingScreen_page-1-no-ad_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (background, surface, surfaceBright), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
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
    - Text "1 / 4", vị trí 24,42, cỡ 29 x 20, chữ "1 / 4", kiểu labelLarge (14/20, đậm 600), màu #5F6675 (onSurfaceVariant)
  - Frame "spacer", vị trí 0,76, cỡ 1 x 200, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Page (pager)", vị trí 0,276, cỡ 411 x 318, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 4 (xs)/24 (lg)/0/24 (lg), căn MIN/CENTER
    - Frame "Illustration", vị trí 56,280, cỡ 300 x 190, nền #ECEEF1 (surfaceContainer), bo góc 20
      - Rectangle "Rectangle", vị trí 96,310, cỡ 70 x 124, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8, độ mờ của lớp 0.50
      - Rectangle "Rectangle", vị trí 170,310, cỡ 70 x 124, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8, độ mờ của lớp 0.75
      - Rectangle "Rectangle", vị trí 246,310, cỡ 70 x 124, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas), bo góc 8
      - Ellipse "Ellipse", vị trí 116,340, cỡ 30 x 30, nền #2F6FE0 (secondary)
      - Ellipse "Ellipse", vị trí 190,370, cỡ 30 x 30, nền #2F6FE0 (secondary)
      - Ellipse "Ellipse", vị trí 266,400, cỡ 30 x 30, nền #2F6FE0 (secondary)
    - Text "Draw frame by frame", vị trí 24,482, cỡ 363 x 32, chữ "Draw frame by frame", kiểu headlineSmall (24/32, đậm 700), màu #12141A (onBackground, onSurface)
    - Text "Sketch each moment, then", vị trí 24,526, cỡ 363 x 48, chữ "Sketch each moment, then flip through the frames to see it move.", kiểu bodyLarge (16/24, đậm 400), màu #5F6675 (onSurfaceVariant)
    - Frame "Page dots", vị trí 174,586, cỡ 62 x 8, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
      - Rectangle "Rectangle", vị trí 174,586, cỡ 20 x 8, nền #3D6A00 (primary, surfaceTint), bo góc 4
      - Rectangle "Rectangle", vị trí 200,586, cỡ 8 x 8, nền #D9DDE2 (surfaceContainerHighest), bo góc 4
      - Rectangle "Rectangle", vị trí 214,586, cỡ 8 x 8, nền #D9DDE2 (surfaceContainerHighest), bo góc 4
      - Rectangle "Rectangle", vị trí 228,586, cỡ 8 x 8, nền #D9DDE2 (surfaceContainerHighest), bo góc 4
  - Frame "spacer", vị trí 0,594, cỡ 1 x 200, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Footer", vị trí 0,795, cỡ 411 x 96, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 24 (lg)/16 (md)/24 (lg)/16 (md), căn MIN/MIN
    - Instance "Button", vị trí 16,819, cỡ 379 x 48, nền #3D6A00 (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 174,833, cỡ 20 x 20
        - Text "glyph", vị trí 174,833, cỡ 20 x 20, chữ "arrow_forward", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)
      - Text "Label", vị trí 202,833, cỡ 32 x 20, chữ "Next", kiểu labelLarge (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest, canvas)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
