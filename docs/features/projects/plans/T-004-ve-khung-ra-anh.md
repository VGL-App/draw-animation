---
ticket: T-004
title: Vẽ khung ra ảnh: gộp layer, nền, camera ảo, ảnh thu nhỏ
status: approved                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 15M          # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [core]      # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer: Nhật               # người duyệt
cross_reviewer: Nhật         # bắt buộc khi risk_level là high
approved_at: 2026-11-22
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-004: VẼ KHUNG RA ẢNH: GỘP LAYER, NỀN, CAMERA ẢO, ẢNH THU NHỎ

## 1. Tóm tắt

Thêm phần vẽ khung vào `:core:project` (gói `render`): từ một dự án và một chỉ số khung, vẽ ra một `Bitmap` ở kích thước yêu cầu, gộp các layer đang hiện từ dưới lên, trên nền của dự án (màu hay ảnh "Photo" cắt vừa khung) hoặc không nền (trong suốt), áp camera ảo nội suy giữa các điểm đặt. Cùng phần này ghi ảnh thu nhỏ `thumbnail.png` từ khung đầu. Màu nền lấy từ một đối tượng mới của `:core:designsystem`, giống nhau ở chế độ sáng và tối. Kiểm chứng bằng test điểm ảnh trên Robolectric (`@GraphicsMode(NATIVE)`), đọc thẳng `Bitmap.getPixel`, và unit test JVM cho phần tính camera.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau. Ticket chỉ làm phần vẽ; màn danh sách hiện ảnh thu nhỏ là T-009, màn vẽ hiện khung là T-019, công cụ camera là T-037, mã hóa file xuất là T-043, T-044, lớp vẽ theo là T-036.

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| projects KB-2 | Khi xem một thẻ thì thẻ có ảnh thu nhỏ của dự án (khung đầu tiên, gộp mọi layer đang hiện, trên nền của dự án; câu hỏi 20) | `ProjectThumbnails.update(id)` vẽ khung có chỉ số 0, có nền, rồi ghi `thumbnail.png` qua `ProjectStore.writeFile(id, ProjectFile.Thumbnail, ...)`. Người gọi (màn vẽ khi rời màn, màn Tạo dự án sau khi tạo) nối ở ticket của họ |
| projects KB-2 | Khi một dự án không đọc được thì thẻ có ảnh thu nhỏ trống | `update` trên dự án `Corrupt`, `NewerVersion` hay `NotFound` không ghi gì, trả `ProjectUnreadable` |
| projects KB-2 | Khi từ màn vẽ quay về danh sách thì ảnh thu nhỏ đúng với nội dung vừa vẽ | Mỗi lần `update` vẽ lại từ tệp ô hiện tại, ghi đè ảnh cũ |
| projects KB-3, câu 3 | Ảnh "Photo" là nền cố định của mọi khung, có trong file xuất, được cắt vừa khung hình | Ảnh nền vẽ kiểu "phủ kín rồi cắt giữa": phóng theo tỷ lệ lớn hơn trong hai chiều, phần thừa cắt đều hai bên; không méo, không viền trống |
| editor KB-6 | Khi bấm ẩn thì layer không hiện trên canvas và lúc phát | Layer có `visible = false` không được vẽ, kể cả khi có tệp ô |
| editor KB-12, câu 12 | Lớp vẽ theo không có trong file xuất | Phần vẽ không bao giờ đọc hay vẽ lớp vẽ theo; màn vẽ (T-036) tự vẽ lớp này |
| editor KB-15 | Lia, phóng, xoay được cả cảnh ở từng khung, không đổi nét vẽ trên các layer; khi phát thì cảnh chuyển động theo camera; khi tắt camera hay xóa điểm đặt thì cảnh về vị trí gốc | Camera là phép biến đổi lúc vẽ, không sửa tệp ô. Khung có điểm đặt dùng đúng điểm đó; khung không có điểm đặt nội suy tuyến tính giữa hai điểm gần nhất, trước điểm đầu là gốc, sau điểm cuối giữ điểm cuối (anh Nhật chốt 2026-11-22, plan T-001 câu 2). `camera.enabled = false` thì mọi khung ở gốc |
| export mục 2, KB-2 | Chất lượng 720p hay 1080p; chuỗi PNG luôn trong suốt, GIF trong suốt bỏ nền | Kích thước ra do người gọi chọn (`RenderSize.forExport(project, resolution)`); tham số `includeBackground = false` cho ảnh không nền, chỗ không có nét có alpha 0 |
| export câu 13 | 720p, 1080p tính theo cạnh ngắn của khung với mọi tỷ lệ | `RenderSize.forExport` dùng `canvasSize(aspectRatio, resolution)` của T-001 |

Luật đã chốt áp vào phần vẽ (plan T-001, mục 8, anh Nhật trả lời 2026-11-22): nội suy camera như trên; lớp vẽ theo dạng video đi theo chỉ số khung (không ảnh hưởng phần vẽ này vì lớp đó không xuất ra); onion skin mặc định tắt và không bao giờ có trong ảnh vẽ ra (onion skin chỉ là hiển thị của màn vẽ, T-022).

Ngoài phạm vi: watermark (T-044 vẽ lên ảnh mà phần này trả ra), lớp vẽ theo (T-036), onion skin, mã hóa GIF, MP4 (T-043, T-044), nối người gọi `update` (T-011, T-019 trở đi), hiện ảnh thu nhỏ trên thẻ (T-009).

Thiết kế tham chiếu: Không có thiết kế riêng (phần vẽ không có giao diện; docs/ops/design-workflow.md). Màu ba ô nền lấy từ `features/projects/design/context/NewProjectScreen_default_light.md` và `_dark.md`, dòng 126 tới 131 (xem câu hỏi 1). Kích thước khung ảnh thu nhỏ trên thẻ: 168 × 94 dp ở `ProjectsScreen_list_light.md`, dòng 91.

ADR liên quan: ADR-0001 (core chỉ phụ thuộc core; `:core:project` thêm phụ thuộc `:core:designsystem`), ADR-0008 (màu lấy từ `:core:designsystem`, code là gốc), ADR-0019 (log không chứa mã dự án, tên, nội dung tệp).

## 3. Giải pháp

### 3.1. Cách làm

Plan này dựa trên mô hình và `ProjectStore` của plan T-001 đã duyệt (`features/projects/plans/T-001-core-project.md`), chạy trước trong cùng phiên. Plan không sửa file Kotlin nào của T-001; chỉ thêm gói `render`, một Hilt module riêng và sửa `build.gradle.kts` của module.

**Màu nền** (`:core:designsystem`, tệp mới `theme/ProjectBackgroundColors.kt`): `object ProjectBackgroundColors { const val WHITE: Int = 0xFFFFFFFF.toInt(); const val DARK: Int = 0xFF22262E.toInt(); const val BLUE: Int = 0xFF2F6FE0.toInt() }` (giá trị theo câu hỏi 1), mã ARGB dạng `Int`, giống nhau ở chế độ sáng và tối. KDoc ghi lý do: nền là một phần của file xuất, nên không được đổi theo theme của máy; ô màu ở màn Tạo dự án (T-011) và nền dự án trên màn vẽ (T-019) cũng dùng đúng các giá trị này (`Color(ProjectBackgroundColors.BLUE)`), không dùng `AppColors.canvas` (màu đó đổi theo theme: `#FFFFFF` sáng, `#FAFAF7` tối). Dùng `Int` chứ không dùng `androidx.compose.ui.graphics.Color` để `:core:project` không cần Compose trên classpath biên dịch (thư viện Compose của `:core:designsystem` là `implementation`, không lộ ra). Giá trị theo câu hỏi 1. Ba màu vào `docs/design-system/tokens.json` thành một nhóm riêng không theo theme, khóa `"projectBackground"` ở cấp trên cùng (`{"white": "#FFFFFF", "dark": "#22262E", "blue": "#2F6FE0"}`), đặt sau nhóm `"color"`: `tokensJson()` trong `DesignTokensTest.kt` sinh thêm dòng này, rồi sinh lại tệp bằng `./gradlew :core:designsystem:testDebugUnitTest -PdesignTokens.update=true`. Nhóm nằm ngoài `"color"` vì `scripts/figma-export.py` coi mỗi khóa con của `"color"` là một theme; script chỉ đọc `color`, `typography`, `spacingDp`, nên bỏ qua khóa mới.

