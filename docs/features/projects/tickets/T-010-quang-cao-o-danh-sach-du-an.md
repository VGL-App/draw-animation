---
id: T-010
title: Quảng cáo ở danh sách dự án
feature: projects
spec: features/projects/spec.md
scenarios: [KB-1, KB-2]
owner: Nhật
status: planned
risk: high
size: S
depends_on: [T-007, T-009]
sprint:
plan: features/projects/plans/T-010-quang-cao-danh-sach.md
auto_run:
created: 2026-11-22
---

# T-010: QUẢNG CÁO Ở DANH SÁCH DỰ ÁN

## Mô tả

Đặt `projects_empty_native` (màn trống, dưới hai nút), `projects_bottom` (thanh đáy, chỉ khi có dự án), `projects_grid` (một ô sau mỗi 6 dự án, cả bố cục gập), theo khóa `is_show_<mã>` của từng vị trí. Bỏ luật trước nét vẽ đầu tiên ngày 2026-11-22, vì anh Nhật bỏ hẳn luật đó (spec projects câu hỏi 24).

## Phạm vi

- Màn bọc `InContentAds`; screenshot test với thẻ giả.

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
