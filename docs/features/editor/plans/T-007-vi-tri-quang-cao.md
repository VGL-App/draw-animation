---
ticket: T-007
title: Vị trí quảng cáo của Frameloop
status: stopped                 # draft | approved | stopped | done
version: 2                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 15M          # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [ads, core] # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-007: VỊ TRÍ QUẢNG CÁO CỦA FRAMELOOP

## 1. Tóm tắt

Khai báo 22 vị trí quảng cáo của sáu feature trong `AdPlacement` của `:core:ads`, mỗi vị trí một dòng theo bảng 9.1 của bản đề xuất và mục 5 của sáu spec; `native_onboarding` chỉ ở lần mở đầu, hai ô lưới có `nativeEvery`, bốn vị trí rewarded dùng `allow_after_2`. Mỗi vị trí chỉ bật tắt bằng khóa `is_show_<mã>` (mặc định tắt) và luật có sẵn của `:core:ads`; không thêm luật riêng nào. Kiểm chứng bằng unit test của khai báo, cập nhật dữ liệu của 5 test có sẵn, và bảng khóa sinh lại.

**Phiên bản 2 (soạn ở RUN-20261122-02 sau khi task dừng, `draft`, chờ người duyệt):**

- Vì sao phiên bản 1 chưa đủ tốt (theo cách hiểu của phiên): code của plan đúng và `scripts/check.sh` qua trên máy (Java 21), nhưng `pr-check` (Java 17) đỏ hai lần ở `:core:ads:testDebugUnitTest` vì `OutOfMemoryError` trong screenshot test có sẵn. `AdsController` dựng chỗ nạp sẵn cho mọi vị trí, nên thêm 22 vị trí làm các test của module tốn thêm bộ nhớ, trong khi heap của test JVM là mặc định và `build-logic/` (nơi đặt heap) nằm ngoài plan. Plan chưa lường giới hạn bộ nhớ của test `:core:ads` trên CI.
- Thay đổi so với phiên bản 1: thêm điều kiện trước khi chạy (mục 3.1, Cách tiếp tục): giới hạn bộ nhớ của test `:core:ads` trên CI đã được sửa trên nhánh chính bằng một thay đổi riêng do người quyết (câu hỏi 2 ở mục 8). Phần code, test và danh sách file giữ nguyên phiên bản 1.

## 2. Hiểu yêu cầu

Ticket xử lý phần khai báo vị trí và chính sách khi không có quảng cáo của các kịch bản sau trong `features/editor/spec.md`. Lời mời, thẻ mời, hộp thoại và hành động mở khóa là của T-023 (cọ), T-028 (layer), T-034 (âm thanh); ticket này chỉ cho chúng vị trí và chính sách.

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-3 | Bấm "Watch ad" và xem hết thì 4 cọ mở 24 giờ; đóng trước khi được thưởng thì cọ vẫn khóa; cần đúng 1 lượt xem | Vị trí `editor_rw_brushes`, định dạng `rewarded`. Đóng sớm thì luôn chặn là luật của `:core:ads` (R-02, R-04), không chỉnh được. Mục 5 spec editor và câu hỏi 4: không có quảng cáo thì `allow_after_2` (lần thử thứ 2 liên tiếp không có quảng cáo thì mở luôn). Phần 24 giờ do T-023 làm |
| KB-7 | Bấm "Watch ad" và xem hết thì layer thứ 6 được thêm, dự án dùng tới 10 layer; đóng sớm thì không thêm | Vị trí `editor_rw_layer`, `rewarded`, `allow_after_2`. Phần "cả dự án" do T-028 làm |
| KB-14 | Xem hết thì mọi gói hiệu ứng mở 24 giờ; đóng sớm thì vẫn khóa | Vị trí `editor_rw_sounds`, `rewarded`, `allow_after_2`. Phần 24 giờ do T-034 làm |
| Mục 5 spec editor: khóa `is_show_<mã>` tắt thì hành động làm thẳng (MC-05) | Khóa tắt thì không mời | Hành vi có sẵn của `rememberRewardedAction`; khóa của 4 vị trí mặc định tắt (MC-03) |
| Ticket, phạm vi | 22 vị trí, khóa `is_show_<mã>`, nhóm A, B; cập nhật `docs/remote-config-keys.md` và test của `:core:ads` | Nhóm A, B là kế hoạch bật trên Firebase, không phải code: ghi trong chú thích của từng dòng (`scripts/ads-plan.py` đưa chú thích vào cột ghi chú của file Excel). Khóa suy ra từ khai báo; bảng khóa sinh lại |
| Quyết định của anh Nhật, 2026-11-22 | Bỏ luật "trước nét vẽ đầu tiên": quảng cáo chỉ theo `is_show_<mã vị trí>` và các khóa, luật có sẵn | Plan không thêm cổng hay điểm nối nào, không đọc mốc `first_stroke_drawn` của T-005 (mốc giữ làm dữ liệu dự phòng, không ai đọc). Spec và ticket được sửa riêng theo quyết định này |

Định dạng chọn theo cột Định dạng của từng spec, đối chiếu danh sách định dạng đóng của `:core:ads` (MC-02, `ads-guide.md` mục 5):