**Bảng màu của phần vẽ** (`render/BackgroundPalette.kt`): `data class BackgroundPalette(white: Int, dark: Int, blue: Int)`, `fun colorOf(color: BackgroundColor): Int`, `companion val Default` lấy từ `ProjectBackgroundColors`. Phần vẽ nhận bảng màu qua hàm dựng (test truyền màu riêng để chứng minh màu đi theo tham số), Hilt truyền `Default`, nên mọi nơi ra cùng một màu mà không nơi nào viết cứng mã màu.

**Kích thước ra** (`render/RenderSize.kt`): `data class RenderSize(width: Int, height: Int)`; `RenderSize.forExport(project, resolution) = canvasSize(project.aspectRatio, resolution)`; `RenderSize.thumbnail(project)`: vừa trong hình vuông `THUMBNAIL_MAX_EDGE = 512` theo tỷ lệ dự án: 16:9 là 512 × 288, 9:16 là 288 × 512, 1:1 là 512 × 512 (khung ảnh trên thẻ 168 × 94 dp ở xxhdpi là 504 × 282 điểm ảnh, nên 512 đủ nét).

**Camera** (`render/CameraTimeline.kt`, Kotlin thuần, không lớp Android): `val CAMERA_ORIGIN = CameraKey(panX = 0f, panY = 0f, zoom = 1f, rotationDegrees = 0f)`; `fun cameraAt(project: Project, frameIndex: Int): CameraKey`:

1. `require(frameIndex in project.frames.indices)`.
2. `project.camera.enabled == false` hay không khung nào có `cameraKey` thì `CAMERA_ORIGIN`.
3. Khung có `cameraKey` thì trả đúng điểm đó.
4. Khung đứng trước điểm đặt đầu tiên thì `CAMERA_ORIGIN`; đứng sau điểm đặt cuối cùng thì điểm cuối.
5. Còn lại nằm giữa hai điểm đặt gần nhất `a < i < b`: nội suy tuyến tính bốn số theo mốc bắt đầu của khung, `t(k)` = tổng `hold` của các khung trước `k`; `f = (t(i) - t(a)) / (t(b) - t(a))`. Tính theo mốc thời gian để cảnh chạy đều khi phát với khung giữ 2× tới 4× (câu hỏi 3). Góc xoay nội suy thẳng theo độ, không đi đường ngắn nhất (từ 0 tới 270 đi qua 90, 180).

**Phép biến đổi camera** (`render/CameraMatrix.kt`): `fun cameraMatrix(key: CameraKey, width: Int, height: Int): android.graphics.Matrix`, theo thứ tự: phóng `zoom` quanh tâm khung, xoay `rotationDegrees` quanh tâm (số dương là chiều kim đồng hồ trên màn hình), rồi dời `panX * width`, `panY * height` điểm ảnh (số dương sang phải, xuống dưới). Tức là `Matrix().apply { postScale(z, z, cx, cy); postRotate(r, cx, cy); postTranslate(panX * width, panY * height) }`. KDoc ghi: điểm đặt mô tả cảnh dịch chuyển thế nào trên khung, không phải máy quay đi đâu; T-037 đổi cử chỉ của người dùng sang điểm đặt theo nghĩa này. Lượng dời tính theo kích thước ảnh ra, nên cùng điểm đặt cho cùng bố cục ở 720p, 1080p và ảnh thu nhỏ.

**Gộp lên canvas** (`render/FrameComposer.kt`, công khai, không đọc tệp). Màn vẽ (T-019) hiện khung qua đúng phần này, với ảnh ô đã giải mã sẵn trong bộ nhớ:

```kotlin
sealed interface FrameBackground {
    data class Solid(val argb: Int) : FrameBackground
    data class Photo(val photo: Bitmap, val gapArgb: Int) : FrameBackground
}

object FrameComposer {
    /** Draws [background] (null: transparent) then [layers] bottom to top (null: empty cell) through [camera]. */
    fun compose(canvas: Canvas, width: Int, height: Int, background: FrameBackground?, layers: List<Bitmap?>, camera: CameraKey)
}
```

`compose` gọi ba bước internal theo thứ tự; `StoreFrameRenderer` gọi cùng ba bước đó, từng ô một (giải mã một ô, vẽ, `recycle()`), để không phải giữ mọi ô trong bộ nhớ cùng lúc. Test `composeInMemory_equalsRenderFromStore` khóa hai đường cho cùng điểm ảnh.

- `drawSolid(argb)`: tô kín canvas, không qua camera (tô kín thì camera không đổi gì).
- `drawPhoto(photo: Bitmap, gapArgb: Int)`: tô kín bằng `gapArgb` (màu trắng của bảng màu), rồi vẽ ảnh qua camera vào cả khung theo kiểu phủ kín cắt giữa: `scale = max(width / photo.width, height / photo.height)`, vùng nguồn rộng `width / scale`, cao `height / scale`, đặt giữa ảnh. Ảnh nền đi cùng cảnh khi lia, phóng, xoay; chỗ hở khi thu nhỏ hay xoay hiện màu trắng (câu hỏi 2).
- `drawLayer(cell: Bitmap)`: vẽ qua camera vào cả khung `Rect(0, 0, width, height)`, `Paint(FILTER_BITMAP_FLAG)`. Tệp ô lưu ở kích thước khung vẽ của dự án; vẽ vào cả khung nên ảnh ra 720p của dự án 1080p, hay ảnh thu nhỏ, được thu nhỏ đúng tỷ lệ.

**Giải mã ảnh nền** (`render/BackgroundDecoder.kt`, `object` công khai, để màn vẽ giải mã ảnh nền đúng như bản xuất): `fun decode(bytes: ByteArray, width: Int, height: Int): Bitmap?` công khai và `internal fun sampleSize(srcWidth: Int, srcHeight: Int, width: Int, height: Int): Int` (test gọi trực tiếp). Đọc kích thước trước (`inJustDecodeBounds`), rồi giải mã với `inSampleSize` là lũy thừa của 2 lớn nhất mà ảnh sau khi giảm vẫn phủ kín khung ra: `scale = max(width / srcWidth, height / srcHeight)`, tăng gấp đôi khi `sample * 2 * scale <= 1`. Ví dụ ảnh 4000 × 1000 vào khung 100 × 100 giải mã thành 500 × 125. Không đọc được thì `null`. Phần này không xoay ảnh theo EXIF và không thêm thư viện: T-011 chép ảnh vào dự án đã xoay đúng và đã thu nhỏ (mục 6).

**Phần vẽ công khai** (`render/FrameRenderer.kt`, interface):

```kotlin
interface FrameRenderer {
    /** Draws frame [frameIndex] of [project] into a new ARGB_8888 bitmap of [size]. */
    suspend fun render(project: Project, frameIndex: Int, size: RenderSize, includeBackground: Boolean): Bitmap

    /** Same as [render], into [target] (cleared first), so export can reuse one bitmap for every frame. */
    suspend fun renderInto(target: Bitmap, project: Project, frameIndex: Int, includeBackground: Boolean)
}
```

Cài đặt `StoreFrameRenderer(store: ProjectStore, palette: BackgroundPalette, renderDispatcher: CoroutineDispatcher, logger: AppLogger)` (internal):

1. `require(frameIndex in project.frames.indices)`; `target.isMutable` và cấu hình `ARGB_8888`, không thì `IllegalArgumentException`.
2. Chạy trong `withContext(renderDispatcher)` (Hilt truyền `Dispatchers.Default`, vì vẽ tốn CPU; đọc tệp đã qua dispatcher của kho).
3. `target.eraseColor(Color.TRANSPARENT)`; tính `cameraAt(project, frameIndex)`.
4. Có nền: `Solid(color)` thì `drawSolid(palette.colorOf(color))`; `Photo(ext)` thì `store.readFile(project.id, ProjectFile.Background(ext))`, giải mã bằng `BackgroundDecoder.decode(bytes, width, height)` rồi `drawPhoto(photo, palette.white)`, `recycle()` sau khi vẽ; không có tệp hay giải mã ra `null` thì `drawSolid(palette.white)` và `logger.warn("ProjectRender", "Background image unreadable")`.
5. Với mỗi layer theo thứ tự `project.layers` (dưới lên trên), bỏ layer `visible = false`: `store.readFile(project.id, ProjectFile.Cell(frame.id, layer.id))`; `null` là ô trống, bỏ qua; giải mã ra `null` thì bỏ qua và `logger.warn("ProjectRender", "Cell image unreadable")`; giải mã được thì `drawLayer`, rồi `recycle()` ngay để mỗi lúc chỉ giữ một ảnh ô trong bộ nhớ (ảnh 1080 × 1920 là khoảng 8 MB).
6. `IOException` của kho đi ra cho người gọi (màn Xuất, màn vẽ đổi thành trạng thái lỗi ở ticket của họ). Log không chứa mã dự án, mã khung, tên dự án hay nội dung tệp.

