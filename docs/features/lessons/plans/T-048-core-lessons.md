---
ticket: T-048
title: Module :core:lessons: nội dung bài học trên R2 và kho tiến độ bài
status: done                    # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 15M          # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [core, r2]  # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer: Nhật               # người duyệt
cross_reviewer: Nhật         # bắt buộc khi risk_level là high
approved_at: 2026-11-22
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-048: MODULE :CORE:LESSONS: NỘI DUNG BÀI HỌC TRÊN R2 VÀ KHO TIẾN ĐỘ BÀI

## 1. Tóm tắt

Tạo module `:core:lessons` gồm ba phần: định dạng và cách tải danh sách bài (`lessons/<phiên bản>/index.json`) cùng gói bài (`lessons/<phiên bản>/<mã bài>/lesson.zip`) qua `RemoteAssets`, giải nén an toàn và giữ trên máy; kho tiến độ bài (bài đã xong, bài đang làm với bước, bản nội dung, mã dự án bài học, mốc bắt đầu) trên DataStore dùng chung; khóa Remote Config `lessons_content_version` khai báo trong module và thêm vào `AppRemoteKeys`. Ba tệp mẫu viết sẵn trong plan (danh sách, `lesson.json`, tiến độ) là hợp đồng định dạng. Kiểm chứng bằng unit test JVM với tệp mẫu, tệp zip dựng trong test và DataStore thật trên thư mục tạm.

## 2. Hiểu yêu cầu

Ticket chỉ làm phần dữ liệu ở core. Màn Bài học (T-050), mở bài (T-052), chỉ dẫn trên màn vẽ (T-039) và màn Hoàn thành (T-053) dùng giao diện của module này. Làm nội dung là T-049.

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-2 | Khi danh sách đang tải thì màn hiện trạng thái đang tải | Luồng `library()` phát `Loading` khi trên máy chưa có danh sách nào dùng được, rồi `Ready` hay `Unavailable`. Khung xương là việc của T-050 |
| KB-2 | Khi tải danh sách lỗi hay không có phản hồi sau 15 giây thì màn hiện lỗi | `library()` phát `Unavailable(failure)` với `NETWORK`, `NOT_FOUND`, `INVALID` hay `OTHER`. Mỗi lần kết nối và đọc của `RemoteAssets` đã có giới hạn 15 giây (`HttpAssetDownloader`); giới hạn tổng cho cả lần tải nếu cần là của ViewModel T-050 |
| KB-2 | Khi bấm "Try again" thì tải lại danh sách | `library()` là luồng lạnh: thu lại luồng là tải lại. ViewModel T-050 thu lại khi bấm |
| KB-2 | Khi danh sách đã tải thành công một lần thì các lần mở sau dùng bản trên máy, không hiện đang tải; app vẫn kiểm bản mới ở nền và cập nhật khi có | Module nhớ phiên bản đã tải thành công gần nhất (khóa DataStore `lessons_index_version`). Lần sau phát ngay `Ready` của bản đó (tệp đã có trên máy, `RemoteAssets` không tải lại), không phát `Loading`. "Bản mới" là giá trị khác của `lessons_content_version`: module tải danh sách của phiên bản mới ở nền, tải được thì phát `Ready` mới, lỗi thì giữ bản cũ và chỉ ghi log |
| KB-2 (đặc biệt) | Khi tải lỗi thì dự án của người dùng vẫn dùng bình thường | Module không chạm dữ liệu dự án; lỗi chỉ là giá trị trả về, không ném ra ngoài |
| KB-3 | Khi chạm thẻ bài chưa mở thì app tải nội dung bài (nếu chưa có trên máy) rồi mở màn vẽ với khung có sẵn, nét dẫn, tốc độ khung và các bước, bắt đầu ở bước 1 | `loadPack(version, lessonId)` tải `lesson.zip` (nếu chưa có), giải nén vào thư mục riêng của bài, đọc `lesson.json` thành `LessonPack` (tỷ lệ, độ phân giải, tốc độ khung, nền, layer, khung, bước, tệp ảnh). Kho tiến độ `start(...)` ghi bài đang làm ở bước 0 (bước 1 khi hiện). Dựng dự án và mở màn vẽ là T-052 |
| KB-3 | Khi tải nội dung bài lỗi hay không có phản hồi sau 15 giây thì báo lỗi, có thử lại | `loadPack` trả `Unavailable(failure)`, không ném lỗi; gọi lại là thử lại |
| KB-3 | Ghi nhận lần bắt đầu bài (sự kiện của T-052) | Kho tiến độ giữ `startedAtMillis` của lần mở đầu tiên, dùng cho `duration_ms` của `lesson_complete` (T-053) |
| KB-4 | Khi rời màn vẽ ở bước 3 của bài 6 bước thì thẻ có thanh tiến độ ứng với các bước đã qua | Kho tiến độ giữ `step` (chỉ số từ 0) và `stepCount`; `LessonAttempt.fraction` theo câu hỏi 7 của spec: bước 3 của 6 (chỉ số 2) đầy một nửa, tức `(step + 1) / stepCount` (câu hỏi 4 ở mục 8). Màn vẽ T-039 gọi `setStep` |
| KB-4 | Khi chạm lại bài đang làm dở thì mở ở bước đang làm, với các nét đã vẽ | Kho giữ `step` và `projectId` (mã dự án bài học của T-001, chuỗi UUID) để T-052 mở đúng dự án và đúng bước |
| KB-4 | Khi app bị tắt đột ngột giữa lúc làm bài thì lần mở sau ở đúng bước | Mỗi `setStep` ghi xuống DataStore ngay; DataStore ghi nguyên tử nên tắt app giữa chừng giữ bản trước hay bản sau, không hỏng |
| KB-4 (đặc biệt) | Khi nội dung trên máy chủ đổi sang bản mới lúc người dùng làm dở thì bài dở vẫn mở bằng bản cũ cho tới khi xong hay làm lại | Lượt làm giữ `contentVersion`; `packVersionFor(lessonId, progress, indexVersion)` trả phiên bản của lượt làm dở, không có lượt dở thì trả phiên bản của danh sách đang hiện. Gói cũ đã tải và đã giải nén vẫn nằm trên máy (đường dẫn R2 không bao giờ đổi nội dung, ADR-0015; module không xóa thư mục cũ) |
| KB-5 (hỗ trợ) | Bài được đánh dấu đã xong; sự kiện hoàn thành chỉ một lần cho mỗi lần làm xong; làm lại bài đã xong vẫn giữ "Done" | `complete(lessonId, nowMillis)` ghi bài đã xong kèm mã dự án của lượt đó (để màn Hoàn thành xuất và hoạt hình còn tới khi làm lại, spec mục 6, KB-7), bỏ lượt làm, trả thời gian làm; gọi lần nữa (xoay máy) trả `null`, nên T-053 chỉ ghi sự kiện khi có giá trị. `start` trên bài đã xong giữ bài trong tập đã xong |
| KB-6 (hỗ trợ) | Bài tiếp theo là bài kế trong nhóm, hết nhóm thì bài đầu nhóm sau; bài cuối không có bài tiếp; "Lesson <số>" là số trong cả thư viện | `LessonIndex.next(lessonId)`, `libraryNumber(lessonId)`, `numberInGroup(lessonId)` theo thứ tự nhóm rồi thứ tự trong nhóm |
| KB-10 (hỗ trợ) | Khi cờ bật lại thì bài đã xong và tiến độ bài dở vẫn còn | Kho không bao giờ tự xóa tiến độ; cờ không ảnh hưởng module |

Thiết kế tham chiếu: Không có thiết kế riêng (module dữ liệu, không có giao diện; docs/ops/design-workflow.md)

ADR liên quan: ADR-0001 (core chỉ phụ thuộc core), ADR-0003 và ADR-0046 (khóa Remote Config ở module sở hữu, mặc định an toàn, sinh lại bảng khóa), ADR-0006 và ADR-0015 (file tĩnh chỉ qua `RemoteAssets`, đường dẫn có phiên bản, không ghi đè hay xóa trên R2), ADR-0019 (log qua `AppLogger`, không ghi dữ liệu cá nhân hay nội dung file).

## 3. Giải pháp

### 3.1. Cách làm

**Module.** `:core:lessons`, gói `com.vgl.draw.animation.core.lessons`. `build.gradle.kts` theo mẫu `:core:assets`: plugin `template.android.library`, `template.hilt`, cộng `libs.plugins.kotlin.serialization`; phụ thuộc `implementation(project(":core:assets"))`, `implementation(project(":core:config"))`, `implementation(project(":core:data"))` (lấy `DataStore<Preferences>` dùng chung), `implementation(project(":core:logging"))`, `implementation(libs.kotlinx.serialization.json)`. Thêm `include(":core:lessons")` vào `settings.gradle.kts`, một dòng vào bảng module của `CLAUDE.md`, và `implementation(project(":core:lessons"))` vào `app/build.gradle.kts` (để `AppRemoteKeys` thấy khóa; đồ thị Hilt của `:app` có luôn hai binding mới).

Vì sao module mới: nội dung bài và tiến độ bài do màn Bài học (`:feature:lessons`, T-050) và màn vẽ (`:feature:editor`, T-039) cùng dùng, mà feature không phụ thuộc feature khác (ADR-0001). `:core:project` (T-001) là dữ liệu dự án của người dùng, `:core:assets` là lối tải chung; đặt nội dung bài vào đó làm module phình ra với việc không phải của nó. Module không phụ thuộc `:core:project`: mã dự án bài học lưu dạng chuỗi UUID, giá trị thô của gói (`"9:16"`, `"720p"`, số khung/giây, `"white"`) trùng giá trị định dạng của T-001; T-052 đổi sang kiểu của `:core:project` khi dựng dự án. Nhờ vậy `:core:project` có thể phụ thuộc `:core:lessons` về sau mà không thành vòng, và module chạy thử được trên nhánh chính hiện tại. `:core:data` không phụ thuộc module này nên không có vòng.

**Khóa Remote Config** (`LessonsSettings.kt`, theo mẫu `NetworkSettings`):

```kotlin
object LessonsSettings {
    val CONTENT_VERSION: RemoteKey<String> = RemoteKey.string(
        name = "lessons_content_version",
        default = "v1",
        description = "R2 folder of the current lesson content, for example v1 for lessons/v1/. " +
            "Every content change goes to a new folder.",
    )
    fun all(): List<RemoteKey<*>> = listOf(CONTENT_VERSION)
}
```

`internal fun contentVersionOrDefault(raw: String): String`: giá trị khớp `v[1-9][0-9]{0,5}` thì trả nguyên, không thì trả `CONTENT_VERSION.default` (người gõ sai trên Firebase, ví dụ `"V2"`, `"v2/"`, `""`, không làm hỏng màn Bài học); người gọi ghi log cảnh báo. `AppRemoteKeys` thêm `":core:lessons" to LessonsSettings.all()` sau dòng `:core:network`.

**Đường dẫn trên R2** (điểm chặn 8; hợp đồng với người làm nội dung, T-049):

```
lessons/<phiên bản>/index.json                 danh sách bài
lessons/<phiên bản>/<mã bài>/thumb.webp        ảnh nhỏ (đường dẫn ghi trong index.json, trường "thumb")
lessons/<phiên bản>/<mã bài>/lesson.zip        gói bài (trường "pack")
```

`<phiên bản>` là giá trị của `lessons_content_version` (`v1`, `v2`...). Sửa bất kỳ bài nào là đưa cả thư viện lên thư mục mới rồi đổi khóa (spec câu hỏi 5); tệp đã đưa lên không bao giờ bị ghi đè (ADR-0015). `thumb` và `pack` là đường dẫn tương đối trong thư mục phiên bản và phải đúng bằng `<mã bài>/thumb.webp`, `<mã bài>/lesson.zip` (giá trị khác làm danh sách không hợp lệ); module ghép thành `AssetPath("lessons/<phiên bản>/<đường dẫn>")`.

**Danh sách bài** (`content/LessonIndex.kt`, công khai; `content/LessonIndexFormat.kt`, internal):

