---
id: T-024
title: Tô màu không tràn
feature: editor
spec: features/editor/spec.md
scenarios: [KB-4]
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

# T-024: TÔ MÀU KHÔNG TRÀN

## Mô tả

Bảng "Fill" (Close gaps: Off, Small · 2 px, Medium · 4 px mặc định, Large · 8 px; Tolerance; Fill under lines); không tràn qua khe 1, 2, 4 px trên bộ ảnh thử.

## Phạm vi

- Trạng thái `tool-fill`; bộ ảnh thử trong test.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Thuật toán mới (tiêu chí E).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
