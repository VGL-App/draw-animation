---
id: T-056
title: Nút lựa chọn quyền riêng tư quảng cáo trên thanh trên khi cờ Cài đặt tắt
feature: projects
spec: features/projects/spec.md
scenarios: [KB-11]
owner: Nhật
status: on-staging
risk: high
size: S
depends_on: [T-009]
sprint:
plan: features/projects/plans/T-056-nut-quyen-rieng-tu.md
auto_run:
created: 2026-11-22
---

# T-056: NÚT LỰA CHỌN QUYỀN RIÊNG TƯ QUẢNG CÁO TRÊN THANH TRÊN KHI CỜ CÀI ĐẶT TẮT

## Mô tả

Khi cờ `feature_settings_enabled` tắt (ví dụ lần mở đầu trên production chưa tải được cấu hình từ xa), thanh trên của danh sách dự án hiện nút lựa chọn quyền riêng tư quảng cáo ở chỗ nút cài đặt, để người dùng vẫn rút được đồng ý quảng cáo (spec projects KB-11, câu hỏi 23).

## Phạm vi

- Thanh trên của danh sách dự án (T-009): cờ Cài đặt bật thì nút cài đặt như cũ; cờ tắt thì `PrivacyOptionsButton` của `:core:ads`, chỉ hiện ở vùng bắt buộc hộp thoại đồng ý; vùng không bắt buộc thì không có nút nào.
- Theo dõi cờ là một `Flow`: cờ đổi giữa chừng thì nút đổi theo.
- Test: cờ tắt và vùng bắt buộc thì có nút quyền riêng tư; vùng không bắt buộc thì không có; cờ bật thì nút cài đặt; screenshot hai trạng thái.

**Không bao gồm:**

- Dòng "Ad privacy choices" trong màn Cài đặt (T-016).

## Rủi ro sơ bộ

Chạm hộp thoại đồng ý của người dùng (điểm chặn 5 của risk-scoring.md): rủi ro cao, người phụ trách sản phẩm cần biết trước khi duyệt.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
