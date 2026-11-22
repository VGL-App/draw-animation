---
ticket: T-006
title: Cờ tính năng của 6 feature Frameloop
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

# PLAN T-006: CỜ TÍNH NĂNG CỦA 6 FEATURE FRAMELOOP

## 1. Tóm tắt

Thêm 6 dòng vào `FeatureFlag` của `:core:config` cho onboarding, projects, settings, editor, export và lessons, đều mặc định tắt; bản staging bật sẵn 5 cờ, trừ lessons. Bảng khóa `docs/remote-config-keys.md` và file mẫu `docs/remote-config-template.json` sinh lại từ code. Hai khóa tham số của ticket (`time_reload_editor_top_native`, `lessons_content_version`) không khai báo ở plan này, vì quy tắc khóa đặt chúng ở module sở hữu mà ticket khác tạo ra (mục 3.1, câu hỏi 1 và 2 ở mục 8). Kiểm chứng bằng unit test của `:core:config`, một test mới cho `StagingFeatureFlagsModule` và `RemoteConfigKeysTest` có sẵn.

## 2. Hiểu yêu cầu

Ticket không có kịch bản riêng (`scenarios: []`). Tiêu chí lấy từ mục 8 (Cờ tính năng) của từng spec và từ phạm vi của ticket:

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| onboarding mục 8 | `feature_onboarding_enabled`, mặc định tắt; bản staging bật sẵn | Dòng `ONBOARDING` trong `FeatureFlag`; bật trong `StagingFeatureFlagsModule` |
| projects mục 8 | `feature_projects_enabled`, mặc định tắt; bản staging bật sẵn; production bật từ lần phát hành đầu (câu hỏi 18) | Dòng `PROJECTS`; staging bật. Bật trên production là giá trị Firebase, không phải mặc định trong code (ADR-0003 buộc mặc định tắt) |
| settings mục 8 | `feature_settings_enabled`, mặc định tắt; bản staging bật sẵn; production bật từ ngày phát hành (câu hỏi 15) | Dòng `SETTINGS`; staging bật. Production như projects |
| editor mục 8 | `feature_editor_enabled`, mặc định tắt; bản staging bật sẵn; camera ảo đi chung cờ này (câu hỏi 19) | Dòng `EDITOR`; staging bật; không thêm cờ camera |
| export mục 8 | `feature_export_enabled`, mặc định tắt; bản staging bật sẵn | Dòng `EXPORT`; staging bật |
| lessons mục 8 | `feature_lessons_enabled`, mặc định tắt; bản staging bật sẵn khi có nội dung bài mẫu | Dòng `LESSONS`; **không** bật ở staging trong plan này. T-054 bật khi có 2 bài mẫu trên R2 staging |
| Ticket, phạm vi | Cập nhật `docs/remote-config-keys.md`; test có sẵn chỉ cập nhật phần dữ liệu (ADR-0029) | Sinh lại bảng khóa và file mẫu bằng lệnh của `docs/ops/remote-config.md`, mục 3; test có sẵn duyệt `FeatureFlag.entries` nên không phải sửa (mục 3.5) |
| editor mục 8, mục 5 | Tham số `time_reload_editor_top_native`, mặc định 120 giây | Không làm ở plan này: khóa riêng của vị trí `editor_top_native`, suy ra từ khai báo vị trí (MC-03), vị trí do T-008 tạo. Câu hỏi 1 ở mục 8 |
| lessons mục 8, câu hỏi 5 | Khóa `lessons_content_version`, thư mục nội dung bài hiện hành trên R2 | Không làm ở plan này: tham số của module core chứa nội dung bài, module do T-048 chọn và tạo. Câu hỏi 2 ở mục 8 |

Thiết kế tham chiếu: Không có thiết kế riêng (cờ và cấu hình, không có giao diện; docs/ops/design-workflow.md)

