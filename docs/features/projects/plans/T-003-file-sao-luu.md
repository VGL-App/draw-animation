---
ticket: T-003
title: File sao lưu .frameloop: ghi và đọc một hay nhiều dự án
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

# PLAN T-003: FILE SAO LƯU .FRAMELOOP: GHI VÀ ĐỌC MỘT HAY NHIỀU DỰ ÁN

## 1. Tóm tắt

Thêm vào `:core:project` gói `backup`: ghi một hay nhiều dự án thường ra một file `.frameloop` (tệp ZIP có `manifest.json` mang số phiên bản định dạng sao lưu, rồi `project.json` và các tệp của từng dự án) qua `Uri` của bộ chọn file hệ thống, và đọc lại thành dự án mới. Khôi phục dựng dự án trong chỗ chuẩn bị `projects/.restoring-<mã>/` (ẩn khỏi danh sách và khỏi `deleteIncomplete()` của T-002), báo tiến độ theo tổng số khung, hủy bằng hủy coroutine, chỉ đổi tên vào chỗ thật khi mọi dự án đã ghi xong; lỗi tách loại (không phải file sao lưu hay hỏng, hết chỗ, bản app mới hơn, ghi lỗi). Kiểm chứng bằng unit test Robolectric trên thư mục tạm với `FileProjectStore` thật, `ContentResolver` thật với `Uri` dạng `file://`, file mẫu nhỏ trong `src/test/resources` và các luồng giả ném lỗi theo lượt.

## 2. Hiểu yêu cầu

Ticket xử lý phần dữ liệu của các kịch bản sau trong `features/projects/spec.md`; hộp thoại, thông báo ngắn và nút là T-013 (danh sách dự án) và T-017 (Cài đặt). Sự kiện `backup_export`, `backup_restore` do T-013, T-017 ghi; plan này trả số dự án cho tham số `project_count`.

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-8 | Bấm "Back up to a file" thì bộ chọn file mở, tên gợi ý là tên dự án với ".frameloop"; không hỏi quyền | Bộ chọn là việc của T-013 (`ActivityResultContracts.CreateDocument`). Plan này cho hàm thuần `BackupFileNames.forProject(name)` trả `"<tên>.frameloop"` (bỏ ký tự cấm trong tên file `/ \ : * ? " < > |` và ký tự điều khiển, thay bằng `_`; tên rỗng sau khi lọc thành `"Frameloop"`) và `BackupFileNames.forAll(date)` trả `"Frameloop-backup-YYYY-MM-DD.frameloop"` (spec settings KB-5), cùng hằng `BACKUP_MIME_TYPE = "application/octet-stream"`. Không cần quyền: ghi qua `ContentResolver.openOutputStream(uri, "wt")` của `Uri` mà bộ chọn trả về |
| KB-8 | Chọn thư mục và lưu thì file được ghi xong, báo "Backup saved" | `export(...)` trả `BackupExportResult.Success(projectCount)` chỉ sau khi luồng ZIP đã đóng thành công (`close()` của `ZipOutputStream` ghi mục lục cuối tệp, lỗi ở đây cũng là lỗi ghi). Thông báo là T-013 |
| KB-8 | Khôi phục file vừa sao lưu thì được dự án giống hệt: số khung, layer, âm thanh, khung hình, độ phân giải, tốc độ, nền, tên | Test khứ hồi: ghi một dự án đầy đủ (mọi trường của `TestProjects.full()` đổi thành dự án thường, ảnh nền, ô vẽ, âm thanh, ảnh vẽ theo, ảnh thu nhỏ), khôi phục, so `Project` khôi phục với bản gốc trừ `id`, `updatedAtMillis` (mới) và so từng tệp theo byte |
| KB-8 | Đóng bộ chọn không lưu thì không có gì xảy ra | Bộ chọn trả `null`, T-013 không gọi `export`. Không có phần của plan này |
| KB-8 | Ghi file thất bại (hết chỗ, mất quyền ghi) thì báo lỗi; dự án trên máy không bị ảnh hưởng | `export` không ném `IOException`, `SecurityException`: trả `Failure(NO_SPACE)` hay `Failure(WRITE_FAILED)`; `export` chỉ đọc kho, không ghi gì vào `projects/` (test so cây thư mục trước và sau) |
| KB-8 | Dự án lớn thì có dấu hiệu đang ghi | `export` là hàm `suspend` an toàn trên luồng chính (chạy trên `ioDispatcher`); dấu hiệu là T-013. Không có tiến độ khi ghi (spec không đòi) |
| KB-9 | Chọn file hợp lệ thì hộp thoại có tên dự án, "<đã xong> of <tổng> frames" và phần trăm | `restore(uri, onProgress)` gọi `onProgress(RestoreProgress(summary, doneFrames = 0))` ngay sau khi đọc `manifest.json`, trước khi ghi tệp nào; `summary` có `projectCount`, `projectName` (tên dự án khi file có một dự án, `null` khi nhiều) và `totalFrames`; sau đó gọi lại mỗi khi `doneFrames` tăng, tăng dần, lần cuối bằng `totalFrames` trước khi trả kết quả. Phần trăm do T-013 tính |
| KB-9 | File nhiều dự án: hộp thoại ghi số dự án, tiến độ trên tổng số khung của mọi dự án; xong thì mọi dự án đứng đầu | `totalFrames` là tổng `frames.size` của mọi dự án trong file (ghi sẵn trong `manifest.json`, kiểm lại khi đọc từng `project.json`); mọi dự án khôi phục có `updatedAtMillis = clock()` lúc bắt đầu đổi tên vào chỗ thật, nên đứng đầu danh sách của `ProjectRepository` (T-002 sắp giảm dần theo `updatedAtMillis`) |
| KB-9 | Khôi phục xong thì dự án mới đứng đầu và mở được, giống hệt | Trả `RestoreResult.Success(projectIds)` sau khi mọi dự án đã ở chỗ thật và đã gọi `ProjectRepository.refresh()` (T-002) để danh sách tải lại; `store.read(id)` của mỗi mã trả `Ok` |
| KB-9 | Bấm "Cancel" thì dừng, không có dự án dở trong danh sách hay trên máy | T-013 hủy coroutine đang chạy `restore`. Khi hủy (lần đọc `Uri` đang chờ thì khi lần đọc đó trả về), `restore` xóa chỗ chuẩn bị trong `withContext(NonCancellable)` rồi ném lại `CancellationException`; không mã nào mới trong `store.ids()`, không thư mục nào mới dưới `projects/` |
| KB-9 | Khôi phục từ trạng thái trống thì màn chuyển sang có dự án | Nhờ `refresh()`; màn hình là T-009, T-013 |
| KB-9 | File của dự án còn trong danh sách thì tạo dự án mới, không ghi đè (câu hỏi 10) | Mỗi dự án khôi phục nhận `ProjectId.random()` mới; tên giữ nguyên như trong file (cho trùng tên) |
| KB-9 | Rời app rồi quay lại thì khôi phục vẫn tiếp tục; app bị đóng thì không còn dự án dở (câu hỏi 11) | Khôi phục chạy trong phạm vi coroutine do T-013 chọn (không gắn vào vòng đời màn); plan này bảo đảm phần dở chỉ nằm trong `.restoring-*`, không bao giờ trong thư mục mã dự án thiếu tệp, và lần mở sau `DefaultProjectRepository` (T-002) dọn mọi `.restoring-*` không thuộc lần khôi phục đang chạy trong tiến trình (`deleteAbandonedImports()`) |
| KB-9 | Máy không đủ chỗ thì dừng, không để dự án dở, báo lỗi hết chỗ (câu hỏi 12) | `IOException` khi ghi vào chỗ chuẩn bị mà `isOutOfSpace(e)` (của T-002) đúng thì xóa chỗ chuẩn bị, trả `Failure(NO_SPACE)` |
| KB-10 | File không phải file sao lưu hay hỏng thì báo lỗi "Can't open this file" | `Failure(NOT_A_BACKUP)` cho: không phải ZIP, không có `manifest.json` ở mục đầu, `manifest.json` sai, `app` khác `"frameloop"`, `project.json` không đọc được hay sai luật của `ProjectFormat`, tệp thiếu thứ tự, mục quá lớn, số khung trong `manifest.json` không khớp, đọc `Uri` lỗi giữa chừng (tệp cụt, mạng của Drive đứt), không mở được `Uri`. Nút "Close", "Choose another file" là T-013 |
| KB-10 | Thất bại giữa chừng thì không có dự án dở, dự án khác không đổi | Mọi lỗi sau khi bắt đầu ghi đều xóa chỗ chuẩn bị; test so `store.ids()` và nội dung một dự án có sẵn trước và sau |
| KB-10 | File của bản app mới hơn thì báo lỗi riêng, không tạo dự án dở (câu hỏi 12) | `Failure(NEWER_VERSION)` khi `backupFormatVersion` trong `manifest.json` lớn hơn `CURRENT_BACKUP_FORMAT_VERSION = 1`, hoặc khi một `project.json` trong file cho `ProjectReadResult.NewerVersion` |
| Ticket | Không chứa dự án bài học | `export` bỏ dự án `ProjectKind.Lesson` (và dự án không đọc được); khi đọc, dự án bài học trong file (chỉ có ở file do chương trình khác tạo) bị bỏ qua, số khung của nó vẫn cộng vào tiến độ để thanh tiến độ tới 100 % |

Thiết kế tham chiếu: Không có thiết kế riêng cho phần này (docs/ops/design-workflow.md); hộp thoại `restoring`, `restore-error` thuộc T-013.

ADR liên quan: ADR-0001 (phụ thuộc module: `:core:project` chỉ phụ thuộc core), ADR-0019 (log không chứa tên dự án, mã hay đường dẫn), ADR-0024 (feature không làm chế độ ngoại tuyến; ở đây chỉ đọc ghi `Uri` do người chọn).

## 3. Giải pháp

### 3.1. Cách làm

Plan này dựa trên **plan T-002 phiên bản 1, còn `draft`, chưa duyệt** (nhánh `claude/plan-T-002-kho-du-an`, PR nháp #17): dùng `ProjectRepository.refresh()`, `isOutOfSpace(e)` trong `repository/StorageErrors.kt`, `DefaultProjectRepository.cleanUpOnce()` và ràng buộc ở mục 6 của plan T-002 (khôi phục dựng dự án trong chỗ chuẩn bị có tên bắt đầu bằng `.`, rồi đổi tên sang thư mục mã dự án với `project.json` đã có sẵn). T-003 chạy sau khi T-002 đã merge; nếu người đổi plan T-002 thì plan này phải xem lại (mục 6, mục 8).

Mọi tệp mới nằm trong gói `com.vgl.draw.animation.core.project.backup` của `:core:project`, trừ phần thêm vào gói `storage` và Hilt module. Làm theo mẫu của T-001 (`FileProjectStore`: lớp cài đặt `internal`, nhận dispatcher và logger qua hàm dựng) và của T-002 (`ProjectResult`, lỗi trả về chứ không ném, `isOutOfSpace`).

**Định dạng file `.frameloop`** (`BackupFormat.kt`, `internal object BackupFormat`): tệp ZIP (`java.util.zip`, có sẵn trong Android và JDK, không thêm thư viện), các mục theo đúng thứ tự:

```
manifest.json                         mục đầu tiên
projects/<i>/project.json             i = 0, 1, ...: dự án thứ i, đúng chuỗi ProjectFormat.encode (có "formatVersion")
projects/<i>/<relativePath>           các tệp của dự án i, theo ProjectFile.relativePath, sau project.json của nó
```

`manifest.json`: `{"app":"frameloop","backupFormatVersion":1,"projects":[{"name":"...","frameCount":N}, ...]}`. Mục lục chi tiết của từng dự án là `project.json` của nó; `manifest.json` chỉ có những gì hộp thoại cần trước khi đọc dự án (tên, số khung). Quy tắc phiên bản như `ProjectFormat`: đổi bố cục tệp ZIP hay `manifest.json` thì tăng `CURRENT_BACKUP_FORMAT_VERSION` và thêm file mẫu `backup-v<N>.frameloop`; file mẫu cũ không sửa. Định dạng của `project.json` bên trong đi theo `ProjectFormat.CURRENT_FORMAT_VERSION` (đọc qua `ProjectFormat.decode`, nên file sao lưu cũ đi qua `migrate` như dự án trên máy). Đổi định dạng sau này là điểm chặn 9.

Thứ tự tệp của dự án i khi ghi: `thumbnail.png` (nếu có), `background.<ext>` (nếu nền ảnh), `sounds/*`, `reference/*`, rồi `cells/<frameId>/<layerId>.png` theo thứ tự khung trong `project.frames`, trong một khung theo thứ tự `project.layers`; ô không có tệp (ô trống) không có mục. Mục dùng `ZipEntry.DEFLATED` mặc định.

**Danh sách tệp của một dự án** (`storage/ReferencedFiles.kt`, `internal fun Project.referencedFiles(): List<ProjectFile>`): đúng thứ tự trên: `Thumbnail`; `Background(ext)` khi `background is Background.Photo`; `Sound(clip.id, clip.fileExtension)` cho mọi âm thanh; `ReferenceImage(i, reference.fileExtension)` cho `i` từ 0 tới `reference.frameCount - 1`; `Cell(frame.id, layer.id)` cho mọi khung × layer. Plan T-002 có hàm cùng nội dung tên `referencedFiles(source)` riêng trong `DefaultProjectRepository` (nhân bản); plan này không sửa phần nhân bản (mục 8, câu 3).

**Giao diện công khai** (`ProjectBackup.kt`):

```kotlin
interface ProjectBackup {
    /** Writes the given user projects (lessons and unreadable ones skipped) to [target]. Never throws IOException. */
    suspend fun export(target: Uri, ids: List<ProjectId>): BackupExportResult
    /** Every readable user project, most recently updated first (the order of ProjectRepository.projects). */
    suspend fun exportAll(target: Uri): BackupExportResult
    /**
     * Restores every project of [source] as new projects. [onProgress] runs on the IO dispatcher, first with
     * doneFrames = 0 once the manifest is read, last with doneFrames = totalFrames; it must be quick and thread safe
     * (for example update a MutableStateFlow). Cancelling the calling coroutine stops the restore and leaves nothing
     * behind. Never throws IOException.
     */
    suspend fun restore(source: Uri, onProgress: suspend (RestoreProgress) -> Unit): RestoreResult
}

sealed interface BackupExportResult {
    data class Success(val projectCount: Int) : BackupExportResult
    data class Failure(val reason: BackupExportFailure) : BackupExportResult
}
enum class BackupExportFailure { NO_SPACE, WRITE_FAILED, NOTHING_TO_EXPORT }

data class BackupSummary(val projectCount: Int, val projectName: String?, val totalFrames: Int)
data class RestoreProgress(val summary: BackupSummary, val doneFrames: Int)

sealed interface RestoreResult {
    data class Success(val projectIds: List<ProjectId>) : RestoreResult
    data class Failure(val reason: RestoreFailure) : RestoreResult
}
enum class RestoreFailure { NOT_A_BACKUP, NEWER_VERSION, NO_SPACE, WRITE_FAILED }

object BackupFileNames {
    const val EXTENSION = "frameloop"
    const val BACKUP_MIME_TYPE = "application/octet-stream"
    fun forProject(name: String): String
    fun forAll(date: java.time.LocalDate): String
}
```

Tên kiểu có thể đổi nhẹ khi viết test (bước 3) nếu ktlint hay trình biên dịch yêu cầu, giữ nghĩa. `BACKUP_MIME_TYPE` là `application/octet-stream` vì `.frameloop` không có kiểu MIME đăng ký: bộ chọn mở file (`OpenDocument`) của T-013 lọc theo kiểu này hay `*/*` (mục 8, câu 5).

**Luồng `Uri`** (`BackupStreams.kt`): `internal interface BackupStreams { fun openRead(uri: Uri): InputStream; fun openWrite(uri: Uri): OutputStream }` và `internal class ContentResolverBackupStreams(resolver: ContentResolver)`: `openInputStream(uri)`, `openOutputStream(uri, "wt")` (chế độ `"wt"` để cắt nội dung cũ khi người chọn ghi đè một file có sẵn: chế độ `"w"` không cắt trên một số nhà cung cấp từ Android 10); `null` thành `FileNotFoundException`. Không cần quyền: `Uri` của bộ chọn mang quyền đọc hay ghi tạm cho app (chạy thử, mục 4.1).

**Kho tệp** (`storage/ProjectStore.kt`, `storage/FileProjectStore.kt`): thêm vào interface

```kotlin
/** Starts staging projects for a restore in a folder `.restoring-<token>/`, hidden from ids() and deleteIncomplete(). */
suspend fun beginImport(): ProjectImport
/** Deletes `.restoring-*` folders of imports not open in this process (the app closed during a restore). */
suspend fun deleteAbandonedImports(): Int

interface ProjectImport {
    /** Same contract as ProjectStore.writeFile, inside the staging folder. */
    suspend fun writeFile(id: ProjectId, file: ProjectFile, bytes: ByteArray)
    /** Same contract as ProjectStore.write, inside the staging folder. Callers write project.json last. */
    suspend fun write(project: Project)
    /** Moves every staged project that has a project.json into place, in the order first written. Throws IOException. */
    suspend fun commit(): List<ProjectId>
    /** Deletes the staging folder. Never throws (logs a warning). Safe after commit and when called twice. */
    suspend fun discard()
}
```

`FileProjectStore`: `beginImport()` tạo `root/.restoring-<UUID ngẫu nhiên>/` trên `ioDispatcher`, ghi mã vào tập `activeImports` (`ConcurrentHashMap.newKeySet()`), trả một `FileProjectImport` (lớp `private` trong cùng tệp). `writeFile`, `write` của nó dùng cùng `writeAtomically`, cùng `ProjectFormat.encode` và cùng kiểm `checkInsideRoot` như kho (thư mục chuẩn bị nằm trong `root`). `commit()`: với mỗi mã theo thứ tự ghi, trong khóa của mã đó, kiểm `projects/<mã>` chưa có (có thì `IOException`: mã ngẫu nhiên trùng), `File.renameTo` thư mục `.restoring-<token>/<mã>` sang `root/<mã>` (cùng hệ tệp, đổi tên nguyên tử; `project.json` đã có sẵn nên `deleteIncomplete()` không bao giờ thấy thư mục dở); đổi tên lỗi thì `IOException`. Mã nào đã chuyển thì trả trong danh sách; lỗi giữa chừng để lại các mã đã chuyển cho người gọi xóa. `discard()`: `deleteRecursively` thư mục chuẩn bị, bỏ mã khỏi `activeImports`; lỗi chỉ ghi log `warn`. `deleteAbandonedImports()`: xóa mọi con của `root` có tên bắt đầu bằng `.restoring-` mà token không có trong `activeImports`; trả số đã xóa; xóa lỗi thì `IOException`. Các hàm cũ không đổi; `ids()` đã bỏ thư mục bắt đầu bằng `.`, `delete()` chỉ dọn `.deleting-*`.

**Hilt và vòng đời dọn dẹp:** `DefaultProjectRepository.cleanUpOnce()` (T-002) gọi thêm `store.deleteAbandonedImports()` ngay sau `store.deleteIncomplete()`, cùng cách xử lý lỗi (`IOException` chỉ ghi log `warn`, danh sách vẫn tải), cùng log số đã dọn khi lớn hơn 0. Không đổi gì khác của tệp T-002.

**Ghi** (`DefaultProjectBackup.kt`, `internal class DefaultProjectBackup(store: ProjectStore, repository: ProjectRepository, streams: BackupStreams, clock: () -> Long, ioDispatcher: CoroutineDispatcher, logger: AppLogger)`):

- `export(target, ids)`: trên `ioDispatcher`. Đọc từng mã bằng `store.read`; giữ `Ok` của dự án thường, bỏ phần còn lại (bài học, `Corrupt`, `NewerVersion`, `NotFound`). Không còn dự án nào thì trả `Failure(NOTHING_TO_EXPORT)` **trước khi mở** `target`. Mở `streams.openWrite(target)`, bọc `ZipOutputStream`, ghi `manifest.json` rồi từng dự án theo thứ tự `ids`: `project.json` (`ProjectFormat.encode`), rồi mỗi tệp của `referencedFiles()` mà `store.readFile` khác `null`. Gọi `ensureActive()` giữa các mục. Đóng luồng (`use`). `IOException` hay `SecurityException` ở bất kỳ bước nào (mở, ghi, đọc tệp của kho, đóng) thành `Failure(NO_SPACE)` khi `isOutOfSpace(e)`, không thì `Failure(WRITE_FAILED)`. Thành công trả `Success(số dự án đã ghi)`. Không xóa file dở ở chỗ người chọn (mục 8, câu 4). Không ghi gì vào `projects/`.
- `exportAll(target)`: lấy `store.ids()`, đọc, giữ dự án thường đọc được, sắp giảm dần theo `updatedAtMillis` (bằng nhau theo `id.value`), rồi như `export`.

**Đọc** (`restore(source, onProgress)`), cả hàm trên `ioDispatcher`, kể cả lời gọi `onProgress` (T-013 cập nhật một `MutableStateFlow`, an toàn đa luồng):

1. Mở `streams.openRead(source)` (`IOException`, `SecurityException` thành `Failure(NOT_A_BACKUP)`), bọc `ZipInputStream`.
2. Mục đầu phải là `manifest.json` (`nextEntry == null` hay tên khác: `NOT_A_BACKUP`). Đọc tối đa `MAX_MANIFEST_BYTES = 1 MB`; giải mã bằng `kotlinx.serialization.json` với `ignoreUnknownKeys = true`; sai JSON, thiếu trường, `app != "frameloop"`: `NOT_A_BACKUP`. `backupFormatVersion > CURRENT_BACKUP_FORMAT_VERSION`: `NEWER_VERSION`; nhỏ hơn 1: `NOT_A_BACKUP`. `projects` rỗng, `frameCount < 1`, hơn `MAX_PROJECTS = 10_000` dự án: `NOT_A_BACKUP`.
3. Gọi `onProgress(RestoreProgress(summary, 0))`. `summary.projectName` là tên trong `manifest.json` khi chỉ một dự án.
4. `store.beginImport()`. Từ đây mọi đường ra (lỗi, hủy) đi qua một `try/finally` gọi `import.discard()` trong `withContext(NonCancellable)`; thành công cũng gọi (thư mục chuẩn bị đã rỗng).
5. Đọc tuần tự các mục. Tên mục phải có dạng `projects/<i>/<phần còn lại>` với `i` là chỉ số dự án hiện tại hay chỉ số kế tiếp; dự án mới phải mở đầu bằng `project.json`. `project.json` (tối đa `MAX_PROJECT_JSON_BYTES = 4 MB`) qua `ProjectFormat.decode`: `NewerVersion` thì `NEWER_VERSION`, `Corrupt` thì `NOT_A_BACKUP`, `Ok` mà `frames.size` khác `frameCount` của `manifest.json` thì `NOT_A_BACKUP`. Dự án bài học: bỏ cả dự án (các mục của nó bị đọc qua, không ghi), số khung của nó đã có trong `totalFrames` nên vẫn được cộng vào `doneFrames` để thanh tiến độ tới 100 %. Các mục tệp: tra `<phần còn lại>` trong bảng `referencedFiles().associateBy { it.relativePath }` của dự án hiện tại; không có trong bảng thì đọc qua và bỏ (không bao giờ dùng tên mục làm đường dẫn); có thì đọc tối đa `MAX_FILE_BYTES = 64 MB` (vượt: `NOT_A_BACKUP`) và `import.writeFile(newId, file, bytes)`. Mục trùng tên, dự án đi lùi chỉ số, thiếu dự án so với `manifest.json`: `NOT_A_BACKUP`.
6. Tiến độ: `doneFrames` tăng khi gặp ô của một khung đứng sau trong `project.frames` (mọi khung trước nó tính là xong) và khi dự án kết thúc (mọi khung còn lại của nó tính là xong); `onProgress` chỉ gọi khi giá trị tăng.
7. Dự án kết thúc (mục của dự án sau, hay hết tệp) thì ghi `project.json` của nó vào chỗ chuẩn bị bằng `import.write(project.copy(id = newId, updatedAtMillis = now))`, `newId = ProjectId.random()`, `now = clock()` đọc một lần ở bước 4. Các trường khác giữ nguyên (tên, `createdAtMillis`, khung, layer, âm thanh, nền, lớp vẽ theo, máy quay, giấy hành tây).
8. Hết tệp: số dự án đã đọc khác `manifest.json` thì `NOT_A_BACKUP`. Không còn dự án thường nào (file chỉ có bài học) thì `NOT_A_BACKUP`. `ensureActive()` lần cuối, rồi `import.commit()` trong `withContext(NonCancellable)`; `commit` ném `IOException` thì `store.delete(id)` từng mã đã chuyển (trong `try` riêng, lỗi chỉ ghi log) và trả `NO_SPACE`/`WRITE_FAILED`. Thành công: `repository.refresh()`, log `info` số dự án, trả `Success(ids)`.
9. Phân loại lỗi trong bước 5 tới 8: `ZipException`, `EOFException` và mọi `IOException` khi **đọc** luồng nguồn thành `NOT_A_BACKUP` (bọc luồng nguồn trong một `InputStream` đánh dấu lỗi đến từ nguồn, để không lẫn với lỗi ghi); `IOException` khi **ghi** vào chỗ chuẩn bị thành `NO_SPACE` khi `isOutOfSpace(e)`, không thì `WRITE_FAILED`. `IllegalArgumentException` từ `ProjectFile` (đuôi tệp sai trong `project.json`) không xảy ra vì `ProjectFormat.decode` đã kiểm; nếu có thì `NOT_A_BACKUP`. `CancellationException` luôn ném lại sau khi dọn.
10. `ensureActive()` trước mỗi mục và sau mỗi `onProgress`, nên hủy có hiệu lực trong vòng một mục (tối đa một tệp 64 MB). Lần đọc đang chờ luồng nguồn (mạng của Drive chậm) không bị ngắt: hủy có hiệu lực khi lần đọc đó trả về (mục 8, câu 9).

**Log** (thẻ `Backup`, ADR-0019): `info` khi ghi xong (`"Export done: n projects"`), khôi phục xong (`"Restore done: n projects"`), khi bị hủy (`"Restore cancelled"`); `warn` khi lỗi: loại lỗi và tên lớp ngoại lệ (`e::class.simpleName`), không truyền ngoại lệ (lời của nó có thể chứa đường dẫn có mã dự án hay tên file người dùng). Không ghi tên dự án, mã dự án, `Uri`.

**Hilt** (`di/BackupModule.kt`, tệp mới, không sửa `ProjectModule.kt`): `@Provides @Singleton ProjectBackup = DefaultProjectBackup(store, repository, ContentResolverBackupStreams(context.contentResolver), System::currentTimeMillis, Dispatchers.IO, logger)`.

**Người dùng sau này:** T-013 dùng `export(uri, listOf(id))`, `restore`, `BackupFileNames.forProject`; T-017 dùng `exportAll`, `restore`, `BackupFileNames.forAll`. Ghi `backup_export`/`backup_restore` với `project_count` từ kết quả là việc của hai ticket đó.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ProjectStore.kt` | Thêm `beginImport()`, `deleteAbandonedImports()`, interface `ProjectImport` kèm KDoc; thêm `.restoring-<token>/` vào sơ đồ thư mục của KDoc lớp |
| Sửa | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/FileProjectStore.kt` | Cài đặt `beginImport()`, `FileProjectImport` (lớp `private`), `deleteAbandonedImports()`, tập `activeImports`, hằng `RESTORING_PREFIX` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ReferencedFiles.kt` | `Project.referencedFiles()` |
| Sửa | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/repository/DefaultProjectRepository.kt` | (tệp của T-002) `cleanUpOnce()` gọi thêm `store.deleteAbandonedImports()` sau `deleteIncomplete()`, cùng cách xử lý lỗi và log |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/backup/ProjectBackup.kt` | Interface `ProjectBackup`, `BackupExportResult`, `BackupExportFailure`, `BackupSummary`, `RestoreProgress`, `RestoreResult`, `RestoreFailure` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/backup/BackupFileNames.kt` | `BackupFileNames` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/backup/BackupFormat.kt` | Hằng phiên bản, tên mục, `Manifest` (`@Serializable`), mã hóa và giải mã `manifest.json`, giới hạn kích thước |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/backup/BackupStreams.kt` | `BackupStreams`, `ContentResolverBackupStreams` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/backup/DefaultProjectBackup.kt` | Lớp cài đặt `internal` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/di/BackupModule.kt` | Hilt module của `ProjectBackup` |
| Sửa | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/storage/FileProjectStoreTest.kt` | Thêm các test `import_*`, `deleteAbandonedImports_*` (mục 4); không đổi test cũ |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/storage/ReferencedFilesTest.kt` | Test danh sách tệp |
| Sửa | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/repository/DefaultProjectRepositoryTest.kt` | (tệp của T-002) Thêm `projects_firstCollect_deletesAbandonedImports`, `projects_abandonedImportCleanupFails_stillLoads`; không đổi test cũ |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/backup/DefaultProjectBackupTest.kt` | Test ghi, đọc, lỗi, hủy, tiến độ (mục 4) |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/backup/BackupFormatTest.kt` | Test `manifest.json` và file mẫu |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/backup/BackupFileNamesTest.kt` | Test tên file gợi ý |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/backup/ContentResolverBackupStreamsTest.kt` | Test luồng qua `ContentResolver` thật với `Uri.fromFile` |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/backup/BackupTestDoubles.kt` | `FileBackupStreams` (map `Uri` sang tệp trong thư mục tạm), `FailingOutputStream` (ném `IOException(message)` sau N byte), `FailingInputStream` (ném sau N byte), `GatedInputStream` (dự phòng của mục 3.4: tới byte thứ N thì `reached.countDown()` rồi chờ `release` là `CountDownLatch` tối đa 5 giây), `FailingImportStore` (bọc `FileProjectStore` bằng `ProjectStore by delegate`; `beginImport()` trả một `ProjectImport` bọc ném `IOException(message)` ở lượt `writeFile` thứ N hay khi `commit`), `RefreshCountingRepository` (`ProjectRepository` giả đếm `refresh()`), `QueueDispatcher` (xếp việc tới khi test gọi `runAll()`, như T-004), dựng tệp ZIP mẫu trong bộ nhớ (`zipOf(vararg entries: Pair<String, ByteArray>)`) |
| Tạo | `core/project/src/test/resources/backup-v1.frameloop` | File mẫu nhỏ: hai dự án thường (một nền màu ba khung hai layer có ô trống, một nền ảnh có âm thanh và ảnh vẽ theo), tệp ảnh là vài byte cố định; hợp đồng định dạng v1, không sửa sau khi duyệt |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/backup/BackupSampleWriter.kt` | Hàm thuần `writeSampleV1(out: OutputStream)` mô tả nội dung `backup-v1.frameloop` theo từng mục, dùng ở `BackupFormatTest.sampleV1_matchesWriter` để file mẫu luôn đọc được bằng mắt qua code |
| Sửa | `CLAUDE.md` | Dòng `:core:project` của bảng module: thêm "sao lưu và khôi phục file `.frameloop`", như T-001, T-002, T-004 ghi phần của mình; không sửa dòng khác |

Nơi khác đọc tệp mà plan sửa:

- `ProjectStore.kt`: mọi lớp cài đặt hay bọc nó (`grep -rn "ProjectStore" --include=*.kt`, trên nhánh chính và trong plan T-002) là `FileProjectStore` (sửa trong plan), lớp bọc `object : ProjectStore by fileStore` trong `ProjectThumbnailsTest.kt`, `StoreFrameRendererTest.kt` và `FailingProjectStore` của T-002 (`ProjectStore by delegate`): ủy quyền bằng `by`, tự có hàm mới, không phải sửa.
- `DefaultProjectRepository.kt`: chỉ `ProjectRepositoryModule.kt` (T-002) tạo nó; hàm dựng không đổi.
- `CLAUDE.md` (`grep -rln "CLAUDE.md" scripts/ .github/ */src/test */src/androidTest core/*/src/test`):
  - `app/src/androidTest/kotlin/com/vgl/draw/animation/AppLaunchTest.kt`, `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/NativeFullScreenOverlayScreenshotTest.kt`, `scripts/local-setup.sh`, `scripts/run-watchdog.py`: chỉ nhắc tên file trong chú thích hay lời nhắn; không ảnh hưởng.
  - `scripts/tests/run.sh`: đọc dòng lệnh `git status --short -- '...'` ở mục Lệnh và dòng `export ANDROID_HOME`; plan chỉ sửa một dòng của bảng module, không đổi hai dòng đó.
- `src/test/resources/`: tệp mới `backup-v1.frameloop` chỉ `BackupFormatTest`, `DefaultProjectBackupTest` đọc; `project-v1.json` không đổi.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có (không có giao diện; cờ `feature_projects_enabled` của T-006 do màn hình dùng).
- Quảng cáo: Không chạm (`restore_mrec` thuộc T-013).
- Sự kiện phân tích: Không chạm. `backup_export`, `backup_restore` do T-013, T-017 ghi, với `project_count` lấy từ `BackupExportResult.Success.projectCount` và `RestoreResult.Success.projectIds.size`.
- File R2: Không chạm.
- Dữ liệu lưu trên máy: không thêm khóa DataStore; thư mục tạm `projects/.restoring-<token>/` chỉ tồn tại trong lúc khôi phục. Định dạng file `.frameloop` là hợp đồng mới với file người dùng giữ ngoài app (spec mục 6: file nằm ở nơi người dùng chọn, app không giữ bản sao hay đường dẫn).
- **Quyền truy cập:** Không thêm. `ContentResolver.openInputStream`/`openOutputStream` trên `Uri` do bộ chọn file hệ thống (`ACTION_CREATE_DOCUMENT`, `ACTION_OPEN_DOCUMENT`, có từ API 19) trả về dùng quyền tạm mà bộ chọn cấp cho `Uri` đó, không cần quyền trong manifest ở mọi phiên bản từ minSdk 26 tới targetSdk 36 (spec mục 6). Không thêm: `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `MANAGE_EXTERNAL_STORAGE`; không gọi `takePersistableUriPermission` (app không giữ `Uri`). Ghi đọc `filesDir` qua `FileProjectStore` như T-001.
- **Thư viện và SDK:** Không thêm. `java.util.zip` (`ZipInputStream`, `ZipOutputStream`, `ZipEntry`, `ZipException`) có trong thư viện chuẩn của Android và JDK; `android.content.ContentResolver`, `android.net.Uri` của SDK Android; `kotlinx.serialization.json` (`libs.kotlinx.serialization.json`, đã có trong `core/project/build.gradle.kts`) cho `manifest.json`; `kotlinx-coroutines` (`NonCancellable`, `ensureActive`) đã có trên classpath của `:core:project`. Test: Robolectric, `androidx.test.core` (đã có trong `testImplementation` của module), `kotlinx-coroutines-test` (đã dùng ở T-001). `core/project/build.gradle.kts` không đổi.

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** test hủy giữa chừng (`restore_cancelledMidway_leavesNothing`, `restore_cleanupDuringRestore_doesNotTouchStaging`) hủy hay chen việc từ trong `onProgress` trên `StandardTestDispatcher(testScheduler)` của `runTest`; ghi chép L-001 cho thấy test dispatcher có thể hỏng theo cách khó đoán trước, và chưa chạy thử phần này.
- **Điều kiện chuyển:** hai test đó không chạy được vì lỗi của khung test (ví dụ `Detected use of different schedulers`, treo tới hết giờ của `runTest`) sau 2 lần sửa phần dựng.
- **Cách làm thay:** chạy hai test đó bằng `runBlocking` với `ioDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()` (đóng ở `@After`) và `GatedInputStream` đặt ở byte cuối của dự án đầu (vị trí tính từ file mẫu); test chờ `reached.await(5, SECONDS)`, kiểm trong `.restoring-*` đã có tệp, rồi hủy (hay gọi dọn dẹp), mở `release`, `withTimeout(5_000) { job.join() }`. Câu khẳng định giữ nguyên. Không có file mới (cùng `DefaultProjectBackupTest.kt`, `BackupTestDoubles.kt`).

### 3.5. Hành vi đã có bị đổi

Plan sửa hai tệp Kotlin ngoài thư mục test: `ProjectStore.kt`, `FileProjectStore.kt` (chỉ thêm hàm, hàm cũ không đổi) và `DefaultProjectRepository.kt` của T-002 (tệp chưa có trên nhánh chính). `scripts/existing-tests.py` (2026-11-22, nhánh từ commit da87fc5) bỏ qua `DefaultProjectRepository.kt` (chưa có trên nhánh chính) và tìm 125 test: 8 Cần xem trước, 117 Nên xem. Đã đọc nhóm Cần xem trước: 6 test của quảng cáo (`AdGalleryScreenTest`, `InListAdsTest`, `NativeAdCardTest`, `NativeAdFontScaleTest`) chỉ trùng tên chung `read`, không dùng `ProjectStore`; `ProjectThumbnailsTest.update_sizeFollowsAspectRatio` và `StoreFrameRendererTest.export720pOf1080pProject_scalesCells` gọi `ProjectStore.write`/`writeFile`, mà plan không đổi. Nhóm Nên xem: các test của `:core:ads`, `:core:config`, `:app` và test khác chỉ trùng tên chung (`read`, `write`, `ids`); các test của `:core:project` trong đó (`FileProjectStoreTest`, `ProjectThumbnailsTest`, `StoreFrameRendererTest`) kiểm các hàm cũ của kho, không đổi. Test của T-002 cho `DefaultProjectRepository` chưa có trên nhánh chính, đọc theo plan T-002 mục 4.

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| `ids()` bỏ thư mục bắt đầu bằng `.` | Không đổi; thêm thư mục `.restoring-*` cũng bị bỏ | `FileProjectStoreTest` (các test `ids_*`, `delete_*`) | Giữ |
| `delete(id)` dọn `.deleting-*` của dự án khác | Không đổi: `delete` không chạm `.restoring-*` | `FileProjectStoreTest` (các test `delete_*`) | Giữ; thêm `import_deleteOfOtherProject_keepsStagingFolder` |
| `cleanUpOnce()` (T-002) chỉ gọi `deleteIncomplete()` | Gọi thêm `deleteAbandonedImports()`, lỗi chỉ ghi log | `DefaultProjectRepositoryTest` của T-002 (các test dọn dẹp, chưa có trên nhánh chính) | Giữ: test của T-002 đếm lần gọi `deleteIncomplete` và kiểm thư mục thiếu `project.json` bị xóa, không kiểm thư mục `.`; thêm hai test mới (mục 4) |
| Lớp bọc `ProjectStore by ...` trong test | Tự có hai hàm mới qua `by` | `ProjectThumbnailsTest`, `StoreFrameRendererTest` | Giữ, không phải sửa |

## 4. Kiểm thử

Mọi test là unit test JVM trong `:core:project`, chạy bằng Robolectric (`@RunWith(RobolectricTestRunner::class)`, vì `Uri`, `ContentResolver`) trên `TemporaryFolder` với `FileProjectStore` thật (`ioDispatcher` là `StandardTestDispatcher(testScheduler)` của chính `runTest`, theo L-001: không dùng dispatcher có scheduler riêng bên trong `runTest`), `FileBackupStreams` hay `ContentResolverBackupStreams` thật với `Uri.fromFile`, `RecordingAppLogger`, đồng hồ giả trả `1_000_000L`. Dự án mẫu: `TestProjects.full().copy(kind = ProjectKind.Regular)` (có ảnh nền, âm thanh, lớp vẽ theo) và các dự án nhỏ dựng trong test; tệp ảnh là mảng byte khác nhau cho từng tệp (`"cell-<frame>-<layer>".toByteArray()`), vì kho chỉ chép byte.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| KB-8, giống hệt sau khi khôi phục (một dự án) | Unit test | `DefaultProjectBackupTest.exportThenRestore_oneProject_identical`: ghi dự án đầy đủ cùng mọi tệp của `referencedFiles()` trừ ô (frame 2, layer 1) để trống; `export(uri, listOf(id))` là `Success(1)`; `restore` là `Success([newId])`, `newId != id`; `store.read(newId)` là `Ok(p)` với `p == original.copy(id = newId, updatedAtMillis = 1_000_000L)`; mỗi tệp của `referencedFiles()` có cùng byte, ô trống vẫn `null` |
| KB-9, nhiều dự án, tiến độ trên tổng số khung, đứng đầu | Unit test | `exportAllThenRestore_threeProjects_allRestoredWithTotalFrames`: ba dự án thường (1, 3, 5 khung) và một dự án bài học; `exportAll` là `Success(3)`; xóa ba dự án gốc; `restore` gọi `onProgress` lần đầu với `BackupSummary(3, null, 9)` và `doneFrames = 0`, các giá trị `doneFrames` tăng dần và lần cuối là 9; kết quả 3 mã mới, tên và số khung đúng như bản gốc, `updatedAtMillis == 1_000_000L` cho cả ba; dự án bài học không được khôi phục |
| KB-9, tiêu đề một dự án | Unit test | `restore_oneProject_summaryHasName`: lần `onProgress` đầu có `projectName == "Cat"`, `projectCount == 1`, `totalFrames == frames.size`, và chưa có tệp nào trong thư mục chuẩn bị lúc đó (đếm tệp dưới `.restoring-*` trong callback là 0) |
| KB-9, tiến độ tăng theo khung | Unit test | `restore_progress_increasesPerFrame`: dự án 4 khung, 1 layer, mọi ô có tệp: dãy `doneFrames` là `[0, 1, 2, 3, 4]`; dự án 4 khung chỉ ô khung 3 có tệp: dãy là `[0, 2, 4]` (không phát giá trị trùng) |
| KB-9, xong thì danh sách tải lại | Unit test | `restore_success_refreshesRepositoryOnce`: `RefreshCountingRepository.count == 1` sau `Success`; sau mỗi loại `Failure` ở các test lỗi dưới thì `count == 0` |
| KB-9, câu 10: không ghi đè dự án đang có | Unit test | `restore_projectStillPresent_createsNewOneAlongside`: khôi phục file của dự án vẫn còn; `store.ids()` có cả mã cũ và mã mới; dự án cũ đọc ra đúng như trước; hai dự án cùng tên |
| KB-9, "Cancel": không có dự án dở | Unit test | `restore_cancelledMidway_leavesNothing`: file ba dự án; `val job = launch { restore(uri) { p -> if (p.doneFrames == framesOfFirst) { stagedFiles = đếm tệp dưới .restoring-*; job.cancel() } } }`, `advanceUntilIdle()`; `stagedFiles > 0` (phần dở đã có khi hủy), `job.isCancelled`; `store.ids()` bằng tập trước khi khôi phục, `root` không còn con nào bắt đầu bằng `.restoring-`, `refresh` không được gọi, log có `"Restore cancelled"`. Đối chứng ở bước 4: bỏ `withContext(NonCancellable)` quanh `discard()` thì test đỏ ở câu không còn `.restoring-` |
| KB-9, "Cancel" trước khi đọc xong `manifest.json` | Unit test | `restore_cancelledBeforeManifest_noStagingCreated`: `val job = launch { restore(...) }`, `job.cancel()` trước `advanceUntilIdle()`; không có `.restoring-*`, `onProgress` không được gọi, `store.ids()` không đổi |
| KB-9, câu 11: app bị đóng thì lần mở sau dọn phần dở | Unit test | `FileProjectStoreTest.deleteAbandonedImports_removesOnlyInactive`: tạo tay `root/.restoring-old/<mã>/project.json`, gọi `beginImport()` (đang mở) rồi `deleteAbandonedImports()`: trả 1, `.restoring-old` mất, thư mục của import đang mở còn; `ids()` trước và sau không có mã trong `.restoring-old` |
| KB-9, câu 11: kho dọn ở lần tải đầu | Unit test | `DefaultProjectRepositoryTest.projects_firstCollect_deletesAbandonedImports`: tạo tay `root/.restoring-old/` có tệp, dựng `DefaultProjectRepository` mới (như lần mở app sau), thu `projects.first()`: `.restoring-old` không còn; `projects_abandonedImportCleanupFails_stillLoads`: `FailingProjectStore` của T-002 bọc thêm `deleteAbandonedImports` ném `IOException` (lớp bọc ẩn danh `object : ProjectStore by store { override suspend fun deleteAbandonedImports(): Int = throw IOException("x") }` trong test, không sửa `RepositoryTestDoubles.kt`): danh sách vẫn tải đủ, log `warn` |
| KB-9, câu 11: dọn dẹp không chạm lần khôi phục đang chạy | Unit test | `restore_cleanupDuringRestore_doesNotTouchStaging`: trong `onProgress` khi `doneFrames` lần đầu lớn hơn 0 (đã có tệp trong chỗ chuẩn bị), gọi `store.deleteIncomplete()` và `store.deleteAbandonedImports()` (trả 0); `restore` là `Success`, dự án khôi phục đủ tệp |
| KB-9, câu 12: hết chỗ khi khôi phục | Unit test | `restore_noSpace_failsWithNoSpaceAndLeavesNothing`: `FailingImportStore` ném `IOException("write failed: ENOSPC (No space left on device)")` ở lượt `writeFile` thứ 3: `Failure(NO_SPACE)`; `store.ids()` không đổi, không còn `.restoring-*`. Cùng test với lời `"Permission denied"`: `Failure(WRITE_FAILED)` |
| KB-9, hết chỗ khi đổi tên vào chỗ thật | Unit test | `restore_commitFails_removesMovedProjects`: `FailingImportStore` ném ở `commit` sau khi đã chuyển dự án đầu của file hai dự án (lớp bọc chuyển dự án đầu qua `delegate` rồi ném): `Failure(WRITE_FAILED)`; `store.ids()` bằng tập trước khi khôi phục |
| KB-10, không phải file sao lưu hay hỏng | Unit test | `restore_notABackup_failsWithNotABackup`, chạy từng trường hợp, mỗi trường hợp kiểm `Failure(NOT_A_BACKUP)`, `store.ids()` không đổi, không còn `.restoring-*`, `refresh` không gọi: (a) tệp văn bản `"hello"`; (b) ảnh PNG 1 × 1; (c) ZIP rỗng; (d) ZIP mà mục đầu không phải `manifest.json`; (e) `manifest.json` không phải JSON; (f) `"app":"other"`; (g) `backupFormatVersion` 0; (h) `project.json` thiếu trường `name`; (i) `frameCount` trong `manifest.json` khác `frames.size`; (j) `manifest.json` ghi 2 dự án, tệp chỉ có 1; (k) mục tệp đứng trước `project.json` của dự án; (l) file mẫu `backup-v1.frameloop` cắt còn một nửa (đọc lỗi giữa chừng, sau khi đã ghi tệp vào chỗ chuẩn bị); (m) mục một ô lớn hơn 64 MB (dựng bằng luồng sinh byte, không giữ trong bộ nhớ: `ZipOutputStream` ghi 64 MB + 1 byte vào tệp tạm); (n) `FailingInputStream` ném `IOException("network")` ở byte giữa tệp |
| KB-10, bước đối chứng cho test lỗi | Unit test | Mỗi trường hợp (d) tới (l) dựng từ chính `backup-v1.frameloop` sửa đúng một chỗ; `restore_sampleV1_succeeds` khôi phục file mẫu nguyên vẹn thành `Success` với 2 dự án, để test lỗi không qua vì khung rỗng |
| KB-10, mục lạ không thành đường dẫn | Unit test | `restore_unknownOrEscapingEntries_ignored`: file mẫu thêm mục `projects/0/../../evil.png`, `projects/0/cells/x/y.png` (mã không hợp lệ), `projects/0/notes.txt`: `Success`, không có tệp nào ngoài thư mục các dự án mới, không có `evil.png` ở đâu dưới thư mục tạm |
| KB-10, bản app mới hơn | Unit test | `restore_newerBackupFormat_failsWithNewerVersion`: `manifest.json` có `backupFormatVersion` 2: `Failure(NEWER_VERSION)`, không có `.restoring-*` (thất bại trước `beginImport`); `restore_newerProjectFormat_failsWithNewerVersion`: `project.json` có `formatVersion` 2: `Failure(NEWER_VERSION)`, không còn `.restoring-*` |
| Ticket: không chứa dự án bài học | Unit test | `export_skipsLessonsAndUnreadable`: `export(uri, [bài học, hỏng, thường])` là `Success(1)`; đọc ZIP: `manifest.json` có 1 dự án, có đúng một `projects/0/project.json`, không có `projects/1/`; `export_onlyLessons_nothingToExport`: `Failure(NOTHING_TO_EXPORT)` và `streams.openWrite` không được gọi (tệp đích không được tạo) |
| KB-8, ghi lỗi: hết chỗ, mất quyền ghi | Unit test | `export_writeFails_reportsKind`: `FailingOutputStream` ném `IOException("ENOSPC")` sau 100 byte: `Failure(NO_SPACE)`; ném `IOException("broken pipe")`: `Failure(WRITE_FAILED)`; `openWrite` ném `SecurityException`: `Failure(WRITE_FAILED)`; `openWrite` ném `FileNotFoundException`: `Failure(WRITE_FAILED)` |
| KB-8, dự án trên máy không bị ảnh hưởng | Unit test | `export_neverWritesToProjects`: liệt kê đệ quy `root` (đường dẫn, kích thước, `lastModified`) trước và sau một `export` thành công và một `export` lỗi: bằng nhau |
| KB-8, ghi xong mới báo xong | Unit test | `export_closeFails_isFailure`: `FailingOutputStream` chỉ ném ở `close()`: `Failure(WRITE_FAILED)` |
| KB-8, an toàn trên luồng chính | Unit test | `export_and_restore_runOnIoDispatcher`: `ioDispatcher` là `QueueDispatcher`; gọi `export` trong `runTest`, `runCurrent()`: chưa xong; `queue.runAll()` rồi `runCurrent()` (lặp tối đa 10 vòng): xong `Success`. Cùng cách với `restore`. Không dùng `StandardTestDispatcher` có scheduler riêng (L-001). Đối chứng ở bước 4: bỏ `withContext(ioDispatcher)` thì đỏ ở câu "chưa xong" |
| KB-8, tên file gợi ý | Unit test | `BackupFileNamesTest`: `forProject("Cat")` là `"Cat.frameloop"`; `forProject("a/b:c*?")` là `"a_b_c__.frameloop"`; `forProject("   ")` là `"Frameloop.frameloop"`; `forAll(LocalDate.of(2026, 11, 21))` là `"Frameloop-backup-2026-11-21.frameloop"` |
| Định dạng v1 là hợp đồng | Unit test | `BackupFormatTest.sampleV1_matchesWriter`: byte của các mục (tên, thứ tự, nội dung) của `backup-v1.frameloop` bằng của `writeSampleV1`; `BackupFormatTest.export_matchesV1Layout`: `export` hai dự án của mẫu (dựng lại trong kho với cùng mã và tệp) cho cùng danh sách tên mục theo cùng thứ tự và cùng `manifest.json` (so JSON đã giải mã) như file mẫu; `manifest_unknownField_ignored`: thêm trường lạ vào `manifest.json` vẫn `Success` |
| Luồng `Uri` thật, chế độ `"wt"` | Unit test | `ContentResolverBackupStreamsTest.write_truncatesExistingFile`: ghi 200 000 byte qua `openWrite(Uri.fromFile(f))`, ghi lại 10 byte: `f.length() == 10`; `read_roundTrips`. Không test `Uri` `content://` của nhà cung cấp không có: Robolectric trả một luồng ném `UnsupportedOperationException` khi đọc, khác máy thật (chạy thử, mục 4.1); đường mở `Uri` lỗi được kiểm ở `DefaultProjectBackupTest.restore_openFails_notABackup` với `FileBackupStreams` ném `FileNotFoundException` và `SecurityException`: cả hai `Failure(NOT_A_BACKUP)`, không có `.restoring-*` |
| Kho: thư mục chuẩn bị | Unit test | `FileProjectStoreTest.import_commit_movesProjectsInOrder`: hai dự án ghi vào import, trước `commit` `ids()` không có mã nào của chúng và `read` là `NotFound`, sau `commit` trả hai mã theo thứ tự ghi, `read` là `Ok`, tệp đúng byte, không còn thư mục chuẩn bị sau `discard()`; `import_stagedProjectWithoutJson_notMoved`: mã chỉ có tệp ô, không có `project.json`, không được chuyển và bị xóa ở `discard()`; `import_deleteIncomplete_doesNotTouchStaging` (cần `deleteIncomplete` của T-002); `import_deleteOfOtherProject_keepsStagingFolder`; `import_discardTwice_noThrow`; `import_targetExists_commitThrows`: tạo sẵn `root/<mã>` rồi `commit` ném `IOException`, thư mục có sẵn không đổi |
| Danh sách tệp | Unit test | `ReferencedFilesTest.fullProject_listsEveryFileInOrder`: với `TestProjects.full()` danh sách bằng đúng danh sách viết tay (thứ tự thumbnail, nền, âm thanh, vẽ theo, ô theo khung rồi layer); `solidBackgroundNoReference_hasNoBackgroundOrReference` |
| Ghi sao lưu và khôi phục trên máy thật qua Google Drive | Kiểm tra thủ công (máy thật: khác, cần ứng dụng Drive đăng nhập tài khoản và bộ chọn file thật; nối giao diện ở T-013) | Thuộc kiểm tra của T-013: dev sao lưu một dự án vào Drive, khôi phục từ Drive. Plan này không có bước tay riêng |

**Ngoại lệ viết test trước:** `BackupModule.kt` (Hilt, chỉ nối đối tượng; đồ thị Hilt được kiểm khi T-013 nối `:core:project` vào `:app`, như plan T-002 mục 6 đã ghi); dòng của `CLAUDE.md` (tài liệu); tệp `backup-v1.frameloop` sinh bằng `writeSampleV1` ở bước 3 (dữ liệu của test, không phải code).

### 4.1. Chạy thử

- **Đã chạy thử:** `java.util.zip` qua `ContentResolver` (kỹ thuật lần đầu dùng trong dự án: `grep -rln 'java.util.zip\|contentResolver\|openOutputStream'` không thấy tệp Kotlin nào). Test tạm không commit `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/backup/TrialZipUriTest.kt` (Robolectric, sdk 35 theo `robolectric.properties` của module), 2026-11-22, nhánh `claude/plan-T-003-file-sao-luu` từ commit da87fc5: ghi ZIP qua `openOutputStream(Uri.fromFile(f), "wt")`, ghi lại ngắn hơn để kiểm cắt nội dung cũ, đọc lại qua `openInputStream`; tệp không phải ZIP; ZIP cụt; `Uri` của nhà cung cấp không có.
- **Lệnh:** `./gradlew :core:project:testDebugUnitTest --tests '*TrialZipUriTest*'`; `scripts/check.sh`
- **Kết quả:** `./gradlew :core:project:testDebugUnitTest --tests '*TrialZipUriTest*'`: 3 test chạy. Qua: ghi ZIP 200 000 byte qua `openOutputStream(Uri.fromFile(f), "wt")`, ghi lại ngắn hơn thì tệp bị cắt (nhỏ hơn lần đầu), đọc lại qua `openInputStream` đúng mục và đúng byte; tệp văn bản thường cho `ZipInputStream.nextEntry == null` (không ném); ZIP cắt còn 500 byte ném `java.io.EOFException: Unexpected end of ZLIB input stream`. Khác dự kiến: `openInputStream(Uri.parse("content://com.example.none/doc/1"))` trên Robolectric không ném `FileNotFoundException` như máy thật mà trả một luồng ném `UnsupportedOperationException: You must use ShadowContentResolver.registerInputStream()` khi đọc; vì vậy mục 4 bỏ test `content://` không có nhà cung cấp, kiểm đường mở lỗi bằng `FileBackupStreams` giả. Hệ quả cho mục 3.1: tệp không phải ZIP cho `nextEntry == null` (bước 2 đã coi là `NOT_A_BACKUP`), tệp cụt cho `EOFException` (bước 9). `scripts/check.sh` lần 1 (có test tạm): đỏ ở `:core:project:ktlintTestSourceSetCheck` vì kiểu viết của chính test tạm (nhiều lệnh trên một dòng); chạy `ktlintTestSourceSetFormat` rồi chạy lại: qua (`EXIT=0`, kết thúc 12:14 UTC; `:core:project:testDebugUnitTest` chạy lại, `TrialZipUriTest` 3 test, 0 lỗi). Chưa chạy thử: hủy qua `onProgress` trên `runTest` (mục 3.4 có dự phòng). Không chạy `./gradlew :app:assembleStagingRelease`: không thêm thư viện hay module, `:app` chưa phụ thuộc `:core:project`.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 7 (module core dùng chung `:core:project`: thêm hàm vào interface công khai `ProjectStore`, sửa tệp của T-002). Điểm chặn 9 không chạm ở plan này (định dạng mới, chưa có dữ liệu cũ để chuyển đổi), nhưng định dạng `.frameloop` v1 thành hợp đồng: đổi sau này chạm điểm chặn 9.

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 2 | Một module: 6 tệp code mới, sửa `ProjectStore.kt`, `FileProjectStore.kt` (thêm hàm vào interface công khai) và một hàm của `DefaultProjectRepository.kt` (T-002); 8 tệp test và một file mẫu nhị phân; một dòng `CLAUDE.md` |
| B. Độ rõ của yêu cầu | 1 | Spec rõ về lỗi, tiến độ, hủy, dự án mới (câu 10, 11, 12, 19 đã trả lời); còn chọn lựa không đổi tiêu chí: file dở ở chỗ người chọn khi ghi lỗi (câu 4), ghi lỗi khi khôi phục mà không hết chỗ hiện lời nào (câu 6), kiểu MIME (câu 5) |
| C. Khả năng kiểm chứng tự động | 1 | Mọi hành vi có unit test trên thư mục tạm, `ContentResolver` Robolectric với `file://`; bộ chọn file thật, Google Drive và hết chỗ thật chỉ kiểm trên máy thật ở T-013 |
| D. Ảnh hưởng chéo | 2 | Thêm hàm vào `ProjectStore` mà T-002 và lớp bọc trong test dùng; dựa trên plan T-002 chưa duyệt; T-013, T-017 dựa vào interface và định dạng file của plan này; file `.frameloop` v1 là hợp đồng lâu dài với người dùng |
| E. Độ mới của kỹ thuật | 2 | Lần đầu đọc ghi ZIP và đọc ghi `Uri` qua `ContentResolver` trong dự án; hủy coroutine kèm dọn dẹp `NonCancellable` lần đầu có test |
| **Tổng** | 8 | **Mức: Cao** (chạm điểm chặn 7; tổng điểm riêng là Trung bình) |

## 6. Ảnh hưởng chéo

- **Dựa trên plan T-002 phiên bản 1, `draft`, chưa duyệt** (PR nháp #17): dùng `ProjectRepository` và `refresh()`, `isOutOfSpace` (`repository/StorageErrors.kt`), `DefaultProjectRepository.cleanUpOnce()`, `deleteIncomplete()` (không chạm thư mục bắt đầu bằng `.`), `FailingProjectStore` của `RepositoryTestDoubles.kt` và ràng buộc ở mục 6 của plan T-002 (khôi phục dựng trong chỗ chuẩn bị bắt đầu bằng `.` rồi đổi tên sang thư mục mã với `project.json` có sẵn). Người đổi plan T-002 (tên, chữ ký, quy tắc dọn dẹp, vị trí `isOutOfSpace`) thì phải xem lại plan này trước khi duyệt. T-003 chỉ chạy sau khi T-002 đã merge (`depends_on`); `plan-check.py` báo hai tệp `Sửa` của T-002 chưa có nếu chạy trước.
- File hoặc thành phần dùng chung bị chạm: `ProjectStore.kt`, `FileProjectStore.kt`, `FileProjectStoreTest.kt` (T-001, T-002 cũng sửa); `DefaultProjectRepository.kt`, `DefaultProjectRepositoryTest.kt` (T-002 tạo); `CLAUDE.md` (bảng module, như T-002: danh sách chạy xếp tuần tự, mỗi plan chỉ sửa dòng của mình).
- Task khác có thể xung đột: T-002 (cùng sửa `ProjectStore.kt`, `FileProjectStore.kt`, `FileProjectStoreTest.kt`, `CLAUDE.md`): chạy sau T-002. Hàm `referencedFiles` có hai bản (T-002 trong `DefaultProjectRepository`, T-003 trong `storage/ReferencedFiles.kt`): câu 3 ở mục 8. T-013, T-017 dùng interface và định dạng của plan này: chạy sau.
- Định dạng `.frameloop` v1 dùng chung cho sao lưu một dự án (projects) và tất cả (settings, câu 19 của spec projects).
- Đồ thị Hilt của `ProjectBackup` (cần `ProjectRepository` của T-002 và `Context`) và R8 chỉ được kiểm khi một feature nối `:core:project` vào `:app` (T-009 hay T-011, như plan T-002 mục 6): ticket đó chạy `./gradlew :app:assembleStagingRelease`. `java.util.zip` là lớp nền của Android, không cần luật R8; `Manifest` dùng `@Serializable` như `ProjectFormat` (plugin serialization đã sinh luật giữ lớp cho T-001).
- Đề xuất thứ tự chạy: sau T-002; trước T-013, T-017.

## 7. Điều kiện dừng riêng

- T-002 chưa merge khi task bắt đầu, hay `ProjectRepository.refresh()`, `isOutOfSpace`, `deleteIncomplete`, `cleanUpOnce` trên nhánh chính khác tên hay khác hợp đồng mà mục 3.1 dựa vào: dừng, chờ plan được xem lại (mục 6).
- Cần đổi định dạng `project.json` (`ProjectFormat`, số phiên bản) hay đổi hành vi của một hàm đã có của `ProjectStore`: dừng (hợp đồng với dữ liệu trên máy người dùng, plan T-001).
- Cần đổi bố cục file `.frameloop` v1 khác mục 3.1 (thứ tự mục, tên mục, trường của `manifest.json`) sau khi file mẫu đã commit ở bước 3: dừng, vì file mẫu là hợp đồng được duyệt.
- Cần sửa `:core:data` hay thêm phụ thuộc module: dừng.

## 8. Câu hỏi cho người duyệt

1. **Plan dựa trên plan T-002 phiên bản 1 chưa duyệt.** Đề xuất của AI: duyệt T-002 trước; nếu T-002 đổi tên hay hợp đồng của `ProjectRepository.refresh()`, `isOutOfSpace`, `deleteIncomplete`, `cleanUpOnce`, hay bỏ ràng buộc chỗ chuẩn bị bắt đầu bằng `.`, AI sửa plan này thành phiên bản 2 trước khi duyệt. `plan-check.py` có thể báo hai tệp `Sửa` của T-002 (`DefaultProjectRepository.kt`, `DefaultProjectRepositoryTest.kt`) chưa có trên nhánh chính: đúng khi T-002 chưa merge, hết khi T-002 đã merge.
2. **Định dạng: ZIP có `manifest.json` ở mục đầu.** Đề xuất của AI: như mục 3.1 (đọc tuần tự được qua luồng của Drive, tên và số khung có trước khi ghi tệp nào, `project.json` dùng lại `ProjectFormat` và `migrate`). Phương án khác: một tệp JSON lớn với ảnh mã hóa base64 (to hơn khoảng 33 %, phải giữ cả file trong bộ nhớ). File mẫu `backup-v1.frameloop` sẽ là hợp đồng, không sửa sau khi duyệt.
3. **Hai bản `referencedFiles`.** Plan T-002 (nhân bản) có hàm riêng trong `DefaultProjectRepository`; plan này thêm `storage/ReferencedFiles.kt`. Đề xuất của AI: plan T-002 phiên bản sau dùng `storage/ReferencedFiles.kt` (chuyển hàm sang T-002, T-003 chỉ dùng lại; khi đó tệp này thành `Sửa`/không có trong mục 3.2 của T-003); nếu T-002 giữ nguyên thì chấp nhận hai bản ở bản đầu, ticket sau gộp.
4. **Ghi sao lưu lỗi giữa chừng để lại file dở ở chỗ người chọn.** Đề xuất của AI: không xóa (xóa qua `DocumentsContract.deleteDocument` không phải nhà cung cấp nào cũng cho, và có thể xóa nhầm file cũ người dùng chọn ghi đè); "Retry" của T-013 ghi lại cùng `Uri` với `"wt"`. File dở khi khôi phục cho `NOT_A_BACKUP`. Phương án khác: thử `DocumentsContract.deleteDocument`, lỗi thì bỏ qua.
5. **Kiểu MIME của file.** `.frameloop` không có kiểu đăng ký. Đề xuất của AI: `application/octet-stream` khi tạo; bộ chọn mở file của T-013 lọc `*/*` (Drive và nhiều nhà cung cấp không nhận ra kiểu riêng), app kiểm nội dung. Phương án khác: kiểu riêng `application/vnd.frameloop` (bộ chọn có thể không cho chọn file đã tải về từ nơi khác).
6. **Khôi phục lỗi ghi mà không phải hết chỗ** (`RestoreFailure.WRITE_FAILED`, ví dụ lỗi hệ tệp): spec không có lời riêng, và lời của KB-10 ("not a Frameloop backup, or it is damaged") sai với trường hợp này. Đề xuất của AI: plan này chỉ trả loại lỗi riêng; T-013 đề xuất lời "Couldn't restore. Try again." vào spec (hay dùng tạm lời hết chỗ) và quyết ở plan T-013.
7. **Giới hạn kích thước để chống file độc** (`manifest.json` 1 MB, `project.json` 4 MB, mỗi tệp 64 MB, 10 000 dự án): đề xuất của AI: chấp nhận; ô vẽ 1080p PNG thường dưới 8 MB. Vượt thì `NOT_A_BACKUP`.
8. **Thời điểm sửa của dự án khôi phục.** Đề xuất của AI: `updatedAtMillis = lúc khôi phục` (để đứng đầu danh sách như spec KB-9), `createdAtMillis` giữ như trong file. Dòng "edited <thời điểm>" của menu khi đó là lúc khôi phục.
9. **Hủy không ngắt được lần đọc đang chờ, và hủy trong lúc đổi tên vào chỗ thật.** Lần đọc luồng nguồn đang chờ (Drive chậm) không bị ngắt: hộp thoại của T-013 đóng ngay khi bấm "Cancel", phần dọn chạy khi lần đọc trả về. `commit` (bước cuối, rất ngắn) chạy `NonCancellable`, nên bấm "Cancel" đúng lúc đó vẫn có dự án mới trong danh sách (dự án đủ, không dở). Đề xuất của AI: chấp nhận cả hai.
10. **Phần chưa chạy thử:** hủy qua `onProgress` trên `runTest` (mục 3.4 có dự phòng bằng luồng thật). Đề xuất của AI: chấp nhận, phiên chuyển sang dự phòng theo điều kiện của mục 3.4.

## 9. Quyết định duyệt

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện
