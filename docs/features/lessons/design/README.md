---
feature: lessons
status: approved                # draft | approved
spec: features/lessons/spec.md
tool: figma
link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O
designer: AI (Claude), anh Nhật duyệt
approved_by: Nhật
approved_at: 2026-11-20
---

# THIẾT KẾ: BÀI HỌC

<!--
Quy trình: docs/ops/design-workflow.md. Quyết định: ADR-0008, ADR-0020, ADR-0021.
screens/ và context/ do scripts/figma-export.py sinh, không sửa tay. Thiết kế đổi thì chạy lại:
  scripts/figma-export.py "https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/Frameloop?node-id=26-1438" lessons
Màu và chữ trong context/ phần lớn ghi "ngoài hệ thống" vì :core:designsystem chưa đổi sang
thương hiệu Studio Night; plan đổi thương hiệu (docs/ops/new-app.md, mục 3) đưa các giá trị đó vào tokens.json.
Phiên tự chạy chỉ đọc thư mục này, không mở được Figma.
-->

## 1. Các màn hình và trạng thái

Kịch bản: theo [spec của feature](../spec.md).

| Màn hình (composable) | Trạng thái | Ảnh trong `screens/`, số liệu trong `context/` | Kịch bản |
| --- | --- | --- | --- |
| `LessonDoneScreen` | default, chế độ tối | `LessonDoneScreen_default_dark` | KB-5, KB-6, KB-7, KB-9 |
| `LessonDoneScreen` | default | `LessonDoneScreen_default_light` | KB-5, KB-6, KB-7, KB-9 |
| `LessonsScreen` | error, chế độ tối | `LessonsScreen_error_dark` | KB-2 |
| `LessonsScreen` | error | `LessonsScreen_error_light` | KB-2 |
| `LessonsScreen` | list, chế độ tối | `LessonsScreen_list_dark` | KB-1, KB-3, KB-4, KB-9 |
| `LessonsScreen` | list | `LessonsScreen_list_light` | KB-1, KB-3, KB-4, KB-9 |
| `LessonsScreen` | loading, chế độ tối | `LessonsScreen_loading_dark` | KB-2 |
| `LessonsScreen` | loading | `LessonsScreen_loading_light` | KB-2 |

## 2. Bảng chữ

Chữ tiếng Anh như trên thiết kế (bản đề xuất Q9: tiếng Anh). Lấy từ frame chế độ tối; frame sáng cùng chữ. Chữ trong ô quảng cáo là chữ giữ chỗ, không vào `strings.xml`.

| Màn hình, trạng thái | Vị trí (phần tử cha trong `context/`) | Chữ | Kiểu chữ (text style trên Figma) |
| --- | --- | --- | --- |
| `LessonDoneScreen`, default, chế độ tối | TopBar | Lesson 1 · Bouncing ball | titleMedium |
| `LessonDoneScreen`, default, chế độ tối | Content | Lesson complete! | headlineSmall |
| `LessonDoneScreen`, default, chế độ tối | Content | 12 frames · 1.0 s · Squash and stretch unlocked next | bodyMedium |
| `LessonDoneScreen`, default, chế độ tối | Button | Next lesson | labelLarge |
| `LessonDoneScreen`, default, chế độ tối | Button | Export my animation | labelLarge |
| `LessonsScreen`, error, chế độ tối | TopBar | Lessons | headlineSmall |
| `LessonsScreen`, error, chế độ tối | Content | Lessons didn't load | headlineSmall |
| `LessonsScreen`, error, chế độ tối | Content | The lesson server is not responding. Your projects still work. | bodyLarge |
| `LessonsScreen`, error, chế độ tối | Button | Try again | labelLarge |
| `LessonsScreen`, error, chế độ tối | NavItem | Projects | labelMedium |
| `LessonsScreen`, error, chế độ tối | NavItem | Lessons | labelMedium |
| `LessonsScreen`, list, chế độ tối | TopBar | Lessons | headlineSmall |
| `LessonsScreen`, list, chế độ tối | Chip | All | labelMedium |
| `LessonsScreen`, list, chế độ tối | Chip | Basics | labelMedium |
| `LessonsScreen`, list, chế độ tối | Chip | Motion | labelMedium |
| `LessonsScreen`, list, chế độ tối | Chip | Characters | labelMedium |
| `LessonsScreen`, list, chế độ tối | Chip | Effects | labelMedium |
| `LessonsScreen`, list, chế độ tối | SectionHeader | Basics | titleLarge |
| `LessonsScreen`, list, chế độ tối | SectionHeader | 2 of 6 | labelLarge |
| `LessonsScreen`, list, chế độ tối | Done badge | Done | labelSmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | Basics · 1 | labelSmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | Bouncing ball | titleSmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | 5 min · 12 frames | bodySmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | Basics · 2 | labelSmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | Squash and stretch | titleSmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | 6 min · 16 frames | bodySmall |
| `LessonsScreen`, list, chế độ tối | SectionHeader | Motion | titleLarge |
| `LessonsScreen`, list, chế độ tối | LessonCard | Motion · 1 | labelSmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | Stick figure walk | titleSmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | 8 min · 24 frames | bodySmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | Motion · 2 | labelSmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | Blink and look | titleSmall |
| `LessonsScreen`, list, chế độ tối | LessonCard | 4 min · 10 frames | bodySmall |
| `LessonsScreen`, list, chế độ tối | NavItem | Projects | labelMedium |
| `LessonsScreen`, list, chế độ tối | NavItem | Lessons | labelMedium |
| `LessonsScreen`, loading, chế độ tối | TopBar | Lessons | headlineSmall |
| `LessonsScreen`, loading, chế độ tối | Chip | All | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Chip | Basics | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Chip | Motion | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Chip | Characters | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Chip | Effects | labelMedium |
| `LessonsScreen`, loading, chế độ tối | NavItem | Projects | labelMedium |
| `LessonsScreen`, loading, chế độ tối | NavItem | Lessons | labelMedium |

