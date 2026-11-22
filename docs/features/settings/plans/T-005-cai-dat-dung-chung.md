---
ticket: T-005
title: Cài đặt dùng chung trên máy: mặc định dự án, lực bút, mốc nét vẽ đầu tiên
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 5M           # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [core]      # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-005: CÀI ĐẶT DÙNG CHUNG TRÊN MÁY: MẶC ĐỊNH DỰ ÁN, LỰC BÚT, MỐC NÉT VẼ ĐẦU TIÊN

## 1. Tóm tắt

Thêm vào `:core:data` một repository `DeviceSettingsRepository` giữ bốn giá trị mà nhiều feature cùng đọc: tốc độ khung mặc định (12), khung hình mặc định (`"9:16"`), lực bút cảm ứng (bật) và mốc "nét vẽ đầu tiên" (chưa có). Giá trị lưu thô (số nguyên, chuỗi, đúng/sai) trong DataStore dùng chung của app, kiểm theo danh sách giá trị hợp lệ ngay trong `:core:data`, luồng đọc tự thử lại khi gặp `IOException`. Kiểm chứng bằng unit test JVM, gồm một test trên tệp DataStore thật để chắc giá trị còn sau khi mở lại.

## 2. Hiểu yêu cầu

Ticket xử lý phần dữ liệu của các kịch bản sau trong `features/settings/spec.md`. Màn Cài đặt (dòng, hộp chọn, công tắc) là T-016; màn Tạo dự án đọc giá trị là T-011; màn vẽ ghi mốc là T-019, đọc lực bút là T-022; luật quảng cáo đọc mốc là T-007.

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-1, trường hợp đặc biệt | Khi mở app lần đầu, chưa có dự án nào, thì màn Cài đặt hiện giá trị mặc định 12 fps, 9:16, lực bút bật | Kho chưa lưu gì thì trả 12, `"9:16"`, `true`; mốc nét vẽ đầu tiên trả `false` |
| KB-2 | Danh sách tốc độ 6, 8, 12, 15, 24, 30; chọn 24 thì lần tạo dự án sau chọn sẵn 24; tắt app rồi mở lại thì giá trị còn | Kho chỉ nhận sáu giá trị đó (settings câu 9); ghi rồi đọc lại ra đúng giá trị; giá trị nằm trong tệp DataStore nên còn sau khi mở lại. Danh sách giá trị hợp lệ là hằng công khai để T-016 và T-011 dùng chung một chỗ |
| KB-3 | Danh sách tỷ lệ 16:9, 9:16, 1:1; chọn 16:9 thì lần tạo dự án sau chọn sẵn 16:9; tắt app rồi mở lại thì giá trị còn | Kho chỉ nhận ba chuỗi `"16:9"`, `"9:16"`, `"1:1"` (settings câu 9, spec projects câu 1 bỏ 4:5). Chuỗi trùng giá trị mà `:core:project` ghi trong tệp dự án (plan T-001), feature đổi sang kiểu của `:core:project` ở chỗ dùng (plan T-001, câu hỏi 4) |
| KB-4 | Bấm "Stylus pressure" thì On thành Off và ngược lại; tắt app rồi mở lại thì lựa chọn còn | Kho giữ một giá trị đúng/sai, mặc định `true`. Đây là giá trị mặc định cho cọ chưa chỉnh riêng (settings câu 7, editor câu 22); lực bút riêng từng cọ không thuộc ticket này (T-022) |
| KB-2, KB-3, trường hợp đặc biệt | Đổi giá trị mặc định thì dự án đã có và bài học không đổi | Kho này không chạm dữ liệu dự án; mỗi dự án giữ tốc độ và khung hình của riêng nó trong tệp dự án (T-001). Không cần code trong ticket này |
| KB-10 (settings câu 15) | Cờ Cài đặt tắt thì màn Tạo dự án dùng 12 fps, 9:16; giá trị đã chọn vẫn giữ trên máy | Kho không đọc cờ và không có hàm xóa, nên giá trị đã lưu không bao giờ bị xóa theo cờ. Việc dùng 12, 9:16 khi cờ tắt là của T-011, dùng hằng `DEFAULT_FRAME_RATE`, `DEFAULT_ASPECT_RATIO` của ticket này |
| Mốc nét vẽ đầu tiên (editor KB-2, tiêu chí cuối; editor mục 6) | Khi người dùng vẽ nét đầu tiên ở bất kỳ dự án hay bài học nào thì app nhớ mốc trên máy | Một mốc cho cả máy, chỉ đi một chiều (chưa có thành có), giữ tới khi xóa dữ liệu app. Luồng đọc phát giá trị mới ngay khi mốc được ghi, để luật quảng cáo đổi trong cùng phiên mà không cần mở lại app |

