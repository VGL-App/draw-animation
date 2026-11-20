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
  scripts/figma-export.py "<link section export trên Figma>" export
Màu và chữ trong context/ phần lớn ghi "ngoài hệ thống" vì :core:designsystem chưa đổi sang
thương hiệu Studio Night; plan đổi thương hiệu (docs/ops/new-app.md, mục 3) đưa các giá trị đó vào tokens.json.
Phiên tự chạy chỉ đọc thư mục này, không mở được Figma.
-->

## 1. Các màn hình và trạng thái

Kịch bản: theo spec của feature (spec chưa có; bản đề xuất mục 6, 9.1).

| Màn hình (composable) | Trạng thái | Ảnh trong `screens/`, số liệu trong `context/` |
| --- | --- | --- |
| `ExportScreen` | done, chế độ tối | `ExportScreen_done_dark` |
| `ExportScreen` | done | `ExportScreen_done_light` |
| `ExportScreen` | error, chế độ tối | `ExportScreen_error_dark` |
| `ExportScreen` | error | `ExportScreen_error_light` |
| `ExportScreen` | exporting, chế độ tối | `ExportScreen_exporting_dark` |
| `ExportScreen` | exporting | `ExportScreen_exporting_light` |
| `ExportScreen` | options, chế độ tối | `ExportScreen_options_dark` |
| `ExportScreen` | options | `ExportScreen_options_light` |

## 2. Bảng chữ

Chữ tiếng Anh như trên thiết kế (bản đề xuất Q9: tiếng Anh). Lấy từ frame chế độ tối; frame sáng cùng chữ. Chữ trong ô quảng cáo là chữ giữ chỗ, không vào `strings.xml`.

| Màn hình, trạng thái | Chữ | Kiểu chữ (text style trên Figma) |
| --- | --- | --- |
| `ExportScreen`, done, chế độ tối | Export | titleLarge |
| `ExportScreen`, done, chế độ tối | Saved to Gallery | headlineSmall |
| `ExportScreen`, done, chế độ tối | Movies/Frameloop/Bouncing ball.mp4 · 2.4 MB | bodyMedium |
| `ExportScreen`, done, chế độ tối | Share | labelLarge |
| `ExportScreen`, done, chế độ tối | Open in Gallery | labelLarge |
| `ExportScreen`, done, chế độ tối | Back to editor | labelLarge |
| `ExportScreen`, error, chế độ tối | Export | titleLarge |
| `ExportScreen`, error, chế độ tối | Export didn't finish | headlineSmall |
| `ExportScreen`, error, chế độ tối | Your project is safe. The phone is low on storage: free up about 50 MB and try again. | bodyLarge |
| `ExportScreen`, error, chế độ tối | Try again | labelLarge |
| `ExportScreen`, error, chế độ tối | Back to editor | labelLarge |
| `ExportScreen`, exporting, chế độ tối | Exporting | headlineSmall |
| `ExportScreen`, exporting, chế độ tối | Making your video… | titleLarge |
| `ExportScreen`, exporting, chế độ tối | Frame 30 of 48 | labelMedium |
| `ExportScreen`, exporting, chế độ tối | 62% · about 5 s left | labelMedium |
| `ExportScreen`, exporting, chế độ tối | Cancel | labelLarge |
| `ExportScreen`, options, chế độ tối | Export | titleLarge |
| `ExportScreen`, options, chế độ tối | Bouncing ball | titleMedium |
| `ExportScreen`, options, chế độ tối | 48 frames · 12 fps · 4.0 s | bodyMedium |
| `ExportScreen`, options, chế độ tối | 9:16 · 1080 × 1920 | bodySmall |
| `ExportScreen`, options, chế độ tối | Format | titleSmall |
| `ExportScreen`, options, chế độ tối | MP4 video | titleSmall |
| `ExportScreen`, options, chế độ tối | With sound · plays everywhere | bodySmall |
| `ExportScreen`, options, chế độ tối | GIF | titleSmall |
| `ExportScreen`, options, chế độ tối | Loops, no sound · good for chats | bodySmall |
| `ExportScreen`, options, chế độ tối | PNG sequence | titleSmall |
| `ExportScreen`, options, chế độ tối | One image per frame · for editing apps | bodySmall |
| `ExportScreen`, options, chế độ tối | Transparent GIF | titleSmall |
| `ExportScreen`, options, chế độ tối | No background · stickers | bodySmall |
| `ExportScreen`, options, chế độ tối | Quality | titleSmall |
| `ExportScreen`, options, chế độ tối | 720p | labelMedium |
| `ExportScreen`, options, chế độ tối | 1080p | labelMedium |
| `ExportScreen`, options, chế độ tối | Frameloop watermark | titleSmall |
| `ExportScreen`, options, chế độ tối | Turn off by watching one ad | bodySmall |
| `ExportScreen`, options, chế độ tối | Export MP4 | labelLarge |

## 3. Ánh xạ sang hệ thống thiết kế

Vai trò màu theo `tokens.json` (tên vai trò Material 3); giá trị Studio Night nằm trong biến Figma collection "Frameloop" (trang Design system).

| Phần tử trên thiết kế (component Figma) | Thành phần trong code | Màu (vai trò) | Kiểu chữ |
| --- | --- | --- | --- |
| Button | `PrimaryButton` (Style=Primary); `FilledTonalButton`, `OutlinedButton`, `TextButton` của Material 3 (Style=Tonal, Outlined, Text), truyền `colors` rõ | `primary`/`onPrimary`, `surfaceContainerHigh`/`onSurface`, `outline`, chữ `primary` | `labelLarge` |
| Chip | `FilterChip` của Material 3 | Default `surfaceContainerHigh`; Selected `primaryContainer`/`onPrimaryContainer` | `labelMedium` |
| FormatOption | Thành phần của feature `export` (thẻ chọn một) | Selected viền `primary` | `titleSmall`, `bodySmall` |
| Icon | `Icon(Icons.Rounded.<Tên>)` (material-icons-extended, bộ Rounded); tên ligature trong thiết kế là tên Material Symbols, đổi sang tên `Icons.Rounded` tương ứng | theo phần tử cha | - |
| IconButton | `IconButton` / `FilledIconButton` / `FilledTonalIconButton` của Material 3, 48 dp | `onSurface`; Filled: `primary`/`onPrimary` | - |
| MrecSlot | `AdSlot(placement)` banner MREC của `:core:ads` | do `:core:ads` vẽ | - |
| ProgressBar | `LinearProgressIndicator` của Material 3 | `primary`, rãnh `surfaceContainerHigh` | - |
| StatusBar | Thanh trạng thái của hệ thống, không vẽ; màn hình dùng `WindowInsets.statusBars` | - | - |
| Switch | `Switch` của Material 3 | On `primary`/`onPrimary` | - |

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