## 3. Ánh xạ sang hệ thống thiết kế

Vai trò màu theo `tokens.json` (tên vai trò Material 3); giá trị Studio Night nằm trong biến Figma collection "Frameloop" (trang Design system).

| Phần tử trên thiết kế (component Figma) | Thành phần trong code | Màu (vai trò) | Kiểu chữ | Khoảng cách |
| --- | --- | --- | --- | --- |
| Button | `PrimaryButton` (Style=Primary); `FilledTonalButton`, `OutlinedButton`, `TextButton` của Material 3 (Style=Tonal, Outlined, Text), truyền `colors` rõ | `primary`/`onPrimary`, `surfaceContainerHigh`/`onSurface`, `outline`, chữ `primary` | `labelLarge` | theo `context/` |
| Chip | `FilterChip` của Material 3 | Default `surfaceContainerHigh`; Selected `primaryContainer`/`onPrimaryContainer` | `labelMedium` | theo `context/` |
| Icon | `Icon(Icons.Rounded.<Tên>)` (material-icons-extended, bộ Rounded); tên ligature trong thiết kế là tên Material Symbols, đổi sang tên `Icons.Rounded` tương ứng | theo phần tử cha | - | theo `context/` |
| IconButton | `IconButton` / `FilledIconButton` / `FilledTonalIconButton` của Material 3, 48 dp | `onSurface`; Filled: `primary`/`onPrimary` | - | theo `context/` |
| LessonCard | Thành phần của feature `lessons` | `surfaceContainer`, nhãn cấp `primary` | `titleSmall`, `bodySmall`, `labelSmall` | theo `context/` |
| NativeAdBar | `BottomAdBarScaffold` của `:core:ads` (thanh đáy native nhỏ) | do `:core:ads` vẽ | - | theo `context/` |
| NativeAdMedium | `NativeAdSlot(placement)` native medium của `:core:ads` | do `:core:ads` vẽ | - | theo `context/` |
| NavItem | `NavigationBarItem` trong `NavigationBar` (màn gập: `NavigationRail`) | chỉ báo `primaryContainer`, icon `onPrimaryContainer` | `labelMedium` | theo `context/` |
| StatusBar | Thanh trạng thái của hệ thống, không vẽ; màn hình dùng `WindowInsets.statusBars` | - | - | theo `context/` |

- **Thành phần mới cần thêm vào hệ thống thiết kế:** Không có thành phần dùng chung mới; `LessonCard` là thành phần của feature.

## 4. Luồng thao tác

Thanh điều hướng > `LessonsScreen` (`list`; `loading` khi đang tải từ R2, `error` khi tải lỗi). Chạm thẻ bài > interstitial `lesson_start_inter` > `EditorScreen` (`lesson-step`). Trước nét vẽ đầu tiên không có interstitial lúc vào bài (bản đề xuất mục 9.1). Hoàn thành bài > `LessonDoneScreen` > "Next lesson" (interstitial `lesson_next_inter`) hoặc "Export my animation" > `ExportScreen`.

## 5. Vị trí quảng cáo

`lessons_bottom`: thanh đáy native nhỏ; `lessons_grid`: ô native sau mỗi 4 thẻ (không có trên ảnh); `lesson_done_native`: native medium dưới kết quả (nhóm B); interstitial `lesson_start_inter`, `lesson_next_inter`. Trước nét vẽ đầu tiên không có quảng cáo nào trên màn này.

## 6. Chuyển sang Compose

- **Phải giống bản mẫu:** bố cục, thứ tự thành phần, chữ (mục 2), vai trò màu và kiểu chữ (mục 3).
- **Được phép khác:** cách vẽ chữ và bóng của hệ thống, hiệu ứng chuyển động, chênh lệch nhỏ theo lưới 4 dp, icon `Icons.Rounded` thay cho phông Material Symbols; khung xương lúc tải không có hiệu ứng nhấp nháy trên ảnh.

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
