---
ticket: T-008
title: Định dạng dải native gọn 56 dp và vị trí editor_top_native
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 16.4M        # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [ads, core] # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-008: ĐỊNH DẠNG DẢI NATIVE GỌN 56 DP VÀ VỊ TRÍ EDITOR_TOP_NATIVE

## 1. Tóm tắt

Thêm vào `:core:ads` của app định dạng native gọn một tầng `native_strip` (cao cố định 56 dp ở mọi cỡ chữ, chỉ icon, nhãn "Ad", CTA viền, chừa góc AdChoices), vị trí `editor_top_native` dùng định dạng đó, và khóa riêng của vị trí `time_reload_editor_top_native` (mặc định 120 giây) suy ra từ khai báo vị trí. Dải đi qua `NativeAdSlot(placement)` có sẵn: khóa tắt hay chưa đồng ý thì không giữ chỗ; khóa bật thì giữ chỗ 56 dp cả lúc nạp hay lỗi; nạp lại theo khóa riêng. Đây là ngoại lệ có chủ đích với MC-02 (danh sách định dạng đóng của dự án mẫu), cần một ADR của app được chấp nhận trước khi chạy (mục 8, câu 1). Kiểm chứng bằng unit test của khai báo và khóa, test Compose đo chiều cao và chữ không bị cắt ở cỡ chữ 1,0 tới 2,0, test nạp lại theo đồng hồ ảo, và screenshot với thẻ giả.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/editor/spec.md`:

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-19 | Khi vị trí dải quảng cáo bật thì dải cao khoảng 56 dp nằm ngay dưới thanh tiêu đề, cách hàng nút của thanh tiêu đề và cách canvas mỗi bên ít nhất 16 dp, ở mọi trạng thái của màn vẽ | T-008 làm phần dải tự có: cao cố định 56 dp. Vị trí trên màn vẽ và khoảng cách 16 dp là việc của T-040 (đặt dải lên màn vẽ, ngoài phạm vi ticket) |
| KB-19 | Khi quảng cáo đang nạp hay nạp lỗi thì dải vẫn giữ chỗ; canvas không đổi chỗ khi quảng cáo nạp xong hay lỗi | Định dạng `native_strip` luôn giữ 56 dp khi cổng mở (khóa bật, có đồng ý, có mã đơn vị), ở cả ba trạng thái nạp, có quảng cáo, lỗi; không phụ thuộc tham số `keepSpaceWhenEmpty` của chỗ gọi |
| KB-19 | Khi vị trí tắt hay người dùng chưa đồng ý quảng cáo thì màn vẽ dùng bố cục không có dải, canvas dời lên | Cổng đóng thì dải chiếm 0 dp (hành vi có sẵn của `NativeAdSlotGate`); bố cục `drawing-no-ad` là việc của T-040 |
| KB-19 | Khi người dùng vẽ, phát, ... thì không quảng cáo nào ngoài dải này ... | Ngoài phạm vi T-008 (màn vẽ của T-040, T-019 trở đi); T-008 không thêm quảng cáo toàn màn hình nào |
| KB-19 | Khi chữ hệ thống ở cỡ lớn thì dải vẫn cao như cũ, nhãn "Ad" luôn thấy, chữ nút của quảng cáo không bị cắt | Ở cỡ chữ 1,0, 1,3, 1,5, 2,0: chiều cao 56 dp; nhãn "Ad" và chữ CTA hiển thị đủ (không cắt ba chấm, không cắt theo chiều cao); tiêu đề và dòng mô tả được cắt bằng "…" |
| editor mục 5, `editor_top_native` | Nạp lại theo `time_reload_editor_top_native`, mặc định 120 giây; khóa bật tắt `is_show_editor_top_native` | Khóa suy ra theo MC-03 (`<tham số>_<mã vị trí>`), đọc như `time_reload_native_small` (0 là không nạp lại, 1 tới 29 thành 30, âm là mặc định) |
| editor mục 5, định dạng mới | Chỉ icon, không ảnh chính; nhãn "Ad" luôn thấy; chừa góc trên phải cho AdChoices; CTA viền, khác nút xanh chanh của app; cao cố định khoảng 56 dp ở mọi cỡ chữ | Bố cục theo `design/context/EditorScreen_drawing_light.md` dòng 91 tới 100 (mục 3.1) |

Thiết kế tham chiếu: `features/editor/design/README.md` (dòng 476, 491), số liệu của instance `NativeAdStrip` trong `design/context/EditorScreen_drawing_light.md` (dòng 91 tới 100) và bản tối `EditorScreen_drawing_dark.md`. Mọi ảnh `design/screens/EditorScreen_*` là ảnh cả màn vẽ, do T-040 và các ticket màn vẽ chụp; T-008 chụp riêng dải bằng thẻ giả.

ADR liên quan: ADR-0045 (quảng cáo, mục 4.9: app không thêm code vào `:core:ads`), ADR-0030 (đồng bộ từ dự án mẫu), ADR-1001 (ngoại lệ heap của `:core:ads`, mẫu cho ADR mới), ADR mới của app cho ngoại lệ MC-02 (mục 8, câu 1).

## 3. Giải pháp

### 3.1. Cách làm

Mọi đường dẫn code dưới đây tương đối với `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/` (gọi là M/), test tương đối với `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/` (gọi là T/).

**Định dạng.** Thêm `AdFormat.NATIVE_STRIP("native_strip")` vào enum ở `M/AdPlacement.kt`. Xử lý mọi `when` liệt kê định dạng (danh sách tra ngày 2026-11-23 trên `origin/main` 6a90f1a):

- `when` không có `else` (không biên dịch tới khi xử lý): `AdPreloadPlan.kt` `isNative` (true); `AdTelemetryRules.kt` `metricCode()` (mã chỉ số native như `NATIVE_SMALL`; xem mục 8, câu 4), `funnelKind()` (như `NATIVE_SMALL`), `loadPlacementOf()` (như `NATIVE_SMALL`); `AdUnitIds.kt` `unitFor()` (mã thử native `ca-app-pub-3940256099942544/1044960115`, đúng mã mà `scripts/release.sh` dòng 58 điền cho mọi `NATIVE_*`; test "the test unit of every format matches GoogleTestAdUnitIds" của `scripts/tests/run.sh` so hai nơi); `AdsController.kt` `nativeLoadSize()` (`NativeAdSize.STRIP`); `NativeAdPool.kt` `poolCapacity()` (`null`: không dùng pool); `RewardedRules.kt` `fullScreenLoaderOf()` (nhánh không toàn màn hình như `NATIVE_SMALL`).
- `when` có `else`, phải quyết rõ: `NativeAdSources.kt` `usesSingleAdSource()` thêm `NATIVE_STRIP` (một quảng cáo, nạp lại tại chỗ như `native_small`); `usesStock`, `stockMayFill`, `STOCK_FORMATS` không thêm (dải không lấy từ kho dùng chung với khe đáy); `NativeAdSlot.kt` `requireNativeFormat` nhận `NATIVE_STRIP` trả `NativeAdSize.STRIP`; `AdsController.kt` `reloadIntervalMillis` (mục Nạp lại dưới đây); các `when` khác (`NativeAdCell.requireCellFormat`, `AdTelemetryRules.metricGroup`, `AdRetryPolicy`, `BannerAdSize`, `BannerLoads`, `MaxInterstitialSlot`, `AdUnitIds.layerFormats`) giữ nhánh `else` có sẵn, vì dải không phải ô danh sách, banner, inter hay lớp thanh đáy.
- Tập định dạng: không thêm vào `NATIVE_EVERY_FORMATS`, `APP_WIDE_FORMATS`; không thêm vào `IN_CONTENT_FORMATS` (mục 8, câu 3).

**Khai báo vị trí.** Thêm tham số `reloadSeconds: Int? = null` vào hàm dựng của `AdPlacement` và trường cùng tên của `AdPlacementDeclaration`; `init` của khai báo `require(reloadSeconds == null || format == AdFormat.NATIVE_STRIP)` và `reloadSeconds` không âm. Dải không khai `reloadSeconds` vẫn hợp lệ (để `AdPlacementDeclaration("x", format)` của `AdPlacementTest.firstOpenOnly_flowStepFormatsOnly_notAppWide` dòng 121 chạy với mọi định dạng): khi đó không có khóa riêng và không nạp lại. Thêm một dòng ngay sau `EDITOR_EXIT_INTER` (cùng nhóm editor), một dòng mà `scripts/release.sh` đọc được:

```kotlin
/** Editor, compact strip under the title bar (editor spec, section 5; KB-19). Group A. App format, ADR of the app. */
EDITOR_TOP_NATIVE("editor_top_native", AdFormat.NATIVE_STRIP, reloadSeconds = 120),
```

**Khóa suy ra.** Trong `M/AdsSettings.kt` thêm `fun reloadInterval(placement: AdPlacement): RemoteKey<Long>?` và bản `internal` nhận `AdPlacementDeclaration`, theo đúng mẫu `nativeEvery`: `placementParamKey("time_reload", declaration)`, `RemoteKey.long(name, default = reloadSeconds.toLong(), description = "Seconds between two loads of this placement's strip; 0 keeps the ad, 1 to 29 means 30. " + derivedNote(declaration))`; thêm vào `listOfNotNull` của `keysFor`. Kết quả: khóa `time_reload_editor_top_native`, mặc định 120. Trong `M/AdsFlags.kt` thêm vào interface `AdsFlags` hàm `fun reloadSeconds(placement: AdPlacement): Flow<Long?> = flowOf(null)` (thân mặc định, kiểu như `nativeEvery`, để lớp giả có sẵn như `FakeAdsFlags` của `app/src/testStaging/.../adgallery/AdGalleryViewModelTest.kt` dòng 219 biên dịch không cần sửa), cài đặt trong `DefaultAdsFlags` như `nativeEvery`: đọc `settings.value(AdsSettings.reloadInterval(declaration))`, qua `reloadSeconds(raw, key.default)` của `M/ReloadInterval.kt`. Vị trí không có khóa thì trả `flowOf(null)`.

**Nạp lại.** `AdsController.reloadIntervalMillis` hiện nhận `AdFormat` và cache theo định dạng. Thêm `reloadIntervalMillis(placement: AdPlacement)`: định dạng `NATIVE_STRIP` thì đọc `flags.reloadSeconds(placement)` (cache theo vị trí; `null` là 0, không nạp lại), còn lại gọi bản theo định dạng như cũ. Chỗ `nativeAd(...)` (khoảng dòng 2405) gọi bản theo vị trí cho định dạng `usesSingleAdSource()`. Bản theo định dạng giữ nguyên cho banner và các nơi khác; `NATIVE_STRIP` vào nhánh `else` (ném lỗi "has no reload key") của bản theo định dạng là đúng, vì dải luôn đi qua bản theo vị trí. Phần nạp lại tại chỗ (`refreshingNativeAdStates` của `M/NativeAdRefresh.kt`) giữ nguyên, nhận khoảng mới. Lưu ý: `usesSingleAdSource()` đi qua `nativeSources.source()` rồi `stockOf()`, nên dải vẫn có một `NativeAdSource` riêng (khóa định dạng và mã đơn vị); vì không có trong `usesStock`, `stockMayFill` (nhánh `else -> false`) nên không nạp sẵn quảng cáo kế tiếp sau mỗi lần lấy: mỗi lần nạp lại là một lần nạp tại chỗ; khóa tắt thì cổng của `NativeAdSlot` hủy dải. Hành vi này được kiểm ở `AdsControllerStripReloadTest`.

**Giữ chỗ.** Trong `M/NativeAdSlot.kt`, hàm `NativeAdSlot` truyền xuống `keepSpaceWhenEmpty = keepSpaceWhenEmpty || size == NativeAdSize.STRIP` (sau `requireNativeFormat`); `Spacer` của nhánh `Unavailable` đã dùng `nativeAdHeight(size, ...)` nên cao 56 dp. Không sửa `NativeAdSlotGate`. Cổng đóng (khóa tắt, chưa đồng ý, không có mã) vẫn vẽ `Box` rỗng 0 dp như cũ.

**Bố cục dải.** Trong `M/NativeAdCard.kt` thêm `NativeAdSize.STRIP` và xử lý mọi `when (size)` không có `else` (`heightOf`, hai nhánh của `NativeAdLayout`, khối giữ chỗ) cùng `NativeRequestOptions.kt` (tỉ lệ media: không xin media chính, như bản không ảnh). `NativeAdDimens` thêm hằng của dải, lấy từ `design/context/EditorScreen_drawing_light.md` dòng 91 tới 100: `StripHeight = 56.dp`, `StripCorner = 10.dp`, `StripBorder = 1.dp`, `StripPaddingHorizontal = 8.dp` (`Spacing.sm`), `StripGap = 10.dp`, `StripIconSize = 40.dp`, `StripIconCorner = 8.dp`, `StripBadgeGap = 6.dp`, `StripCtaHeight = 32.dp`, `StripCtaCorner = 6.dp`, `StripCtaPaddingHorizontal = 12.dp`. Bố cục `StripLayout`: `Row` cao đúng `StripHeight` (không theo cỡ chữ), nền `surfaceContainerLow`, viền `outlineVariant`; icon 40 dp; cột chữ chiếm phần còn lại gồm hàng tiêu đề (nhãn "Ad" rồi tiêu đề) và một dòng mô tả; CTA viền ở cuối hàng. Chữ: nhãn "Ad" `labelSmall`, màu `onSurfaceVariant` trên nền `surfaceContainerHighest` (khác `NativeAdBadge` hiện dùng `tertiary`: thêm tham số màu cho `NativeAdBadge` hay một biến thể riêng, giữ tag `BADGE`); tiêu đề `labelLarge` `onSurface`, mô tả `bodySmall` `onSurfaceVariant`, cả hai `maxLines = 1`, `TextOverflow.Ellipsis`; CTA viền `secondary` 1 dp, chữ `labelMedium` `secondary`, giữ tag `CTA`, là `Box` (không phải `Button`) để chạm tới view của SDK như CTA có sẵn. Chừa góc AdChoices (O-08: AdChoices không bị đè): thiết kế đặt CTA cách đỉnh dải 12 dp và mép phải 9 dp, lấn ô 16 x 16 dp ở góc trên phải. Plan chọn lề phải của cả hàng (sau CTA) 16 dp thay cho 8 dp của thiết kế, cột chữ hẹp đi 8 dp (lệch thiết kế có chủ ý, mục 8, câu 8); hằng `StripPaddingEnd = 16.dp`. Thẻ giả vẽ `AdChoicesMarker` ở góc trên phải. Để chữ không bị cắt khi chiều cao cố định, mỗi chữ có hệ số cỡ chữ trần riêng (đọc `LocalDensity.fontScale`, cỡ hiệu dụng `min(fontScale, trần)`, đặt qua một `CompositionLocalProvider(LocalDensity)` bọc riêng từng chữ): nhãn "Ad" trần `StripBadgeMaxFontScale = 1.0f` (khung nhãn 18 dp chứa đúng dòng 16 sp với lề 1 dp, nên nhãn không lớn theo cỡ chữ hệ thống nhưng luôn thấy đủ); CTA trần `StripCtaMaxFontScale = 2.0f` (dòng 16 sp nhân 2 vừa 32 dp, lề dọc 0); tiêu đề và mô tả trần `StripTextMaxFontScale = 1.4f` (20 + 16 dp nhân 1,4 bằng 50,4 dp, vừa 56 dp). Số trần của tiêu đề và mô tả được chỉnh bằng test ở mục 4 nếu đo khác, không vượt mức làm cột chữ quá 56 dp. Bố cục bản tối dùng đúng vai trò màu (theme của app đã có giá trị tối).

**Bảng khóa.** Sinh lại bằng `./gradlew :app:testStagingDebugUnitTest -PremoteConfigKeys.update=true` (`docs/ops/remote-config.md` mục 3); `git diff -- docs/remote-config-keys.md docs/remote-config-template.json` chỉ có hai khóa mới: `is_show_editor_top_native` (tắt) và `time_reload_editor_top_native` (120).

**`scripts/ads-plan.py`.** Thêm một dòng `native_strip` vào `FORMATS` (mô tả: "Native gọn một tầng, cao 56 dp", chỗ dùng "NativeAdSlot(placement)", nạp lại theo `time_reload_<mã>`, mã chỉ số như `native_small`), để sheet định dạng và sheet vị trí đọc được vị trí mới (dòng đủ 10 cột như các dòng khác); thêm `time_reload_` vào `DERIVED` (khoảng dòng 723) để khóa mới vào nhóm khóa suy ra thay vì "Khóa của app". `read_placements` không cần đọc `reloadSeconds` (giá trị mặc định có ở sheet khóa). `scripts/ads-plan.py` là script của dự án mẫu: thay đổi đi cùng ngoại lệ của ADR mới và issue `from-app`.

Thứ tự commit (`docs/auto-run.md` bước 3): commit test gồm các file test mới của mục 4 và khung rỗng để biên dịch được: `AdFormat.NATIVE_STRIP`, `NativeAdSize.STRIP` (nhánh `when` trả giá trị của `NATIVE_SMALL`, `SMALL`), dòng `EDITOR_TOP_NATIVE` với `reloadSeconds`, `AdsSettings.reloadInterval` trả `null`, `AdsFlags.reloadSeconds` trả `null`. Test thất bại ở câu khẳng định (chiều cao 106 dp thay vì 56, khóa `null`, không nạp lại theo 120 giây). Commit code làm phần còn lại, cập nhật dữ liệu test có sẵn ở mục 3.5, sinh lại bảng khóa, sửa `scripts/ads-plan.py`.

Sau khi merge, người mở issue `from-app` ở repo dự án mẫu khi dải chạy ổn trên bản phát hành (editor mục 5); phiên tự chạy chỉ ghi một dòng vào mục Cần người kiểm tra của báo cáo. Lần chạy `template-sync.sh` kế tiếp cho app đặt `hold=core/ads` theo ADR mới.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdPlacement.kt` | `AdFormat.NATIVE_STRIP`; tham số và trường `reloadSeconds` với `require` trong `init`; dòng `EDITOR_TOP_NATIVE` có KDoc một dòng |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdsSettings.kt` | `reloadInterval(...)` theo mẫu `nativeEvery`; thêm vào `keysFor` |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdsFlags.kt` | `reloadSeconds(placement)` trong interface và cài đặt |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdsController.kt` | `reloadIntervalMillis(placement)` cache theo vị trí, gọi ở `nativeAd`; nhánh `nativeLoadSize()` |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdPreloadPlan.kt` | Nhánh `isNative` |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdTelemetryRules.kt` | Nhánh `metricCode`, `funnelKind`, `loadPlacementOf` |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdUnitIds.kt` | Nhánh `unitFor` (mã thử native) |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/NativeAdPool.kt` | Nhánh `poolCapacity` (`null`) |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/RewardedRules.kt` | Nhánh `fullScreenLoaderOf` |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/NativeAdSources.kt` | `usesSingleAdSource` gồm `NATIVE_STRIP` |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/NativeAdSlot.kt` | `requireNativeFormat` nhận dải; dải luôn giữ chỗ khi cổng mở |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/NativeAdCard.kt` | `NativeAdSize.STRIP`, hằng `Strip*` của `NativeAdDimens`, `StripLayout`, CTA viền, màu nhãn "Ad" của dải, giữ chỗ của dải |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/NativeRequestOptions.kt` | Nhánh `NativeAdSize.STRIP` (không media chính) |
| Tạo | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/NativeStripPlacementTest.kt` | Khai báo, khóa, cờ (mục 4) |
| Tạo | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/NativeAdStripLayoutTest.kt` | Chiều cao, chữ không bị cắt, góc AdChoices, giữ chỗ (mục 4) |
| Tạo | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdsControllerStripReloadTest.kt` | Nạp lại theo khóa của vị trí (mục 4) |
| Tạo | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/NativeAdStripScreenshotTest.kt` | Screenshot thẻ giả và giữ chỗ (mục 4) |
| Tạo | `core/ads/src/test/screenshots/NativeAdStrip_default_light.png` | Ảnh chuẩn mới |
| Tạo | `core/ads/src/test/screenshots/NativeAdStrip_default_dark.png` | Ảnh chuẩn mới |
| Tạo | `core/ads/src/test/screenshots/NativeAdStrip_long-texts_light.png` | Ảnh chuẩn mới |
| Tạo | `core/ads/src/test/screenshots/NativeAdStrip_font-scale-2_light.png` | Ảnh chuẩn mới |
| Tạo | `core/ads/src/test/screenshots/NativeAdStrip_placeholder_light.png` | Ảnh chuẩn mới |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/NativeAdSourcesTest.kt` | Viết lại `singleAdSourceFormats_areSmallAndCollapsible` dưới tên mới theo mục 3.5 (ADR-0031) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/NativeAdCardTest.kt` | Thêm nhánh `NativeAdSize.STRIP -> 56.dp` vào `when (current)` của `placeholder_matchesCardHeight_forEachSize` (mục 3.5) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdTelemetryRulesTest.kt` | Thêm `NATIVE_STRIP` vào hai map kỳ vọng (mục 3.5) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdsSettingsTest.kt` | Hàm `isDerived` coi `time_reload_<mã vị trí>` là khóa suy ra (mục 3.5) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdPlacementTest.kt` | Viết lại câu khẳng định của `galleryPlacements_coverEveryFormatOnce` theo câu mới ở mục 3.5 (ADR-0031) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdFormatTest.kt` | Thêm `"native_strip"` vào tập của `codes_areTheClosedListOfMc02` (mục 3.5) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/FrameloopPlacementsTest.kt` | Thêm `"editor_top_native" to "native_strip"` vào map của `placements_matchSpecTables` (mục 3.5; plan T-007 mục 6 đã báo) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdUnitIdsTest.kt` | Thêm `editor_top_native=unit/editor_top_native` vào `FRAMELOOP_PLACEMENT_UNITS` (mục 3.5) |
| Sửa | `docs/remote-config-keys.md` | Sinh lại bằng lệnh của mục 3.1; không sửa tay |
| Sửa | `docs/remote-config-template.json` | Sinh lại cùng lệnh; không sửa tay |
| Sửa | `scripts/ads-plan.py` | Dòng `native_strip` trong `FORMATS` |

