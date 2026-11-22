---
id: T-041
title: Màn vẽ trên điện thoại gập khi mở ra
feature: editor
spec: features/editor/spec.md
scenarios: [KB-21]
owner: Nhật
status: backlog
risk: low
size: S
depends_on: [T-022, T-027, T-040]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-041: MÀN VẼ TRÊN ĐIỆN THOẠI GẬP KHI MỞ RA

## Mô tả

Bố cục 673 × 841 theo ảnh `EditorScreen_drawing_*_unfolded`: bảng phải luôn hiện "Layers" và tùy chọn cọ, dải quảng cáo rộng hết màn.

## Phạm vi

- Screenshot test cùng tên ảnh `_unfolded`.

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
