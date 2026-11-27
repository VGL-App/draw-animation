# TIẾN ĐỘ DỰ ÁN

<!-- Sinh bởi scripts/status.py từ trạng thái trong các tài liệu. Không sửa tay: sửa tài liệu gốc rồi chạy lại script (docs/CLAUDE.md, mục Trang tiến độ). -->

## 1. Đang chờ người

- Phản hồi chờ phân loại: [FB-20261127-01-mo-man-tao-du-an-bi-dong.md](features/projects/feedback/FB-20261127-01-mo-man-tao-du-an-bi-dong.md) (App bị đóng khi mở màn Tạo dự án trên bản staging)
- Bài học `proposed` chờ retro: L-001 (Test "chạy trên dispatcher được tiêm" không dùng scheduler riêng bên trong `runTest`)
- Bài học `proposed` chờ retro: L-002 (Bảng tra lỗi chưa có dòng cho lỗi Robolectric không tải được android-all)
- Bài học `proposed` chờ retro: L-003 (Lỗi quyền GitHub App của routine chỉ lộ ra khi phiên đẩy lần đầu)
- Bài học `proposed` chờ retro: L-004 (Thêm vị trí quảng cáo làm test JVM của `:core:ads` hết bộ nhớ trên CI)
- Bài học `proposed` chờ retro: L-005 (Plan ghi `advanceUntilIdle()` cho luồng thu trong `backgroundScope`)
- Bài học `proposed` chờ retro: L-006 (Mục 3.1 của `auto-run.md` chưa nói rõ plan phiên bản mới khi task dừng nằm ở đâu)
- Bài học `proposed` chờ retro: L-007 (Commit duyệt đợt chèn lặp khối quyết định vào khắp tệp plan)
- Bài học `proposed` chờ retro: L-008 (Tên nhánh task lấy từ plan khi plan đã ghi tên)
- Bài học `proposed` chờ retro: L-009 (Test dựng lại activity kiểm `rememberSaveable` phải dùng một lambda nội dung)
- Bài học `proposed` chờ retro: L-010 (Lời giao phiên con chưa cấm đổi cơ chế của plan để test qua)

## 2. Yêu cầu

| Yêu cầu | Ngày | Feature | Trạng thái | Ticket xong |
| --- | --- | --- | --- | --- |
| Màn vẽ ([2026-11-21-ban-dau.md](features/editor/requests/2026-11-21-ban-dau.md)) | 2026-11-21 | editor | `specified` đã có spec | 1/27 |
| Xuất hoạt hình thành video, gif, chuỗi ảnh ([2026-11-21-ban-dau.md](features/export/requests/2026-11-21-ban-dau.md)) | 2026-11-21 | export | `specified` đã có spec | 1/6 |
| Thư viện bài học ([2026-11-21-ban-dau.md](features/lessons/requests/2026-11-21-ban-dau.md)) | 2026-11-21 | lessons | `specified` đã có spec | 1/7 |
| Onboarding 4 trang ở lần mở đầu ([2026-11-21-ban-dau.md](features/onboarding/requests/2026-11-21-ban-dau.md)) | 2026-11-21 | onboarding | `specified` đã có spec | 0/1 |
| Danh sách dự án, tạo dự án, sao lưu và khôi phục ([2026-11-21-ban-dau.md](features/projects/requests/2026-11-21-ban-dau.md)) | 2026-11-21 | projects | `specified` đã có spec | 10/12 |
| Màn cài đặt của frameloop ([2026-11-21-ban-dau.md](features/settings/requests/2026-11-21-ban-dau.md)) | 2026-11-21 | settings | `specified` đã có spec | 1/3 |

## 3. Feature

### ads

Spec: [spec.md](features/ads/spec.md) `confirmed`. Ticket: chưa có.

Phản hồi: chưa có.

### editor

Spec: [spec.md](features/editor/spec.md) `confirmed`. Ticket: 26 `backlog`, 1 `on-staging`.