Nơi đọc các file bị đổi (`grep -rn` trong `scripts/`, `.github/`, `*/src/test`):

- `AdPlacement.kt`: `scripts/release.sh` và `AdPlacementSourceTest` (đọc khai báo một dòng; dòng mới đúng mẫu `KEY("key", AdFormat.X, thuộc tính = giá trị)`), `scripts/ads-plan.py` (đọc mã, định dạng, thuộc tính; sửa ở trên), `scripts/tests/run.sh` (dùng tệp mẫu `scripts/tests/fixtures/release/AdPlacement.kt`, không đọc tệp thật). `StagingRemoteValuesModule` bật mọi vị trí không phải toàn app trên staging, kể cả `editor_top_native`; không sửa. Không sửa `scripts/release.sh` (CLAUDE.md, Không được làm); bản production cần mã đơn vị thật cho `editor_top_native` (mục 6).
- Hai file bảng khóa: `app/src/test/kotlin/com/vgl/draw/animation/config/RemoteConfigKeysTest.kt` (so hai file với code; không sửa, vì câu khẳng định vẫn đúng sau khi sinh lại, mục 3.5), `app/build.gradle.kts` (chỉ truyền đường dẫn, không đổi), `scripts/ads-plan.py` (khóa `time_reload_` vào nhóm khóa suy ra; xem lại khi sửa), `scripts/main-guard-verified.sh`, `scripts/tests/run.sh` (chỉ dùng tên tệp).
- `scripts/ads-plan.py`: `scripts/tests/run.sh` có test của script (chạy trong `scripts/check.sh`); test nào đỏ vì dữ liệu chưa có định dạng mới thì cập nhật dữ liệu đó theo ADR-0029.

