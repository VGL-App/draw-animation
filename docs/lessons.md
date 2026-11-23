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

### L-004. Thêm vị trí quảng cáo làm test JVM của `:core:ads` hết bộ nhớ trên CI

- **Trạng thái:** proposed. **Loại:** môi trường. **Phạm vi:** chung.
- **Nguồn:** RUN-20261122-02, T-007 (PR #20).
- **Chuyện gì:** T-007 thêm 22 vị trí vào `AdPlacement`. `scripts/check.sh` qua trên máy của phiên (Java 21), nhưng `pr-check` (Java 17) đỏ hai lần ở `:core:ads:testDebugUnitTest`: mỗi lần một hay hai screenshot test có sẵn khác nhau ném `java.lang.OutOfMemoryError at DataBufferInt.java:75`. `AdsController` dựng chỗ nạp sẵn cho mọi vị trí, nên mỗi controller của hàng trăm test giữ thêm đối tượng; mọi test của module chạy trong một JVM với heap mặc định (không có `maxHeapSize` trong `build-logic/`); screenshot test có hiệu ứng chạy mãi vẽ tới sát giới hạn bộ nhớ rồi mới chụp. Cách sửa nằm ở `build-logic/` nên phiên dừng task. Bảng tra lỗi không có dòng cho mẫu này (`khong-co`), và máy của phiên không có Java 17 để tái hiện.
- **Bằng chứng:** lần chạy 45043991143 của `pr-check` (lần 1 và lần chạy lại đều đỏ); nhật ký plan T-007; T-048, T-044, T-002 cùng phiên xanh trên `pr-check` với `:core:ads` của nhánh chính.
- **Đề xuất:** dự án mẫu đặt `maxHeapSize` cho task `Test` trong convention plugin và đổi các screenshot test có hiệu ứng chạy mãi của `:core:ads` sang dừng đồng hồ; `docs/ops/ads-guide.md`, mục thêm vị trí: chạy test `:core:ads` với Java của CI khi thêm nhiều vị trí; thêm dòng `OutOfMemoryError` của test JVM vào `docs/ops/failure-playbook.md` (loại `that`, gợi ý xem heap của test), kèm tệp log trong `scripts/tests/playbook/`. Cân nhắc cho `scripts/cloud-setup.sh` cài JDK cùng phiên bản với CI để `check.sh` của phiên giống CI.
- **Áp dụng:** issue `from-app` VGL-Studio/app-project-template#572 (2026-11-23), sau khi app sửa riêng `core/ads/build.gradle.kts` (ADR-1001, T-007 PR #20).
- **Kiểm chứng:**

### L-005. Plan ghi `advanceUntilIdle()` cho luồng thu trong `backgroundScope`

- **Trạng thái:** proposed. **Loại:** plan. **Phạm vi:** chung.
- **Nguồn:** RUN-20261122-02, T-002 (PR #23).
- **Chuyện gì:** mục 4 của plan T-002 viết hai test thu danh sách dự án trong `backgroundScope` rồi chờ bằng `advanceUntilIdle()`. Với kotlinx-coroutines-test 1.10, `advanceUntilIdle()` dừng khi chỉ còn việc nền, nên lần đọc đĩa không chạy và hai test đỏ cả khi code đúng. Phiên con đổi sang `runCurrent()` sau commit test (bước 4, chi tiết test), giữ mọi câu khẳng định, kèm lần chạy trên khung rỗng.
- **Bằng chứng:** nhật ký plan T-002, dòng Lệch so với plan; mô tả PR #23.
- **Đề xuất:** `CLAUDE.md`, mục Viết một feature mới, ý Test: luồng thu trong `backgroundScope` thì chờ bằng `runCurrent()` (hay `advanceTimeBy` khi có `delay`), không bằng `advanceUntilIdle()`. `docs/CLAUDE.md`, mục Khi viết plan: test dạng này chạy thử bằng một test tạm.
- **Áp dụng:**
- **Kiểm chứng:**

### L-006. Mục 3.1 của `auto-run.md` chưa nói rõ plan phiên bản mới khi task dừng nằm ở đâu

- **Trạng thái:** proposed. **Loại:** quy trình. **Phạm vi:** chung.
- **Nguồn:** RUN-20261122-02, T-007.
- **Chuyện gì:** mục Chuẩn bị cho người khi task dừng nói plan phiên bản mới ở trạng thái `draft` nằm "ngay sau mục nhật ký của lần dừng", nhưng cũng nói "plan vẫn ở trạng thái `stopped` cho tới khi người duyệt phiên bản mới". Phiên phải đoán: sửa ngay trong file plan (tăng `version`, giữ `status: stopped`, mục 9 tách khối "Phiên bản 1:", "Phiên bản 2:" như `plan-check.py` đọc), không tạo file mới. Chưa có tiền lệ trong repo.
- **Bằng chứng:** plan T-007 phiên bản 2 trên nhánh báo cáo của RUN-20261122-02.
- **Đề xuất:** `docs/auto-run.md`, mục Chuẩn bị cho người khi task dừng: ghi rõ phiên bản mới sửa ngay trong file plan, `status` giữ `stopped`, mục nào thêm (đoạn "Phiên bản N" ở mục 1, "Cách tiếp tục" ở mục 3.1, khối "Phiên bản N:" ở mục 9); `docs/templates/plan.md` có ví dụ.
- **Áp dụng:**
- **Kiểm chứng:**

### L-007. Commit duyệt đợt chèn lặp khối quyết định vào khắp tệp plan

- **Trạng thái:** proposed. **Loại:** quy trình. **Phạm vi:** chung.
- **Nguồn:** RUN-20261123-01, T-012.
- **Chuyện gì:** khi ghi trạng thái duyệt cho đợt 20261123-02, khối "Quyết định / Người duyệt / Ghi chú" của mục 9 bị chèn sau gần như mọi đoạn của plan T-012 (70 lần, kể cả trong khối code); plan T-009 cùng commit thì đúng. `plan-check.py` và `run-check.py` đều qua nên lỗi lên nhánh chính và tới phiên tự chạy; phiên con và rà soát độc lập phải lọc tệp mới đọc được.
- **Bằng chứng:** `git show f5bc141:docs/features/projects/plans/T-012-menu-du-an.md | grep -c 'Người duyệt'` ra 70; bản trước commit đó không có dòng nào.
- **Đề xuất:** `scripts/plan-check.py` báo lỗi khi dòng `**Quyết định:**` hay `**Người duyệt:**` xuất hiện ngoài mục 9, hay nhiều hơn số khối phiên bản của mục 9; người duyệt dọn tệp plan T-012 trong một PR tài liệu.
- **Áp dụng:**
- **Kiểm chứng:**