Thiết kế tham chiếu: Không có thiết kế riêng (ticket chỉ làm phần dữ liệu, không có màn hình; docs/ops/design-workflow.md).

ADR liên quan: ADR-0001 (core chỉ phụ thuộc core, kiểm bằng `checkModuleDependencies`), ADR-0006 (dữ liệu chỉ ở trên máy), ADR-0019 (ghi log qua `:core:logging`, không ghi dữ liệu người dùng).

## 3. Giải pháp

### 3.1. Cách làm

Làm theo mẫu `DataStoreCounterRepository` của `:feature:sample` (đọc qua `retryWhen`, ghi qua `edit`) và `FirstOpenMark` của `:core:ads` (mốc một lần, ghi log khi lỗi). Dùng chung một `DataStore<Preferences>` mà `DataStoreModule` của `:core:data` đã cấp (tệp `app_preferences`), không tạo tệp mới.

**Giao diện công khai** (`core/data/.../settings/DeviceSettingsRepository.kt`):

```kotlin
interface DeviceSettingsRepository {
    val defaultFrameRate: Flow<Int>          // one of DeviceSettingsValues.FRAME_RATES
    val defaultAspectRatio: Flow<String>     // one of DeviceSettingsValues.ASPECT_RATIOS
    val stylusPressure: Flow<Boolean>
    val firstStrokeDrawn: Flow<Boolean>

    /** Throws IllegalArgumentException for a value outside FRAME_RATES, IOException when the write fails. */
    suspend fun setDefaultFrameRate(frameRate: Int)
    /** Throws IllegalArgumentException for a value outside ASPECT_RATIOS, IOException when the write fails. */
    suspend fun setDefaultAspectRatio(aspectRatio: String)
    /** Throws IOException when the write fails. */
    suspend fun setStylusPressure(enabled: Boolean)
    /** Stores the mark once; true when it is stored (now or before), false when the write failed. Never throws IOException. */
    suspend fun markFirstStrokeDrawn(): Boolean
}

object DeviceSettingsValues {
    val FRAME_RATES: List<Int> = listOf(6, 8, 12, 15, 24, 30)
    const val ASPECT_RATIO_16_9 = "16:9"
    const val ASPECT_RATIO_9_16 = "9:16"
    const val ASPECT_RATIO_1_1 = "1:1"
    val ASPECT_RATIOS: List<String> = listOf(ASPECT_RATIO_16_9, ASPECT_RATIO_9_16, ASPECT_RATIO_1_1)
    const val DEFAULT_FRAME_RATE = 12
    const val DEFAULT_ASPECT_RATIO = ASPECT_RATIO_9_16
    const val DEFAULT_STYLUS_PRESSURE = true
}
```

Thứ tự trong hai danh sách là thứ tự hiện ở hộp chọn của T-016 (tốc độ tăng dần như chip của màn Tạo dự án; tỷ lệ theo spec settings KB-3). KDoc của interface ghi: giá trị thô, không phụ thuộc `:core:project`; màn vẽ đọc `firstStrokeDrawn` và chỉ gọi `markFirstStrokeDrawn()` khi luồng còn `false`, không gọi ở mỗi nét; gọi tối đa một lần mỗi lần mở màn vẽ, hàm trả `false` thì không gọi lại tới lần mở màn vẽ sau (khi tệp đọc lỗi mãi, luồng giữ `false` và mỗi lần gọi lại lỗi, ghi log).

**Cài đặt** (`DataStoreDeviceSettingsRepository`, `internal`, `@Inject constructor(dataStore: DataStore<Preferences>, logger: AppLogger)`):

- Bốn khóa mới trong tệp `app_preferences`: `intPreferencesKey("settings_default_frame_rate")`, `stringPreferencesKey("settings_default_aspect_ratio")`, `booleanPreferencesKey("settings_stylus_pressure")`, `booleanPreferencesKey("first_stroke_drawn")`. Tên khóa là hợp đồng với dữ liệu đã lưu trên máy người dùng: test khóa tên (mục 4), đổi về sau là chạm điểm chặn 9. Không trùng khóa đã có (`sample_count`, `ads_first_open_done`, `ads_inter_display_count`).
- Một luồng chung `preferences = dataStore.data.retryWhen { cause, attempt -> ... }`: chỉ thử lại khi `cause is IOException`; mỗi lần ghi `logger.warn("Settings", "Device settings not read, retry ${attempt + 1}", cause)`, phát `emptyPreferences()` (các luồng ra giá trị mặc định), chờ `min(1_000L shl attempt.coerceAtMost(5), 30_000L)` ms như mẫu sample, rồi thử lại. Lỗi khác ném ra ngoài như cũ.
- Bốn luồng công khai `map` từ luồng chung rồi `distinctUntilChanged()`:
  - `defaultFrameRate`: giá trị đã lưu nếu nằm trong `FRAME_RATES`, không thì `DEFAULT_FRAME_RATE`.
  - `defaultAspectRatio`: giá trị đã lưu nếu nằm trong `ASPECT_RATIOS`, không thì `DEFAULT_ASPECT_RATIO`. Giá trị lạ (ví dụ `"4:5"` của bản thiết kế cũ, hay lỗi của bản sau) không làm hỏng màn Tạo dự án.
  - `stylusPressure`: giá trị đã lưu, không có thì `DEFAULT_STYLUS_PRESSURE`.
  - `firstStrokeDrawn`: `true` chỉ khi khóa có giá trị `true`. Đọc lỗi thì ra `false`: luật quảng cáo khi đó hiện ít quảng cáo hơn, là phía an toàn (câu hỏi 2).
