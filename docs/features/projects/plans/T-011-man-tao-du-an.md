---
ticket: T-011
title: Màn Tạo dự án
status: done                    # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 17.4M        # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [ads, analytics] # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer: Nhật             # người duyệt
cross_reviewer: Nhật       # bắt buộc khi risk_level là high
approved_at: 2026-11-27
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-011: MÀN TẠO DỰ ÁN

## 1. Tóm tắt

Thêm màn Tạo dự án (`NewProjectScreen`) vào `:feature:projects`: ô tên, ba nhóm chọn khung hình, độ phân giải, tốc độ khung, nhóm nền (ba màu cố định hay ảnh qua Photo Picker), nút "Create" ghim đáy, ô `new_project_native` trong phần cuộn và `create_inter` sau khi tạo từ dự án thứ 2. Dự án tạo bằng `ProjectRepository.create` của `:core:project`, ảnh nền được xoay theo EXIF và thu nhỏ tới 3840 điểm ảnh cạnh dài trước khi chép vào dự án, sự kiện `project_create` ghi khi tạo xong. Kiểm chứng bằng unit test ViewModel và kho, test Robolectric cho chuẩn bị ảnh, test giao diện, screenshot cùng tên ảnh thiết kế, test vỏ app cho điều hướng, và một kịch bản emulator cho luồng từ danh sách tới tạo dự án. Phần "mở ngay màn vẽ" do T-018 hoàn tất.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/projects/spec.md`:

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-3 | Màn có tiêu đề "New animation", nút đóng, ô "Name", nhóm "Canvas", "Size", "Frame rate", "Background", nút "Create" ghim đáy, đúng thiết kế | Theo `design/context/NewProjectScreen_default_*.md`. Nhóm Canvas ba ô (9:16, 16:9, 1:1; đã bỏ 4:5). Chip tốc độ đang chọn ghi "12 fps", chip khác chỉ ghi số (theo thiết kế) |
| KB-3 | Mỗi nhóm có sẵn một lựa chọn: khung hình, tốc độ theo Cài đặt (chưa đổi thì 9:16, 12); 1080p; nền trắng; tên "My animation <số>", số là số dự án đã từng tạo cộng một | Đọc `DeviceSettingsRepository.defaultAspectRatio`, `defaultFrameRate` (T-005) và `ProjectRepository.createdCount` một lần khi mở màn. Cờ `feature_settings_enabled` tắt thì dùng 9:16 và 12 (settings câu 15), giá trị đã lưu không bị xóa |
| KB-3 | Bấm "Create" không đổi gì thì tạo với giá trị mặc định và mở ngay màn vẽ; từ danh sách trống tới màn vẽ đúng 2 lần chạm | Danh sách đã gọi `onNewProject` (T-009). Màn vẽ chưa có, nên ticket này chỉ làm phần tạo: "Create" tạo dự án rồi về danh sách (cờ màn vẽ bật) hay hiện "Coming soon" (cờ tắt). Phần "mở ngay màn vẽ, đúng 2 lần chạm" do T-018 hoàn tất khi nối `onOpenProject` (mục 3.1, phần Điều hướng; câu hỏi 1) |
| KB-3 | Chọn lựa chọn khác thì nó sáng lên, lựa chọn cũ tắt; mỗi nhóm luôn đúng một lựa chọn | Mỗi nhóm là `selectableGroup`, mỗi ô `selectable(role = RadioButton)`; bấm lại ô đang chọn không đổi gì |
| KB-3 | Kích thước khung vẽ theo khung hình và độ phân giải | `canvasSize` của `:core:project` đã có và đã có test; plan kiểm `NewProject` truyền đúng `aspectRatio`, `resolution` |
| KB-3 | "Photo" mở bộ chọn ảnh của hệ thống, không hỏi quyền; chọn ảnh thì ảnh là nền; đóng bộ chọn thì giữ lựa chọn trước; ảnh là nền cố định, cắt vừa khung | `ActivityResultContracts.PickVisualMedia(ImageOnly)`. Cắt vừa khung là việc của phần vẽ (`BackgroundDecoder`, T-004); màn này chỉ chép ảnh đã xoay và thu nhỏ |
| KB-3 | Ảnh gốc bị xóa sau khi tạo thì nền không đổi | Bytes ảnh đã chuẩn bị được ghi thành `background.jpg` trong thư mục dự án (`NewBackground.Photo`) |
| KB-3 | Nút đóng hay nút quay lại về danh sách, không tạo dự án | `onClose` gọi `popBackStack`; nút quay lại của máy qua `BackHandler` của màn, cũng gọi `onClose` (mục 3.1) |
| KB-3 | Dự án nằm trên máy, ở đầu danh sách khi quay về | Kho dự án sắp theo `updatedAtMillis` (T-002); test vỏ app kiểm quay về đúng danh sách |
| KB-3 | Xóa hết tên rồi "Create" thì dùng tên mặc định; ô tên nhận tối đa 40 ký tự | Tên trống (sau `trim`) thay bằng tên mặc định. Đếm theo code point như `ProjectNames.MAX_NAME_LENGTH` |
| KB-3 | Dự án đầu tiên: "Create" không hiện quảng cáo toàn màn hình | `createdCount` đọc ngay trước khi tạo; bằng 0 thì không gọi `create_inter` |
| KB-3 | Máy không đủ chỗ: báo lỗi dễ hiểu, giữ ở màn với lựa chọn đã chọn, không tạo dự án dở | `ProjectFailure.NO_SPACE` thành thông báo "Not enough space on your phone. Free up some space and try again." (dùng lại chuỗi có sẵn `projects_message_no_space`, cùng lời với spec câu hỏi 12); kho không để lại dự án dở (T-002) |
| KB-3 | Màn thấp 360 × 640: phần tùy chọn cuộn được, "Create" luôn thấy, không che lựa chọn cuối | `Column(verticalScroll)` có trọng số, `Footer` ngoài phần cuộn |
| KB-3 | Bấm "Create" hai lần nhanh chỉ tạo một dự án | Cờ `creating` trong ViewModel, bỏ qua lần bấm thứ hai khi đang tạo hay đã tạo xong |
| KB-2 | Cờ màn vẽ tắt: "Create" hiện "Coming soon", ở lại màn, không tạo dự án | `FeatureFlag.EDITOR` theo dõi bằng `Flow`, đọc giá trị lúc bấm; chữ dùng lại chuỗi có sẵn `projects_coming_soon` |
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

- `DefaultNewProjectRepository` nhận `ProjectRepository`, `ProjectThumbnails`, `DeviceSettingsRepository`, `PhotoPreparer`, `@ApplicationContext Context` (chỉ để đọc chuỗi), `@ProjectsIoDispatcher`. `defaults()` đọc `createdCount.first()`, `defaultAspectRatio.first()`, `defaultFrameRate.first()`; đổi chuỗi `DeviceSettingsValues.ASPECT_RATIO_9_16`, `ASPECT_RATIO_16_9`, `ASPECT_RATIO_1_1` sang `AspectRatio` (dùng hằng, không viết cứng chuỗi; giá trị dự phòng là `DEFAULT_ASPECT_RATIO`, `DEFAULT_FRAME_RATE`) và số sang `FrameRate.fromFpsOrNull`, giá trị lạ thì 9:16 và 12. Tên mặc định là chuỗi `new_project_default_name` ("My animation %1$d") với `createdCount + 1`; tên layer đầu là chuỗi `new_project_first_layer_name` (câu hỏi 2). Kho không đọc cờ Cài đặt: ViewModel quyết.
- `create` gọi `ProjectRepository.create`, khi `Success` gọi `thumbnails.update(id)` (kết quả của ảnh thu nhỏ không đổi kết quả tạo; danh sách vẫn tự vẽ ảnh thiếu, T-009).

**Chuẩn bị ảnh** (`data/PhotoPreparer.kt`, `internal class PhotoPreparer @Inject constructor(@ApplicationContext context: Context, logger: AppLogger)` cộng bốn hàm thuần `internal`):

- `photoSampleSize(width, height): Int`: lũy thừa của 2 nhỏ nhất sao cho cạnh dài sau khi giảm không quá 4096. Ví dụ 4032 × 3024 ra 1; 8000 × 2000 ra 2; 12000 × 9000 ra 4; 1000 × 800 ra 1. Giới hạn bộ nhớ của ảnh giải mã khoảng 4096 × 3072 × 4 byte (50 MB).
- `photoTargetSize(width, height): Pair<Int, Int>` sau khi xoay: cạnh dài lớn hơn 3840 thì thu về đúng 3840, giữ tỷ lệ (làm tròn); không phóng to. Ví dụ 4032 × 3024 ra 3840 × 2880; 4000 × 1000 ra 3840 × 960; 3000 × 2250 giữ nguyên.
- `exifTransform(orientation: Int): PhotoTransform(rotationDegrees, mirrorHorizontally)` cho 8 giá trị `ExifInterface.ORIENTATION_*` (1 giữ nguyên; 2 lật ngang; 3 xoay 180; 4 lật ngang rồi xoay 180; 5 lật ngang rồi xoay 270; 6 xoay 90; 7 lật ngang rồi xoay 90; 8 xoay 270); giá trị khác như 1.
- `orientationOrDefault(logger: AppLogger, read: () -> Int): Int`: gọi `read()`; ném `IOException` hay `RuntimeException` thì ghi `logger.warn("NewProject", "Photo EXIF not readable (<loại lỗi>)")` và trả 1 (ảnh vẫn dùng được, chỉ không xoay).
- `prepare(uri)`, trên luồng IO: mở `ContentResolver.openInputStream` lần một đọc kích thước (`inJustDecodeBounds`), lần hai đọc hướng bằng `orientationOrDefault(logger) { android.media.ExifInterface(stream).getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL) }` (hàm dựng nhận `InputStream` từ API 24, có sẵn từ minSdk 26), lần ba giải mã với `inSampleSize`; dựng một `Matrix` (lật, xoay, thu nhỏ) rồi vẽ lên `Bitmap` `ARGB_8888` đã tô trắng (ảnh PNG có vùng trong suốt ra nền trắng, không ra đen sau JPEG); nén JPEG chất lượng 90, đuôi `"jpg"`; ảnh xem trước vuông cắt giữa cho nút "Photo", cạnh bằng 48 dp đổi theo mật độ màn hình (`48 * displayMetrics.density`, làm tròn; 144 px ở xxhdpi) để ảnh không mờ. Giải phóng bitmap trung gian bằng `recycle()`. Lỗi đọc EXIF không làm hỏng ảnh (ý trên). Trả `null` khi luồng mở không được, `IOException` hay `SecurityException` lúc đọc kích thước hay giải mã, ảnh không giải mã được (ví dụ HEIC trên Android 8) hay `OutOfMemoryError`; mỗi lần ghi `logger.warn("NewProject", "Photo not usable (<loại lỗi>)")`, không ghi đường dẫn hay tên tệp.

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

`Loading` chỉ kéo dài lúc đọc giá trị chọn sẵn (vài mili giây). Khi đó màn vẽ thanh trên (tiêu đề, nút đóng dùng được), phần cuộn trống, nút "Create" tắt; không có vòng chờ, để không có hiệu ứng chạy mãi trong test.

**ViewModel** (`create/NewProjectViewModel.kt`, `@HiltViewModel`, nhận `NewProjectRepository`, `FeatureFlags`, `Analytics`, `AppLogger`):

- Lúc khởi tạo: `defaults()` một lần, kết hợp với giá trị đầu tiên của `FeatureFlag.SETTINGS` (tắt thì 9:16, 12). Giá trị chọn sẵn tính một lần; cờ đổi sau đó không đè lựa chọn của người dùng. Độ phân giải 1080p, nền trắng, tên bằng tên mặc định.
- Theo dõi `FeatureFlag.EDITOR` (giữ giá trị mới nhất, như `ProjectsViewModel.editorEnabled`) và `FeatureFlag.PROJECTS` (tắt thì phát `Close` một lần).
- `onNameChange(text)`: giữ tối đa 40 code point (dán chuỗi dài thì cắt), không `trim` khi đang gõ.
- `onAspectRatio`, `onResolution`, `onFrameRate`, `onSolidBackground(color)`: đổi lựa chọn, xóa `error`.
- `onPhotoPicked(uri: String?)`: `null` (đóng bộ chọn) không đổi gì. Có uri: `photoLoading = true`, chạy `preparePhoto` trong một `Job` riêng (`photoJob`); được thì giữ `PreparedPhoto` trong ViewModel (không đưa bytes vào `UiState`), `background = Photo`, `photoPreview`; `null` thì giữ nền trước, phát `ShowPhotoFailed`. Lần chọn ảnh mới thay ảnh cũ. Chọn lại một màu thì nền là màu, ảnh đã chuẩn bị vẫn giữ để bấm "Photo" lần sau mở lại bộ chọn (không tự chọn lại ảnh cũ). Chọn một màu khi ảnh còn đang chuẩn bị thì hủy `photoJob`, `photoLoading = false`, nền là màu vừa chọn; kết quả của ảnh đó bị bỏ, không đè lựa chọn màu.
- `onPhotoPickerFailed()`: Route gọi khi không mở được bộ chọn ảnh; phát `ShowPhotoFailed`, log `Photo picker unavailable`.
- `onCreate()`: bỏ qua khi `creating`, `photoLoading`, hay đã tạo xong (một ViewModel tạo tối đa một dự án). Cờ màn vẽ tắt: phát `ShowComingSoon`, không gọi kho, ghi log `Create blocked: editor off`. Ngược lại: `creating = true`; `countBefore = createdCount()`; tên `name.trim()` trống thì tên mặc định; `create(NewProject(...))`. `Success`: ghi `project_create`, log `Project created`, phát `ProjectCreated(id, interstitialAllowed = countBefore >= 1)`, giữ `creating = true` tới khi màn đóng, để nút "Create", nút đóng và nút quay lại không dùng được nữa (việc rời màn do hiệu ứng `ProjectCreated` làm, đúng một lần). `Failure(NO_SPACE)` hay `WRITE_FAILED` (lời báo "Couldn't create the project. Try again.", tiếng Việt "Không tạo được dự án. Hãy thử lại.", câu hỏi 4): `creating = false`, `error` tương ứng, log `Create failed: <lý do>`; lựa chọn giữ nguyên.
- Sự kiện `project_create` (hằng `EVENT_PROJECT_CREATE = "project_create"`): `ratio` = `9:16` / `16:9` / `1:1`; `size` = `720p` / `1080p`; `fps` = số dạng chuỗi; `background` = `white` / `dark` / `blue` / `photo`; `is_default` = `"true"` khi bốn nhóm (khung hình, độ phân giải, tốc độ, nền) bằng giá trị chọn sẵn lúc mở màn, không xét tên (câu hỏi 3). Không tham số nào khác; không ghi tên dự án.

**Màn hình** (`create/NewProjectScreen.kt`, `create/NewProjectOptions.kt`):

- `NewProjectRoute(viewModel, onClose, onOpenProject: ((ProjectId) -> Unit)?, createAd: @Composable (onFinished: () -> Unit) -> () -> Unit = { rememberShowFullScreenAd(AdPlacement.CREATE_INTER, it) }, onBeforePhotoPicker: () -> Unit = <hold của rememberAdSuppression()>, onPhotoPickerResult: () -> Unit = <release>, newProjectAd: @Composable (Modifier) -> Unit = { NativeAdSlot(AdPlacement.NEW_PROJECT_NATIVE, it, keepSpaceWhenEmpty = true) })`. Hai tham số quảng cáo để test thay bằng bản giả (CLAUDE.md, bước 7).
- Photo Picker: `rememberLauncherForActivityResult(PickVisualMedia())`. Trước khi `launch` gọi `onBeforePhotoPicker()`, dòng đầu của callback gọi `onPhotoPickerResult()`. `launch` đặt trong `try`; bắt `ActivityNotFoundException` (máy không có ứng dụng nào nhận yêu cầu chọn ảnh) thì gọi `onPhotoPickerResult()` ngay (nhả chặn quảng cáo, "kể cả khi mở thất bại" của `docs/ops/ads-guide.md`, mục rời app) rồi `viewModel.onPhotoPickerFailed()`; hai tham số của `NewProjectRoute` này mặc định là `hold()` và `release()` của `rememberAdSuppression()` (`:core:ads`, G-03 cách 1), test truyền bản ghi thứ tự gọi; rồi `viewModel.onPhotoPicked(uri?.toString())`. Không cần quyền: Android 13 trở lên (và Android 11, 12 có tiện ích SDK 2) mở bộ chọn ảnh của hệ thống; máy cũ hơn, thư viện `activity` tự chuyển sang bộ chọn ảnh bản cài qua Google Play (nếu có) hoặc `ACTION_OPEN_DOCUMENT` với `image/*`. Quyền đọc uri do bộ chọn cấp tạm thời cho app; ảnh được chuẩn bị ngay khi chọn, không giữ uri.
- Hiệu ứng (`LaunchedEffect(viewModel)`): `ProjectCreated` ghi id vào `rememberSaveable` `pendingOpen` dạng `String?` (`id.value`; `ProjectId` là value class, không tự lưu được vào `Bundle`), lúc mở dựng lại `ProjectId(pendingOpen)`; nếu `interstitialAllowed` và vòng đời của `LocalLifecycleOwner` đang `RESUMED` thì gọi hành động của `createAd`, `onFinished` mở dự án; ngược lại mở ngay (app ra nền giữa lúc ghi thì không có quảng cáo khi quay lại). Mở dự án: `onOpenProject?.invoke(id) ?: onClose()`; xóa `pendingOpen`. `ShowComingSoon`, `ShowPhotoFailed`: thông báo ngắn trên `SnackbarHost` của màn. `Close`: `onClose()`. Lỗi trong `UiState` hiện dưới dạng thông báo ngắn rồi `viewModel.onErrorShown()`.
- Dựng lại activity khi quảng cáo còn che (xoay máy sau `ProjectCreated`, trước `onFinished`): composition mới thấy `pendingOpen != null` lúc khởi đầu thì mở dự án ngay, không gọi quảng cáo, rồi xóa `pendingOpen`. Mở dự án đi qua một hàm duy nhất kiểm `pendingOpen` còn khác `null` rồi mới gọi và xóa nó, nên `onFinished` cũ chạy muộn không mở lần hai.
- Nút quay lại của máy: Route đặt một `BackHandler` luôn bật, `BackHandler { if (!creating) onClose() }`, và nút đóng tắt khi `creating`. Nhờ vậy `viewModelScope` không hủy `create` giữa chừng (`DefaultProjectRepository.create` không dọn thư mục khi bị hủy). Tạo thành công: `creating` giữ `true`, quay lại không làm gì; màn rời đi đúng một lần qua hiệu ứng `ProjectCreated` (mở dự án hay `onClose`), không đóng hai lần. Tạo lỗi: `creating` về `false`, quay lại gọi `onClose` một lần như thường.
- `NewProjectScreen(uiState, callbacks..., newProjectAd, snackbarHostState, scrollState: ScrollState = rememberScrollState())` không trạng thái; `scrollState` là tham số để screenshot test truyền `ScrollState(Int.MAX_VALUE)` (Compose giới hạn về giá trị lớn nhất, tức cuộn hết xuống): `InContentAds(listOf(AdPlacement.NEW_PROJECT_NATIVE)) { ... }` bọc gốc (MC-13); `Column(fillMaxSize)`: thanh trên 64 dp (nút `close` 48 dp, tiêu đề `titleLarge`), phần cuộn `weight(1f).verticalScroll` lề 16 dp, lề trên 8 dp, các nhóm cách 24 dp (`Spacing.lg`): ô tên `OutlinedTextField` một dòng có nhãn "Name"; nhóm "Canvas" ba ô 84 dp cao, bo 14 dp, ô chọn nền `primaryContainer` viền 2 dp `primary`, ô khác `surfaceContainer`, hình chữ nhật minh họa 18 × 32, 32 × 18, 26 × 26; nhóm "Size", "Frame rate" là chip 28 dp bo 8 dp (`primaryContainer` khi chọn, `surfaceVariant` khi không); ô quảng cáo `newProjectAd(Modifier.fillMaxWidth().gapAboveWhenShown(24.dp))` giữa "Frame rate" và "Background", cách mỗi nhóm 24 dp. Khe không có quảng cáo (khóa `is_show_new_project_native` tắt, mặc định tắt, hay chưa có đồng ý) thì `NativeAdSlot` vẽ một `Box` cao 0 (`core/ads/.../NativeAdSlot.kt`, nhánh `!allowed`); nếu khe là một phần tử riêng của cột `spacedBy(24.dp)` thì khoảng "Frame rate" tới "Background" thành 48 dp. Vì vậy khe nằm chung một phần tử của cột với nhóm "Frame rate" (một `Column` con không có khoảng cách), và khoảng 24 dp phía trên khe do `gapAboveWhenShown` (hàm `private` của tệp, dựng bằng `Modifier.layout`) thêm: đo khe, cao 0 thì trả cao 0; cao hơn 0 thì trả cao cộng 24 dp và đặt khe ở `y = 24 dp`. Khoảng từ khe (hay từ "Frame rate" khi khe trống) tới "Background" là 24 dp của `spacedBy`; nhóm "Background" ba ô tròn 36 dp (vòng 2 dp `onSurface` quanh ô đang chọn, màu 28 dp khi chọn, 32 dp khi không, viền 1 dp `outlineVariant`) với màu từ `ProjectBackgroundColors`, nút tròn 48 dp `surfaceVariant` biểu tượng `R.drawable.ic_image` (có ảnh thì vẽ ảnh xem trước cắt tròn, có vòng khi nền là ảnh) và chữ "Photo"; nút tắt khi `photoLoading` hay `creating`. Biểu tượng `image` không có trong `material-icons-core`, nên vẽ bằng tệp vector mới của module như `ic_upload_file.xml`; `close` và `check` có sẵn trong `Icons.Filled` của core. Phần đáy ngoài phần cuộn: lề 24/16 dp, `PrimaryButton` "Create" 48 dp, biểu tượng `check`; tắt khi `creating` hay `photoLoading`. Màn rộng hơn 600 dp: nội dung `widthIn(max = 600.dp)` căn giữa (câu hỏi 21 của spec).
- Khoảng cách ngoài token (10, 12, 14, 20 dp) đặt thành hằng riêng của tệp như `ProjectsScreen.kt`. Màu từ `MaterialTheme.colorScheme` và `ProjectBackgroundColors`, không viết cứng.
- Tag: `NewProjectTags` (`SCREEN = "new_project_screen"` ở gốc màn, `CREATE = "new_project_create"`, `NAME_FIELD`, `RATIO_<giá trị>`, `SIZE_<giá trị>`, `FPS_<số>`, `BG_WHITE`, `BG_DARK`, `BG_BLUE`, `BG_PHOTO`, `AD`, `CONTENT`). Tệp kịch bản trên emulator dùng `SCREEN` và `CREATE`, nên hai giá trị này cố định.

**Điều hướng:**

- `navigation/NewProjectNavigation.kt`: `@Serializable data object NewProjectDestination`, `NavGraphBuilder.newProjectScreen(onClose: () -> Unit, onOpenProject: ((ProjectId) -> Unit)?)`.
- `AppNavHost.kt`: `AppScreens.projects` nhận thêm `onNewProject: () -> Unit` (kiểu `NavGraphBuilder.(ProjectsCallbacks) -> Unit` với `data class ProjectsCallbacks(val onFeatureDisabled: () -> Unit, val onNewProject: () -> Unit)`), mặc định truyền `onNewProject` cho `projectsScreen`; thêm `AppScreens.newProject: NavGraphBuilder.(NewProjectCallbacks) -> Unit` với `data class NewProjectCallbacks(val onClose: () -> Unit)`, mặc định `newProjectScreen(onClose = it.onClose, onOpenProject = null)`; T-018 chỉ thêm trường `onOpenProject` vào lớp này, không đổi kiểu lần nữa. `onNewProject` gọi `navController.navigate(NewProjectDestination) { launchSingleTop = true }`; `onClose` gọi `navController.popBackStack(NewProjectDestination, inclusive = true)`. Màn vẽ chưa có: `onOpenProject = null` nên tạo xong quay về danh sách, dự án mới ở đầu (câu hỏi 1). T-018 nối `onOpenProject` với `popUpTo<NewProjectDestination> { inclusive = true }` để nút quay lại ở màn vẽ về danh sách.
- Thanh điều hướng của vỏ app chỉ hiện ở danh sách (`showsNavigationBar` theo route), nên màn Tạo dự án không có thanh; test vỏ app kiểm điều này.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `feature/projects/build.gradle.kts` | Thêm `implementation(project(":core:data"))` để đọc `DeviceSettingsRepository` (feature phụ thuộc core, đúng ADR-0001). (dự phòng) Khối `tasks.withType<Test>().configureEach { maxHeapSize = "2g" }`, chỉ khi `pr-check` hết bộ nhớ trong test của module (mục 3.4); khối đã có thì không thêm lần nữa |
| Tạo | `feature/projects/src/main/res/drawable/ic_image.xml` | Biểu tượng Material Symbols Rounded "image" (Apache 2.0), 24 dp, màu theo `tint` của `Icon`, cùng cách ghi với `ic_upload_file.xml` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/NewProjectRepository.kt` | Interface `NewProjectRepository`, `NewProjectDefaults`, `PreparedPhoto` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultNewProjectRepository.kt` | Cài đặt `internal`: giá trị chọn sẵn, tên mặc định, tạo rồi vẽ ảnh thu nhỏ, chuẩn bị ảnh |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/PhotoPreparer.kt` | `PhotoPreparer`, `photoSampleSize`, `photoTargetSize`, `exifTransform`, `PhotoTransform` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/di/ProjectsModule.kt` | `@Binds` `DefaultNewProjectRepository` thành `NewProjectRepository` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectUiState.kt` | `NewProjectUiState`, `BackgroundChoice`, `NewProjectError`, `NewProjectEffect` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectViewModel.kt` | ViewModel của mục 3.1, hằng `EVENT_PROJECT_CREATE` và tên tham số |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectScreen.kt` | `NewProjectRoute`, `NewProjectScreen`, `NewProjectTags` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/create/NewProjectOptions.kt` | Ô khung hình, chip, ô màu, nút "Photo" |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/navigation/NewProjectNavigation.kt` | `NewProjectDestination`, `newProjectScreen()` |
| Sửa | `feature/projects/src/main/res/values/strings.xml` | Chuỗi tiếng Anh: tiêu đề, "Name", "Canvas", "Size", "Frame rate", "Background", "Photo", "Create", mô tả nút đóng, tên màu cho trợ năng ("White", "Dark gray", "Blue"), `new_project_first_layer_name`, lỗi ghi, lỗi ảnh; `new_project_default_name` ("My animation %1$d"), "%1$d fps", "720p", "1080p", "9:16", "16:9", "1:1" ghi `translatable="false"`. Không thêm chuỗi "Coming soon" và lỗi hết chỗ: dùng lại `projects_coming_soon`, `projects_message_no_space` có sẵn |
| Sửa | `feature/projects/src/main/res/values-vi/strings.xml` | Bản tiếng Việt của các chuỗi dịch được |
| Sửa | `app/src/main/kotlin/com/vgl/draw/animation/navigation/AppNavHost.kt` | `ProjectsCallbacks`, `NewProjectCallbacks`, `AppScreens.newProject`, nối `onNewProject`, `onClose` (mục 3.1) |
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
| Tạo | `feature/projects/src/test/screenshots/NewProjectScreen_default_small.png` | Ảnh mới, khung 360 × 640, cuộn hết xuống bằng `ScrollState(Int.MAX_VALUE)` |
| Tạo | `feature/projects/src/test/screenshots/NewProjectScreen_photo_light.png` | Ảnh mới: nền là ảnh, 16:9, 720p, 24 fps |
| Tạo | `feature/projects/src/test/screenshots/NewProjectScreen_default_unfolded.png` | Ảnh mới, khung 673 × 841, một cột căn giữa |
| Tạo | `app/src/deviceScenarios/T-011-tao-du-an.txt` | Kịch bản trên emulator: từ danh sách trống bấm "New animation", bấm "Create", về danh sách có một thẻ dự án (nội dung ở mục 4) |
| Sửa | `scripts/device-scenario.py` | Ngân sách kịch bản 600 thành 800 ở hai chỗ: docstring (dòng 68, `DEVICE_SCENARIOS_BUDGET seconds (default 600)` thành `(default 800)`) và mặc định `env_seconds("DEVICE_SCENARIOS_BUDGET", 600)` thành `800` (dòng 516) |
| Sửa | `scripts/device-session.sh` | Chú thích dòng 32 `default 600` thành `default 800`; dòng 276 `${DEVICE_SCENARIOS_BUDGET:-600}` thành `${DEVICE_SCENARIOS_BUDGET:-800}` (hạn của `timeout -k 30` thành 920 giây) |
| Sửa | `docs/ops/test-sessions.md` | Mục 5, đoạn Hạn chót (dòng 184): `(mặc định 600)` thành `(mặc định 800)` |

Không sửa: `ProjectsScreen.kt`, `ProjectsViewModel.kt`, `ProjectsNavigation.kt` (tham số `onNewProject` đã có), `:core:project`, `:core:data`, `:core:ads`, `consumer-rules.pro` (không lớp nào cần giữ tên khi R8 chạy: đích điều hướng `@Serializable` đã có luật của Navigation, như `ProjectsDestination`). Ảnh chuẩn có sẵn không đổi: màn danh sách không đổi. Không script hay workflow nào đọc các file trên theo tên (`grep -rn "AppNavHost\|feature/projects" scripts/ .github/` chỉ thấy `scripts/design-compare.sh` đọc ảnh theo mẫu tên thiết kế). Tệp kịch bản mới được `scripts/device-scenario.py` đọc theo thư mục, không theo tên. Ngân sách kịch bản (`docs/ops/test-sessions.md`, mục 5, Hạn chót; `docs/foundation.md`, ý ngân sách kịch bản của ADR-0032): trên nhánh chính có hai tệp `app/src/deviceScenarios/T-009-danh-sach-du-an.txt` và `T-024-mo-app-tu-choi-dong-y.txt`, mỗi tệp `timeout: 180`; thêm tệp mới `timeout: 120`. Tổng 180 + 180 + 120 + 3 × 80 = 720 giây, vượt mặc định 600, làm tròn lên hàng trăm là 800. Trần: `timeout-minutes: 50` của job trong `.github/workflows/device-session.yml` (chỉ đọc), (50 - 20) × 60 - 120 = 1680, làm tròn xuống 1600; 800 không vượt trần. Plan này nâng mặc định lên 800 trong cùng PR (ba dòng `Sửa` của bảng trên). Các tệp khác nhắc `scripts/device-scenario.py` không bị ảnh hưởng vì chỉ nhắc trong chú thích, không đọc ngân sách: `app/src/test/kotlin/com/vgl/draw/animation/network/NetworkDialogHostTest.kt`, `app/src/test/kotlin/com/vgl/draw/animation/ui/UiAutomatorTagsTest.kt` (cách chọn phần tử theo `resource-id`), `scripts/tests/fake-adb` (bản dump giả). Test của script trong `scripts/tests/run.sh` luôn đặt `DEVICE_SCENARIOS_BUDGET` riêng cho từng lần chạy (ví dụ 0, 6, 9, 12, 60), không test nào kiểm giá trị mặc định, nên không sửa `scripts/tests/`; vẫn chạy `bash scripts/tests/run.sh` để kiểm hai script sau khi sửa (cũng chạy trong `scripts/check.sh`). Tệp do plan khác cùng đợt thêm vào thì phiên tính lại tổng lúc gộp.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: không thêm cờ. Đọc `FeatureFlag.PROJECTS` (tắt thì đóng màn), `FeatureFlag.EDITOR` (tắt thì "Coming soon", không tạo), `FeatureFlag.SETTINGS` (tắt thì 9:16, 12), cả ba bằng `Flow`.
- Quảng cáo: hai vị trí đã có trong spec mục 5 và trong `AdPlacement` (T-007): `new_project_native` (`NEW_PROJECT_NATIVE`, native medium, nhóm B, khóa `is_show_new_project_native` mặc định tắt, `keepSpaceWhenEmpty = true`, khai `InContentAds`) và `create_inter` (`CREATE_INTER`, interstitial, chỉ khi `createdCount` trước lần tạo từ 1 trở lên; khoảng cách `ads_interval` và combo `native_combo` do `:core:ads` lo). `ads_interval` mặc định là 45 giây trong `docs/remote-config-keys.md` (spec ghi 30), và quảng cáo mở app cũng tính vào khoảng cách này. Không thêm vị trí, không đổi định dạng.
- Sự kiện phân tích: `project_create` với `ratio`, `size`, `fps`, `background`, `is_default` đúng spec mục 6. Không thêm sự kiện hay tham số.
- File R2: Không chạm.
- Dữ liệu trên máy: không thêm khóa. Đọc khóa của T-005 và bộ đếm `projects_created_count` của T-002 qua giao diện có sẵn; tệp dự án do `ProjectRepository` ghi.
- **Quyền truy cập:** Không thêm. Đã tra từ minSdk 26 tới targetSdk 36: `PickVisualMedia` không cần quyền ở mọi bản (Android 13 trở lên và Android 11, 12 có tiện ích SDK 2 dùng bộ chọn ảnh của hệ thống; bản cũ hơn chuyển sang bộ chọn qua Google Play hay `ACTION_OPEN_DOCUMENT`, đều cấp quyền đọc uri tạm thời); `ContentResolver.openInputStream` trên uri đó không cần quyền; ghi tệp dự án trong thư mục riêng của app không cần quyền. Không thêm: `READ_MEDIA_IMAGES`, `READ_EXTERNAL_STORAGE`, `READ_MEDIA_VISUAL_USER_SELECTED` (spec mục 6: "Chọn ảnh nền dùng bộ chọn ảnh của hệ thống, không cần quyền"). Không thêm khai báo dịch vụ `ModuleDependencies` cho bộ chọn ảnh bản Google Play trong manifest (câu hỏi 5).
- **Thư viện và SDK:** Không thêm thư viện mới. `PickVisualMedia`, `rememberLauncherForActivityResult` thuộc `androidx-activity-compose` (phiên bản 1.10.1 trong `gradle/libs.versions.toml`), convention plugin `template.android.feature` đã thêm `implementation` cho mọi feature. `ExifInterface` dùng lớp của nền tảng `android.media.ExifInterface` (hàm dựng nhận `InputStream` từ API 24), không dùng bản AndroidX. `rememberAdSuppression`, `rememberShowFullScreenAd`, `NativeAdSlot`, `InContentAds` của `:core:ads`; `DeviceSettingsRepository` của `:core:data` (thêm phụ thuộc module, không phải thư viện ngoài); `ProjectRepository`, `ProjectThumbnails` của `:core:project` (đã có).

### 3.4. Phương án dự phòng

Hai điểm chưa chắc, mỗi điểm một cách làm thay.

**Ảnh thật trên Robolectric.**

- **Điểm chưa chắc:** đọc EXIF và giải mã JPEG thật trong `PhotoPreparerTest` trên Robolectric `NATIVE`.
- **Điều kiện chuyển:** test chuẩn bị ảnh thật (xoay 90, thu nhỏ, nền trắng) thất bại vì Robolectric không giải mã hay không đọc được EXIF (bitmap `null`, hướng luôn 1) sau 2 lần sửa, hoặc hết bộ nhớ, hoặc một test chạy quá 60 giây.
- **Cách làm thay:** tách `PhotoPreparer.prepare` thành hai bước: `readOrientation(stream)` và `transform(bitmap, orientation)`. Test xoay, thu nhỏ, nền trắng gọi `transform` với `Bitmap` dựng trong test (không qua JPEG); test đọc hướng dùng tệp JPEG mẫu nhỏ có EXIF ghi bằng `ExifInterface.saveAttributes` trong test. Câu khẳng định của mục 4 giữ nguyên. Không thêm tệp ngoài mục 3.2.

**Bộ nhớ của test trên CI.**

- **Điểm chưa chắc:** `pr-check` chạy Java 17 với heap mặc định của task `Test`, còn `scripts/check.sh` của phiên chạy Java 21; T-011 thêm nhiều test Robolectric `NATIVE` và screenshot vào `:feature:projects` (bài học L-004).
- **Điều kiện chuyển:** `pr-check` đỏ với `java.lang.OutOfMemoryError` trong một test của `:feature:projects:testDebugUnitTest`.
- **Cách làm thay:** thêm vào `feature/projects/build.gradle.kts` khối `tasks.withType<Test>().configureEach { maxHeapSize = "2g" }` (dòng "(dự phòng)" của mục 3.2; tệp riêng của app, không phải `build-logic/`, không cần ADR; T-009, T-012 có cùng dự phòng, người phụ trách sản phẩm đã đồng ý; khối đã có thì không thêm lần nữa). Ghi vào mô tả PR rằng dự án mẫu nên đặt `maxHeapSize` chung (`docs/ops/from-app.md`). Không đổi test hay câu khẳng định. Vẫn hết bộ nhớ thì dừng task.

### 3.5. Hành vi đã có bị đổi

`scripts/existing-tests.py` liệt kê (xem kết quả dưới bảng). Hành vi bị đổi:

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| `AppScreens.projects` có kiểu `NavGraphBuilder.(onFeatureDisabled: () -> Unit) -> Unit` | Kiểu `NavGraphBuilder.(ProjectsCallbacks) -> Unit` | `AppNavHostTest` (bản giả `fakeScreens.projects`, mọi test dùng nó) | Câu khẳng định giữ nguyên. Bản giả đổi chữ ký: `projects = { callbacks -> ... callbacks.onFeatureDisabled() ... }` và thêm nút giả gọi `callbacks.onNewProject()`; đây là dữ liệu dựng test theo phần plan thêm (ADR-0029), không phải câu khẳng định |
| Danh sách bấm "New animation" hiện "Coming soon" (vỏ app truyền `onNewProject = null`) | Vỏ app truyền `onNewProject`, danh sách mở màn Tạo dự án | Không test nào của `:app` kiểm "Coming soon" của nút này; `ProjectsRouteTest` kiểm nhánh `null` của `ProjectsRoute` với tham số truyền thẳng | Giữ: `ProjectsRoute` không đổi, nhánh `null` vẫn đúng cho route |

Kết quả `scripts/existing-tests.py` (2026-11-23): 3 test dùng tên của file plan sửa; "Cần xem trước" 0, "Nên xem" 0. Ba test còn lại (`--all`): `AppNavHostTest.recreate_keepsProjectsScreen` (giữ nguyên câu khẳng định, chỉ đổi bản giả như dòng đầu của bảng), `ScreenNameTest.reportedScreen_nullBeforeFirstEntryAndOnLoading` và `NetworkDialogControllerTest.sameScreenReportedAgain_keepsDialogAndRecordsNothing` (chỉ nhắc `AppNavHost` trong chú thích; cách báo màn cho popup mất mạng không đổi, route mới được báo như mọi route khác). Không test nào phải viết lại câu khẳng định.

## 4. Kiểm thử

Các test ViewModel dùng `MainDispatcherRule`, `RecordingAppLogger` và hai bản giả có sẵn trong `feature/projects/src/test/.../FakeProjectsRepository.kt`: `FakeFeatureFlags` (cờ đổi được qua `set`) và `NoAnalytics` (ghi lại các sự kiện vào `events`); không viết bản giả mới cho cờ hay phân tích. Theo bài học L-005: luồng thu trong `backgroundScope` (theo dõi cờ, `uiState`, hiệu ứng) chờ bằng `runCurrent()`, không dùng `advanceUntilIdle()`.

Tiêu chí "từ danh sách trống tới màn vẽ đúng 2 lần chạm" của KB-3 chỉ kiểm đủ ở T-018, khi có màn vẽ. Ticket này kiểm phần của nó: hai lần chạm ("New animation", "Create") tạo dự án và rời màn qua `onOpenProject` (hay `onClose` khi `null`).

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| KB-3: giá trị chọn sẵn theo Cài đặt | Unit test | `NewProjectViewModelTest.defaults_followSettings`: kho giả trả 16:9, 24 fps, cờ Cài đặt bật; `Ready` có `LANDSCAPE_16_9`, `FPS_24`, `P1080`, `Solid(WHITE)`, tên "My animation 5" (kho giả trả tên này). Đối chứng: cùng test với 1:1, 6 fps ra đúng 1:1, 6 (không phải giá trị cứng) |
| Settings câu 15: cờ Cài đặt tắt | Unit test | `NewProjectViewModelTest.settingsFlagOff_uses916And12`: kho giả trả 16:9, 24; cờ tắt; `Ready` có 9:16, 12. `settingsFlagTurnsOnLater_keepsChoices`: cờ bật sau khi `Ready` thì lựa chọn không đổi |
| KB-3: tên "My animation <số>", số = đã tạo + 1 | Unit test | `DefaultNewProjectRepositoryTest.defaultName_isCreatedCountPlusOne`: `createdCount` giả 4 ra "My animation 5"; 0 ra "My animation 1" |
| KB-3: chuỗi Cài đặt lạ | Unit test | `DefaultNewProjectRepositoryTest.unknownSettings_fallBackTo916And12`: `"4:5"`, 7 fps ra 9:16, 12; đối chứng `"1:1"`, 30 ra 1:1, 30 |
| T-005: mọi giá trị Cài đặt đổi được | Unit test | `DefaultNewProjectRepositoryTest.allSettingsValues_map`: duyệt `DeviceSettingsValues.ASPECT_RATIOS` và `FRAME_RATES`; mỗi giá trị ra đúng `AspectRatio`, `FrameRate` tương ứng (bảng trong test), không giá trị nào rơi vào dự phòng |
| KB-3: "Create" không đổi gì thì tạo với mặc định | Unit test | `NewProjectViewModelTest.create_withDefaults_createsOnceWithDefaults`: một lần `create` với `NewProject(name = "My animation 5", PORTRAIT_9_16, P1080, FPS_12, Solid(WHITE), firstLayerName = <kho giả>)`; phát `ProjectCreated` |
| KB-3: kích thước khung theo lựa chọn | Unit test | `NewProjectViewModelTest.create_passesChosenFormat`: chọn 16:9, 720p, 30 rồi tạo; `NewProject` có `LANDSCAPE_16_9`, `P720`, `FPS_30`; `canvasSize` của giá trị đó là 1280 × 720 (kích thước đã có test ở `:core:project`) |
| KB-3: mỗi nhóm đúng một lựa chọn | Compose UI test | `NewProjectScreenTest.eachGroup_hasExactlyOneSelected`: mỗi nhóm đếm node `isSelected()` bằng 1; bấm `RATIO_16_9` gọi callback với 16:9; `NewProjectScreenTest.selectedTile_showsSelectedState` dựng `Ready` với 16:9 thì `RATIO_16_9` `assertIsSelected`, `RATIO_9_16` `assertIsNotSelected`; `background = Photo` thì trong nhóm nền chỉ `BG_PHOTO` được chọn |
| KB-3: lựa chọn sáng lên, lựa chọn cũ tắt (màu) | Compose UI test (`NATIVE`) | `NewProjectScreenTest.selectedColors`: vẽ `decorView` ra `Bitmap`; điểm giữa `RATIO_16_9` là `primaryContainer` khi chọn, `surfaceContainer` khi không (sai số 2 mỗi kênh); chip `FPS_24` là `primaryContainer` khi chọn, `surfaceVariant` khi không; điểm đo cách mép và góc bo |
| KB-3: tiêu đề, nhóm, chữ đúng bảng chữ của thiết kế | Compose UI test | `NewProjectScreenTest.texts_matchDesign`: có "New animation", "Name", "Canvas", "9:16", "16:9", "1:1", "Size", "720p", "1080p", "Frame rate", "6", "8", "12 fps", "15", "24", "30", "Background", "Photo", "Create"; không có "4:5" |
| KB-3: tên trống thì dùng tên mặc định | Unit test | `NewProjectViewModelTest.blankName_usesDefaultName`: `onNameChange("   ")` rồi tạo; `NewProject.name == "My animation 5"` |
| KB-3: tối đa 40 ký tự | Unit test | `NewProjectViewModelTest.name_keepsAtMost40CodePoints`: dán 45 ký tự thì giữ 40 đầu; 39 chữ cộng một emoji ghép cặp thay thế (`"😀"`) giữ đủ 40 code point, không cắt đôi emoji |
| KB-3: "Photo" chọn ảnh thì ảnh là nền | Unit test | `NewProjectViewModelTest.photoPicked_setsPhotoBackground`: `onPhotoPicked("content://x")`; `background == Photo`, `photoPreview != null`; tạo thì `NewProject.background` là `NewBackground.Photo` với đúng bytes và đuôi `"jpg"` của kho giả |
| KB-3: đóng bộ chọn thì giữ lựa chọn trước | Unit test | `NewProjectViewModelTest.photoPickerCancelled_keepsPrevious`: chọn `BLUE`, `onPhotoPicked(null)`; vẫn `Solid(BLUE)`, kho không được gọi `preparePhoto` |
| KB-3: ảnh không dùng được | Unit test | `NewProjectViewModelTest.photoNotUsable_keepsPreviousAndTells`: kho trả `null`; nền giữ `Solid(DARK)`, phát `ShowPhotoFailed`, `photoLoading == false`. `NewProjectRouteTest.photoNotUsable_showsMessage`: thông báo có chữ "Couldn't use this photo. Try another one." |
| Ảnh đang chuẩn bị mà người dùng chọn màu | Unit test và Compose UI test | `NewProjectViewModelTest.colorPickedWhilePhotoLoading_dropsPhoto`: `preparePhoto` của kho giả treo bằng `CompletableDeferred`; `onPhotoPicked("content://x")`, rồi `onSolidBackground(BLUE)`; `photoLoading == false`, nền `Solid(BLUE)`; hoàn tất `preparePhoto` với một ảnh thì nền vẫn `Solid(BLUE)`, `photoPreview == null`; tạo thì `NewProject.background` là màu xanh. `NewProjectScreenTest.photoButtonDisabledWhileLoading`: `photoLoading = true` thì `BG_PHOTO` `assertIsNotEnabled`, `CREATE` `assertIsNotEnabled` |
| KB-3: bộ chọn mở không hỏi quyền, chặn quảng cáo tự hiện | Compose UI test | `NewProjectRouteTest.photoButton_launchesPhotoPicker`: dùng `ActivityResultRegistry` giả qua `LocalActivityResultRegistryOwner`; bấm `BG_PHOTO` thì registry nhận một lần với `PickVisualMediaRequest` loại `ImageOnly` (kiểm `Intent` do `PickVisualMedia().createIntent` sinh có action là bộ chọn ảnh hay `ACTION_OPEN_DOCUMENT`, không phải yêu cầu quyền); registry trả uri thì ViewModel giả nhận đúng chuỗi; trả `null` thì nhận `null` |
| Chặn quảng cáo tự hiện quanh bộ chọn ảnh (G-03) | Compose UI test | `NewProjectRouteTest.photoPicker_holdsBeforeLaunchReleasesFirstInCallback`: `onBeforePhotoPicker`, `onPhotoPickerResult` giả và registry giả ghi vào cùng một danh sách; thứ tự đúng `hold`, `launch`, `release`, `onPhotoPicked`; cả khi registry trả `null` vẫn có `release` |
| Không mở được bộ chọn ảnh | Compose UI test | `NewProjectRouteTest.photoPickerLaunchFails_releasesHold`: registry giả ném `ActivityNotFoundException` khi `launch`; danh sách ghi đúng `hold`, `release` (không có `onPhotoPicked`); ViewModel giả nhận `onPhotoPickerFailed` một lần; app không dừng |
| KB-3: ảnh xoay theo EXIF, thu nhỏ tới 3840 | Unit test | `PhotoPreparerTest.exifTransform_allOrientations` (8 giá trị cộng giá trị lạ, theo bảng mục 3.1); `photoSampleSize_examples`, `photoTargetSize_examples` (các ví dụ của mục 3.1) |
| Lỗi đọc EXIF vẫn dùng ảnh | Unit test | `PhotoPreparerTest.exifReadFails_usesOrientation1`: `orientationOrDefault(logger) { throw IOException() }` trả 1 và `RecordingAppLogger` có một dòng `warn` chứa "EXIF not readable"; đối chứng: hàm đọc trả 6 thì ra 6, không có dòng log. `prepare` gọi đúng hàm này (rà soát code); ảnh PNG 1000 × 800 của dòng thu nhỏ dưới (không có EXIF) vẫn ra ảnh |
| KB-3: hướng lật gương đúng trên tệp thật | Unit test (Robolectric `NATIVE`) | `PhotoPreparerTest.prepare_mirroredOrientation`: JPEG 40 × 20 bốn góc bốn màu (đỏ, xanh lá, xanh dương, vàng; mỗi góc 20 × 10), EXIF hướng 5; kết quả 20 × 40, màu ở giữa bốn góc đúng vị trí sau lật ngang rồi xoay 270 (sai số 40 mỗi kênh). Đối chứng: hướng 8 (chỉ xoay) cho hai góc đổi chỗ |
| KB-3: ảnh xoay đúng trên tệp thật | Unit test (Robolectric `NATIVE`) | `PhotoPreparerTest.prepare_rotatesByExif`: JPEG 40 × 20, nửa trái đỏ, nửa phải xanh, ghi EXIF hướng 6 bằng `ExifInterface.saveAttributes`, mở qua uri `file://`; kết quả giải mã 20 × 40, điểm ảnh ở (10, 5) đỏ, (10, 35) xanh (sai số mỗi kênh 40 vì JPEG). Đối chứng: cùng ảnh hướng 1 ra 40 × 20 |
| KB-3: ảnh lớn thu nhỏ | Unit test (Robolectric `NATIVE`) | `PhotoPreparerTest.prepare_scalesLongSideTo3840`: PNG 8000 × 200 ra 3840 × 96 (qua `inSampleSize = 2` rồi thu về 3840, ảnh nhỏ để không hết bộ nhớ trên CI); PNG 1000 × 800 giữ 1000 × 800 |
| KB-3: vùng trong suốt ra trắng | Unit test (Robolectric `NATIVE`) | `PhotoPreparerTest.prepare_transparentBecomesWhite`: PNG 10 × 10 trong suốt hoàn toàn; điểm giữa của kết quả trắng (mỗi kênh từ 245) |
| KB-3: tệp không phải ảnh | Unit test (Robolectric `NATIVE`) | `PhotoPreparerTest.prepare_notAnImage_returnsNull` (tệp chữ), `prepare_missingFile_returnsNull`; cả hai ghi một dòng `warn` chứa "Photo not usable" và không chứa đường dẫn tệp |
| Ảnh xem trước đủ nét | Unit test (Robolectric `NATIVE`, `xxhdpi`) | `PhotoPreparerTest.preview_is48dpForDensity`: ảnh xem trước của PNG 1000 × 800 là 144 × 144 px |
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
| Spec mục 5: `new_project_native` giữa "Frame rate" và "Background", cách 24 dp | Compose UI test (`NATIVE`) | `NewProjectScreenTest.ad_betweenFrameRateAndBackground`: khe giả 250 dp tag `AD`; `AD.top - FPS_30.bottom` và `"Background".top - AD.bottom` trong khoảng 24 ± 1 dp; khe nằm trong `CONTENT` (phần cuộn), không trong phần đáy. `NewProjectScreenTest.adSlotEmpty_frameRateToBackgroundIs24dp`: khe mặc định (`NativeAdSlot`, không có `AdsController`, nên vẽ `Box` cao 0); `"Background".top - FPS_30.bottom` trong khoảng 24 ± 1 dp, không phải 48 |
| Spec mục 5: giữ chỗ khi chưa có quảng cáo | Rà soát code (ngoại lệ viết test trước) và Compose UI test | Vị trí `NEW_PROJECT_NATIVE` và `keepSpaceWhenEmpty = true` của khe mặc định kiểm bằng rà soát code; hành vi giữ chỗ đã có test ở `:core:ads`. `NewProjectScreenTest.defaultAdSlot_noController_drawsNothing`: khe mặc định không ném lỗi và cao 0 dp khi không có `AdsController` |
| `create_inter` mặc định khi không có `AdsController` | Compose UI test | `NewProjectRouteTest.defaultCreateAd_noController_opensAtOnce`: `createAd` mặc định, `interstitialAllowed = true`; `onOpenProject` gọi ngay đúng một lần |
| MC-13: khai `InContentAds` | Rà soát code (ngoại lệ viết test trước) | `:core:ads` không có điểm nối công khai để đọc danh sách đã khai; rà soát độc lập kiểm `NewProjectScreen` bọc gốc bằng `InContentAds(listOf(AdPlacement.NEW_PROJECT_NATIVE))` |
| Spec mục 6: `project_create` và tham số | Unit test | `NewProjectViewModelTest.create_logsProjectCreate_defaults`: đúng một sự kiện `AnalyticsEvent("project_create", mapOf("ratio" to "9:16", "size" to "1080p", "fps" to "12", "background" to "white", "is_default" to "true"))`; `create_logsProjectCreate_changed`: 16:9, 720p, 30, photo thì `"16:9"`, `"720p"`, `"30"`, `"photo"`, `"false"`; `changedThenBack_isDefaultTrue`; `nameChangedOnly_isDefaultTrue` (theo câu hỏi 3); `createFailed_logsNothing` |
| Spec mục 6: không ghi tên dự án | Unit test | Trong các test trên: không tham số nào chứa tên đã nhập ("Secret name") |
| KB-3: hết chỗ thì báo lỗi, giữ lựa chọn | Unit test và Compose UI test | `NewProjectViewModelTest.noSpace_keepsChoicesAndShowsError`: kho trả `Failure(NO_SPACE)`; `error == NO_SPACE`, `creating == false`, lựa chọn 16:9, 720p giữ nguyên, không phát `ProjectCreated`, không sự kiện; bấm lại "Create" thì gọi kho lần hai. `NewProjectRouteTest.noSpace_showsMessage`: thông báo có chữ "Not enough space on your phone. Free up some space and try again." |
| KB-3: lỗi ghi khác | Unit test và Compose UI test | `NewProjectViewModelTest.writeFailed_showsGenericError`; `NewProjectRouteTest.writeFailed_showsMessage`: thông báo có chữ "Couldn't create the project. Try again." |
| Quay lại khi đang tạo không để dự án dở | Compose UI test | `NewProjectRouteTest.backWhileCreating_ignored`: `create` treo, bấm "Create", gọi `onBackPressedDispatcher.onBackPressed()`; `onClose` 0 lần, nút đóng `assertIsNotEnabled`; hoàn tất `create` lỗi thì quay lại gọi `onClose` đúng một lần. `NewProjectRouteTest.backAfterCreated_noSecondClose`: `onOpenProject = null`, tạo thành công; `onClose` gọi một lần do hiệu ứng; quay lại sau đó không gọi thêm (`onClose` vẫn một lần) |
| Dựng lại khi quảng cáo còn che | Compose UI test | `NewProjectRouteTest.recreatedWhileAdPending_opensOnce`: `createAd` giả giữ `onFinished`; sau `ProjectCreated` dựng lại activity (cách của dòng KB-12); `onOpenProject` (hay `onClose` khi `null`) gọi đúng một lần sau khi dựng lại; gọi `onFinished` cũ sau đó không gọi thêm |
| KB-3: bấm "Create" hai lần nhanh | Unit test và Compose UI test | `NewProjectViewModelTest.doubleCreate_createsOnce`: `create` treo, gọi `onCreate()` hai lần, hoàn tất; kho đếm 1, một `ProjectCreated`, đúng một sự kiện `project_create`. Đối chứng: sau `Failure` gọi lại thì đếm 2. `NewProjectScreenTest.createDisabledWhileCreating`: `creating = true` thì `CREATE` `assertIsNotEnabled` |
| KB-2: cờ màn vẽ tắt thì "Coming soon", không tạo | Unit test và Compose UI test | `NewProjectViewModelTest.editorOff_comingSoonNoCreate`: kho đếm 0, phát `ShowComingSoon`, không sự kiện; cờ bật lại rồi bấm thì tạo (cổng mở lại). `editorTurnsOffAfterOpen_blocks`: cờ bật lúc mở, tắt trước khi bấm thì không tạo. `NewProjectRouteTest.editorOff_showsComingSoon` thấy chữ "Coming soon" |
| KB-13: cờ projects tắt | Unit test và Compose UI test | `NewProjectViewModelTest.projectsFlagOff_closes`: phát `Close` một lần; `NewProjectRouteTest.projectsFlagOff_callsOnClose`: `onClose` gọi đúng một lần |
| KB-12: xoay, gập giữ lựa chọn | Compose UI test | `NewProjectRouteTest.recreate_keepsChoices`: ViewModel tạo bằng `viewModel { NewProjectViewModel(...) }` trong activity; chọn 16:9, 720p, `BLUE`, tên "Ball"; `activityRule.scenario.recreate()` rồi `onActivity { it.setContent { ... } }` dựng lại nội dung (cách của `core/ads/src/test/.../NativeFullScreenStepRecreateTest.kt`, vì `ComponentActivity` không tự dựng lại nội dung), ViewModel lấy lại từ `ViewModelStore` của activity; `RATIO_16_9`, `SIZE_720P`, `BG_BLUE` được chọn, ô tên "Ball" |
| KB-3: màn 360 × 640, "Create" luôn thấy, không che lựa chọn cuối | Compose UI test (`NATIVE`, khung `w360dp-h640dp-xxhdpi`) | `NewProjectScreenTest.small_createVisibleAndLastRowReachable`: khe giả 250 dp; `CREATE` nằm trọn trong cửa sổ; `performScrollTo` nút `BG_PHOTO` rồi `BG_PHOTO.bottom <= CREATE` phần đáy `.top` |
| Màn hình thấp | Screenshot test khung `w360dp-h640dp-xxhdpi`, trạng thái nhiều nội dung nhất | `NewProjectScreen_default_small.png` (khe giả 250 dp; `NewProjectScreen` nhận `scrollState = ScrollState(Int.MAX_VALUE)` nên cuộn hết xuống ngay lần vẽ đầu). Lớp screenshot chỉ chụp bằng khối `captureRoboImage("src/test/screenshots/<Tên>.png") { ... }`, không thêm `createComposeRule()` |
| Đúng thiết kế | Screenshot test cùng tên với từng ảnh thiết kế, ảnh so sánh `scripts/design-compare.sh projects`, rà soát độc lập theo mục 7 của mẫu thiết kế | `NewProjectScreen_default_light.png`, `NewProjectScreen_default_dark.png`: khung `en-w411dp-h891dp-xxhdpi`, đồ họa `NATIVE`, khe giả `NativeAdCard` medium với chữ minh họa của thiết kế, tên "My animation 5". Lệch có chủ ý: ô màu thứ hai (mục 2) |
| Ảnh thiết kế của ticket khác (T-009, T-012, T-013): `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark`, `ProjectsScreen_list_light`, `ProjectsScreen_list_dark`, `ProjectsScreen_list_light_unfolded`, `ProjectsScreen_list_dark_unfolded`, `ProjectsScreen_menu_light`, `ProjectsScreen_menu_dark`, `ProjectsScreen_restoring_light`, `ProjectsScreen_restoring_dark`, `ProjectsScreen_restore-error_light`, `ProjectsScreen_restore-error_dark` | Không thuộc ticket này | Không có |
| Nền là ảnh | Screenshot test | `NewProjectScreen_photo_light.png`: `photoPreview` là ảnh dựng trong test (ô màu kẻ sọc), 16:9, 720p, 24 fps |
| Bố cục rộng (spec câu hỏi 21) | Screenshot test khung `en-w673dp-h841dp-xxhdpi` | `NewProjectScreen_default_unfolded.png`; kèm `NewProjectScreenTest.unfolded_contentAtMost600dpCentered` (bề rộng `CONTENT` ≤ 600 dp, lề trái bằng lề phải ± 1 dp) |
| Ô màu là màu cố định ở cả hai theme | Compose UI test (`NATIVE`) | `NewProjectScreenTest.swatchColors_fixedInBothThemes`: vẽ `decorView` ra `Bitmap`, đọc điểm giữa `BG_DARK` ở theme sáng và tối đều là `#22262E` (sai số 2 mỗi kênh); `BG_BLUE` là `#2F6FE0` |
| KB-3: chọn ảnh trên máy thật (bộ chọn của hãng, ảnh chụp dọc có EXIF) | Kiểm tra thủ công (máy thật: khác, bộ chọn ảnh khác nhau theo hãng và bản Android, Android 8 đến 10 dùng bộ chọn tài liệu; cần ảnh chụp bằng camera thật có EXIF xoay) | Trên bản staging: chụp một ảnh dọc, tạo dự án 9:16 nền "Photo" với ảnh đó; ảnh thu nhỏ của thẻ ở danh sách đứng đúng chiều; xóa ảnh gốc khỏi thư viện, mở lại app, thẻ vẫn có ảnh nền |
| `create_inter` thật từ dự án thứ 2 | Kiểm tra thủ công (máy thật: quảng cáo thật, Remote Config) | Trên bản staging, bật `is_show_create_inter` trên Firebase staging, xem log bằng `adb logcat -v threadtime | grep ' VGL\.'`. Dữ liệu app mới: dự án đầu không có interstitial và không có dòng log nào của `create_inter`, vì feature không gọi quảng cáo. Dự án thứ hai: có interstitial và dòng `Interstitial shown: create_inter` (`core/ads/.../AdsController.kt`, hàm hiện interstitial), có thể kèm native toàn màn hình theo `native_combo`. Tạo tiếp ngay trong khoảng `ads_interval` thì không có quảng cáo, có dòng `Full-screen create_inter skipped: THROTTLED` (cùng tệp, hàm ghi lượt bỏ qua). `ads_interval` mặc định 45 giây (`docs/remote-config-keys.md`; spec ghi 30), và quảng cáo mở app cũng tính vào khoảng này: dự án thứ hai tạo ngay sau quảng cáo mở app có thể bị `THROTTLED`, thì chờ hết khoảng rồi tạo lại |
| KB-3: từ danh sách mở màn Tạo dự án, "Create" tạo dự án và về danh sách | Kiểm tra thủ công (emulator: `app/src/deviceScenarios/T-011-tao-du-an.txt`) | Bản staging mới cài: bấm "New animation", thấy `new_project_screen`; bấm "Create"; về `projects_screen` và có đúng một thẻ `project_card_` (dự án đầu tiên nên không có `create_inter`) |

**Ngoại lệ viết test trước:** tệp kịch bản `app/src/deviceScenarios/T-011-tao-du-an.txt` chạy ở `device-session` cuối phiên, không "thất bại trước" được. Biểu tượng `ic_image.xml` (chỉ là tài nguyên; dùng ở nút "Photo" mà screenshot test vẽ). Khai `InContentAds` của màn (MC-13) và vị trí, `keepSpaceWhenEmpty` của khe `new_project_native` mặc định kiểm bằng rà soát code, vì `:core:ads` không có điểm nối công khai để đọc. Chuỗi trong `strings.xml` (chỉ là tài nguyên; chữ tiếng Anh được `texts_matchDesign` kiểm). Phần nối `AppNavHost.kt` mặc định (`AppScreens()` dùng màn thật cần Hilt) không có test riêng; test vỏ app kiểm cách nối bằng bản giả, `scripts/check.sh` build `:app` kiểm đồ thị Hilt. Người duyệt cần đồng ý.

Nội dung tệp `app/src/deviceScenarios/T-011-tao-du-an.txt` (cú pháp của `docs/ops/test-sessions.md`, mục 5; phần đầu giống `T-009-danh-sach-du-an.txt`). Đã chạy `scripts/device-scenario.py check` trên bản tạm ngoài repo (2026-11-23): chỉ báo `new_project_screen` (dòng 13) và `new_project_create` (dòng 14, 16) chưa có trong mã nguồn, đúng vì code chưa viết; không lỗi cú pháp. Phiên tạo tệp và chạy lại lệnh này sau khi viết màn. `fresh` xóa dữ liệu nên đây luôn là dự án đầu tiên: không có `create_inter`, không cần cặp chờ nút đóng quảng cáo sau "Create". T-018 sửa hai dòng cuối khi "Create" mở màn vẽ.

```text
ticket: T-011
criterion: Từ danh sách trống bấm "New animation" mở màn Tạo dự án; bấm "Create" thì về danh sách có một dự án mới
timeout: 120
# Same entry as T-009: the first-open app open ad may show over loading, and loading may stop with a retry button.
fresh
consent decline 30
wait id=projects_screen | text="Continue to app" | desc="Continue to app" | id=loading_retry 60
tap_if id=loading_retry
wait id=projects_screen | text="Continue to app" | desc="Continue to app" 60
tap_if text="Continue to app" | desc="Continue to app"
wait id=projects_screen 30
tap id=projects_new
wait id=new_project_screen 15
wait id=new_project_create enabled=true 15
shot new_project
tap id=new_project_create
wait id=projects_screen 30
count idp=project_card_ 1
shot projects_after_create
```

### 4.1. Chạy thử

- **Đã chạy thử:** 2026-11-23, trên `fd811d0` (nhánh chính), test tạm `TmpPhotoTrialTest` trong `feature/projects/src/test` (Robolectric `NATIVE`, sdk 35), không commit, đã xóa. Test dựng JPEG 40 × 20 (nửa trái đỏ, nửa phải xanh), ghi EXIF hướng 6 bằng `android.media.ExifInterface.saveAttributes`, đọc lại hướng qua `ContentResolver.openInputStream` của uri `file://` bằng `ExifInterface(InputStream)`, giải mã bằng `BitmapFactory.decodeStream`, xoay 90 bằng `Matrix`; gọi `ActivityResultContracts.PickVisualMedia().createIntent(context, PickVisualMediaRequest(ImageOnly))`.
- **Lệnh:** `./gradlew :feature:projects:testDebugUnitTest --tests '*TmpPhotoTrialTest*'`, rồi `scripts/check.sh` (2026-11-23, cùng test tạm). Dòng cuối của `check.sh`: `== Cổng chất lượng: QUA` (Test JVM 2922 test, 0 lỗi; `TmpPhotoTrialTest` 1 test, 0 lỗi, in cùng các giá trị dưới).
- **Kết quả:** qua. Hướng đọc được là 6; ảnh giải mã 40 × 20, sau khi xoay 20 × 40; điểm (10, 5) là `#FE0000` (đỏ), điểm (10, 35) là `#0000FE` (xanh), đúng chiều xoay của mục 3.1 và đủ cho câu khẳng định của `PhotoPreparerTest.prepare_rotatesByExif` (sai số 40 mỗi kênh). `PickVisualMedia().createIntent` trên Robolectric sdk 35 trả action `android.provider.action.PICK_IMAGES`, type `image/*`, không có lớp nào thiếu trên classpath test của `:feature:projects`. Phương án dự phòng ở mục 3.4 chưa cần thử. Chưa chạy thử: đọc EXIF của PNG, thu nhỏ 8000 × 2000, `ActivityResultRegistry` giả.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 1 (quảng cáo: thêm hai vị trí `new_project_native`, `create_inter` vào màn hình), 4 (thu thập dữ liệu: thêm sự kiện `project_create`). Rủi ro Cao, cần duyệt chéo; người phụ trách sản phẩm biết trước khi duyệt (risk-scoring.md, mục 3). Bảng điểm dưới chỉ để tham khảo. Thêm `implementation(project(":core:data"))` vào `feature/projects/build.gradle.kts` không chạm điểm chặn 7 (cấu hình build): chỉ là phụ thuộc giữa hai module có sẵn, không đổi plugin, phiên bản hay cấu hình dùng chung, giống cách T-009 xử lý `build.gradle.kts` của module (câu hỏi 9 của T-009). Khối `maxHeapSize` dự phòng cũng chỉ ở tệp của module này.

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 3 | Hai module (`:feature:projects`, `:app`), thêm phụ thuộc `:core:data` vào file build của feature, đổi kiểu công khai `AppScreens.projects`, đổi một giá trị mặc định ở hai script dùng chung (ngân sách kịch bản) |
| B. Độ rõ của yêu cầu | 2 | Spec đã xác nhận và có thiết kế, nhưng còn câu hỏi ảnh hưởng tới cách làm: điều hướng khi chưa có màn vẽ, tên layer đầu, `is_default` có xét tên không, lời báo ảnh lỗi (mục 8) |
| C. Khả năng kiểm chứng tự động | 2 | Phần lớn có test JVM và một kịch bản emulator; nhưng bộ chọn ảnh thật của hãng, EXIF của ảnh chụp thật và quảng cáo thật (`create_inter`, `new_project_native`) chỉ kiểm được trên máy thật |
| D. Ảnh hưởng chéo | 2 | Sửa `AppNavHost.kt` mà các ticket màn hình khác (T-013, T-016, T-018, T-050) cũng sửa; cùng module và gói với T-010 (khác file) |
| E. Độ mới của kỹ thuật | 2 | Photo Picker (`PickVisualMedia`), đọc EXIF và xoay, thu nhỏ ảnh lần đầu có trong dự án; chạy thử ở mục 4.1 |
| **Tổng** | 11 | **Mức: Cao** (chạm điểm chặn 1 và 4) |

## 6. Ảnh hưởng chéo

- Script dùng chung của dự án mẫu bị chạm: `scripts/device-scenario.py`, `scripts/device-session.sh`, `docs/ops/test-sessions.md`, chỉ đổi mặc định của `DEVICE_SCENARIOS_BUDGET` (600 thành 800). Không cần đề xuất về dự án mẫu: đây là giá trị riêng của app, đã có trong bảng của `docs/ops/to-app.md`, mục 6 (giá trị riêng của app nằm trong file của dự án mẫu, đặt lại sau mỗi lần đồng bộ). Mô tả PR ghi "giá trị chỉ app cần, không đề xuất về dự án mẫu" và nhắc đặt lại 800 sau mỗi lần lấy bản mẫu.
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

Các đề xuất dưới đây chờ người phụ trách sản phẩm trả lời trong phiên duyệt đợt (ADR-0048, phần C); lời duyệt ghi nguyên văn ở mục 9. Lời "Duyệt mọi đề xuất của AI" ngày 2026-11-23 có trước các đề xuất này, nên không thay cho lời duyệt. Mỗi ý là một đề xuất duy nhất, plan đã viết theo nó.

**Một dòng cho người phụ trách sản phẩm:** `new_project_native` thuộc nhóm B, mặc định tắt; `create_inter` không bao giờ hiện ở lần "Create" đầu tiên của máy, từ lần sau theo `ads_interval` (mặc định 45 giây, tính cả quảng cáo mở app) và có thể nối `native_combo`; `project_create` gửi 5 tham số (`ratio`, `size`, `fps`, `background`, `is_default`), không gửi tên; có hai lời báo lỗi mới không có trên thiết kế (ý 4).

1. **Tạo xong khi màn vẽ chưa có.** Cờ màn vẽ bật sẵn trên staging, nhưng chưa có đích màn vẽ. Đề xuất: tạo dự án, hiện `create_inter` nếu đủ điều kiện, rồi quay về danh sách (dự án mới ở đầu), không hiện thông báo. T-018 nối `onOpenProject` và hoàn tất tiêu chí "mở ngay màn vẽ, 2 lần chạm".
2. **Tên layer đầu tiên** (`NewProject.firstLayerName` bắt buộc; spec và thiết kế không ghi). Đề xuất: "Layer 1" (tiếng Việt "Lớp 1"), chuỗi trong `strings.xml` của `:feature:projects`.
3. **`is_default` của `project_create`.** Đề xuất: chỉ xét bốn nhóm (khung hình, độ phân giải, tốc độ, nền), so giá trị lúc bấm với giá trị chọn sẵn (đổi rồi đổi lại vẫn là `true`); đổi tên không làm `is_default` thành `false`.
4. **Hai lời báo lỗi mới.** Ảnh không dùng được hay không mở được bộ chọn ảnh: giữ nền trước, thông báo ngắn "Couldn't use this photo. Try another one." ("Không dùng được ảnh này. Hãy thử ảnh khác."). Lỗi ghi khác hết chỗ: "Couldn't create the project. Try again." ("Không tạo được dự án. Hãy thử lại."). Hết chỗ và "Coming soon" dùng lại chuỗi có sẵn của module.
5. **Bộ chọn ảnh bản Google Play cho Android 8 đến 10.** Đề xuất: không thêm khai báo `ModuleDependencies` vào manifest; máy cũ dùng bộ chọn tài liệu của hệ thống, vẫn không hỏi quyền, đúng spec.
6. **Mất ảnh đã chọn khi hệ thống đóng app ở nền.** Đề xuất: chấp nhận ở bản đầu (màn mở lại với giá trị chọn sẵn); KB-12 chỉ đòi xoay và gập.
7. **Ô màu thứ hai lệch ảnh thiết kế** (`#22262E` thay `#E3E6EA`, theo plan T-004, câu hỏi 1). Đề xuất: giữ màu cố định, ghi lệch có chủ ý trong mô tả PR; `scripts/design-compare.sh` báo lệch ở ô này tới khi Figma được sửa.
8. **Cờ Cài đặt chỉ đọc giá trị đầu.** `FeatureFlag.SETTINGS` chỉ dùng để tính lựa chọn sẵn lúc mở màn, khác câu "theo dõi, không đọc một lần" của ADR-0003. Đề xuất: chấp nhận, vì cờ đổi sau khi mở không được đè lựa chọn của người dùng (test `settingsFlagTurnsOnLater_keepsChoices`). Cờ màn vẽ và cờ projects vẫn theo dõi suốt.
9. **Ảnh thu nhỏ vẽ trước khi trả kết quả tạo.** `thumbnails.update` chạy trong `create` của kho, có thể chậm với ảnh nền 3840. Đề xuất: giữ như mục 3.1, vì `update` không ném lỗi và nút đã tắt khi đang tạo.
10. **Tên mặc định không dịch.** Đề xuất: `new_project_default_name` ghi `translatable="false"`, tên luôn là "My animation <số>" như spec; tên màu cho trợ năng là "White", "Dark gray", "Blue".
11. **Tăng heap của test khi CI hết bộ nhớ.** Đề xuất: dùng cùng dự phòng đã duyệt ở T-009, T-012 (mục 3.4).
12. **`scripts/check.sh` của phần chạy thử:** đã chạy ngày 2026-11-23 cùng test tạm, kết quả `== Cổng chất lượng: QUA` (mục 4.1). Không cần người làm gì thêm.

## 9. Quyết định duyệt

<!-- Người duyệt điền, hoặc AI ghi theo lời đồng ý trong phiên của người duyệt, nguyên văn (ADR-0048, phần C). Plan `lane: quick`: ghi "Duyệt trong phiên làm nhanh" kèm lời đồng ý. -->

- **Quyết định:** Duyệt
- **Người duyệt:** Nhật, **duyệt chéo:** Nhật (nhóm chỉ có một dev, theo risk-scoring.md mục 3)
- **Ghi chú:** Duyệt cả đợt 20261123-03 ngày 2026-11-27, trong phiên, lời đồng ý nguyên văn: "Duyệt đợt 20261123-03: plan T-012 phiên bản 2, T-010, T-011, T-056 và danh sách RUN-20261123-02, bắt đầu phiên khi merge; đồng ý mọi đề xuất ở mục 8 của bốn plan, kể cả chọn (a) cho FB-20261123-01. Tôi vừa duyệt vừa duyệt chéo vì nhóm chỉ có một dev." Câu trả lời cho mục 8: đồng ý mọi đề xuất (câu 1 tới 12), kể cả dòng cho người phụ trách sản phẩm.

## 10. Nhật ký thực hiện

<!-- AI ghi khi task đã có kết quả cuối (đã merge hoặc đã dừng), trên nhánh báo cáo của phiên (auto-run.md, bước 10). Mục mới thêm vào cuối, không sửa mục cũ. -->

### RUN-20261123-02, phiên bản plan 1

- **Kết quả:** đã merge (commit `cc32b8b` trên nhánh chính, 2026-11-27 08:22 UTC).
- **Nhánh, PR:** `claude/T-011-man-tao-du-an`, PR #45. Commit: `5ccca07` (test, kèm khung rỗng và `@Binds` của `di/ProjectsModule.kt` để đồ thị Hilt của `:app` biên dịch), `2daa086` (code), `fd30be3` (test, sửa theo rà soát), `28d85b9` (code, sửa theo rà soát).
- **Đã làm:** màn Tạo dự án (tên, khung hình, fps, màu nền hay ảnh, đọc EXIF), `new_project_native` và `create_inter`, sự kiện `project_create`, nối `AppNavHost`, kịch bản emulator `T-011-tao-du-an.txt`, ngân sách kịch bản 600 lên 800 giây. Bước 3 đến 5 do phiên con làm trong worktree riêng.
- **Test viết trước:** 85 test (79 ở `:feature:projects`, 6 ở `AppNavHostTest`), đều thất bại đúng lý do trên khung rỗng; 5 test ảnh qua ngay (ảnh chỉ ghi sau), đúng plan; 11 test viết chặt bằng đối chứng. Test JVM mọi module ở bước 3: không test cũ nào đỏ.
- **Rà soát độc lập:** lần 1: 1 chặn, 1 nên sửa, 6 ghi chú. Chặn: mã dự án chờ mở giữ trong ViewModel, khác cơ chế `rememberSaveable` của mục 3.1: đã sửa về `rememberSaveable` `pendingOpen` (lần sửa 1), thêm dấu `LeftMark` vì trạng thái của composition cũ còn giữ mã sau khi dựng lại (rà soát lại xếp là chi tiết cài đặt; câu cuối của mục 3.1 dòng "Dựng lại activity" mô tả chưa đủ). Nên sửa: xoay máy khi đang tạo có thể vừa mở dự án vừa hiện `create_inter` (cơ chế ViewModel): test mới `recreatedWhileCreating_writtenAfterOldScreenLeft_opensOnce`, đỏ với `2daa086`, qua với cơ chế của plan, đột biến (bỏ ghi `pendingOpen`; mở trước khi gọi quảng cáo) làm nó đỏ; khẳng định `adCalls == 1` theo mục 3.1. Rà soát lại vòng 1: 0 chặn, 0 nên sửa, 3 ghi chú. Ghi chú khác: nút "Create" cao 52 dp (sàn `PrimaryButton`) so với 48 dp thiết kế; thẻ native giả theo `NativeAdCard`; ô khung hình và chip không tắt khi đang tạo; `aspectRatioOf` gọi lại chính nó với giá trị mặc định.
- **Kết quả CI:** `scripts/check.sh` trên máy: QUA, 3037 test JVM, 0 lỗi. `pr-check` của PR #45 (commit `28d85b9`, lần chạy 45962606211): xanh, một vòng.
- **Lệch so với plan:** ngoài mục 3.2: `docs/features/projects/design/review/` (`summary.md`, `NewProjectScreen_default_{light,dark}_compare.png`, tài liệu, auto-run.md bước 7). Bước 4 (chi tiết test), sửa file test sau commit test: `NewProjectRouteTest` dùng chung một lambda nội dung cho `show()` và `recreate()` qua `setContent` của activity, `recreate()` thêm `beforeContent` (khóa của `rememberSaveable` theo vị trí lambda; trước: `recreatedWhileAdPending_opensOnce` đỏ với cơ chế của plan, sau: 20/20 qua; không câu nào đổi hay nới); `newAnimation_opensNewProject` tách hai (`newAnimation_twiceFast_oneEntry`); "ViewModel giả" của `NewProjectRouteTest` là ViewModel thật trên kho giả, đo qua `OrderLogger` và `prepare:<uri>`. Không dùng phương án dự phòng. Không câu khẳng định cũ viết lại, không bỏ test.
- **Việc còn lại:** kiểm trên máy thật (Photo Picker hệ thống, EXIF ảnh camera thật, `create_inter` thật); kịch bản emulator ở `device-session` cuối phiên.
- **Cần người xem:** 5 ảnh chuẩn mới `feature/projects/src/test/screenshots/NewProjectScreen_{default_light,default_dark,default_small,photo_light,default_unfolded}.png`; ảnh so sánh `docs/features/projects/design/review/NewProjectScreen_default_*_compare.png`; nút "Create" 52 dp.
