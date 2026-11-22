---
id: T-003
title: File sao lưu .frameloop: ghi và đọc một hay nhiều dự án
feature: projects
spec: features/projects/spec.md
scenarios: [KB-8, KB-9, KB-10]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-002]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-003: FILE SAO LƯU .FRAMELOOP: GHI VÀ ĐỌC MỘT HAY NHIỀU DỰ ÁN

## Mô tả

Ghi một hay nhiều dự án thường ra một file `.frameloop` và đọc lại thành dự án mới, dùng cho sao lưu ở menu dự án và ở Cài đặt.

## Phạm vi

- Định dạng có phiên bản; ghi, đọc qua `Uri` của bộ chọn file hệ thống, không cần quyền; không chứa dự án bài học.
- Khôi phục luôn tạo dự án mới, giữ tên trong file (projects câu 10); tiến độ theo tổng số khung; hủy thì không để dự án dở.
- Lỗi tách loại: không phải file sao lưu hay hỏng, hết chỗ, file của bản app mới hơn (projects câu 12).
- Dọn phần dở ở lần mở sau khi app bị đóng giữa lúc khôi phục (projects câu 11). Unit test với file mẫu nhỏ.

**Không bao gồm:**

- Hộp thoại và nút trên màn (T-013, T-017).

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
