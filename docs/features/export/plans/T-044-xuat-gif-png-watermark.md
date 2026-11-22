---
ticket: T-044
title: Làm ra file GIF, chuỗi PNG, GIF trong suốt và watermark
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

# PLAN T-044: LÀM RA FILE GIF, CHUỖI PNG, GIF TRONG SUỐT VÀ WATERMARK

## 1. Tóm tắt

Tạo module `:feature:export` với phần làm ra file ảnh: bộ mã hóa GIF tự viết (bảng màu theo từng khung, nén LZW, lặp vô hạn, trong suốt một bit), chuỗi PNG trong suốt, và watermark Frameloop (chữ trắng trên viên thuốc nền đen 35%) vẽ lên từng khung mà `FrameRenderer` của T-004 trả ra. Không thêm thư viện. Kiểm chứng bằng cách giải mã ngược file làm ra bằng bộ đọc GIF của JDK (`javax.imageio`) và bằng `BitmapFactory` của Robolectric `NATIVE`: số khung, thời gian mỗi khung, vòng lặp, điểm trong suốt, màu điểm ảnh của watermark.

## 2. Hiểu yêu cầu

Ticket chỉ làm phần làm ra file. Màn chọn định dạng là T-042, MP4 là T-043 (dùng lại watermark và lịch thời gian của ticket này), màn đang xuất, hủy, lỗi là T-045, lưu vào thư viện là T-046.

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-4 | Khi xuất GIF thì file lặp vô hạn, không âm thanh | GIF89a có khối `NETSCAPE2.0` số lần lặp 0. GIF không có âm thanh |
| KB-4 | Khi xuất chuỗi PNG thì có đúng một ảnh cho mỗi khung của dự án | Một tệp PNG cho mỗi phần tử của `project.frames`, kể cả khung giữ 2× tới 4× (vẫn một ảnh, câu hỏi 3) |
| KB-4 | Khi watermark bật thì mọi khung của file xuất ra có watermark Frameloop (chữ "Frameloop" kèm biểu tượng app, màu trắng trên viên thuốc nền đen mờ, góc dưới phải, cao khoảng 5% cạnh ngắn, cách mép 3%); khi tắt thì không khung nào có | `WatermarkPainter` vẽ lên từng khung trước khi mã hóa. Số đo theo `design/README.md`: viên thuốc cao `round(0.05 × cạnh ngắn)` (54 điểm ảnh ở 1080p, 36 ở 720p), cách mép phải và mép dưới `round(0.03 × cạnh ngắn)` (32 và 22), bo tròn hai đầu; biểu tượng `brand_mark` và chữ Inter Semi Bold cao bằng nửa viên thuốc, lề hai bên và khoảng cách bằng một phần tư viên thuốc (frame `PreviewWatermark` của `context/ExportScreen_options_dark.md`, dòng 86 tới 89: viên 14 × 4, lề 1, cách 1, biểu tượng 2, cỡ chữ 2) |
| KB-4 | Khi xuất thì đúng tốc độ khung của dự án, ở độ phân giải đã chọn | Kích thước khung ra là `RenderSize.forExport(project, resolution)` của T-004. Thời gian mỗi khung GIF tính từ `hold` và tốc độ khung (mục 3.1, `ExportTimeline`), làm tròn theo mốc cộng dồn để tổng thời lượng đúng |
| KB-4 | Khi xuất, dù thành công, lỗi hay hủy, thì dự án không thay đổi | Phần xuất chỉ đọc qua `FrameRenderer`, không nhận `ProjectStore`, nên không có đường ghi nào vào dự án |
| KB-4 | Khi bấm "Cancel" thì không để lại file dở (cả KB-6, KB-7: lỗi, rời app) | Hủy coroutine hay lỗi giữa chừng thì phần xuất xóa mọi tệp nó đã tạo trong thư mục làm việc rồi ném lại lỗi. Nút "Cancel", trạng thái lỗi và rời app là T-045 |
| KB-4 (hỗ trợ) | Phần trăm bằng số khung đã làm chia tổng số khung | Phần xuất báo `onFrameDone(done, total)` sau mỗi khung, `total = project.frames.size`. Cách hiện là T-045 |
| Mục 2, câu hỏi 1 | Chuỗi PNG luôn trong suốt, GIF trong suốt bỏ nền | PNG và GIF trong suốt vẽ khung với `includeBackground = false`; GIF thường và MP4 có nền |

Thiết kế tham chiếu: `features/export/design/README.md`, đoạn mẫu watermark (dòng 27) và frame `PreviewWatermark` trong `design/context/ExportScreen_options_dark.md` (dòng 86 tới 89). Không có ảnh thiết kế riêng cho watermark ("không có screenshot test riêng cho mẫu watermark"). Ảnh thiết kế của ticket khác (T-042, T-045, T-046): mọi ảnh `ExportScreen_*` trong `design/screens/`.

ADR liên quan: ADR-0001 (feature chỉ phụ thuộc core; `:feature:export` phụ thuộc `:core:project`), ADR-0008 (màu watermark lấy từ `:core:designsystem`), ADR-0019 (log không có tên dự án, mã dự án, nội dung tệp).

## 3. Giải pháp

### 3.1. Cách làm

Plan dựa trên mô hình của T-001 (`Project`, `Frame.hold`, `FrameRate`, `Resolution`) và phần vẽ của T-004 (`FrameRenderer.renderInto`, `RenderSize.forExport`), cả hai đã duyệt và chạy trước trong RUN-20261122-01. Plan không sửa file Kotlin nào của hai plan đó. Tên thật trên nhánh chính khác tên trong plan của chúng thì phiên dùng tên thật (lệch L5), không đổi hành vi.

**Chọn module.** Phần làm ra file đặt trong `:feature:export`, module mà T-042, T-043, T-045, T-046 cùng dùng. Chỉ màn Xuất cần mã hóa file: màn Hoàn thành bài học mở màn Xuất (T-053), không tự xuất. Nên không cần một module core mới (ADR-0001 chỉ yêu cầu module core khi nhiều feature dùng chung). T-044 không phụ thuộc T-042, nên ticket này tạo module trước: `feature/export/build.gradle.kts` theo mẫu `:feature:sample` (plugin `template.android.feature`, `namespace = "com.vgl.draw.animation.feature.export"`, thêm `implementation(project(":core:project"))`), một dòng `include(":feature:export")` ở `settings.gradle.kts` ngay sau `:feature:loading`, `consumer-rules.pro` rỗng như `:feature:sample`, và `src/test/resources/robolectric.properties` một dòng `sdk=35` như mọi module có Robolectric. `:app` chưa phụ thuộc module này; T-042 thêm phụ thuộc khi đăng ký màn Xuất (mục 6). Phương án khác ở câu hỏi 1. Màu watermark là phần duy nhất đặt ở module core, vì ADR-0008 và quy ước code không cho viết cứng mã màu trong feature: mọi màu lấy từ `:core:designsystem`, như `ProjectBackgroundColors` của T-004. Hai nơi dùng màu này (file xuất và ảnh xem trước của T-042) đều ở `:feature:export`; màu không đổi theo chế độ sáng tối vì là một phần của file xuất.

**Màu watermark** (`:core:designsystem`, tệp mới `theme/WatermarkColors.kt`, theo mẫu `ProjectBackgroundColors` của T-004): `object WatermarkColors { const val CONTENT: Int = 0xFFFFFFFF.toInt(); const val PILL: Int = 0x59000000 }`, mã ARGB dạng `Int` (0x59 = 89 = 35% của 255, làm tròn xuống). KDoc ghi: watermark là một phần của file xuất, không đổi theo theme; ảnh xem trước của màn Xuất (T-042) dùng đúng hai hằng này (`Color(WatermarkColors.PILL)`). Thêm vào `docs/design-system/tokens.json` một nhóm cấp trên cùng, đặt ở CUỐI đối tượng cấp trên cùng, sau nhóm có sẵn cuối cùng (không chen giữa nhóm `"projectBackground"` của T-004 và các nhóm khác, để không đổi tên hay vị trí mà `projectBackgroundColors_listedInTokens` của T-004 khóa): `"watermark": {"content": "#FFFFFF", "pill": "#000000", "pillOpacity": 0.35}`. `tokensJson()` trong `DesignTokensTest.kt` sinh thêm dòng này từ `WatermarkColors` (màu bỏ kênh alpha như hàm `hex` đang làm; độ mờ tính bằng số nguyên, không phụ thuộc locale: `val pct = (alpha * 100 + 127) / 255` ra 35, ghi `String.format(Locale.ROOT, "0.%02d", pct)` khi `pct < 100`, tức `0.35`; không dùng `String.format("%.2f")` theo locale máy, vì locale tiếng Việt ghi dấu phẩy), rồi sinh lại tệp bằng `./gradlew :core:designsystem:testDebugUnitTest -PdesignTokens.update=true`. `scripts/figma-export.py` chỉ đọc `color`, `typography`, `spacingDp` nên bỏ qua nhóm mới.

**Định dạng và tùy chọn** (`model/ExportFormat.kt`, `model/ExportOptions.kt`, công khai trong module, để T-042, T-045 dùng lại):

```kotlin
enum class ExportFormat(val includesBackground: Boolean) {
    MP4(includesBackground = true),
    GIF(includesBackground = true),
    PNG_SEQUENCE(includesBackground = false),
    TRANSPARENT_GIF(includesBackground = false),
}

data class ExportOptions(val format: ExportFormat, val resolution: Resolution, val watermark: Boolean)

/** Files written into the work directory, in frame order, and their total size. */
data class ExportedFiles(val files: List<File>, val totalBytes: Long)
```

**Lịch thời gian** (`encode/ExportTimeline.kt`, Kotlin thuần): `class ExportTimeline(holds: List<Int>, fps: Int)`, `fps` là số khung mỗi giây của `project.frameRate` (thuộc tính số do T-001 đặt trong `CanvasFormat.kt`). Mốc bắt đầu của khung `k` tính theo đơn vị khung: `u(k)` = tổng `hold` của các khung trước `k`; `u(n)` là tổng thời lượng.

- `gifDelaysCentiseconds(): IntArray`: thời gian khung `k` = `cs(u(k + 1)) - cs(u(k))` với `cs(u) = (200 × u + fps) / (2 × fps)` (phần trăm giây, làm tròn nửa lên, số nguyên). Làm tròn theo mốc cộng dồn nên lỗi không dồn lại: 48 khung ở 12 khung/giây cộng đúng 400 (4,0 giây). Khung giữ 3× là một khung GIF có thời gian gấp ba, không lặp ảnh. Với tốc độ tối đa 30 khung/giây, mỗi khung ít nhất 3 phần trăm giây, trên ngưỡng 2 mà trình xem GIF đổi thành 10.
- `frameStartMicros(k): Long = u(k) × 1_000_000 / fps` và `durationMicros: Long`, cho MP4 của T-043.
- `require(fps > 0)`, mọi `hold` từ 1 tới 4, danh sách không rỗng.

**Bảng màu** (`encode/ColorQuantizer.kt`, Kotlin thuần, `internal`): `fun quantize(argb: IntArray, transparent: Boolean): QuantizedFrame(palette: IntArray /* 256 RGB */, indices: ByteArray)`. Điểm ảnh vào là ARGB chưa nhân alpha (`Bitmap.getPixels`).

1. Chế độ trong suốt: điểm có alpha dưới 128 dùng chỉ số 255 (`TRANSPARENT_INDEX`), còn lại coi là đặc với màu RGB của nó. Chế độ thường bỏ qua alpha (khung có nền luôn đặc).
2. Đếm màu khác nhau, dừng khi vượt giới hạn (256 màu ở chế độ thường, 255 ở chế độ trong suốt). Không vượt thì bảng màu là đúng các màu đó, không mất màu nào. Tranh vẽ nét đặc thường rơi vào trường hợp này.
3. Vượt thì cắt trung vị (median cut) trên biểu đồ 5-5-5 bit (32 768 ô; mỗi ô giữ số điểm và tổng ba kênh 8 bit): lặp tách hộp có kênh trải rộng nhất tại trung vị có trọng số, tới khi đủ số hộp hay không hộp nào tách được; màu của hộp là trung bình có trọng số của màu thật; mỗi ô biểu đồ trỏ về hộp chứa nó. Không dùng dithering: vùng màu phẳng của tranh vẽ giữ phẳng, file nhỏ hơn.

**Nén LZW** (`encode/LzwEncoder.kt`, `internal object`), LZW biến thể GIF với `minCodeSize = 8`: mã xóa `clear = 256`, mã kết thúc `eoi = 257`, `codeSize` bắt đầu 9, mã kế tiếp `nextCode` bắt đầu 258. Các bước:

1. Phát mã xóa 256 trước tiên (9 bit). `prefix` = chỉ số đầu tiên.
2. Với mỗi chỉ số `c` tiếp theo: khóa `(prefix shl 8) or c` có trong bảng thì `prefix` = mã của khóa. Không có thì phát `prefix`; rồi nếu `nextCode < 4096` thì thêm khóa với mã `nextCode`, tăng `nextCode`, và nếu `nextCode == (1 shl codeSize) + 1` (tức mã kế tiếp vừa vượt `1 shl codeSize`, như bộ giải mã chậm một mã) và `codeSize < 12` thì `codeSize++`; nếu `nextCode` đã là 4096 thì phát mã xóa bằng 12 bit, xóa bảng, đặt lại `codeSize = 9`, `nextCode = 258`. Sau đó `prefix = c`.
3. Hết dữ liệu: phát `prefix` hiện tại; nếu `nextCode == (1 shl codeSize)` và `codeSize < 12` thì `codeSize++` (bộ giải mã thêm một mục khi đọc mã cuối nên đã chuyển độ rộng); rồi phát mã kết thúc 257, đẩy nốt byte còn dở (bit thừa là 0).
4. Bit ghi từ thấp lên cao; luồng byte cắt thành khối con tối đa 255 byte (mỗi khối có byte độ dài đứng trước), kết thúc bằng khối 0.

Bảng băm địa chỉ mở 5 003 ô, khóa `(prefix shl 8) or index`. Điều kiện tăng `codeSize` ở bước 2 là đúng điều kiện bộ mã hóa thử đã dùng và qua bộ đọc của JDK lẫn Skia (mục 4.1); test `noiseFrame_exactPalette_everyPixelRoundTrips` khóa nó, vì sai thời điểm tăng một bit làm hỏng ảnh ngay sau mã 511.

**Bộ mã hóa GIF** (`encode/GifEncoder.kt`, Kotlin thuần, công khai trong module; người gọi mở và đóng luồng ra):

```kotlin
class GifEncoder(out: OutputStream, width: Int, height: Int, transparent: Boolean) {
    fun addFrame(argb: IntArray, delayCentiseconds: Int)
    fun finish()
}
```

Hàm dựng ghi `GIF89a`, mô tả màn hình (không có bảng màu chung) và khối lặp `NETSCAPE2.0` với số lần lặp 0. `addFrame`: `require(argb.size == width * height)`, `require(delayCentiseconds in 0..65535)`; khối điều khiển đồ họa (cách bỏ khung: 2 "trả về nền" khi trong suốt, để khung sau không lộ khung trước; 1 "giữ nguyên" khi đặc; cờ trong suốt và chỉ số 255 khi trong suốt; thời gian), mô tả ảnh cả khung `0, 0, width, height` có bảng màu riêng 256 màu, rồi dữ liệu LZW với `minCodeSize = 8`. `finish` ghi `0x3B`. Mỗi khung một bảng màu riêng vì khung của hoạt hình đổi màu theo nét vẽ.

**Watermark** (`encode/WatermarkPainter.kt`):

```kotlin
class WatermarkPainter(
    val text: String,
    typeface: Typeface,
    icon: Drawable,
    contentArgb: Int = WatermarkColors.CONTENT,
    pillArgb: Int = WatermarkColors.PILL,
) {
    /** Bounds of the pill on a frame of [width] x [height]. */
    fun pillBounds(width: Int, height: Int): RectF
    /** Draws the watermark; [opaquePill] draws the pill without transparency, for 1-bit transparent GIF. */
    fun draw(canvas: Canvas, width: Int, height: Int, opaquePill: Boolean = false)

    companion object {
        fun create(context: Context): WatermarkPainter
    }
}
```

