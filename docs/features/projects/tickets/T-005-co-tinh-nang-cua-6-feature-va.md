---
id: T-005
title: Cờ tính năng của 6 feature và khóa lessons_content_version
feature: projects
spec: features/projects/spec.md
scenarios: [KB-13]
owner: Nhật
status: backlog
risk: high
size: S
depends_on: []
sprint:
plan:
auto_run:
created: 2026-11-22
---

# T-005: CỜ TÍNH NĂNG CỦA 6 FEATURE VÀ KHÓA LESSONS_CONTENT_VERSION

## Mô tả

Khai báo cờ `feature_onboarding_enabled`, `feature_projects_enabled`, `feature_settings_enabled`, `feature_editor_enabled`, `feature_export_enabled`, `feature_lessons_enabled` (mặc định tắt, bản staging bật sẵn) và khóa Remote Config `lessons_content_version` trong `:core:config`, một lần cho cả 6 feature để các plan sau không cùng sửa `FeatureFlag.kt`.

## Phạm vi

- Thêm 6 dòng vào `FeatureFlag` và vào `StagingFeatureFlagsModule`.
- Khai báo khóa `lessons_content_version` theo `docs/ops/remote-config.md` (lessons câu 5).
- Cập nhật `docs/remote-config-keys.md` nếu tài liệu đó liệt kê khóa.
- Cập nhật test có sẵn chỉ ở phần dữ liệu (dòng enum mới), theo ADR-0029.

**Không bao gồm:**

- Hành vi khi cờ tắt ở từng màn: thuộc ticket của màn đó.

## Rủi ro sơ bộ

Chạm module core dùng chung (điểm chặn 7 của risk-scoring.md): rủi ro cao, cần duyệt chéo.

## Định nghĩa hoàn thành

- [ ] Mọi tiêu chí chấp nhận của các kịch bản liệt kê ở trên đạt.
- [ ] Có test tự động cho phần logic và giao diện chính.
- [ ] CI qua toàn bộ, đã merge vào nhánh chính.
- [ ] Có trong bản staging, đúng với thiết kế đã duyệt.
- [ ] Đã được nghiệm thu chéo và nghiệm thu cuối.
