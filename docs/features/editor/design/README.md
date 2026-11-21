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
  scripts/figma-export.py "https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/Frameloop?node-id=26-4514" editor
Màu và chữ trong context/ phần lớn ghi "ngoài hệ thống" vì :core:designsystem chưa đổi sang
thương hiệu Studio Night; plan đổi thương hiệu (docs/ops/new-app.md, mục 3) đưa các giá trị đó vào tokens.json.
Phiên tự chạy chỉ đọc thư mục này, không mở được Figma.
-->

## 1. Các màn hình và trạng thái

Kịch bản: theo [spec của feature](../spec.md).

Bổ sung ngày 2026-11-21 theo câu trả lời ở mục 9 của spec, chờ anh Nhật duyệt: `more-menu` (menu của nút "thêm": hút màu, thước, đối xứng, nhập ảnh hay video, âm thanh, xem lại hướng dẫn), `tool-eraser`, `tool-shape`, `color-picker`, `camera`, `frame-rate`, `hold-frame`, `reference` (lớp vẽ theo sau khi nhập), `ruler`, `symmetry`, `timeline-collapsed` (dòng thời gian 56 dp). Thu gọn: vuốt xuống trên dòng thời gian; mở lại bằng nút mũi tên ở cuối hàng điều khiển. Ở khung 411 × 891, canvas 9:16 bị giới hạn bởi bề ngang giữa thanh công cụ và cột tùy chọn, nên khi thu gọn chỉ lớn từ 288 × 512 lên 296 × 526 dp.

