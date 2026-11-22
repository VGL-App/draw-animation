---
id: T-032
title: Âm thanh: nhạc và ghi âm
feature: editor
spec: features/editor/spec.md
scenarios: [KB-13]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-026]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-032: ÂM THANH: NHẠC VÀ GHI ÂM

## Mô tả

Bảng "Sound": nhạc từ bộ chọn file, ghi âm (quyền `RECORD_AUDIO` hỏi khi bấm "Record"; từ chối hẳn thì lời giải thích và nút mở cài đặt), đặt từ khung hiện tại.

## Phạm vi

- Trạng thái `sound` (phần nhạc, giọng).

**Không bao gồm:**

- Hiệu ứng (T-033).

## Rủi ro sơ bộ

Quyền mới (điểm chặn 3).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
