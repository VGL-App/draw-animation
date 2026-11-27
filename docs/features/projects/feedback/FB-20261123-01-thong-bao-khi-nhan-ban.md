---
id: FB-20261123-01
title: Đang nhân bản thì thông báo thứ hai chờ tới khi nhân bản xong
reporter: AI (rà soát độc lập của RUN-20261123-01, anh Nhật chọn hướng sửa)
source: staging              # staging | nghiem-thu | store | crash
build: 0.1.0-staging.5 (5)
feature: projects
ticket: T-012                # nếu biết
severity: nhe                # chan | nang | nhe | gop-y
status: planned              # new | triaged | planned | fixed | wontfix
created: 2026-11-23
---

# FB-20261123-01: ĐANG NHÂN BẢN THÌ THÔNG BÁO THỨ HAI CHỜ TỚI KHI NHÂN BẢN XONG

## Điều đã xảy ra

Rà soát độc lập của PR #36 (T-012, RUN-20261123-01) phát hiện, báo cáo phiên ghi ở mục "Cần người kiểm tra": trong lúc nhân bản, thông báo "Duplicating “<tên>”…" đang hiện. Người dùng làm một việc ra thông báo (chạm thẻ khi màn vẽ chưa có ra "Coming soon"), "Coming soon" hiện ngay. Trong 4 giây sau đó có thông báo thứ hai (chạm một thẻ khác, lỗi đổi tên hay lỗi xóa): thông báo thứ hai không hiện. Khi "Coming soon" tự ẩn, "Duplicating…" hiện lại, và thông báo thứ hai chỉ hiện khi nhân bản xong. Với dự án lớn, người dùng có thể không bao giờ thấy thông báo lỗi đó đúng lúc.

Nguyên nhân trong code (`feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreen.kt`, nhánh chính `a6bb121`): "Duplicating…" và mọi thông báo khác dùng chung một `SnackbarHostState`. Hàm `show` của `ProjectsRoute` (khoảng dòng 105 đến 111) chỉ ẩn thông báo `Indefinite`, còn vòng lặp của "Duplicating…" (khoảng dòng 257, 258) xếp hàng lại ngay sau thông báo thứ nhất. Thông báo thứ hai xếp sau vòng lặp đó, nên chờ tới khi vòng lặp bị hủy (nhân bản xong).

Cùng nguyên nhân, ngoài lúc nhân bản: hai thông báo liên tiếp (ví dụ hai lần chạm thẻ ra "Coming soon" rồi "Can't open this project.") thì thông báo thứ hai chờ thông báo thứ nhất tự ẩn (4 giây). Màn của T-009 trước T-012 ẩn thông báo đang hiện và hiện ngay thông báo mới.

Đo lại bằng một test tạm không commit (2026-11-23, nhánh `claude/batch-20261123-03`, `cdebb3c`, code giống nhánh chính), khung `w411dp-h891dp-xxhdpi`, đồng hồ test dừng:

| Thời điểm | "Coming soon" | "Can't open this project." | "Duplicating…" |
| --- | --- | --- | --- |
| Đang nhân bản, chạm thẻ thứ hai, sau 500 ms | có | không | không |
| Chạm thẻ hỏng 1 giây sau, sau 500 ms | có | **không** | không |
| Thêm 5 giây | không | **không** | có |
| Nhân bản xong, sau 112 ms | không | có | đang ẩn |

Không nhân bản, chạm thẻ thường rồi 1 giây sau chạm thẻ hỏng: sau 500 ms vẫn là "Coming soon"; "Can't open this project." chỉ hiện sau 4,5 giây.

## Các bước làm lại

1. Có ít nhất ba dự án, một dự án lớn (nhân bản lâu), một dự án hỏng; cờ màn vẽ tắt (hay chưa có màn vẽ).
2. Nhấn giữ dự án lớn, bấm "Duplicate". "Duplicating “<tên>”…" hiện.
3. Chạm một thẻ thường: "Coming soon" hiện.
4. Trong 4 giây, chạm thẻ hỏng: "Can't open this project." không hiện; sau đó "Duplicating…" hiện lại và thông báo kia chỉ hiện khi nhân bản xong.

## Mong đợi

Mỗi thông báo hiện ngay khi có, theo thứ tự (spec, KB-2: "app hiện thông báo ngắn 'Coming soon'"; mục 4: thông báo ngắn chuẩn). "Duplicating…" nhường chỗ cho thông báo khác và hiện lại khi không còn thông báo nào mà vẫn đang nhân bản (plan T-012, mục 3.1: "thông báo mới hiện ngay").

## Ảnh chụp, video

Không có. Bằng chứng là số đo của test tạm ở trên và mục "Cần người kiểm tra" của `docs/runs/RUN-20261123-01.md`.

## Thiết bị

- Máy: Robolectric (test JVM); chưa thử trên máy thật vì staging chưa tạo được dự án (cần T-011).
- Phiên bản Android: không áp dụng.
- Ngôn ngữ, chế độ tối: tiếng Anh, sáng.

## Phân loại

- **Loại:** lỗi so với spec (thông báo ngắn không hiện khi có; cũng trái câu "thông báo mới hiện ngay" của plan T-012 đã duyệt).
- **Xử lý:** sửa trong sprint này, bằng plan T-012 phiên bản 2: "Duplicating…" dùng một `SnackbarHost` riêng, chính là phương án dự phòng đã duyệt ở mục 3.4 của phiên bản 1; thông báo khác trở lại cách của T-009 (ẩn thông báo đang hiện rồi hiện ngay).
- **Lý do:** báo cáo phiên RUN-20261123-01 chỉ liệt kê ba phương án, không đề xuất: (a) plan mới với host riêng, (b) trả cách ẩn mọi thông báo của T-009 cho host chung, (c) chấp nhận. Ngày 2026-11-23 anh Nhật nói "Duyệt mọi đề xuất của AI", rồi AI tự chọn (a). (b) một mình không đủ: "Duplicating…" vẫn ở host chung thì thông báo khác ẩn nó và vòng lặp lại xếp hàng chung. Lựa chọn này chờ chủ feature xác nhận khi duyệt plan phiên bản 2 (câu hỏi 14 của plan).
- **Liên kết:** `features/projects/plans/T-012-menu-du-an.md`, phiên bản 2; ticket `features/projects/tickets/T-012-menu-du-an-mo-doi-ten-nhan.md`.