| Ticket | Tên | Trạng thái | Plan | Ước tính token | Phiên chạy |
| --- | --- | --- | --- | --- | --- |
| [T-007](features/editor/tickets/T-007-vi-tri-quang-cao-cua-frameloop-va.md) | Vị trí quảng cáo của Frameloop | `on-staging` | `done` (bản 2) | 15M | RUN-20261122-02, RUN-20261122-03 |
| [T-008](features/editor/tickets/T-008-dinh-dang-dai-native-gon-56-dp.md) | Định dạng dải native gọn 56 dp và vị trí editor_top_native | `backlog` | chưa có | - | - |
| [T-018](features/editor/tickets/T-018-man-ve-bo-cuc-mo-du-an.md) | Màn vẽ: bố cục, mở dự án, cờ | `backlog` | chưa có | - | - |
| [T-019](features/editor/tickets/T-019-loi-net-ve-va-canvas-co-pencil.md) | Lõi nét vẽ và canvas: cọ Pencil, di chuyển, thu phóng | `backlog` | chưa có | - | - |
| [T-020](features/editor/tickets/T-020-tu-luu-va-khoi-phuc-sau-khi.md) | Tự lưu và khôi phục sau khi app tắt đột ngột | `backlog` | chưa có | - | - |
| [T-021](features/editor/tickets/T-021-hoan-tac-lam-lai.md) | Hoàn tác, làm lại | `backlog` | chưa có | - | - |
| [T-022](features/editor/tickets/T-022-co-tay-chon-mau-hut-mau.md) | Cọ, tẩy, chọn màu, hút màu | `backlog` | chưa có | - | - |
| [T-023](features/editor/tickets/T-023-goi-co-nang-cao-mo-bang-quang.md) | Gói cọ nâng cao mở bằng quảng cáo có thưởng | `backlog` | chưa có | - | - |
| [T-024](features/editor/tickets/T-024-to-mau-khong-tran.md) | Tô màu không tràn | `backlog` | chưa có | - | - |
| [T-025](features/editor/tickets/T-025-chon-vung-hinh-chu.md) | Chọn vùng, hình, chữ | `backlog` | chưa có | - | - |
| [T-026](features/editor/tickets/T-026-menu-them-thuoc-ve-doi-xung.md) | Menu "thêm", thước, vẽ đối xứng | `backlog` | chưa có | - | - |
| [T-027](features/editor/tickets/T-027-layer-1-den-5.md) | Layer 1 đến 5 | `backlog` | chưa có | - | - |
| [T-028](features/editor/tickets/T-028-mo-layer-6-den-10-bang-quang.md) | Mở layer 6 đến 10 bằng quảng cáo có thưởng | `backlog` | chưa có | - | - |
| [T-029](features/editor/tickets/T-029-onion-skin.md) | Onion skin | `backlog` | chưa có | - | - |
| [T-030](features/editor/tickets/T-030-dong-thoi-gian-khung-chon-nhieu-chep.md) | Dòng thời gian: khung, chọn nhiều, chép và dán dải | `backlog` | chưa có | - | - |
| [T-031](features/editor/tickets/T-031-giu-khung-va-toc-do-khung.md) | Giữ khung và tốc độ khung | `backlog` | chưa có | - | - |
| [T-032](features/editor/tickets/T-032-am-thanh-nhac-va-ghi-am.md) | Âm thanh: nhạc và ghi âm | `backlog` | chưa có | - | - |
| [T-033](features/editor/tickets/T-033-hieu-ung-am-thanh-co-ban-tu.md) | Hiệu ứng âm thanh cơ bản từ R2 | `backlog` | chưa có | - | - |
| [T-034](features/editor/tickets/T-034-goi-hieu-ung-am-thanh-mo-bang.md) | Gói hiệu ứng âm thanh mở bằng quảng cáo có thưởng | `backlog` | chưa có | - | - |
| [T-035](features/editor/tickets/T-035-phat-thu.md) | Phát thử | `backlog` | chưa có | - | - |
| [T-036](features/editor/tickets/T-036-nhap-anh-video-de-ve-theo.md) | Nhập ảnh, video để vẽ theo | `backlog` | chưa có | - | - |
| [T-037](features/editor/tickets/T-037-camera-ao.md) | Camera ảo | `backlog` | chưa có | - | - |
| [T-038](features/editor/tickets/T-038-huong-dan-9-buoc-tren-man-ve.md) | Hướng dẫn 9 bước trên màn vẽ | `backlog` | chưa có | - | - |
| [T-039](features/editor/tickets/T-039-chi-dan-bai-hoc-tren-man-ve.md) | Chỉ dẫn bài học trên màn vẽ | `backlog` | chưa có | - | - |
| [T-040](features/editor/tickets/T-040-dai-quang-cao-man-ve-roi-man.md) | Dải quảng cáo màn vẽ, rời màn vẽ, nút Xuất | `backlog` | chưa có | - | - |
| [T-041](features/editor/tickets/T-041-man-ve-tren-dien-thoai-gap-khi.md) | Màn vẽ trên điện thoại gập khi mở ra | `backlog` | chưa có | - | - |
| [T-055](features/editor/tickets/T-055-chon-va-dua-file-am-thanh-cc0.md) | Chọn và đưa file âm thanh CC0 lên R2 staging | `backlog` | chưa có | - | - |

Phản hồi: chưa có.

### export

Spec: [spec.md](features/export/spec.md) `confirmed`. Ticket: 5 `backlog`, 1 `on-staging`.

| Ticket | Tên | Trạng thái | Plan | Ước tính token | Phiên chạy |
| --- | --- | --- | --- | --- | --- |
| [T-042](features/export/tickets/T-042-man-xuat-chon-dinh-dang-chat-luong.md) | Màn Xuất: chọn định dạng, chất lượng, watermark, tùy chọn mở bằng quảng cáo | `backlog` | chưa có | - | - |
| [T-043](features/export/tickets/T-043-lam-ra-file-mp4-co-am-thanh.md) | Làm ra file MP4 có âm thanh | `backlog` | chưa có | - | - |
| [T-044](features/export/tickets/T-044-lam-ra-file-gif-chuoi-png-gif.md) | Làm ra file GIF, chuỗi PNG, GIF trong suốt và watermark | `on-staging` | `done` (bản 1) | 15M | RUN-20261122-02 |
| [T-045](features/export/tickets/T-045-dang-xuat-huy-loi-roi-app.md) | Đang xuất, hủy, lỗi, rời app | `backlog` | chưa có | - | - |
| [T-046](features/export/tickets/T-046-xuat-xong-luu-vao-thu-vien-chia.md) | Xuất xong: lưu vào thư viện, chia sẻ, mở | `backlog` | chưa có | - | - |
| [T-047](features/export/tickets/T-047-quyen-luu-vao-thu-vien-tren-android.md) | Quyền lưu vào thư viện trên Android 9 trở xuống | `backlog` | chưa có | - | - |

Phản hồi: chưa có.

### lessons

Spec: [spec.md](features/lessons/spec.md) `confirmed`. Ticket: 6 `backlog`, 1 `on-staging`.

| Ticket | Tên | Trạng thái | Plan | Ước tính token | Phiên chạy |
| --- | --- | --- | --- | --- | --- |
| [T-048](features/lessons/tickets/T-048-noi-dung-bai-hoc-tren-r2-va.md) | Nội dung bài học trên R2 và kho tiến độ bài ở core | `on-staging` | `done` (bản 1) | 15M | RUN-20261122-02 |
| [T-049](features/lessons/tickets/T-049-soan-va-ve-noi-dung-bai-hoc.md) | Soạn và vẽ nội dung bài học, đưa 2 bài mẫu lên R2 staging | `backlog` | chưa có | - | - |
| [T-050](features/lessons/tickets/T-050-man-bai-hoc-thu-vien-loc-nhom.md) | Màn Bài học: thư viện, lọc nhóm, đang tải, lỗi | `backlog` | chưa có | - | - |
| [T-051](features/lessons/tickets/T-051-quang-cao-o-man-bai-hoc.md) | Quảng cáo ở màn Bài học | `backlog` | chưa có | - | - |
| [T-052](features/lessons/tickets/T-052-mo-bai-hoc-lam-do-va-quay.md) | Mở bài học, làm dở và quay lại | `backlog` | chưa có | - | - |
| [T-053](features/lessons/tickets/T-053-man-hoan-thanh-bai-hoc-sang-bai.md) | Màn Hoàn thành bài học, sang bài tiếp, xuất hoạt hình | `backlog` | chưa có | - | - |
| [T-054](features/lessons/tickets/T-054-bat-co-bai-hoc-tren-ban-staging.md) | Bật cờ bài học trên bản staging | `backlog` | chưa có | - | - |

