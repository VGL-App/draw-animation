---
id: T-017
title: Tự lưu và khôi phục sau khi app tắt đột ngột
feature: editor
spec: features/editor/spec.md
scenarios: [KB-16]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-016]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-017: TỰ LƯU VÀ KHÔI PHỤC SAU KHI APP TẮT ĐỘT NGỘT

## Mô tả

Màn vẽ tự lưu để mất tối đa 30 giây công, lưu xong trước khi rời màn, và khi mở lại sau crash có lại nội dung; dòng "saved" trên thanh tiêu đề theo trạng thái lưu.

## Phạm vi

- Lưu định kỳ và khi app ra nền; lỗi ghi báo trên màn, không mất dữ liệu đang có.
- Unit test với đồng hồ giả.

**Không bao gồm:**

- Định dạng lưu (T-001).

## Rủi ro sơ bộ

Không chạm điểm chặn; khó kiểm chứng phần vòng đời app.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