Không đọc `project.reference`, `project.onionSkin`, `project.sounds`: ảnh vẽ ra không bao giờ có lớp vẽ theo hay onion skin. Khung giữ 2× tới 4× vẫn chỉ là một ảnh; người xuất lặp ảnh theo `hold` (T-043, T-044).

**Ảnh thu nhỏ** (`render/ProjectThumbnails.kt`): interface `ProjectThumbnails { suspend fun update(id: ProjectId): ThumbnailResult }`, `enum class ThumbnailResult { WRITTEN, PROJECT_UNREADABLE, FAILED }`. Cài đặt `DefaultProjectThumbnails(store, renderer, logger)` (internal):

1. `store.read(id)`; khác `Ok` thì `PROJECT_UNREADABLE`, không ghi gì, không xóa ảnh cũ (KB-2: thẻ hỏng hiện ảnh trống là việc của T-009, dựa vào kết quả đọc dự án).
2. `renderer.render(project, 0, RenderSize.thumbnail(project), includeBackground = true)`, nén PNG (`Bitmap.compress(PNG, 100, ...)`), `store.writeFile(id, ProjectFile.Thumbnail, bytes)` (ghi nguyên tử của T-001), `recycle()`, trả `WRITTEN`.
3. `IOException` ở bước đọc tệp hay ghi thì `FAILED` và `logger.warn("ProjectRender", "Thumbnail not written")`; không ném lỗi, vì ảnh thu nhỏ hỏng không được làm hỏng việc rời màn vẽ.

**Hilt** (`di/RenderModule.kt`, tệp mới, không sửa `ProjectModule.kt` của T-001 để không cùng sửa với T-002): `@Provides @Singleton FrameRenderer = StoreFrameRenderer(store, BackgroundPalette.Default, Dispatchers.Default, logger)`, `@Provides @Singleton ProjectThumbnails = DefaultProjectThumbnails(store, renderer, logger)`.

**Build** (`core/project/build.gradle.kts`, T-001 tạo): thêm `implementation(project(":core:designsystem"))`; `testImplementation(libs.robolectric)`, `testImplementation(libs.androidx.test.core)`, `testImplementation(libs.androidx.test.ext.junit)` như `core/network/build.gradle.kts`. Convention `template.android.library` không thêm Robolectric (chỉ `template.android.compose` thêm), nên module tự khai; `unitTests.isIncludeAndroidResources = true` đã có ở `configureKotlinAndroid`. Thêm `src/test/resources/robolectric.properties` một dòng `sdk=35` như các module có Robolectric khác. Không áp plugin Compose cho `:core:project`.

**`CLAUDE.md`**: dòng `:core:project` (T-001 thêm) ghi thêm "vẽ khung ra ảnh (gộp layer, nền, camera ảo, ảnh thu nhỏ) cho màn vẽ, ảnh thu nhỏ, màn Xuất và bài học".

Mẫu có sẵn được làm theo: `withContext(ioDispatcher)` và log của `FileProjectStore` (T-001); Robolectric trong module core không Compose của `:core:network`; đọc điểm ảnh trong test `@GraphicsMode(NATIVE)` của `:core:ads` (ở đây đọc thẳng `Bitmap` do phần vẽ trả ra, không cần vẽ cửa sổ).

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `core/project/build.gradle.kts` | Thêm `implementation(project(":core:designsystem"))` và ba dòng `testImplementation` Robolectric, `androidx.test.core`, `androidx.test.ext.junit` (T-001 tạo tệp) |
| Sửa | `CLAUDE.md` | Dòng `:core:project` của bảng Cấu trúc module ghi thêm phần vẽ khung |
| Tạo | `core/designsystem/src/main/kotlin/com/vgl/draw/animation/core/designsystem/theme/ProjectBackgroundColors.kt` | Ba màu nền dự án, ARGB `Int`, không đổi theo theme |
| Tạo | `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/ProjectBackgroundColorsTest.kt` | Khóa ba giá trị đã duyệt ở câu hỏi 1 |
| Sửa | `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/DesignTokensTest.kt` | `tokensJson()` sinh thêm nhóm `"projectBackground"` từ `ProjectBackgroundColors`; thêm test `projectBackgroundColors_listedInTokens` |
| Sửa | `docs/design-system/tokens.json` | Sinh lại bằng `-PdesignTokens.update=true`: thêm một dòng nhóm `"projectBackground"`, phần khác giữ nguyên |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/render/BackgroundPalette.kt` | Bảng màu, `colorOf`, `Default` từ `ProjectBackgroundColors` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/render/RenderSize.kt` | `RenderSize`, `forExport`, `thumbnail`, `THUMBNAIL_MAX_EDGE` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/render/CameraTimeline.kt` | `CAMERA_ORIGIN`, `cameraAt` (nội suy theo mốc thời gian) |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/render/CameraMatrix.kt` | `cameraMatrix(key, width, height)` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/render/FrameComposer.kt` | `FrameBackground`, `FrameComposer.compose` công khai; bước internal `drawSolid`, `drawPhoto` (phủ kín cắt giữa), `drawLayer` qua camera |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/render/BackgroundDecoder.kt` | Giải mã ảnh nền với `inSampleSize` theo khung ra |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/render/FrameRenderer.kt` | Interface công khai `render`, `renderInto` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/render/StoreFrameRenderer.kt` | Đọc ô và ảnh nền từ `ProjectStore`, giải mã, gộp, log |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/render/ProjectThumbnails.kt` | Interface, `ThumbnailResult`, `DefaultProjectThumbnails` |
| Tạo | `core/project/src/main/kotlin/com/vgl/draw/animation/core/project/di/RenderModule.kt` | Hilt cấp `FrameRenderer`, `ProjectThumbnails` |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/render/CameraTimelineTest.kt` | Nội suy, trước điểm đầu, sau điểm cuối, camera tắt (JVM) |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/render/RenderSizeTest.kt` | Kích thước xuất và ảnh thu nhỏ (JVM) |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/render/BackgroundPaletteTest.kt` | `Default` khớp `ProjectBackgroundColors` (JVM) |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/render/FrameComposerTest.kt` | Điểm ảnh: nền màu, ảnh nền cắt giữa, trong suốt, camera (Robolectric `NATIVE`) |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/render/StoreFrameRendererTest.kt` | Điểm ảnh qua kho thật: thứ tự layer, ẩn, ô thiếu, ô hỏng, độ phân giải, lớp vẽ theo không vẽ, dùng lại bitmap (Robolectric `NATIVE`) |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/render/BackgroundDecoderTest.kt` | Ảnh lớn được giảm mẫu và vẫn cắt giữa đúng, ảnh hỏng ra `null` (Robolectric `NATIVE`) |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/render/ProjectThumbnailsTest.kt` | Ghi `thumbnail.png` đúng cỡ và nội dung khung đầu, dự án hỏng, lỗi ghi (Robolectric `NATIVE`) |
| Tạo | `core/project/src/test/resources/robolectric.properties` | Một dòng `sdk=35`, như mọi module có test Robolectric (`core/network`, `core/ads`, `core/designsystem`): không có tệp này Robolectric chạy theo targetSdk 36 và dừng ở bước dựng môi trường (mục 4.1) |
| Tạo | `core/project/src/test/kotlin/com/vgl/draw/animation/core/project/render/RenderTestImages.kt` | Hàm phụ của test: dựng bitmap ô có ô vuông màu, nén PNG, đọc điểm ảnh, dự án mẫu cho phần vẽ |

Không có ảnh chuẩn screenshot: test so điểm ảnh bằng câu khẳng định, không bằng ảnh chuẩn.

Nơi đọc các file có sẵn mà plan sửa, và vì sao không phải sửa thêm:

