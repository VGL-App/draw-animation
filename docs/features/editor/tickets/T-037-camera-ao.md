---
id: T-037
title: Camera ảo
feature: editor
spec: features/editor/spec.md
scenarios: [KB-15]
owner: Nhật
status: backlog
risk: medium
size: M
depends_on: [T-031]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-037: CAMERA ẢO

## Mô tả

Công cụ camera ảo theo từng khung; bảng "Camera" (Zoom, Rotate, Camera on, Reset camera); phát và xuất theo camera (vẽ qua T-004).

## Phạm vi

- Trạng thái `camera`. Cần sửa phần vẽ khung của `:core:project` thì nâng risk lên cao khi viết plan.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Kỹ thuật mới (tiêu chí E).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
