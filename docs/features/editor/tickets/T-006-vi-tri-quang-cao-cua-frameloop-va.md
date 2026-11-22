---
id: T-006
title: Vị trí quảng cáo của Frameloop và luật trước nét vẽ đầu tiên
feature: editor
spec: features/editor/spec.md
scenarios: [KB-3, KB-7, KB-14, KB-19]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: []
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-006: VỊ TRÍ QUẢNG CÁO CỦA FRAMELOOP VÀ LUẬT TRƯỚC NÉT VẼ ĐẦU TIÊN

## Mô tả

Khai báo trong `:core:ads` mọi vị trí quảng cáo của 6 feature theo bảng 9.1 của bản đề xuất (khóa `is_show_<mã>`, nhóm A, B), luật "trước nét vẽ đầu tiên" (app tự làm, `:core:ads` chưa có) với mốc nét vẽ đầu tiên lưu trên máy, và chính sách rewarded `allow_after_2` cho bốn vị trí rewarded (editor câu 4). Một ticket cho cả app để các plan màn hình sau không cùng sửa `AdPlacement`.

## Phạm vi

- Các vị trí: `native_onboarding`; `projects_empty_native`, `projects_bottom`, `projects_grid`, `restore_mrec`, `create_inter`, `new_project_native`; `settings_bottom`; `editor_top_native`, `editor_exit_inter`, `editor_rw_layer`, `editor_rw_brushes`, `editor_rw_sounds`; `export_options_mrec`, `export_wait_mrec`, `export_done_native`, `export_interstitial`, `export_rw_extra`; `lessons_grid`, `lessons_bottom`, `lesson_start_inter`, `lesson_done_native`, `lesson_next_inter`. Định dạng, màn, giới hạn đúng mục 5 của từng spec.
- Mốc "nét vẽ đầu tiên": API ghi một lần (màn vẽ gọi), đọc dạng luồng; trước mốc chỉ cho `native_onboarding`, `projects_empty_native`, `editor_top_native` (và rewarded người dùng tự chọn).
- `allow_after_2` cho `editor_rw_layer`, `editor_rw_brushes`, `editor_rw_sounds`, `export_rw_extra`.
- Cập nhật `docs/remote-config-keys.md` và kế hoạch quảng cáo nếu có; test của `:core:ads`.

**Không bao gồm:**

- Định dạng dải native gọn 56 dp (T-007); đặt vị trí lên từng màn (ticket của màn).

## Rủi ro sơ bộ

Quảng cáo và module core (điểm chặn 1, 7). Người phụ trách sản phẩm cần biết trước khi duyệt.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
