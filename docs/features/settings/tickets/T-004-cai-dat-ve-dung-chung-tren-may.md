---
id: T-004
title: Cài đặt vẽ dùng chung trên máy: tốc độ khung, khung hình mặc định, lực bút
feature: settings
spec: features/settings/spec.md
scenarios: [KB-2, KB-3, KB-4]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: []
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-004: CÀI ĐẶT VẼ DÙNG CHUNG TRÊN MÁY: TỐC ĐỘ KHUNG, KHUNG HÌNH MẶC ĐỊNH, LỰC BÚT

## Mô tả

Lưu trên máy các giá trị mặc định mà màn Cài đặt đặt và màn Tạo dự án, màn vẽ đọc: tốc độ khung (mặc định 12), khung hình (mặc định 9:16), lực bút cảm ứng (mặc định bật). Đặt ở module core vì ba feature cùng dùng.

## Phạm vi

- Kho DataStore trong module core (`:core:data` hay `:core:project`, plan chọn), luồng đọc tự thử lại khi `IOException`.
- Giá trị hợp lệ: tốc độ 6, 8, 12, 15, 24, 30; tỷ lệ 16:9, 9:16, 1:1 (settings câu 9).
- Giá trị giữ trên máy cả khi cờ của Cài đặt tắt (settings câu 15).
- Unit test.

**Không bao gồm:**

- Màn Cài đặt (T-014), lực bút riêng từng cọ (T-019).

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo. Thêm khóa DataStore mới (lần đầu, chưa cần chuyển đổi dữ liệu).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
