# SỐ LIỆU THIẾT KẾ: ProjectsScreen_empty_light

Sinh bởi `scripts/figma-export.py`. Không sửa tay; thiết kế đổi thì chạy lại script (ADR-0020).

## 1. Nguồn

- Link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=23-2345
- Frame: `23:2345`, giao diện sáng
- Ngày lấy: 2026-11-20

## 2. Giá trị dùng

Đối chiếu với `docs/design-system/tokens.json` theo giá trị. "ngoài hệ thống" nghĩa là không có token
nào trùng; plan quyết định làm tròn về token gần nhất hay thêm token.

| Loại | Giá trị | Token |
| --- | --- | --- |
| khoảng cách | 10 | ngoài hệ thống |
| khoảng cách | 12 | ngoài hệ thống |
| khoảng cách | 14 | ngoài hệ thống |
| khoảng cách | 2 | ngoài hệ thống |
| khoảng cách | 20 | ngoài hệ thống |
| khoảng cách | 24 | lg |
| khoảng cách | 4 | xs |
| khoảng cách | 6 | ngoài hệ thống |
| khoảng cách | 8 | sm |
| kiểu chữ | 11/16, đậm 500 | labelSmall |
| kiểu chữ | 12/16, đậm 400 | bodySmall |
| kiểu chữ | 12/16, đậm 500 | labelMedium |
| kiểu chữ | 14/20, đậm 500 | titleSmall, labelLarge |
| kiểu chữ | 14/20, đậm 600 | ngoài hệ thống |
| kiểu chữ | 16/16, đậm 400 | ngoài hệ thống |
| kiểu chữ | 16/24, đậm 400 | bodyLarge |
| kiểu chữ | 20/20, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/24, đậm 400 | ngoài hệ thống |
| kiểu chữ | 24/32, đậm 700 | ngoài hệ thống |
| màu chữ | #12141A | ngoài hệ thống |
| màu chữ | #1C2C05 | ngoài hệ thống |
| màu chữ | #2F6FE0 | ngoài hệ thống |
| màu chữ | #5F6675 | ngoài hệ thống |
| màu chữ | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest |
| màu nền | #1A1C22 | ngoài hệ thống |
| màu nền | #2F6FE0 | ngoài hệ thống |
| màu nền | #3D6A00 | ngoài hệ thống |
| màu nền | #D9DDE2 | ngoài hệ thống |
| màu nền | #DDF5B8 | ngoài hệ thống |
| màu nền | #E3E6EA | ngoài hệ thống |
| màu nền | #ECEEF1 | ngoài hệ thống |
| màu nền | #F7F8FA | ngoài hệ thống |
| màu nền | #FFFFFF | onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest |

## 3. Cấu trúc và số liệu

Toạ độ và cỡ tính theo dp, gốc là góc trên trái của khung. Thứ tự từ trên xuống như trong Figma.

