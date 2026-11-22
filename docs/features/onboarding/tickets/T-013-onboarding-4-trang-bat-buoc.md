---
id: T-013
title: Onboarding 4 trang bắt buộc
feature: onboarding
spec: features/onboarding/spec.md
scenarios: [KB-1, KB-2, KB-3, KB-4, KB-5, KB-6, KB-7]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-005, T-006, T-008]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-013: ONBOARDING 4 TRANG BẮT BUỘC

## Mô tả

Tạo `:feature:onboarding`: 4 trang giới thiệu sau Loading ở lần mở đầu, bấm "Next" hay vuốt để đổi trang, Back về trang trước, "Start drawing" đánh dấu đã xong rồi mở danh sách dự án. Có thẻ `native_onboarding` ở lần mở đầu và sự kiện `onboarding_page_view`, `onboarding_complete`.

## Phạm vi

- `OnboardingScreen` trang 1 đến 4 và `page-1-no-ad` theo thiết kế; nút ghim đáy cách thẻ ≥ 24 dp ở mọi cỡ chữ.
- Lưu "đã xong" trên máy; lỗi ghi vẫn đi tiếp, ghi log (onboarding câu 9).
- Khung giữ chỗ khi quảng cáo đang nạp, không giữ chỗ khi không được phép (onboarding câu 3).
- Cờ tắt thì mở thẳng danh sách dự án.
- Screenshot test, khung 360 × 640.

**Không bao gồm:**

- Màn hình dự án trống (T-008).

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích mới (điểm chặn 1, 4); sửa luồng mở app trong `:app`.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
