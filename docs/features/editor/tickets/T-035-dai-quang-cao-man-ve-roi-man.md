---
id: T-035
title: Dải quảng cáo màn vẽ, rời màn vẽ, mở màn Xuất
feature: editor
spec: features/editor/spec.md
scenarios: [KB-19, KB-20]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-007, T-016]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-035: DẢI QUẢNG CÁO MÀN VẼ, RỜI MÀN VẼ, MỞ MÀN XUẤT

## Mô tả

Đặt `editor_top_native` dưới thanh tiêu đề, cách hàng nút và canvas ≥ 16 dp, giữ chỗ khi nạp hay lỗi; rời màn vẽ tự lưu rồi có thể hiện `editor_exit_inter`; nút "Export" mở màn Xuất (ẩn khi cờ Xuất tắt).

## Phạm vi

- Không quảng cáo nào khác trên canvas, dòng thời gian, bảng công cụ.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Quảng cáo (điểm chặn 1).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
