---
id: T-013
title: Sao lưu một dự án và khôi phục từ file
feature: projects
spec: features/projects/spec.md
scenarios: [KB-8, KB-9, KB-10]
owner: Nhật
status: planned
risk: high
size: M
depends_on: [T-003, T-007, T-012]
sprint:
plan: features/projects/plans/T-013-sao-luu-khoi-phuc.md
auto_run:
created: 2026-11-22
---

# T-013: SAO LƯU MỘT DỰ ÁN VÀ KHÔI PHỤC TỪ FILE

## Mô tả

"Back up to a file" ghi `.frameloop`; nút nhập khôi phục với hộp thoại tiến độ, `restore_mrec`, "Cancel", hộp thoại lỗi. Sự kiện `backup_export`, `backup_restore` (`source`, `project_count`, chỉ khi thành công).

## Phạm vi

- Trạng thái `restoring`, `restore-error`; nội dung hộp thoại cuộn được ở 360 × 640.
- "Backup saved"; lỗi "Couldn't save the backup. Try again." có "Retry"; lời báo hết chỗ, bản mới hơn (projects câu 12).
- Khôi phục gọi từ Cài đặt dùng cùng hộp thoại (cho T-017).

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
