---
id: T-006
title: Cờ tính năng và khóa Remote Config của 6 feature
feature: projects
spec: features/projects/spec.md
scenarios: []
owner: Nhật
status: approved
risk: high
size: S
depends_on: []
sprint:
plan: features/projects/plans/T-006-co-tinh-nang.md
auto_run:
created: 2026-11-22
---

# T-006: CỜ TÍNH NĂNG VÀ KHÓA REMOTE CONFIG CỦA 6 FEATURE

## Mô tả

Khai báo một lần trong `:core:config` 6 cờ `feature_<f>_enabled` (mặc định tắt), để các plan sau không cùng sửa `FeatureFlag.kt` và `docs/remote-config-keys.md`. Khóa `time_reload_editor_top_native` chuyển sang T-008, khóa `lessons_content_version` chuyển sang T-048, vì khóa nằm ở module sở hữu (ADR-0046, quyết định 3), theo lời anh Nhật trong phiên 2026-11-22 (câu hỏi của plan T-006).

## Phạm vi

- Bản staging bật sẵn 5 cờ: onboarding, projects, settings, editor, export. Cờ lessons chỉ bật khi có nội dung bài mẫu (T-054, theo lessons mục 8).
- Cập nhật `docs/remote-config-keys.md`; test có sẵn chỉ cập nhật phần dữ liệu (dòng enum mới), theo ADR-0029.

**Không bao gồm:**

- Hành vi khi cờ tắt ở từng màn (KB tắt từ xa của từng feature, ticket của màn).

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
