---
id: T-022
title: Cọ, tẩy, chọn màu, hút màu
feature: editor
spec: features/editor/spec.md
scenarios: [KB-2]
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

# T-022: CỌ, TẨY, CHỌN MÀU, HÚT MÀU

## Mô tả

Bảng "Brush" 8 cọ (Size, Opacity, Stabilizer, Pressure from stylus riêng từng cọ, mặc định theo Cài đặt); bảng "Eraser" (Size, Softness, Erase on all layers); bảng "Color" 12 ô và Opacity; hút màu.

## Phạm vi

- Trạng thái `tool-brush`, `tool-eraser`, `color-picker`; lực bút riêng từng cọ lưu trong dự án hay trên máy (plan chọn).

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
