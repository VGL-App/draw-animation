---
id: T-030
title: Âm thanh: nhạc, ghi âm, hiệu ứng cơ bản
feature: editor
spec: features/editor/spec.md
scenarios: [KB-13]
owner: Nhật
status: backlog
risk: high
size: L
depends_on: [T-023]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-030: ÂM THANH: NHẠC, GHI ÂM, HIỆU ỨNG CƠ BẢN

## Mô tả

Bảng "Sound": thêm nhạc từ bộ chọn file, ghi âm (quyền `RECORD_AUDIO` hỏi khi bấm "Record"), hiệu ứng cơ bản tải từ R2 (`sounds/basic/v1/`); đặt từ khung hiện tại.

## Phạm vi

- Trạng thái `sound` theo thiết kế.
- Từ chối hẳn quyền thì hiện lời giải thích và nút mở cài đặt.
- Đăng file R2 vào `docs/ops/required-assets.txt` khi có.

**Không bao gồm:**

- Gói hiệu ứng thêm (T-031).

## Rủi ro sơ bộ

Quyền mới và đường dẫn R2 (điểm chặn 3, 8).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
