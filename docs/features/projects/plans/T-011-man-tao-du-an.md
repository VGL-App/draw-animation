---
ticket: T-011
title: Màn Tạo dự án
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 17.4M        # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [ads, analytics] # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-011: MÀN TẠO DỰ ÁN

## 1. Tóm tắt

Thêm màn Tạo dự án (`NewProjectScreen`) vào `:feature:projects`: ô tên, ba nhóm chọn khung hình, độ phân giải, tốc độ khung, nhóm nền (ba màu cố định hay ảnh qua Photo Picker), nút "Create" ghim đáy, ô `new_project_native` trong phần cuộn và `create_inter` sau khi tạo từ dự án thứ 2. Dự án tạo bằng `ProjectRepository.create` của `:core:project`, ảnh nền được xoay theo EXIF và thu nhỏ tới 3840 điểm ảnh cạnh dài trước khi chép vào dự án, sự kiện `project_create` ghi khi tạo xong. Kiểm chứng bằng unit test ViewModel và kho, test Robolectric cho chuẩn bị ảnh, test giao diện, screenshot cùng tên ảnh thiết kế, test vỏ app cho điều hướng.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/projects/spec.md`:

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-3 | Màn có tiêu đề "New animation", nút đóng, ô "Name", nhóm "Canvas", "Size", "Frame rate", "Background", nút "Create" ghim đáy, đúng thiết kế | Theo `design/context/NewProjectScreen_default_*.md`. Nhóm Canvas ba ô (9:16, 16:9, 1:1; đã bỏ 4:5). Chip tốc độ đang chọn ghi "12 fps", chip khác chỉ ghi số (theo thiết kế) |
| KB-3 | Mỗi nhóm có sẵn một lựa chọn: khung hình, tốc độ theo Cài đặt (chưa đổi thì 9:16, 12); 1080p; nền trắng; tên "My animation <số>", số là số dự án đã từng tạo cộng một | Đọc `DeviceSettingsRepository.defaultAspectRatio`, `defaultFrameRate` (T-005) và `ProjectRepository.createdCount` một lần khi mở màn. Cờ `feature_settings_enabled` tắt thì dùng 9:16 và 12 (settings câu 15), giá trị đã lưu không bị xóa |
| KB-3 | Bấm "Create" không đổi gì thì tạo với giá trị mặc định và mở ngay màn vẽ; từ danh sách trống tới màn vẽ đúng 2 lần chạm | Danh sách đã gọi `onNewProject` (T-009). Màn vẽ chưa có (T-018): xem mục 3.1, phần Điều hướng, và câu hỏi 1 |
| KB-3 | Chọn lựa chọn khác thì nó sáng lên, lựa chọn cũ tắt; mỗi nhóm luôn đúng một lựa chọn | Mỗi nhóm là `selectableGroup`, mỗi ô `selectable(role = RadioButton)`; bấm lại ô đang chọn không đổi gì |
| KB-3 | Kích thước khung vẽ theo khung hình và độ phân giải | `canvasSize` của `:core:project` đã có và đã có test; plan kiểm `NewProject` truyền đúng `aspectRatio`, `resolution` |
| KB-3 | "Photo" mở bộ chọn ảnh của hệ thống, không hỏi quyền; chọn ảnh thì ảnh là nền; đóng bộ chọn thì giữ lựa chọn trước; ảnh là nền cố định, cắt vừa khung | `ActivityResultContracts.PickVisualMedia(ImageOnly)`. Cắt vừa khung là việc của phần vẽ (`BackgroundDecoder`, T-004); màn này chỉ chép ảnh đã xoay và thu nhỏ |
| KB-3 | Ảnh gốc bị xóa sau khi tạo thì nền không đổi | Bytes ảnh đã chuẩn bị được ghi thành `background.jpg` trong thư mục dự án (`NewBackground.Photo`) |
| KB-3 | Nút đóng hay nút quay lại về danh sách, không tạo dự án | `onClose` gọi `popBackStack`; nút quay lại của máy theo `NavHost` |
| KB-3 | Dự án nằm trên máy, ở đầu danh sách khi quay về | Kho dự án sắp theo `updatedAtMillis` (T-002); test vỏ app kiểm quay về đúng danh sách |
| KB-3 | Xóa hết tên rồi "Create" thì dùng tên mặc định; ô tên nhận tối đa 40 ký tự | Tên trống (sau `trim`) thay bằng tên mặc định. Đếm theo code point như `ProjectNames.MAX_NAME_LENGTH` |
| KB-3 | Dự án đầu tiên: "Create" không hiện quảng cáo toàn màn hình | `createdCount` đọc ngay trước khi tạo; bằng 0 thì không gọi `create_inter` |
| KB-3 | Máy không đủ chỗ: báo lỗi dễ hiểu, giữ ở màn với lựa chọn đã chọn, không tạo dự án dở | `ProjectFailure.NO_SPACE` thành thông báo "Not enough space on your phone. Free up some space and try again." (cùng lời với spec câu hỏi 12); kho không để lại dự án dở (T-002) |
| KB-3 | Màn thấp 360 × 640: phần tùy chọn cuộn được, "Create" luôn thấy, không che lựa chọn cuối | `Column(verticalScroll)` có trọng số, `Footer` ngoài phần cuộn |
| KB-3 | Bấm "Create" hai lần nhanh chỉ tạo một dự án | Cờ `creating` trong ViewModel, bỏ qua lần bấm thứ hai khi đang tạo hay đã tạo xong |
| KB-2 | Cờ màn vẽ tắt: "Create" hiện "Coming soon", ở lại màn, không tạo dự án | `FeatureFlag.EDITOR` theo dõi bằng `Flow`, đọc giá trị lúc bấm |
| KB-12 | Xoay hay gập mở máy ở màn Tạo dự án thì lựa chọn giữ nguyên | Lựa chọn nằm trong ViewModel (sống qua đổi cấu hình). Bố cục rộng: một cột căn giữa, rộng tối đa bằng bố cục điện thoại (spec câu hỏi 21) |
| KB-13 | Cờ projects tắt thì không vào được màn Tạo dự án | Lối vào duy nhất là danh sách, mà danh sách chuyển sang màn mẫu khi cờ tắt (T-009). Màn này cũng theo dõi cờ: tắt thì đóng màn (`onClose`) |

Thiết kế tham chiếu: `features/projects/design/README.md`, các ảnh trong `design/screens/`: `NewProjectScreen_default_light`, `NewProjectScreen_default_dark`; số liệu ở `design/context/NewProjectScreen_default_*.md`. Ô màu nền dùng ba màu cố định `ProjectBackgroundColors` (`#FFFFFF`, `#22262E`, `#2F6FE0`) cho cả sáng và tối, khác ảnh thiết kế (ô thứ hai trên ảnh là `#E3E6EA`), theo lời anh Nhật ngày 2026-11-22 (plan T-004, câu hỏi 1); ảnh so sánh sẽ lệch đúng ở ô này.

ADR liên quan: ADR-0001 (phụ thuộc module), ADR-0003 (cờ là `Flow`), ADR-0019 (log), ADR-0020 (screenshot cùng tên thiết kế), ADR-0024 (không chế độ ngoại tuyến), ADR-0045 (quảng cáo qua `:core:ads`).

## 3. Giải pháp

### 3.1. Cách làm

Làm theo mẫu của màn danh sách trong cùng module (`ProjectsViewModel`, `ProjectsRoute`, `ProjectsScreen`, `DefaultProjectsRepository`, `ProjectsModule`) và bước "Viết một feature mới" của `CLAUDE.md`. Code mới nằm trong gói con `create`, kho trong `data`.

**Kho** (`data/NewProjectRepository.kt`, công khai trong module như `ProjectsRepository`; cài đặt `internal`):

```kotlin
interface NewProjectRepository {
    /** Read once when the screen opens. Never throws an IOException (the flows retry by themselves). */
    suspend fun defaults(): NewProjectDefaults
    /** Projects ever created with the Create screen, read right before creating (create_inter, question 16). */
    suspend fun createdCount(): Int
    /** ProjectRepository.create, then ProjectThumbnails.update on success (contract with T-004). */
    suspend fun create(request: NewProject): ProjectResult<Project>
    /** Rotated by EXIF, scaled to at most 3840 px on the long side, JPEG; null when the image cannot be used. */
    suspend fun preparePhoto(uri: String): PreparedPhoto?
}

data class NewProjectDefaults(
    val aspectRatio: AspectRatio, val frameRate: FrameRate,
    val defaultName: String, val firstLayerName: String,
)

class PreparedPhoto(val bytes: ByteArray, val extension: String, val preview: ImageBitmap)
```

- `DefaultNewProjectRepository` nhận `ProjectRepository`, `ProjectThumbnails`, `DeviceSettingsRepository`, `PhotoPreparer`, `@ApplicationContext Context` (chỉ để đọc chuỗi), `@ProjectsIoDispatcher`. `defaults()` đọc `createdCount.first()`, `defaultAspectRatio.first()`, `defaultFrameRate.first()`; đổi chuỗi `"9:16"`, `"16:9"`, `"1:1"` sang `AspectRatio` và số sang `FrameRate.fromFpsOrNull`, giá trị lạ thì 9:16 và 12. Tên mặc định là chuỗi `new_project_default_name` ("My animation %1$d") với `createdCount + 1`; tên layer đầu là chuỗi `new_project_first_layer_name` (câu hỏi 2). Kho không đọc cờ Cài đặt: ViewModel quyết.
- `create` gọi `ProjectRepository.create`, khi `Success` gọi `thumbnails.update(id)` (kết quả của ảnh thu nhỏ không đổi kết quả tạo; danh sách vẫn tự vẽ ảnh thiếu, T-009).

**Chuẩn bị ảnh** (`data/PhotoPreparer.kt`, `internal class PhotoPreparer @Inject constructor(@ApplicationContext context)` cộng hai hàm thuần `internal`):

- `photoSampleSize(width, height): Int`: lũy thừa của 2 nhỏ nhất sao cho cạnh dài sau khi giảm không quá 4096. Ví dụ 4032 × 3024 ra 1; 8000 × 2000 ra 2; 12000 × 9000 ra 4; 1000 × 800 ra 1. Giới hạn bộ nhớ của ảnh giải mã khoảng 4096 × 3072 × 4 byte (50 MB).
- `photoTargetSize(width, height): Pair<Int, Int>` sau khi xoay: cạnh dài lớn hơn 3840 thì thu về đúng 3840, giữ tỷ lệ (làm tròn); không phóng to. Ví dụ 4032 × 3024 ra 3840 × 2880; 4000 × 1000 ra 3840 × 960; 3000 × 2250 giữ nguyên.
- `exifTransform(orientation: Int): PhotoTransform(rotationDegrees, mirrorHorizontally)` cho 8 giá trị `ExifInterface.ORIENTATION_*` (1 giữ nguyên; 2 lật ngang; 3 xoay 180; 4 lật ngang rồi xoay 180; 5 lật ngang rồi xoay 270; 6 xoay 90; 7 lật ngang rồi xoay 90; 8 xoay 270); giá trị khác như 1.
- `prepare(uri)`, trên luồng IO: mở `ContentResolver.openInputStream` lần một đọc kích thước (`inJustDecodeBounds`), lần hai đọc `android.media.ExifInterface(InputStream)` (API 24 trở lên, có sẵn từ minSdk 26), lần ba giải mã với `inSampleSize`; dựng một `Matrix` (lật, xoay, thu nhỏ) rồi vẽ lên `Bitmap` `ARGB_8888` đã tô trắng (ảnh PNG có vùng trong suốt ra nền trắng, không ra đen sau JPEG); nén JPEG chất lượng 90, đuôi `"jpg"`; ảnh xem trước 96 × 96 cắt giữa cho nút "Photo". Giải phóng bitmap trung gian bằng `recycle()`. Trả `null` khi luồng mở không được, `IOException`, `SecurityException`, ảnh không giải mã được (ví dụ HEIC trên Android 8) hay `OutOfMemoryError`; mỗi lần ghi `logger.warn("NewProject", "Photo not usable (<loại lỗi>)")`, không ghi đường dẫn hay tên tệp.

**UiState** (`create/NewProjectUiState.kt`):

```kotlin
sealed interface NewProjectUiState {
    data object Loading : NewProjectUiState
    data class Ready(
        val name: String, val defaultName: String,
        val aspectRatio: AspectRatio, val resolution: Resolution, val frameRate: FrameRate,
        val background: BackgroundChoice, val photoPreview: ImageBitmap?,
        val photoLoading: Boolean, val creating: Boolean, val error: NewProjectError?,
    ) : NewProjectUiState
}
sealed interface BackgroundChoice { data class Solid(val color: BackgroundColor) : BackgroundChoice; data object Photo : BackgroundChoice }
enum class NewProjectError { NO_SPACE, WRITE_FAILED }
sealed interface NewProjectEffect {
    data class ProjectCreated(val id: ProjectId, val interstitialAllowed: Boolean) : NewProjectEffect
    data object ShowComingSoon : NewProjectEffect
    data object ShowPhotoFailed : NewProjectEffect
    data object Close : NewProjectEffect
}
```

**ViewModel** (`create/NewProjectViewModel.kt`, `@HiltViewModel`, nhận `NewProjectRepository`, `FeatureFlags`, `Analytics`, `AppLogger`):

- Lúc khởi tạo: `defaults()` một lần, kết hợp với giá trị đầu tiên của `FeatureFlag.SETTINGS` (tắt thì 9:16, 12). Giá trị chọn sẵn tính một lần; cờ đổi sau đó không đè lựa chọn của người dùng. Độ phân giải 1080p, nền trắng, tên bằng tên mặc định.
- Theo dõi `FeatureFlag.EDITOR` (giữ giá trị mới nhất, như `ProjectsViewModel.editorEnabled`) và `FeatureFlag.PROJECTS` (tắt thì phát `Close` một lần).
- `onNameChange(text)`: giữ tối đa 40 code point (dán chuỗi dài thì cắt), không `trim` khi đang gõ.
- `onAspectRatio`, `onResolution`, `onFrameRate`, `onSolidBackground(color)`: đổi lựa chọn, xóa `error`.
- `onPhotoPicked(uri: String?)`: `null` (đóng bộ chọn) không đổi gì. Có uri: `photoLoading = true`, gọi `preparePhoto`; được thì giữ `PreparedPhoto` trong ViewModel (không đưa bytes vào `UiState`), `background = Photo`, `photoPreview`; `null` thì giữ nền trước, phát `ShowPhotoFailed`. Lần chọn ảnh mới thay ảnh cũ. Chọn lại một màu thì nền là màu, ảnh đã chuẩn bị vẫn giữ để bấm "Photo" lần sau mở lại bộ chọn (không tự chọn lại ảnh cũ).
- `onCreate()`: bỏ qua khi `creating`, `photoLoading`, hay đã tạo xong (một ViewModel tạo tối đa một dự án). Cờ màn vẽ tắt: phát `ShowComingSoon`, không gọi kho, ghi log `Create blocked: editor off`. Ngược lại: `creating = true`; `countBefore = createdCount()`; tên `name.trim()` trống thì tên mặc định; `create(NewProject(...))`. `Success`: ghi `project_create`, log `Project created`, phát `ProjectCreated(id, interstitialAllowed = countBefore >= 1)`, giữ `creating = true` để nút không bấm lại được. `Failure(NO_SPACE)` hay `WRITE_FAILED`: `creating = false`, `error` tương ứng, log `Create failed: <lý do>`; lựa chọn giữ nguyên.
- Sự kiện `project_create` (hằng `EVENT_PROJECT_CREATE = "project_create"`): `ratio` = `9:16` / `16:9` / `1:1`; `size` = `720p` / `1080p`; `fps` = số dạng chuỗi; `background` = `white` / `dark` / `blue` / `photo`; `is_default` = `"true"` khi bốn nhóm (khung hình, độ phân giải, tốc độ, nền) bằng giá trị chọn sẵn lúc mở màn, không xét tên (câu hỏi 3). Không tham số nào khác; không ghi tên dự án.

**Màn hình** (`create/NewProjectScreen.kt`, `create/NewProjectOptions.kt`):

- `NewProjectRoute(viewModel, onClose, onOpenProject: ((ProjectId) -> Unit)?, createAd: @Composable (onFinished: () -> Unit) -> () -> Unit = { rememberShowFullScreenAd(AdPlacement.CREATE_INTER, it) }, newProjectAd: @Composable (Modifier) -> Unit = { NativeAdSlot(AdPlacement.NEW_PROJECT_NATIVE, it, keepSpaceWhenEmpty = true) })`. Hai tham số quảng cáo để test thay bằng bản giả (CLAUDE.md, bước 7).
- Photo Picker: `rememberLauncherForActivityResult(PickVisualMedia())`. Trước khi `launch` gọi `adSuppression.hold()` (`rememberAdSuppression()` của `:core:ads`, G-03 cách 1), dòng đầu của callback gọi `release()`, rồi `viewModel.onPhotoPicked(uri?.toString())`. Không cần quyền: Android 13 trở lên (và Android 11, 12 có tiện ích SDK 2) mở bộ chọn ảnh của hệ thống; máy cũ hơn, thư viện `activity` tự chuyển sang bộ chọn ảnh bản cài qua Google Play (nếu có) hoặc `ACTION_OPEN_DOCUMENT` với `image/*`. Quyền đọc uri do bộ chọn cấp tạm thời cho app; ảnh được chuẩn bị ngay khi chọn, không giữ uri.
- Hiệu ứng (`LaunchedEffect(viewModel)`): `ProjectCreated` ghi id vào `rememberSaveable` `pendingOpen`; nếu `interstitialAllowed` và vòng đời của `LocalLifecycleOwner` đang `RESUMED` thì gọi hành động của `createAd`, `onFinished` mở dự án; ngược lại mở ngay (app ra nền giữa lúc ghi thì không có quảng cáo khi quay lại). Mở dự án: `onOpenProject?.invoke(id) ?: onClose()`; xóa `pendingOpen`. `ShowComingSoon`, `ShowPhotoFailed`: thông báo ngắn trên `SnackbarHost` của màn. `Close`: `onClose()`. Lỗi trong `UiState` hiện dưới dạng thông báo ngắn rồi `viewModel.onErrorShown()`.
- `NewProjectScreen(uiState, callbacks..., newProjectAd, snackbarHostState)` không trạng thái: `InContentAds(listOf(AdPlacement.NEW_PROJECT_NATIVE)) { ... }` bọc gốc (MC-13); `Column(fillMaxSize)`: thanh trên 64 dp (nút `close` 48 dp, tiêu đề `titleLarge`), phần cuộn `weight(1f).verticalScroll` lề 16 dp, lề trên 8 dp, các nhóm cách 24 dp (`Spacing.lg`): ô tên `OutlinedTextField` một dòng có nhãn "Name"; nhóm "Canvas" ba ô 84 dp cao, bo 14 dp, ô chọn nền `primaryContainer` viền 2 dp `primary`, ô khác `surfaceContainer`, hình chữ nhật minh họa 18 × 32, 32 × 18, 26 × 26; nhóm "Size", "Frame rate" là chip 28 dp bo 8 dp (`primaryContainer` khi chọn, `surfaceVariant` khi không); ô quảng cáo `newProjectAd(Modifier.fillMaxWidth())` giữa "Frame rate" và "Background", cách mỗi nhóm 24 dp; nhóm "Background" ba ô tròn 36 dp (vòng 2 dp `onSurface` quanh ô đang chọn, màu 28 dp khi chọn, 32 dp khi không, viền 1 dp `outlineVariant`) với màu từ `ProjectBackgroundColors`, nút tròn 48 dp `surfaceVariant` biểu tượng `image` (có ảnh thì vẽ ảnh xem trước cắt tròn, có vòng khi nền là ảnh) và chữ "Photo". Phần đáy ngoài phần cuộn: lề 24/16 dp, `PrimaryButton` "Create" 48 dp, biểu tượng `check`; tắt khi `creating` hay `photoLoading`. Màn rộng hơn 600 dp: nội dung `widthIn(max = 600.dp)` căn giữa (câu hỏi 21 của spec).
- Khoảng cách ngoài token (10, 12, 14, 20 dp) đặt thành hằng riêng của tệp như `ProjectsScreen.kt`. Màu từ `MaterialTheme.colorScheme` và `ProjectBackgroundColors`, không viết cứng.
- Tag: `NewProjectTags` (`NAME_FIELD`, `RATIO_<giá trị>`, `SIZE_<giá trị>`, `FPS_<số>`, `BG_WHITE`, `BG_DARK`, `BG_BLUE`, `BG_PHOTO`, `CREATE`, `AD`, `CONTENT`).

**Điều hướng:**

- `navigation/NewProjectNavigation.kt`: `@Serializable data object NewProjectDestination`, `NavGraphBuilder.newProjectScreen(onClose: () -> Unit, onOpenProject: ((ProjectId) -> Unit)?)`.
- `AppNavHost.kt`: `AppScreens.projects` nhận thêm `onNewProject: () -> Unit` (kiểu `NavGraphBuilder.(ProjectsCallbacks) -> Unit` với `data class ProjectsCallbacks(val onFeatureDisabled: () -> Unit, val onNewProject: () -> Unit)`), mặc định truyền `onNewProject` cho `projectsScreen`; thêm `AppScreens.newProject: NavGraphBuilder.(onClose: () -> Unit) -> Unit`, mặc định `newProjectScreen(onClose = it, onOpenProject = null)`. `onNewProject` gọi `navController.navigate(NewProjectDestination) { launchSingleTop = true }`; `onClose` gọi `navController.popBackStack(NewProjectDestination, inclusive = true)`. Màn vẽ chưa có: `onOpenProject = null` nên tạo xong quay về danh sách, dự án mới ở đầu (câu hỏi 1). T-018 nối `onOpenProject` với `popUpTo<NewProjectDestination> { inclusive = true }` để nút quay lại ở màn vẽ về danh sách.
- Thanh điều hướng của vỏ app chỉ hiện ở danh sách (`showsNavigationBar` theo route), nên màn Tạo dự án không có thanh; test vỏ app kiểm điều này.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `feature/projects/build.gradle.kts` | Thêm `implementation(project(":core:data"))` để đọc `DeviceSettingsRepository` (feature phụ thuộc core, đúng ADR-0001) |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/NewProjectRepository.kt` | Interface `NewProjectRepository`, `NewProjectDefaults`, `PreparedPhoto` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultNewProjectRepository.kt` | Cài đặt `internal`: giá trị chọn sẵn, tên mặc định, tạo rồi vẽ ảnh thu nhỏ, chuẩn bị ảnh |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/PhotoPreparer.kt` | `PhotoPreparer`, `photoSampleSize`, `photoTargetSize`, `exifTransform`, `PhotoTransform` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/di/ProjectsModule.kt` | `@Binds` `DefaultNewProjectRepository` thành `NewProjectRepository` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectUiState.kt` | `NewProjectUiState`, `BackgroundChoice`, `NewProjectError`, `NewProjectEffect` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectViewModel.kt` | ViewModel của mục 3.1, hằng `EVENT_PROJECT_CREATE` và tên tham số |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectScreen.kt` | `NewProjectRoute`, `NewProjectScreen`, `NewProjectTags` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectOptions.kt` | Ô khung hình, chip, ô màu, nút "Photo" |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/navigation/NewProjectNavigation.kt` | `NewProjectDestination`, `newProjectScreen()` |
| Sửa | `feature/projects/src/main/res/values/strings.xml` | Chuỗi tiếng Anh: tiêu đề, "Name", "Canvas", "Size", "Frame rate", "Background", "Photo", "Create", mô tả nút đóng, tên màu cho trợ năng ("White", "Dark", "Blue"), `new_project_default_name`, `new_project_first_layer_name`, lỗi hết chỗ, lỗi ghi, lỗi ảnh; "%1$d fps", "720p", "1080p", "9:16", "16:9", "1:1" ghi `translatable="false"` khi không dịch |
| Sửa | `feature/projects/src/main/res/values-vi/strings.xml` | Bản tiếng Việt của các chuỗi dịch được |
| Sửa | `app/src/main/kotlin/com/vgl/draw/animation/navigation/AppNavHost.kt` | `ProjectsCallbacks`, `AppScreens.newProject`, nối `onNewProject`, `onClose` (mục 3.1) |
| Sửa | `app/src/test/kotlin/com/vgl/draw/animation/navigation/AppNavHostTest.kt` | Bản giả của màn Tạo dự án theo kiểu mới của `AppScreens.projects`; test của mục 4 |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/create/FakeNewProjectRepository.kt` | Kho giả: giá trị chọn sẵn đặt được, đếm lần `create`, kết quả đặt được, `create` có thể treo bằng `CompletableDeferred`, `preparePhoto` trả ảnh hay `null` |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectViewModelTest.kt` | Unit test ViewModel |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectScreenTest.kt` | Test giao diện Robolectric của `NewProjectScreen` |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectRouteTest.kt` | Test của `NewProjectRoute`: quảng cáo, ra nền, cờ, thông báo, dựng lại activity |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectScreenScreenshotTest.kt` | Screenshot test |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultNewProjectRepositoryTest.kt` | Unit test kho (Robolectric cho chuỗi) với `ProjectRepository`, `ProjectThumbnails`, `DeviceSettingsRepository` giả |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/data/PhotoPreparerTest.kt` | Hàm thuần và chuẩn bị ảnh thật trên Robolectric `NATIVE` |
| Tạo | `feature/projects/src/test/screenshots/NewProjectScreen_default_light.png` | Ảnh mới, cùng tên ảnh thiết kế |
| Tạo | `feature/projects/src/test/screenshots/NewProjectScreen_default_dark.png` | Ảnh mới, cùng tên ảnh thiết kế |
| Tạo | `feature/projects/src/test/screenshots/NewProjectScreen_default_small.png` | Ảnh mới, khung 360 × 640, cuộn hết xuống |
| Tạo | `feature/projects/src/test/screenshots/NewProjectScreen_photo_light.png` | Ảnh mới: nền là ảnh, 16:9, 720p, 24 fps |
| Tạo | `feature/projects/src/test/screenshots/NewProjectScreen_default_unfolded.png` | Ảnh mới, khung 673 × 841, một cột căn giữa |

Không sửa: `ProjectsScreen.kt`, `ProjectsViewModel.kt`, `ProjectsNavigation.kt` (tham số `onNewProject` đã có), `:core:project`, `:core:data`, `:core:ads`, `consumer-rules.pro` (không lớp nào cần giữ tên khi R8 chạy: đích điều hướng `@Serializable` đã có luật của Navigation, như `ProjectsDestination`). Ảnh chuẩn có sẵn không đổi: màn danh sách không đổi. Không script hay workflow nào đọc các file trên theo tên (`grep -rn "AppNavHost\|feature/projects" scripts/ .github/` chỉ thấy `scripts/design-compare.sh` đọc ảnh theo mẫu tên thiết kế).

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: không thêm cờ. Đọc `FeatureFlag.PROJECTS` (tắt thì đóng màn), `FeatureFlag.EDITOR` (tắt thì "Coming soon", không tạo), `FeatureFlag.SETTINGS` (tắt thì 9:16, 12), cả ba bằng `Flow`.
- Quảng cáo: hai vị trí đã có trong spec mục 5 và trong `AdPlacement` (T-007): `new_project_native` (`NEW_PROJECT_NATIVE`, native medium, `keepSpaceWhenEmpty = true`, khai `InContentAds`) và `create_inter` (`CREATE_INTER`, interstitial, chỉ khi `createdCount` trước lần tạo từ 1 trở lên; khoảng cách `ads_interval` và combo `native_combo` do `:core:ads` lo). Không thêm vị trí, không đổi định dạng.
- Sự kiện phân tích: `project_create` với `ratio`, `size`, `fps`, `background`, `is_default` đúng spec mục 6. Không thêm sự kiện hay tham số.
- File R2: Không chạm.
- Dữ liệu trên máy: không thêm khóa. Đọc khóa của T-005 và bộ đếm `projects_created_count` của T-002 qua giao diện có sẵn; tệp dự án do `ProjectRepository` ghi.
- **Quyền truy cập:** Không thêm. Đã tra từ minSdk 26 tới targetSdk 36: `PickVisualMedia` không cần quyền ở mọi bản (Android 13 trở lên và Android 11, 12 có tiện ích SDK 2 dùng bộ chọn ảnh của hệ thống; bản cũ hơn chuyển sang bộ chọn qua Google Play hay `ACTION_OPEN_DOCUMENT`, đều cấp quyền đọc uri tạm thời); `ContentResolver.openInputStream` trên uri đó không cần quyền; ghi tệp dự án trong thư mục riêng của app không cần quyền. Không thêm: `READ_MEDIA_IMAGES`, `READ_EXTERNAL_STORAGE`, `READ_MEDIA_VISUAL_USER_SELECTED` (spec mục 6: "Chọn ảnh nền dùng bộ chọn ảnh của hệ thống, không cần quyền"). Không thêm khai báo dịch vụ `ModuleDependencies` cho bộ chọn ảnh bản Google Play trong manifest (câu hỏi 5).
- **Thư viện và SDK:** Không thêm thư viện mới. `PickVisualMedia`, `rememberLauncherForActivityResult` thuộc `androidx-activity-compose` (phiên bản 1.10.1 trong `gradle/libs.versions.toml`), convention plugin `template.android.feature` đã thêm `implementation` cho mọi feature. `ExifInterface` dùng lớp của nền tảng `android.media.ExifInterface` (hàm dựng nhận `InputStream` từ API 24), không dùng bản AndroidX. `rememberAdSuppression`, `rememberShowFullScreenAd`, `NativeAdSlot`, `InContentAds` của `:core:ads`; `DeviceSettingsRepository` của `:core:data` (thêm phụ thuộc module, không phải thư viện ngoài); `ProjectRepository`, `ProjectThumbnails` của `:core:project` (đã có).

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** đọc EXIF và giải mã JPEG thật trong `PhotoPreparerTest` trên Robolectric `NATIVE`.
- **Điều kiện chuyển:** test chuẩn bị ảnh thật (xoay 90, thu nhỏ, nền trắng) thất bại vì Robolectric không giải mã hay không đọc được EXIF (bitmap `null`, hướng luôn 1) sau 2 lần sửa.
- **Cách làm thay:** tách `PhotoPreparer.prepare` thành hai bước: `readOrientation(stream)` và `transform(bitmap, orientation)`. Test xoay, thu nhỏ, nền trắng gọi `transform` với `Bitmap` dựng trong test (không qua JPEG); test đọc hướng dùng tệp JPEG mẫu nhỏ có EXIF ghi bằng `ExifInterface.saveAttributes` trong test. Câu khẳng định của mục 4 giữ nguyên. Không thêm tệp ngoài mục 3.2.

### 3.5. Hành vi đã có bị đổi

`scripts/existing-tests.py` liệt kê (xem kết quả dưới bảng). Hành vi bị đổi:

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| `AppScreens.projects` có kiểu `NavGraphBuilder.(onFeatureDisabled: () -> Unit) -> Unit` | Kiểu `NavGraphBuilder.(ProjectsCallbacks) -> Unit` | `AppNavHostTest` (bản giả `fakeScreens.projects`, mọi test dùng nó) | Câu khẳng định giữ nguyên. Bản giả đổi chữ ký: `projects = { callbacks -> ... callbacks.onFeatureDisabled() ... }` và thêm nút giả gọi `callbacks.onNewProject()`; đây là dữ liệu dựng test theo phần plan thêm (ADR-0029), không phải câu khẳng định |
| Danh sách bấm "New animation" hiện "Coming soon" (vỏ app truyền `onNewProject = null`) | Vỏ app truyền `onNewProject`, danh sách mở màn Tạo dự án | Không test nào của `:app` kiểm "Coming soon" của nút này; `ProjectsRouteTest` kiểm nhánh `null` của `ProjectsRoute` với tham số truyền thẳng | Giữ: `ProjectsRoute` không đổi, nhánh `null` vẫn đúng cho route |

Kết quả `scripts/existing-tests.py` (2026-11-23): 3 test dùng tên của file plan sửa; "Cần xem trước" 0, "Nên xem" 0. Ba test còn lại (`--all`): `AppNavHostTest.recreate_keepsProjectsScreen` (giữ nguyên câu khẳng định, chỉ đổi bản giả như dòng đầu của bảng), `ScreenNameTest.reportedScreen_nullBeforeFirstEntryAndOnLoading` và `NetworkDialogControllerTest.sameScreenReportedAgain_keepsDialogAndRecordsNothing` (chỉ nhắc `AppNavHost` trong chú thích; cách báo màn cho popup mất mạng không đổi, route mới được báo như mọi route khác). Không test nào phải viết lại câu khẳng định.

## 4. Kiểm thử

Các test ViewModel dùng `MainDispatcherRule`, `FakeFeatureFlags` hay `MutableStateFlow` cho cờ như `ProjectsViewModelTest`, `RecordingAppLogger`, `FakeAnalytics` (có sẵn trong test của module hay viết trong `FakeNewProjectRepository.kt` khi chưa có).

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| KB-3: giá trị chọn sẵn theo Cài đặt | Unit test | `NewProjectViewModelTest.defaults_followSettings`: kho giả trả 16:9, 24 fps, cờ Cài đặt bật; `Ready` có `LANDSCAPE_16_9`, `FPS_24`, `P1080`, `Solid(WHITE)`, tên "My animation 5" (kho giả trả tên này). Đối chứng: cùng test với 1:1, 6 fps ra đúng 1:1, 6 (không phải giá trị cứng) |
| Settings câu 15: cờ Cài đặt tắt | Unit test | `NewProjectViewModelTest.settingsFlagOff_uses916And12`: kho giả trả 16:9, 24; cờ tắt; `Ready` có 9:16, 12. `settingsFlagTurnsOnLater_keepsChoices`: cờ bật sau khi `Ready` thì lựa chọn không đổi |
| KB-3: tên "My animation <số>", số = đã tạo + 1 | Unit test | `DefaultNewProjectRepositoryTest.defaultName_isCreatedCountPlusOne`: `createdCount` giả 4 ra "My animation 5"; 0 ra "My animation 1" |
| KB-3: chuỗi Cài đặt lạ | Unit test | `DefaultNewProjectRepositoryTest.unknownSettings_fallBackTo916And12`: `"4:5"`, 7 fps ra 9:16, 12; đối chứng `"1:1"`, 30 ra 1:1, 30 |
| KB-3: "Create" không đổi gì thì tạo với mặc định | Unit test | `NewProjectViewModelTest.create_withDefaults_createsOnceWithDefaults`: một lần `create` với `NewProject(name = "My animation 5", PORTRAIT_9_16, P1080, FPS_12, Solid(WHITE), firstLayerName = <kho giả>)`; phát `ProjectCreated` |
| KB-3: kích thước khung theo lựa chọn | Unit test | `NewProjectViewModelTest.create_passesChosenFormat`: chọn 16:9, 720p, 30 rồi tạo; `NewProject` có `LANDSCAPE_16_9`, `P720`, `FPS_30`; `canvasSize` của giá trị đó là 1280 × 720 (kích thước đã có test ở `:core:project`) |
| KB-3: mỗi nhóm đúng một lựa chọn | Compose UI test | `NewProjectScreenTest.eachGroup_hasExactlyOneSelected`: mỗi nhóm đếm node `isSelected()` bằng 1; bấm `RATIO_16_9` gọi callback với 16:9; `NewProjectScreenTest.selectedTile_showsSelectedState` dựng `Ready` với 16:9 thì `RATIO_16_9` `assertIsSelected`, `RATIO_9_16` `assertIsNotSelected` |
| KB-3: tiêu đề, nhóm, chữ đúng bảng chữ của thiết kế | Compose UI test | `NewProjectScreenTest.texts_matchDesign`: có "New animation", "Name", "Canvas", "9:16", "16:9", "1:1", "Size", "720p", "1080p", "Frame rate", "6", "8", "12 fps", "15", "24", "30", "Background", "Photo", "Create"; không có "4:5" |
| KB-3: tên trống thì dùng tên mặc định | Unit test | `NewProjectViewModelTest.blankName_usesDefaultName`: `onNameChange("   ")` rồi tạo; `NewProject.name == "My animation 5"` |
| KB-3: tối đa 40 ký tự | Unit test | `NewProjectViewModelTest.name_keepsAtMost40CodePoints`: dán 45 ký tự thì giữ 40 đầu; 39 chữ cộng một emoji ghép cặp thay thế (`"😀"`) giữ đủ 40 code point, không cắt đôi emoji |
| KB-3: "Photo" chọn ảnh thì ảnh là nền | Unit test | `NewProjectViewModelTest.photoPicked_setsPhotoBackground`: `onPhotoPicked("content://x")`; `background == Photo`, `photoPreview != null`; tạo thì `NewProject.background` là `NewBackground.Photo` với đúng bytes và đuôi `"jpg"` của kho giả |
| KB-3: đóng bộ chọn thì giữ lựa chọn trước | Unit test | `NewProjectViewModelTest.photoPickerCancelled_keepsPrevious`: chọn `BLUE`, `onPhotoPicked(null)`; vẫn `Solid(BLUE)`, kho không được gọi `preparePhoto` |
| KB-3: ảnh không dùng được | Unit test | `NewProjectViewModelTest.photoNotUsable_keepsPreviousAndTells`: kho trả `null`; nền giữ `Solid(DARK)`, phát `ShowPhotoFailed`, `photoLoading == false` |
| KB-3: bộ chọn mở không hỏi quyền, chặn quảng cáo tự hiện | Compose UI test | `NewProjectRouteTest.photoButton_launchesPhotoPicker`: dùng `ActivityResultRegistry` giả qua `LocalActivityResultRegistryOwner`; bấm `BG_PHOTO` thì registry nhận một lần với `PickVisualMediaRequest` loại `ImageOnly` (kiểm `Intent` do `PickVisualMedia().createIntent` sinh có action là bộ chọn ảnh hay `ACTION_OPEN_DOCUMENT`, không phải yêu cầu quyền); registry trả uri thì ViewModel giả nhận đúng chuỗi; trả `null` thì nhận `null` |
| KB-3: ảnh xoay theo EXIF, thu nhỏ tới 3840 | Unit test | `PhotoPreparerTest.exifTransform_allOrientations` (8 giá trị cộng giá trị lạ, theo bảng mục 3.1); `photoSampleSize_examples`, `photoTargetSize_examples` (các ví dụ của mục 3.1) |
| KB-3: ảnh xoay đúng trên tệp thật | Unit test (Robolectric `NATIVE`) | `PhotoPreparerTest.prepare_rotatesByExif`: JPEG 40 × 20, nửa trái đỏ, nửa phải xanh, ghi EXIF hướng 6 bằng `ExifInterface.saveAttributes`, mở qua uri `file://`; kết quả giải mã 20 × 40, điểm ảnh ở (10, 5) đỏ, (10, 35) xanh (sai số mỗi kênh 40 vì JPEG). Đối chứng: cùng ảnh hướng 1 ra 40 × 20 |
| KB-3: ảnh lớn thu nhỏ | Unit test (Robolectric `NATIVE`) | `PhotoPreparerTest.prepare_scalesLongSideTo3840`: PNG 8000 × 2000 ra 3840 × 960; PNG 1000 × 800 giữ 1000 × 800 |
| KB-3: vùng trong suốt ra trắng | Unit test (Robolectric `NATIVE`) | `PhotoPreparerTest.prepare_transparentBecomesWhite`: PNG 10 × 10 trong suốt hoàn toàn; điểm giữa của kết quả trắng (mỗi kênh từ 245) |
| KB-3: tệp không phải ảnh | Unit test (Robolectric `NATIVE`) | `PhotoPreparerTest.prepare_notAnImage_returnsNull` (tệp chữ), `prepare_missingFile_returnsNull` |
| KB-3: ảnh gốc bị xóa không đổi nền | Unit test | `DefaultNewProjectRepositoryTest.create_photo_writesBytesIntoProject`: `ProjectRepository` giả nhận `NewBackground.Photo` có đúng bytes đã chuẩn bị (kho dự án ghi `background.jpg` trong thư mục dự án, đã có test ở T-002), không nhận uri |
| T-004: vẽ ảnh thu nhỏ sau khi tạo | Unit test | `DefaultNewProjectRepositoryTest.create_success_updatesThumbnail`: `ProjectThumbnails.update` gọi một lần với id mới; `create_failure_noThumbnail`: kết quả `Failure` thì không gọi |
| KB-3: nút đóng về danh sách, không tạo | Compose UI test và test vỏ app | `NewProjectScreenTest.close_callsOnClose`; `AppNavHostTest.newProject_closeReturnsToProjects` và `newProject_backReturnsToProjects` (bản giả của màn Tạo dự án có nút đóng; nút quay lại của máy qua `onBackPressedDispatcher`): sau đó `PROJECTS` có, `NEW_PROJECT` không, activity chưa `isFinishing` |
| KB-3: "New animation" mở màn Tạo dự án | Test vỏ app | `AppNavHostTest.newAnimation_opensNewProject`: bấm nút giả gọi `onNewProject` thì có `NEW_PROJECT`; bấm hai lần nhanh chỉ một mục trong back stack (quay lại một lần là về `PROJECTS`) |
| KB-3: màn Tạo dự án không có thanh điều hướng | Test vỏ app | `AppNavHostTest.newProject_hasNoNavigationBar`: `lessonsEnabled = true`; ở `PROJECTS` có thanh, ở `NEW_PROJECT` không có node `TopLevelTab` nào |
| KB-3: dựng lại activity giữ màn Tạo dự án | Test vỏ app | `AppNavHostTest.recreate_keepsNewProjectScreen`: `StateRestorationTester`, mở màn Tạo dự án, `emulateSavedInstanceStateRestore()`; vẫn `NEW_PROJECT`, quay lại về `PROJECTS` |
| KB-3: tạo xong, màn vẽ chưa có thì về danh sách | Compose UI test | `NewProjectRouteTest.created_noEditorDestination_closes`: `onOpenProject = null`, kho giả tạo thành công; `onClose` gọi một lần |
| KB-3: tạo xong mở màn vẽ | Compose UI test | `NewProjectRouteTest.created_opensProject`: `onOpenProject` nhận đúng id một lần |
| KB-3: dự án đầu tiên không có quảng cáo | Unit test và Compose UI test | `NewProjectViewModelTest.firstProject_interstitialNotAllowed` (`createdCount` 0 thì `interstitialAllowed == false`; 1 thì `true`; đọc lúc bấm: đổi `createdCount` của kho giả từ 0 sang 3 sau khi mở màn thì `true`); `NewProjectRouteTest.firstProject_noAd_opensAtOnce`: `createAd` giả đếm lần gọi, `interstitialAllowed = false` thì 0 lần, `onOpenProject` gọi ngay |
| Spec mục 5: `create_inter` từ dự án thứ 2, mở sau khi quảng cáo đóng | Compose UI test | `NewProjectRouteTest.secondProject_showsAdThenOpens`: `createAd` giả giữ `onFinished`; sau khi tạo, giả được gọi một lần, `onOpenProject` chưa gọi; gọi `onFinished` thì `onOpenProject` gọi đúng một lần với id |
| Ra nền giữa lúc tạo thì không có quảng cáo khi quay lại | Compose UI test | `NewProjectRouteTest.createFinishesInBackground_noAdOnReturn`: `create` treo; bấm "Create"; `activityRule.scenario.moveToState(CREATED)`; hoàn tất `create`; `moveToState(RESUMED)`; `createAd` giả 0 lần, `onOpenProject` một lần |
| Spec mục 5: `new_project_native` giữa "Frame rate" và "Background", cách 24 dp | Compose UI test (`NATIVE`) | `NewProjectScreenTest.ad_betweenFrameRateAndBackground`: khe giả 250 dp tag `AD`; `AD.top - FPS_30.bottom` và `"Background".top - AD.bottom` trong khoảng 24 ± 1 dp; khe nằm trong `CONTENT` (phần cuộn), không trong phần đáy |
| Spec mục 5: giữ chỗ khi chưa có quảng cáo | Người (đọc code) và rà soát | Khe mặc định gọi `NativeAdSlot(..., keepSpaceWhenEmpty = true)`; hành vi giữ chỗ đã có test ở `:core:ads`. `NewProjectScreenTest.defaultAdSlot_isNewProjectNative` kiểm khe mặc định không ném lỗi khi không có `AdsController` (test không có controller nên không vẽ gì) |
| MC-13: khai `InContentAds` | Compose UI test | `NewProjectScreenTest.declaresInContentAds`: đọc danh sách vị trí đã khai qua điểm nối test có sẵn của `InContentAds` nếu có; không có thì kiểm bằng rà soát code (ghi ở nhật ký) |
| Spec mục 6: `project_create` và tham số | Unit test | `NewProjectViewModelTest.create_logsProjectCreate_defaults`: đúng một sự kiện `AnalyticsEvent("project_create", mapOf("ratio" to "9:16", "size" to "1080p", "fps" to "12", "background" to "white", "is_default" to "true"))`; `create_logsProjectCreate_changed`: 16:9, 720p, 30, photo thì `"16:9"`, `"720p"`, `"30"`, `"photo"`, `"false"`; `changedThenBack_isDefaultTrue`; `nameChangedOnly_isDefaultTrue` (theo câu hỏi 3); `createFailed_logsNothing` |
| Spec mục 6: không ghi tên dự án | Unit test | Trong các test trên: không tham số nào chứa tên đã nhập ("Secret name") |
| KB-3: hết chỗ thì báo lỗi, giữ lựa chọn | Unit test và Compose UI test | `NewProjectViewModelTest.noSpace_keepsChoicesAndShowsError`: kho trả `Failure(NO_SPACE)`; `error == NO_SPACE`, `creating == false`, lựa chọn 16:9, 720p giữ nguyên, không phát `ProjectCreated`, không sự kiện; bấm lại "Create" thì gọi kho lần hai. `NewProjectRouteTest.noSpace_showsMessage`: thông báo có chữ "Not enough space on your phone. Free up some space and try again." |
| KB-3: lỗi ghi khác | Unit test | `NewProjectViewModelTest.writeFailed_showsGenericError` |
| KB-3: bấm "Create" hai lần nhanh | Unit test và Compose UI test | `NewProjectViewModelTest.doubleCreate_createsOnce`: `create` treo, gọi `onCreate()` hai lần, hoàn tất; kho đếm 1, một `ProjectCreated`. Đối chứng: sau `Failure` gọi lại thì đếm 2. `NewProjectScreenTest.createDisabledWhileCreating`: `creating = true` thì `CREATE` `assertIsNotEnabled` |
| KB-2: cờ màn vẽ tắt thì "Coming soon", không tạo | Unit test và Compose UI test | `NewProjectViewModelTest.editorOff_comingSoonNoCreate`: kho đếm 0, phát `ShowComingSoon`, không sự kiện; cờ bật lại rồi bấm thì tạo (cổng mở lại). `editorTurnsOffAfterOpen_blocks`: cờ bật lúc mở, tắt trước khi bấm thì không tạo. `NewProjectRouteTest.editorOff_showsComingSoon` thấy chữ "Coming soon" |
| KB-13: cờ projects tắt | Unit test | `NewProjectViewModelTest.projectsFlagOff_closes`: phát `Close` một lần |
| KB-12: xoay, gập giữ lựa chọn | Compose UI test | `NewProjectRouteTest.recreate_keepsChoices`: ViewModel tạo bằng `viewModel { NewProjectViewModel(...) }` trong activity; chọn 16:9, 720p, `BLUE`, tên "Ball"; `activityRule.scenario.recreate()`; `RATIO_16_9`, `SIZE_720P`, `BG_BLUE` được chọn, ô tên "Ball" |
| KB-3: màn 360 × 640, "Create" luôn thấy, không che lựa chọn cuối | Compose UI test (`NATIVE`, khung `w360dp-h640dp-xxhdpi`) | `NewProjectScreenTest.small_createVisibleAndLastRowReachable`: khe giả 250 dp; `CREATE` nằm trọn trong cửa sổ; `performScrollTo` nút `BG_PHOTO` rồi `BG_PHOTO.bottom <= CREATE` phần đáy `.top` |
| Màn hình thấp | Screenshot test khung `w360dp-h640dp-xxhdpi`, trạng thái nhiều nội dung nhất | `NewProjectScreen_default_small.png` (khe giả 250 dp, cuộn hết xuống) |
| Đúng thiết kế | Screenshot test cùng tên với từng ảnh thiết kế, ảnh so sánh `scripts/design-compare.sh projects`, rà soát độc lập theo mục 7 của mẫu thiết kế | `NewProjectScreen_default_light.png`, `NewProjectScreen_default_dark.png`: khung `en-w411dp-h891dp-xxhdpi`, đồ họa `NATIVE`, khe giả `NativeAdCard` medium với chữ minh họa của thiết kế, tên "My animation 5". Lệch có chủ ý: ô màu thứ hai (mục 2) |
| Ảnh thiết kế của ticket khác (T-009, T-012, T-013): `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark`, `ProjectsScreen_list_light`, `ProjectsScreen_list_dark`, `ProjectsScreen_list_light_unfolded`, `ProjectsScreen_list_dark_unfolded`, `ProjectsScreen_menu_light`, `ProjectsScreen_menu_dark`, `ProjectsScreen_restoring_light`, `ProjectsScreen_restoring_dark`, `ProjectsScreen_restore-error_light`, `ProjectsScreen_restore-error_dark` | Không thuộc ticket này | Không có |
| Nền là ảnh | Screenshot test | `NewProjectScreen_photo_light.png`: `photoPreview` là ảnh dựng trong test (ô màu kẻ sọc), 16:9, 720p, 24 fps |
| Bố cục rộng (spec câu hỏi 21) | Screenshot test khung `en-w673dp-h841dp-xxhdpi` | `NewProjectScreen_default_unfolded.png`; kèm `NewProjectScreenTest.unfolded_contentAtMost600dpCentered` (bề rộng `CONTENT` ≤ 600 dp, lề trái bằng lề phải ± 1 dp) |
| Ô màu là màu cố định ở cả hai theme | Compose UI test (`NATIVE`) | `NewProjectScreenTest.swatchColors_fixedInBothThemes`: vẽ `decorView` ra `Bitmap`, đọc điểm giữa `BG_DARK` ở theme sáng và tối đều là `#22262E` (sai số 2 mỗi kênh); `BG_BLUE` là `#2F6FE0` |
| KB-3: chọn ảnh trên máy thật (bộ chọn của hãng, ảnh chụp dọc có EXIF) | Kiểm tra thủ công (máy thật: khác, bộ chọn ảnh khác nhau theo hãng và bản Android, Android 8 đến 10 dùng bộ chọn tài liệu; cần ảnh chụp bằng camera thật có EXIF xoay) | Trên bản staging: chụp một ảnh dọc, tạo dự án 9:16 nền "Photo" với ảnh đó; ảnh thu nhỏ của thẻ ở danh sách đứng đúng chiều; xóa ảnh gốc khỏi thư viện, mở lại app, thẻ vẫn có ảnh nền |
| `create_inter` thật từ dự án thứ 2 | Kiểm tra thủ công (máy thật: quảng cáo thật, Remote Config) | Bật `is_show_create_inter` trên Firebase staging; dữ liệu app mới: dự án đầu không có interstitial; dự án thứ hai có (hay log `VGL.Ads` có dòng cổng của `create_inter`); tạo tiếp trong 30 giây thì không có (`THROTTLED`) |

**Ngoại lệ viết test trước:** chuỗi trong `strings.xml` (chỉ là tài nguyên; chữ tiếng Anh được `texts_matchDesign` kiểm). Phần nối `AppNavHost.kt` mặc định (`AppScreens()` dùng màn thật cần Hilt) không có test riêng; test vỏ app kiểm cách nối bằng bản giả, `scripts/check.sh` build `:app` kiểm đồ thị Hilt. Người duyệt cần đồng ý.

### 4.1. Chạy thử

- **Đã chạy thử:** 2026-11-23, trên `fd811d0` (nhánh chính), test tạm `TmpPhotoTrialTest` trong `feature/projects/src/test` (Robolectric `NATIVE`, sdk 35), không commit, đã xóa. Test dựng JPEG 40 × 20 (nửa trái đỏ, nửa phải xanh), ghi EXIF hướng 6 bằng `android.media.ExifInterface.saveAttributes`, đọc lại hướng qua `ContentResolver.openInputStream` của uri `file://` bằng `ExifInterface(InputStream)`, giải mã bằng `BitmapFactory.decodeStream`, xoay 90 bằng `Matrix`; gọi `ActivityResultContracts.PickVisualMedia().createIntent(context, PickVisualMediaRequest(ImageOnly))`.
- **Lệnh:** `./gradlew :feature:projects:testDebugUnitTest --tests '*TmpPhotoTrialTest*'`, rồi `scripts/check.sh` (2026-11-23, cùng test tạm). Dòng cuối của `check.sh`: `== Cổng chất lượng: QUA` (Test JVM 2922 test, 0 lỗi; `TmpPhotoTrialTest` 1 test, 0 lỗi, in cùng các giá trị dưới).
- **Kết quả:** qua. Hướng đọc được là 6; ảnh giải mã 40 × 20, sau khi xoay 20 × 40; điểm (10, 5) là `#FE0000` (đỏ), điểm (10, 35) là `#0000FE` (xanh), đúng chiều xoay của mục 3.1 và đủ cho câu khẳng định của `PhotoPreparerTest.prepare_rotatesByExif` (sai số 40 mỗi kênh). `PickVisualMedia().createIntent` trên Robolectric sdk 35 trả action `android.provider.action.PICK_IMAGES`, type `image/*`, không có lớp nào thiếu trên classpath test của `:feature:projects`. Phương án dự phòng ở mục 3.4 chưa cần thử. Chưa chạy thử: đọc EXIF của PNG, thu nhỏ 8000 × 2000, `ActivityResultRegistry` giả.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 1 (quảng cáo: thêm hai vị trí `new_project_native`, `create_inter` vào màn hình), 4 (thu thập dữ liệu: thêm sự kiện `project_create`). Rủi ro Cao, cần duyệt chéo; người phụ trách sản phẩm biết trước khi duyệt (risk-scoring.md, mục 3). Bảng điểm dưới chỉ để tham khảo.

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 3 | Hai module (`:feature:projects`, `:app`), thêm phụ thuộc `:core:data` vào file build của feature, đổi kiểu công khai `AppScreens.projects` |
| B. Độ rõ của yêu cầu | 2 | Spec đã xác nhận và có thiết kế, nhưng còn câu hỏi ảnh hưởng tới cách làm: điều hướng khi chưa có màn vẽ, tên layer đầu, `is_default` có xét tên không, lời báo ảnh lỗi (mục 8) |
| C. Khả năng kiểm chứng tự động | 1 | Phần lớn có test JVM; bộ chọn ảnh của hãng, EXIF của ảnh chụp thật và quảng cáo thật cần máy thật |
| D. Ảnh hưởng chéo | 2 | Sửa `AppNavHost.kt` mà các ticket màn hình khác (T-013, T-016, T-018, T-050) cũng sửa; cùng module và gói với T-010 (khác file) |
| E. Độ mới của kỹ thuật | 2 | Photo Picker (`PickVisualMedia`), đọc EXIF và xoay, thu nhỏ ảnh lần đầu có trong dự án; chạy thử ở mục 4.1 |
| **Tổng** | 10 | **Mức: Cao** (chạm điểm chặn 1 và 4) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `app/.../AppNavHost.kt` và `AppNavHostTest.kt` (đổi kiểu `AppScreens.projects`); `feature/projects/build.gradle.kts`; `ProjectsModule.kt`; `strings.xml` hai ngôn ngữ của `:feature:projects`. Dùng mà không sửa: `ProjectRepository`, `ProjectThumbnails`, `ProjectBackgroundColors`, `DeviceSettingsRepository`, `NativeAdSlot`, `rememberShowFullScreenAd`, `rememberAdSuppression`, `InContentAds`.
- Task khác có thể xung đột:
  - T-010 (quảng cáo ở danh sách, plan nháp PR #38): sửa `ProjectsScreen.kt`, `ProjectCard.kt`, hai test screenshot và ảnh chuẩn của danh sách. Plan này không sửa file nào trong số đó; hai plan không cùng sửa file. Cả hai cùng dùng `NativeAdCard` giả trong test: plan này viết khe giả riêng trong `NewProjectScreenScreenshotTest.kt`, không dùng `FakeProjectsAdSlots.kt` của T-010, để không phụ thuộc thứ tự.
  - T-013 (sao lưu, khôi phục), T-016 (Cài đặt), T-018 (màn vẽ), T-050 (bài học): cùng sửa `AppNavHost.kt` và có thể cùng đổi `AppScreens.projects`. Plan này gom tham số thành `ProjectsCallbacks` để ticket sau chỉ thêm trường. T-018 nối `onOpenProject` của `newProjectScreen` (mục 3.1).
- Đề xuất thứ tự chạy: T-010 và T-011 chạy được theo thứ tự bất kỳ, không cùng file; nếu cùng danh sách chạy thì đề xuất T-010 trước (nhỏ hơn, chỉ ở màn danh sách), T-011 sau. T-011 chạy trước các ticket khác cùng sửa `AppNavHost.kt` (T-013, T-016, T-018), xếp tuần tự.

## 7. Điều kiện dừng riêng

- Dừng nếu cần đổi `ProjectRepository`, `NewProject`, `NewBackground` hay `BackgroundDecoder` của `:core:project` (hợp đồng với T-002, T-004).
- Dừng nếu cần thêm khai báo vào `AndroidManifest.xml` để bộ chọn ảnh chạy.
- Dừng nếu cần sửa `:core:ads` để gọi `create_inter`, `new_project_native` hay chặn quảng cáo tự hiện.

## 8. Câu hỏi cho người duyệt

1. **Tạo xong khi màn vẽ chưa có (T-018 chưa làm).** Cờ màn vẽ bật sẵn trên staging, nhưng chưa có đích màn vẽ. Đề xuất của AI: tạo dự án, hiện `create_inter` nếu đủ điều kiện, rồi quay về danh sách (dự án mới ở đầu), không hiện thông báo; T-018 nối `onOpenProject`. Phương án khác: coi như cờ màn vẽ tắt ("Coming soon", không tạo), nhưng khi đó không thử được việc tạo dự án trên staging tới khi có T-018.
2. **Tên layer đầu tiên của dự án mới** (`NewProject.firstLayerName` bắt buộc). Spec và thiết kế không ghi (thiết kế màn vẽ dùng tên minh họa "Sketch", "Ball"). Đề xuất của AI: "Layer 1" (tiếng Việt "Lớp 1"), chuỗi trong `strings.xml` của `:feature:projects`; spec editor sau này đặt tên layer mới theo cùng mẫu.
3. **`is_default` của `project_create`** có xét ô tên không? Spec ghi "người dùng không đổi lựa chọn nào". Đề xuất của AI: chỉ xét bốn nhóm (khung hình, độ phân giải, tốc độ, nền), so giá trị lúc bấm với giá trị chọn sẵn (đổi rồi đổi lại vẫn là `true`); đổi tên không làm `is_default` thành `false`.
4. **Ảnh chọn không dùng được** (không giải mã được, ví dụ HEIC trên Android 8, hay tệp hỏng). Spec không nói. Đề xuất của AI: giữ nền trước, hiện thông báo ngắn "Couldn't use this photo. Try another one." (tiếng Việt "Không dùng được ảnh này. Hãy thử ảnh khác."). Chữ mới, không có trên thiết kế.
5. **Bộ chọn ảnh bản Google Play cho Android 8 đến 10.** Muốn máy cũ có bộ chọn ảnh giống Android 13 thì thêm một khai báo dịch vụ `ModuleDependencies` vào manifest (không thêm quyền). Đề xuất của AI: không thêm ở ticket này; máy cũ không có bộ chọn qua Google Play thì dùng bộ chọn tài liệu của hệ thống, vẫn không hỏi quyền, đúng spec.
6. **Mất ảnh đã chọn khi hệ thống đóng app ở nền.** Lựa chọn giữ qua xoay và gập máy (ViewModel), nhưng khi hệ thống đóng app lúc ở nền rồi mở lại, màn Tạo dự án mở với giá trị chọn sẵn (ảnh đã chuẩn bị không được lưu). Đề xuất của AI: chấp nhận ở bản đầu; KB-12 chỉ đòi xoay và gập.
7. **Ô màu thứ hai lệch ảnh thiết kế** (`#22262E` thay `#E3E6EA`), theo quyết định đã có (plan T-004, câu hỏi 1). Ảnh so sánh của `scripts/design-compare.sh` sẽ báo lệch ở ô này tới khi Figma được sửa. Đề xuất của AI: giữ màu cố định, ghi lệch có chủ ý trong mô tả PR.
8. **`scripts/check.sh` của phần chạy thử:** đã chạy ngày 2026-11-23 cùng test tạm, kết quả `== Cổng chất lượng: QUA` (mục 4.1). Không cần người làm gì thêm.

## 9. Quyết định duyệt

<!-- Người duyệt điền, hoặc AI ghi theo lời đồng ý trong phiên của người duyệt, nguyên văn (ADR-0048, phần C). Plan `lane: quick`: ghi "Duyệt trong phiên làm nhanh" kèm lời đồng ý. -->

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện

<!-- AI ghi khi task đã có kết quả cuối (đã merge hoặc đã dừng), trên nhánh báo cáo của phiên (auto-run.md, bước 10). Mục mới thêm vào cuối, không sửa mục cũ. -->
