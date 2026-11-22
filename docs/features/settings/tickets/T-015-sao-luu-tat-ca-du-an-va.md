---
id: T-015
title: Sao lưu tất cả dự án và khôi phục từ Cài đặt
feature: settings
spec: features/settings/spec.md
scenarios: [KB-5, KB-6]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: [T-002, T-011, T-014]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-015: SAO LƯU TẤT CẢ DỰ ÁN VÀ KHÔI PHỤC TỪ CÀI ĐẶT

## Mô tả

Dòng "Back up all projects" ghi mọi dự án ra một file; "Restore from a file" chọn file rồi về danh sách dự án khôi phục với hộp thoại của projects. Trạng thái `backing-up`, `no-projects` và thông báo kết quả.

## Phạm vi

- Trạng thái theo thiết kế bổ sung; sự kiện `backup_export`, `backup_restore` với `source` = `settings`.

**Không bao gồm:**

- Hộp thoại khôi phục (T-011).

## Rủi ro sơ bộ

Sự kiện phân tích (điểm chặn 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
