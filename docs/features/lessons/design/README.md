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
  scripts/figma-export.py "<link section lessons trên Figma>" lessons
Màu và chữ trong context/ phần lớn ghi "ngoài hệ thống" vì :core:designsystem chưa đổi sang
thương hiệu Studio Night; plan đổi thương hiệu (docs/ops/new-app.md, mục 3) đưa các giá trị đó vào tokens.json.
Phiên tự chạy chỉ đọc thư mục này, không mở được Figma.
-->

## 1. Các màn hình và trạng thái

Kịch bản: theo spec của feature (spec chưa có; bản đề xuất mục 5, 6, Q12).

| Màn hình (composable) | Trạng thái | Ảnh trong `screens/`, số liệu trong `context/` |
| --- | --- | --- |
| `LessonDoneScreen` | default, chế độ tối | `LessonDoneScreen_default_dark` |
| `LessonDoneScreen` | default | `LessonDoneScreen_default_light` |
| `LessonsScreen` | error, chế độ tối | `LessonsScreen_error_dark` |
| `LessonsScreen` | error | `LessonsScreen_error_light` |
| `LessonsScreen` | list, chế độ tối | `LessonsScreen_list_dark` |
| `LessonsScreen` | list | `LessonsScreen_list_light` |
| `LessonsScreen` | loading, chế độ tối | `LessonsScreen_loading_dark` |
| `LessonsScreen` | loading | `LessonsScreen_loading_light` |

## 2. Bảng chữ

Chữ tiếng Anh như trên thiết kế (bản đề xuất Q9: tiếng Anh). Lấy từ frame chế độ tối; frame sáng cùng chữ. Chữ trong ô quảng cáo là chữ giữ chỗ, không vào `strings.xml`.

| Màn hình, trạng thái | Chữ | Kiểu chữ (text style trên Figma) |
| --- | --- | --- |
| `LessonDoneScreen`, default, chế độ tối | Lesson 1 · Bouncing ball | titleMedium |
| `LessonDoneScreen`, default, chế độ tối | Lesson complete! | headlineSmall |
| `LessonDoneScreen`, default, chế độ tối | 12 frames · 1.0 s · Squash and stretch unlocked next | bodyMedium |
| `LessonDoneScreen`, default, chế độ tối | Next lesson | labelLarge |
| `LessonDoneScreen`, default, chế độ tối | Export my animation | labelLarge |
| `LessonsScreen`, error, chế độ tối | Lessons | headlineSmall |
| `LessonsScreen`, error, chế độ tối | Lessons didn't load | headlineSmall |
| `LessonsScreen`, error, chế độ tối | The lesson server is not responding. Your projects still work. | bodyLarge |
| `LessonsScreen`, error, chế độ tối | Try again | labelLarge |
| `LessonsScreen`, error, chế độ tối | Projects | labelMedium |
| `LessonsScreen`, error, chế độ tối | Lessons | labelMedium |
| `LessonsScreen`, list, chế độ tối | Lessons | headlineSmall |
| `LessonsScreen`, list, chế độ tối | All | labelMedium |
| `LessonsScreen`, list, chế độ tối | Basics | labelMedium |
| `LessonsScreen`, list, chế độ tối | Motion | labelMedium |
| `LessonsScreen`, list, chế độ tối | Characters | labelMedium |
| `LessonsScreen`, list, chế độ tối | Effects | labelMedium |
| `LessonsScreen`, list, chế độ tối | Basics | titleLarge |
| `LessonsScreen`, list, chế độ tối | 2 of 6 | labelLarge |
| `LessonsScreen`, list, chế độ tối | Done | labelSmall |
| `LessonsScreen`, list, chế độ tối | Basics · 1 | labelSmall |
| `LessonsScreen`, list, chế độ tối | Bouncing ball | titleSmall |
| `LessonsScreen`, list, chế độ tối | 5 min · 12 frames | bodySmall |
| `LessonsScreen`, list, chế độ tối | Basics · 2 | labelSmall |
| `LessonsScreen`, list, chế độ tối | Squash and stretch | titleSmall |
| `LessonsScreen`, list, chế độ tối | 6 min · 16 frames | bodySmall |
| `LessonsScreen`, list, chế độ tối | Motion | titleLarge |
| `LessonsScreen`, list, chế độ tối | Motion · 1 | labelSmall |
| `LessonsScreen`, list, chế độ tối | Stick figure walk | titleSmall |
| `LessonsScreen`, list, chế độ tối | 8 min · 24 frames | bodySmall |
| `LessonsScreen`, list, chế độ tối | Motion · 2 | labelSmall |
| `LessonsScreen`, list, chế độ tối | Blink and look | titleSmall |
| `LessonsScreen`, list, chế độ tối | 4 min · 10 frames | bodySmall |
| `LessonsScreen`, list, chế độ tối | Projects | labelMedium |
| `LessonsScreen`, list, chế độ tối | Lessons | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Lessons | headlineSmall |
| `LessonsScreen`, loading, chế độ tối | All | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Basics | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Motion | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Characters | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Effects | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Projects | labelMedium |
| `LessonsScreen`, loading, chế độ tối | Lessons | labelMedium |

## 3. Ánh xạ sang hệ thống thiết kế

Vai trò màu theo `tokens.json` (tên vai trò Material 3); giá trị Studio Night nằm trong biến Figma collection "Frameloop" (trang Design system).

| Phần tử trên thiết kế (component Figma) | Thành phần trong code | Màu (vai trò) | Kiểu chữ |
| --- | --- | --- | --- |
| Button | `PrimaryButton` (Style=Primary); `FilledTonalButton`, `OutlinedButton`, `TextButton` của Material 3 (Style=Tonal, Outlined, Text), truyền `colors` rõ | `primary`/`onPrimary`, `surfaceContainerHigh`/`onSurface`, `outline`, chữ `primary` | `labelLarge` |
| Chip | `FilterChip` của Material 3 | Default `surfaceContainerHigh`; Selected `primaryContainer`/`onPrimaryContainer` | `labelMedium` |
| Icon | `Icon(Icons.Rounded.<Tên>)` (material-icons-extended, bộ Rounded); tên ligature trong thiết kế là tên Material Symbols, đổi sang tên `Icons.Rounded` tương ứng | theo phần tử cha | - |
| IconButton | `IconButton` / `FilledIconButton` / `FilledTonalIconButton` của Material 3, 48 dp | `onSurface`; Filled: `primary`/`onPrimary` | - |
| LessonCard | Thành phần của feature `lessons` | `surfaceContainer`, nhãn cấp `primary` | `titleSmall`, `bodySmall`, `labelSmall` |
| NavItem | `NavigationBarItem` trong `NavigationBar` (màn gập: `NavigationRail`) | chỉ báo `primaryContainer`, icon `onPrimaryContainer` | `labelMedium` |
| StatusBar | Thanh trạng thái của hệ thống, không vẽ; màn hình dùng `WindowInsets.statusBars` | - | - |

- **Thành phần mới cần thêm vào hệ thống thiết kế:** Không có thành phần dùng chung mới; `LessonCard` là thành phần của feature.

## 4. Luồng thao tác

Thanh điều hướng > `LessonsScreen` (`list`; `loading` khi đang tải từ R2, `error` khi tải lỗi). Chạm thẻ bài > interstitial `lesson_start_inter` > `EditorScreen` (`lesson-step`). Hoàn thành bài > `LessonDoneScreen` > "Next lesson" (interstitial `lesson_next_inter`) hoặc "Export my animation" > `ExportScreen`.

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
