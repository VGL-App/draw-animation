# SỔ BÀI HỌC

Sổ này ghi những điều nhóm và AI học được khi làm việc, và đã làm gì với chúng. Đây là đầu vào của buổi retro sau mỗi phiên tự chạy (auto-run.md, mục 6).

## Cách ghi

- Mỗi bài học một mục, mã `L-NNN` tăng dần. Chỉ thêm mục mới hoặc cập nhật dòng **Trạng thái**, **Áp dụng**, **Kiểm chứng** của mục cũ; không sửa phần mô tả.
- Trạng thái:
  - `proposed`: AI hoặc người vừa ghi, chưa ai quyết định.
  - `accepted`: retro đồng ý làm, chưa làm.
  - `applied`: đã làm, ghi PR ở dòng **Áp dụng**.
  - `verified`: phiên sau cho thấy cải tiến có hiệu quả, ghi bằng chứng ở dòng **Kiểm chứng**.
  - `rejected`: retro quyết định không làm, ghi lý do.
- Loại: `quy trình` (auto-run, docs/CLAUDE.md, mẫu tài liệu), `môi trường` (cloud, CI, script), `quy tắc code` (CLAUDE.md), `plan` (cách viết plan, test), `rủi ro` (thang điểm).
- Phạm vi: `chung` (đúng với mọi app, làm trong repo dự án mẫu trước), `app` (chỉ app này).
- Chỉ người chuyển trạng thái sang `accepted`, `rejected` hay `verified`. AI chỉ ghi `proposed`, và ghi `applied` khi PR cải tiến đã merge.

Bài học `chung` ghi ở app thì được đưa về dự án mẫu qua issue `from-app` (`docs/ops/from-app.md`). Ở dự án mẫu, bài học từ app ghi nguồn là app và số issue. Chiều ngược lại, các app đã tạo trước đó nhận cải tiến của dự án mẫu qua PR đồng bộ dựng bằng `scripts/template-sync.sh` (`docs/ops/to-app.md`, ADR-0030).

## Bài học

<!-- Các bài học ghi dưới dòng này. -->

Bài học trước ngày 2026-11-20 nằm trong sổ bài học của repo dự án mẫu (commit 936eb3dc).

### L-001. Test "chạy trên dispatcher được tiêm" không dùng scheduler riêng bên trong `runTest`

- **Trạng thái:** proposed. **Loại:** plan. **Phạm vi:** chung.
- **Nguồn:** RUN-20261122-01, T-001 (PR #14) và T-004 (PR #15).
- **Chuyện gì:** hai plan viết test kiểm thao tác chạy trên dispatcher được tiêm bằng một `StandardTestDispatcher()` có scheduler riêng, gọi bên trong `runTest`. kotlinx-coroutines-test từ chối: `IllegalStateException: Detected use of different schedulers`. Test không chạy được như plan viết; phiên sửa test sau commit test ở cả hai task (bước 4, chi tiết test), giữ câu khẳng định, kèm lần đối chứng.
- **Bằng chứng:** nhật ký plan T-001 và T-004, dòng Lệch so với plan; mô tả PR #14, #15.
- **Đề xuất:** `CLAUDE.md`, mục Viết một feature mới, ý Test: test kiểm việc chạy trên dispatcher được tiêm dùng `StandardTestDispatcher(testScheduler)` của chính `runTest`, hoặc một dispatcher hàng đợi tự viết; không dùng dispatcher có scheduler riêng bên trong `runTest`. `docs/CLAUDE.md`, mục Khi viết plan: test dạng này chạy thử bằng một test tạm trước khi duyệt.
- **Áp dụng:**
- **Kiểm chứng:**

### L-002. Bảng tra lỗi chưa có dòng cho lỗi Robolectric không tải được android-all

- **Trạng thái:** proposed. **Loại:** môi trường. **Phạm vi:** chung.
- **Nguồn:** RUN-20261122-01, T-001 (PR #14).
- **Chuyện gì:** `pr-check` đỏ ở `build-and-test`. Nhiều lớp test Robolectric của nhiều module báo `java.lang.AssertionError at MavenArtifactFetcher.java:130`, gây bởi `java.io.IOException at HttpURLConnection.java:2059`. `scripts/failure-kind.sh --playbook` trả `khong-co`; phiên tự xếp là chập chờn, chạy lại job đỏ một lần thì xanh.
- **Bằng chứng:** lần chạy 45008324763 của `pr-check` (lần đầu đỏ, lần chạy lại xanh), 2026-11-22.
- **Đề xuất:** thêm dòng vào `docs/ops/failure-playbook.md` cho mẫu `MavenArtifactFetcher` cùng `IOException`, loại `chay-lai`, kèm tệp log trong `scripts/tests/playbook/`.
- **Áp dụng:**
- **Kiểm chứng:**

### L-003. Lỗi quyền GitHub App của routine chỉ lộ ra khi phiên đẩy lần đầu

- **Trạng thái:** proposed. **Loại:** quy trình. **Phạm vi:** chung.
- **Nguồn:** RUN-20261122-01, đầu phiên.
- **Chuyện gì:** `start-run` kích hoạt routine thành công, nhưng lần đẩy nhánh báo cáo đầu tiên bị từ chối 403 ("Claude doesn't have GitHub access to VGL-App/draw-animation for your organization"), dù `gh api` vẫn báo quyền push. Phiên dừng, báo người; người cấu hình lại GitHub, phiên chạy tiếp sau khoảng 6 phút.
- **Bằng chứng:** dòng thời gian của RUN-20261122-01, 07:27 tới 07:33.
- **Đề xuất:** `docs/ops/staging-and-runs.md`, mục dựng routine: sau khi dựng, chạy một phiên thử đẩy một nhánh tạm để thấy lỗi quyền GitHub App trước lần kích hoạt thật; hoặc `start-run.yml` thêm bước kiểm tương đương nếu làm được.
- **Áp dụng:**
- **Kiểm chứng:**
