---
id: T-007
title: Vị trí quảng cáo của Frameloop
feature: editor
spec: features/editor/spec.md
scenarios: [KB-3, KB-7, KB-14]
owner: Nhật
status: approved
risk: high
size: M
depends_on: [T-006]
sprint:
plan: features/editor/plans/T-007-vi-tri-quang-cao.md
auto_run:
created: 2026-11-22
---

# T-007: VỊ TRÍ QUẢNG CÁO CỦA FRAMELOOP

## Mô tả

Khai báo trong `:core:ads` 22 vị trí quảng cáo của 6 feature theo bảng 9.1 (khóa `is_show_<mã>`, nhóm A, B), và chính sách rewarded `allow_after_2`. Mỗi vị trí chỉ theo khóa `is_show_<mã>` và các tham số Remote Config; luật "trước nét vẽ đầu tiên" đã bỏ khỏi ticket ngày 2026-11-22, vì anh Nhật bỏ hẳn luật đó (spec editor câu hỏi 24). Một ticket để các plan màn hình không cùng sửa `AdPlacement`.

## Phạm vi

- Vị trí: `native_onboarding`; `projects_empty_native`, `projects_bottom`, `projects_grid`, `restore_mrec`, `create_inter`, `new_project_native`; `settings_bottom`; `editor_exit_inter`, `editor_rw_layer`, `editor_rw_brushes`, `editor_rw_sounds`; `export_options_mrec`, `export_wait_mrec`, `export_done_native`, `export_interstitial`, `export_rw_extra`; `lessons_grid`, `lessons_bottom`, `lesson_start_inter`, `lesson_done_native`, `lesson_next_inter`.
- `allow_after_2` cho `editor_rw_layer`, `editor_rw_brushes`, `editor_rw_sounds`, `export_rw_extra` (editor câu 4).
- Cập nhật `docs/remote-config-keys.md`, test của `:core:ads`.

**Không bao gồm:**

- `editor_top_native` và định dạng 56 dp (T-008).
- Luật giữ quảng cáo trước nét vẽ đầu tiên (bỏ ngày 2026-11-22, spec editor câu hỏi 24).

## Rủi ro sơ bộ

Quảng cáo và module core (điểm chặn 1, 7). Người phụ trách sản phẩm cần biết trước khi duyệt.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
