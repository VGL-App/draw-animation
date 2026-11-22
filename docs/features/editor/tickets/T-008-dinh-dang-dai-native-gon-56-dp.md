---
id: T-008
title: Định dạng dải native gọn 56 dp và vị trí editor_top_native
feature: editor
spec: features/editor/spec.md
scenarios: [KB-19]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-007]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-008: ĐỊNH DẠNG DẢI NATIVE GỌN 56 DP VÀ VỊ TRÍ EDITOR_TOP_NATIVE

## Mô tả

App tự làm trong `:core:ads` định dạng native banner gọn khoảng 56 dp và khai vị trí `editor_top_native` (bản đề xuất Q17): giữ chỗ cố định khi nạp hay lỗi, tải lại theo `time_reload_editor_top_native`.

## Phạm vi

- Composable theo thiết kế `NativeAdStrip`; không có quảng cáo do khóa tắt thì không giữ chỗ.
- ADR của app (dải 1001 trở lên) ghi ngoại lệ với MC-02; ghi `hold=core/ads` cho lần chạy `template-sync.sh`; mở issue `from-app` sau khi chạy ổn trên staging (editor mục 5).
- Test: nhãn "Ad" và chữ nút không bị cắt ở cỡ chữ lớn nhất; screenshot với thẻ giả; tải lại theo thời gian.

**Không bao gồm:**

- Đặt dải lên màn vẽ (T-040).

## Rủi ro sơ bộ

Định dạng quảng cáo mới trong module core (điểm chặn 1, 7).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
