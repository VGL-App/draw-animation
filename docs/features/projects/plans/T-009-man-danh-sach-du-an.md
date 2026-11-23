---
ticket: T-009
title: Màn danh sách dự án, thanh điều hướng của vỏ app, màn bắt đầu
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score: 9                # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 15.2M        # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: []          # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # cần điền: risk_level high; bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-009: MÀN DANH SÁCH DỰ ÁN, THANH ĐIỀU HƯỚNG CỦA VỎ APP, MÀN BẮT ĐẦU

## 1. Tóm tắt

Tạo module `:feature:projects` theo mẫu `:feature:sample` với `ProjectsScreen` hai trạng thái `empty` và `list` (lưới hai cột, thẻ có ảnh thu nhỏ đọc từ `:core:project`), đặt làm màn sau màn Loading khi cờ `feature_projects_enabled` bật, còn cờ tắt thì về màn mẫu như hôm nay. Thanh điều hướng Projects/Lessons là composable của `:app` (`AppNavigationBar`), chỉ hiện khi cờ Bài học bật. Mở dự án, Bài học, Cài đặt, Tạo dự án và nhập file là callback; đích chưa có (T-011, T-013, T-016, T-018, T-050) thì màn hiện "Coming soon". Kiểm chứng bằng unit test ViewModel, test giao diện Robolectric, screenshot test cùng tên ảnh thiết kế (không có quảng cáo) và test Compose dựng `AppNavHost` với các đích giả.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/projects/spec.md`:

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-1 | Chưa có dự án: hình minh họa, "Make your first animation", lời giới thiệu, "New animation", "Try a free lesson", đúng thiết kế màn trống | `ProjectsUiState.Empty`; hình minh họa vẽ bằng Compose theo `context/ProjectsScreen_empty_*.md` (khung 160 × 120 nền `canvas`, ba chấm `secondary`, vạch `onCanvas`) |
| KB-1 | Bấm "New animation" mở màn Tạo dự án | Callback `onNewProject`; T-009 chưa có màn Tạo dự án (T-011) nên `:app` truyền `null` và màn hiện "Coming soon" |
| KB-1 | Bấm "Try a free lesson" mở danh sách bài học | Callback `onOpenLessons`; chưa có màn Bài học (T-050): "Coming soon" |
| KB-1 | Cờ Bài học tắt: không có "Try a free lesson", thanh điều hướng không có "Lessons"; còn một mục thì ẩn cả thanh | Nút: `ProjectsViewModel` theo dõi `FeatureFlag.LESSONS`. Thanh: `:app` theo dõi cùng cờ, cờ tắt thì không vẽ `AppNavigationBar` (thanh chỉ có hai mục, tắt một là còn một) |
| KB-1 | Xóa dự án cuối thì về trống ngay | `ProjectRepository.projects` phát lại sau mỗi thay đổi; ViewModel ánh xạ danh sách rỗng thành `Empty`. Lối xóa thật là T-012; T-009 kiểm bằng kho giả phát danh sách rỗng sau danh sách một dự án |
| KB-1 | Đã làm bài học mà chưa tạo dự án vẫn trống | `ProjectRepository.projects` đã bỏ dự án kiểu bài học (T-002); test kho giả chỉ cho danh sách rỗng, kèm test hợp đồng của T-002 đã có |
| KB-1 | Nút quay lại ở danh sách đóng app | Loading rời back stack (`popUpTo<LoadingDestination> { inclusive = true }`), danh sách là mục duy nhất; test vỏ app |
| KB-1 | 360 × 640: cuộn được, hai nút không bị che | Nội dung trạng thái trống trong `verticalScroll`; screenshot `_small` và test vị trí nút |
| KB-1 | Mất mạng: popup chung | Không làm gì trong feature (ADR-0024); `ScreenName` tự ra `projects` |
| KB-2 | Có dự án: "New animation" ở đầu, "Your projects" + nhãn "Recent", lưới hai cột, đúng thiết kế | `ProjectsUiState.List`; `LazyVerticalGrid(GridCells.Fixed(2))`, nút và tiêu đề là mục `span` cả hàng |
| KB-2 | Thẻ: ảnh thu nhỏ, tên, "<số khung> frames · <tốc độ> fps" | Ảnh thu nhỏ: tệp `thumbnail.png` của dự án qua `ProjectStore.readFile(id, ProjectFile.Thumbnail)`; chưa có tệp (dự án vừa tạo, chưa rời màn vẽ) thì gọi `ProjectThumbnails.update(id)` một lần rồi đọc lại. Dòng phụ dùng `plurals` cho "frame"/"frames" |
| KB-2 | Sửa gần nhất đứng đầu | Thứ tự do `ProjectRepository.projects` (T-002); ViewModel giữ nguyên thứ tự, test kiểm không sắp lại |
| KB-2 | Chạm thẻ mở màn vẽ | Callback `onOpenProject(ProjectId)`; chưa có màn vẽ (T-018) hay cờ `EDITOR` tắt: "Coming soon" |
| KB-2 | Cờ màn vẽ tắt, chạm thẻ: "Coming soon", ở lại | ViewModel theo dõi `FeatureFlag.EDITOR`; T-009 chỉ có lối chạm thẻ ("Open" là T-012, "Create" là T-011) |
| KB-2 | Từ màn vẽ quay về: thẻ đứng đầu, ảnh và số khung đúng | Danh sách phát lại khi màn vẽ lưu qua `ProjectRepository`; ảnh thu nhỏ tải lại theo khóa `(id, updatedAtMillis)`. Kiểm đầy đủ sau T-018; T-009 kiểm bằng kho giả đổi `updatedAtMillis` |
| KB-2 | Danh sách dài cuộn được; thanh trên và thanh điều hướng giữ chỗ | Thanh trên nằm ngoài lưới; thanh điều hướng nằm ở `Scaffold` của `:app` |
| KB-2 | Tên dài cắt "…" một dòng, không đè dòng số khung | `maxLines = 1`, `TextOverflow.Ellipsis`; test `isLineEllipsized` và vị trí hai dòng |
| KB-2 (đặc biệt) | 100 dự án mở và cuộn không giật | Lưới lười, ảnh thu nhỏ tải khi thẻ hiện, giải mã với `inSampleSize` về cỡ thẻ; kiểm thủ công trên máy thật |
| KB-2 (đặc biệt) | Dự án hỏng: dự án khác vẫn hiện và mở được; thẻ hỏng có tên (nếu đọc được), ảnh trống; chạm thì báo không mở được; không tự xóa | `ProjectListItem.Unreadable` thành thẻ `readable = false`; chạm hiện thông báo "Can't open this project." (câu hỏi 3). Không đọc được tên (`name == null`) thì thẻ ghi chữ thay thế `projects_card_unnamed` "Untitled project" (câu hỏi 3). Menu chỉ còn "Delete" là T-012 |
| KB-11 | Bấm nút cài đặt mở Cài đặt | Callback `onOpenSettings`; chưa có màn Cài đặt (T-016): "Coming soon" |
| KB-11 | Cờ Cài đặt tắt: không có nút cài đặt | ViewModel theo dõi `FeatureFlag.SETTINGS`. Nút quyền riêng tư quảng cáo thay chỗ là T-056, không thuộc T-009. Giá trị chọn sẵn ở Tạo dự án là T-011 |
| KB-11 | Từ Cài đặt quay lại đúng chỗ cuộn | `rememberLazyGridState` trong `NavBackStackEntry` giữ vị trí; kiểm đầy đủ sau T-016 (câu hỏi 5) |
| KB-13 | Cờ projects tắt: vào màn mẫu, không vào được danh sách | ViewModel theo dõi `FeatureFlag.PROJECTS`; `false` thì `ProjectsUiState.Disabled`, `ProjectsRoute` gọi `onFeatureDisabled` một lần, `:app` thay đích bằng `SampleDestination` (`popUpTo<ProjectsDestination> { inclusive = true }`). Việc thay đích chỉ chạy một lần: cờ bật lại sau đó thì người dùng ở màn mẫu tới lần mở app sau (mục 3.1, câu hỏi 7) |

Thiết kế tham chiếu: `features/projects/design/README.md`, các ảnh trong `design/screens/`: `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark`, `ProjectsScreen_list_light`, `ProjectsScreen_list_dark`. Các ảnh khác của feature thuộc ticket khác (mục 4).

ADR liên quan: ADR-0001 (phụ thuộc module), ADR-0003 (cờ là `Flow`), ADR-0008 (hệ thống thiết kế), ADR-0019 (log), ADR-0020 (screenshot theo thiết kế), ADR-0024 (mạng), ADR-0031 (viết lại câu khẳng định cũ), ADR-0043 (test emulator chỉ sau merge), ADR-0045 (quảng cáo: không chạm).

## 3. Giải pháp

### 3.1. Cách làm

**Module.** `:feature:projects`, gói `com.vgl.draw.animation.feature.projects`, `build.gradle.kts` chỉ áp dụng `template.android.feature`, `namespace`, cộng `implementation(project(":core:project"))` (core, đúng ADR-0001). `settings.gradle.kts` thêm `include(":feature:projects")` sau `include(":feature:export")`. `:app` thêm `implementation(project(":feature:projects"))`.

**Dữ liệu.** `data/ProjectsRepository.kt` (interface, cùng gói `data`) gồm:

- `val items: Flow<List<ProjectListItem>>` lấy thẳng `ProjectRepository.projects`.
- `suspend fun thumbnail(id: ProjectId, maxWidthPx: Int): ImageBitmap?`: đọc `ProjectStore.readFile(id, ProjectFile.Thumbnail)`; `null` và dự án đọc được thì `ProjectThumbnails.update(id)` một lần rồi đọc lại; giải mã bằng `BitmapFactory` với `inSampleSize` lớn nhất (lũy thừa của 2) mà bề rộng ảnh vẫn ≥ `maxWidthPx` (ảnh 512 px, `maxWidthPx = 200` thì `inSampleSize = 2`, ra 256 px); mọi `IOException` hay giải mã hỏng trả `null` (thẻ vẽ ô trống màu `canvas`). Chạy trên `Dispatchers.IO` (tiêm `CoroutineDispatcher` để test).

`data/DefaultProjectsRepository.kt` là lớp `internal`, gắn bằng `di/ProjectsModule.kt` (`@Binds`).

**Trạng thái.** `ProjectsUiState` (sealed interface): `Loading` (khung đầu, chưa có dữ liệu hay cờ), `Disabled`, `Empty(showLessonButton, showSettingsButton)`, `List(cards: List<ProjectCardUi>, showSettingsButton)`. `ProjectCardUi(id, name: String?, frameCount, fps, updatedAtMillis, readable)`; `name == null` chỉ có ở thẻ hỏng không đọc được tên, màn vẽ chữ `projects_card_unnamed` "Untitled project" thay cho tên. Thanh trên (wordmark, nút nhập file, nút cài đặt) có ở cả `Empty` và `List`.

**ViewModel.** `ProjectsViewModel(repository, featureFlags, analytics, logger)`: `combine` của `items` với bốn cờ `PROJECTS`, `LESSONS`, `SETTINGS`, `EDITOR` (theo dõi, không đọc một lần), `stateIn(WhileSubscribed(5_000), Loading)`. Không ghi sự kiện phân tích nào (spec mục 6 không có sự kiện cho màn danh sách); `Analytics` nhận theo mẫu nhưng không gọi. Hành động: `onProjectClick(card)` gửi một `ProjectsEffect`: `OpenProject(id)` khi `readable` và cờ `EDITOR` bật, `ShowCannotOpen` khi thẻ hỏng, `ShowComingSoon` khi cờ `EDITOR` tắt. Hiệu ứng đi qua `Channel<ProjectsEffect>(Channel.BUFFERED)` và lộ ra bằng `val effects: Flow<ProjectsEffect> = channel.receiveAsFlow()`: hiệu ứng gửi khi màn chưa thu (lúc dựng lại) vẫn được giữ tới khi màn thu, và mỗi hiệu ứng chỉ được nhận một lần. Ảnh thu nhỏ: `thumbnails: StateFlow<Map<ProjectId, ImageBitmap?>>` và `onThumbnailNeeded(card, maxWidthPx)`; thẻ `readable = false` thì không đọc gì (ảnh trống, kể cả khi thư mục dự án hỏng vẫn còn `thumbnail.png` cũ); khóa bộ nhớ là `(id, updatedAtMillis)`, nên dự án vừa sửa tải ảnh mới; hai lần gọi cùng khóa đang tải thì chỉ một lần đọc (giữ một `Map` của `Job` đang chạy). Bộ nhớ ảnh có giới hạn: mỗi ảnh 512 px tốn khoảng 590 KB, nên `Map` giữ tối đa `THUMBNAIL_CACHE_SIZE = 40` ảnh theo kiểu LRU (`LinkedHashMap(accessOrder = true)`, bỏ ảnh dùng lâu nhất khi vượt); mỗi lần `items` phát danh sách mới, ảnh và `Job` của id không còn trong danh sách bị bỏ (dự án đã xóa không giữ ảnh). Việc bỏ ảnh chạy trong chính đường của `uiState` (`combine(items.onEach { repository.retainThumbnails(ids) }, …)`), không có lần thu `items` riêng nào (không `init { collect }`, không `launchIn`), nên kho dự án chỉ có một người thu và vẫn theo `WhileSubscribed(5_000)`; `retainThumbnails` là hàm của `ProjectsRepository`. Map LRU thay đổi được nên chỉ chạm dưới một khóa, và mỗi lần phát qua `StateFlow` là một bản chép `toMap()`. Thẻ bị bỏ khỏi bộ nhớ mà hiện lại thì gọi `onThumbnailNeeded` lần nữa và đọc lại. Ghi log (thẻ `Projects`): trạng thái chính đổi (`Empty`/`List` kèm số thẻ, `Disabled`), chạm thẻ hỏng, ảnh thu nhỏ đọc lỗi (không ghi tên dự án hay đường dẫn).

**Màn hình.** `ProjectsScreen.kt`: `ProjectsRoute(viewModel: ProjectsViewModel = hiltViewModel(), onNewProject: (() -> Unit)?, onOpenProject: ((ProjectId) -> Unit)?, onOpenLessons: (() -> Unit)?, onOpenSettings: (() -> Unit)?, onImport: (() -> Unit)?, onFeatureDisabled: () -> Unit)`; callback `null` nghĩa là đích chưa có, màn hiện "Coming soon" bằng `SnackbarHost` của chính màn (thông báo ngắn, tự ẩn). `ProjectsScreen(uiState, thumbnails, snackbarHostState, gridState: LazyGridState = rememberLazyGridState(), các callback)` không trạng thái; `rememberLazyGridState()` gọi ở đầu `ProjectsScreen`, trên nhánh `when (uiState)`, để vị trí cuộn được lưu theo back stack cả khi trạng thái thoáng qua `Loading` (KB-11). Chuỗi "Coming soon" của feature tên `projects_coming_soon`. Thành phần: `ProjectsTopBar` (chấm `primary` 14 dp, "Frameloop" `headlineSmall`, `IconButton` 48 dp `upload_file`, `settings`), `EmptyContent` (hình minh họa, tiêu đề, lời, `PrimaryButton` "New animation" có icon `add`, `FilledTonalButton` "Try a free lesson" có icon `school`, truyền `colors` rõ), `ProjectGrid` và `ProjectCard` (nền `surfaceContainer`, bo 14, lề 8/8/12/8, ô ảnh thu nhỏ tỷ lệ 168 × 94 bo 8 nền `canvas`, ảnh vẽ `ContentScale.Fit` giữa ô (dự án 9:16 hay 1:1 hiện trọn, hai bên là nền `canvas`, không cắt), tên `titleSmall`, dòng phụ `bodySmall` `onSurfaceVariant`). Số liệu lấy từ `design/context/ProjectsScreen_empty_*.md` và `ProjectsScreen_list_*.md`; khoảng cách ngoài hệ thống (12, 20) làm tròn về `Spacing` gần nhất chỉ khi `Spacing` có giá trị đó, không thì giữ số của thiết kế trong một hằng riêng của file (như các feature đã làm). Màu, chữ lấy từ `MaterialTheme`/`AppTheme`. `testTag`: `projects_screen` (gốc), `projects_empty`, `projects_grid`, `project_card_<id>`, `projects_new`, `projects_lesson`, `projects_settings`, `projects_import`.

**Icon.** Bộ icon lõi đi kèm `material3` 1.3.2 (có sẵn trên classpath biên dịch của feature, đã xem bằng `./gradlew :feature:sample:dependencies`) có `Icons.Rounded.Add`, `Icons.Rounded.Settings`, không có `upload_file`, `school`, `animation`. Ba icon này làm thành vector drawable trong `res/drawable/` từ SVG Material Symbols Rounded (giấy phép Apache 2.0), tải tại `https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/<tên>/default/24px.svg` (đã thử, trả 200): `viewportWidth/Height = 960`, một `<group android:translateY="960">` bọc `pathData` của SVG, `fillColor` `@android:color/white` (màu thật do `Icon` tô). Phiên chạy không gọi máy chủ ngoài được, nên `pathData` dán sẵn dưới đây (tải ngày 2026-11-22):

- `upload_file` (`ic_upload_file.xml` của feature):
  `M440-367v127q0 17 11.5 28.5T480-200q17 0 28.5-11.5T520-240v-127l36 36q6 6 13.5 9t15 2.5q7.5-.5 14.5-3.5t13-9q11-12 11.5-28T612-388L508-492q-6-6-13-8.5t-15-2.5q-8 0-15 2.5t-13 8.5L348-388q-12 12-11.5 28t12.5 28q12 11 28 11.5t28-11.5l35-35ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h287q16 0 30.5 6t25.5 17l194 194q11 11 17 25.5t6 30.5v447q0 33-23.5 56.5T720-80H240Zm280-560v-160H240v640h480v-440H560q-17 0-28.5-11.5T520-640ZM240-800v200-200 640-640Z`
- `school` (`ic_school.xml` của feature và `ic_nav_lessons.xml` của `:app`):
  `M242-249q-20-11-31-29.5T200-320v-192l-96-53q-11-6-16-15t-5-20q0-11 5-20t16-15l338-184q9-5 18.5-7.5T480-829q10 0 19.5 2.5T518-819l381 208q10 5 15.5 14.5T920-576v256q0 17-11.5 28.5T880-280q-17 0-28.5-11.5T840-320v-236l-80 44v192q0 23-11 41.5T718-249L518-141q-9 5-18.5 7.5T480-131q-10 0-19.5-2.5T442-141L242-249Zm238-203 274-148-274-148-274 148 274 148Zm0 241 200-108v-151l-161 89q-9 5-19 7.5t-20 2.5q-10 0-20-2.5t-19-7.5l-161-89v151l200 108Zm0-241Zm0 121Zm0 0Z`
- `animation` (`ic_nav_projects.xml` của `:app`):
  `M360-80q-58 0-109-22t-89-60q-38-38-60-89T80-360q0-81 42-148t110-102q20-39 49.5-68.5T350-728q33-68 101-110t149-42q58 0 109 22t89 60q38 38 60 89t22 109q0 85-42 150T728-350q-20 39-49.5 68.5T610-232q-35 68-102 110T360-80Zm0-80q33 0 63.5-10t56.5-30q-58 0-109-22t-89-60q-38-38-60-89t-22-109q-20 26-30 56.5T160-360q0 42 16 78t43 63q27 27 63 43t78 16Zm120-120q33 0 64.5-10t57.5-30q-59 0-110-22.5T403-403q-38-38-60.5-89T320-602q-20 26-30 57.5T280-480q0 42 15.5 78t43.5 63q27 28 63 43.5t78 15.5Zm120-120q18 0 34.5-3t33.5-9q22-60 6.5-115.5T621-621q-38-38-93.5-53.5T412-668q-6 17-9 33.5t-3 34.5q0 42 15.5 78t43.5 63q27 28 63 43.5t78 15.5Zm160-78q20-26 30-57.5t10-64.5q0-42-15.5-78T741-741q-27-28-63-43.5T600-800q-35 0-65.5 10T478-760q59 0 110 22.5t89 60.5q38 38 60.5 89T760-478ZM600-600Z`

Không thêm `material-icons-extended` (thư viện mới, sửa `libs.versions.toml`).

**Điều hướng.** `navigation/ProjectsNavigation.kt`: `@Serializable data object ProjectsDestination`, `NavGraphBuilder.projectsScreen(...)` cùng tham số như `ProjectsRoute`. `AppNavHost`: Loading xong thì `navigate(ProjectsDestination) { popUpTo<LoadingDestination> { inclusive = true } }`; `onFeatureDisabled` thì `navigate(SampleDestination) { popUpTo<ProjectsDestination> { inclusive = true } }`, chạy trên luồng chính vì gọi từ `LaunchedEffect` của Route. T-009 truyền `null` cho mọi callback mở đích khác.

Lần thay đích về màn mẫu chỉ chạy một lần và bỏ `ProjectsDestination` khỏi back stack. Ở lần mở đầu tiên trên production, Remote Config lỗi hay hết giờ chờ (10 giây, `RemoteConfigRefresher`) thì cờ `PROJECTS` còn giá trị mặc định `false`, app vào màn mẫu; cờ bật sau đó (lần tải sau thành công) thì người dùng vẫn ở màn mẫu tới lần mở app sau, vì màn mẫu không theo dõi cờ này. Plan giữ cách này theo câu trả lời câu hỏi 18 của spec (cờ chỉ để giấu feature trong lúc làm, production bật từ lần phát hành đầu) và hỏi lại ở câu hỏi 7; test `AppNavHostTest.projectsFlagOnAfterDisabled_staysOnSample` khóa hành vi đã chọn.

**Điểm nối test của `AppNavHost`.** `LoadingRoute`, `SampleRoute`, `ProjectsRoute` là `internal` và gọi `hiltViewModel()`, nên test của `:app` không dựng chúng. `AppNavHost` nhận thêm `screens: AppScreens = AppScreens()`, với `data class AppScreens(val loading: NavGraphBuilder.(onFinished: () -> Unit) -> Unit = { loadingScreen(it) }, val projects: NavGraphBuilder.(onFeatureDisabled: () -> Unit) -> Unit = { projectsScreen(onFeatureDisabled = it, các callback khác = null) }, val sample: NavGraphBuilder.() -> Unit = { sampleScreen() })` trong `AppNavHost.kt`. Mặc định là các hàm mở rộng công khai của feature, nên bản chạy thật không đổi. Test truyền đích giả: `composable<LoadingDestination> { LaunchedEffect(Unit) { loadingRuns++; onFinished() } }`, `composable<ProjectsDestination> { Box(Modifier.fillMaxSize().testTag("projects_screen")); if (projectsOff) LaunchedEffect(Unit) { onFeatureDisabled() } }`, `composable<SampleDestination> { Box(Modifier.testTag("sample_screen")) }`. Test chỉ đọc cây ngữ nghĩa (tag, chữ) và biến đếm của test; không bỏ phần nào mà câu khẳng định cần, vì mọi câu kiểm cách `AppNavHost` nối đích, không kiểm nội dung màn của feature (đã có test riêng ở feature).

**Lề hệ thống.** Hiện `NavHost` có `safeDrawingPadding()`. `Scaffold` mới đặt `contentWindowInsets = WindowInsets(0)`, và `NavHost` dùng `Modifier.padding(innerPadding).consumeWindowInsets(innerPadding).safeDrawingPadding()`: khi có thanh, phần đáy đã do `NavigationBar` (tự cộng lề thanh hệ thống) chiếm và không bị cộng lại; khi không có thanh, lề như hôm nay. Robolectric mặc định không có lề thanh hệ thống, nên test lề gửi một lề thanh điều hướng giả vào cửa sổ, như `PhoneInsets.kt` của `app/src/testStaging`: `WindowCompat.setDecorFitsSystemWindows(activity.window, false)` rồi `ViewCompat.dispatchApplyWindowInsets(decorView, insets)` với `navigationBars()` đáy `TEST_NAVIGATION_BAR_PX = 72` (24 dp ở `xxhdpi`). Hàm của `PhoneInsets.kt` là `internal` trong source set `testStaging`, test của `app/src/test` không gọi được, nên plan thêm hàm riêng `dispatchNavigationBarInsets(rule, bottomPx)` trong `app/src/test/.../navigation/NavigationBarInsets.kt`.

**Thanh điều hướng của vỏ app.** `app/.../navigation/AppNavigationBar.kt`: `AppNavigationBar(selected: TopLevelTab, onSelect: (TopLevelTab) -> Unit)` với `NavigationBar` hai `NavigationBarItem` "Projects" (icon `animation`) và "Lessons" (icon `school`), chỉ báo `primaryContainer`; `enum class TopLevelTab { Projects, Lessons }`. `AppNavHost` nhận thêm `lessonsEnabled: Boolean` và bọc `NavHost` trong `Scaffold(bottomBar = { if (lessonsEnabled && route là đích cấp cao) AppNavigationBar(...) })`; chạm "Lessons" khi chưa có đích Bài học (T-050) hiện "Coming soon" (chuỗi `app_coming_soon`, khác tên với chuỗi của feature) bằng `SnackbarHost` của `Scaffold`. `MainActivity` tiêm `FeatureFlags` và truyền `featureFlags.enabled(FeatureFlag.LESSONS).collectAsState(false)`. Hàm thuần `showsNavigationBar(route: String?, lessonsEnabled: Boolean)` trong cùng file để test. Bố cục cột bên trái của máy gập là T-014.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `settings.gradle.kts` | Thêm `include(":feature:projects")` sau `include(":feature:export")` |
| Tạo | `feature/projects/build.gradle.kts` | Plugin `template.android.feature`, `namespace = "com.vgl.draw.animation.feature.projects"`, `implementation(project(":core:project"))` |
| Tạo | `feature/projects/consumer-rules.pro` | Chép từ `:feature:sample`; không cần luật R8 (`assembleStagingRelease` qua khi chạy thử, mục 4.1) |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsUiState.kt` | `ProjectsUiState`, `ProjectCardUi`, `ProjectsEffect` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsViewModel.kt` | Mục 3.1, ViewModel |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreen.kt` | `ProjectsRoute`, `ProjectsScreen`, thanh trên, nội dung trống, `ProjectsTestTags` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectCard.kt` | Thẻ dự án, lưới, ô ảnh thu nhỏ |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/EmptyIllustration.kt` | Hình minh họa màn trống vẽ bằng Compose |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/ProjectsRepository.kt` | Interface |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultProjectsRepository.kt` | Lớp `internal`: danh sách, ảnh thu nhỏ |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/di/ProjectsModule.kt` | `@Binds` repository, `@Provides` dispatcher IO có qualifier |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/navigation/ProjectsNavigation.kt` | `ProjectsDestination`, `projectsScreen(...)` |
| Tạo | `feature/projects/src/main/res/values/strings.xml` | Chữ của bảng chữ thiết kế, "Coming soon", "Can't open this project.", `projects_card_unnamed` "Untitled project", `plurals` `projects_card_frames`, mô tả icon cho trợ năng |
| Tạo | `feature/projects/src/main/res/values-vi/strings.xml` | Bản tiếng Việt cùng tên (câu hỏi 4) |
| Tạo | `feature/projects/src/main/res/drawable/ic_upload_file.xml` | Vector Material Symbols Rounded `upload_file` |
| Tạo | `feature/projects/src/main/res/drawable/ic_school.xml` | Vector `school` (nút "Try a free lesson") |
| Tạo | `feature/projects/src/test/resources/robolectric.properties` | Như `:feature:sample` |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/MainDispatcherRule.kt` | Chép từ `:feature:sample` |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/FakeProjectsRepository.kt` | Kho giả: `MutableStateFlow` danh sách, ảnh thu nhỏ đặt sẵn, đếm số lần đọc |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsViewModelTest.kt` | Unit test mục 4 |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreenTest.kt` | Test giao diện Robolectric mục 4 |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsRouteTest.kt` | `onFeatureDisabled` gọi đúng một lần; callback `null` hiện "Coming soon" |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreenScreenshotTest.kt` | Screenshot test mục 4 |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultProjectsRepositoryTest.kt` | `@RunWith(RobolectricTestRunner::class)` và `@GraphicsMode(GraphicsMode.Mode.NATIVE)` (chế độ mặc định giải mã ra bitmap giả, nên test tệp hỏng và cỡ ảnh không có nghĩa). Ảnh thu nhỏ: có tệp, chưa có tệp (gọi `update` một lần), tệp hỏng, dự án hỏng, thu nhỏ theo bề rộng yêu cầu; PNG thử tạo bằng `Bitmap.compress` trong test; `ProjectStore` và `ProjectThumbnails` giả |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_empty_light.png` | Ảnh chuẩn mới |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_empty_dark.png` | Ảnh chuẩn mới |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_list_light.png` | Ảnh chuẩn mới |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_list_dark.png` | Ảnh chuẩn mới |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_empty_small.png` | Ảnh chuẩn mới, khung 360 × 640 |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_list_small.png` | Ảnh chuẩn mới, khung 360 × 640, 8 dự án có tên dài |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_list_unreadable.png` | Ảnh chuẩn mới: một thẻ hỏng giữa thẻ thường |
| Sửa | `app/build.gradle.kts` | `implementation(project(":feature:projects"))` |
| Sửa | `app/src/main/kotlin/com/vgl/draw/animation/navigation/AppNavHost.kt` | Đích Projects sau Loading, `onFeatureDisabled` về Sample, `Scaffold` có thanh điều hướng, tham số `lessonsEnabled` |
| Tạo | `app/src/main/kotlin/com/vgl/draw/animation/navigation/AppNavigationBar.kt` | `AppNavigationBar`, `TopLevelTab`, `showsNavigationBar` |
| Sửa | `app/src/main/kotlin/com/vgl/draw/animation/MainActivity.kt` | Tiêm `FeatureFlags`, truyền `lessonsEnabled` |
| Sửa | `app/src/main/res/values/strings.xml` | "Projects", "Lessons", "Coming soon" của vỏ app |
| Sửa | `app/src/main/res/values-vi/strings.xml` | Bản tiếng Việt |
| Tạo | `app/src/main/res/drawable/ic_nav_projects.xml` | Vector `animation` |
| Tạo | `app/src/main/res/drawable/ic_nav_lessons.xml` | Vector `school` |
| Tạo | `app/src/test/kotlin/com/vgl/draw/animation/navigation/AppNavigationBarTest.kt` | `showsNavigationBar`, chọn mục, "Coming soon" khi chạm Lessons |
| Tạo | `app/src/test/kotlin/com/vgl/draw/animation/navigation/AppNavigationBarScreenshotTest.kt` | Ảnh thanh điều hướng sáng, tối |
| Tạo | `app/src/test/screenshots/AppNavigationBar_light.png` | Ảnh chuẩn mới |
| Tạo | `app/src/test/screenshots/AppNavigationBar_dark.png` | Ảnh chuẩn mới |
| Tạo | `app/src/test/kotlin/com/vgl/draw/animation/navigation/AppNavHostTest.kt` | Test Compose Robolectric, `createAndroidComposeRule<ComponentActivity>()`, dựng `AppNavHost` với `AppScreens` giả (mục 3.1): sau Loading có `projects_screen`; cờ projects tắt thì `sample_screen`; Back ở danh sách kết thúc activity; dựng lại bằng `StateRestorationTester` giữ đích; cờ projects bật lại sau khi đã về màn mẫu thì vẫn ở màn mẫu; thanh điều hướng theo `lessonsEnabled` (cả khi đổi giữa chừng); chạm \"Lessons\" hiện \"Coming soon\"; nội dung không bị cộng lề hai lần (có lề thanh hệ thống giả) |
| Tạo | `app/src/test/kotlin/com/vgl/draw/animation/navigation/NavigationBarInsets.kt` | `internal fun dispatchNavigationBarInsets(rule: ComposeContentTestRule, bottomPx: Int)` và `TEST_NAVIGATION_BAR_PX = 72`: `WindowCompat.setDecorFitsSystemWindows(window, false)`, rồi `ViewCompat.dispatchApplyWindowInsets(decorView, …)` với lề `navigationBars()` ở đáy, trên luồng giao diện, rồi `waitForIdle()` (theo `PhoneInsets.kt` của `testStaging`, hàm đó `internal` ở source set khác) |
| Sửa | `app/src/androidTest/kotlin/com/vgl/draw/animation/AppLaunchTest.kt` | Màn bắt đầu của staging thành `projects_screen` (mục 3.5) |
| Sửa | `app/src/deviceScenarios/T-024-mo-app-tu-choi-dong-y.txt` | Ba dòng `wait id=sample_screen` thành `id=projects_screen` (mục 3.5) |
| Tạo | `app/src/deviceScenarios/T-009-danh-sach-du-an.txt` | Kịch bản emulator, nội dung ở mục 4 (dưới bảng) |

