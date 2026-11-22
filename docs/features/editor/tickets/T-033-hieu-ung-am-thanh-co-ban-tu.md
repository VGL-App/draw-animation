---
id: T-033
title: Hiệu ứng âm thanh cơ bản từ R2
feature: editor
spec: features/editor/spec.md
scenarios: [KB-13]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: [T-032]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-033: HIỆU ỨNG ÂM THANH CƠ BẢN TỪ R2

## Mô tả

Nút "Effects" tải bộ cơ bản (khoảng 10 hiệu ứng, dưới 2 MB) từ `sounds/basic/v1/` qua `RemoteAssets`, giữ trên máy; lỗi có thử lại.

## Phạm vi

- Đăng file vào `docs/ops/required-assets.txt` khi có file. File thật chờ T-055 (người làm); test dùng file mẫu.
- Nếu T-035 (phát thử) xong trước thì kiểm thêm hiệu ứng đã đặt phát đúng khung.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Đường dẫn R2 (điểm chặn 8).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
