---
ticket: T-007
title: Vị trí quảng cáo của Frameloop và luật trước nét vẽ đầu tiên
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
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

# PLAN T-007: VỊ TRÍ QUẢNG CÁO CỦA FRAMELOOP VÀ LUẬT TRƯỚC NÉT VẼ ĐẦU TIÊN

## 1. Tóm tắt

Khai báo 22 vị trí quảng cáo của sáu feature trong `AdPlacement` của `:core:ads`, mỗi vị trí một dòng theo bảng 9.1 của bản đề xuất và mục 5 của sáu spec; bốn vị trí rewarded dùng `allow_after_2`. Luật "trước nét vẽ đầu tiên" đi qua một chỗ duy nhất: `:core:ads` thêm một điểm nối chung `AdPlacementHold` (cùng kiểu `AdFreeStatus`) mà công tắc của mọi vị trí đọc qua, còn quy tắc của Frameloop (vị trí nào bị giữ, mốc `firstStrokeDrawn` của T-005) nằm ở `:app`. Kiểm chứng bằng unit test của khai báo, test Robolectric đi qua `AdsController` thật cho mọi đường tới lượt nạp và lượt hiện (giữ, nhả, giữ lại), unit test của quy tắc ở `:app`, và bảng khóa sinh lại.

## 2. Hiểu yêu cầu

Ticket xử lý phần quảng cáo (khai báo vị trí, chính sách khi không có quảng cáo, luật trước nét vẽ đầu tiên) của các kịch bản sau trong `features/editor/spec.md`. Lời mời, thẻ mời, hộp thoại và hành động mở khóa là của T-023 (cọ), T-028 (layer), T-034 (âm thanh); ticket này chỉ cho chúng vị trí và chính sách.

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-3 | Bấm "Watch ad" và xem hết thì 4 cọ mở 24 giờ; đóng trước khi được thưởng thì cọ vẫn khóa; cần đúng 1 lượt xem | Vị trí `editor_rw_brushes`, định dạng `rewarded`. Đóng sớm thì luôn chặn là luật của `:core:ads` (R-02, R-04), không chỉnh được. Mục 5 của spec editor và câu hỏi 4: không có quảng cáo thì `allow_after_2` (lần thử thứ 2 liên tiếp không có quảng cáo thì mở luôn). Phần 24 giờ do T-023 làm |
| KB-7 | Bấm "Watch ad" và xem hết thì layer thứ 6 được thêm, dự án dùng tới 10 layer; đóng sớm thì không thêm | Vị trí `editor_rw_layer`, `rewarded`, `allow_after_2`. Phần "cả dự án" do T-028 làm |
| KB-14 | Xem hết thì mọi gói hiệu ứng mở 24 giờ; đóng sớm thì vẫn khóa | Vị trí `editor_rw_sounds`, `rewarded`, `allow_after_2`. Phần 24 giờ do T-034 làm |
| Mục 5 spec editor: khóa `is_show_<mã>` tắt thì hành động làm thẳng (MC-05) | Khóa tắt thì không mời | Hành vi có sẵn của `rememberRewardedAction`; khóa của 4 vị trí mặc định tắt (MC-03) |
| KB-20, tiêu chí 2; mục 5 spec editor, "Trước nét vẽ đầu tiên" | Người dùng chưa vẽ nét đầu tiên nào trên máy thì rời màn vẽ không có `editor_exit_inter` | `editor_exit_inter` bị giữ (đọc là tắt) tới khi `firstStrokeDrawn` là `true` |
| Bản đề xuất dưới bảng 9.1; mục 5 của projects, export, lessons, onboarding, settings | Tới nét vẽ đầu tiên, app chỉ hiện `native_onboarding`, `projects_empty_native`, `editor_top_native`; rewarded chỉ khi người dùng tự chọn | Bị giữ: 16 vị trí còn lại của bảng 9.1 (thanh đáy, ô lưới, native medium, MREC, interstitial). Không giữ: `native_onboarding`, `projects_empty_native`, 4 vị trí rewarded, vị trí toàn app (`open_app` từ lần mở thứ hai và `resume`, `resume_inter` theo khóa riêng của chúng, bản đề xuất dưới bảng 9.1; `native_combo` không cần giữ vì nó chỉ hiện sau interstitial của feature, luật K-01, mà mọi interstitial của feature đều bị giữ), vị trí của màn xem thử staging (`gallery_*`). `editor_top_native` chưa có: T-008 tạo nó và thêm vào tập không giữ (ticket T-008, phạm vi) |
| Như trên | Mốc nhớ trên máy, dùng chung mọi feature, đổi trong cùng phiên | Đọc `DeviceSettingsRepository.firstStrokeDrawn` của T-005. Luồng phát giá trị mới khi mốc được ghi, nên vị trí bị giữ được nhả ngay trong phiên (slot nạp, interstitial kế tiếp được hiện), không cần mở lại app |
| Ticket, phạm vi | 22 vị trí, khóa `is_show_<mã>`, nhóm A, B; cập nhật `docs/remote-config-keys.md` và test của `:core:ads` | Nhóm A, B là kế hoạch bật trên Firebase, không phải code: ghi trong chú thích của từng dòng (`scripts/ads-plan.py` đưa chú thích vào cột ghi chú của file Excel). Khóa suy ra từ khai báo; bảng khóa sinh lại |

Định dạng chọn theo cột Định dạng của từng spec, đối chiếu danh sách định dạng đóng của `:core:ads` (MC-02, `ads-guide.md` mục 5):

| Vị trí | Định dạng | Thuộc tính | Bị giữ trước nét vẽ đầu tiên |
| --- | --- | --- | --- |
| `native_onboarding` | `NATIVE_STEP` | `firstOpenOnly = true` (spec onboarding mục 5) | Không |
| `projects_empty_native` | `NATIVE_MEDIUM` | | Không |
| `projects_bottom` | `NATIVE_SMALL` (thanh đáy) | | Có |
| `projects_grid` | `NATIVE_IN_COLLECTION` (ô trong lưới) | `nativeEvery = 6` | Có |
| `restore_mrec` | `BANNER_MREC` | | Có |
| `create_inter` | `INTERSTITIAL` | nhóm giãn cách mặc định (`ads_interval`) | Có |
| `new_project_native` | `NATIVE_MEDIUM` | | Có |
| `settings_bottom` | `NATIVE_SMALL` (thanh đáy) | | Có |
| `editor_exit_inter` | `INTERSTITIAL` | nhóm mặc định | Có |
| `editor_rw_layer` | `REWARDED` | `noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)` | Không |
| `editor_rw_brushes` | `REWARDED` | `noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)` | Không |
| `editor_rw_sounds` | `REWARDED` | `noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)` | Không |
| `export_options_mrec` | `BANNER_MREC` | | Có |
| `export_wait_mrec` | `BANNER_MREC` | | Có |
| `export_done_native` | `NATIVE_MEDIUM` | | Có |
| `export_interstitial` | `INTERSTITIAL` | nhóm mặc định | Có |
| `export_rw_extra` | `REWARDED` | `noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)` (spec export câu 14) | Không |
| `lessons_grid` | `NATIVE_IN_COLLECTION` (ô trong lưới) | `nativeEvery = 4` | Có |
| `lessons_bottom` | `NATIVE_SMALL` (thanh đáy) | | Có |
| `lesson_start_inter` | `INTERSTITIAL` | nhóm mặc định | Có |
| `lesson_done_native` | `NATIVE_MEDIUM` | | Có |
| `lesson_next_inter` | `INTERSTITIAL` | nhóm mặc định | Có |

`ads_interval` 30 giây (Q16) là giá trị đặt trên Firebase, không đổi mặc định 45 giây của code (bản đề xuất 9.1). "Từ dự án thứ 2" của `create_inter`, "mở 24 giờ", "cả dự án", `keepSpaceWhenEmpty` của `new_project_native` và khung 250 dp của `export_options_mrec` là việc của ticket màn hình, không phải thuộc tính khai báo.