- Ba hàm ghi giá trị: `require(...)` trước (giá trị ngoài danh sách là lỗi lập trình, ném `IllegalArgumentException` và không ghi gì), rồi `dataStore.edit { it[KEY] = value }`. `IOException` ném ra cho ViewModel bắt và đưa vào `UiState` (CLAUDE.md, Viết một feature mới, bước 4).
- `markFirstStrokeDrawn()`: `dataStore.edit { if (it[KEY] != true) it[KEY] = true }`, trả `true`; bắt `IOException`, ghi `logger.warn("Settings", "First-stroke mark not written", e)`, trả `false` (câu hỏi 1). DataStore không ghi tệp khi dữ liệu không đổi, nên gọi lại khi mốc đã có không tốn lần ghi đĩa. Không có hàm xóa mốc.
- Log không chứa giá trị cài đặt, chỉ có số lần thử và loại lỗi.

**Hilt:** `di/DeviceSettingsModule.kt`, `internal abstract class` với `@Binds` như `SampleModule`, cài vào `SingletonComponent`. `:core:data` thêm `implementation(project(":core:logging"))` để có `AppLogger`; `:core:logging` không phụ thuộc module nào nên không thành vòng. `:core:data` vẫn không phụ thuộc `:core:project` (quyết định của anh Nhật ngày 2026-11-22, plan T-001 câu hỏi 4), vì `:core:project` (T-002) sẽ phụ thuộc `:core:data`.

**Chưa nối vào đâu:** ticket không sửa feature nào. `:app` đã phụ thuộc `:core:data`, nên đồ thị Hilt của `:app` có binding mới ngay; `scripts/check.sh` build `:app` nên lỗi binding lộ ra ở phiên.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `core/data/build.gradle.kts` | Thêm `implementation(project(":core:logging"))` |
| Tạo | `core/data/src/main/kotlin/com/vgl/draw/animation/core/data/settings/DeviceSettingsRepository.kt` | Interface `DeviceSettingsRepository` và `object DeviceSettingsValues` (mục 3.1), có KDoc |
| Tạo | `core/data/src/main/kotlin/com/vgl/draw/animation/core/data/settings/DataStoreDeviceSettingsRepository.kt` | Cài đặt `internal` trên DataStore: bốn khóa, luồng đọc thử lại khi `IOException`, kiểm giá trị hợp lệ, mốc một chiều |
| Tạo | `core/data/src/main/kotlin/com/vgl/draw/animation/core/data/di/DeviceSettingsModule.kt` | Hilt `@Binds` `DataStoreDeviceSettingsRepository` thành `DeviceSettingsRepository` |
| Tạo | `core/data/src/test/kotlin/com/vgl/draw/animation/core/data/settings/DataStoreDeviceSettingsRepositoryTest.kt` | Unit test của mục 4 |
| Tạo | `core/data/src/test/kotlin/com/vgl/draw/animation/core/data/settings/TestDataStores.kt` | Bản giả dùng trong test: `MemoryDataStore` (có `state` để đọc khóa thô), `UnreadableDataStore`, `FlakyDataStore(failuresBeforeSuccess, stored)`, `UnwritableDataStore`, `BrokenDataStore` (luồng ném `IllegalStateException`) |

