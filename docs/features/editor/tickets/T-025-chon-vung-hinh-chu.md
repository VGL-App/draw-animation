---
id: T-025
title: Chọn vùng, hình, chữ
feature: editor
spec: features/editor/spec.md
scenarios: [KB-5]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-021]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-025: CHỌN VÙNG, HÌNH, CHỮ

## Mô tả

Chọn vùng với thanh thao tác; bảng "Shape" (Line, Rectangle, Ellipse, Arrow, Stroke, Fill shape); bảng "Text".

## Phạm vi

- Trạng thái `tool-select`, `tool-shape`, `tool-text`.

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
