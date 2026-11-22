---
id: T-026
title: Onion skin
feature: editor
spec: features/editor/spec.md
scenarios: [KB-8]
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

# T-026: ONION SKIN

## Mô tả

Chip onion skin bật tắt và bảng chỉnh số khung trước, sau, độ mờ; khung trước tô `onionPrevious`, khung sau `onionNext`.

## Phạm vi

- Trạng thái `onion-settings` theo thiết kế.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Không chạm điểm chặn.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