`h = round(0.05 × min(width, height))`, `m = round(0.03 × min(width, height))`; chữ cỡ `h / 2`, `Paint(ANTI_ALIAS_FLAG)`; bề rộng viên thuốc `h / 4 + h / 2 + h / 4 + measureText(text) + h / 4`; viên thuốc có mép phải `width - m`, mép dưới `height - m`, bán kính bo `h / 2`. Biểu tượng `h / 2 × h / 2` cách mép trái viên thuốc `h / 4`, giữa theo chiều dọc, tô `contentArgb` (`mutate().setTint`); chữ bắt đầu sau biểu tượng `h / 4`, đường cơ sở `centerY - (ascent + descent) / 2`. `opaquePill = true` vẽ viên thuốc bằng `pillArgb or 0xFF000000.toInt()` (đen đặc), vì GIF chỉ có trong suốt một bit: viên thuốc alpha 89 sẽ thành trong suốt hết ở ngưỡng 128 (câu hỏi 2). `create(context)`: chữ `context.getString(R.string.export_watermark_brand)` ("Frameloop", `translatable="false"` vì là tên thương hiệu), phông `context.resources.getFont(<R của designsystem>.font.inter_semibold)` (API 26, bằng minSdk), biểu tượng `context.getDrawable(<R của designsystem>.drawable.brand_mark)`: cùng ký hiệu "animation" mà `brand_mark.xml` ghi là ký hiệu của watermark. Không vẽ cứng mã màu hay chữ.

**Phần xuất** (`encode/GifPngExporter.kt`): interface công khai và cài đặt `internal`:

```kotlin
interface GifPngExporter {
    /**
     * Writes [options.format] (GIF, PNG_SEQUENCE or TRANSPARENT_GIF) of [project] into [workDir].
     * [onFrameDone] is called after each frame with (done, total), on the encoding thread.
     * Cancellation or an error deletes every file written by this call, then rethrows.
     */
    suspend fun export(project: Project, options: ExportOptions, workDir: File, onFrameDone: (Int, Int) -> Unit): ExportedFiles
}
```

`DefaultGifPngExporter(renderer: FrameRenderer, watermark: WatermarkPainter, encodeDispatcher: CoroutineDispatcher, logger: AppLogger)`:

1. `require(options.format != MP4)` (MP4 là T-043), trước khi vẽ gì.
2. Chạy trong `withContext(encodeDispatcher)` (Hilt truyền `Dispatchers.Default`). Hợp đồng với người gọi: mỗi lần xuất truyền một thư mục làm việc mới hay rỗng (T-045, mục 6). `size = RenderSize.forExport(project, options.resolution)`; `workDir.mkdirs()`; một `Bitmap` ARGB_8888 đổi được dùng cho mọi khung, một `IntArray` điểm ảnh cho GIF.
3. Với mỗi khung `k` theo thứ tự: `ensureActive()`; `renderer.renderInto(bitmap, project, k, options.format.includesBackground)`; watermark bật thì `watermark.draw(Canvas(bitmap), w, h, opaquePill = format == TRANSPARENT_GIF)`; rồi:
   - GIF, GIF trong suốt: `bitmap.getPixels(...)`, `encoder.addFrame(pixels, delays[k])` vào một tệp `animation.gif` (luồng ghi có đệm 64 KB, mở bằng `use {}` để luồng luôn đóng trước khi tệp bị xóa khi hủy hay lỗi), `delays` từ `ExportTimeline`.
   - Chuỗi PNG: `bitmap.compress(PNG, 100, ...)` vào tệp `pngFileName(k, n)`: số thứ tự từ 1, đệm số 0 tới `max(3, số chữ số của n)` chữ số (`001.png`; 1 000 khung thì `0001.png`). Tên cuối "<tên dự án>_001.png" do T-046 đặt khi lưu vào thư viện.
   - `onFrameDone(k + 1, n)`.
4. Xong: `logger.info("Export", "Export finished: format=<tên enum>, frames=<n>, size=<w>x<h>, bytes=<tổng>")`, trả `ExportedFiles` theo thứ tự khung.
5. Bắt `Throwable` (gồm `CancellationException`, `IOException` khi hết chỗ, cả `OutOfMemoryError`), sau khi luồng ghi đã đóng: xóa mọi tệp đã tạo trong lần gọi này, log (`info("Export", "Export cancelled")` khi hủy, `warn("Export", "Export failed", e)` khi lỗi), rồi ném lại nguyên lỗi để T-045 phân loại (thiếu bộ nhớ hay lỗi khác). `finally` gọi `bitmap.recycle()`.

Hủy có hiệu lực ở đầu khung kế tiếp; khung đang mã hóa dở chạy nốt (dưới 1 giây ở 1080p theo số đo mục 4.1, trên máy thật chậm hơn). Log không có tên dự án, mã dự án, đường dẫn hay nội dung tệp.

**An toàn luồng của `WatermarkPainter`:** `WatermarkPainter` là singleton nhưng không giữ trạng thái đổi được giữa hai lần vẽ: chỉ giữ `text`, `Typeface`, hai màu và `Drawable` gốc. Mỗi lần `draw` (và `pillBounds`) tạo `Paint` mới, và `icon.constantState!!.newDrawable().mutate()` mới rồi mới `setBounds`, `setTint`, nên ảnh xem trước của T-042 (luồng chính) và phần xuất (`Dispatchers.Default`) vẽ cùng lúc được. Test `WatermarkPainterTest.draw_twoSizesInterleaved_independent`: vẽ ở 1080 × 1920 rồi 720 × 1280 rồi lại 1080 × 1920 bằng cùng một painter, `pillPoint` của lần ba bằng lần một (biểu tượng không giữ bounds của lần trước).

**Hilt** (`di/ExportEncodingModule.kt`): `@Provides @Singleton WatermarkPainter = WatermarkPainter.create(context)` với `@ApplicationContext`; `@Provides GifPngExporter = DefaultGifPngExporter(renderer, watermark, Dispatchers.Default, logger)`. `FrameRenderer` lấy từ `RenderModule` của T-004.

Mẫu có sẵn được làm theo: `build.gradle.kts` của `:feature:sample`; hằng màu ARGB `Int` không theo theme và nhóm tokens riêng của `ProjectBackgroundColors` (T-004); `withContext(dispatcher)` và log của `StoreFrameRenderer` (T-004); test điểm ảnh Robolectric `NATIVE` đọc thẳng `Bitmap.getPixel` (T-004).

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `settings.gradle.kts` | Thêm `include(":feature:export")` sau `include(":feature:loading")` |
| Tạo | `feature/export/build.gradle.kts` | Plugin `template.android.feature`, `namespace`, `implementation(project(":core:project"))` |
| Tạo | `feature/export/consumer-rules.pro` | Tệp rỗng như `feature/sample/consumer-rules.pro`: convention `template.android.library` khai `consumerProguardFiles("consumer-rules.pro")`, thiếu tệp thì `:feature:export:lintDebug` và mọi bản build có module dừng ở `mergeDebugConsumerProguardFiles` (mục 4.1). Không cần luật giữ lớp: không dùng phản chiếu hay tuần tự hóa |
| Tạo | `feature/export/src/main/res/values/strings.xml` | `export_watermark_brand` = "Frameloop", `translatable="false"` |
| Tạo | `feature/export/src/main/kotlin/com/vgl/draw/animation/feature/export/model/ExportFormat.kt` | Enum bốn định dạng, `includesBackground` |
| Tạo | `feature/export/src/main/kotlin/com/vgl/draw/animation/feature/export/model/ExportOptions.kt` | `ExportOptions`, `ExportedFiles` |
| Tạo | `feature/export/src/main/kotlin/com/vgl/draw/animation/feature/export/encode/ExportTimeline.kt` | Thời gian khung GIF theo `hold` và tốc độ khung, mốc micro giây cho MP4 |
| Tạo | `feature/export/src/main/kotlin/com/vgl/draw/animation/feature/export/encode/ColorQuantizer.kt` | Bảng màu đúng khi ít màu, cắt trung vị khi nhiều màu, ngưỡng trong suốt 128 |
| Tạo | `feature/export/src/main/kotlin/com/vgl/draw/animation/feature/export/encode/LzwEncoder.kt` | Nén LZW biến thể GIF, khối con 255 byte |
| Tạo | `feature/export/src/main/kotlin/com/vgl/draw/animation/feature/export/encode/GifEncoder.kt` | GIF89a, lặp vô hạn, bảng màu riêng mỗi khung, trong suốt một bit |
| Tạo | `feature/export/src/main/kotlin/com/vgl/draw/animation/feature/export/encode/WatermarkPainter.kt` | Vẽ viên thuốc, biểu tượng, chữ theo tỷ lệ cạnh ngắn; `create(context)` |
| Tạo | `feature/export/src/main/kotlin/com/vgl/draw/animation/feature/export/encode/GifPngExporter.kt` | Interface, `DefaultGifPngExporter`, `pngFileName` |
| Tạo | `feature/export/src/main/kotlin/com/vgl/draw/animation/feature/export/di/ExportEncodingModule.kt` | Hilt cấp `WatermarkPainter`, `GifPngExporter` |
| Tạo | `feature/export/src/test/resources/robolectric.properties` | Một dòng `sdk=35`, như mọi module có Robolectric |
| Tạo | `feature/export/src/test/kotlin/com/vgl/draw/animation/feature/export/encode/ExportTimelineTest.kt` | Thời gian khung, tổng thời lượng, mốc micro giây (JVM) |
| Tạo | `feature/export/src/test/kotlin/com/vgl/draw/animation/feature/export/encode/ColorQuantizerTest.kt` | Bảng màu đúng, ranh giới 255, 256 màu, sai số khi nhiều màu, ngưỡng alpha (JVM) |
| Tạo | `feature/export/src/test/kotlin/com/vgl/draw/animation/feature/export/encode/GifEncoderTest.kt` | Giải mã ngược bằng `javax.imageio`: khung, thời gian, lặp, trong suốt, cách bỏ khung, LZW với bảng đầy (JVM) |
| Tạo | `feature/export/src/test/kotlin/com/vgl/draw/animation/feature/export/encode/WatermarkPainterTest.kt` | Vị trí, cỡ, màu điểm ảnh, màu theo tham số, chữ từ `strings.xml` (Robolectric `NATIVE`) |
| Tạo | `feature/export/src/test/kotlin/com/vgl/draw/animation/feature/export/encode/GifPngExporterTest.kt` | Xuất GIF, GIF trong suốt, PNG qua `FrameRenderer` giả; watermark mọi khung; tiến độ; hủy, lỗi không để tệp dở; log (Robolectric `NATIVE`) |
| Tạo | `feature/export/src/test/kotlin/com/vgl/draw/animation/feature/export/encode/ExportTestSupport.kt` | `FakeFrameRenderer`, `exportTestProject(...)`, `GifTestReader` (đọc GIF bằng `javax.imageio`), màu test |
| Tạo | `core/designsystem/src/main/kotlin/com/vgl/draw/animation/core/designsystem/theme/WatermarkColors.kt` | Hai màu watermark, ARGB `Int`, không theo theme |
| Tạo | `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/WatermarkColorsTest.kt` | Khóa hai giá trị theo thiết kế đã duyệt |
| Sửa | `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/DesignTokensTest.kt` | `tokensJson()` sinh thêm nhóm `"watermark"` ở cuối đối tượng cấp trên cùng; test `watermarkColors_listedInTokens` |
| Sửa | `docs/design-system/tokens.json` | Sinh lại bằng `-PdesignTokens.update=true`: thêm một dòng nhóm `"watermark"` ở cuối đối tượng cấp trên cùng; dòng nhóm đứng trước nó (hiện là `"spacingDp"`) thêm dấu phẩy ở cuối do lệnh sinh tự làm; phần khác giữ nguyên |

