---
id: T-034
title: Gói hiệu ứng âm thanh mở bằng quảng cáo có thưởng
feature: editor
spec: features/editor/spec.md
scenarios: [KB-14]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: [T-007, T-033]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-034: GÓI HIỆU ỨNG ÂM THANH MỞ BẰNG QUẢNG CÁO CÓ THƯỞNG

## Mô tả

Gói "Cartoon", "Ambience" có khóa; `editor_rw_sounds` mở 24 giờ; hiệu ứng đã đặt vẫn phát và xuất sau khi hết hạn. Sự kiện `sound_pack_unlock_rewarded`.

## Phạm vi

- Trạng thái `sound-locked`; gói tải từ `sounds/<gói>/v1/`.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Quảng cáo, sự kiện, R2 (điểm chặn 1, 4, 8).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
