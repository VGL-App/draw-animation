---
id: T-001
title: Module :core:project: mô hình dự án và định dạng lưu có phiên bản
feature: projects
spec: features/projects/spec.md
scenarios: [KB-2]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: []
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-001: MODULE :CORE:PROJECT: MÔ HÌNH DỰ ÁN VÀ ĐỊNH DẠNG LƯU CÓ PHIÊN BẢN

## Mô tả

Tạo module `:core:project` với mô hình dự án đầy đủ mà các feature lưu, và định dạng lưu trên máy có số phiên bản ngay từ đầu, để các ticket màn vẽ sau không phải sửa module core.

## Phạm vi

- Mô hình: tên, tỷ lệ, độ phân giải, tốc độ khung, nền (màu hay ảnh "Photo" đã chép vào dự án), khung, layer (tên, thứ tự, ẩn hiện), nội dung mỗi layer ở mỗi khung lưu dạng ảnh điểm (nét, tô, hình, chữ vẽ vào ảnh khi xác nhận; hình, chữ, vùng chọn đang sửa chỉ là trạng thái tạm của màn vẽ), giữ khung 1× đến 4×, âm thanh đã đặt (nhạc, giọng, hiệu ứng), camera ảo theo khung, cài đặt onion skin, lớp vẽ theo, quyền mở layer 6 đến 10, mốc sửa gần nhất.
- Loại dự án "bài học" (mã bài, bước đang làm): không hiện trong danh sách, không tính vào số dự án đã tạo, không vào bản sao lưu (settings câu 6, lessons mục 6).
- Định dạng lưu: mỗi dự án một thư mục, có số phiên bản; đọc lỗi thì trả kết quả "hỏng", không ném lỗi ra ngoài.
- Unit test cho đọc, ghi, phiên bản.

**Không bao gồm:**

- Thao tác repository và bộ đếm (T-002), file `.frameloop` (T-003), vẽ khung ra ảnh (T-004).

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo. Đặt lược đồ dữ liệu trên máy lần đầu; đổi về sau là điểm chặn 9.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
