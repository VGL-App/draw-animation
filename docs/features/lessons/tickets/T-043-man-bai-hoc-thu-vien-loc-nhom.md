---
id: T-043
title: Màn Bài học: thư viện, lọc nhóm, đang tải, lỗi
feature: lessons
spec: features/lessons/spec.md
scenarios: [KB-1, KB-2, KB-8, KB-9, KB-10]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-008, T-041]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-043: MÀN BÀI HỌC: THƯ VIỆN, LỌC NHÓM, ĐANG TẢI, LỖI

## Mô tả

Tạo `:feature:lessons` với màn Bài học: lọc theo nhóm, thẻ bài (Done, thanh tiến độ, số đếm "<đã xong> of <tổng>"), trạng thái đang tải và lỗi; mục "Lessons" trên thanh điều hướng; `lessons_grid` (sau mỗi 4 thẻ), `lessons_bottom`.

## Phạm vi

- `LessonsScreen` trạng thái `list`, `loading`, `error` theo thiết kế; screenshot test.
- Cờ tắt: ẩn mục "Lessons" và nút "Try a free lesson".

**Không bao gồm:**

- Mở bài (T-044).

## Rủi ro sơ bộ

Quảng cáo (điểm chặn 1).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