- `LessonGroup(id: String, title: String)`, `LessonEntry(id, groupId, title, minutes: Int, frameCount: Int, stepCount: Int, thumb: String, pack: String)`: lớp dữ liệu công khai, dựng được trong test của feature.
- `LessonIndex(groups: List<LessonGroup>, lessons: List<LessonEntry>)`: `ordered` (bài xếp theo thứ tự nhóm trong `groups`, rồi thứ tự xuất hiện trong `lessons`), `lessonsIn(groupId)`, `entry(lessonId)`, `numberInGroup(lessonId)` (từ 1), `libraryNumber(lessonId)` (từ 1, theo `ordered`), `next(lessonId)` (bài kế trong `ordered`, `null` với bài cuối), `completedCount(groupId, completed: Set<String>)` (người gọi truyền `progress.completed.keys`) (số "<đã xong> of <tổng>", spec câu hỏi 2). Mã không có thì `null` hay 0.
- `LessonIndexFormat.decode(text, version): IndexDecodeResult` (`Ok(index)` hay `Invalid(reason: String)`), không ném lỗi. `Json { ignoreUnknownKeys = true }`: nội dung từ máy chủ được thêm trường mới mà bản app cũ vẫn đọc được; thay đổi không tương thích thì tăng `formatVersion`. Mọi trường của mẫu dưới đây là bắt buộc. Luật: `formatVersion` là `1` (lớn hơn thì `Invalid("newer format N")`); ít nhất 1 nhóm; mã nhóm, mã bài khớp `[a-z0-9]+(-[a-z0-9]+)*`, dài tối đa 40, không trùng; tên nhóm, tên bài không trống; mỗi bài thuộc một nhóm có thật; mỗi nhóm có ít nhất 1 bài; `minutes` 1 tới 120; `frameCount` 1 tới 1000; `stepCount` 1 tới 50; `thumb` đúng bằng `"<mã bài>/thumb.webp"` và `pack` đúng bằng `"<mã bài>/lesson.zip"` (đường dẫn cố định của mục trên, ghi trong tệp để người làm nội dung thấy rõ; ghép thành `AssetPath` luôn được vì mã bài đã qua mẫu).

**Tệp mẫu danh sách** (`core/lessons/src/test/resources/index-v1.json`). Bước 3 chép nguyên văn, không sinh từ code. Bài nhóm `motion` đứng đầu mảng để test thứ tự theo nhóm.

```json
{
  "formatVersion": 1,
  "groups": [
    { "id": "basics", "title": "Basics" },
    { "id": "motion", "title": "Motion" }
  ],
  "lessons": [
    {
      "id": "walk-in-place",
      "group": "motion",
      "title": "Walk in place",
      "minutes": 8,
      "frameCount": 16,
      "stepCount": 5,
      "thumb": "walk-in-place/thumb.webp",
      "pack": "walk-in-place/lesson.zip"
    },
    {
      "id": "bouncing-ball",
      "group": "basics",
      "title": "Bouncing ball",
      "minutes": 5,
      "frameCount": 4,
      "stepCount": 3,
      "thumb": "bouncing-ball/thumb.webp",
      "pack": "bouncing-ball/lesson.zip"
    },
    {
      "id": "squash-and-stretch",
      "group": "basics",
      "title": "Squash and stretch",
      "minutes": 6,
      "frameCount": 12,
      "stepCount": 6,
      "thumb": "squash-and-stretch/thumb.webp",
      "pack": "squash-and-stretch/lesson.zip"
    }
  ]
}
```

**Gói bài** (`lesson.zip`). Bố cục trong zip (đề xuất của AI, hợp đồng chung với T-039 và T-049, spec editor câu hỏi 16: dự án dựng sẵn, danh sách bước gồm tên, lời chỉ dẫn, khung áp dụng, lớp nét dẫn, đoạn mẫu):

```
lesson.json                    mô tả bài (mẫu dưới)
cells/<khung>/<layer>.png      nội dung dựng sẵn của một layer ở một khung, chỉ số từ 0; không có tệp = ô trống
guides/<bước>.png              nét dẫn màu đỏ của bước, phủ trên khung của bước (khi "guide": true)
demo/<bước>/<i>.png            đoạn mẫu của nút "Show me", i từ 0 tới demoFrameCount - 1, phát theo frameRate
```

Mọi ảnh có kích thước khung vẽ của `aspectRatio` và `resolution` (như `canvasSize` của T-001); module không giải mã ảnh, chỉ kiểm tệp có mặt. Tên mục trong zip chỉ gồm chữ thường, số, `.`, `_`, `-` và `/`, nằm ngay ở gốc zip (không có thư mục bọc ngoài). Lệnh đóng gói cho T-049, chạy trong thư mục của bài: `cd <thư mục bài> && zip -r -X ../lesson.zip . -x '.*' '__MACOSX/*'` (nén `DEFLATED`, không kèm tệp ẩn hay thư mục của Finder). Bộ giải nén vẫn bỏ qua mục `__MACOSX/...` và mục có đoạn bắt đầu bằng `.` (ví dụ `.DS_Store`), phòng khi gói được nén bằng Finder.

**Tệp mẫu `lesson.json`** (`core/lessons/src/test/resources/lesson-v1.json`, bước 3 chép nguyên văn):

```json
{
  "formatVersion": 1,
  "id": "bouncing-ball",
  "aspectRatio": "9:16",
  "resolution": "720p",
  "frameRate": 12,
  "background": "white",
  "layers": [
    { "name": "Ball" },
    { "name": "Shadow" }
  ],
  "frames": [
    { "hold": 1 },
    { "hold": 1 },
    { "hold": 2 },
    { "hold": 1 }
  ],
  "steps": [
    {
      "title": "Draw the ball",
      "instruction": "Trace the red circle on frame 1.",
      "frame": 0,
      "layer": 0,
      "guide": true,
      "demoFrameCount": 2
    },
    {
      "title": "Squash it",
      "instruction": "On frame 3, draw the ball flat where it hits the ground.",
      "frame": 2,
      "layer": 0,
      "guide": true,
      "demoFrameCount": 0
    },
    {
      "title": "Play it",
      "instruction": "Tap play to watch your ball bounce.",
      "frame": 0,
      "layer": 0,
      "guide": false,
      "demoFrameCount": 0
    }
  ]
}
```

- `LessonPack` (công khai): `lessonId`, `contentVersion`, `aspectRatio: String`, `resolution: String`, `frameRate: Int`, `background: String`, `layers: List<LessonLayer(name)>`, `frames: List<LessonFrame(hold)>`, `steps: List<LessonStep(title, instruction, frame, layer, hasGuide, demoFrameCount)>`, `dir: File`; hàm `cellFile(frame, layer): File?` (`null` khi không có tệp), `guideFile(step): File?`, `demoFiles(step): List<File>`. Chữ của bài (tên layer, tên bước, lời chỉ dẫn) chỉ tiếng Anh, nằm trong nội dung (spec câu hỏi 5).
- `LessonPackFormat.decode(text, expectedId, version, dir): PackDecodeResult` (`Ok(pack)` hay `Invalid(reason)`), không ném lỗi, `ignoreUnknownKeys = true` như danh sách. Luật: `formatVersion` là `1`; `id` bằng mã bài được yêu cầu; `aspectRatio` thuộc `"9:16"`, `"16:9"`, `"1:1"`; `resolution` thuộc `"720p"`, `"1080p"`; `frameRate` thuộc 6, 8, 12, 15, 24, 30; `background` thuộc `"white"`, `"dark"`, `"blue"` (đúng giá trị định dạng của T-001); 1 tới 5 layer (T-001 khóa layer 6 tới 10 sau `extraLayersUnlocked`; gói giữ trong phần miễn phí để T-052 không phải mở khóa), tên không trống, dài tối đa 40; ít nhất 1 khung, `hold` 1 tới 4; ít nhất 1 bước, tên và lời chỉ dẫn không trống; `frame`, `layer` của bước nằm trong danh sách; `demoFrameCount` 0 tới 120; bước có `guide` thì `guides/<bước>.png` có trong thư mục; `demo/<bước>/0.png` tới `demo/<bước>/<demoFrameCount - 1>.png` có đủ.

**Giải nén** (`content/PackExtractor.kt`, internal, `PackExtractor(root: File, maxEntries: Int = 4_000, maxBytes: Long = 32L * 1024 * 1024)`):

- `suspend fun extract(zip: File, version: String, lessonId: String): File` (`suspend` vì giữ `Mutex`; người gọi chạy trong `withContext(ioDispatcher)`) trả `root/<version>/<lessonId>/`. Thư mục đích đã có thì trả ngay, không giải nén lại (thư mục chỉ xuất hiện qua lần đổi tên cuối, nên có là đầy đủ).
- Đọc bằng `java.util.zip.ZipInputStream` vào thư mục tạm `root/.tmp-<UUID>/`, xong thì `renameTo` thành đích. Lỗi giữa chừng thì xóa thư mục tạm; đích không xuất hiện. Đổi tên thất bại mà đích đã có (lượt khác vừa xong) thì xóa thư mục tạm và trả đích.
- Từ chối (ném `InvalidPackException(reason)`, internal, không phải `IOException`): tên mục không khớp `[a-z0-9][a-z0-9._-]*(/[a-z0-9][a-z0-9._-]*)*` sau khi bỏ `/` cuối của mục thư mục (nên không có `..`, đường dẫn tuyệt đối, `\`, chữ hoa); đường dẫn chuẩn hóa không nằm dưới thư mục tạm (so `canonicalFile.toPath().startsWith`, như T-001); tên trùng; quá `maxEntries` mục; tổng số byte đọc ra vượt `maxBytes` (đếm khi đọc, không tin kích thước khai trong zip); đọc hết mà không có mục `lesson.json` (kể cả tệp không phải zip, vì `ZipInputStream` khi đó không thấy mục nào). Bỏ qua (không ghi, không tính vào luật tên) mục bắt đầu bằng `__MACOSX/` và mục có đoạn bắt đầu bằng `.`, nhưng chỉ khi tên không có đoạn `..`, không bắt đầu bằng `/` và không có `\`; tên có những dấu đó luôn bị từ chối, kể cả trong `__MACOSX/`. Mục bị bỏ qua vẫn tính vào `maxEntries`, và dữ liệu của nó được đọc bỏ qua qua cùng bộ đếm byte `maxBytes`, vì `ZipInputStream` vẫn giải nén hết mục bị bỏ qua. `ZipException` từ `ZipInputStream` (zip cụt hay hỏng; trên Android 14 trở lên với targetSdk từ 34, `ZipPathValidator` của hệ thống ném `ZipException` ngay ở `getNextEntry()` cho tên có `..` hay `/` đầu, điều test JVM không thấy) đổi thành `InvalidPackException`, để mọi gói nguy hiểm hay hỏng đều ra `INVALID` trên máy thật như trên JVM. Lỗi đọc ghi đĩa khác ném `IOException`.
- Khóa: một `Mutex` mỗi cặp `(version, lessonId)` như `DefaultRemoteAssets`. Tham số test `onExtract: () -> Unit = {}` gọi mỗi lần thật sự giải nén, để test đếm số lần giải nén.

**Lối dùng nội dung** (`content/LessonContent.kt`, interface công khai; `content/DefaultLessonContent.kt`, internal, `DefaultLessonContent(assets: RemoteAssets, settings: RemoteSettings, dataStore: DataStore<Preferences>, extractor: PackExtractor, ioDispatcher: CoroutineDispatcher, logger: AppLogger)`):

```kotlin
interface LessonContent {
    /** Cold flow: collect again to retry. */
    fun library(): Flow<LessonLibraryState>
    /** Thumbnail of a lesson of [version], or null when it cannot be loaded (the card shows an empty background). */
    suspend fun thumbnail(version: String, entry: LessonEntry): File?
    /** Downloads and extracts the pack of [lessonId] listed in the index of [version]. Never throws for load errors. */
    suspend fun loadPack(version: String, lessonId: String): LessonPackResult
}
sealed interface LessonLibraryState {
    data object Loading : LessonLibraryState
    data class Ready(val version: String, val index: LessonIndex) : LessonLibraryState
    data class Unavailable(val failure: LessonLoadFailure) : LessonLibraryState
}
sealed interface LessonPackResult {
    data class Ready(val pack: LessonPack) : LessonPackResult
    data class Unavailable(val failure: LessonLoadFailure) : LessonPackResult
}
enum class LessonLoadFailure { NETWORK, NOT_FOUND, INVALID, OTHER }
```

- Tải danh sách của một phiên bản (`loadIndex(version)`, internal): `assets.load(AssetPath("lessons/$version/index.json"))`; `Unavailable(reason)` đổi `NETWORK`, `NOT_FOUND`, `OTHER` sang cùng tên; `Ready(file)` thì đọc chữ trong `withContext(ioDispatcher)` (đọc lỗi `IOException` ra `OTHER`) rồi `LessonIndexFormat.decode` (`Invalid` ra `INVALID`).
- `library()`, `flow { ... }`:
  1. Đọc phiên bản đã nhớ (`stringPreferencesKey("lessons_index_version")`) bằng `dataStore.data.first()`; `IOException` thì coi như chưa có, ghi log. Có, và khớp mẫu phiên bản, thì `loadIndex`; được thì phát `Ready(saved, index)`, đặt `shown = saved`.
  2. `shown == null` thì phát `Loading`.
  3. Thu `settings.value(LessonsSettings.CONTENT_VERSION).map(::contentVersionOrDefault).distinctUntilChanged()` (giá trị sai thì `logger.warn("Lessons", "Invalid lessons_content_version, using v1")`, không ghi giá trị). Mỗi giá trị `v` khác `shown`: `loadIndex(v)`; được thì ghi `lessons_index_version = v` TRƯỚC (ghi lỗi `IOException` thì `warn` rồi vẫn tiếp), rồi `logger.info("Lessons", "Lesson index $v shown")`, rồi mới phát `Ready(v, index)` và đặt `shown = v` (ghi trước khi phát để người thu chỉ lấy vài giá trị rồi hủy, như `take(2)`, vẫn thấy phiên bản đã nhớ); lỗi mà `shown == null` thì phát `Unavailable(failure)`; lỗi mà đã có `shown` thì giữ bản đang hiện, `logger.warn("Lessons", "Lesson index $v not loaded: $failure")`.
  Lần mở đầu tiên trước khi Remote Config kích hoạt giá trị từ máy chủ, luồng nhận mặc định `v1` trước, nên `lessons/v1/` phải luôn còn trên R2 (không bao giờ xóa, đúng ADR-0015).
  Lần mở sau khi đã tải được: bước 1 phát `Ready` từ tệp trên máy, không phát `Loading` (KB-2). Khóa đổi khi đang xem: bước 3 tải bản mới ở nền. Quay khóa về bản cũ cũng là một giá trị khác `shown`, nên app quay về bản cũ (tệp đã có trên máy).
- `thumbnail(version, entry)`: `assets.load(AssetPath("lessons/$version/${entry.thumb}"))`; `Ready` trả tệp, còn lại trả `null`.
- `loadPack(version, lessonId)`: phiên bản không khớp mẫu thì `Unavailable(INVALID)`; `loadIndex(version)` (lỗi thì trả lỗi đó); không có bài trong danh sách thì `Unavailable(NOT_FOUND)`; `assets.load(<pack>)`; `extractor.extract` trong `withContext(ioDispatcher)` (`InvalidPackException` ra `INVALID`, `IOException` ra `OTHER`); đọc `lesson.json` và `LessonPackFormat.decode` (`Invalid` ra `INVALID`); `pack.steps.size` khác `entry.stepCount` hay `pack.frames.size` khác `entry.frameCount` thì `INVALID` (thẻ bài và màn vẽ không nói hai số khác nhau). Mở được thì `logger.info("Lessons", "Lesson pack $version/$lessonId opened")`. Lý do của `Invalid(reason)` chỉ nêu luật bị sai và tên trường, không chép nội dung tệp (ADR-0019). Mỗi lỗi ghi một dòng `warn` thẻ `Lessons` với phiên bản, mã bài và lý do. Gói đã giải nén mà `lesson.json` sai thì lần sau vẫn sai (đường dẫn R2 bất biến): nhóm đưa bản mới lên thư mục phiên bản mới.

**Tiến độ bài** (`progress/LessonProgress.kt`, `progress/LessonProgressStore.kt` công khai; `progress/DataStoreLessonProgressStore.kt` internal):

```kotlin
data class LessonAttempt(
    val contentVersion: String,
    val step: Int,              // index of the step being done, from 0
    val stepCount: Int,
    val projectId: String,      // canonical lower-case UUID of the lesson project (T-001)
    val startedAtMillis: Long,
) { val fraction: Float get() = (step + 1).toFloat() / stepCount }
data class CompletedLesson(val projectId: String)   // project of the last completion, kept for export until redo
data class LessonProgress(val completed: Map<String, CompletedLesson> = emptyMap(), val inProgress: Map<String, LessonAttempt> = emptyMap())
fun packVersionFor(lessonId: String, progress: LessonProgress, indexVersion: String): String =
    progress.inProgress[lessonId]?.contentVersion ?: indexVersion

interface LessonProgressStore {
    val progress: Flow<LessonProgress>
    /** Starts or restarts a lesson; keeps it in completed. Throws IllegalArgumentException, IOException. */
    suspend fun start(lessonId: String, attempt: LessonAttempt)
    /** False when the lesson is not in progress (nothing written). Throws IllegalArgumentException, IOException. */
    suspend fun setStep(lessonId: String, step: Int): Boolean
    /** Marks the lesson done; returns the time since start, or null when it was not in progress. Throws IOException. */
    suspend fun complete(lessonId: String, nowMillis: Long): Long?
}
```

- Lưu trong tệp `app_preferences` dùng chung (`DataStore<Preferences>` của `:core:data`), một khóa `stringPreferencesKey("lessons_progress")` chứa JSON có số phiên bản. Một khóa thay vì một khóa mỗi bài để mọi thay đổi là một giao dịch `edit` và định dạng có phiên bản như T-001. Tên khóa `lessons_progress` và `lessons_index_version` là hợp đồng với dữ liệu trên máy người dùng; không trùng khóa đã có (`sample_count`, `ads_first_open_done`, `ads_inter_display_count`) hay khóa của T-005 (`settings_default_frame_rate`, `settings_default_aspect_ratio`, `settings_stylus_pressure`, `first_stroke_drawn`; mốc này vẫn được lưu làm dữ liệu dự phòng).
- **Tệp mẫu tiến độ** (`core/lessons/src/test/resources/progress-v1.json`, bước 3 chép nguyên văn):

```json
{
  "formatVersion": 1,
  "completed": {
    "bouncing-ball": { "projectId": "6f1c2a3e-0000-4000-8000-000000000002" }
  },
  "inProgress": {
    "squash-and-stretch": {
      "contentVersion": "v1",
      "step": 2,
      "stepCount": 6,
      "projectId": "6f1c2a3e-0000-4000-8000-000000000001",
      "startedAtMillis": 1763769600000
    }
  }
}
```

- Ghi: `completed` theo mã tăng dần, `inProgress` theo mã tăng dần, để cùng trạng thái luôn ra cùng chuỗi. Đọc chặt (`ignoreUnknownKeys = false`) rồi kiểm: mã bài theo mẫu của danh sách; `contentVersion` theo mẫu phiên bản; `stepCount` 1 tới 50; `step` từ 0 tới `stepCount - 1`; `projectId` là UUID chuẩn chữ thường (`UUID.fromString(s).toString() == s`); `startedAtMillis` không âm; `projectId` của mỗi bài đã xong cũng là UUID chuẩn; `formatVersion` là 1. Sai bất kỳ chỗ nào (kể cả JSON hỏng hay `formatVersion` lớn hơn) thì coi như chưa có tiến độ và ghi `logger.warn("Lessons", "Lesson progress unreadable: <lý do>")`; lần ghi sau ghi lại từ trạng thái rỗng. Luồng `map { it[KEY] }.distinctUntilChanged()` trước khi giải mã, nên giá trị hỏng chỉ ghi `warn` một lần, không ghi lại mỗi khi khóa khác của `app_preferences` đổi. Đọc lỗi `IOException` thì thử lại như mẫu `DataStoreCounterRepository` (phát trạng thái rỗng, chờ tăng dần tối đa 30 giây, đọc lại, ghi log mỗi lần).
- `start`: kiểm `lessonId`, `attempt` theo luật trên (sai thì `IllegalArgumentException`, không ghi gì), rồi trong một `edit` đặt `inProgress[lessonId] = attempt` (thay lượt cũ nếu có), giữ `completed` (nhãn "Done" và mã dự án của lần xong trước còn tới khi lượt mới xong; xóa dự án cũ khi làm lại là việc của T-052).
- `setStep`: `require(step >= 0)`; trong một `edit`: không có lượt làm thì không ghi, trả `false`; `step >= stepCount` thì `IllegalArgumentException`; còn lại cập nhật `step`, trả `true`.
- `complete`: trong một `edit`: có lượt làm thì bỏ lượt, đặt `completed[lessonId] = CompletedLesson(attempt.projectId)` (thay mã dự án của lần xong trước), trả `max(0, nowMillis - startedAtMillis)`; không có lượt làm thì không ghi, trả `null`.
- Lỗi ghi `IOException` ném ra cho ViewModel bắt (CLAUDE.md, Viết một feature mới, bước 4). DataStore chạy các `edit` lần lượt, nên hai thao tác cùng lúc trên hai bài không đè nhau.
- Hai bản cùng giữ bước: `ProjectKind.Lesson(lessonId, step)` của T-001 nằm trong dự án bài học. Kho tiến độ là nguồn cho màn Bài học (đọc 18 dự án để vẽ thanh tiến độ là chậm và đọc dữ liệu không cần); T-039 ghi cả hai khi người dùng bấm "Next step" (câu hỏi 5 ở mục 8).

**Hilt** (`di/LessonsModule.kt`, `internal object`, theo mẫu `AssetsModule`): `@Provides @Singleton LessonContent = DefaultLessonContent(assets, settings, dataStore, PackExtractor(File(context.noBackupFilesDir, "lessons")), Dispatchers.IO, logger)`; `@Provides @Singleton LessonProgressStore = DataStoreLessonProgressStore(dataStore, logger)`. Chọn `noBackupFilesDir` như `:core:assets`: gói tải lại được, hệ thống không tự xóa như thư mục cache, và không sao lưu.

**Tệp bắt buộc trên R2 staging** (câu hỏi 1, anh Nhật chọn "T-048 đăng luôn"): thêm vào cuối `docs/ops/required-assets.txt`, theo đúng định dạng của tệp (mỗi dòng một đường dẫn tương đối với địa chỉ staging, `#` là chú thích; `scripts/setup-check.sh` bỏ phần sau `#` và khoảng trắng):

```
# Lesson content (plan T-048): the index and the two sample lessons of lessons_content_version v1.
lessons/v1/index.json
lessons/v1/bouncing-ball/thumb.webp
lessons/v1/bouncing-ball/lesson.zip
lessons/v1/squash-and-stretch/thumb.webp
lessons/v1/squash-and-stretch/lesson.zip
```

**Điều kiện trước phiên** (dev kiểm trước phiên, không phải lệnh `curl` trong phiên: proxy của `claude --cloud` chặn R2): dev đưa nội dung mẫu `lessons/v1/...` (năm tệp trên, đúng hợp đồng ở mục này) lên R2 staging, chạy `scripts/setup-check.sh` trên máy dev thấy cả năm dòng `ok`, rồi ghi kết quả vào danh sách chạy, trước khi phiên chạy T-048 bắt đầu. Lý do: `setup-check.sh` chạy trước mỗi phiên (`start-run.yml`) và mỗi sáng (`env-canary.yml`); PR của T-048 merge mà tệp chưa có thì kiểm tra đỏ, mở issue `setup-broken` và mọi phiên sau bị từ chối. Tệp trên R2 không bao giờ bị ghi đè (ADR-0015), nên nội dung đưa lên `v1` là bản `v1` mãi mãi: nên đưa hai bài mẫu thật đã duyệt (hay ghi rõ đây là bản mẫu của staging); bản sửa sau đi vào `v2`. Nội dung phải đúng hợp đồng: `index.json` chỉ gồm các bài có gói trong năm tệp (bỏ bài `walk-in-place` của tệp mẫu test), mỗi `lesson.zip` có `lesson.json` qua luật của `LessonPackFormat`, số bước và số khung khớp danh sách; đóng gói bằng lệnh ở mục này.

**Không làm trong ticket:** dọn thư mục phiên bản cũ (câu hỏi 6); dựng dự án từ gói (T-052).

Mẫu có sẵn được làm theo: `NetworkSettings` (khóa của module core), `DefaultRemoteAssets` (khóa theo đường dẫn, ghi tạm rồi đổi tên, `withContext(ioDispatcher)`), `DataStoreCounterRepository` (đọc thử lại khi `IOException`), định dạng JSON có phiên bản và tệp mẫu của plan T-001, test trên tệp DataStore thật của plan T-005.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `settings.gradle.kts` | Thêm `include(":core:lessons")` sau dòng `include` cuối của nhóm `:core:*` |
| Sửa | `CLAUDE.md` | Thêm dòng `:core:lessons` vào bảng Cấu trúc module: nội dung bài học trên R2 (danh sách, gói bài), kho tiến độ bài, khóa `lessons_content_version` |
| Sửa | `app/build.gradle.kts` | Thêm `implementation(project(":core:lessons"))` sau dòng `:core:network` |
| Sửa | `app/src/main/kotlin/com/vgl/draw/animation/config/AppRemoteKeys.kt` | Thêm `":core:lessons" to LessonsSettings.all()` và import |
| Sửa | `app/src/test/kotlin/com/vgl/draw/animation/config/RemoteConfigKeysTest.kt` | Thêm test `lessonsKeys_areListed` (mục 4); không đổi test có sẵn |
| Sửa | `docs/remote-config-keys.md` | Sinh lại bằng lệnh của `docs/ops/remote-config.md`, mục 3: thêm dòng `lessons_content_version` |
| Sửa | `docs/remote-config-template.json` | Sinh lại cùng lệnh: thêm tham số `lessons_content_version` |
| Sửa | `docs/ops/required-assets.txt` | Thêm một dòng chú thích và năm đường dẫn của mục 3.1 (Tệp bắt buộc trên R2 staging), ở cuối tệp, không đổi dòng có sẵn |
| Tạo | `core/lessons/build.gradle.kts` | Plugin thư viện, Hilt, kotlin serialization; phụ thuộc `:core:assets`, `:core:config`, `:core:data`, `:core:logging`, `kotlinx-serialization-json` |
| Tạo | `core/lessons/consumer-rules.pro` | Một dòng chú thích: kotlinx.serialization đã kèm luật R8 cho lớp `@Serializable` (mục 4.1) |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/LessonsSettings.kt` | Khóa `lessons_content_version`, `all()`, `contentVersionOrDefault` |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/content/LessonIndex.kt` | `LessonGroup`, `LessonEntry`, `LessonIndex` (thứ tự, số trong nhóm, số trong thư viện, bài tiếp, số đã xong) |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/content/LessonIndexFormat.kt` | Lớp JSON, `decode`, luật kiểm của danh sách |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/content/LessonPack.kt` | `LessonPack`, `LessonLayer`, `LessonFrame`, `LessonStep`, hàm tìm tệp ảnh |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/content/LessonPackFormat.kt` | Lớp JSON, `decode`, luật kiểm của `lesson.json` và tệp ảnh |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/content/PackExtractor.kt` | Giải nén an toàn, giới hạn, thư mục tạm rồi đổi tên, khóa theo bài, `InvalidPackException` |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/content/LessonContent.kt` | Interface `LessonContent`, `LessonLibraryState`, `LessonPackResult`, `LessonLoadFailure` |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/content/DefaultLessonContent.kt` | Cài đặt: tải danh sách, nhớ phiên bản, cập nhật ở nền, ảnh nhỏ, gói bài, log |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/progress/LessonProgress.kt` | `LessonAttempt` (có `fraction`), `LessonProgress`, `packVersionFor` |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/progress/LessonProgressStore.kt` | Interface công khai, KDoc |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/progress/DataStoreLessonProgressStore.kt` | Cài đặt trên DataStore: định dạng JSON có phiên bản, luật kiểm, đọc thử lại, ghi trong `edit` |
| Tạo | `core/lessons/src/main/kotlin/com/vgl/draw/animation/core/lessons/di/LessonsModule.kt` | Hilt cấp `LessonContent`, `LessonProgressStore` |
| Tạo | `core/lessons/src/test/kotlin/com/vgl/draw/animation/core/lessons/LessonsSettingsTest.kt` | Khóa, mặc định, phiên bản sai ra mặc định |
| Tạo | `core/lessons/src/test/kotlin/com/vgl/draw/animation/core/lessons/content/LessonIndexFormatTest.kt` | Tệp mẫu, thứ tự, bài tiếp, luật kiểm |
| Tạo | `core/lessons/src/test/kotlin/com/vgl/draw/animation/core/lessons/content/LessonPackFormatTest.kt` | Tệp mẫu, luật kiểm, tệp ảnh thiếu |
| Tạo | `core/lessons/src/test/kotlin/com/vgl/draw/animation/core/lessons/content/PackExtractorTest.kt` | Giải nén, từ chối tên nguy hiểm, giới hạn, không để lại thư mục dở, khóa |
| Tạo | `core/lessons/src/test/kotlin/com/vgl/draw/animation/core/lessons/content/DefaultLessonContentTest.kt` | Luồng `library()`, ảnh nhỏ, `loadPack`, log |
| Tạo | `core/lessons/src/test/kotlin/com/vgl/draw/animation/core/lessons/progress/LessonProgressTest.kt` | `fraction`, `packVersionFor` |
| Tạo | `core/lessons/src/test/kotlin/com/vgl/draw/animation/core/lessons/progress/DataStoreLessonProgressStoreTest.kt` | Tệp mẫu, bắt đầu, bước, hoàn thành, dữ liệu hỏng, lỗi đọc ghi, giữ qua lần mở lại |
| Tạo | `core/lessons/src/test/kotlin/com/vgl/draw/animation/core/lessons/TestDoubles.kt` | `FakeRemoteAssets` (bảng đường dẫn ra kết quả, ghi các đường dẫn đã gọi, chặn tạm một đường dẫn bằng `CompletableDeferred`), `FakeRemoteSettings` (`MutableStateFlow` mỗi khóa), `testDataStore(folder, scope)` (DataStore thật trên tệp tạm), `UnreadableDataStore`, `FlakyDataStore`, `UnwritableDataStore`, `zipOf(file, entries: Map<String, ByteArray>)`, `samplePackEntries()` |
| Tạo | `core/lessons/src/test/resources/index-v1.json` | Tệp mẫu danh sách, chép nguyên văn từ mục 3.1 |
| Tạo | `core/lessons/src/test/resources/lesson-v1.json` | Tệp mẫu `lesson.json`, chép nguyên văn từ mục 3.1 |
| Tạo | `core/lessons/src/test/resources/progress-v1.json` | Tệp mẫu tiến độ, chép nguyên văn từ mục 3.1 |

Không có ảnh chuẩn screenshot.

Nơi đọc các file có sẵn mà plan sửa (`grep -rln` trên `scripts/ .github/ */src/test */src/androidTest` và file build), và vì sao không phải sửa thêm:

- `settings.gradle.kts`: như plan T-001, mục 3.2. `.github/workflows/pr-check.yml` chỉ dùng tên file trong bộ lọc đường dẫn (PR chạy cả bản release); `app/src/test/kotlin/com/vgl/draw/animation/network/NetworkDialogLanguageTest.kt`, `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdsTestThemeMatchTest.kt`, `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/BrandTaglineLanguageTest.kt`, `feature/loading/src/test/kotlin/com/vgl/draw/animation/feature/loading/LoadingScreenLanguageTest.kt` chỉ tìm thư mục gốc bằng việc file có tồn tại; `scripts/ads-plan.py` chỉ đọc dòng `rootProject.name`; `scripts/tests/run.sh` đọc mọi dòng `include("...")` để kiểm module nằm dưới `UI_PATHS` của `scripts/ui-test-needed.sh`, `:core:lessons` nằm dưới `core/` như các module core khác.
- `CLAUDE.md`: như plan T-001. `app/src/androidTest/kotlin/com/vgl/draw/animation/AppLaunchTest.kt`, `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/NativeFullScreenOverlayScreenshotTest.kt`, `scripts/local-setup.sh`, `scripts/run-watchdog.py` chỉ nhắc tên file; `scripts/tests/run.sh` đọc dòng `git status --short -- '...'` và dòng `export ANDROID_HOME` ở mục Lệnh, plan chỉ thêm một dòng vào bảng module.
- `app/build.gradle.kts`: `scripts/setup-check.sh` đọc `ASSETS_BASE_URL` và `applicationId`; `scripts/release.sh` (không chạm, ADR-0049) và `scripts/ui-test-verdict.sh` đọc `applicationId`, `versionCode`, `versionName`; `.github/workflows/pr-check.yml` lọc theo tên file; `app/src/test/kotlin/com/vgl/draw/animation/ProductionPropertiesTest.kt` chỉ đọc khối `productFlavors` (khóa AppsFlyer, AppLovin của production). Plan chỉ thêm một dòng `implementation` trong khối `dependencies`, không đổi các dòng đó.
- `AppRemoteKeys.kt`: `RemoteConfigKeysTest` (sửa, mục 3.5), `StagingRemoteValuesModuleTest.everyLocalValue_isADeclaredKey` (chỉ cần tập khóa chứa mọi giá trị cục bộ; plan thêm khóa, không thêm giá trị cục bộ, nên vẫn qua).
- `docs/ops/required-assets.txt`: `scripts/setup-check.sh` đọc từng dòng (bỏ phần sau `#`, khoảng trắng) và kiểm `200` ở địa chỉ staging; test của script trong `scripts/tests/run.sh` dùng danh sách riêng qua `SETUP_CHECK_ASSETS_LIST`, không đọc tệp thật. Dòng mới đúng định dạng nên script không phải sửa; điều kiện là tệp đã có trên R2 (mục 3.1).
- `docs/remote-config-keys.md`, `docs/remote-config-template.json`: `app/build.gradle.kts` (truyền đường dẫn cho `RemoteConfigKeysTest`), `scripts/main-guard-verified.sh` (danh sách `GRADLE_READ_DOCS`, chỉ dùng tên file), `scripts/tests/run.sh` (test của `main-guard-verified.sh`, chỉ dùng tên file), `scripts/ads-plan.py` (đọc mọi dòng của bảng khóa để dựng trang Remote Config; dòng mới có đúng dạng của bảng, script không lọc theo module).

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có. Cờ `feature_lessons_enabled` thuộc T-006; module không đọc cờ (dữ liệu vẫn còn khi cờ tắt, KB-10).
- Quảng cáo: Không chạm.
- Sự kiện phân tích: Không chạm. Module chỉ cung cấp `startedAtMillis` và giá trị trả về của `complete` để T-052, T-053 ghi `lesson_start`, `lesson_complete`.
- Remote Config: khóa mới `lessons_content_version` (string, mặc định `"v1"`), đã có trong spec lessons mục 8 và câu hỏi 5; khai báo ở module sở hữu, sinh lại bảng khóa trong cùng PR (ADR-0046). Không thêm giá trị cục bộ cho staging: mặc định `v1` đúng với nội dung mẫu của T-049.
- File R2: đặt cách đặt tên `lessons/<phiên bản>/index.json`, `lessons/<phiên bản>/<mã bài>/thumb.webp`, `lessons/<phiên bản>/<mã bài>/lesson.zip` (điểm chặn 8). Chỉ đọc qua `RemoteAssets`, không ghi hay xóa gì trên R2. Phiên không cần R2: mọi test dùng `FakeRemoteAssets`. Năm tệp của `lessons/v1/` đăng vào `docs/ops/required-assets.txt`; dev đưa lên R2 staging và kiểm trước phiên (mục 3.1).
- Dữ liệu trên máy: hai khóa mới trong tệp `app_preferences` (`lessons_progress`, `lessons_index_version`), thư mục mới `noBackupFilesDir/lessons/`. Lần đầu đặt, không có dữ liệu cũ phải chuyển, nên không chạm điểm chặn 9 (như plan T-001, T-005); mọi lần đổi định dạng sau sẽ chạm.
- **Quyền truy cập:** Không thêm. Tải file đi qua `:core:assets` (quyền `INTERNET` đã có); giải nén và DataStore chỉ dùng `noBackupFilesDir` và `filesDir` của app, không cần quyền ở mọi phiên bản từ minSdk 26 tới targetSdk 36.
- **Thư viện và SDK:** Không thêm thư viện mới. `java.util.zip` có sẵn trong Android và JDK. Dùng thư viện đã có trong `gradle/libs.versions.toml`: `kotlinx-serialization-json` và plugin `kotlin-serialization` (thêm phụ thuộc ở `:core:lessons`); DataStore Preferences qua `api` của `:core:data`; Hilt qua `template.hilt`; `junit4`, `kotlinx-coroutines-test` do convention plugin thêm cho test.

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** DataStore thật dựng trên `backgroundScope` của `runTest` (mục 4) chưa được thử đúng cách đó; `reopen_keepsProgress` cần một scope hủy được.
- **Điều kiện chuyển:** DataStore trên `backgroundScope` treo, báo lỗi scheduler, hay không đóng được để mở lại cùng tệp.
- **Cách làm thay:** dùng mẫu đã chạy được của T-005 trên nhánh chính: `CoroutineScope(Dispatchers.IO + Job())`, `cancelAndJoin()` trong `finally` trước khi mở lại tệp. Chỉ đổi phần dựng test, câu khẳng định giữ nguyên.

### 3.5. Hành vi đã có bị đổi

Plan chỉ thêm code: một module mới, một dòng trong `AppRemoteKeys`, một khóa mới trong hai bảng khóa sinh tự động. `scripts/existing-tests.py` (kết quả ở mô tả PR) chỉ ra các test đọc những file đó; không câu khẳng định nào phải đổi. Hai test đỏ vì dữ liệu sinh tự động chưa có khóa mới được cập nhật dữ liệu theo ADR-0029, không sửa test.

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Bảng khóa và tệp mẫu Firebase khớp các khóa của `AppRemoteKeys` (không có `lessons_content_version`) | `AppRemoteKeys` thêm `:core:lessons` với khóa `lessons_content_version` | `RemoteConfigKeysTest.keysFile_matchesCode`, `RemoteConfigKeysTest.templateFile_matchesCode` | Giữ nguyên câu khẳng định. Dữ liệu: sinh lại `docs/remote-config-keys.md` và `docs/remote-config-template.json` bằng `./gradlew :app:testStagingDebugUnitTest -PremoteConfigKeys.update=true` (ADR-0029: dữ liệu chưa có khóa plan thêm); `git diff` của hai tệp chỉ có dòng, mục của `lessons_content_version` |
| Mọi tên khóa không trùng giữa các module | Thêm một tên mới | `RemoteConfigKeysTest.everyKeyName_isUniqueAcrossModules` | Giữ nguyên; `lessons_content_version` không trùng khóa nào |
| Mọi khóa số ngoài `:core:ads` có đuôi đơn vị | Khóa mới là string | `RemoteConfigKeysTest.everyNumberKey_endsWithUnit` | Giữ nguyên; test chỉ xét khóa `LONG`, `DOUBLE` |
| Khóa quảng cáo, AppsFlyer, cờ và vị trí quảng cáo có trong `AppRemoteKeys` | Thêm một module khác, không đổi các module đó | `RemoteConfigKeysTest.adsNumberKeys_stateUnitInDescription`, `analyticsKeys_areListed`, `everyFeatureFlagAndPlacement_isListed`, `everyPlacementShowKey_isInTableOff` | Giữ nguyên; các test chỉ xét `:core:ads`, `:core:analytics`, `FeatureFlag`, `AdPlacement` |
| Mọi giá trị cục bộ của staging là khóa đã khai báo | Thêm khóa, không thêm giá trị cục bộ | `StagingRemoteValuesModuleTest.everyLocalValue_isADeclaredKey` | Giữ nguyên |

## 4. Kiểm thử

Mọi test là unit test JVM (JUnit 4, `kotlinx-coroutines-test`, `TemporaryFolder`), không cần Robolectric: module chỉ dùng lớp Android ở Hilt module. DataStore thật dựng bằng `PreferenceDataStoreFactory.create(scope = backgroundScope, produceFile = { File(folder, "test.preferences_pb") })` trong `runTest` (như plan T-005). `DefaultLessonContent` và `PackExtractor` dựng với `StandardTestDispatcher(testScheduler)` và `RecordingAppLogger`. "Tệp mẫu sửa X" nghĩa là đọc tệp mẫu thành `JsonObject`, đổi đúng chỗ X, rồi ghi lại thành chuỗi. "Gói mẫu" là `zipOf(...)` của `samplePackEntries()`: `lesson.json` (chép từ `lesson-v1.json`), `cells/1/0.png`, `cells/3/1.png`, `guides/0.png`, `guides/1.png`, `demo/0/0.png`, `demo/0/1.png`, mỗi tệp ảnh là vài byte khác nhau (module không giải mã ảnh). `FakeRemoteAssets` mặc định trả `Ready` cho `lessons/v1/index.json` (tệp mẫu danh sách) và `lessons/v1/bouncing-ball/lesson.zip` (gói mẫu), `Unavailable(NOT_FOUND)` cho đường dẫn khác.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| Khóa Remote Config đúng tên, kiểu, mặc định | Unit test | `LessonsSettingsTest.contentVersion_isStringV1`: tên `lessons_content_version`, kiểu `STRING`, mặc định `"v1"`; `all()` bằng `listOf(CONTENT_VERSION)` |
| Giá trị khóa sai không làm hỏng màn Bài học | Unit test | `LessonsSettingsTest.contentVersionOrDefault_rejectsInvalid`: `"v1"`, `"v2"`, `"v120"` trả nguyên; `""`, `"V2"`, `"v2/"`, `"v0"`, `"2"`, `"v1/../x"`, `"v1.2"` trả `"v1"` |
| Khóa có trong bảng khóa của app | Unit test | `RemoteConfigKeysTest.lessonsKeys_areListed`: `AppRemoteKeys.byModule[":core:lessons"] == LessonsSettings.all()` (như `networkKeys_areListed`); cùng `keysFile_matchesCode`, `templateFile_matchesCode` sau khi sinh lại |
| Định dạng danh sách đúng hợp đồng | Unit test | `LessonIndexFormatTest.decode_v1Sample_returnsIndex`: `decode(<tệp mẫu>, "v1")` là `Ok`; `groups` là `basics`, `motion`; ba bài đủ mọi trường như tệp mẫu |
| KB-1, KB-6: thứ tự, số trong nhóm, số trong thư viện, bài tiếp | Unit test | `LessonIndexFormatTest.sampleIndex_orderAndNumbers`: `ordered` là `bouncing-ball`, `squash-and-stretch`, `walk-in-place`; `numberInGroup` 1, 2, 1; `libraryNumber` 1, 2, 3; `next("bouncing-ball")` là `squash-and-stretch`, `next("squash-and-stretch")` là `walk-in-place` (sang nhóm sau), `next("walk-in-place") == null`; `lessonsIn("basics")` có 2 bài; `completedCount("basics", setOf("bouncing-ball", "walk-in-place")) == 1`; mã không có: `next`, `entry` trả `null`, `libraryNumber` trả `null` |
| Nội dung mới có trường lạ vẫn đọc được | Unit test | `LessonIndexFormatTest.decode_unknownField_isIgnored`: tệp mẫu thêm `"extra": 1` ở gốc và ở một bài ra `Ok` |
| Danh sách sai ra "không hợp lệ", không ném lỗi | Unit test | `LessonIndexFormatTest.decode_invalid_returnsInvalid`, mỗi ca sửa một chỗ của tệp mẫu và phải ra `Invalid`: chuỗi `"{"`; chuỗi rỗng; thiếu `formatVersion`; `formatVersion` 2 (lý do chứa `newer`); `groups` rỗng; trùng mã nhóm; trùng mã bài; mã bài `"Bouncing"`, `"a--b"`, `"../x"`, chuỗi 41 ký tự; tên bài `" "`; `group` của bài là `"effects"` (không có nhóm); nhóm `motion` không còn bài; `minutes` 0 và 121; `frameCount` 0; `stepCount` 0 và 51; thiếu `pack`; `pack` là `"../x.zip"`; `thumb` là `".hidden/thumb.webp"`; `pack` là `"walk-in-place/lesson.zip"` ở bài `bouncing-ball` (thư mục của bài khác); `thumb` là `"bouncing-ball/thumb.png"`; `pack` là `"bouncing-ball/pack.zip"`. Đối chứng: tệp mẫu không sửa ra `Ok` trong cùng test |
| Định dạng gói bài đúng hợp đồng | Unit test | `LessonPackFormatTest.decode_v1Sample_returnsPack`: thư mục tạm có các tệp của gói mẫu; `decode(<lesson-v1.json>, "bouncing-ball", "v1", dir)` là `Ok`; `aspectRatio == "9:16"`, `resolution == "720p"`, `frameRate == 12`, `background == "white"`, 2 layer `Ball`, `Shadow`, 4 khung `hold` 1, 1, 2, 1, 3 bước đúng chữ của mẫu; `guideFile(0)`, `guideFile(1)` là tệp có thật, `guideFile(2) == null`; `demoFiles(0)` là `demo/0/0.png`, `demo/0/1.png`; `demoFiles(1)` rỗng; `cellFile(1, 0)` có, `cellFile(0, 0) == null` |
| Gói sai ra "không hợp lệ" | Unit test | `LessonPackFormatTest.decode_invalid_returnsInvalid`, mỗi ca sửa một chỗ và phải ra `Invalid`: JSON hỏng; `formatVersion` 2; `id` khác mã yêu cầu; `aspectRatio` `"4:5"`; `resolution` `"4k"`; `frameRate` 10; `background` `"red"`; 0 layer; 6 layer; tên layer trống; 0 khung; `hold` 0 và 5; 0 bước; `frame` 4; `layer` 2; `demoFrameCount` -1 và 121; tên bước trống; xóa tệp `guides/1.png`; xóa tệp `demo/0/1.png`. Đối chứng: mẫu không sửa ra `Ok`; xóa `cells/1/0.png` vẫn ra `Ok` (ô dựng sẵn không bắt buộc) |
| Giải nén gói đúng | Unit test | `PackExtractorTest.extract_samplePack_writesAllFiles`: `extract(<gói mẫu>, "v1", "bouncing-ball")` trả `root/v1/bouncing-ball`; mỗi mục có đúng byte; `root` không còn thư mục `.tmp-*`; zip có mục thư mục (`guides/`) cũng qua |
| Đã giải nén thì không giải nén lại | Unit test | `PackExtractorTest.extract_existingTarget_returnsWithoutRewriting`: sau lần đầu, ghi đè `guides/0.png` bằng byte khác; gọi lại với chính tệp zip trả cùng thư mục và tệp vẫn giữ byte đã ghi đè |
| Mục rác của Finder bị bỏ qua | Unit test | `PackExtractorTest.extract_ignoresMacMetadata`: gói mẫu thêm `__MACOSX/guides/._0.png`, `.DS_Store`, `guides/.DS_Store`; giải nén qua, thư mục đích không có các mục đó, các tệp khác đủ. Đối chứng: `Guides/0.png` (không phải mục rác) vẫn bị từ chối |
| Tên mục nguy hiểm bị từ chối, không ghi ra ngoài | Unit test | `PackExtractorTest.extract_unsafeName_throwsAndWritesNothing`, mỗi ca một zip gồm `lesson.json` và một mục: `../evil.txt`, `a/../../evil.txt`, `/abs.txt`, `a\\b.png`, `Guides/0.png`, `.hidden/../../evil.txt`; ném `InvalidPackException`; không có `evil.txt` nào trong thư mục tạm của test; `root/v1/bouncing-ball` không tồn tại; `root` không còn `.tmp-*` |
| Zip quá lớn hay trùng tên bị từ chối | Unit test | `PackExtractorTest.extract_limits_throw`: `PackExtractor(root, maxEntries = 3)` với 4 mục; `PackExtractor(root, maxBytes = 100)` với một mục 101 byte; `PackExtractor(root, maxBytes = 100)` với mục `__MACOSX/x` 101 byte (mục bị bỏ qua vẫn tính byte); `PackExtractor(root, maxEntries = 3)` với `lesson.json` và 3 mục `__MACOSX/a`, `__MACOSX/b`, `__MACOSX/c`; zip có hai mục `lesson.json` (`ZipOutputStream` từ chối tên trùng, nên dựng zip có `lesson.json` và `lesson.jsoo`, nén kiểu `STORED`, rồi thay mọi chuỗi byte `lesson.jsoo` trong mảng byte của zip bằng `lesson.json`, cùng độ dài; `ZipInputStream` chỉ đọc phần đầu của từng mục nên đọc ra hai mục cùng tên); đều ném `InvalidPackException`, không để lại đích hay `.tmp-*`. Đối chứng: `maxBytes = 101` với cùng mục qua |
| Tệp không phải zip, zip thiếu `lesson.json` | Unit test | `PackExtractorTest.extract_notAZipOrNoLessonJson_isRejected`: tệp chứa chữ `"hello"` (`ZipInputStream` không thấy mục nào), zip chỉ có `guides/0.png`, và gói mẫu cắt còn nửa đầu số byte (zip cụt; `ZipException` đổi thành `InvalidPackException`): `extract` ném `InvalidPackException`, không để lại đích hay `.tmp-*`; `DefaultLessonContentTest.loadPack_notAZip_returnsInvalid`: gói là tệp `"hello"` thì `loadPack` ra `Unavailable(INVALID)` |
| Hai lần mở cùng một bài cùng lúc | Unit test | `PackExtractorTest.extract_concurrentSameLesson_extractsOnce`: dispatcher 4 luồng thật (`Executors.newFixedThreadPool(4).asCoroutineDispatcher()`, đóng sau test); 8 lần `extract` song song bằng `async`; không lần nào ném lỗi; mọi lần trả cùng thư mục; `root` chỉ có `v1` (không còn `.tmp-*`); bộ đếm của `onExtract` bằng 1 |
| KB-2: lần đầu, đang tải rồi có danh sách | Unit test | `DefaultLessonContentTest.library_firstOpen_emitsLoadingThenReady`: DataStore rỗng, khóa `v1`; `library().take(2).toList()` là `[Loading, Ready("v1", <index mẫu>)]`; sau đó khóa `lessons_index_version` trong DataStore là `"v1"` |
| KB-2: lần sau dùng bản trên máy, không hiện đang tải | Unit test | `DefaultLessonContentTest.library_savedVersion_emitsReadyWithoutLoading`: DataStore có `lessons_index_version = "v1"`, khóa `v1`; giá trị đầu tiên của `library()` là `Ready("v1", ...)`, không có `Loading` trong 2 giây ảo (`withTimeoutOrNull`), `FakeRemoteAssets` được gọi đúng 1 lần cho `lessons/v1/index.json`. Đối chứng: cùng ca với DataStore rỗng thì giá trị đầu là `Loading` |
| KB-2: kiểm bản mới ở nền, cập nhật khi có | Unit test | `DefaultLessonContentTest.library_newVersion_keepsOldUntilLoaded`: DataStore nhớ `v1`, khóa `v2`, `lessons/v2/index.json` bị chặn bằng `CompletableDeferred`; giá trị đầu là `Ready("v1")`; trong lúc chặn không có giá trị nào khác (không `Loading`); mở chặn với danh sách `v2` (tệp mẫu đổi tên bài đầu) thì giá trị kế là `Ready("v2")`; DataStore nhớ `"v2"` |
| KB-2: bản mới lỗi thì giữ bản cũ, không báo lỗi | Unit test | `DefaultLessonContentTest.library_newVersionFails_keepsOldAndLogs`: nhớ `v1`, khóa `v2` trả `Unavailable(NETWORK)`; `library()` chỉ phát `Ready("v1")` trong 2 giây ảo, không có `Unavailable`; `RecordingAppLogger` có một `WARN` thẻ `Lessons` chứa `v2` và `NETWORK`; DataStore vẫn nhớ `"v1"` |
| KB-2: khóa đổi lúc đang xem | Unit test | `DefaultLessonContentTest.library_keyChangesWhileCollecting_emitsNewVersion`: khóa `v1` rồi `FakeRemoteSettings` đổi sang `v2` (có danh sách); giá trị là `Loading`, `Ready("v1")`, `Ready("v2")`; đổi lại `v1` thì thêm `Ready("v1")` |
| KB-2: lỗi tải khi chưa có gì trên máy | Unit test | `DefaultLessonContentTest.library_firstOpenFails_emitsUnavailable`: khóa `v1`; danh sách trả `Unavailable(NETWORK)` thì `[Loading, Unavailable(NETWORK)]`; `NOT_FOUND` thì `Unavailable(NOT_FOUND)`; `OTHER` thì `Unavailable(OTHER)`; tệp có mà chứa `"{"` thì `Unavailable(INVALID)` |
| KB-2: "Try again" tải lại | Unit test | `DefaultLessonContentTest.library_collectAgain_retries`: lần thu đầu ra `Unavailable(NETWORK)`; đổi `FakeRemoteAssets` sang `Ready`; thu lại ra `[Loading, Ready("v1")]`; `lessons/v1/index.json` được gọi 2 lần |
| Khóa sai trên Firebase | Unit test | `DefaultLessonContentTest.library_invalidKey_usesDefaultAndLogs`: khóa `"v2/"`; `library()` ra `Ready("v1")`; đường dẫn đã gọi chỉ có `lessons/v1/index.json`; có một `WARN` thẻ `Lessons` |
| Bản đã nhớ không còn dùng được | Unit test | `DefaultLessonContentTest.library_savedVersionUnusable_fallsBackToKey`: nhớ `v1` mà `lessons/v1/index.json` trả `Unavailable(NETWORK)` (dữ liệu app bị xóa một phần), khóa `v2` có danh sách; giá trị là `[Loading, Ready("v2")]` |
| Ghi phiên bản lỗi không làm hỏng danh sách | Unit test | `DefaultLessonContentTest.library_saveVersionFails_stillReady`: `UnwritableDataStore` (đọc ra rỗng, ghi ném `IOException`); `library()` ra `[Loading, Ready("v1")]`, có một `WARN`; đọc DataStore lỗi (`UnreadableDataStore`) cũng ra `[Loading, Ready("v1")]` |
| Ảnh nhỏ | Unit test | `DefaultLessonContentTest.thumbnail_readyOrNull`: `lessons/v1/bouncing-ball/thumb.webp` trả `Ready(file)` thì `thumbnail("v1", entry)` là đúng tệp đó; trả `Unavailable` thì `null` |
| KB-3: tải và mở nội dung bài | Unit test | `DefaultLessonContentTest.loadPack_sample_returnsReadyPack`: `loadPack("v1", "bouncing-ball")` là `Ready`; `pack.lessonId == "bouncing-ball"`, `contentVersion == "v1"`, 3 bước, `pack.dir` là `<packRoot>/v1/bouncing-ball`; đường dẫn đã gọi có `lessons/v1/index.json` và `lessons/v1/bouncing-ball/lesson.zip` |
| KB-3: tải nội dung bài lỗi thì báo lỗi, thử lại được | Unit test | `DefaultLessonContentTest.loadPack_failures`, mỗi ca dùng một thư mục tạm và một `DefaultLessonContent` riêng (thư mục giải nén đã có thì không giải nén lại): gói trả `Unavailable(NETWORK)` thì `Unavailable(NETWORK)`; bài `"no-such-lesson"` thì `Unavailable(NOT_FOUND)`; danh sách lỗi `NETWORK` thì `Unavailable(NETWORK)`; gói có mục `../evil.txt` thì `Unavailable(INVALID)`; `lesson.json` mang `"id": "other"` thì `Unavailable(INVALID)`; phiên bản `"v2/"` thì `Unavailable(INVALID)`; danh sách cũ có `stepCount` 4 cho `bouncing-ball` (gói có 3 bước) thì `Unavailable(INVALID)`; mỗi lỗi có một `WARN` thẻ `Lessons`, không chứa chữ của `lesson.json`; tiếp ca lỗi `NETWORK` của gói, cùng đối tượng, gọi lại sau khi `FakeRemoteAssets` trả `Ready` thì ra `Ready` và có một `INFO` chứa `v1/bouncing-ball` |
| KB-4 (đặc biệt): bài dở mở bằng bản cũ | Unit test | `DefaultLessonContentTest.loadPack_oldVersion_usesOldPaths`: khóa là `v2`, `FakeRemoteAssets` có cả `v1` và `v2`; `loadPack(packVersionFor("bouncing-ball", <tiến độ có lượt làm v1>, "v2"), "bouncing-ball")` chỉ gọi đường dẫn `lessons/v1/...`, `pack.contentVersion == "v1"`; `LessonProgressTest.packVersionFor_inProgressUsesAttemptVersion`: có lượt làm `v1` thì `"v1"`; không có lượt làm thì `"v2"`; bài đã xong mà không có lượt làm thì `"v2"` |
| KB-4: thanh tiến độ theo bước | Unit test | `LessonProgressTest.fraction_followsStepAndCount`: `step = 2`, `stepCount = 6` ra `0.5f`; `step = 0`, `stepCount = 6` ra `1f / 6`; `step = 5`, `stepCount = 6` ra `1f` |
| Định dạng tiến độ đúng hợp đồng | Unit test | `DataStoreLessonProgressStoreTest.sampleFormat_readAndWrite`: ghi thẳng chuỗi của `progress-v1.json` vào khóa `lessons_progress` của DataStore thật; `progress.first()` bằng `LessonProgress(mapOf("bouncing-ball" to CompletedLesson("6f1c2a3e-0000-4000-8000-000000000002")), mapOf("squash-and-stretch" to LessonAttempt("v1", 2, 6, "6f1c2a3e-0000-4000-8000-000000000001", 1763769600000)))`; rồi DataStore rỗng, `start` và `complete` để ra cùng trạng thái (`start("bouncing-ball", <lượt có projectId ...0002>)`, `complete("bouncing-ball", ...)`, `start("squash-and-stretch", ...)`, `setStep("squash-and-stretch", 2)`); `Json.parseToJsonElement` của chuỗi trong khóa bằng phần tử JSON của tệp mẫu |
| Chưa có tiến độ | Unit test | `DataStoreLessonProgressStoreTest.empty_returnsEmptyProgress`: DataStore rỗng ra `LessonProgress()`. Đối chứng: sau `start` thì không còn rỗng |
| KB-3: bắt đầu bài ở bước 1 | Unit test | `DataStoreLessonProgressStoreTest.start_storesAttempt`: `start("bouncing-ball", LessonAttempt("v1", 0, 3, <uuid>, 1000))` thì `inProgress["bouncing-ball"]` đúng lượt đó, `completed` rỗng |
| KB-4: ghi bước đang làm | Unit test | `DataStoreLessonProgressStoreTest.setStep_updatesStep`: sau `start`, `setStep("bouncing-ball", 2)` trả `true`, bước là 2; `setStep("walk-in-place", 1)` (bài chưa bắt đầu) trả `false` và chuỗi trong khóa `lessons_progress` không đổi; trên một DataStore rỗng riêng, `setStep` trả `false` và không có khóa `lessons_progress`; `setStep(.., 3)` với `stepCount` 3 và `setStep(.., -1)` ném `IllegalArgumentException`, bước vẫn là 2 |
| KB-5: hoàn thành chỉ một lần mỗi lần làm xong | Unit test | `DataStoreLessonProgressStoreTest.complete_onceThenNull`: `start` với `startedAtMillis = 1_000`; `complete(id, 61_000)` trả `60_000`; `completed[id]` là `CompletedLesson(<projectId của lượt>)`, `inProgress` không còn; gọi lại `complete(id, 70_000)` trả `null`, `completed` vẫn có bài; `complete` một bài chưa bắt đầu trả `null` và không ghi gì; `complete` với `nowMillis` nhỏ hơn mốc bắt đầu trả `0` |
| KB-5: làm lại bài đã xong vẫn giữ "Done" | Unit test | `DataStoreLessonProgressStoreTest.start_completedLesson_keepsCompleted`: sau `start` rồi `complete`, `start` lần nữa với bước 0: `completed` vẫn có bài, `inProgress` có lượt mới bước 0; lượt mới có `projectId` khác; trong lúc làm lại `completed[id]` vẫn là mã dự án cũ; `complete` lần nữa: `completed[id]` là mã dự án mới |
| Bắt đầu lại thay lượt cũ | Unit test | `DataStoreLessonProgressStoreTest.start_inProgress_replacesAttempt`: lượt `v1` bước 2 rồi `start` lượt `v2` bước 0 thì còn đúng lượt mới |
| Không ghi dữ liệu sai | Unit test | `DataStoreLessonProgressStoreTest.start_invalid_throwsAndWritesNothing`: mã bài `"Bad Id"`; `contentVersion` `"x"`; `projectId` `"1-1-1-1-1"` và UUID viết hoa; `stepCount` 0; `step` bằng `stepCount`; `startedAtMillis` -1: đều ném `IllegalArgumentException`, DataStore không có khóa `lessons_progress` |
| Giá trị hỏng chỉ cảnh báo một lần | Unit test | `DataStoreLessonProgressStoreTest.corruptValue_warnsOnceAcrossOtherKeyChanges`: khóa `lessons_progress` chứa `"{"`; trong lúc thu `progress`, ghi khóa khác `sample_count` ba lần; chỉ có 1 `WARN` |
| Dữ liệu tiến độ hỏng không làm dừng app | Unit test | `DataStoreLessonProgressStoreTest.corruptValue_readsEmptyAndLogs`: khóa `lessons_progress` chứa `"{"`, rồi `progress-v1.json` có `formatVersion` 2, rồi có `step` 6 với `stepCount` 6: mỗi ca `progress.first()` là `LessonProgress()` và có một `WARN` thẻ `Lessons` không chứa `projectId`; sau đó `start` ghi được và đọc lại ra lượt vừa ghi |
| Đọc lỗi thì thử lại | Unit test | `DataStoreLessonProgressStoreTest.readIOException_retries`: `FlakyDataStore(failuresBeforeSuccess = 2, stored = <tiến độ mẫu>)`; giá trị đầu là `LessonProgress()`, sau `advanceTimeBy(3_000)` rồi `runCurrent()` là tiến độ mẫu; có 2 `WARN` |
| Ghi lỗi báo cho người gọi | Unit test | `DataStoreLessonProgressStoreTest.writeIOException_throws`: `UnwritableDataStore`; `start`, `setStep`, `complete` đều ném lỗi `is IOException` (so loại) |
| Hai thao tác cùng lúc không đè nhau | Unit test | `DataStoreLessonProgressStoreTest.concurrentWrites_bothKept`: DataStore thật; `start` hai bài trước; rồi song song bằng `async`: `setStep("bouncing-ball", 1)` và `complete("squash-and-stretch", ...)`; sau `awaitAll`: bài đầu bước 1, bài sau đã xong |
| KB-4, KB-10: tắt app hay tắt cờ không mất tiến độ | Unit test | `DataStoreLessonProgressStoreTest.reopen_keepsProgress`: ghi tiến độ bằng một DataStore thật trên tệp tạm, hủy scope của nó, dựng DataStore và kho mới trên cùng tệp (scope mới); `progress.first()` bằng trạng thái đã ghi |
| Khóa DataStore là hợp đồng | Unit test | `DataStoreLessonProgressStoreTest.keyName_isLessonsProgress` và `DefaultLessonContentTest.savedVersionKey_isLessonsIndexVersion`: đọc thô `stringPreferencesKey("lessons_progress")`, `stringPreferencesKey("lessons_index_version")` sau khi ghi thấy giá trị |
| Module tuân luật phụ thuộc | Lệnh | `scripts/check.sh` chạy `checkModuleDependencies`: `:core:lessons` chỉ phụ thuộc `:core:assets`, `:core:config`, `:core:data`, `:core:logging` |
| Bảng module của `CLAUDE.md` có dòng mới | Kiểm tra thủ công (người: đọc tài liệu) | Người duyệt PR của task đọc dòng `:core:lessons` trong bảng |

**Ngoại lệ viết test trước:** `di/LessonsModule.kt` (Hilt), `settings.gradle.kts`, `core/lessons/build.gradle.kts`, `consumer-rules.pro`, dòng `implementation` của `app/build.gradle.kts`, dòng của `CLAUDE.md`: phần nối và cấu hình, không có hành vi riêng; `scripts/check.sh` build `:app` nên đồ thị Hilt có hai binding mới được kiểm biên dịch. Sinh lại `docs/remote-config-keys.md` và `docs/remote-config-template.json` ở bước 4 (dữ liệu sinh từ code).

Thứ tự commit: bước 3 commit các tệp test, `TestDoubles.kt`, ba tệp mẫu (chép nguyên văn từ mục 3.1), test mới trong `RemoteConfigKeysTest`, cùng khung tối thiểu để biên dịch (module, `settings.gradle.kts`, dòng `implementation` của `:app`, `LessonsSettings` có khai báo khóa, dòng của `AppRemoteKeys`, lớp mô hình có `@Serializable`, interface, thân hàm `TODO()`). Test đỏ vì `NotImplementedError` hay sai kết quả; `keysFile_matchesCode`, `templateFile_matchesCode` đỏ vì bảng khóa chưa sinh lại. Bước 4 viết phần cài đặt và sinh lại hai bảng khóa. Các test của giá trị mặc định (`empty_returnsEmptyProgress`, `LessonsSettingsTest.contentVersion_isStringV1`, `lessonsKeys_areListed`) có thể qua ngay ở bước 3 vì khung đã có khai báo; đối chứng của chúng ghi sẵn ở từng dòng (sau `start` thì không rỗng; bảng khóa sinh lại có dòng mới). Hai test định dạng có thể qua ngay nếu khung mô hình đủ; khi đó ghi vào nhật ký lần đối chứng: đổi tạm tên trường `minutes` thành `durationMinutes` trong lớp JSON thì `decode_v1Sample_returnsIndex` đỏ.

### 4.1. Chạy thử

- **Đã chạy thử:** 2026-11-22, trong worktree nháp từ `origin/main` 5ea6ec0, không commit: module `:core:lessons` khung (đúng `build.gradle.kts`, `consumer-rules.pro` của mục 3.2), `LessonsSettings` với khóa thật, một hàm giải nén bằng `ZipInputStream` có kiểm tên và giới hạn byte, đọc `lesson.json` bằng kotlinx.serialization (`ignoreUnknownKeys`, lớp sealed có `@SerialName`), một kho tiến độ thử ghi JSON vào `stringPreferencesKey` qua `dataStore.edit`, Hilt module cấp kho thử; `:app` phụ thuộc module, `AppRemoteKeys` có dòng `:core:lessons`, hai bảng khóa sinh lại. Ba test JVM: giải nén rồi đọc `lesson.json` có trường lạ; zip có mục `../evil.txt` ném `IOException` và không ghi ra ngoài; ghi rồi đọc tiến độ. Để R8 thật sự xử lý mã giải nén và JSON, nhánh nháp tạm gọi hàm giải nén trong `TemplateApplication.onCreate` dưới một điều kiện không bao giờ đúng (chỉ để thử, plan không làm vậy).
- **Lệnh:** `scripts/check.sh`, rồi `./gradlew :core:lessons:lintDebug :app:assembleStagingRelease`. Sinh bảng khóa: `./gradlew :app:testStagingDebugUnitTest --tests '*RemoteConfigKeysTest*' -PremoteConfigKeys.update=true`.
- **Kết quả:** `scripts/check.sh` qua ("Cổng chất lượng: QUA", 7 phút 28 giây): "Module dependency rule OK." với `:core:lessons` phụ thuộc `:core:assets`, `:core:config`, `:core:data`, `:core:logging`; ktlint qua (lần đầu báo một dòng test dài 121 ký tự, xuống dòng là qua; phiên chạy `ktlintFormat` trước `check.sh`); `./core/lessons: 3 test, 0 lỗi`; `RemoteConfigKeysTest` qua sau khi sinh lại hai bảng khóa (diff chỉ có dòng, mục `lessons_content_version`). `./gradlew :core:lessons:lintDebug :app:assembleStagingRelease` qua (BUILD SUCCESSFUL, 2 phút 24 giây); lint của module "No issues found"; bản mapping R8 giữ `TrialPack$$serializer`, `TrialStep$$serializer` (chỉ đổi tên lớp), nên không cần luật giữ lớp riêng, như plan T-001 đã thấy. Thêm một test JVM dùng DataStore thật (`PreferenceDataStoreFactory.create` trên tệp tạm trong `runTest`): ghi, hủy scope, dựng DataStore mới trên cùng tệp đọc lại đúng giá trị; `:core:lessons:testDebugUnitTest` 4 test qua. Hàm giải nén thử ném `IOException` cho tên nguy hiểm; plan dùng `InvalidPackException` riêng để tách lỗi nội dung với lỗi đĩa. Chưa chạy trên máy thật; lần chạy thật đầu tiên là T-050, T-052. Phương án dự phòng: không có (mục 3.4).

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 7 (module core dùng chung `:core:lessons`, mới; sửa `AppRemoteKeys` của `:app`), 8 (cách đặt tên và đường dẫn file bài học trên R2). Không chạm điểm 9: hai khóa DataStore và thư mục mới, không có dữ liệu cũ phải chuyển (mục 3.3). Không chạm điểm khác: không quảng cáo (1), không SDK (2; `java.util.zip` có sẵn), không quyền (3), không sự kiện phân tích (4), không đổi hộp thoại đồng ý hay thứ tự khởi tạo (5), không liên kết ngoài (6).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 3 | Module mới với nhiều giao diện công khai (`LessonContent`, `LessonProgressStore`, mô hình), cộng sửa `:app` (`AppRemoteKeys`, file build), `settings.gradle.kts`, `CLAUDE.md` và hai bảng khóa sinh tự động |
| B. Độ rõ của yêu cầu | 1 | Spec đã xác nhận phần lớn (đường dẫn có phiên bản, khóa, bài dở giữ bản cũ). Bố cục trong gói bài, cách tính thanh tiến độ, chỗ nhớ bước (kho tiến độ hay dự án), việc đăng tệp bắt buộc, mã dự án của lần xong và trần layer là AI đề xuất, đã được anh Nhật trả lời ngày 2026-11-22 (mục 8, câu 1 tới 5, 8, 9); thêm điều kiện trước phiên về tệp trên R2 staging |
| C. Khả năng kiểm chứng tự động | 0 | Mọi hành vi có unit test JVM với tệp mẫu, zip dựng trong test và DataStore thật; chỉ dòng tài liệu cần người đọc |
| D. Ảnh hưởng chéo | 2 | Chưa feature nào dùng, nhưng T-039, T-050, T-052, T-053 và nội dung của T-049 dựa vào định dạng và giao diện này; hai bảng khóa cũng bị T-006 (đã merge), T-007 (cùng đợt) sinh lại; `settings.gradle.kts` cũng bị T-001 và T-044 (cùng đợt) sửa; `CLAUDE.md` cũng bị T-001, T-004 sửa |
| E. Độ mới của kỹ thuật | 2 | Giải nén zip an toàn (`ZipInputStream`, chống đường dẫn ra ngoài, giới hạn byte) chưa có trong dự án nhưng phổ biến; phần còn lại là biến thể của mẫu có sẵn. Đã chạy thử bằng `scripts/check.sh` (mục 4.1) |
| **Tổng** | 8 | **Mức: Cao** (chạm điểm chặn 7, 8) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `settings.gradle.kts`, `CLAUDE.md` (bảng module), `app/build.gradle.kts`, `AppRemoteKeys.kt`, `RemoteConfigKeysTest.kt`, `docs/remote-config-keys.md`, `docs/remote-config-template.json` (`scripts/ads-plan.py` đọc), tệp DataStore `app_preferences` (hai khóa mới). Module mới `:core:lessons`.
- Task khác có thể xung đột:
  - T-001 (RUN-20261122-01, đã merge) thêm `include(":core:project")` vào `settings.gradle.kts` và một dòng vào bảng module của `CLAUDE.md`. T-048 chạy sau T-001 (`depends_on`), thêm dòng của mình sau dòng của T-001; xung đột chỉ là chữ, giải bằng cách thêm lại dòng trên nhánh mới nhất. T-004 (đã merge) sửa dòng `:core:project` của bảng module; T-044 (cùng đợt) cũng thêm dòng vào `settings.gradle.kts`: cùng cách giải.
  - T-006 (đã merge) và T-007 (cùng đợt 20261122-02) sinh lại hai bảng khóa. Hai tệp sinh từ code nên xung đột giải bằng lệnh sinh lại trên nhánh mới nhất; danh sách chạy xếp tuần tự các task này.
  - T-005 (đã merge) thêm khóa vào `app_preferences` và sửa `core/data/build.gradle.kts`; T-048 không sửa `:core:data`, tên khóa không trùng (mục 3.1).
  - T-002 có thể cho `:core:project` phụ thuộc `:core:data`; `:core:lessons` không phụ thuộc `:core:project`, nên không có vòng nếu về sau `:core:project` cần `:core:lessons`.
  - T-039 (màn vẽ) đọc `LessonPack` và gọi `setStep`; tiêu đề "Lesson 1 · Bouncing ball" của màn vẽ cần tên bài và `libraryNumber` từ `LessonIndex`, nên T-039 và T-052 dùng cả danh sách lẫn gói; T-050 thu `library()`, đọc `progress`, gọi `thumbnail`; T-052 gọi `packVersionFor`, `loadPack`, `start`, dựng dự án `ProjectKind.Lesson` từ gói (đổi giá trị thô sang kiểu của `:core:project`); T-053 gọi `complete` và dùng giá trị trả về cho `duration_ms`, `next` cho "Next lesson", `completed[id].projectId` cho "Export my animation". T-052 xóa dự án của lần xong trước khi bắt đầu làm lại bài đã xong. Giới hạn 15 giây tổng cho lần tải (spec KB-2, KB-3) nằm ở ViewModel của T-050, T-052 nếu cần hơn giới hạn từng lần kết nối của `RemoteAssets`.
  - T-049 làm nội dung thật của thư viện theo đúng hợp đồng ở mục 3.1 (đường dẫn R2, `index.json`, bố cục và `lesson.json` của gói), nén zip bằng đúng lệnh ở mục 3.1. Năm tệp `lessons/v1/...` mà T-048 đăng vào `docs/ops/required-assets.txt` phải luôn trả `200` trên R2 staging: `v1` giữ nguyên, không xóa hay đổi tên; nội dung mới đi vào `v2` và đổi `lessons_content_version`. Hai bài mẫu đưa lên trước phiên của T-048 (điều kiện trước phiên, mục 3.1) nên là bản T-049 đã duyệt, vì `v1` không sửa được về sau.
- `:app` phụ thuộc `:core:lessons` ngay ở task này (vì `AppRemoteKeys`), nên R8 của bản release xử lý module ngay; `pr-check` build bản release vì PR đổi file build.
- Đề xuất thứ tự chạy: sau T-001 (phụ thuộc theo ticket, và cùng sửa `settings.gradle.kts`, `CLAUDE.md`), sau T-006 và T-007 nếu cùng phiên (cùng sinh lại bảng khóa); trước T-039, T-050, T-052, T-053.

## 7. Điều kiện dừng riêng

- Danh sách chạy chưa ghi kết quả dev kiểm năm tệp `lessons/v1/...` trên R2 staging (điều kiện trước phiên, mục 3.1): không làm task, dừng ở đầu task, vì merge PR sẽ làm `setup-check.sh` đỏ và chặn mọi phiên sau; phiên không đưa được tệp lên R2. Khi phiên đã bắt đầu thì `start-run.yml` đã qua `setup-check.sh` với danh sách cũ, nên phiên không thấy lỗi; nếu trong phiên có issue `setup-broken` đang mở báo thiếu tệp `lessons/v1/...`, không merge, dừng task, ghi vào nhật ký và báo cáo để dev đưa tệp lên.

- Cần đổi một trong ba tệp mẫu (`index-v1.json`, `lesson-v1.json`, `progress-v1.json`) so với khối ở mục 3.1 (tên khóa, cấu trúc, giá trị) để test qua: dừng, vì tệp mẫu là hợp đồng định dạng người duyệt duyệt cùng plan.
- Cần đổi cách đặt đường dẫn R2 ở mục 3.1 (thư mục, tên tệp `index.json`, `thumb.webp`, `lesson.zip`): dừng (điểm chặn 8).
- Cần thêm phụ thuộc của `:core:lessons` vào module ngoài `:core:assets`, `:core:config`, `:core:data`, `:core:logging` (ngoài thư viện ở mục 3.3), hay cần sửa `:core:data`, `:core:assets`, `:core:config` để test qua: dừng.
- `git diff` của hai bảng khóa sau khi sinh lại có dòng khác ngoài `lessons_content_version` (trừ dòng mà task khác đã merge vào nhánh chính trước đó): dừng.

## 8. Câu hỏi cho người duyệt

1. **Đăng tệp vào `docs/ops/required-assets.txt` khi nào?** Ticket ghi T-048 đăng tệp; spec mục 7 ghi "ghi vào `required-assets.txt` khi có nội dung". Tệp trong danh sách được `scripts/setup-check.sh` kiểm mỗi sáng (`env-canary.yml`) và trước mỗi phiên (`start-run.yml`): thêm khi R2 staging chưa có tệp làm kiểm tra đỏ, mở issue `setup-broken` và chặn mọi phiên tự chạy. AI đã đưa hai cách: T-048 đăng, dev đưa tệp lên trước phiên; hay chuyển việc đăng sang T-049.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): "T-048 đăng luôn". Giữ ticket như đã viết: T-048 thêm năm đường dẫn `lessons/v1/index.json`, `lessons/v1/bouncing-ball/thumb.webp`, `lessons/v1/bouncing-ball/lesson.zip`, `lessons/v1/squash-and-stretch/thumb.webp`, `lessons/v1/squash-and-stretch/lesson.zip` vào `docs/ops/required-assets.txt` (mục 3.2); dev đưa nội dung mẫu `lessons/v1/...` lên R2 staging và kiểm trước phiên (mục 3.1); điều kiện dừng ở mục 7.
2. **Module mới `:core:lessons`?** Đề xuất của AI: một module core mới, không phụ thuộc `:core:project` (mục 3.1, lý do). Cách khác: đặt trong `:core:project` (dùng chung kiểu `AspectRatio`, `FrameRate`), nhưng module đó là dữ liệu dự án của người dùng và chưa có trên nhánh chính.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Module mới `:core:lessons`, theo đề xuất của AI.
3. **Hợp đồng nội dung bài** (mục 3.1: đường dẫn R2, `index.json`, bố cục gói `cells/`, `guides/`, `demo/`, `lesson.json`): đây là dạng chung của spec editor câu 16 và spec lessons câu 5, do AI chi tiết hóa. Điểm cần anh xem: mỗi bước có tối đa một ảnh nét dẫn phủ trên một khung; đoạn mẫu "Show me" là dãy ảnh phát theo tốc độ khung của bài; "khung áp dụng" là một khung (`frame`) và một layer (`layer`) mà người dùng vẽ ở bước đó; chữ trong gói chỉ tiếng Anh. Đổi sau khi T-049 đã làm nội dung là tăng `formatVersion` và làm lại gói. Anh duyệt dạng này, hay muốn T-039 (màn vẽ) góp ý trước?
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Duyệt hợp đồng nội dung bài như mục 3.1, theo đề xuất của AI.
4. **Thanh tiến độ tính thế nào?** Spec câu hỏi 7 ghi "bước 3 của 6 thì đầy một nửa", còn KB-4 ghi "ứng với các bước đã qua" (ở bước 3 thì mới qua 2 bước, tức một phần ba). Đề xuất của AI: theo câu trả lời của câu 7, `(step + 1) / stepCount` với `step` từ 0; bài vừa mở ở bước 1 của 6 có thanh đầy một phần sáu, bước cuối chưa xong có thanh đầy. Cách khác: `step / stepCount` (bài vừa mở có thanh rỗng).
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): `(step + 1) / stepCount`, theo đề xuất của AI.
5. **Bước đang làm lưu ở đâu là chính?** Dự án bài học của T-001 có `ProjectKind.Lesson(lessonId, step)`; kho tiến độ của plan này cũng giữ `step`. Đề xuất của AI: kho tiến độ là nguồn cho màn Bài học và cho bước mở lại; T-039 ghi cả hai mỗi lần bấm "Next step" (ghi dự án theo tự lưu của màn vẽ, ghi kho bằng `setStep`); khi hai giá trị lệch (tắt app giữa hai lần ghi), T-052 mở theo kho tiến độ.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Kho tiến độ là nguồn chính, T-039 ghi cả hai, theo đề xuất của AI.
6. **Dọn bản nội dung cũ.** Spec mục 6 ghi danh sách và nội dung bài giữ "đến khi có bản mới". Module không xóa thư mục phiên bản cũ (bài dở còn cần), cũng không xóa tệp trong bộ nhớ của `:core:assets` (module đó chưa có hàm xóa). Mỗi phiên bản dưới 40 MB và thư viện ít khi đổi. Đề xuất của AI: chưa dọn ở bản đầu; khi cần, một ticket sau xóa phiên bản không còn bài dở nào dùng (cần thêm hàm xóa vào `:core:assets`). Anh đồng ý để lại không?
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Chưa dọn bản cũ ở bản đầu, theo đề xuất của AI.
7. **Một bài sai làm cả danh sách lỗi.** Plan kiểm chặt: một bài sai luật trong `index.json` (thiếu trường, nhóm không có, đường dẫn sai) làm cả danh sách ra `INVALID`, màn Bài học báo lỗi. Gói của một bài lệch danh sách (số bước, số khung) chỉ làm `loadPack` của bài đó ra `INVALID`; danh sách vẫn hiện. Đề xuất của AI: giữ kiểm chặt để lỗi nội dung lộ ra ngay trên staging; T-049 kiểm tệp trước khi đưa lên bằng chính bộ đọc của module (ví dụ chép tệp vào thư mục test tạm và chạy test đọc). Cách khác: bỏ qua bài sai, chỉ hiện bài đúng (lỗi nội dung khó thấy hơn).
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Giữ kiểm chặt, theo đề xuất của AI.
8. **Giữ mã dự án của lần xong.** Spec mục 6 giữ hoạt hình của bài tới khi làm lại, KB-7 xuất hoạt hình từ màn Hoàn thành, KB-5 làm lại thì bỏ hoạt hình cũ. Đề xuất của AI: `completed` là bảng mã bài ra `CompletedLesson(projectId)` của lần xong gần nhất; làm lại vẫn giữ mã cũ (nhãn "Done" còn) tới khi lượt mới xong thì thay; T-052 xóa dự án cũ khi bắt đầu làm lại. Anh xác nhận?
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Giữ mã dự án của lần xong gần nhất, theo đề xuất của AI.
9. **Gói tối đa 5 layer.** T-001 khóa layer 6 tới 10 sau `extraLayersUnlocked`. Đề xuất của AI: gói bài tối đa 5 layer, để bài không cần mở khóa. Cách khác: cho tới 10 layer và T-052 đặt `extraLayersUnlocked = true` cho dự án bài học.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Gói tối đa 5 layer, theo đề xuất của AI.
10. **Duyệt chéo khi nhóm một dev.** Plan rủi ro Cao cần duyệt chéo; nếu nhóm chỉ có anh, mục 9 ghi anh vừa duyệt vừa duyệt chéo kèm lý do (risk-scoring.md, mục 3).
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Anh Nhật vừa duyệt vừa duyệt chéo, vì nhóm hiện chỉ có một dev.

