---
feature: editor
status: approved                # draft | approved
spec: features/editor/spec.md
tool: figma
link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O
designer: AI (Claude), anh Nhật duyệt
approved_by: Nhật
approved_at: 2026-11-20
---

# THIẾT KẾ: MÀN VẼ

<!--
Quy trình: docs/ops/design-workflow.md. Quyết định: ADR-0008, ADR-0020, ADR-0021.
screens/ và context/ do scripts/figma-export.py sinh, không sửa tay. Thiết kế đổi thì chạy lại:
  scripts/figma-export.py "<link section editor trên Figma>" editor
Màu và chữ trong context/ phần lớn ghi "ngoài hệ thống" vì :core:designsystem chưa đổi sang
thương hiệu Studio Night; plan đổi thương hiệu (docs/ops/new-app.md, mục 3) đưa các giá trị đó vào tokens.json.
Phiên tự chạy chỉ đọc thư mục này, không mở được Figma.
-->

## 1. Các màn hình và trạng thái

Kịch bản: theo spec của feature (spec chưa có; bản đề xuất mục 5, 6, 7).

| Màn hình (composable) | Trạng thái | Ảnh trong `screens/`, số liệu trong `context/` |
| --- | --- | --- |
| `EditorScreen` | brush-locked, chế độ tối | `EditorScreen_brush-locked_dark` |
| `EditorScreen` | brush-locked | `EditorScreen_brush-locked_light` |
| `EditorScreen` | drawing-no-ad, chế độ tối | `EditorScreen_drawing-no-ad_dark` |
| `EditorScreen` | drawing-no-ad | `EditorScreen_drawing-no-ad_light` |
| `EditorScreen` | drawing, chế độ tối | `EditorScreen_drawing_dark` |
| `EditorScreen` | drawing, chế độ tối, điện thoại gập mở ra | `EditorScreen_drawing_dark_unfolded` |
| `EditorScreen` | drawing | `EditorScreen_drawing_light` |
| `EditorScreen` | drawing, điện thoại gập mở ra | `EditorScreen_drawing_light_unfolded` |
| `EditorScreen` | layer-locked, chế độ tối | `EditorScreen_layer-locked_dark` |
| `EditorScreen` | layer-locked | `EditorScreen_layer-locked_light` |
| `EditorScreen` | layers, chế độ tối | `EditorScreen_layers_dark` |
| `EditorScreen` | layers | `EditorScreen_layers_light` |
| `EditorScreen` | lesson-step, chế độ tối | `EditorScreen_lesson-step_dark` |
| `EditorScreen` | lesson-step | `EditorScreen_lesson-step_light` |
| `EditorScreen` | onion-settings, chế độ tối | `EditorScreen_onion-settings_dark` |
| `EditorScreen` | onion-settings | `EditorScreen_onion-settings_light` |
| `EditorScreen` | playing, chế độ tối | `EditorScreen_playing_dark` |
| `EditorScreen` | playing | `EditorScreen_playing_light` |
| `EditorScreen` | sound-locked, chế độ tối | `EditorScreen_sound-locked_dark` |
| `EditorScreen` | sound-locked | `EditorScreen_sound-locked_light` |
| `EditorScreen` | sound, chế độ tối | `EditorScreen_sound_dark` |
| `EditorScreen` | sound | `EditorScreen_sound_light` |
| `EditorScreen` | timeline-multiselect, chế độ tối | `EditorScreen_timeline-multiselect_dark` |
| `EditorScreen` | timeline-multiselect | `EditorScreen_timeline-multiselect_light` |
| `EditorScreen` | tool-brush, chế độ tối | `EditorScreen_tool-brush_dark` |
| `EditorScreen` | tool-brush | `EditorScreen_tool-brush_light` |
| `EditorScreen` | tool-fill, chế độ tối | `EditorScreen_tool-fill_dark` |
| `EditorScreen` | tool-fill | `EditorScreen_tool-fill_light` |
| `EditorScreen` | tool-select, chế độ tối | `EditorScreen_tool-select_dark` |
| `EditorScreen` | tool-select | `EditorScreen_tool-select_light` |
| `EditorScreen` | tool-text, chế độ tối | `EditorScreen_tool-text_dark` |
| `EditorScreen` | tool-text | `EditorScreen_tool-text_light` |
| `EditorScreen` | tour, chế độ tối | `EditorScreen_tour_dark` |
| `EditorScreen` | tour | `EditorScreen_tour_light` |

