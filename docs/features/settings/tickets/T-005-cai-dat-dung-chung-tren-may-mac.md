---
id: T-005
title: Cài đặt dùng chung trên máy: mặc định dự án, lực bút, mốc nét vẽ đầu tiên
feature: settings
spec: features/settings/spec.md
scenarios: [KB-2, KB-3, KB-4]
owner: Nhật
status: on-staging
risk: high
size: S
depends_on: []
sprint:
plan: features/settings/plans/T-005-cai-dat-dung-chung.md
auto_run:
created: 2026-11-22
---

# T-005: CÀI ĐẶT DÙNG CHUNG TRÊN MÁY: MẶC ĐỊNH DỰ ÁN, LỰC BÚT, MỐC NÉT VẼ ĐẦU TIÊN

## Mô tả

Lưu trong `:core:data` các giá trị nhiều feature cùng đọc: tốc độ khung mặc định (12), khung hình mặc định (9:16), lực bút cảm ứng (bật), và mốc "nét vẽ đầu tiên" (màn vẽ ghi một lần; luật quảng cáo đọc).

## Phạm vi

- Giá trị hợp lệ: tốc độ 6, 8, 12, 15, 24, 30; tỷ lệ 16:9, 9:16, 1:1 (settings câu 9).
- Giá trị giữ trên máy cả khi cờ Cài đặt tắt (settings câu 15). Luồng đọc tự thử lại khi `IOException`. Unit test.

**Không bao gồm:**

- Màn Cài đặt (T-016); lực bút riêng từng cọ (T-022).

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo. Khóa DataStore mới.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
