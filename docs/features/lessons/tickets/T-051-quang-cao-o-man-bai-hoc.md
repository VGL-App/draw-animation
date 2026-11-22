---
id: T-051
title: Quảng cáo ở màn Bài học
feature: lessons
spec: features/lessons/spec.md
scenarios: [KB-9]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: [T-007, T-050]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-051: QUẢNG CÁO Ở MÀN BÀI HỌC

## Mô tả

`lessons_grid` (sau mỗi 4 thẻ của danh sách đang hiện, bỏ qua tiêu đề nhóm) và `lessons_bottom`, chỉ ở trạng thái danh sách, theo khóa `is_show_<mã>` của từng vị trí. Bỏ luật trước nét vẽ đầu tiên ngày 2026-11-22, vì anh Nhật bỏ hẳn luật đó (spec lessons câu hỏi 17).

## Phạm vi


**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Quảng cáo (điểm chặn 1).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