## 2. Bảng chữ

Chữ tiếng Anh như trên thiết kế (bản đề xuất Q9: tiếng Anh). Lấy từ frame chế độ tối; frame sáng cùng chữ. Chữ trong ô quảng cáo là chữ giữ chỗ, không vào `strings.xml`.

| Màn hình, trạng thái | Chữ | Kiểu chữ (text style trên Figma) |
| --- | --- | --- |
| `EditorScreen`, brush-locked, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, brush-locked, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, brush-locked, chế độ tối | Export | labelLarge |
| `EditorScreen`, brush-locked, chế độ tối | 8 | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, brush-locked, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, brush-locked, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, brush-locked, chế độ tối | Brush | titleLarge |
| `EditorScreen`, brush-locked, chế độ tối | Pro brushes | titleSmall |
| `EditorScreen`, brush-locked, chế độ tối | Watch one ad to use all 12 pro brushes for 24 hours. | bodySmall |
| `EditorScreen`, brush-locked, chế độ tối | Watch ad | labelLarge |
| `EditorScreen`, brush-locked, chế độ tối | Pencil | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Ink | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Marker | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Airbrush | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Soft | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Pixel | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Calligraphy | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Chalk | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Watercolor | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Glow | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Grain | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Spray | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Size | labelLarge |
| `EditorScreen`, brush-locked, chế độ tối | 8 px | labelLarge |
| `EditorScreen`, brush-locked, chế độ tối | Pressure from stylus | bodyLarge |
| `EditorScreen`, drawing-no-ad, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, drawing-no-ad, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, drawing-no-ad, chế độ tối | Export | labelLarge |
| `EditorScreen`, drawing-no-ad, chế độ tối | 8 | labelSmall |
| `EditorScreen`, drawing-no-ad, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, drawing-no-ad, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, drawing-no-ad, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, drawing, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, drawing, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, drawing, chế độ tối | Export | labelLarge |
| `EditorScreen`, drawing, chế độ tối | 8 | labelSmall |
| `EditorScreen`, drawing, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, drawing, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, drawing, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Bouncing ball | titleMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Export | labelLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Layers | titleMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Ball | bodyLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Shadow | bodyLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Background | bodyLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Brush | titleMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Size | labelLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | 8 px | labelLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Opacity | labelLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | 100% | labelLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Onion 2 · 1 | labelMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | 12 / 48 | labelMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | 12 fps | labelMedium |
| `EditorScreen`, layer-locked, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, layer-locked, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, layer-locked, chế độ tối | Export | labelLarge |
| `EditorScreen`, layer-locked, chế độ tối | 8 | labelSmall |
| `EditorScreen`, layer-locked, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, layer-locked, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, layer-locked, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, layer-locked, chế độ tối | Unlock layers 6 to 10 | titleLarge |
| `EditorScreen`, layer-locked, chế độ tối | Watch one short ad to use up to 10 layers in this project. | bodyMedium |
| `EditorScreen`, layer-locked, chế độ tối | Not now | labelLarge |
| `EditorScreen`, layer-locked, chế độ tối | Watch ad | labelLarge |
| `EditorScreen`, layers, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, layers, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, layers, chế độ tối | Export | labelLarge |
| `EditorScreen`, layers, chế độ tối | 8 | labelSmall |
| `EditorScreen`, layers, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, layers, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, layers, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, layers, chế độ tối | Layers | titleLarge |
| `EditorScreen`, layers, chế độ tối | 3 / 5 | labelLarge |
| `EditorScreen`, layers, chế độ tối | Ball | bodyLarge |
| `EditorScreen`, layers, chế độ tối | Shadow | bodyLarge |
| `EditorScreen`, layers, chế độ tối | Sketch | bodyLarge |
| `EditorScreen`, layers, chế độ tối | Background | bodyLarge |
| `EditorScreen`, layers, chế độ tối | 5 layers are free. Layers 6 to 10 unlock for this project with one ad. | bodySmall |
| `EditorScreen`, lesson-step, chế độ tối | Lesson 1 · Bouncing ball | titleMedium |
| `EditorScreen`, lesson-step, chế độ tối | Step 3 of 6 | bodySmall |
| `EditorScreen`, lesson-step, chế độ tối | 8 | labelSmall |
| `EditorScreen`, lesson-step, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, lesson-step, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, lesson-step, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, lesson-step, chế độ tối | Squash on impact | titleMedium |
| `EditorScreen`, lesson-step, chế độ tối | 3 / 6 | labelMedium |
| `EditorScreen`, lesson-step, chế độ tối | Draw the ball a little flatter where it hits the ground. Follow the red guide, then press Next. | bodyMedium |
| `EditorScreen`, lesson-step, chế độ tối | Show me | labelLarge |
| `EditorScreen`, lesson-step, chế độ tối | Next step | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, onion-settings, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, onion-settings, chế độ tối | Export | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | 8 | labelSmall |
| `EditorScreen`, onion-settings, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, onion-settings, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, onion-settings, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, onion-settings, chế độ tối | Onion skin | titleLarge |
| `EditorScreen`, onion-settings, chế độ tối | Show onion skin | bodyLarge |
| `EditorScreen`, onion-settings, chế độ tối | Frames before | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | 2 | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | Frames after | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | 1 | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | Opacity | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | 45% | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | Before | bodyMedium |
| `EditorScreen`, onion-settings, chế độ tối | After | bodyMedium |
| `EditorScreen`, onion-settings, chế độ tối | Loop at the ends | bodyLarge |
| `EditorScreen`, playing, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, playing, chế độ tối | Playing · 12 fps | bodySmall |
| `EditorScreen`, playing, chế độ tối | Export | labelLarge |
| `EditorScreen`, playing, chế độ tối | Loop | labelMedium |
| `EditorScreen`, playing, chế độ tối | 24 / 48 | labelMedium |
| `EditorScreen`, playing, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, sound-locked, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, sound-locked, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | Export | labelLarge |
| `EditorScreen`, sound-locked, chế độ tối | 8 | labelSmall |
| `EditorScreen`, sound-locked, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, sound-locked, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, sound-locked, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, sound-locked, chế độ tối | Sound | titleLarge |
| `EditorScreen`, sound-locked, chế độ tối | Lofi beat.mp3 | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | Music · from 0:00 · 4.0 s | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | Boing | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | Sound effect · frame 12 | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | Effect packs | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | Cartoon | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | 24 sounds · boing, pop, swoosh | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | Ambience | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | 16 sounds · rain, city, forest | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | All effect packs | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | Watch one ad to use every effect pack for 24 hours. Your music and voice stay free. | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | Watch ad | labelLarge |
| `EditorScreen`, sound-locked, chế độ tối | Music | labelLarge |
| `EditorScreen`, sound-locked, chế độ tối | Record | labelLarge |
| `EditorScreen`, sound-locked, chế độ tối | Effects | labelLarge |
| `EditorScreen`, sound, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, sound, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, sound, chế độ tối | Export | labelLarge |
| `EditorScreen`, sound, chế độ tối | 8 | labelSmall |
| `EditorScreen`, sound, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, sound, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, sound, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, sound, chế độ tối | Sound | titleLarge |
| `EditorScreen`, sound, chế độ tối | Lofi beat.mp3 | titleSmall |
| `EditorScreen`, sound, chế độ tối | Music · from 0:00 · 4.0 s | bodySmall |
| `EditorScreen`, sound, chế độ tối | Voice 1 | titleSmall |
| `EditorScreen`, sound, chế độ tối | Recorded · frames 8 to 20 | bodySmall |
| `EditorScreen`, sound, chế độ tối | Boing | titleSmall |
| `EditorScreen`, sound, chế độ tối | Sound effect · frame 12 | bodySmall |
| `EditorScreen`, sound, chế độ tối | Music | labelLarge |
| `EditorScreen`, sound, chế độ tối | Record | labelLarge |
| `EditorScreen`, sound, chế độ tối | Effects | labelLarge |
| `EditorScreen`, timeline-multiselect, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, timeline-multiselect, chế độ tối | Frames 3 to 6 selected | bodySmall |
| `EditorScreen`, timeline-multiselect, chế độ tối | Export | labelLarge |
| `EditorScreen`, timeline-multiselect, chế độ tối | 8 | labelSmall |
| `EditorScreen`, timeline-multiselect, chế độ tối | 4 frames selected | titleSmall |
| `EditorScreen`, tool-brush, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, tool-brush, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tool-brush, chế độ tối | Export | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | 8 | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tool-brush, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, tool-brush, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, tool-brush, chế độ tối | Brush | titleLarge |
| `EditorScreen`, tool-brush, chế độ tối | Pencil | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Ink | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Marker | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Airbrush | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Soft | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Pixel | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Calligraphy | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Chalk | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Size | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | 8 px | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | Opacity | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | 100% | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | Stabilizer | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | 30% | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | Pressure from stylus | bodyLarge |
| `EditorScreen`, tool-fill, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, tool-fill, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tool-fill, chế độ tối | Export | labelLarge |
| `EditorScreen`, tool-fill, chế độ tối | 8 | labelSmall |
| `EditorScreen`, tool-fill, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tool-fill, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, tool-fill, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, tool-fill, chế độ tối | Fill | titleLarge |
| `EditorScreen`, tool-fill, chế độ tối | Close gaps | labelLarge |
| `EditorScreen`, tool-fill, chế độ tối | Medium · 4 px | labelLarge |
| `EditorScreen`, tool-fill, chế độ tối | Small gaps in your lines close automatically, so the color stays inside. | bodySmall |
| `EditorScreen`, tool-fill, chế độ tối | Tolerance | labelLarge |
| `EditorScreen`, tool-fill, chế độ tối | 20% | labelLarge |
| `EditorScreen`, tool-fill, chế độ tối | Fill under lines | bodyLarge |
| `EditorScreen`, tool-fill, chế độ tối | No white edge between the line and the color | bodySmall |
| `EditorScreen`, tool-select, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, tool-select, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tool-select, chế độ tối | Export | labelLarge |
| `EditorScreen`, tool-select, chế độ tối | 8 | labelSmall |
| `EditorScreen`, tool-select, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tool-select, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, tool-select, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, tool-text, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tool-text, chế độ tối | Export | labelLarge |
| `EditorScreen`, tool-text, chế độ tối | BOING! | 44/56, đậm 700 |
| `EditorScreen`, tool-text, chế độ tối | 8 | labelSmall |
| `EditorScreen`, tool-text, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Text | titleLarge |
| `EditorScreen`, tool-text, chế độ tối | Done | labelLarge |
| `EditorScreen`, tool-text, chế độ tối | Text | labelSmall |
| `EditorScreen`, tool-text, chế độ tối | BOING! | bodyLarge |
| `EditorScreen`, tool-text, chế độ tối | Space Grotesk | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Inter | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Marker | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Size | labelLarge |
| `EditorScreen`, tool-text, chế độ tối | 44 pt | labelLarge |
| `EditorScreen`, tour, chế độ tối | Bouncing ball | titleMedium |
| `EditorScreen`, tour, chế độ tối | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tour, chế độ tối | Export | labelLarge |
| `EditorScreen`, tour, chế độ tối | 8 | labelSmall |
| `EditorScreen`, tour, chế độ tối | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tour, chế độ tối | 12 / 48 | labelMedium |
| `EditorScreen`, tour, chế độ tối | 12 fps | labelMedium |
| `EditorScreen`, tour, chế độ tối | 1 / 9 | labelSmall |
| `EditorScreen`, tour, chế độ tối | Draw on the canvas | titleMedium |
| `EditorScreen`, tour, chế độ tối | Pick a brush on the left, then draw with one finger. Two fingers move and zoom. | bodyMedium |
| `EditorScreen`, tour, chế độ tối | Skip tour | labelLarge |
| `EditorScreen`, tour, chế độ tối | Next | labelLarge |

