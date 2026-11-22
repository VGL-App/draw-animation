---
id: T-040
title: Dải quảng cáo màn vẽ, rời màn vẽ, nút Xuất
feature: editor
spec: features/editor/spec.md
scenarios: [KB-19, KB-20]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-008, T-020]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-040: DẢI QUẢNG CÁO MÀN VẼ, RỜI MÀN VẼ, NÚT XUẤT

## Mô tả

Đặt `editor_top_native` cách hàng nút và canvas ≥ 16 dp, giữ chỗ khi nạp hay lỗi; rời màn tự lưu xong rồi có thể hiện `editor_exit_inter`; nút "Export" (ẩn khi cờ Xuất tắt) gọi callback; nếu màn Xuất (T-042) đã có thì nối trong `:app`, không thì T-042 nối. Sự kiện `frame_add` (gom theo phiên, ghi khi rời màn).

## Phạm vi

- Trạng thái `drawing`; không quảng cáo nào khác trên canvas, dòng thời gian, bảng công cụ.

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Quảng cáo, sự kiện phân tích (điểm chặn 1, 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
