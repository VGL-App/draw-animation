---
id: T-047
title: Quyền lưu vào thư viện trên Android 9 trở xuống
feature: export
spec: features/export/spec.md
scenarios: [KB-8]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: [T-046]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-047: QUYỀN LƯU VÀO THƯ VIỆN TRÊN ANDROID 9 TRỞ XUỐNG

## Mô tả

Khai `WRITE_EXTERNAL_STORAGE` `maxSdkVersion=28`; hỏi khi bấm xuất; từ chối thì báo "Allow storage access to save your animation to the Gallery." với "Allow", từ chối hẳn thì "Open settings".

## Phạm vi


**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Quyền mới (điểm chặn 3).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
