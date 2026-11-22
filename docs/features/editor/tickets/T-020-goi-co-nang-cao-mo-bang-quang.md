---
id: T-020
title: Gói cọ nâng cao mở bằng quảng cáo có thưởng
feature: editor
spec: features/editor/spec.md
scenarios: [KB-3]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: [T-006, T-019]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-020: GÓI CỌ NÂNG CAO MỞ BẰNG QUẢNG CÁO CÓ THƯỞNG

## Mô tả

4 cọ "Watercolor", "Glow", "Grain", "Spray" có khóa; chạm hiện thẻ mời trong bảng, xem hết `editor_rw_brushes` thì mở 24 giờ; hết hạn lúc đang chọn thì vẽ tiếp tới khi rời màn, lần sau về "Pencil". Sự kiện `brush_pack_unlock_rewarded`.

## Phạm vi

- Trạng thái `brush-locked` theo thiết kế; lưu thời điểm mở trên máy.

**Không bao gồm:**

- Chính sách khi không có quảng cáo (T-006).

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
