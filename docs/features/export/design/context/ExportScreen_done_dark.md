# SỐ LIỆU THIẾT KẾ: ExportScreen_done_dark

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=10-754
- Frame: `10:754`, giao diện tối
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
| khoảng cách | 2 | ngoài hệ thống |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 24 | lg |
| khoảng cách | 4 | xs |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 11/16, đậm 500 | labelSmall |
| kiểu chữ | 12/16, đậm 400 | bodySmall |
| kiểu chữ | 14/20, đậm 400 | bodyMedium |
| kiểu chữ | 14/20, đậm 500 | titleSmall, labelLarge |
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | ngoài hệ thống |
| kiểu chữ | 40/40, đậm 400 | ngoài hệ thống |
| màu chữ | #10140A | ngoài hệ thống |
| màu chữ | #6EA8FF | ngoài hệ thống |
| màu chữ | #8C93A3 | ngoài hệ thống |
| màu chữ | #B6F35C | ngoài hệ thống |
| màu chữ | #EEF0F4 | ngoài hệ thống |
| màu nền | #0E1014 | ngoài hệ thống |
| màu nền | #171A20 | ngoài hệ thống |
| màu nền | #1A1C22 | ngoài hệ thống |
| màu nền | #22262E | ngoài hệ thống |
| màu nền | #2B303A | ngoài hệ thống |
| màu nền | #6EA8FF | ngoài hệ thống |
| màu nền | #B6F35C | ngoài hệ thống |
| màu nền | #FAFAF7 | ngoài hệ thống |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "ExportScreen_done_dark", vị trí 0,0, cỡ 411 x 891, nền #0E1014 (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #EEF0F4 (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,48, cỡ 24 x 24
        - Text "glyph", vị trí 16,48, cỡ 24 x 24, chữ "close", kiểu ngoài hệ thống (24/24, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Text "Export", vị trí 56,46, cỡ 72 x 28, chữ "Export", kiểu ngoài hệ thống (22/28, đậm 500), màu #EEF0F4 (ngoài hệ thống)
    - Frame "spacer", vị trí 132,60, cỡ 271 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Content", vị trí 0,92, cỡ 411 x 591, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 24 (lg)/24 (lg)/0/24 (lg), căn MIN/CENTER
    - Frame "Preview", vị trí 156,116, cỡ 100 x 178, nền #FAFAF7 (ngoài hệ thống), bo góc 8
      - Rectangle "Rectangle", vị trí 164,262, cỡ 84 x 2, nền #1A1C22 (ngoài hệ thống)
      - Ellipse "Ellipse", vị trí 194,238, cỡ 24 x 24, nền #6EA8FF (ngoài hệ thống)
    - Instance "Icon", vị trí 186,306, cỡ 40 x 40, CHÚ Ý: ghi đè màu nền của component gốc
      - Text "glyph", vị trí 186,306, cỡ 40 x 40, chữ "check_circle", kiểu ngoài hệ thống (40/40, đậm 400), màu #B6F35C (ngoài hệ thống)
    - Text "Saved to Gallery", vị trí 24,358, cỡ 363 x 32, chữ "Saved to Gallery", kiểu ngoài hệ thống (24/32, đậm 700), màu #EEF0F4 (ngoài hệ thống)
    - Text "Movies/Frameloop/Bouncin", vị trí 24,402, cỡ 363 x 20, chữ "Movies/Frameloop/Bouncing ball.mp4 · 2.4 MB", kiểu bodyMedium (14/20, đậm 400), màu #8C93A3 (ngoài hệ thống)
    - Instance "export_done_native (NativeAdMedium)", vị trí 24,434, cỡ 363 x 250, nền #171A20 (ngoài hệ thống), viền #2C313B (ngoài hệ thống), dày 1.0, bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/MIN
      - Frame "Header", vị trí 37,447, cỡ 337 x 36, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Rectangle "Ad icon", vị trí 37,447, cỡ 36 x 36, nền #2B303A (ngoài hệ thống), bo góc 8
        - Frame "Text", vị trí 83,447, cỡ 291 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Frame "Headline row", vị trí 83,447, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
            - Frame "Ad badge", vị trí 83,447, cỡ 27 x 20, nền #2B303A (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
              - Text "Ad", vị trí 89,449, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #8C93A3 (ngoài hệ thống)
            - Text "Headline", vị trí 116,447, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #EEF0F4 (ngoài hệ thống)
          - Text "Advertiser", vị trí 83,467, cỡ 59 x 16, chữ "Advertiser", kiểu bodySmall (12/16, đậm 400), màu #8C93A3 (ngoài hệ thống)
      - Rectangle "Ad media", vị trí 37,491, cỡ 337 x 136, nền #2B303A (ngoài hệ thống), bo góc 8
      - Frame "Ad CTA", vị trí 37,635, cỡ 337 x 36, viền #6EA8FF (ngoài hệ thống), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Install", vị trí 185,643, cỡ 41 x 20, chữ "Install", kiểu ngoài hệ thống (14/20, đậm 600), màu #6EA8FF (ngoài hệ thống)
    - Frame "spacer", vị trí 205,696, cỡ 1 x 1, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Footer", vị trí 0,683, cỡ 411 x 208, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 24 (lg)/16 (md)/24 (lg)/16 (md), căn MIN/CENTER
    - Instance "Button", vị trí 16,707, cỡ 379 x 48, nền #B6F35C (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 170,721, cỡ 20 x 20
        - Text "glyph", vị trí 170,721, cỡ 20 x 20, chữ "share", kiểu ngoài hệ thống (20/20, đậm 400), màu #10140A (ngoài hệ thống)
      - Text "Label", vị trí 198,721, cỡ 40 x 20, chữ "Share", kiểu ngoài hệ thống (14/20, đậm 600), màu #10140A (ngoài hệ thống)
    - Instance "Button", vị trí 16,763, cỡ 379 x 48, nền #22262E (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 138,777, cỡ 20 x 20
        - Text "glyph", vị trí 138,777, cỡ 20 x 20, chữ "photo_library", kiểu ngoài hệ thống (20/20, đậm 400), màu #EEF0F4 (ngoài hệ thống)
      - Text "Label", vị trí 166,777, cỡ 104 x 20, chữ "Open in Gallery", kiểu ngoài hệ thống (14/20, đậm 600), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Button", vị trí 136,819, cỡ 139 x 48, bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Text "Label", vị trí 156,833, cỡ 95 x 20, chữ "Back to editor", kiểu ngoài hệ thống (14/20, đậm 600), màu #B6F35C (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