Không có ảnh chuẩn screenshot: test so điểm ảnh và nội dung file giải mã bằng câu khẳng định. `CLAUDE.md` không đổi: bảng module đã có dòng `:feature:<ten>`.

Nơi đọc các file có sẵn mà plan sửa, và vì sao không phải sửa thêm:

- `settings.gradle.kts` (`grep -rln "settings.gradle" scripts/ .github/ */src/test */*/src/test`):
  - `.github/workflows/pr-check.yml`: bộ lọc `release` có `settings.gradle.kts` và `**/build.gradle.kts`, nên PR của task chạy thêm job build bản release; không đọc nội dung, không sửa.
  - `scripts/ads-plan.py`: chỉ đọc dòng `rootProject.name`, plan không đổi dòng đó.
  - `scripts/tests/run.sh` (khoảng dòng 1834 tới 1838): đọc mọi dòng `include("...")` và kiểm mỗi module nằm dưới `UI_PATHS` của `scripts/ui-test-needed.sh`; `feature/` có trong `UI_PATHS` nên `:feature:export` qua (lần chạy thử có dòng `include` mới, `All script tests passed`).
  - `scripts/plan-check.py`: chỉ có tên tệp trong danh sách tên chung (`GENERIC_NAMES`), không đọc nội dung.
  - `app/src/test/kotlin/com/vgl/draw/animation/network/NetworkDialogLanguageTest.kt`, `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdsTestThemeMatchTest.kt`, `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/BrandTaglineLanguageTest.kt`, `feature/loading/src/test/kotlin/com/vgl/draw/animation/feature/loading/LoadingScreenLanguageTest.kt`: chỉ dùng sự có mặt của tệp để tìm thư mục gốc của repo, không đọc nội dung.
  - `scripts/check.sh` duyệt thư mục `feature/*/`, nên tự kiểm module mới (chạy thử ở mục 4.1).
- `docs/design-system/tokens.json` (`grep -rln "tokens.json" scripts/ .github/ */src/test */*/src/test`):
  - `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/DesignTokensTest.kt`: sinh và so tệp; có trong bảng trên.
  - `core/designsystem/build.gradle.kts`: truyền đường dẫn và cờ `designTokens.update` cho test; không đổi.
  - `core/designsystem/src/test/kotlin/com/vgl/draw/animation/core/designsystem/DesignSystemCatalogScreenshotTest.kt`: chỉ nhắc tên tệp trong KDoc; catalog không vẽ màu mới nên ảnh chuẩn không đổi.
  - `scripts/figma-export.py`: đọc `color`, `typography`, `spacingDp`; khóa mới ở cấp trên cùng bị bỏ qua.
  - `scripts/main-guard-verified.sh`, `scripts/tests/run.sh`: coi tệp là code khi đổi, không đọc nội dung.
  - `scripts/plan-check.py`: chỉ nhắc tên trong chú thích.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có. Phần này không có giao diện; cờ `feature_export_enabled` do T-042 dùng ở lối vào màn Xuất.
- Quảng cáo: Không chạm
- Sự kiện phân tích: Không chạm
- File R2: Không chạm
- **Quyền truy cập:** Không thêm. Phần xuất chỉ ghi vào thư mục làm việc do người gọi truyền vào (T-045 dùng thư mục cache của app, không cần quyền); không thêm: `WRITE_EXTERNAL_STORAGE` (T-047, khi lưu vào thư viện).
- **Thư viện và SDK:** Không thêm. Mã hóa GIF tự viết thay vì thêm thư viện (ticket yêu cầu, tránh điểm chặn 2 và sửa `gradle/libs.versions.toml`); PNG dùng `Bitmap.compress` của Android; phông dùng `Resources.getFont` (API 26). `:feature:export` có các phụ thuộc của convention `template.android.feature` cộng `project(":core:project")`. Test dùng `javax.imageio` của JDK chạy test (có sẵn trên classpath test JVM, đã chạy thử ở mục 4.1), Robolectric, `androidx.test.core`, `kotlinx-coroutines-test` của convention.

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** bộ cắt trung vị có thể chậm hay lệch màu quá ngưỡng của `ColorQuantizerTest.manyColours_errorWithinTolerance` với ảnh nhiều màu; phần LZW và bảng màu đúng đã chạy thử qua bộ đọc JDK (mục 4.1), nên chỉ phần cắt trung vị còn chưa chắc.
- **Điều kiện chuyển:** test sai số của ảnh nhiều màu vẫn đỏ sau 2 lần sửa vì cùng một lỗi, hay `GifPngExporterTest` hết thời gian chờ mặc định của Gradle vì mã hóa chậm.
- **Cách làm thay:** lưới đều theo khoảng màu thật của khung, trong cùng `ColorQuantizer.kt` (dự phòng): tìm giá trị nhỏ nhất và lớn nhất của từng kênh trong khung, chia số mức cho ba kênh theo tỷ lệ độ rộng khoảng sao cho tích số mức không vượt giới hạn (256 hay 255), mỗi điểm làm tròn về mức gần nhất của từng kênh, chỉ số tính trực tiếp, không cần tìm kiếm. Với ảnh của test sai số (đỏ và xanh lá trải 0 tới 252, xanh dương cố định) lưới là 16 × 16 × 1, bước 17, sai số tối đa 9, dưới ngưỡng 16. Nhánh bảng màu đúng, ngưỡng alpha và mọi test giữ nguyên.

### 3.5. Hành vi đã có bị đổi

Không có. Plan chỉ tạo file Kotlin mới ở `src/main` (module mới `:feature:export` và `WatermarkColors.kt`); phần sửa duy nhất ngoài test là một dòng `include` của `settings.gradle.kts` và một dòng của `tokens.json` (cộng dấu phẩy ở cuối dòng nhóm đứng trước, do lệnh sinh), không đổi hành vi nào mà test có sẵn kiểm. `DesignTokensTest.tokensFile_matchesTheDesignSystem` vẫn kiểm đúng ý định cũ (tệp khớp code) sau khi sinh lại tệp. `DesignTokensTest.projectBackgroundColors_listedInTokens` của T-004 giữ nguyên câu khẳng định, không sửa: nhóm `"watermark"` đặt ở cuối đối tượng nên không đổi tên hay vị trí nhóm `"projectBackground"` (điều kiện dừng ở mục 7).

## 4. Kiểm thử

Test JVM chạy bằng JUnit 4. Test có `Bitmap`, `Canvas`, `Resources` dùng `@RunWith(AndroidJUnit4::class)` và `@GraphicsMode(GraphicsMode.Mode.NATIVE)`. Hàm phụ ở `ExportTestSupport.kt`:

- Màu test: `RED = 0xFFFF0000.toInt()`, `GREEN = 0xFF00FF00.toInt()`, `BLUE = 0xFF0000FF.toInt()`, `YELLOW = 0xFFFFFF00.toInt()`, `WHITE = 0xFFFFFFFF.toInt()`; `FRAME_COLORS = listOf(RED, GREEN, BLUE, YELLOW)`.
- `exportTestProject(holds: List<Int>, fps: Int = 12, aspectRatio = SQUARE_1_1, resolution = P720)`: `Project.create(...)` của T-001 rồi `copy(frames = holds.map { Frame(FrameId.random(), hold = it) })`, `name = "Secret name"`.
- `FakeFrameRenderer : FrameRenderer`: `renderInto(target, project, k, includeBackground)` ghi lại `(k, includeBackground, target.width, target.height)`; có nền thì tô kín `FRAME_COLORS[k % 4]`; không nền thì xóa trong suốt rồi tô ô vuông `FRAME_COLORS[k % 4]` từ `(w / 4, h / 4)` tới `(w / 2, h / 2)`, `Paint` tắt khử răng cưa. `failAt: Int?` ném `IOException("disk full")` ở khung đó. `render` tạo bitmap rồi gọi `renderInto`.
- `GifTestReader.read(bytes)`: `ImageIO.getImageReadersByFormatName("gif")`, trả số khung, `BufferedImage` từng khung, `delayTime`, `disposalMethod`, `transparentColorFlag` của `GraphicControlExtension`, và số lần lặp đọc từ `ApplicationExtension` `NETSCAPE` của khung đầu (cây `javax_imageio_gif_image_1.0`). Bộ đọc này độc lập với bộ mã hóa của plan.
- Điểm watermark `pillPoint(bounds) = (bounds.left + bounds.height() / 2, bounds.top + bounds.height() / 10)`: trong viên thuốc, trên biểu tượng, xa chữ và xa phần bo.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| KB-4: thời gian khung GIF theo `hold` và tốc độ khung | Unit test | `ExportTimelineTest.gifDelays_followHoldsWithCumulativeRounding`: 12 khung/giây, `hold` `[1, 3, 1, 2]` ra `[8, 25, 9, 16]`, tổng 58. `gifDelays_48FramesAt12Fps_total400`: 48 khung `hold` 1, tổng 400, mỗi khung 8 hay 9. `gifDelays_examples`: 30 khung/giây `[1, 1, 1]` ra `[3, 4, 3]`; 24 khung/giây `[4]` ra `[17]`; 6 khung/giây `[4]` ra `[67]`. `gifDelays_neverBelow3`: mọi tốc độ 6, 8, 12, 15, 24, 30 với 50 `hold` ngẫu nhiên 1 tới 4 (`Random(44)`), mọi khung từ 3 trở lên và tổng bằng `cs(tổng hold)` |
| KB-4 (cho T-043): mốc thời gian MP4 | Unit test | `ExportTimelineTest.frameStartMicros_usesHolds`: 12 khung/giây `[1, 3]`: khung 1 bắt đầu ở 83 333, `durationMicros` 333 333. `init_rejectsInvalid`: `hold` 0, `hold` 5, `fps` 0, danh sách rỗng ném `IllegalArgumentException` |
| KB-4: GIF lặp vô hạn, đúng số khung, đúng màu | Unit test | `GifEncoderTest.threeFrames_roundTripThroughImageIo`: ba khung đặc 64 × 48 `RED`, `GREEN`, `BLUE`, thời gian `[8, 25, 9]`: đọc lại có 3 khung, `delayTime` `[8, 25, 9]`, điểm (5, 5) mỗi khung đúng màu, số lần lặp `NETSCAPE` là 0, byte đầu là `GIF89a`, byte cuối `0x3B` |
| Nén LZW đúng cả khi bảng mã đầy | Unit test | `GifEncoderTest.noiseFrame_exactPalette_everyPixelRoundTrips`: 300 × 200 điểm chọn ngẫu nhiên (`Random(44)`) trong 200 màu: mọi điểm đọc lại bằng đúng màu gốc (bảng mã đầy và xóa nhiều lần). `largeUniformFrame_roundTrips`: 1080 × 1920 một màu. `onePixelFrame_roundTrips`: 1 × 1. `addFrame_rejectsWrongSizeOrDelay`: mảng thiếu một điểm, thời gian -1 hay 65 536 ném `IllegalArgumentException` |
| Mã kết thúc đúng độ rộng | Unit test | `GifEncoderTest.endCode_usesDecoderWidth`: dò độ dài dữ liệu một khung (dãy chỉ số tăng dần, không lặp cặp) sao cho `nextCode` cuối bằng 512; đọc dòng dữ liệu LZW của khung bằng một bộ đọc bit nhỏ viết theo đặc tả GIF89a trong test (đọc mã, tự dựng độ rộng như bộ giải mã); mã cuối đọc được là 257 khi đọc ở 10 bit. Đối chứng: cùng dữ liệu ngắn hơn một chỉ số (`nextCode` cuối 511) thì mã 257 đọc ở 9 bit. JDK và Skia bỏ qua mã kết thúc sai nên chỉ test này bắt được lỗi |
| GIF trong suốt: nền bỏ, khung sau không lộ khung trước | Unit test | `GifEncoderTest.transparent_alphaThresholdAndDisposal`: chế độ trong suốt, điểm alpha 127 đọc lại có alpha 0, điểm alpha 128 màu `0x80336699` đọc lại là `0xFF336699`; `transparentColorFlag` `TRUE`, `disposalMethod` `restoreToBackgroundColor`. Đối chứng cùng test: chế độ thường cho `transparentColorFlag` `FALSE`, `disposalMethod` `doNotDispose`, mọi điểm alpha 255 |
| Bảng màu: không mất màu khi ít màu; nhiều màu thì gần đúng | Unit test | `ColorQuantizerTest.exactPalette_upTo256Colours`: 256 màu khác nhau ở chế độ thường: `palette[indices[i]] == argb[i] and 0xFFFFFF` với mọi điểm. `transparentMode_255ColoursExact_256Reduced`: 255 màu đặc ở chế độ trong suốt vẫn đúng tuyệt đối, không màu nào dùng chỉ số 255; 256 màu thì đi nhánh cắt trung vị, bảng màu tối đa 255 màu đặc. `manyColours_errorWithinTolerance`: ảnh 64 × 64 có 4 096 màu (`r = 4x`, `g = 4y`, `b = 128`), mỗi kênh của mọi điểm lệch không quá 16. `alphaIgnoredInOpaqueMode`: chế độ thường, điểm alpha 0 vẫn lấy màu RGB |
| KB-4: watermark đúng vị trí và cỡ | Unit test (Robolectric `NATIVE`) | `WatermarkPainterTest.pillBounds_scaleWithShortEdge`: 1080 × 1920: cao 54, mép phải 1048, mép dưới 1888; 720 × 1280: cao 36, mép phải 698, mép dưới 1258; 1920 × 1080: cao 54, mép phải 1888, mép dưới 1048; 1080 × 1080: cao 54, mép phải 1048, mép dưới 1048. Mọi trường hợp bề rộng nằm giữa `3,3 × cao` và `4,5 × cao` (chạy thử: 206,5 ở cao 54, tức 3,8 lần) |
| KB-4: watermark chữ trắng trên viên thuốc đen 35% | Unit test (Robolectric `NATIVE`) | `WatermarkPainterTest.draw_pillTextAndIconColours`: bitmap 1080 × 1920 tô `WHITE`, `create(context).draw(...)`: điểm `pillPoint` có mỗi kênh `166 ± 2` (đen 35% trên trắng); vùng chữ (từ `left + h` tới mép phải viên thuốc) có hơn 100 điểm `WHITE` mà nếu thiếu chữ thì là 166; vùng biểu tượng (x từ `left + h / 4` tới `left + 3h / 4`, y từ `centerY - h / 4` tới `centerY + h / 4` của viên thuốc) có hơn 50 điểm `WHITE` (chạy thử đếm được 125); điểm (540, 960) và điểm cách mép trái viên thuốc 5 điểm ảnh vẫn `WHITE`. Đối chứng trong cùng test: vẽ cùng watermark trên nền `BLUE` thì `pillPoint` là `0xFF0000A6 ± 2` |
| Màu lấy từ hệ thống thiết kế, không viết cứng | Unit test (Robolectric `NATIVE`) | `WatermarkPainterTest.colours_followParameters`: `WatermarkPainter(..., pillArgb = 0x80FF0000.toInt())` trên nền trắng cho `pillPoint` đỏ nhạt (đỏ 255, xanh lá và xanh dương `127 ± 2`); mặc định dùng `WatermarkColors`. `WatermarkColorsTest.values`: `CONTENT == 0xFFFFFFFF.toInt()`, `PILL == 0x59000000` |
| Watermark trong GIF trong suốt thấy được | Unit test (Robolectric `NATIVE`) | `WatermarkPainterTest.opaquePill_drawsSolidBlack`: bitmap trong suốt, `draw(..., opaquePill = true)`: `pillPoint` là `0xFF000000`; `opaquePill = false`: `pillPoint` alpha `89 ± 1`, RGB 0 |
| Một painter dùng chung vẽ đúng ở nhiều cỡ (an toàn luồng, mục 3.1) | Unit test (Robolectric `NATIVE`) | `WatermarkPainterTest.draw_twoSizesInterleaved_independent`: cùng một painter vẽ 1080 × 1920, rồi 720 × 1280, rồi lại 1080 × 1920 lên ba bitmap trắng; bitmap thứ ba bằng bitmap thứ nhất từng điểm ảnh trong vùng viên thuốc, và vùng biểu tượng của bitmap thứ hai nằm trong viên thuốc 720 × 1280 (hơn 30 điểm `WHITE`) |
| Chữ watermark từ `strings.xml` | Unit test (Robolectric `NATIVE`) | `WatermarkPainterTest.create_usesBrandString`: `create(context).text == context.getString(R.string.export_watermark_brand)` và bằng "Frameloop" |
| Tên tệp PNG | Unit test | `GifPngExporterTest.pngFileName_padsToCount`: `(0, 3)` ra `001.png`, `(47, 48)` ra `048.png`, `(999, 1000)` ra `1000.png`, `(0, 1000)` ra `0001.png` |
| KB-4: GIF một khung GIF cho mỗi khung dự án, thời gian theo `hold`, độ phân giải đã chọn | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.gif_oneFramePerProjectFrame_delaysFollowHolds`: dự án 1:1, 720p, `hold` `[1, 3, 1, 2]`, GIF, watermark tắt: một tệp `animation.gif`; đọc lại 4 khung 720 × 720, `delayTime` `[8, 25, 9, 16]`, điểm (360, 180) khung `k` là `FRAME_COLORS[k]`; renderer được gọi đúng 4 lần, khung 0 tới 3 theo thứ tự, `includeBackground = true`, đích 720 × 720. `gif_portrait1080p_size`: 9:16, 1080p ra 1080 × 1920. `gif_firstFrameDecodesWithBitmapFactory`: `BitmapFactory.decodeFile` đọc được, điểm (360, 180) là `RED` (bộ giải mã Skia, độc lập với `javax.imageio`) |
| KB-4, câu hỏi 1: GIF trong suốt bỏ nền | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.transparentGif_backgroundOmitted`: renderer nhận `includeBackground = false`; đọc lại khung 0: điểm (5, 5) alpha 0, điểm (270, 270) là `RED` đặc; `transparentColorFlag` `TRUE` |
| KB-4: chuỗi PNG một ảnh mỗi khung, trong suốt | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.pngSequence_onePngPerFrame_transparent`: `hold` `[1, 4, 2]` ra đúng 3 tệp `001.png`, `002.png`, `003.png` (không phải 7); `BitmapFactory` đọc mỗi tệp 720 × 720, điểm (5, 5) alpha 0, điểm (270, 270) là `FRAME_COLORS[k]`; `ExportedFiles.totalBytes` bằng tổng `length()` |
| KB-4: watermark bật thì mọi khung có | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.watermarkOn_everyFrameHasIt`, ba ca GIF, GIF trong suốt, PNG với 3 khung: tại `pillPoint(watermark.pillBounds(720, 720))` mỗi khung: GIF có mỗi kênh bằng `0.65 × màu khung ± 3` (ví dụ `RED` thành đỏ `166 ± 3`; sai số 3 vì mép khử răng cưa của chữ và biểu tượng có thể làm khung vượt 256 màu và đi nhánh cắt trung vị, nhưng các màu nằm trên vài đoạn thẳng giữa màu khung, đen 35% và trắng, nên màu viên thuốc là một khối lớn, giữ hộp 5-5-5 riêng); GIF trong suốt là `0xFF000000`; PNG có alpha `89 ± 1`, RGB 0 |
| KB-4: watermark tắt thì không khung nào có | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.watermarkOff_noFrameHasIt`, cùng ba ca: tại cùng điểm, GIF là đúng màu khung, GIF trong suốt và PNG có alpha 0. Đối chứng: ca bật ở dòng trên |
| KB-4 (hỗ trợ): tiến độ theo khung | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.progress_reportsEachFrameInOrder`: 4 khung, `onFrameDone` nhận `(1, 4)`, `(2, 4)`, `(3, 4)`, `(4, 4)` theo thứ tự, ở cả GIF và PNG |
| KB-4: hủy thì không để lại file dở | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.cancel_deletesPartialFiles`: GIF và PNG, 5 khung, `runTest` khởi chạy `export` trong một `Job` với `StandardTestDispatcher` của test làm `encodeDispatcher`; `onFrameDone` gọi `job.cancel()` khi `done == 2`: sau `join()`, `job.isCancelled`, thư mục làm việc không còn tệp nào, renderer được gọi tối đa 3 lần, logger có dòng `INFO` "Export cancelled". Đối chứng: tệp có trong thư mục ngay trước khi hủy (đếm trong `onFrameDone` khi `done == 1`) |
| KB-6: lỗi thì không để lại file dở | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.renderError_deletesPartialFilesAndRethrows`: `FakeFrameRenderer(failAt = 2)`, GIF và PNG: thư mục làm việc có sẵn tệp `keep.txt` từ trước khi xuất; lỗi ném ra là `IOException` (so loại lỗi); sau đó tập tên tệp trong thư mục bằng `setOf("keep.txt")` (phần xuất chỉ xóa tệp của nó, không để tệp dở); logger có dòng `WARN` thẻ "Export" |
| MP4 không thuộc ticket này | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.mp4_rejectedBeforeRendering`: định dạng `MP4` ném `IllegalArgumentException`, renderer không được gọi, thư mục không có tệp |
| KB-4: dự án không thay đổi | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.export_onlyReadsProject`: sau khi xuất, đối tượng `Project` truyền vào bằng (`==`) bản chép trước khi xuất, và mọi lần gọi renderer nhận đúng đối tượng đó. `DefaultGifPngExporter` không nhận `ProjectStore` nên không ghi được vào dự án |
| ADR-0019: log không có dữ liệu dự án | Unit test (Robolectric `NATIVE`) | `GifPngExporterTest.logs_finishWithoutProjectData`: sau khi xuất GIF xong, `RecordingAppLogger` có một dòng `INFO` thẻ "Export" chứa `format=GIF` và `frames=4`; không dòng nào chứa "Secret name", mã dự án hay đường dẫn thư mục làm việc |
| Màu watermark có trong `tokens.json` | Unit test | `DesignTokensTest.watermarkColors_listedInTokens`: `tokensJson()` có dòng `"watermark": {"content": "#FFFFFF", "pill": "#000000", "pillOpacity": 0.35}` và nhóm này là nhóm cuối của đối tượng cấp trên cùng; chạy thêm dưới `Locale.setDefault(Locale.forLanguageTag("vi-VN"))` vẫn ra `0.35` (khôi phục locale cũ trong `finally`); `projectBackgroundColors_listedInTokens` của T-004 vẫn qua; `tokensFile_matchesTheDesignSystem` qua sau khi sinh lại tệp |
| Watermark trông đúng mẫu thiết kế, GIF phát đúng nhịp trong thư viện máy | Kiểm tra thủ công (người: so ảnh khung xuất với frame `Watermark_reference` trên Figma) | Làm khi T-046 đã lưu được file vào thư viện: dev xuất một GIF và một chuỗi PNG từ bản staging, mở bằng ứng dụng xem ảnh, so watermark với frame `Watermark_reference` (node 33-8115), xem GIF lặp và khung giữ 3× đứng lâu gấp ba. Ticket này chưa có lối vào trên giao diện nên không có bước trên máy ở phiên này |

Thứ tự commit: bước 3 commit các test, `ExportTestSupport.kt`, `WatermarkColorsTest.kt`, phần sửa `DesignTokensTest.kt`, cùng khung rỗng tối thiểu để test biên dịch được (`settings.gradle.kts`, `build.gradle.kts`, `consumer-rules.pro`, `strings.xml`, `robolectric.properties`, `WatermarkColors` có hai hằng bằng 0, các lớp, interface, enum, thân hàm `TODO()`); test đỏ vì `NotImplementedError` hay sai giá trị. Bước 4 viết phần cài đặt, giá trị màu thật và sinh lại `docs/design-system/tokens.json`. `create_usesBrandString` có thể qua ngay ở bước 3 nếu khung rỗng đã đọc chuỗi; khi đó ghi vào nhật ký lần đối chứng: đổi tạm `R.string.export_watermark_brand` thành một chuỗi khác thì test đỏ.

Trong phiên, sau `scripts/check.sh` chạy thêm `./gradlew :feature:export:lintDebug :core:designsystem:lintDebug`, vì lint của `check.sh` chỉ chạy cho `:app` mà `:app` chưa phụ thuộc `:feature:export`.

**Ngoại lệ viết test trước:** `ExportEncodingModule.kt` (nối Hilt) không có test riêng: `:app` chưa phụ thuộc module nên đồ thị Hilt chưa được dựng; T-045 kiểm khi ViewModel nhận `GifPngExporter` qua Hilt. Dòng kiểm tra thủ công chạy sau T-046.

### 4.1. Chạy thử

- **Đã chạy thử:** 2026-11-22, worktree nháp tách từ `origin/main` 5ea6ec0, không commit. Code của T-001, T-004 chưa có trên nhánh chính, nên lần thử không gọi `FrameRenderer`; phần đó chỉ là lời gọi interface đã duyệt ở T-004. Nhánh nháp tạo `:feature:export` đúng như mục 3.1 (dòng `include`, `build.gradle.kts` không có dòng `:core:project`, `strings.xml`, `robolectric.properties`), một bộ mã hóa GIF thử (bảng màu đúng, cắt trung vị 5-5-5, LZW bảng băm 5 003 ô, khối con 255 byte, `NETSCAPE2.0`, khối điều khiển đồ họa) và một test Robolectric `NATIVE` gồm 5 ca: đọc lại 3 khung bằng `javax.imageio` (thời gian `[8, 9, 25]`, màu từng khung, số lần lặp 0); ảnh 128 × 128 trong suốt nhiều màu (điểm alpha 0 đọc lại alpha 0, điểm gradient lệch mỗi kênh không quá 12); `BitmapFactory.decodeByteArray` đọc khung đầu đúng màu; 3 khung 1080 × 1920 nhiều màu; watermark vẽ bằng `Resources.getFont(R.font.inter_semibold)` và `getDrawable(R.drawable.brand_mark)` của `:core:designsystem`, chữ từ `R.string.export_watermark_brand`.
- **Lệnh:** `./gradlew :feature:export:ktlintFormat`, `scripts/check.sh`, `./gradlew :feature:export:lintDebug`, `./gradlew :app:assembleStagingRelease`.
- **Kết quả:**
  - Lần 1, `scripts/check.sh`: `== Cổng chất lượng: QUA`, 2442 test JVM 0 lỗi, `:feature:export` 5 test 0 lỗi; `checkModuleDependencies` và ktlint qua với module mới. `javax.imageio` biên dịch và chạy được trong test Robolectric của module Android: đọc đúng số khung, `delayTime`, khối `NETSCAPE`. `BitmapFactory` của Robolectric `NATIVE` đọc được khung đầu của GIF tự mã hóa. `getFont` và `getDrawable` với R của `:core:designsystem` chạy được trên Robolectric `NATIVE`. Ở 1080 × 1920, chữ "Frameloop" cỡ 27 rộng 139, viên thuốc 206,5 × 54; điểm đệm của viên thuốc trên nền trắng là `#A6A6A6` (166); vùng chữ có 986 điểm trắng, vùng biểu tượng 125. Mã hóa 3 khung 1080 × 1920 nhiều màu (nhánh cắt trung vị) mất 466 ms trên máy dev, khoảng 155 ms mỗi khung; file 1 023 327 byte, khoảng 340 KB mỗi khung.
  - `./gradlew :feature:export:lintDebug` sau lần 1 đỏ: `Supplied consumer proguard configuration does not exist: .../feature/export/consumer-rules.pro` ở `mergeDebugConsumerProguardFiles`. `check.sh` không thấy lỗi này vì `:app` chưa phụ thuộc module. Thêm tệp rỗng như `:feature:sample` (đã đưa vào mục 3.2) thì lint qua: "No issues found".
  - Lần 2: thêm tạm `implementation(project(":feature:export"))` vào `app/build.gradle.kts`, như T-042 sẽ làm, và một ca đo sai số cắt trung vị (ảnh 64 × 64 có 4 096 màu: sai số mỗi kênh tối đa 12, dưới ngưỡng 16 của mục 4). `./gradlew :app:assembleStagingRelease` qua, R8 không cần luật thêm. `scripts/check.sh` lần 2 (có module trong `:app`, nên `:app:lintStagingDebug` quét cả module): `== Cổng chất lượng: QUA`, 2443 test JVM 0 lỗi, `:feature:export` 6 test 0 lỗi.
  - Phương án dự phòng ở mục 3.4 chưa thử, vì phương án chính qua. Worktree nháp đã xóa sau khi thử.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 7 (module core dùng chung: thêm `WatermarkColors` vào `:core:designsystem`, sửa `DesignTokensTest.kt` và `tokens.json`). Dòng `include` ở `settings.gradle.kts` là bước chuẩn khi thêm một feature, như `:feature:sample`, không đổi cấu hình build chung; nếu người duyệt tính nó là cấu hình build thì vẫn là điểm chặn 7, mức không đổi.

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 3 | Module mới `:feature:export` với giao diện công khai cho T-042, T-043, T-045; thêm hằng ở `:core:designsystem`; sửa `settings.gradle.kts`, `DesignTokensTest.kt`, `tokens.json` |
| B. Độ rõ của yêu cầu | 2 | Spec và mẫu watermark đã duyệt, có số đo; còn câu hỏi ảnh hưởng tới cách làm và giá trị test: viên thuốc trong GIF trong suốt (câu 2), chuỗi PNG bỏ qua `hold` (câu 3), chỗ đặt module (câu 1) |
| C. Khả năng kiểm chứng tự động | 1 | Mọi hành vi có test giải mã ngược bằng hai bộ đọc độc lập và test điểm ảnh; chỉ việc so watermark với mẫu Figma và xem GIF phát trong thư viện máy là người xem, sau T-046 |
| D. Ảnh hưởng chéo | 2 | Sửa `DesignTokensTest.kt`, `tokens.json` mà T-004 vừa sửa; `settings.gradle.kts` mà T-001 vừa sửa và T-048 cùng đợt cũng sửa (thêm `:core:lessons`); T-042, T-043, T-045, T-046 dựa vào API của module này. Không đổi hành vi có sẵn |
| E. Độ mới của kỹ thuật | 2 | Bộ mã hóa GIF tự viết (LZW, cắt trung vị) lần đầu có trong dự án; kỹ thuật phổ biến, có tài liệu đầy đủ (đặc tả GIF89a), đã chạy thử qua bộ đọc của JDK và Skia (mục 4.1) |
| **Tổng** | 10 | **Mức: Cao** (chạm điểm chặn 7, tổng điểm cũng từ 9) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `settings.gradle.kts` (một dòng), `:core:designsystem` (đối tượng mới `WatermarkColors`, không sửa lớp có sẵn), `DesignTokensTest.kt` và `docs/design-system/tokens.json` (nhóm `"watermark"` mới). Người của nhóm thiết kế thêm biến màu watermark không theo theme vào Figma sau khi merge nếu muốn; AI không sửa Figma.
- Task khác có thể xung đột:
  - T-001, T-004: phải merge trước. Plan dùng `Project.create`, `Frame`, `FrameId`, `FrameRate`, `Resolution`, `AspectRatio`, `FrameRenderer.renderInto`, `RenderSize.forExport` và `RenderModule` đúng như mục 3.1 của hai plan đó. Nhóm `"watermark"` đứng cuối đối tượng cấp trên cùng của `tokens.json`, sau mọi nhóm có sẵn kể cả `"projectBackground"`; T-004 chưa merge thì plan không chạy được (phụ thuộc của ticket).
  - T-042 (màn Xuất, chọn định dạng): ticket T-042 được sửa trong PR của đợt này: thêm T-044 vào `depends_on` và ghi dùng `:feature:export` do T-044 tạo (Sửa, không Tạo). Sau plan này, module đã có: plan T-042 ghi Sửa cho `feature/export/build.gradle.kts`, không thêm lại dòng `include`, dùng lại `ExportFormat`, `ExportOptions`, và thêm `implementation(project(":feature:export"))` vào `app/build.gradle.kts` khi đăng ký màn. Ảnh xem trước có watermark (`PreviewWatermark`) dùng `WatermarkColors` và nên dùng chính `WatermarkPainter.pillBounds`, `draw` qua `drawIntoCanvas { it.nativeCanvas }`, để ảnh xem trước giống file xuất.
  - T-043 (MP4): dùng `WatermarkPainter.draw` (viên thuốc 35%, `opaquePill = false`), `ExportTimeline.frameStartMicros` cho mốc thời gian, `ExportFormat.MP4`; viết exporter riêng cạnh `GifPngExporter`, nên cùng chữ ký `suspend fun export(project, options, workDir, onFrameDone): ExportedFiles` để T-045 không phải rẽ nhánh theo định dạng ngoài việc chọn exporter.
  - T-045 (đang xuất, hủy, lỗi): gọi `GifPngExporter.export` với thư mục làm việc trong cache của app, đổi `onFrameDone` thành tiến độ (gọi trên luồng mã hóa, nên cập nhật `StateFlow` chứ không gọi composable), hủy bằng cách hủy coroutine, phân loại `IOException` hết chỗ thành lỗi thiếu bộ nhớ. Mỗi lần xuất truyền một thư mục làm việc mới hay rỗng. Phần xuất đã xóa tệp dở; T-045 xóa thư mục làm việc sau khi lưu xong hay khi rời màn, và dọn mọi thư mục làm việc còn sót khi app khởi động (app bị dừng giữa lúc xuất; spec export mục 6: tệp tạm xóa khi lưu xong, hủy, lỗi hay dừng).
  - T-046 (lưu vào thư viện): đổi tên `animation.gif` thành "<tên dự án>.gif", `001.png` thành "<tên dự án>_001.png" khi chép vào MediaStore.
  - T-048 (cùng đợt, `features/lessons/plans/T-048-core-lessons.md`): tạo `:core:lessons`, cũng sửa `settings.gradle.kts` (dòng `include` sau nhóm `:core:*`). Hai plan chỉ thêm một dòng ở hai chỗ khác nhau; plan chạy sau giữ dòng của plan trước.
- Đề xuất thứ tự chạy: sau T-001 và T-004 (bắt buộc); trước T-042, T-043, T-045. Thứ tự với T-048 không quan trọng về hành vi; danh sách chạy ghi hai plan cùng sửa `settings.gradle.kts`.

## 7. Điều kiện dừng riêng

- Cần sửa file Kotlin ngoài thư mục test của T-001 hay T-004 (mô hình, `FrameRenderer`, `StoreFrameRenderer`, `RenderSize`, các Hilt module) để phần xuất chạy: dừng, vì plan dựa trên API đã duyệt của hai plan đó.
- Cần một thư viện mã hóa GIF hay xử lý ảnh: dừng (điểm chặn 2).
- `javax.imageio` không dùng được trong test trên CI (lớp không có trên classpath test, hay bộ đọc GIF không có): dừng, không thay bằng bộ giải mã GIF tự viết trong test, vì bộ đọc của test phải độc lập với bộ mã hóa.
- `DesignTokensTest.projectBackgroundColors_listedInTokens` của T-004 đỏ vì nhóm `"watermark"` mới: dừng, không sửa câu khẳng định của test đó.
- Cần đổi lớp `AppColors`, phần nào của `tokens.json` ngoài nhóm `"watermark"` mới (trừ dấu phẩy mà lệnh sinh thêm vào cuối dòng nhóm đứng trước), hay ảnh chuẩn screenshot nào của `:core:designsystem`: dừng.

## 8. Câu hỏi cho người duyệt

1. **Phần làm ra file đặt ở module nào?** Ticket T-042 ghi nó tạo `:feature:export`, nhưng T-044 chỉ phụ thuộc T-004 và có thể chạy trước T-042. Đề xuất của AI: T-044 tạo `:feature:export` (chỉ màn Xuất cần mã hóa file; màn Hoàn thành bài học mở màn Xuất chứ không tự xuất), T-042 nhận module có sẵn và ghi Sửa. Phương án khác: đặt vào `:core:project` gói `export` cạnh phần vẽ của T-004; khi đó không tạo module, nhưng `:core:project` thêm chữ hiển thị và phần mã hóa vốn chỉ màn Xuất dùng. Cả hai phương án đều chạm điểm chặn 7 vì màu watermark ở `:core:designsystem`.
2. **Viên thuốc của watermark trong GIF trong suốt.** GIF chỉ có trong suốt một bit; viên thuốc đen 35% (alpha 89) dưới ngưỡng 128 nên sẽ biến mất, chỉ còn chữ trắng, không đọc được trên nền trắng của app khác. Đề xuất của AI: trong GIF trong suốt, viên thuốc vẽ đen đặc (`#000000`), chữ và biểu tượng giữ trắng; MP4, GIF thường và chuỗi PNG giữ đen 35% như mẫu. Ngưỡng 128 cũng áp cho mép nét vẽ (mép khử răng cưa nửa trong trở lên thành đặc). Phương án khác: viên thuốc xám đặc `#A6A6A6` (màu của đen 35% trên nền trắng), giống mẫu khi đặt trên nền trắng nhưng chữ trắng kém nổi.
3. **Chuỗi PNG với khung giữ 2× tới 4×.** Spec ghi "đúng một ảnh cho mỗi khung của dự án". Đề xuất của AI: làm đúng như spec, khung giữ 3× vẫn là một ảnh; app chỉnh sửa khác sẽ không biết khung đó đứng lâu hơn. Phương án khác: lặp ảnh theo `hold` (khung giữ 3× thành 3 ảnh giống nhau), giữ đúng nhịp khi nhập vào app khác nhưng trái chữ của spec, cần sửa spec.
4. **GIF ở độ phân giải đã chọn, không dithering.** Spec cho chọn 720p hay 1080p với mọi định dạng. GIF 1080 × 1920 của tranh nét đặc nhỏ (bảng màu đúng, nén tốt), nhưng tranh nhiều màu (ảnh nền "Photo") có thể khoảng 340 KB mỗi khung (mục 4.1), tức vài chục MB cho 48 khung. Đề xuất của AI: giữ đúng độ phân giải đã chọn, không dithering (vùng màu phẳng giữ phẳng); ảnh nền "Photo" trong GIF bị dồn màu là giới hạn của GIF, người dùng muốn đẹp thì chọn MP4. Phương án khác: thêm dithering Floyd-Steinberg cho khung vượt 256 màu (ảnh nền đẹp hơn, file lớn hơn, nét đặc có thể lấm tấm).
5. **Duyệt chéo khi nhóm một dev.** Plan rủi ro Cao cần duyệt chéo; nếu nhóm vẫn chỉ có anh, mục 9 ghi anh vừa duyệt vừa duyệt chéo kèm lý do (risk-scoring.md, mục 3).

## 9. Quyết định duyệt

<!-- Người duyệt điền, hoặc AI ghi theo lời đồng ý trong phiên của người duyệt, nguyên văn (ADR-0048, phần C). Plan `lane: quick`: ghi "Duyệt trong phiên làm nhanh" kèm lời đồng ý. -->

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện

<!-- AI ghi khi task đã có kết quả cuối (đã merge hoặc đã dừng), trên nhánh báo cáo của phiên (auto-run.md, bước 10). Mục mới thêm vào cuối, không sửa mục cũ. -->