Không sửa `DataStoreModule.kt`, `consumer-rules.pro` (không có lớp nào cần giữ tên khi R8 chạy: khóa DataStore là chuỗi), `CLAUDE.md`, `settings.gradle.kts`. Không script, workflow hay test nào đọc `core/data/build.gradle.kts` (`grep -rn core/data scripts/ .github/` không ra dòng nào). `core/data` chưa có `src/test`, nên khi thêm test, Gradle hết bỏ qua lỗi "không tìm thấy test" cho module này (`KotlinAndroid.kt` chỉ tắt kiểm này khi thiếu `src/test`); mục 4 có đủ test nên không ảnh hưởng.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có. Kho không đọc `feature_settings_enabled`; khi cờ tắt, giá trị đã lưu vẫn còn (settings câu 15), còn việc dùng 12 fps, 9:16 thay cho giá trị đã lưu là của T-011.
- Quảng cáo: Không chạm. Ticket chỉ lưu mốc nét vẽ đầu tiên; luật "trước nét vẽ đầu tiên" đọc mốc ở T-007, T-008, T-010, T-011 và các ticket quảng cáo của bài học, xuất file, Cài đặt.
- Sự kiện phân tích: Không chạm.
- File R2: Không chạm.
- Dữ liệu trên máy: bốn khóa mới trong tệp DataStore `app_preferences` có sẵn (mục 3.1). Không chạm điểm chặn 9: điểm này nói về lược đồ cần chuyển đổi dữ liệu cũ, mà đây là lần đầu có các khóa này, app chưa phát hành, không máy nào có giá trị cũ dưới tên hay dạng khác để chuyển; khóa đã có của dự án mẫu giữ nguyên. Lần đổi tên khóa hay dạng giá trị về sau sẽ chạm điểm 9. App tắt sao lưu của Android (`android:allowBackup="false"`), nên các khóa không đi theo bản sao lưu hệ thống sang máy khác.
- **Quyền truy cập:** Không thêm. DataStore ghi trong thư mục riêng của app, không cần quyền ở mọi phiên bản từ minSdk 26 tới targetSdk 36.
- **Thư viện và SDK:** Không thêm thư viện mới. Dùng `androidx-datastore-preferences` đã có trong `gradle/libs.versions.toml` (`:core:data` đã khai báo `api`); `:core:logging` là module có sẵn; test dùng `junit4`, `kotlinx-coroutines-test`, `turbine` mà convention plugin đã thêm cho mọi module (`KotlinAndroid.kt`).

### 3.4. Phương án dự phòng

Không có. Mọi kỹ thuật đã có mẫu trong dự án; test trên tệp DataStore thật đã chạy thử (mục 4.1).

### 3.5. Hành vi đã có bị đổi

Không có. Plan chỉ tạo file Kotlin mới và thêm một dòng phụ thuộc vào `core/data/build.gradle.kts`; `DataStoreModule` và các khóa đã có không đổi. `python3 scripts/existing-tests.py` cho plan này in "Không có tên nào để tìm (plan không sửa file Kotlin ngoài test)".

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Không có | Không có | Không có | Không có |

## 4. Kiểm thử

Mọi test là unit test JVM (JUnit 4, `kotlinx-coroutines-test`, Turbine), trong `DataStoreDeviceSettingsRepositoryTest`, không cần Robolectric. Kho dựng là `DataStoreDeviceSettingsRepository(store, logger)` với `logger = RecordingAppLogger()`. `MemoryDataStore(initial)` giữ `state: MutableStateFlow<Preferences>` như bản giả của `FirstOpenMarkTest`, để test đặt sẵn và đọc khóa thô. `FlakyDataStore` ném `IOException` ở `failuresBeforeSuccess` lần thu đầu rồi phát `stored`, như bản giả của `DataStoreCounterRepositoryTest`. "Khóa thô" là bốn khóa của mục 3.1 viết lại ngay trong test bằng chuỗi (`intPreferencesKey("settings_default_frame_rate")`, ...), không lấy từ lớp cài đặt, để đổi tên khóa làm test đỏ.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| KB-1 (trường hợp đặc biệt): chưa lưu gì thì ra mặc định | Unit test | `emptyStore_returnsDefaults_untilWritten`: kho trên `MemoryDataStore()` trống: `defaultFrameRate.first() == 12`, `defaultAspectRatio.first() == "9:16"`, `stylusPressure.first() == true`, `firstStrokeDrawn.first() == false`. Bước đối chứng trong cùng test: `setDefaultFrameRate(24)`, `setDefaultAspectRatio("16:9")`, `setStylusPressure(false)`, `markFirstStrokeDrawn()`, rồi đọc lại ra `24`, `"16:9"`, `false`, `true`; bản cài trả hằng số thì test đỏ |
| Hằng mặc định và danh sách đúng spec | Unit test | `values_matchSpec`: `FRAME_RATES == listOf(6, 8, 12, 15, 24, 30)`, `ASPECT_RATIOS == listOf("16:9", "9:16", "1:1")`, `DEFAULT_FRAME_RATE == 12`, `DEFAULT_ASPECT_RATIO == "9:16"`, `DEFAULT_STYLUS_PRESSURE == true`; `DEFAULT_FRAME_RATE in FRAME_RATES`, `DEFAULT_ASPECT_RATIO in ASPECT_RATIOS` |
| KB-2: chọn mỗi tốc độ hợp lệ thì đọc lại đúng | Unit test | `setDefaultFrameRate_eachAllowedValue_readsBack`: với từng giá trị của `listOf(6, 8, 12, 15, 24, 30)` (viết thẳng trong test), ghi rồi `defaultFrameRate.first()` bằng giá trị đó |
| KB-3: chọn mỗi tỷ lệ hợp lệ thì đọc lại đúng | Unit test | `setDefaultAspectRatio_eachAllowedValue_readsBack`: với `"16:9"`, `"9:16"`, `"1:1"`, ghi rồi đọc lại bằng giá trị đó |
| KB-4: tắt rồi bật lực bút | Unit test | `setStylusPressure_offThenOn_readsBack`: ghi `false` đọc ra `false`, ghi `true` đọc ra `true` |
| Settings câu 9: không nhận tốc độ ngoài danh sách | Unit test | `setDefaultFrameRate_notAllowed_throwsAndKeepsValue`: sau `setDefaultFrameRate(24)`, mỗi giá trị `10`, `0`, `-1`, `60` ném `IllegalArgumentException`, kiểm bằng `val e = runCatching { setDefaultFrameRate(v) }.exceptionOrNull(); assertTrue(e is IllegalArgumentException)`; `defaultFrameRate.first()` vẫn `24`; khóa thô vẫn `24` |
| Settings câu 9: không nhận tỷ lệ ngoài danh sách | Unit test | `setDefaultAspectRatio_notAllowed_throwsAndKeepsValue`: sau `setDefaultAspectRatio("1:1")`, mỗi giá trị `"4:5"`, `""`, `"9:16 "`, `"16x9"` ném `IllegalArgumentException` (kiểm bằng `runCatching { ... }.exceptionOrNull()` rồi `assertTrue(e is IllegalArgumentException)`); đọc lại vẫn `"1:1"`; khóa thô vẫn `"1:1"` |
| Giá trị lạ đã lưu không làm hỏng màn Tạo dự án | Unit test | `storedInvalidValue_readsDefault`: `MemoryDataStore` đặt sẵn khóa thô tốc độ `10`, tỷ lệ `"4:5"`: đọc ra `12`, `"9:16"`. Đối chứng trong cùng test: kho khác đặt sẵn khóa thô `24`, `"1:1"`, `false`, `true` đọc ra `24`, `"1:1"`, `false`, `true` (đọc được giá trị đặt bằng khóa thô, nên test cũng khóa tên khóa khi đọc) |
| Tên khóa ổn định (dữ liệu của người dùng không mất khi đổi code) | Unit test | `writes_useStableKeys`: sau `setDefaultFrameRate(24)`, `setDefaultAspectRatio("16:9")`, `setStylusPressure(false)`, `markFirstStrokeDrawn()`, `store.state.value` có đúng bốn khóa thô với giá trị `24`, `"16:9"`, `false`, `true`, và `asMap().size == 4` |
| Ghi một giá trị không đụng giá trị khác trong tệp dùng chung | Unit test | `write_keepsOtherKeys`: `MemoryDataStore` đặt sẵn `booleanPreferencesKey("ads_first_open_done") to true` và khóa thô tỷ lệ `"1:1"`; sau `setDefaultFrameRate(24)` và `markFirstStrokeDrawn()`, `ads_first_open_done` vẫn `true`, `defaultAspectRatio.first()` vẫn `"1:1"` |
| Mốc nét vẽ đầu tiên một chiều | Unit test | `markFirstStrokeDrawn_isOneWayAndIdempotent`: trước là `false`; gọi lần một trả `true`, đọc ra `true`; gọi lần hai trả `true`, đọc vẫn `true`; `setStylusPressure(false)` sau đó không đổi mốc (vẫn `true`) |
| Luật quảng cáo đổi trong cùng phiên khi có mốc | Unit test | `firstStrokeDrawn_emitsWhileCollecting`: `firstStrokeDrawn.test { awaitItem() == false; markFirstStrokeDrawn(); awaitItem() == true; markFirstStrokeDrawn(); expectNoEvents() }` (Turbine) |
| Màn Cài đặt thấy giá trị mới ngay | Unit test | `defaultFrameRate_emitsWhileCollecting`: `defaultFrameRate.test { awaitItem() == 12; setDefaultFrameRate(24); awaitItem() == 24; setDefaultFrameRate(24); setStylusPressure(false); expectNoEvents() }`: ghi lại cùng giá trị hay ghi khóa khác không phát thêm (nhờ `distinctUntilChanged`) |
| Luồng đọc tự thử lại khi `IOException` | Unit test | `readFailsOnce_emitsDefaultThenStoredValue`: mỗi luồng dùng một `FlakyDataStore(failuresBeforeSuccess = 1, stored = <khóa thô 24, "16:9", false, true>)` riêng và một repository riêng với `RecordingAppLogger()` mới: `defaultFrameRate.take(2).toList() == listOf(12, 24)`, `defaultAspectRatio` ra `listOf("9:16", "16:9")`, `stylusPressure` ra `listOf(true, false)`, `firstStrokeDrawn` ra `listOf(false, true)`; mỗi logger có đúng 1 mục `WARN` thẻ `Settings` với `error is IOException` |
| Đọc lỗi mãi thì không dừng app, ra mặc định, ghi log | Unit test | `readAlwaysFails_returnsDefaultsAndLogs`: trên `UnreadableDataStore`, `first()` của bốn luồng ra `12`, `"9:16"`, `true`, `false`, không ném lỗi; sau một lần `defaultFrameRate.first()` trên một `UnreadableDataStore` mới, với repository mới và `RecordingAppLogger()` mới, logger đó có đúng 1 mục `WARN`, thẻ `Settings`, `error is IOException` |
| Chỉ thử lại khi `IOException` | Unit test | `readFailsWithOtherError_throws`: trên `BrokenDataStore` (luồng ném `IllegalStateException`), `val e = runCatching { repository.defaultFrameRate.first() }.exceptionOrNull(); assertTrue(e is IllegalStateException)`, không có mục `WARN`. Đối chứng của ý định: test `readAlwaysFails_returnsDefaultsAndLogs` ở trên |
| Ghi lỗi thì báo cho ViewModel | Unit test | `setters_writeFails_throwIOException`: trên `UnwritableDataStore`, `setDefaultFrameRate(24)`, `setDefaultAspectRatio("16:9")`, `setStylusPressure(false)` mỗi lần ném lỗi `IOException`, kiểm bằng `val e = runCatching { ... }.exceptionOrNull(); assertTrue(e is IOException)` (so loại lỗi, không so đối tượng); `setDefaultFrameRate(10)` trên cùng kho cho `e is IllegalArgumentException` theo cùng cách (kiểm giá trị trước khi ghi) |
| Ghi mốc lỗi không dừng màn vẽ | Unit test | `markFirstStrokeDrawn_writeFails_returnsFalseAndLogs`: trên `UnwritableDataStore`, `markFirstStrokeDrawn()` trả `false`, không ném lỗi, có đúng 1 mục `WARN` thẻ `Settings` với `error is IOException`. Đối chứng: trên `MemoryDataStore()` trả `true`, không có `WARN` |
| KB-2, KB-3, KB-4: tắt app rồi mở lại thì giá trị còn | Unit test | `realFile_valuesSurviveNewInstance`: tệp `File(tempFolder.root, "app_preferences.preferences_pb")` (`TemporaryFolder` của JUnit); `PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + job1)) { file }`, kho thứ nhất ghi `24`, `"16:9"`, `false` và mốc; `job1.cancelAndJoin()`; kho DataStore thứ hai trên cùng tệp với `job2`, repository mới đọc ra `24`, `"16:9"`, `false`, `true`; `job2.cancelAndJoin()`. Thân test bọc trong `try { ... } finally { job1.cancel(); job2.cancel() }` (và job của kho đối chứng; khai báo `job1`, `job2`, `jobControl = Job()` trước `try`), để test đỏ giữa chừng không để DataStore chạy tiếp. Đối chứng trong cùng test: DataStore trên một tệp khác chưa ghi đọc ra `12`, `"9:16"`, `true`, `false` |
| KB-10 (settings câu 15): giá trị giữ khi cờ tắt | Unit test và đọc code | `realFile_valuesSurviveNewInstance` ở trên cho thấy giá trị chỉ mất khi xóa tệp; `DeviceSettingsRepository` không có hàm xóa và lớp cài đặt không nhận `FeatureFlags` (rà soát độc lập đọc chữ ký hàm dựng). Hành vi dùng 12, 9:16 khi cờ tắt kiểm ở T-011 |
| Luật phụ thuộc module | Lệnh | `scripts/check.sh` chạy `checkModuleDependencies`: `:core:data` chỉ phụ thuộc `:core:logging` (và thư viện), không phụ thuộc `:core:project` |
| Binding Hilt dựng được | Lệnh | `scripts/check.sh` build `:app` (đã phụ thuộc `:core:data`), Hilt kiểm `DeviceSettingsModule` |

**Ngoại lệ viết test trước:** `di/DeviceSettingsModule.kt` và dòng `implementation(project(":core:logging"))` của `core/data/build.gradle.kts`: phần nối và cấu hình, không có hành vi riêng để test trước; build của `scripts/check.sh` kiểm chúng.

Thứ tự commit: bước 3 commit `TestDataStores.kt`, `DataStoreDeviceSettingsRepositoryTest.kt`, dòng phụ thuộc của `build.gradle.kts` và khung tối thiểu để test biên dịch: `DeviceSettingsRepository.kt` đầy đủ (interface và `DeviceSettingsValues` là hợp đồng, không có logic), lớp `DataStoreDeviceSettingsRepository` có bốn luồng và bốn hàm thân `TODO()`. Mọi test của kho đỏ vì `NotImplementedError`; riêng `values_matchSpec` có thể qua ngay vì hằng đã viết ở khung, ghi điều đó vào nhật ký. Bước 4 viết phần cài đặt và `DeviceSettingsModule.kt`.

### 4.1. Chạy thử

Không cần (E dưới 2). Để chắc phần ít gặp nhất (DataStore trên tệp thật trong test JVM, hai phiên bản DataStore lần lượt trên cùng tệp), đã chạy một lần ngắn ngày 2026-11-22 trên worktree tạm từ `origin/main` 757bbad, không commit: bản cài như mục 3.1, `DeviceSettingsModule`, dòng phụ thuộc `:core:logging`, và hai test tạm (`realFile_survivesNewInstance`, `turbine_markFlips` giống `realFile_valuesSurviveNewInstance` và `firstStrokeDrawn_emitsWhileCollecting` của mục 4). Lệnh `./gradlew :core:data:testDebugUnitTest checkModuleDependencies` qua 2/2 test, luật phụ thuộc qua; `./gradlew :app:assembleStagingDebug :core:data:lintDebug` qua (Hilt dựng được binding, lint không báo lỗi). Phiên chạy `scripts/check.sh` như mọi task.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 7 (module core dùng chung `:core:data`). Không chạm điểm 9 (khóa DataStore mới, không có dữ liệu cũ phải chuyển, mục 3.3).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 2 | Nhiều phần trong một module, có giao diện công khai mới (`DeviceSettingsRepository`, `DeviceSettingsValues`) và đổi file build của module |
| B. Độ rõ của yêu cầu | 1 | Spec đã xác nhận, giá trị và mặc định rõ; vài chi tiết AI tự quyết: tên khóa, ghi mốc lỗi trả `false`, đọc lỗi coi là chưa có mốc, giá trị lạ đã lưu ra mặc định (mục 8) |
| C. Khả năng kiểm chứng tự động | 0 | Mọi hành vi có unit test JVM, kể cả giữ giá trị qua lần mở lại trên tệp thật |
| D. Ảnh hưởng chéo | 2 | Sửa file mà task khác trong sprint có thể cũng sửa: T-002 có thể sửa `core/data/build.gradle.kts` khi thêm khóa đếm dự án; tệp `app_preferences` dùng chung với khóa của T-002. Chưa feature nào dùng giao diện mới, nên không đổi hành vi feature khác đang dựa vào |
| E. Độ mới của kỹ thuật | 1 | Biến thể nhỏ của mẫu `DataStoreCounterRepository` và `FirstOpenMark`; test trên tệp DataStore thật là phần mới nhỏ, đã chạy thử (mục 4.1) |
| **Tổng** | 6 | **Mức: Cao** (chạm điểm chặn 7) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `core/data/build.gradle.kts`; tệp DataStore `app_preferences` dùng chung (thêm khóa, không đổi khóa cũ).
- Task khác có thể xung đột:
  - T-002 (kho dự án) dự kiến cho `:core:project` phụ thuộc `:core:data` để lưu số dự án đã từng tạo; plan này giữ `:core:data` không phụ thuộc `:core:project`, nên không thành vòng. Nếu T-002 thêm khóa vào cùng tệp `app_preferences`, khóa đó không được trùng bốn khóa của mục 3.1. T-002 có thể sửa `core/data/build.gradle.kts` hay không tùy plan của nó; nếu có thì danh sách chạy xếp tuần tự.
  - T-011 đổi `defaultAspectRatio` (chuỗi) sang `AspectRatio` của `:core:project` và `defaultFrameRate` (số) sang `FrameRate`; plan T-011 nên có test đổi được mọi phần tử của `DeviceSettingsValues.ASPECT_RATIOS` và `FRAME_RATES`, vì `:core:data` không kiểm được điều này (không phụ thuộc `:core:project`).
  - T-007 chọn chỗ áp luật trước nét vẽ đầu tiên; `:core:ads` đã phụ thuộc `:core:data` nên đọc được `firstStrokeDrawn` mà không thêm phụ thuộc. Luồng ra `false` khi đọc lỗi (mục 3.1).
  - T-019 gọi `markFirstStrokeDrawn()` khi `firstStrokeDrawn` còn `false`, tối đa một lần mỗi lần mở màn vẽ; hàm trả `false` thì không gọi lại tới lần mở màn vẽ sau (KDoc của interface, mục 3.1), để lỗi đọc kéo dài không biến mỗi nét thành một lần ghi lỗi và một dòng log.
  - T-022 dùng `stylusPressure` làm giá trị cho cọ chưa chỉnh riêng. Ticket T-022 ghi `depends_on: [T-021]`, không ghi T-005; chuỗi hiện có là T-022, T-021, T-019, T-005 (T-021 phụ thuộc T-019, T-019 phụ thuộc T-005). Plan T-022 thêm T-005 vào `depends_on` hoặc ghi rõ dựa vào chuỗi này.
  - Plan T-004 và T-006 viết cùng đợt không chạm `:core:data`.
