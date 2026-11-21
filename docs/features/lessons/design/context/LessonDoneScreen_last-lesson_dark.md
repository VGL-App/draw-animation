# SỐ LIỆU THIẾT KẾ: LessonDoneScreen_last-lesson_dark

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=33-7571
- Frame: `33:7571`, giao diện tối
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
| kiểu chữ | 14/20, đậm 400 | bodyMedium |
| kiểu chữ | 14/20, đậm 500 | titleSmall |
| kiểu chữ | 14/20, đậm 600 | labelLarge |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 600 | titleMedium |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | headlineSmall |
| kiểu chữ | 32/32, đậm 400 | ngoài hệ thống |
| màu chữ | #10140A | onPrimary |
| màu chữ | #6EA8FF | secondary |
| màu chữ | #8C93A3 | onSurfaceVariant |
| màu chữ | #B6F35C | primary, surfaceTint |
| màu chữ | #EEF0F4 | onBackground, onSurface, inverseSurface |
| màu nền | #0E1014 | background, surface, surfaceDim |
| màu nền | #171A20 | surfaceContainer |
| màu nền | #1A1C22 | onCanvas |
| màu nền | #2B303A | surfaceBright, surfaceContainerHighest |
| màu nền | #6EA8FF | secondary |
| màu nền | #B6F35C | primary, surfaceTint |
| màu nền | #FAFAF7 | canvas |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "LessonDoneScreen_last-lesson_dark", vị trí 0,0, cỡ 411 x 891, nền #0E1014 (background, surface, surfaceDim), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu labelLarge (14/20, đậm 600), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 56, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/16 (md)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,32, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,44, cỡ 24 x 24
        - Text "glyph", vị trí 16,44, cỡ 24 x 24, chữ "close", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Text "Lesson 18 · Fireworks", vị trí 56,44, cỡ 167 x 24, chữ "Lesson 18 · Fireworks", kiểu titleMedium (16/24, đậm 600), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
  - Frame "Content", vị trí 0,84, cỡ 411 x 735, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 8 (sm)/16 (md)/0/16 (md), căn MIN/CENTER
    - Frame "Preview", vị trí 156,92, cỡ 100 x 178, nền #FAFAF7 (canvas), bo góc 8
      - Rectangle "Rectangle", vị trí 164,238, cỡ 84 x 2, nền #1A1C22 (onCanvas)
      - Ellipse "Ellipse", vị trí 194,214, cỡ 24 x 24, nền #6EA8FF (secondary)
    - Instance "Icon", vị trí 190,282, cỡ 32 x 32, CHÚ Ý: ghi đè màu nền của component gốc
      - Text "glyph", vị trí 190,282, cỡ 32 x 32, chữ "celebration", kiểu ngoài hệ thống (32/32, đậm 400), màu #B6F35C (primary, surfaceTint)
    - Text "Lesson complete!", vị trí 104,326, cỡ 204 x 32, chữ "Lesson complete!", kiểu headlineSmall (24/32, đậm 700), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Text "Summary", vị trí 16,370, cỡ 379 x 20, chữ "36 frames · 3.0 s · You finished every lesson", kiểu bodyMedium (14/20, đậm 400), màu #8C93A3 (onSurfaceVariant)
    - Instance "lesson_done_native (NativeAdMedium)", vị trí 16,402, cỡ 379 x 250, nền #171A20 (surfaceContainer), viền #2C313B (outlineVariant), dày 1.0, bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/MIN
      - Frame "Header", vị trí 29,415, cỡ 353 x 36, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Rectangle "Ad icon", vị trí 29,415, cỡ 36 x 36, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 8
        - Frame "Text", vị trí 75,415, cỡ 307 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Frame "Headline row", vị trí 75,415, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
            - Frame "Ad badge", vị trí 75,415, cỡ 27 x 20, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
              - Text "Ad", vị trí 81,417, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #8C93A3 (onSurfaceVariant)
            - Text "Headline", vị trí 108,415, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall (14/20, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
          - Text "Advertiser", vị trí 75,435, cỡ 59 x 16, chữ "Advertiser", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (onSurfaceVariant)
      - Rectangle "Ad media", vị trí 29,459, cỡ 353 x 136, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 8
      - Frame "Ad CTA", vị trí 29,603, cỡ 353 x 36, viền #6EA8FF (secondary), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Install", vị trí 185,611, cỡ 41 x 20, chữ "Install", kiểu labelLarge (14/20, đậm 600), màu #6EA8FF (secondary)
    - Frame "spacer", vị trí 205,664, cỡ 1 x 155, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Footer", vị trí 0,819, cỡ 411 x 72, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/16 (md)/24 (lg)/16 (md), căn MIN/CENTER
    - Instance "Button", vị trí 16,819, cỡ 379 x 48, nền #B6F35C (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 120,833, cỡ 20 x 20
        - Text "glyph", vị trí 120,833, cỡ 20 x 20, chữ "ios_share", kiểu ngoài hệ thống (20/20, đậm 400), màu #10140A (onPrimary)
      - Text "Label", vị trí 148,833, cỡ 140 x 20, chữ "Export my animation", kiểu labelLarge (14/20, đậm 600), màu #10140A (onPrimary)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