Nơi đọc các file có sẵn mà plan sửa (`grep -rn "settings.gradle\|AppNavHost\|sample_screen\|AppLaunchTest" scripts/ .github/ */src/test */src/androidTest */src/testStaging app/src/deviceScenarios`):

- `settings.gradle.kts`: `.github/workflows/pr-check.yml` có tệp này trong bộ lọc `release`, nên PR chạy thêm job build release; không đọc nội dung, không sửa.
- `sample_screen`: `AppLaunchTest.kt` và `T-024-mo-app-tu-choi-dong-y.txt` (đã có trong bảng); `SampleScreenTest` của `:feature:sample` vẫn đúng vì màn mẫu không đổi.
- `AppNavHost`: chỉ `MainActivity.kt` gọi.
- `settings.gradle.kts` còn được đọc ở: `app/src/test/kotlin/com/vgl/draw/animation/network/NetworkDialogLanguageTest.kt`, `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdsTestThemeMatchTest.kt`, `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/BrandTaglineLanguageTest.kt`, `feature/loading/src/test/kotlin/com/vgl/draw/animation/feature/loading/LoadingScreenLanguageTest.kt` (chỉ tìm thư mục gốc có tệp này); `scripts/ads-plan.py` (chỉ đọc `rootProject.name`); `scripts/tests/run.sh` (chép tệp vào repo thử và mô phỏng bộ lọc đường dẫn của workflow). Plan chỉ thêm một dòng `include`, không đổi `rootProject.name` hay vị trí tệp: không ảnh hưởng.
- `app/build.gradle.kts` còn được đọc ở: `app/src/test/kotlin/com/vgl/draw/animation/ProductionPropertiesTest.kt` (đọc khối flavor `staging`, `production` để kiểm khóa AppsFlyer; dòng mới nằm trong khối `dependencies`), `scripts/release.sh`, `scripts/setup-check.sh`, `scripts/ui-test-verdict.sh` (`applicationId`, `versionCode`, `ASSETS_BASE_URL`), `scripts/tests/run.sh` (bộ lọc đường dẫn của `pr-check`). Plan chỉ thêm một dòng `implementation(project(":feature:projects"))`, không đổi các dòng đó: không ảnh hưởng. Phiên không chạy `scripts/release.sh`.
- `MainActivity.kt`: `scripts/tests/run.sh` đếm đúng một lời gọi `exposeTestTagsForUiAutomator()` trong tệp. Plan giữ nguyên lời gọi đó, không thêm lời gọi thứ hai (thanh điều hướng nằm trong `Surface` đã có tag).
- `app/src/main/res/values/strings.xml`: `scripts/tests/run.sh` chỉ đọc `strings.xml` của repo app mới do `new-app.sh` sinh, không đọc tệp của repo này: không ảnh hưởng.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: đọc `PROJECTS`, `LESSONS`, `SETTINGS`, `EDITOR` đã có (T-006), không thêm cờ. Bản staging bật sẵn `PROJECTS`, `SETTINGS`, `EDITOR` (`StagingFeatureFlagsModule`), chưa bật `LESSONS`: trên staging thanh điều hướng và nút "Try a free lesson" ẩn tới T-054; nhánh cờ bật kiểm bằng test.
- Quảng cáo: Không chạm (T-010). Màn chưa bọc `InContentAds`; T-010 thêm.
- Sự kiện phân tích: Không chạm.
- File R2: Không chạm.
- **Quyền truy cập:** Không thêm. Đọc tệp trong bộ nhớ trong của app không cần quyền.
- **Thư viện và SDK:** Không thêm. Test vỏ app không dùng Hilt (`hilt-android-testing` không có trong `gradle/libs.versions.toml`): dùng `StateRestorationTester` và `createAndroidComposeRule` của `androidx-compose-ui-test-junit4` mà `:app` đã có ở cấu hình test (tiền lệ `AdGalleryScreenTest`). Dùng `Icons.Rounded.Add`, `Icons.Rounded.Settings` của bộ icon lõi đã đi kèm `androidx-compose-material3` trong `gradle/libs.versions.toml` (phụ thuộc bắc cầu, không khai báo thêm); `:feature:projects` thêm phụ thuộc module `:core:project` (có sẵn); `:app` thêm `:feature:projects`.

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** Không còn sau chạy thử (mục 4.1). Hai điểm trước đây chưa chắc đã thử bằng test tạm và qua: `StateRestorationTester` lưu và dựng lại back stack của `rememberNavController()` trong `AppNavHost` (đích giả Loading không chạy lại, `projects_screen` còn); lề thanh hệ thống giả gửi bằng `ViewCompat.dispatchApplyWindowInsets` tới được `Scaffold` và `NavigationBar` trên Robolectric `NATIVE`.
- **Điều kiện chuyển:** sau `emulateSavedInstanceStateRestore()` mà đích giả Loading chạy lại hay `projects_screen` mất, sau 2 lần sửa (ví dụ code thật của `AppNavHost` khác khung đã thử).
- **Cách làm thay:** `AppNavHost` nhận thêm `navController: NavHostController = rememberNavController()`; test `AppNavHostTest.recreate_keepsProjectsScreen` lấy `navController.saveState()`, dựng lại `AppNavHost` với một `rememberNavController()` mới đã `restoreState(bundle)` trước lần dựng `NavHost`, rồi vẫn kiểm `projects_screen` có và Loading không chạy lại. Câu khẳng định giữ nguyên; chỉ đổi cách dựng lại. Không thêm tệp mới.

### 3.5. Hành vi đã có bị đổi

`scripts/existing-tests.py` (chạy trên bản plan này) đưa ra các test dưới; từng test đã đọc.

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Loading xong thì vào màn mẫu | Loading xong thì vào `ProjectsDestination`; cờ projects tắt thì về `SampleDestination` | `AppLaunchTest.appStarts_andReachesStartScreen` (androidTest, chờ `sample_screen`) | Câu mới: chờ có giới hạn tới khi `onAllNodesWithTag("projects_screen")` có node (staging bật `PROJECTS`). Đúng sáu điều của ADR-0031: spec KB-13 và mục 3.1 nói rõ màn bắt đầu mới, ý định "app tới được màn bắt đầu" giữ nguyên, không nới thời gian chờ |
| Kịch bản T-024 chờ `sample_screen` | Màn bắt đầu là `projects_screen` | `app/src/deviceScenarios/T-024-mo-app-tu-choi-dong-y.txt` dòng 10, 12, 14 | Câu mới: thay `id=sample_screen` bằng `id=projects_screen` ở ba dòng, giữ các lựa chọn khác và thời gian chờ |
| `ScreenName` báo `sample` sau Loading | Báo `projects` | `ScreenNameTest` (chỉ kiểm hàm thuần với tuyến đặt sẵn) | Giữ: hàm không đổi; thêm một câu `screenNameOf(".../ProjectsDestination") == "projects"` vào `AppNavigationBarTest` không cần, vì hàm đã có quy tắc chung |
| `AppNavHost` không có `Scaffold` | Bọc `Scaffold` có thanh đáy khi cờ Bài học bật | `NetworkDialogHostTest`, `NetworkLostScreenScreenshotTest` (dựng `SampleScreen`, không dựng `AppNavHost`) | Giữ: không dựng `AppNavHost` |
| `AppNavHost` báo màn cho popup mạng chỉ khi back stack có mục (`reportsScreen`) | Không đổi: `LaunchedEffect(route)` giữ nguyên, `Scaffold` bọc ngoài `NavHost` | `ScreenNameTest.reportedScreen_nullBeforeFirstEntryAndOnLoading`, `NetworkDialogControllerTest.sameScreenReportedAgain_keepsDialogAndRecordsNothing` (chỉ nhắc `AppNavHost` trong chú thích) | Giữ |
| `MainActivity.onCreate` dựng `AdsHost`, `FullScreenAdHost` | Không đổi thứ tự; chỉ thêm `@Inject FeatureFlags` và tham số `lessonsEnabled` | `FullScreenAdHostAttachTest.recreate_whileComboOpen_turnKept_finishesOnceOnClose`, `NativeFullScreenOverlayTest.recreate_keepsDeadline_notRestarted` (`:core:ads`, chỉ nhắc `MainActivity` trong chú thích) | Giữ |

