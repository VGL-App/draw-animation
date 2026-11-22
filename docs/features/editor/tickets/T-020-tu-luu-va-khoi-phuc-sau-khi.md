---
id: T-020
title: Tự lưu và khôi phục sau khi app tắt đột ngột
feature: editor
spec: features/editor/spec.md
scenarios: [KB-16]
owner: Nhật
status: backlog
risk: medium
size: M
depends_on: [T-019]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-020: TỰ LƯU VÀ KHÔI PHỤC SAU KHI APP TẮT ĐỘT NGỘT

## Mô tả

Tự lưu để mất tối đa 30 giây công, lưu xong trước khi rời màn, mở lại sau crash có lại nội dung; dòng "saved" theo trạng thái lưu.

## Phạm vi

- Lưu định kỳ và khi app ra nền; lỗi ghi báo trên màn. Unit test với đồng hồ giả.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Khó kiểm chứng vòng đời app (tiêu chí C).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
