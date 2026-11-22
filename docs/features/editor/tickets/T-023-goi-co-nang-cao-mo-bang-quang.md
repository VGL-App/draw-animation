---
id: T-023
title: Gói cọ nâng cao mở bằng quảng cáo có thưởng
feature: editor
spec: features/editor/spec.md
scenarios: [KB-3]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: [T-007, T-022]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-023: GÓI CỌ NÂNG CAO MỞ BẰNG QUẢNG CÁO CÓ THƯỞNG

## Mô tả

4 cọ nâng cao có khóa, thẻ mời trong bảng, `editor_rw_brushes` mở 24 giờ; hết hạn lúc đang chọn thì vẽ tiếp tới khi rời màn, lần sau về "Pencil". Sự kiện `brush_pack_unlock_rewarded`.

## Phạm vi

- Trạng thái `brush-locked`; mốc mở 24 giờ lưu trong DataStore riêng của module editor.

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