- Đồng bộ từ dự án mẫu: `:core:data` là module của dự án mẫu mà `docs/ops/to-app.md`, mục 2, chưa liệt kê ở nhóm nào. Sau ticket này, `core/data/build.gradle.kts` có phần riêng của app (phụ thuộc `:core:logging`) và `:core:data` có file riêng của app (`settings/`, `di/DeviceSettingsModule.kt`, test của chúng): đợt `scripts/template-sync.sh` phải gộp tay `core/data/build.gradle.kts` và giữ các file mới. Đây là phần riêng của app (cài đặt của Frameloop), không đề xuất về dự án mẫu.
- Đề xuất thứ tự chạy: không phụ thuộc ticket nào, chạy được ngay; phải chạy trước T-007, T-011, T-016, T-019.

## 7. Điều kiện dừng riêng

- Cần cho `:core:data` phụ thuộc `:core:project` hay một module ngoài `:core:logging`: dừng, vì trái quyết định của anh Nhật (plan T-001, câu hỏi 4) và có thể thành vòng với T-002.
- Cần sửa `DataStoreModule.kt` (tên tệp `app_preferences`, cách xử lý tệp hỏng): dừng, vì đổi tên tệp làm mất mọi dữ liệu đã lưu của dự án mẫu và của ticket này.
- Cần đổi tên một trong bốn khóa của mục 3.1 hay kiểu giá trị của nó để test qua: dừng, vì tên khóa là hợp đồng người duyệt đã duyệt.

## 8. Câu hỏi cho người duyệt

1. **Ghi mốc nét vẽ đầu tiên lỗi thì sao?** Ba hàm ghi cài đặt ném `IOException` cho ViewModel báo người dùng, còn `markFirstStrokeDrawn()` tự bắt lỗi, ghi log, trả `false`, để màn vẽ không phải xử lý lỗi giữa lúc vẽ; màn vẽ gọi lại ở lần mở màn vẽ sau (mỗi lần mở gọi tối đa một lần, mục 3.1). Đề xuất của AI: giữ cách này.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Đồng ý đề xuất.
2. **Đọc lỗi thì mốc coi là chưa có.** Khi tệp đọc lỗi, `firstStrokeDrawn` ra `false` tới lần đọc được, nên luật quảng cáo tạm hiện ít quảng cáo hơn; tốc độ, khung hình, lực bút ra giá trị mặc định. Đề xuất của AI: giữ, vì đây là phía an toàn với người dùng.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Đồng ý đề xuất.
3. **Giá trị lạ đã lưu** (ví dụ `"4:5"` hay 10 fps) đọc ra giá trị mặc định, không báo lỗi, không tự sửa tệp. Đề xuất của AI: giữ.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Đồng ý đề xuất.
4. **Tên khóa và tệp.** Bốn khóa `settings_default_frame_rate`, `settings_default_aspect_ratio`, `settings_stylus_pressure`, `first_stroke_drawn` trong tệp `app_preferences` dùng chung, không tạo tệp DataStore riêng (tệp riêng cần thêm một bản cấp phát Hilt có định danh). Đề xuất của AI: giữ; mốc không mang tiền tố `settings_` vì là của màn vẽ.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Đồng ý đề xuất.
5. **`:core:data` thêm phụ thuộc `:core:logging`** để ghi log lần thử lại và lỗi ghi mốc (ADR-0019). Phương án khác: không ghi log trong kho, như `DataStoreCounterRepository`, khi đó lỗi đọc không để lại dấu vết. Đề xuất của AI: thêm phụ thuộc.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Đồng ý đề xuất.
6. **Bảng module của `CLAUDE.md`** vẫn ghi `:core:data` là "DataStore dùng chung". Đề xuất của AI: không sửa trong ticket này, để không cùng sửa `CLAUDE.md` với plan khác trong đợt; tên `DeviceSettingsRepository` và ticket đủ để các plan sau tìm.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Đồng ý đề xuất.
7. **Duyệt chéo khi nhóm một dev.** Plan rủi ro Cao cần duyệt chéo; nếu nhóm chỉ có anh, mục 9 ghi anh vừa duyệt vừa duyệt chéo kèm lý do (risk-scoring.md, mục 3).
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Anh Nhật vừa duyệt vừa duyệt chéo, vì nhóm hiện chỉ có một dev.

## 9. Quyết định duyệt

<!-- Người duyệt điền, hoặc AI ghi theo lời đồng ý trong phiên của người duyệt, nguyên văn (ADR-0048, phần C). Plan `lane: quick`: ghi "Duyệt trong phiên làm nhanh" kèm lời đồng ý. -->

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện
