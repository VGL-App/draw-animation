---
id: T-018
title: Hoàn tác, làm lại
feature: editor
spec: features/editor/spec.md
scenarios: [KB-11]
owner: Nhật
status: backlog
risk: low
size: M
depends_on: [T-016]
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-018: HOÀN TÁC, LÀM LẠI

## Mô tả

Ngăn xếp hoàn tác ít nhất 50 bước mỗi lần mở dự án cho nét vẽ, tô, biến đổi vùng chọn, thêm hay xóa khung, layer; không giữ sau khi rời màn vẽ.

## Phạm vi

- Khung thao tác để các công cụ sau đăng ký bước hoàn tác.
- Unit test.

**Không bao gồm:**

- Các công cụ cụ thể (ticket của công cụ).

## Rủi ro sơ bộ

Không chạm điểm chặn.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