- `core/project/build.gradle.kts`: `grep -rn "build.gradle.kts" scripts/ .github/` chỉ thấy `.github/workflows/pr-check.yml` (bộ lọc `**/build.gradle.kts`: PR của task chạy cả job build bản release), `scripts/ui-test-verdict.sh` và `scripts/setup-check.sh` (chỉ đọc `app/build.gradle.kts`), `scripts/tests/run.sh` (dựng tệp giả trong thư mục tạm, không đọc tệp của module này).
- `docs/design-system/tokens.json` (`grep -rln "tokens.json" scripts/ .github/ */src/test */*/src/test`):
  - `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/DesignTokensTest.kt`: sinh và so tệp; có trong bảng trên.
  - `core/designsystem/build.gradle.kts`: truyền đường dẫn tệp và cờ `designTokens.update` cho test; không đổi.
  - `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/DesignSystemCatalogScreenshotTest.kt`: chỉ nhắc tên tệp trong KDoc; ảnh của catalog không đổi vì catalog không vẽ màu mới.
  - `scripts/figma-export.py`: đọc `color`, `typography`, `spacingDp`; khóa mới ở cấp trên cùng bị bỏ qua (mục 3.1).
  - `scripts/main-guard-verified.sh`, `scripts/tests/run.sh` (`GRADLE_READ_DOCS`, thử `merge-if-green`): coi tệp là code khi đổi, nên PR của task chạy đủ CI như PR code; không đọc nội dung.
  - `scripts/plan-check.py`: chỉ nhắc tên trong chú thích về cách tìm tên tệp.
- `CLAUDE.md` (`grep -rln "CLAUDE.md" scripts/ .github/ */src/test */src/androidTest core/*/src/test`):
  - `app/src/androidTest/kotlin/com/vgl/draw/animation/AppLaunchTest.kt`, `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/NativeFullScreenOverlayScreenshotTest.kt`, `scripts/local-setup.sh`, `scripts/run-watchdog.py`: chỉ nhắc tên file trong chú thích hay lời nhắn.
  - `scripts/tests/run.sh`: đọc dòng lệnh `git status --short -- '...'` ở mục Lệnh và dòng `export ANDROID_HOME`; plan chỉ sửa một dòng của bảng module, không đổi hai dòng đó.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có (phần dùng chung của module core; cờ của màn hình thuộc ticket màn hình).
- Quảng cáo: Không chạm.
- Sự kiện phân tích: Không chạm.
- File R2: Không chạm.
- Dữ liệu trên máy: ghi tệp `thumbnail.png` đã có trong định dạng phiên bản 1 của T-001 (mục 3.1 của T-001), không đổi `project.json`, không đổi phiên bản định dạng, không chạm điểm chặn 9.
- **Quyền truy cập:** Không thêm. Không thêm: `CAMERA` (camera ảo chỉ là phép biến đổi lúc vẽ, không dùng máy ảnh của điện thoại). `Bitmap`, `Canvas`, `Matrix`, `BitmapFactory`, `Bitmap.compress` không cần quyền ở mọi phiên bản từ minSdk 26 tới targetSdk 36; tệp đọc ghi trong `filesDir` qua `ProjectStore`.
- **Thư viện và SDK:** Không thêm thư viện mới. `:core:project` thêm `implementation(project(":core:designsystem"))` (module core, ADR-0001 cho phép). Test dùng thư viện đã có trong `gradle/libs.versions.toml`: `robolectric` (4.17), `androidx-test-core`, `androidx-test-ext-junit`, thêm `testImplementation` ở `:core:project` như `:core:network` đã làm. Lớp Android dùng (`android.graphics.*`) có sẵn trong `android.jar` của compileSdk.

### 3.4. Phương án dự phòng

Không có: đã chạy thử (mục 4.1), mọi phép so điểm ảnh khớp đúng tuyệt đối. Điểm ảnh lệch thì sửa code; vẫn lệch sau 3 lần sửa thì dừng theo điều kiện 4 của `risk-scoring.md`, không nới phép so.

### 3.5. Hành vi đã có bị đổi

Không có. Plan chỉ tạo file Kotlin mới (gói `render`, `RenderModule.kt`, `ProjectBackgroundColors.kt`); không sửa file Kotlin nào ngoài thư mục test, kể cả file của T-001. `DesignTokensTest.kt` là file test; `core/project/build.gradle.kts` chỉ thêm phụ thuộc; `CLAUDE.md`, `tokens.json` là tài liệu. `scripts/existing-tests.py` không tìm thấy test nào kiểm hành vi bị đổi (kết quả ở mô tả PR).

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Không có | Không có | Không có | Không có |

## 4. Kiểm thử

Quy ước chung:

