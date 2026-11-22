---
id: T-015
title: Onboarding 4 trang bắt buộc
feature: onboarding
spec: features/onboarding/spec.md
scenarios: [KB-1, KB-2, KB-3, KB-4, KB-5, KB-6, KB-7]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-007, T-009]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-015: ONBOARDING 4 TRANG BẮT BUỘC

## Mô tả

Tạo `:feature:onboarding`: 4 trang sau Loading ở lần mở đầu, "Next" hay vuốt, Back về trang trước, "Start drawing" đánh dấu đã xong rồi mở danh sách dự án; thẻ `native_onboarding`; sự kiện `onboarding_page_view`, `onboarding_complete`.

## Phạm vi

- Trang 1 đến 4 và `page-1-no-ad` theo thiết kế; nút cách thẻ ≥ 24 dp ở mọi cỡ chữ; screenshot 360 × 640.
- Lỗi ghi "đã xong" vẫn đi tiếp, ghi log (onboarding câu 9); cờ tắt thì mở thẳng danh sách dự án. Cùng sửa `AppNavHost` của `:app` với các ticket màn hình khác: danh sách chạy xếp tuần tự các ticket này (run-check.py báo cùng sửa file).

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích (điểm chặn 1, 4); sửa luồng mở app.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