Phản hồi: chưa có.

### loading

Spec: [spec.md](features/loading/spec.md) `confirmed`. Ticket: chưa có.

Phản hồi: chưa có.

### network

Spec: [spec.md](features/network/spec.md) `confirmed`. Ticket: chưa có.

Phản hồi: chưa có.

### onboarding

Spec: [spec.md](features/onboarding/spec.md) `confirmed`. Ticket: 1 `backlog`.

| Ticket | Tên | Trạng thái | Plan | Ước tính token | Phiên chạy |
| --- | --- | --- | --- | --- | --- |
| [T-015](features/onboarding/tickets/T-015-onboarding-4-trang-bat-buoc.md) | Onboarding 4 trang bắt buộc | `backlog` | chưa có | - | - |

Phản hồi: chưa có.

### projects

Spec: [spec.md](features/projects/spec.md) `confirmed`. Ticket: 10 `on-staging`, 2 `backlog`.

| Ticket | Tên | Trạng thái | Plan | Ước tính token | Phiên chạy |
| --- | --- | --- | --- | --- | --- |
| [T-001](features/projects/tickets/T-001-module-core-project-mo-hinh-du-an.md) | Module :core:project: mô hình dự án và định dạng lưu có phiên bản | `on-staging` | `done` (bản 1) | 15M | RUN-20261122-01 |
| [T-002](features/projects/tickets/T-002-kho-du-an-danh-sach-tao-doi.md) | Kho dự án: danh sách, tạo, đổi tên, nhân bản, xóa, đếm dự án | `on-staging` | `done` (bản 1) | 15M | RUN-20261122-02 |
| [T-003](features/projects/tickets/T-003-file-sao-luu-frameloop-ghi-va-doc.md) | File sao lưu .frameloop: ghi và đọc một hay nhiều dự án | `on-staging` | `done` (bản 1) | 15M | RUN-20261122-02 |
| [T-004](features/projects/tickets/T-004-ve-khung-ra-anh-gop-layer-nen.md) | Vẽ khung ra ảnh: gộp layer, nền, camera ảo, ảnh thu nhỏ | `on-staging` | `done` (bản 1) | 15M | RUN-20261122-01 |
| [T-006](features/projects/tickets/T-006-co-tinh-nang-va-khoa-remote-config.md) | Cờ tính năng và khóa Remote Config của 6 feature | `on-staging` | `done` (bản 1) | 5M | RUN-20261122-01 |
| [T-009](features/projects/tickets/T-009-man-danh-sach-du-an-thanh-dieu.md) | Màn danh sách dự án, thanh điều hướng của vỏ app, màn bắt đầu | `on-staging` | `done` (bản 1) | 15.2M | RUN-20261123-01 |
| [T-010](features/projects/tickets/T-010-quang-cao-o-danh-sach-du-an.md) | Quảng cáo ở danh sách dự án | `on-staging` | `done` (bản 1) | 5.0M | RUN-20261123-02 |
| [T-011](features/projects/tickets/T-011-man-tao-du-an.md) | Màn Tạo dự án | `on-staging` | `done` (bản 1) | 17.4M | RUN-20261123-02 |
| [T-012](features/projects/tickets/T-012-menu-du-an-mo-doi-ten-nhan.md) | Menu dự án: mở, đổi tên, nhân bản, xóa | `on-staging` | `done` (bản 2) | 5.0M | RUN-20261123-01, RUN-20261123-02 |
| [T-013](features/projects/tickets/T-013-sao-luu-mot-du-an-va-khoi.md) | Sao lưu một dự án và khôi phục từ file | `backlog` | chưa có | - | - |
| [T-014](features/projects/tickets/T-014-danh-sach-du-an-tren-dien-thoai.md) | Danh sách dự án trên điện thoại gập khi mở ra | `backlog` | chưa có | - | - |
| [T-056](features/projects/tickets/T-056-nut-quyen-rieng-tu-khi-co-cai-dat-tat.md) | Nút lựa chọn quyền riêng tư quảng cáo trên thanh trên khi cờ Cài đặt tắt | `on-staging` | `done` (bản 1) | 5.0M | RUN-20261123-02 |

