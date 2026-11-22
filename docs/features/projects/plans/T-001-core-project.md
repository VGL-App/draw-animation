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

Tạo module `:core:project` chứa mô hình dự án đầy đủ (mọi thứ màn vẽ, danh sách dự án, xuất file và bài học cần lưu) và kho lưu trên máy dạng "mỗi dự án một thư mục": một tệp `project.json` có số phiên bản cho phần mô tả, các tệp ảnh và âm thanh nằm cạnh. Đọc không bao giờ ném lỗi ra ngoài: tệp hỏng trả `Corrupt` (kèm tên nếu đọc được), tệp của bản app mới hơn trả `NewerVersion`. Kiểm chứng bằng unit test JVM trên thư mục tạm, gồm một tệp mẫu phiên bản 1 viết sẵn trong plan này để khóa định dạng.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/projects/spec.md`. Ticket chỉ làm phần dữ liệu; kho dự án (danh sách, tạo, đổi tên, nhân bản, bộ đếm) là T-002, cũng trong `:core:project`; giao diện từ T-009 trở đi.

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-2 | Khi xem một thẻ thì thẻ có ảnh thu nhỏ, tên dự án và dòng "<số khung> frames · <tốc độ> fps" | Mô hình có tên, danh sách khung, tốc độ khung; kho có tệp ảnh thu nhỏ riêng (T-004 vẽ, T-002 đọc) |
| KB-2 | Khi danh sách có nhiều dự án thì dự án sửa gần nhất đứng đầu | Mô hình có `updatedAtMillis` (mốc sửa gần nhất); T-002 sắp xếp |
| KB-2 | Khi một dự án không đọc được thì các dự án khác vẫn hiện và mở được bình thường | Đọc từng dự án độc lập; một thư mục hỏng không làm hỏng lần đọc các thư mục khác, không ném lỗi |
| KB-2 | Khi một dự án không đọc được thì thẻ vẫn hiện với tên (nếu đọc được); app không tự xóa dự án | `Corrupt` và `NewerVersion` mang `name` khi trường `name` còn đọc được; kho không bao giờ tự xóa thư mục hỏng |
| KB-2 | Khi có 100 dự án thì danh sách mở ra không giật | Phần mô tả mỗi dự án nhỏ (dưới 16 KB với dự án mẫu đầy đủ), không chứa ảnh; ảnh nằm ở tệp riêng nên đọc 100 dự án chỉ đọc 100 tệp JSON nhỏ |
| KB-3 (hỗ trợ) | 16:9 ở 1080p là 1920 × 1080; 9:16 ở 1080p là 1080 × 1920; 1:1 là 1080 × 1080; 720p cho 1280 × 720, 720 × 1280, 720 × 720 | Hàm `canvasSize(aspectRatio, resolution)` trong mô hình, để mọi feature dùng một chỗ |
| KB-3 (hỗ trợ) | Ảnh gốc bị xóa sau khi tạo dự án thì nền không đổi, vì app đã chép ảnh vào dự án | Nền `Photo` trỏ tới tệp `background.<đuôi>` trong thư mục dự án, không trỏ ra ngoài |
| KB-3 (hỗ trợ) | Máy không đủ chỗ thì không tạo dự án dở | Thư mục chưa có `project.json` không được liệt kê; người tạo ghi tệp phụ trước, `project.json` sau cùng |

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

ADR liên quan: ADR-0001 (core chỉ phụ thuộc core), ADR-0019 (ghi log qua `:core:logging`, không ghi dữ liệu cá nhân, nội dung file, mã định danh).

## 3. Giải pháp

### 3.1. Cách làm

**Module.** `:core:project`, gói `com.vgl.draw.animation.core.project`. `build.gradle.kts` theo mẫu `:core:assets`: plugin `template.android.library`, `template.hilt`, cộng `libs.plugins.kotlin.serialization`; phụ thuộc `implementation(project(":core:logging"))` và `implementation(libs.kotlinx.serialization.json)`. Thêm `include(":core:project")` vào `settings.gradle.kts`, thêm một dòng vào bảng module của `CLAUDE.md`. Chưa module nào phụ thuộc `:core:project` trong ticket này; plan đầu tiên nối module vào một feature (màn danh sách T-009 hay màn vẽ) thêm phụ thuộc ở module của feature đó.

**Mô hình** (gói `model`, lớp dữ liệu bất biến, `@Serializable`, không dùng lớp Android, để test chạy trên JVM). Mọi lớp con sealed và mọi hằng enum có `@SerialName` cố định như tệp mẫu ở dưới, để đổi tên gói hay tên lớp không làm đổi định dạng.

- `ProjectId`, `FrameId`, `LayerId`, `SoundClipId`: `@JvmInline value class` bọc chuỗi, khối `init` `require` chuỗi là UUID dạng chuẩn chữ thường (`UUID.fromString(s).toString() == s`), nên không mã nào có `/`, `..` hay chữ hoa; giải mã gặp mã sai thì ra `Corrupt`. `random()` sinh mã mới; `ProjectId.parseOrNull(String)` trả `null` thay vì ném lỗi, dùng khi duyệt thư mục.
- `Project(id, kind, name, aspectRatio, resolution, frameRate, background, layers, frames, sounds, camera, onionSkin, reference, extraLayersUnlocked, createdAtMillis, updatedAtMillis)`.
- `ProjectKind`: sealed, `Regular` (`"regular"`) và `Lesson(lessonId: String, step: Int)` (`"lesson"`); thuộc tính `Project.isUserProject` trả `kind is Regular`.
- `AspectRatio` (`PORTRAIT_9_16` ghi `"9:16"`, `LANDSCAPE_16_9` ghi `"16:9"`, `SQUARE_1_1` ghi `"1:1"`), `Resolution` (`P720` ghi `"720p"`, `P1080` ghi `"1080p"`), `FrameRate` (6, 8, 12, 15, 24, 30, ghi thành số nguyên qua serializer riêng). `canvasSize(aspectRatio, resolution): CanvasSize(width, height)`: cạnh ngắn bằng 720 hay 1080, cạnh dài theo tỷ lệ.
- `Background`: sealed, `Solid(color: BackgroundColor)` (`"solid"`) với `BackgroundColor { WHITE, DARK, BLUE }` (ghi `"white"`, `"dark"`, `"blue"`, trùng giá trị tham số `background` của sự kiện `project_create`), và `Photo(fileExtension: String)` (`"photo"`). Mã màu thật không nằm ở đây: lớp vẽ (T-004) lấy từ `:core:designsystem`.
- `Layer(id, name, visible)`; danh sách `layers` xếp từ dưới lên trên. Tên mặc định ("Layer 1") là chữ hiển thị, do màn vẽ lấy từ `strings.xml` rồi truyền vào; module không có chuỗi hiển thị.
- `Frame(id, hold: Int = 1, cameraKey: CameraKey? = null)`; `CameraKey(panX: Float, panY: Float, zoom: Float, rotationDegrees: Float)`, `panX`, `panY` tính theo phần của bề rộng, bề cao khung vẽ (không theo điểm ảnh, để đúng ở mọi độ phân giải), `zoom` là hệ số (1.2 nghĩa là 120%). Định dạng chỉ lưu điểm đặt; khung không có điểm đặt được vẽ thế nào (giữ điểm trước, nội suy, hay về gốc) là luật của lớp vẽ T-004, T-037, đổi luật đó không đổi định dạng (câu hỏi 2).
- `CameraSettings(enabled: Boolean = false)`: công tắc "Camera on" của dự án.
- `SoundClip(id, type: SoundType { MUSIC, VOICE, EFFECT }, title: String, fileExtension: String, startFrameId: FrameId?, durationMillis: Long, effectSource: EffectSource?)`, `SoundType` ghi `"music"`, `"voice"`, `"effect"`; `startFrameId == null` nghĩa là phát từ đầu dự án ("Music · from 0:00"); `EffectSource(packId, soundId)` chỉ để hiện tên, còn tệp âm thanh luôn được chép vào dự án nên hiệu ứng của gói hết 24 giờ vẫn phát và xuất được (spec editor KB-14).
- `OnionSkinSettings(enabled: Boolean = false, framesBefore: Int = 2, framesAfter: Int = 1, opacityPercent: Int = 45, beforeColorArgb: Int? = null, afterColorArgb: Int? = null, loop: Boolean = false)`; màu `null` nghĩa là dùng màu của theme (`AppTheme.colors.onionPrevious`, `onionNext`), để module không viết cứng mã màu.
- `ReferenceLayer(type: ReferenceType { IMAGE, VIDEO }, fileExtension: String, frameCount: Int, opacityPercent: Int = 40, visible: Boolean = true)`, `ReferenceType` ghi `"image"`, `"video"`; `reference == null` nghĩa là chưa có lớp vẽ theo. Ảnh: `frameCount = 1`. Video: mỗi ảnh tách ra theo tốc độ khung lúc nhập là một tệp. Định dạng chỉ lưu dãy ảnh; ảnh nào hiện ở khung nào (theo chỉ số khung hay theo thời gian khi có giữ khung) là luật của màn vẽ T-036 (câu hỏi 3).
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
<filesDir>/projects/.deleting-<projectId>/   dự án đang xóa dở, bị bỏ qua khi liệt kê
```

Tệp không có trong mô hình (ô của khung đã xóa, âm thanh đã bỏ) bị bỏ qua khi đọc; người gọi xóa chúng sau khi ghi `project.json`.

**Tệp mẫu phiên bản 1** (`core/project/src/test/resources/project-v1.json`). Đây là hợp đồng định dạng người duyệt duyệt cùng plan. Bước 3 chép nguyên văn khối dưới đây vào tệp, không sinh từ code; `TestProjects.full()` dựng đúng dự án này.

```json
{
  "formatVersion": 1,
  "project": {
    "id": "6f1c2a3e-0000-4000-8000-000000000001",
    "kind": { "type": "lesson", "lessonId": "walk-cycle", "step": 3 },
    "name": "My animation 3",
    "aspectRatio": "16:9",
    "resolution": "720p",
    "frameRate": 24,
    "background": { "type": "photo", "fileExtension": "jpg" },
    "layers": [
      { "id": "6f1c2a3e-0000-4000-8000-0000000000a1", "name": "Layer 1", "visible": true },
      { "id": "6f1c2a3e-0000-4000-8000-0000000000a2", "name": "Sketch", "visible": false },
      { "id": "6f1c2a3e-0000-4000-8000-0000000000a3", "name": "Layer 3", "visible": true }
    ],
    "frames": [
      { "id": "6f1c2a3e-0000-4000-8000-0000000000b1", "hold": 1, "cameraKey": null },
      { "id": "6f1c2a3e-0000-4000-8000-0000000000b2", "hold": 3, "cameraKey": null },
      {
        "id": "6f1c2a3e-0000-4000-8000-0000000000b3",
        "hold": 1,
        "cameraKey": { "panX": 0.1, "panY": -0.2, "zoom": 1.2, "rotationDegrees": 15.0 }
      },
      { "id": "6f1c2a3e-0000-4000-8000-0000000000b4", "hold": 1, "cameraKey": null }
    ],
    "sounds": [
      {
        "id": "6f1c2a3e-0000-4000-8000-0000000000c1",
        "type": "music",
        "title": "Track 1",
        "fileExtension": "mp3",
        "startFrameId": null,
        "durationMillis": 4000,
        "effectSource": null
      },
      {
        "id": "6f1c2a3e-0000-4000-8000-0000000000c2",
        "type": "voice",
        "title": "Recording 1",
        "fileExtension": "m4a",
        "startFrameId": "6f1c2a3e-0000-4000-8000-0000000000b2",
        "durationMillis": 2500,
        "effectSource": null
      },
      {
        "id": "6f1c2a3e-0000-4000-8000-0000000000c3",
        "type": "effect",
        "title": "Pop",
        "fileExtension": "ogg",
        "startFrameId": "6f1c2a3e-0000-4000-8000-0000000000b3",
        "durationMillis": 300,
        "effectSource": { "packId": "cartoon", "soundId": "pop" }
      }
    ],
    "camera": { "enabled": true },
    "onionSkin": {
      "enabled": true,
      "framesBefore": 3,
      "framesAfter": 2,
      "opacityPercent": 60,
      "beforeColorArgb": -65536,
      "afterColorArgb": -16776961,
      "loop": true
    },
    "reference": { "type": "video", "fileExtension": "jpg", "frameCount": 120, "opacityPercent": 40, "visible": true },
    "extraLayersUnlocked": true,
    "createdAtMillis": 1763769600000,
    "updatedAtMillis": 1763773200000
  }
}
```

