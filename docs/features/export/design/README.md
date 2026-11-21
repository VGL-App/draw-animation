---
feature: export
status: approved                # draft | approved
spec: features/export/spec.md
tool: figma
link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O
designer: AI (Claude), anh Nhật duyệt
approved_by: Nhật
approved_at: 2026-11-20
---

# THIẾT KẾ: XUẤT VIDEO

<!--
Quy trình: docs/ops/design-workflow.md. Quyết định: ADR-0008, ADR-0020, ADR-0021.
screens/ và context/ do scripts/figma-export.py sinh, không sửa tay. Thiết kế đổi thì chạy lại:
  scripts/figma-export.py "https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/Frameloop?node-id=26-1437" export
Màu và chữ trong context/ phần lớn ghi "ngoài hệ thống" vì :core:designsystem chưa đổi sang
thương hiệu Studio Night; plan đổi thương hiệu (docs/ops/new-app.md, mục 3) đưa các giá trị đó vào tokens.json.
Phiên tự chạy chỉ đọc thư mục này, không mở được Figma.
-->

## 1. Các màn hình và trạng thái

Kịch bản: theo [spec của feature](../spec.md).

Sửa ngày 2026-11-21 theo câu trả lời ở mục 9 của spec, chờ anh Nhật duyệt: ảnh xem trước ở `options`, `exporting`, `done` có watermark đúng tỷ lệ. Mẫu watermark: frame `Watermark_reference` trên trang Screens/Export & Lessons (https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/?node-id=33-8115), không xuất ảnh vì không phải màn hình. Số đo: biểu tượng app và chữ "Frameloop" (Inter Semi Bold), trắng 90% có viền tối 0,5 px (đen 45%); cao bằng 5% cạnh ngắn của khung (54 px ở 1080 × 1920, 36 px ở 720p); góc dưới phải, cách mép 3% cạnh ngắn; giống nhau ở MP4, GIF, chuỗi PNG, GIF trong suốt. Lớp `PreviewWatermark` trên ảnh xem trước vẽ theo tỷ lệ khung, không vào bảng chữ; không có screenshot test riêng cho mẫu watermark.

| Màn hình (composable) | Trạng thái | Ảnh trong `screens/`, số liệu trong `context/` | Kịch bản |
| --- | --- | --- | --- |
| `ExportScreen` | done, chế độ tối | `ExportScreen_done_dark` | KB-5, KB-9 |
| `ExportScreen` | done | `ExportScreen_done_light` | KB-5, KB-9 |
| `ExportScreen` | error, chế độ tối | `ExportScreen_error_dark` | KB-6, KB-9 |
| `ExportScreen` | error | `ExportScreen_error_light` | KB-6, KB-9 |
| `ExportScreen` | exporting, chế độ tối | `ExportScreen_exporting_dark` | KB-4, KB-7, KB-9 |
| `ExportScreen` | exporting | `ExportScreen_exporting_light` | KB-4, KB-7, KB-9 |
| `ExportScreen` | options, chế độ tối | `ExportScreen_options_dark` | KB-1, KB-2, KB-3, KB-8, KB-9 |
| `ExportScreen` | options | `ExportScreen_options_light` | KB-1, KB-2, KB-3, KB-8, KB-9 |

## 2. Bảng chữ

Chữ tiếng Anh như trên thiết kế (bản đề xuất Q9: tiếng Anh). Lấy từ frame chế độ tối; frame sáng cùng chữ. Chữ trong ô quảng cáo là chữ giữ chỗ, không vào `strings.xml`.