| Màn hình (composable) | Trạng thái | Ảnh trong `screens/`, số liệu trong `context/` | Kịch bản |
| --- | --- | --- | --- |
| `EditorScreen` | brush-locked, chế độ tối | `EditorScreen_brush-locked_dark` | KB-3 |
| `EditorScreen` | brush-locked | `EditorScreen_brush-locked_light` | KB-3 |
| `EditorScreen` | camera, chế độ tối | `EditorScreen_camera_dark` | KB-15 |
| `EditorScreen` | camera | `EditorScreen_camera_light` | KB-15 |
| `EditorScreen` | color-picker, chế độ tối | `EditorScreen_color-picker_dark` | KB-2 |
| `EditorScreen` | color-picker | `EditorScreen_color-picker_light` | KB-2 |
| `EditorScreen` | drawing-no-ad, chế độ tối | `EditorScreen_drawing-no-ad_dark` | KB-1, KB-19 |
| `EditorScreen` | drawing-no-ad | `EditorScreen_drawing-no-ad_light` | KB-1, KB-19 |
| `EditorScreen` | drawing, chế độ tối | `EditorScreen_drawing_dark` | KB-1, KB-2, KB-6, KB-9, KB-11, KB-12, KB-15, KB-16, KB-19, KB-20 |
| `EditorScreen` | drawing, chế độ tối, điện thoại gập mở ra | `EditorScreen_drawing_dark_unfolded` | KB-21 |
| `EditorScreen` | drawing | `EditorScreen_drawing_light` | KB-1, KB-2, KB-6, KB-9, KB-11, KB-12, KB-15, KB-16, KB-19, KB-20 |
| `EditorScreen` | drawing, điện thoại gập mở ra | `EditorScreen_drawing_light_unfolded` | KB-21 |
| `EditorScreen` | frame-rate, chế độ tối | `EditorScreen_frame-rate_dark` | KB-9 |
| `EditorScreen` | frame-rate | `EditorScreen_frame-rate_light` | KB-9 |
| `EditorScreen` | hold-frame, chế độ tối | `EditorScreen_hold-frame_dark` | KB-9 |
| `EditorScreen` | hold-frame | `EditorScreen_hold-frame_light` | KB-9 |
| `EditorScreen` | layer-locked, chế độ tối | `EditorScreen_layer-locked_dark` | KB-7 |
| `EditorScreen` | layer-locked | `EditorScreen_layer-locked_light` | KB-7 |
| `EditorScreen` | layers, chế độ tối | `EditorScreen_layers_dark` | KB-6 |
| `EditorScreen` | layers | `EditorScreen_layers_light` | KB-6 |
| `EditorScreen` | lesson-step, chế độ tối | `EditorScreen_lesson-step_dark` | KB-18 |
| `EditorScreen` | lesson-step | `EditorScreen_lesson-step_light` | KB-18 |
| `EditorScreen` | more-menu, chế độ tối | `EditorScreen_more-menu_dark` | KB-2, KB-5, KB-12, KB-13, KB-17 |
| `EditorScreen` | more-menu | `EditorScreen_more-menu_light` | KB-2, KB-5, KB-12, KB-13, KB-17 |
| `EditorScreen` | onion-settings, chế độ tối | `EditorScreen_onion-settings_dark` | KB-8 |
| `EditorScreen` | onion-settings | `EditorScreen_onion-settings_light` | KB-8 |
| `EditorScreen` | playing, chế độ tối | `EditorScreen_playing_dark` | KB-10 |
| `EditorScreen` | playing | `EditorScreen_playing_light` | KB-10 |
| `EditorScreen` | reference, chế độ tối | `EditorScreen_reference_dark` | KB-12 |
| `EditorScreen` | reference | `EditorScreen_reference_light` | KB-12 |
| `EditorScreen` | ruler, chế độ tối | `EditorScreen_ruler_dark` | KB-5 |
| `EditorScreen` | ruler | `EditorScreen_ruler_light` | KB-5 |
| `EditorScreen` | sound-locked, chế độ tối | `EditorScreen_sound-locked_dark` | KB-14 |
| `EditorScreen` | sound-locked | `EditorScreen_sound-locked_light` | KB-14 |
| `EditorScreen` | sound, chế độ tối | `EditorScreen_sound_dark` | KB-13 |
| `EditorScreen` | sound | `EditorScreen_sound_light` | KB-13 |
| `EditorScreen` | symmetry, chế độ tối | `EditorScreen_symmetry_dark` | KB-5 |
| `EditorScreen` | symmetry | `EditorScreen_symmetry_light` | KB-5 |
| `EditorScreen` | timeline-collapsed, chế độ tối | `EditorScreen_timeline-collapsed_dark` | KB-9 |
| `EditorScreen` | timeline-collapsed | `EditorScreen_timeline-collapsed_light` | KB-9 |
| `EditorScreen` | timeline-multiselect, chế độ tối | `EditorScreen_timeline-multiselect_dark` | KB-9 |
| `EditorScreen` | timeline-multiselect | `EditorScreen_timeline-multiselect_light` | KB-9 |
| `EditorScreen` | tool-brush, chế độ tối | `EditorScreen_tool-brush_dark` | KB-2 |
| `EditorScreen` | tool-brush | `EditorScreen_tool-brush_light` | KB-2 |
| `EditorScreen` | tool-eraser, chế độ tối | `EditorScreen_tool-eraser_dark` | KB-2 |
| `EditorScreen` | tool-eraser | `EditorScreen_tool-eraser_light` | KB-2 |
| `EditorScreen` | tool-fill, chế độ tối | `EditorScreen_tool-fill_dark` | KB-4 |
| `EditorScreen` | tool-fill | `EditorScreen_tool-fill_light` | KB-4 |
| `EditorScreen` | tool-select, chế độ tối | `EditorScreen_tool-select_dark` | KB-5 |
| `EditorScreen` | tool-select | `EditorScreen_tool-select_light` | KB-5 |
| `EditorScreen` | tool-shape, chế độ tối | `EditorScreen_tool-shape_dark` | KB-5 |
| `EditorScreen` | tool-shape | `EditorScreen_tool-shape_light` | KB-5 |
| `EditorScreen` | tool-text, chế độ tối | `EditorScreen_tool-text_dark` | KB-5 |
| `EditorScreen` | tool-text | `EditorScreen_tool-text_light` | KB-5 |
| `EditorScreen` | tour, chế độ tối | `EditorScreen_tour_dark` | KB-17 |
| `EditorScreen` | tour | `EditorScreen_tour_light` | KB-17 |

## 2. Bảng chữ

Chữ tiếng Anh như trên thiết kế (bản đề xuất Q9: tiếng Anh). Lấy từ frame chế độ tối; frame sáng cùng chữ. Chữ trong ô quảng cáo là chữ giữ chỗ, không vào `strings.xml`.

