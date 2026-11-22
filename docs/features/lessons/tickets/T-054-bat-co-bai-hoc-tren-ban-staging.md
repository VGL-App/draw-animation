---
id: T-054
title: Bật cờ bài học trên bản staging
feature: lessons
spec: features/lessons/spec.md
scenarios: [KB-10]
owner: Nhật
status: backlog
risk: low
size: S
depends_on: [T-049, T-053]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-054: BẬT CỜ BÀI HỌC TRÊN BẢN STAGING

## Mô tả

Thêm `feature_lessons_enabled` vào `StagingFeatureFlagsModule` khi đã có 2 bài mẫu trên R2 staging (lessons mục 8).

## Phạm vi


**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Không chạm điểm chặn; chỉ sửa cấu hình staging của `:app`.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
