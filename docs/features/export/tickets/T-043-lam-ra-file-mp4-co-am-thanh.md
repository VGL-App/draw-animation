---
id: T-043
title: Làm ra file MP4 có âm thanh
feature: export
spec: features/export/spec.md
scenarios: [KB-4]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-004, T-032]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-043: LÀM RA FILE MP4 CÓ ÂM THANH

## Mô tả

Mã hóa MP4 bằng `MediaCodec`, `MediaMuxer` có âm thanh của dự án, kèm watermark; báo tiến độ theo khung, hủy được.

## Phạm vi

- Unit test tiến độ, hủy; test mã hóa nhỏ khi chạy được trên JVM.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Kỹ thuật mới (tiêu chí E); module feature, không SDK mới.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
