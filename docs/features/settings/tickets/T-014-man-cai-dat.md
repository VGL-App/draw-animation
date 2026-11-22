---
id: T-014
title: Màn Cài đặt
feature: settings
spec: features/settings/spec.md
scenarios: [KB-1, KB-2, KB-3, KB-4, KB-7, KB-8, KB-9, KB-10]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-004, T-005, T-006, T-008]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-014: MÀN CÀI ĐẶT

## Mô tả

Tạo `:feature:settings`: tốc độ khung và khung hình mặc định (hộp thoại chọn), "Stylus pressure", "Autosave · Every 30 s" chỉ để xem, "Ad privacy choices" chỉ khi hộp thoại là bắt buộc, "Privacy policy" (https://studio.vgl.io.vn/privacy-policy), "Rate Frameloop" mở Google Play, "Version", và `settings_bottom`.

## Phạm vi

- `SettingsScreen` trạng thái `default` theo thiết kế; screenshot test cùng tên, khung 360 × 640.
- Mở hộp thoại quyền riêng tư quảng cáo qua `:core:ads`.
- Cờ tắt: danh sách dự án ẩn nút cài đặt.

**Không bao gồm:**

- Sao lưu tất cả và khôi phục (T-015).

## Rủi ro sơ bộ

Quảng cáo, hộp thoại đồng ý, liên kết ra ngoài app (điểm chặn 1, 5, 6).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
