# SỐ LIỆU THIẾT KẾ: SettingsScreen_default_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-2575
- Frame: `23:2575`, giao diện sáng
- Ngày lấy: 2026-11-20

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
| khoảng cách | 4 | xs |
| khoảng cách | 6 | ngoài hệ thống |
| kiểu chữ | 11/16, đậm 500 | labelSmall |
| kiểu chữ | 12/16, đậm 400 | bodySmall |
| kiểu chữ | 14/20, đậm 400 | bodyMedium |
| kiểu chữ | 14/20, đậm 500 | titleSmall, labelLarge |
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 400 | bodyLarge |
| kiểu chữ | 22/28, đậm 500 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| màu chữ | #12141A | ngoài hệ thống |
| màu chữ | #2F6FE0 | ngoài hệ thống |
| màu chữ | #3D6A00 | ngoài hệ thống |
| màu chữ | #5F6675 | ngoài hệ thống |
| màu nền | #D9DDE2 | ngoài hệ thống |
| màu nền | #F1F2F5 | ngoài hệ thống |
| màu nền | #F7F8FA | ngoài hệ thống |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "SettingsScreen_default_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/16 (md)/0/4 (xs), căn MIN/CENTER
    - Instance "IconButton", vị trí 4,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 16,48, cỡ 24 x 24
        - Text "glyph", vị trí 16,48, cỡ 24 x 24, chữ "arrow_back", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Text "Settings", vị trí 56,46, cỡ 89 x 28, chữ "Settings", kiểu ngoài hệ thống (22/28, đậm 500), màu #12141A (ngoài hệ thống)
  - Frame "Content", vị trí 0,92, cỡ 411 x 727, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Frame "Section Drawing", vị trí 0,92, cỡ 411 x 40, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 16 (md)/0/4 (xs)/16 (md), căn MIN/MIN
      - Text "Drawing", vị trí 16,108, cỡ 57 x 20, chữ "Drawing", kiểu ngoài hệ thống (14/20, đậm 600), màu #3D6A00 (ngoài hệ thống)
    - Instance "ListItem", vị trí 0,132, cỡ 411 x 56, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/CENTER
      - Instance "Leading icon", vị trí 16,148, cỡ 24 x 24
        - Text "glyph", vị trí 16,148, cỡ 24 x 24, chữ "speed", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Title", vị trí 56,148, cỡ 283 x 24, chữ "Default frame rate", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Trailing", vị trí 355,150, cỡ 40 x 20, chữ "12 fps", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "ListItem", vị trí 0,188, cỡ 411 x 56, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/CENTER
      - Instance "Leading icon", vị trí 16,204, cỡ 24 x 24
        - Text "glyph", vị trí 16,204, cỡ 24 x 24, chữ "aspect_ratio", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Title", vị trí 56,204, cỡ 295 x 24, chữ "Default canvas", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Trailing", vị trí 367,206, cỡ 28 x 20, chữ "9:16", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "ListItem", vị trí 0,244, cỡ 411 x 56, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/CENTER
      - Instance "Leading icon", vị trí 16,260, cỡ 24 x 24
        - Text "glyph", vị trí 16,260, cỡ 24 x 24, chữ "stylus", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Title", vị trí 56,260, cỡ 304 x 24, chữ "Stylus pressure", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Trailing", vị trí 376,262, cỡ 19 x 20, chữ "On", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Frame "Section Your projects", vị trí 0,300, cỡ 411 x 40, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 16 (md)/0/4 (xs)/16 (md), căn MIN/MIN
      - Text "Your projects", vị trí 16,316, cỡ 90 x 20, chữ "Your projects", kiểu ngoài hệ thống (14/20, đậm 600), màu #3D6A00 (ngoài hệ thống)
    - Instance "ListItem", vị trí 0,340, cỡ 411 x 56, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/CENTER
      - Instance "Leading icon", vị trí 16,356, cỡ 24 x 24
        - Text "glyph", vị trí 16,356, cỡ 24 x 24, chữ "save_alt", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Title", vị trí 56,356, cỡ 271 x 24, chữ "Back up all projects", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Trailing", vị trí 343,358, cỡ 52 x 20, chữ "To a file", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "ListItem", vị trí 0,396, cỡ 411 x 56, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/CENTER
      - Instance "Leading icon", vị trí 16,412, cỡ 24 x 24
        - Text "glyph", vị trí 16,412, cỡ 24 x 24, chữ "upload_file", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Title", vị trí 56,412, cỡ 323 x 24, chữ "Restore from a file", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Trailing", vị trí 395,414, cỡ 0 x 20, chữ "", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "ListItem", vị trí 0,452, cỡ 411 x 56, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/CENTER
      - Instance "Leading icon", vị trí 16,468, cỡ 24 x 24
        - Text "glyph", vị trí 16,468, cỡ 24 x 24, chữ "history", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Title", vị trí 56,468, cỡ 252 x 24, chữ "Autosave", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Trailing", vị trí 324,470, cỡ 71 x 20, chữ "Every 30 s", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Frame "Section About", vị trí 0,508, cỡ 411 x 40, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 16 (md)/0/4 (xs)/16 (md), căn MIN/MIN
      - Text "About", vị trí 16,524, cỡ 42 x 20, chữ "About", kiểu ngoài hệ thống (14/20, đậm 600), màu #3D6A00 (ngoài hệ thống)
    - Instance "ListItem", vị trí 0,548, cỡ 411 x 56, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/CENTER
      - Instance "Leading icon", vị trí 16,564, cỡ 24 x 24
        - Text "glyph", vị trí 16,564, cỡ 24 x 24, chữ "shield_person", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Title", vị trí 56,564, cỡ 323 x 24, chữ "Ad privacy choices", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Trailing", vị trí 395,566, cỡ 0 x 20, chữ "", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "ListItem", vị trí 0,604, cỡ 411 x 56, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/CENTER
      - Instance "Leading icon", vị trí 16,620, cỡ 24 x 24
        - Text "glyph", vị trí 16,620, cỡ 24 x 24, chữ "policy", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Title", vị trí 56,620, cỡ 323 x 24, chữ "Privacy policy", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Trailing", vị trí 395,622, cỡ 0 x 20, chữ "", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "ListItem", vị trí 0,660, cỡ 411 x 56, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/CENTER
      - Instance "Leading icon", vị trí 16,676, cỡ 24 x 24
        - Text "glyph", vị trí 16,676, cỡ 24 x 24, chữ "star", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Title", vị trí 56,676, cỡ 323 x 24, chữ "Rate Frameloop", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Trailing", vị trí 395,678, cỡ 0 x 20, chữ "", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "ListItem", vị trí 0,716, cỡ 411 x 56, xếp ngang, cách nhau 16 (md), lề trên/phải/dưới/trái 0/16 (md)/0/16 (md), căn MIN/CENTER
      - Instance "Leading icon", vị trí 16,732, cỡ 24 x 24
        - Text "glyph", vị trí 16,732, cỡ 24 x 24, chữ "info", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Title", vị trí 56,732, cỡ 292 x 24, chữ "Version", kiểu bodyLarge (16/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Trailing", vị trí 364,734, cỡ 31 x 20, chữ "1.0.0", kiểu bodyMedium (14/20, đậm 400), màu #5F6675 (ngoài hệ thống)
  - Instance "settings_bottom (NativeAdBar)", vị trí 0,819, cỡ 411 x 72, nền #F1F2F5 (ngoài hệ thống), viền #D2D6DC (ngoài hệ thống), dày 1.0, xếp ngang, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/16 (md)/10 (ngoài hệ thống)/16 (md), căn MIN/CENTER
    - Rectangle "Ad media", vị trí 16,832, cỡ 48 x 48, nền #D9DDE2 (ngoài hệ thống), bo góc 8
    - Frame "Text", vị trí 76,836, cỡ 236 x 38, xếp dọc, cách nhau 2 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
      - Frame "Headline row", vị trí 76,836, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Frame "Ad badge", vị trí 76,836, cỡ 27 x 20, nền #D9DDE2 (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
          - Text "Ad", vị trí 82,838, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (ngoài hệ thống)
        - Text "Headline", vị trí 109,836, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
      - Text "Body", vị trí 76,858, cỡ 236 x 16, chữ "Native ad, bottom bar", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Frame "Ad CTA", vị trí 324,838, cỡ 71 x 36, viền #2F6FE0 (ngoài hệ thống), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
      - Text "Install", vị trí 339,846, cỡ 41 x 20, chữ "Install", kiểu ngoài hệ thống (14/20, đậm 600), màu #2F6FE0 (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
