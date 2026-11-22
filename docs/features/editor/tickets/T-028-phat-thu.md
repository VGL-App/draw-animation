---
id: T-028
title: Phát thử
feature: editor
spec: features/editor/spec.md
scenarios: [KB-10]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-027]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-028: PHÁT THỬ

## Mô tả

Nút phát chạy hoạt hình lặp theo tốc độ khung, giữ khung, âm thanh; mượt 12 khung/giây với dự án 200 khung × 5 layer (≥ 95% khung đúng nhịp, máy đo chốt ở prototype). Sự kiện `playback_start`, `frame_add` (gom theo phiên).

## Phạm vi

- Trạng thái `playing` theo thiết kế.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Sự kiện phân tích (điểm chặn 4); hiệu năng khó kiểm tự động.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
