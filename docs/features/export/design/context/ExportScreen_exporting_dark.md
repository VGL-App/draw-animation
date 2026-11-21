# SỐ LIỆU THIẾT KẾ: ExportScreen_exporting_dark

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=10-717
- Frame: `10:717`, giao diện tối
- Ngày lấy: 2026-11-21

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| instance ghi đè component | Icon | màu nền, viền |
| khoảng cách | 1 | ngoài hệ thống |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 16 | md |
| khoảng cách | 2 | ngoài hệ thống |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 24 | lg |
| khoảng cách | 3 | ngoài hệ thống |
| khoảng cách | 4 | xs |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 11/16, đậm 500 | labelSmall |
| kiểu chữ | 12/16, đậm 500 | labelMedium |
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | ngoài hệ thống |
| kiểu chữ | 5/6, đậm 600 | ngoài hệ thống |
| kiểu chữ | 6/6, đậm 400 | ngoài hệ thống |
| màu chữ | #8C93A3 | ngoài hệ thống |
| màu chữ | #B6F35C | ngoài hệ thống |
| màu chữ | #EEF0F4 | ngoài hệ thống |
| màu chữ | #FFFFFF | ngoài hệ thống |
| màu nền | #000000 (độ mờ 0.35, lớp tô 0.35) | ngoài hệ thống |
| màu nền | #0E1014 | ngoài hệ thống |
| màu nền | #13161B | ngoài hệ thống |
| màu nền | #1A1C22 | ngoài hệ thống |
| màu nền | #22262E | ngoài hệ thống |
| màu nền | #2B303A | ngoài hệ thống |
| màu nền | #6EA8FF | ngoài hệ thống |
| màu nền | #B6F35C | ngoài hệ thống |
| màu nền | #FAFAF7 | ngoài hệ thống |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "ExportScreen_exporting_dark", vị trí 0,0, cỡ 411 x 891, nền #0E1014 (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #EEF0F4 (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #EEF0F4 (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "Exporting", vị trí 20,44, cỡ 115 x 32, chữ "Exporting", kiểu ngoài hệ thống (24/32, đậm 700), màu #EEF0F4 (ngoài hệ thống)
    - Frame "spacer", vị trí 139,60, cỡ 264 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Content", vị trí 0,92, cỡ 411 x 525, xếp dọc, cách nhau 16 (md), lề trên/phải/dưới/trái 24 (lg)/24 (lg)/0/24 (lg), căn MIN/CENTER
    - Frame "Preview", vị trí 126,116, cỡ 160 x 284, nền #FAFAF7 (ngoài hệ thống), bo góc 8
      - Rectangle "Rectangle", vị trí 138,349, cỡ 134 x 2, nền #1A1C22 (ngoài hệ thống)
      - Ellipse "Ellipse", vị trí 186,310, cỡ 38 x 38, nền #6EA8FF (ngoài hệ thống)
      - Frame "PreviewWatermark", vị trí 240,386, cỡ 40 x 9, nền #000000 (độ mờ 0.35, lớp tô 0.35) (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 1 (ngoài hệ thống)/3 (ngoài hệ thống)/1 (ngoài hệ thống)/3 (ngoài hệ thống), căn MIN/CENTER
        - Instance "Icon", vị trí 244,387, cỡ 6 x 6, CHÚ Ý: ghi đè màu nền, viền của component gốc
          - Text "glyph", vị trí 244,387, cỡ 6 x 6, chữ "animation", kiểu ngoài hệ thống (6/6, đậm 400), màu #FFFFFF (ngoài hệ thống)
        - Text "Frameloop", vị trí 252,387, cỡ 26 x 7, chữ "Frameloop", kiểu ngoài hệ thống (5/6, đậm 600), màu #FFFFFF (ngoài hệ thống)
    - Text "Making your video…", vị trí 24,416, cỡ 363 x 28, chữ "Making your video…", kiểu ngoài hệ thống (22/28, đậm 500), màu #EEF0F4 (ngoài hệ thống)
    - Instance "ProgressBar", vị trí 24,460, cỡ 363 x 8, nền #22262E (ngoài hệ thống), bo góc 999
      - Rectangle "Value", vị trí 24,460, cỡ 218 x 8, nền #B6F35C (ngoài hệ thống), bo góc 4
    - Frame "Meta", vị trí 24,484, cỡ 363 x 16, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "Frame 29 of 48", vị trí 24,484, cỡ 88 x 16, chữ "Frame 29 of 48", kiểu labelMedium (12/16, đậm 500), màu #8C93A3 (ngoài hệ thống)
      - Frame "spacer", vị trí 112,484, cỡ 164 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Text "60% · about 5 s left", vị trí 276,484, cỡ 111 x 16, chữ "60% · about 5 s left", kiểu labelMedium (12/16, đậm 500), màu #EEF0F4 (ngoài hệ thống)
    - Instance "Button", vị trí 160,516, cỡ 92 x 48, bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Text "Label", vị trí 180,530, cỡ 48 x 20, chữ "Cancel", kiểu ngoài hệ thống (14/20, đậm 600), màu #B6F35C (ngoài hệ thống)
    - Frame "spacer", vị trí 205,580, cỡ 1 x 37, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "export_wait_mrec", vị trí 0,617, cỡ 411 x 274, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/16 (md)/24 (lg)/16 (md), căn MIN/CENTER
    - Instance "MrecSlot", vị trí 56,617, cỡ 300 x 250, nền #13161B (ngoài hệ thống), viền #2C313B (ngoài hệ thống), dày 1.0, bo góc 4
      - Text "Label", vị trí 134,734, cỡ 142 x 16, chữ "Banner MREC 300 × 250", kiểu labelMedium (12/16, đậm 500), màu #8C93A3 (ngoài hệ thống)
      - Frame "Ad badge", vị trí 62,623, cỡ 27 x 20, nền #2B303A (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
        - Text "Ad", vị trí 68,625, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #8C93A3 (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