- Test Robolectric: `@RunWith(AndroidJUnit4::class)`, `@GraphicsMode(GraphicsMode.Mode.NATIVE)` (chế độ mặc định không vẽ điểm ảnh thật), không `@Config` riêng; SDK của Robolectric lấy từ `robolectric.properties` (`sdk=35`). Đọc điểm ảnh bằng `bitmap.getPixel(x, y)` trên `Bitmap` do phần vẽ trả ra; không `captureToImage()`, không `captureRoboImage`. Màu so bằng `assertEquals` giá trị ARGB; điểm đo ở giữa vùng màu, cách mép ít nhất 5 điểm ảnh.
- `RenderTestImages.cell(width, height, vararg squares: Square)` dựng bitmap trong suốt có các ô vuông màu đặc (`Paint` tắt khử răng cưa), `png(bitmap)` nén PNG. Màu dùng trong test: `const val BLACK: Int = 0xFF000000.toInt()`, `RED = 0xFFFF0000.toInt()`, `GREEN = 0xFF00FF00.toInt()`, `BLUE_INK = 0xFF0000FF.toInt()`. Bảng màu test `TEST_PALETTE = BackgroundPalette(white = 0xFFFFFFFF.toInt(), dark = 0xFF333333.toInt(), blue = 0xFF0055AA.toInt())`, khác `Default`, để chứng minh màu đi theo tham số.
- Test qua kho dùng `FileProjectStore` thật của T-001 trên `TemporaryFolder`, `RecordingAppLogger`, dispatcher `StandardTestDispatcher(testScheduler)` của `runTest` cho cả kho và phần vẽ. Dự án dựng bằng `Project.create(...)` của T-001 rồi `copy(...)`; ô ghi bằng `store.writeFile(id, ProjectFile.Cell(frameId, layerId), png(...))`, dự án ghi bằng `store.write(project)`.
- Khung 100 × 100 trong test của `FrameComposer` (canvas tạo trực tiếp từ `Bitmap.createBitmap(100, 100, ARGB_8888)`).

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| Camera tắt thì cảnh ở gốc | Unit test | `CameraTimelineTest.cameraDisabled_returnsOriginEvenWithKeys`: dự án 3 khung, khung 1 có điểm `(0.3, 0.1, 2.0, 45)`, `camera.enabled = false`: mọi khung trả `CAMERA_ORIGIN`; đối chứng: cùng dự án `enabled = true` thì khung 1 trả đúng điểm đó |
| Không có điểm đặt | Unit test | `CameraTimelineTest.noKeys_returnsOrigin`: `enabled = true`, không khung nào có điểm: mọi khung là `CAMERA_ORIGIN` |
| Nội suy theo mốc thời gian | Unit test | `CameraTimelineTest.betweenKeys_interpolatesByFrameStartTime`: 5 khung, `hold` `[1, 1, 3, 1, 1]`, khung 1 có `(0, 0, 1, 0)`, khung 3 có `(0.4, -0.2, 2.0, 90)`. Mốc bắt đầu 0, 1, 2, 5, 6; khung 2 trả `(0.1, -0.05, 1.25, 22.5)` (sai số `1e-5`), không phải giá trị theo chỉ số `(0.2, -0.1, 1.5, 45)` |
| Trước điểm đầu là gốc, sau điểm cuối giữ điểm cuối | Unit test | `CameraTimelineTest.beforeFirstKey_isOrigin_afterLastKey_holdsLast`: cùng dự án trên: khung 0 là `CAMERA_ORIGIN`, khung 4 là `(0.4, -0.2, 2.0, 90)` |
| Khung có điểm đặt dùng đúng điểm | Unit test | `CameraTimelineTest.keyFrame_returnsItsKey`: khung 1 và khung 3 trả đúng điểm của mình |
| Xoay không đi đường ngắn nhất | Unit test | `CameraTimelineTest.rotation_interpolatesLinearlyInDegrees`: 3 khung `hold` 1, khung 0 có góc 0, khung 2 có góc 270: khung 1 có góc 135 |
| Chỉ số khung sai | Unit test | `CameraTimelineTest.frameIndexOutOfRange_throws`: -1 và `frames.size` ném `IllegalArgumentException` |
| Kích thước xuất theo cạnh ngắn | Unit test | `RenderSizeTest.forExport_matchesCanvasSize`: (16:9, 1080p) 1920 × 1080, (9:16, 720p) 720 × 1280, (1:1, 720p) 720 × 720 |
| Kích thước ảnh thu nhỏ | Unit test | `RenderSizeTest.thumbnail_fitsMaxEdgeKeepingRatio`: 16:9 là 512 × 288, 9:16 là 288 × 512, 1:1 là 512 × 512, cho cả 720p và 1080p |
| Màu nền lấy từ `:core:designsystem` | Unit test | `BackgroundPaletteTest.default_usesDesignSystemColors`: `Default.colorOf(WHITE) == ProjectBackgroundColors.WHITE`, tương tự `DARK`, `BLUE`; ba giá trị khác nhau đôi một |
| Ba màu nền đã duyệt, không đổi theo theme | Unit test | `ProjectBackgroundColorsTest.values_matchApprovedColors`: `WHITE == 0xFFFFFFFF.toInt()`, `DARK == 0xFF22262E.toInt()`, `BLUE == 0xFF2F6FE0.toInt()` (giá trị theo câu trả lời câu hỏi 1); đối tượng không phụ thuộc theme nên không có bản sáng, tối riêng |
| Nền màu tô kín | Robolectric | `FrameComposerTest.drawSolid_fillsWholeFrame`: `drawSolid(TEST_PALETTE.dark)`: điểm (5, 5), (50, 50), (94, 94) đều `0xFF333333.toInt()` |
| Không nền thì trong suốt | Robolectric | `FrameComposerTest.noBackground_leavesTransparent`: chỉ `drawLayer(cell(100, 100, Square(0, 0, 20, BLACK)))`: (10, 10) là `BLACK`, (50, 50) có alpha 0 |
| Ảnh nền phủ kín, cắt giữa (khung vuông) | Robolectric | `FrameComposerTest.drawPhoto_landscapePhotoInSquare_cropsCenter`: ảnh 300 × 100 ba sọc dọc `GREEN`, `RED`, `BLUE_INK` mỗi sọc rộng 100: điểm (5, 50), (95, 50), (50, 5), (50, 94) đều `RED` (kéo méo thì (5, 50) là `GREEN`; thu vừa có viền thì (50, 5) là màu trắng) |
| Ảnh nền phủ kín, cắt giữa (khung dọc) | Robolectric | `FrameComposerTest.drawPhoto_squarePhotoInPortrait_cropsCenter`: canvas 50 × 100, ảnh 100 × 100 nửa trái `GREEN`, nửa phải `RED`: (10, 50) là `GREEN`, (40, 50) là `RED` |
| Lia | Robolectric | `FrameComposerTest.pan_movesLayerByFractionOfFrame`: ô `Square(0, 0, 20, BLACK)`, điểm `(0.5, 0.25, 1, 0)`: (60, 35) là `BLACK`, (10, 10) alpha 0; đối chứng `CAMERA_ORIGIN`: (10, 10) là `BLACK` |
| Phóng quanh tâm | Robolectric | `FrameComposerTest.zoom_scalesAroundCenter`: ô `Square(40, 40, 20, BLACK)` (40 tới 60), zoom 2: (33, 33) là `BLACK` (ô thành 30 tới 70); đối chứng zoom 1: (33, 33) alpha 0 |
| Xoay theo chiều kim đồng hồ quanh tâm | Robolectric | `FrameComposerTest.rotate_clockwiseAroundCenter`: ô `Square(30, 30, 10, BLACK)`, góc 90: (65, 35) là `BLACK`, (35, 35) alpha 0 |
| Thứ tự phóng, xoay, rồi lia | Robolectric | `FrameComposerTest.combined_scaleThenRotateThenPan`: ô `Square(30, 30, 10, BLACK)`, điểm `(0.1, 0, 2, 90)`: ô thành x 80 tới 100, y 10 tới 30; (90, 20) là `BLACK`, (70, 20) alpha 0 |
| Lia theo phần của khung ở mọi kích thước | Robolectric | `FrameComposerTest.pan_scalesWithOutputSize`: canvas 200 × 200, ô 100 × 100 có `Square(0, 0, 20, BLACK)` (vẽ phóng thành 0 tới 40), điểm `(0.5, 0, 1, 0)`: (120, 20) là `BLACK`, (20, 20) alpha 0 |
| Ảnh nền đi cùng camera, chỗ hở là màu trắng | Robolectric | `FrameComposerTest.drawPhoto_followsCamera_gapIsWhite`: ảnh 100 × 100 toàn `RED`, zoom 0.5: (5, 5) là `TEST_PALETTE.white`, (50, 50) là `RED`; `drawSolid` với zoom 0.5: (5, 5) vẫn là màu nền (theo câu trả lời câu hỏi 2) |
| Layer gộp từ dưới lên, layer ẩn không vẽ | Robolectric | `StoreFrameRendererTest.layers_bottomToTop_hiddenSkipped`: dự án 1:1 720p, 3 layer: dưới `Square(0, 0, 720, BLACK)`, giữa ẩn `Square(0, 0, 720, RED)`, trên `Square(0, 0, 360, BLUE_INK)`; ảnh ra 100 × 100 có nền: (75, 75) là `BLACK`, (25, 25) là `BLUE_INK`; đối chứng: layer giữa hiện thì (75, 75) là `RED` |
| Nền màu của dự án theo bảng màu | Robolectric | `StoreFrameRendererTest.solidBackground_usesPaletteColor`: dự án không có ô nào, nền `Solid(BLUE)`, ảnh ra 100 × 100: (50, 50) là `TEST_PALETTE.blue`; nền `Solid(WHITE)` là `TEST_PALETTE.white` |
| Không nền (chuỗi PNG, GIF trong suốt) | Robolectric | `StoreFrameRendererTest.withoutBackground_isTransparentOutsideStrokes`: dự án nền `Photo("png")` có `background.png`, một ô `Square(0, 0, 360, BLACK)`, `includeBackground = false`: (25, 25) là `BLACK`, (75, 75) alpha 0 |
| Ảnh nền "Photo" từ dự án | Robolectric | `StoreFrameRendererTest.photoBackground_readFromProject`: `background.png` 300 × 100 ba sọc như trên, dự án 1:1, không ô: (5, 50) và (95, 50) là `RED` |
| Ảnh nền mất hay hỏng | Robolectric | `StoreFrameRendererTest.photoBackgroundMissing_fallsBackToWhiteAndLogs`: nền `Photo("png")` không có tệp: (50, 50) là `TEST_PALETTE.white`, `RecordingAppLogger` có đúng 1 mục `WARN` thẻ `ProjectRender`; tệp chứa `"not a png"` cũng vậy |
| Ô thiếu là ô trống | Robolectric | `StoreFrameRendererTest.missingCell_isEmpty`: dự án 1:1 720p nền `Solid(WHITE)`, 2 layer, chỉ layer dưới có tệp ô `Square(0, 0, 360, BLACK)`: render ra 100 × 100 không ném lỗi, không log `WARN`; (25, 25) là `BLACK`, (75, 75) là `TEST_PALETTE.white` |
| Ô hỏng bỏ qua, có log không chứa dữ liệu | Robolectric | `StoreFrameRendererTest.undecodableCell_skippedAndLogged`: cùng dự án như trên, tệp ô của layer trên chứa `"not a png"`: (25, 25) là `BLACK`, (75, 75) là `TEST_PALETTE.white`, và mảng `getPixels` bằng mảng của render khi layer trên không có tệp; đúng 1 mục `WARN` thẻ `ProjectRender`; thông điệp không chứa mã dự án, mã khung, mã layer, tên dự án |
| Đúng khung được chọn | Robolectric | `StoreFrameRendererTest.frameIndex_selectsThatFrame`: dự án 1:1 720p nền `Solid(WHITE)`, khung 0 có ô `Square(0, 0, 360, BLACK)`, khung 1 có ô `Square(360, 360, 360, BLACK)`: render khung 1 ra 100 × 100 thì (75, 75) là `BLACK`, (25, 25) là `TEST_PALETTE.white`; đối chứng: render khung 0 thì ngược lại |
| Camera của dự án áp vào ảnh ra | Robolectric | `StoreFrameRendererTest.cameraKey_appliedWhenEnabled`: khung 0 có `cameraKey (0.5, 0, 1, 0)`, ô `Square(0, 0, 144, BLACK)` trên khung 720 × 720, ảnh ra 100 × 100: `camera.enabled = true` thì (60, 10) là `BLACK`, (10, 10) là màu nền; `enabled = false` thì ngược lại |
| Thu nhỏ khi xuất 720p dự án 1080p | Robolectric | `StoreFrameRendererTest.export720pOf1080pProject_scalesCells`: dự án 16:9 1080p, ô 1920 × 1080 có `Square(0, 0, 192, BLACK)` (cao 192): render ở `RenderSize.forExport(project, P720)` ra 1280 × 720; (64, 64) là `BLACK`, (200, 200) là màu nền |
| Lớp vẽ theo, onion skin không có trong ảnh ra | Robolectric | `StoreFrameRendererTest.referenceAndOnionSkin_neverDrawn`: dự án có `reference` video `frameCount = 2` kèm tệp `reference/0.png`, `reference/1.png` toàn `RED`, onion skin bật, khung 0 có ô `Square(0, 0, 720, GREEN)`; render khung 1 (ô `Square(0, 0, 360, BLACK)`): mọi điểm ảnh bằng render của cùng dự án với `reference = null`, `onionSkin.enabled = false` (so mảng `getPixels`); (75, 75) là màu nền, không `RED`, không `GREEN` |
| Dùng lại bitmap cho mọi khung | Robolectric | `StoreFrameRendererTest.renderInto_clearsPreviousContent`: `renderInto(target)` khung có ô `Square(0, 0, 720, BLACK)`, rồi `renderInto(target)` khung không ô, `includeBackground = false`: (50, 50) alpha 0 |
| Màn vẽ và phần vẽ từ kho ra cùng ảnh | Robolectric | `StoreFrameRendererTest.composeInMemory_equalsRenderFromStore`: dự án 1:1 720p nền `Photo("png")` (ảnh ba sọc), camera bật, khung 0 có điểm `(0.1, 0, 1.5, 30)`, 3 layer (giữa ẩn) có ô như test thứ tự layer; `render` ra 100 × 100, và `FrameComposer.compose` trên canvas 100 × 100 với `FrameBackground.Photo` (ảnh giải mã bằng `BackgroundDecoder.decode(bytes, 100, 100)` từ cùng byte), danh sách ô giải mã từ cùng byte (layer ẩn bỏ ra, như màn vẽ làm), `cameraAt(project, 0)`: hai mảng `getPixels` bằng nhau |
| Ảnh nền lớn không làm tràn bộ nhớ | Robolectric | `BackgroundDecoderTest.largePhoto_isDownsampled_andCropsCenter`: PNG 4000 × 1000, sọc dọc `GREEN` (0 tới 1500), `RED` (1500 tới 2500), `BLUE_INK` (2500 tới 4000); `decode(bytes, 100, 100)` ra 500 × 125; vẽ phủ kín cắt giữa vào 100 × 100 thì (5, 50) và (94, 50) là `RED`; `decode("not a png")` là `null`; `sampleSize(100, 100, 100, 100) == 1`. Đối chứng ở `StoreFrameRendererTest.photoBackground_large_rendersCenter`: cùng ảnh làm `background.png` của dự án 1:1, render 100 × 100: (5, 50) và (94, 50) là `RED` |
| Ba màu nền có trong `tokens.json` | Unit test | `DesignTokensTest.projectBackgroundColors_listedInTokens`: `tokensJson()` có đúng dòng `"projectBackground": {"white": "#FFFFFF", "dark": "#22262E", "blue": "#2F6FE0"}` ở cấp trên cùng, lấy từ `ProjectBackgroundColors` (khóa cả tên nhóm `"projectBackground"` và vị trí, phần mà `ProjectBackgroundColorsTest` không kiểm); `tokensFile_matchesTheDesignSystem` có sẵn đỏ tới khi tệp được sinh lại |
| Bitmap đích sai, chỉ số sai | Robolectric | `StoreFrameRendererTest.invalidArguments_throw`: bitmap không sửa được (`Bitmap.createBitmap(...).copy(ARGB_8888, false)`), bitmap `RGB_565`, chỉ số khung -1 và `frames.size` đều ném `IllegalArgumentException` |
| Lỗi đọc tệp đi ra người gọi | Robolectric | `StoreFrameRendererTest.storeIoError_propagates`: kho giả trong test bọc `FileProjectStore`, `readFile` của ô ném `IOException("disk")`: `render` ném lỗi `is IOException` |
| Vẽ chạy trên dispatcher riêng | Robolectric | `StoreFrameRendererTest.render_runsOnRenderDispatcher`: kho dựng với `UnconfinedTestDispatcher(testScheduler)`, phần vẽ với `val render = StandardTestDispatcher()` (scheduler riêng); `val d = async { renderer.render(...) }; runCurrent(); assertFalse(d.isCompleted)`; rồi lặp tối đa 10 lần `render.scheduler.advanceUntilIdle(); advanceUntilIdle()` tới khi `d.isCompleted`; `assertTrue(d.isCompleted)` và ảnh ra đúng nền |
| KB-2: ảnh thu nhỏ là khung đầu trên nền | Robolectric | `ProjectThumbnailsTest.update_writesFirstFrameWithBackground`: dự án 16:9 720p nền `Solid(BLUE)`, khung 0 có ô `Square(0, 0, 640, BLACK)` (ô vuông 640 × 640 ở góc trên trái, thành 256 × 256 trong ảnh thu nhỏ), khung 1 toàn `RED`: `update` trả `WRITTEN`; `readFile(Thumbnail)` giải mã ra 512 × 288; (100, 144) là `BLACK`, (400, 144) là `TEST_PALETTE.blue`, không điểm đo nào `RED` |
| Ảnh thu nhỏ theo tỷ lệ | Robolectric | `ProjectThumbnailsTest.update_sizeFollowsAspectRatio`: dự án 9:16 ra 288 × 512, 1:1 ra 512 × 512 |
| KB-2: ảnh thu nhỏ đúng nội dung vừa vẽ | Robolectric | `ProjectThumbnailsTest.update_again_reflectsNewCells`: sau lần `update` đầu, ghi đè ô khung 0 thành toàn `GREEN`, `update` lần nữa: (400, 144) là `GREEN` |
| KB-2: dự án hỏng không có ảnh thu nhỏ mới | Robolectric | `ProjectThumbnailsTest.update_unreadableProject_writesNothing`: `project.json` ghi đè bằng `"{"`: trả `PROJECT_UNREADABLE`, không có tệp `thumbnail.png`; mã chưa có dự án cũng trả `PROJECT_UNREADABLE`; đối chứng: dự án đúng trả `WRITTEN` và có tệp |
| Lỗi ghi không làm dừng app | Robolectric | `ProjectThumbnailsTest.update_writeFails_returnsFailedAndLogs`: kho giả bọc `FileProjectStore`, `writeFile` ném `IOException`: trả `FAILED`, không ném lỗi, đúng 1 mục `WARN` thẻ `ProjectRender`, không chứa mã dự án |
| Module tuân luật phụ thuộc | Lệnh | `scripts/check.sh` chạy `checkModuleDependencies`: `:core:project` chỉ phụ thuộc `:core:logging`, `:core:designsystem` (và `:core:data` nếu T-002 đã thêm) |
| Bảng module của `CLAUDE.md` ghi phần vẽ | Kiểm tra thủ công (người: đọc tài liệu) | Người duyệt PR của task đọc dòng `:core:project` |
| Ảnh vẽ ra nhìn đúng trên máy thật (nét mịn, ảnh nền không vỡ) | Kiểm tra thủ công (người: xem ảnh thu nhỏ, file xuất khi T-009, T-043, T-044 lên staging) | Ticket này chưa có màn nào hiện ảnh; người kiểm ở ticket đầu tiên hiện ảnh thu nhỏ (T-009) |

