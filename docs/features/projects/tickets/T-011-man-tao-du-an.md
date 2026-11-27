---
id: T-011
title: Màn Tạo dự án
feature: projects
spec: features/projects/spec.md
scenarios: [KB-3]
owner: Nhật
status: on-staging
risk: high
size: M
depends_on: [T-005, T-007, T-009]
sprint:
plan: features/projects/plans/T-011-man-tao-du-an.md
auto_run:
created: 2026-11-22
---

# T-011: MÀN TẠO DỰ ÁN

## Mô tả

Màn Tạo dự án: tên (mặc định "My animation <số>", tối đa 40 ký tự), khung hình, độ phân giải, tốc độ khung, nền (màu hay "Photo" qua Photo Picker), "Create" tạo dự án rồi mở màn vẽ. Có `new_project_native`, `create_inter`, sự kiện `project_create`.

## Phạm vi

- `NewProjectScreen` theo thiết kế (đã bỏ 4:5). Giá trị chọn sẵn lấy từ T-005; khi cờ Cài đặt tắt thì dùng 12 fps, 9:16 (settings câu 15).
- Ô chọn nền dùng ba màu cố định `#FFFFFF`, `#22262E`, `#2F6FE0` của `ProjectBackgroundColors` (T-004), giống nhau ở chế độ sáng và tối, dù ảnh thiết kế `NewProjectScreen` tô theo theme; người sửa Figma cho khớp (lời anh Nhật trong phiên 2026-11-22, câu hỏi 1 của plan T-004).
- Ảnh nền "Photo" chép vào dự án đã xoay theo EXIF và thu nhỏ tới khoảng 3840 điểm ảnh cạnh dài (hợp đồng với T-004).
- `create_inter` từ dự án thứ 2 theo `ads_interval`. Bỏ luật trước nét vẽ đầu tiên ngày 2026-11-22, vì anh Nhật bỏ hẳn luật đó (spec projects câu hỏi 24).
- Cờ màn vẽ tắt: "Coming soon", không tạo dự án. Mở màn vẽ kiểm đầy đủ sau T-018. Cùng sửa `AppNavHost` của `:app` với các ticket màn hình khác: danh sách chạy xếp tuần tự các ticket này (run-check.py báo cùng sửa file).

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