**Đọc và ghi phần mô tả** (`ProjectFormat`, internal, có test trực tiếp):

- `CURRENT_FORMAT_VERSION = 1`. `Json` riêng: `encodeDefaults = true`, `explicitNulls = true`, `ignoreUnknownKeys = false`, `classDiscriminator = "type"`.
- `validate(project): String?` trả lý do sai đầu tiên hay `null`; dùng chung cho ghi và đọc. Luật: ít nhất 1 khung; 1 tới 10 layer; mã khung, mã layer, mã âm thanh không trùng; `hold` từ 1 tới 4; `CameraKey` có bốn số hữu hạn và `zoom > 0`; `opacityPercent` của onion skin và lớp vẽ theo từ 0 tới 100; `framesBefore`, `framesAfter` không âm; `durationMillis` không âm; `startFrameId` của âm thanh là `null` hay mã một khung có thật; `Lesson.step` không âm; `frameCount` của lớp vẽ theo ít nhất 1; `fileExtension` (nền, âm thanh, lớp vẽ theo) khớp `[a-z0-9]{1,5}`. Không kiểm độ dài tên (T-002 cắt ở 40 ký tự khi tạo, đọc thì nhận nguyên).
- `encode(project): String`: `validate` trả lý do thì ném `IllegalArgumentException` (lỗi lập trình của người gọi, không chạm đĩa); không thì ghi `{"formatVersion":1,"project":{...}}`.
- `decode(text): ProjectReadResult` theo bốn bước, bắt `SerializationException` và `IllegalArgumentException`, không ném lỗi:
  1. Phân tích thành `JsonObject`; lỗi thì `Corrupt(name = null, reason = INVALID_JSON)`.
  2. Đọc `formatVersion`; thiếu hay không phải số nguyên thì `Corrupt(reason = MISSING_VERSION)`; lớn hơn `CURRENT_FORMAT_VERSION` thì `NewerVersion(version, name)`; nhỏ hơn thì chạy chuỗi chuyển đổi `migrate(version, obj)`; không có bước chuyển đổi cho phiên bản đó (hiện chưa có bước nào, vì 1 là phiên bản đầu) thì `Corrupt(reason = UNKNOWN_VERSION)`.
  3. Giải mã chặt (khóa lạ, kiểu sai, giá trị enum lạ, mã không phải UUID chuẩn, tốc độ khung ngoài danh sách là lỗi) rồi `validate`; lỗi thì `Corrupt(reason = INVALID_CONTENT)`.
  4. Mọi `Corrupt` sau bước 1 và mọi `NewerVersion` thử đọc riêng `project.name` (chuỗi khác rỗng) để gắn vào `name`.
- Quy tắc phiên bản, ghi trong KDoc của `ProjectFormat`: mọi thay đổi của phần mô tả (thêm, bớt, đổi trường hay nghĩa của trường) đều tăng `CURRENT_FORMAT_VERSION`, thêm một bước `migrate` và một tệp mẫu `project-v<N>.json` trong test; tệp mẫu cũ không bao giờ bị sửa. Đổi định dạng về sau là điểm chặn 9 của `risk-scoring.md`.
- `ProjectReadResult`: sealed, `Ok(project)`, `NotFound`, `Corrupt(name: String?, reason: CorruptReason)`, `NewerVersion(version: Int, name: String?)`. `CorruptReason { UNREADABLE_FILE, INVALID_JSON, MISSING_VERSION, UNKNOWN_VERSION, INVALID_CONTENT }`.

**Tệp phụ** (`ProjectFile`): sealed, `Thumbnail`, `Background(extension)`, `Cell(frameId, layerId)`, `Sound(clipId, extension)`, `ReferenceImage(index, extension)`; `relativePath` dựng đường dẫn trong thư mục dự án; đuôi sai dạng `[a-z0-9]{1,5}` hay chỉ số âm thì `require` ném `IllegalArgumentException`. Mã đã được kiểm ở khối `init` của lớp mã.

**Kho** (`ProjectStore`, interface công khai: T-002 bọc nó thành kho dự án, màn vẽ đọc ghi ô ảnh qua nó; cùng module nên T-002, T-003 dùng được bản cài đặt trực tiếp):

- `suspend fun ids(): List<ProjectId>`: thư mục con của gốc có tên là UUID dạng chuẩn và có mục `project.json` (tệp hay thư mục); bỏ qua tệp, thư mục bắt đầu bằng `.` (dự án đang xóa, phần dở của khôi phục T-003), thư mục chưa có `project.json` (dự án tạo dở) và tên khác. Gốc chưa có thì trả danh sách rỗng.
- `suspend fun read(id): ProjectReadResult`: mục `project.json` không tồn tại (`!exists()`) thì `NotFound`; đọc lỗi `IOException` (kể cả khi `project.json` là thư mục) thì `Corrupt(null, UNREADABLE_FILE)`; còn lại theo `ProjectFormat.decode`; giải mã được mà `project.id != id` thì `Corrupt(name, INVALID_CONTENT)`.
- `suspend fun write(project)`: `encode` trước (dự án sai ném `IllegalArgumentException`, không chạm đĩa), tạo thư mục nếu chưa có, ghi nguyên tử (dưới). Lỗi ghi ném `IOException` cho người gọi (T-002 đổi thành kết quả lỗi, tách loại hết chỗ). Không tự đổi `updatedAtMillis`: người gọi đặt.
- `suspend fun readFile(id, file: ProjectFile): ByteArray?` (`null` khi không có tệp; lỗi đọc ném `IOException`), `suspend fun writeFile(id, file, bytes: ByteArray)` (ghi nguyên tử), `suspend fun deleteFile(id, file)` (không có tệp thì không làm gì; xóa không được thì ném `IOException`).
- `suspend fun delete(id)`: đổi tên thư mục thành `.deleting-<id>` rồi xóa đệ quy, nên dự án biến khỏi `ids()` ngay cả khi xóa hỏng giữa chừng; dự án không có thì không làm gì; đổi tên hay xóa không được thì ném `IOException`. Lần `delete` sau cũng dọn mọi thư mục `.deleting-*` còn sót.
- Ghi nguyên tử (`writeAtomically(target, bytes)`, dùng cho `project.json` và mọi tệp phụ): ghi `<tên>.tmp` cạnh đích, `FileOutputStream.fd.sync()`, rồi `renameTo` đè đích; `renameTo` trả `false` thì xóa tệp tạm và ném `IOException`, như `DefaultRemoteAssets`. Mất điện giữa chừng thì đích giữ bản cũ đầy đủ.
- Ghi song song: một `Mutex` cho mỗi mã dự án (`ConcurrentHashMap<ProjectId, Mutex>`) bao `write`, `writeFile`, `deleteFile`, `delete`, vì tự lưu (T-020), lưu khi ra nền và đổi tên từ danh sách (T-002) có thể ghi cùng một dự án cùng lúc. Đọc không khóa: đổi tên nguyên tử nên người đọc luôn thấy một bản đầy đủ. Dự án khác nhau ghi song song được.
- Đường dẫn đích của mọi thao tác phải nằm dưới thư mục dự án sau khi chuẩn hóa (`canonicalFile`); không thì `IllegalArgumentException` (lớp phòng thủ thứ hai sau kiểm mã).
- Thứ tự ghi người gọi phải theo, ghi trong KDoc của `ProjectStore`: tệp phụ trước, `project.json` sau, xóa tệp thừa sau cùng; tạo dự án mới cũng vậy, nên hết chỗ giữa chừng chỉ để lại thư mục chưa có `project.json`, không hiện trong `ids()`; người tạo gọi `delete(id)` khi tạo lỗi.
- Mã hóa ảnh, âm thanh không thuộc module này: kho chỉ nhận và trả mảng byte.

**Cài đặt** `FileProjectStore(root: File, ioDispatcher: CoroutineDispatcher, logger: AppLogger)` (internal): mọi thao tác chạy trong `withContext(ioDispatcher)`. Đọc ra `Corrupt` thì ghi `logger.warn("Project", "Project unreadable: <reason>")`, ra `NewerVersion` thì `"Project has newer format <version>"`; không ghi mã dự án, tên hay nội dung tệp (ADR-0019).

`di/ProjectModule.kt`: Hilt `@Provides @Singleton ProjectStore = FileProjectStore(File(context.filesDir, "projects"), Dispatchers.IO, logger)`, theo mẫu `AssetsModule`. Chọn `filesDir` vì dữ liệu dự án phải còn tới khi người dùng xóa. App đã tắt sao lưu lên đám mây của Android (`android:allowBackup="false"` trong `app/src/main/AndroidManifest.xml`); chuyển dữ liệu trực tiếp sang máy mới (Android 12 trở lên) theo luật `dataExtractionRules` được gộp từ AppsFlyer, ticket này không đổi luật đó.