## 9. Quyết định duyệt

- **Quyết định:** Duyệt
- **Người duyệt:** Nhật, **duyệt chéo:** Nhật (nhóm chỉ có một dev, theo risk-scoring.md mục 3; khi có dev thứ hai thì quy tắc duyệt chéo áp dụng lại)
- **Ghi chú:** Duyệt cả đợt 20261122-02 ngày 2026-11-22, trong phiên, lời đồng ý nguyên văn: "Duyệt đợt 20261122-02: plan T-007, T-048, T-044, T-002, T-003 và danh sách RUN-20261122-02, bắt đầu phiên khi merge; xác nhận sáu spec editor, export, lessons, onboarding, projects, settings. Tôi vừa duyệt vừa duyệt chéo vì nhóm chỉ có một dev." Câu trả lời cho mục 8 ghi ở từng câu hỏi.

## 10. Nhật ký thực hiện

<!-- AI ghi khi task đã có kết quả cuối (đã merge hoặc đã dừng), trên nhánh báo cáo của phiên (auto-run.md, bước 10). Mục mới thêm vào cuối, không sửa mục cũ. -->

### RUN-20261122-02, phiên bản plan 1

- **Kết quả:** đã merge (PR #21, commit merge f6fa2eb, 2026-11-22 17:50 UTC).
- **Nhánh, PR:** `claude/T-048-core-lessons`, PR #21.
- **Đã làm:** module `:core:lessons` đúng mục 3.1, 3.2 (33 file, không file nào ngoài mục 3.2): định dạng danh sách và gói bài, tải qua `RemoteAssets`, giải nén an toàn, kho tiến độ trên DataStore, khóa `lessons_content_version` trong `AppRemoteKeys`; hai bảng khóa sinh lại (chỉ thêm `lessons_content_version`); `docs/ops/required-assets.txt` thêm năm đường dẫn; dòng `:core:lessons` trong `CLAUDE.md`. Bước 3 đến 5 do phiên con làm trong worktree riêng.
- **Test viết trước:** commit cd114b6 (kèm khung biên dịch, thân `TODO()`): 48 test của `:core:lessons` và `RemoteConfigKeysTest.lessonsKeys_areListed`. Lần chạy đỏ: 47 test của `:core:lessons` đỏ (`NotImplementedError`, luồng rỗng), 2 test của `:app` đỏ (bảng khóa chưa sinh lại); `LessonsSettingsTest.contentVersion_isStringV1` và `lessonsKeys_areListed` qua ngay đúng như plan dự đoán (đối chứng: `keysFile_matchesCode`, `templateFile_matchesCode` qua sau khi sinh lại). Không test nào phải viết chặt thêm. Ba tệp mẫu khớp từng byte với mục 3.1. Commit code 7b077e8.
- **Rà soát độc lập:** 0 chặn, 0 nên sửa, 5 ghi chú: (1) lệch đã khai báo ở `PackExtractor` (dưới) chấp nhận được; (2) chi tiết cài đặt ngoài chữ của plan chấp nhận được; (3) ca biên để ticket sau: thư mục `.tmp-*` còn lại khi app bị tắt giữa lúc giải nén không được dọn, tệp `a` rồi mục `a/b` ra `OTHER` thay vì `INVALID`, bảng khóa theo bài chỉ lớn lên; (4) R8 chưa chạy lúc rà soát (đã chạy sau đó, qua); (5) điều kiện R2 của mục 7 đã có trong danh sách chạy.
- **Kết quả CI:** `scripts/check.sh` trên máy: QUA (2596 test JVM, 0 lỗi); `./gradlew :app:assembleStagingRelease`: qua. `pr-check` lần chạy 45049834122: xanh ở vòng đầu.
- **Lệch so với plan:** bước 4 (chi tiết cài đặt để đạt test của mục 4): plan dự đoán zip cắt còn nửa làm `ZipInputStream` ném `ZipException`; với gói mẫu, điểm cắt rơi đúng ranh giới mục nên `getNextEntry()` trả `null` lặng lẽ và test `PackExtractorTest.extract_notAZipOrNoLessonJson_isRejected` (ca zip cụt) đỏ dù phần giải nén đã viết theo plan. Không sửa test; `PackExtractor` kiểm thêm bản ghi cuối của zip (`PK\5\6` trong 22 + 65535 byte cuối) và đổi `EOFException` thành `InvalidPackException`. Tệp `"hello"` giờ bị từ chối ở bước này (cùng loại lỗi). Không có file ngoài mục 3.2, không dùng phương án dự phòng 3.4.
- **Việc còn lại:** ba ca biên ở ghi chú 3 của rà soát, nếu muốn, thành ticket sau.
- **Cần người xem:** lệch ở `PackExtractor` (kiểm bản ghi cuối của zip).