- Frame "ProjectsScreen_empty_light", vị trí 0,0, cỡ 411 x 891, nền #F7F8FA (ngoài hệ thống), xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
  - Instance "StatusBar", vị trí 0,0, cỡ 411 x 28, xếp ngang, cách nhau 4 (xs), lề trên/phải/dưới/trái 0/20 (ngoài hệ thống)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Text "9:41", vị trí 20,4, cỡ 30 x 20, chữ "9:41", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 54,14, cỡ 277 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "Icon", vị trí 335,6, cỡ 16 x 16
      - Text "glyph", vị trí 335,6, cỡ 16 x 16, chữ "signal_cellular_alt", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 355,6, cỡ 16 x 16
      - Text "glyph", vị trí 355,6, cỡ 16 x 16, chữ "wifi", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "Icon", vị trí 375,6, cỡ 16 x 16
      - Text "glyph", vị trí 375,6, cỡ 16 x 16, chữ "battery_full", kiểu ngoài hệ thống (16/16, đậm 400), màu #12141A (ngoài hệ thống)
  - Frame "TopBar", vị trí 0,28, cỡ 411 x 64, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/4 (xs)/0/20 (ngoài hệ thống), căn MIN/CENTER
    - Frame "Wordmark", vị trí 20,44, cỡ 144 x 32, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
      - Ellipse "Ellipse", vị trí 20,53, cỡ 14 x 14, nền #3D6A00 (ngoài hệ thống)
      - Text "Frameloop", vị trí 42,44, cỡ 122 x 32, chữ "Frameloop", kiểu ngoài hệ thống (24/32, đậm 700), màu #12141A (ngoài hệ thống)
    - Frame "spacer", vị trí 164,60, cỡ 147 x 1, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "IconButton", vị trí 311,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 323,48, cỡ 24 x 24
        - Text "glyph", vị trí 323,48, cỡ 24 x 24, chữ "upload_file", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
    - Instance "IconButton", vị trí 359,36, cỡ 48 x 48, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn CENTER/CENTER
      - Instance "Glyph", vị trí 371,48, cỡ 24 x 24
        - Text "glyph", vị trí 371,48, cỡ 24 x 24, chữ "settings", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
  - Frame "Content", vị trí 0,92, cỡ 411 x 711, xếp dọc, cách nhau 12 (ngoài hệ thống), lề trên/phải/dưới/trái 0/24 (lg)/0/24 (lg), căn CENTER/CENTER
    - Frame "Illustration", vị trí 126,120, cỡ 160 x 120, nền #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest), bo góc 14
      - Ellipse "Ellipse", vị trí 156,190, cỡ 40 x 40, nền #2F6FE0 (ngoài hệ thống)
      - Ellipse "Ellipse", vị trí 196,174, cỡ 40 x 40, nền #2F6FE0 (ngoài hệ thống)
      - Ellipse "Ellipse", vị trí 236,158, cỡ 40 x 40, nền #2F6FE0 (ngoài hệ thống)
      - Rectangle "Rectangle", vị trí 146,250, cỡ 160 x 3, nền #1A1C22 (ngoài hệ thống)
    - Text "Make your first animatio", vị trí 24,252, cỡ 363 x 32, chữ "Make your first animation", kiểu ngoài hệ thống (24/32, đậm 700), màu #12141A (ngoài hệ thống)
    - Text "Draw frame by frame, add", vị trí 24,296, cỡ 363 x 72, chữ "Draw frame by frame, add sound, then export a video or GIF. Everything saves on your phone as you go.", kiểu bodyLarge (16/24, đậm 400), màu #5F6675 (ngoài hệ thống)
    - Instance "Button", vị trí 24,380, cỡ 363 x 48, nền #3D6A00 (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 139,394, cỡ 20 x 20
        - Text "glyph", vị trí 139,394, cỡ 20 x 20, chữ "add", kiểu ngoài hệ thống (20/20, đậm 400), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
      - Text "Label", vị trí 167,394, cỡ 101 x 20, chữ "New animation", kiểu ngoài hệ thống (14/20, đậm 600), màu #FFFFFF (onPrimary, onSecondary, onTertiary, onError, surfaceContainerLowest)
    - Instance "Button", vị trí 24,440, cỡ 363 x 48, nền #E3E6EA (ngoài hệ thống), bo góc 14, xếp ngang, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/24 (lg)/12 (ngoài hệ thống)/20 (ngoài hệ thống), căn CENTER/CENTER
      - Instance "Leading icon", vị trí 133,454, cỡ 20 x 20
        - Text "glyph", vị trí 133,454, cỡ 20 x 20, chữ "school", kiểu ngoài hệ thống (20/20, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Label", vị trí 161,454, cỡ 113 x 20, chữ "Try a free lesson", kiểu ngoài hệ thống (14/20, đậm 600), màu #12141A (ngoài hệ thống)
    - Frame "gap 24", vị trí 205,500, cỡ 1 x 12, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
    - Instance "projects_empty_native (NativeAdMedium)", vị trí 24,524, cỡ 363 x 250, nền #ECEEF1 (ngoài hệ thống), viền #D2D6DC (ngoài hệ thống), dày 1.0, bo góc 14, xếp dọc, cách nhau 8 (sm), lề trên/phải/dưới/trái 12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống)/12 (ngoài hệ thống), căn MIN/MIN
      - Frame "Header", vị trí 37,538, cỡ 337 x 36, xếp ngang, cách nhau 10 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
        - Rectangle "Ad icon", vị trí 37,538, cỡ 36 x 36, nền #D9DDE2 (ngoài hệ thống), bo góc 8
        - Frame "Text", vị trí 83,538, cỡ 291 x 36, xếp dọc, cách nhau 0, lề trên/phải/dưới/trái 0/0/0/0, căn MIN/MIN
          - Frame "Headline row", vị trí 83,538, cỡ 176 x 20, xếp ngang, cách nhau 6 (ngoài hệ thống), lề trên/phải/dưới/trái 0/0/0/0, căn MIN/CENTER
            - Frame "Ad badge", vị trí 83,538, cỡ 27 x 20, nền #D9DDE2 (ngoài hệ thống), bo góc 4, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 2 (ngoài hệ thống)/6 (ngoài hệ thống)/2 (ngoài hệ thống)/6 (ngoài hệ thống), căn MIN/MIN
              - Text "Ad", vị trí 89,540, cỡ 15 x 16, chữ "Ad", kiểu labelSmall (11/16, đậm 500), màu #5F6675 (ngoài hệ thống)
            - Text "Headline", vị trí 116,538, cỡ 143 x 20, chữ "Sponsored app name", kiểu titleSmall, labelLarge (14/20, đậm 500), màu #12141A (ngoài hệ thống)
          - Text "Advertiser", vị trí 83,558, cỡ 59 x 16, chữ "Advertiser", kiểu bodySmall (12/16, đậm 400), màu #5F6675 (ngoài hệ thống)
      - Rectangle "Ad media", vị trí 37,582, cỡ 337 x 136, nền #D9DDE2 (ngoài hệ thống), bo góc 8
      - Frame "Ad CTA", vị trí 37,726, cỡ 337 x 36, viền #2F6FE0 (ngoài hệ thống), dày 1.0, bo góc 6, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 0/14 (ngoài hệ thống)/0/14 (ngoài hệ thống), căn CENTER/CENTER
        - Text "Install", vị trí 185,734, cỡ 41 x 20, chữ "Install", kiểu ngoài hệ thống (14/20, đậm 600), màu #2F6FE0 (ngoài hệ thống)
  - Frame "BottomNav", vị trí 0,803, cỡ 411 x 88, nền #ECEEF1 (ngoài hệ thống), xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/0/12 (ngoài hệ thống)/0, căn SPACE_BETWEEN/MIN
    - Instance "NavItem", vị trí 0,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 71,817, cỡ 64 x 32, nền #DDF5B8 (ngoài hệ thống), bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 91,821, cỡ 24 x 24
          - Text "glyph", vị trí 91,821, cỡ 24 x 24, chữ "animation", kiểu ngoài hệ thống (24/24, đậm 400), màu #1C2C05 (ngoài hệ thống)
      - Text "Label", vị trí 79,853, cỡ 47 x 16, chữ "Projects", kiểu labelMedium (12/16, đậm 500), màu #12141A (ngoài hệ thống)
    - Instance "NavItem", vị trí 206,807, cỡ 206 x 72, xếp dọc, cách nhau 4 (xs), lề trên/phải/dưới/trái 10 (ngoài hệ thống)/0/10 (ngoài hệ thống)/0, căn MIN/CENTER
      - Frame "Indicator", vị trí 276,817, cỡ 64 x 32, bo góc 999, xếp ngang, cách nhau 0, lề trên/phải/dưới/trái 4 (xs)/20 (ngoài hệ thống)/4 (xs)/20 (ngoài hệ thống), căn MIN/MIN
        - Instance "Glyph", vị trí 296,821, cỡ 24 x 24
          - Text "glyph", vị trí 296,821, cỡ 24 x 24, chữ "school", kiểu ngoài hệ thống (24/24, đậm 400), màu #12141A (ngoài hệ thống)
      - Text "Label", vị trí 284,853, cỡ 48 x 16, chữ "Lessons", kiểu labelMedium (12/16, đậm 500), màu #5F6675 (ngoài hệ thống)

## 4. Code tham khảo

Không có. Script không sinh code; AI viết Compose theo mục 2 và 3.