Phản hồi: 1 `new`, 1 `planned`.
- Đang mở: [FB-20261123-01-thong-bao-khi-nhan-ban.md](features/projects/feedback/FB-20261123-01-thong-bao-khi-nhan-ban.md) `planned`, mức `nhe`: Đang nhân bản thì thông báo thứ hai chờ tới khi nhân bản xong
- Đang mở: [FB-20261127-01-mo-man-tao-du-an-bi-dong.md](features/projects/feedback/FB-20261127-01-mo-man-tao-du-an-bi-dong.md) `new`, mức `chan`: App bị đóng khi mở màn Tạo dự án trên bản staging

### settings

Spec: [spec.md](features/settings/spec.md) `confirmed`. Ticket: 2 `backlog`, 1 `on-staging`.

| Ticket | Tên | Trạng thái | Plan | Ước tính token | Phiên chạy |
| --- | --- | --- | --- | --- | --- |
| [T-005](features/settings/tickets/T-005-cai-dat-dung-chung-tren-may-mac.md) | Cài đặt dùng chung trên máy: mặc định dự án, lực bút, mốc nét vẽ đầu tiên | `on-staging` | `done` (bản 1) | 5M | RUN-20261122-01 |
| [T-016](features/settings/tickets/T-016-man-cai-dat.md) | Màn Cài đặt | `backlog` | chưa có | - | - |
| [T-017](features/settings/tickets/T-017-sao-luu-tat-ca-du-an-va.md) | Sao lưu tất cả dự án và khôi phục từ Cài đặt | `backlog` | chưa có | - | - |

Phản hồi: chưa có.

## 4. Phiên tự chạy

| Phiên | Trạng thái | Bắt đầu | Ticket | Bản staging |
| --- | --- | --- | --- | --- |
| [RUN-20261123-02](runs/RUN-20261123-02.md) | `finished` | 2026-11-27T05:29Z | T-012, T-010, T-011, T-056 | - |
| [RUN-20261123-01](runs/RUN-20261123-01.md) | `finished` | 2026-11-23 07:22 UTC | T-009, T-012 | 0.1.0-staging.5 (5) |
| [RUN-20261122-03](runs/RUN-20261122-03.md) | `finished` | 2026-11-23 01:18 UTC | T-007 | 0.1.0-staging.4 (4) |
| [RUN-20261122-02](runs/RUN-20261122-02.md) | `finished` | 2026-11-22T15:54Z | T-007, T-048, T-044, T-002, T-003 | 0.1.0-staging.3 (3) |
| [RUN-20261122-01](runs/RUN-20261122-01.md) | `finished` | 2026-11-22T07:27Z | T-006, T-005, T-001, T-004 | 0.1.0-staging.2 (2) |

## 5. Bài học và ADR

- Bài học ([lessons.md](lessons.md)): 10 bài; 10 `proposed`.
- ADR: 34; 34 `accepted`.

## 6. Sẵn sàng phát hành

<!-- Chỉ từ tài liệu trong repo. Mã quảng cáo thật, cấu hình production và tình trạng trên Google Play
không nằm trong repo: người phát hành kiểm theo docs/ops/release.md. -->

### Quảng cáo

