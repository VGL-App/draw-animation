---
id: T-038
title: Làm ra file MP4, GIF, chuỗi PNG, GIF trong suốt kèm watermark
feature: export
spec: features/export/spec.md
scenarios: [KB-4]
owner: Nhật
status: backlog
risk: high
size: L
depends_on: [T-003]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-038: LÀM RA FILE MP4, GIF, CHUỖI PNG, GIF TRONG SUỐT KÈM WATERMARK

## Mô tả

Phần làm ra file trong `:feature:export`: mã hóa MP4 có âm thanh của dự án, GIF lặp, chuỗi PNG trong suốt, GIF trong suốt; vẽ watermark (viên thuốc nền đen 35%, cao khoảng 5% cạnh ngắn, góc dưới phải); báo tiến độ theo khung, hủy được.

## Phạm vi

- Dùng `MediaCodec`/`MediaMuxer` cho MP4; bộ mã hóa GIF (plan quyết thư viện hay tự viết).
- Unit test cho tiến độ, hủy, watermark; test nhỏ trên JVM cho mã hóa khi chạy được.

**Không bao gồm:**

- Màn đang xuất, lưu vào thư viện (T-039).

## Rủi ro sơ bộ

Có thể cần SDK hay thư viện mới cho GIF (điểm chặn 2); kỹ thuật mới (tiêu chí E).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
