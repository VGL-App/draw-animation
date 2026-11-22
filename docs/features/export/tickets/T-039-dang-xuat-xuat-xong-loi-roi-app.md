---
id: T-039
title: Đang xuất, xuất xong, lỗi, rời app
feature: export
spec: features/export/spec.md
scenarios: [KB-4, KB-5, KB-6, KB-7]
owner: Nhật
status: backlog
risk: high
size: L
depends_on: [T-037, T-038]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-039: ĐANG XUẤT, XUẤT XONG, LỖI, RỜI APP

## Mô tả

Trạng thái đang xuất (tiến độ, "Cancel", `export_wait_mrec`), xong (lưu vào thư viện máy qua MediaStore, "Share", "Open in Gallery", `export_done_native`, `export_interstitial` khi rời màn), lỗi có thử lại; rời app thì dừng, quay lại có thông báo. Sự kiện `export_start`, `export_success`, `export_fail`.

## Phạm vi

- Trạng thái `exporting`, `done`, `error` theo thiết kế; tên file theo dự án, trùng thì " (2)".
- Mở từ màn Hoàn thành bài học thì nút ghi "Back" và về màn đó.

**Không bao gồm:**

- Quyền lưu trên Android 8, 9 (T-040).

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
