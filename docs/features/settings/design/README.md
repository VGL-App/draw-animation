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
  scripts/figma-export.py "https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/Frameloop?node-id=26-935" settings
Màu và chữ trong context/ đối chiếu với tokens.json của Studio Night (:core:designsystem đổi thương hiệu ngày
2026-11-22). Giá trị còn ghi "ngoài hệ thống" là màu nội dung (tranh mẫu, ô màu, quảng cáo) hay giá trị riêng của màn.
Phiên tự chạy chỉ đọc thư mục này, không mở được Figma.
-->

## 1. Các màn hình và trạng thái

Kịch bản: theo [spec của feature](../spec.md).

Bổ sung ngày 2026-11-21 theo câu trả lời ở mục 9 của spec, anh Nhật duyệt ngày 2026-11-21 (chọn "Duyệt" trong phiên): `backing-up`, `no-projects`. Thông báo sao lưu xong hay lỗi dùng Snackbar chuẩn, không có frame riêng.

| Màn hình (composable) | Trạng thái | Ảnh trong `screens/`, số liệu trong `context/` | Kịch bản |
| --- | --- | --- | --- |
| `SettingsScreen` | backing-up, chế độ tối | `SettingsScreen_backing-up_dark` | KB-5 |
| `SettingsScreen` | backing-up | `SettingsScreen_backing-up_light` | KB-5 |
| `SettingsScreen` | default, chế độ tối | `SettingsScreen_default_dark` | KB-1, KB-2, KB-3, KB-4, KB-5, KB-6, KB-7, KB-8, KB-9 |
| `SettingsScreen` | default | `SettingsScreen_default_light` | KB-1, KB-2, KB-3, KB-4, KB-5, KB-6, KB-7, KB-8, KB-9 |
| `SettingsScreen` | no-projects, chế độ tối | `SettingsScreen_no-projects_dark` | KB-5 |
| `SettingsScreen` | no-projects | `SettingsScreen_no-projects_light` | KB-5 |

## 2. Bảng chữ

Chữ tiếng Anh như trên thiết kế (bản đề xuất Q9: tiếng Anh). Lấy từ frame chế độ tối; frame sáng cùng chữ. Chữ trong ô quảng cáo là chữ giữ chỗ, không vào `strings.xml`.

| Màn hình, trạng thái | Vị trí (phần tử cha trong `context/`) | Chữ | Kiểu chữ (text style trên Figma) |
| --- | --- | --- | --- |
| `SettingsScreen`, backing-up, chế độ tối | TopBar | Settings | titleLarge |
| `SettingsScreen`, backing-up, chế độ tối | Section Drawing | Drawing | labelLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Default frame rate | bodyLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | 12 fps | bodyMedium |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Default canvas | bodyLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | 9:16 | bodyMedium |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Stylus pressure | bodyLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | On | bodyMedium |
| `SettingsScreen`, backing-up, chế độ tối | Section Your projects | Your projects | labelLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Back up all projects | bodyLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Restore from a file | bodyLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Autosave | bodyLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Every 30 s | bodyMedium |
| `SettingsScreen`, backing-up, chế độ tối | Section About | About | labelLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Ad privacy choices | bodyLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Privacy policy | bodyLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Rate Frameloop | bodyLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | Version | bodyLarge |
| `SettingsScreen`, backing-up, chế độ tối | ListItem | 1.0.0 | bodyMedium |
| `SettingsScreen`, default, chế độ tối | TopBar | Settings | titleLarge |
| `SettingsScreen`, default, chế độ tối | Section Drawing | Drawing | labelLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | Default frame rate | bodyLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | 12 fps | bodyMedium |
| `SettingsScreen`, default, chế độ tối | ListItem | Default canvas | bodyLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | 9:16 | bodyMedium |
| `SettingsScreen`, default, chế độ tối | ListItem | Stylus pressure | bodyLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | On | bodyMedium |
| `SettingsScreen`, default, chế độ tối | Section Your projects | Your projects | labelLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | Back up all projects | bodyLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | To a file | bodyMedium |
| `SettingsScreen`, default, chế độ tối | ListItem | Restore from a file | bodyLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | Autosave | bodyLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | Every 30 s | bodyMedium |
| `SettingsScreen`, default, chế độ tối | Section About | About | labelLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | Ad privacy choices | bodyLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | Privacy policy | bodyLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | Rate Frameloop | bodyLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | Version | bodyLarge |
| `SettingsScreen`, default, chế độ tối | ListItem | 1.0.0 | bodyMedium |
| `SettingsScreen`, no-projects, chế độ tối | TopBar | Settings | titleLarge |
| `SettingsScreen`, no-projects, chế độ tối | Section Drawing | Drawing | labelLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Default frame rate | bodyLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | 12 fps | bodyMedium |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Default canvas | bodyLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | 9:16 | bodyMedium |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Stylus pressure | bodyLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | On | bodyMedium |
| `SettingsScreen`, no-projects, chế độ tối | Section Your projects | Your projects | labelLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Back up all projects | bodyLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | No projects yet | bodyMedium |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Restore from a file | bodyLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Autosave | bodyLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Every 30 s | bodyMedium |
| `SettingsScreen`, no-projects, chế độ tối | Section About | About | labelLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Ad privacy choices | bodyLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Privacy policy | bodyLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Rate Frameloop | bodyLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | Version | bodyLarge |
| `SettingsScreen`, no-projects, chế độ tối | ListItem | 1.0.0 | bodyMedium |

## 3. Ánh xạ sang hệ thống thiết kế

Vai trò màu theo `tokens.json` (tên vai trò Material 3); giá trị Studio Night nằm trong biến Figma collection "Frameloop" (trang Design system).

| Phần tử trên thiết kế (component Figma) | Thành phần trong code | Màu (vai trò) | Kiểu chữ | Khoảng cách |
| --- | --- | --- | --- | --- |
| Icon | `Icon(Icons.Rounded.<Tên>)` (material-icons-extended, bộ Rounded); tên ligature trong thiết kế là tên Material Symbols, đổi sang tên `Icons.Rounded` tương ứng | theo phần tử cha | - | theo `context/` |
| IconButton | `IconButton` / `FilledIconButton` / `FilledTonalIconButton` của Material 3, 48 dp | `onSurface`; Filled: `primary`/`onPrimary` | - | theo `context/` |
| ListItem | `ListItem` của Material 3 | `onSurface`, `onSurfaceVariant` | `bodyLarge`, `bodyMedium` | theo `context/` |
| NativeAdBar | `BottomAdBarScaffold` của `:core:ads` (thanh đáy native nhỏ) | do `:core:ads` vẽ | - | theo `context/` |
| StatusBar | Thanh trạng thái của hệ thống, không vẽ; màn hình dùng `WindowInsets.statusBars` | - | - | theo `context/` |

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