| Màn hình, trạng thái | Vị trí (phần tử cha trong `context/`) | Chữ | Kiểu chữ (text style trên Figma) |
| --- | --- | --- | --- |
| `EditorScreen`, brush-locked, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, brush-locked, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, brush-locked, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, brush-locked, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, brush-locked, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, brush-locked, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, brush-locked, chế độ tối | Brush sheet | Brush | titleLarge |
| `EditorScreen`, brush-locked, chế độ tối | RewardedInvite | Pro brushes | titleSmall |
| `EditorScreen`, brush-locked, chế độ tối | RewardedInvite | Watch one ad to unlock the 4 pro brushes for 24 hours. | bodySmall |
| `EditorScreen`, brush-locked, chế độ tối | Button | Watch ad | labelLarge |
| `EditorScreen`, brush-locked, chế độ tối | Pencil | Pencil | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Ink | Ink | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Marker | Marker | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Airbrush | Airbrush | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Soft | Soft | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Pixel | Pixel | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Calligraphy | Calligraphy | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Chalk | Chalk | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Watercolor (locked) | Watercolor | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Glow (locked) | Glow | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Grain (locked) | Grain | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Spray (locked) | Spray | labelSmall |
| `EditorScreen`, brush-locked, chế độ tối | Slider | Size | labelLarge |
| `EditorScreen`, brush-locked, chế độ tối | Slider | 8 px | labelLarge |
| `EditorScreen`, brush-locked, chế độ tối | Pressure from stylus | Pressure from stylus | bodyLarge |
| `EditorScreen`, camera, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, camera, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, camera, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, camera, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, camera, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, camera, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, camera, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, camera, chế độ tối | Camera sheet | Camera | titleLarge |
| `EditorScreen`, camera, chế độ tối | Body | Frame 12 · Drag on the canvas to pan. Pinch to zoom. | bodySmall |
| `EditorScreen`, camera, chế độ tối | Zoom | Zoom | labelLarge |
| `EditorScreen`, camera, chế độ tối | Zoom | 120% | labelLarge |
| `EditorScreen`, camera, chế độ tối | Rotate | Rotate | labelLarge |
| `EditorScreen`, camera, chế độ tối | Rotate | 0° | labelLarge |
| `EditorScreen`, camera, chế độ tối | Camera on | Camera on | bodyLarge |
| `EditorScreen`, camera, chế độ tối | Camera on | The scene moves as the camera moves between frames. | bodySmall |
| `EditorScreen`, camera, chế độ tối | Reset camera | Reset camera | labelLarge |
| `EditorScreen`, color-picker, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, color-picker, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, color-picker, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, color-picker, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, color-picker, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, color-picker, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, color-picker, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, color-picker, chế độ tối | Color sheet | Color | titleLarge |
| `EditorScreen`, color-picker, chế độ tối | Opacity | Opacity | labelLarge |
| `EditorScreen`, color-picker, chế độ tối | Opacity | 100% | labelLarge |
| `EditorScreen`, color-picker, chế độ tối | Body | Tip: use the eyedropper in the More menu to pick a color from the canvas. | bodySmall |
| `EditorScreen`, drawing-no-ad, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, drawing-no-ad, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, drawing-no-ad, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, drawing-no-ad, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, drawing-no-ad, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, drawing-no-ad, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, drawing-no-ad, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, drawing, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, drawing, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, drawing, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, drawing, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, drawing, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, drawing, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, drawing, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Title | Bouncing ball | titleMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Button | Export | labelLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Layers header | Layers | titleMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | LayerRow | Ball | bodyLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | LayerRow | Shadow | bodyLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | LayerRow | Background | bodyLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | SidePanel | Brush | titleMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Slider | Size | labelLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Slider | 8 px | labelLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Slider | Opacity | labelLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Slider | 100% | labelLarge |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, drawing, chế độ tối, điện thoại gập mở ra | Chip | 12 fps | labelMedium |
| `EditorScreen`, frame-rate, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, frame-rate, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, frame-rate, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, frame-rate, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, frame-rate, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, frame-rate, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, frame-rate, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, frame-rate, chế độ tối | Frame rate sheet | Frame rate | titleLarge |
| `EditorScreen`, frame-rate, chế độ tối | 6 | 6 | labelMedium |
| `EditorScreen`, frame-rate, chế độ tối | 8 | 8 | labelMedium |
| `EditorScreen`, frame-rate, chế độ tối | 12 | 12 | labelMedium |
| `EditorScreen`, frame-rate, chế độ tối | 15 | 15 | labelMedium |
| `EditorScreen`, frame-rate, chế độ tối | 24 | 24 | labelMedium |
| `EditorScreen`, frame-rate, chế độ tối | 30 | 30 | labelMedium |
| `EditorScreen`, frame-rate, chế độ tối | Body | 12 frames per second · 48 frames play in 4.0 s | bodySmall |
| `EditorScreen`, hold-frame, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, hold-frame, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, hold-frame, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, hold-frame, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, hold-frame, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, hold-frame, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, hold-frame, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, hold-frame, chế độ tối | Hold frame sheet | Hold frame | titleLarge |
| `EditorScreen`, hold-frame, chế độ tối | Body | Frame 12 stays on screen for | bodySmall |
| `EditorScreen`, hold-frame, chế độ tối | 1× | 1× | labelMedium |
| `EditorScreen`, hold-frame, chế độ tối | 2× | 2× | labelMedium |
| `EditorScreen`, hold-frame, chế độ tối | 3× | 3× | labelMedium |
| `EditorScreen`, hold-frame, chế độ tối | 4× | 4× | labelMedium |
| `EditorScreen`, hold-frame, chế độ tối | Body | A held frame plays longer without drawing it again. | bodySmall |
| `EditorScreen`, layer-locked, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, layer-locked, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, layer-locked, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, layer-locked, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, layer-locked, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, layer-locked, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, layer-locked, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, layer-locked, chế độ tối | Dialog | Unlock layers 6 to 10 | titleLarge |
| `EditorScreen`, layer-locked, chế độ tối | Dialog | Watch one short ad to use up to 10 layers in this project. | bodyMedium |
| `EditorScreen`, layer-locked, chế độ tối | Button | Not now | labelLarge |
| `EditorScreen`, layer-locked, chế độ tối | Button | Watch ad | labelLarge |
| `EditorScreen`, layers, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, layers, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, layers, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, layers, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, layers, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, layers, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, layers, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, layers, chế độ tối | Layers sheet | Layers | titleLarge |
| `EditorScreen`, layers, chế độ tối | Layers sheet | 3 / 5 | labelLarge |
| `EditorScreen`, layers, chế độ tối | LayerRow | Ball | bodyLarge |
| `EditorScreen`, layers, chế độ tối | LayerRow | Shadow | bodyLarge |
| `EditorScreen`, layers, chế độ tối | LayerRow | Sketch | bodyLarge |
| `EditorScreen`, layers, chế độ tối | LayerRow | Background | bodyLarge |
| `EditorScreen`, layers, chế độ tối | Body | 5 layers are free. Layers 6 to 10 unlock for this project with one ad. | bodySmall |
| `EditorScreen`, lesson-step, chế độ tối | Title | Lesson 1 · Bouncing ball | titleMedium |
| `EditorScreen`, lesson-step, chế độ tối | Title | Step 3 of 6 | bodySmall |
| `EditorScreen`, lesson-step, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, lesson-step, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, lesson-step, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, lesson-step, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, lesson-step, chế độ tối | LessonStep | Squash on impact | titleMedium |
| `EditorScreen`, lesson-step, chế độ tối | LessonStep | 3 / 6 | labelMedium |
| `EditorScreen`, lesson-step, chế độ tối | LessonStep | Draw the ball a little flatter where it hits the ground. Follow the red guide, then press Next. | bodyMedium |
| `EditorScreen`, lesson-step, chế độ tối | Button | Show me | labelLarge |
| `EditorScreen`, lesson-step, chế độ tối | Button | Next step | labelLarge |
| `EditorScreen`, more-menu, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, more-menu, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, more-menu, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, more-menu, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, more-menu, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, more-menu, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, more-menu, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, more-menu, chế độ tối | Eyedropper | Eyedropper | titleSmall |
| `EditorScreen`, more-menu, chế độ tối | Eyedropper | Pick a color from the canvas | bodySmall |
| `EditorScreen`, more-menu, chế độ tối | Ruler | Ruler | titleSmall |
| `EditorScreen`, more-menu, chế độ tối | Ruler | Lines follow a straight edge | bodySmall |
| `EditorScreen`, more-menu, chế độ tối | Symmetry | Symmetry | titleSmall |
| `EditorScreen`, more-menu, chế độ tối | Symmetry | Mirror strokes left to right | bodySmall |
| `EditorScreen`, more-menu, chế độ tối | Import photo or video | Import photo or video | titleSmall |
| `EditorScreen`, more-menu, chế độ tối | Import photo or video | Trace over it, not exported | bodySmall |
| `EditorScreen`, more-menu, chế độ tối | Sound | Sound | titleSmall |
| `EditorScreen`, more-menu, chế độ tối | Sound | Music, voice and effects | bodySmall |
| `EditorScreen`, more-menu, chế độ tối | Replay tour | Replay tour | titleSmall |
| `EditorScreen`, more-menu, chế độ tối | Replay tour | Show the drawing tips again | bodySmall |
| `EditorScreen`, onion-settings, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, onion-settings, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, onion-settings, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, onion-settings, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, onion-settings, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, onion-settings, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, onion-settings, chế độ tối | Onion skin sheet | Onion skin | titleLarge |
| `EditorScreen`, onion-settings, chế độ tối | Show onion skin | Show onion skin | bodyLarge |
| `EditorScreen`, onion-settings, chế độ tối | Slider | Frames before | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | Slider | 2 | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | Slider | Frames after | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | Slider | 1 | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | Slider | Opacity | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | Slider | 45% | labelLarge |
| `EditorScreen`, onion-settings, chế độ tối | Colors | Before | bodyMedium |
| `EditorScreen`, onion-settings, chế độ tối | Colors | After | bodyMedium |
| `EditorScreen`, onion-settings, chế độ tối | Loop at the ends | Loop at the ends | bodyLarge |
| `EditorScreen`, playing, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, playing, chế độ tối | Title | Playing · 12 fps | bodySmall |
| `EditorScreen`, playing, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, playing, chế độ tối | Chip | Loop | labelMedium |
| `EditorScreen`, playing, chế độ tối | Controls | 24 / 48 | labelMedium |
| `EditorScreen`, playing, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, reference, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, reference, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, reference, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, reference, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, reference, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, reference, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, reference, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, reference, chế độ tối | Reference sheet | Reference | titleLarge |
| `EditorScreen`, reference, chế độ tối | Opacity | Opacity | labelLarge |
| `EditorScreen`, reference, chế độ tối | Opacity | 40% | labelLarge |
| `EditorScreen`, reference, chế độ tối | Show reference | Show reference | bodyLarge |
| `EditorScreen`, reference, chế độ tối | Show reference | Trace over a photo or video. It is not exported. | bodySmall |
| `EditorScreen`, reference, chế độ tối | Remove reference | Remove reference | labelLarge |
| `EditorScreen`, ruler, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, ruler, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, ruler, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, ruler, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, ruler, chế độ tối | Ruler on · tap to turn off | Ruler on · tap to turn off | labelMedium |
| `EditorScreen`, ruler, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, ruler, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, ruler, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, sound-locked, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, sound-locked, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, sound-locked, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, sound-locked, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, sound-locked, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, sound-locked, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, sound-locked, chế độ tối | Sound sheet | Sound | titleLarge |
| `EditorScreen`, sound-locked, chế độ tối | Lofi beat.mp3 | Lofi beat.mp3 | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | Lofi beat.mp3 | Music · from 0:00 · 4.0 s | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | Boing | Boing | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | Boing | Sound effect · frame 12 | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | Effect packs | Effect packs | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | Cartoon pack (locked) | Cartoon | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | Cartoon pack (locked) | 24 sounds · boing, pop, swoosh | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | Ambience pack (locked) | Ambience | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | Ambience pack (locked) | 16 sounds · rain, city, forest | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | RewardedInvite | All effect packs | titleSmall |
| `EditorScreen`, sound-locked, chế độ tối | RewardedInvite | Watch one ad to use every effect pack for 24 hours. Your music and voice stay free. | bodySmall |
| `EditorScreen`, sound-locked, chế độ tối | Button | Watch ad | labelLarge |
| `EditorScreen`, sound-locked, chế độ tối | Button | Music | labelLarge |
| `EditorScreen`, sound-locked, chế độ tối | Button | Record | labelLarge |
| `EditorScreen`, sound-locked, chế độ tối | Button | Effects | labelLarge |
| `EditorScreen`, sound, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, sound, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, sound, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, sound, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, sound, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, sound, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, sound, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, sound, chế độ tối | Sound sheet | Sound | titleLarge |
| `EditorScreen`, sound, chế độ tối | Lofi beat.mp3 | Lofi beat.mp3 | titleSmall |
| `EditorScreen`, sound, chế độ tối | Lofi beat.mp3 | Music · from 0:00 · 4.0 s | bodySmall |
| `EditorScreen`, sound, chế độ tối | Voice 1 | Voice 1 | titleSmall |
| `EditorScreen`, sound, chế độ tối | Voice 1 | Recorded · frames 8 to 20 | bodySmall |
| `EditorScreen`, sound, chế độ tối | Boing | Boing | titleSmall |
| `EditorScreen`, sound, chế độ tối | Boing | Sound effect · frame 12 | bodySmall |
| `EditorScreen`, sound, chế độ tối | Button | Music | labelLarge |
| `EditorScreen`, sound, chế độ tối | Button | Record | labelLarge |
| `EditorScreen`, sound, chế độ tối | Button | Effects | labelLarge |
| `EditorScreen`, symmetry, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, symmetry, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, symmetry, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, symmetry, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, symmetry, chế độ tối | Symmetry on · tap to turn off | Symmetry on · tap to turn off | labelMedium |
| `EditorScreen`, symmetry, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, symmetry, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, symmetry, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, timeline-collapsed, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, timeline-collapsed, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, timeline-collapsed, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, timeline-collapsed, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, timeline-collapsed, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, timeline-collapsed, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, timeline-collapsed, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, timeline-multiselect, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, timeline-multiselect, chế độ tối | Title | Frames 3 to 6 selected | bodySmall |
| `EditorScreen`, timeline-multiselect, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, timeline-multiselect, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, timeline-multiselect, chế độ tối | SelectionBar | 4 frames selected | titleSmall |
| `EditorScreen`, tool-brush, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, tool-brush, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tool-brush, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tool-brush, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, tool-brush, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, tool-brush, chế độ tối | Brush sheet | Brush | titleLarge |
| `EditorScreen`, tool-brush, chế độ tối | Pencil | Pencil | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Ink | Ink | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Marker | Marker | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Airbrush | Airbrush | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Soft | Soft | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Pixel | Pixel | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Calligraphy | Calligraphy | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Chalk | Chalk | labelSmall |
| `EditorScreen`, tool-brush, chế độ tối | Slider | Size | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | Slider | 8 px | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | Slider | Opacity | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | Slider | 100% | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | Slider | Stabilizer | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | Slider | 30% | labelLarge |
| `EditorScreen`, tool-brush, chế độ tối | Pressure from stylus | Pressure from stylus | bodyLarge |
| `EditorScreen`, tool-eraser, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, tool-eraser, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tool-eraser, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, tool-eraser, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, tool-eraser, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tool-eraser, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, tool-eraser, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, tool-eraser, chế độ tối | Eraser sheet | Eraser | titleLarge |
| `EditorScreen`, tool-eraser, chế độ tối | Size | Size | labelLarge |
| `EditorScreen`, tool-eraser, chế độ tối | Size | 24 px | labelLarge |
| `EditorScreen`, tool-eraser, chế độ tối | Softness | Softness | labelLarge |
| `EditorScreen`, tool-eraser, chế độ tối | Softness | 0% | labelLarge |
| `EditorScreen`, tool-eraser, chế độ tối | Erase on all layers | Erase on all layers | bodyLarge |
| `EditorScreen`, tool-eraser, chế độ tối | Erase on all layers | Off erases only the selected layer. | bodySmall |
| `EditorScreen`, tool-fill, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, tool-fill, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tool-fill, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, tool-fill, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, tool-fill, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tool-fill, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, tool-fill, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, tool-fill, chế độ tối | Fill sheet | Fill | titleLarge |
| `EditorScreen`, tool-fill, chế độ tối | Slider | Close gaps | labelLarge |
| `EditorScreen`, tool-fill, chế độ tối | Slider | Medium · 4 px | labelLarge |
| `EditorScreen`, tool-fill, chế độ tối | Body | Small gaps in your lines close automatically, so the color stays inside. | bodySmall |
| `EditorScreen`, tool-fill, chế độ tối | Slider | Tolerance | labelLarge |
| `EditorScreen`, tool-fill, chế độ tối | Slider | 20% | labelLarge |
| `EditorScreen`, tool-fill, chế độ tối | Fill under lines | Fill under lines | bodyLarge |
| `EditorScreen`, tool-fill, chế độ tối | Fill under lines | No white edge between the line and the color | bodySmall |
| `EditorScreen`, tool-select, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, tool-select, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tool-select, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, tool-select, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, tool-select, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tool-select, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, tool-select, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, tool-shape, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, tool-shape, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tool-shape, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, tool-shape, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, tool-shape, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tool-shape, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, tool-shape, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, tool-shape, chế độ tối | Shape sheet | Shape | titleLarge |
| `EditorScreen`, tool-shape, chế độ tối | Line | Line | labelMedium |
| `EditorScreen`, tool-shape, chế độ tối | Rectangle | Rectangle | labelMedium |
| `EditorScreen`, tool-shape, chế độ tối | Ellipse | Ellipse | labelMedium |
| `EditorScreen`, tool-shape, chế độ tối | Arrow | Arrow | labelMedium |
| `EditorScreen`, tool-shape, chế độ tối | Stroke | Stroke | labelLarge |
| `EditorScreen`, tool-shape, chế độ tối | Stroke | 6 px | labelLarge |
| `EditorScreen`, tool-shape, chế độ tối | Fill shape | Fill shape | bodyLarge |
| `EditorScreen`, tool-shape, chế độ tối | Fill shape | Use the selected color inside the shape. | bodySmall |
| `EditorScreen`, tool-text, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, tool-text, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tool-text, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, tool-text, chế độ tối | Canvas 9:16 | BOING! | 44/56, đậm 700 |
| `EditorScreen`, tool-text, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, tool-text, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Text sheet | Text | titleLarge |
| `EditorScreen`, tool-text, chế độ tối | Button | Done | labelLarge |
| `EditorScreen`, tool-text, chế độ tối | TextField | Text | labelSmall |
| `EditorScreen`, tool-text, chế độ tối | TextField | BOING! | bodyLarge |
| `EditorScreen`, tool-text, chế độ tối | Chip | Space Grotesk | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Chip | Inter | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Chip | Marker | labelMedium |
| `EditorScreen`, tool-text, chế độ tối | Slider | Size | labelLarge |
| `EditorScreen`, tool-text, chế độ tối | Slider | 44 pt | labelLarge |
| `EditorScreen`, tour, chế độ tối | Title | Bouncing ball | titleMedium |
| `EditorScreen`, tour, chế độ tối | Title | Frame 12 / 48 · saved | bodySmall |
| `EditorScreen`, tour, chế độ tối | Button | Export | labelLarge |
| `EditorScreen`, tour, chế độ tối | ToolOptions | 8 | labelSmall |
| `EditorScreen`, tour, chế độ tối | Chip | Onion 2 · 1 | labelMedium |
| `EditorScreen`, tour, chế độ tối | Controls | 12 / 48 | labelMedium |
| `EditorScreen`, tour, chế độ tối | Chip | 12 fps | labelMedium |
| `EditorScreen`, tour, chế độ tối | CoachMark | 1 / 9 | labelSmall |
| `EditorScreen`, tour, chế độ tối | CoachMark | Draw on the canvas | titleMedium |
| `EditorScreen`, tour, chế độ tối | CoachMark | Pick a brush on the left, then draw with one finger. Two fingers move and zoom. | bodyMedium |
| `EditorScreen`, tour, chế độ tối | Skip button | Skip tour | labelLarge |
| `EditorScreen`, tour, chế độ tối | Next button | Next | labelLarge |

