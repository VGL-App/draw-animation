---
ticket: T-056
title: Nút lựa chọn quyền riêng tư quảng cáo trên thanh trên khi cờ Cài đặt tắt
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 5.0M         # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [consent]   # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-056: NÚT LỰA CHỌN QUYỀN RIÊNG TƯ QUẢNG CÁO TRÊN THANH TRÊN KHI CỜ CÀI ĐẶT TẮT

## 1. Tóm tắt

Khi cờ `feature_settings_enabled` tắt, thanh trên của danh sách dự án đặt `PrivacyOptionsButton` của `:core:ads` vào chỗ nút cài đặt. Nút này tự quyết theo vùng: chỉ hiện khi UMP báo lối vào quyền riêng tư là bắt buộc, bấm thì mở lại hộp thoại quyền riêng tư dùng chung. `ProjectsScreen` nhận nút qua một tham số composable (mặc định `PrivacyOptionsButton`), như `bottomAd` của `:feature:sample`, nên test truyền nút giả và kiểm được cả ba trường hợp, kể cả cờ đổi giữa chừng.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/projects/spec.md`:

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-11 | Khi cờ của feature Cài đặt tắt mà người dùng ở vùng bắt buộc hộp thoại đồng ý quảng cáo thì thanh trên hiện nút lựa chọn quyền riêng tư quảng cáo ở chỗ nút cài đặt | Cờ `SETTINGS` tắt thì thanh trên gọi khe quyền riêng tư ở đúng chỗ của nút cài đặt (sau nút nhập file, sát mép phải). Khe mặc định là `PrivacyOptionsButton()`, nút này chỉ vẽ khi `AdsController.privacyOptionsRequired` là `true`. Đó là cách duy nhất `:core:ads` cho feature biết "vùng bắt buộc" (ADR-0045, mục 3.5 và 4.1; `ads-business-rules.md`, S-14); feature không đọc `AdsController`. |
| KB-11 | Bấm thì hộp thoại lựa chọn quyền riêng tư quảng cáo dùng chung của app mở lại, như dòng "Ad privacy choices" của màn Cài đặt (spec settings KB-7) | Bấm nút gọi `AdsController.showPrivacyOptionsForm(activity)` bên trong `PrivacyOptionsButton`. Phần đóng mở cổng đồng ý, chặn quảng cáo tự hiện khi form mở (S-16), rút hay cấp lại đồng ý (S-14) là của `:core:ads`, đã có test ở `ConsentCoordinatorTest`. Plan này không đổi phần đó. |
| KB-11 | Ở vùng không bắt buộc thì không hiện nút nào (câu hỏi 23) | `PrivacyOptionsButton` không vẽ gì khi không bắt buộc, và khi không có `LocalAdsController` (test, preview). Thanh trên khi đó chỉ còn chữ "Frameloop" và nút nhập file. |
| KB-11 | Khi cờ của feature Cài đặt tắt thì thanh trên không có nút cài đặt (tiêu chí đã có, T-009) | Giữ nguyên. Cờ bật thì nút cài đặt như cũ và khe quyền riêng tư không được gọi. |
| Ticket, Phạm vi | Theo dõi cờ là một `Flow`: cờ đổi giữa chừng thì nút đổi theo | `ProjectsViewModel` đã `combine` `featureFlags.enabled(FeatureFlag.SETTINGS)` vào `uiState` (`showSettingsButton`). Thanh trên đọc `showSettingsButton` mỗi lần vẽ, nên cờ đổi thì nút đổi. Plan thêm test cho cả hai chiều đổi ở ViewModel và ở `ProjectsRoute` thật. |

Thiết kế tham chiếu: `features/projects/design/README.md`. Thiết kế không có trạng thái "cờ Cài đặt tắt"; nút dùng nguyên dáng của `PrivacyOptionsButton` (nút chữ Material `TextButton`, chữ "Privacy settings" của `:core:ads`) (câu hỏi 1). Không có ảnh thiết kế mới; mọi ảnh trong `design/screens/` thuộc T-009, T-011, T-012, T-013, T-014.

ADR liên quan: ADR-0003 (cờ là `Flow`, mặc định tắt), ADR-0045 (mục 3.5 `PrivacyOptionsButton`, mục 4.1 feature chỉ dùng composable của `:core:ads`), ADR-0020 (screenshot test), ADR-0031 (mục 3.5).

## 3. Giải pháp

### 3.1. Cách làm

1. **`ProjectsScreen.kt`**:
   - `ProjectsScreen` thêm tham số cuối `privacyOptionsButton: @Composable (Modifier) -> Unit = { PrivacyOptionsButton(it) }` (import `com.vgl.draw.animation.core.ads.PrivacyOptionsButton`). KDoc: "Shown in place of the settings button when the Settings flag is off. The default renders only where the consent law requires the privacy options entry; tests pass a fake."
   - `ProjectsTopBar` nhận thêm `privacyOptionsButton`. Nhánh `if (showSettingsButton) { IconButton(...) }` thêm `else { privacyOptionsButton(Modifier.testTag(ProjectsTestTags.PRIVACY)) }`. Vị trí như nút cài đặt: sau nút nhập file, trong cùng `Row`, mép phải theo `padding(end = Spacing.xs)` sẵn có. Thanh trên đổi `.height(TopBarHeight)` thành `.heightIn(min = TopBarHeight)`: ở cỡ chữ thường vẫn cao 64 dp (mọi ảnh chuẩn có sẵn qua, mục 4.1), ở cỡ chữ lớn thanh cao theo nút để chữ của nút không bị cắt (câu hỏi 1). Không đổi khoảng cách nào khác.
   - Hai chỗ gọi `ProjectsTopBar` (trạng thái `Empty` và `List`) truyền `privacyOptionsButton`.
   - `ProjectsTestTags` thêm `const val PRIVACY = "projects_privacy"`.
   - `ProjectsRoute` thêm tham số `privacyOptionsButton: @Composable (Modifier) -> Unit = { PrivacyOptionsButton(it) }`, đặt cuối danh sách, truyền thẳng xuống `ProjectsScreen`. `ProjectsNavigation.kt` không đổi: dùng mặc định.
2. **Không đổi** `ProjectsViewModel.kt`, `ProjectsUiState.kt`: `showSettingsButton` đã theo cờ `SETTINGS` qua `combine` (T-009). Không thêm chữ: chữ của nút là `R.string.ads_privacy_options` của `:core:ads`.
3. **Không ghi log mới**: feature không biết nút có vẽ hay không (quyết định nằm trong `:core:ads`). `ConsentCoordinator.showPrivacyOptions` đã ghi `Privacy options form opened` và kết quả rút đồng ý (ADR-0019).
4. **Test**: nút giả trong test là `TextButton(onClick = ..., modifier = it) { Text(stringResource(com.vgl.draw.animation.core.ads.R.string.ads_privacy_options)) }`, đúng cách `PrivacyOptionsButton` vẽ (`AdSlot.kt`, dòng 405 tới 415), để ảnh và số đo giống nút thật. Đặt hàm `@Composable fun FakePrivacyButton(modifier: Modifier = Modifier, onClick: () -> Unit = {})` dùng chung trong tệp test mới `FakePrivacyButton.kt`; bản giả truyền `modifier` cho `TextButton`, để tag `PRIVACY` mà thanh trên gắn vào còn trên node.

Mẫu làm theo: khe composable truyền vào có mặc định là composable của `:core:ads`, test truyền bản giả (`bottomBar`/`NativeAdCard` của `:feature:sample`, `CLAUDE.md` bước 7).

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreen.kt` | Tham số `privacyOptionsButton` của `ProjectsRoute` và `ProjectsScreen`, nhánh `else` trong `ProjectsTopBar`, thanh trên `heightIn(min = TopBarHeight)`, tag `PRIVACY` |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/FakePrivacyButton.kt` | Nút giả vẽ như `PrivacyOptionsButton` (mục 3.1, ý 4) |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreenTest.kt` | Thêm 7 test của mục 4; `show()` nhận thêm tham số `privacyOptionsButton: (@Composable (Modifier) -> Unit)? = null` (null: gọi `ProjectsScreen` không có tham số đó, dùng mặc định của màn); hai test đổi trạng thái tự gọi `composeRule.setContent` với `uiState` trong `mutableStateOf` (mục 4) |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsRouteTest.kt` | Thêm 2 test của mục 4; `show()` nhận thêm tham số `privacyOptionsButton: (@Composable (Modifier) -> Unit)? = null` (null: gọi `ProjectsRoute` không có tham số đó) |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsViewModelTest.kt` | Thêm `settingsFlagTurnsOff_hidesSettings` |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreenScreenshotTest.kt` | Thêm 4 test ảnh; hàm `Screen` nhận thêm tham số `privacyOptionsButton` có mặc định |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_privacy_light.png` | Ảnh chuẩn mới: danh sách, cờ Cài đặt tắt, vùng bắt buộc, sáng |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_privacy_dark.png` | Ảnh chuẩn mới: như trên, tối |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_privacy_small.png` | Ảnh chuẩn mới: khung `w360dp-h640dp-xxhdpi`, danh sách tám dự án, cờ tắt, vùng bắt buộc |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_noSettings_light.png` | Ảnh chuẩn mới: danh sách, cờ tắt, vùng không bắt buộc (khe không vẽ gì) |
| Sửa | `feature/projects/build.gradle.kts` | (dự phòng) Khối `tasks.withType<Test>().configureEach { maxHeapSize = "2g" }`, chỉ khi `pr-check` hết bộ nhớ trong test của module (mục 3.4) |

Không sửa: `ProjectsViewModel.kt`, `ProjectsUiState.kt`, `ProjectsNavigation.kt`, `app/` (đích và `AppNavHost` không đổi), `:core:ads` (dùng `PrivacyOptionsButton` như đang có), `:core:config` (`FeatureFlag.SETTINGS` đã có). Không ảnh chuẩn có sẵn nào đổi: mọi screenshot test hiện có truyền `showSettingsButton = true`, nhánh đó không đổi. Không script hay workflow nào đọc các tệp trên theo tên (`grep -rn "ProjectsScreen\|feature/projects" scripts/ .github/` chỉ thấy `scripts/design-compare.sh` đọc ảnh theo mẫu tên ảnh thiết kế; bốn tên ảnh mới không trùng ảnh thiết kế nào).

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: `FeatureFlag.SETTINGS` (`feature_settings_enabled`), đã có, theo dõi qua `Flow` trong `ProjectsViewModel`. Không thêm cờ. `StagingFeatureFlagsModule` bật sẵn cờ này trên staging, nên nhánh cờ tắt chỉ kiểm bằng test.
- Quảng cáo: không thêm, bớt hay dời vị trí nào. Dùng `PrivacyOptionsButton` có sẵn của `:core:ads` (ADR-0045, mục 3.5 và 4.1); chạm lối vào hộp thoại đồng ý (điểm chặn 5).
- Sự kiện phân tích: Không chạm.
- File R2: Không chạm.
- **Quyền truy cập:** Không thêm.
- **Thư viện và SDK:** Không thêm. `:core:ads` đã là phụ thuộc `implementation` của mọi module feature qua plugin `template.android.feature` (`AndroidFeatureConventionPlugin.kt`); `PrivacyOptionsButton` và `R.string.ads_privacy_options` của nó dùng được từ `:feature:projects` (đã biên dịch được ở lần chạy thử, mục 4.1).

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** bộ nhớ của test trên CI. `pr-check` chạy Java 17 với heap mặc định của task `Test`, còn `scripts/check.sh` của phiên chạy Java 21. Plan thêm 4 ảnh Robolectric `NATIVE` và 10 test JVM khác vào `:feature:projects`, vốn đã có nhiều test ảnh (bài học L-004).
- **Điều kiện chuyển:** `pr-check` đỏ với `java.lang.OutOfMemoryError` trong một test của `:feature:projects:testDebugUnitTest`.
- **Cách làm thay:** thêm vào `feature/projects/build.gradle.kts` khối `tasks.withType<Test>().configureEach { maxHeapSize = "2g" }` (tệp riêng của app, không phải `build-logic/`, không cần ADR; T-009, T-012 có cùng dự phòng, khối đã có thì không thêm lần nữa). Ghi vào mô tả PR rằng dự án mẫu nên đặt `maxHeapSize` chung (`docs/ops/from-app.md`). Không đổi test hay câu khẳng định. Vẫn hết bộ nhớ thì dừng task.

### 3.5. Hành vi đã có bị đổi

`scripts/existing-tests.py` (2026-11-23, trên `cdebb3c`): xem kết quả ở cuối mục. Plan chỉ thêm một nhánh `else` cho trường hợp `showSettingsButton = false` và một tham số có mặc định; nhánh cờ bật, bố cục và khoảng cách của thanh trên giữ nguyên.

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Cờ Cài đặt tắt: thanh trên chỉ có nút nhập file, không có nút cài đặt | Cờ tắt: khe quyền riêng tư được gọi ở chỗ nút cài đặt; khe mặc định không vẽ gì khi không có `LocalAdsController` (test) hay khi không bắt buộc | `ProjectsScreenTest.settingsHidden_hasNoSettingsNode` | Giữ nguyên: test không truyền khe, khe mặc định không vẽ gì trong test, nên nút nhập file vẫn có và không có nút cài đặt |
| Thanh trên cao cố định 64 dp (`.height(TopBarHeight)`) | Cao ít nhất 64 dp (`.heightIn(min = TopBarHeight)`); ở cỡ chữ thường vẫn 64 dp | `ProjectsScreenTest.scrollGrid_topBarStays`, `ProjectsScreenTest.small_emptyButtonsReachable`, mọi test của `ProjectsScreenScreenshotTest` và `ProjectMenuScreenshotTest` | Giữ nguyên: lần chạy thử đã đổi dòng này rồi chạy `verifyRoborazziDebug` và cả bộ test của module, mọi test và ảnh có sẵn qua (mục 4.1) |
| Cờ Cài đặt bật: nút cài đặt sau nút nhập file, bấm gọi `onOpenSettings` | Không đổi | `ProjectsScreenTest.settings_callsCallback`, `ProjectsScreenTest.scrollGrid_topBarStays`, `ProjectsRouteTest.settings_withoutDestination_showsComingSoon` | Giữ nguyên |
| `uiState.showSettingsButton` theo cờ `SETTINGS`, cờ bật giữa chừng thì nút hiện | Không đổi | `ProjectsViewModelTest.settingsFlagOff_hidesSettings`, `ProjectsViewModelTest.settingsFlagTurnsOn_showsSettings` | Giữ nguyên |
| Ảnh chuẩn của danh sách, màn trống, menu (cờ bật) | Không đổi | `ProjectsScreenScreenshotTest.*`, `ProjectMenuScreenshotTest.*` | Giữ nguyên, ảnh không được đổi (mọi test truyền `showSettingsButton = true`) |

Kết quả `scripts/existing-tests.py` (2026-11-23, trên `cdebb3c`): 160 test dùng tên hay tag của `ProjectsScreen.kt`; "Cần xem trước" 7, "Nên xem" 33, plan đã nhắc 5. Đã đọc từng test của hai nhóm:

- "Cần xem trước": `ProjectMenuTest.deleteDialog_confirmUsesErrorColor`, `menuContent_bottomInsetAddedOnce`, `menuSheet_isAtBottom`, `renameDialog_buttonsUsePrimary`, `small_menuFits`, `renameText_survivesRestore` đo menu và hộp thoại của T-012, mở bằng `ProjectsScreen` hay `ProjectsRoute` với `showSettingsButton = true` (cờ bật trong `FakeFeatureFlags`); `ProjectsScreenTest.small_emptyButtonsReachable` đo hai nút của màn trống với `showSettingsButton = true`. Plan không đổi menu, hộp thoại, nội dung hay chiều cao thanh trên, và nhánh cờ bật giữ nguyên: giữ cả bảy.
- "Nên xem": các test còn lại của `ProjectMenuTest`, `ProjectsRouteTest`, `ProjectsScreenTest`, `ProjectMenuScreenshotTest.duplicating_light`, `ProjectsScreenScreenshotTest.list_unreadable` chỉ dùng tag của thẻ, lưới, menu, nút "New animation", nút bài học, nút nhập file, hay gọi `ProjectsScreen`/`ProjectsRoute` không truyền tham số mới. Tham số mới có mặc định, tag cũ không đổi: giữ tất cả.

## 4. Kiểm thử

Mọi test UI dùng `AppTheme(darkTheme = false)`, khung mặc định của lớp test trừ khi ghi khác. `FakePrivacyButton(modifier, onClick)` là nút giả của mục 3.1, ý 4; truyền vào bằng `privacyOptionsButton = { FakePrivacyButton(modifier = it, onClick = { privacyClicks++ }) }`. Test dựng một trạng thái cố định dùng `show()` với tham số khe nullable. Test đổi `uiState` giữa chừng không qua `show()`: khai báo `val uiState = mutableStateOf<ProjectsUiState>(...)` trong test, tự gọi `composeRule.setContent { AppTheme(darkTheme = false) { ProjectsScreen(uiState = uiState.value, ...) } }`, rồi gán `uiState.value = ...` và `composeRule.waitForIdle()`. "Khe rỗng" là `privacyOptionsButton = {}`: giống `PrivacyOptionsButton` ở vùng không bắt buộc.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| KB-11: cờ tắt, vùng bắt buộc, nút quyền riêng tư ở chỗ nút cài đặt | Compose UI test | `ProjectsScreenTest.settingsOff_showsPrivacyButtonInPlaceOfSettings`: tự gọi `composeRule.setContent` một lần, `uiState` là `mutableStateOf` của test, khe là `FakePrivacyButton`. Lần đầu `ProjectsUiState.Empty(showLessonButton = true, showSettingsButton = false)`, khẳng định; rồi đổi sang `ProjectsUiState.List(designCards(), showSettingsButton = false)`, `waitForIdle()`, khẳng định lại. Khẳng định: node tag `PRIVACY` có; `onAllNodesWithTag(SETTINGS)` đếm 0; node `IMPORT` có; `privacy.boundsInRoot.left >= import.boundsInRoot.right`; `privacy.boundsInRoot.bottom <= ` 64 dp đổi ra px (`with(composeRule.density) { 64.dp.toPx() }`): nút nằm trong thanh trên, không rơi xuống nội dung. Thất bại trước: chưa có tham số `privacyOptionsButton` và tag `PRIVACY` (lỗi biên dịch) |
| KB-11: cờ bật thì nút cài đặt như cũ, không gọi khe | Compose UI test | `ProjectsScreenTest.settingsOn_doesNotComposePrivacySlot`: `list(designCards())` (cờ bật), khe là `{ slotComposed = true; FakePrivacyButton(modifier = it) }`. Khẳng định: `slotComposed == false`, node `SETTINGS` có, `onAllNodesWithTag(PRIVACY)` đếm 0. Dựng bằng `composeRule.setContent` tự gọi, `uiState` là `mutableStateOf` của test (không qua `show()`). Đối chứng ngay trong test: gán `uiState.value = ProjectsUiState.List(designCards(), showSettingsButton = false)`, `waitForIdle()`, thì `slotComposed == true` |
| KB-11: bấm nút mở hộp thoại quyền riêng tư | Compose UI test | `ProjectsScreenTest.privacyButton_tapReachesButton`: `List`, cờ tắt, khe `FakePrivacyButton(modifier = it, onClick = { privacyClicks++ })`. `performClick()` trên tag `PRIVACY` thì `privacyClicks == 1` và `settingsClicks == 0`. Phần "nút thật mở form của UMP" nằm trong `PrivacyOptionsButton` (`ads.showPrivacyOptionsForm(activity)`) và `ConsentCoordinatorTest.privacyForm_alwaysHoldsSuppression`, `privacyFormWithdrawn_closesEveryGate`, `privacyFormRegranted_initsOrPreloads` (có sẵn, không đổi) |
| KB-11: vùng không bắt buộc thì không hiện nút nào | Compose UI test | `ProjectsScreenTest.settingsOff_emptySlot_noTopButton`: `List`, cờ tắt, khe rỗng. Khẳng định: `SETTINGS` đếm 0, `PRIVACY` đếm 0, `onAllNodesWithText("Privacy settings")` đếm 0, node `IMPORT` có. Đối chứng: `settingsOff_showsPrivacyButtonInPlaceOfSettings` cùng trạng thái với khe giả thì có node |
| KB-11: khe mặc định là `PrivacyOptionsButton`, không vẽ gì khi không có bộ điều khiển quảng cáo | Compose UI test | `ProjectsScreenTest.settingsOff_defaultSlot_rendersNothingWithoutAds`: `List`, cờ tắt, không truyền khe. Khẳng định: `onAllNodesWithText("Privacy settings")` đếm 0, `PRIVACY` đếm 0, `SETTINGS` đếm 0, không ngoại lệ. Test canh: qua ngay ở bước 3 sau khi phần biên dịch có tag; giữ để bắt lỗi khe mặc định vẽ thứ khác `PrivacyOptionsButton` (ví dụ nút tự dựng gọi thẳng `AdsController`, trái ADR-0045, mục 4.1) |
| KB-11: nút vừa thanh trên ở màn hẹp, không đè chữ "Frameloop", chữ không bị cắt | Compose UI test, `@GraphicsMode(NATIVE)`, `@Config(qualifiers = "en-w360dp-h640dp-xxhdpi")` | `ProjectsScreenTest.privacyButton_fitsTopBar_small`: `List(designCards())`, cờ tắt, `FakePrivacyButton`. Lấy `TextLayoutResult` của node chữ "Privacy settings" (`useUnmergedTree = true`) qua `GetTextLayoutResult`: `lineCount == 1`, `isLineEllipsized(0) == false`, `getLineRight(0) <= ` bề rộng node chữ. Bố cục: phải của node chữ "Frameloop" `<=` trái của `IMPORT`; phải của `IMPORT` `<=` trái của `PRIVACY`; phải của `PRIVACY` `<=` bề rộng gốc. Số đo của lần chạy thử (mục 4.1): chữ 1 dòng, `lineRight` 326,9 px trên node 327 px; "Frameloop" 126 tới 491 px, nhập file 537 tới 657 px, nút 669 tới 1068 px trên gốc 1080 px |
| KB-11: chữ nút không bị cắt ở cỡ chữ lớn | Compose UI test, `@GraphicsMode(NATIVE)`, `@Config(qualifiers = "en-w360dp-h640dp-xxhdpi", fontScale = 2.0f)` | `ProjectsScreenTest.privacyButton_largeFont_notClipped`: `List(designCards())`, cờ tắt, `FakePrivacyButton`. Lấy `TextLayoutResult` của chữ "Privacy settings": `hasVisualOverflow == false`, `getLineBottom(lineCount - 1) <= ` chiều cao node chữ, không dòng nào `isLineEllipsized`. Bố cục: đáy của `PRIVACY` `<=` đỉnh của node `GRID` (nội dung bắt đầu dưới thanh trên). Thất bại trước, đã đo trên code có khe mà thanh còn `.height(TopBarHeight)` (mục 4.1): chữ 4 dòng, đáy dòng cuối 448 px trên node 144 px, `hasVisualOverflow == true`, ảnh chỉ thấy "Priv". Sau khi đổi sang `heightIn`: đáy dòng cuối 448 px trên node 448 px, `hasVisualOverflow == false` |
| Cờ đổi giữa chừng (ViewModel) | Unit test | `ProjectsViewModelTest.settingsFlagTurnsOff_hidesSettings`: `repository.projects.value = listOf(readableItem(1))`, cờ bật, `started()`; `showSettingsButton` của `List` là `true`; `flags[FeatureFlag.SETTINGS] = false`; `runCurrent()`; là `false`. Test canh: hành vi đã có từ T-009 nên qua ngay ở bước 3; đối chứng là test có sẵn `settingsFlagTurnsOn_showsSettings` (chiều ngược lại) |
| Cờ đổi giữa chừng (màn thật, `Flow` tới thanh trên) | Compose UI test | `ProjectsRouteTest.settingsFlag_flipsBetweenSettingsAndPrivacy`: `flags[FeatureFlag.SETTINGS] = false` trước `show(privacyOptionsButton = { FakePrivacyButton(modifier = it) })` (`show()` của `ProjectsRouteTest` nhận khe nullable, null thì gọi `ProjectsRoute` không có tham số đó). Sau `show()`, đặt `composeRule.mainClock.autoAdvance = true` (hàm `show()` tắt nó). Khẳng định: `PRIVACY` có, `SETTINGS` đếm 0. Đặt cờ `true`, `waitForIdle()`: `SETTINGS` có, `PRIVACY` đếm 0. Đặt lại `false`, `waitForIdle()`: `PRIVACY` có, `SETTINGS` đếm 0. Thất bại trước: chưa có tham số `privacyOptionsButton` của `ProjectsRoute` |
| Khe mặc định của `ProjectsRoute` | Compose UI test | `ProjectsRouteTest.settingsFlagOff_defaultSlot_noButtonWithoutAds`: cờ tắt, `show()` không truyền khe. Khẳng định: `onAllNodesWithText("Privacy settings")` đếm 0, `SETTINGS` đếm 0, `IMPORT` có. Test canh như dòng khe mặc định của màn |
| Giao diện, cờ tắt, vùng bắt buộc | Screenshot test | `ProjectsScreenScreenshotTest.privacy_light`, `privacy_dark`: `List(designCards(), showSettingsButton = false)`, khe `FakePrivacyButton`, khung lớp `en-w411dp-h891dp-xxhdpi`, `captureRoboImage("src/test/screenshots/ProjectsScreen_privacy_light.png")` và `"src/test/screenshots/ProjectsScreen_privacy_dark.png"` |
| Màn hình thấp | Screenshot test khung `w360dp-h640dp-xxhdpi`, trạng thái nhiều nội dung nhất | `ProjectsScreenScreenshotTest.privacy_small`, `@Config(qualifiers = "en-w360dp-h640dp-xxhdpi")`: tám thẻ như `list_small`, cờ tắt, `FakePrivacyButton`, `captureRoboImage("src/test/screenshots/ProjectsScreen_privacy_small.png")` |
| Giao diện, cờ tắt, vùng không bắt buộc | Screenshot test | `ProjectsScreenScreenshotTest.noSettings_light`: `List(designCards(), showSettingsButton = false)`, khe rỗng, `captureRoboImage("src/test/screenshots/ProjectsScreen_noSettings_light.png")` |
| Ảnh có sẵn không đổi | Screenshot test có sẵn | `./gradlew :feature:projects:verifyRoborazziDebug` qua; sau khi ghi ảnh mới bằng `./gradlew :feature:projects:recordRoborazziDebug :feature:projects:testDebugUnitTest --rerun`, `git status --short -- '*/src/test/screenshots/*'` chỉ thấy bốn ảnh mới của mục 3.2; ảnh khác đổi thì trả bằng `git checkout -- <ảnh>` |
| Đúng thiết kế (khi có `design/screens/`) | Không có ảnh thiết kế cho trạng thái này (mục 2, câu hỏi 1). Ảnh thiết kế của ticket khác (T-009): `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark`, `ProjectsScreen_list_light`, `ProjectsScreen_list_dark`. Ảnh thiết kế của ticket khác (T-014): `ProjectsScreen_list_light_unfolded`, `ProjectsScreen_list_dark_unfolded`. Ảnh thiết kế của ticket khác (T-011): `NewProjectScreen_default_light`, `NewProjectScreen_default_dark`. Ảnh thiết kế của ticket khác (T-012): `ProjectsScreen_menu_light`, `ProjectsScreen_menu_dark`. Ảnh thiết kế của ticket khác (T-013): `ProjectsScreen_restoring_light`, `ProjectsScreen_restoring_dark`, `ProjectsScreen_restore-error_light`, `ProjectsScreen_restore-error_dark` | Không áp dụng |
| Nút thật chỉ hiện ở vùng bắt buộc và bấm mở form UMP | Kiểm tra thủ công (máy thật: khác, cờ `feature_settings_enabled` bật cứng trong `StagingFeatureFlagsModule` nên bản staging không có nhánh cờ tắt; cần form UMP thật) | Dev làm một lần sau khi merge, không commit: bỏ hàm `settings()` khỏi `app/src/staging/kotlin/com/vgl/draw/animation/di/StagingFeatureFlagsModule.kt`, chạy `./gradlew :app:installStagingDebug`, xóa dữ liệu app, mở app. Staging giả lập vùng EEA (`docs/ops/ads-guide.md`, dòng 272), nên hộp thoại đồng ý hiện; đồng ý. Ở danh sách dự án, thanh trên có "Privacy settings" ở chỗ nút cài đặt, không có nút cài đặt. Bấm: form lựa chọn quyền riêng tư của UMP mở, log `adb logcat -v threadtime \| grep ' VGL\.Ads'` có `Privacy options form opened` (`ConsentCoordinator.showPrivacyOptions`). Rút đồng ý trong form rồi đóng: log `Consent withdrawn`. Trả tệp bằng `git checkout -- app/src/staging/kotlin/com/vgl/draw/animation/di/StagingFeatureFlagsModule.kt`. Đối chiếu: màn "Ad gallery" của staging, trang "Ad status", dùng cùng `PrivacyOptionsButton` (`app/src/staging/kotlin/com/vgl/draw/animation/adgallery/AdGalleryScreen.kt`, dòng 317) và có dòng "Privacy options required" |
| Dáng nút trên thanh trên | Kiểm tra thủ công (người: xem ảnh chuẩn) | Người duyệt PR mở `ProjectsScreen_privacy_light.png`, `_dark.png`, `_small.png`, `ProjectsScreen_noSettings_light.png`, xác nhận dáng và vị trí theo câu hỏi 1 |

**Ngoại lệ viết test trước:** phần lọc "vùng bắt buộc" của nút thật không có test tự động nào, kể cả trong `:core:ads`. `PrivacyOptionsButton` đọc `AdsController.privacyOptionsRequired`; `AdsController` có hàm dựng `internal` và tự dựng `UmpConsentPlatform` thật, nên không test nào dựng được trạng thái "bắt buộc". Hai test khe mặc định chỉ qua vì `LocalAdsController` là null trong test, không kiểm nhánh `required`. Phần này kiểm bằng bước tay ở trên (một lần, sau merge) và một đề xuất `from-app` cho dự án mẫu: thêm `PrivacyOptionsButtonTest` vào `:core:ads` (bắt buộc thì có nút, không bắt buộc thì không có, bấm thì gọi form), cần cho `AdsController` nhận `ConsentPlatform` giả. Plan này không sửa `:core:ads`; phiên ghi đề xuất vào mô tả PR. Ngoài ra, ba test canh (`settingsFlagTurnsOff_hidesSettings`, hai test khe mặc định) qua ngay ở bước 3 vì kiểm hành vi đã có hay mặc định của `:core:ads`; ghi rõ ở trên, có đối chứng.

### 4.1. Chạy thử

- **Đã chạy thử:** 2026-11-23, trên `origin/main` `ad2826a`, worktree tạm, đã xóa. Sửa tạm `ProjectsScreen.kt` đúng như mục 3.1 (tham số khe, nhánh `else`, tag), một test tạm có `FakePrivacyButton` đúng mục 3.1, ý 4 (dùng `com.vgl.draw.animation.core.ads.R.string.ads_privacy_options`: biên dịch được), đo node, ghi ảnh `w360`.
- **Lệnh:** `./gradlew :feature:projects:testDebugUnitTest --tests '*TrialPrivacyTest*'`, rồi `recordRoborazziDebug` cho ảnh `w360`. Không chạy `scripts/check.sh` vì E dưới 2 (mẫu khe truyền vào đã có ở `:feature:sample`).
- **Kết quả (px, mật độ 3):**
  - `w411dp`: "Frameloop" 126 tới 491, nhập file 690 tới 810, nút 822 tới 1221 (rộng 133 dp), chữ 1 dòng.
  - `w360dp`: "Frameloop" 126 tới 491, nhập file 537 tới 657, nút 669 tới 1068 trên gốc 1080, chữ 1 dòng, không cắt, `lineRight` 326,9 trên node 327. Ảnh: ba phần tử thẳng hàng, không đè nhau, khoảng trống giữa "Frameloop" và nút nhập file 15 dp.
  - `w360dp`, cỡ chữ 1,3: chữ nút xuống 2 dòng ("Privacy / settings"), nút cao 64 dp, vừa đúng thanh trên 64 dp, không cắt (câu hỏi 1).
  - `w320dp`: chữ nút xuống 2 dòng. App không có khung chuẩn hẹp hơn 360 dp; không thêm test.
  - Lần chạy thử thứ hai (2026-11-23, cùng commit, worktree tạm riêng, đã xóa), khung `w360dp`, thanh còn `.height(TopBarHeight)`: cỡ chữ 1,5 thì chữ 2 dòng, đáy dòng cuối 190 px trên node 144 px, `hasVisualOverflow == true` (dòng 2 bị cắt); cỡ chữ 2,0 thì chữ 4 dòng, đáy dòng cuối 448 px trên node 144 px, ảnh chỉ thấy "Priv".
  - Đổi thanh sang `.heightIn(min = TopBarHeight)`: cỡ chữ 1,5 thì node chữ 190 px, hết tràn; cỡ chữ 2,0 thì node chữ 448 px, hết tràn, thanh cao 496 px (khoảng 165 dp), "Frameloop" và nút nhập file căn giữa theo chiều dọc, chữ ngắt giữa từ ("Priv / acy / setti / ngs") vì cột của nút chỉ còn 84 dp. Cùng thay đổi đó, `./gradlew :feature:projects:verifyRoborazziDebug :feature:projects:testDebugUnitTest --rerun`: 139 test có sẵn qua, mọi ảnh chuẩn có sẵn khớp; test lỗi duy nhất là test tạm thiếu ảnh chuẩn.
  - Không giới hạn được chữ 1 dòng (`maxLines`, ba chấm) từ phía feature: `Text` nằm trong `PrivacyOptionsButton`, `TextButton` đặt kiểu chữ riêng, không có tham số nào cho feature đổi số dòng.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** điểm 5 (hộp thoại xin đồng ý của người dùng): plan thêm một lối vào mở lại hộp thoại quyền riêng tư quảng cáo. Rủi ro **Cao**; người phụ trách sản phẩm (anh Nhật) cần biết trước khi duyệt (`risk-scoring.md`, mục 3). Điểm tiêu chí ghi để tham khảo:

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 2 | Một tệp code trong một module feature, nhưng thêm tham số vào composable công khai `ProjectsScreen` (và `ProjectsRoute`, `internal`) |
| B. Độ rõ của yêu cầu | 1 | Spec đã xác nhận, câu hỏi 23 đã trả lời; thiết kế không có trạng thái này, dáng nút và chữ AI tự chọn theo nút có sẵn (câu hỏi 1) |
| C. Khả năng kiểm chứng tự động | 1 | Phần của feature có test đủ; phần vùng và form là code `:core:ads` có sẵn, chỉ test gián tiếp, nhánh cờ tắt không kiểm được trên staging |
| D. Ảnh hưởng chéo | 2 | `ProjectsScreen.kt` cũng do T-010 (thêm `adSlots`, `InContentAds`) và T-013 sửa |
| E. Độ mới của kỹ thuật | 1 | Biến thể nhỏ của mẫu khe truyền vào (`bottomBar` của `:feature:sample`) |
| **Tổng** | 7 | **Mức: Cao (chạm điểm chặn 5)** |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: không sửa tệp dùng chung. Dùng `PrivacyOptionsButton` và chuỗi `ads_privacy_options` của `:core:ads` (chỉ đọc).
- Task khác trong sprint có thể xung đột:
  - T-010 (quảng cáo ở danh sách, plan nháp cùng đợt): sửa `ProjectsScreen.kt` (tham số `adSlots`, bọc `InContentAds`, thanh đáy) và `ProjectsScreenScreenshotTest.kt`, ghi lại ảnh danh sách. Hai plan cùng thêm tham số cuối vào `ProjectsScreen` và sửa hàm `Screen` của lớp test ảnh: task chạy sau gộp cả hai tham số. T-056 không đụng phần quảng cáo; T-010 không đụng thanh trên. Nếu T-010 chạy trước và ghi lại ảnh danh sách, bốn ảnh mới của T-056 ghi trên bố cục đã có thanh đáy, vẫn đúng tên ở mục 3.2.
  - T-013 (sao lưu, khôi phục): sửa `ProjectsScreen.kt` ở phần hộp thoại khôi phục, không đụng thanh trên.
  - T-016 (dòng "Ad privacy choices" ở màn Cài đặt): cùng gọi `PrivacyOptionsButton` hay hàm tương đương của `:core:ads`; không chung tệp. Nếu T-016 cần một dáng khác (dòng của danh sách cài đặt), đó là việc của T-016.
  - T-014 (bố cục điện thoại gập mở ra, KB-12): thanh trên rộng có thêm nút "New animation"; ticket đó giữ nhánh `else` của plan này.
  - T-012 phiên bản 2 (plan cùng đợt): thay `SnackbarHost` bằng `ProjectsSnackbars` trong `ProjectsScreen.kt`. Không đụng thanh trên; T-056 chạy sau thì giữ phần của T-012.
  - T-011 (màn Tạo dự án): có cùng khối dự phòng `maxHeapSize` ở `feature/projects/build.gradle.kts` (như T-009, T-012); khối đã có thì không thêm lần nữa.
- Đề xuất thứ tự chạy: T-012 phiên bản 2, rồi T-010, rồi T-056, để T-056 ghi ảnh trên bố cục cuối của danh sách và gộp các tham số đã có. Chạy trước cũng được: task sau gộp tham số `privacyOptionsButton` vào hàm `Screen` của lớp test ảnh.

## 7. Điều kiện dừng riêng

- Cần sửa `:core:ads` (dáng, chữ hay điều kiện hiện của `PrivacyOptionsButton`) để đạt tiêu chí: dừng, vì là thay đổi dùng chung cần đề xuất về dự án mẫu (câu hỏi 1).
- Code cần đọc `AdsController`, `LocalAdsController` hay trạng thái đồng ý trực tiếp trong `:feature:projects`: dừng (ADR-0045, mục 4.1).
- Ảnh chuẩn có sẵn nào đổi (ngoài bốn ảnh mới): dừng, không ghi lại.

## 8. Câu hỏi cho người duyệt

1. **Dáng và chữ của nút, cỡ chữ lớn.** Thiết kế không có trạng thái này; thanh trên thiết kế chỉ có nút biểu tượng. `PrivacyOptionsButton` là nút chữ Material màu `primary`, chữ "Privacy settings" của `:core:ads`. Chữ này chưa có bản tiếng Việt (`:core:ads` chỉ có `values/`), nên ở bản `vi` đây là chữ tiếng Anh duy nhất trên màn; dòng ở màn Cài đặt lại là "Ad privacy choices". Feature không được tự dựng nút gọi `AdsController` (ADR-0045, mục 4.1), không đổi được số dòng của chữ, và thanh trên cố định 64 dp cắt chữ từ cỡ chữ 1,5 (mục 4.1).
   - Đề xuất của AI: dùng nguyên nút chữ của `:core:ads`, cho thanh trên cao theo nội dung (`heightIn(min = 64.dp)`) để chữ không bao giờ bị cắt; chấp nhận thanh cao hơn và chữ ngắt giữa từ ở cỡ chữ 2,0, vì nút chỉ hiện khi cờ Cài đặt tắt (tình huống hiếm). Đồng thời gửi đề xuất `from-app` về dự án mẫu: biến thể biểu tượng của `PrivacyOptionsButton` và bản tiếng Việt của chữ; khi dự án mẫu có, một ticket sau đổi sang biến thể đó. Không cần bổ sung thiết kế.
2. **Không kiểm được trên bản staging, và lọc vùng không có test tự động.** `StagingFeatureFlagsModule` bật cứng cờ Cài đặt, nên nút không bao giờ hiện trên thanh trên của bản staging. Phần lọc "vùng bắt buộc" của `PrivacyOptionsButton` không có test tự động nào, kể cả trong `:core:ads` (mục 4, ngoại lệ viết test trước); test khe mặc định chỉ qua vì không có `LocalAdsController`.
   - Đề xuất của AI: dev làm bước tay một lần sau merge (mục 4: bản staging cục bộ bỏ `settings()` khỏi `StagingFeatureFlagsModule`, không commit); đối chiếu màn "Ad gallery" của staging dùng cùng `PrivacyOptionsButton`; gửi đề xuất `from-app` thêm `PrivacyOptionsButtonTest` vào `:core:ads` của dự án mẫu. Mục "Có trong bản staging" trong Định nghĩa hoàn thành của ticket được đánh dấu theo test và bước tay này, vì cờ bị bật cứng trên staging.
3. **Ghi log.** Feature không biết nút có vẽ hay không, nên plan không ghi log mới; lần mở form đã có log `Privacy options form opened` của `:core:ads`.
   - Đề xuất của AI: đồng ý không thêm log.

## 9. Quyết định duyệt

<!-- Người duyệt điền, hoặc AI ghi theo lời đồng ý trong phiên của người duyệt, nguyên văn (ADR-0048, phần C). Plan `lane: quick`: ghi "Duyệt trong phiên làm nhanh" kèm lời đồng ý. -->

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện

<!-- AI ghi khi task đã có kết quả cuối (đã merge hoặc đã dừng), trên nhánh báo cáo của phiên (auto-run.md, bước 10). Mục mới thêm vào cuối, không sửa mục cũ. -->