Không sửa `docs/ops/*` (tài liệu của dự án mẫu, gồm MC-02 của `ads-business-rules.md`), `build-logic/`, `scripts/release.sh`, hay file nào của `:feature:*` (đặt dải lên màn vẽ là T-040).

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có. Vị trí không đọc cờ của feature; T-040 đặt dải theo cờ của màn vẽ.
- Quảng cáo: thêm định dạng `native_strip` và vị trí `editor_top_native` (editor spec mục 5, đã `confirmed`), khóa `is_show_editor_top_native` mặc định tắt, `time_reload_editor_top_native` mặc định 120. Chưa màn nào đặt vị trí. Không đổi định dạng, nhịp hay luật có sẵn nào của `:core:ads`.
- Sự kiện phân tích: Không thêm sự kiện hay tham số. Sự kiện quảng cáo có sẵn mang `ad_placement = editor_top_native` và mã định dạng khi T-040 đặt dải (mục 8, câu 4 về mã chỉ số).
- File R2: Không chạm.
- Dữ liệu trên máy: Không chạm.
- **Quyền truy cập:** Không thêm. Không gọi API Android mới.
- **Thư viện và SDK:** Không thêm. Dùng Compose, `NativeAdView` của SDK AdMob đã có trong `:core:ads`.

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** hệ số cỡ chữ trần của từng chữ (mục 3.1, Bố cục dải) có thể không đủ để mọi chữ vừa 56 dp ở cỡ chữ 2,0; cách bọc `LocalDensity` riêng từng chữ có thể không đổi được cỡ chữ của `Text` trong `ComposeView` của SDK.
- **Điều kiện chuyển:** test `strip_badgeAndCta_notCut_fontScales` vẫn đỏ sau 2 lần sửa vì nhãn hay CTA bị cắt theo chiều cao.
- **Cách làm thay:** nhãn "Ad" và CTA: cỡ chữ tính thẳng từ dp (`with(density) { 11.dp.toSp() }`, `12.dp.toSp()`), không phụ thuộc cỡ chữ hệ thống; cột chữ: bỏ dòng mô tả khi cỡ chữ từ 1,3 trở lên (chỉ còn hàng tiêu đề). Chiều cao 56 dp giữ nguyên; tiêu chí và test ở mục 4 giữ nguyên. Cùng file `NativeAdCard.kt`.