**Ngoại lệ viết test trước:** `di/RenderModule.kt` (Hilt cấp hai đối tượng), dòng `implementation`, `testImplementation` của `build.gradle.kts` và dòng của `CLAUDE.md`: phần nối và cấu hình, không có hành vi riêng; build của `scripts/check.sh` kiểm chúng biên dịch được. Chưa module nào phụ thuộc `:core:project`, nên đồ thị Hilt của `:app` chưa dùng `RenderModule`; ticket đầu tiên nối module vào feature kiểm qua đồ thị đó.

Thứ tự commit: bước 3 commit các test, `RenderTestImages.kt`, `ProjectBackgroundColorsTest.kt`, phần sửa `DesignTokensTest.kt`, các dòng `testImplementation` cùng dòng `implementation(project(":core:designsystem"))` (khung rỗng dùng `ProjectBackgroundColors`) và khung rỗng tối thiểu để test biên dịch được (`ProjectBackgroundColors` có ba hằng bằng 0, các lớp, interface, hằng `CAMERA_ORIGIN`, thân hàm `TODO()`); test đỏ vì `NotImplementedError` hay sai màu. Bước 4 viết phần cài đặt, giá trị màu thật và sinh lại `docs/design-system/tokens.json`.

### 4.1. Chạy thử

- **Đã chạy thử:** 2026-11-22, worktree nháp tách từ `origin/main` 757bbad, không commit. Vì code của T-001 chưa có trên nhánh chính, nhánh nháp dựng module `:core:project` tối thiểu (các dòng `build.gradle.kts` của T-001 trừ plugin `kotlin.serialization` và `kotlinx-serialization-json`, vì code thử không dùng JSON, cộng các dòng của plan này: `template.android.library`, `template.hilt`, `implementation(project(":core:designsystem"))`, `:core:logging`, ba dòng `testImplementation`), `ProjectBackgroundColors` ARGB `Int` trong `:core:designsystem`, một bộ gộp thử gọi đúng các API của mục 3.1 (`Bitmap.createBitmap`, `Canvas.drawColor`, `drawBitmap` với vùng nguồn cắt giữa và `Paint(FILTER_BITMAP_FLAG)`, `Canvas.concat(Matrix)` với `postScale`, `postRotate`, `postTranslate` quanh tâm, `Bitmap.compress(PNG)`, `BitmapFactory.decodeByteArray`) và một test Robolectric `@GraphicsMode(NATIVE)` đọc `getPixel`: ảnh nền ba sọc cắt giữa vào khung vuông, lia nửa khung, phóng 2 rồi xoay 90 độ, ảnh không nền có alpha 0, nén rồi giải mã PNG giữ alpha 0, màu nền từ `ProjectBackgroundColors`.
- **Lệnh:** `scripts/check.sh` (sau `./gradlew :core:project:ktlintFormat :core:designsystem:ktlintFormat`). Trong phiên, sau `scripts/check.sh` chạy thêm `./gradlew :core:project:lintDebug :core:designsystem:lintDebug`, vì lint của `check.sh` chỉ chạy cho `:app` mà `:app` chưa phụ thuộc `:core:project`.
- **Kết quả:** Lần 1 đỏ: `:core:project:testDebugUnitTest` dừng ở bước dựng môi trường Robolectric, `RuntimeException: Failed to interact with raw FileDescriptor internals; perhaps JRE has changed?` (`ApplicationSharedMemory.create`), vì không có `robolectric.properties` nên Robolectric chạy SDK 36 theo targetSdk. Mọi module có Robolectric của dự án có `src/test/resources/robolectric.properties` `sdk=35`; thêm tệp đó (đã đưa vào mục 3.2) thì lần 2 qua: `== Cổng chất lượng: QUA`, 6 phút 47 giây, 2438 test JVM 0 lỗi, `:core:project` 1 test 0 lỗi, `checkModuleDependencies` qua với `:core:project -> :core:designsystem`. Mọi câu khẳng định điểm ảnh của test thử đúng giá trị tuyệt đối (`assertEquals` ARGB), kể cả sau `Paint(FILTER_BITMAP_FLAG)` và xoay 90 độ, tại điểm đo cách mép ít nhất 5 điểm ảnh; PNG giải mã giữ alpha 0. Không chạy `:app:assembleStagingRelease` vì không thêm thư viện và `:app` chưa phụ thuộc `:core:project`; `ProjectBackgroundColors` chỉ là hằng `const`. Worktree nháp đã xóa sau khi thử. Lần thử thứ hai (2026-11-22, sau rà soát độc lập, worktree nháp mới từ 757bbad): `BackgroundDecoder` thử gọi `BitmapFactory.decodeByteArray` với `inJustDecodeBounds` rồi `inSampleSize`, test NATIVE: PNG 4000 × 1000 vào khung 100 × 100 giải mã ra đúng 500 × 125, cắt giữa ra (5, 50) và (94, 50) là `RED`, byte hỏng ra `null`. `scripts/check.sh`: `== Cổng chất lượng: QUA`, `:core:project` 1 test 0 lỗi. Phần `FrameComposer.compose` công khai chỉ gói lại các lời gọi đã thử ở lần đầu, không cần thử riêng.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 7 (module core dùng chung: `:core:project` và `:core:designsystem`).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 3 | Hai module (`:core:project` thêm gói `render` có giao diện công khai, `:core:designsystem` thêm `ProjectBackgroundColors` và sửa `DesignTokensTest.kt`), cộng `docs/design-system/tokens.json` và `CLAUDE.md` |
| B. Độ rõ của yêu cầu | 2 | Spec rõ về gộp layer, nền, trong suốt, camera; còn câu hỏi ảnh hưởng tới cách làm: mã màu nền mâu thuẫn giữa spec và thiết kế (câu 1), ảnh nền có đi theo camera không (câu 2), nội suy theo thời gian hay chỉ số (câu 3) |
| C. Khả năng kiểm chứng tự động | 1 | Mọi hành vi có test điểm ảnh hay unit test; độ mịn của nét khi thu nhỏ chỉ người xem được, ở ticket hiện ảnh đầu tiên |
| D. Ảnh hưởng chéo | 2 | Sửa `core/project/build.gradle.kts` mà T-002 cũng sửa; T-009, T-011, T-019, T-037, T-043, T-044 và bài học dựa vào API này. Chưa có hành vi cũ nào bị đổi |
| E. Độ mới của kỹ thuật | 2 | Vẽ bitmap bằng `Canvas`, `Matrix` và test điểm ảnh Robolectric trong module core không Compose lần đầu có trong dự án; kỹ thuật phổ biến, đã chạy thử (mục 4.1) |
| **Tổng** | 10 | **Mức: Cao** (chạm điểm chặn 7, tổng điểm cũng từ 9) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `core/project/build.gradle.kts`, `CLAUDE.md` (dòng `:core:project`), `:core:designsystem` (đối tượng mới, không sửa lớp có sẵn), `DesignTokensTest.kt` và `docs/design-system/tokens.json` (thêm nhóm `"projectBackground"`). Người của nhóm thiết kế thêm ba biến màu không theo theme vào Figma sau khi merge; AI không sửa Figma.
- Task khác có thể xung đột:
  - T-001: phải chạy và merge trước; plan dùng `Project`, `CameraKey`, `BackgroundColor`, `canvasSize`, `ProjectStore`, `ProjectFile`, `FileProjectStore` đúng như mục 3.1 của T-001. Tên khác đi thì phiên dùng tên thật của T-001 (lệch L5), không đổi hành vi.
  - T-002: cũng sửa `core/project/build.gradle.kts` (thêm `:core:data`). Hai plan chỉ thêm dòng vào khối `dependencies`; chạy sau T-002 thì giữ dòng của T-002. Plan không sửa `ProjectModule.kt` (Hilt module riêng `RenderModule.kt`) để tránh cùng sửa.
  - T-011 (màn Tạo dự án): ô màu nền dùng `ProjectBackgroundColors` thay vì vai trò màu theo theme như ảnh thiết kế, để ô màu khớp màu của file xuất (câu hỏi 1). T-011 cũng gọi `ProjectThumbnails.update` sau khi tạo dự án. Hợp đồng ảnh nền: T-011 lưu `background.<đuôi>` đã xoay đúng theo EXIF (phần vẽ không đọc EXIF) và đã thu nhỏ để cạnh dài khoảng 2 lần cạnh dài lớn nhất của khung vẽ (1920 × 2 = 3840), không thêm thư viện.
  - T-019, T-020 (màn vẽ, tự lưu): gọi `update` khi rời màn vẽ; màn vẽ hiện khung bằng `FrameComposer.compose` với ảnh ô đã có trong bộ nhớ, `cameraAt` và `BackgroundPalette.Default`; ảnh nền "Photo" giải mã bằng `BackgroundDecoder.decode` theo cỡ canvas, không tự giải mã. `DesignSystemCatalog` không hiện nhóm `projectBackground` (nhóm không theo theme). Nền dự án trên màn vẽ luôn lấy từ `BackgroundPalette`/`ProjectBackgroundColors`, không bao giờ từ `AppColors.canvas`, vì `canvas` đổi theo theme còn file xuất thì không. Onion skin và lớp vẽ theo do màn vẽ tự vẽ thêm.
  - T-037 (camera ảo): đổi cử chỉ sang `CameraKey` theo nghĩa ở mục 3.1 (cảnh dịch chuyển trên khung, xoay dương theo chiều kim đồng hồ). Đổi luật nội suy hay thứ tự biến đổi là sửa phần vẽ của module core, rủi ro cao như ticket T-037 đã ghi. Vì có nội suy, xóa một điểm đặt ở giữa không đưa khung đó về gốc (khung nhận giá trị nội suy từ hai điểm bên cạnh). "Reset camera" của một khung (spec editor KB-15) phải ghi điểm đặt `CAMERA_ORIGIN` cho khung đó, không xóa điểm; "xóa điểm đặt thì về gốc" chỉ đúng khi không còn điểm nào, hay khung đứng trước điểm đầu.
  - T-043, T-044 (xuất): dùng `renderInto` một bitmap cho mọi khung, lặp theo `hold`, vẽ watermark lên ảnh trả ra. Mỗi khung giải mã lại ảnh nền "Photo"; nếu xuất chậm vì vậy, T-043 thêm bộ nhớ đệm ảnh nền trong phần vẽ (sửa `StoreFrameRenderer.kt`).
  - T-009: đọc `thumbnail.png` bằng `ProjectStore.readFile(id, ProjectFile.Thumbnail)`; chưa có tệp thì hiện ảnh trống. Dự án mà `read` không trả `Ok` (hỏng, bản mới hơn) thì luôn hiện ảnh trống, kể cả khi `thumbnail.png` cũ vẫn còn (spec projects KB-2).
- Đề xuất thứ tự chạy: sau T-001 (bắt buộc), sau T-002 nếu cùng danh sách (cùng sửa `build.gradle.kts`); trước T-009, T-011, T-019, T-043, T-044.

## 7. Điều kiện dừng riêng

- Cần sửa file Kotlin ngoài thư mục test của T-001 (mô hình, `ProjectStore`, `FileProjectStore`, `ProjectModule.kt`) để phần vẽ chạy: dừng, vì plan dựa trên API của T-001 như đã duyệt.
- Cần áp plugin Compose hay thêm thư viện Compose cho `:core:project`: dừng, vì plan chọn mã ARGB `Int` để module không cần Compose.
- Cần đổi lớp `AppColors`, hay đổi phần nào của `docs/design-system/tokens.json` ngoài nhóm `"projectBackground"` mới: dừng.

## 8. Câu hỏi cho người duyệt

1. **Mã màu ba nền "trắng, xám đậm, xanh" mâu thuẫn giữa spec và thiết kế.** Spec projects KB-3 ghi "xám đậm"; ảnh thiết kế `NewProjectScreen_default_light` tô ô thứ hai bằng `#E3E6EA` (xám nhạt, vai trò `surfaceVariant`), bản tối tô `#22262E` (xám đậm). Ô xanh là `#2F6FE0` (sáng) và `#6EA8FF` (tối), vai trò `secondary`. Ô trắng là `#FFFFFF` (sáng) và `#FAFAF7` (tối), vai trò `canvas` (`NewProjectScreen_default_dark.md`, dòng 126); `AppColors.canvas` đổi theo theme, nên nếu màn vẽ tô nền bằng `canvas` thì màn vẽ ở chế độ tối hiện `#FAFAF7` còn file xuất ghi `#FFFFFF`. Cả ba ô gắn vai trò màu theo theme, nhưng nền là một phần của file xuất nên không được đổi theo chế độ sáng, tối của máy. Đề xuất của AI: ba màu cố định, không theo theme, đặt ở `ProjectBackgroundColors` của `:core:designsystem`: trắng `#FFFFFF`, xám đậm `#22262E`, xanh `#2F6FE0`; T-011 dùng đúng ba màu này cho ô chọn, T-019 dùng cho nền màn vẽ. Như vậy lệch ảnh thiết kế ở ô xám bản sáng (`#E3E6EA` thành `#22262E`, từ xám nhạt thành xám đậm), ô xanh bản tối và ô trắng bản tối. Ba màu vào `tokens.json` thành nhóm `"projectBackground"`; người thêm biến vào Figma. Anh chọn mã màu nào? Riêng màu trắng xuất ra là `#FFFFFF` cố định, hay theo `canvas` của theme (khi đó file xuất phụ thuộc theme của máy, AI không đề xuất)?
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Ba màu cố định `#FFFFFF`, `#22262E`, `#2F6FE0`, theo đề xuất của AI; ghi vào ticket T-011.
2. **Ảnh nền "Photo" có đi theo camera ảo không?** Spec editor KB-15 ghi camera "lia, phóng, xoay cả cảnh"; spec projects câu 3 ghi ảnh là "nền cố định của mọi khung". Đề xuất của AI: ảnh nền đi cùng cảnh (cả cảnh di chuyển như máy quay thật); chỗ hở khi thu nhỏ hay xoay hiện màu trắng của bảng màu. Nền màu tô kín nên không thấy khác. Phương án khác: ảnh nền đứng yên, camera chỉ di chuyển nét vẽ.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Ảnh nền đi cùng cảnh, theo đề xuất của AI.
3. **Nội suy camera theo mốc thời gian hay theo chỉ số khung?** Khi có khung giữ 2× tới 4×, hai cách cho kết quả khác nhau. Đề xuất của AI: theo mốc bắt đầu của khung (tổng `hold` phía trước), để cảnh chạy đều khi phát. Luật trước điểm đầu là gốc, sau điểm cuối giữ điểm cuối giữ nguyên như anh đã chốt. Anh mới chốt "nội suy giữa hai điểm đặt gần nhất", chưa chốt trọng số; test `CameraTimelineTest.betweenKeys_interpolatesByFrameStartTime` cần câu trả lời ở mục 9 trước khi phiên chạy (theo chỉ số thì khung 2 của test ra `(0.2, -0.1, 1.5, 45)`). Lớp vẽ theo đã chốt theo chỉ số khung, nhưng lớp đó không xuất ra nên không ràng buộc câu này. Hệ quả cho T-037: vì có nội suy, "Reset camera" của một khung ghi điểm gốc chứ không xóa điểm (mục 6). Mâu thuẫn cần anh xác nhận: spec editor KB-15 ghi "xóa điểm đặt thì cảnh về vị trí gốc"; khi có nội suy, xóa một điểm đặt ở giữa cho khung đó giá trị nội suy từ các điểm còn lại, không về gốc. Đề xuất của AI: hiểu KB-15 là "Reset camera" ghi điểm gốc (`CAMERA_ORIGIN`) cho khung đó, còn xóa điểm đặt chỉ về gốc khi không còn điểm nào; ghi cách hiểu này vào ticket T-037.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Theo thời gian; đồng ý cách hiểu KB-15, ghi vào ticket T-037.
4. **Kích thước ảnh thu nhỏ.** Đề xuất của AI: vừa trong 512 × 512 theo tỷ lệ dự án (16:9 là 512 × 288), đủ nét cho khung ảnh trên thẻ 168 × 94 dp ở xxhdpi; mỗi tệp khoảng vài chục KB.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Vừa trong 512 × 512, theo đề xuất của AI.
5. **Duyệt chéo khi nhóm một dev.** Plan rủi ro Cao cần duyệt chéo; nếu nhóm chỉ có anh, mục 9 ghi anh vừa duyệt vừa duyệt chéo kèm lý do (risk-scoring.md, mục 3).
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Anh Nhật vừa duyệt vừa duyệt chéo, vì nhóm hiện chỉ có một dev (trả lời ở plan T-001, áp dụng cho đợt).

## 9. Quyết định duyệt

<!-- Người duyệt điền, hoặc AI ghi theo lời đồng ý trong phiên của người duyệt, nguyên văn (ADR-0048, phần C). -->

- **Quyết định:** Duyệt
- **Người duyệt:** Nhật, **duyệt chéo:** Nhật (nhóm chỉ có một dev, theo risk-scoring.md mục 3; khi có dev thứ hai thì quy tắc duyệt chéo áp dụng lại)
- **Ghi chú:** Duyệt cả đợt 20261122-01 ngày 2026-11-22, trong phiên, lời đồng ý nguyên văn: "Duyệt đợt 20261122-01: plan T-004, T-005, T-006 và danh sách RUN-20261122-01, bắt đầu phiên khi merge. Tôi vừa duyệt vừa duyệt chéo vì nhóm chỉ có một dev." Câu trả lời cho mục 8 ghi ở từng câu hỏi.

## 10. Nhật ký thực hiện

<!-- AI ghi khi task đã có kết quả cuối (đã merge hoặc đã dừng), trên nhánh báo cáo của phiên (auto-run.md, bước 10). Mục mới thêm vào cuối, không sửa mục cũ. -->
