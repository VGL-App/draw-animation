---
id: T-031
title: Giữ khung và tốc độ khung
feature: editor
spec: features/editor/spec.md
scenarios: [KB-9]
owner: Nhật
status: backlog
risk: low
size: S
depends_on: [T-030]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-031: GIỮ KHUNG VÀ TỐC ĐỘ KHUNG

## Mô tả

Bảng "Hold frame" 1× đến 4× cho các khung đang chọn; bảng "Frame rate" 6, 8, 12, 15, 24, 30 với dòng thời lượng.

## Phạm vi

- Trạng thái `hold-frame`, `frame-rate`.

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
