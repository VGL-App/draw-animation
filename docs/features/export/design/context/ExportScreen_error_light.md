# SỐ LIỆU THIẾT KẾ: ExportScreen_error_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-3371
- Frame: `23:3371`, giao diện sáng
- Ngày lấy: 2026-11-20

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| instance ghi đè component | Icon | màu nền |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 16 | md |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 24 | lg |
| khoảng cách | 4 | xs |
| khoảng cách | 8 | sm |
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 400 | bodyLarge |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 22/28, đậm 500 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | ngoài hệ thống |
| kiểu chữ | 36/36, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | ngoài hệ thống |
| màu chữ | #3D6A00 | ngoài hệ thống |
| màu chữ | #5A0F0F | ngoài hệ thống |
| màu chữ | #5F6675 | ngoài hệ thống |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest |
| màu nền | #3D6A00 | ngoài hệ thống |
| màu nền | #F7F8FA | ngoài hệ thống |
| màu nền | #FDE2E2 | ngoài hệ thống |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "ExportScreen_error_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/8 (sm)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,48, cỡ 24 x 24
        - Text "glyph", vị trí 16,48, cỡ 24 x 24, chữ "close", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Text "Export", vị trí 56,46, cỡ 72 x 28, chữ "Export", kiểu ngoài hệ thống (22/28, đậm 500), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 132,60, cỡ 271 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Frame "Content", vị trí 0,92, cỡ 411 x 671, xếp dọc, cách nhau 16 (md), lề trên/phải/dưới/trái 0/24 (lg)/0/24 (lg), căn CENTER/CENTER
    - Frame "Icon bg", vị trí 170,336, cỡ 72 x 72, nền #FDE2E2 (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Icon", vị trí 188,354, cỡ 36 x 36, CHÚ Ý: ghi đè màu nền của component gốc
        - Text "glyph", vị trí 188,354, cỡ 36 x 36, chữ "error", kiểu ngoài hệ thống (36/36, đậm 400), màu #5A0F0F (ngoài hệ thống)
    - Text "Export didn't finish", vị trí 24,424, cỡ 363 x 32, chữ "Export didn't finish", kiểu ngoài hệ thống (24/32, đậm 700), màu #12141A (ngoài hệ thống)
    - Text "Your project is safe. Th", vị trí 24,472, cỡ 363 x 48, chữ "Your project is safe. The phone is low on storage: free up about 50 MB and try again.", kiểu bodyLarge (16/24, đậm 400), màu #5F6675 (ngoài hệ thống)
  - Frame "Footer", vị trí 0,763, cỡ 411 x 128, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/16 (md)/24 (lg)/16 (md), căn MIN/CENTER
    - Instance "Button", vị trí 16,763, cỡ 379 x 48, nền #3D6A00 (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 158,777, cỡ 20 x 20
        - Text "glyph", vị trí 158,777, cỡ 20 x 20, chữ "refresh", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
      - Text "Label", vị trí 186,777, cỡ 63 x 20, chữ "Try again", kiểu ngoài hệ thống (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
    - Instance "Button", vị trí 136,819, cỡ 139 x 48, bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Text "Label", vị trí 156,833, cỡ 95 x 20, chữ "Back to editor", kiểu ngoài hệ thống (14/20, đậm 600), màu #3D6A00 (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
