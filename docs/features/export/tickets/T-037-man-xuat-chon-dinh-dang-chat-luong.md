---
id: T-037
title: Màn Xuất: chọn định dạng, chất lượng, watermark, tùy chọn mở bằng quảng cáo
feature: export
spec: features/export/spec.md
scenarios: [KB-1, KB-2, KB-3, KB-9, KB-10]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-005, T-006, T-016]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-037: MÀN XUẤT: CHỌN ĐỊNH DẠNG, CHẤT LƯỢNG, WATERMARK, TÙY CHỌN MỞ BẰNG QUẢNG CÁO

## Mô tả

Tạo `:feature:export` với trạng thái chọn: MP4, GIF, chuỗi PNG (Ad), GIF trong suốt (Ad), 720p/1080p theo dự án, công tắc watermark; một lượt `export_rw_extra` mở cả ba tùy chọn trong một lần mở màn; `export_options_mrec`.

## Phạm vi

- `ExportScreen` trạng thái `options` theo thiết kế; screenshot test, 360 × 640, máy gập.
- Sự kiện `export_option_unlock_rewarded`.
- Cờ tắt: màn vẽ và màn Hoàn thành ẩn nút xuất.

**Không bao gồm:**

- Làm ra file (T-038, T-039).

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
