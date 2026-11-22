---
id: T-046
title: Xuất xong: lưu vào thư viện, chia sẻ, mở
feature: export
spec: features/export/spec.md
scenarios: [KB-5]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-045]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-046: XUẤT XONG: LƯU VÀO THƯ VIỆN, CHIA SẺ, MỞ

## Mô tả

Lưu qua MediaStore (MP4 Movies/Frameloop, GIF và PNG Pictures/Frameloop), tên theo dự án, trùng thì " (2)"; "Share", "Open in Gallery", "Back to editor" (mở từ bài học: "Back"); `export_done_native`, `export_interstitial` khi rời màn; sự kiện `export_success`.

## Phạm vi

- Trạng thái `done`.

**Không bao gồm:**

- Quyền Android 8, 9 (T-047).

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
