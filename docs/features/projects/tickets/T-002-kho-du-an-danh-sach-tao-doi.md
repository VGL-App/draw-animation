---
id: T-002
title: Kho dự án: danh sách, tạo, đổi tên, nhân bản, xóa, đếm dự án
feature: projects
spec: features/projects/spec.md
scenarios: [KB-2, KB-5, KB-6, KB-7]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-001]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-002: KHO DỰ ÁN: DANH SÁCH, TẠO, ĐỔI TÊN, NHÂN BẢN, XÓA, ĐẾM DỰ ÁN

## Mô tả

Repository trong `:core:project` cho mọi feature: luồng danh sách dự án thường (sắp theo lần sửa gần nhất), tạo, lưu, đổi tên, nhân bản, xóa, và số dự án đã từng tạo.

## Phạm vi

- Nhân bản đặt tên "<tên> copy", "<tên> copy 2", cắt tên gốc khi vượt 40 ký tự; hết chỗ không để bản dở.
- Dự án hỏng vẫn có trong danh sách với cờ hỏng, không tự xóa (projects câu 9).
- Số dự án đã từng tạo bằng màn Tạo dự án: không giảm khi xóa, không tính bài học, khôi phục, nhân bản (projects câu 4, 16).
- Lỗi ghi `IOException` thành kết quả lỗi; hết chỗ là loại lỗi riêng. Unit test.

**Không bao gồm:**

- Giao diện (T-009 trở đi).

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
