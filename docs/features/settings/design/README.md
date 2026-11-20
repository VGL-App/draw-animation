---
feature: settings
status: approved                # draft | approved
spec: features/settings/spec.md
tool: figma
link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O
designer: AI (Claude), anh Nhật duyệt
approved_by: Nhật
approved_at: 2026-11-20
---

# THIẾT KẾ: CÀI ĐẶT

<!--
Quy trình: docs/ops/design-workflow.md. Quyết định: ADR-0008, ADR-0020, ADR-0021.
screens/ và context/ do scripts/figma-export.py sinh, không sửa tay. Thiết kế đổi thì chạy lại:
  scripts/figma-export.py "<link section settings trên Figma>" settings
Màu và chữ trong context/ phần lớn ghi "ngoài hệ thống" vì :core:designsystem chưa đổi sang
thương hiệu Studio Night; plan đổi thương hiệu (docs/ops/new-app.md, mục 3) đưa các giá trị đó vào tokens.json.
Phiên tự chạy chỉ đọc thư mục này, không mở được Figma.
-->

## 1. Các màn hình và trạng thái

Kịch bản: theo spec của feature (spec chưa có; bản đề xuất mục 6, 7).

| Màn hình (composable) | Trạng thái | Ảnh trong `screens/`, số liệu trong `context/` |
| --- | --- | --- |
| `SettingsScreen` | default, chế độ tối | `SettingsScreen_default_dark` |
| `SettingsScreen` | default | `SettingsScreen_default_light` |

## 2. Bảng chữ

Chữ tiếng Anh như trên thiết kế (bản đề xuất Q9: tiếng Anh). Lấy từ frame chế độ tối; frame sáng cùng chữ. Chữ trong ô quảng cáo là chữ giữ chỗ, không vào `strings.xml`.

| Màn hình, trạng thái | Chữ | Kiểu chữ (text style trên Figma) |
| --- | --- | --- |
| `SettingsScreen`, default, chế độ tối | Settings | titleLarge |
| `SettingsScreen`, default, chế độ tối | Drawing | labelLarge |
| `SettingsScreen`, default, chế độ tối | Default frame rate | bodyLarge |
| `SettingsScreen`, default, chế độ tối | 12 fps | bodyMedium |
| `SettingsScreen`, default, chế độ tối | Default canvas | bodyLarge |
| `SettingsScreen`, default, chế độ tối | 9:16 | bodyMedium |
| `SettingsScreen`, default, chế độ tối | Stylus pressure | bodyLarge |
| `SettingsScreen`, default, chế độ tối | On | bodyMedium |
| `SettingsScreen`, default, chế độ tối | Your projects | labelLarge |
| `SettingsScreen`, default, chế độ tối | Back up all projects | bodyLarge |
| `SettingsScreen`, default, chế độ tối | To a file | bodyMedium |
| `SettingsScreen`, default, chế độ tối | Restore from a file | bodyLarge |
| `SettingsScreen`, default, chế độ tối |  | bodyMedium |
| `SettingsScreen`, default, chế độ tối | Autosave | bodyLarge |
| `SettingsScreen`, default, chế độ tối | Every 30 s | bodyMedium |
| `SettingsScreen`, default, chế độ tối | About | labelLarge |
| `SettingsScreen`, default, chế độ tối | Ad privacy choices | bodyLarge |
| `SettingsScreen`, default, chế độ tối | Privacy policy | bodyLarge |
| `SettingsScreen`, default, chế độ tối | Rate Frameloop | bodyLarge |
| `SettingsScreen`, default, chế độ tối | Version | bodyLarge |
| `SettingsScreen`, default, chế độ tối | 1.0.0 | bodyMedium |

## 3. Ánh xạ sang hệ thống thiết kế

Vai trò màu theo `tokens.json` (tên vai trò Material 3); giá trị Studio Night nằm trong biến Figma collection "Frameloop" (trang Design system).

| Phần tử trên thiết kế (component Figma) | Thành phần trong code | Màu (vai trò) | Kiểu chữ |
| --- | --- | --- | --- |
| Icon | `Icon(Icons.Rounded.<Tên>)` (material-icons-extended, bộ Rounded); tên ligature trong thiết kế là tên Material Symbols, đổi sang tên `Icons.Rounded` tương ứng | theo phần tử cha | - |
| IconButton | `IconButton` / `FilledIconButton` / `FilledTonalIconButton` của Material 3, 48 dp | `onSurface`; Filled: `primary`/`onPrimary` | - |
| ListItem | `ListItem` của Material 3 | `onSurface`, `onSurfaceVariant` | `bodyLarge`, `bodyMedium` |
| StatusBar | Thanh trạng thái của hệ thống, không vẽ; màn hình dùng `WindowInsets.statusBars` | - | - |

- **Thành phần mới cần thêm vào hệ thống thiết kế:** Không có.

## 4. Luồng thao tác

`ProjectsScreen` > nút cài đặt > `SettingsScreen`. Back về `ProjectsScreen`. Mục "Ad privacy choices" mở lại hộp thoại đồng ý quảng cáo của `:core:ads`; "Back up all projects" và "Restore from a file" mở bộ chọn file hệ thống.

## 5. Vị trí quảng cáo

`settings_bottom`: thanh đáy native nhỏ (nhóm B).

## 6. Chuyển sang Compose

- **Phải giống bản mẫu:** bố cục, thứ tự thành phần, chữ (mục 2), vai trò màu và kiểu chữ (mục 3).
- **Được phép khác:** cách vẽ chữ và bóng của hệ thống, hiệu ứng chuyển động, chênh lệch nhỏ theo lưới 4 dp, icon `Icons.Rounded` thay cho phông Material Symbols.

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
