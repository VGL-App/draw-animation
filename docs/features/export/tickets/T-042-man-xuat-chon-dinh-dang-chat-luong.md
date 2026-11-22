---
id: T-042
title: Màn Xuất: chọn định dạng, chất lượng, watermark, tùy chọn mở bằng quảng cáo
feature: export
spec: features/export/spec.md
scenarios: [KB-1, KB-2, KB-3, KB-9, KB-10]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-007, T-018]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-042: MÀN XUẤT: CHỌN ĐỊNH DẠNG, CHẤT LƯỢNG, WATERMARK, TÙY CHỌN MỞ BẰNG QUẢNG CÁO

## Mô tả

Tạo `:feature:export` với trạng thái chọn (MP4, GIF, chuỗi PNG, GIF trong suốt; 720p/1080p theo dự án; watermark); một lượt `export_rw_extra` mở cả ba tùy chọn trong một lần mở màn; `export_options_mrec`. Tạo đích điều hướng của màn Xuất; nối nút "Export" của màn vẽ trong `:app` nếu T-040 đã xong (không thì T-040 nối).

## Phạm vi

- `ExportScreen` `options`; screenshot, 360 × 640, máy gập. Sự kiện `export_option_unlock_rewarded`.
- Mở từ màn vẽ kiểm đầy đủ sau T-040; lối từ màn Hoàn thành bài học do T-053 nối. Cùng sửa `AppNavHost` của `:app` với các ticket màn hình khác: danh sách chạy xếp tuần tự các ticket này (run-check.py báo cùng sửa file).

**Không bao gồm:**

- Làm ra file (T-043, T-044).

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
