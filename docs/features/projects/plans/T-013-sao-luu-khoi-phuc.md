---
ticket: T-013
title: Sao lưu một dự án và khôi phục từ file
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 18.7M        # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [ads, analytics] # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-013: SAO LƯU MỘT DỰ ÁN VÀ KHÔI PHỤC TỪ FILE

## 1. Tóm tắt

Nối mục "Back up to a file" của menu dự án và nút nhập file trên thanh trên của `:feature:projects` với `ProjectBackup` có sẵn của `:core:project` (plan T-003: `export`, `restore` có tiến độ, hủy được, không để lại dự án dở). Bộ chọn file của hệ thống (`ActivityResultContracts.CreateDocument`, `OpenDocument`) mở từ `ProjectsRoute` qua một lớp mỏng `ProjectsFilePickers` để test thay được; `ProjectsViewModel` giữ việc sao lưu (thông báo "Backup saved", lỗi có "Retry", dấu hiệu đang ghi) và việc khôi phục (`RestoreUi`: đang khôi phục, lỗi), ghi `backup_export`, `backup_restore` khi thành công. Hộp thoại đang khôi phục có `restore_mrec` qua `ProjectsAdSlots`, nội dung cuộn được ở 360 × 640. Có lối vào cho T-017 (`NavController.restoreBackupInProjects`), không có chỗ gọi trong `:app` ở task này. Kiểm chứng bằng unit test ViewModel, test giao diện Robolectric, screenshot test cùng tên bốn ảnh thiết kế `restoring`, `restore-error`.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/projects/spec.md`:

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-8 | Bấm "Back up to a file" thì bộ chọn file mở để lưu, tên gợi ý là tên dự án + ".frameloop"; không hỏi quyền | `CreateDocument("application/octet-stream")` với `BackupFileNames.forProject(name)`; bộ chọn của hệ thống không cần quyền (spec mục 6) |
| KB-8 | Lưu xong thì thông báo ngắn "Backup saved", tự ẩn, không nút | `SnackbarDuration.Short`, không `actionLabel`; ghi `backup_export` (`source=projects`, `project_count=1`) |
| KB-8 | Khôi phục file vừa sao lưu được dự án giống hệt | Đã kiểm ở `:core:project` (`DefaultProjectBackupTest`, plan T-003); ở đây kiểm ViewModel truyền đúng id và uri xuống `ProjectsRepository` |
| KB-8 | Đóng bộ chọn không lưu thì không có gì xảy ra | Kết quả `null` của launcher: không gọi repository, không thông báo |
| KB-8, đặc biệt | Ghi file thất bại thì "Couldn't save the backup. Try again." có "Retry"; dự án không đổi | Mọi `BackupExportResult.Failure` cùng một lời; "Retry" ghi lại vào cùng uri (quyền tạm của bộ chọn còn trong process); `SnackbarDuration.Long` |
| KB-8, đặc biệt | Dự án lớn thì có dấu hiệu đang ghi, người dùng vẫn ở danh sách | Thông báo không tự ẩn "Backing up “<tên>”…" theo đúng mẫu "Duplicating…" của T-012 (host riêng, nhường thông báo khác); chữ chưa có trong spec (mục 8, câu 1) |
| KB-9 | Bấm nút nhập thì bộ chọn file mở để chọn; không hỏi quyền | `OpenDocument` với `arrayOf("*/*")` (đuôi `.frameloop` không có kiểu MIME đăng ký; Drive có thể trả kiểu khác) |
| KB-9 | Chọn file hợp lệ thì hộp thoại "Restoring “<tên>”", "Keep the app open. This takes a few seconds.", thanh tiến độ, "<xong> of <tổng> frames", phần trăm, "Cancel" | `RestoreUi.Restoring` lấy từ `RestoreProgress` của core; phần trăm = `done * 100 / total` làm tròn xuống, `total = 0` thì 100% sau khi xong và 0% trước đó |
| KB-9 | File nhiều dự án thì tiêu đề ghi số dự án; tiến độ theo tổng khung | `BackupSummary.projectName == null` thì tiêu đề "Restoring <n> projects" (plurals; chữ chưa có nguyên văn trong spec, mục 8, câu 2) |
| KB-9 | Xong thì hộp thoại đóng, dự án mới đứng đầu, mở được | Core đặt `updatedAtMillis = now - index` nên dự án khôi phục đứng đầu; ViewModel chờ danh sách có id đầu tiên (giới hạn `SCROLL_WAIT_MILLIS`) rồi gửi `ScrollToTop`, như nhân bản |
| KB-9 | "Cancel" dừng, đóng hộp thoại, không còn dự án dở | Hủy `Job` của lần khôi phục; core dọn thư mục `.restoring-*`; không ghi sự kiện |
| KB-9 | Đóng bộ chọn không chọn thì không có gì | Kết quả `null` |
| KB-9 | Khôi phục từ trạng thái trống thì chuyển sang có dự án | Tự có từ `repository.items`; có test |
| KB-9, đặc biệt | Dự án còn trong danh sách thì tạo dự án mới, không ghi đè | Core luôn cấp id mới; test ViewModel kiểm danh sách có cả hai |
| KB-9, đặc biệt | Chạm ngoài không đóng; nút quay lại như "Cancel" | `DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = true, usePlatformDefaultWidth = false)`, `onDismissRequest = onRestoreCancel` |
| KB-9, đặc biệt | 360 × 640: nội dung cuộn được, "Cancel" luôn tới được | Phần trên (biểu tượng, tiêu đề, lời, tiến độ, quảng cáo) trong `verticalScroll`, hàng nút ghim đáy hộp thoại (spec câu 13) |
| KB-9, đặc biệt | Rời app rồi quay lại thì vẫn chạy; app bị đóng thì không còn dự án dở | Việc chạy trong `viewModelScope` (sống qua đổi cấu hình và lúc app ở nền khi process còn); process chết thì `DefaultProjectRepository` gọi `deleteAbandonedImports()` lúc mở lại (T-002, T-003); ViewModel không lưu việc đang chạy vào trạng thái khôi phục được |
| KB-9, đặc biệt | Hết chỗ thì dừng, không dự án dở, hộp thoại lỗi "Not enough space on your phone. Free up some space and try again." | `RestoreFailure.NO_SPACE` → `RestoreUi.Error(NO_SPACE)` |
| KB-10 | File không phải bản sao lưu hay hỏng: "Can't open this file", lời của spec, "Close", "Choose another file" | `NOT_A_BACKUP` → `RestoreUi.Error(NOT_A_BACKUP)` |
| KB-10 | "Close" đóng, danh sách giữ nguyên; "Choose another file" mở lại bộ chọn | `onRestoreErrorClose`, `onRestoreChooseAnother` (đóng lỗi rồi gửi `ProjectsEffect.PickRestoreFile` với cùng `source`) |
| KB-10 | Thất bại giữa chừng không để dự án dở | Core bảo đảm; test ViewModel kiểm danh sách không đổi sau `Failure` |
| KB-10, đặc biệt | File của bản mới hơn: "This backup was made with a newer version of Frameloop. Update the app and try again." | `NEWER_VERSION`; cùng tiêu đề và hai nút (spec câu 12: "dùng cùng hộp thoại, đổi lời") |
| KB-12 | Gập mở máy giữ việc khôi phục đang chạy và hộp thoại | Trạng thái ở ViewModel; có test dựng lại activity |
| Phạm vi ticket | Khôi phục gọi từ Cài đặt dùng cùng hộp thoại (cho T-017) | `NavController.restoreBackupInProjects(uri)` đặt uri vào `savedStateHandle` của mục `ProjectsDestination`; `projectsScreen` đọc một lần, xóa khóa, gọi `startRestore(uri, RestoreSource.SETTINGS)`; T-017 gọi hàm này từ `:app` |

Thiết kế tham chiếu: `features/projects/design/README.md`, các ảnh trong `design/screens/`: `ProjectsScreen_restoring_light`, `ProjectsScreen_restoring_dark`, `ProjectsScreen_restore-error_light`, `ProjectsScreen_restore-error_dark`; số liệu ở `design/context/` cùng tên (hộp thoại đang khôi phục 348 dp, lề 24/24/16/24, cách nhau 16, bo góc 20, nền `surfaceContainerHigh`, lớp tối 0.55; hộp thoại lỗi 328 dp, nút "Close" chữ `primary`, nút "Choose another file" nền `primary`). Thông báo sao lưu xong, lỗi, đang ghi: thông báo ngắn chuẩn, không có frame riêng (spec mục 4).

ADR liên quan: ADR-0001 (feature chỉ phụ thuộc core), ADR-0003 (theo dõi cờ), ADR-0019 (log), ADR-0020 (screenshot theo thiết kế), ADR-0027 (sự kiện), ADR-0029, ADR-0031 (mục 3.5), ADR-0045 (quảng cáo qua `:core:ads`, MC-13).

## 3. Giải pháp

### 3.1. Cách làm

**Dữ liệu.** `ProjectsRepository` thêm hai hàm, `DefaultProjectsRepository` nhận `ProjectBackup` (Hilt đã cung cấp ở `BackupModule` của `:core:project`) và đổi chuỗi uri thành `Uri` bằng `Uri.parse`:

```kotlin
/** Writes the project to the file the user chose ([target] is the picker's uri as a string). Never throws IOException. */
suspend fun backUp(id: ProjectId, target: String): BackupExportResult
/** Restores every project of [source] as new projects (`ProjectBackup.restore`). Cancelling stops it, leaving nothing. */
suspend fun restore(source: String, onProgress: suspend (RestoreProgress) -> Unit): RestoreResult
```

ViewModel và test ViewModel chỉ thấy chuỗi, nên test ViewModel vẫn là JUnit thường (không cần `android.net.Uri`).

**Bộ chọn file.** `ProjectsFilePickers` (file mới `FilePickers.kt`) là interface hai hàm `createBackupFile(suggestedName: String)` và `openBackupFile()`; kết quả trả về qua callback `onBackupFileCreated(String?)`, `onBackupFileOpened(String?)` mà `ProjectsRoute` truyền vào `rememberSystemFilePickers(...)`. Bản thật dùng `rememberLauncherForActivityResult(CreateDocument(BackupFileNames.BACKUP_MIME_TYPE))` và `OpenDocument()` với `arrayOf("*/*")`. `ProjectsRoute` nhận `filePickers: (onCreated, onOpened) -> ProjectsFilePickers` có mặc định là bản thật; test truyền bản giả ghi lại lời gọi và trả uri bằng tay. Dự án đang chờ sao lưu (id, tên) giữ trong `rememberSaveable` của `ProjectsRoute` dưới dạng chuỗi (`id.value` và tên, hai `String?`, lưu được trong `Bundle` không cần `Saver` riêng) lúc bộ chọn mở, để kết quả về sau khi activity dựng lại vẫn biết dự án nào; nguồn của lần mở bộ chọn khôi phục (`projects` hay `settings`, khi "Choose another file") cũng giữ ở đó.

**Sao lưu (KB-8).** Luồng: `onMenuBackUp` giữ nguyên (đóng menu, gửi `ProjectsEffect.BackUpProject(id, name)`); `ProjectsRoute` nhận hiệu ứng này thì gọi `pickers.createBackupFile(BackupFileNames.forProject(name))` thay vì gọi callback hay hiện "Coming soon". Kết quả: `null` thì thôi; có uri thì `viewModel.onBackupFileChosen(id, name, uri)`:

- Một lần sao lưu một lúc: đang có `backingUpName` thì bỏ qua lời gọi mới (như nhân bản), log `Backup ignored: one is running`.
- `ProjectsMenuState` thêm `backingUpName: String?`; trong lúc ghi, màn hiện "Backing up “<tên>”…" bằng host riêng như "Duplicating…" (cùng `ProjectsSnackbars`, host thứ ba nhường mọi thông báo khác; khi cả hai cùng chạy thì "Duplicating…" hiện trước, "Backing up…" chờ, vì một host một lúc).
- `Success` → `ProjectsEffect.ShowMessage(ProjectsMessage.BACKUP_SAVED)`, `analytics.log(AnalyticsEvent("backup_export", mapOf("source" to "projects", "project_count" to "${result.projectCount}")))`, log `Backup done`.
- `Failure` → `ProjectsEffect.ShowBackupFailed`, giữ `lastFailedBackup = (id, name, uri)`; log `Backup failed: <reason>`. Không ghi sự kiện. `ProjectsRoute` hiện "Couldn't save the backup. Try again." với `actionLabel = "Retry"`, `SnackbarDuration.Long`; `SnackbarResult.ActionPerformed` → `viewModel.onBackupRetry()` (ghi lại vào cùng uri; không còn `lastFailedBackup` thì bỏ qua).
- Hằng sự kiện trong `companion object` của ViewModel: `EVENT_BACKUP_EXPORT = "backup_export"`, `EVENT_BACKUP_RESTORE = "backup_restore"`, `PARAM_SOURCE = "source"`, `PARAM_PROJECT_COUNT = "project_count"`; giá trị nguồn từ `RestoreSource`/hằng `SOURCE_PROJECTS = "projects"`, `SOURCE_SETTINGS = "settings"` (`scripts/ads-plan.py` đọc hằng `EVENT_`).

**Khôi phục (KB-9, KB-10).** `ProjectsUiState.kt` thêm:

```kotlin
enum class RestoreSource { PROJECTS, SETTINGS }
enum class RestoreErrorKind { NOT_A_BACKUP, NEWER_VERSION, NO_SPACE, WRITE_FAILED }
sealed interface RestoreUi {
    data object None : RestoreUi
    /** [projectName] null and [projectCount] above 1: a file of several projects. [summaryKnown] false until the manifest is read. */
    data class Restoring(val projectName: String?, val projectCount: Int, val doneFrames: Int, val totalFrames: Int, val summaryKnown: Boolean) : RestoreUi
    data class Error(val kind: RestoreErrorKind, val source: RestoreSource) : RestoreUi
}
```

`ProjectsViewModel.restoreState: StateFlow<RestoreUi>` (một `MutableStateFlow`, không phụ thuộc người theo dõi, nên việc chạy không dừng khi màn không thu thập). Hàm:

- `startRestore(source: String, origin: RestoreSource)`: đang `Restoring` thì bỏ qua; đặt `Restoring(summaryKnown = false, …)` ngay (hộp thoại hiện ngay, có "Cancel", trước khi đọc xong manifest, vì đọc file trên Drive có thể chậm; mục 8, câu 3), rồi `viewModelScope.launch { repository.restore(source) { p -> _restoreState.value = Restoring(p.summary.projectName, p.summary.projectCount, p.doneFrames, p.summary.totalFrames, true) } }`. `onProgress` chạy trên luồng IO: chỉ gán `MutableStateFlow`, an toàn luồng.
- `Success(ids)` → `None`; `backup_restore` (`source` = `projects`/`settings`, `project_count = ids.size`); log `Restore done: <n> projects`; rồi chờ `withTimeoutOrNull(SCROLL_WAIT_MILLIS) { items.firstOrNull { it.firstOrNull()?.id in ids } }` và gửi `ScrollToTop` khi thấy. Dùng `firstOrNull`, không `first`: luồng kết thúc sớm (fake hay luồng hữu hạn) trả `null` thay vì ném `NoSuchElementException` (bài học của FB-20261127-01).
- `Failure(reason)` → `Error(kind, origin)`; log `Restore failed: <reason>`; không ghi sự kiện.
- `onRestoreCancel()`: hủy `Job`, đặt `None`, log `Restore cancelled`. Kết quả về sau khi hủy không đổi trạng thái (kiểm `job` hiện tại trước khi gán).
- `onRestoreErrorClose()` → `None`; `onRestoreChooseAnother()` → `None` rồi `ProjectsEffect.PickRestoreFile(source)`; nút nhập trên thanh trên gọi thẳng `pickers.openBackupFile()` với nguồn `PROJECTS`.

ViewModel không chờ cờ nào: các cờ vẫn chỉ đọc trong `combine` của `uiState` như hiện nay. Không thêm `first { … }` trên luồng cờ (FB-20261127-01); test ở mục 4 dựng ViewModel với luồng cờ đã kết thúc (`flowOf(true)`) để khóa điều này.

**Giao diện.** File mới `RestoreDialogs.kt` (không import `:core:ads`, như `ProjectDialogs.kt`):

- `RestoreProgressDialog(state: RestoreUi.Restoring, ad: @Composable (Modifier) -> Unit, onCancel)`: `Dialog` với `DialogProperties` ở mục 2; thẻ rộng `min(348.dp, maxWidth - 2 * 6.dp)` (348 dp ở 360 dp còn lề 6 dp, để vùng trong đủ 300 dp cho MREC, `ads-guide.md`: chỗ hẹp hơn 300 dp thì MREC không hiện), nền `surfaceContainerHigh`, bo 20 dp, lề 24/24/16/24; `Column` có phần cuộn (`weight(1f, fill = false)` + `verticalScroll`): biểu tượng khôi phục 28 dp màu `primary`, tiêu đề `titleLarge`, lời `bodyMedium` `onSurfaceVariant`, `ProgressBar` của `:core:designsystem` (8 dp), hàng "<xong> of <tổng> frames" bên trái và "<p>%" bên phải (`labelMedium`), khoảng 16 dp, rồi `ad(Modifier)`; hàng nút ghim đáy, lề trên 8 dp, nút chữ "Cancel" (`TextButton` truyền `colors` rõ, chữ `primary`) căn phải. Khi `summaryKnown = false`: tiêu đề "Restoring…", thanh ở 0, không có hàng số. Khoảng cách từ đáy quảng cáo tới nút "Cancel" ≥ 24 dp (16 khoảng cách cột + 8 lề trên hàng nút, spec mục 5) khi chưa cuộn.
- `RestoreErrorDialog(kind, onClose, onChooseAnother)`: `BasicAlertDialog` như `ProjectDialogs.kt`, thẻ 328 dp tối đa, biểu tượng 28 dp, tiêu đề "Can't open this file", lời theo `kind`, hai nút "Close" (`TextButton`, `colors` rõ, chữ `primary`) và "Choose another file" (`Button` nền `primary`). Không có quảng cáo.
- `ProjectsScreen` thêm tham số `restoreState: RestoreUi = RestoreUi.None`, `onRestoreCancel`, `onRestoreErrorClose`, `onRestoreChooseAnother` (mặc định rỗng), vẽ hộp thoại sau `ProjectOverlay`; `ProjectsAdSlots` thêm `@Composable fun RestoreAd(modifier: Modifier)`, `DefaultProjectsAdSlots.RestoreAd` gọi `AdSlot(AdPlacement.RESTORE_MREC, modifier)`. `projectsInContentAds()` đã có `RESTORE_MREC` (T-010), không đổi.
- Ánh xạ chữ: `NOT_A_BACKUP` → `projects_restore_error_damaged`; `NEWER_VERSION` → `projects_restore_error_newer`; `NO_SPACE` → `projects_message_no_space` (có sẵn, cùng chữ của spec câu 12); `WRITE_FAILED` → `projects_restore_error_failed` (chữ chưa có trong spec, mục 8, câu 4).

**Điều hướng.** `ProjectsRoute` và `projectsScreen()` bỏ tham số `onImport` và `onBackUpProject` (nút nhập và sao lưu giờ chạy trong feature); `AppNavHost.kt` bỏ dòng `onImport = null`. `ProjectsNavigation.kt` thêm:

```kotlin
/** Goes back to the project list and restores [uri] there with its restore dialog (T-017, source "settings"). */
fun NavController.restoreBackupInProjects(uri: String)
```

Hàm `popBackStack<ProjectsDestination>(inclusive = false)` rồi `getBackStackEntry<ProjectsDestination>().savedStateHandle[RESTORE_REQUEST_KEY] = uri`. Việc nhận yêu cầu tách làm hai phần:

- `ProjectsRoute` nhận hai tham số `restoreRequest: String? = null` và `onRestoreRequestConsumed: () -> Unit = {}`; `LaunchedEffect(restoreRequest)`: khác `null` thì gọi `onRestoreRequestConsumed()` trước rồi `viewModel.startRestore(restoreRequest, RestoreSource.SETTINGS)`. Phần này có test ở mức Route (mục 4), với ViewModel và repository giả như các test Route khác.
- Trong `composable<ProjectsDestination> { entry -> … }`, `entry.savedStateHandle.getStateFlow<String?>(RESTORE_REQUEST_KEY, null).collectAsStateWithLifecycle()` truyền vào `restoreRequest`, và `onRestoreRequestConsumed = { entry.savedStateHandle[RESTORE_REQUEST_KEY] = null }`: khóa bị xóa trước khi khôi phục chạy, nên dựng lại activity không chạy lại. Phần nối này (đọc `savedStateHandle`, `NavController.restoreBackupInProjects`) không có test JVM: `projectsScreen()` dùng `hiltViewModel()` nên test không thay được repository, và `TestNavHostController` nằm trong thư viện kiểm thử điều hướng chưa có trong `gradle/libs.versions.toml` (mục 3.3 không thêm thư viện). Ghi vào ngoại lệ viết test trước; T-017 kiểm luồng đủ khi nối Cài đặt.

Task này không gọi hàm từ `:app` (T-017 gọi).

**Chữ mới** (`res/values/strings.xml`, bản tiếng Việt ở `res/values-vi/strings.xml`): `projects_backup_saved` "Backup saved"; `projects_backup_failed` "Couldn't save the backup. Try again."; `projects_backup_retry` "Retry"; `projects_backing_up` "Backing up “%1$s”…"; `projects_restoring_title` "Restoring “%1$s”"; `projects_restoring_title_many` (plurals) "Restoring %1$d projects"; `projects_restoring_title_unknown` "Restoring…"; `projects_restoring_body` "Keep the app open. This takes a few seconds."; `projects_restoring_frames` "%1$d of %2$d frames"; `projects_restoring_percent` `%1$d%%` (`translatable="false"`); `projects_restoring_cancel` "Cancel"; `projects_restore_error_title` "Can't open this file"; `projects_restore_error_damaged` "This file is not a Frameloop backup, or it is damaged. Your other projects are safe."; `projects_restore_error_newer` "This backup was made with a newer version of Frameloop. Update the app and try again."; `projects_restore_error_failed` "Couldn't restore the backup. Try again."; `projects_restore_error_close` "Close"; `projects_restore_error_choose` "Choose another file". Không chuỗi nào đi cùng con trượt; con số duy nhất là phần trăm và số khung (giá trị lớn nhất 100% = khôi phục xong).

Mẫu làm theo: menu và "Duplicating…" của T-012 (`ProjectsViewModel`, `ProjectsSnackbars`), quảng cáo qua tham số `ProjectsAdSlots` của T-010, bộ chọn ảnh `PickVisualMedia` của T-011 (`NewProjectScreen.kt`), sự kiện `project_create` của T-011.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/ProjectsRepository.kt` | Thêm `backUp`, `restore` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultProjectsRepository.kt` | Nhận `ProjectBackup`, cài hai hàm bằng `Uri.parse` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsUiState.kt` | `RestoreSource`, `RestoreErrorKind`, `RestoreUi`; `ProjectsMenuState.backingUpName`; `ProjectsEffect.ShowBackupFailed`, `PickRestoreFile(source)`; `ProjectsMessage.BACKUP_SAVED`; KDoc của `BackUpProject` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsViewModel.kt` | `onBackupFileChosen`, `onBackupRetry`, `restoreState`, `startRestore`, `onRestoreCancel`, `onRestoreErrorClose`, `onRestoreChooseAnother`, hai sự kiện, log; bỏ `@Suppress("unused")` của `analytics` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreen.kt` | Route: bộ chọn, `rememberSaveable` dự án chờ, thông báo có "Retry", bỏ `onImport`, `onBackUpProject`, thêm `restoreRequest`, `onRestoreRequestConsumed`; Screen: tham số khôi phục, hộp thoại, host "Backing up…" |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/RestoreDialogs.kt` | `RestoreProgressDialog`, `RestoreErrorDialog`, tag test |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/FilePickers.kt` | `ProjectsFilePickers`, `rememberSystemFilePickers` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsAds.kt` | `ProjectsAdSlots.RestoreAd`, mặc định `AdSlot(AdPlacement.RESTORE_MREC)` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/navigation/ProjectsNavigation.kt` | Bỏ `onImport`, `onBackUpProject`; đọc yêu cầu khôi phục từ `savedStateHandle` của mục và truyền vào `ProjectsRoute` (phần nối); `NavController.restoreBackupInProjects`, `RESTORE_REQUEST_KEY` |
| Sửa | `feature/projects/src/main/res/values/strings.xml` | Chữ mới ở mục 3.1 |
| Sửa | `feature/projects/src/main/res/values-vi/strings.xml` | Bản tiếng Việt của chữ mới |
| Sửa | `app/src/main/kotlin/com/vgl/draw/animation/navigation/AppNavHost.kt` | Bỏ `onImport = null` khỏi lời gọi `projectsScreen` |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/FakeProjectsRepository.kt` | `backUp`, `restore` giả (kết quả đặt trước, `CompletableDeferred` để giữ khôi phục đang chạy, ghi tiến độ); dùng lại `NoAnalytics` có sẵn (đã ghi `events`); `FakeFeatureFlags` thêm cách dựng luồng hữu hạn `flowOf(value)` |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/FakeProjectsAdSlots.kt` | `RestoreAd` vẽ khung giả 300 × 250 có tag `fake_restore_mrec` |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/FakeFilePickers.kt` | `ProjectsFilePickers` giả ghi tên gợi ý, số lần mở, trả uri bằng tay; `FakeResultRegistry` (`ActivityResultRegistry` giả trả kết quả theo khóa, như `FakeRegistry` của `NewProjectRouteTest`) |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/BackupRestoreViewModelTest.kt` | Unit test ViewModel (mục 4) |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/RestoreDialogTest.kt` | Test giao diện Robolectric hộp thoại và Route |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/RestoreScreenshotTest.kt` | Năm ảnh chuẩn dưới |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/FilePickersTest.kt` | Bản thật mở `ACTION_CREATE_DOCUMENT`, `ACTION_OPEN_DOCUMENT` (`createAndroidComposeRule<ComponentActivity>`, `shadowOf(activity).nextStartedActivityForResult`) |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultProjectsRepositoryTest.kt` | `FakeProjectBackup` trong file; test `backUp`, `restore` truyền đúng `Uri` và id |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectMenuTest.kt` | Viết lại hai test sao lưu (mục 3.5); bỏ `onImport`, `onBackUpProject` khỏi hàm `Route` của file; truyền `FakeFilePickers` |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsRouteTest.kt` | Viết lại `import_withoutDestination_showsComingSoon` (mục 3.5); bỏ `onImport = null` |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_restoring_light.png` | Ảnh chuẩn, cùng tên ảnh thiết kế |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_restoring_dark.png` | Ảnh chuẩn, cùng tên ảnh thiết kế |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_restore-error_light.png` | Ảnh chuẩn, cùng tên ảnh thiết kế |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_restore-error_dark.png` | Ảnh chuẩn, cùng tên ảnh thiết kế |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_restoring_small.png` | Khung 360 × 640, nhiều dự án, tên dài, có quảng cáo giả |

`ProjectsScreen.kt` và `ProjectsAds.kt` là file mà `ProjectsAdsTest`, `ProjectsScreenTest`, `ProjectMenuScreenshotTest`, `ProjectsScreenScreenshotTest` dựng màn qua tham số mặc định: tham số mới đều có mặc định, nên các file đó không đổi; `ProjectsAdsTest`, `ProjectsScreenTest`, các lớp screenshot gọi `ProjectsScreen(onImport = {})`, không gọi `ProjectsRoute`, nên không bị việc bỏ `onImport` của Route ảnh hưởng (đã `grep -rn "onImport\|onBackUpProject" feature/projects/src/test app/src`). Không ghi lại ảnh chuẩn nào đã có.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không thêm. Sao lưu và khôi phục là một phần của màn danh sách, theo cờ `PROJECTS` có sẵn (cờ tắt thì cả màn được thay, KB-13). Không chờ cờ nào.
- Quảng cáo: `restore_mrec` (banner MREC, spec mục 5, đã có trong `AdPlacement.RESTORE_MREC` và trong `projectsInContentAds()`), chỉ trong hộp thoại đang khôi phục, không trong hộp thoại lỗi; qua `AdSlot` của `:core:ads`. Không vị trí nào khác.
- Sự kiện phân tích: `backup_export` (`source=projects`, `project_count`), `backup_restore` (`source=projects|settings`, `project_count`), chỉ khi thành công (spec mục 6). Không ghi tên dự án hay uri.
- File R2: Không chạm.
- **Quyền truy cập:** Không thêm. `CreateDocument`/`OpenDocument` (Storage Access Framework) và `ContentResolver.openInputStream/openOutputStream` trên uri của bộ chọn dùng quyền tạm theo uri, không cần quyền nào từ API 26 tới 36 (spec mục 6); không thêm: `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `MANAGE_EXTERNAL_STORAGE`; không gọi `takePersistableUriPermission`.
- **Thư viện và SDK:** Không thêm. `androidx.activity:activity-compose` (`ActivityResultContracts`, `rememberLauncherForActivityResult`) đã có trên classpath biên dịch của `:feature:projects` (dùng ở `NewProjectScreen.kt`); `ProjectBackup` của `:core:project` đã là phụ thuộc của module; `AdSlot` của `:core:ads` đã là phụ thuộc (T-010); `androidx.navigation` (`NavController`, `savedStateHandle`) đã có qua plugin `template.android.feature`.

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** `AdSlot` MREC trong cửa sổ `Dialog` (lần đầu trong dự án): khung 300 × 250 có thể không được đo đủ chỗ, hay `AdSlot` dựa vào `LocalLifecycleOwner` của cửa sổ dialog mà không nạp.
- **Điều kiện chuyển:** test `restoreDialog_at360dp_adSlotGets300dp` hay test kịch bản tay ở staging (dòng máy thật ở mục 4) thấy MREC không có chỗ 300 dp, hay log `VGL.Ads` không có lượt gửi của `restore_mrec` khi hộp thoại mở, sau 2 lần sửa.
- **Cách làm thay:** vẽ hộp thoại đang khôi phục không bằng `Dialog` mà bằng một lớp phủ toàn màn trong cùng cửa sổ với màn danh sách (`Box(Modifier.fillMaxSize().background(scrim 0.55).pointerInput {}`) + `BackHandler(onBack = onCancel)`), cùng file `RestoreDialogs.kt`; tiêu chí và test ở mục 4 giữ nguyên (test chạm ngoài, nút quay lại vẫn qua vì lớp phủ chặn chạm và `BackHandler` bắt nút quay lại).