## 4. Kiểm thử

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| KB-1, trống thì `Empty` | Unit test | `ProjectsViewModelTest.emptyRepository_givesEmpty`; đối chứng: cùng test đặt một dự án rồi kiểm `List`, rồi đặt lại rỗng và kiểm `Empty` (KB-1 "xóa dự án cuối về trống ngay") |
| KB-1, chữ đúng thiết kế | Compose UI test | `ProjectsScreenTest.empty_showsDesignTexts`: có đúng chữ "Make your first animation", lời giới thiệu nguyên văn, "New animation", "Try a free lesson", "Frameloop" |
| KB-1, "New animation" | Compose UI test | `ProjectsScreenTest.newAnimation_callsCallback` (callback có); `ProjectsRouteTest.newAnimation_withoutDestination_showsComingSoon` (callback `null`: snackbar "Coming soon", cách kiểm snackbar ở dưới bảng) |
| KB-1, "Try a free lesson" | Compose UI test | `ProjectsScreenTest.lessonButton_callsCallback`; `ProjectsRouteTest.lesson_withoutDestination_showsComingSoon` |
| KB-1, cờ Bài học tắt | Unit test, Compose UI test | `ProjectsViewModelTest.lessonsFlagOff_hidesLessonButton` và `lessonsFlagTurnsOffWhileShown_hidesLessonButton` (cờ đổi giữa chừng); `ProjectsScreenTest.empty_withoutLessonButton_hasNoLessonNode`; `AppNavigationBarTest.showsNavigationBar_onlyWhenLessonsEnabledOnTopLevelRoute` (bảng: cờ tắt mọi tuyến `false`; cờ bật tuyến Projects `true`, Loading, Sample `false`); `AppNavHostTest.lessonsFlagTurnsOff_barDisappearsAndContentExtends` (`lessonsEnabled` là `mutableStateOf(true)` của test, đổi thành `false` giữa chừng: node `app_navigation_bar` mất, `bottom` của `projects_screen` xuống tới đáy cửa sổ trừ lề thanh hệ thống giả; đối chứng: trước khi đổi, `bottom` bằng `top` của thanh) |
| KB-1, Back đóng app | Robolectric (vỏ app) | `AppNavHostTest.backOnProjects_finishesActivity` (`composeRule.activity.onBackPressedDispatcher.onBackPressed()` rồi `activity.isFinishing`) |
| KB-1, 360 × 640 | Screenshot test, Compose UI test | `ProjectsScreen_empty_small.png` khung `w360dp-h640dp-xxhdpi`; `ProjectsScreenTest.small_emptyButtonsReachable` (`@Config` 360 × 640, `@GraphicsMode(NATIVE)`): cuộn tới `projects_lesson`, nút nằm trọn trong gốc màn, hai nút không chồng nhau (`bottom` nút trên ≤ `top` nút dưới) |
| KB-2, có dự án thì `List` theo đúng thứ tự | Unit test | `ProjectsViewModelTest.projects_keepRepositoryOrder` (ba dự án `updatedAtMillis` lộn xộn theo thứ tự kho; thẻ giữ đúng thứ tự kho) |
| KB-2, thẻ: tên, "48 frames · 12 fps" | Unit test, Compose UI test | `ProjectsViewModelTest.card_hasFrameCountAndFps`; `ProjectsScreenTest.card_showsFramesLine` ("48 frames · 12 fps", và "1 frame · 12 fps" với dự án một khung) |
| KB-2, ảnh thu nhỏ | Unit test | `DefaultProjectsRepositoryTest.thumbnail_readsStoredFile`; `missingThumbnail_updatesOnceThenReads` (đếm `update` = 1); `corruptBytes_returnsNull`; `unreadableProject_doesNotUpdate`; `thumbnail_downsamplesToRequestedWidth` (PNG rộng 512 px, `maxWidthPx = 200` thì ảnh trả về rộng 256 px; đối chứng: `maxWidthPx = 512` thì rộng 512 px); `ProjectsViewModelTest.thumbnail_reloadsWhenUpdatedAtChanges` (hai lần đọc khi `updatedAtMillis` đổi); `thumbnail_twoRequestsSameKey_readOnce` (hai yêu cầu chồng nhau cùng khóa: một lần đọc); `thumbnail_droppedWhenProjectRemoved` (kèm: ViewModel chỉ thu `uiState` thì kho giả đếm đúng 1 người thu `items`) (ảnh của dự án A đã tải; kho giả phát danh sách không còn A: `thumbnails` không còn khóa A; đối chứng: ảnh của dự án B vẫn còn trong danh sách thì giữ); `thumbnail_cacheKeepsAtMost40` (tải ảnh cho 41 thẻ: `thumbnails` có 40 khóa, thẻ đầu tiên bị bỏ; gọi lại `onThumbnailNeeded` cho thẻ đó thì kho đọc lần thứ hai) |
| KB-2, chạm thẻ mở màn vẽ | Unit test | `ProjectsViewModelTest.clickReadable_editorOn_emitsOpenProject` |
| KB-2, cờ màn vẽ tắt | Unit test | `ProjectsViewModelTest.clickReadable_editorOff_emitsComingSoon`; `editorFlagTurnsOff_thenClick_emitsComingSoon` (cờ bật rồi tắt, kiểm ngay lúc chạm) |
| KB-2, màn vẽ chưa có (callback `null`) | Compose UI test | `ProjectsRouteTest.openProject_withoutDestination_showsComingSoon` |
| KB-2, tên dài cắt "…" | Compose UI test | `ProjectsScreenTest.longName_isEllipsizedOnOneLine` (`@GraphicsMode(NATIVE)`): `GetTextLayoutResult` của tên có `lineCount == 1`, `isLineEllipsized(0)`; `bottom` của tên ≤ `top` của dòng số khung |
| KB-2, cuộn, thanh trên giữ chỗ | Compose UI test | `ProjectsScreenTest.scrollGrid_topBarStays`: 20 thẻ, cuộn tới thẻ cuối, `projects_settings` vẫn ở cùng `top` |
| KB-2, dự án hỏng | Unit test, Compose UI test | `ProjectsViewModelTest.unreadable_isShownAndOthersOpen` (thẻ hỏng `readable = false`, thẻ thường vẫn `OpenProject`); `clickUnreadable_emitsCannotOpen`; `unreadable_thumbnailNotRead` (thẻ hỏng: `onThumbnailNeeded` không gọi kho; đối chứng: kho giả có sẵn ảnh cho cả thẻ hỏng lẫn thẻ thường, thẻ thường có ảnh, thẻ hỏng không, số lần đọc của id hỏng là 0); `ProjectsScreenTest.unreadable_showsNameAndEmptyThumbnail` (tên hiện, không có ảnh); `ProjectsScreenTest.unreadable_withoutName_showsPlaceholder` (thẻ hỏng `name = null`: có chữ "Untitled project"; đối chứng: thẻ hỏng có tên "Walk cycle" hiện "Walk cycle", không có "Untitled project"); ảnh `ProjectsScreen_list_unreadable.png` |
| KB-11, nút cài đặt | Unit test, Compose UI test | `ProjectsViewModelTest.settingsFlagOff_hidesSettings` và `settingsFlagTurnsOn_showsSettings`; `ProjectsScreenTest.settings_callsCallback`; `ProjectsRouteTest.settings_withoutDestination_showsComingSoon` |
| KB-11, giữ chỗ cuộn khi quay lại | Compose UI test | `ProjectsScreenTest.scrollPosition_survivesRestore`: `StateRestorationTester`, 20 thẻ, cuộn tới thẻ thứ 15, `emulateSavedInstanceStateRestore()`, `gridState.firstVisibleItemIndex` không đổi; đối chứng: trước khi cuộn chỉ số là 0. Lối quay lại thật từ Cài đặt kiểm ở T-016 |
| KB-11, Tạo dự án chọn sẵn theo Cài đặt; nút quyền riêng tư khi cờ Cài đặt tắt | Không thuộc T-009 | Thuộc T-011 (giá trị chọn sẵn) và T-056 (nút quyền riêng tư) |
| KB-1, đã làm bài học mà chưa tạo dự án | Unit test | Kho giả phát danh sách rỗng: `ProjectsViewModelTest.emptyRepository_givesEmpty`. Việc bỏ dự án kiểu bài học khỏi `ProjectRepository.projects` đã có test của T-002 (`:core:project`), plan không đổi |
| KB-1, mất mạng | Không thuộc feature | Popup chung của `:app` (ADR-0024); `ScreenName` tự ra `projects` theo quy tắc đã có test (`ScreenNameTest`); feature không thêm gì |
| KB-13, cờ projects tắt | Unit test, Compose UI test, Robolectric (vỏ app) | `ProjectsViewModelTest.projectsFlagOff_givesDisabled`; `projectsFlagTurnsOffWhileListed_givesDisabled` (đang `List` thì cờ tắt chuyển `Disabled`); `ProjectsRouteTest.disabled_callsOnFeatureDisabledOnce` (hai lần đổi trạng thái `Disabled` vẫn một lần gọi); `AppNavHostTest.projectsFlagOff_opensSample`; `AppNavHostTest.projectsFlagOnAfterDisabled_staysOnSample` (đích giả Projects gọi `onFeatureDisabled`, có `sample_screen`; đổi biến cờ giả của test về bật và `waitForIdle()`: vẫn `sample_screen`, không có `projects_screen`; khóa hành vi của câu hỏi 7, người duyệt chọn trở về danh sách thì câu khẳng định đổi theo trước khi duyệt) |
| Vỏ app: dựng lại activity | Robolectric (vỏ app) | `AppNavHostTest.recreate_keepsProjectsScreen` (`StateRestorationTester(composeRule)`, `emulateSavedInstanceStateRestore()`, rồi vẫn `projects_screen`, biến đếm Loading vẫn 1) |
| Thanh điều hướng | Compose UI test, Screenshot test | `AppNavHostTest.lessonsEnabled_showsBarOnProjects` (bật: có node `app_navigation_bar` ở Projects; tắt: không có; Sample: không có); `AppNavHostTest.lessonsTab_showsComingSoon` (chạm "Lessons": snackbar "Coming soon"); `AppNavHostTest.barShown_contentEndsAtBarTop` (`@GraphicsMode(NATIVE)`, sau `setContent` gọi `dispatchNavigationBarInsets(composeRule, TEST_NAVIGATION_BAR_PX)`: có thanh thì `bottom` của `projects_screen` bằng `top` của `app_navigation_bar` và `bottom` của thanh bằng đáy cửa sổ, lệch tối đa 1 px; đối chứng không có thanh: `bottom` của `projects_screen` bằng đáy cửa sổ trừ `TEST_NAVIGATION_BAR_PX`, lệch tối đa 1 px. Không gửi lề giả thì Robolectric không có lề thanh hệ thống và câu "không cộng lề hai lần" không thể thất bại); `AppNavigationBar_light.png`, `AppNavigationBar_dark.png` (khung `w411dp-h891dp-xxhdpi`, chụp `onRoot()` của thanh; ảnh tối bọc `AppTheme(darkTheme = true)`, không dựa vào chế độ tối của máy test) |
| Giao diện màn | Screenshot test | `ProjectsScreen_empty_light.png`, `ProjectsScreen_empty_dark.png`, `ProjectsScreen_list_light.png`, `ProjectsScreen_list_dark.png`, khung `w411dp-h891dp-xxhdpi`, bốn dự án của bảng chữ thiết kế; ảnh thu nhỏ giả màu đặc; ảnh `_dark` bọc `AppTheme(darkTheme = true)`, ảnh `_light` bọc `AppTheme(darkTheme = false)` |
| Màn hình thấp | Screenshot test khung `w360dp-h640dp-xxhdpi`, trạng thái nhiều nội dung nhất | `ProjectsScreen_list_small.png` (8 dự án, một tên dài), `ProjectsScreen_empty_small.png` |
| Đúng thiết kế (khi có `design/screens/`) | Screenshot test cùng tên với từng ảnh thiết kế, ảnh so sánh `scripts/design-compare.sh projects`, rà soát độc lập theo mục 7 của mẫu thiết kế. Ảnh của T-009 không có thanh điều hướng (ở `:app`) và không có quảng cáo (T-010): phần đó lệch so với thiết kế, ghi trong báo cáo (câu hỏi 1) | `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark`, `ProjectsScreen_list_light`, `ProjectsScreen_list_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-009 | Ảnh thiết kế của ticket khác (T-011): `NewProjectScreen_default_light`, `NewProjectScreen_default_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-009 | Ảnh thiết kế của ticket khác (T-012): `ProjectsScreen_menu_light`, `ProjectsScreen_menu_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-009 | Ảnh thiết kế của ticket khác (T-013): `ProjectsScreen_restoring_light`, `ProjectsScreen_restoring_dark`, `ProjectsScreen_restore-error_light`, `ProjectsScreen_restore-error_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-009 | Ảnh thiết kế của ticket khác (T-014): `ProjectsScreen_list_light_unfolded`, `ProjectsScreen_list_dark_unfolded` |
| Chữ đúng bảng chữ của thiết kế | Compose UI test | `ProjectsScreenTest.empty_showsDesignTexts`, `list_showsDesignTexts` ("Your projects", "Recent", "New animation", bốn tên và bốn dòng số khung); screenshot cùng tên ảnh thiết kế đặt `@Config(qualifiers = "en-w411dp-h891dp-xxhdpi")` (chữ thiết kế tiếng Anh) |
| Bản tiếng Việt | Compose UI test | `ProjectsScreenTest.vietnamese_framesLineKeepsNumbers` (`@Config(qualifiers = "vi")`): dòng phụ có "48" và "12" đúng chỗ khung và tốc độ, không đảo (câu hỏi 4) |
| Màn bắt đầu, Back trên máy | Kiểm tra thủ công (emulator: `app/src/deviceScenarios/T-009-danh-sach-du-an.txt`) | Mở app, chờ `projects_screen`, thấy màn trống và nút "New animation", Back thì `projects_screen` không còn |
| 100 dự án cuộn không giật | Kiểm tra thủ công (máy thật: FPS, khung hình) | Dev tạo 100 dự án (sau T-011, hoặc khôi phục file nhiều dự án sau T-013), cuộn lưới trên máy tầm trung, xem không giật |

