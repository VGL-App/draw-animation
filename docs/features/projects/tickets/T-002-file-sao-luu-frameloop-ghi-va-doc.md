---
id: T-002
title: File sao lưu .frameloop: ghi và đọc một hay nhiều dự án
feature: projects
spec: features/projects/spec.md
scenarios: [KB-8, KB-9, KB-10]
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

# T-002: FILE SAO LƯU .FRAMELOOP: GHI VÀ ĐỌC MỘT HAY NHIỀU DỰ ÁN

## Mô tả

Thêm vào `:core:project` cách ghi một hay nhiều dự án ra một file `.frameloop` và đọc lại thành dự án mới, dùng cho sao lưu ở menu dự án (projects) và ở Cài đặt (settings). Sau ticket này có API sao lưu, khôi phục có tiến độ và hủy được.

## Phạm vi

- Định dạng file: một hay nhiều dự án, có phiên bản; ghi qua `Uri` của bộ chọn file hệ thống, không cần quyền.
- Khôi phục: luôn tạo dự án mới, giữ tên trong file, cho trùng tên, không ghi đè (projects câu 10); tiến độ theo tổng số khung; hủy giữa chừng thì không để lại dự án dở.
- Lỗi tách loại: không phải file sao lưu hay hỏng, hết chỗ trên máy, file của bản app mới hơn (projects câu 12).
- Dọn phần dở khi app bị hệ thống đóng giữa lúc khôi phục, ở lần mở sau (projects câu 11).
- Unit test cho ghi, đọc, hủy và từng loại lỗi; file mẫu nhỏ trong test.

**Không bao gồm:**

- Hộp thoại và nút trên màn (T-011, T-015).

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
