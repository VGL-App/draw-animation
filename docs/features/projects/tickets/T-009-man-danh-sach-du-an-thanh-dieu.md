---
id: T-009
title: Màn danh sách dự án, thanh điều hướng của vỏ app, màn bắt đầu
feature: projects
spec: features/projects/spec.md
scenarios: [KB-1, KB-2, KB-11, KB-13]
owner: Nhật
status: backlog
risk: medium
size: M
depends_on: [T-002, T-004, T-006]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-009: MÀN DANH SÁCH DỰ ÁN, THANH ĐIỀU HƯỚNG CỦA VỎ APP, MÀN BẮT ĐẦU

## Mô tả

Tạo `:feature:projects` với màn danh sách dự án (trống, có dự án, lưới thẻ có ảnh thu nhỏ) và đặt làm màn bắt đầu; thanh điều hướng Projects/Lessons đặt ở vỏ `:app` để màn Bài học dùng chung.

## Phạm vi

- `ProjectsScreen` trạng thái `empty`, `list` theo thiết kế (chưa có quảng cáo); screenshot test, khung 360 × 640.
- Nút "New animation", "Try a free lesson" (ẩn khi cờ bài học tắt), nút cài đặt (ẩn khi cờ Cài đặt tắt), mở dự án: màn nhận callback, `:app` nối đích.
- Cờ projects tắt thì vào màn mẫu có sẵn từ dự án mẫu.
- Tiêu chí mở màn vẽ, mở Bài học, mở Cài đặt chỉ kiểm đầy đủ sau T-018, T-050, T-016; trước đó "Coming soon". Cùng sửa `AppNavHost` của `:app` với các ticket màn hình khác: danh sách chạy xếp tuần tự các ticket này (run-check.py báo cùng sửa file).

**Không bao gồm:**

- Quảng cáo của màn (T-010), Tạo dự án (T-011), menu (T-012).

## Rủi ro sơ bộ

Sửa `:app` (điều hướng, màn bắt đầu, thanh điều hướng).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
