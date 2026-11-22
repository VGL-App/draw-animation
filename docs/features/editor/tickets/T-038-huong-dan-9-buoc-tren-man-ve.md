---
id: T-038
title: Hướng dẫn 9 bước trên màn vẽ
feature: editor
spec: features/editor/spec.md
scenarios: [KB-17]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-026]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-038: HƯỚNG DẪN 9 BƯỚC TRÊN MÀN VẼ

## Mô tả

Hiện ở lần đầu vào dự án thường (không khi vào qua bài học), "Skip tour", "Next"; xem lại từ menu "thêm".

## Phạm vi

- Trạng thái `tour`; "đã xem" lưu trong DataStore riêng của module editor.
- Lớp tối không phủ dải quảng cáo kiểm đầy đủ sau T-040.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Không chạm điểm chặn.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
