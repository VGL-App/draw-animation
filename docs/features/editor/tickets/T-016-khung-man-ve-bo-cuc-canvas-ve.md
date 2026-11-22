---
id: T-016
title: Khung màn vẽ: bố cục, canvas, vẽ bằng cọ Pencil, mở dự án
feature: editor
spec: features/editor/spec.md
scenarios: [KB-1, KB-22]
owner: Nhật
status: backlog
risk: high
size: L
depends_on: [T-001, T-003, T-005, T-006, T-008]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-016: KHUNG MÀN VẼ: BỐ CỤC, CANVAS, VẼ BẰNG CỌ PENCIL, MỞ DỰ ÁN

## Mô tả

Tạo `:feature:editor` với bố cục màn vẽ theo thiết kế (thanh tiêu đề, thanh công cụ 8 nút, cột tùy chọn, dòng thời gian, canvas theo tỷ lệ dự án), vẽ nét bằng cọ Pencil một ngón, hai ngón di chuyển, thu phóng, xoay. Mở dự án từ danh sách và màn Tạo dự án; ghi mốc nét vẽ đầu tiên và sự kiện `project_open`.

## Phạm vi

- `EditorScreen` trạng thái `drawing`, `drawing-no-ad`; vùng chạm cột phải và thanh công cụ ≥ 48 dp.
- Canvas 9:16 ≥ 50% chiều cao ở 411 × 891; khung 360 × 640 thanh công cụ cuộn được.
- Cờ tắt: mọi lối mở màn vẽ hiện "Coming soon" (đã làm ở các màn gọi; ticket này khai đích và cờ).
- Nền tảng nét vẽ và dữ liệu khung để các công cụ sau dùng.

**Không bao gồm:**

- Tự lưu (T-017), hoàn tác (T-018), các công cụ khác (T-019 trở đi), dải quảng cáo (T-035).

## Rủi ro sơ bộ

Sự kiện phân tích (điểm chặn 4); kỹ thuật vẽ bằng ngón và bút mới trong dự án.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