### 3.5. Hành vi đã có bị đổi

`python3 scripts/existing-tests.py docs/features/editor/plans/T-008-dai-native-56dp.md` liệt kê test có sẵn dùng các tên plan đổi (kết quả ở mục 8 của mô tả PR plan). Plan thêm định dạng, vị trí và khóa; không đổi bố cục, chiều cao hay luật của định dạng có sẵn. Các test duyệt `AdFormat.entries`, `AdPlacement.entries` hay `AdsSettings.all()` (như `AdsSettingsTest.placementsAreOffByDefault`, `AdFreeGateTest.adFreeMidSession_everyPlacementSwitchOff`, `StagingRemoteValuesModuleTest.everyAdPlacement_isShown`, `RemoteConfigKeysTest.everyPlacementShowKey_isInTableOff`, `AdPlacementSourceTest.releaseScript_readsEveryShippedPlacement`) tự gồm phần mới và giữ nguyên câu khẳng định.

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Danh sách định dạng đóng đúng 12 mã của MC-02 | Thêm `native_strip` (ngoại lệ của app, ADR mới) | `AdFormatTest.codes_areTheClosedListOfMc02` | Cập nhật dữ liệu: thêm `"native_strip"` vào tập `mc02`, kèm chú thích một dòng "app format, ADR-<số của ADR mới>"; câu so tập bằng nhau giữ nguyên (vẫn chặn định dạng ngoài danh sách) |
| Màn xem thử (gallery, chỉ staging) có một vị trí cho mỗi định dạng trừ lớp thu gọn | Định dạng `native_strip` của app không có vị trí gallery (gallery là phần của dự án mẫu trong `:feature:sample`) | `AdPlacementTest.galleryPlacements_coverEveryFormatOnce` | Câu khẳng định mới (ADR-0031, plan nói rõ ở đây và mục 8, câu 7): `assertEquals(AdFormat.entries.toSet() - AdFormat.NATIVE_COLLAPSIBLE - AdFormat.NATIVE_STRIP, galleryPlacements.map { it.format }.toSet())`, chú thích thêm "app format (ADR of the app) has no gallery placement"; `assertEquals(14, galleryPlacements.size)` và câu mã không trùng giữ nguyên. Ý định giữ: mọi định dạng của dự án mẫu có đúng một vị trí gallery |
| Các test duyệt `AdFormat.entries` hay `NativeAdSize.entries` (`AdPlacementTest` dòng 96, 108, 119; `RewardedRulesTest` dòng 79; `BannerAdSizeTest` dòng 50; `AdFormatTest` dòng 11; `NativeAdCardTest` dòng 374, `placeholder_matchesCardHeight_forEachSize`; `AdFormatPoolTest`; `NativeFlowStepTest`; `BannerLoadsTest`; `AdUnitIdsTest`; `MaxSdkStarterTest`) | Tự gồm `NATIVE_STRIP`, `NativeAdSize.STRIP` | Như cột trái | Giữ nguyên: dải không toàn màn hình, không pool, không banner, không phải bước của luồng, không `ctaLarge`; giữ chỗ của dải cao bằng thẻ (56 dp), nên `placeholder_matchesCardHeight_forEachSize` vẫn đúng và kiểm luôn dải |
| Chỉ `native_small` và lớp thu gọn dùng một nguồn quảng cáo nạp lại tại chỗ | Thêm `native_strip` | `NativeAdSourcesTest.singleAdSourceFormats_areSmallAndCollapsible` (dòng 313 tới 319) | Viết lại dưới tên mới `singleAdSourceFormats_areSmallCollapsibleAndStrip` (ADR-0031, plan nói rõ ở mục 3.1): `assertEquals(listOf(AdFormat.NATIVE_SMALL, AdFormat.NATIVE_COLLAPSIBLE, AdFormat.NATIVE_STRIP), AdFormat.entries.filter { it.usesSingleAdSource() })` (giữ cách lọc của câu cũ); ý định giữ: đúng các định dạng được nạp lại tại chỗ |
| Giữ chỗ cao bằng thẻ cho mọi kích thước | Thêm `STRIP` | `NativeAdCardTest.placeholder_matchesCardHeight_forEachSize` (`when (current)` không có `else`, dòng 379) | Cập nhật dữ liệu: thêm nhánh `NativeAdSize.STRIP -> 56.dp`; câu so chiều cao giữ nguyên |
| Mã chỉ số và loại phễu phủ mọi định dạng | Thêm `NATIVE_STRIP` | `AdTelemetryRulesTest` dòng 38 (`funnelKind`) và dòng 59 (`metricCode_coversEveryFormat`) | Cập nhật dữ liệu: thêm `AdFormat.NATIVE_STRIP to AdFunnelKind.NATIVE` và `AdFormat.NATIVE_STRIP to "NATIVE_SMALL"` (theo câu hỏi 4); tập mã chỉ số (dòng 63 tới 67) và câu so tập giữ nguyên |
| Khóa chung của `AdsSettings` khớp bảng AdsKit, khóa suy ra loại ra theo tiền tố | Thêm khóa suy ra `time_reload_<mã>` | `AdsSettingsTest.globalKeys_matchAdsKitTable` (hàm `isDerived` dòng 32 tới 34) | Cập nhật dữ liệu của hàm phụ trợ (commit code, ADR-0029): thêm điều kiện `name.startsWith("time_reload_") && AdPlacement.entries.any { name == "time_reload_${it.key}" }`, cập nhật chú thích; bảng AdsKit và câu so tập giữ nguyên (`time_reload_native_small` vẫn là khóa chung vì không có vị trí `native_small`) |
| 22 vị trí của Frameloop | Thêm `editor_top_native` | `FrameloopPlacementsTest.placements_matchSpecTables` | Cập nhật dữ liệu: thêm `"editor_top_native" to "native_strip"` vào map; câu so map giữ nguyên |
| Chuỗi mã production gồm mọi vị trí | Thêm `editor_top_native` | `AdUnitIdsTest.configuredUnits_areParsedPerPlacementKey`, `AdUnitIdsTest.missingUnitsWarning_listsMissingKeys`, `AdsControllerUnitIdsTest.start_withAllUnits_noWarning` | Cập nhật dữ liệu: nối `;editor_top_native=unit/editor_top_native` vào hằng `FRAMELOOP_PLACEMENT_UNITS`; danh sách kỳ vọng giữ nguyên |
| Bảng khóa không có hai khóa của dải | Thêm `is_show_editor_top_native`, `time_reload_editor_top_native` | `RemoteConfigKeysTest.keysFile_matchesCode`, `templateFile_matchesCode` | Giữ câu khẳng định; sinh lại hai file |
| `FrameloopPlacementsTest` khẳng định "22 vị trí" ở các test khác (`switches_offByDefault_inKeyList`, `grids_nativeEvery`, ...) | Không đổi | Các test đó dùng danh sách 22 hằng viết thẳng, không lọc `entries` | Giữ nguyên |

Test nào khác đỏ khi chạy test JVM của mọi module ở bước 3 chỉ vì dữ liệu chưa có định dạng hay vị trí mới thì cập nhật dữ liệu theo ADR-0029, ghi vào nhật ký; đỏ vì hành vi thì dừng (mục 7).

## 4. Kiểm thử

Test mới là test JVM của `:core:ads`; test Compose chạy bằng Robolectric `@GraphicsMode(GraphicsMode.Mode.NATIVE)`, thẻ giả qua `NativeAdCard(assets, NativeAdSize.STRIP)` và `FakeLoadedNativeAd` của `T/FakeNativeAds.kt`, cỡ chữ qua `WithFontScale` của `T/FontScaleTestSupport.kt`, kiểm chữ bằng `assertTextsNotCut` hay cách tương tự của `T/NativeAdFontScaleTest.kt`.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| Ticket: khai báo vị trí `editor_top_native` dạng `native_strip` | Unit test | `NativeStripPlacementTest.editorTopNative_isStripWith120s` (test chặn, qua ngay trên khung rỗng vì khung có sẵn dòng khai báo): `EDITOR_TOP_NATIVE.key == "editor_top_native"`, `format == AdFormat.NATIVE_STRIP`, `format.code == "native_strip"`, `declaration.reloadSeconds == 120`, `appWide == false`, `stagingOnly == false`, `nativeEvery == null`; đối chứng: `AdPlacement.entries.filter { it.format == AdFormat.NATIVE_STRIP } == listOf(EDITOR_TOP_NATIVE)` |
| editor mục 5: khóa `time_reload_editor_top_native` mặc định 120, `is_show_editor_top_native` mặc định tắt | Unit test | `NativeStripPlacementTest.reloadKey_derivedFromDeclaration`: `AdsSettings.reloadInterval(EDITOR_TOP_NATIVE)!!.name == "time_reload_editor_top_native"`, `default == 120L`, có trong `AdsSettings.all()`; `AdsSettings.placementEnabled(EDITOR_TOP_NATIVE)` tên `is_show_editor_top_native`, mặc định `false`. Đối chứng: `AdsSettings.reloadInterval(p) == null` với mọi vị trí khác (gồm `GALLERY_NATIVE_SMALL`, `PROJECTS_BOTTOM`) |
| Khai báo sai bị chặn | Unit test | `NativeStripPlacementTest.reloadSeconds_onlyForStrip`: dựng `AdPlacementDeclaration` (hay điểm nối dựng khai báo có sẵn của test) với `NATIVE_SMALL` có `reloadSeconds = 60` ném `IllegalArgumentException`; `reloadSeconds = -1` ném; đối chứng: `NATIVE_STRIP` không có `reloadSeconds` dựng được và `AdsSettings.reloadInterval(declaration) == null` |
| Khóa đọc như `time_reload_native_small` | Unit test | `NativeStripPlacementTest.reloadFlag_readsRemoteValue`: với `AdsFlags` thật trên bộ giá trị giả (như `AdsFlagsTest`): chưa có giá trị thì `reloadSeconds(EDITOR_TOP_NATIVE).first() == 120`; giá trị `45` cho `45`; `10` cho `30`; `0` cho `0`; `-5` cho `120`. Đối chứng: `reloadSeconds(PROJECTS_BOTTOM) == null` |
| KB-19: dải cao 56 dp | Compose UI test | `NativeAdStripLayoutTest.strip_height56dp_fontScales`: ở cỡ chữ 1,0, 1,3, 1,5, 2,0, khung 411 dp, node gốc của thẻ giả `NativeAdCard(sampleNativeAdAssets, NativeAdSize.STRIP)` cao `56.dp` (sai lệch không quá 0,5 dp); đối chứng trong cùng test: `NativeAdSize.SMALL` ở cỡ chữ 2,0 cao hơn 106 dp (chứng tỏ test đo đúng cỡ chữ) |
| KB-19: nhãn "Ad" luôn thấy, chữ CTA không bị cắt ở cỡ chữ lớn | Compose UI test | `NativeAdStripLayoutTest.strip_badgeAndCta_notCut_fontScales`: ở cỡ chữ 1,0 tới 2,0 (bước như trên), khung 411 dp và 360 dp, với `longFullNativeAdAssets` (CTA dài): node tag `BADGE` và `CTA` hiển thị đủ chữ (không `isLineEllipsized`, `getLineRight` không vượt bề rộng node, đáy dòng chữ không vượt đáy node); khẳng định tuyệt đối trong cùng test: thẻ cao 56 dp và đáy node `BADGE`, `CTA` không vượt đỉnh thẻ + 56 dp (khung rỗng vẽ bố cục SMALL cao theo chữ nên đỏ ở câu này). Tiêu đề và mô tả một dòng, được phép cắt "…" |
| editor mục 5: chừa góc AdChoices | Compose UI test | `NativeAdStripLayoutTest.strip_keepsAdChoicesCornerFree`: vùng 16 x 16 dp ở góc trên phải của thẻ không giao với node tiêu đề, nhãn hay CTA (so `boundsInRoot`); thêm: thẻ cao 56 dp và CTA cách mép phải thẻ 16 dp (sai lệch 0,5 dp), để test đỏ trên khung rỗng (bố cục SMALL) |
| editor mục 5: chỉ icon, không ảnh chính; CTA viền | Compose UI test | `NativeAdStripLayoutTest.strip_iconOnly_outlinedCta`: thẻ giả STRIP không có node ảnh chính (tag media của bản nhỏ, `NativeAdAsset.MEDIA` hay tương đương) và có node icon 40 dp; điểm ảnh giữa nền CTA (vẽ `decorView` ra `Bitmap` như CLAUDE.md, bước 10) có màu nền thẻ `surfaceContainerLow`, không phải màu `secondary` (CTA viền, không tô); một điểm ảnh trên viền CTA (giữa cạnh dưới, lệch 0,5 dp vào trong) có màu `secondary`; điểm ảnh trên nét chữ CTA (đo với chữ CTA một ký tự rộng như "W") gần màu `secondary` |
| KB-19: khóa bật thì giữ chỗ cả lúc nạp hay lỗi | Compose UI test | `NativeAdStripLayoutTest.slot_keepsSpace_loadingAndFailed`: `NativeAdSlot(EDITOR_TOP_NATIVE)` với controller giả (theo mẫu của `NativeAdSlotTest`) có cổng mở: lúc đang nạp cao 56 dp; sau khi nạp lỗi (loader giả trả lỗi) vẫn cao 56 dp; sau khi có quảng cáo cao 56 dp. Gọi không truyền `keepSpaceWhenEmpty` |
| KB-19: khóa tắt hay chưa đồng ý thì không giữ chỗ | Compose UI test | `NativeAdStripLayoutTest.slot_noSpace_whenGateClosed`: cùng dựng, khóa `is_show_editor_top_native` tắt thì cao 0 dp; khóa bật mà cổng đồng ý đóng thì cao 0 dp. Đối chứng: bật lại khóa thì cao 56 dp |
| editor mục 5: nạp lại theo `time_reload_editor_top_native` | Unit test | `AdsControllerStripReloadTest.strip_reloadsAtPlacementInterval`: controller với loader giả đếm lượt nạp, đồng hồ ảo (theo mẫu `AdsControllerReloadIntervalTest`), giá trị khóa mặc định: sau lần nạp đầu, tới 119 giây chưa nạp lại, tới 120 giây nạp lần 2; đặt `time_reload_editor_top_native = 0` thì không nạp lại sau 600 giây; đặt `time_reload_native_small = 30` không đổi nhịp của dải (vẫn 120). Sau mỗi lần lấy quảng cáo của dải không có lượt nạp sẵn (`PRELOAD`) nào cho định dạng `native_strip` (mục 3.1, Nạp lại). Đối chứng: một vị trí `native_small` vẫn nạp lại theo `time_reload_native_small` |
| Giao diện dải | Screenshot test | `NativeAdStripScreenshotTest`, khung `w411dp-h891dp-xxhdpi`, thẻ giả trong `AdsTestTheme`: `NativeAdStrip_default_light.png`, `NativeAdStrip_default_dark.png`, `NativeAdStrip_long-texts_light.png` (chữ dài), `NativeAdStrip_font-scale-2_light.png` (cỡ chữ 2,0), `NativeAdStrip_placeholder_light.png` (`NativeAdPlaceholder(NativeAdSize.STRIP, animated = false)`) |
| Màn hình thấp | Screenshot test khung `w360dp-h640dp-xxhdpi` | Không áp dụng riêng: dải không phải màn hình; bề rộng 360 dp đã kiểm ở `strip_badgeAndCta_notCut_fontScales`. Ảnh cả màn vẽ ở khung thấp là việc của T-040 |
| Đúng thiết kế | Screenshot test | Ảnh thiết kế của ticket khác (T-040): `EditorScreen_drawing_light`, `EditorScreen_drawing_dark`, `EditorScreen_drawing_light_unfolded`, `EditorScreen_drawing_dark_unfolded`, `EditorScreen_drawing-no-ad_light`, `EditorScreen_drawing-no-ad_dark`; dải trong các ảnh đó dùng số liệu của mục 3.1. Rà soát độc lập so `NativeAdStrip_default_light.png` với vùng dải của `design/screens/EditorScreen_drawing_light.png` bằng mắt |
| Mở issue `from-app` khi chạy ổn trên bản phát hành | Kiểm tra thủ công (người: việc ở repo dự án mẫu, không phải chạy app) | Người mở issue `from-app` (`docs/ops/from-app.md`) sau khi dải chạy ổn trên bản phát hành |