| Vị trí | Định dạng | Thuộc tính |
| --- | --- | --- |
| `native_onboarding` | `NATIVE_STEP` | `firstOpenOnly = true` (spec onboarding mục 5) |
| `projects_empty_native` | `NATIVE_MEDIUM` | |
| `projects_bottom` | `NATIVE_SMALL` (thanh đáy) | |
| `projects_grid` | `NATIVE_IN_COLLECTION` (ô trong lưới) | `nativeEvery = 6` |
| `restore_mrec` | `BANNER_MREC` | |
| `create_inter` | `INTERSTITIAL` | nhóm giãn cách mặc định (`ads_interval`) |
| `new_project_native` | `NATIVE_MEDIUM` | |
| `settings_bottom` | `NATIVE_SMALL` (thanh đáy) | |
| `editor_exit_inter` | `INTERSTITIAL` | nhóm mặc định |
| `editor_rw_layer` | `REWARDED` | `noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)` |
| `editor_rw_brushes` | `REWARDED` | `noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)` |
| `editor_rw_sounds` | `REWARDED` | `noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)` |
| `export_options_mrec` | `BANNER_MREC` | |
| `export_wait_mrec` | `BANNER_MREC` | |
| `export_done_native` | `NATIVE_MEDIUM` | |
| `export_interstitial` | `INTERSTITIAL` | nhóm mặc định |
| `export_rw_extra` | `REWARDED` | `noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)` (spec export câu 14) |
| `lessons_grid` | `NATIVE_IN_COLLECTION` (ô trong lưới) | `nativeEvery = 4` |
| `lessons_bottom` | `NATIVE_SMALL` (thanh đáy) | |
| `lesson_start_inter` | `INTERSTITIAL` | nhóm mặc định |
| `lesson_done_native` | `NATIVE_MEDIUM` | |
| `lesson_next_inter` | `INTERSTITIAL` | nhóm mặc định |

Hai ô lưới dùng `native_in_collection` theo câu trả lời của anh Nhật (mục 8). `ads_interval` 30 giây (Q16) là giá trị đặt trên Firebase, không đổi mặc định 45 giây của code (bản đề xuất 9.1). "Từ dự án thứ 2" của `create_inter`, "mở 24 giờ", "cả dự án", `keepSpaceWhenEmpty` của `new_project_native` và khung 250 dp của `export_options_mrec` là việc của ticket màn hình, không phải thuộc tính khai báo. `editor_top_native` và định dạng 56 dp là của T-008.

Thiết kế tham chiếu: Không có thiết kế riêng (ticket không có màn hình; vùng quảng cáo trên thiết kế của từng feature thuộc ticket màn hình, docs/ops/design-workflow.md).

ADR liên quan: ADR-0045 (quảng cáo, MC-01 tới MC-05; mục 4.9: thêm vị trí không thêm code vào `:core:ads`), ADR-0046 (Remote Config, khóa suy ra), ADR-0030 (`AdPlacement.kt` là phần app được sửa trong `:core:ads`).

## 3. Giải pháp

### 3.1. Cách làm

Làm theo `docs/ops/ads-guide.md` mục 5, bước 2 và 3. Chỉ sửa `AdPlacement.kt`, file mà app được sửa trong `:core:ads` (`docs/ops/to-app.md` mục 2); không thêm code nào khác vào `:core:ads`, đúng ADR-0045 mục 4.9.

Thêm 22 dòng ngay sau `RESUME_INTER`, trước khối `gallery_*`, theo thứ tự của bảng ở mục 2. Mỗi dòng một khai báo mà `scripts/release.sh` đọc được, dưới 120 ký tự (dòng dài nhất `EDITOR_RW_BRUSHES` khoảng 110 ký tự). Chính sách viết thẳng `noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)` trên dòng, không đặt thành hằng, vì `scripts/ads-plan.py` chỉ đọc chính sách viết trên dòng. Mỗi dòng có một dòng KDoc ngay trên, nói màn hình, nhóm (A hay B) và spec; dòng rewarded ghi thêm hành động nó giữ (`ads-guide.md` mục 5). Ví dụ:

```kotlin
/** Projects list with projects, bottom bar (projects spec, section 5). Group A. */
PROJECTS_BOTTOM("projects_bottom", AdFormat.NATIVE_SMALL),
/** Editor, unlocks the pro brushes for 24 hours (T-023). Group A. allow_after_2 (editor spec, question 4). */
EDITOR_RW_BRUSHES("editor_rw_brushes", AdFormat.REWARDED, noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)),
```

Không đổi dòng có sẵn nào. Khóa tự suy ra: 22 khóa `is_show_<mã>` (mặc định tắt) và hai khóa `native_every_projects_grid` (6), `native_every_lessons_grid` (4); không có nhóm giãn cách riêng nên không có khóa `ads_interval_<nhóm>`. Sinh lại bảng khóa bằng `./gradlew :app:testStagingDebugUnitTest -PremoteConfigKeys.update=true` (`docs/ops/remote-config.md` mục 3), rồi `git diff -- docs/remote-config-keys.md docs/remote-config-template.json` chỉ có 24 khóa mới.

Không có log, ViewModel hay màn hình mới. Không thêm luật nào ngoài khóa của vị trí và luật có sẵn của `:core:ads` (quyết định bỏ luật trước nét vẽ đầu tiên, mục 8).

