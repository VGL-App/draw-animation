---
id: T-042
title: Soạn và vẽ nội dung bài học, đưa 2 bài mẫu lên R2 staging
feature: lessons
spec: features/lessons/spec.md
scenarios: [KB-1]
owner: Nhật
status: backlog
risk: medium
size: L
depends_on: [T-041]
sprint:
plan:
auto_run: false
created: 2026-11-22
---

# T-042: SOẠN VÀ VẼ NỘI DUNG BÀI HỌC, ĐƯA 2 BÀI MẪU LÊN R2 STAGING

## Mô tả

AI soạn kịch bản 18 bài thành tài liệu trong `docs/features/lessons/` để anh Nhật duyệt; người trong nhóm vẽ khung và nét dẫn; đưa 2 bài mẫu ("Bouncing ball", "Squash and stretch") lên R2 staging trước, phần còn lại trước khi phát hành.

## Phạm vi

- Kịch bản 18 bài, 4 nhóm.
- Đưa file lên R2 staging (người làm).

**Không bao gồm:**

- Không có.

## Rủi ro sơ bộ

Việc của người (vẽ, đưa file lên R2), phiên tự chạy không làm.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