Không có kịch bản emulator: chưa màn nào đặt dải (T-040), người dùng staging không thấy gì mới. Không có test dựng lại activity hay đưa app ra nền: plan không thêm trạng thái ở vỏ app, không có bước chờ trước khi hiện; khóa đổi giữa chừng có ở `slot_noSpace_whenGateClosed` (bật lại khóa) và `strip_reloadsAtPlacementInterval` (đổi khoảng nạp lại).

**Ngoại lệ viết test trước:** hai file bảng khóa sinh bằng lệnh (`RemoteConfigKeysTest` có sẵn kiểm); dòng `native_strip` của `scripts/ads-plan.py` (test của script trong `scripts/tests/run.sh` kiểm khi chạy `check.sh`); KDoc; nhánh `when` của các file chỉ thêm định dạng vào chỗ liệt kê (biên dịch kiểm). Ba test cập nhật dữ liệu ở mục 3.5 vào commit code.

### 4.1. Chạy thử

Bắt buộc (E bằng 2, mục 5). Chạy thử: chưa chạy xong (phiên tự chạy RUN-20261122-03 soạn plan trong giới hạn 45 phút, không đủ thời gian cho một lần `scripts/check.sh` với khung rỗng). Hai điểm nên chạy thử trước khi duyệt nằm ở mục 8, câu 5.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 1 (quảng cáo: định dạng và vị trí mới), 7 (module core dùng chung `:core:ads`, ngoài `AdPlacement.kt`: phần mà app không được sửa theo ADR-0030 và ADR-0045 mục 4.9). Không chạm điểm 4 (không thêm sự kiện), điểm 5 (cổng đồng ý và thứ tự khởi tạo SDK không đổi), điểm 9 (không thêm khóa DataStore).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 2 | 13 file chính của `:core:ads`, phần lớn chỉ thêm một nhánh `when`; thay đổi thật ở `NativeAdCard.kt` (bố cục mới), `NativeAdSlot.kt` (giữ chỗ), `AdsController.kt`, `AdsSettings.kt`, `AdsFlags.kt` (khóa và nạp lại theo vị trí) |
| B. Độ rõ của yêu cầu | 1 | Spec và thiết kế có số liệu đủ; còn chưa rõ cách giữ chữ vừa 56 dp ở cỡ chữ 2,0 và mã chỉ số (mục 8) |
| C. Khả năng kiểm chứng tự động | 1 | Khai báo, khóa, chiều cao, chữ, giữ chỗ, nạp lại có test JVM; hiển thị với quảng cáo thật (AdChoices của SDK) chỉ kiểm được trên máy khi T-040 đặt dải |
| D. Ảnh hưởng chéo | 2 | Mọi vị trí native dùng `NativeAdSlot`, `NativeAdCard`, `AdsController`; định dạng mới lệch dự án mẫu, ảnh hưởng mọi lần `template-sync.sh` (`hold=core/ads`) |
| E. Độ mới của kỹ thuật | 2 | Bố cục, khóa theo vị trí và nạp lại tại chỗ theo mẫu có sẵn; nhưng giới hạn hệ số cỡ chữ riêng từng chữ trong thẻ native cao cố định là cách làm mới trong `:core:ads` |
| **Tổng** | 8 | **Mức: Cao** (chạm điểm chặn 1 và 7) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `AdPlacement`, `AdsSettings`, `AdsFlags`, `AdsController`, `NativeAdSlot`, `NativeAdCard` của `:core:ads`; hai file bảng khóa; `scripts/ads-plan.py`.
- Task khác có thể xung đột:
  - T-007 (đã merge): `FrameloopPlacementsTest`, `AdUnitIdsTest` cập nhật dữ liệu như mục 3.5.
  - T-040 (đặt dải lên màn vẽ, ảnh thiết kế của màn vẽ): phụ thuộc T-008; dùng `NativeAdSlot(AdPlacement.EDITOR_TOP_NATIVE)`.
  - Mọi ticket thêm vị trí sau này cùng sửa `AdPlacement.kt` và sinh lại bảng khóa; giải xung đột bằng lệnh sinh trên nhánh mới nhất.
  - `template-sync.sh`: từ khi T-008 merge, mọi lần chạy cho app đặt `hold=core/ads` tới khi dự án mẫu nhận định dạng (ADR mới). Khối heap của ADR-1001 cũng nằm trong `core/ads`.
