---
id: T-012
title: Menu dự án: mở, đổi tên, nhân bản, xóa
feature: projects
spec: features/projects/spec.md
scenarios: [KB-4, KB-5, KB-6, KB-7]
owner: Nhật
status: planned
risk: low
size: M
depends_on: [T-009]
sprint:
plan: features/projects/plans/T-012-menu-du-an.md
auto_run:
created: 2026-11-22
---

# T-012: MENU DỰ ÁN: MỞ, ĐỔI TÊN, NHÂN BẢN, XÓA

## Mô tả

Nhấn giữ thẻ mở menu đáy; đổi tên bằng hộp thoại, nhân bản có dấu hiệu đang làm, xóa có hộp thoại xác nhận, không hoàn tác; dự án hỏng chỉ còn "Delete".

## Phạm vi

- Trạng thái `menu` theo thiết kế; hộp thoại chuẩn (projects câu 2).

**Không bao gồm:**

- "Back up to a file" (T-013).

## Rủi ro sơ bộ

Không chạm điểm chặn.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