| Màn hình, trạng thái | Vị trí (phần tử cha trong `context/`) | Chữ | Kiểu chữ (text style trên Figma) |
| --- | --- | --- | --- |
| `ExportScreen`, done, chế độ tối | TopBar | Export | titleLarge |
| `ExportScreen`, done, chế độ tối | Content | Saved to Gallery | headlineSmall |
| `ExportScreen`, done, chế độ tối | Content | Movies/Frameloop/Bouncing ball.mp4 · 2.4 MB | bodyMedium |
| `ExportScreen`, done, chế độ tối | Button | Share | labelLarge |
| `ExportScreen`, done, chế độ tối | Button | Open in Gallery | labelLarge |
| `ExportScreen`, done, chế độ tối | Button | Back to editor | labelLarge |
| `ExportScreen`, error, chế độ tối | TopBar | Export | titleLarge |
| `ExportScreen`, error, chế độ tối | Content | Export didn't finish | headlineSmall |
| `ExportScreen`, error, chế độ tối | Content | Your project is safe. The phone is low on storage: free up about 50 MB and try again. | bodyLarge |
| `ExportScreen`, error, chế độ tối | Button | Try again | labelLarge |
| `ExportScreen`, error, chế độ tối | Button | Back to editor | labelLarge |
| `ExportScreen`, exporting, chế độ tối | TopBar | Exporting | headlineSmall |
| `ExportScreen`, exporting, chế độ tối | Content | Making your video… | titleLarge |
| `ExportScreen`, exporting, chế độ tối | Meta | Frame 30 of 48 | labelMedium |
| `ExportScreen`, exporting, chế độ tối | Meta | 62% · about 5 s left | labelMedium |
| `ExportScreen`, exporting, chế độ tối | Button | Cancel | labelLarge |
| `ExportScreen`, options, chế độ tối | TopBar | Export | titleLarge |
| `ExportScreen`, options, chế độ tối | ProjectSummary | Bouncing ball | titleMedium |
| `ExportScreen`, options, chế độ tối | ProjectSummary | 48 frames · 12 fps · 4.0 s | bodyMedium |
| `ExportScreen`, options, chế độ tối | ProjectSummary | 9:16 · 1080 × 1920 | bodySmall |
| `ExportScreen`, options, chế độ tối | Content | Format | titleSmall |
| `ExportScreen`, options, chế độ tối | FormatOption | MP4 video | titleSmall |
| `ExportScreen`, options, chế độ tối | FormatOption | With sound · plays everywhere | bodySmall |
| `ExportScreen`, options, chế độ tối | FormatOption | GIF | titleSmall |
| `ExportScreen`, options, chế độ tối | FormatOption | Loops, no sound · good for chats | bodySmall |
| `ExportScreen`, options, chế độ tối | FormatOption | PNG sequence | titleSmall |
| `ExportScreen`, options, chế độ tối | FormatOption | One image per frame · for editing apps | bodySmall |
| `ExportScreen`, options, chế độ tối | Reward badge | Ad | labelMedium |
| `ExportScreen`, options, chế độ tối | FormatOption | Transparent GIF | titleSmall |
| `ExportScreen`, options, chế độ tối | FormatOption | No background · stickers | bodySmall |
| `ExportScreen`, options, chế độ tối | Content | Quality | titleSmall |
| `ExportScreen`, options, chế độ tối | Chip | 720p | labelMedium |
| `ExportScreen`, options, chế độ tối | Chip | 1080p | labelMedium |
| `ExportScreen`, options, chế độ tối | Watermark | Frameloop watermark | titleSmall |
| `ExportScreen`, options, chế độ tối | Watermark | Turn off by watching one ad | bodySmall |
| `ExportScreen`, options, chế độ tối | Button | Export MP4 | labelLarge |

## 3. Ánh xạ sang hệ thống thiết kế

Vai trò màu theo `tokens.json` (tên vai trò Material 3); giá trị Studio Night nằm trong biến Figma collection "Frameloop" (trang Design system).

