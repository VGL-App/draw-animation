---
ticket: T-009
title: Màn danh sách dự án, thanh điều hướng của vỏ app, màn bắt đầu
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score: 8                # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: medium           # low | medium | high
token_estimate: 15.2M        # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: []          # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-009: MÀN DANH SÁCH DỰ ÁN, THANH ĐIỀU HƯỚNG CỦA VỎ APP, MÀN BẮT ĐẦU

## 1. Tóm tắt

Tạo module `:feature:projects` theo mẫu `:feature:sample` với `ProjectsScreen` hai trạng thái `empty` và `list` (lưới hai cột, thẻ có ảnh thu nhỏ đọc từ `:core:project`), đặt làm màn sau màn Loading khi cờ `feature_projects_enabled` bật, còn cờ tắt thì về màn mẫu như hôm nay. Thanh điều hướng Projects/Lessons là composable của `:app` (`AppNavigationBar`), chỉ hiện khi cờ Bài học bật. Mở dự án, Bài học, Cài đặt, Tạo dự án và nhập file là callback; đích chưa có (T-011, T-013, T-016, T-018, T-050) thì màn hiện "Coming soon". Kiểm chứng bằng unit test ViewModel, test giao diện Robolectric, screenshot test cùng tên ảnh thiết kế (không có quảng cáo) và test dựng vỏ app.

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
| KB-2 (đặc biệt) | Dự án hỏng: dự án khác vẫn hiện và mở được; thẻ hỏng có tên (nếu đọc được), ảnh trống; chạm thì báo không mở được; không tự xóa | `ProjectListItem.Unreadable` thành thẻ `readable = false`; chạm hiện thông báo "Can't open this project." (câu hỏi 3). Menu chỉ còn "Delete" là T-012 |
| KB-11 | Bấm nút cài đặt mở Cài đặt | Callback `onOpenSettings`; chưa có màn Cài đặt (T-016): "Coming soon" |
| KB-11 | Cờ Cài đặt tắt: không có nút cài đặt | ViewModel theo dõi `FeatureFlag.SETTINGS`. Nút quyền riêng tư quảng cáo thay chỗ là T-056, không thuộc T-009. Giá trị chọn sẵn ở Tạo dự án là T-011 |
| KB-11 | Từ Cài đặt quay lại đúng chỗ cuộn | `rememberLazyGridState` trong `NavBackStackEntry` giữ vị trí; kiểm đầy đủ sau T-016 (câu hỏi 5) |
| KB-13 | Cờ projects tắt: vào màn mẫu, không vào được danh sách | ViewModel theo dõi `FeatureFlag.PROJECTS`; `false` thì `ProjectsUiState.Disabled`, `ProjectsRoute` gọi `onFeatureDisabled` một lần, `:app` thay đích bằng `SampleDestination` (`popUpTo<ProjectsDestination> { inclusive = true }`) |

Thiết kế tham chiếu: `features/projects/design/README.md`, các ảnh trong `design/screens/`: `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark`, `ProjectsScreen_list_light`, `ProjectsScreen_list_dark`. Các ảnh khác của feature thuộc ticket khác (mục 4).

ADR liên quan: ADR-0001 (phụ thuộc module), ADR-0003 (cờ là `Flow`), ADR-0008 (hệ thống thiết kế), ADR-0019 (log), ADR-0020 (screenshot theo thiết kế), ADR-0024 (mạng), ADR-0031 (viết lại câu khẳng định cũ), ADR-0043 (test emulator chỉ sau merge), ADR-0045 (quảng cáo: không chạm).

## 3. Giải pháp

### 3.1. Cách làm

**Module.** `:feature:projects`, gói `com.vgl.draw.animation.feature.projects`, `build.gradle.kts` chỉ áp dụng `template.android.feature`, `namespace`, cộng `implementation(project(":core:project"))` (core, đúng ADR-0001). `settings.gradle.kts` thêm `include(":feature:projects")` sau `include(":feature:export")`. `:app` thêm `implementation(project(":feature:projects"))`.

**Dữ liệu.** `data/ProjectsRepository.kt` (interface, cùng gói `data`) gồm:

- `val items: Flow<List<ProjectListItem>>` lấy thẳng `ProjectRepository.projects`.
- `suspend fun thumbnail(id: ProjectId, maxWidthPx: Int): ImageBitmap?`: đọc `ProjectStore.readFile(id, ProjectFile.Thumbnail)`; `null` và dự án đọc được thì `ProjectThumbnails.update(id)` một lần rồi đọc lại; giải mã bằng `BitmapFactory` với `inSampleSize` lớn nhất mà bề rộng ảnh vẫn ≥ `maxWidthPx`; mọi `IOException` hay giải mã hỏng trả `null` (thẻ vẽ ô trống màu `canvas`). Chạy trên `Dispatchers.IO` (tiêm `CoroutineDispatcher` để test).

`data/DefaultProjectsRepository.kt` là lớp `internal`, gắn bằng `di/ProjectsModule.kt` (`@Binds`).

**Trạng thái.** `ProjectsUiState` (sealed interface): `Loading` (khung đầu, chưa có dữ liệu hay cờ), `Disabled`, `Empty(showLessonButton, showSettingsButton)`, `List(cards: List<ProjectCardUi>, showSettingsButton)`. `ProjectCardUi(id, name: String?, frameCount, fps, updatedAtMillis, readable)`. Thanh trên (wordmark, nút nhập file, nút cài đặt) có ở cả `Empty` và `List`.

**ViewModel.** `ProjectsViewModel(repository, featureFlags, analytics, logger)`: `combine` của `items` với bốn cờ `PROJECTS`, `LESSONS`, `SETTINGS`, `EDITOR` (theo dõi, không đọc một lần), `stateIn(WhileSubscribed(5_000), Loading)`. Không ghi sự kiện phân tích nào (spec mục 6 không có sự kiện cho màn danh sách); `Analytics` nhận theo mẫu nhưng không gọi. Hành động: `onProjectClick(card)` trả về một `ProjectsEffect` qua `SharedFlow` một lần: `OpenProject(id)` khi `readable` và cờ `EDITOR` bật, `ShowCannotOpen` khi thẻ hỏng, `ShowComingSoon` khi cờ `EDITOR` tắt. Ảnh thu nhỏ: `thumbnails: StateFlow<Map<ProjectId, ImageBitmap?>>` và `onThumbnailNeeded(id, updatedAtMillis, maxWidthPx)`; khóa bộ nhớ là `(id, updatedAtMillis)`, nên dự án vừa sửa tải ảnh mới; hai lần gọi cùng khóa đang tải thì chỉ một lần đọc (giữ một `Map` của `Job` đang chạy). Ghi log (thẻ `Projects`): trạng thái chính đổi (`Empty`/`List` kèm số thẻ, `Disabled`), chạm thẻ hỏng, ảnh thu nhỏ đọc lỗi (không ghi tên dự án hay đường dẫn).

**Màn hình.** `ProjectsScreen.kt`: `ProjectsRoute(onNewProject: (() -> Unit)?, onOpenProject: ((ProjectId) -> Unit)?, onOpenLessons: (() -> Unit)?, onOpenSettings: (() -> Unit)?, onImport: (() -> Unit)?, onFeatureDisabled: () -> Unit)`; callback `null` nghĩa là đích chưa có, màn hiện "Coming soon" bằng `SnackbarHost` của chính màn (thông báo ngắn, tự ẩn). `ProjectsScreen(uiState, thumbnails, snackbarHostState, các callback)` không trạng thái. Thành phần: `ProjectsTopBar` (chấm `primary` 14 dp, "Frameloop" `headlineSmall`, `IconButton` 48 dp `upload_file`, `settings`), `EmptyContent` (hình minh họa, tiêu đề, lời, `PrimaryButton` "New animation" có icon `add`, `FilledTonalButton` "Try a free lesson" có icon `school`, truyền `colors` rõ), `ProjectGrid` và `ProjectCard` (nền `surfaceContainer`, bo 14, lề 8/8/12/8, ảnh thu nhỏ tỷ lệ 168 × 94 bo 8 nền `canvas`, tên `titleSmall`, dòng phụ `bodySmall` `onSurfaceVariant`). Số liệu lấy từ `design/context/ProjectsScreen_empty_*.md` và `ProjectsScreen_list_*.md`; khoảng cách ngoài hệ thống (12, 20) làm tròn về `Spacing` gần nhất chỉ khi `Spacing` có giá trị đó, không thì giữ số của thiết kế trong một hằng riêng của file (như các feature đã làm). Màu, chữ lấy từ `MaterialTheme`/`AppTheme`. `testTag`: `projects_screen` (gốc), `projects_empty`, `projects_grid`, `project_card_<id>`, `projects_new`, `projects_lesson`, `projects_settings`, `projects_import`.

**Icon.** Bộ icon lõi đi kèm `material3` 1.3.2 (có sẵn trên classpath biên dịch của feature, đã xem bằng `./gradlew :feature:sample:dependencies`) có `Icons.Rounded.Add`, `Icons.Rounded.Settings`, không có `upload_file`, `school`, `animation`. Ba icon này làm thành vector drawable trong `res/drawable/` từ SVG Material Symbols Rounded (giấy phép Apache 2.0), tải tại `https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/<tên>/default/24px.svg` (đã thử, trả 200): `viewportWidth/Height = 960`, một `<group android:translateY="960">` bọc `pathData` của SVG. Không thêm `material-icons-extended` (thư viện mới, sửa `libs.versions.toml`).

**Điều hướng.** `navigation/ProjectsNavigation.kt`: `@Serializable data object ProjectsDestination`, `NavGraphBuilder.projectsScreen(...)` cùng tham số như `ProjectsRoute`. `AppNavHost`: Loading xong thì `navigate(ProjectsDestination) { popUpTo<LoadingDestination> { inclusive = true } }`; `onFeatureDisabled` thì `navigate(SampleDestination) { popUpTo<ProjectsDestination> { inclusive = true } }`, chạy trên luồng chính vì gọi từ `LaunchedEffect` của Route. T-009 truyền `null` cho mọi callback mở đích khác.

**Thanh điều hướng của vỏ app.** `app/.../navigation/AppNavigationBar.kt`: `AppNavigationBar(selected: TopLevelTab, onSelect: (TopLevelTab) -> Unit)` với `NavigationBar` hai `NavigationBarItem` "Projects" (icon `animation`) và "Lessons" (icon `school`), chỉ báo `primaryContainer`; `enum class TopLevelTab { Projects, Lessons }`. `AppNavHost` nhận thêm `lessonsEnabled: Boolean` và bọc `NavHost` trong `Scaffold(bottomBar = { if (lessonsEnabled && route là đích cấp cao) AppNavigationBar(...) })`; chạm "Lessons" khi chưa có đích Bài học (T-050) hiện "Coming soon" bằng `SnackbarHost` của `Scaffold`. `MainActivity` tiêm `FeatureFlags` và truyền `featureFlags.enabled(FeatureFlag.LESSONS).collectAsState(false)`. Hàm thuần `showsNavigationBar(route: String?, lessonsEnabled: Boolean)` trong cùng file để test. Bố cục cột bên trái của máy gập là T-014.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `settings.gradle.kts` | Thêm `include(":feature:projects")` sau `include(":feature:export")` |
| Tạo | `feature/projects/build.gradle.kts` | Plugin `template.android.feature`, `namespace = "com.vgl.draw.animation.feature.projects"`, `implementation(project(":core:project"))` |
| Tạo | `feature/projects/consumer-rules.pro` | Tệp rỗng như `:feature:sample` (luật R8 thêm vào đây nếu `assembleStagingRelease` đòi) |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsUiState.kt` | `ProjectsUiState`, `ProjectCardUi`, `ProjectsEffect` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsViewModel.kt` | Mục 3.1, ViewModel |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreen.kt` | `ProjectsRoute`, `ProjectsScreen`, thanh trên, nội dung trống, `ProjectsTestTags` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectCard.kt` | Thẻ dự án, lưới, ô ảnh thu nhỏ |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/EmptyIllustration.kt` | Hình minh họa màn trống vẽ bằng Compose |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/ProjectsRepository.kt` | Interface |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultProjectsRepository.kt` | Lớp `internal`: danh sách, ảnh thu nhỏ |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/di/ProjectsModule.kt` | `@Binds` repository, `@Provides` dispatcher IO có qualifier |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/navigation/ProjectsNavigation.kt` | `ProjectsDestination`, `projectsScreen(...)` |
| Tạo | `feature/projects/src/main/res/values/strings.xml` | Chữ của bảng chữ thiết kế, "Coming soon", "Can't open this project.", `plurals` `projects_card_frames`, mô tả icon cho trợ năng |
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
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultProjectsRepositoryTest.kt` | Ảnh thu nhỏ: có tệp, chưa có tệp (gọi `update` một lần), tệp hỏng, dự án hỏng; `ProjectStore` và `ProjectThumbnails` giả |
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
| Tạo | `app/src/testStaging/kotlin/com/vgl/draw/animation/navigation/AppNavHostTest.kt` | Test Robolectric dựng `MainActivity` (Hilt, `@HiltAndroidTest`) với cờ giả: cờ projects bật thì sau Loading có `projects_screen`; tắt thì `sample_screen`; Back ở danh sách kết thúc activity; `recreate()` giữ đích (câu hỏi 6) |
| Sửa | `app/src/androidTest/kotlin/com/vgl/draw/animation/AppLaunchTest.kt` | Màn bắt đầu của staging thành `projects_screen` (mục 3.5) |
| Sửa | `app/src/deviceScenarios/T-024-mo-app-tu-choi-dong-y.txt` | Ba dòng `wait id=sample_screen` thành `id=projects_screen` (mục 3.5) |
| Tạo | `app/src/deviceScenarios/T-009-danh-sach-du-an.txt` | Kịch bản emulator: mở app, thấy màn trống hay lưới, chạm "New animation" thấy "Coming soon", Back đóng app |

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
- **Thư viện và SDK:** Không thêm. Dùng `Icons.Rounded.Add`, `Icons.Rounded.Settings` của bộ icon lõi đã đi kèm `androidx-compose-material3` trong `gradle/libs.versions.toml` (phụ thuộc bắc cầu, không khai báo thêm); `:feature:projects` thêm phụ thuộc module `:core:project` (có sẵn); `:app` thêm `:feature:projects`.

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** test dựng `MainActivity` bằng Robolectric với Hilt ở `:app` (`AppNavHostTest`) lần đầu có trong dự án; `MainActivity` gọi `ads.gatherConsent(this)` và `AdsHost`, có thể không dựng được trên JVM.
- **Điều kiện chuyển:** test không dựng được activity vì `AdsController`, Hilt hay Firebase sau 2 lần sửa.
- **Cách làm thay:** test dựng `AppNavHost` trực tiếp trong `createAndroidComposeRule<ComponentActivity>()` với `LoadingRoute` thay bằng tham số `loadingContent` có mặc định (đổi nhỏ ở `AppNavHost.kt`, đã trong mục 3.2) và `FeatureFlags` giả qua `ProjectsViewModel` tạo tay (`viewModel { ... }`); vẫn kiểm đủ bốn câu của `AppNavHostTest`. File: `app/src/test/kotlin/com/vgl/draw/animation/navigation/AppNavHostComposeTest.kt` (dự phòng).

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Tạo | `app/src/test/kotlin/com/vgl/draw/animation/navigation/AppNavHostComposeTest.kt` | (dự phòng) Bốn câu của `AppNavHostTest` trên `AppNavHost` dựng trực tiếp |

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
| KB-1, "New animation" | Compose UI test | `ProjectsScreenTest.newAnimation_callsCallback` (callback có); `ProjectsRouteTest.newAnimation_withoutDestination_showsComingSoon` (callback `null`: snackbar "Coming soon") |
| KB-1, "Try a free lesson" | Compose UI test | `ProjectsScreenTest.lessonButton_callsCallback`; `ProjectsRouteTest.lesson_withoutDestination_showsComingSoon` |
| KB-1, cờ Bài học tắt | Unit test, Compose UI test | `ProjectsViewModelTest.lessonsFlagOff_hidesLessonButton` và `lessonsFlagTurnsOffWhileShown_hidesLessonButton` (cờ đổi giữa chừng); `ProjectsScreenTest.empty_withoutLessonButton_hasNoLessonNode`; `AppNavigationBarTest.showsNavigationBar_onlyWhenLessonsEnabledOnTopLevelRoute` (bảng: cờ tắt mọi tuyến `false`; cờ bật tuyến Projects `true`, Loading, Sample `false`) |
| KB-1, Back đóng app | Robolectric (vỏ app) | `AppNavHostTest.backOnProjects_finishesActivity` (`pressBack()` rồi `activity.isFinishing`) |
| KB-1, 360 × 640 | Screenshot test, Compose UI test | `ProjectsScreen_empty_small.png` khung `w360dp-h640dp-xxhdpi`; `ProjectsScreenTest.small_emptyButtonsReachable` (`@Config` 360 × 640, `@GraphicsMode(NATIVE)`): cuộn tới `projects_lesson`, nút nằm trọn trong gốc màn, hai nút không chồng nhau (`bottom` nút trên ≤ `top` nút dưới) |
| KB-2, có dự án thì `List` theo đúng thứ tự | Unit test | `ProjectsViewModelTest.projects_keepRepositoryOrder` (ba dự án `updatedAtMillis` lộn xộn theo thứ tự kho; thẻ giữ đúng thứ tự kho) |
| KB-2, thẻ: tên, "48 frames · 12 fps" | Unit test, Compose UI test | `ProjectsViewModelTest.card_hasFrameCountAndFps`; `ProjectsScreenTest.card_showsFramesLine` ("48 frames · 12 fps", và "1 frame · 12 fps" với dự án một khung) |
| KB-2, ảnh thu nhỏ | Unit test | `DefaultProjectsRepositoryTest.thumbnail_readsStoredFile`; `missingThumbnail_updatesOnceThenReads` (đếm `update` = 1); `corruptBytes_returnsNull`; `unreadableProject_doesNotUpdate`; `ProjectsViewModelTest.thumbnail_reloadsWhenUpdatedAtChanges` (hai lần đọc khi `updatedAtMillis` đổi); `thumbnail_twoRequestsSameKey_readOnce` (hai yêu cầu chồng nhau cùng khóa: một lần đọc) |
| KB-2, chạm thẻ mở màn vẽ | Unit test | `ProjectsViewModelTest.clickReadable_editorOn_emitsOpenProject` |
| KB-2, cờ màn vẽ tắt | Unit test | `ProjectsViewModelTest.clickReadable_editorOff_emitsComingSoon`; `editorFlagTurnsOff_thenClick_emitsComingSoon` (cờ bật rồi tắt, kiểm ngay lúc chạm) |
| KB-2, màn vẽ chưa có (callback `null`) | Compose UI test | `ProjectsRouteTest.openProject_withoutDestination_showsComingSoon` |
| KB-2, tên dài cắt "…" | Compose UI test | `ProjectsScreenTest.longName_isEllipsizedOnOneLine` (`@GraphicsMode(NATIVE)`): `GetTextLayoutResult` của tên có `lineCount == 1`, `isLineEllipsized(0)`; `bottom` của tên ≤ `top` của dòng số khung |
| KB-2, cuộn, thanh trên giữ chỗ | Compose UI test | `ProjectsScreenTest.scrollGrid_topBarStays`: 20 thẻ, cuộn tới thẻ cuối, `projects_settings` vẫn ở cùng `top` |
| KB-2, dự án hỏng | Unit test, Compose UI test | `ProjectsViewModelTest.unreadable_isShownAndOthersOpen` (thẻ hỏng `readable = false`, thẻ thường vẫn `OpenProject`); `clickUnreadable_emitsCannotOpen`; `ProjectsScreenTest.unreadable_showsNameAndEmptyThumbnail` (tên hiện, không có ảnh); ảnh `ProjectsScreen_list_unreadable.png` |
| KB-11, nút cài đặt | Unit test, Compose UI test | `ProjectsViewModelTest.settingsFlagOff_hidesSettings` và `settingsFlagTurnsOn_showsSettings`; `ProjectsScreenTest.settings_callsCallback`; `ProjectsRouteTest.settings_withoutDestination_showsComingSoon` |
| KB-13, cờ projects tắt | Unit test, Compose UI test, Robolectric (vỏ app) | `ProjectsViewModelTest.projectsFlagOff_givesDisabled`; `ProjectsRouteTest.disabled_callsOnFeatureDisabledOnce` (hai lần đổi trạng thái `Disabled` vẫn một lần gọi); `AppNavHostTest.projectsFlagOff_opensSample` |
| Vỏ app: dựng lại activity | Robolectric (vỏ app) | `AppNavHostTest.recreate_keepsProjectsScreen` (`ActivityScenario.recreate()` rồi vẫn `projects_screen`, không quay lại Loading) |
| Thanh điều hướng | Compose UI test, Screenshot test | `AppNavigationBarTest.lessons_withoutDestination_showsComingSoon`; `AppNavigationBar_light.png`, `AppNavigationBar_dark.png` (khung `w411dp-h891dp-xxhdpi`, chụp `onRoot()` của thanh) |
| Giao diện màn | Screenshot test | `ProjectsScreen_empty_light.png`, `ProjectsScreen_empty_dark.png`, `ProjectsScreen_list_light.png`, `ProjectsScreen_list_dark.png`, khung `w411dp-h891dp-xxhdpi`, bốn dự án của bảng chữ thiết kế; ảnh thu nhỏ giả màu đặc |
| Màn hình thấp | Screenshot test khung `w360dp-h640dp-xxhdpi`, trạng thái nhiều nội dung nhất | `ProjectsScreen_list_small.png` (8 dự án, một tên dài), `ProjectsScreen_empty_small.png` |
| Đúng thiết kế (khi có `design/screens/`) | Screenshot test cùng tên với từng ảnh thiết kế, ảnh so sánh `scripts/design-compare.sh projects`, rà soát độc lập theo mục 7 của mẫu thiết kế. Ảnh của T-009 không có thanh điều hướng (ở `:app`) và không có quảng cáo (T-010): phần đó lệch so với thiết kế, ghi trong báo cáo (câu hỏi 1) | `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark`, `ProjectsScreen_list_light`, `ProjectsScreen_list_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-009 | Ảnh thiết kế của ticket khác (T-011): `NewProjectScreen_default_light`, `NewProjectScreen_default_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-009 | Ảnh thiết kế của ticket khác (T-012): `ProjectsScreen_menu_light`, `ProjectsScreen_menu_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-009 | Ảnh thiết kế của ticket khác (T-013): `ProjectsScreen_restoring_light`, `ProjectsScreen_restoring_dark`, `ProjectsScreen_restore-error_light`, `ProjectsScreen_restore-error_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-009 | Ảnh thiết kế của ticket khác (T-014): `ProjectsScreen_list_light_unfolded`, `ProjectsScreen_list_dark_unfolded` |
| Chữ đúng bảng chữ của thiết kế | Compose UI test | `ProjectsScreenTest.empty_showsDesignTexts`, `list_showsDesignTexts` ("Your projects", "Recent", "New animation", bốn tên và bốn dòng số khung); screenshot cùng tên ảnh thiết kế đặt `@Config(qualifiers = "en-w411dp-h891dp-xxhdpi")` (chữ thiết kế tiếng Anh) |
| Bản tiếng Việt | Compose UI test | `ProjectsScreenTest.vietnamese_framesLineKeepsNumbers` (`@Config(qualifiers = "vi")`): dòng phụ có "48" và "12" đúng chỗ khung và tốc độ, không đảo (câu hỏi 4) |
| Màn bắt đầu, nút, Back trên máy | Kiểm tra thủ công (emulator: `app/src/deviceScenarios/T-009-danh-sach-du-an.txt`) | Mở app, chờ `projects_screen`, chạm `projects_new`, thấy "Coming soon", Back thì app ra màn hình chính |
| 100 dự án cuộn không giật | Kiểm tra thủ công (máy thật: FPS, khung hình) | Dev tạo 100 dự án (sau T-011, hoặc khôi phục file nhiều dự án sau T-013), cuộn lưới trên máy tầm trung, xem không giật |

**Ngoại lệ viết test trước:** `settings.gradle.kts`, `feature/projects/build.gradle.kts`, `consumer-rules.pro`, `app/build.gradle.kts`, `di/ProjectsModule.kt`, các vector drawable và `strings.xml`: phần nối, cấu hình và tài nguyên, không có hành vi riêng ngoài phần các test trên đã kiểm. Tệp kịch bản `T-009-danh-sach-du-an.txt` và ba dòng sửa của `T-024-mo-app-tu-choi-dong-y.txt` chạy ở `device-session` cuối phiên, không "thất bại trước" được. `AppLaunchTest` chỉ chạy sau merge (ADR-0043).

Thứ tự commit: bước 3 commit các tệp test, `FakeProjectsRepository.kt`, `MainDispatcherRule.kt`, câu mới của `AppLaunchTest.kt`, cùng khung rỗng tối thiểu để test biên dịch được (module, `settings.gradle.kts`, `build.gradle.kts`, các lớp trạng thái, `ProjectsRepository` interface, chữ ký `ProjectsScreen`/`ProjectsRoute`/`AppNavigationBar`/`showsNavigationBar` trả giá trị rỗng); bước 4 commit phần code. Ảnh chuẩn ghi ở bước 4 bằng `./gradlew :feature:projects:recordRoborazziDebug :feature:projects:testDebugUnitTest --rerun` và `./gradlew :app:recordRoborazziStagingDebug :app:testStagingDebugUnitTest --rerun`, rồi `git status --short -- '*/src/test/screenshots/*'` chỉ được thấy các ảnh của mục 3.2; ảnh khác đổi thì trả bằng `git checkout -- <ảnh>`.

### 4.1. Chạy thử

- **Đã chạy thử:** ngày 2026-11-22, trên worktree tạm từ `origin/main` (`b5a33cf`): `./gradlew -q :feature:sample:dependencies --configuration debugCompileClasspath` cho thấy bộ icon lõi của Compose Material (bản 1.7.8) có trên classpath biên dịch của feature, kéo theo bởi `material3` 1.3.2; ba SVG Material Symbols `upload_file`, `school`, `animation` tải được (HTTP 200) trong môi trường cloud.
- **Lệnh:** `scripts/check.sh` và `./gradlew :app:assembleStagingRelease` trên nhánh có code liên quan: Chạy thử: chưa chạy xong (phiên con soạn plan có giới hạn khoảng 35 phút, không đủ để dựng module mới và chạy hai lệnh này; câu hỏi 7).
- **Kết quả:** Chưa có kết quả của `check.sh` và `assembleStagingRelease`. Phương án dự phòng ở mục 3.4 chưa thử.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** Không có. Không chạm module core (chỉ thêm phụ thuộc vào `:core:project` có sẵn), không quảng cáo, không sự kiện, không quyền, không thư viện mới. Dòng `include` của `settings.gradle.kts` là bước chuẩn khi thêm module feature (như T-044).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 3 | Module feature mới cộng sửa vỏ `:app` (màn bắt đầu, `Scaffold`, thanh điều hướng, `MainActivity`), test androidTest và kịch bản có sẵn |
| B. Độ rõ của yêu cầu | 1 | Spec đã xác nhận, có thiết kế và số liệu; vài điểm AI tự quyết (chữ báo dự án hỏng, nút nhập file trước T-013, vị trí thanh điều hướng trong ảnh) nêu ở mục 8 |
| C. Khả năng kiểm chứng tự động | 1 | Mọi hành vi có test JVM; chỉ độ mượt khi cuộn 100 dự án cần máy thật, màn bắt đầu trên emulator chỉ kiểm sau merge |
| D. Ảnh hưởng chéo | 2 | Đổi màn bắt đầu của app (mọi luồng đi qua); `AppNavHost` cùng sửa với T-010, T-011, T-012, T-013, T-014, T-056 và các ticket màn hình khác; T-010 đến T-014 dựa vào cấu trúc màn này |
| E. Độ mới của kỹ thuật | 1 | Theo mẫu `:feature:sample`; mới một phần: test dựng `MainActivity` có Hilt trên Robolectric (có dự phòng ở mục 3.4), vector drawable từ Material Symbols |
| **Tổng** | 8 | **Mức: Trung bình** |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `settings.gradle.kts` (một dòng), `app/build.gradle.kts`, `AppNavHost.kt`, `MainActivity.kt`, `app/src/main/res/values*/strings.xml`, `AppLaunchTest.kt`, `T-024-mo-app-tu-choi-dong-y.txt`. Không chạm `:core:*`.
- Task khác trong sprint có thể xung đột:
  - T-010, T-011, T-012, T-013, T-014, T-056 (feature projects) đều dựa vào `:feature:projects` và `ProjectsScreen` của T-009, và sửa lại các file của plan này: chạy sau T-009, tuần tự.
  - T-007 (vị trí quảng cáo, `status: stopped`, chưa merge): T-009 không phụ thuộc và không chạm `:core:ads`; T-010 mới cần T-007. Plan T-009 không thêm test vào `:core:ads`, nên không làm nặng thêm bộ nhớ của test `:core:ads` trên CI (bài học L-004).
  - Ticket màn hình khác sửa `AppNavHost.kt` (onboarding, bài học, cài đặt, màn vẽ): danh sách chạy xếp tuần tự (run-check.py báo cùng sửa file). Ticket onboarding đổi đích sau Loading: thống nhất rằng đích sau onboarding là `ProjectsDestination` của plan này.
  - `T-024-mo-app-tu-choi-dong-y.txt`: plan nào khác cũng sửa kịch bản này thì xếp sau T-009.
- Đề xuất thứ tự chạy: sau T-002, T-004, T-006 (đã merge); trước T-010, T-011, T-012, T-014, T-056.

## 7. Điều kiện dừng riêng

- Cần đổi `ProjectRepository`, `ProjectStore` hay `ProjectThumbnails` của `:core:project` để làm màn: dừng (sẽ chạm điểm chặn 7, plan chưa nêu).
- Màn mẫu (`:feature:sample`) không còn mở được khi cờ projects tắt: dừng, không bỏ đường về màn mẫu (spec KB-13).

## 8. Câu hỏi cho người duyệt

1. **Ảnh cùng tên thiết kế không có thanh điều hướng và quảng cáo.** Thanh điều hướng nằm ở `:app` (theo ticket) nên ảnh của `:feature:projects` không có thanh; quảng cáo là T-010. `scripts/design-compare.sh projects` sẽ thấy lệch ở đáy ảnh. Đề xuất: chấp nhận lệch này ở T-009, rà soát độc lập chỉ so phần trên thanh; thanh có ảnh riêng ở `:app` (`AppNavigationBar_*`). Phương án khác: đưa `AppNavigationBar` vào `:core:designsystem` để ảnh của feature vẽ cả thanh, nhưng chạm điểm chặn 7 (rủi ro cao).
2. **Nút nhập file trên thanh trên trước T-013.** Thiết kế có nút `upload_file` ở cả hai trạng thái. Đề xuất: hiện nút theo thiết kế, chạm thì "Coming soon" tới T-013. Phương án khác: ẩn nút tới T-013 (ảnh lệch thiết kế).
3. **Chữ báo dự án hỏng khi chạm thẻ** (KB-2, spec không ghi chữ). Đề xuất: thông báo ngắn "Can't open this project." tự ẩn, không nút.
4. **Bản tiếng Việt.** Spec theo chữ tiếng Anh (bản đề xuất Q9); quy ước của dự án có `values-vi/`. Đề xuất: thêm `values-vi/strings.xml` như `:feature:loading` ("Tạo hoạt hình đầu tiên", "Hoạt hình mới", "Thử một bài học miễn phí", "%1$d khung · %2$d fps"), anh xác nhận chữ. Phương án khác: chỉ tiếng Anh.
5. **Giữ chỗ cuộn khi từ Cài đặt quay lại** (KB-11) chỉ kiểm được khi có màn Cài đặt (T-016). Đề xuất: T-009 dùng `rememberLazyGridState` (tự lưu theo back stack), T-016 thêm test quay lại.
6. **Test dựng `MainActivity` bằng Robolectric với Hilt** (`AppNavHostTest`, `testStaging`) chưa có tiền lệ trong `:app`; có phương án dự phòng (mục 3.4). Anh đồng ý cả hai?
7. **Chạy thử chưa xong.** `scripts/check.sh` và `./gradlew :app:assembleStagingRelease` trên nhánh có module mới chưa chạy (giới hạn thời gian của phiên con soạn plan). Đề xuất: người lập danh sách chạy (hay phiên chạy task, ở bước 4) chạy hai lệnh này; `pr-check` của task cũng build bản release vì PR đổi `settings.gradle.kts`.
8. **Màn bắt đầu trên staging đổi ngay khi merge.** Staging bật sẵn `PROJECTS`, nên sau merge mọi người thử staging vào danh sách dự án (chưa tạo được dự án tới T-011) thay cho màn mẫu. Đề xuất: chấp nhận; màn mẫu vẫn vào được bằng cách tắt cờ trong test.
