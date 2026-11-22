---
id: T-009
title: Màn Tạo dự án
feature: projects
spec: features/projects/spec.md
scenarios: [KB-3]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-004, T-008]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-009: MÀN TẠO DỰ ÁN

## Mô tả

Màn Tạo dự án: tên (mặc định "My animation <số>", tối đa 40 ký tự), khung hình 9:16, 16:9, 1:1, độ phân giải 720p, 1080p, tốc độ khung, nền (màu hay "Photo" chọn bằng Photo Picker), nút "Create" tạo dự án rồi mở màn vẽ. Có `new_project_native`, `create_inter` và sự kiện `project_create`.

## Phạm vi

- `NewProjectScreen` theo thiết kế (đã bỏ 4:5); giá trị chọn sẵn lấy từ cài đặt dùng chung (T-004).
- Nền "Photo": Photo Picker, không xin quyền; chép ảnh vào dự án.
- `create_inter` từ dự án thứ 2 (đếm ở T-001), theo `ads_interval` và luật trước nét vẽ đầu tiên.
- Sự kiện `project_create` với tham số ở mục 6 của spec.

**Không bao gồm:**

- Mở màn vẽ khi cờ màn vẽ tắt: thông báo "Coming soon", không tạo dự án (theo spec, làm ở ticket này).

## Rủi ro sơ bộ

Quảng cáo và sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
