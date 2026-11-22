---
id: T-027
title: Dòng thời gian: khung, chọn nhiều, giữ khung, tốc độ khung
feature: editor
spec: features/editor/spec.md
scenarios: [KB-9]
owner: Nhật
status: backlog
risk: low
size: L
depends_on: [T-018]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-027: DÒNG THỜI GIAN: KHUNG, CHỌN NHIỀU, GIỮ KHUNG, TỐC ĐỘ KHUNG

## Mô tả

Thêm, xóa, nhân bản khung; nhấn giữ vào chọn nhiều, kéo để chọn dải; chép và dán dải; giữ khung 1× đến 4×; bảng tốc độ khung 6, 8, 12, 15, 24, 30.

## Phạm vi

- Trạng thái `timeline-multiselect`, `hold-frame`, `frame-rate` theo thiết kế.
- Nhân bản 10 khung trong tối đa 3 thao tác.

**Không bao gồm:**

- Phát thử (T-028).

## Rủi ro sơ bộ

Không chạm điểm chặn; cỡ L, cân nhắc tách khi viết plan.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