## 3. Ánh xạ sang hệ thống thiết kế

Vai trò màu theo `tokens.json` (tên vai trò Material 3); giá trị Studio Night nằm trong biến Figma collection "Frameloop" (trang Design system).

| Phần tử trên thiết kế (component Figma) | Thành phần trong code | Màu (vai trò) | Kiểu chữ | Khoảng cách |
| --- | --- | --- | --- | --- |
| Button | `PrimaryButton` (Style=Primary); `FilledTonalButton`, `OutlinedButton`, `TextButton` của Material 3 (Style=Tonal, Outlined, Text), truyền `colors` rõ | `primary`/`onPrimary`, `surfaceContainerHigh`/`onSurface`, `outline`, chữ `primary` | `labelLarge` | theo `context/` |
| Chip | `FilterChip` của Material 3 | Default `surfaceContainerHigh`; Selected `primaryContainer`/`onPrimaryContainer` | `labelMedium` | theo `context/` |
| CoachMark | Thành phần mới (tour trên màn vẽ, chỉ dẫn bài học) | `inverseSurface`, `inverseOnSurface`, nút `primary` | `titleMedium`, `bodyMedium` | theo `context/` |
| ColorSwatch | Thành phần riêng của mỗi feature dùng nó (`editor`, `projects`); không dùng chéo giữa feature (ADR-0001); dùng ở nhiều feature thì đưa vào `:core:designsystem` bằng plan rủi ro cao | màu đang chọn, viền `outlineVariant`, vòng chọn `onSurface` | - | theo `context/` |
| Dialog | `AlertDialog` của Material 3 | `surfaceContainerHigh` | `titleLarge`, `bodyMedium` | theo `context/` |
| FrameThumb | Thành phần của feature `editor` (khung trong dòng thời gian) | `canvas`; Current viền `primary`; Selected viền `secondary` và dấu chọn | - | theo `context/` |
| Icon | `Icon(Icons.Rounded.<Tên>)` (material-icons-extended, bộ Rounded); tên ligature trong thiết kế là tên Material Symbols, đổi sang tên `Icons.Rounded` tương ứng | theo phần tử cha | - | theo `context/` |
| IconButton | `IconButton` / `FilledIconButton` / `FilledTonalIconButton` của Material 3, 48 dp | `onSurface`; Filled: `primary`/`onPrimary` | - | theo `context/` |
| LayerRow | Thành phần của feature `editor` | Selected `primaryContainer`/`onPrimaryContainer` | `bodyLarge` | theo `context/` |
| NativeAdStrip | Định dạng native gọn mới, app tự làm trong `:core:ads` (bản đề xuất mục 9.1, Q17) | do `:core:ads` vẽ | - | theo `context/` |
| SheetHandle | `ModalBottomSheet` của Material 3 (tay kéo mặc định) | `surfaceContainerHigh`, tay kéo `outline` | - | theo `context/` |
| Slider | `Slider` của Material 3 kèm hàng nhãn và giá trị | `primary`, rãnh `surfaceContainerHighest` | `labelLarge` | theo `context/` |
| StatusBar | Thanh trạng thái của hệ thống, không vẽ; màn hình dùng `WindowInsets.statusBars` | - | - | theo `context/` |
| Switch | `Switch` của Material 3 | On `primary`/`onPrimary` | - | theo `context/` |
| ToolButton | Thành phần mới của feature `editor` (nút 44 dp trong thanh công cụ dọc) | Selected `primary`/`onPrimary` | - | theo `context/` |

