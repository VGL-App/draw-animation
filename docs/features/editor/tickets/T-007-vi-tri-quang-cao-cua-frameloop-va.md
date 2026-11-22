---
id: T-007
title: Vị trí quảng cáo của Frameloop và luật trước nét vẽ đầu tiên
feature: editor
spec: features/editor/spec.md
scenarios: [KB-3, KB-7, KB-14]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-005, T-006]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-007: VỊ TRÍ QUẢNG CÁO CỦA FRAMELOOP VÀ LUẬT TRƯỚC NÉT VẼ ĐẦU TIÊN

## Mô tả

Khai báo trong `:core:ads` 22 vị trí quảng cáo của 6 feature theo bảng 9.1 (khóa `is_show_<mã>`, nhóm A, B), chính sách rewarded `allow_after_2`, và cách áp luật "trước nét vẽ đầu tiên" (app tự làm) dựa trên mốc ở T-005. Một ticket để các plan màn hình không cùng sửa `AdPlacement`.

## Phạm vi

- Vị trí: `native_onboarding`; `projects_empty_native`, `projects_bottom`, `projects_grid`, `restore_mrec`, `create_inter`, `new_project_native`; `settings_bottom`; `editor_exit_inter`, `editor_rw_layer`, `editor_rw_brushes`, `editor_rw_sounds`; `export_options_mrec`, `export_wait_mrec`, `export_done_native`, `export_interstitial`, `export_rw_extra`; `lessons_grid`, `lessons_bottom`, `lesson_start_inter`, `lesson_done_native`, `lesson_next_inter`.
- Luật trước nét vẽ đầu tiên: chỉ `native_onboarding`, `projects_empty_native`, `editor_top_native` và rewarded người dùng tự chọn. Plan chọn chỗ đặt (giúp đỡ trong `:core:ads` hay kiểm ở vỏ app), ưu tiên chỗ ít làm `:core:ads` lệch dự án mẫu.
- `allow_after_2` cho `editor_rw_layer`, `editor_rw_brushes`, `editor_rw_sounds`, `export_rw_extra` (editor câu 4).
- Cập nhật `docs/remote-config-keys.md`, test của `:core:ads`.

**Không bao gồm:**

- `editor_top_native` và định dạng 56 dp (T-008).

## Rủi ro sơ bộ

Quảng cáo và module core (điểm chặn 1, 7). Người phụ trách sản phẩm cần biết trước khi duyệt.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
