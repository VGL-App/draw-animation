---
id: T-024
title: Layer 1 đến 5
feature: editor
spec: features/editor/spec.md
scenarios: [KB-6]
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

# T-024: LAYER 1 ĐẾN 5

## Mô tả

Bảng "Layers": thêm, chọn, đổi thứ tự bằng tay kéo, ẩn hiện, đổi tên; tối đa 5 layer miễn phí.

## Phạm vi

- Trạng thái `layers` theo thiết kế.

**Không bao gồm:**

- Layer 6 đến 10 (T-025).

## Rủi ro sơ bộ

Không chạm điểm chặn.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
