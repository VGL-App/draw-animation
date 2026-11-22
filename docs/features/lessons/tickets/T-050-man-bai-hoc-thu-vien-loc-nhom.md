---
id: T-050
title: Màn Bài học: thư viện, lọc nhóm, đang tải, lỗi
feature: lessons
spec: features/lessons/spec.md
scenarios: [KB-1, KB-2, KB-8, KB-10]
owner: Nhật
status: backlog
risk: medium
size: M
depends_on: [T-009, T-048]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-050: MÀN BÀI HỌC: THƯ VIỆN, LỌC NHÓM, ĐANG TẢI, LỖI

## Mô tả

Tạo `:feature:lessons`: lọc theo nhóm, thẻ bài (Done, thanh tiến độ, "<đã xong> of <tổng>"), đang tải, lỗi; dùng thanh điều hướng của vỏ app (T-009).

## Phạm vi

- `LessonsScreen` `list`, `loading`, `error`; screenshot test. Cùng sửa `AppNavHost` của `:app` với các ticket màn hình khác: danh sách chạy xếp tuần tự các ticket này (run-check.py báo cùng sửa file).

**Không bao gồm:**

- Quảng cáo (T-051), mở bài (T-052).

## Rủi ro sơ bộ

Sửa `:app` (điều hướng).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
