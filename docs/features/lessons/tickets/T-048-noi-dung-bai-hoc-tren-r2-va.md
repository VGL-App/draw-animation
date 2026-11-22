---
id: T-048
title: Nội dung bài học trên R2 và kho tiến độ bài ở core
feature: lessons
spec: features/lessons/spec.md
scenarios: [KB-2, KB-3, KB-4]
owner: Nhật
status: approved
risk: high
size: M
depends_on: [T-001]
sprint:
plan: features/lessons/plans/T-048-core-lessons.md
auto_run:
created: 2026-11-22
---

# T-048: NỘI DUNG BÀI HỌC TRÊN R2 VÀ KHO TIẾN ĐỘ BÀI Ở CORE

## Mô tả

Định dạng file danh sách và gói bài trong `lessons/<phiên bản>/` (thư mục hiện hành theo `lessons_content_version`), tải qua `RemoteAssets`, giữ trên máy; kho tiến độ bài (bước đang làm, đã xong, dự án bài học của T-001) ở module core để màn vẽ ghi và màn Bài học đọc.

## Phạm vi

- Mô hình nội dung bài ở core (plan chọn module); bài dở giữ bản nội dung cũ tới khi xong.
- Khai báo khóa `lessons_content_version` trong module core chứa nội dung bài, thêm module vào `AppRemoteKeys` (chuyển từ T-006, theo lời anh Nhật trong phiên 2026-11-22 (câu hỏi của plan T-006); bỏ T-006 khỏi phụ thuộc).
- Đăng file vào `docs/ops/required-assets.txt`. Unit test với file mẫu.

**Không bao gồm:**

- Làm nội dung (T-049).

## Rủi ro sơ bộ

Đường dẫn R2, module core (điểm chặn 7, 8).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
