# SỐ LIỆU THIẾT KẾ: ExportScreen_done_dark

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=10-754
- Frame: `10:754`, giao diện tối
- Ngày lấy: 2026-11-22

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| instance ghi đè component | Icon | màu nền |
| instance ghi đè component | Icon | màu nền, viền |
| khoảng cách | 1 | ngoài hệ thống |
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
| kiểu chữ | 2/3, đậm 600 | ngoài hệ thống |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | titleLarge |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | headlineSmall |
| kiểu chữ | 3/3, đậm 400 | ngoài hệ thống |
| kiểu chữ | 40/40, đậm 400 | ngoài hệ thống |
| màu chữ | #10140A | onPrimary |
| màu chữ | #6EA8FF | secondary |
| màu chữ | #8C93A3 | onSurfaceVariant |
| màu chữ | #B6F35C | primary, surfaceTint |
| màu chữ | #EEF0F4 | onBackground, onSurface, inverseSurface |
| màu chữ | #FFFFFF | ngoài hệ thống |
| màu nền | #000000 (độ mờ 0.35, lớp tô 0.35) | ngoài hệ thống |
| màu nền | #0E1014 | background, surface, surfaceDim |
| màu nền | #171A20 | surfaceContainer |
| màu nền | #1A1C22 | onCanvas |
| màu nền | #22262E | surfaceVariant, surfaceContainerHigh |
| màu nền | #2B303A | surfaceBright, surfaceContainerHighest |
| màu nền | #6EA8FF | secondary |
| màu nền | #B6F35C | primary, surfaceTint |
| màu nền | #FAFAF7 | canvas |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "ExportScreen_done_dark", vị trí 0,0, cỡ 411 x 891, nền #0E1014 (background, surface, surfaceDim), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu labelLarge (14/20, đậm 600), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,48, cỡ 24 x 24
        - Text "glyph", vị trí 16,48, cỡ 24 x 24, chữ "close", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Text "Export", vị trí 56,46, cỡ 72 x 28, chữ "Export", kiểu titleLarge (22/28, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Frame "spacer", vị trí 132,60, cỡ 271 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Content", vị trí 0,92, cỡ 411 x 591, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 24 (lg)/24 (lg)/0/24 (lg), căn MIN/CENTER
    - Frame "Preview", vị trí 166,116, cỡ 80 x 142, nền #FAFAF7 (canvas), bo góc 6
      - Rectangle "Rectangle", vị trí 172,233, cỡ 67 x 2, nền #1A1C22 (onCanvas)
      - Ellipse "Ellipse", vị trí 196,214, cỡ 19 x 19, nền #6EA8FF (secondary)
      - Frame "PreviewWatermark", vị trí 223,251, cỡ 20 x 5, nền #000000 (độ mờ 0.35, lớp tô 0.35) (ngoài hệ thống), bo góc 2, xếp ngang, cách nhau 1 (ngoài hệ thống), lề trên/phải/dưới/trái 1 (ngoài hệ thống)/2 (ngoài hệ thống)/1 (ngoài hệ thống)/2 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Icon", vị trí 224,252, cỡ 3 x 3, CHÚ Ý: ghi đè màu nền, viền của component gốc
          - Text "glyph", vị trí 224,252, cỡ 3 x 3, chữ "animation", kiểu ngoài hệ thống (3/3, đậm 400), màu #FFFFFF (ngoài hệ thống)
        - Text "Frameloop", vị trí 228,251, cỡ 13 x 4, chữ "Frameloop", kiểu ngoài hệ thống (2/3, đậm 600), màu #FFFFFF (ngoài hệ thống)
    - Instance "Icon", vị trí 186,270, cỡ 40 x 40, CHÚ Ý: ghi đè màu nền của component gốc
      - Text "glyph", vị trí 186,270, cỡ 40 x 40, chữ "check_circle", kiểu ngoài hệ thống (40/40, đậm 400), màu #B6F35C (primary, surfaceTint)
    - Text "Saved to Gallery", vị trí 24,322, cỡ 363 x 32, chữ "Saved to Gallery", kiểu headlineSmall (24/32, đậm 700), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Text "Movies/Frameloop/Bouncin", vị trí 24,366, cỡ 363 x 20, chữ "Movies/Frameloop/Bouncing ball.mp4 · 2.4 MB", kiểu bodyMedium (14/20, đậm 400), màu #8C93A3 (onSurfaceVariant)
    - Instance "export_done_native (NativeAdMedium)", vị trí 24,398, cỡ 363 x 250, nền #171A20 (surfaceContainer), viền #2C313B (outlineVariant), dày 1.0, bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/MIN
      - Frame "Header", vị trí 37,411, cỡ 337 x 36, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Rectangle "Ad icon", vị trí 37,411, cỡ 36 x 36, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 8
        - Frame "Text", vị trí 83,411, cỡ 291 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Frame "Headline row", vị trí 83,411, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
            - Frame "Ad badge", vị trí 83,411, cỡ 27 x 20, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
              - Text "Ad", vị trí 89,413, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #8C93A3 (onSurfaceVariant)
            - Text "Headline", vị trí 116,411, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall (14/20, đậm 500), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
          - Text "Advertiser", vị trí 83,431, cỡ 59 x 16, chữ "Advertiser", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (onSurfaceVariant)
      - Rectangle "Ad media", vị trí 37,455, cỡ 337 x 136, nền #2B303A (surfaceBright, surfaceContainerHighest), bo góc 8
      - Frame "Ad CTA", vị trí 37,599, cỡ 337 x 36, viền #6EA8FF (secondary), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Install", vị trí 185,607, cỡ 41 x 20, chữ "Install", kiểu labelLarge (14/20, đậm 600), màu #6EA8FF (secondary)
    - Frame "spacer", vị trí 205,660, cỡ 1 x 23, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Footer", vị trí 0,683, cỡ 411 x 208, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 24 (lg)/16 (md)/24 (lg)/16 (md), căn MIN/CENTER
    - Instance "Button", vị trí 16,707, cỡ 379 x 48, nền #B6F35C (primary, surfaceTint), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 170,721, cỡ 20 x 20
        - Text "glyph", vị trí 170,721, cỡ 20 x 20, chữ "share", kiểu ngoài hệ thống (20/20, đậm 400), màu #10140A (onPrimary)
      - Text "Label", vị trí 198,721, cỡ 40 x 20, chữ "Share", kiểu labelLarge (14/20, đậm 600), màu #10140A (onPrimary)
    - Instance "Button", vị trí 16,763, cỡ 379 x 48, nền #22262E (surfaceVariant, surfaceContainerHigh), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 138,777, cỡ 20 x 20
        - Text "glyph", vị trí 138,777, cỡ 20 x 20, chữ "photo_library", kiểu ngoài hệ thống (20/20, đậm 400), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
      - Text "Label", vị trí 166,777, cỡ 104 x 20, chữ "Open in Gallery", kiểu labelLarge (14/20, đậm 600), màu #EEF0F4 (onBackground, onSurface, inverseSurface)
    - Instance "Button", vị trí 136,819, cỡ 139 x 48, bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Text "Label", vị trí 156,833, cỡ 95 x 20, chữ "Back to editor", kiểu labelLarge (14/20, đậm 600), màu #B6F35C (primary, surfaceTint)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
