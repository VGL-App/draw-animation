---
id: T-041
title: Nội dung bài học trên R2: định dạng, tải danh sách và gói bài
feature: lessons
spec: features/lessons/spec.md
scenarios: [KB-2, KB-3, KB-4]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-005]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-041: NỘI DUNG BÀI HỌC TRÊN R2: ĐỊNH DẠNG, TẢI DANH SÁCH VÀ GÓI BÀI

## Mô tả

Định dạng file danh sách và gói từng bài (dự án dựng sẵn, các bước, nét dẫn, đoạn mẫu) trong `lessons/<phiên bản>/`, thư mục hiện hành đọc từ `lessons_content_version`; tải qua `RemoteAssets`, giữ trên máy, bài dở giữ bản cũ tới khi xong.

## Phạm vi

- Mô hình nội dung bài dùng chung với màn vẽ (đặt ở core để editor đọc được, plan chọn module).
- Đăng file vào `docs/ops/required-assets.txt`.
- Unit test với file mẫu.

**Không bao gồm:**

- Làm nội dung 18 bài (T-042).

## Rủi ro sơ bộ

Đường dẫn R2 và module core (điểm chặn 7, 8).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