### 3.5. Hành vi đã có bị đổi

Từ `python3 scripts/existing-tests.py docs/features/projects/plans/T-013-sao-luu-khoi-phuc.md` (kết quả ở dưới bảng).

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| "Back up to a file" không có đích (callback `null`) thì hiện "Coming soon" | Luôn mở bộ chọn file để lưu; không còn "Coming soon" cho sao lưu | `ProjectMenuTest.backUp_withoutDestination_showsComingSoon` | Viết lại tại chỗ thành `backUp_opensCreatePickerWithFileName`: sau khi bấm `MENU_BACKUP`, `FakeFilePickers.createdNames == listOf("Walk cycle.frameloop")`, không có node chữ "Coming soon", menu đã đóng |
| "Back up to a file" có đích thì gọi `onBackUpProject(id, name)` | Route mở bộ chọn; uri trả về thì gọi `repository.backUp(id, uri)` | `ProjectMenuTest.backUp_withDestination_callsWithIdAndName` | Viết lại tại chỗ thành `backUp_pickerResult_backsUpProject`: bấm `MENU_BACKUP`, `FakeFilePickers.completeCreate("content://t/1")`, `repository.backUps == listOf(WALK to "content://t/1")`, có node chữ "Backup saved" |
| Nút nhập không có đích thì hiện "Coming soon" | Nút nhập mở bộ chọn file để chọn | `ProjectsRouteTest.import_withoutDestination_showsComingSoon` | Viết lại tại chỗ thành `import_opensOpenPicker`: bấm `ProjectsTestTags.IMPORT`, `FakeFilePickers.openCount == 1`, không có node chữ "Coming soon" |
| `onMenuBackUp` đóng menu, gửi `BackUpProject(id, name)` | Giữ nguyên | `ProjectMenuViewModelTest.menuBackUp_emitsBackUpProjectAndCloses` | Giữ |
| `ProjectsRoute`/`projectsScreen` nhận `onImport`, `onBackUpProject` | Bỏ hai tham số | Mọi lời gọi `ProjectsRoute(onImport = null, …)` trong `ProjectsRouteTest`, hàm `Route` của `ProjectMenuTest` | Chỉ sửa phần dựng (bỏ đối số, truyền `FakeFilePickers`), không đổi câu khẳng định |

Nhóm "Cần xem trước" (30 test, phần lớn trùng chỉ vì chữ `List`): các test của `:feature:projects` trong nhóm này (`ProjectMenuTest.menu_showsHeaderAndFiveItems`, `renameText_survivesRestore`, `small_menuFits`, `ProjectsAdsTest.emptyAd_belowLessonButton_36dp`, `emptyAd_withoutLessonButton_belowNewButton`, `gridAd_heightKeepsMediaFloor`, `small_emptyAdReachable`, `snackbar_duplicatingAboveBottomBar`, `ProjectsScreenTest.privacyButton_fitsTopBar_small`, `privacyButton_largeFont_notClipped`, `settingsOff_showsPrivacyButtonInPlaceOfSettings`, `small_emptyButtonsReachable`) đo menu, thanh trên, quảng cáo của trạng thái trống, lưới và vị trí thông báo "Duplicating…"; plan không đổi bố cục nào trong số đó (hộp thoại khôi phục là cửa sổ riêng, host "Backing up…" đặt cùng chỗ với host "Duplicating…"). Giữ. Các test còn lại của nhóm (`AdGalleryScreenTest`, `InListAdsTest`, `NativeAd*Test`, `CollapsibleNativeAdTest`, `GifPngExporterTest`) ở module khác, chỉ trùng chữ `List`. `ProjectsAdsTest.noFirstStrokeRule` quét `ProjectsScreen.kt`, `ProjectsAds.kt`, `ProjectsViewModel.kt`: code mới không dùng `DeviceSettingsRepository` hay `firstStrokeDrawn`. Giữ.

Các test khác mà script liệt kê (nhóm "Nên xem": `ProjectsScreenTest`, `ProjectsAdsTest`, `ProjectsInContentAdsTest`, `ProjectsScreenScreenshotTest`, `ProjectMenuScreenshotTest`, `ProjectsViewModelTest`, `DefaultProjectsRepositoryTest`) kiểm hành vi mà plan không đổi: tham số mới của `ProjectsScreen` có mặc định, `uiState` và `menuState` cũ không đổi giá trị (trường mới `backingUpName` mặc định `null`), `ProjectsInContentAdsTest` vẫn thấy ba vị trí. Giữ.

## 4. Kiểm thử

Mọi test dưới viết trước code (bước 3) trong các file của mục 3.2; khung mặc định `w411dp-h891dp-xxhdpi`. Phần hàm phụ trợ trong file test (`FakeProjectsRepository`, `FakeProjectsAdSlots`, `FakeFilePickers`) vào commit test của bước 3 cùng chữ ký mới; interface `ProjectsRepository`, `ProjectsAdSlots`, `ProjectsFilePickers` vào commit test dưới dạng khai báo không thân (hàm của bản thật `TODO()`), để test biên dịch được và thất bại vì hành vi chưa có.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| KB-8, lưu xong | Unit test | `BackupRestoreViewModelTest.backupChosen_success_showsSavedAndLogsEvent`: repo trả `Success(1)`; hiệu ứng `ShowMessage(BACKUP_SAVED)`; `NoAnalytics.events == listOf(AnalyticsEvent("backup_export", mapOf("source" to "projects", "project_count" to "1")))`; `repository.backUps == listOf(WALK to "content://t/1")`; Route `RestoreDialogTest.backupSaved_noAction`: khi đang hiện "Backup saved" thì không có node chữ "Retry" |
| KB-8, đóng bộ chọn | Unit test và Compose UI test | `BackupRestoreViewModelTest.backupChosen_null_doesNothing` (không gọi repo, không hiệu ứng, không sự kiện); `RestoreDialogTest.backupPickerCancelled_noMessage` (Route, `FakeFilePickers.completeCreate(null)`, `advanceTimeBy(500)`, không node chữ "Backup saved" hay "Couldn't save the backup. Try again.") |
| KB-8, lỗi ghi | Unit test | `backupChosen_failure_showsFailedNoEvent`, chạy cho cả bốn `BackupExportFailure`: hiệu ứng `ShowBackupFailed`, không sự kiện; `backupRetry_writesSameUriAgain`: lần hai repo trả `Success`, `repository.backUps` có hai lần cùng `(WALK, "content://t/1")`, rồi `ShowMessage(BACKUP_SAVED)` và đúng một sự kiện |
| KB-8, lỗi ghi: "Retry" | Compose UI test | `RestoreDialogTest.backupFailed_showsRetry_andRetryCallsAgain`: Route, repo trả `Failure(NO_SPACE)` rồi `Success`; có chữ "Couldn't save the backup. Try again." và nút "Retry"; bấm "Retry" thì `repository.backUps.size == 2` và có chữ "Backup saved" |
| KB-8, màu nút "Retry" trên thông báo | Compose UI test (`@GraphicsMode(NATIVE)`) | `RestoreDialogTest.backupFailed_retryColorIsInversePrimary`: vẽ `decorView` ra `Bitmap` như `windowBitmap()` của `ProjectMenuTest`, lấy các điểm nét chữ trong `boundsInWindow` của node chữ "Retry" bằng cách của `ProjectMenuTest.inkColorsIn` (nền là `inverseSurface`, nền của `Snackbar`), số điểm ≥ `MIN_INK`, và `shareCloser(ink, inversePrimary, primary) ≥ 0.8` với màu của `AppTheme(darkTheme = false)` (`inversePrimary` là màu nút của `Snackbar` chuẩn, `primary` là màu `TextButton` tự đặt khi bị đè); hai hàm phụ trợ chép vào file test mới |
| KB-8, dấu hiệu đang ghi | Unit test và Compose UI test | `backupChosen_whileRunning_setsBackingUpNameThenClears` (repo giữ bằng `CompletableDeferred`: `menuState.value.backingUpName == "Walk cycle"`, xong thì `null`); `backupChosen_whileOneRuns_isIgnored` (`repository.backUps.size == 1`); `RestoreDialogTest.backingUp_showsMessageUntilDone` (`mainClock.autoAdvance = false`; có chữ "Backing up “Walk cycle”…" khi repo chưa trả, `advanceTimeBy(500)` sau khi trả thì không còn); danh sách vẫn hiện (node `ProjectsTestTags.card(WALK)` có) |
| KB-8, tên gợi ý, không hỏi quyền | Compose UI test | `ProjectMenuTest.backUp_opensCreatePickerWithFileName` (mục 3.5); `FilePickersTest.create_launchesCreateDocument`: intent `ACTION_CREATE_DOCUMENT`, `type == "application/octet-stream"`, `EXTRA_TITLE == "Walk cycle.frameloop"`; `FilePickersTest.open_launchesOpenDocument`: `ACTION_OPEN_DOCUMENT`, `EXTRA_MIME_TYPES` chứa `"*/*"` hay `type == "*/*"`; không quyền: manifest không đổi (không file manifest nào trong mục 3.2) |
| KB-8, giống hệt sau khôi phục | Unit test (có sẵn) | `DefaultProjectBackupTest` của `:core:project` (T-003); thêm `DefaultProjectsRepositoryTest.backUp_passesIdAndParsedUri` và `restore_passesParsedUriAndForwardsProgress` với `FakeProjectBackup` (Robolectric, `Uri.parse("content://t/1")` so bằng `equals`) |
| KB-9, nút nhập mở bộ chọn | Compose UI test | `ProjectsRouteTest.import_opensOpenPicker` (mục 3.5) |
| KB-9, hộp thoại đang khôi phục | Unit test | `startRestore_beforeManifest_showsUnknownSummary` (`Restoring(summaryKnown = false, doneFrames = 0)`); `startRestore_progress_updatesState` (fake gọi `onProgress` với `BackupSummary(1, "Walk cycle", 120)` và 72: `Restoring("Walk cycle", 1, 72, 120, true)`) |
| KB-9, chữ hộp thoại | Compose UI test | `RestoreDialogTest.restoring_showsTitleBodyFramesPercentCancel`: `Restoring("Walk cycle", 1, 72, 120, true)` → có đúng các node chữ "Restoring “Walk cycle”", "Keep the app open. This takes a few seconds.", "72 of 120 frames", "60%", "Cancel"; `restoring_unknownSummary_showsGenericTitleNoFrames` ("Restoring…", không node chữ chứa " frames") |
| KB-9, nhiều dự án | Compose UI test | `restoring_manyProjects_showsCount`: `Restoring(null, 3, 10, 40, true)` → "Restoring 3 projects", "10 of 40 frames", "25%" |
| KB-9, xong thì đóng, đứng đầu | Unit test | `restoreSuccess_closesLogsEventAndScrollsToTop`: repo trả `Success([NEW])` và đặt danh sách `[NEW, WALK]`; `restoreState.value == None`; sự kiện `backup_restore` với `source=projects`, `project_count=1`; hiệu ứng `ScrollToTop` |
| KB-9, nhiều dự án đứng đầu | Unit test | `restoreSuccess_many_logsCount`: `Success([A, B, C])` → `project_count = "3"` |
| KB-9, "Cancel" | Unit test và Compose UI test | `restoreCancel_cancelsJobAndCloses`: fake ghi `cancelled = true` khi coroutine bị hủy; `restoreState == None`; không sự kiện; kết quả `Success` về muộn (fake trả sau hủy bằng `NonCancellable`) không đổi trạng thái; `RestoreDialogTest.restoring_cancelClick_callsCancel` |
| KB-9, đóng bộ chọn | Compose UI test | `RestoreDialogTest.restorePickerCancelled_noDialog`: `completeOpen(null)` → không node `RESTORE_DIALOG`, `repository.restores` rỗng |
| KB-9, từ trạng thái trống | Unit test | `restoreFromEmpty_endsInList`: `uiState` là `Empty` trước, repo đặt danh sách một dự án khi trả `Success`; `uiState` thành `List` |
| KB-9, không ghi đè | Unit test | `restoreSameProject_keepsBothCards`: danh sách `[WALK]`, repo trả `Success([NEW])` và đặt `[NEW, WALK]`; `uiState` có hai thẻ |
| KB-9, chạm ngoài, nút quay lại | Compose UI test | `RestoreDialogTest.restoring_tapOutside_staysOpen` (dialog `usePlatformDefaultWidth = false` nên chạm vào gốc cửa sổ dialog có thể trúng thẻ: chạm vùng ngoài thẻ, tại góc trên trái gốc cửa sổ activity, `onAllNodes(isRoot())[0].performTouchInput { click(Offset(5f, 5f)) }`, sau khi kiểm (5, 5) nằm ngoài `boundsInWindow` của node `RESTORE_DIALOG`; node `RESTORE_DIALOG` vẫn có, `cancelCalls == 0`. Đối chứng ghi sẵn: tạm đổi `dismissOnClickOutside = false` thành `true` trong `RestoreDialogs.kt` bằng `perl -pi -e 's/\QdismissOnClickOutside = false\E/dismissOnClickOutside = true/' feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/RestoreDialogs.kt`, `git diff --stat -- <tệp>` thấy đổi, chạy lại test thì `cancelCalls == 1` (test đỏ), rồi `git checkout -- <tệp>`); `restoring_back_callsCancel` (`Espresso.pressBack()` → `cancelCalls == 1`) |
| KB-9, 360 × 640 | Compose UI test (`@Config(qualifiers = "w360dp-h640dp-xxhdpi")`, `@GraphicsMode(NATIVE)`) | `RestoreDialogTest.restoringSmall_cancelVisibleAndAdGap`: tên dài 40 ký tự, `Restoring(…, true)`, quảng cáo giả 300 × 250; đáy nút "Cancel" ≤ đáy cửa sổ dialog, nút không bị phần cuộn che (đỉnh nút ≥ đáy vùng cuộn); `performScrollTo` tới quảng cáo giả thì khoảng từ đáy quảng cáo tới đỉnh hàng nút ≥ 24 dp; `restoreDialog_at360dp_adSlotGets300dp`: bề rộng node `fake_restore_mrec` = 300 dp ± 0,5, bề rộng thẻ 348 dp ± 0,5 |
| KB-9, rời app rồi quay lại | Compose UI test | `RestoreDialogTest.restoring_backgroundAndBack_keepsRunning`: `createAndroidComposeRule<ComponentActivity>`, ViewModel dựng ngoài composition, repo giữ bằng `CompletableDeferred`; `scenario.moveToState(CREATED)` rồi `RESUMED`: `repository.restoreCancelled == false`, hộp thoại còn, gửi tiếp tiến độ 120/120 thì chữ "120 of 120 frames" |
| KB-9, app bị đóng | Unit test (có sẵn) | `FileProjectStoreTest` và `DefaultProjectRepositoryTest` của `:core:project` (dọn `.restoring-*` lúc mở, T-002, T-003); ViewModel không lưu việc đang chạy: `restoreState` không dùng `SavedStateHandle` (đọc code ở rà soát) |
| KB-9, hết chỗ | Unit test và Compose UI test | `restoreFailure_mapsEveryReason`: bốn `RestoreFailure` → bốn `RestoreErrorKind` cùng tên, không sự kiện, danh sách không đổi; `RestoreDialogTest.restoreError_noSpace_showsSpaceMessage` |
| KB-10, hộp thoại lỗi | Compose UI test | `restoreError_damaged_showsTitleBodyButtons` ("Can't open this file", lời của spec, "Close", "Choose another file"); `restoreError_newer_showsNewerMessage`; `restoreError_writeFailed_showsFailedMessage`; không node `fake_restore_mrec` trong mọi hộp thoại lỗi |
| KB-10, "Close" | Unit test và Compose UI test | `restoreErrorClose_returnsToNone`; `RestoreDialogTest.restoreError_close_callsClose` |
| KB-10, "Choose another file" | Unit test và Compose UI test | `restoreChooseAnother_emitsPickWithSameSource` (`Error(NOT_A_BACKUP, SETTINGS)` → `PickRestoreFile(SETTINGS)`, `None`); Route `restoreError_chooseAnother_opensPickerAgain` (`FakeFilePickers.openCount` tăng 1) |
| KB-10, thất bại giữa chừng không dự án dở | Unit test | `restoreFailure_mapsEveryReason` (danh sách trước và sau bằng nhau) |
| KB-12, giữ việc khôi phục qua đổi cấu hình | Compose UI test | `RestoreDialogTest.restoring_recreate_keepsDialogAndJob`: `createAndroidComposeRule<ComponentActivity>`, ViewModel dựng ngoài composition (sống qua dựng lại như `ViewModelStore`), `activityRule.scenario.recreate()` rồi gọi lại `setContent` với cùng một lambda nội dung đã giữ trong biến của test (như `NewProjectRouteTest.recreate()`: khóa `rememberSaveable` theo vị trí lambda); chữ "72 of 120 frames" vẫn có, `repository.restoreCancelled == false` |
| Dự án chờ sao lưu qua dựng lại activity | Compose UI test | `RestoreDialogTest.backupPending_recreate_stillBacksUpRightProject`: bộ chọn ở test này là bản thật `rememberSystemFilePickers` trên một `ActivityResultRegistry` giả truyền qua `LocalActivityResultRegistryOwner` (như `FakeRegistry` của `NewProjectRouteTest`, kết quả trả theo khóa đăng ký, không qua closure của lần dựng trước); bấm `MENU_BACKUP`, `scenario.recreate()`, `setContent` lại cùng lambda, registry giả trả `Uri.parse("content://t/1")` cho khóa của `CreateDocument` → `repository.backUps == listOf(WALK to "content://t/1")`. Đối chứng: không bấm `MENU_BACKUP` mà vẫn trả kết quả thì `repository.backUps` rỗng |
| "Backing up…" nằm trên thanh đáy, như "Duplicating…" | Compose UI test (`@GraphicsMode(NATIVE)`) | `RestoreDialogTest.backingUp_aboveBottomBar`: `ProjectsScreen` với `FakeProjectsAdSlots`, `ProjectsMenuState(backingUpName = "A")`, `mainClock.autoAdvance = false`, `advanceTimeBy(500)`; đáy node chữ "Backing up “A”…" ≤ đỉnh thanh đáy giả (cùng cách đo của `ProjectsAdsTest.snackbar_duplicatingAboveBottomBar`) |
| Quảng cáo chỉ lúc đang khôi phục, rời giao diện khi đóng | Compose UI test | `restoreAd_onlyWhileRestoring`: `None` → không node `fake_restore_mrec`; `Restoring` → có đúng 1; đổi sang `Error` → 0; đổi sang `None` → 0 (khóa `is_show_restore_mrec` và việc hủy quảng cáo khi rời giao diện do `AdSlot` của `:core:ads` lo, đã có test ở `:core:ads`) |
| Không chờ cờ, luồng cờ đã kết thúc (FB-20261127-01) | Unit test | `restore_withFinishedFlagFlows_completes`: `FakeFeatureFlags` dựng bằng `flowOf(true)` cho mọi cờ (luồng kết thúc ngay), thu thập `uiState`; `startRestore` rồi repo trả `Success([NEW])`: không ngoại lệ, `restoreState == None`, đúng một sự kiện. Đối chứng ghi sẵn (không commit): tạm thêm dòng `featureFlags.enabled(FeatureFlag.PROJECTS).first { !it }` vào đầu coroutine của `startRestore` bằng `perl -pi`, `git diff --stat` thấy đổi, test đỏ vì `NoSuchElementException`, rồi `git checkout -- feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsViewModel.kt`. `restoreSuccess_listWithoutNew_noScrollAfterWait`: repo trả `Success([NEW])` mà danh sách không có `NEW`; `advanceTimeBy(SCROLL_WAIT_MILLIS + 1)`: không `ScrollToTop`, `restoreState == None`, không ngoại lệ |
| T-017: yêu cầu khôi phục từ Cài đặt | Compose UI test | `RestoreDialogTest.settingsRequest_startsRestoreOnceWithSettingsSource`: Route với ViewModel và `FakeProjectsRepository` dựng ngoài composition, `restoreRequest` là một `mutableStateOf<String?>("content://t/9")` của test, `onRestoreRequestConsumed` đặt nó về `null` và đếm lần gọi; sau `waitForIdle`: `repository.restores == listOf("content://t/9")`, đếm tiêu thụ = 1, giá trị đã về `null`; `scenario.recreate()` rồi `setContent` lại cùng lambda: `repository.restores.size` vẫn 1; repo trả `Success([NEW])` thì `NoAnalytics.events` có `backup_restore` với `source=settings`. Đối chứng: `restoreRequest = null` thì `repository.restores` rỗng |
| Giao diện đang khôi phục, đúng thiết kế | Screenshot test cùng tên ảnh thiết kế, `scripts/design-compare.sh projects` | `RestoreScreenshotTest`: `ProjectsScreen_restoring_light.png`, `ProjectsScreen_restoring_dark.png` (danh sách bốn thẻ như thiết kế, `Restoring("Walk cycle", 1, 72, 120, true)`, `FakeProjectsAdSlots`) |
| Giao diện lỗi khôi phục, đúng thiết kế | Screenshot test cùng tên ảnh thiết kế | `ProjectsScreen_restore-error_light.png`, `ProjectsScreen_restore-error_dark.png` (`Error(NOT_A_BACKUP, PROJECTS)`) |
| Màn hình thấp | Screenshot test khung `w360dp-h640dp-xxhdpi`, trạng thái nhiều nội dung nhất | `ProjectsScreen_restoring_small.png` (tên dài, có quảng cáo giả) |
| Ảnh thiết kế của ticket khác (T-009, T-011, T-012): `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark`, `ProjectsScreen_list_light`, `ProjectsScreen_list_dark`, `ProjectsScreen_list_light_unfolded`, `ProjectsScreen_list_dark_unfolded`, `ProjectsScreen_menu_light`, `ProjectsScreen_menu_dark`, `NewProjectScreen_default_light`, `NewProjectScreen_default_dark` | | Không đổi |
| Chữ đúng bảng chữ của thiết kế | Compose UI test | `restoring_showsTitleBodyFramesPercentCancel`, `restoreError_damaged_showsTitleBodyButtons` (chữ nguyên văn của bảng chữ trong `design/README.md`) |
| Bộ chọn file thật trên máy, chọn được Google Drive; `restore_mrec` thật trong hộp thoại | Kiểm tra thủ công (máy thật: khác, bộ chọn file của hệ thống với ứng dụng Drive đã đăng nhập, và quảng cáo thật) | Trên bản staging: sao lưu một dự án vào Drive, thấy "Backup saved"; khôi phục file đó từ Drive, thấy hộp thoại có tiến độ và MREC (log `adb logcat -v threadtime \| grep ' VGL\.'` có dòng `Restore done: 1 projects` của thẻ `Projects`, ghi ở `ProjectsViewModel` khi `RestoreResult.Success`, và dòng nạp banner của `restore_mrec` thẻ `VGL.Ads`); thẻ mới đứng đầu. `is_show_restore_mrec` không có trong `StagingFeatureFlagsModule` (khóa quảng cáo đi qua Remote Config, Firebase staging bật tắt được) |
| Khôi phục khi máy hết chỗ, file của bản mới hơn trên máy thật | Kiểm tra thủ công (người: dev xem ảnh chuẩn và test JVM; dựng máy đầy bộ nhớ hay file bản mới hơn không hợp với phiên tự chạy) | Đọc ảnh chuẩn `restore-error` và kết quả `restoreFailure_mapsEveryReason`, `restoreError_newer_showsNewerMessage` |

Mọi câu khẳng định dựa vào số đã ghi ở trên; không câu nào đọc giá trị mặc định mà không có đối chứng: `backupChosen_null_doesNothing` đi cùng `backupChosen_success_…` trên cùng fake (fake ghi lại lời gọi), `restore_withFinishedFlagFlows_completes` có lần đối chứng thêm tạm `first { !it }`.

**Ngoại lệ viết test trước:** Chữ tiếng Việt trong `res/values-vi/strings.xml` (chỉ tài nguyên, lint kiểm đủ khóa); dòng bỏ `onImport = null` ở `AppNavHost.kt` (chỉ để biên dịch; vỏ app không gọi hàm `suspend` mới nào); phần nối `savedStateHandle` trong `projectsScreen()` và hàm `NavController.restoreBackupInProjects` (lý do ở mục 3.1: `hiltViewModel()` và thiếu thư viện kiểm thử điều hướng; phần quyết định đã test ở mức Route). Không có tệp kịch bản emulator: bộ chọn file của hệ thống (DocumentsUI) khác nhau theo bản Android và không có tệp mẫu `.frameloop` trên emulator của CI.

### 4.1. Chạy thử

- **Đã chạy thử:** Chạy thử: chưa chạy xong (hết giờ của phiên con soạn plan: `scripts/check.sh` chạy khoảng 30 phút mới tới cuối task `:feature:projects:testDebugUnitTest`, phiên dừng lệnh trước khi tới lint và build staging). 2026-11-27, trên `9ada9d2` (nhánh chính), worktree tạm, test tạm `TrialT013Test` trong `feature/projects/src/test` (Robolectric, `createAndroidComposeRule<ComponentActivity>`, khung `w360dp-h640dp-xxhdpi`), không commit, đã xóa cùng worktree.
- **Lệnh:** `scripts/check.sh`
- **Kết quả:** chưa có dòng cuối `Cổng chất lượng` (lệnh bị dừng, mã thoát 144). Đã thấy trong task `:feature:projects:testDebugUnitTest` của `check.sh`:
  - `mrecInDialog_at360dp_keeps348dpDialog` qua: `Dialog(usePlatformDefaultWidth = false)` chứa `Box(width 348.dp)` có `AdSlot(AdPlacement.RESTORE_MREC)` dựng được ở 360 dp, thẻ đo 348 dp; `AdSlot` không có `AdsController` (test) có khung 0 × 0, nên test 300 dp ở mục 4 phải đo khung giả `fake_restore_mrec` của `FakeProjectsAdSlots`, không đo `AdSlot` thật. Chưa thấy MREC thật nạp trong cửa sổ dialog (chỉ kiểm được trên máy, dòng máy thật ở mục 4; phương án dự phòng ở mục 3.4 chưa thử).
  - `createAndOpenDocument_startPickerIntents` thất bại vì chính test tạm: hai nút đặt chồng nhau (không có `Column`), bấm chữ "create" trúng nút "open", intent nhận được là `android.intent.action.OPEN_DOCUMENT` (bộ chọn mở được qua `ActivityResultRegistry` trên Robolectric, đọc được bằng `shadowOf(activity).nextStartedActivityForResult`). Chưa thấy `ACTION_CREATE_DOCUMENT`, `type`, `EXTRA_TITLE` của `CreateDocument`: không ghi kết quả chưa thấy. Mục 8, câu 7.
  - Không lớp nào thiếu trên classpath test của `:feature:projects` (`ActivityResultContracts.CreateDocument`, `OpenDocument`, `AdSlot`, `BackupFileNames` biên dịch được).

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 1 (quảng cáo: đặt vị trí `restore_mrec` vào màn hình lần đầu), 4 (thu thập dữ liệu: thêm sự kiện `backup_export`, `backup_restore`). Rủi ro Cao, cần duyệt chéo; người phụ trách sản phẩm biết trước khi duyệt (risk-scoring.md, mục 3). Bảng điểm dưới chỉ để tham khảo. Không chạm module core: `ProjectBackup` dùng nguyên như T-003 để lại.

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 3 | Hai module (`:feature:projects`, một dòng ở `:app`), đổi giao diện công khai của `ProjectsRepository`, `ProjectsAdSlots`, `projectsScreen()`, thêm hàm điều hướng công khai cho T-017 |
| B. Độ rõ của yêu cầu | 2 | Spec đã xác nhận và có thiết kế cho hai hộp thoại, nhưng còn bốn chữ chưa có trong spec (đang ghi, nhiều dự án, chưa đọc xong manifest, lỗi ghi) và cách nối với T-017 (mục 8) |
| C. Khả năng kiểm chứng tự động | 2 | Phần lớn có test JVM; bộ chọn file thật, Google Drive và MREC thật chỉ kiểm được trên máy thật |
| D. Ảnh hưởng chéo | 2 | Sửa `ProjectsScreen.kt`, `ProjectsViewModel.kt` mà T-012 phiên bản 2 cũng sửa, và `AppNavHost.kt` mà các ticket màn khác (T-016, T-017, T-018) sửa; tạo lối vào mà T-017 dựa vào |
| E. Độ mới của kỹ thuật | 2 | `CreateDocument`/`OpenDocument` và MREC trong cửa sổ `Dialog` lần đầu trong dự án (phổ biến); chạy thử ở mục 4.1 |
| **Tổng** | 11 | **Mức: Cao** (chạm điểm chặn 1 và 4) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `ProjectsScreen.kt`, `ProjectsViewModel.kt`, `ProjectsUiState.kt`, `ProjectsAds.kt` (cùng module với T-009, T-010, T-012); `AppNavHost.kt` (một dòng); dùng `ProjectBackup` của `:core:project` không sửa.
- Task khác trong sprint có thể xung đột: T-012 phiên bản 2 (sửa thông báo "Duplicating…" trong `ProjectsScreen.kt`, `ProjectsViewModel.kt`; đã merge theo lời phiên giao việc, plan này đọc nhánh chính `9ada9d2`), T-017 (dùng `restoreBackupInProjects`, `RestoreSource.SETTINGS`; plan T-017 phải đọc tên trong plan này), T-016, T-018 (sửa `AppNavHost.kt`).
- Đề xuất thứ tự chạy: sau mọi plan đang sửa `ProjectsScreen.kt`; trước T-017.

## 7. Điều kiện dừng riêng

- Cần sửa file của `:core:project` (ví dụ `ProjectBackup` thiếu một điều mà tiêu chí cần): dừng, vì plan dựa vào hợp đồng có sẵn của T-003 và điểm chặn 7 chưa được chấm.
- Cần thêm vị trí quảng cáo, sự kiện hay tham số sự kiện ngoài mục 3.3: dừng.

## 8. Câu hỏi cho người duyệt

1. **Chữ dấu hiệu đang ghi bản sao lưu** (KB-8 "có dấu hiệu đang ghi tới khi xong"): spec không có chữ. Đề xuất: thông báo không tự ẩn "Backing up “<tên>”…" (bản tiếng Việt "Đang sao lưu “<tên>”…") theo đúng mẫu "Duplicating…" của T-012; trong lúc ghi, bấm sao lưu lần nữa thì bỏ qua.
2. **Tiêu đề khi file có nhiều dự án** (KB-9, spec câu 19 chỉ ghi "ghi số dự án thay cho tên"): đề xuất "Restoring 3 projects" (plurals; "Restoring 1 project" không xảy ra vì một dự án thì ghi tên).
3. **Hộp thoại trước khi đọc xong đầu file**: file trên Drive có thể mất vài giây mới đọc được tên dự án. Đề xuất: hộp thoại hiện ngay khi chọn file với tiêu đề "Restoring…", thanh ở 0, chưa có dòng số khung, nút "Cancel" bấm được; đọc xong đầu file thì đổi sang tiêu đề có tên. File không phải bản sao lưu thì hộp thoại này chuyển ngay sang hộp thoại lỗi.
4. **Lời lỗi khi ghi dự án khôi phục thất bại vì lý do khác hết chỗ** (`WRITE_FAILED`, spec không có lời): lời "not a Frameloop backup, or it is damaged" sai với trường hợp này. Đề xuất: cùng tiêu đề "Can't open this file", lời "Couldn't restore the backup. Try again.", cùng hai nút.
5. **Lối vào cho T-017**: đề xuất `NavController.restoreBackupInProjects(uri)` của `:feature:projects` (về danh sách, đặt uri vào `savedStateHandle` của mục danh sách, màn đọc một lần rồi xóa); `:app` gọi khi Cài đặt chọn được file. T-013 chỉ tạo và test hàm, không nối Cài đặt. Phần quyết định nằm ở tham số `restoreRequest`, `onRestoreRequestConsumed` của `ProjectsRoute` (có test); phần đọc `savedStateHandle` là phần nối không test JVM (mục 3.1). Phương án (b): T-013 bỏ hẳn lối vào cho T-017 (bỏ hai tham số, hàm `restoreBackupInProjects`, hàng `settingsRequest_…` ở mục 4); T-017 làm cả phần nhận và phần nối.
6. **"Retry" của lỗi sao lưu**: đề xuất thông báo hiện `SnackbarDuration.Long` (khoảng 10 giây) rồi tự ẩn, "Retry" ghi lại vào đúng file đã chọn (không mở lại bộ chọn); với lỗi `NOTHING_TO_EXPORT` (dự án vừa bị hỏng hay xóa ở nơi khác) cũng cùng lời, "Retry" sẽ lỗi lại. Phương án khác: "Retry" mở lại bộ chọn để chọn nơi khác.

7. **Điều kiện trước khi duyệt: chạy thử phải xong** (E = 2, `docs/CLAUDE.md`, mục Khi viết plan; mục 4.1). Đây là việc phải làm, không phải câu hỏi: trước khi ghi `approved`, một phiên có đủ giờ (`check.sh` của lần thử chạy hơn 30 phút) dựng worktree tạm từ nhánh chính, thêm lại test tạm `TrialT013Test` với hai test riêng (một cho `CreateDocument`, một cho `OpenDocument`, mỗi test một nút), chạy `export ANDROID_HOME=$HOME/android-sdk; scripts/check.sh`, rồi ghi vào mục 4.1: dòng cuối `== Cổng chất lượng: …`, và giá trị thấy được của intent `CreateDocument`: `action` (chờ `android.intent.action.CREATE_DOCUMENT`), `type` (chờ `application/octet-stream`), `EXTRA_TITLE` (chờ `Walk cycle.frameloop`), cùng `action` của `OpenDocument`. Giá trị khác thì sửa mục 3.1 và mục 4 trước khi duyệt. Xóa worktree tạm, không commit test tạm.

## 9. Quyết định duyệt

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện
