---
id: T-016
title: Màn Cài đặt
feature: settings
spec: features/settings/spec.md
scenarios: [KB-1, KB-2, KB-3, KB-4, KB-7, KB-8, KB-9, KB-10]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-005, T-007, T-009]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-016: MÀN CÀI ĐẶT

## Mô tả

Tạo `:feature:settings`: tốc độ khung và khung hình mặc định, "Stylus pressure", "Autosave · Every 30 s" (chỉ xem), "Ad privacy choices" (chỉ khi bắt buộc), "Privacy policy" (https://studio.vgl.io.vn/privacy-policy), "Rate Frameloop" (Google Play), "Version", `settings_bottom`.

## Phạm vi

- `SettingsScreen` `default` theo thiết kế; screenshot 360 × 640.
- Tác dụng của lực bút lên nét kiểm đầy đủ sau T-022. Cùng sửa `AppNavHost` của `:app` với các ticket màn hình khác: danh sách chạy xếp tuần tự các ticket này (run-check.py báo cùng sửa file).

**Không bao gồm:**

- Sao lưu tất cả (T-017).

## Rủi ro sơ bộ

Quảng cáo, hộp thoại đồng ý, liên kết ra ngoài (điểm chặn 1, 5, 6).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
