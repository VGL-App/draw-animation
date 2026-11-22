---
id: T-023
title: Menu "thêm", thước, vẽ đối xứng
feature: editor
spec: features/editor/spec.md
scenarios: [KB-5, KB-17]
owner: Nhật
status: backlog
risk: low
size: S
depends_on: [T-016]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-023: MENU "THÊM", THƯỚC, VẼ ĐỐI XỨNG

## Mô tả

Nút "thêm" mở menu: hút màu, thước, đối xứng, nhập ảnh hay video, âm thanh, xem lại hướng dẫn. Bật thước thì nét bám đường thẳng; bật đối xứng thì nét lặp qua trục dọc; chip dưới canvas để tắt.

## Phạm vi

- Trạng thái `more-menu`, `ruler`, `symmetry` theo thiết kế.

**Không bao gồm:**

- Các mục mở màn khác (T-019, T-029, T-030, T-033).

## Rủi ro sơ bộ

Không chạm điểm chặn.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