- **Thành phần mới cần thêm vào hệ thống thiết kế:** `ToolButton`, `CoachMark` (dùng cả cho chỉ dẫn bài học); nếu dùng ở nhiều feature thì đưa vào `:core:designsystem` (plan rủi ro cao).

## 4. Luồng thao tác

`ProjectsScreen` hay `NewProjectScreen` > `EditorScreen`. Lần đầu có tour (`tour`, bỏ qua được). Thanh công cụ trái chọn công cụ; chạm lại công cụ đang chọn mở bảng tùy chọn (bottom sheet: `tool-brush`, `tool-fill`, `tool-text`; `tool-select` là thanh thao tác nổi). Biểu tượng layers trên cột phải (vùng chạm 48 dp) mở bảng layer; thêm layer thứ 6 mở hộp thoại rewarded (`layer-locked`). Chip onion skin mở `onion-settings`. Nhấn giữ một khung trên dòng thời gian vào chọn nhiều (`timeline-multiselect`). Nút phát > `playing`. Nút nhạc > `sound`; chọn gói hiệu ứng khóa > `sound-locked`. Chọn cọ nâng cao > `brush-locked`. Bài học mở `lesson-step`. "Export" > `ExportScreen`. Back > `ProjectsScreen` (interstitial `editor_exit_inter`). Bổ sung 2026-11-21: chạm lại tẩy > `tool-eraser`, hình > `tool-shape`, camera ảo > `camera`; ô màu ở cột phải > `color-picker`; nút "thêm" (ba chấm) > `more-menu`, từ đó bật thước (`ruler`), đối xứng (`symmetry`), nhập ảnh hay video (sau khi chọn: `reference`), mở `sound`, xem lại `tour`; chip tốc độ khung > `frame-rate`; nút giữ khung trên thanh chọn nhiều > `hold-frame`; vuốt xuống trên dòng thời gian > `timeline-collapsed`, nút mũi tên ở cuối hàng điều khiển mở lại.

## 5. Vị trí quảng cáo

`editor_top_native`: dải native gọn khoảng 56 dp ngay dưới thanh tiêu đề, cách hàng nút và canvas mỗi bên ít nhất 16 dp, giữ chỗ khi vị trí bật, lớp mờ của tour không phủ lên; định dạng mới app tự làm (bản đề xuất mục 9.1, Q17). `drawing-no-ad` là bố cục khi vị trí tắt hay chưa có đồng ý. Không quảng cáo nào khác trên canvas, dòng thời gian và các bảng công cụ. Rewarded do người dùng tự chọn: `editor_rw_layer`, `editor_rw_brushes`, `editor_rw_sounds`: thẻ mời trong bảng công cụ có nút "Watch ad"; bấm nút mới mở hộp thoại rewarded của `:core:ads`, không tự mở.

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
