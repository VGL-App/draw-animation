---
id: T-034
title: Chỉ dẫn bài học trên màn vẽ
feature: editor
spec: features/editor/spec.md
scenarios: [KB-18]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-016, T-041]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-034: CHỈ DẪN BÀI HỌC TRÊN MÀN VẼ

## Mô tả

Khi mở một bài học, màn vẽ hiện thẻ bước, nét dẫn, "Show me" (phát đoạn mẫu), "Next step"; đóng giữa chừng về màn Bài học, giữ bước; bước cuối báo cho lessons.

## Phạm vi

- Trạng thái `lesson-step` theo thiết kế; nội dung bài theo định dạng của T-041.

**Không bao gồm:**

- Màn Hoàn thành (T-045).

## Rủi ro sơ bộ

Ghép với feature lessons qua điều hướng và dữ liệu dùng chung.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