- Bản production cần một mã quảng cáo thật cho mỗi khóa dưới đây (docs/ops/release.md, mục 1.2): `sample_native_small` (native_small), `open_app` (app_open), `open_app_inter` (interstitial), `native_combo` (native_full_screen), `resume` (app_open), `resume_inter` (interstitial), `native_onboarding` (native_step), `projects_empty_native` (native_medium), `projects_bottom` (native_small), `projects_grid` (native_in_collection), `restore_mrec` (banner_mrec), `create_inter` (interstitial), `new_project_native` (native_medium), `settings_bottom` (native_small), `editor_exit_inter` (interstitial), `editor_rw_layer` (rewarded), `editor_rw_brushes` (rewarded), `editor_rw_sounds` (rewarded), `export_options_mrec` (banner_mrec), `export_wait_mrec` (banner_mrec), `export_done_native` (native_medium), `export_interstitial` (interstitial), `export_rw_extra` (rewarded), `lessons_grid` (native_in_collection), `lessons_bottom` (native_small), `lesson_start_inter` (interstitial), `lesson_done_native` (native_medium), `lesson_next_inter` (interstitial), `banner` (khe thanh đáy), `native_collap` (khe thanh đáy).

### Kiểm thủ công chưa đánh dấu

38 mục trong 5 phiên. Người kiểm xong thì đánh dấu [x] trong báo cáo phiên.
- [RUN-20261123-02](runs/RUN-20261123-02.md): Ảnh chuẩn screenshot mới hay ghi lại (`feature/projects/src/test/screenshots/`): T-010 ghi lại `ProjectsScreen_{empty,list}_{light,dark,small}.png`, mới...
- [RUN-20261123-02](runs/RUN-20261123-02.md): Ảnh so sánh với thiết kế: `docs/features/projects/design/review/` (lần đầu commit). Rà soát nêu: native medium và thanh đáy khác thiết kế (thứ tự media, tiêu...
- [RUN-20261123-02](runs/RUN-20261123-02.md): Trong `ProjectsScreen_list_small.png` (T-010) và `ProjectsScreen_privacy_small.png` (T-056), khung thanh đáy quảng cáo giả đè lên hàng thẻ thứ ba: xem có đúng...
- [RUN-20261123-02](runs/RUN-20261123-02.md): Tiêu chí kiểm tra thủ công (máy thật, người): T-012 hai thông báo liên tiếp khi đang nhân bản với TalkBack (câu hỏi 13); T-010 khóa bật, tắt trên Firebase...
- [RUN-20261123-02](runs/RUN-20261123-02.md): Kịch bản emulator lỗi hay bỏ qua: `T-011-tao-du-an.txt` lỗi ở bước 9 vì app bị đóng khi mở màn Tạo dự án (crash, FB-20261127-01, mức `chan`); bản staging không...
- [RUN-20261123-02](runs/RUN-20261123-02.md): Phần ngoài mục 3.2 đã merge (ADR-0029): T-010 `docs/features/projects/design/review/` (ảnh so sánh, theo rà soát, PR #44); T-011 cùng thư mục (PR #45).
- [RUN-20261123-02](runs/RUN-20261123-02.md): Quyết định AI đã tự làm: 7, xem mục trên.
- [RUN-20261123-02](runs/RUN-20261123-02.md): Điểm rà soát mức ghi chú: T-012 có thể chớp một khung hình "Duplicating…" khi thông báo thay nhau; T-010 đối chứng ở `Empty` của `snackbar_aboveBottomBar` chỉ...
- [RUN-20261123-02](runs/RUN-20261123-02.md): Nhánh thừa trên GitHub: `claude/T-012-snackbar-nhan-ban` (tạo nhầm tên ở bước 1 của T-012, không có commit riêng; lệnh xóa bị từ chối "remote end hung up") và...
- [RUN-20261123-02](runs/RUN-20261123-02.md): Đề xuất về dự án mẫu (`docs/ops/from-app.md`): `PrivacyOptionsButtonTest` trong `:core:ads`, biến thể biểu tượng của nút quyền riêng tư (PR #46); sàn media của...
- [RUN-20261123-01](runs/RUN-20261123-01.md): Ảnh chuẩn screenshot mới của T-009:...
- [RUN-20261123-01](runs/RUN-20261123-01.md): Ảnh chuẩn screenshot mới của T-012:...
- [RUN-20261123-01](runs/RUN-20261123-01.md): Ảnh so sánh với thiết kế: `scripts/design-compare.sh projects` không commit (`design/review/` không có trong mục 3.2 của hai plan). Lệch: empty_light 11.6%,...
- [RUN-20261123-01](runs/RUN-20261123-01.md): T-012, điểm nên sửa chưa sửa: đang nhân bản, chạm thẻ ra "Coming soon", rồi trong 4 giây có thông báo thứ hai (chạm thẻ khác, lỗi đổi tên hay xóa): thông báo...
- [RUN-20261123-01](runs/RUN-20261123-01.md): T-012: sheet góc vuông theo câu trả lời câu 10 của plan, nhưng ảnh PNG thiết kế `ProjectsScreen_menu_*` có bo hai góc trên khoảng 28 dp (`context/` không ghi...
- [RUN-20261123-01](runs/RUN-20261123-01.md): T-009: nút "New animation", "Try a free lesson" theo `PrimaryButton` chung (52 dp, bo 16) so với thiết kế 48 dp, bo 14; icon cài đặt đặc so với nét viền; hình...
- [RUN-20261123-01](runs/RUN-20261123-01.md): Tiêu chí kiểm tra thủ công: T-009 KB-2 "100 dự án mở và cuộn không giật" (máy thật); T-012 nền tối của sheet thật, bàn phím khi đổi tên, nhấn giữ trên máy (cần...
- [RUN-20261123-01](runs/RUN-20261123-01.md): Câu khẳng định cũ viết lại (ADR-0031): T-009, đúng câu mục 3.5 viết sẵn: `AppLaunchTest.appStarts_andReachesStartScreen` chờ `projects_screen` thay...
- [RUN-20261123-01](runs/RUN-20261123-01.md): Quyết định AI đã tự làm: 6, xem mục trên.
- [RUN-20261123-01](runs/RUN-20261123-01.md): Điểm rà soát mức ghi chú: T-009 `ProjectsRoute` thu hiệu ứng không theo vòng đời (xem lại ở T-018 khi có đích thật); T-012 host chung giờ chỉ ẩn thông báo...
- [RUN-20261123-01](runs/RUN-20261123-01.md): Tệp `docs/features/projects/plans/T-012-menu-du-an.md` trên nhánh chính có khối "Quyết định / Người duyệt / Ghi chú" của mục 9 bị chèn lặp sau gần như mọi đoạn...
- [RUN-20261123-01](runs/RUN-20261123-01.md): Nhánh `claude/T-009-man-danh-sach-du-an`, `claude/T-012-menu-du-an` không xóa được sau merge (`merge-if-green.sh` báo chú ý): người xóa tay.
- [RUN-20261122-03](runs/RUN-20261122-03.md): Tiêu chí kiểm tra thủ công (máy thật, người): T-007, kế hoạch bật trên Firebase (người): chạy `scripts/ads-plan.py --keep <file cũ>`, xem 22 dòng mới ở sheet...
- [RUN-20261122-03](runs/RUN-20261122-03.md): Điểm rà soát mức ghi chú: T-007, KDoc của `EXPORT_RW_EXTRA` ghi "(q. 14)" chưa nói spec export; KDoc cũ của `AdsControllerFirstOpenOnlyTest` và...
- [RUN-20261122-03](runs/RUN-20261122-03.md): T-007, `core/ads/build.gradle.kts` là file của dự án mẫu (ngoại lệ ADR-1001): người mở issue `from-app` ở repo dự án mẫu trong vòng một tuần, đề xuất nâng heap...
- [RUN-20261122-03](runs/RUN-20261122-03.md): Nhánh `claude/T-007-vi-tri-quang-cao` chưa xóa: `merge-if-green.sh` báo "không xóa được nhánh sau khi merge"; người xóa tay (L-092).
- [RUN-20261122-03](runs/RUN-20261122-03.md): Bản production cần 22 mã đơn vị quảng cáo thật cho các vị trí mới (`scripts/release.sh` đòi); MREC `restore_mrec`, `export_options_mrec`, `export_wait_mrec`...
- [RUN-20261122-03](runs/RUN-20261122-03.md): Bản staging: sau T-007, `StagingRemoteValuesModule` bật mọi vị trí mới, nên 4 khe rewarded và 5 khe interstitial nạp sẵn quảng cáo thử ngay lúc mở app dù chưa...
- [RUN-20261122-02](runs/RUN-20261122-02.md): T-007 dừng: quyết cách sửa bộ nhớ test của `:core:ads` trên CI (plan T-007 phiên bản 2, câu hỏi 2: nâng `maxHeapSize` của test JVM trong `build-logic/` bằng...
- [RUN-20261122-02](runs/RUN-20261122-02.md): Tiêu chí kiểm tra thủ công (máy thật, người): (2 dòng con)
- [RUN-20261122-02](runs/RUN-20261122-02.md): Kịch bản emulator lỗi hay bỏ qua: xem mục Kiểm thử trên máy ảo
- [RUN-20261122-02](runs/RUN-20261122-02.md): Quyết định AI đã tự làm: 6, xem mục trên (sửa test ở bước 4 của T-002 và T-003; lệch ở `PackExtractor` của T-048)
- [RUN-20261122-02](runs/RUN-20261122-02.md): Điểm rà soát mức ghi chú: (6 dòng con)
- [RUN-20261122-02](runs/RUN-20261122-02.md): Năm nhánh task (`claude/T-048-core-lessons`, `claude/T-044-xuat-gif-png-watermark`, `claude/T-002-kho-du-an`, `claude/T-003-file-sao-luu`, và...
- [RUN-20261122-01](runs/RUN-20261122-01.md): Tiêu chí kiểm tra thủ công (máy thật, người): (3 dòng con)
- [RUN-20261122-01](runs/RUN-20261122-01.md): Quyết định AI đã tự làm: 3, xem mục trên (hai lần sửa test ở bước 4 của T-001 và T-004)
- [RUN-20261122-01](runs/RUN-20261122-01.md): Điểm rà soát mức ghi chú: (5 dòng con)
- [RUN-20261122-01](runs/RUN-20261122-01.md): Bốn nhánh task (`claude/T-006-co-tinh-nang`, `claude/T-005-cai-dat-dung-chung`, `claude/T-001-core-project`, `claude/T-004-ve-khung-ra-anh`) không xóa được sau...

### Ticket chưa lên staging

42 ticket.
- editor: T-008 `backlog`, T-018 `backlog`, T-019 `backlog`, T-020 `backlog`, T-021 `backlog`, T-022 `backlog`, T-023 `backlog`, T-024 `backlog`, T-025 `backlog`, T-026 `backlog`, T-027 `backlog`, T-028 `backlog`, T-029 `backlog`, T-030 `backlog`, T-031 `backlog`, T-032 `backlog`, T-033 `backlog`, T-034 `backlog`, T-035 `backlog`, T-036 `backlog`, T-037 `backlog`, T-038 `backlog`, T-039 `backlog`, T-040 `backlog`, T-041 `backlog`, T-055 `backlog`
- export: T-042 `backlog`, T-043 `backlog`, T-045 `backlog`, T-046 `backlog`, T-047 `backlog`
- lessons: T-049 `backlog`, T-050 `backlog`, T-051 `backlog`, T-052 `backlog`, T-053 `backlog`, T-054 `backlog`
- onboarding: T-015 `backlog`
- projects: T-013 `backlog`, T-014 `backlog`
- settings: T-016 `backlog`, T-017 `backlog`

### Phản hồi đang mở

- FB-20261123-01 `planned`, mức `nhe`: Đang nhân bản thì thông báo thứ hai chờ tới khi nhân bản xong
- FB-20261127-01 `new`, mức `chan`: App bị đóng khi mở màn Tạo dự án trên bản staging

### Issue đang mở

- [Mọi issue đang mở](https://github.com/VGL-App/draw-animation/issues?q=is%3Aissue+is%3Aopen), [issue main-broken](https://github.com/VGL-App/draw-animation/issues?q=is%3Aissue+is%3Aopen+label%3Amain-broken)
