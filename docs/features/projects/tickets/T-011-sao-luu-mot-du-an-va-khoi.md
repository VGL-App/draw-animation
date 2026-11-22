---
id: T-011
title: Sao lưu một dự án và khôi phục từ file
feature: projects
spec: features/projects/spec.md
scenarios: [KB-8, KB-9, KB-10]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-002, T-010]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-011: SAO LƯU MỘT DỰ ÁN VÀ KHÔI PHỤC TỪ FILE

## Mô tả

Mục "Back up to a file" ghi dự án ra file `.frameloop` qua bộ chọn file hệ thống; nút nhập trên thanh trên khôi phục từ file với hộp thoại có tiến độ, `restore_mrec`, nút "Cancel", và hộp thoại lỗi. Có sự kiện `backup_export`, `backup_restore` (tham số `source`, `project_count`, chỉ khi thành công).

## Phạm vi

- Trạng thái `restoring`, `restore-error` theo thiết kế; nội dung hộp thoại cuộn được ở 360 × 640, nút "Cancel" luôn thấy.
- Thông báo "Backup saved", lỗi "Couldn't save the backup. Try again." có "Retry".
- Lời báo hết chỗ và file của bản app mới hơn (projects câu 12).
- Khôi phục từ Cài đặt quay về danh sách và dùng cùng hộp thoại (đầu vào cho T-015).

**Không bao gồm:**

- Nút sao lưu tất cả ở Cài đặt (T-015).

## Rủi ro sơ bộ

Quảng cáo `restore_mrec` và sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
