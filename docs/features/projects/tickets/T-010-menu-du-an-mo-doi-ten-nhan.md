---
id: T-010
title: Menu dự án: mở, đổi tên, nhân bản, xóa
feature: projects
spec: features/projects/spec.md
scenarios: [KB-4, KB-5, KB-6, KB-7]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-008]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-010: MENU DỰ ÁN: MỞ, ĐỔI TÊN, NHÂN BẢN, XÓA

## Mô tả

Nhấn giữ thẻ dự án mở menu đáy ("Open", "Rename", "Duplicate", "Back up to a file", "Delete"); đổi tên bằng hộp thoại, nhân bản có dấu hiệu đang làm, xóa có hộp thoại xác nhận, không hoàn tác.

## Phạm vi

- Trạng thái `menu` theo thiết kế; hộp thoại và thông báo dùng thành phần chuẩn (projects câu 2).
- Lỗi hết chỗ khi nhân bản báo bằng lời dễ hiểu, không để bản dở.
- Dự án hỏng: menu chỉ còn "Delete".

**Không bao gồm:**

- "Back up to a file" (T-011).

## Rủi ro sơ bộ

Không chạm điểm chặn; dùng repository của T-001.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