| Phần tử trên thiết kế (component Figma) | Thành phần trong code | Màu (vai trò) | Kiểu chữ | Khoảng cách |
| --- | --- | --- | --- | --- |
| Button | `PrimaryButton` (Style=Primary); `FilledTonalButton`, `OutlinedButton`, `TextButton` của Material 3 (Style=Tonal, Outlined, Text), truyền `colors` rõ | `primary`/`onPrimary`, `surfaceContainerHigh`/`onSurface`, `outline`, chữ `primary` | `labelLarge` | theo `context/` |
| Chip | `FilterChip` của Material 3 | Default `surfaceContainerHigh`; Selected `primaryContainer`/`onPrimaryContainer` | `labelMedium` | theo `context/` |
| FormatOption | Thành phần của feature `export` (thẻ chọn một) | Selected viền `primary` | `titleSmall`, `bodySmall` | theo `context/` |
| Icon | `Icon(Icons.Rounded.<Tên>)` (material-icons-extended, bộ Rounded); tên ligature trong thiết kế là tên Material Symbols, đổi sang tên `Icons.Rounded` tương ứng | theo phần tử cha | - | theo `context/` |
| IconButton | `IconButton` / `FilledIconButton` / `FilledTonalIconButton` của Material 3, 48 dp | `onSurface`; Filled: `primary`/`onPrimary` | - | theo `context/` |
| MrecSlot | `AdSlot(placement)` banner MREC của `:core:ads` | do `:core:ads` vẽ | - | theo `context/` |
| NativeAdMedium | `NativeAdSlot(placement)` native medium của `:core:ads` | do `:core:ads` vẽ | - | theo `context/` |
| ProgressBar | `LinearProgressIndicator` của Material 3 | `primary`, rãnh `surfaceContainerHigh` | - | theo `context/` |
| StatusBar | Thanh trạng thái của hệ thống, không vẽ; màn hình dùng `WindowInsets.statusBars` | - | - | theo `context/` |
| Switch | `Switch` của Material 3 | On `primary`/`onPrimary` | - | theo `context/` |

- **Thành phần mới cần thêm vào hệ thống thiết kế:** Không có thành phần dùng chung mới; `FormatOption` là thành phần của feature.

## 4. Luồng thao tác

`EditorScreen` > "Export" > `ExportScreen` (`options`) > chọn định dạng (định dạng có nhãn Ad mở rewarded `export_rw_extra`), chất lượng, watermark > "Export MP4" > `exporting` > `done` (Share, Open in Gallery, Back to editor) hoặc `error` (Try again, Back to editor). Rời `done` có interstitial `export_interstitial`.

## 5. Vị trí quảng cáo

`export_options_mrec`: MREC ngay dưới thẻ tóm tắt, cách thẻ định dạng 24 dp, khung cố định 250 dp khi vị trí bật; `export_wait_mrec`: MREC ở màn chờ; `export_done_native`: native medium dưới kết quả, các nút ghim đáy cách 24 dp; `export_interstitial` khi rời màn xong; `export_rw_extra` rewarded cho PNG, nền trong, bỏ watermark.

## 6. Chuyển sang Compose

- **Phải giống bản mẫu:** bố cục, thứ tự thành phần, chữ (mục 2), vai trò màu và kiểu chữ (mục 3).
- **Được phép khác:** cách vẽ chữ và bóng của hệ thống, hiệu ứng chuyển động, chênh lệch nhỏ theo lưới 4 dp, icon `Icons.Rounded` thay cho phông Material Symbols; ảnh xem trước là khung đầu của dự án.

## 7. Tiêu chí khi duyệt

- [ ] Dùng đúng màu, chữ, khoảng cách, thành phần của hệ thống thiết kế.
- [ ] Đủ các trạng thái trong mục 1, kể cả chế độ tối.
- [ ] Chữ đúng bảng chữ, không bị cắt hay tràn.
- [ ] Bố cục và thứ tự thành phần giống bản mẫu; lệch về vị trí không quá một bước lưới 4 dp.
- [ ] Vùng chạm đủ lớn (ít nhất 48 dp), chữ dễ đọc.
- [ ] Dùng được ở màn hình thấp (360 x 640): nội dung cuộn được, chữ không chồng nhau, nút chính không bị che hay cắt.
- [ ] Vùng quảng cáo tách bạch, không gây bấm nhầm.
- [ ] Giao diện và nội dung không được thiết kế để thu hút chủ yếu trẻ em.

## 8. Kết quả đối chiếu

<!-- AI dán review/summary.md sau mỗi lần chạy scripts/design-compare.sh, kèm nhận xét của rà soát độc lập. -->
