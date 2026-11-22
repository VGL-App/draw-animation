---
id: T-044
title: Làm ra file GIF, chuỗi PNG, GIF trong suốt và watermark
feature: export
spec: features/export/spec.md
scenarios: [KB-4]
owner: Nhật
status: approved
risk: medium
size: M
depends_on: [T-004]
sprint:
plan: features/export/plans/T-044-xuat-gif-png-watermark.md
auto_run:
created: 2026-11-22
---

# T-044: LÀM RA FILE GIF, CHUỖI PNG, GIF TRONG SUỐT VÀ WATERMARK

## Mô tả

Tự viết bộ mã hóa GIF (không thêm thư viện, tránh sửa `gradle/libs.versions.toml`), chuỗi PNG trong suốt, GIF trong suốt; vẽ watermark viên thuốc nền đen 35%, cao khoảng 5% cạnh ngắn, góc dưới phải.

## Phạm vi

- Unit test cho bộ mã hóa và watermark.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Thuật toán mới (tiêu chí E). Nếu plan cần thư viện thì thành điểm chặn 2.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
