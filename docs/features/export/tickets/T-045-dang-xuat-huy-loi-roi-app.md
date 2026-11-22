---
id: T-045
title: Đang xuất, hủy, lỗi, rời app
feature: export
spec: features/export/spec.md
scenarios: [KB-4, KB-6, KB-7]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-042, T-043, T-044]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-045: ĐANG XUẤT, HỦY, LỖI, RỜI APP

## Mô tả

Trạng thái đang xuất (tiến độ, "Cancel", `export_wait_mrec`), lỗi có thử lại, rời app thì dừng và báo khi quay lại. Sự kiện `export_start`, `export_fail`.

## Phạm vi

- Trạng thái `exporting`, `error`.

**Không bao gồm:**

- Xuất xong (T-046).

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
