---
id: T-039
title: Chỉ dẫn bài học trên màn vẽ
feature: editor
spec: features/editor/spec.md
scenarios: [KB-18]
owner: Nhật
status: backlog
risk: medium
size: M
depends_on: [T-019, T-048]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-039: CHỈ DẪN BÀI HỌC TRÊN MÀN VẼ

## Mô tả

Màn vẽ ở chế độ bài học: thẻ bước, nét dẫn, "Show me", "Next step"; ghi bước đang làm vào kho tiến độ bài ở core (T-048); đóng giữa chừng về màn Bài học.

## Phạm vi

- Trạng thái `lesson-step`.

**Không bao gồm:**

- Màn Hoàn thành (T-052).

## Rủi ro sơ bộ

Ghép với lessons qua dữ liệu core và điều hướng (tiêu chí D).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