Thiết kế tham chiếu: Không có thiết kế riêng (ticket không có màn hình; vùng quảng cáo trên thiết kế của từng feature thuộc ticket màn hình, docs/ops/design-workflow.md).

ADR liên quan: ADR-1001 (ngoại lệ của app: điểm nối `AdPlacementHold` trong `:core:ads`; `proposed` trong cùng PR của đợt, phải `accepted` trên nhánh chính trước khi task chạy, mục 7), ADR-0030 và ADR-0045 mục 4.9 (quy tắc mà ADR-1001 tạo ngoại lệ), ADR-0045 (quảng cáo, MC-01 tới MC-05), ADR-0046 (Remote Config, khóa suy ra), ADR-0001 (phụ thuộc module), ADR-0019 (log).

## 3. Giải pháp

### 3.1. Cách làm

**Phần 1: khai báo 22 vị trí** (`AdPlacement.kt`, file mà app được sửa theo `docs/ops/to-app.md` mục 2). Thêm 22 dòng ngay sau `RESUME_INTER`, trước khối `gallery_*`, theo thứ tự của bảng ở mục 2. Mỗi dòng một khai báo mà `scripts/release.sh` đọc được, dưới 120 ký tự (dòng dài nhất `EDITOR_RW_BRUSHES` khoảng 110 ký tự). Chính sách viết thẳng `noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)` trên dòng, không đặt thành hằng, vì `scripts/ads-plan.py` chỉ đọc chính sách viết trên dòng. Mỗi dòng có một dòng KDoc ngay trên, nói màn hình, nhóm (A hay B), có bị giữ trước nét vẽ đầu tiên không, và spec; ví dụ:

```kotlin
/** Projects list with projects, bottom bar. Group A. Held before the first stroke (projects spec, section 5). */
PROJECTS_BOTTOM("projects_bottom", AdFormat.NATIVE_SMALL),
/** Editor, unlocks the pro brushes for 24 hours (T-023). Group A. allow_after_2 (editor spec, question 4). */
EDITOR_RW_BRUSHES("editor_rw_brushes", AdFormat.REWARDED, noAdPolicy = RewardedNoAdPolicy.AllowAfter(2)),
```

Chú thích không có mã ticket ngoài tên ticket làm hành động của rewarded (`ads-guide.md` mục 5: hành động ghi trong chú thích của dòng). Không đổi dòng có sẵn nào. Khóa tự suy ra: 22 khóa `is_show_<mã>` (mặc định tắt) và hai khóa `native_every_projects_grid` (6), `native_every_lessons_grid` (4); không có nhóm giãn cách riêng nên không có khóa `ads_interval_<nhóm>`.

**Phần 2: điểm nối giữ vị trí trong `:core:ads`.** Tạo `AdPlacementHold.kt`:

```kotlin
/**
 * Placements the app holds back for now, by a rule of its own spec that :core:ads does not know (for example no ad
 * before the first stroke), given by the app through Hilt like [AdFreeStatus]. A held placement reads as switched
 * off wherever its switch is read (AdsController.isEnabled): no load, no preload, slots drop their ad, a full-screen
 * turn is DISABLED, a rewarded action runs at once (MC-05). Asked only for placements that are not app-wide. [held]
 * must emit early and never wait indefinitely, read errors included (emit a value, never throw), since every
 * switch read waits for it; it may change many times (a hold can end and come back) and :core:ads follows every
 * value. App exception to the template (ADR-1001).
 */
interface AdPlacementHold {
    fun held(placement: AdPlacement): Flow<Boolean>

    companion object {
        /** No placement is ever held. */
        val NONE: AdPlacementHold = object : AdPlacementHold {
            override fun held(placement: AdPlacement): Flow<Boolean> = flowOf(false)
        }
    }
}
```

Sửa `DefaultAdsFlags` trong `AdsFlags.kt`: thêm tham số thứ ba `private val hold: AdPlacementHold = AdPlacementHold.NONE` (mặc định như `adFreeStatus`, để mọi test đang dựng `DefaultAdsFlags(settings)` hay `DefaultAdsFlags(settings, adFree)` không phải sửa; Hilt luôn tiêm binding của `:app`). Hàm công khai `placementEnabled(placement: AdPlacement)` thành `combine(placementEnabled(placement.declaration), hold.held(placement)) { on, held -> on && !held }.distinctUntilChanged()`. Hàm `internal placementEnabled(declaration)` (test dùng cho khai báo riêng) giữ nguyên. KDoc của `AdsFlags.placementEnabled` thành `is_show_ads && !adFree && is_show_<code> && !held`.

Vì sao một chỗ này đủ: `AdsController.isEnabled` là nơi duy nhất đọc công tắc của vị trí (KDoc của nó: mọi định dạng phải đọc nó trước khi nạp hay hiện; L-072), và với mọi vị trí không phải toàn app nó đi qua `placementSwitch` tới `flags.placementEnabled`. Các đường tới lời gọi quảng cáo đều đọc nó: slot trong màn (`AdSlot`, `NativeAdSlot`, `NativeAdCell`, thanh đáy `BottomAdBar`, hàng trong danh sách qua `inListEvery`), `InContentAds`, lượt nạp sẵn khi vị trí được bật (`Preload PLACEMENT_ENABLED`, `AdsController` dòng 791 tới 804), lượt nạp lại và thử lại (dòng 2659, 2775, 2854), lượt hiện toàn màn hình (`isEnabled(placement).first()` ngay trước khi hiện, dòng 1334, 1859), kiểm của khe MAX (dòng 2023, 2065), rewarded (`RewardedPort.enabled`, dòng 524). Vị trí toàn app không đi qua `flags.placementEnabled` nên không bao giờ bị hỏi.

**Phần 3: quy tắc của Frameloop ở `:app`.** Tạo `app/src/main/kotlin/com/vgl/draw/animation/ads/FirstStrokeAdHold.kt`:

```kotlin
/** Shown before the first stroke on the device (proposal, under table 9.1). T-008 adds EDITOR_TOP_NATIVE. */
internal val SHOWN_BEFORE_FIRST_STROKE: Set<AdPlacement> =
    setOf(AdPlacement.NATIVE_ONBOARDING, AdPlacement.PROJECTS_EMPTY_NATIVE)

/** Held until the first stroke: every placement but app-wide, staging-only, rewarded and the ones above. */
internal fun heldBeforeFirstStroke(placement: AdPlacement): Boolean = !placement.appWide &&
    !placement.stagingOnly && placement.format != AdFormat.REWARDED && placement !in SHOWN_BEFORE_FIRST_STROKE

@Singleton
internal class FirstStrokeAdHold(deviceSettings: DeviceSettingsRepository, scope: CoroutineScope) : AdPlacementHold {
    @Inject
    constructor(deviceSettings: DeviceSettingsRepository) :
        this(deviceSettings, CoroutineScope(SupervisorJob() + Dispatchers.Default))

    /** One collection of the mark for the whole process, shared by every held placement. */
    private val heldNow: Flow<Boolean> = deviceSettings.firstStrokeDrawn.map { drawn -> !drawn }
        .distinctUntilChanged()
        .shareIn(scope, SharingStarted.Eagerly, replay = 1)

    override fun held(placement: AdPlacement): Flow<Boolean> =
        if (heldBeforeFirstStroke(placement)) heldNow else flowOf(false)
}
```

Quy tắc viết theo kiểu "chỉ cho qua những vị trí đã kể": vị trí mới thêm về sau mặc định bị giữ, là phía ít quảng cáo hơn, đúng câu "không hiện vị trí quảng cáo nào khác" của bản đề xuất. `sample_native_small` (màn mẫu, cờ tắt) vì vậy cũng bị giữ, không ảnh hưởng gì (câu hỏi 3). Test của quy tắc khóa đúng tập vị trí bị giữ (mục 4), nên T-008 thêm `EDITOR_TOP_NATIVE` mà quên thêm vào `SHOWN_BEFORE_FIRST_STROKE` thì test đỏ.

