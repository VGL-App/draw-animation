---
id: T-055
title: Chọn và đưa file âm thanh CC0 lên R2 staging
feature: editor
spec: features/editor/spec.md
scenarios: [KB-13, KB-14]
owner: Nhật
status: backlog
risk: medium
size: M
depends_on: []
sprint:
plan:
auto_run: false
created: 2026-11-22
---

# T-055: CHỌN VÀ ĐƯA FILE ÂM THANH CC0 LÊN R2 STAGING

## Mô tả

Nhóm chọn hiệu ứng âm thanh CC0 cho bộ cơ bản (khoảng 10, dưới 2 MB), gói "Cartoon" (24 âm) và "Ambience" (16 âm), ghi nguồn và giấy phép, đưa lên R2 staging theo đường dẫn của T-033, T-034 (editor câu 14).

## Phạm vi

- Danh sách file kèm nguồn và giấy phép CC0, ghi trong `docs/features/editor/`.
- Đưa file lên R2 staging (người làm).

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Việc của người, phiên tự chạy không làm.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