Mẫu có sẵn được làm theo: ghi tạm rồi đổi tên và `withContext(ioDispatcher)` của `DefaultRemoteAssets` (`:core:assets`); `@Serializable` của đích điều hướng (`feature/sample/.../navigation/SampleNavigation.kt`); Hilt module của `AssetsModule`.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `settings.gradle.kts` | Thêm `include(":core:project")` sau `include(":core:network")` |
| Sửa | `CLAUDE.md` | Thêm dòng `:core:project` vào bảng Cấu trúc module: mô hình dự án và kho lưu dự án trên máy |
| Tạo | `core/project/build.gradle.kts` | Plugin thư viện, Hilt, kotlin serialization; phụ thuộc `:core:logging`, `kotlinx-serialization-json` |
| Tạo | `core/project/consumer-rules.pro` | Tệp có một dòng chú thích: thư viện kotlinx.serialization đã kèm luật R8 cho lớp `@Serializable` (mục 4.1) |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/Ids.kt` | `ProjectId`, `FrameId`, `LayerId`, `SoundClipId`, kiểm UUID chuẩn |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/Project.kt` | `Project`, `ProjectKind`, `Project.create`, `isUserProject` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/CanvasFormat.kt` | `AspectRatio`, `Resolution`, `FrameRate` (serializer số nguyên), `CanvasSize`, `canvasSize` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/Background.kt` | `Background`, `BackgroundColor` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/Timeline.kt` | `Layer`, `Frame`, `CameraKey`, `CameraSettings` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/SoundClip.kt` | `SoundClip`, `SoundType`, `EffectSource` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/model/EditorSettings.kt` | `OnionSkinSettings`, `ReferenceLayer`, `ReferenceType` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ProjectFormat.kt` | Phiên bản, `Json`, `validate`, `encode`, `decode`, `migrate` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ProjectReadResult.kt` | `ProjectReadResult`, `CorruptReason` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ProjectFile.kt` | Các loại tệp phụ và đường dẫn tương đối |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/ProjectStore.kt` | Interface công khai, KDoc thứ tự ghi |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/storage/FileProjectStore.kt` | Bản cài đặt trên thư mục: ghi nguyên tử, khóa theo dự án, xóa qua `.deleting-`, log |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/di/ProjectModule.kt` | Hilt module cấp `ProjectStore` |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/model/CanvasFormatTest.kt` | Kích thước khung vẽ, `Project.create` |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/model/IdsTest.kt` | Mã nhận UUID chuẩn, từ chối dạng khác |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/storage/ProjectFormatTest.kt` | Ghi rồi đọc, tệp mẫu v1, phiên bản, tệp hỏng, luật kiểm |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/storage/FileProjectStoreTest.kt` | Thư mục, tệp phụ, ghi nguyên tử, ghi song song, xóa, nhiều dự án có một dự án hỏng, log |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/storage/ProjectFileTest.kt` | Đường dẫn tương đối, từ chối đuôi sai |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/TestProjects.kt` | `TestProjects.full()` đúng tệp mẫu v1 |
| Tạo | `core/project/src/test/resources/project-v1.json` | Tệp mẫu phiên bản 1, chép nguyên văn từ mục 3.1 |

Không có ảnh chuẩn screenshot.

Nơi đọc hai file có sẵn mà plan sửa (`grep -rln "settings.gradle.kts\|CLAUDE.md" scripts/ .github/ */src/test */src/androidTest`), và vì sao không phải sửa:

- `settings.gradle.kts`:
  - `.github/workflows/pr-check.yml`: chỉ dùng tên file trong bộ lọc đường dẫn (file đổi thì chạy job build và job release); PR của task vì vậy chạy cả bản release.
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

Không có. Mọi kỹ thuật (JSON với kotlinx.serialization, ghi tạm rồi đổi tên, `Mutex` của coroutines, thư mục tạm của JUnit) đã có mẫu trong dự án hay đã chạy thử (mục 4.1).

### 3.5. Hành vi đã có bị đổi

Không có. Plan chỉ tạo file Kotlin mới trong module mới; `settings.gradle.kts` thêm một dòng `include`, không đổi module có sẵn. `scripts/existing-tests.py` không tìm thấy test nào kiểm hành vi bị đổi (kết quả ở mô tả PR).

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Không có | Không có | Không có | Không có |

## 4. Kiểm thử

Mọi test là unit test JVM (JUnit 4, `kotlinx-coroutines-test`, `TemporaryFolder` của JUnit), không cần Robolectric vì module không gọi lớp Android ngoài Hilt module. `FileProjectStore` dựng với `StandardTestDispatcher(testScheduler)` của `runTest` và `RecordingAppLogger`, trừ test ghi song song. `TestProjects.full()` dựng đúng dự án của tệp mẫu v1 ở mục 3.1. "Tệp mẫu sửa X" nghĩa là đọc `project-v1.json` thành `JsonObject`, đổi đúng chỗ X, rồi ghi lại thành chuỗi.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| Định dạng phiên bản 1 đúng hợp đồng đã duyệt | Unit test | `ProjectFormatTest.decode_v1Sample_returnsFullProject`: `decode(<tệp mẫu>)` là `Ok(TestProjects.full())`; `ProjectFormatTest.encode_fullProject_matchesV1Sample`: `Json.parseToJsonElement(encode(TestProjects.full()))` bằng phần tử JSON của tệp mẫu, nên đổi tên trường, giá trị phân biệt hay thêm trường mà không tăng phiên bản làm test đỏ |
| Mô hình lưu đủ mọi phần của ticket | Unit test | `ProjectFormatTest.encodeThenDecode_fullProject_returnsEqualProject`: `decode(encode(full))` là `Ok(full)` |
| Tệp ghi ra có số phiên bản | Unit test | `ProjectFormatTest.encode_writesCurrentFormatVersion`: khóa `formatVersion` bằng `1` và bằng `CURRENT_FORMAT_VERSION` |
| Tốc độ khung ghi thành số | Unit test | `ProjectFormatTest.encode_frameRateAsNumber`: dự án 12 fps có `"frameRate": 12` (số, không phải chuỗi) |
| KB-2: phần mô tả nhỏ | Unit test | `ProjectFormatTest.encode_fullProject_isSmall`: `encode(full).toByteArray().size < 16_384` |
| Tệp của bản app mới hơn | Unit test | `ProjectFormatTest.decode_newerFormatVersion_returnsNewerVersion`: tệp mẫu sửa `formatVersion` thành `2` và thêm khóa lạ vào `project` trả `NewerVersion(2, "My animation 3")`, không phải `Corrupt` |
| Đọc lỗi trả "hỏng", không ném lỗi | Unit test | `ProjectFormatTest.decode_invalidJson_returnsCorruptWithoutName` (`"{not json"`, chuỗi rỗng, nửa đầu tệp mẫu): `Corrupt(null, INVALID_JSON)`; `decode_missingVersion_keepsName` và `decode_versionNotNumber_keepsName`: `Corrupt("My animation 3", MISSING_VERSION)`; `decode_unknownKey_returnsCorrupt` (thêm `"extra": 1` vào `project`): `Corrupt(reason = INVALID_CONTENT)` |
| KB-2: dự án hỏng vẫn hiện tên nếu đọc được | Unit test | `ProjectFormatTest.decode_invalidContentWithName_keepsName`: tệp mẫu sửa `frames` thành `[]` trả `Corrupt("My animation 3", INVALID_CONTENT)`; đối chứng: `decode_invalidJson_returnsCorruptWithoutName` cho `name == null` |
| Luật kiểm khi đọc | Unit test | `ProjectFormatTest.decode_ruleViolation_returnsCorrupt`, mỗi ca sửa một chỗ của tệp mẫu và phải ra `Corrupt(reason = INVALID_CONTENT)`: 0 khung; 0 layer; 11 layer; trùng mã khung; trùng mã layer; trùng mã âm thanh; `hold` 0 và 5; `zoom` 0; `fps` 10; `aspectRatio` `"4:5"`; `type` của nền `"gradient"`; onion `opacityPercent` 101; `framesBefore` -1; `durationMillis` -1; `startFrameId` là UUID không có trong `frames`; `step` -1; lớp vẽ theo `frameCount` 0; `fileExtension` `"../x"`; mã khung `"../x"`; mã layer viết hoa; mã dự án `"1-1-1-1-1"`. Đối chứng: tệp mẫu không sửa ra `Ok` trong cùng test |
| Chuyển đổi phiên bản cũ có chỗ cắm | Unit test | `ProjectFormatTest.decode_olderVersionWithoutMigration_returnsCorrupt`: `formatVersion` 0 trả `Corrupt(reason = UNKNOWN_VERSION)`; phiên bản sau thay test này bằng test của bước chuyển đổi |
| Không ghi ra dự án sai | Unit test | `ProjectFormatTest.encode_invalidProject_throws`: dự án 11 layer, dự án `CameraKey` có `Float.NaN` đều ném `IllegalArgumentException`; `FileProjectStoreTest.write_invalidProject_throwsAndKeepsPrevious`: sau `write(full)`, `write(full.copy(frames = emptyList()))` ném `IllegalArgumentException`, `read` vẫn ra `Ok(full)`, thư mục không có tệp `.tmp` |
| Mã dự án, khung, layer, âm thanh an toàn | Unit test | `IdsTest.acceptsCanonicalUuidOnly`: `ProjectId(UUID.randomUUID().toString())` dựng được; `"../x"`, `""`, `"1-1-1-1-1"`, UUID viết hoa đều ném `IllegalArgumentException` với cả bốn lớp mã; `ProjectId.parseOrNull` trả `null` cho các chuỗi đó |
| KB-3: kích thước khung vẽ | Unit test | `CanvasFormatTest.canvasSize_matchesSpec`: (16:9, 1080p) 1920 × 1080; (9:16, 1080p) 1080 × 1920; (1:1, 1080p) 1080 × 1080; (16:9, 720p) 1280 × 720; (9:16, 720p) 720 × 1280; (1:1, 720p) 720 × 720 |
| Dự án mới có giá trị mặc định | Unit test | `CanvasFormatTest.create_hasOneFrameOneLayerAndDefaults`: 1 khung `hold = 1` không điểm đặt, 1 layer hiện với tên truyền vào, không âm thanh, camera tắt, onion skin `false, 2, 1, 45, null, null, false`, `reference == null`, `extraLayersUnlocked == false`, `createdAtMillis == updatedAtMillis == nowMillis`, `kind == Regular`, `isUserProject == true`; đối chứng: dự án `Lesson` có `isUserProject == false`; dự án từ `create` qua `validate` không lỗi |
| Ghi rồi đọc qua kho | Unit test | `FileProjectStoreTest.writeThenRead_returnsEqualProject`: `write(full)` rồi `read(id)` là `Ok(full)`; `projects/<id>/project.json` có trên đĩa |
| Không có dự án | Unit test | `FileProjectStoreTest.read_missingProject_returnsNotFound`: mã chưa ghi, và thư mục có mà không có `project.json`, đều trả `NotFound` |
| Mã trong tệp khác tên thư mục | Unit test | `FileProjectStoreTest.read_idMismatch_returnsCorrupt`: chép `project.json` của dự án A vào thư mục của mã B; `read(B)` trả `Corrupt("My animation 3", INVALID_CONTENT)` |
| Ghi đè và ghi dở không làm mất bản cũ | Unit test | `FileProjectStoreTest.write_overwrite_replacesAndLeavesNoTempFile`: ghi hai lần với tên khác, đọc ra bản sau, thư mục không còn tệp `.tmp`; `FileProjectStoreTest.write_tempFails_throwsIOExceptionAndKeepsPrevious`: sau lần ghi đầu, tạo thư mục `project.json.tmp` có một tệp bên trong (không xóa được, không mở ghi được, kể cả khi chạy bằng root), lần ghi thứ hai ném lỗi `is IOException`, `read` vẫn trả bản đầu |
| Tệp tạm sót lại không ảnh hưởng | Unit test | `FileProjectStoreTest.read_ignoresLeftoverTempFile`: có `project.json` đúng và `project.json.tmp` chứa rác, `read` trả `Ok` |
| Ghi song song cùng một dự án | Unit test | `FileProjectStoreTest.write_concurrentSameProject_endsWithOneWholeWrite`: kho dựng với dispatcher 4 luồng thật (`Executors.newFixedThreadPool(4).asCoroutineDispatcher()`, đóng sau test); 20 lần `write(full.copy(name = "n$i"))` song song bằng `async` rồi `awaitAll`; không lần nào ném lỗi; `read` là `Ok` với tên thuộc `n0` tới `n19`; thư mục không còn tệp `.tmp`. Đối chứng của ý định (không có khóa thì hỏng): ghi vào nhật ký lần chạy thử tạm bỏ `Mutex` (đột biến) và kết quả |
| KB-2: một dự án hỏng không làm hỏng các dự án khác | Unit test | `FileProjectStoreTest.readAll_hundredProjectsOneCorrupt_othersOk`: ghi 100 dự án, ghi đè `project.json` của một dự án bằng `"{"`; `ids()` có đủ 100 mã; đọc lần lượt cả 100: đúng 99 `Ok` (bằng dự án đã ghi) và 1 `Corrupt`, không lần nào ném lỗi; thư mục hỏng vẫn còn trên đĩa |
| Tệp không đọc được | Unit test | `FileProjectStoreTest.read_unreadableFile_returnsCorrupt`: `project.json` là một thư mục; `read` trả `Corrupt(null, UNREADABLE_FILE)`, không phải `NotFound` |
| Chỉ liệt kê dự án hoàn chỉnh | Unit test | `FileProjectStoreTest.ids_listsOnlyProjectDirsWithMetadata`: gốc có 2 thư mục UUID có `project.json`, 1 thư mục UUID không có `project.json`, `.deleting-<uuid>`, `.restore-x`, `abc`, `1-1-1-1-1`, một UUID viết hoa (có `project.json`), tệp `<uuid>.txt`: `ids()` trả đúng 2 mã; gốc chưa có thì trả danh sách rỗng |
| Tệp phụ của dự án | Unit test | `FileProjectStoreTest.writeFileThenRead_eachKind_returnsSameBytes`: mỗi loại `ProjectFile` ghi rồi đọc ra đúng mảng byte và nằm đúng đường dẫn; `readFile_missing_returnsNull`; `deleteFile_removesOnlyThatFile`: xóa một ô, ô khác của cùng khung còn; `deleteFile_missing_doesNothing` |
| Xóa dự án | Unit test | `FileProjectStoreTest.delete_removesWholeDirectory`: sau `delete(id)`, thư mục và `.deleting-<id>` không còn, `read` trả `NotFound`, dự án khác còn; `delete` mã không có không ném lỗi; `FileProjectStoreTest.delete_cleansLeftoverDeletingDirs`: có sẵn `.deleting-<uuid>` chứa tệp, sau một lần `delete` bất kỳ thì thư mục đó không còn |
| KB-3: tạo dở không hiện | Unit test | `FileProjectStoreTest.filesWithoutMetadata_notListed`: `writeFile(id, Background("png"))` khi chưa `write`; `ids()` không có `id`, `read(id)` trả `NotFound`; sau `write(project)` thì `ids()` có `id` |
| KB-3: nền "Photo" nằm trong dự án | Unit test | `FileProjectStoreTest.photoBackground_storedInsideProject`: ghi dự án nền `Photo("png")` và `writeFile(Background("png"))` từ byte của một tệp nguồn trong thư mục tạm khác; xóa tệp nguồn; `readFile(Background("png"))` vẫn ra đúng byte |
| Đường dẫn tệp phụ an toàn | Unit test | `ProjectFileTest.relativePath_eachKind`: `Cell` là `cells/<frameId>/<layerId>.png`, `Sound(id, "m4a")` là `sounds/<clipId>.m4a`, `ReferenceImage(0, "jpg")` là `reference/0.jpg`, `Background("png")` là `background.png`, `Thumbnail` là `thumbnail.png`; `ProjectFileTest.invalidExtensionOrIndex_throws`: đuôi `"../x"`, `""`, `"PNG"`, `"abcdef"` và chỉ số -1 ném `IllegalArgumentException` |
| Log không chứa dữ liệu người dùng, mã định danh | Unit test | `FileProjectStoreTest.read_corrupt_logsWarningWithoutNameOrId`: dự án tên `"Secret name"` có `frames: []`; sau `read`, `RecordingAppLogger` có đúng 1 mục `WARN`, thẻ `Project`, chứa `INVALID_CONTENT`, không chứa `"Secret name"` và không chứa mã dự án; tương tự `NewerVersion` chứa `2`, không chứa mã; đối chứng: `read` một dự án đúng không ghi `WARN` |
| Kho đọc ghi ngoài luồng chính | Unit test | `FileProjectStoreTest.operations_runOnIoDispatcher`: `val io = StandardTestDispatcher()` (scheduler riêng); trong `runTest`: `val d = async { store.read(id) }; runCurrent(); assertFalse(d.isCompleted); io.scheduler.advanceUntilIdle(); runCurrent(); assertTrue(d.await() is ProjectReadResult.Ok)` |
| Module tuân luật phụ thuộc | Lệnh | `scripts/check.sh` chạy `checkModuleDependencies`: `:core:project` chỉ phụ thuộc `:core:logging` |
| Bảng module của `CLAUDE.md` có dòng mới | Kiểm tra thủ công (người: đọc tài liệu) | Người duyệt PR của task đọc dòng `:core:project` trong bảng |

**Ngoại lệ viết test trước:** `di/ProjectModule.kt` (Hilt cấp `ProjectStore`), `settings.gradle.kts`, `build.gradle.kts`, `consumer-rules.pro` và dòng của `CLAUDE.md`: phần nối và cấu hình, không có hành vi riêng để test trước; build của `scripts/check.sh` kiểm chúng biên dịch được. Chưa module nào phụ thuộc `:core:project`, nên đồ thị Hilt của `:app` chưa dùng tới `ProjectModule`; plan đầu tiên nối module vào một feature kiểm qua đồ thị đó.

Thứ tự commit: bước 3 commit các tệp test, `TestProjects.kt`, `project-v1.json` (chép nguyên văn từ mục 3.1) cùng khung rỗng tối thiểu để test biên dịch được (module, `settings.gradle.kts`, các lớp mô hình có `@Serializable` và `@SerialName`, interface, thân hàm `TODO()`); test đỏ vì `NotImplementedError` hay sai kết quả. Bước 4 viết phần cài đặt. Hai test về định dạng v1 có thể qua ngay ở bước 3 nếu khung mô hình đã đủ; khi đó ghi vào nhật ký lần đối chứng: đổi tạm `@SerialName("photo")` thành `"picture"` thì cả hai test đỏ.

### 4.1. Chạy thử

- **Đã chạy thử:** 2026-11-22, trên nhánh nháp từ `origin/main` 9539a6e, không commit: module `:core:project` khung (đúng `build.gradle.kts`, `consumer-rules.pro` của mục 3.2), một lớp `@Serializable` có sealed `@SerialName` và `classDiscriminator = "type"`, ghi tạm có `fd.sync()` rồi `renameTo`, một test JVM `TemporaryFolder` ghi rồi đọc. Để R8 thật sự xử lý module, nhánh nháp tạm cho `:app` phụ thuộc `:core:project` và gọi ghi, đọc trong `TemplateApplication.onCreate` (chỉ để thử, plan không làm vậy).
- **Lệnh:** `./gradlew :core:project:testDebugUnitTest :core:project:lintDebug :core:project:ktlintCheck checkModuleDependencies :app:assembleStagingRelease`. Không chạy cả `scripts/check.sh` vì E dưới 2; phiên chạy `scripts/check.sh` như mọi task, rồi `./gradlew :core:project:lintDebug :app:assembleStagingRelease` (lint của `check.sh` chỉ chạy cho `:app`, mà `:app` chưa phụ thuộc module mới).
- **Kết quả:** qua hết (BUILD SUCCESSFUL, 2 phút 19 giây). Lần đầu ktlint báo một dòng dài ở code thử, sửa xuống dòng là qua: phiên chạy `ktlintFormat` trước `check.sh`. Test thử qua 1/1; lint của module "No issues found"; luật phụ thuộc "Module dependency rule OK". Bản mapping R8 giữ `TrialProject$$serializer`, `TrialKind$Lesson$$serializer` (chỉ đổi tên lớp), nên không cần luật giữ lớp riêng: tên khóa và giá trị phân biệt sinh lúc biên dịch, không phụ thuộc tên lớp sau R8. Chưa chạy bản release trên máy; lần chạy thật đầu tiên là plan đầu tiên nối module vào một feature.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 7 (module core dùng chung `:core:project`, mới).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 2 | Nhiều phần trong một module mới, có giao diện công khai (`ProjectStore`, mô hình); ngoài module chỉ thêm một dòng ở `settings.gradle.kts` và `CLAUDE.md` |
| B. Độ rõ của yêu cầu | 1 | Spec đã xác nhận, đủ tiêu chí; vài chi tiết mô hình AI tự quyết (neo âm thanh theo khung, đơn vị lia camera, onion skin mặc định tắt), nêu ở mục 8 |
| C. Khả năng kiểm chứng tự động | 0 | Mọi hành vi có unit test JVM; chỉ dòng tài liệu cần người đọc |
| D. Ảnh hưởng chéo | 2 | Chưa feature nào dùng, nhưng hơn 15 ticket sau (T-002 tới T-004, màn vẽ, xuất file, bài học) dựa vào mô hình này; `settings.gradle.kts` và `CLAUDE.md` có thể bị plan thêm module khác cùng sửa |
| E. Độ mới của kỹ thuật | 1 | Biến thể nhỏ của mẫu đã có: JSON với kotlinx.serialization, ghi tạm rồi đổi tên; đã chạy thử (mục 4.1) |
| **Tổng** | 6 | **Mức: Cao** (chạm điểm chặn 7) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `settings.gradle.kts`, `CLAUDE.md` (bảng module). Module mới `:core:project`.
- Task khác có thể xung đột:
  - T-002 (kho dự án, cùng module) sẽ lưu "số dự án đã từng tạo", chỗ hợp lý là DataStore của `:core:data`, tức `:core:project` phụ thuộc `:core:data`. Vì vậy T-005 không được cho `:core:data` phụ thuộc `:core:project` (thành vòng): T-005 giữ giá trị thô (chuỗi `"9:16"`, số khung/giây) và feature đổi sang kiểu của `:core:project` ở chỗ dùng (câu hỏi 4).
  - T-002 (nhân bản) và T-003 (khôi phục) dựng dự án mới bằng `readFile`, `writeFile`, `write` theo thứ tự ghi ở mục 3.1 và chỗ chuẩn bị riêng bắt đầu bằng `.`; chúng ở cùng module nên thêm hàm vào `FileProjectStore` nếu cần, không phải đổi định dạng.
  - T-004, T-036, T-037 quyết luật vẽ camera ảo và lớp vẽ theo trên dữ liệu của mục 3.1 (câu hỏi 2, 3).
  - Không plan nào khác hiện đang sửa `settings.gradle.kts`.
- R8 và lint của `:core:project` được kiểm thật lần đầu ở plan đầu tiên nối module vào một feature (`:app` mới có mã của module).
- Đề xuất thứ tự chạy: chạy trước T-002, T-003, T-004.

## 7. Điều kiện dừng riêng

- Cần đổi `project-v1.json` so với khối ở mục 3.1 (tên khóa, giá trị phân biệt, cấu trúc) để test qua: dừng, vì tệp mẫu là hợp đồng định dạng người duyệt đã duyệt.
- Cần thêm phụ thuộc của `:core:project` vào một module ngoài `:core:logging` (ngoài thư viện ở mục 3.3): dừng.

## 8. Câu hỏi cho người duyệt

1. **Chỗ đặt âm thanh.** Plan neo âm thanh vào mã khung bắt đầu (`startFrameId`, `null` là từ đầu) cộng thời lượng, nên chèn hay xóa khung phía trước thì âm thanh đi theo khung. Spec chỉ ghi chữ hiển thị ("from 0:00", "frames 8 to 20", "frame 12"). Đề xuất của AI: giữ cách neo theo khung; khung bắt đầu bị xóa thì màn vẽ (T-032) dời về khung kế tiếp. Anh muốn neo theo thời gian thay không? Đổi sau khi đã có dữ liệu là tăng phiên bản định dạng.
2. **Khung không có điểm đặt camera** được vẽ thế nào? Đề xuất của AI: nội suy tuyến tính giữa hai điểm đặt gần nhất, trước điểm đầu là vị trí gốc, sau điểm cuối giữ điểm cuối; luật nằm ở lớp vẽ T-004, T-037, định dạng không đổi theo câu trả lời.
3. **Lớp vẽ theo dạng video khi có giữ khung:** ảnh thứ i đi với khung thứ i (theo chỉ số), hay theo thời gian phát? Đề xuất của AI: theo chỉ số khung, khung vượt số ảnh thì không có ảnh vẽ theo; dễ hiểu khi vẽ từng khung. Luật nằm ở T-036, định dạng không đổi theo câu trả lời.
4. **T-005 giữ giá trị thô cho khung hình và tốc độ mặc định?** Đề xuất của AI: có, để tránh vòng phụ thuộc giữa `:core:data` và `:core:project` (mục 6).
5. **Onion skin mặc định tắt hay bật** ở dự án mới? Spec editor KB-8 chỉ ghi số mặc định. Đề xuất của AI: tắt, người dùng bật bằng chip.
6. **Có viết ADR cho định dạng lưu dự án không?** Đề xuất của AI: tệp mẫu v1 trong plan, KDoc của `ProjectFormat` và test là đủ cho bản đầu; nếu anh muốn, AI viết ADR-1001 trong một PR tài liệu riêng, không chặn plan này.
7. **Duyệt chéo khi nhóm một dev.** Plan rủi ro Cao cần duyệt chéo; nếu nhóm chỉ có anh, mục 9 ghi anh vừa duyệt vừa duyệt chéo kèm lý do (risk-scoring.md, mục 3).

## 9. Quyết định duyệt

<!-- Người duyệt điền, hoặc AI ghi theo lời đồng ý trong phiên của người duyệt, nguyên văn (ADR-0048, phần C). Plan `lane: quick`: ghi "Duyệt trong phiên làm nhanh" kèm lời đồng ý. -->

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện

<!-- AI ghi khi task đã có kết quả cuối (đã merge hoặc đã dừng), trên nhánh báo cáo của phiên (auto-run.md, bước 10). Mục mới thêm vào cuối, không sửa mục cũ. -->