Một luồng chia sẻ cho cả process (`shareIn` trên scope riêng của binding `@Singleton`; `:app` chưa có scope ứng dụng dùng chung) để mỗi lần `:core:ads` đọc công tắc của một vị trí bị giữ không mở thêm một lần thu DataStore. Khi tệp đọc lỗi mãi, T-005 ghi một dòng `WARN` cho mỗi lần thử lại của một lần thu, nên chỉ có một chuỗi dòng `WARN` (giãn tới 30 giây), không phải một dòng mỗi lần đọc công tắc. Hàm dựng có `scope` cho test truyền `backgroundScope`.

Tạo `app/src/main/kotlin/com/vgl/draw/animation/di/AdPlacementHoldModule.kt`: `@Module @InstallIn(SingletonComponent::class) internal abstract class` với `@Binds abstract fun bindAdPlacementHold(impl: FirstStrokeAdHold): AdPlacementHold`, cạnh `AdFreeModule`. `:app` đã phụ thuộc `:core:ads` và `:core:data`; binding `DeviceSettingsRepository` có từ T-005.

**Cổng mở rồi đóng lại.** Với app, mốc chỉ đi một chiều (chưa có thành có, T-005), nhưng luồng `firstStrokeDrawn` có thể quay về `false` khi đọc tệp lỗi giữa phiên (T-005: lỗi đọc phát giá trị mặc định rồi thử lại). Khi đó vị trí bị giữ lại tới lần đọc được: slot đang có quảng cáo bỏ quảng cáo (như khi khóa tắt giữa chừng), lượt toàn màn hình kế tiếp là `DISABLED`. Vì `:core:ads` đọc công tắc ngay trước mỗi lượt nạp và lượt hiện, không chỉ lúc nhận tín hiệu đầu, nên lượt sau khi cổng đóng lại không chạy. Test ở mục 4 kiểm cả ba bước giữ, nhả, giữ lại trên cùng một `AdsController`.

**Chờ giá trị đầu.** `combine` chờ giá trị đầu của `held`. Vị trí không bị giữ trả `flowOf(false)` nên không chờ. Vị trí bị giữ chờ lần đọc DataStore đầu của luồng chia sẻ, một lần cho cả process (vài mili giây; đọc lỗi thì T-005 phát `false` ngay rồi thử lại), nên không treo: KDoc của `AdPlacementHold` đòi phát giá trị ngay cả khi đọc lỗi, vì `decideFullScreenTurn` chờ công tắc và `onFinished` chỉ chạy sau đó. Vị trí toàn app (màn loading, quay lại app) không bao giờ chờ. Sau khi nhả, interstitial bị giữ mới bắt đầu nạp sẵn, nên lượt rời màn vẽ ngay sau nét vẽ đầu tiên có thể chưa có quảng cáo (`NOT_READY`); đó là phía ít quảng cáo hơn, chấp nhận được.

**Log.** Plan không viết dòng log mới: `:core:ads` đã ghi `Full-screen <mã> skipped: DISABLED` cho lượt toàn màn hình bị giữ, và không ghi gì cho slot tắt như khi khóa tắt. Quy tắc ở `:app` không ghi log mỗi vị trí. Dòng `WARN` khi tệp đọc lỗi là của T-005 và nhờ luồng chia sẻ chỉ có một chuỗi cho cả process (trên).

**Vì sao không làm cách khác** (ticket: plan chọn giữa giúp đỡ trong `:core:ads` và kiểm ở vỏ app, ưu tiên chỗ ít lệch dự án mẫu):

- Kiểm ở vỏ app hay ở từng feature, không sửa `:core:ads`: feature tự bỏ slot và bỏ lời gọi interstitial khi chưa có mốc. Cách này không chặn được lượt nạp sẵn: `:core:ads` nạp sẵn interstitial, rewarded và pool native của mọi vị trí có khóa bật, nên quảng cáo vẫn được tải rồi hết hạn mà không hiện, làm sai tỷ lệ hiện trên lượt tải. Ngoài ra luật nằm rải ở năm feature và mỗi ticket màn hình phải tự làm, tự test.
- Bọc `RemoteSettings` để khóa `is_show_<mã>` của vị trí bị giữ đọc ra `false`, không sửa `:core:ads`: `:core:config` là phần "cả hai cùng sửa" (`docs/ops/to-app.md` mục 2), nên app được đổi binding `DefaultRemoteSettings` trong `ConfigModule` thành một lớp bọc. Không chọn vì: lớp bọc che giá trị từ xa với mọi nơi đọc (bảng khóa trên staging, màn xem thử, log, mọi module khác thấy khóa là `false` dù Firebase bật), nên khó tìm vì sao một vị trí không hiện; lớp bọc ở `:core:config` phải biết tên khóa quảng cáo và tập vị trí, trái ADR-0046 (khóa quảng cáo chỉ đọc qua `AdsFlags`); và vẫn là sửa một module core, chỉ khác chỗ.
- Đọc thẳng `DeviceSettingsRepository` trong `:core:ads` (`:core:ads` đã phụ thuộc `:core:data`): ít file hơn nhưng đưa luật riêng của Frameloop vào module của dự án mẫu, không đề xuất về mẫu được.
- Cách đã chọn chỉ thêm một file và sửa một hàm của `:core:ads`, theo đúng mẫu `AdFreeStatus` có sẵn; điểm nối chung cho mọi app nên đề xuất được về dự án mẫu (`docs/ops/from-app.md`). Phần lệch dự án mẫu là ngoại lệ ghi ở ADR-1001 (tạo trong cùng PR của đợt, `proposed`): `hold=core/ads` khi đồng bộ, xem lại sau mỗi đợt đồng bộ, mở issue `from-app` khi chạy ổn trên staging.

Thứ tự commit (test viết trước, `docs/auto-run.md` bước 3), bước 3 gồm hai commit. Commit thứ nhất: `AdPlacementHoldGateTest.kt`, hai test mới trong `AdsFlagsTest.kt` (chỉ dùng vị trí gallery) và khung tối thiểu để chúng biên dịch: `AdPlacementHold.kt` đầy đủ (interface và `NONE`, không có logic), tham số `hold` của `DefaultAdsFlags` có mà chưa được đọc; các test giữ vị trí đỏ đúng lý do: vị trí bị giữ vẫn đọc là bật. Commit thứ hai: `FrameloopPlacementsTest.kt` và `FirstStrokeAdHoldTest.kt`, đỏ vì chưa biên dịch được (thiếu 22 hằng, `FirstStrokeAdHold`, `heldBeforeFirstStroke`); chạy test của commit thứ nhất trước commit này để ghi nhật ký. Bước 4 viết 22 dòng, phần đọc `hold`, quy tắc và module của `:app`, cập nhật dữ liệu của test có sẵn theo mục 3.5, sinh lại bảng khóa.

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdPlacement.kt` | 22 dòng vị trí với KDoc một dòng (mục 3.1, phần 1); không đổi dòng có sẵn |
| Tạo | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdPlacementHold.kt` | Interface `AdPlacementHold` và `NONE` (mục 3.1, phần 2) |
| Sửa | `core/ads/src/main/kotlin/com/vgl/draw/animation/core/ads/AdsFlags.kt` | `DefaultAdsFlags` nhận `hold`, `placementEnabled(placement)` gộp `!held`; KDoc của `AdsFlags.placementEnabled` |
| Tạo | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/FrameloopPlacementsTest.kt` | Test khai báo 22 vị trí (mục 4) |
| Tạo | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdPlacementHoldGateTest.kt` | Test Robolectric qua `AdsController` thật cho mọi đường (mục 4), theo mẫu `AdFreeGateTest` |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdsFlagsTest.kt` | Thêm 2 test của `DefaultAdsFlags` với `hold` (mục 4) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdPlacementTest.kt` | Cập nhật dữ liệu của `inListGalleryPlacements_declareNativeEvery8` (mục 3.5, ADR-0029) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdPreloadPlanTest.kt` | Cập nhật dữ liệu của `fromPlacements_hasRewardedInSecondGroup` (mục 3.5) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdUnitIdsTest.kt` | Thêm hằng `internal const val FRAMELOOP_PLACEMENT_UNITS` cấp file, nối vào chuỗi mã của hai test (mục 3.5) |
| Sửa | `core/ads/src/test/kotlin/com/vgl/draw/animation/core/ads/AdsControllerUnitIdsTest.kt` | Nối `FRAMELOOP_PLACEMENT_UNITS` vào `PLACEMENT_UNITS` (mục 3.5) |
| Tạo | `app/src/main/kotlin/com/vgl/draw/animation/ads/FirstStrokeAdHold.kt` | `SHOWN_BEFORE_FIRST_STROKE`, `heldBeforeFirstStroke`, `FirstStrokeAdHold` (mục 3.1, phần 3) |
| Tạo | `app/src/main/kotlin/com/vgl/draw/animation/di/AdPlacementHoldModule.kt` | Hilt `@Binds` `FirstStrokeAdHold` thành `AdPlacementHold` |
| Tạo | `app/src/test/kotlin/com/vgl/draw/animation/ads/FirstStrokeAdHoldTest.kt` | Unit test của quy tắc và luồng (mục 4), có `FakeDeviceSettingsRepository` riêng trong file |
| Sửa | `docs/remote-config-keys.md` | Sinh lại bằng `./gradlew :app:testStagingDebugUnitTest -PremoteConfigKeys.update=true`; không sửa tay |
| Sửa | `docs/remote-config-template.json` | Sinh lại cùng lệnh; không sửa tay |

Nơi đọc các file bị đổi (`grep -rn` trong `scripts/`, `.github/`, `*/src/test`):

- `AdPlacement.kt`: `scripts/release.sh` và `AdPlacementSourceTest` (đọc khai báo một dòng; 22 dòng mới đúng mẫu, lần thử ở mục 4.1 qua), `scripts/ads-plan.py` (đọc mã, định dạng, `nativeEvery`, chính sách, `firstOpenOnly` và chú thích; lần thử ở mục 4.1 đọc đủ 22 dòng). `StagingRemoteValuesModule` bật mọi vị trí không phải toàn app trên staging, kể cả 22 vị trí mới, không phải sửa. Không sửa `scripts/release.sh` (CLAUDE.md, Không được làm). Bản production cần 22 mã đơn vị thật (mục 6).
- `docs/remote-config-keys.md`, `docs/remote-config-template.json`: `app/src/test/kotlin/com/vgl/draw/animation/config/RemoteConfigKeysTest.kt` (so hai file với code; không sửa, vì câu khẳng định vẫn đúng sau khi sinh lại, mục 3.5), `app/build.gradle.kts` (truyền đường dẫn, không đổi), `scripts/ads-plan.py` (khóa `is_show_`, `native_every_` vào nhóm khóa suy ra, không sửa), `scripts/main-guard-verified.sh` (chỉ phân loại tệp), `scripts/tests/run.sh` (chỉ dùng tên tệp trong dữ liệu thử của script, không đọc nội dung).
- `AdsFlags.kt`: mọi test dựng `DefaultAdsFlags` bằng tham số vị trí hay mặc định; tham số mới có mặc định nên không test nào phải sửa. `AdGalleryViewModelTest` cài `AdsFlags` giả, không đổi.

Không sửa `docs/ops/ads-guide.md` hay `docs/ops/remote-config.md`: chúng là tài liệu của dự án mẫu (`docs/ops/to-app.md` mục 2); cách dùng điểm nối nằm trong KDoc và sẽ đi theo đề xuất về mẫu. Không sửa `AdsController.kt`, `StartupAdChoice.kt` hay file chính nào khác của `:core:ads` (mục 7). Không sửa file mà T-005, T-006 tạo; plan chỉ đọc `DeviceSettingsRepository` của T-005.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có. Vị trí không đọc cờ của feature; màn nào đặt vị trí thì đi theo cờ của màn đó (ticket màn hình).
- Quảng cáo: thêm 22 vị trí của bảng 9.1 bản đề xuất và mục 5 của sáu spec đã xác nhận (mục 2), mọi khóa mặc định tắt; luật trước nét vẽ đầu tiên (mục 5 spec editor, projects, export, lessons, onboarding, settings); `allow_after_2` cho bốn vị trí rewarded (editor câu 4, export câu 14). Chưa màn nào đặt vị trí; ticket màn hình đặt chúng. Không đổi định dạng, nhịp hay luật nào của `:core:ads`.
- Sự kiện phân tích: Không thêm sự kiện hay tham số. Các sự kiện quảng cáo có sẵn sẽ mang giá trị `ad_placement` mới là mã vị trí của spec khi màn hình đặt vị trí; plan này chưa sinh sự kiện nào.
- File R2: Không chạm.
- Dữ liệu trên máy: chỉ đọc khóa `first_stroke_drawn` qua `DeviceSettingsRepository` (T-005); không thêm khóa.
- **Quyền truy cập:** Không thêm. Không gọi API Android mới.
- **Thư viện và SDK:** Không thêm. Dùng `kotlinx-coroutines` (đã có ở `:core:ads` và `:app`), Hilt (plugin `template.hilt` đã áp ở `:core:ads` và `:app`), `:core:data` (`:app` đã khai báo `implementation(project(":core:data"))`).

### 3.4. Phương án dự phòng

Không có. Khai báo vị trí là mẫu có sẵn; điểm nối lặp lại đúng cách `AdFreeStatus` đi vào `DefaultAdsFlags`. Phần dễ vướng nhất (thêm vị trí làm test có sẵn đỏ) đã thử ở mục 4.1.

### 3.5. Hành vi đã có bị đổi

`python3 scripts/existing-tests.py docs/features/editor/plans/T-007-vi-tri-quang-cao.md` liệt kê test có sẵn của các tên plan đổi; lần thử ở mục 4.1 (thêm 22 dòng trên `origin/main` 5ea6ec0, chạy `:core:ads:testDebugUnitTest` và `:app:testStagingDebugUnitTest`) cho đúng 7 test đỏ, đều chỉ vì dữ liệu của test chưa có 22 vị trí mới. Cả 7 là cập nhật dữ liệu theo ADR-0029, quyết định 2: test vẫn kiểm phần mới như phần cũ. Các test còn lại của nhóm "Cần xem trước" và "Nên xem" (duyệt `AdPlacement.entries`, như `AdFreeGateTest.adFreeMidSession_everyPlacementSwitchOff`, `AdsSettingsTest.placementsAreOffByDefault`, `AdPlacementTest.appWidePlacements_areOpenAppAndCombo`, `AdUnitIdsTest.placementKeys_neverSlotNames`, `AdsFlagsTest` dòng 345, `StagingRemoteValuesModuleTest`) qua trong lần thử và giữ nguyên câu khẳng định. Đổi `DefaultAdsFlags` không đổi hành vi khi `hold` là `NONE`, mặc định của mọi test có sẵn.

13 test của nhóm "Cần xem trước" (đo vị trí, kích thước) dùng vị trí gallery hay `sample_native_small`, hay dựng `DefaultAdsFlags` không có `hold`, nên không đổi: `AdsControllerNativeStockTest.configurationChange_layerGoes_adDestroyed_logged`, `AnchoredAdWiringTest.firstNoUnit_secondRuns`, `BannerAdSizeTest.admobOrder_withMaxKey_noMaxPlatform_heightIsAdmob`, `BannerAdSizeTest.bottomBannerSlot_reportsAnchoredHeight`, `BottomBarSlotWiringTest.bottomBannerSlot_failedBeforeAd_reportsNoHeight`, `InListAdsTest.grid_fullWidthRow_poolExhausted_keepsZeroHeightRow`, `InListAdsTest.lazyColumn_sevenItemsEveryThree_adRowsAfterItems3And6`, `InListAdsTest.positions_followEvery`, `NativeAdCellTest.mediumSlot_recordsShownOnce_notReadyOnDispose`, `NativeAdCellTest.otherFormat_isRejected`, `NativeAdSlotTest.withoutAdsController_takesNoSpace`, `NativeFullScreenOverlayTest.smallScreen_countdownAndCtaInside`, `RewardedActionTest.realController_adFreeWhileDialogOpen_watchRunsAction`. Cả 13 qua trong lần thử ở mục 4.1. 407 test của nhóm "Nên xem" chỉ dùng tên `AdPlacement`, `AdsFlags`, `DefaultAdsFlags`, `nativeEvery`; trừ 7 test ở bảng dưới, chúng qua trong lần thử, và phần điểm nối không đổi gì với chúng vì `hold` mặc định `NONE`.

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Chỉ ba vị trí gallery khai báo `nativeEvery` | `projects_grid` khai báo 6, `lessons_grid` khai báo 4 | `AdPlacementTest.inListGalleryPlacements_declareNativeEvery8` (đỏ: `projects_grid expected null, but was:<6>`) | Cập nhật dữ liệu: thêm `assertEquals(6, AdPlacement.PROJECTS_GRID.nativeEvery)`, `assertEquals(4, AdPlacement.LESSONS_GRID.nativeEvery)` và hai hằng đó vào tập `inList`; câu "mọi vị trí khác có `nativeEvery` null" giữ nguyên |
| Chỉ `gallery_rw_action` là rewarded | Thêm bốn vị trí rewarded | `AdPreloadPlanTest.fromPlacements_hasRewardedInSecondGroup` (đỏ ở câu đối chứng: bỏ `GALLERY_RW_ACTION` vẫn còn REWARDED) | Cập nhật dữ liệu của câu đối chứng: `AdPreloadPlan.fromPlacements(AdPlacement.entries - setOf(AdPlacement.GALLERY_RW_ACTION, AdPlacement.EDITOR_RW_LAYER, AdPlacement.EDITOR_RW_BRUSHES, AdPlacement.EDITOR_RW_SOUNDS, AdPlacement.EXPORT_RW_EXTRA))` không có `AdFormat.REWARDED`; ý định giữ: REWARDED chỉ đến từ vị trí rewarded, danh sách bỏ ra viết thẳng như câu cũ. Ba câu khẳng định chính giữ nguyên |
| Chuỗi mã production gồm đủ mọi vị trí có sẵn | Thêm 22 vị trí production cần mã | `AdUnitIdsTest.configuredUnits_areParsedPerPlacementKey`, `AdUnitIdsTest.missingUnitsWarning_listsMissingKeys`, `AdsControllerUnitIdsTest.start_withAllUnits_noWarning` (đỏ: cảnh báo thiếu 22 mã) | Cập nhật dữ liệu: hằng cấp file `internal const val FRAMELOOP_PLACEMENT_UNITS` trong `AdUnitIdsTest.kt`, một chuỗi viết thẳng (không dựng bằng `joinToString`, để `const val PLACEMENT_UNITS` của `AdsControllerUnitIdsTest` nối được) gồm 22 cặp `<mã>=unit/<mã>` theo thứ tự mục 2, cách nhau `;`. Nối vào chuỗi đầu và hai chuỗi đối chứng của `configuredUnits_areParsedPerPlacementKey`, vào `placementUnits` của `missingUnitsWarning_listsMissingKeys`, và vào `PLACEMENT_UNITS` của `AdsControllerUnitIdsTest`. Danh sách kỳ vọng (`banner`, `native_collap`, các vị trí toàn app) giữ nguyên |
| Bảng khóa không có khóa của 22 vị trí | Thêm 22 khóa `is_show_*` và 2 khóa `native_every_*` | `RemoteConfigKeysTest.keysFile_matchesCode`, `templateFile_matchesCode` | Giữ câu khẳng định; sinh lại hai file bằng lệnh của mục 3.2 |
| `DefaultAdsFlags.placementEnabled` = `is_show_ads && !adFree && is_show_<code>` | Thêm `&& !held` | `AdsFlagsTest` (mọi test dựng không có `hold`) | Giữ: `hold` mặc định `NONE`, không bao giờ giữ |

Câu khẳng định trong KDoc của `AdsControllerFirstOpenOnlyTest` và chú thích của `AdsSettingsTest.placementEnabled_firstOpenOnly_saysSo` nói "dự án mẫu chưa khai báo vị trí `firstOpenOnly`"; sau plan app có `native_onboarding`. Test vẫn đúng (chúng tự đặt điểm nối hay duyệt mọi vị trí), chỉ chú thích cũ; không sửa, để `:core:ads` lệch mẫu ít nhất.

## 4. Kiểm thử

Test ở `:core:ads` là test JVM của module (Robolectric khi cần `AdsController`), theo mẫu `AdFreeGateTest`: `testAdsController(context = application, unitIds = AdUnitIds { null }, flags = DefaultAdsFlags(settings, AdFreeStatus.NONE, hold), metrics, logger)`, bản giả `FakeHold(heldNow: MutableStateFlow<Set<AdPlacement>>)` có `held(p) = heldNow.map { p in it }.distinctUntilChanged()` và ghi lại `asked: MutableSet<AdPlacement>`. "Giữ", "nhả", "giữ lại" là đặt `heldNow.value` thành tập có vị trí, tập rỗng, rồi tập có vị trí trên cùng một controller. Test ở `:app` là unit test JVM (`runTest`) với `FakeDeviceSettingsRepository`: `firstStrokeDrawn` là `MutableSharedFlow<Boolean>(replay = 1)` (phát lại cả giá trị trùng, khác `MutableStateFlow`, để câu kiểm bỏ trùng đỏ được) bọc trong `onStart { collections++ }` để đếm số lần thu; các hàm khác `error("not used")`. `FirstStrokeAdHold(fake, backgroundScope)`.

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| Ticket: 22 vị trí đúng mã và định dạng của bảng 9.1 | Unit test | `FrameloopPlacementsTest.placements_matchSpecTables`: map viết thẳng 22 cặp mã to mã định dạng của bảng mục 2 (ví dụ `"projects_grid" to "native_in_collection"`) bằng `AdPlacement.entries.filterNot { it.stagingOnly \|\| it.appWide \|\| it == AdPlacement.SAMPLE_NATIVE_SMALL }.associate { it.key to it.format.code }` |
| Ticket: khóa `is_show_<mã>` mặc định tắt | Unit test | `FrameloopPlacementsTest.switches_offByDefault_inKeyList`: với 22 vị trí, `AdsSettings.placementEnabled(p).name == "is_show_${p.key}"`, `default == false`, có trong `AdsSettings.all()`; đối chứng: tên 22 khóa khác nhau |
| projects mục 5, lessons mục 5: một ô sau mỗi 6 dự án, sau mỗi 4 thẻ | Unit test | `FrameloopPlacementsTest.grids_nativeEvery`: `PROJECTS_GRID.nativeEvery == 6`, `LESSONS_GRID.nativeEvery == 4`; `AdsSettings.nativeEvery(PROJECTS_GRID)` tên `native_every_projects_grid` mặc định `6L`, của `LESSONS_GRID` tên `native_every_lessons_grid` mặc định `4L` |
| KB-3, KB-7, KB-14, export câu 14: không có quảng cáo thì mở ở lần thử thứ 2 liên tiếp | Unit test | `FrameloopPlacementsTest.rewarded_allowAfter2`: với `EDITOR_RW_LAYER`, `EDITOR_RW_BRUSHES`, `EDITOR_RW_SOUNDS`, `EXPORT_RW_EXTRA`: `declaration.effectiveNoAdPolicy == RewardedNoAdPolicy.AllowAfter(2)`, `grantsWithoutAd(policy, 1) == false`, `grantsWithoutAd(policy, 2) == true`. Đối chứng: `GALLERY_RW_ACTION` (`Block`) cho `grantsWithoutAd(policy, 2) == false`; đúng bốn vị trí rewarded của Frameloop có chính sách khác `Block` |
| KB-3, KB-7, KB-14: đóng trước khi được thưởng thì không mở | Test có sẵn | Luật của `:core:ads` không chỉnh được (R-02, R-04), đã có test (`RewardedTurnsTest`, `RewardedActionTest`); vị trí mới dùng cùng đường. Hành động mở khóa kiểm ở T-023, T-028, T-034 |
| onboarding mục 5: chỉ lần mở đầu | Unit test | `FrameloopPlacementsTest.onlyNativeOnboarding_firstOpenOnly`: `NATIVE_ONBOARDING.declaration.firstOpenOnly == true`, format `NATIVE_STEP`; 21 vị trí còn lại `false` |
| Interstitial theo `ads_interval` | Unit test | `FrameloopPlacementsTest.interstitials_defaultIntervalGroup`: sáu interstitial có `intervalGroup == AdIntervalGroup.DEFAULT`, `declaration.readsInterval() == true`, `declaration.pushesIntervalMark() == true`; không vị trí nào trong 22 là `appWide` hay `stagingOnly` |
| `release.sh` đọc được mọi vị trí mới | Test có sẵn | `AdPlacementSourceTest.releaseScript_readsEveryShippedPlacement` (giữ nguyên, tự gồm 22 vị trí) |
| Vị trí bị giữ đọc là tắt, nhả thì bật, giữ lại thì tắt; giữ không bao giờ bật vị trí | Unit test | `AdsFlagsTest.placementEnabled_followsHold`: `DefaultAdsFlags(settings, AdFreeStatus.NONE, hold)` với `is_show_gallery_native_medium` bật; Turbine trên `placementEnabled(GALLERY_NATIVE_MEDIUM)`: giữ ra `false`, nhả ra `true`, giữ lại ra `false`. Đối chứng trong cùng test: `is_show_ads` tắt rồi nhả, vẫn `false`; khóa vị trí tắt và nhả, `false` |
| Điểm nối mặc định không giữ gì | Unit test | `AdsFlagsTest.defaultHold_neverHolds`: `DefaultAdsFlags(settings)` với khóa bật ra `true` cho mọi vị trí không phải toàn app; `AdPlacementHold.NONE.held(p).first() == false` với mọi `AdPlacement.entries` |
| Luật áp cho mọi vị trí không phải toàn app, vị trí toàn app không bị hỏi | Robolectric | `AdPlacementHoldGateTest.held_everyNonAppWidePlacementOff_appWideNeverAsked`: hai vòng `open_ad_type`, `resume_ad_type` là `aoa` rồi `inter` như `AdFreeGateTest.adFreeMidSession_everyPlacementSwitchOff`, mọi khóa bật; giữ mọi vị trí: mọi vị trí không phải toàn app có `controller.isEnabled(p).first() == false`; vị trí toàn app được chọn ở vòng đó vẫn `true`; `hold.asked` không chứa vị trí toàn app nào. Đối chứng trong cùng test: tập giữ rỗng thì mọi vị trí không phải toàn app là `true` |
| Lượt toàn màn hình kiểm cổng ngay trước khi hiện, sau khi cổng đã mở rồi đóng lại | Robolectric | `AdPlacementHoldGateTest.fullScreenTurn_heldReleasedHeldAgain`: với `GALLERY_INTERSTITIAL` và `GALLERY_NATIVE_FULL_SCREEN`, một controller đã `start`, cổng đồng ý mở (`openConsentGateForTest`), `is_log_ad_fullscreen_events` bật. Giữ: `decideFullScreenTurn == DISABLED`, `showFullScreenAd` trả `false`, `onFinished` chạy 1 lần, không có `AdMetricEvent.Opportunity`, log có `Full-screen <mã> skipped: DISABLED`. Nhả trên cùng controller: lượt kế tiếp ra `NOT_READY` và một opportunity `not_ready` (đi tới bước kiểm quảng cáo). Giữ lại: lượt kế tiếp lại `DISABLED`, không thêm opportunity |
| Không nạp sẵn khi bị giữ; nhả thì nạp | Robolectric | `AdPlacementHoldGateTest.preload_askedOnlyAfterRelease`: `is_show_gallery_interstitial` bật, giữ `GALLERY_INTERSTITIAL`, `start` rồi chạy looper tối đa 2 giây: số dòng log bắt đầu bằng `Preload PLACEMENT_ENABLED` là 0; nhả: chờ có giới hạn như `AdsControllerFirstOpenOnlyTest.waitForAsks`, số dòng là 1. Đối chứng: controller khác không giữ, cùng khóa, có 1 dòng |
| Slot và hàng trong danh sách theo cổng | Robolectric | `AdPlacementHoldGateTest.inListEvery_followsHold`: `controller.inListEvery(GALLERY_INLIST_SMALL)` ra `8`, giữ ra `null`, nhả ra `8` (Turbine), như `AdFreeGateTest.adFreeMidSession_inListEveryNull`. Slot (`AdSlot`, `NativeAdSlot`, `NativeAdCell`, `BottomAdBar`), `InContentAds` và khe MAX đọc cùng `isEnabled`; test có sẵn kiểm mỗi đường khi công tắc tắt giữa chừng, đi qua đúng hàm mà điểm nối đổi: `BottomAdBarTest.placementSwitchedOff_barGone_reservationZero_adsDestroyed`, `BannerSlotHostTest.placementSwitchedOffMidway_givesSpaceBack_destroysOnce_sendsNoMore`, `InContentAdsWiringTest.declaredOff_layerAsUsual_sameFrames`, `AdsControllerMaxInterstitialTest.allInterPlacementsOff_maxRetryDue_noRequest`, `AdsControllerMaxRewardedTest.rewardedSwitchedOff_maxRetryDue_noRequest` (giữ nguyên) |
| Rewarded bị giữ thì hành động làm thẳng (điểm nối chung) | Robolectric | `AdPlacementHoldGateTest.rewardedSwitch_followsHold`: `controller.rewardedSwitch(GALLERY_RW_ACTION).first()` là `true`, giữ thì `false`, nhả thì `true`. App không giữ vị trí rewarded nào (test của `:app` dưới) |
| Bản đề xuất dưới bảng 9.1: đúng 16 vị trí của bảng bị giữ, cộng vị trí mẫu | Unit test | `FirstStrokeAdHoldTest.heldPlacements_exactly`: `AdPlacement.entries.filter(::heldBeforeFirstStroke).map { it.key }.toSet()` bằng tập viết thẳng 17 mã: `projects_bottom`, `projects_grid`, `restore_mrec`, `create_inter`, `new_project_native`, `settings_bottom`, `editor_exit_inter`, `export_options_mrec`, `export_wait_mrec`, `export_done_native`, `export_interstitial`, `lessons_grid`, `lessons_bottom`, `lesson_start_inter`, `lesson_done_native`, `lesson_next_inter`, `sample_native_small` |
| Trước nét vẽ đầu tiên vẫn hiện `native_onboarding`, `projects_empty_native`, rewarded, vị trí toàn app | Unit test | `FirstStrokeAdHoldTest.shownBeforeFirstStroke`: `heldBeforeFirstStroke` là `false` cho `NATIVE_ONBOARDING`, `PROJECTS_EMPTY_NATIVE`, mọi vị trí `format == REWARDED`, mọi vị trí `appWide`, mọi vị trí `stagingOnly`; đối chứng: `true` cho `CREATE_INTER` |
| Nhả ngay trong phiên khi có mốc | Unit test | `FirstStrokeAdHoldTest.held_releasedWhenFirstStrokeDrawn`: phát mốc `false`; `held(CREATE_INTER)` qua Turbine ra `true`; phát `true` ra `false`; phát `true` lần nữa rồi `expectNoEvents()`. Đối chứng: `held(PROJECTS_EMPTY_NATIVE).first() == false` khi mốc còn `false` |
| Mốc mất vì đọc lỗi thì giữ lại | Unit test | `FirstStrokeAdHoldTest.held_comesBackWhenMarkReadsFalse`: mốc `true`, `held(EDITOR_EXIT_INTER)` ra `false`; mốc về `false` (như khi T-005 phát mặc định lúc đọc lỗi) ra `true` |
| Vị trí không bị giữ không chờ mốc; mốc chỉ thu một lần | Unit test | `FirstStrokeAdHoldTest.notHeld_doesNotWaitForMark`: bản giả chưa phát giá trị nào; `held(EDITOR_RW_LAYER).first() == false` và `held(OPEN_APP).first() == false` trả ngay. `FirstStrokeAdHoldTest.mark_collectedOncePerProcess`: phát mốc `false`; gọi `held(p).first()` ba lần cho mỗi `CREATE_INTER`, `PROJECTS_BOTTOM`, `EDITOR_EXIT_INTER`: bản giả đếm đúng 1 lần thu. Đối chứng: một `FirstStrokeAdHold` thứ hai trên cùng bản giả làm số lần thu thành 2 |
| Binding Hilt dựng được | Lệnh | `scripts/check.sh` build `:app`; Hilt báo lỗi khi thiếu binding `AdPlacementHold` hay `DeviceSettingsRepository` |
| Bảng khóa khớp code | Test có sẵn | `RemoteConfigKeysTest` sau khi sinh lại; `git diff -- docs/remote-config-keys.md docs/remote-config-template.json` chỉ có 22 dòng `is_show_*` và 2 dòng `native_every_*` mới (mục 7) |
| Kế hoạch bật trên Firebase: nhóm A khi phát hành, nhóm B bật dần, `ads_interval` 30 giây | Kiểm tra thủ công (người: việc vận hành Firebase, không phải chạy app) | Dev chạy `scripts/ads-plan.py --keep <file cũ>`, xem 22 dòng mới ở sheet vị trí (chú thích có nhóm A, B), điền giá trị phát hành theo nhóm và `ads_interval` 30 |

Không có test dựng lại activity hay đưa app ra nền: plan không thêm trạng thái ở `MainActivity` hay `AppNavHost` và không có bước chờ trước khi hiện thứ gì. Điểm nối là một binding `Singleton` không giữ tài nguyên; tài nguyên quảng cáo theo cổng (slot, khe toàn màn hình) do `:core:ads` giữ, và trường hợp cổng đổi giữa chừng có test ở trên (giữ, nhả, giữ lại). Không có bước kiểm trên máy: chưa màn nào đặt 22 vị trí, nên người dùng staging không thấy gì mới (khe nạp sẵn thì có đổi, mục 6); luật trước nét vẽ đầu tiên thấy được trên staging khi T-010, T-015, T-019 đã merge, và kiểm ở các ticket đó.

**Ngoại lệ viết test trước:** `AdPlacementHoldModule.kt` (phần nối Hilt, build của `scripts/check.sh` kiểm); hai file bảng khóa sinh bằng lệnh, `RemoteConfigKeysTest` có sẵn kiểm; KDoc. Năm test cập nhật dữ liệu ở mục 3.5 (bốn file) đỏ chỉ sau khi 22 dòng có mặt, nên vào commit code của bước 4 cùng 22 dòng. `AdsFlagsTest.defaultHold_neverHolds` giữ hành vi có sẵn nên có thể qua ngay ở bước 3; ghi điều đó vào nhật ký.

### 4.1. Chạy thử

Không cần (E dưới 2). Để viết mục 3.5 chính xác, đã thử một lần ngày 2026-11-22 trên worktree tạm từ `origin/main` 5ea6ec0, không commit: chỉ thêm 22 dòng của mục 3.1, phần 1 vào `AdPlacement.kt` (chưa có điểm nối, vì `DeviceSettingsRepository` của T-005 chưa có trên nhánh chính).

- **Lệnh:** `./gradlew :core:ads:testDebugUnitTest :app:testStagingDebugUnitTest --continue`, rồi `./gradlew :feature:loading:testDebugUnitTest :feature:sample:testDebugUnitTest --continue`; `python3` gọi `read_placements(False)` của `scripts/ads-plan.py`.
- **Kết quả:** biên dịch qua, không dòng nào quá 120 ký tự. `:core:ads` 2002 test, 5 đỏ; `:app` 171 test, 2 đỏ; `:feature:loading` và `:feature:sample` 79 test, 0 đỏ; đúng 7 test của mục 3.5, mỗi test đỏ vì dữ liệu chưa có vị trí mới. `AdFreeGateTest`, `AdsSettingsTest`, `AdsFlagsTest`, `AdPlacementSourceTest`, `StagingRemoteValuesModuleTest` qua, kể cả với `native_onboarding` là `firstOpenOnly` (không treo khi đọc `isEnabled(...).first()` trong test chưa `start`). `ads-plan.py` đọc đủ 22 dòng với đúng định dạng, `nativeEvery` 6 và 4, chính sách `allow_after_2`, `firstOpenOnly`. Phiên chạy `scripts/check.sh` như mọi task.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** 1 (quảng cáo: thêm 22 vị trí, luật giữ vị trí), 7 (module core dùng chung `:core:ads`, thêm điểm nối, lệch dự án mẫu). Không chạm điểm 4 (không thêm sự kiện hay tham số phân tích), điểm 5 (cổng đồng ý và thứ tự khởi tạo SDK không đổi; vị trí bị giữ đọc như khóa tắt, sau cổng đồng ý), điểm 9 (không thêm khóa DataStore). Người phụ trách sản phẩm cần biết trước khi duyệt (`risk-scoring.md` mục 3).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 3 | Hai module (`:core:ads`, `:app`) và hai file bảng khóa; giao diện công khai mới `AdPlacementHold`, 22 hằng công khai mới |
| B. Độ rõ của yêu cầu | 1 | Spec đã xác nhận, bảng vị trí và luật rõ; AI tự quyết vài chi tiết: định dạng ô lưới, vị trí mẫu và gallery, chỗ đặt điểm nối (mục 8) |
| C. Khả năng kiểm chứng tự động | 1 | Khai báo, quy tắc và mọi đường đọc công tắc có test JVM; hiển thị thật trên staging chỉ thấy khi ticket màn hình merge |
| D. Ảnh hưởng chéo | 3 | Mọi ticket đặt quảng cáo dựa vào khai báo và luật này (T-008, T-010, T-011, T-013, T-015, T-016, T-019, T-023, T-028, T-034, T-040, T-042, T-045, T-046, T-051 tới T-053); sửa hai file bảng khóa mà T-006, T-048, T-008 cũng sinh lại |
| E. Độ mới của kỹ thuật | 1 | Biến thể nhỏ của mẫu có sẵn: dòng `AdPlacement` theo `ads-guide.md` mục 5, điểm nối theo `AdFreeStatus` |
| **Tổng** | 9 | **Mức: Cao** (chạm điểm chặn 1 và 7) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: `AdPlacement` (mọi màn đặt quảng cáo), `DefaultAdsFlags` (mọi công tắc vị trí), hai file bảng khóa (sinh từ `AppRemoteKeys`, `scripts/ads-plan.py` đọc), đồ thị Hilt của `:app`.
- Task khác có thể xung đột:
  - T-005 (phải merge trước): plan dùng `DeviceSettingsRepository.firstStrokeDrawn`; không sửa file của T-005.
  - T-006 (phải merge trước): cùng sinh lại `docs/remote-config-keys.md`, `docs/remote-config-template.json`; giải xung đột bằng lệnh sinh trên nhánh mới nhất.
  - T-048 (cùng đợt): cũng sinh lại hai file đó (thêm `lessons_content_version`). Cách giải giống trên: chạy lại lệnh sinh trên nhánh mới nhất. T-048 merge trước thì dòng `lessons_content_version` đã có trên nhánh chính, không phải dòng lạ của mục 7.
  - T-008: thêm `EDITOR_TOP_NATIVE` vào `AdPlacement` và vào `SHOWN_BEFORE_FIRST_STROKE` của `app/src/main/kotlin/com/vgl/draw/animation/ads/FirstStrokeAdHold.kt`; khi đó `FirstStrokeAdHoldTest.heldPlacements_exactly` giữ nguyên tập 17 mã (vị trí mới không bị giữ), còn quên thêm thì test đỏ. `FrameloopPlacementsTest.placements_matchSpecTables` cần thêm dòng `editor_top_native` (cập nhật dữ liệu, ADR-0029). Ngoại lệ MC-02 của định dạng dải 56 dp là của T-008: T-008 viết một ADR hợp nhất thay ADR-1001 (`supersedes: ADR-1001`), chép lại mọi quy tắc còn đúng rồi thêm phần định dạng, không sửa ADR-1001 khi nó đã `accepted` (một quy tắc không nằm rải ở hai ADR còn hiệu lực, ADR-0044).
  - Ticket màn hình (T-010, T-011, T-013, T-015, T-016, T-040, T-042, T-045, T-046, T-051, T-052, T-053) và rewarded (T-023, T-028, T-034): chỉ dùng hằng `AdPlacement` và hàm của `:core:ads`; luật trước nét vẽ đầu tiên đã áp ở công tắc, feature không tự đọc mốc, không cần test riêng cho luật (test của màn kiểm phần của màn). T-019 ghi mốc bằng `markFirstStrokeDrawn()` (plan T-005).
  - Bản production: `scripts/release.sh` đòi mã đơn vị thật cho cả 22 vị trí mới; người phát hành tạo 22 đơn vị trên AdMob (và MAX nếu dùng) trước bản production đầu tiên. Đơn vị MREC (`restore_mrec`, `export_options_mrec`, `export_wait_mrec`) tắt tự làm mới (`ads-guide.md` mục 5, bước 6).
  - Lần chạy `template-sync.sh` kế tiếp: `AdsFlags.kt` và `AdPlacementHold.kt` là phần app sửa trong `:core:ads`; người chạy đặt `hold=core/ads` theo ADR-1001.
  - Bản staging: `StagingRemoteValuesModule` bật mọi vị trí mới, nên sau khi merge, 4 khe rewarded nạp sẵn ngay lúc mở app (rewarded không bị giữ), và 6 khe interstitial nạp sẵn sau nét vẽ đầu tiên, dù chưa màn nào hiện chúng. Đó là quảng cáo thử của Google trên staging, không ảnh hưởng production (khóa mặc định tắt).
