---
id: T-003
title: Vẽ khung ra ảnh: gộp layer, nền, camera ảo, ảnh thu nhỏ
feature: projects
spec: features/projects/spec.md
scenarios: [KB-2]
owner: Nhật
status: backlog
risk: high
size: M
depends_on: [T-001]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-003: VẼ KHUNG RA ẢNH: GỘP LAYER, NỀN, CAMERA ẢO, ẢNH THU NHỎ

## Mô tả

Thêm vào `:core:project` phần vẽ một khung của dự án ra ảnh: gộp các layer đang hiện theo thứ tự, trên nền của dự án (màu hay ảnh "Photo"), áp camera ảo của khung. Màn vẽ, ảnh thu nhỏ của danh sách dự án, màn Xuất và bài học dùng chung phần này để mọi nơi ra cùng một ảnh.

## Phạm vi

- Hàm vẽ khung ra bitmap ở độ phân giải yêu cầu (ảnh thu nhỏ, 720p, 1080p), có hay không có nền (cho chuỗi PNG, GIF trong suốt).
- Ảnh thu nhỏ là khung đầu, cập nhật khi rời màn vẽ (projects câu 20).
- Nền "Photo" cắt vừa khung (projects câu 3).
- Test so điểm ảnh trên Robolectric cho gộp layer, layer ẩn, nền, không nền, camera.

**Không bao gồm:**

- Watermark (T-038), lớp vẽ theo (không xuất ra file, T-029).

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo. Kỹ thuật vẽ bitmap mới trong dự án.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
