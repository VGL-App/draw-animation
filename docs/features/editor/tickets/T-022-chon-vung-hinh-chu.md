---
id: T-022
title: Chọn vùng, hình, chữ
feature: editor
spec: features/editor/spec.md
scenarios: [KB-5]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-018]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-022: CHỌN VÙNG, HÌNH, CHỮ

## Mô tả

Chọn vùng khoanh tự do với thanh thao tác (di chuyển, đổi cỡ, xoay, lật, chép, xóa); bảng "Shape" (Line, Rectangle, Ellipse, Arrow, Stroke, Fill shape); bảng "Text" (ba phông, Size, Done).

## Phạm vi

- Trạng thái `tool-select`, `tool-shape`, `tool-text` theo thiết kế.

**Không bao gồm:**

- Thước, đối xứng (T-023).

## Rủi ro sơ bộ

Không chạm điểm chặn.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