- Đề xuất thứ tự chạy: sau T-005 và T-006 (`depends_on`), trước mọi ticket đặt quảng cáo, nhất là T-008.

## 7. Điều kiện dừng riêng

- ADR-1001 chưa `accepted` trên nhánh chính lúc bắt đầu task: dừng, vì điểm nối là ngoại lệ của ADR-0030 và ADR-0045 mục 4.9 chưa được duyệt.
- Lệnh sinh lại bảng khóa đổi dòng nào khác ngoài 22 khóa `is_show_<mã>` và 2 khóa `native_every_<mã>` mới (trong `docs/remote-config-keys.md`), hay ngoài 24 tham số tương ứng (trong `docs/remote-config-template.json`): dừng, vì bảng trên nhánh chính đã lệch code. Dòng của khóa mà task đã merge trước sinh ra (`lessons_content_version` của T-048) không tính là dòng lạ, vì nó đã có trên nhánh chính.
- Làm điểm nối phải sửa code chính (ngoài thư mục test) của `:core:ads` ngoài `AdPlacement.kt`, `AdPlacementHold.kt`, `AdsFlags.kt`, ví dụ `AdsController.kt` hay `StartupAdChoice.kt`: dừng, vì mỗi file thêm là một chỗ lệch dự án mẫu mà người duyệt chưa đồng ý.
- Một test có sẵn của `:core:ads` đỏ vì hành vi (không phải vì dữ liệu chưa có vị trí mới như mục 3.5): dừng theo điều kiện 3, không sửa test.

## 8. Câu hỏi cho người duyệt

1. **Chỗ áp luật trước nét vẽ đầu tiên.** AI đề xuất điểm nối `AdPlacementHold` trong `:core:ads` (một file mới, một hàm của `DefaultAdsFlags`), quy tắc ở `:app` (mục 3.1). Lý do: mọi đường nạp và hiện đọc cùng một công tắc, nên luật chặn cả lượt nạp sẵn và một chỗ test được hết; năm feature không phải tự làm. Phương án khác: không sửa `:core:ads`, từng feature tự bỏ slot và lời gọi interstitial khi chưa có mốc; không lệch mẫu nhưng `:core:ads` vẫn nạp sẵn quảng cáo của vị trí có khóa bật rồi để hết hạn, và luật rải ở năm feature. Anh chọn.
2. **Duyệt ADR-1001 cùng đợt.** `docs/ops/to-app.md` mục 2 và ADR-0030: `:core:ads` trừ `AdPlacement.kt` là của dự án mẫu, app không sửa; ADR-0045 mục 4.9: cơ chế mới đề xuất về mẫu. Điểm nối là ngoại lệ, nên PR của đợt có `docs/adr/1001-diem-noi-giu-vi-tri-quang-cao.md` (`origin: app`, `proposed`): cho phép điểm nối `AdPlacementHold` và phần sửa `AdsFlags.kt`; `hold=core/ads` khi đồng bộ; xem lại sau mỗi đợt đồng bộ; mở issue `from-app` khi chạy ổn trên staging. Anh duyệt ADR cùng plan; ADR phải `accepted` trên nhánh chính trước khi task chạy (mục 7). Không duyệt ADR thì plan quay về phương án ở câu hỏi 1 (feature tự kiểm) hay phương án bọc `RemoteSettings`, và cần plan phiên bản 2.
3. **Vị trí ngoài bảng 9.1.** Theo quy tắc "chỉ cho qua vị trí đã kể", `sample_native_small` (màn mẫu, cờ tắt) bị giữ trước nét vẽ đầu tiên, không ảnh hưởng người dùng. Vị trí `gallery_*` của màn xem thử staging không bị giữ, để người thử xem đủ định dạng mà không phải vẽ trước. AI đề xuất giữ như vậy.
4. **Định dạng của `projects_grid` và `lessons_grid`.** Spec ghi "native ô lưới", "một ô sau mỗi N thẻ", nên AI chọn `native_in_collection` (ô thẻ chiếm một ô, N đếm theo mục, không làm tròn theo số cột). Phương án khác là `native_medium` (hàng rộng hết lưới, N làm tròn lên bội số cột). Ticket màn hình (T-010, T-051) dựng theo lựa chọn này.
5. **Rời màn vẽ ngay sau nét vẽ đầu tiên.** Interstitial bị giữ chỉ bắt đầu nạp sẵn khi mốc được ghi, nên lần rời màn vẽ đầu tiên thường chưa có quảng cáo (KB-20 vẫn đạt: "có thể hiện"). AI đề xuất chấp nhận, vì đây là phía ít quảng cáo hơn và đúng lúc người dùng mới bắt đầu dùng app.

## 9. Quyết định duyệt

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện
