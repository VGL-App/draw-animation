---
ticket: T-002
title: Kho dự án: danh sách, tạo, đổi tên, nhân bản, xóa, đếm dự án
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 15M          # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [core]      # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-002: KHO DỰ ÁN: DANH SÁCH, TẠO, ĐỔI TÊN, NHÂN BẢN, XÓA, ĐẾM DỰ ÁN

## 1. Tóm tắt

Thêm vào `:core:project` một kho dự án (`ProjectRepository`) bọc `ProjectStore` của T-001: luồng danh sách dự án thường sắp theo lần sửa gần nhất (dự án hỏng vẫn có, mang cờ không đọc được), tạo, lưu, đổi tên, nhân bản, xóa, và luồng số dự án đã từng tạo bằng màn Tạo dự án, lưu trong DataStore dùng chung của `:core:data`. Mọi lỗi ghi trả về kết quả lỗi (không ném), hết chỗ là loại lỗi riêng; tạo hay nhân bản hỏng giữa chừng không để lại dự án dở. Kiểm chứng bằng unit test JVM trên thư mục tạm với `FileProjectStore` thật, một kho bọc ném lỗi ghi theo lượt, và DataStore giả trong bộ nhớ.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/projects/spec.md`. Ticket chỉ làm phần dữ liệu; giao diện danh sách là T-009, Tạo dự án là T-011, menu (đổi tên, nhân bản, xóa) là T-012, sao lưu và khôi phục là T-003, T-013.

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-2 | Khi danh sách có nhiều dự án thì dự án sửa gần nhất đứng đầu; dự án vừa tạo, vừa nhân bản hay vừa khôi phục đứng đầu | `projects` sắp giảm dần theo `updatedAtMillis`; tạo và nhân bản đặt `createdAtMillis = updatedAtMillis = now`. Khôi phục (T-003) ghi `updatedAtMillis = now` rồi gọi `refresh()` |
| KB-2 | Khi từ màn vẽ quay về danh sách thì thẻ của dự án đó đứng đầu, ảnh thu nhỏ và số khung đúng | Màn vẽ lưu bằng `save(project)`: kho đặt `updatedAtMillis = now` và phát lại danh sách. Ảnh thu nhỏ do T-004 vẽ; màn vẽ ghi ô ảnh thẳng qua `ProjectStore` rồi gọi `save` hay `refresh()` |
| KB-2 | Khi có 100 dự án thì danh sách mở ra không giật | Kho đọc `project.json` của mọi dự án trên dispatcher IO của `ProjectStore`, không đọc ảnh; một test 100 dự án kiểm đủ 100 mục (không đo thời gian) |
| KB-2 | Khi một dự án không đọc được thì các dự án khác vẫn hiện; thẻ của nó vẫn hiện với tên nếu đọc được; app không tự xóa (câu hỏi 9) | Mục `ProjectListItem.Unreadable(id, name, newerVersion)` cho kết quả `Corrupt` và `NewerVersion`; không xóa. Vị trí của mục hỏng: cuối danh sách (câu hỏi 1) |
| KB-3 | Tên mặc định "My animation <số>", số là số dự án đã từng tạo cộng một, không giảm khi xóa (câu hỏi 4) | `createdCount: Flow<Int>`; T-011 ghép chữ tên mặc định (chữ hiển thị nằm ở feature). `create` của dự án thường tăng bộ đếm sau khi ghi xong; xóa không giảm |
| KB-3 | Khi tạo dự án thì dự án nằm trên máy, ở đầu danh sách | `create(NewProject)` ghi tệp nền ảnh (nếu có) rồi `project.json`, trả `Success(project)` |
| KB-3 | Ô tên trống thì dùng tên mặc định; tối đa 40 ký tự (câu hỏi 5) | T-011 thay tên trống bằng tên mặc định trước khi gọi; kho từ chối tên trống (`IllegalArgumentException`, lỗi lập trình) và cắt tên ở 40 ký tự (đếm theo code point, không cắt đôi cặp surrogate), bỏ khoảng trắng hai đầu |
| KB-3 | Khi máy không đủ chỗ thì báo lỗi dễ hiểu, không tạo dự án dở | Lỗi ghi hết chỗ trả `Failure(NO_SPACE)`; mọi lỗi ghi khi tạo đều xóa thư mục dự án mới (`ProjectStore.delete`), không tăng bộ đếm |
| KB-5 | Nhập tên mới và xác nhận thì thẻ hiện tên mới ngay; mở lại app vẫn giữ; tên trùng vẫn cho lưu | `rename(id, name)` ghi `project.json` và phát lại danh sách; không kiểm trùng. Đổi tên không đổi `updatedAtMillis` (câu hỏi 2) |
| KB-5 | Ô nhập trống hay chỉ khoảng trắng thì nút xác nhận tắt | Phần nút là T-012; kho từ chối tên trống sau khi bỏ khoảng trắng (`IllegalArgumentException`) để không bao giờ lưu tên trống |
| KB-6 | Nhân bản có cùng khung, layer, âm thanh, khung hình, độ phân giải, tốc độ, nền; ở đầu danh sách; tên "<tên> copy", lần sau "<tên> copy 2", cắt tên gốc khi vượt 40 ký tự; gốc không đổi | `duplicate(id)`: mã dự án mới, chép `project.json` (đổi `id`, `name`, `createdAtMillis`, `updatedAtMillis`, `kind = Regular` giữ nguyên vì chỉ dự án thường có trong danh sách) và mọi tệp mà dự án tham chiếu (ảnh thu nhỏ, nền ảnh, từng ô khung × layer, âm thanh, ảnh lớp vẽ theo). Tên theo `ProjectNames.copyName` (mục 3.1) |
| KB-6 | Vẽ trên bản sao thì gốc không đổi, và ngược lại | Bản sao có thư mục riêng, tệp chép từng byte, không dùng liên kết |
| KB-6 | Máy không đủ chỗ thì báo lỗi và không để lại bản sao dở | Lỗi ghi ở bất kỳ tệp nào: xóa thư mục bản sao, trả `Failure(NO_SPACE)` hay `Failure(WRITE_FAILED)`; `project.json` ghi sau cùng nên bản dở không bao giờ có trong `ids()` |
| KB-6 | Dự án lớn: có dấu hiệu đang làm, không bấm nhân bản lần nữa được | Phần giao diện là T-012. Kho chạy mọi lần tạo, nhân bản, dọn dẹp tuần tự (một `Mutex` của kho), nên hai lần nhân bản chồng nhau vẫn cho hai tên khác nhau |
| KB-7 | Xác nhận thì thẻ biến mất và dữ liệu bị xóa khỏi máy; xóa dự án cuối thì danh sách trống | `delete(id)` gọi `ProjectStore.delete`, phát lại danh sách; danh sách rỗng là `emptyList()` |
| Ticket | Số dự án đã từng tạo không tính bài học, khôi phục, nhân bản (câu hỏi 4, 16) | Chỉ `create` với `kind = Regular` tăng bộ đếm; `create` với `ProjectKind.Lesson` (cho feature bài học) không tăng; `duplicate` không tăng; T-003 khôi phục không đi qua `create` |
| Ticket | Lỗi ghi `IOException` thành kết quả lỗi; hết chỗ là loại lỗi riêng | `ProjectResult.Failure(ProjectFailure)` với `NO_SPACE`, `WRITE_FAILED`, `NOT_FOUND`, `UNREADABLE`; nhận biết hết chỗ bằng hàm thuần `isOutOfSpace` (mục 3.1) |

Thiết kế tham chiếu: Không có thiết kế riêng (ticket không có giao diện; docs/ops/design-workflow.md).

ADR liên quan: ADR-0001 (`:core:project` thêm phụ thuộc `:core:data`, core phụ thuộc core), ADR-0019 (ghi log bằng `AppLogger`, không ghi tên dự án hay mã dự án).

## 3. Giải pháp

### 3.1. Cách làm

Mọi tệp mới nằm trong gói `com.vgl.draw.animation.core.project.repository` của `:core:project`, trừ Hilt module ở `di/`. Làm theo mẫu của T-001 (`FileProjectStore`: lớp cài đặt `internal`, nhận dispatcher và logger qua hàm dựng) và mẫu DataStore của `DataStoreDeviceSettingsRepository` (T-005).

**Giao diện công khai** (`ProjectRepository.kt`):

```kotlin
interface ProjectRepository {
    /** User projects (no lessons), most recently updated first; unreadable ones last. Re-emitted after every change made here and after refresh(). */
    val projects: Flow<List<ProjectListItem>>
    /** Projects ever created with create() and kind Regular. Never decreases. 0 when nothing stored or unreadable. */
    val createdCount: Flow<Int>
    suspend fun create(request: NewProject): ProjectResult<Project>
    suspend fun save(project: Project): ProjectResult<Project>
    suspend fun rename(id: ProjectId, name: String): ProjectResult<Project>
    suspend fun duplicate(id: ProjectId): ProjectResult<Project>
    suspend fun delete(id: ProjectId): ProjectResult<Unit>
    /** Reloads the list after another writer (restore, cells written by the editor) changed projects. */
    fun refresh()
}

sealed interface ProjectListItem {
    val id: ProjectId
    data class Readable(val project: Project) : ProjectListItem   // id = project.id
    data class Unreadable(override val id: ProjectId, val name: String?, val newerVersion: Boolean) : ProjectListItem
}

data class NewProject(
    val name: String, val aspectRatio: AspectRatio, val resolution: Resolution, val frameRate: FrameRate,
    val background: NewBackground, val firstLayerName: String, val kind: ProjectKind = ProjectKind.Regular,
)

sealed interface NewBackground {
    data class Solid(val color: BackgroundColor) : NewBackground
    /** Bytes already rotated and scaled by the caller (T-011); extension like "jpg", checked as in ProjectFile. */
    class Photo(val bytes: ByteArray, val extension: String) : NewBackground
}

sealed interface ProjectResult<out T> {
    data class Success<T>(val value: T) : ProjectResult<T>
    data class Failure(val reason: ProjectFailure) : ProjectResult<Nothing>
}

enum class ProjectFailure { NO_SPACE, WRITE_FAILED, NOT_FOUND, UNREADABLE }
```

`NewBackground.Photo` là `class` thường (không `data class`) vì `ByteArray` không so bằng nội dung. Tên kiểu có thể đổi nhẹ khi viết test (bước 3) nếu ktlint hay trình biên dịch yêu cầu, giữ nghĩa.

**Tên** (`ProjectNames.kt`, `object ProjectNames`, hàm thuần):

- `const val MAX_NAME_LENGTH = 40`.
- `normalize(name: String): String`: bỏ khoảng trắng hai đầu (`trim()`); rỗng thì `IllegalArgumentException`; dài hơn 40 code point thì giữ 40 code point đầu (`name.substring(0, name.offsetByCodePoints(0, 40))`) rồi bỏ khoảng trắng cuối.
- `copyName(source: String, existing: Collection<String>): String`: thử lần lượt hậu tố `" copy"`, `" copy 2"`, `" copy 3"`, ...; với mỗi hậu tố, phần gốc là `source` (đã `trim()`) cắt bớt code point cuối cho tới khi phần gốc cộng hậu tố không quá 40 code point (rồi bỏ khoảng trắng cuối của phần gốc); trả tên đầu tiên không có trong `existing` (so đúng từng ký tự). `existing` là tên của mọi mục trong danh sách hiện có (mục hỏng có tên thì tính). Tên "<tên> copy" nhân bản tiếp cho "<tên> copy copy" (câu hỏi 3).

**Nhận biết hết chỗ** (`StorageErrors.kt`): `internal fun isOutOfSpace(e: Throwable): Boolean` đi theo chuỗi `cause` (tối đa 10 tầng, tránh vòng), trả `true` khi `message` của một tầng chứa `"ENOSPC"` hay `"No space left on device"` (Android báo lỗi ghi dạng `write failed: ENOSPC (No space left on device)`; JVM trên Linux báo `No space left on device`). Không dùng `android.system.ErrnoException` để hàm chạy được trong unit test JVM không Robolectric. Không kiểm trước dung lượng trống (`usableSpace`): lỗi ghi thật là nguồn đúng duy nhất, và mọi lỗi đã dọn bản dở.

**Bộ đếm** (`CreatedProjectsCounter.kt`): `internal interface CreatedProjectsCounter { val count: Flow<Int>; suspend fun increment() }` và `internal class DataStoreCreatedProjectsCounter(dataStore: DataStore<Preferences>, logger: AppLogger)` trên DataStore `app_preferences` dùng chung (do `DataStoreModule` của `:core:data` cung cấp), khóa `intPreferencesKey("projects_created_count")` (không trùng bốn khóa của T-005: `settings_default_frame_rate`, `settings_default_aspect_ratio`, `settings_stylus_pressure`, `first_stroke_drawn`). `count`: `dataStore.data.retryWhen` như `DataStoreDeviceSettingsRepository` (gặp `IOException` thì ghi log `warn`, phát `emptyPreferences()`, chờ rồi đọc lại; lỗi khác không thử lại), `map { it[KEY]?.coerceAtLeast(0) ?: 0 }`, `distinctUntilChanged()`. `increment()`: `dataStore.edit { it[KEY] = (it[KEY] ?: 0).coerceAtLeast(0) + 1 }`, ném `IOException` cho kho. Thời gian chờ thử lại: 1 giây nhân đôi tới tối đa 30 giây (giống T-005; hằng số riêng trong tệp, không dùng chung hàm `internal` của `:core:data`).

**Kho** (`DefaultProjectRepository.kt`, `internal class DefaultProjectRepository(store: ProjectStore, counter: CreatedProjectsCounter, clock: () -> Long, logger: AppLogger)`):

- Một `MutableStateFlow<Int>` đếm phiên bản; mỗi thay đổi thành công và `refresh()` tăng nó. `projects = version.mapLatest { loadAll() }` (`@OptIn(ExperimentalCoroutinesApi::class)`), không `distinctUntilChanged` để `refresh()` luôn phát lại. Lần thu đầu tiên chạy `cleanUpOnce()` trước `loadAll()`.
- `loadAll()`: `store.ids()`, `store.read` từng mã; `Ok` của dự án thường thành `Readable`, `Ok` của bài học bị bỏ, `Corrupt` thành `Unreadable(name, newerVersion = false)`, `NewerVersion` thành `Unreadable(name, newerVersion = true)`, `NotFound` bị bỏ (dự án bị xóa giữa `ids()` và `read`). Sắp: `Readable` giảm dần theo `updatedAtMillis`, bằng nhau thì theo `id.value`; sau đó `Unreadable` theo `id.value`.
- Một `Mutex` của kho (`writeLock`) bao `create`, `duplicate`, `cleanUpOnce`; `save`, `rename`, `delete` không cần (đã có khóa theo dự án của `FileProjectStore`, và không đổi tên dự án khác). Mục đích: hai lần nhân bản chồng nhau cho hai tên khác nhau; dọn dẹp không xóa thư mục của một lần tạo đang chạy.
- `create(request)`: `ProjectNames.normalize(request.name)` (tên trống ném `IllegalArgumentException` trước khi chạm đĩa); `NewBackground.Photo` có `extension` sai cũng ném trước (`ProjectFile.Background(extension)` kiểm). Mã mới `ProjectId.random()`, `Project.create(..., nowMillis = clock())`, nền `Background.Solid(color)` hay `Background.Photo(extension)`. Thứ tự ghi theo `ProjectStore`: `writeFile(Background(ext), bytes)` nếu có ảnh, rồi `write(project)`. Bắt `IOException`: gọi `store.delete(newId)` trong `try` riêng (lỗi dọn chỉ ghi log), trả `Failure(NO_SPACE)` nếu `isOutOfSpace`, không thì `Failure(WRITE_FAILED)`. Ghi xong và `kind == Regular` thì `counter.increment()`; `increment` ném `IOException` thì ghi log `warn` và vẫn trả `Success` (dự án đã có trên máy; câu hỏi 4). Tăng `version`.
- `save(project)`: `copy(updatedAtMillis = clock())`, `store.write`; `IOException` thành `Failure`; tăng `version` khi thành công. `IllegalArgumentException` (dự án sai) để lọt ra: lỗi lập trình, như `ProjectStore.write`.
- `rename(id, name)`: `normalize(name)`; `store.read(id)`: `NotFound` thì `Failure(NOT_FOUND)`, `Corrupt`/`NewerVersion` thì `Failure(UNREADABLE)`; `Ok` thì `write(project.copy(name = normalized))` (không đổi `updatedAtMillis`), tăng `version`.
- `duplicate(id)`: trong `writeLock`; đọc nguồn như `rename` (`NOT_FOUND`, `UNREADABLE`). Tên: `copyName(source.name, tên hiện có từ loadAll())`. Mã mới, `now = clock()`, bản sao `source.copy(id = newId, name = ..., createdAtMillis = now, updatedAtMillis = now)`. Chép tệp theo danh sách tham chiếu `referencedFiles(source)`: `Thumbnail`; `Background(ext)` nếu nền ảnh; `Cell(frame.id, layer.id)` cho mọi khung × layer; `Sound(clip.id, clip.fileExtension)` cho mọi âm thanh; `ReferenceImage(i, ref.fileExtension)` cho `i` từ 0 tới `ref.frameCount - 1` nếu có lớp vẽ theo (tên trường đúng theo mô hình của T-001; xem `SoundClip.kt`, `EditorSettings.kt`). Mỗi tệp: `readFile` của nguồn, `null` thì bỏ qua (ô trống), có thì `writeFile` cho mã mới. Cuối cùng `write(copy)`. `IOException` ở bất kỳ bước nào (kể cả đọc nguồn): `store.delete(newId)` trong `try` riêng, trả `NO_SPACE` hay `WRITE_FAILED`. Không tăng bộ đếm. Tăng `version` khi thành công.
- `delete(id)`: `store.delete(id)`; `IOException` thành `Failure(WRITE_FAILED)` (hết chỗ khi xóa không có nghĩa, vẫn dùng `isOutOfSpace` cho thống nhất); thành công tăng `version`. Không giảm bộ đếm. Mã không có cũng là `Success` (như `ProjectStore.delete`).
- `cleanUpOnce()`: chỉ một lần mỗi đối tượng kho (cờ `AtomicBoolean` đặt trong `writeLock`): `store.deleteIncomplete()`; `IOException` chỉ ghi log `warn`, danh sách vẫn tải.
- Log (thẻ `ProjectRepo`): `info` khi tạo, nhân bản, xóa, đổi tên thành công (không tên, không mã); `warn` khi lỗi ghi (loại lỗi `NO_SPACE`/`WRITE_FAILED`, kèm ngoại lệ), khi tăng bộ đếm lỗi, khi dọn dẹp lỗi; `info` số thư mục dở đã dọn khi lớn hơn 0.

**Kho tệp** (`ProjectStore.kt`, `FileProjectStore.kt`): thêm `suspend fun deleteIncomplete(): Int` vào interface (KDoc: xóa mọi thư mục dự án có tên là mã hợp lệ mà không có `project.json`, tức lần tạo hay nhân bản bị dừng giữa chừng; không chạm thư mục bắt đầu bằng `.`; trả số thư mục đã xóa; ném `IOException` khi không xóa được một thư mục; người gọi không được chạy nó khi đang tạo dự án). `FileProjectStore`: trên `ioDispatcher`, liệt kê con của `root` (không có thì 0), lọc thư mục có `ProjectId.parseOrNull(name) != null` và không có `project.json`, xóa từng thư mục dưới khóa của dự án đó (`withProjectLock` hiện chỉ nhận khối thường, giữ nguyên; gọi `locks.computeIfAbsent(id)` như nó), kiểm lại `project.json` trong khóa trước khi xóa, `deleteRecursively` (ném `IOException` khi còn sót). Thư mục bắt đầu bằng `.` (`.deleting-*` của T-001, chỗ chuẩn bị của khôi phục T-003) không bị chạm. Các hàm cũ không đổi.

**Hilt** (`di/ProjectRepositoryModule.kt`, tệp mới, không sửa `ProjectModule.kt` hay `RenderModule.kt`): `@Provides @Singleton CreatedProjectsCounter = DataStoreCreatedProjectsCounter(dataStore, logger)` (nhận `DataStore<Preferences>` của `DataStoreModule`), `@Provides @Singleton ProjectRepository = DefaultProjectRepository(store, counter, System::currentTimeMillis, logger)`.

**Build**: `core/project/build.gradle.kts` thêm `implementation(project(":core:data"))` (cho `DataStore<Preferences>`, mà `:core:data` khai `api`). `:core:data` không đổi, không phụ thuộc `:core:project` (quyết định của anh Nhật, plan T-001 câu hỏi 4). Test dùng DataStore giả trong bộ nhớ tự viết trong `:core:project` (các lớp giả của `:core:data` là `internal` trong test của module đó).

**Người dùng sau này:** T-009 thu `projects` và `createdCount` không cần; T-011 dùng `createdCount` và `create`; T-012 dùng `rename`, `duplicate`, `delete`; màn vẽ (T-018) dùng `save`; T-003 dùng `refresh()` sau khi khôi phục, và dùng chỗ chuẩn bị bắt đầu bằng `.` để `deleteIncomplete` không chạm phần đang khôi phục.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `core/project/build.gradle.kts` | Thêm `implementation(project(":core:data"))` |
| Sửa | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ProjectStore.kt` | Thêm `deleteIncomplete()` kèm KDoc; sửa đoạn KDoc của lớp nói "A creator calls [delete] when creating fails" để nhắc thêm `deleteIncomplete` |
| Sửa | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/FileProjectStore.kt` | Cài đặt `deleteIncomplete()` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/repository/ProjectRepository.kt` | Interface `ProjectRepository`, `ProjectListItem`, `NewProject`, `NewBackground`, `ProjectResult`, `ProjectFailure` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/repository/DefaultProjectRepository.kt` | Lớp cài đặt `internal` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/repository/ProjectNames.kt` | `MAX_NAME_LENGTH`, `normalize`, `copyName` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/repository/StorageErrors.kt` | `isOutOfSpace` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/repository/CreatedProjectsCounter.kt` | Interface và lớp DataStore của bộ đếm |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/di/ProjectRepositoryModule.kt` | Hilt module của kho và bộ đếm |
| Sửa | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/storage/FileProjectStoreTest.kt` | Thêm các test `deleteIncomplete_*` (mục 4); không đổi test cũ |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/repository/DefaultProjectRepositoryTest.kt` | Test của kho (mục 4) |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/repository/ProjectNamesTest.kt` | Test tên |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/repository/StorageErrorsTest.kt` | Test nhận biết hết chỗ |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/repository/CreatedProjectsCounterTest.kt` | Test bộ đếm |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/repository/RepositoryTestDoubles.kt` | `MemoryPreferencesDataStore`, `UnreadablePreferencesDataStore`, `UnwritablePreferencesDataStore`, `FailingProjectStore` (bọc `FileProjectStore` bằng `ProjectStore by delegate`, ném `IOException(message)` ở lượt ghi thứ N của `writeFile`/`write`), `MemoryCounter` (bộ đếm giả, có cờ ném khi `increment`), đồng hồ giả |

| Sửa | `CLAUDE.md` | Dòng `:core:project` của bảng module: thêm "kho dự án cho mọi feature (danh sách, tạo, lưu, đổi tên, nhân bản, xóa, số dự án đã từng tạo)", như T-001, T-004 đã ghi phần của mình; không sửa dòng khác |

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có (kho không có giao diện; cờ `feature_projects_enabled` của T-006 do màn hình dùng).
- Quảng cáo: Không chạm. Bộ đếm `createdCount` là dữ liệu cho điều kiện "từ dự án thứ 2" của `create_inter`, do T-011 đọc.
- Sự kiện phân tích: Không chạm (`project_create` do T-011 ghi).
- File R2: Không chạm.
- Dữ liệu lưu trên máy: khóa mới `projects_created_count` (Int) trong tệp DataStore `app_preferences` dùng chung; đúng dòng "Số dự án người dùng đã tạo" của spec mục 6.
- **Quyền truy cập:** Không thêm. Kho chỉ đọc ghi trong `filesDir` của app (qua `FileProjectStore`) và DataStore của app, không cần quyền ở mọi phiên bản từ minSdk 26 tới targetSdk 36.
- **Thư viện và SDK:** Không thêm. `androidx.datastore.preferences` (`libs.androidx.datastore.preferences`, đã có) tới `:core:project` qua `api` của `:core:data`; `:core:project` thêm phụ thuộc module `implementation(project(":core:data"))`. `kotlinx-coroutines` (`Flow`, `Mutex`, `mapLatest`) đã có trên classpath của `:core:project` (T-001 dùng `Mutex`); `kotlinx-coroutines-test` đã có trong test (T-001 dùng `runTest`).

### 3.4. Phương án dự phòng

Không có. Mọi kỹ thuật đã có trong dự án (tệp qua `FileProjectStore`, DataStore Preferences, `Flow` trong unit test).

### 3.5. Hành vi đã có bị đổi

`scripts/existing-tests.py` liệt kê các test của `FileProjectStoreTest` và các test dùng `ProjectStore` (kết quả ở mục 4.1). Plan chỉ thêm một hàm vào `ProjectStore` và `FileProjectStore`; không đổi hàm nào đã có.

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Thư mục mã hợp lệ không có `project.json` bị ẩn khỏi `ids()`, không ai dọn | Vẫn ẩn khỏi `ids()`; thêm `deleteIncomplete()` để kho xóa chúng một lần khi tải danh sách đầu tiên | `FileProjectStoreTest.ids_listsOnlyProjectDirsWithMetadata`, `FileProjectStoreTest.filesWithoutMetadata_notListed` | Giữ nguyên: `ids()` không đổi; việc dọn chỉ chạy khi gọi `deleteIncomplete()` |
| `ProjectStore` có 7 hàm; lớp bọc trong test dùng `ProjectStore by fileStore` | Interface có thêm `deleteIncomplete()` | `ProjectThumbnailsTest` (lớp bọc `object : ProjectStore by fileStore`) | Giữ nguyên: ủy quyền bằng `by` tự có hàm mới, test vẫn biên dịch |
| `delete()` dọn mọi `.deleting-*` còn sót | Không đổi; `deleteIncomplete()` không chạm thư mục bắt đầu bằng `.` | `FileProjectStoreTest.delete_cleansLeftoverDeletingDirs` | Giữ nguyên |

## 4. Kiểm thử

Mọi test là unit test JVM trong `:core:project`, chạy bằng `runTest`. Kho thật: `FileProjectStore(root = File(tmp.root, "projects"), StandardTestDispatcher(testScheduler), RecordingAppLogger())` với `TemporaryFolder`, tức dispatcher dùng chung scheduler của `runTest` (bài học L-001: không tạo `StandardTestDispatcher` có scheduler riêng bên trong `runTest`). Đồng hồ giả: `var now = 1_000L; clock = { now }`. Bộ đếm thật trên `MemoryPreferencesDataStore` (trừ khi ghi khác). Kho được dựng bằng `DefaultProjectRepository(store, counter, clock, logger)`. Đọc danh sách: `repo.projects.first()`. Không test nào kiểm "chạy trên dispatcher riêng": kho không có dispatcher riêng (mọi việc đĩa đã qua `ioDispatcher` của `FileProjectStore`, đã có test của T-001).

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| KB-2: sửa gần nhất đứng đầu | Unit test | `DefaultProjectRepositoryTest.projects_sortedByUpdatedDescending`: ghi thẳng qua `store.write` ba dự án thường `updatedAtMillis` 10, 30, 20; danh sách là ba `Readable` theo thứ tự 30, 20, 10 |
| KB-2: vừa tạo, vừa nhân bản đứng đầu | Unit test | `create_newProjectFirstInList`: có sẵn dự án `updatedAtMillis = 5000`, `now = 6000`, `create` thì mục đầu là dự án mới, `createdAtMillis == updatedAtMillis == 6000`. `duplicate_copyFirstInList`: nguồn `updatedAtMillis = 100`, dự án khác 200, `now = 300`, `duplicate(nguồn)` thì mục đầu là bản sao |
| KB-2: lưu từ màn vẽ đưa dự án lên đầu | Unit test | `save_setsUpdatedAtAndMovesFirst`: hai dự án 100, 200; `now = 500`, `save(dựÁn100.copy(frames = thêm một khung))` trả `Success` có `updatedAtMillis == 500`; danh sách đầu là dự án đó với 2 khung; `store.read` trả cùng dự án |
| KB-2: danh sách phát lại sau mỗi thay đổi | Unit test | `projects_reemitsAfterEachChange`: thu `projects` vào danh sách giá trị bằng `backgroundScope.launch` (dispatcher `UnconfinedTestDispatcher(testScheduler)`), `create`, `rename`, `duplicate`, `delete`, `refresh()` lần lượt, `advanceUntilIdle()` sau mỗi bước; số phần tử của giá trị cuối sau mỗi bước là 1, 1 (tên mới), 2, 1, 1 và tổng số lần phát là 6 (lần đầu cộng 5) |
| KB-2: `refresh()` thấy thay đổi từ bên ngoài | Unit test | `refresh_picksUpExternalWrite`: thu như trên; ghi thẳng `store.write(dựÁnMới)`, giá trị cuối chưa có nó; `refresh()`, `advanceUntilIdle()` thì có |
| KB-2: 100 dự án, một hỏng | Unit test | `projects_hundredWithOneCorrupt_allListed`: 99 dự án hợp lệ qua `store.write`, một thư mục mã hợp lệ có `project.json` là `{"formatVersion":1,"name":"Broken"` (JSON hỏng, tên không đọc được) ; danh sách có 100 mục, 99 `Readable` rồi 1 `Unreadable(name = null, newerVersion = false)` ở cuối; thư mục hỏng vẫn còn trên đĩa |
| KB-2: dự án hỏng có tên, bản mới hơn | Unit test | `projects_unreadableKeepsNameAndNewerFlag`: một `project.json` hợp lệ JSON có `"formatVersion":1,"name":"Kept"` nhưng thiếu trường bắt buộc (kết quả `Corrupt(name = "Kept")`), một có `"formatVersion":99,"name":"Future"`; danh sách có `Unreadable(name = "Kept", newerVersion = false)` và `Unreadable(name = "Future", newerVersion = true)`, sắp theo `id.value`. Câu khẳng định đọc kết quả `ProjectFormat.decode` thật, nên trước khi viết, phiên chạy `store.read` trên hai chuỗi này trong test và dùng đúng nội dung khiến `read` trả hai kết quả trên |
| Bài học không có trong danh sách | Unit test | `projects_excludesLessonProjects`: một dự án thường và một `kind = ProjectKind.Lesson("walk", 0)`; danh sách chỉ có dự án thường |
| KB-3: tạo với nền màu | Unit test | `create_solid_writesProjectAndReturnsIt`: `create(NewProject("My animation 1", PORTRAIT_9_16, P1080, FPS_12, Solid(WHITE), "Layer 1"))` trả `Success(p)`; `store.read(p.id)` là `Ok(p)`; `p.name == "My animation 1"`, 1 khung, 1 layer tên "Layer 1", `background == Background.Solid(WHITE)`, `kind == Regular` |
| KB-3: tạo với ảnh nền | Unit test | `create_photo_copiesBackgroundBytes`: `Photo(byteArrayOf(1,2,3), "jpg")`; `p.background == Background.Photo("jpg")`; `store.readFile(p.id, ProjectFile.Background("jpg"))` là `[1,2,3]` |
| KB-3: tên 40 ký tự, bỏ khoảng trắng | Unit test | `create_trimsAndTruncatesName`: tên `"  " + "a".repeat(45) + "  "` cho `"a".repeat(40)`. `ProjectNamesTest.normalize_*`: `" Cat "` cho `"Cat"`; 40 ký tự giữ nguyên; 41 ký tự cắt còn 40; `"a".repeat(39) + "😀b"` (😀 là 2 `Char`, 1 code point) cho `"a".repeat(39) + "😀"` (40 code point, không cắt đôi); `"a".repeat(39) + " b"` cho `"a".repeat(39)` (bỏ khoảng trắng cuối sau khi cắt); `"   "` và `""` ném `IllegalArgumentException` |
| KB-3: tên trống bị từ chối, không chạm đĩa | Unit test | `create_blankName_throwsWithoutWriting`: `assertThrows(IllegalArgumentException)` với tên `"  "`; `store.ids()` rỗng, thư mục `root` không có con nào (hay không tồn tại), `createdCount.first() == 0` |
| KB-3: hết chỗ, không tạo dự án dở | Unit test | `create_noSpace_returnsNoSpaceAndLeavesNothing`: `FailingProjectStore` ném `IOException("write failed: ENOSPC (No space left on device)")` ở lượt `write` đầu (ảnh nền đã ghi xong ở `writeFile`); kết quả `Failure(NO_SPACE)`; `store.ids()` rỗng; `root` không còn thư mục mã nào (đã xóa cả tệp nền); `createdCount.first() == 0`; danh sách rỗng |
| Lỗi ghi khác | Unit test | `create_otherIoError_returnsWriteFailed`: ném `IOException("Could not replace a project file")` ở `writeFile` đầu; `Failure(WRITE_FAILED)`, không có thư mục dở, bộ đếm 0 |
| KB-3 câu 4: bộ đếm tăng khi tạo, mặc định 0 | Unit test | `createdCount_startsAtZero_thenCountsCreates`: `createdCount.first() == 0` khi chưa ghi gì (đối chứng ở bước kế: ) ; hai lần `create` thường thì `== 2` |
| Bộ đếm không giảm khi xóa | Unit test | `createdCount_notDecreasedByDelete`: tạo 2, xóa cả 2, `createdCount.first() == 2`, danh sách rỗng |
| Bộ đếm không tính nhân bản, bài học, ghi từ ngoài | Unit test | `createdCount_ignoresDuplicateLessonAndExternalWrites`: tạo 1 thường (đếm 1); `duplicate` nó; `create` với `kind = Lesson("walk", 0)`; ghi thẳng `store.write` một dự án thường (như khôi phục của T-003); `refresh()`; `createdCount.first() == 1` |
| Hai lần tạo chồng nhau | Unit test | `create_concurrent_bothCountedAndListed`: hai `async { repo.create(...) }` cùng lúc, `awaitAll()`; cả hai `Success` với mã khác nhau; danh sách 2 mục; `createdCount.first() == 2` |
| Tăng bộ đếm lỗi vẫn tạo được | Unit test | `create_counterWriteFails_stillSucceeds`: bộ đếm thật trên `UnwritablePreferencesDataStore`; `create` trả `Success`, dự án có trong danh sách, logger có một dòng `WARN` thẻ `ProjectRepo` |
| KB-5: đổi tên giữ lại | Unit test | `rename_persistsTrimmedNameKeepsUpdatedAt`: dự án `updatedAtMillis = 100`, `now = 900`; `rename(id, "  New name ")` trả `Success` tên `"New name"`, `updatedAtMillis == 100`; một kho mới trên cùng thư mục (`DefaultProjectRepository(FileProjectStore(cùng root,...), ...)`) đọc danh sách thấy `"New name"` |
| KB-5: tên trùng cho lưu; tên trống từ chối | Unit test | `rename_duplicateNameAllowed`: hai dự án "A", "B"; `rename(B, "A")` trả `Success`, danh sách có hai tên "A". `rename_blank_throwsAndKeepsName`: `assertThrows(IllegalArgumentException)` với `" "`, tên giữ nguyên trên đĩa |
| KB-5: dự án không có, hỏng | Unit test | `rename_missing_returnsNotFound`: mã ngẫu nhiên, `Failure(NOT_FOUND)`. `rename_corrupt_returnsUnreadable`: dự án JSON hỏng, `Failure(UNREADABLE)`, tệp không đổi (so byte) |
| KB-6: nhân bản đủ nội dung, gốc không đổi | Unit test | `duplicate_copiesEveryReferencedFile`: nguồn là `TestProjects.full()` đổi `kind = Regular` và `id` mới (có nền ảnh "jpg", 3 layer × 4 khung, âm thanh, lớp vẽ theo); ghi bằng `store.writeFile` ảnh thu nhỏ, nền, 5 ô (bỏ trống các ô khác), mỗi tệp âm thanh, mỗi ảnh lớp vẽ theo, mỗi tệp một nội dung byte khác nhau; `duplicate` trả `Success(copy)`: `copy.id != nguồn.id`, `copy` bằng nguồn trừ `id`, `name`, `createdAtMillis`, `updatedAtMillis` (so bằng `copy.copy(id = nguồn.id, name = nguồn.name, createdAtMillis = nguồn.createdAtMillis, updatedAtMillis = nguồn.updatedAtMillis) == nguồn`); mọi `ProjectFile` của nguồn có cùng byte ở bản sao; ô trống của nguồn cũng không có tệp ở bản sao; `store.read(nguồn.id)` không đổi và tệp nguồn không đổi |
| KB-6: sửa bản sao không đổi gốc, và ngược lại | Unit test | `duplicate_independentFiles`: sau `duplicate`, `writeFile` một ô của bản sao với byte mới thì ô đó của nguồn giữ byte cũ; `writeFile` ô của nguồn thì bản sao giữ byte cũ; `rename` bản sao không đổi tên nguồn |
| KB-6: tên bản sao | Unit test | `ProjectNamesTest.copyName_*`: `("Cat", [])` cho `"Cat copy"`; `("Cat", ["Cat", "Cat copy"])` cho `"Cat copy 2"`; `("Cat", ["Cat copy", "Cat copy 2"])` cho `"Cat copy 3"`; `("Cat copy", ["Cat copy"])` cho `"Cat copy copy"`; nguồn 40 chữ `a` cho `"a".repeat(35) + " copy"` (40 ký tự); nguồn 40 chữ `a` khi đã có tên đó cho `"a".repeat(33) + " copy 2"`; nguồn `"a".repeat(34) + " bcdef"` (40) cho `"a".repeat(34) + " copy"` (bỏ khoảng trắng cuối của phần gốc rồi mới ghép: `"a".repeat(34)` + `" copy"`, 39 ký tự); nguồn có 😀 ở vị trí 35 không bị cắt đôi |
| KB-6: lần sau "copy 2" qua kho | Unit test | `duplicate_twice_namesCopyThenCopy2`: nguồn "Cat"; hai lần `duplicate(nguồn)` cho "Cat copy" rồi "Cat copy 2" |
| KB-6: hai lần nhân bản chồng nhau | Unit test | `duplicate_concurrent_distinctNames`: hai `async { repo.duplicate(nguồn) }`; hai tên là `{"Cat copy", "Cat copy 2"}` |
| KB-6: hết chỗ không để bản dở | Unit test | `duplicate_noSpaceMidway_leavesNoCopy`: nguồn có 5 ô; `FailingProjectStore` ném `IOException("No space left on device")` ở lượt `writeFile` thứ 3; `Failure(NO_SPACE)`; `store.ids()` chỉ có nguồn; `root` chỉ có thư mục của nguồn (không còn thư mục mã nào khác, kể cả `.deleting-*`); danh sách 1 mục; bộ đếm không đổi |
| KB-6: lỗi ghi `project.json` của bản sao | Unit test | `duplicate_metadataWriteFails_leavesNoCopy`: ném `IOException("Could not replace a project file")` ở lượt `write` đầu (mọi tệp đã chép); `Failure(WRITE_FAILED)`; `root` chỉ còn thư mục nguồn |
| KB-6: nguồn không có, hỏng | Unit test | `duplicate_missing_returnsNotFound`, `duplicate_corrupt_returnsUnreadable` (không tạo thư mục mới) |
| KB-7: xóa | Unit test | `delete_removesFromListAndDisk`: hai dự án, `delete(một)` trả `Success(Unit)`; danh sách 1 mục; thư mục của nó không còn; kho mới trên cùng `root` không thấy nó. `delete_last_listEmpty`: một dự án, xóa, danh sách `emptyList()` |
| KB-7: xóa lỗi | Unit test | `delete_ioError_returnsWriteFailed`: `FailingProjectStore` có `delete` ném `IOException("Could not delete a project folder")`; `Failure(WRITE_FAILED)`; dự án vẫn trong danh sách |
| Dọn thư mục dở khi tải lần đầu | Unit test | `projects_firstLoad_cleansIncompleteDirs`: trước khi dựng kho, tạo thư mục `<mã hợp lệ>/cells/x.png` không có `project.json` và một dự án hợp lệ; `projects.first()` có 1 mục; thư mục dở không còn; thư mục của dự án hợp lệ còn. Đối chứng cùng test: trước `projects.first()` thư mục dở vẫn còn (dựng kho không dọn) |
| Dọn chỉ một lần | Unit test | `projects_cleanUpRunsOnce`: `FailingProjectStore` đếm số lần gọi `deleteIncomplete`; thu `projects.first()` hai lần và `refresh()`; số lần gọi là 1 |
| Dọn lỗi không chặn danh sách | Unit test | `projects_cleanUpFails_listStillLoads`: `deleteIncomplete` ném `IOException`; `projects.first()` vẫn có dự án hợp lệ; logger có `WARN` |
| `deleteIncomplete` của kho tệp | Unit test | `FileProjectStoreTest.deleteIncomplete_removesOnlyUuidDirsWithoutMetadata`: dưới `root` có (a) thư mục mã hợp lệ có `project.json`, (b) thư mục mã hợp lệ không có `project.json` nhưng có `background.jpg`, (c) `.deleting-<mã>`, (d) `.restoring-x`, (e) thư mục tên `not-a-uuid` không có `project.json`, (f) tệp thường tên là một mã hợp lệ; trả `1`; chỉ (b) bị xóa, (a), (c), (d), (e), (f) còn nguyên. `deleteIncomplete_noRoot_returnsZero`: `root` chưa tồn tại thì trả 0, không tạo `root` |
| Nhận biết hết chỗ | Unit test | `StorageErrorsTest`: `IOException("write failed: ENOSPC (No space left on device)")` là `true`; `IOException("No space left on device")` là `true`; `IOException("x", IOException("No space left on device"))` (nằm ở `cause`) là `true`; `IOException("Could not replace a project file")` là `false`; `IOException(null as String?)` là `false`; ngoại lệ có `cause` tự trỏ vòng (lớp con ghi đè `cause`) trả `false`, không treo |
| Bộ đếm DataStore | Unit test | `CreatedProjectsCounterTest`: `count_default_zero` (rồi đối chứng `count_incrementThenRead_one`: `increment()` một lần thì `count.first() == 1`, và giá trị lưu ở khóa `intPreferencesKey("projects_created_count")` của `MemoryPreferencesDataStore.state` là 1); `count_negativeStored_readsZero` (khóa đặt -3 thì đọc 0, `increment` cho 1); `count_unreadable_emitsZeroAndLogs` (`UnreadablePreferencesDataStore`: `count.first() == 0`, logger có `WARN`); `increment_unwritable_throwsIOException` (`assertTrue(e is IOException)`, so loại lỗi) |
| Hilt dựng được kho | Lệnh | `scripts/check.sh` biên dịch `:app` (Hilt kiểm đồ thị phụ thuộc lúc biên dịch: `DataStore<Preferences>` của `DataStoreModule`, `ProjectStore` của `ProjectModule`) và chạy `checkModuleDependencies` (`:core:project` phụ thuộc `:core:data`, `:core:designsystem`, `:core:logging`, đều là core) |
| Bản release dựng được | Lệnh | `./gradlew :app:assembleStagingRelease` (thêm phụ thuộc module mới cho `:core:project`; R8 chạy ở bản release) |

**Bước đối chứng cho test giá trị mặc định** (docs/CLAUDE.md, mục Khi viết plan): `createdCount_startsAtZero_thenCountsCreates` và `CreatedProjectsCounterTest.count_default_zero` có thể qua trên khung rỗng nếu khung trả `flowOf(0)`; cả hai cùng test (hay test ngay sau) ghi rồi đọc lại (`create` hai lần cho 2; `increment` cho 1) nên khung rỗng thất bại đúng lý do. `projects_unreadable...`, `create_noSpace...` kiểm trạng thái trên đĩa sau thao tác, có dữ liệu đúng (dự án hợp lệ) cạnh dữ liệu sai.

**Ghi chú cho phiên viết test (bước 3):** khung rỗng của các lớp mới là `TODO()`; test thất bại với `NotImplementedError`. `FailingProjectStore` và các DataStore giả nằm trong `RepositoryTestDoubles.kt`, vào commit test. Hàm `FileProjectStore.deleteIncomplete` ở khung rỗng là `TODO()` (interface phải có hàm để test biên dịch).

**Ngoại lệ viết test trước:** `ProjectRepositoryModule.kt` (Hilt, chỉ nối; kiểm bằng biên dịch `:app` trong `scripts/check.sh`) và dòng phụ thuộc trong `core/project/build.gradle.kts` (kiểm bằng biên dịch và `checkModuleDependencies`).

### 4.1. Chạy thử

- **Đã chạy thử:** Không cần (E dưới 2): DataStore Preferences, tệp qua `FileProjectStore`, `Flow` và `runTest` đều đã dùng trong dự án (T-001, T-005).
- **Lệnh:** `python3 scripts/existing-tests.py docs/features/projects/plans/T-002-kho-du-an.md` (cho mục 3.5); `python3 scripts/token-usage.py estimate` (cỡ M, gợi ý 15.0M).
- **Kết quả:** existing-tests.py: chưa chạy xong (đang cập nhật). token-usage.py: cỡ M trung vị 13.3M trên 2 task, gợi ý 15.0M (tạm, chưa đủ 5 task). `./gradlew :app:assembleStagingRelease` là bước của phiên (mục 4), không chạy lúc lập plan vì chưa có code thêm phụ thuộc.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 7 (module core dùng chung: `:core:project`, và thêm phụ thuộc giữa hai module core `:core:project` → `:core:data`).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 2 | Một module (`:core:project`): 6 tệp mới, sửa 2 tệp của gói `storage` (thêm một hàm vào interface công khai) và `build.gradle.kts`; dùng DataStore dùng chung của `:core:data` mà không sửa `:core:data` |
| B. Độ rõ của yêu cầu | 1 | Spec và ticket rõ về tên, bộ đếm, dự án hỏng, lỗi hết chỗ; còn vài chọn lựa nhỏ không đổi tiêu chí: vị trí của mục hỏng (câu 1), đổi tên có đưa lên đầu không (câu 2), tên bản sao của bản sao (câu 3), bộ đếm khi ghi lỗi (câu 4) |
| C. Khả năng kiểm chứng tự động | 1 | Mọi hành vi có unit test trên thư mục tạm và DataStore giả; lỗi hết chỗ thật của Android chỉ giả lập bằng chuỗi lỗi (nhận biết theo nội dung thông báo), chưa thấy trên máy thật |
| D. Ảnh hưởng chéo | 2 | Interface công khai mà T-003, T-009, T-011, T-012, T-018 và bài học dựa vào; thêm khóa vào tệp `app_preferences` dùng chung với T-005; thêm hàm vào `ProjectStore` mà các lớp bọc trong test dùng (`by` nên vẫn biên dịch) |
| E. Độ mới của kỹ thuật | 0 | Tệp, DataStore Preferences, `Flow`, `Mutex` đều đã có trong dự án |
| **Tổng** | 6 | **Mức: Cao** (chạm điểm chặn 7; tổng điểm riêng là Trung bình) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `core/project/build.gradle.kts`, `ProjectStore.kt`, `FileProjectStore.kt`, `FileProjectStoreTest.kt` (T-001, T-004 đã merge); tệp DataStore `app_preferences` (khóa mới `projects_created_count`, không trùng khóa của T-005).
- Task khác trong sprint có thể xung đột: T-003 (tệp sao lưu) phụ thuộc T-002 và có thể sửa `FileProjectStore.kt`, `ProjectStore.kt` (chỗ chuẩn bị khôi phục bắt đầu bằng `.`): chạy sau T-002, giữ `deleteIncomplete` không chạm thư mục bắt đầu bằng `.`. T-009, T-011, T-012, T-018 dùng interface của plan này: chạy sau. Không plan `approved` nào chưa chạy cùng sửa các tệp của mục 3.2 (T-001, T-004, T-005, T-006 đã merge).
- Đề xuất thứ tự chạy: sau T-001 (đã merge); trước T-003, T-009, T-011, T-012, T-018.

## 7. Điều kiện dừng riêng

- Cần sửa `:core:data` (tệp nào trong `core/data/`) hay cho `:core:data` phụ thuộc `:core:project`: dừng, vì trái quyết định của anh Nhật (plan T-001 câu hỏi 4) và có thể thành vòng.
- Cần đổi định dạng `project.json` (`ProjectFormat`, số phiên bản) hay đổi hành vi của một hàm đã có của `ProjectStore`: dừng, vì là hợp đồng với dữ liệu trên máy người dùng (plan T-001).

## 8. Câu hỏi cho người duyệt

1. **Dự án không đọc được đứng ở đâu trong danh sách?** Không có `updatedAtMillis` đọc được. Đề xuất của AI: cuối danh sách, sắp theo mã (ổn định giữa các lần mở). Phương án khác: dùng giờ sửa của tệp `project.json` để xếp chung với dự án khác (cần thêm hàm vào `ProjectStore`).
2. **Đổi tên có đưa dự án lên đầu danh sách không?** Spec nói "dự án sửa gần nhất đứng đầu" và dòng menu ghi "edited <thời điểm>". Đề xuất của AI: không; đổi tên không đổi `updatedAtMillis`, vì "sửa" là vẽ, và đổi tên một dự án cũ không nên làm nó nhảy lên đầu. Phương án khác: đổi tên đặt `updatedAtMillis = now`.
3. **Tên bản sao của một bản sao:** spec ghi "<tên> copy" (lần sau "<tên> copy 2"). Đề xuất của AI: làm đúng chữ, nhân bản "Cat copy" cho "Cat copy copy". Phương án khác: bỏ hậu tố " copy"/" copy N" của tên gốc trước, nên nhân bản "Cat copy" cho "Cat copy 2".
4. **Ghi bộ đếm lỗi sau khi dự án đã tạo xong:** đề xuất của AI: vẫn báo tạo thành công (dự án đã có trên máy), chỉ ghi log; hệ quả là lần sau tên mặc định có thể trùng số, và `create_inter` có thể chậm một lần. Phương án khác: tăng bộ đếm trước khi ghi dự án (ghi dự án lỗi thì số bị nhảy một).
5. **Nhận biết hết chỗ theo nội dung thông báo lỗi** (`ENOSPC`, "No space left on device"): đây là cách Android và JVM báo, nhưng chưa thử trên máy thật hết chỗ. Đề xuất của AI: chấp nhận; lỗi không nhận ra vẫn là `WRITE_FAILED` (báo lỗi chung, vẫn không để bản dở). Kiểm trên máy thật hết chỗ khi có T-011, T-012 (dev).
6. **`existing-tests.py` chưa chạy xong lúc soạn bản đầu** (mục 4.1): phiên con sẽ cập nhật; nếu mục 4.1 vẫn ghi "chưa chạy xong" thì người duyệt yêu cầu chạy lại trước khi duyệt.

## 9. Quyết định duyệt

- **Quyết định:**
- **Người duyệt:** , **duyệt chéo:**
- **Ghi chú:**

## 10. Nhật ký thực hiện