## 3. Ánh xạ sang hệ thống thiết kế

Vai trò màu theo `tokens.json` (tên vai trò Material 3); giá trị Studio Night nằm trong biến Figma collection "Frameloop" (trang Design system).

| Phần tử trên thiết kế (component Figma) | Thành phần trong code | Màu (vai trò) | Kiểu chữ |
| --- | --- | --- | --- |
| Button | `PrimaryButton` (Style=Primary); `FilledTonalButton`, `OutlinedButton`, `TextButton` của Material 3 (Style=Tonal, Outlined, Text), truyền `colors` rõ | `primary`/`onPrimary`, `surfaceContainerHigh`/`onSurface`, `outline`, chữ `primary` | `labelLarge` |
| Chip | `FilterChip` của Material 3 | Default `surfaceContainerHigh`; Selected `primaryContainer`/`onPrimaryContainer` | `labelMedium` |
| CoachMark | Thành phần mới (tour trên màn vẽ, chỉ dẫn bài học) | `inverseSurface`, `inverseOnSurface`, nút `primary` | `titleMedium`, `bodyMedium` |
| ColorSwatch | Thành phần của feature `editor` | màu đang chọn, viền `outlineVariant`, vòng chọn `onSurface` | - |
| Dialog | `AlertDialog` của Material 3 | `surfaceContainerHigh` | `titleLarge`, `bodyMedium` |
| FrameThumb | Thành phần của feature `editor` (khung trong dòng thời gian) | `canvas`; Current viền `primary`; Selected viền `secondary` và dấu chọn | - |
| Icon | `Icon(Icons.Rounded.<Tên>)` (material-icons-extended, bộ Rounded); tên ligature trong thiết kế là tên Material Symbols, đổi sang tên `Icons.Rounded` tương ứng | theo phần tử cha | - |
| IconButton | `IconButton` / `FilledIconButton` / `FilledTonalIconButton` của Material 3, 48 dp | `onSurface`; Filled: `primary`/`onPrimary` | - |
| LayerRow | Thành phần của feature `editor` | Selected `primaryContainer`/`onPrimaryContainer` | `bodyLarge` |
| NativeAdStrip | Định dạng native gọn mới, app tự làm trong `:core:ads` (bản đề xuất mục 9.1, Q17) | do `:core:ads` vẽ | - |
| SheetHandle | `ModalBottomSheet` của Material 3 (tay kéo mặc định) | `surfaceContainerHigh`, tay kéo `outline` | - |
| Slider | `Slider` của Material 3 kèm hàng nhãn và giá trị | `primary`, rãnh `surfaceContainerHighest` | `labelLarge` |
| StatusBar | Thanh trạng thái của hệ thống, không vẽ; màn hình dùng `WindowInsets.statusBars` | - | - |
| Switch | `Switch` của Material 3 | On `primary`/`onPrimary` | - |
| ToolButton | Thành phần mới của feature `editor` (nút 44 dp trong thanh công cụ dọc) | Selected `primary`/`onPrimary` | - |

