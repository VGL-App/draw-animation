---
id: T-001
title: Module :core:project: mô hình và kho dự án trên máy
feature: projects
spec: features/projects/spec.md
scenarios: [KB-2, KB-5, KB-6, KB-7]
owner: Nhật
status: backlog
risk: high
size: L
depends_on: []
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-001: MODULE :CORE:PROJECT: MÔ HÌNH VÀ KHO DỰ ÁN TRÊN MÁY

## Mô tả

Tạo module `:core:project` giữ mô hình dự án (khung, layer, nét, nền, âm thanh đã đặt, camera ảo, tốc độ khung, tỷ lệ, độ phân giải, tên) và kho lưu trên máy dùng chung cho projects, editor, export, lessons. Sau ticket này các feature đọc, tạo, lưu, đổi tên, nhân bản, xóa dự án qua một repository, chưa có giao diện.

## Phạm vi

- Module `:core:project` (plugin core như các module core khác), mô hình dự án và định dạng lưu trên máy (mỗi dự án một thư mục, có phiên bản định dạng).
- Repository: danh sách (sắp theo lần sửa gần nhất), tạo, đọc, lưu, đổi tên, nhân bản (tên "<tên> copy", "<tên> copy 2", tối đa 40 ký tự), xóa; luồng danh sách tự cập nhật.
- Dự án không đọc được vẫn có trong danh sách với cờ hỏng, không tự xóa (projects câu 9).
- Số dự án đã từng tạo (không giảm khi xóa; không tính bài học, khôi phục, nhân bản), dùng cho tên mặc định và điều kiện `create_inter` (projects câu 4, 16).
- Ghi lỗi `IOException` thành kết quả lỗi, không làm app dừng; hết chỗ trên máy là một loại lỗi riêng.
- Unit test cho repository và định dạng lưu.

**Không bao gồm:**

- Định dạng file sao lưu `.frameloop` (T-002), vẽ khung ra ảnh và ảnh thu nhỏ (T-003), giao diện (T-008 trở đi).

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo. Đặt lược đồ dữ liệu trên máy lần đầu (điểm chặn 9 nếu sau này đổi): phải có số phiên bản định dạng ngay từ đầu.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
