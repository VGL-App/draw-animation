---
id: T-052
title: Mở bài học, làm dở và quay lại
feature: lessons
spec: features/lessons/spec.md
scenarios: [KB-3, KB-4]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-007, T-039, T-050]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-052: MỞ BÀI HỌC, LÀM DỞ VÀ QUAY LẠI

## Mô tả

Chạm thẻ: "Opening lesson…", tải gói bài, mở màn vẽ ở chế độ bài học; lỗi có "Try again"; bài dở mở lại đúng bước; bài đã xong làm lại từ bước 1. `lesson_start_inter`, sự kiện `lesson_start`.

## Phạm vi

- Trạng thái `opening`, `open-failed`.

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
