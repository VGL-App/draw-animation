---
id: T-007
title: Định dạng dải native gọn 56 dp cho màn vẽ
feature: editor
spec: features/editor/spec.md
scenarios: [KB-19]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-006]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-007: ĐỊNH DẠNG DẢI NATIVE GỌN 56 DP CHO MÀN VẼ

## Mô tả

App tự làm trong `:core:ads` định dạng native banner gọn khoảng 56 dp cho `editor_top_native` (bản đề xuất Q17): giữ chỗ cố định khi đang nạp hay lỗi để canvas không nhảy, tải lại theo `time_reload_editor_top_native` (mặc định 120 giây). Chạy ổn rồi mới đề xuất về dự án mẫu.

## Phạm vi

- Composable dải native gọn theo thiết kế `NativeAdStrip` (biểu tượng, nhãn Ad, tên, nút), khóa tham số tải lại khai theo `remote-config.md`.
- Giữ khung cố định cả lúc nạp, lỗi; không có quảng cáo do khóa tắt thì không giữ chỗ (trạng thái `drawing-no-ad`).
- Screenshot test với thẻ giả, test tải lại theo thời gian.

**Không bao gồm:**

- Đặt dải lên màn vẽ và khoảng cách 16 dp tới canvas (T-035).

## Rủi ro sơ bộ

Định dạng quảng cáo mới trong module core (điểm chặn 1, 7); ngoại lệ với MC-02, cách giữ khi đồng bộ dự án mẫu ghi ở bản đề xuất.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