Thứ tự commit (test viết trước, `docs/auto-run.md` bước 3): bước 3 commit `FrameloopPlacementsTest.kt`, đỏ vì chưa biên dịch được (thiếu 22 hằng của `AdPlacement`). Bước 4 viết 22 dòng, cập nhật dữ liệu của 5 test có sẵn theo mục 3.5 (chúng chỉ đỏ khi 22 dòng có mặt, nên vào cùng commit code), sinh lại bảng khóa.

**Cách tiếp tục (phiên bản 2):** tiếp tục trên nhánh cũ `claude/T-007-vi-tri-quang-cao`, PR #20 (commit test d2faad9, commit code c76d8bf). Điều kiện trước khi chạy: thay đổi sửa giới hạn bộ nhớ của test `:core:ads` trên CI đã merge vào nhánh chính và `main-guard` của nó xanh (dev kiểm trước phiên). Phiên cập nhật nhánh theo nhánh chính (`git fetch origin main`, `git merge origin/main`), sinh lại hai file bảng khóa nếu nhánh chính đã đổi chúng (lệnh ở trên), chạy `scripts/check.sh`, rồi đi tiếp từ bước 6 (rà soát độc lập cho phần đổi so với lần trước) và bước 8 (CI). Không viết lại test của bước 3: test viết trước giữ nguyên, đã thất bại đúng lý do ở phiên bản 1. CI vẫn đỏ vì `OutOfMemoryError` ở `:core:ads` thì dừng như phiên bản 1, không tự sửa `build-logic/`.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdPlacement.kt` | 22 dòng vị trí với KDoc một dòng (mục 3.1); không đổi dòng có sẵn |
| Tạo | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/FrameloopPlacementsTest.kt` | Test khai báo 22 vị trí (mục 4) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdPlacementTest.kt` | Cập nhật dữ liệu của `inListGalleryPlacements_declareNativeEvery8` (mục 3.5, ADR-0029) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdPreloadPlanTest.kt` | Cập nhật dữ liệu của `fromPlacements_hasRewardedInSecondGroup` (mục 3.5) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdUnitIdsTest.kt` | Thêm hằng `internal const val FRAMELOOP_PLACEMENT_UNITS` cấp file, nối vào chuỗi mã của hai test (mục 3.5) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdsControllerUnitIdsTest.kt` | Nối `FRAMELOOP_PLACEMENT_UNITS` vào `PLACEMENT_UNITS` (mục 3.5) |
| Sửa | `docs/remote-config-keys.md` | Sinh lại bằng lệnh của mục 3.1; không sửa tay |
| Sửa | `docs/remote-config-template.json` | Sinh lại cùng lệnh; không sửa tay |

Nơi đọc các file bị đổi (`grep -rn` trong `scripts/`, `.github/`, `*/src/test`):

- `AdPlacement.kt`: `scripts/release.sh` và `AdPlacementSourceTest` (đọc khai báo một dòng; 22 dòng mới đúng mẫu, lần thử ở mục 4.1 qua), `scripts/ads-plan.py` (đọc mã, định dạng, `nativeEvery`, chính sách, `firstOpenOnly` và chú thích; lần thử đọc đủ 22 dòng). `StagingRemoteValuesModule` bật mọi vị trí không phải toàn app trên staging, kể cả 22 vị trí mới, không phải sửa (ảnh hưởng ở mục 6). Không sửa `scripts/release.sh` (CLAUDE.md, Không được làm). Bản production cần 22 mã đơn vị thật (mục 6).
- `docs/remote-config-keys.md`, `docs/remote-config-template.json`: `app/src/test/kotlin/com/vgl/draw/animation/config/RemoteConfigKeysTest.kt` (so hai file với code; không sửa, vì câu khẳng định vẫn đúng sau khi sinh lại, mục 3.5), `app/build.gradle.kts` (truyền đường dẫn, không đổi), `scripts/ads-plan.py` (khóa `is_show_`, `native_every_` vào nhóm khóa suy ra, không sửa), `scripts/main-guard-verified.sh` (chỉ phân loại tệp), `scripts/tests/run.sh` (chỉ dùng tên tệp trong dữ liệu thử của script, không đọc nội dung).

Không sửa `docs/ops/*` (tài liệu của dự án mẫu) hay file chính nào khác của `:core:ads`. Không đọc hay sửa file của T-005.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có. Vị trí không đọc cờ của feature; màn nào đặt vị trí thì đi theo cờ của màn đó (ticket màn hình).
- Quảng cáo: thêm 22 vị trí của bảng 9.1 bản đề xuất và mục 5 của sáu spec (mục 2; đang `changed` sau khi bỏ luật trước nét vẽ đầu tiên, phải được xác nhận lại cùng lời duyệt đợt trước khi duyệt hay chạy), mọi khóa mặc định tắt; `allow_after_2` cho bốn vị trí rewarded (editor câu 4, export câu 14). Chưa màn nào đặt vị trí; ticket màn hình đặt chúng. Không đổi định dạng, nhịp hay luật nào của `:core:ads`.
- Sự kiện phân tích: Không thêm sự kiện hay tham số. Các sự kiện quảng cáo có sẵn sẽ mang giá trị `ad_placement` mới là mã vị trí của spec khi màn hình đặt vị trí; plan này chưa sinh sự kiện nào.
- File R2: Không chạm.
- Dữ liệu trên máy: Không chạm.
- **Quyền truy cập:** Không thêm. Không gọi API Android mới.
- **Thư viện và SDK:** Không thêm. Chỉ thêm dòng khai báo enum; test dùng JUnit 4 mà convention plugin đã thêm.

### 3.4. Phương án dự phòng

Không có. Khai báo vị trí là mẫu có sẵn; phần dễ vướng nhất (thêm vị trí làm test có sẵn đỏ) đã thử ở mục 4.1.

### 3.5. Hành vi đã có bị đổi

`python3 scripts/existing-tests.py docs/features/editor/plans/T-007-vi-tri-quang-cao.md` liệt kê test có sẵn dùng tên `AdPlacement` và các tên plan nêu. Lần thử ở mục 4.1 (thêm đúng 22 dòng trên `origin/main` 5ea6ec0) cho đúng 7 test đỏ, đều chỉ vì dữ liệu của test chưa có 22 vị trí mới: 5 test ở bảng dưới cần cập nhật dữ liệu theo ADR-0029, quyết định 2 (test vẫn kiểm phần mới như phần cũ), và 2 test của `RemoteConfigKeysTest` qua sau khi sinh lại bảng khóa.

13 test của nhóm "Cần xem trước" (đo vị trí, kích thước) dùng vị trí gallery hay `sample_native_small`, nên không đổi: `AdsControllerNativeStockTest.configurationChange_layerGoes_adDestroyed_logged`, `AnchoredAdWiringTest.firstNoUnit_secondRuns`, `BannerAdSizeTest.admobOrder_withMaxKey_noMaxPlatform_heightIsAdmob`, `BannerAdSizeTest.bottomBannerSlot_reportsAnchoredHeight`, `BottomBarSlotWiringTest.bottomBannerSlot_failedBeforeAd_reportsNoHeight`, `InListAdsTest.grid_fullWidthRow_poolExhausted_keepsZeroHeightRow`, `InListAdsTest.lazyColumn_sevenItemsEveryThree_adRowsAfterItems3And6`, `InListAdsTest.positions_followEvery`, `NativeAdCellTest.mediumSlot_recordsShownOnce_notReadyOnDispose`, `NativeAdCellTest.otherFormat_isRejected`, `NativeAdSlotTest.withoutAdsController_takesNoSpace`, `NativeFullScreenOverlayTest.smallScreen_countdownAndCtaInside`, `RewardedActionTest.realController_adFreeWhileDialogOpen_watchRunsAction`. Cả 13 qua trong lần thử. Các test của nhóm "Nên xem" duyệt `AdPlacement.entries` hay dùng hằng có sẵn (như `AdFreeGateTest.adFreeMidSession_everyPlacementSwitchOff`, `AdsSettingsTest.placementsAreOffByDefault`, `AdPlacementTest.appWidePlacements_areOpenAppAndCombo`, `AdUnitIdsTest.placementKeys_neverSlotNames`, `StagingRemoteValuesModuleTest.everyAdPlacement_isShown`, `RemoteConfigKeysTest.everyPlacementShowKey_isInTableOff`, test của `:feature:loading`) qua trong lần thử, trừ 5 test ở bảng dưới, và giữ nguyên câu khẳng định.

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Chỉ ba vị trí gallery khai báo `nativeEvery` | `projects_grid` khai báo 6, `lessons_grid` khai báo 4 | `AdPlacementTest.inListGalleryPlacements_declareNativeEvery8` (đỏ: `projects_grid expected null, but was:<6>`) | Cập nhật dữ liệu: thêm `assertEquals(6, AdPlacement.PROJECTS_GRID.nativeEvery)`, `assertEquals(4, AdPlacement.LESSONS_GRID.nativeEvery)` và hai hằng đó vào tập `inList`; câu "mọi vị trí khác có `nativeEvery` null" giữ nguyên |
| Chỉ `gallery_rw_action` là rewarded | Thêm bốn vị trí rewarded | `AdPreloadPlanTest.fromPlacements_hasRewardedInSecondGroup` (đỏ ở câu đối chứng: bỏ `GALLERY_RW_ACTION` vẫn còn REWARDED) | Cập nhật dữ liệu của câu đối chứng: `AdPreloadPlan.fromPlacements(AdPlacement.entries - setOf(AdPlacement.GALLERY_RW_ACTION, AdPlacement.EDITOR_RW_LAYER, AdPlacement.EDITOR_RW_BRUSHES, AdPlacement.EDITOR_RW_SOUNDS, AdPlacement.EXPORT_RW_EXTRA))` không có `AdFormat.REWARDED`; ý định giữ: REWARDED chỉ đến từ vị trí rewarded, danh sách bỏ ra viết thẳng như câu cũ. Ba câu khẳng định chính giữ nguyên |
| Chuỗi mã production gồm đủ mọi vị trí có sẵn | Thêm 22 vị trí production cần mã | `AdUnitIdsTest.configuredUnits_areParsedPerPlacementKey`, `AdUnitIdsTest.missingUnitsWarning_listsMissingKeys`, `AdsControllerUnitIdsTest.start_withAllUnits_noWarning` (đỏ: cảnh báo thiếu 22 mã) | Cập nhật dữ liệu: hằng cấp file `internal const val FRAMELOOP_PLACEMENT_UNITS` trong `AdUnitIdsTest.kt`, một chuỗi viết thẳng (không dựng bằng `joinToString`, để `const val PLACEMENT_UNITS` của `AdsControllerUnitIdsTest` nối được) gồm 22 cặp `<mã>=unit/<mã>` theo thứ tự mục 2, cách nhau `;`. Nối vào chuỗi đầu và hai chuỗi đối chứng của `configuredUnits_areParsedPerPlacementKey`, vào `placementUnits` của `missingUnitsWarning_listsMissingKeys`, và vào `PLACEMENT_UNITS` của `AdsControllerUnitIdsTest`. Danh sách kỳ vọng (`banner`, `native_collap`, các vị trí toàn app) giữ nguyên |
| Bảng khóa không có khóa của 22 vị trí | Thêm 22 khóa `is_show_*` và 2 khóa `native_every_*` | `RemoteConfigKeysTest.keysFile_matchesCode`, `templateFile_matchesCode` | Giữ câu khẳng định; sinh lại hai file bằng lệnh của mục 3.1 |

Chú thích trong KDoc của `AdsControllerFirstOpenOnlyTest` và `AdsSettingsTest.placementEnabled_firstOpenOnly_saysSo` nói dự án mẫu chưa khai báo vị trí `firstOpenOnly`; sau plan app có `native_onboarding`. Test vẫn đúng (chúng tự đặt điểm nối test hay duyệt mọi vị trí), chỉ chú thích cũ; không sửa, để `:core:ads` lệch mẫu ít nhất.

## 4. Kiểm thử

Test mới là unit test JVM của `:core:ads` (không cần Robolectric), trong `FrameloopPlacementsTest`. "22 vị trí" trong test là danh sách 22 hằng viết thẳng theo thứ tự mục 2, không lọc từ `AdPlacement.entries`, trừ câu so tập ở test đầu.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| Ticket: 22 vị trí đúng mã và định dạng của bảng 9.1 | Unit test | `FrameloopPlacementsTest.placements_matchSpecTables`: map viết thẳng 22 cặp mã to mã định dạng của bảng mục 2 (ví dụ `"projects_grid" to "native_in_collection"`) bằng `AdPlacement.entries.filterNot { it.stagingOnly \|\| it.appWide \|\| it == AdPlacement.SAMPLE_NATIVE_SMALL }.associate { it.key to it.format.code }` |
| Ticket: khóa `is_show_<mã>` mặc định tắt | Unit test | `FrameloopPlacementsTest.switches_offByDefault_inKeyList`: với 22 vị trí, `AdsSettings.placementEnabled(p).name == "is_show_${p.key}"`, `default == false`, có trong `AdsSettings.all()`; đối chứng: tên 22 khóa khác nhau |
| projects mục 5, lessons mục 5: một ô sau mỗi 6 dự án, sau mỗi 4 thẻ | Unit test | `FrameloopPlacementsTest.grids_nativeEvery`: `PROJECTS_GRID.nativeEvery == 6`, `LESSONS_GRID.nativeEvery == 4`, format `NATIVE_IN_COLLECTION`; `AdsSettings.nativeEvery(PROJECTS_GRID)` tên `native_every_projects_grid` mặc định `6L`, của `LESSONS_GRID` tên `native_every_lessons_grid` mặc định `4L`; đối chứng: 20 vị trí còn lại có `nativeEvery == null` và `AdsSettings.nativeEvery(p) == null` |
| KB-3, KB-7, KB-14, export câu 14: không có quảng cáo thì mở ở lần thử thứ 2 liên tiếp | Unit test | `FrameloopPlacementsTest.rewarded_allowAfter2`: với `EDITOR_RW_LAYER`, `EDITOR_RW_BRUSHES`, `EDITOR_RW_SOUNDS`, `EXPORT_RW_EXTRA`: format `REWARDED`, `declaration.effectiveNoAdPolicy == RewardedNoAdPolicy.AllowAfter(2)`, `grantsWithoutAd(policy, 1) == false`, `grantsWithoutAd(policy, 2) == true`. Đối chứng: `GALLERY_RW_ACTION` (`Block`) cho `grantsWithoutAd(policy, 2) == false`; trong 22 vị trí, đúng bốn vị trí này là rewarded |
| KB-3, KB-7, KB-14: đóng trước khi được thưởng thì không mở; khóa tắt thì hành động làm thẳng | Test có sẵn | Luật của `:core:ads` không chỉnh được (R-02, R-04, MC-05), đã có test (`RewardedTurnsTest`, `RewardedActionTest`); vị trí mới dùng cùng đường. Hành động mở khóa kiểm ở T-023, T-028, T-034 |
| onboarding mục 5: chỉ lần mở đầu | Unit test | `FrameloopPlacementsTest.onlyNativeOnboarding_firstOpenOnly`: `NATIVE_ONBOARDING.declaration.firstOpenOnly == true`, format `NATIVE_STEP`; 21 vị trí còn lại `false` |
| Interstitial theo `ads_interval` | Unit test | `FrameloopPlacementsTest.interstitials_defaultIntervalGroup`: năm interstitial viết thẳng `CREATE_INTER`, `EDITOR_EXIT_INTER`, `EXPORT_INTERSTITIAL`, `LESSON_START_INTER`, `LESSON_NEXT_INTER`; mỗi vị trí `intervalGroup == AdIntervalGroup.DEFAULT`, `declaration.readsInterval() == true`, `declaration.pushesIntervalMark() == true`; đối chứng: trong 22 vị trí, đúng năm vị trí này là `INTERSTITIAL` |
| Không vị trí nào của Frameloop là toàn app hay chỉ cho staging | Unit test | `FrameloopPlacementsTest.noneAppWideOrStagingOnly`: 22 vị trí có `appWide == false`, `stagingOnly == false` |
| `release.sh` đọc được mọi vị trí mới | Test có sẵn | `AdPlacementSourceTest.releaseScript_readsEveryShippedPlacement` (giữ nguyên, tự gồm 22 vị trí) |
| Bảng khóa khớp code | Test có sẵn | `RemoteConfigKeysTest` sau khi sinh lại; `git diff -- docs/remote-config-keys.md docs/remote-config-template.json` chỉ có 22 dòng `is_show_*` và 2 dòng `native_every_*` mới (mục 7) |
| Kế hoạch bật trên Firebase: nhóm A khi phát hành, nhóm B bật dần, `ads_interval` 30 giây | Kiểm tra thủ công (người: việc vận hành Firebase, không phải chạy app) | Dev chạy `scripts/ads-plan.py --keep <file cũ>`, xem 22 dòng mới ở sheet vị trí (chú thích có nhóm A, B), điền giá trị phát hành theo nhóm và `ads_interval` 30 |

Không có test dựng lại activity, đưa app ra nền hay cờ tắt giữa chừng: plan không thêm trạng thái ở vỏ app, không có bước chờ, không giữ tài nguyên mới (khóa đổi giữa chừng đã có test trong `:core:ads` cho mọi vị trí). Không có bước kiểm trên máy: chưa màn nào đặt 22 vị trí, nên người dùng staging không thấy gì mới (khe nạp sẵn thì có đổi, mục 6).

**Ngoại lệ viết test trước:** hai file bảng khóa sinh bằng lệnh, `RemoteConfigKeysTest` có sẵn kiểm; KDoc. Năm test cập nhật dữ liệu ở mục 3.5 (bốn file) đỏ chỉ sau khi 22 dòng có mặt, nên vào commit code của bước 4 cùng 22 dòng.

### 4.1. Chạy thử

Không cần (E dưới 2). Để viết mục 3.5 chính xác, đã thử một lần ngày 2026-11-22 trên worktree tạm từ `origin/main` 5ea6ec0, không commit: chỉ thêm 22 dòng của mục 3.1 vào `AdPlacement.kt`, đúng phần code của plan.

- **Lệnh:** `./gradlew :core:ads:testDebugUnitTest :app:testStagingDebugUnitTest --continue`, rồi `./gradlew :feature:loading:testDebugUnitTest :feature:sample:testDebugUnitTest --continue`; `python3` gọi `read_placements(False)` của `scripts/ads-plan.py`.
- **Kết quả:** biên dịch qua, không dòng nào quá 120 ký tự. `:core:ads` 2002 test, 5 đỏ; `:app` 171 test, 2 đỏ; `:feature:loading` và `:feature:sample` 79 test, 0 đỏ; đúng 7 test của mục 3.5, mỗi test đỏ vì dữ liệu chưa có vị trí mới. `AdFreeGateTest`, `AdsSettingsTest`, `AdsFlagsTest`, `AdPlacementSourceTest`, `StagingRemoteValuesModuleTest` qua, kể cả với `native_onboarding` là `firstOpenOnly` (không treo khi đọc `isEnabled(...).first()` trong test chưa `start`). `ads-plan.py` đọc đủ 22 dòng với đúng định dạng, `nativeEvery` 6 và 4, chính sách `allow_after_2`, `firstOpenOnly`. Phiên chạy `scripts/check.sh` như mọi task.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 1 (quảng cáo: thêm 22 vị trí), 7 (module core dùng chung `:core:ads`, chỉ ở `AdPlacement.kt`). Không chạm điểm 4 (không thêm sự kiện hay tham số phân tích), điểm 5 (cổng đồng ý và thứ tự khởi tạo SDK không đổi), điểm 9 (không thêm khóa DataStore). Người phụ trách sản phẩm cần biết trước khi duyệt (`risk-scoring.md` mục 3).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 1 | Code chính chỉ ở một file (`AdPlacement.kt`), thêm 22 hằng công khai; cộng test của cùng module và hai file bảng khóa sinh ra |
| B. Độ rõ của yêu cầu | 0 | Bảng vị trí rõ; sáu spec đang `changed` chỉ vì bỏ luật trước nét vẽ đầu tiên và được xác nhận lại cùng lời duyệt đợt; câu hỏi về định dạng ô lưới đã được anh Nhật trả lời (mục 8) |
| C. Khả năng kiểm chứng tự động | 0 | Mọi thuộc tính khai báo có unit test; bảng khóa có test so với code |
| D. Ảnh hưởng chéo | 2 | Sửa `AdPlacement.kt` mà T-008 cũng sửa, và hai file bảng khóa mà T-006, T-048, T-008 cũng sinh lại; mọi ticket đặt quảng cáo dùng các hằng này |
| E. Độ mới của kỹ thuật | 0 | Đúng mẫu `ads-guide.md` mục 5 |
| **Tổng** | 3 | **Mức: Cao** (chạm điểm chặn 1 và 7) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `AdPlacement` (mọi màn đặt quảng cáo), hai file bảng khóa (sinh từ `AppRemoteKeys`, `scripts/ads-plan.py` đọc).
- Task khác có thể xung đột:
  - T-005: không còn là phụ thuộc (plan không đọc mốc `first_stroke_drawn`; ticket `depends_on: [T-006]`, mục 8).
  - T-006 (phải merge trước): cùng sinh lại `docs/remote-config-keys.md`, `docs/remote-config-template.json`; giải xung đột bằng lệnh sinh trên nhánh mới nhất.
  - T-048 (cùng đợt): cũng sinh lại hai file đó (thêm `lessons_content_version`). Cách giải giống trên. T-048 merge trước thì dòng `lessons_content_version` đã có trên nhánh chính, không phải dòng lạ của mục 7.
  - T-008: thêm `EDITOR_TOP_NATIVE` vào `AdPlacement.kt` sau task này; `FrameloopPlacementsTest.placements_matchSpecTables` cần thêm dòng `editor_top_native` (cập nhật dữ liệu, ADR-0029). Ngoại lệ MC-02 của định dạng 56 dp là việc của T-008.
  - Ticket màn hình (T-010, T-011, T-013, T-015, T-016, T-040, T-042, T-045, T-046, T-051, T-052, T-053) và rewarded (T-023, T-028, T-034): chỉ dùng hằng `AdPlacement` và hàm của `:core:ads`, bật tắt bằng `is_show_<mã>`.
  - Bản staging: `StagingRemoteValuesModule` bật mọi vị trí mới, nên sau khi merge, 4 khe rewarded và 5 khe interstitial nạp sẵn quảng cáo thử của Google ngay lúc mở app, dù chưa màn nào hiện chúng. Không ảnh hưởng production (khóa mặc định tắt).
  - Bản production: `scripts/release.sh` đòi mã đơn vị thật cho cả 22 vị trí mới; người phát hành tạo 22 đơn vị trên AdMob (và MAX nếu dùng) trước bản production đầu tiên. Đơn vị MREC (`restore_mrec`, `export_options_mrec`, `export_wait_mrec`) tắt tự làm mới (`ads-guide.md` mục 5, bước 6).
- Đề xuất thứ tự chạy: sau T-006, trước mọi ticket đặt quảng cáo, nhất là T-008.

## 7. Điều kiện dừng riêng

- Lệnh sinh lại bảng khóa đổi dòng nào khác ngoài 22 khóa `is_show_<mã>` và 2 khóa `native_every_<mã>` mới (trong `docs/remote-config-keys.md`), hay ngoài 24 tham số tương ứng (trong `docs/remote-config-template.json`): dừng, vì bảng trên nhánh chính đã lệch code. Dòng của khóa mà task đã merge trước sinh ra (`lessons_content_version` của T-048) không tính là dòng lạ, vì nó đã có trên nhánh chính.
- Làm task phải sửa code chính (ngoài thư mục test) của `:core:ads` ngoài `AdPlacement.kt`: dừng, vì thêm vị trí không được thêm code vào `:core:ads` (ADR-0045 mục 4.9).
- Một test có sẵn của `:core:ads` đỏ vì hành vi (không phải vì dữ liệu chưa có vị trí mới như mục 3.5): dừng theo điều kiện 3, không sửa test.

## 8. Câu hỏi cho người duyệt

Đã trả lời (anh Nhật, 2026-11-22, trong phiên):

- **Luật "trước nét vẽ đầu tiên":** bỏ hẳn. Lời anh Nhật: "Việc thiết lập quảng cáo sẽ cần linh hoạt tuỳ theo kịch bản kinh doanh". Quảng cáo chỉ theo `is_show_<mã vị trí>` và các khóa, luật có sẵn; mốc `first_stroke_drawn` của T-005 giữ làm dữ liệu dự phòng, không ai đọc. Câu hỏi cũ về chỗ đặt luật và ADR của ngoại lệ ở `:core:ads` không còn.
- **Định dạng của `projects_grid` và `lessons_grid`:** `native_in_collection`, theo đề xuất của AI.
- **Vị trí mẫu và gallery trước nét vẽ đầu tiên; lần rời màn vẽ ngay sau nét vẽ đầu tiên:** đồng ý, nhưng không còn ý nghĩa vì luật đã bỏ.

Còn mở:

1. **Phụ thuộc T-005 của ticket.** Plan không còn đọc gì của T-005. AI đề xuất bỏ T-005 khỏi `depends_on` của ticket (chỉ còn T-006) để T-007 không phải chờ; giữ nguyên cũng không hại gì.
   - Đã làm trong PR của đợt: ticket T-007 chỉ còn `depends_on: [T-006]`; anh Nhật xác nhận cùng lời duyệt đợt.

2. **(Phiên bản 2) Sửa giới hạn bộ nhớ của test `:core:ads` trên CI bằng cách nào?** Phương án AI đề xuất: (a) nâng heap của test JVM, ví dụ `maxHeapSize = "2g"` cho mọi task `Test` trong convention plugin của `build-logic/`, bằng một plan rủi ro cao duyệt chéo của app và đề xuất về dự án mẫu (`docs/ops/from-app.md`), vì mọi app thêm vị trí quảng cáo đều gặp; hoặc (b) đổi các screenshot test có hiệu ứng chạy mãi của `:core:ads` sang dừng đồng hồ (`mainClock.autoAdvance = false`) ở dự án mẫu rồi đồng bộ về. (a) nhỏ và chắc hơn. Trước khi duyệt, nên chạy `:core:ads:testDebugUnitTest` của nhánh `claude/T-007-vi-tri-quang-cao` với Java 17 để xác nhận nguyên nhân (máy của phiên chỉ có Java 21).

## 9. Quyết định duyệt

Phiên bản 1:


- **Quyết định:** Duyệt
- **Người duyệt:** Nhật, **duyệt chéo:** Nhật (nhóm chỉ có một dev, theo risk-scoring.md mục 3; khi có dev thứ hai thì quy tắc duyệt chéo áp dụng lại)
- **Ghi chú:** Duyệt cả đợt 20261122-02 ngày 2026-11-22, trong phiên, lời đồng ý nguyên văn: "Duyệt đợt 20261122-02: plan T-007, T-048, T-044, T-002, T-003 và danh sách RUN-20261122-02, bắt đầu phiên khi merge; xác nhận sáu spec editor, export, lessons, onboarding, projects, settings. Tôi vừa duyệt vừa duyệt chéo vì nhóm chỉ có một dev." Câu trả lời cho mục 8 ghi ở từng câu hỏi.

Phiên bản 2:

- **Quyết định:** <Duyệt / Yêu cầu sửa / Từ chối>
- **Người duyệt:** <tên>, **duyệt chéo:** <tên>
- **Ghi chú:** <...>

## 10. Nhật ký thực hiện

### RUN-20261122-02, phiên bản plan 1

- **Kết quả:** dừng ở bước 8 (CI đỏ), điều kiện dừng 1 của `risk-scoring.md` mục 4: cách sửa nằm ở `build-logic/`, plan chưa nêu.
- **Nhánh, PR:** `claude/T-007-vi-tri-quang-cao`, PR #20 (để mở, không merge).
- **Đã làm:** 22 dòng vị trí trong `AdPlacement.kt` đúng mục 3.1; dữ liệu của 5 test có sẵn theo mục 3.5; sinh lại hai file bảng khóa (chỉ thêm 22 khóa `is_show_*` và 2 khóa `native_every_*`).
- **Test viết trước:** 7 test trong `FrameloopPlacementsTest` (commit d2faad9), đỏ vì chưa biên dịch được khi chưa có 22 hằng, đúng mục 3.1. Commit code c76d8bf.
- **Rà soát độc lập:** 0 chặn, 0 nên sửa, 3 ghi chú (KDoc của `EXPORT_RW_EXTRA` ghi "(q. 14)" chưa nói spec export; KDoc cũ của `AdsControllerFirstOpenOnlyTest`, `AdsSettingsTest.placementEnabled_firstOpenOnly_saysSo` giữ theo mục 3.5; tiêu chí đóng sớm, khóa tắt dựa vào test có sẵn như mục 4).
- **Kết quả CI:** `scripts/check.sh` trên máy (Java 21) qua: 2554 test JVM, 0 lỗi. `pr-check` lần chạy 45043991143 đỏ hai lần ở `:core:ads:testDebugUnitTest`, mỗi lần một hay hai screenshot test có sẵn khác nhau ném `java.lang.OutOfMemoryError at DataBufferInt.java:75`: lần 1 `NativeFullScreenAdScreenScreenshotTest.default_dark`, `NativeFullScreenOverlayScreenshotTest.loading_light`; lần chạy lại (job đỏ, một lần như lỗi chập chờn) `NativeFlowStepScreenshotTest.fullPage_placeholder_light`. `failure-kind.sh --playbook`: `khong-co`.
- **Nguyên nhân (giả thuyết có căn cứ, chưa đo trên Java 17):** `AdsController` dựng chỗ nạp sẵn cho mọi vị trí (`fullScreenPlacements()`, `pooledPlacements()`, `stockPlacements()` đọc `AdPlacement.entries`), nên 22 vị trí mới (9 toàn màn hình, 8 dùng pool) làm mỗi controller trong hàng trăm test của `:core:ads` giữ nhiều đối tượng hơn. Mọi test của module chạy trong một JVM với heap mặc định của Gradle (không có `maxHeapSize` trong `build-logic/`); screenshot test có hiệu ứng chạy mãi vẽ tới sát giới hạn bộ nhớ rồi mới chụp, nên lần chụp hết chỗ. Trên nhánh chính (cùng các test đó) `pr-check` và `main-guard` vẫn xanh. Máy phiên chỉ có Java 21 nên không tái hiện được; không tự tải JDK 17.
- **Lệch so với plan:** Không có.
- **Việc còn lại:** nâng heap của test JVM (ví dụ `maxHeapSize` cho `Test` trong convention plugin của `build-logic/`) hay đổi các screenshot test có hiệu ứng chạy mãi của `:core:ads` sang dừng đồng hồ, rồi chạy lại PR #20. Cả hai nằm ngoài plan; `build-logic/` thuộc mục "Không được làm" của `CLAUDE.md` và là phần dùng chung nên đề xuất về dự án mẫu (`docs/ops/from-app.md`).
- **Cần người xem:** quyết cách sửa bộ nhớ test của `:core:ads` (plan riêng rủi ro cao cho `build-logic/` hay sửa ở dự án mẫu), rồi cho PR #20 chạy lại CI; mọi ticket thêm vị trí quảng cáo sau này (T-008) gặp cùng giới hạn.
