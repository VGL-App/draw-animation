---
id: T-018
title: Màn vẽ: bố cục, mở dự án, cờ
feature: editor
spec: features/editor/spec.md
scenarios: [KB-1, KB-22]
owner: Nhật
status: backlog
risk: medium
size: M
depends_on: [T-002, T-006, T-009]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-018: MÀN VẼ: BỐ CỤC, MỞ DỰ ÁN, CỜ

## Mô tả

Tạo `:feature:editor` với bố cục theo thiết kế (thanh tiêu đề, thanh công cụ 8 nút, cột tùy chọn, dòng thời gian, canvas theo tỷ lệ), mở dự án từ danh sách và màn Tạo dự án; sự kiện `project_open`.

## Phạm vi

- `EditorScreen` `drawing-no-ad` theo thiết kế; vùng chạm ≥ 48 dp; 360 × 640 thanh công cụ cuộn được; canvas 9:16 ≥ 50% chiều cao.
- Cờ tắt: mọi lối mở màn vẽ hiện "Coming soon". Cùng sửa `AppNavHost` của `:app` với các ticket màn hình khác: danh sách chạy xếp tuần tự các ticket này (run-check.py báo cùng sửa file).

**Không bao gồm:**

- Lõi nét vẽ (T-019), dải quảng cáo (T-040).

## Rủi ro sơ bộ

Sự kiện phân tích (điểm chặn 4).

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
