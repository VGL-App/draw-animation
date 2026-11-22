---
id: T-028
title: Mở layer 6 đến 10 bằng quảng cáo có thưởng
feature: editor
spec: features/editor/spec.md
scenarios: [KB-7]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: [T-007, T-027]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-028: MỞ LAYER 6 ĐẾN 10 BẰNG QUẢNG CÁO CÓ THƯỞNG

## Mô tả

Hộp thoại "Unlock layers 6 to 10"; `editor_rw_layer` mở cho cả dự án. Sự kiện `layer_unlock_rewarded`.

## Phạm vi

- Trạng thái `layer-locked`.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
