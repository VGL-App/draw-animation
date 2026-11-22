---
id: T-004
title: Vẽ khung ra ảnh: gộp layer, nền, camera ảo, ảnh thu nhỏ
feature: projects
spec: features/projects/spec.md
scenarios: [KB-2]
owner: Nhật
status: on-staging
risk: high
size: M
depends_on: [T-001]
sprint:
plan: features/projects/plans/T-004-ve-khung-ra-anh.md
auto_run:
created: 2026-11-22
---

# T-004: VẼ KHUNG RA ẢNH: GỘP LAYER, NỀN, CAMERA ẢO, ẢNH THU NHỎ

## Mô tả

Phần vẽ một khung ra ảnh trong `:core:project`: gộp layer đang hiện, trên nền của dự án, áp camera ảo; màn vẽ, ảnh thu nhỏ, màn Xuất và bài học dùng chung để mọi nơi ra cùng một ảnh.

## Phạm vi

- Vẽ ở độ phân giải yêu cầu (ảnh thu nhỏ, 720p, 1080p), có hay không có nền (chuỗi PNG, GIF trong suốt).
- Ảnh thu nhỏ là khung đầu (projects câu 20); nền "Photo" cắt vừa khung (projects câu 3); camera ảo đủ cho T-037 (lia, phóng, xoay).
- Test so điểm ảnh trên Robolectric.

**Không bao gồm:**

- Watermark (T-044); lớp vẽ theo không xuất ra file (T-036).

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo. Kỹ thuật vẽ bitmap mới trong dự án.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