Cách kiểm snackbar "Coming soon" và "Can't open this project." (`ProjectsRouteTest.*_showsComingSoon`, `AppNavHostTest.lessonsTab_showsComingSoon`): snackbar tự ẩn sau vài giây, nên test không chờ để đồng hồ tự chạy. Test đặt `composeRule.mainClock.autoAdvance = false`, chạm nút, `mainClock.advanceTimeBy(500)`, rồi kiểm node có chữ "Coming soon"; đối chứng: trước khi chạm không có node đó. Cách thay khi cây ngữ nghĩa chưa có chữ: truyền `SnackbarHostState` của test vào màn và kiểm `snackbarHostState.currentSnackbarData?.visuals?.message`.

Nội dung tệp `app/src/deviceScenarios/T-009-danh-sach-du-an.txt` (đã chạy `scripts/device-scenario.py check` trên bản tạm: chỉ báo các tag `projects_screen`, `projects_empty`, `projects_new` chưa có trong mã nguồn, đúng vì code chưa viết; không lỗi cú pháp). `fresh` xóa dữ liệu nên luôn ra màn trống; không khẳng định snackbar tự ẩn (dễ chập chờn); "app đóng" kiểm bằng `back` rồi `expect_no`:

```
ticket: T-009
criterion: Mở app thì vào danh sách dự án trống (staging bật cờ projects); Back ở danh sách đóng app
timeout: 180
# Same entry as T-024: the first-open app open ad may show over loading, and loading may stop with a retry button.
fresh
consent decline 30
wait id=projects_screen | text="Continue to app" | desc="Continue to app" | id=loading_retry 60
tap_if id=loading_retry
wait id=projects_screen | text="Continue to app" | desc="Continue to app" 60
tap_if text="Continue to app" | desc="Continue to app"
wait id=projects_screen 30
expect id=projects_empty
expect id=projects_new
shot projects_empty
back
expect_no id=projects_screen
```

**Ngoại lệ viết test trước:** `settings.gradle.kts`, `feature/projects/build.gradle.kts`, `consumer-rules.pro`, `app/build.gradle.kts`, `di/ProjectsModule.kt`, các vector drawable và `strings.xml`: phần nối, cấu hình và tài nguyên, không có hành vi riêng ngoài phần các test trên đã kiểm. Tệp kịch bản `T-009-danh-sach-du-an.txt` và ba dòng sửa của `T-024-mo-app-tu-choi-dong-y.txt` chạy ở `device-session` cuối phiên, không "thất bại trước" được. `AppLaunchTest` chỉ chạy sau merge (ADR-0043).

Thứ tự commit: bước 3 commit các tệp test, `FakeProjectsRepository.kt`, `MainDispatcherRule.kt`, câu mới của `AppLaunchTest.kt`, cùng khung rỗng tối thiểu để test biên dịch được (module, `settings.gradle.kts`, `build.gradle.kts`, các lớp trạng thái, `ProjectsRepository` interface, chữ ký `ProjectsScreen`/`ProjectsRoute`/`AppNavigationBar`/`showsNavigationBar` trả giá trị rỗng); bước 4 commit phần code. Ảnh chuẩn ghi ở bước 4 bằng `./gradlew :feature:projects:recordRoborazziDebug :feature:projects:testDebugUnitTest --rerun` và `./gradlew :app:recordRoborazziStagingDebug :app:testStagingDebugUnitTest --rerun`, rồi `git status --short -- '*/src/test/screenshots/*'` chỉ được thấy các ảnh của mục 3.2; ảnh khác đổi thì trả bằng `git checkout -- <ảnh>`.

### 4.1. Chạy thử

- **Đã chạy thử:** ngày 2026-11-23, trên worktree tạm `git worktree add --detach ../draw-animation-trial-T-009 origin/main` (commit `df17154`, đã có code T-001 tới T-007, T-044, T-048), với môi trường `source ~/.local/share/draw-animation/env.sh; export ANDROID_HOME=${ANDROID_HOME:-$HOME/Library/Android/sdk}`. Khung tối thiểu: dòng `include(":feature:projects")` trong `settings.gradle.kts`; `feature/projects/build.gradle.kts` (`template.android.feature`, `namespace`, `implementation(project(":core:project"))`); `consumer-rules.pro` chép từ `:feature:sample`; `ProjectsModule` (`@Binds`); `DefaultProjectsRepository @Inject` tiêm `ProjectRepository`, `ProjectStore`, `ProjectThumbnails`; `ProjectsViewModel` (`@HiltViewModel`) tiêm `ProjectsRepository`; `ProjectsRoute` gọi `hiltViewModel()`; `projectsScreen()` trong `AppNavHost` (Loading xong vào `ProjectsDestination`), `AppScreens` như mục 3.1; `:app` thêm `implementation(project(":feature:projects"))`. Đây là lần đầu các Hilt module `ProjectModule`, `ProjectRepositoryModule`, `RenderModule`, `BackupModule` và code kotlinx-serialization của `:core:project` vào đồ thị Hilt và R8 của `:app`. Ba test tạm, không commit: `AppNavHostTrialTest.recreate_keepsProjectsScreen` (dựng `AppNavHost` thật với `AppScreens` giả, `StateRestorationTester`), `AppNavHostTrialTest.backOnProjects_finishesActivity`, `InsetsTrialTest.bar_thenNoBar` (`Scaffold(contentWindowInsets = WindowInsets(0))`, nội dung `padding(inner).consumeWindowInsets(inner).safeDrawingPadding()`, lề thanh điều hướng giả 72 px ở khung `w411dp-h891dp-xxhdpi`, `@GraphicsMode(NATIVE)`), cùng một test tạm trong `:feature:projects`.
- **Lệnh:** `./gradlew ktlintFormat`, rồi `scripts/check.sh`, rồi `./gradlew :app:assembleStagingRelease`.
- **Kết quả:**
  - `AppNavHostTrialTest`: 2 test qua. Sau dựng lại vẫn có `projects_screen`, biến đếm Loading vẫn 1; Back ở danh sách thì `activity.isFinishing`.
  - `InsetsTrialTest`: qua. Có thanh: đáy gốc 2673 px, đáy nội dung 2361 px bằng đỉnh thanh 2361 px, đáy thanh 2673 px (thanh cao 312 px = 80 dp cộng 72 px lề). Bỏ thanh giữa chừng: đáy nội dung 2601 px = 2673 trừ 72. Lề không bị cộng hai lần, và câu đối chứng có số đo thật.
  - `./gradlew :app:assembleStagingRelease`: `BUILD SUCCESSFUL`, `minifyStagingReleaseWithR8` chạy; không cần luật R8 mới (`kotlinx.serialization` có sẵn luật cho lớp `@Serializable`, Hilt tự sinh luật). Hai cảnh báo R8 về AppsFlyer, TikTok là cảnh báo đã có trên nhánh chính.
  - `scripts/check.sh` lần đầu dừng ở `:feature:projects:testDebugUnitTest`: "There are test sources present ... did not discover any tests" vì module chỉ có `robolectric.properties` mà chưa có test. Thêm một test tạm rồi chạy lại: `== Cổng chất lượng: QUA` (2773 test JVM, 0 lỗi; `:app` 176 test gồm ba test tạm, `:feature:projects` 1 test; 99 ảnh screenshot). Bước 3 của phiên commit các test của mục 4 cùng `robolectric.properties`, nên không gặp lỗi này; không commit `robolectric.properties` riêng trước test.
  - `scripts/device-scenario.py check` đã chạy trên tệp kịch bản tạm ngày 2026-11-22 (mục 4). Bộ icon lõi (bản 1.7.8, kéo theo bởi `material3` 1.3.2) có trên classpath biên dịch của feature (`./gradlew -q :feature:sample:dependencies --configuration debugCompileClasspath`, ngày 2026-11-22); ba SVG Material Symbols tải được (HTTP 200).

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** Không có. Không chạm module core (chỉ thêm phụ thuộc vào `:core:project` có sẵn), không quảng cáo, không sự kiện, không quyền, không thư viện mới. Dòng `include` của `settings.gradle.kts` và `feature/projects/build.gradle.kts` mới: AI không coi là "cấu hình build" của điểm chặn 7 vì chỉ áp dụng convention plugin có sẵn, nhưng người duyệt quyết (câu hỏi 9).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 3 | Module feature mới cộng sửa vỏ `:app` (màn bắt đầu, `Scaffold`, thanh điều hướng, `MainActivity`), test androidTest và kịch bản có sẵn |
| B. Độ rõ của yêu cầu | 1 | Spec đã xác nhận, có thiết kế và số liệu; vài điểm AI tự quyết (chữ báo dự án hỏng, nút nhập file trước T-013, vị trí thanh điều hướng trong ảnh) nêu ở mục 8 |
| C. Khả năng kiểm chứng tự động | 1 | Mọi hành vi có test JVM; chỉ độ mượt khi cuộn 100 dự án cần máy thật, màn bắt đầu trên emulator chỉ kiểm sau merge |
| D. Ảnh hưởng chéo | 3 | Đổi màn bắt đầu của app (mọi luồng đi qua); `AppNavHost` cùng sửa với T-010, T-011, T-012, T-013, T-014, T-056 và các ticket màn hình khác; T-010 đến T-014 dựa vào cấu trúc màn này |
| E. Độ mới của kỹ thuật | 1 | Theo mẫu `:feature:sample`; mới một phần: dựng lại `NavHost` bằng `StateRestorationTester` (có dự phòng ở mục 3.4), vector drawable từ Material Symbols |
| **Tổng** | 9 | **Mức: Cao** (tổng từ 9; cần `cross_reviewer`) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `settings.gradle.kts` (một dòng), `app/build.gradle.kts`, `AppNavHost.kt`, `MainActivity.kt`, `app/src/main/res/values*/strings.xml`, `AppLaunchTest.kt`, `T-024-mo-app-tu-choi-dong-y.txt`. Không chạm `:core:*`.
- Task khác trong sprint có thể xung đột:
  - T-010, T-011, T-012, T-013, T-014, T-056 (feature projects) đều dựa vào `:feature:projects` và `ProjectsScreen` của T-009, và sửa lại các file của plan này: chạy sau T-009, tuần tự. T-010, T-012, T-014, T-056 cùng sửa `ProjectsScreen.kt` và `ProjectsViewModel.kt`.
  - T-012 (menu dự án) cùng đợt 20261123-02: chạy ngay sau T-009 trong cùng danh sách. T-012 dùng lại chuỗi `projects_card_unnamed` "Untitled project" của plan này cho đầu menu và hộp xác nhận xóa của thẻ hỏng không có tên, không tạo chuỗi mới; T-009 đổi tên chuỗi, lớp hay tag mà plan T-012 dẫn thì phải báo plan T-012.
  - T-007 (vị trí quảng cáo) đã merge (`ad0b766`), nên T-010 chạy được ngay sau T-009. Trong đợt này T-010 xếp sau T-012 (đợt sau), vì cùng sửa `ProjectsScreen.kt`, `ProjectsViewModel.kt`. Plan T-009 không thêm test vào `:core:ads`, nên không làm nặng thêm bộ nhớ của test `:core:ads` trên CI (bài học L-004).
  - T-014 (máy gập): thanh điều hướng thành cột dọc bên trái khi mở máy, nên plan T-014 phải có `app/src/main/kotlin/com/vgl/draw/animation/navigation/AppNavigationBar.kt` và `AppNavHost.kt` trong mục 3.2, không chỉ các tệp của `:feature:projects`.
  - Ticket màn hình khác sửa `AppNavHost.kt` (onboarding, bài học, cài đặt, màn vẽ): danh sách chạy xếp tuần tự (run-check.py báo cùng sửa file). Ticket onboarding đổi đích sau Loading: thống nhất rằng đích sau onboarding là `ProjectsDestination` của plan này.
  - `T-024-mo-app-tu-choi-dong-y.txt`: plan nào khác cũng sửa kịch bản này thì xếp sau T-009.
  - T-018 (màn vẽ): để KB-2 "từ màn vẽ quay về, ảnh thu nhỏ đúng" đạt, màn vẽ phải ghi ảnh thu nhỏ (`ProjectThumbnails.update`) trước khi lưu `project.json` lần cuối, vì danh sách tải lại ảnh theo `updatedAtMillis`; ghi ảnh sau lần lưu thì danh sách giữ ảnh cũ.
- Đề xuất thứ tự chạy: sau T-002, T-004, T-006, T-007 (đã merge). Trong đợt 20261123-02: T-009 rồi T-012. Sau đó T-010, T-011, T-013, T-014, T-056, tuần tự.

## 7. Điều kiện dừng riêng

- Cần đổi `ProjectRepository`, `ProjectStore` hay `ProjectThumbnails` của `:core:project` để làm màn: dừng (sẽ chạm điểm chặn 7, plan chưa nêu).
- Màn mẫu (`:feature:sample`) không còn mở được khi cờ projects tắt: dừng, không bỏ đường về màn mẫu (spec KB-13).

## 8. Câu hỏi cho người duyệt

1. **Ảnh cùng tên thiết kế không có thanh điều hướng và quảng cáo.** Thanh điều hướng nằm ở `:app` (theo ticket) nên ảnh của `:feature:projects` không có thanh; quảng cáo là T-010. `scripts/design-compare.sh projects` sẽ thấy lệch ở đáy ảnh. Đề xuất: chấp nhận lệch này ở T-009, rà soát độc lập chỉ so phần trên thanh; thanh có ảnh riêng ở `:app` (`AppNavigationBar_*`). Phương án khác: (a) đưa `AppNavigationBar` vào `:core:designsystem` để ảnh của feature vẽ cả thanh, nhưng chạm điểm chặn 7; (b) `ProjectsScreen` nhận slot thanh đáy, screenshot test truyền thanh giả như `bottomAd` của `:feature:sample` (ảnh gần thiết kế hơn, nhưng thanh trong ảnh không phải thanh thật).
2. **Nút nhập file trên thanh trên trước T-013.** Thiết kế có nút `upload_file` ở cả hai trạng thái. Đề xuất: hiện nút theo thiết kế, chạm thì "Coming soon" tới T-013. Phương án khác: ẩn nút tới T-013 (ảnh lệch thiết kế).
3. **Chữ của thẻ dự án hỏng** (KB-2, spec không ghi chữ). Đề xuất: chạm thẻ thì thông báo ngắn "Can't open this project." tự ẩn, không nút; thẻ không đọc được tên thì ghi "Untitled project" (`projects_card_unnamed`, bản Việt "Dự án chưa có tên"; T-012 dùng lại chuỗi này).
4. **Bản tiếng Việt.** Spec theo chữ tiếng Anh (bản đề xuất Q9); quy ước của dự án có `values-vi/`. Đề xuất: thêm `values-vi/strings.xml` như `:feature:loading` ("Tạo hoạt hình đầu tiên", "Hoạt hình mới", "Thử một bài học miễn phí", "%1$d khung · %2$d fps"), anh xác nhận chữ. Phương án khác: chỉ tiếng Anh.
5. **Giữ chỗ cuộn khi từ Cài đặt quay lại** (KB-11): T-009 kiểm việc lưu và dựng lại vị trí cuộn bằng `StateRestorationTester` (mục 4); lối quay lại thật chỉ kiểm được khi có màn Cài đặt. Đề xuất: T-016 thêm test quay lại.
6. **Test vỏ app dựng `AppNavHost` với đích giả** qua tham số `screens: AppScreens` (mục 3.1), không dựng `MainActivity` và không dùng Hilt (bản trước của plan dùng `@HiltAndroidTest`, cần thư viện `hilt-android-testing` chưa có). Phần `MainActivity` truyền `lessonsEnabled` không có test JVM (chỉ một dòng nối). Anh đồng ý điểm nối này?
7. **Cờ projects bật muộn ở lần mở đầu trên production** (KB-13, mục 3.1). Remote Config lỗi hay hết 10 giây chờ ở lần mở đầu thì cờ `PROJECTS` còn `false`, app về màn mẫu một lần; cờ bật sau đó thì người dùng vẫn ở màn mẫu tới lần mở app sau. Đề xuất: chấp nhận, theo câu trả lời câu hỏi 18 của spec (cờ chỉ để giấu feature trong lúc làm, production bật từ lần phát hành đầu); test `AppNavHostTest.projectsFlagOnAfterDisabled_staysOnSample` khóa hành vi này. Phương án khác: màn mẫu do `:app` theo dõi cờ, cờ bật thì về danh sách (`navigate(ProjectsDestination) { popUpTo<SampleDestination> { inclusive = true } }`), test đổi thành `projectsFlagOnAfterDisabled_returnsToProjects`; người đang dùng màn mẫu bị chuyển màn đột ngột.
8. **Màn bắt đầu trên staging đổi ngay khi merge.** Staging bật sẵn `PROJECTS`, nên sau merge mọi người thử staging vào danh sách dự án (chưa tạo được dự án tới T-011) thay cho màn mẫu. Đề xuất: chấp nhận; màn mẫu vẫn vào được bằng cách tắt cờ trong test.
9. **Dòng `include` và `build.gradle.kts` của module feature mới có tính là "cấu hình build" của điểm chặn 7 không?** AI chấm không chạm điểm chặn (mục 5), nhưng mức đã là Cao vì tổng 9, nên dù quyết thế nào plan vẫn cần duyệt chéo.
