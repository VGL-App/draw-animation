---
feature: projects
status: approved                # draft | approved
spec: features/projects/spec.md
tool: figma
link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O
designer: AI (Claude), anh Nhật duyệt
approved_by: Nhật
approved_at: 2026-11-20
---

# THIẾT KẾ: DANH SÁCH DỰ ÁN VÀ TẠO DỰ ÁN

<!--
Quy trình: docs/ops/design-workflow.md. Quyết định: ADR-0008, ADR-0020, ADR-0021.
screens/ và context/ do scripts/figma-export.py sinh, không sửa tay. Thiết kế đổi thì chạy lại:
  scripts/figma-export.py "<link section projects trên Figma>" projects
Màu và chữ trong context/ phần lớn ghi "ngoài hệ thống" vì :core:designsystem chưa đổi sang
thương hiệu Studio Night; plan đổi thương hiệu (docs/ops/new-app.md, mục 3) đưa các giá trị đó vào tokens.json.
Phiên tự chạy chỉ đọc thư mục này, không mở được Figma.
-->

## 1. Các màn hình và trạng thái

Kịch bản: theo spec của feature (spec chưa có; bản đề xuất mục 5, 6).

| Màn hình (composable) | Trạng thái | Ảnh trong `screens/`, số liệu trong `context/` |
| --- | --- | --- |
| `NewProjectScreen` | default, chế độ tối | `NewProjectScreen_default_dark` |
| `NewProjectScreen` | default | `NewProjectScreen_default_light` |
| `ProjectsScreen` | empty, chế độ tối | `ProjectsScreen_empty_dark` |
| `ProjectsScreen` | empty | `ProjectsScreen_empty_light` |
| `ProjectsScreen` | list, chế độ tối | `ProjectsScreen_list_dark` |
| `ProjectsScreen` | list, chế độ tối, điện thoại gập mở ra | `ProjectsScreen_list_dark_unfolded` |
| `ProjectsScreen` | list | `ProjectsScreen_list_light` |
| `ProjectsScreen` | list, điện thoại gập mở ra | `ProjectsScreen_list_light_unfolded` |
| `ProjectsScreen` | menu, chế độ tối | `ProjectsScreen_menu_dark` |
| `ProjectsScreen` | menu | `ProjectsScreen_menu_light` |
| `ProjectsScreen` | restore-error, chế độ tối | `ProjectsScreen_restore-error_dark` |
| `ProjectsScreen` | restore-error | `ProjectsScreen_restore-error_light` |
| `ProjectsScreen` | restoring, chế độ tối | `ProjectsScreen_restoring_dark` |
| `ProjectsScreen` | restoring | `ProjectsScreen_restoring_light` |

## 2. Bảng chữ

Chữ tiếng Anh như trên thiết kế (bản đề xuất Q9: tiếng Anh). Lấy từ frame chế độ tối; frame sáng cùng chữ. Chữ trong ô quảng cáo là chữ giữ chỗ, không vào `strings.xml`.

| Màn hình, trạng thái | Chữ | Kiểu chữ (text style trên Figma) |
| --- | --- | --- |
| `NewProjectScreen`, default, chế độ tối | New animation | titleLarge |
| `NewProjectScreen`, default, chế độ tối | Name | labelSmall |
| `NewProjectScreen`, default, chế độ tối | My animation 5 | bodyLarge |
| `NewProjectScreen`, default, chế độ tối | Canvas | titleSmall |
| `NewProjectScreen`, default, chế độ tối | 9:16 | labelLarge |
| `NewProjectScreen`, default, chế độ tối | 16:9 | labelLarge |
| `NewProjectScreen`, default, chế độ tối | 1:1 | labelLarge |
| `NewProjectScreen`, default, chế độ tối | 4:5 | labelLarge |
| `NewProjectScreen`, default, chế độ tối | Size | titleSmall |
| `NewProjectScreen`, default, chế độ tối | 720p | labelMedium |
| `NewProjectScreen`, default, chế độ tối | 1080p | labelMedium |
| `NewProjectScreen`, default, chế độ tối | Frame rate | titleSmall |
| `NewProjectScreen`, default, chế độ tối | 6 | labelMedium |
| `NewProjectScreen`, default, chế độ tối | 8 | labelMedium |
| `NewProjectScreen`, default, chế độ tối | 12 fps | labelMedium |
| `NewProjectScreen`, default, chế độ tối | 15 | labelMedium |
| `NewProjectScreen`, default, chế độ tối | 24 | labelMedium |
| `NewProjectScreen`, default, chế độ tối | 30 | labelMedium |
| `NewProjectScreen`, default, chế độ tối | Background | titleSmall |
| `NewProjectScreen`, default, chế độ tối | Photo | labelLarge |
| `NewProjectScreen`, default, chế độ tối | Create | labelLarge |
| `ProjectsScreen`, empty, chế độ tối | Frameloop | headlineSmall |
| `ProjectsScreen`, empty, chế độ tối | Make your first animation | headlineSmall |
| `ProjectsScreen`, empty, chế độ tối | Draw frame by frame, add sound, then export a video or GIF. Everything saves on your phone as you go. | bodyLarge |
| `ProjectsScreen`, empty, chế độ tối | New animation | labelLarge |
| `ProjectsScreen`, empty, chế độ tối | Try a free lesson | labelLarge |
| `ProjectsScreen`, empty, chế độ tối | Projects | labelMedium |
| `ProjectsScreen`, empty, chế độ tối | Lessons | labelMedium |
| `ProjectsScreen`, list, chế độ tối | Frameloop | headlineSmall |
| `ProjectsScreen`, list, chế độ tối | New animation | labelLarge |
| `ProjectsScreen`, list, chế độ tối | Your projects | titleLarge |
| `ProjectsScreen`, list, chế độ tối | Recent | labelLarge |
| `ProjectsScreen`, list, chế độ tối | Bouncing ball | titleSmall |
| `ProjectsScreen`, list, chế độ tối | 48 frames · 12 fps | bodySmall |
| `ProjectsScreen`, list, chế độ tối | Walk cycle | titleSmall |
| `ProjectsScreen`, list, chế độ tối | 24 frames · 12 fps | bodySmall |
| `ProjectsScreen`, list, chế độ tối | Logo sting | titleSmall |
| `ProjectsScreen`, list, chế độ tối | 90 frames · 24 fps | bodySmall |
| `ProjectsScreen`, list, chế độ tối | Blink test | titleSmall |
| `ProjectsScreen`, list, chế độ tối | 16 frames · 8 fps | bodySmall |
| `ProjectsScreen`, list, chế độ tối | Projects | labelMedium |
| `ProjectsScreen`, list, chế độ tối | Lessons | labelMedium |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | Projects | labelMedium |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | Lessons | labelMedium |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | Frameloop | headlineSmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | New animation | labelLarge |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | Your projects | titleLarge |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | Bouncing ball | titleSmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | 48 frames · 12 fps | bodySmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | Walk cycle | titleSmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | 24 frames · 12 fps | bodySmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | Logo sting | titleSmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | 90 frames · 24 fps | bodySmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | Blink test | titleSmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | 16 frames · 8 fps | bodySmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | Rain loop | titleSmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | 32 frames · 12 fps | bodySmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | Jump | titleSmall |
| `ProjectsScreen`, list, chế độ tối, điện thoại gập mở ra | 20 frames · 12 fps | bodySmall |
| `ProjectsScreen`, menu, chế độ tối | Frameloop | headlineSmall |
| `ProjectsScreen`, menu, chế độ tối | New animation | labelLarge |
| `ProjectsScreen`, menu, chế độ tối | Your projects | titleLarge |
| `ProjectsScreen`, menu, chế độ tối | Recent | labelLarge |
| `ProjectsScreen`, menu, chế độ tối | Bouncing ball | titleSmall |
| `ProjectsScreen`, menu, chế độ tối | 48 frames · 12 fps | bodySmall |
| `ProjectsScreen`, menu, chế độ tối | Walk cycle | titleSmall |
| `ProjectsScreen`, menu, chế độ tối | 24 frames · 12 fps | bodySmall |
| `ProjectsScreen`, menu, chế độ tối | Logo sting | titleSmall |
| `ProjectsScreen`, menu, chế độ tối | 90 frames · 24 fps | bodySmall |
| `ProjectsScreen`, menu, chế độ tối | Blink test | titleSmall |
| `ProjectsScreen`, menu, chế độ tối | 16 frames · 8 fps | bodySmall |
| `ProjectsScreen`, menu, chế độ tối | Projects | labelMedium |
| `ProjectsScreen`, menu, chế độ tối | Lessons | labelMedium |
| `ProjectsScreen`, menu, chế độ tối | Walk cycle | titleMedium |
| `ProjectsScreen`, menu, chế độ tối | 24 frames · 12 fps · edited today | bodySmall |
| `ProjectsScreen`, menu, chế độ tối |  | bodyMedium |
| `ProjectsScreen`, menu, chế độ tối | Rename | bodyLarge |
| `ProjectsScreen`, menu, chế độ tối | Duplicate | bodyLarge |
| `ProjectsScreen`, menu, chế độ tối | Back up to a file | bodyLarge |
| `ProjectsScreen`, menu, chế độ tối | .frameloop | bodyMedium |
| `ProjectsScreen`, menu, chế độ tối | Delete | bodyLarge |
| `ProjectsScreen`, restore-error, chế độ tối | Frameloop | headlineSmall |
| `ProjectsScreen`, restore-error, chế độ tối | New animation | labelLarge |
| `ProjectsScreen`, restore-error, chế độ tối | Your projects | titleLarge |
| `ProjectsScreen`, restore-error, chế độ tối | Recent | labelLarge |
| `ProjectsScreen`, restore-error, chế độ tối | Bouncing ball | titleSmall |
| `ProjectsScreen`, restore-error, chế độ tối | 48 frames · 12 fps | bodySmall |
| `ProjectsScreen`, restore-error, chế độ tối | Walk cycle | titleSmall |
| `ProjectsScreen`, restore-error, chế độ tối | 24 frames · 12 fps | bodySmall |
| `ProjectsScreen`, restore-error, chế độ tối | Logo sting | titleSmall |
| `ProjectsScreen`, restore-error, chế độ tối | 90 frames · 24 fps | bodySmall |
| `ProjectsScreen`, restore-error, chế độ tối | Blink test | titleSmall |
| `ProjectsScreen`, restore-error, chế độ tối | 16 frames · 8 fps | bodySmall |
| `ProjectsScreen`, restore-error, chế độ tối | Projects | labelMedium |
| `ProjectsScreen`, restore-error, chế độ tối | Lessons | labelMedium |
| `ProjectsScreen`, restore-error, chế độ tối | Can't open this file | titleLarge |
| `ProjectsScreen`, restore-error, chế độ tối | This file is not a Frameloop backup, or it is damaged. Your other projects are safe. | bodyMedium |
| `ProjectsScreen`, restore-error, chế độ tối | Close | labelLarge |
| `ProjectsScreen`, restore-error, chế độ tối | Choose another file | labelLarge |
| `ProjectsScreen`, restoring, chế độ tối | Frameloop | headlineSmall |
| `ProjectsScreen`, restoring, chế độ tối | New animation | labelLarge |
| `ProjectsScreen`, restoring, chế độ tối | Your projects | titleLarge |
| `ProjectsScreen`, restoring, chế độ tối | Recent | labelLarge |
| `ProjectsScreen`, restoring, chế độ tối | Bouncing ball | titleSmall |
| `ProjectsScreen`, restoring, chế độ tối | 48 frames · 12 fps | bodySmall |
| `ProjectsScreen`, restoring, chế độ tối | Walk cycle | titleSmall |
| `ProjectsScreen`, restoring, chế độ tối | 24 frames · 12 fps | bodySmall |
| `ProjectsScreen`, restoring, chế độ tối | Logo sting | titleSmall |
| `ProjectsScreen`, restoring, chế độ tối | 90 frames · 24 fps | bodySmall |
| `ProjectsScreen`, restoring, chế độ tối | Blink test | titleSmall |
| `ProjectsScreen`, restoring, chế độ tối | 16 frames · 8 fps | bodySmall |
| `ProjectsScreen`, restoring, chế độ tối | Projects | labelMedium |
| `ProjectsScreen`, restoring, chế độ tối | Lessons | labelMedium |
| `ProjectsScreen`, restoring, chế độ tối | Restoring “Walk cycle” | titleLarge |
| `ProjectsScreen`, restoring, chế độ tối | Keep the app open. This takes a few seconds. | bodyMedium |
| `ProjectsScreen`, restoring, chế độ tối | 48 of 120 frames | labelMedium |
| `ProjectsScreen`, restoring, chế độ tối | 40% | labelMedium |

## 3. Ánh xạ sang hệ thống thiết kế

Vai trò màu theo `tokens.json` (tên vai trò Material 3); giá trị Studio Night nằm trong biến Figma collection "Frameloop" (trang Design system).

| Phần tử trên thiết kế (component Figma) | Thành phần trong code | Màu (vai trò) | Kiểu chữ |
| --- | --- | --- | --- |
| Button | `PrimaryButton` (Style=Primary); `FilledTonalButton`, `OutlinedButton`, `TextButton` của Material 3 (Style=Tonal, Outlined, Text), truyền `colors` rõ | `primary`/`onPrimary`, `surfaceContainerHigh`/`onSurface`, `outline`, chữ `primary` | `labelLarge` |
| Chip | `FilterChip` của Material 3 | Default `surfaceContainerHigh`; Selected `primaryContainer`/`onPrimaryContainer` | `labelMedium` |
| ColorSwatch | Thành phần của feature `editor` | màu đang chọn, viền `outlineVariant`, vòng chọn `onSurface` | - |
| Dialog | `AlertDialog` của Material 3 | `surfaceContainerHigh` | `titleLarge`, `bodyMedium` |
| Icon | `Icon(Icons.Rounded.<Tên>)` (material-icons-extended, bộ Rounded); tên ligature trong thiết kế là tên Material Symbols, đổi sang tên `Icons.Rounded` tương ứng | theo phần tử cha | - |
| IconButton | `IconButton` / `FilledIconButton` / `FilledTonalIconButton` của Material 3, 48 dp | `onSurface`; Filled: `primary`/`onPrimary` | - |
| ListItem | `ListItem` của Material 3 | `onSurface`, `onSurfaceVariant` | `bodyLarge`, `bodyMedium` |
| MrecSlot | `AdSlot(placement)` banner MREC của `:core:ads` | do `:core:ads` vẽ | - |
| NavItem | `NavigationBarItem` trong `NavigationBar` (màn gập: `NavigationRail`) | chỉ báo `primaryContainer`, icon `onPrimaryContainer` | `labelMedium` |
| ProgressBar | `LinearProgressIndicator` của Material 3 | `primary`, rãnh `surfaceContainerHigh` | - |
| ProjectCard | Thành phần của feature `projects` | `surfaceContainer`, ảnh thu nhỏ `canvas` | `titleSmall`, `bodySmall` |
| SheetHandle | `ModalBottomSheet` của Material 3 (tay kéo mặc định) | `surfaceContainerHigh`, tay kéo `outline` | - |
| StatusBar | Thanh trạng thái của hệ thống, không vẽ; màn hình dùng `WindowInsets.statusBars` | - | - |

- **Thành phần mới cần thêm vào hệ thống thiết kế:** Không có thành phần dùng chung mới; `ProjectCard` là thành phần của feature.

## 4. Luồng thao tác

`OnboardingScreen` > `ProjectsScreen` (trống) > "New animation" > `NewProjectScreen` > "Create" > `EditorScreen`. "Try a free lesson" > `LessonsScreen`. Chạm thẻ dự án > `EditorScreen`; nhấn giữ thẻ > menu dự án (bottom sheet: Open, Rename, Duplicate, Back up to a file, Delete). Nút tải lên trên thanh trên > bộ chọn file hệ thống > hộp thoại khôi phục (`restoring`) > danh sách, hoặc hộp thoại lỗi (`restore-error`). Nút cài đặt > `SettingsScreen`. Thanh điều hướng: Projects, Lessons.

## 5. Vị trí quảng cáo

`projects_empty_native` native medium dưới hai nút ở màn trống (cách 24 dp); `projects_bottom` thanh đáy native nhỏ trên thanh điều hướng; `projects_grid` ô native trong lưới sau mỗi 6 dự án (không có trên ảnh vì các frame có ít dự án); `restore_mrec` MREC trong hộp thoại khôi phục, cách nút 24 dp, hộp thoại rộng 348 dp; `new_project_native` native medium trong phần cuộn của `NewProjectScreen`, nút Create ghim đáy cách 24 dp; `create_inter` interstitial khi bấm Create từ dự án thứ 2. Màn Projects khai `InContentAds` (MC-13). Trước nét vẽ đầu tiên chỉ có `projects_empty_native` trên màn này (bản đề xuất mục 9.1).

## 6. Chuyển sang Compose

- **Phải giống bản mẫu:** bố cục, thứ tự thành phần, chữ (mục 2), vai trò màu và kiểu chữ (mục 3).
- **Được phép khác:** cách vẽ chữ và bóng của hệ thống, hiệu ứng chuyển động, chênh lệch nhỏ theo lưới 4 dp, icon `Icons.Rounded` thay cho phông Material Symbols; hộp thoại khôi phục dùng `usePlatformDefaultWidth = false` để đủ 348 dp.

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
