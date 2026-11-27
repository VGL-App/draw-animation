---
id: FB-20261127-01
title: App bị đóng khi mở màn Tạo dự án trên bản staging
reporter: AI (phiên RUN-20261123-02, device-session)
source: crash
build: staging debug từ commit d423d80 (device-session.yml, lần chạy 45970121323)
feature: projects
ticket: T-011
severity: chan
status: new
created: 2026-11-27
---

# FB-20261127-01: APP BỊ ĐÓNG KHI MỞ MÀN TẠO DỰ ÁN TRÊN BẢN STAGING

## Điều đã xảy ra

Trên emulator của `device-session`, bấm "New animation" ở màn danh sách dự án trống thì app bị đóng ngay ("Frameloop keeps stopping"). Gặp hai lần: một lần trong lượt monkey (09:10:52 UTC), một lần ở bước 8 của kịch bản `T-011-tao-du-an.txt` (09:11:55 UTC); bước 9 chờ `new_project_screen` không thấy.

Dấu vết (logcat, bản staging đã R8):

```text
11-27 09:11:55.851  8185  8185 E AndroidRuntime: FATAL EXCEPTION: main
11-27 09:11:55.851  8185  8185 E AndroidRuntime: java.util.NoSuchElementException: Expected at least one element matching the predicate
```

Ngăn xếp đi từ nhà máy ViewModel của Hilt vào hàm dựng một ViewModel, rồi một coroutine chạy ngay gọi `first { ... }` trên một `Flow`.

Kết luận (đọc code, chưa chạy lại trên máy): `NewProjectViewModel.init` (`feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectViewModel.kt`, dòng 69) gọi `featureFlags.enabled(FeatureFlag.PROJECTS).first { !it }` để đóng màn khi cờ tắt. Bản staging bật cờ `PROJECTS` tại chỗ (`StagingFeatureFlagsModule.projects()`), và `DefaultFeatureFlags.enabled` khi đó trả `flowOf(true)`: luồng phát một giá trị rồi kết thúc, nên `first { !it }` ném `NoSuchElementException`. Coroutine chạy ngay trên luồng chính trong `viewModelScope`, nên lỗi làm app dừng. Test JVM dùng cờ giả là luồng không kết thúc, nên không bắt được. Nghi gây ra: commit `cc32b8b` (T-011, PR #45).

## Các bước làm lại

1. Cài bản staging từ nhánh chính (commit `d423d80`), từ chối hộp thoại đồng ý quảng cáo, vào màn danh sách dự án trống.
2. Bấm "New animation".
3. App bị đóng.

## Mong đợi

Màn Tạo dự án mở (KB của T-011: từ danh sách trống bấm "New animation" mở màn Tạo dự án; bấm "Create" thì về danh sách có một dự án mới).

Hướng sửa đề xuất: chờ cờ tắt bằng `firstOrNull { !it } ?: return@launch` (hay bắt `NoSuchElementException`), và thêm test ViewModel với `FeatureFlags` giả trả `flowOf(true)` (luồng đã kết thúc), cùng một test cho mọi chỗ khác chờ cờ đổi bằng `first { ... }`.

## Ảnh chụp, video

`FB-20261127-01-1.png`: ảnh `fail.png` của bước lỗi trong artifact `device-session`.

## Thiết bị

- Máy: emulator `sdk_gphone64_x86_64` của `device-session.yml`
- Phiên bản Android: 14 (API 34)
- Ngôn ngữ, chế độ tối: mặc định của emulator

## Phân loại

<!-- Chủ feature điền, có thể nhờ AI đề xuất. -->

- **Loại:** lỗi so với spec (đề xuất của AI)
- **Xử lý:** sửa trước khi phát staging (đề xuất của AI)
- **Lý do:** app bị đóng ở luồng chính tạo dự án, không dùng được feature
- **Liên kết:** T-011, PR #45