ADR liên quan: ADR-0003 (cờ tính năng mặc định tắt, đọc bằng `Flow`), ADR-0012 (Firebase, staging dùng file giả), ADR-0046 (khóa phẳng có kiểu, khai báo một chỗ ở module sở hữu, sinh bảng khóa), ADR-0045 mục 6 và `docs/ops/ads-business-rules.md` MC-03 (khóa riêng của vị trí quảng cáo suy ra theo mẫu), ADR-0029 (cập nhật dữ liệu của test cũ), ADR-0001 (core chỉ phụ thuộc core).

**Mâu thuẫn giữa ticket và quy tắc (ghi theo `docs/CLAUDE.md`, mục Đọc trước khi làm):** ticket ghi "khai báo một lần trong `:core:config`" cả hai khóa tham số. Quy tắc nói khác:

- ADR-0046, quyết định 3: mỗi khóa khai báo đúng một chỗ, trong code của module sở hữu nó. `docs/ops/remote-config.md`, mục 3: tham số khác cờ là "một `RemoteKey` trong module core sở hữu". KDoc của `RemoteSettings` nói rõ hơn: "keys are declared as [RemoteKey] by the core module that uses them" và "Feature modules do not use this". `:core:config` không đọc hai khóa này (`:core:ads` đọc nhịp tải lại, module nội dung bài đọc phiên bản nội dung), nên `:core:config` không phải module sở hữu.
- `time_reload_editor_top_native` có dạng `<tham số>_<mã vị trí>`: theo MC-03 và `remote-config.md` mục 2, khóa riêng của vị trí suy ra từ khai báo vị trí trong `AdPlacement`, không đặt tay. Vị trí `editor_top_native` chưa có; T-008 tạo nó cùng định dạng 56 dp và phần tải lại.
- Đặt `time_reload_editor_top_native` vào `:core:config` làm đỏ `RemoteConfigKeysTest.everyNumberKey_endsWithUnit`: khóa số ngoài `:core:ads` phải kết thúc bằng `_seconds`, `_count` hay `_percent`, mà tên khóa đã chốt trong spec editor.
- `lessons_content_version` mang tiền tố `lessons_`, tức tham số của module nội dung bài (`<module>_<tham số>`); T-048 ghi "plan chọn module". Khai báo trước ở plan này là chọn thay T-048 module và giá trị mặc định (đường dẫn trên R2, điểm chặn 8).

Plan đề xuất phạm vi chỉ gồm 6 cờ, và để hai khóa cho T-008 và T-048. Người duyệt quyết ở câu hỏi 1 và 2, mục 8.

## 3. Giải pháp

### 3.1. Cách làm

Làm theo đúng mẫu có sẵn của `FeatureFlag.SAMPLE` và `FeatureFlag.LOADING`:

1. `FeatureFlag.kt`: thêm 6 dòng sau `LOADING`, theo thứ tự các feature, mô tả một câu tiếng Anh (hiện trên Firebase và bảng khóa, dưới 256 ký tự), `defaultValue` để mặc định `false`:

   | Dòng enum | `key` | `description` |
   | --- | --- | --- |
   | `ONBOARDING` | `feature_onboarding_enabled` | `Shows onboarding to users who have not finished it; off opens the project list directly.` |
   | `PROJECTS` | `feature_projects_enabled` | `Opens the project list and the Create project screen.` |
   | `SETTINGS` | `feature_settings_enabled` | `Shows the settings button on the project list and opens the Settings screen, where users withdraw ad consent.` |
   | `EDITOR` | `feature_editor_enabled` | `Lets users open the drawing screen, virtual camera included.` |
   | `EXPORT` | `feature_export_enabled` | `Shows the export button on the drawing and lesson complete screens and opens the Export screen.` |
   | `LESSONS` | `feature_lessons_enabled` | `Shows the Lessons tab, the free lesson button on the empty project list and the Lessons screen.` |

   `remoteKey` của mỗi dòng tự có qua `RemoteKey.boolean(key, defaultValue, description)`. `SAMPLE` và `LOADING` giữ nguyên: `:feature:sample` và `:feature:loading` vẫn có trong `settings.gradle.kts` và đọc hai cờ này (`SampleViewModel`, `LoadingViewModel`).
2. `StagingFeatureFlagsModule.kt`: thêm 5 hàm `@Provides @IntoSet @LocallyEnabledFlags` như `sample()` và `loading()`: `onboarding()`, `projects()`, `settings()`, `editor()`, `export()`. Không thêm `lessons()`; KDoc của lớp ghi một câu: cờ lessons bật khi có nội dung bài mẫu (T-054, spec lessons mục 8).
3. `AppRemoteKeys` không phải sửa: dòng `":core:config" to FeatureFlag.entries.map { it.remoteKey }` tự gồm cờ mới.
4. Sinh lại hai file bằng `./gradlew :app:testStagingDebugUnitTest -PremoteConfigKeys.update=true` (`docs/ops/remote-config.md`, mục 3), rồi `git diff -- docs/remote-config-keys.md docs/remote-config-template.json`: chỉ có 6 dòng bảng mới và 6 tham số mới trong file mẫu, mỗi khóa `false`, module `:core:config`.

Nơi đọc các file bị đổi (`grep -rn` trên `scripts/ .github/ */src/test */src/testStaging */src/androidTest */*/src/test */*/src/androidTest`, tức cả `core/*/src/test` và `feature/*/src/test`):

- `docs/remote-config-keys.md`, `docs/remote-config-template.json`: `app/src/test/kotlin/com/vgl/draw/animation/config/RemoteConfigKeysTest.kt` (so hai file với code; không sửa, vì câu khẳng định vẫn đúng sau khi sinh lại, xem mục 3.5); `app/build.gradle.kts` (truyền đường dẫn cho test, không đổi); `scripts/main-guard-verified.sh` (`GRADLE_READ_DOCS`, chỉ phân loại tệp là code, không đọc nội dung); `scripts/tests/run.sh` (dùng tệp giả trong thư mục tạm, và các câu `ads-plan` trên bảng thật chỉ đếm vị trí quảng cáo và nhóm của khóa `time_reload_*`, không đổi khi thêm cờ).
- `scripts/ads-plan.py` đọc `docs/remote-config-keys.md`: khóa không có trong `KNOWN_KEYS` vào nhóm "Khóa của app" với mặc định và mô tả từ bảng, nên 6 cờ mới tự hiện, không phải sửa script (script của dự án mẫu). Giá trị phát hành của `feature_projects_enabled` và `feature_settings_enabled` là `true` theo spec: nhóm điền ở cột Giá trị phát hành của file Excel, không ghi vào code (mục 4, dòng người).
- `FeatureFlag`: `DefaultFeatureFlagsTest` và `RemoteConfigKeysTest.everyFeatureFlagAndPlacement_isListed` duyệt `FeatureFlag.entries` (mục 3.5). Không test nào quét mã nguồn tìm chuỗi `feature_..._enabled`. Không test emulator nào trong `app/src/androidTest` đọc `FeatureFlag`.
- `StagingFeatureFlagsModule`: chưa có test; plan tạo `StagingFeatureFlagsModuleTest`.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `core/config/src/main/kotlin/com/vgl/draw/animation/core/config/FeatureFlag.kt` | Thêm 6 dòng `ONBOARDING`, `PROJECTS`, `SETTINGS`, `EDITOR`, `EXPORT`, `LESSONS` với khóa và mô tả của mục 3.1 |
| Sửa | `core/config/src/test/kotlin/com/vgl/draw/animation/core/config/DefaultFeatureFlagsTest.kt` | Thêm 3 test của mục 4 (commit test của bước 3; `sampleAndLoadingFlags_stayDeclared` là test giữ hành vi, qua ngay) |
| Sửa | `app/src/staging/kotlin/com/vgl/draw/animation/di/StagingFeatureFlagsModule.kt` | Thêm 5 hàm bật cờ; KDoc ghi cờ lessons để T-054 |
| Tạo | `app/src/testStaging/kotlin/com/vgl/draw/animation/di/StagingFeatureFlagsModuleTest.kt` | Test tập cờ bật sẵn của staging (mục 4) |
| Sửa | `docs/remote-config-keys.md` | Sinh lại bằng lệnh của mục 3.1, bước 4; không sửa tay |
| Sửa | `docs/remote-config-template.json` | Sinh lại cùng lệnh; không sửa tay |

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: thêm `feature_onboarding_enabled`, `feature_projects_enabled`, `feature_settings_enabled`, `feature_editor_enabled`, `feature_export_enabled`, `feature_lessons_enabled`, mặc định tắt; staging bật sẵn 5 cờ đầu. Chưa màn nào đọc các cờ này: hành vi khi cờ tắt thuộc ticket của từng màn (ticket, Không bao gồm).
- Quảng cáo: Không chạm. Không thêm vị trí, không thêm khóa quảng cáo (`time_reload_editor_top_native` để T-008, câu hỏi 1).
- Sự kiện phân tích: Không chạm. Khóa Remote Config không phải sự kiện phân tích.
- File R2: Không chạm (`lessons_content_version` để T-048, câu hỏi 2).
- **Quyền truy cập:** Không thêm
- **Thư viện và SDK:** Không thêm. Test mới dùng JUnit 4 đã có trên classpath test của `:app` (như `StagingRemoteValuesModuleTest`), reflection của Java (`Class.declaredMethods`, đã dùng trong `FullScreenAdGuardTest` của `:core:ads`) và chú thích `dagger.Provides`, `dagger.multibindings.IntoSet` mà `:app` đã có (Hilt), cũng nằm trên classpath unit test.

### 3.4. Phương án dự phòng

Không có. Điểm E là 0: lặp lại đúng mẫu của `SAMPLE`, `LOADING` và `StagingRemoteValuesModuleTest`.

### 3.5. Hành vi đã có bị đổi

Kết quả `scripts/existing-tests.py` cho plan này: xem dòng cuối của mục. Mọi test có sẵn đọc `FeatureFlag` hay bảng khóa đều duyệt danh sách từ code, không có danh sách dòng enum viết tay, nên không test nào phải cập nhật dữ liệu theo ADR-0029.

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| `FeatureFlag` có 2 dòng | `FeatureFlag` có 8 dòng, 6 dòng mới mặc định tắt | `DefaultFeatureFlagsTest.everyFlag_isOffByDefault`, `withoutLocalOrRemoteValues_everyFlagIsOff`, `everyFlag_remoteKeyMatchesKeyAndDefault`, `everyFlagKey_followsNamingRule` | Giữ nguyên, không sửa dữ liệu: các test duyệt `FeatureFlag.entries`, nên tự kiểm cả 6 dòng mới |
| Cờ `SAMPLE` bật cục bộ, theo giá trị từ xa | Không đổi | `DefaultFeatureFlagsTest.locallyEnabledFlag_isOn`, `locallyEnabledFlag_staysOnWhenRemoteSaysOff`, `remoteValueTrue_turnsFlagOn`, `remoteValueActivatedLater_reachesCollectors` | Giữ nguyên |
| Bảng khóa và file mẫu có 2 cờ | Có 8 cờ, sinh lại bằng lệnh của mục 3.1, bước 4 | `RemoteConfigKeysTest.keysFile_matchesCode`, `templateFile_matchesCode` | Giữ nguyên câu khẳng định; hai file sinh lại trong commit code của bước 4 (đây là dữ liệu sinh từ code, không phải sửa test) |
| Mọi cờ có trong `AppRemoteKeys` | Không đổi, cờ mới tự vào qua `FeatureFlag.entries` | `RemoteConfigKeysTest.everyFeatureFlagAndPlacement_isListed`, `everyKeyName_isUniqueAcrossModules` | Giữ nguyên, không sửa dữ liệu |
| Khóa số ngoài `:core:ads` có đơn vị ở cuối tên | Không đổi: plan không thêm khóa số | `RemoteConfigKeysTest.everyNumberKey_endsWithUnit` | Giữ nguyên. Đây là một lý do không đặt `time_reload_editor_top_native` vào `:core:config` (mục 2) |
| Staging chỉ có giá trị cục bộ cho khóa quảng cáo | Không đổi: plan không thêm giá trị cục bộ | `StagingRemoteValuesModuleTest.everyLocalValue_isADeclaredKey` | Giữ nguyên |
| Màn mẫu và màn loading đọc cờ `SAMPLE`, `LOADING` qua `FeatureFlags` | Không đổi: hai dòng cũ giữ nguyên tên và khóa | `LoadingRouteTest.finishFromBackgroundThread_navigatesOnMainThread`, `LoadingViewModelTest.awaitThrows_finishesWithoutAd`, `SampleViewModelTest.whenFlagTurnsOnLater_thenScreenBecomesReady` | Giữ nguyên, không sửa dữ liệu: các test dùng `FeatureFlags` giả trả cùng giá trị cho mọi cờ, không liệt kê dòng enum, và không code nào có `when` duyệt hết `FeatureFlag` |

`scripts/existing-tests.py` (chạy lại 2026-11-22, sau khi sửa theo rà soát): 77 test dùng tên của các file plan sửa; nhóm "Cần xem trước" 0 test; nhóm "Nên xem" 0 test chưa nhắc (3 test của lần chạy đầu, ghi ở dòng cuối của bảng, nay đã được tính là plan có nhắc); plan nhắc 12 test; 65 test còn lại chỉ dùng tên plan không đổi (`FeatureFlags`, `LocallyEnabledFlags`).

## 4. Kiểm thử

Test viết trước, trong commit test của bước 3. Câu khẳng định đọc khóa dạng chuỗi, không tham chiếu tên dòng enum mới, nên test biên dịch được trên code hiện tại và đỏ khi chạy, đúng lý do "chưa có cờ".

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| Mục 8 của 6 spec: đủ 6 cờ, đúng tên, mỗi khóa đúng một dòng | Unit test | `DefaultFeatureFlagsTest.frameloopFlags_areDeclaredOnceWithSpecKeys`: với mỗi khóa trong `listOf("feature_onboarding_enabled", "feature_projects_enabled", "feature_settings_enabled", "feature_editor_enabled", "feature_export_enabled", "feature_lessons_enabled")`, `assertEquals(key, 1, FeatureFlag.entries.count { it.key == key })`; thêm `assertEquals(FeatureFlag.entries.size, FeatureFlag.entries.map { it.key }.toSet().size)` (không trùng khóa). Đỏ trên code hiện tại: số đếm là 0 |
| Cờ của màn mẫu và màn loading còn (không bị thay khi thêm cờ mới) | Unit test | `DefaultFeatureFlagsTest.sampleAndLoadingFlags_stayDeclared`: `assertTrue(FeatureFlag.entries.any { it.key == "feature_sample_enabled" })`, tương tự `feature_loading_enabled`. Câu riêng, để sau này bỏ cờ mẫu chỉ phải bỏ test này, không đụng test 6 cờ. Qua cả trên code hiện tại: là test giữ hành vi, không phải test viết trước |
| Mục 8: mỗi cờ mới theo giá trị từ xa, bật rồi tắt lại được; giá trị của cờ này không lọt sang cờ khác | Unit test (Turbine) | `DefaultFeatureFlagsTest.eachFrameloopFlag_followsOnlyItsOwnRemoteValue`: với mỗi khóa trong 6 khóa mới, `val flag = FeatureFlag.entries.single { it.key == key }`; `flags().enabled(flag).test { assertFalse(awaitItem()); remote.booleans.value = mapOf(key to true); assertTrue(awaitItem()); remote.booleans.value = emptyMap(); assertFalse(awaitItem()) }`; đối chứng: khi `remote.booleans.value = mapOf(key to true)`, mọi cờ khác trong `FeatureFlag.entries` vẫn `false` (`flags().enabled(other).first()`). Đỏ trên code hiện tại: `single` ném `NoSuchElementException` |
| Mục 8: mặc định tắt khi chưa có giá trị nào | Unit test có sẵn | `DefaultFeatureFlagsTest.everyFlag_isOffByDefault`, `withoutLocalOrRemoteValues_everyFlagIsOff` (duyệt `entries`). Bước đối chứng: test `eachFrameloopFlag_followsOnlyItsOwnRemoteValue` ở trên cho thấy cùng cờ đó bật được khi có giá trị `true`, nên "tắt" không phải do cờ không đọc được |
| Staging bật sẵn onboarding, projects, settings, editor, export; lessons không bật | Unit test (`testStaging`) | `StagingFeatureFlagsModuleTest.stagingEnablesFiveFrameloopFlags_lessonsStaysOff`: lấy các hàm cung cấp vào tập: `val providers = StagingFeatureFlagsModule::class.java.declaredMethods.filter { it.isAnnotationPresent(Provides::class.java) && it.isAnnotationPresent(IntoSet::class.java) }` (`@Provides` và `@IntoSet` giữ tới lúc chạy; `@LocallyEnabledFlags` giữ `BINARY` nên reflection không đọc được, không lọc theo nó); `val keys = providers.map { (it.invoke(StagingFeatureFlagsModule) as FeatureFlag).key }`. Khẳng định: mọi hàm của module trả `FeatureFlag` đều mang cả `@Provides` và `@IntoSet` (lấy `val methods = StagingFeatureFlagsModule::class.java.declaredMethods` một lần, lọc `providers` từ `methods`, rồi `methods.filter { it.returnType == FeatureFlag::class.java }.toSet()` bằng `providers.toSet()`, vì JVM không bảo đảm thứ tự mảng giữa hai lần gọi); `keys.toSet()` bằng `setOf("feature_sample_enabled", "feature_loading_enabled", "feature_onboarding_enabled", "feature_projects_enabled", "feature_settings_enabled", "feature_editor_enabled", "feature_export_enabled")` (phép so bằng đã loại `feature_lessons_enabled`, không cần câu riêng, để T-054 chỉ thêm một khóa vào tập); đối chứng: `keys` không rỗng và chứa `feature_sample_enabled` (reflection đọc đúng module), `keys.size == keys.toSet().size` (không bật trùng). Đỏ trên code hiện tại: tập chỉ có 2 khóa |
| Ticket: bảng khóa và file mẫu khớp code | Unit test có sẵn | `RemoteConfigKeysTest.keysFile_matchesCode`, `templateFile_matchesCode`: đỏ sau khi sửa `FeatureFlag.kt` tới khi sinh lại hai file bằng lệnh của mục 3.1, bước 4; xanh sau khi sinh lại |
| Toàn bộ | Cổng chất lượng | `scripts/check.sh` |
| Giá trị phát hành trên production: `feature_projects_enabled` và `feature_settings_enabled` là `true` từ lần phát hành đầu (projects câu hỏi 18, settings câu hỏi 15) | Kiểm tra thủ công (người: việc vận hành Firebase, không phải chạy app) | Dev chạy `scripts/ads-plan.py`, điền `true` ở cột Giá trị phát hành của hai khóa này trong sheet Remote Config, các cờ khác theo kế hoạch phát hành; đặt giá trị trên Firebase production là việc của người (ADR-0046, mục 6). Dòng này không chặn nghiệm thu T-006: là việc chuẩn bị phát hành |

Không có bước kiểm trên máy: chưa màn nào đọc 6 cờ mới, nên bản staging không đổi gì người dùng thấy. Năm cờ mới bật sẵn ở `StagingFeatureFlagsModule` luôn bật trên staging, Firebase staging không tắt được (`docs/ops/remote-config.md`, mục 4): nhánh "cờ tắt" của từng màn kiểm bằng test của ticket màn đó, không bằng bước tay trên staging.

**Ngoại lệ viết test trước:** hai file sinh `docs/remote-config-keys.md` và `docs/remote-config-template.json` là kết quả của lệnh sinh, không viết test trước riêng; `RemoteConfigKeysTest` có sẵn đã kiểm chúng. `sampleAndLoadingFlags_stayDeclared` giữ hành vi đã có nên qua ngay trên code hiện tại, không có bước đỏ.

### 4.1. Chạy thử

Không cần (E dưới 2).

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** điểm 7 (module core dùng chung `:core:config`). Không chạm điểm chặn khác: không thêm vị trí hay khóa quảng cáo (điểm 1), không SDK (2), không quyền (3), không sự kiện phân tích (4; khóa Remote Config không phải sự kiện), không đổi hộp thoại đồng ý hay thứ tự khởi tạo SDK (5), không liên kết ngoài (6), không đường dẫn R2 (8; `lessons_content_version` để T-048), không đổi lược đồ dữ liệu (9). Nếu người duyệt chọn đưa hai khóa tham số vào plan này (câu hỏi 1, 2), plan bản 2 chạm thêm điểm 1 (khóa nhịp tải lại quảng cáo trong `:core:ads`) và điểm 8.

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 3 | Hai module (`:core:config`, `:app` bản staging) cộng hai file tài liệu sinh từ code; đổi enum công khai `FeatureFlag` |
| B. Độ rõ của yêu cầu | 2 | Spec của 6 feature đã chốt, nhưng ticket mâu thuẫn quy tắc khóa ở hai khóa tham số; phạm vi chờ câu hỏi 1, 2 |
| C. Khả năng kiểm chứng tự động | 1 | Mọi tiêu chí của code có unit test; còn một dòng thủ công (giá trị phát hành trên Firebase production) |
| D. Ảnh hưởng chéo | 2 | `FeatureFlag.kt`, `StagingFeatureFlagsModule.kt` và hai file bảng khóa cũng bị T-054, T-007, T-008, T-048 sửa sau |
| E. Độ mới của kỹ thuật | 0 | Lặp đúng mẫu `SAMPLE`, `LOADING`, `StagingRemoteValuesModuleTest` |
| **Tổng** | 8 (để tham khảo) | **Mức: Cao** (điểm chặn 7) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: enum `FeatureFlag` (mọi feature đọc), `StagingFeatureFlagsModule` (bản staging), `docs/remote-config-keys.md` và `docs/remote-config-template.json` (sinh từ `AppRemoteKeys`, `scripts/ads-plan.py` đọc).
- Task khác có thể xung đột:
  - T-054 thêm `lessons()` vào `StagingFeatureFlagsModule`, sau plan này; T-054 cập nhật dữ liệu của `StagingFeatureFlagsModuleTest` (chỉ thêm `feature_lessons_enabled` vào tập so bằng), đúng loại cập nhật dữ liệu ADR-0029 cho phép; plan T-054 nên ghi điều này ở mục 3.5.
  - T-007 (22 vị trí quảng cáo), T-008 (vị trí `editor_top_native` và, theo câu hỏi 1, khóa `time_reload_editor_top_native`), T-048 (theo câu hỏi 2, `lessons_content_version`) sinh lại cùng hai file bảng khóa. Hai file sinh từ code nên xung đột được giải bằng cách chạy lại lệnh sinh trên nhánh mới nhất.
  - T-004, T-005 (cùng đợt) không chạm file của plan này.
  - T-009, T-018, T-048, T-007 ghi `depends_on: T-006`.
- Đề xuất thứ tự chạy: chạy sớm, trước T-007, T-009, T-018, T-048; không phụ thuộc task nào (`depends_on: []`).

## 7. Điều kiện dừng riêng

- Lệnh sinh lại bảng khóa đổi dòng nào khác ngoài 6 dòng `feature_*_enabled` mới (trong `docs/remote-config-keys.md`) hay 6 tham số mới (trong `docs/remote-config-template.json`): dừng, vì bảng khóa trên nhánh chính đã lệch code và cần người xem.
- Cần đổi `AppRemoteKeys`, `RemoteSettings`, `DefaultFeatureFlags` hay cách `LocallyEnabledFlags` hoạt động để test qua: dừng, vì plan chỉ thêm dữ liệu.

## 8. Câu hỏi cho người duyệt

1. **`time_reload_editor_top_native` đặt ở đâu?** Ticket ghi khai báo trong `:core:config`; quy tắc (MC-03, `docs/ops/remote-config.md` mục 2 và 3, ADR-0046 quyết định 3) đặt nó là khóa riêng của vị trí `editor_top_native`, suy ra từ khai báo vị trí trong `:core:ads`, và `RemoteConfigKeysTest.everyNumberKey_endsWithUnit` không cho khóa số này ở `:core:config`. AI đề xuất: chuyển khóa sang T-008, nơi tạo vị trí `editor_top_native` và phần tải lại (thêm một thuộc tính nhịp tải lại vào khai báo vị trí, khóa suy ra như `AdsSettings.nativeEvery`, mặc định 120 giây). Cách khác: plan bản 2 khai báo tay khóa này trong `AdsSettings` ngay ở T-006 (chạm điểm chặn 1, lệch MC-03 vì vị trí chưa có).
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Chuyển sang T-008, theo đề xuất của AI.
2. **`lessons_content_version` đặt ở đâu?** Khóa là tham số `<module>_<tham số>` của module core chứa nội dung bài, mà T-048 chọn và tạo ("plan chọn module"); mặc định là thư mục trên R2 (điểm chặn 8), cũng do T-048 quyết. AI đề xuất: chuyển khóa sang T-048, khai báo trong module đó và thêm module vào `AppRemoteKeys`. Cách khác: plan bản 2 khai báo trước trong `:core:config`, nhưng `:core:config` không phải module đọc khóa này, nên không phải module sở hữu (ADR-0046, quyết định 3).
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Chuyển sang T-048, theo đề xuất của AI.
3. **Điều kiện duyệt nếu đồng ý câu 1 và 2:** sửa ticket trong PR của đợt, trước khi danh sách chạy được duyệt; không thì phiên rà soát của task so với mô tả cũ của T-006 (còn hai khóa) và báo lệch. Cụ thể: T-006 bỏ hai khóa khỏi mô tả; T-008 thêm một dòng phạm vi "khai báo khóa `time_reload_editor_top_native`, mặc định 120 giây, suy ra từ khai báo vị trí `editor_top_native`"; T-048 thêm khóa `lessons_content_version` vào phạm vi và xem lại `depends_on: [T-001, T-006]` (T-048 phụ thuộc T-006 chỉ vì khóa này, nên có thể bỏ T-006). Người duyệt xác nhận AI làm các sửa này trong PR của đợt.
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): Đồng ý; AI đã sửa ticket T-006, T-008, T-048 trong PR của đợt.
4. **Spec nói "production bật từ lần phát hành", ADR-0003 buộc mọi cờ mặc định tắt khi chưa có cấu hình.** Lần mở đầu trên production mà `RemoteConfigRefresher` hết 10 giây (mạng chậm, Firebase lỗi), hai cờ đọc mặc định `false`:
   - Trả lời (anh Nhật, 2026-11-22, chọn trong phiên): (b) Ticket riêng cho lối vào tùy chọn quyền riêng tư khi cờ settings tắt; T-009 và vỏ app quyết màn khi cờ projects tắt. Không chặn đợt này.
   - `feature_settings_enabled` (nặng hơn): người dùng mất nút Cài đặt, lối vào duy nhất để rút đồng ý quảng cáo (spec settings, câu hỏi 15), trong khi quảng cáo vẫn có thể hiện.
   - `feature_projects_enabled`: danh sách dự án tắt.
   Plan này không đổi hai điều đó (test `everyFlag_isOffByDefault` giữ ADR-0003). Người duyệt chọn: (a) chấp nhận khoảng trống tới lần tải cấu hình sau; hay (b) giao một ticket khác cho lối vào tùy chọn quyền riêng tư (privacy options) khi cờ settings tắt, và để T-009 cùng vỏ app quyết màn hình khi cờ projects tắt. AI đề xuất (b), vì khoảng trống ở settings là chuyện tuân thủ đồng ý quảng cáo, không chỉ trải nghiệm.

## 9. Quyết định duyệt

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện
