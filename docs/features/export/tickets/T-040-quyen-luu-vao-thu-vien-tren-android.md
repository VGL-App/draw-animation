---
id: T-040
title: Quyền lưu vào thư viện trên Android 9 trở xuống
feature: export
spec: features/export/spec.md
scenarios: [KB-8]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: [T-039]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-040: QUYỀN LƯU VÀO THƯ VIỆN TRÊN ANDROID 9 TRỞ XUỐNG

## Mô tả

Khai `WRITE_EXTERNAL_STORAGE` với `maxSdkVersion=28`; hỏi khi bấm xuất trên Android 8, 9; từ chối thì không xuất, báo lý do, từ chối hẳn thì nút "Open settings".

## Phạm vi

- Thông báo "Allow storage access to save your animation to the Gallery." với "Allow".

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Quyền mới trong manifest (điểm chặn 3).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
