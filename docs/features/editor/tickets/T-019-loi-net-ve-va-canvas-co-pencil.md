---
id: T-019
title: Lõi nét vẽ và canvas: cọ Pencil, di chuyển, thu phóng
feature: editor
spec: features/editor/spec.md
scenarios: [KB-1, KB-2]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-004, T-005, T-018]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-019: LÕI NÉT VẼ VÀ CANVAS: CỌ PENCIL, DI CHUYỂN, THU PHÓNG

## Mô tả

Vẽ nét một ngón bằng cọ Pencil trên khung và layer đang chọn, hai ngón di chuyển, thu phóng, xoay canvas; ghi mốc nét vẽ đầu tiên. Nền tảng nét và hiển thị khung cho các công cụ sau.

## Phạm vi

- Hiển thị khung qua phần vẽ của T-004.
- Unit test mô hình nét, test giao diện cử chỉ.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Kỹ thuật vẽ bằng ngón và bút mới (tiêu chí E).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
