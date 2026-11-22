---
id: T-030
title: Dòng thời gian: khung, chọn nhiều, chép và dán dải
feature: editor
spec: features/editor/spec.md
scenarios: [KB-9]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-021]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-030: DÒNG THỜI GIAN: KHUNG, CHỌN NHIỀU, CHÉP VÀ DÁN DẢI

## Mô tả

Thêm, xóa, nhân bản khung; nhấn giữ chọn nhiều, kéo để chọn dải; chép, dán dải; nhân bản 10 khung trong tối đa 3 thao tác.

## Phạm vi

- Trạng thái `timeline-multiselect`.

**Không bao gồm:**

- Giữ khung, tốc độ khung (T-031).

## Rủi ro sơ bộ

Không chạm điểm chặn.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
