---
ticket: T-001
title: Module :core:project: mô hình dự án và định dạng lưu có phiên bản
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

# PLAN T-001: MODULE :CORE:PROJECT: MÔ HÌNH DỰ ÁN VÀ ĐỊNH DẠNG LƯU CÓ PHIÊN BẢN

## 1. Tóm tắt

Tạo module `:core:project` chứa mô hình dự án đầy đủ (mọi thứ màn vẽ, danh sách dự án, xuất file và bài học cần lưu) và kho lưu trên máy dạng "mỗi dự án một thư mục": một tệp `project.json` có số phiên bản cho phần mô tả, các tệp ảnh và âm thanh nằm cạnh. Đọc không bao giờ ném lỗi ra ngoài: tệp hỏng trả `Corrupt` (kèm tên nếu đọc được), tệp của bản app mới hơn trả `NewerVersion`. Kiểm chứng bằng unit test JVM trên thư mục tạm, gồm một tệp mẫu phiên bản 1 khóa định dạng.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/projects/spec.md`. Ticket chỉ làm phần dữ liệu; giao diện và kho dự án (danh sách, tạo, đổi tên, bộ đếm) thuộc T-002 trở đi.

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-2 | Khi xem một thẻ thì thẻ có ảnh thu nhỏ, tên dự án và dòng "<số khung> frames · <tốc độ> fps" | Mô hình có tên, danh sách khung, tốc độ khung; kho có tệp ảnh thu nhỏ riêng (T-004 vẽ, T-002 đọc) |
| KB-2 | Khi danh sách có nhiều dự án thì dự án sửa gần nhất đứng đầu | Mô hình có `updatedAtMillis` (mốc sửa gần nhất); T-002 sắp xếp |
| KB-2 | Khi một dự án không đọc được thì các dự án khác vẫn hiện và mở được bình thường | Đọc từng dự án độc lập; một thư mục hỏng không làm hỏng lần đọc các thư mục khác, không ném lỗi |
| KB-2 | Khi một dự án không đọc được thì thẻ vẫn hiện với tên (nếu đọc được); app không tự xóa dự án | `Corrupt` mang `name` khi trường `name` còn đọc được; kho không bao giờ tự xóa thư mục hỏng |
| KB-2 | Khi có 100 dự án thì danh sách mở ra không giật | Phần mô tả mỗi dự án nhỏ (vài KB), không chứa ảnh; ảnh nằm ở tệp riêng nên đọc 100 dự án chỉ đọc 100 tệp JSON nhỏ |
| KB-3 (hỗ trợ) | 16:9 ở 1080p là 1920 × 1080; 9:16 ở 1080p là 1080 × 1920; 1:1 là 1080 × 1080; 720p cho 1280 × 720, 720 × 1280, 720 × 720 | Hàm `canvasSize(aspectRatio, resolution)` trong mô hình, để mọi feature dùng một chỗ |
| KB-3 (hỗ trợ) | Ảnh gốc bị xóa sau khi tạo dự án thì nền không đổi, vì app đã chép ảnh vào dự án | Nền `Photo` trỏ tới tệp `background.<đuôi>` trong thư mục dự án, không trỏ ra ngoài |

Phạm vi mô hình lấy từ ticket, đối chiếu với spec editor (KB-6 tới KB-16, mục 6), spec lessons (mục 6) và spec settings (câu 6):

- Tên, tỷ lệ khung (9:16, 16:9, 1:1), độ phân giải (720p, 1080p), tốc độ khung (6, 8, 12, 15, 24, 30).
- Nền: màu (trắng, xám đậm, xanh) hay ảnh "Photo" đã chép vào dự án.
- Khung (giữ khung 1× đến 4×, điểm đặt camera ảo của khung), layer (tên, thứ tự, ẩn hiện, tối đa 10), nội dung mỗi layer ở mỗi khung là một ảnh PNG riêng. Hình, chữ, vùng chọn đang sửa là trạng thái tạm của màn vẽ, không vào mô hình.
- Âm thanh đã đặt: nhạc, giọng ghi âm, hiệu ứng; tệp âm thanh chép vào dự án.
- Camera ảo: công tắc "Camera on" của dự án, điểm đặt theo khung (lia, phóng, xoay).
- Onion skin: bật tắt, số khung trước (mặc định 2), sau (1), độ mờ (45%), màu trước, màu sau, lặp ở hai đầu.
- Lớp vẽ theo: ảnh hay video (đã chia thành ảnh theo tốc độ khung, tối đa 10 giây), độ mờ (40%), ẩn hiện; không xuất ra file (T-036 dùng).
- Quyền mở layer 6 đến 10 của dự án.
- Mốc tạo, mốc sửa gần nhất.
- Loại dự án: thường, hay bài học (mã bài, bước đang làm). Lọc bài học khỏi danh sách, bộ đếm và bản sao lưu là việc của T-002, T-003; ticket này chỉ cho dữ liệu để lọc.

Không lưu trong dự án (thuộc chỗ khác): lực bút từng cọ, mốc mở gói cọ và gói hiệu ứng, mốc nét vẽ đầu tiên, đã xem hướng dẫn, số dự án đã từng tạo (T-002, T-005 và màn vẽ).

Thiết kế tham chiếu: Không có thiết kế riêng (module dữ liệu, không có giao diện; docs/ops/design-workflow.md)

ADR liên quan: ADR-0001 (core chỉ phụ thuộc core), ADR-0019 (ghi log qua `:core:logging`, không ghi dữ liệu người dùng).

## 3. Giải pháp

### 3.1. Cách làm

**Module.** `:core:project`, gói `com.vgl.draw.animation.core.project`. `build.gradle.kts` theo mẫu `:core:assets`: plugin `template.android.library`, `template.hilt`, cộng `libs.plugins.kotlin.serialization`; phụ thuộc `implementation(project(":core:logging"))` và `implementation(libs.kotlinx.serialization.json)`. Thêm `include(":core:project")` vào `settings.gradle.kts`, thêm một dòng vào bảng module của `CLAUDE.md`. Chưa module nào phụ thuộc `:core:project` trong ticket này; T-002 thêm phụ thuộc ở module của nó.

**Mô hình** (gói `model`, lớp dữ liệu bất biến, `@Serializable`, không dùng lớp Android, để test chạy trên JVM):

- `ProjectId`, `FrameId`, `LayerId`, `SoundClipId`: `@JvmInline value class` bọc chuỗi UUID; hàm `random()` sinh mã mới; `ProjectId.parseOrNull(String)` nhận đúng dạng UUID, dùng khi duyệt thư mục.
- `Project(id, kind, name, aspectRatio, resolution, frameRate, background, layers, frames, sounds, camera, onionSkin, reference, extraLayersUnlocked, createdAtMillis, updatedAtMillis)`.
- `ProjectKind`: sealed, `Regular` và `Lesson(lessonId: String, step: Int)`; thuộc tính `Project.isUserProject` trả `kind is Regular`.
- `AspectRatio` (`PORTRAIT_9_16` ghi `"9:16"`, `LANDSCAPE_16_9` ghi `"16:9"`, `SQUARE_1_1` ghi `"1:1"`), `Resolution` (`P720` ghi `"720p"`, `P1080` ghi `"1080p"`), `FrameRate` (6, 8, 12, 15, 24, 30, ghi thành số nguyên qua serializer riêng). `canvasSize(aspectRatio, resolution): CanvasSize(width, height)`: cạnh ngắn bằng 720 hay 1080, cạnh dài theo tỷ lệ.
- `Background`: sealed, `Solid(color: BackgroundColor)` với `BackgroundColor { WHITE, DARK, BLUE }` (ghi `"white"`, `"dark"`, `"blue"`, trùng giá trị tham số `background` của sự kiện `project_create`), và `Photo(fileExtension: String)`. Mã màu thật không nằm ở đây: lớp vẽ (T-004) lấy từ `:core:designsystem`.
- `Layer(id, name, visible)`; danh sách `layers` xếp từ dưới lên trên. Tên mặc định ("Layer 1") là chữ hiển thị, do màn vẽ lấy từ `strings.xml` rồi truyền vào; module không có chuỗi hiển thị.
- `Frame(id, hold: Int = 1, cameraKey: CameraKey? = null)`; `CameraKey(panX: Float, panY: Float, zoom: Float, rotationDegrees: Float)`, `panX`, `panY` tính theo phần của bề rộng, bề cao khung vẽ (không theo điểm ảnh, để đúng ở mọi độ phân giải), `zoom` là hệ số (1.2 nghĩa là 120%).
- `CameraSettings(enabled: Boolean = false)`: công tắc "Camera on" của dự án.
- `SoundClip(id, type: SoundType { MUSIC, VOICE, EFFECT }, title: String, fileExtension: String, startFrameId: FrameId?, durationMillis: Long, effectSource: EffectSource?)`; `startFrameId == null` nghĩa là phát từ đầu dự án ("Music · from 0:00"); `EffectSource(packId, soundId)` chỉ để hiện tên, còn tệp âm thanh luôn được chép vào dự án nên hiệu ứng của gói hết 24 giờ vẫn phát và xuất được (spec editor KB-14).
- `OnionSkinSettings(enabled: Boolean = false, framesBefore: Int = 2, framesAfter: Int = 1, opacityPercent: Int = 45, beforeColorArgb: Int? = null, afterColorArgb: Int? = null, loop: Boolean = false)`; màu `null` nghĩa là dùng màu của theme (`AppTheme.colors.onionPrevious`, `onionNext`), để module không viết cứng mã màu.
- `ReferenceLayer(type: ReferenceType { IMAGE, VIDEO }, fileExtension: String, frameCount: Int, opacityPercent: Int = 40, visible: Boolean = true)`; `reference == null` nghĩa là chưa có lớp vẽ theo. Ảnh: `frameCount = 1`. Video: mỗi ảnh tách ra là một tệp, ảnh thứ i đi với khung thứ i.
- `Project.create(id, name, aspectRatio, resolution, frameRate, background, firstLayerName, nowMillis, kind = Regular)`: một khung, một layer, các giá trị mặc định ở trên, `createdAtMillis = updatedAtMillis = nowMillis`.

**Định dạng trên máy** (gói `storage`):

```
<filesDir>/projects/<projectId>/
  project.json                      phần mô tả, có "formatVersion"
  thumbnail.png                     ảnh thu nhỏ (T-004 vẽ)
  background.<đuôi>                 ảnh nền "Photo"
  cells/<frameId>/<layerId>.png     nội dung một layer ở một khung; không có tệp = ô trống
  sounds/<clipId>.<đuôi>            tệp âm thanh
  reference/<chỉ số>.<đuôi>         ảnh vẽ theo, chỉ số từ 0
```

- `ProjectFormat` (internal, có test trực tiếp): `CURRENT_FORMAT_VERSION = 1`; `encode(project): String` ghi `{"formatVersion":1, "project":{...}}` bằng một `Json` riêng (`encodeDefaults = true`, `ignoreUnknownKeys = false`, `classDiscriminator = "type"`).
- `ProjectFormat.decode(text): ProjectReadResult` theo bốn bước, không ném lỗi:
  1. Phân tích thành `JsonObject`; lỗi thì `Corrupt(name = null, reason = INVALID_JSON)`.
  2. Đọc `formatVersion`; thiếu hay không phải số nguyên thì `Corrupt(reason = MISSING_VERSION)`; lớn hơn `CURRENT_FORMAT_VERSION` thì `NewerVersion(version)`; nhỏ hơn thì chạy chuỗi chuyển đổi `migrate(version, obj)`; không có bước chuyển đổi cho phiên bản đó (hiện chưa có bước nào, vì 1 là phiên bản đầu) thì `Corrupt(reason = UNKNOWN_VERSION)`; phiên bản sau thêm một bước và một tệp mẫu test.
  3. Giải mã chặt (khóa lạ, kiểu sai, giá trị enum lạ, tốc độ khung ngoài danh sách là lỗi) rồi kiểm luật ở dưới; lỗi thì `Corrupt(reason = INVALID_CONTENT)`.
  4. Mọi trường hợp `Corrupt` sau bước 1 thử đọc riêng `project.name` (chuỗi khác rỗng) để gắn vào `name`.
- Luật kiểm khi đọc: ít nhất 1 khung; 1 tới 10 layer; mã khung, mã layer, mã âm thanh không trùng; `hold` từ 1 tới 4; `opacityPercent` của onion skin và lớp vẽ theo từ 0 tới 100; `framesBefore`, `framesAfter` không âm; `durationMillis` không âm; `startFrameId` của âm thanh là `null` hay mã một khung có thật; `Lesson.step` không âm; `frameCount` của lớp vẽ theo ít nhất 1; `fileExtension` (nền, âm thanh, lớp vẽ theo) khớp `[a-z0-9]{1,5}`. Không kiểm độ dài tên (tên do T-002 cắt ở 40 ký tự khi tạo, đọc thì nhận nguyên).
- Quy tắc phiên bản, ghi trong KDoc của `ProjectFormat`: mọi thay đổi của phần mô tả (thêm, bớt, đổi trường) đều tăng `CURRENT_FORMAT_VERSION`, thêm một bước `migrate` và một tệp mẫu `project-v<N>.json` trong test; tệp mẫu cũ không bao giờ bị sửa. Đổi định dạng về sau là điểm chặn 9 của `risk-scoring.md`.
- `ProjectReadResult`: sealed, `Ok(project)`, `NotFound`, `Corrupt(name: String?, reason: CorruptReason)`, `NewerVersion(version: Int)`. `CorruptReason { UNREADABLE_FILE, INVALID_JSON, MISSING_VERSION, UNKNOWN_VERSION, INVALID_CONTENT }`.
- `ProjectFile`: sealed, mỗi loại tệp phụ một lớp: `Thumbnail`, `Background(extension)`, `Cell(frameId, layerId)`, `Sound(clipId, extension)`, `ReferenceImage(index, extension)`; `relativePath` dựng đường dẫn trong thư mục dự án, đuôi sai dạng `[a-z0-9]{1,5}` hay chỉ số âm thì `require` ném `IllegalArgumentException` (lỗi lập trình, không phải dữ liệu người dùng).
- `ProjectStore` (interface công khai, để T-002 dùng bản giả trong test):
  - `suspend fun ids(): List<ProjectId>`: các thư mục con của gốc có tên là UUID hợp lệ; bỏ qua tệp, thư mục bắt đầu bằng `.` (chỗ dành cho phần dở của khôi phục, T-003) và tên khác.
  - `suspend fun read(id): ProjectReadResult`: không có thư mục hay `project.json` thì `NotFound`; đọc tệp lỗi `IOException` thì `Corrupt(reason = UNREADABLE_FILE)`; còn lại theo `ProjectFormat.decode`.
  - `suspend fun write(project)`: tạo thư mục nếu chưa có, ghi `project.json.tmp` rồi đổi tên đè `project.json`, nên tệp cũ còn nguyên nếu ghi dở. Lỗi ghi ném `IOException` cho người gọi (T-002 đổi thành kết quả lỗi, tách loại hết chỗ). Không tự đổi `updatedAtMillis`: người gọi đặt.
  - `suspend fun readFile(id, file: ProjectFile): ByteArray?` (`null` khi không có tệp; lỗi đọc ném `IOException`), `suspend fun writeFile(id, file, bytes: ByteArray)` (ghi tạm rồi đổi tên như trên), `suspend fun deleteFile(id, file)`.
  - `suspend fun delete(id)`: xóa cả thư mục dự án; dự án không có thì không làm gì.
  - Mã hóa ảnh, âm thanh không thuộc module này: kho chỉ nhận và trả mảng byte.
- `FileProjectStore(root: File, ioDispatcher: CoroutineDispatcher, logger: AppLogger)` (internal): mọi thao tác chạy trong `withContext(ioDispatcher)`. Đọc ra `Corrupt` hay `NewerVersion` thì ghi `logger.warn("Project", "Project <id> unreadable: <reason>")` hay `"Project <id> has newer format <version>"`; không ghi tên dự án hay nội dung tệp (ADR-0019).
- `di/ProjectModule.kt`: Hilt `@Provides @Singleton ProjectStore = FileProjectStore(File(context.filesDir, "projects"), Dispatchers.IO, logger)`, theo mẫu `AssetsModule`. Chọn `filesDir` vì dữ liệu dự án phải còn tới khi người dùng xóa; app đã tắt sao lưu tự động của Android (`android:allowBackup="false"` trong `app/src/main/AndroidManifest.xml`), nên dữ liệu không rời máy.

Mẫu có sẵn được làm theo: ghi tạm rồi đổi tên và `withContext(ioDispatcher)` của `DefaultRemoteAssets` (`:core:assets`); `@Serializable` của đích điều hướng (`feature/sample/.../navigation/SampleNavigation.kt`); Hilt module của `AssetsModule`.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `settings.gradle.kts` | Thêm `include(":core:project")` sau `include(":core:network")` |
| Sửa | `CLAUDE.md` | Thêm dòng `:core:project` vào bảng Cấu trúc module: mô hình dự án và kho lưu dự án trên máy |
| Tạo | `core/project/build.gradle.kts` | Plugin thư viện, Hilt, kotlin serialization; phụ thuộc `:core:logging`, `kotlinx-serialization-json` |
| Tạo | `core/project/consumer-rules.pro` | Tệp trống có một dòng chú thích: thư viện kotlinx.serialization đã kèm luật R8 cho lớp `@Serializable` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/Ids.kt` | `ProjectId`, `FrameId`, `LayerId`, `SoundClipId` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/Project.kt` | `Project`, `ProjectKind`, `Project.create`, `isUserProject` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/CanvasFormat.kt` | `AspectRatio`, `Resolution`, `FrameRate` (serializer số nguyên), `CanvasSize`, `canvasSize` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/Background.kt` | `Background`, `BackgroundColor` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/Timeline.kt` | `Layer`, `Frame`, `CameraKey`, `CameraSettings` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/SoundClip.kt` | `SoundClip`, `SoundType`, `EffectSource` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/EditorSettings.kt` | `OnionSkinSettings`, `ReferenceLayer`, `ReferenceType` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ProjectFormat.kt` | Phiên bản, `Json`, `encode`, `decode`, `migrate`, luật kiểm |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ProjectReadResult.kt` | `ProjectReadResult`, `CorruptReason` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ProjectFile.kt` | Các loại tệp phụ và đường dẫn tương đối |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ProjectStore.kt` | Interface công khai |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/FileProjectStore.kt` | Bản cài đặt trên thư mục, ghi tạm rồi đổi tên, log |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/di/ProjectModule.kt` | Hilt module cấp `ProjectStore` |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/model/CanvasFormatTest.kt` | Kích thước khung vẽ, `Project.create` |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/storage/ProjectFormatTest.kt` | Ghi rồi đọc, tệp mẫu v1, phiên bản, tệp hỏng, luật kiểm |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/storage/FileProjectStoreTest.kt` | Thư mục, tệp phụ, ghi tạm rồi đổi tên, đọc nhiều dự án có một dự án hỏng, log |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/storage/ProjectFileTest.kt` | Đường dẫn tương đối, từ chối đuôi sai |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/TestProjects.kt` | Hàm dựng dự án mẫu đủ mọi trường cho các test |
| Tạo | `core/project/src/test/resources/project-v1.json` | Tệp mẫu phiên bản 1, khóa định dạng |

Không có ảnh chuẩn screenshot.

Nơi đọc hai file có sẵn mà plan sửa (`grep -rln "settings.gradle.kts\|CLAUDE.md" scripts/ .github/ */src/test */src/androidTest`), và vì sao không phải sửa:

- `settings.gradle.kts`:
  - `.github/workflows/pr-check.yml`: chỉ dùng tên file trong bộ lọc đường dẫn (file đổi thì chạy job build và job release); PR của task vì vậy chạy cả bản release, đúng ý mục 4.1.
  - `app/src/test/kotlin/com/vgl/draw/animation/network/NetworkDialogLanguageTest.kt`, `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdsTestThemeMatchTest.kt`, `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/BrandTaglineLanguageTest.kt`, `feature/loading/src/test/kotlin/com/vgl/draw/animation/feature/loading/LoadingScreenLanguageTest.kt`: chỉ tìm thư mục gốc của repo bằng việc file có tồn tại hay không.
  - `scripts/ads-plan.py`: chỉ đọc dòng `rootProject.name`, plan không đổi dòng này.
  - `scripts/tests/run.sh`: dùng tên file trong danh sách đường dẫn là code, và đọc mọi dòng `include("...")` để kiểm mọi module nằm dưới `UI_PATHS` của `scripts/ui-test-needed.sh`; `:core:project` nằm dưới `core/` nên qua như các module core khác.
- `CLAUDE.md`:
  - `app/src/androidTest/kotlin/com/vgl/draw/animation/AppLaunchTest.kt`, `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/NativeFullScreenOverlayScreenshotTest.kt`, `scripts/local-setup.sh`, `scripts/run-watchdog.py`: chỉ nhắc tên file trong chú thích hay lời nhắn.
  - `scripts/tests/run.sh`: đọc dòng lệnh `git status --short -- '...'` ở mục Lệnh và dòng `export ANDROID_HOME`; plan chỉ thêm một dòng vào bảng module, không đổi hai dòng đó.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có (module dữ liệu; cờ `feature_projects_enabled` thuộc T-006, giao diện thuộc T-009 trở đi).
- Quảng cáo: Không chạm.
- Sự kiện phân tích: Không chạm.
- File R2: Không chạm.
- Dữ liệu trên máy: thư mục mới `filesDir/projects/`, định dạng phiên bản 1 (mục 3.1). Đây là lần đầu đặt định dạng, không có dữ liệu cũ phải chuyển, nên không chạm điểm chặn 9; mọi lần đổi sau sẽ chạm.
- **Quyền truy cập:** Không thêm. Kho chỉ đọc ghi trong `filesDir` của app, không cần quyền ở mọi phiên bản từ minSdk 26 tới targetSdk 36.
- **Thư viện và SDK:** Không thêm thư viện mới. Dùng thư viện đã có trong `gradle/libs.versions.toml`: `kotlinx-serialization-json` và plugin `kotlin-serialization` (đã dùng ở mọi `:feature:*` qua convention plugin), thêm phụ thuộc ở `:core:project`; Hilt qua `template.hilt`; `:core:logging`.

### 3.4. Phương án dự phòng

Không có. Mọi kỹ thuật (JSON với kotlinx.serialization, ghi tạm rồi đổi tên, thư mục tạm của JUnit) đã có mẫu trong dự án hay trong thư viện chuẩn của JVM.

### 3.5. Hành vi đã có bị đổi

Không có. Plan chỉ tạo file Kotlin mới trong module mới; `settings.gradle.kts` thêm một dòng `include`, không đổi module có sẵn. `scripts/existing-tests.py` không tìm thấy test nào kiểm hành vi bị đổi (kết quả ở mô tả PR).

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Không có | Không có | Không có | Không có |

## 4. Kiểm thử

Mọi test là unit test JVM (JUnit 4, `kotlinx-coroutines-test`, `TemporaryFolder` của JUnit), không cần Robolectric vì module không gọi lớp Android ngoài Hilt module. `FileProjectStore` dựng với `StandardTestDispatcher` của `runTest` (truyền `testScheduler`) và `RecordingAppLogger`. `TestProjects.full()` dựng một dự án có đủ mọi trường khác mặc định: loại `Lesson("walk-cycle", 3)`, 16:9, 720p, 24 fps, nền `Photo("jpg")`, 3 layer (một layer ẩn), 4 khung (một khung `hold = 3`, một khung có `CameraKey(0.1f, -0.2f, 1.2f, 15f)`), camera bật, 3 âm thanh (nhạc `startFrameId = null`, giọng từ khung 2, hiệu ứng ở khung 3 có `EffectSource`), onion skin bật với màu riêng, lớp vẽ theo video 120 ảnh, `extraLayersUnlocked = true`.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| Mô hình lưu đủ mọi phần của ticket | Unit test | `ProjectFormatTest.encodeThenDecode_fullProject_returnsEqualProject`: `decode(encode(TestProjects.full()))` là `Ok` và bằng dự án gốc (so `data class`) |
| Định dạng phiên bản 1 không đổi ngầm | Unit test | `ProjectFormatTest.decode_v1Sample_returnsExpectedProject`: đọc `project-v1.json` (tạo từ `encode(TestProjects.full())` lúc viết test, rồi giữ cố định) ra đúng `TestProjects.full()`; `ProjectFormatTest.encode_fullProject_matchesV1Sample`: `Json.parseToJsonElement(encode(TestProjects.full()))` bằng phần tử JSON của tệp mẫu, nên đổi tên trường hay thêm trường mà không tăng phiên bản làm test đỏ |
| Tệp ghi ra có số phiên bản | Unit test | `ProjectFormatTest.encode_writesCurrentFormatVersion`: khóa `formatVersion` của JSON bằng `1` và bằng `CURRENT_FORMAT_VERSION` |
| Tệp của bản app mới hơn | Unit test | `ProjectFormatTest.decode_newerFormatVersion_returnsNewerVersion`: tệp mẫu đổi `formatVersion` thành `2` (cả khi phần `project` có khóa lạ) trả `NewerVersion(2)`, không phải `Corrupt` |
| Đọc lỗi trả "hỏng", không ném lỗi | Unit test | `ProjectFormatTest.decode_invalidJson_returnsCorruptWithoutName` (chuỗi `"{not json"`, chuỗi rỗng, nửa đầu của tệp mẫu): `Corrupt(null, INVALID_JSON)`; `decode_missingVersion_returnsCorrupt` và `decode_versionNotNumber_returnsCorrupt`: `Corrupt(reason = MISSING_VERSION)`; `decode_unknownKey_returnsCorrupt` (thêm `"extra": 1` vào `project` khi `formatVersion` là 1): `Corrupt(reason = INVALID_CONTENT)` |
| KB-2: dự án hỏng vẫn hiện tên nếu đọc được | Unit test | `ProjectFormatTest.decode_invalidContentWithName_keepsName`: tệp mẫu đặt `frames` thành `[]` trả `Corrupt(name = <tên trong tệp mẫu>, reason = INVALID_CONTENT)`; đối chứng: `decode_invalidJson_returnsCorruptWithoutName` cho `name == null` |
| Luật kiểm khi đọc | Unit test | `ProjectFormatTest.decode_ruleViolation_returnsCorrupt`, một ca cho mỗi luật ở mục 3.1, mỗi ca sửa một chỗ của tệp mẫu: 0 khung; 0 layer; 11 layer; trùng mã khung; trùng mã layer; trùng mã âm thanh; `hold` 0 và 5; `fps` 10; `aspectRatio` `"4:5"`; onion `opacityPercent` 101; `framesBefore` -1; `durationMillis` -1; `startFrameId` không có trong `frames`; `step` -1; lớp vẽ theo `frameCount` 0; `fileExtension` `"../x"`. Đối chứng: tệp mẫu không sửa đọc ra `Ok` trong cùng test |
| Chuyển đổi phiên bản cũ có chỗ cắm | Unit test | `ProjectFormatTest.decode_olderVersionWithoutMigration_returnsCorrupt`: `formatVersion` 0 trả `Corrupt(reason = UNKNOWN_VERSION)` vì không có bước chuyển đổi nào từ 0; phiên bản sau thay test này bằng test của bước chuyển đổi |
| KB-3: kích thước khung vẽ | Unit test | `CanvasFormatTest.canvasSize_matchesSpec`: đủ 6 cặp: (16:9, 1080p) 1920 × 1080; (9:16, 1080p) 1080 × 1920; (1:1, 1080p) 1080 × 1080; (16:9, 720p) 1280 × 720; (9:16, 720p) 720 × 1280; (1:1, 720p) 720 × 720 |
| Dự án mới có giá trị mặc định | Unit test | `CanvasFormatTest.create_hasOneFrameOneLayerAndDefaults`: 1 khung `hold = 1`, 1 layer hiện với tên truyền vào, không âm thanh, camera tắt, onion skin `false, 2, 1, 45, null, null, false`, `reference == null`, `extraLayersUnlocked == false`, `createdAtMillis == updatedAtMillis == nowMillis`, `kind == Regular`, `isUserProject == true`; đối chứng: dự án `Lesson` có `isUserProject == false` |
| Tốc độ khung ghi thành số | Unit test | `ProjectFormatTest.encode_frameRateAsNumber`: JSON của dự án 12 fps có `"frameRate": 12` (số, không phải chuỗi) |
| Ghi rồi đọc qua kho | Unit test | `FileProjectStoreTest.writeThenRead_returnsEqualProject`: `write(full)` rồi `read(id)` là `Ok(full)`; tệp `projects/<id>/project.json` có trên đĩa |
| Không có dự án | Unit test | `FileProjectStoreTest.read_missingProject_returnsNotFound`: mã chưa ghi trả `NotFound`; thư mục có mà thiếu `project.json` cũng trả `NotFound` |
| Ghi đè và ghi dở không làm mất bản cũ | Unit test | `FileProjectStoreTest.write_overwrite_replacesAndLeavesNoTempFile`: ghi hai lần với tên khác, đọc ra bản sau, thư mục không còn `project.json.tmp`; `FileProjectStoreTest.write_tempFails_throwsIOExceptionAndKeepsPrevious`: sau lần ghi đầu, tạo một thư mục tên `project.json.tmp` trong thư mục dự án (làm lần ghi tạm thất bại kể cả khi chạy bằng root), lần ghi thứ hai ném `IOException` (`assertTrue(error is IOException)`), `read` vẫn trả bản đầu |
| Tệp tạm sót lại không ảnh hưởng | Unit test | `FileProjectStoreTest.read_ignoresLeftoverTempFile`: có `project.json` đúng và `project.json.tmp` chứa rác, `read` trả `Ok` |
| KB-2: một dự án hỏng không làm hỏng các dự án khác | Unit test | `FileProjectStoreTest.readAll_hundredProjectsOneCorrupt_othersOk`: ghi 100 dự án, ghi đè `project.json` của một dự án bằng `"{"`; `ids()` có đủ 100 mã; đọc lần lượt cả 100: đúng 99 `Ok` (bằng dự án đã ghi) và 1 `Corrupt`, không lần nào ném lỗi; thư mục hỏng vẫn còn trên đĩa sau khi đọc (không tự xóa) |
| Tệp không đọc được | Unit test | `FileProjectStoreTest.read_unreadableFile_returnsCorrupt`: `project.json` là một thư mục (đọc ném `IOException`) trả `Corrupt(null, UNREADABLE_FILE)` |
| Chỉ liệt kê thư mục dự án | Unit test | `FileProjectStoreTest.ids_skipsDotDirsFilesAndNonUuidNames`: gốc có 2 thư mục UUID, thư mục `.restore-x`, thư mục `abc`, tệp `<uuid>.txt`: `ids()` trả đúng 2 mã; gốc chưa có thì trả danh sách rỗng |
| Tệp phụ của dự án (ô layer, nền, âm thanh, ảnh thu nhỏ, ảnh vẽ theo) | Unit test | `FileProjectStoreTest.writeFileThenRead_eachKind_returnsSameBytes`: mỗi loại `ProjectFile` ghi rồi đọc ra đúng mảng byte và nằm đúng đường dẫn; `readFile_missing_returnsNull`; `deleteFile_removesOnlyThatFile`: xóa một ô, ô khác của cùng khung còn |
| Xóa dự án | Unit test | `FileProjectStoreTest.delete_removesWholeDirectory`: sau `delete(id)`, thư mục không còn, `read` trả `NotFound`, dự án khác còn; `delete` mã không có không ném lỗi |
| KB-3: nền "Photo" nằm trong dự án | Unit test | `FileProjectStoreTest.photoBackground_storedInsideProject`: ghi dự án nền `Photo("png")` và `writeFile(Background("png"))`; xóa tệp nguồn của test (bản ngoài thư mục dự án); `readFile(Background("png"))` vẫn ra đúng byte |
| Đường dẫn tệp phụ an toàn | Unit test | `ProjectFileTest.relativePath_eachKind`: `Cell` là `cells/<frameId>/<layerId>.png`, `Sound` là `sounds/<clipId>.m4a`, `ReferenceImage(0, "jpg")` là `reference/0.jpg`, `Background("png")` là `background.png`, `Thumbnail` là `thumbnail.png`; `ProjectFileTest.invalidExtensionOrIndex_throws`: đuôi `"../x"`, `""`, `"PNG"`, `"abcdef"` và chỉ số -1 ném `IllegalArgumentException` |
| Log không chứa dữ liệu người dùng | Unit test | `FileProjectStoreTest.read_corrupt_logsWarningWithoutName`: dự án tên `"Secret name"` có `frames: []`; sau `read`, `RecordingAppLogger` có đúng 1 mục `WARN`, thẻ `Project`, chứa mã dự án, không chứa `"Secret name"`; đối chứng: `read` một dự án đúng không ghi `WARN` |
| Kho đọc ghi ngoài luồng chính | Unit test | `FileProjectStoreTest.operations_runOnIoDispatcher`: truyền một `StandardTestDispatcher` riêng (scheduler riêng, không phải của `runTest`) làm `ioDispatcher`; gọi `read` trong `runTest` thì lệnh chưa xong cho tới khi chạy scheduler riêng (`advanceUntilIdle()` của nó), rồi trả `Ok` |
| Module tuân luật phụ thuộc | Lệnh | `scripts/check.sh` chạy `checkModuleDependencies`: `:core:project` chỉ phụ thuộc `:core:logging` |
| Bảng module của `CLAUDE.md` có dòng mới | Kiểm tra thủ công (người: đọc tài liệu) | Người duyệt PR của task đọc dòng `:core:project` trong bảng |

**Ngoại lệ viết test trước:** `di/ProjectModule.kt` (Hilt cấp `ProjectStore`), `settings.gradle.kts`, `build.gradle.kts`, `consumer-rules.pro` và dòng của `CLAUDE.md`: đây là phần nối và cấu hình, không có hành vi riêng để test trước; build của `scripts/check.sh` kiểm chúng biên dịch được. Chưa module nào phụ thuộc `:core:project`, nên đồ thị Hilt của `:app` chưa dùng tới `ProjectModule`; T-002 sẽ nối và có test qua đồ thị đó.

Thứ tự commit: bước 3 commit các tệp test, `TestProjects.kt`, `project-v1.json` cùng khung rỗng tối thiểu để test biên dịch được (module, `settings.gradle.kts`, các lớp mô hình và interface với thân `TODO()`), test đỏ vì `NotImplementedError` hay sai kết quả; bước 4 viết phần cài đặt. `project-v1.json` sinh bằng `encode(TestProjects.full())` ngay khi `encode` chạy được, rồi được kiểm bằng mắt (đủ mọi trường, tên trường như mục 3.1) trước khi khóa.

### 4.1. Chạy thử

- **Đã chạy thử:** Không cần (E dưới 2): kotlinx.serialization đã dùng ở mọi `:feature:*`, ghi tạm rồi đổi tên đã có ở `DefaultRemoteAssets`.
- **Lệnh:** `scripts/check.sh` chạy trong phiên như mọi task; thêm `./gradlew :app:assembleStagingRelease` sau khi `check.sh` qua, vì plan thêm module mới (`docs/CLAUDE.md`, mục Khi viết plan).
- **Kết quả:** Chưa chạy trước khi duyệt. `:app` chưa phụ thuộc `:core:project` trong ticket này nên bản release chưa chứa module; lần build vẫn xác nhận việc thêm module không làm hỏng build release. Luật R8 cho lớp `@Serializable` của module sẽ được kiểm thật ở plan đầu tiên nối `:core:project` vào một feature (T-002 hay T-009), bằng lệnh này.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 7 (module core dùng chung `:core:project`, mới).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 2 | Nhiều phần trong một module mới, có giao diện công khai (`ProjectStore`, mô hình); ngoài module chỉ thêm một dòng ở `settings.gradle.kts` và `CLAUDE.md` |
| B. Độ rõ của yêu cầu | 1 | Spec đã xác nhận, đủ tiêu chí; vài chi tiết mô hình AI tự quyết (chỗ đặt âm thanh theo khung, đơn vị lia camera, onion skin mặc định tắt), nêu ở mục 8 |
| C. Khả năng kiểm chứng tự động | 0 | Mọi hành vi có unit test JVM; chỉ dòng tài liệu cần người đọc |
| D. Ảnh hưởng chéo | 2 | Chưa feature nào dùng, nhưng hơn 15 ticket sau (T-002 tới T-004, màn vẽ, xuất file, bài học) dựa vào mô hình này; `settings.gradle.kts` và `CLAUDE.md` có thể bị plan thêm module khác cùng sửa |
| E. Độ mới của kỹ thuật | 1 | Biến thể nhỏ của mẫu đã có: JSON với kotlinx.serialization, ghi tạm rồi đổi tên |
| **Tổng** | 6 | **Mức: Cao** (chạm điểm chặn 7) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `settings.gradle.kts`, `CLAUDE.md` (bảng module). Module mới `:core:project`.
- Task khác trong sprint có thể xung đột: T-005 (cài đặt mặc định lưu tỷ lệ khung và tốc độ khung mặc định trong `:core:data`) nên dùng `AspectRatio`, `FrameRate` của module này, tức `:core:data` phụ thuộc `:core:project` (core phụ thuộc core, hợp luật ADR-0001); nếu T-005 chạy trước, nó cần tự giữ giá trị thô rồi đổi lại sau (câu hỏi 4). T-002, T-003, T-004 phụ thuộc trực tiếp ticket này. Không plan nào khác hiện đang sửa `settings.gradle.kts`.
- Đề xuất thứ tự chạy: chạy trước T-002, T-003, T-004 và trước T-005 nếu T-005 dùng kiểu của module này.

## 7. Điều kiện dừng riêng

- Cần đổi tên trường hay cấu trúc JSON sau khi `project-v1.json` đã được khóa ở bước 3 (ví dụ test viết trước buộc phải sửa tệp mẫu để qua): dừng, vì tệp mẫu là hợp đồng định dạng mà người duyệt đã duyệt qua mục 3.1.
- Cần thêm phụ thuộc của `:core:project` vào một module ngoài `:core:logging` (ngoài thư viện ở mục 3.3): dừng.

## 8. Câu hỏi cho người duyệt

1. **Chỗ đặt âm thanh.** Plan neo âm thanh vào mã khung bắt đầu (`startFrameId`, `null` là từ đầu) cộng thời lượng, nên chèn hay xóa khung phía trước thì âm thanh đi theo khung. Spec chỉ ghi chữ hiển thị ("from 0:00", "frames 8 to 20", "frame 12"). Đề xuất của AI: giữ cách neo theo khung; nếu khung bắt đầu bị xóa thì màn vẽ (T-032) dời về khung kế tiếp. Anh muốn neo theo thời gian thay không?
2. **Onion skin mặc định tắt hay bật** ở dự án mới? Spec editor KB-8 chỉ ghi số mặc định. Đề xuất của AI: tắt, người dùng bật bằng chip.
3. **Có viết ADR cho định dạng lưu dự án không?** Định dạng là quyết định lâu dài (đổi sau là điểm chặn 9). Đề xuất của AI: KDoc của `ProjectFormat` và tệp mẫu test là đủ cho bản đầu; nếu anh muốn, AI viết ADR-1001 trong một PR tài liệu riêng, không chặn plan này.
4. **T-005 dùng kiểu `AspectRatio`, `FrameRate` của `:core:project`?** Đề xuất của AI: có, để giá trị mặc định ở Cài đặt và ở dự án là một kiểu; T-005 chạy sau ticket này và `:core:data` phụ thuộc `:core:project`.
5. **Duyệt chéo khi nhóm một dev.** Plan rủi ro Cao cần duyệt chéo; nếu nhóm chỉ có anh, mục 9 ghi anh vừa duyệt vừa duyệt chéo kèm lý do (risk-scoring.md, mục 3).

## 9. Quyết định duyệt

<!-- Người duyệt điền, hoặc AI ghi theo lời đồng ý trong phiên của người duyệt, nguyên văn (ADR-0048, phần C). Plan `lane: quick`: ghi "Duyệt trong phiên làm nhanh" kèm lời đồng ý. -->

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện

<!-- AI ghi khi task đã có kết quả cuối (đã merge hoặc đã dừng), trên nhánh báo cáo của phiên (auto-run.md, bước 10). Mục mới thêm vào cuối, không sửa mục cũ. -->
