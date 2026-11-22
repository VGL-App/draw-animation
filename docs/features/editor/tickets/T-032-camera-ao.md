---
id: T-032
title: Camera ảo
feature: editor
spec: features/editor/spec.md
scenarios: [KB-15]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-003, T-027]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-032: CAMERA ẢO

## Mô tả

Công cụ camera ảo: lia, phóng, xoay cả cảnh ở từng khung; bảng "Camera" (Zoom, Rotate, Camera on, Reset camera); phát và xuất theo camera.

## Phạm vi

- Trạng thái `camera` theo thiết kế; dùng phần vẽ khung của T-003.

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
