---
id: T-053
title: Màn Hoàn thành bài học, sang bài tiếp, xuất hoạt hình
feature: lessons
spec: features/lessons/spec.md
scenarios: [KB-5, KB-6, KB-7]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-042, T-052]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-053: MÀN HOÀN THÀNH BÀI HỌC, SANG BÀI TIẾP, XUẤT HOẠT HÌNH

## Mô tả

Màn Hoàn thành: ảnh xem trước, tóm tắt "<khung> frames · <giây> s · Next: <bài>", "Next lesson" (bài cuối không có), "Export my animation" mở màn Xuất; `lesson_done_native`, `lesson_next_inter`, sự kiện `lesson_complete`.

## Phạm vi

- `LessonDoneScreen` `default`, `last-lesson`.
- Nối "Export my animation" tới màn Xuất trong `:app`; rời màn Xuất về màn Hoàn thành. Cùng sửa `AppNavHost` của `:app` với các ticket màn hình khác: danh sách chạy xếp tuần tự các ticket này (run-check.py báo cùng sửa file).

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