- Bản staging: `StagingRemoteValuesModule` bật `editor_top_native`; chưa màn nào đặt nên không nạp gì (dải không dùng pool hay kho nạp sẵn).
- Bản production: `scripts/release.sh` đòi mã đơn vị thật cho `editor_top_native` (native, tắt tự làm mới trên AdMob vì app tự nạp lại theo khóa).
- Đề xuất thứ tự chạy: sau T-007 (đã merge), trước T-040.

## 7. Điều kiện dừng riêng

- ADR của app ghi ngoại lệ MC-02 cho `native_strip` chưa `accepted` trên nhánh chính khi task bắt đầu: dừng.
- Lệnh sinh lại bảng khóa đổi dòng nào khác ngoài `is_show_editor_top_native` và `time_reload_editor_top_native`: dừng.
- Một test có sẵn của `:core:ads` đỏ vì hành vi của định dạng có sẵn (chiều cao, giữ chỗ, nạp lại của `native_small`, `native_medium`, ...), không phải vì dữ liệu chưa có phần mới: dừng theo điều kiện 3, không sửa test.
- Một ảnh chuẩn screenshot có sẵn của `:core:ads` đổi: dừng (plan không cho ghi lại ảnh có sẵn).
- Làm task phải sửa file của `:feature:*`, `build-logic/` hay `docs/ops/*`: dừng.

