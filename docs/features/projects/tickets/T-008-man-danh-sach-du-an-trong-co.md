---
id: T-008
title: Màn danh sách dự án: trống, có dự án, mở dự án, màn bắt đầu
feature: projects
spec: features/projects/spec.md
scenarios: [KB-1, KB-2, KB-11, KB-13]
owner: Nhật
status: backlog
risk: high
size: L
depends_on: [T-001, T-003, T-005, T-006]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-008: MÀN DANH SÁCH DỰ ÁN: TRỐNG, CÓ DỰ ÁN, MỞ DỰ ÁN, MÀN BẮT ĐẦU

## Mô tả

Tạo `:feature:projects` với màn danh sách dự án (trống và có dự án, lưới thẻ có ảnh thu nhỏ), thanh điều hướng Projects/Lessons, nút cài đặt, và đặt màn này làm màn bắt đầu của app thay màn mẫu. Có quảng cáo `projects_empty_native`, `projects_bottom`, `projects_grid`.

## Phạm vi

- Màn `ProjectsScreen` trạng thái `empty`, `list` theo thiết kế; chạm thẻ mở màn vẽ (đích của editor; cờ màn vẽ tắt thì "Coming soon").
- Nút "New animation" mở màn Tạo dự án, nút "Try a free lesson" mở màn Bài học (ẩn khi cờ bài học tắt), nút cài đặt (ẩn khi cờ Cài đặt tắt).
- Đăng ký điều hướng trong `:app`; cờ projects tắt thì vào màn mẫu có sẵn từ dự án mẫu.
- Vị trí quảng cáo theo mục 5 (ô lưới sau mỗi 6 dự án, thanh đáy chỉ ở trạng thái có dự án) và luật trước nét vẽ đầu tiên.
- Screenshot test cùng tên ảnh thiết kế, khung 360 × 640.

**Không bao gồm:**

- Màn Tạo dự án (T-009), menu dự án (T-010), sao lưu, khôi phục (T-011), màn gập (T-012).

## Rủi ro sơ bộ

Quảng cáo (điểm chặn 1); sửa `:app` (điều hướng, màn bắt đầu).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