- **Thành phần mới cần thêm vào hệ thống thiết kế:** `ToolButton`, `CoachMark` (dùng cả cho chỉ dẫn bài học); nếu dùng ở nhiều feature thì đưa vào `:core:designsystem` (plan rủi ro cao).

## 4. Luồng thao tác

`ProjectsScreen` hay `NewProjectScreen` > `EditorScreen`. Lần đầu có tour (`tour`, bỏ qua được). Thanh công cụ trái chọn công cụ; chạm lại công cụ đang chọn mở bảng tùy chọn (bottom sheet: `tool-brush`, `tool-fill`, `tool-text`; `tool-select` là thanh thao tác nổi). Nút layers trên cột phải mở bảng layer; thêm layer thứ 6 mở hộp thoại rewarded (`layer-locked`). Chip onion skin mở `onion-settings`. Nhấn giữ một khung trên dòng thời gian vào chọn nhiều (`timeline-multiselect`). Nút phát > `playing`. Nút nhạc > `sound`; chọn gói hiệu ứng khóa > `sound-locked`. Chọn cọ nâng cao > `brush-locked`. Bài học mở `lesson-step`. "Export" > `ExportScreen`. Back > `ProjectsScreen` (interstitial `editor_exit_inter`).

## 5. Vị trí quảng cáo

`editor_top_native`: dải native gọn khoảng 56 dp ngay dưới thanh tiêu đề, cách hàng nút và canvas mỗi bên ít nhất 16 dp, giữ chỗ khi vị trí bật, lớp mờ của tour không phủ lên; định dạng mới app tự làm (bản đề xuất mục 9.1, Q17). `drawing-no-ad` là bố cục khi vị trí tắt hay chưa có đồng ý. Không quảng cáo nào khác trên canvas, dòng thời gian và các bảng công cụ. Rewarded do người dùng tự chọn: `editor_rw_layer`, `editor_rw_brushes`, `editor_rw_sounds` (thẻ mời nhỏ, chạm mới mở hộp thoại).

## 6. Chuyển sang Compose

- **Phải giống bản mẫu:** bố cục, thứ tự thành phần, chữ (mục 2), vai trò màu và kiểu chữ (mục 3).
- **Được phép khác:** cách vẽ chữ và bóng của hệ thống, hiệu ứng chuyển động, chênh lệch nhỏ theo lưới 4 dp, icon `Icons.Rounded` thay cho phông Material Symbols; nội dung canvas (bóng, nét, onion skin) là ví dụ, không phải giao diện.

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