## 8. Câu hỏi cho người duyệt

1. **ADR của app cho ngoại lệ MC-02.** Spec (editor mục 5) và ticket đòi một ADR của app (dải 1001 trở lên). Plan dừng khi ADR chưa `accepted` (mục 7). ADR-1001 (mục Việc AI phải tuân thủ) cấm ngoại lệ khác ngoài khối heap và cấm `hold=core/ads`, nên ADR mới phải thay quy tắc đó. Phiên tự chạy không soạn ADR (chưa kịp trong giới hạn 45 phút). Phương án AI đề xuất: người (hay AI trong phiên có người theo dõi) viết ADR-1002 trong PR của plan này, sinh lại `docs/foundation.md`, nội dung: app thêm định dạng `native_strip` và khóa nạp lại theo vị trí trong `:core:ads`, là ngoại lệ tạm của MC-02 và ADR-0030; mọi lần `template-sync.sh` đặt `hold=core/ads` tới khi dự án mẫu nhận; mở issue `from-app` khi chạy ổn trên bản phát hành; ghi cả `scripts/ads-plan.py` là phần lệch mẫu (`hold=core/ads` không che `scripts/`). Người duyệt chấp nhận ADR cùng lời duyệt plan, như ADR-1001 với T-007. Lưu ý: ADR-1001 ghi "không dùng `hold=core/ads`" cho khối heap; ADR-1002 đòi `hold=core/ads` cho cả module, nên khối heap của ADR-1001 cũng bị giữ cùng.
2. **Tên định dạng.** AI đề xuất `native_strip` (`AdFormat.NATIVE_STRIP`, `NativeAdSize.STRIP`), theo tên thành phần `NativeAdStrip` của thiết kế. Có muốn tên khác không (ví dụ `native_banner_compact`)?
3. **Có đưa `native_strip` vào `IN_CONTENT_FORMATS` (MC-13) không?** MC-13 chặn lớp nổi (native thu gọn của thanh đáy, banner thu gọn) hiện cạnh vị trí trong nội dung. Màn vẽ không có thanh đáy nên hiện không có khác biệt. AI đề xuất không thêm (dải là một khe ở đầu màn, không phải nội dung), để định dạng mới chạm ít luật có sẵn nhất.
4. **Mã chỉ số (`metricCode`) và nhóm số liệu của định dạng mới.** AI đề xuất dùng chung mã của `native_small` (`NATIVE_SMALL`) để sự kiện `af_native_*` và bảng số liệu có sẵn đọc được ngay; hoặc một mã riêng `NATIVE_STRIP` (rõ hơn khi xem doanh thu, nhưng phải thêm vào bảng sự kiện của `docs/ops/ads-business-rules.md`, tài liệu của dự án mẫu). Mã riêng có thể chạm điểm chặn 4 (sự kiện phân tích).
5. **Chạy thử trước khi duyệt.** Phiên chưa chạy thử (mục 4.1). Nên chạy trước khi duyệt, trên một nhánh nháp có khung rỗng của mục 3.1: (a) `NativeAdStripLayoutTest.strip_badgeAndCta_notCut_fontScales` với bố cục 56 dp để tìm `StripMaxFontScale` và xem phương án dự phòng (mục 3.4) có cần không; (b) `scripts/check.sh` để thấy test nào của `scripts/tests/run.sh` (test của `scripts/ads-plan.py`) và của `:core:ads` đỏ vì dữ liệu, rồi bổ sung mục 3.2, 3.5. Có muốn phiên có người theo dõi chạy hai việc này rồi cập nhật plan không?
6. **Rà soát độc lập (trong phiên, sau bản đầu):** 7 chặn, 7 nên sửa; đã sửa một lần trong plan (mã thử native, `require` của dải, `NativeAdSourcesTest`, `NativeAdCardTest`, `AdTelemetryRulesTest`, `AdsSettingsTest`, thân mặc định của `AdsFlags.reloadSeconds`, hệ số trần từng chữ và phương án dự phòng cho nhãn, lề AdChoices, `ads-plan.py`, E thành 2). Điểm còn lại: ADR-1002 chưa viết (câu 1). Nên rà soát lại sau khi chạy thử câu 5.
7. **Vị trí gallery cho định dạng mới.** `AdPlacementTest.galleryPlacements_coverEveryFormatOnce` đòi mỗi định dạng có một vị trí gallery. AI đề xuất viết lại câu khẳng định để loại `NATIVE_STRIP` (mục 3.5), vì gallery thuộc `:feature:sample` của dự án mẫu và dải chưa có màn nào. Phương án khác: thêm `GALLERY_NATIVE_STRIP` và một ô trong màn gallery của `:feature:sample` (thêm file ngoài `:core:ads`, ảnh chuẩn gallery đổi), để xem dải với quảng cáo thử trên staging trước T-040.

8. **Lệch thiết kế ở lề phải để chừa AdChoices.** Theo thiết kế, CTA cách đỉnh dải 12 dp và mép phải 9 dp, lấn ô 16 x 16 dp mà AdChoices của SDK cần (O-08). Plan chọn (a): lề phải của hàng 16 dp thay 8 dp, cột chữ hẹp đi 8 dp. Phương án khác: (b) CTA canh đáy dải (đỉnh CTA cách đỉnh dải 24 dp), giữ lề phải 8 dp; (c) giữ đúng thiết kế, chấp nhận AdChoices đè góc CTA (trái O-08, AI không đề xuất). Người duyệt chọn và ghi vào mục 9.

## 9. Quyết định duyệt

- **Quyết định:**
- **Người duyệt:** , **duyệt chéo:**
- **Ghi chú:**

## 10. Nhật ký thực hiện
