---
id: T-012
title: Danh sách dự án trên điện thoại gập khi mở ra
feature: projects
spec: features/projects/spec.md
scenarios: [KB-12]
owner: Nhật
status: backlog
risk: low
size: S
depends_on: [T-008]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-012: DANH SÁCH DỰ ÁN TRÊN ĐIỆN THOẠI GẬP KHI MỞ RA

## Mô tả

Bố cục khung 673 × 841: lưới ba cột, thanh điều hướng thành thanh bên, theo ảnh `ProjectsScreen_list_*_unfolded`; trạng thái trống, menu, hộp thoại giữ bố cục điện thoại căn giữa.

## Phạm vi

- Screenshot test cùng tên ảnh `_unfolded`; ô quảng cáo vẫn sau mỗi 6 dự án.

**Không bao gồm:**

- Màn vẽ trên máy gập (T-036).

## Rủi ro sơ bộ

Không chạm điểm chặn.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
