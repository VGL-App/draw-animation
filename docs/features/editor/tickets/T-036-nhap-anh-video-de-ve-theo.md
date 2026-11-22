---
id: T-036
title: Nhập ảnh, video để vẽ theo
feature: editor
spec: features/editor/spec.md
scenarios: [KB-12]
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

# T-036: NHẬP ẢNH, VIDEO ĐỂ VẼ THEO

## Mô tả

Photo Picker (không quyền); video chia theo tốc độ khung, tối đa 10 giây; bảng "Reference" (Opacity 40%, Show reference, Remove reference); không xuất ra file.

## Phạm vi

- Trạng thái `reference`.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Đọc video lần đầu (tiêu chí E).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
