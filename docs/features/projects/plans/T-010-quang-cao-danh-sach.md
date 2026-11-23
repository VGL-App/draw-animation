---
ticket: T-010
title: Quảng cáo ở danh sách dự án
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score:                  # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: high             # low | medium | high
token_estimate: 5.0M         # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: [ads]       # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-010: QUẢNG CÁO Ở DANH SÁCH DỰ ÁN

## 1. Tóm tắt

Đặt ba vị trí đã khai báo sẵn trong `AdPlacement` (T-007) vào màn danh sách dự án: `projects_empty_native` (native medium dưới hai nút của màn trống), `projects_bottom` (thanh đáy native nhỏ, chỉ ở trạng thái có dự án) và `projects_grid` (thẻ dọc một ô sau mỗi 6 dự án), bọc gốc màn bằng `InContentAds` với ba vị trí trong nội dung của spec (MC-13). Mọi vị trí chỉ theo khóa `is_show_<mã>` và các tham số Remote Config, không có luật trước nét vẽ đầu tiên (spec câu hỏi 24). Phần quảng cáo đi vào màn qua một bộ khe (`ProjectsAdSlots`, mặc định là composable của `:core:ads`, như `AdGallerySlots` của màn xem thử); test truyền khe giả vẽ `NativeAdCard`, nên kiểm chứng bằng test giao diện Robolectric đo vị trí, unit test danh sách vị trí, và screenshot test cùng tên ảnh thiết kế có quảng cáo.

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/projects/spec.md` (phần quảng cáo của KB-1, KB-2 nằm ở mục 5 của spec):

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-1 | Màn trống đúng thiết kế màn trống; mục 5: `projects_empty_native` native medium dưới hai nút "New animation" và "Try a free lesson", cách ít nhất 24 dp, mọi lúc danh sách trống, kể cả sau khi xóa hết dự án | Ô native medium là phần tử cuối của cột cuộn của màn trống, rộng bằng hai nút (363 dp ở 411 dp). Thiết kế đặt ô cách nút cuối 36 dp (`ProjectsScreen_empty_light`: nút "Try a free lesson" kết thúc ở y = 488, ô bắt đầu ở y = 524), thỏa "ít nhất 24 dp". Cờ Bài học tắt (không có nút "Try a free lesson") thì ô cách nút "New animation" 36 dp. Trạng thái trống không có thanh đáy (câu hỏi 14) |
| KB-1 | Khi màn hình thấp 360 × 640 thì nội dung cuộn được; hai nút không bị che hay cắt | Ô quảng cáo nằm dưới hai nút trong cùng cột cuộn, nên không đẩy hay che nút; cuộn tới hết ô được |
| KB-1 | Khi người dùng xóa dự án cuối cùng thì màn chuyển về trạng thái trống ngay | Trạng thái `Empty` có ô quảng cáo, `List` có thanh đáy và ô lưới; ô theo trạng thái nên đổi ngay khi trạng thái đổi (luồng sẵn có của T-009, T-012) |
| KB-2 | Màn có dự án đúng thiết kế màn danh sách; mục 5: `projects_bottom` thanh đáy native nhỏ trên thanh điều hướng, chỉ ở trạng thái có dự án | `ProjectsScreen` ở trạng thái `List` bọc lưới trong `BottomAdBarScaffold(PROJECTS_BOTTOM)`; thanh điều hướng thuộc `Scaffold` của `:app` nên thanh đáy của màn nằm ngay trên nó. Khe đáy cao theo `:core:ads` (106 dp ở cỡ chữ mặc định, D-02), không theo 72 dp của khung giữ chỗ trên thiết kế |
| KB-2 | Mục 5: `projects_grid` một ô sau mỗi 6 dự án (sau dự án thứ 6, 12, ...); có cả ở bố cục điện thoại gập (câu hỏi 15) | Lưới gọi `itemsWithInListAds(cards, ads, columns = PROJECT_GRID_COLUMNS)`: thẻ dọc chiếm một ô, N đếm theo dự án, không làm tròn theo cột; chỉ nằm giữa hai dự án (6 dự án thì không có ô, 7 dự án thì có một ô sau dự án thứ 6). N mặc định là `nativeEvery = 6` của vị trí; khóa `native_every_projects_grid` (mặc định 6) đã do T-007 sinh tự động từ khai báo vị trí, không thêm gì (câu hỏi 3). Số cột là một hằng dùng chung cho `GridCells.Fixed` và hàm chèn quảng cáo, để T-014 (ba cột khi gập mở ra) chỉ đổi hằng đó |
| KB-2 | Khi danh sách dài hơn màn thì cuộn được; thanh trên và thanh điều hướng giữ nguyên chỗ | Lưới nhận `padding` của thanh đáy làm lề dưới của phần cuộn: dự án cuối cuộn lên trên thanh đáy, không bị che |
| KB-4 (đặc biệt) | Menu đang mở thì không có quảng cáo nào trong menu | Không thêm quảng cáo vào `ProjectMenuSheet.kt`, `ProjectDialogs.kt`; thanh đáy ở cửa sổ activity, dưới lớp nền tối của cửa sổ menu (như plan T-012 đã nêu) |
| Mục 5, MC-13 | Màn danh sách khai báo ba vị trí trong nội dung `projects_empty_native`, `projects_grid`, `restore_mrec`: khi một trong ba đang bật thì thanh đáy không có lớp native thu gọn | Gốc màn bọc `InContentAds(projectsInContentAds())`, danh sách cố định ba vị trí, gồm cả `restore_mrec` dù hộp thoại khôi phục là T-013 (câu hỏi 2). Không liệt kê `projects_bottom` |
| Câu hỏi 24 | Không có luật trước nét vẽ đầu tiên | Không đọc `firstStrokeDrawn` của `:core:data` ở feature này; ba vị trí chỉ theo khóa và luật của `:core:ads` |

Thiết kế tham chiếu: `features/projects/design/README.md`, các ảnh trong `design/screens/`: `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark` (ô native medium), `ProjectsScreen_list_light`, `ProjectsScreen_list_dark`, `ProjectsScreen_menu_light`, `ProjectsScreen_menu_dark` (thanh đáy). Thiết kế không có frame cho ô quảng cáo trong lưới (frame danh sách có 4 dự án, frame gập mở ra có 6 dự án, đều chưa tới chỗ chèn ô). Ảnh `_unfolded` là của T-014.

ADR liên quan: ADR-0045 (quảng cáo: AdsKit PARTNER, mục 4 cách feature đặt vị trí), ADR-0001 (phụ thuộc module: `:core:ads` đã có trong plugin `template.android.feature`), ADR-0003 (khóa là `Flow`, do `:core:ads` theo dõi), ADR-0019 (log: `:core:ads` tự ghi), ADR-0020 (screenshot theo thiết kế), ADR-0029 (phần ngoài mục 3.2), ADR-0031 (viết lại câu khẳng định cũ).

## 3. Giải pháp

### 3.1. Cách làm

Làm theo mẫu có sẵn: tham số quảng cáo của `SampleScreen` (`bottomBar` mặc định `BottomAdBarScaffold`) và bộ khe `AdGallerySlots` của màn xem thử staging (`app/src/staging/.../adgallery/AdGalleryScreen.kt`: `gridEvery()` gọi `rememberInListAds` ở mức trang, `LazyGridScope.gridWithAds` gọi `itemsWithInListAds`, `InContentAds(galleryInContentAds(uiState))` ở gốc trang).

1. **Tệp mới `ProjectsAds.kt`** trong `:feature:projects`:
   - `interface ProjectsAdSlots` với ba phần, mỗi phần là composable hay hàm của `LazyGridScope`:
     - `@Composable fun EmptyAd(modifier: Modifier)`: ô native medium của màn trống.
     - `@Composable fun BottomBar(content: @Composable (PaddingValues) -> Unit)`: thanh đáy bọc nội dung của trạng thái có dự án.
     - `@Composable fun rememberGridAds(): ProjectsGridAds`, với `fun interface ProjectsGridAds { fun LazyGridScope.cards(cards: List<ProjectCardUi>, columns: Int, itemContent: @Composable LazyGridItemScope.(ProjectCardUi) -> Unit) }`: gọi ở mức màn, ngoài `LazyVerticalGrid`, như `rememberInListAds`.
   - `object DefaultProjectsAdSlots : ProjectsAdSlots`: `EmptyAd` gọi `NativeAdSlot(AdPlacement.PROJECTS_EMPTY_NATIVE, modifier)` (`keepSpaceWhenEmpty` mặc định `false`: ô nằm dưới mọi nút của màn, quảng cáo về hay mất không làm nút nào dời chỗ); `BottomBar` gọi `BottomAdBarScaffold(AdPlacement.PROJECTS_BOTTOM, content = content)`; `rememberGridAds` gọi `rememberInListAds(AdPlacement.PROJECTS_GRID)` rồi trả `ProjectsGridAds` gọi `itemsWithInListAds(items = cards, ads = ads, columns = columns, key = { it.id.value }, adCellHeight = { width -> projectCardHeight(width) }, itemContent = itemContent)`.
   - `fun projectsInContentAds(): List<AdPlacement> = listOf(PROJECTS_EMPTY_NATIVE, PROJECTS_GRID, RESTORE_MREC)`: danh sách cố định của màn cho `InContentAds`.
2. **`ProjectsScreen.kt`**:
   - `ProjectsScreen` thêm tham số `adSlots: ProjectsAdSlots = DefaultProjectsAdSlots` (sau các tham số có sẵn, có mặc định, nên `ProjectsRoute`, `AppNavHost` và test có sẵn không phải đổi lời gọi).
   - Gốc của `ProjectsScreen` bọc `InContentAds(projectsInContentAds()) { ... }` quanh `Box` gốc (bọc cả thanh đáy). Lớp phủ menu và hộp thoại (`ProjectOverlay`) ở cửa sổ riêng, không cần nằm trong.
   - Trạng thái `Empty`: `EmptyContent` nhận thêm `emptyAd: @Composable (Modifier) -> Unit`, đặt là phần tử cuối của cột cuộn, sau nút cuối (nút "Try a free lesson", hay "New animation" khi cờ Bài học tắt): `emptyAd(Modifier.fillMaxWidth().padding(top = EmptyAdTopPadding).testTag(ProjectsTestTags.EMPTY_AD))` với `EmptyAdTopPadding = 24.dp`, cộng `spacedBy(12.dp)` sẵn có của cột thành 36 dp như thiết kế. Không có thanh đáy.
   - Trạng thái `List`: thanh trên giữ ngoài thanh đáy (như hôm nay, thanh trên không cuộn); phần dưới thanh trên là `adSlots.BottomBar { padding -> ProjectGrid(..., bottomPadding = padding.calculateBottomPadding()) }`.
   - `SnackbarHost`: ở trạng thái `List` đặt trong nội dung của thanh đáy, lề dưới bằng `padding` của thanh, để thông báo ngắn (kể cả "Duplicating…" của T-012) không đè lên quảng cáo; trạng thái khác giữ ở đáy màn như hôm nay. Không có `AdsController` (test) thì `padding` là 0, vị trí như hôm nay.
   - `ProjectsTestTags` thêm `EMPTY_AD = "projects_empty_ad"`.
3. **`ProjectCard.kt`**:
   - Hằng `internal const val PROJECT_GRID_COLUMNS = 2`, dùng cho `GridCells.Fixed(...)` và tham số `columns` của khe lưới.
   - `ProjectGrid` nhận thêm `gridAds: ProjectsGridAds` và `bottomPadding: Dp`; `items(cards, ...)` thay bằng `with(gridAds) { cards(cards, PROJECT_GRID_COLUMNS) { card -> ProjectCard(...) } }`; `contentPadding.bottom = Spacing.md + bottomPadding`.
   - `@Composable internal fun projectCardHeight(cellWidth: Dp): Dp`: chiều cao thẻ dự án tính từ bề rộng ô (ảnh thu nhỏ `(cellWidth - 2 × Spacing.sm) / THUMBNAIL_ASPECT`, lề trên `Spacing.sm`, lề dưới 12 dp, hai khoảng `Spacing.sm`, một dòng `titleSmall`, một dòng `bodySmall` theo `lineHeight` của theme hiện tại đổi qua `LocalDensity`), để ô quảng cáo cao bằng thẻ dự án cạnh nó (câu hỏi 1). Hàm đọc cỡ chữ hiện tại nên chữ lớn thì cao theo.
4. **Không đổi `ProjectsViewModel.kt`, `ProjectsUiState.kt`, `AppNavHost`**: khóa `is_show_<mã>`, đồng ý quảng cáo, pool, nạp lại, log `VGL.Ads` và số liệu đều do `:core:ads` làm; ViewModel không cần biết quảng cáo. Không đọc mốc nét vẽ đầu tiên (câu hỏi 24 của spec).
5. **Không có chữ mới**: chữ trong ô quảng cáo là của quảng cáo (thiết kế README mục 2: chữ giữ chỗ, không vào `strings.xml`).

Khe giả cho test (`src/test`, tệp `FakeProjectsAdSlots.kt`): `EmptyAd` vẽ `NativeAdCard(assets giả, NativeAdSize.MEDIUM, modifier)`; `BottomBar` vẽ `BottomAdBarLayout(reservation = BottomAdBarDefaults.nativeReservation(), slot = { NativeAdCard(assets giả, NativeAdSize.SMALL, it) }, layer = null, content = content)` (không vẽ lớp thu gọn: màn có ô lưới hay ô màn trống bật thì không có lớp, MC-13, và ảnh thiết kế không có lớp) và ghi số lần được gọi; `rememberGridAds` trả hàm gọi `itemsWithFixedAds(cards, AdFormat.NATIVE_IN_COLLECTION, every = AdPlacement.PROJECTS_GRID.nativeEvery!!, columns, key = { it.id.value }, adContent = { BoxWithConstraints { NativeAdCard(assets giả, NativeAdSize.CARD, Modifier.testTag("projects_grid_ad_$it"), cardHeight = projectCardHeight(maxWidth)) } })`. Assets giả theo chữ của thiết kế: "Sponsored app name", "Advertiser", "Install" (ô màn trống), "Native ad, bottom bar" (thanh đáy).

### 3.2. Danh sách file

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsAds.kt` | `ProjectsAdSlots`, `ProjectsGridAds`, `DefaultProjectsAdSlots`, `projectsInContentAds()` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreen.kt` | Tham số `adSlots`, bọc `InContentAds`, ô của màn trống, thanh đáy của trạng thái có dự án, `SnackbarHost` trên thanh đáy, tag `EMPTY_AD` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectCard.kt` | `PROJECT_GRID_COLUMNS`, `ProjectGrid` nhận `gridAds`, `bottomPadding`; `projectCardHeight(cellWidth)` |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/FakeProjectsAdSlots.kt` | Khe giả vẽ `NativeAdCard`, `BottomAdBarLayout`, `itemsWithFixedAds`; đếm lần gọi `BottomBar` |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsAdsTest.kt` | Unit test danh sách vị trí, định dạng, `nativeEvery`; test giao diện vị trí ô, thanh đáy, ô lưới, thông báo trên thanh đáy, màn thấp |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreenScreenshotTest.kt` | Ảnh cùng tên thiết kế và ảnh `_small` truyền khe giả; thêm ảnh lưới có ô quảng cáo |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectMenuScreenshotTest.kt` | `menu_light`, `menu_dark` truyền khe giả (thanh đáy dưới lớp nền tối của menu, như thiết kế) |
| Sửa | `feature/projects/src/test/screenshots/ProjectsScreen_empty_light.png` | Ghi lại: có ô native medium giả |
| Sửa | `feature/projects/src/test/screenshots/ProjectsScreen_empty_dark.png` | Ghi lại: có ô native medium giả |
| Sửa | `feature/projects/src/test/screenshots/ProjectsScreen_empty_small.png` | Ghi lại: có ô native medium giả (cuộn ở đầu, ô lộ một phần) |
| Sửa | `feature/projects/src/test/screenshots/ProjectsScreen_list_light.png` | Ghi lại: có thanh đáy giả |
| Sửa | `feature/projects/src/test/screenshots/ProjectsScreen_list_dark.png` | Ghi lại: có thanh đáy giả |
| Sửa | `feature/projects/src/test/screenshots/ProjectsScreen_list_small.png` | Ghi lại: có thanh đáy giả |
| Sửa | `feature/projects/src/test/screenshots/ProjectsScreen_menu_light.png` | Ghi lại: thanh đáy giả dưới lớp nền tối |
| Sửa | `feature/projects/src/test/screenshots/ProjectsScreen_menu_dark.png` | Ghi lại: thanh đáy giả dưới lớp nền tối |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_listGridAd_light.png` | Ảnh mới: 8 dự án, ô thẻ dọc giả sau dự án thứ 6, thanh đáy giả |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_listGridAd_small.png` | Ảnh mới: như trên ở khung 360 × 640, cuộn tới ô quảng cáo |

Không đổi: `ProjectsViewModel.kt`, `ProjectsUiState.kt`, `ProjectMenuSheet.kt`, `ProjectDialogs.kt`, `strings.xml`, `AppNavHost.kt`, `:core:ads`. Ảnh chuẩn khác của module (`ProjectsScreen_list_unreadable`, `_menu_small`, `_menuUnreadable_light`, `_rename_*`, `_delete_*`, `_duplicating_light`) giữ nguyên: các test đó dùng khe mặc định, không có `AdsController` thì khe không vẽ gì. Ảnh chuẩn là tệp chỉ do Roborazzi của chính module đọc (`grep -rn "ProjectsScreen_" scripts/ .github/` chỉ thấy `scripts/design-compare.sh` đọc theo mẫu tên thiết kế, không theo danh sách cố định).

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không có cờ mới. Màn vẫn sau `feature_projects_enabled` (T-006, T-009).
- Quảng cáo: `projects_empty_native` (native medium), `projects_bottom` (thanh đáy native nhỏ), `projects_grid` (native trong tập hợp, `nativeEvery = 6`), đều có trong spec mục 5 và đã khai báo trong `AdPlacement` (T-007); khóa `is_show_projects_empty_native`, `is_show_projects_bottom`, `is_show_projects_grid`, `native_every_projects_grid` đã có trong `docs/remote-config-keys.md`. Khai báo `restore_mrec` trong `InContentAds` (không đặt khe của nó; hộp thoại là T-013). Không thêm, bớt vị trí hay khóa nào.
- Sự kiện phân tích: Không chạm. Số liệu quảng cáo do `:core:ads` ghi theo vị trí.
- File R2: Không chạm.
- **Quyền truy cập:** Không thêm.
- **Thư viện và SDK:** Không thêm. Dùng `:core:ads` (đã có trong plugin `template.android.feature`, `build-logic/convention/src/main/kotlin/AndroidFeatureConventionPlugin.kt`) với các hàm công khai `NativeAdSlot`, `BottomAdBarScaffold`, `InContentAds`, `rememberInListAds`, `itemsWithInListAds`; test dùng `NativeAdCard`, `NativeAdSize`, `BottomAdBarLayout`, `BottomAdBarDefaults`, `itemsWithFixedAds`, `AdFormat` của cùng module.

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** thẻ dọc `NativeAdCard(NativeAdSize.CARD)` ở chiều cao thẻ dự án (khoảng 166 dp ở ô rộng 184 dp theo thiết kế) có thể không đủ chỗ cho icon, tiêu đề, nhãn "Ad" và CTA 36 dp (thẻ mặc định cao 300 dp, ads-guide mục 4); khi đó media co về 0 hay CTA bị cắt.
- **Điều kiện chuyển:** test `ProjectsAdsTest.gridAd_ctaInsideCell` (mục 4) vẫn đỏ sau 2 lần sửa vì CTA của ô nằm ngoài khung ô, hay ảnh `ProjectsScreen_listGridAd_light.png` cho thấy nhãn "Ad" hoặc CTA bị cắt.
- **Cách làm thay:** không truyền `adCellHeight` (ô quảng cáo cao 300 dp mặc định của `:core:ads`), thẻ dự án cùng hàng giữ chiều cao của nó và căn trên. Test `gridAd_heightMatchesCard` đổi thành câu của phương án này đã viết sẵn ở mục 4 (chỉ khi chuyển). Người duyệt chọn trước ở câu hỏi 1.

### 3.5. Hành vi đã có bị đổi

Kết quả `scripts/existing-tests.py` ghi ở cuối mục này. Ngoài các ảnh chuẩn cùng tên thiết kế (mục 3.2 cho ghi lại), plan chỉ đổi hành vi khi có `AdsController`; test JVM không có `AdsController` nên khe mặc định không vẽ gì, `padding` của thanh đáy là 0.

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Cột cuộn của màn trống kết thúc ở nút cuối, lề dưới `Spacing.lg` | Cột có thêm ô quảng cáo sau nút cuối, lề trên 24 dp; không có `AdsController` thì ô là `Box` rỗng cao 0 nhưng vẫn cộng 24 dp lề trên và 12 dp khoảng cách vào cuối cột | `ProjectsScreenTest.small_emptyButtonsReachable` (cuộn tới nút, nút thấy được) | Giữ: hai nút vẫn ở trên ô, cuộn tới được; phần thêm nằm dưới nút cuối |
| Ảnh màn trống, danh sách, menu không có quảng cáo (plan T-009 câu hỏi 1, T-012) | Ảnh cùng tên thiết kế có ô native medium giả hay thanh đáy giả | `ProjectsScreenScreenshotTest.empty_light`, `empty_dark`, `empty_small`, `list_light`, `list_dark`, `list_small`; `ProjectMenuScreenshotTest.menu_light`, `menu_dark` | Ghi lại 8 ảnh của mục 3.2 với khe giả; ảnh khác của module không đổi |
| `SnackbarHost` luôn ở đáy `Box` gốc | Ở trạng thái `List`, `SnackbarHost` ở trên thanh đáy (lề dưới bằng `padding` của thanh) | `ProjectMenuTest.duplicating_otherMessageShowsImmediately`, `duplicating_showsSnackbarAndDisablesItem` (chỉ kiểm chữ có hay không, không kiểm vị trí) | Giữ |
| `ProjectGrid` chèn dự án bằng `items(cards, key = { it.id.value })` | Chèn qua `ProjectsGridAds.cards(...)`; khe mặc định không có `AdsController` thì không có ô quảng cáo, khóa dự án như cũ | `ProjectsScreenTest.scrollGrid_topBarStays`, `scrollPosition_survivesRestore`, `cardClick_callsCallbackWithCard`, `longName_isEllipsizedOnOneLine` | Giữ |

`scripts/existing-tests.py` (2026-11-23, trên `fd811d0`): 160 test dùng tên hay tag của `ProjectsScreen.kt`, `ProjectCard.kt`; "Cần xem trước" 6 test, "Nên xem" 30 test (10 đã nhắc ở bảng trên). Đã đọc từng test:

- "Cần xem trước": `ProjectMenuTest.deleteDialog_confirmUsesErrorColor`, `menuContent_bottomInsetAddedOnce`, `menuSheet_isAtBottom`, `renameDialog_buttonsUsePrimary`, `renameText_survivesRestore`, `small_menuFits`: đo trong cửa sổ riêng của sheet hay hộp thoại (hoặc lề thanh hệ thống truyền qua tham số), không đo phần nằm dưới thanh đáy; khe mặc định không vẽ gì khi không có `AdsController`. Giữ.
- "Nên xem": các test còn lại của `ProjectMenuTest`, `ProjectsRouteTest`, `ProjectsScreenTest` chỉ dùng tag (`NEW`, `LESSON`, `GRID`, `card`, ...) để chạm hay kiểm có node; tag và khóa dự án không đổi, khe mặc định không thêm node. Giữ. `ProjectMenuTest.scrollToTop_scrollsGridToFirstCard` cuộn lưới bằng `gridState`: chỉ số mục không đổi khi không có ô quảng cáo. Giữ.

## 4. Kiểm thử

Mọi test mới ở `ProjectsAdsTest.kt` (Robolectric, `@GraphicsMode(GraphicsMode.Mode.NATIVE)` cho test đo, khung `w411dp-h891dp-xxhdpi` trừ khi ghi khác), dựng `ProjectsScreen` trong `AppTheme` với `adSlots = FakeProjectsAdSlots()`. Test viết trước thất bại vì chưa có tham số `adSlots`, `ProjectsAdSlots`, `projectsInContentAds`, `projectCardHeight` (lỗi biên dịch của tệp test là lý do đúng ở bước 3); câu khẳng định đo vị trí thất bại đúng lý do khi bước 4 mới dựng khung rỗng (tham số có mà chưa vẽ khe).

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| MC-13: ba vị trí trong nội dung, không có thanh đáy của màn | Unit test | `ProjectsAdsTest.inContentAds_areTheThreeOfTheSpec`: `projectsInContentAds()` bằng đúng `listOf(PROJECTS_EMPTY_NATIVE, PROJECTS_GRID, RESTORE_MREC)`, không chứa `PROJECTS_BOTTOM`; `inContentAds_doNotThrow`: dựng `InContentAds(projectsInContentAds()) { Text("x") }` không ném lỗi và có chữ "x" (đối chứng: `InContentAds(listOf(AdPlacement.CREATE_INTER)) {}` ném `IllegalArgumentException`, để chắc kiểm tra định dạng có chạy) |
| Mục 5: định dạng và nhịp của ba vị trí | Unit test | `ProjectsAdsTest.placements_matchSpec`: `PROJECTS_EMPTY_NATIVE.format == NATIVE_MEDIUM`, `PROJECTS_BOTTOM.format == NATIVE_SMALL`, `PROJECTS_GRID.format == NATIVE_IN_COLLECTION`, `PROJECTS_GRID.nativeEvery == 6`, mã `projects_empty_native`, `projects_bottom`, `projects_grid` |
| KB-1: ô native medium dưới hai nút, cách ít nhất 24 dp | Compose UI test | `ProjectsAdsTest.emptyAd_belowLessonButton_36dp`: trạng thái `Empty(showLessonButton = true)`: `top(projects_empty_ad) - bottom(projects_lesson) == 36 dp` (sai số 0,5 dp) và `>= 24 dp`; bề rộng ô bằng bề rộng nút "New animation" (363 dp); ô nằm dưới cả hai nút (không chồng). `emptyAd_withoutLessonButton_belowNewButton`: `showLessonButton = false`: `top(projects_empty_ad) - bottom(projects_new) == 36 dp`; đối chứng không có node `projects_lesson` |
| KB-1: trạng thái trống không có thanh đáy; KB-2: thanh đáy chỉ ở trạng thái có dự án | Compose UI test | `ProjectsAdsTest.bottomBar_onlyInList`: `Empty` thì khe giả đếm `BottomBar` = 0 lần và không có node tag `fake_bottom_bar`; đổi `uiState` sang `List` (2 dự án) trong cùng nội dung: có `fake_bottom_bar`, không có `projects_empty_ad` |
| KB-1: xóa dự án cuối thì về trống ngay (quảng cáo đổi theo) | Compose UI test | `ProjectsAdsTest.listToEmpty_swapsBarForEmptyAd`: `List` rồi `Empty` trong cùng nội dung (`mutableStateOf`): sau `waitForIdle` không còn `fake_bottom_bar`, có `projects_empty_ad` |
| KB-2: thanh đáy trên thanh điều hướng, dự án cuối không bị che | Compose UI test | `ProjectsAdsTest.list_lastCardScrollsAboveBottomBar`: 12 dự án, cuộn lưới tới cuối (`performScrollToIndex` mục cuối): `bottom(card cuối) <= top(fake_bottom_bar)`; thanh đáy giả bám đáy màn (`bottom(fake_bottom_bar) == chiều cao gốc`) |
| Thông báo ngắn không đè quảng cáo | Compose UI test | `ProjectsAdsTest.snackbar_aboveBottomBar`: `List`, `snackbarHostState.showSnackbar("Coming soon")` (`mainClock.autoAdvance = false`, `advanceTimeBy(500)`): `bottom(node chữ "Coming soon") <= top(fake_bottom_bar)`; đối chứng ở `Empty`: thông báo vẫn hiện (có chữ) |
| KB-2: một ô sau mỗi 6 dự án, chỉ giữa hai dự án | Compose UI test | `ProjectsAdsTest.gridAd_afterSixthProject`: 7 dự án: có đúng một node `projects_grid_ad_0`; ô nằm sau dự án thứ 6 theo thứ tự lưới (hàng 4, cột 1: `left(ad) == left(card 1)`, `top(ad) > top(card 6)`; dự án thứ 7 ở cột 2 cùng hàng: `top(card 7) == top(ad)`). `gridAd_noneWithSixProjects`: 6 dự án: không có node `projects_grid_ad_*` (đối chứng của câu "không ở cuối"). `gridAd_twoWithThirteenProjects`: 13 dự án: có `projects_grid_ad_0`, `projects_grid_ad_1`; 12 dự án: chỉ `projects_grid_ad_0` |
| KB-2: ô quảng cáo cao bằng thẻ cạnh nó | Compose UI test | `ProjectsAdsTest.gridAd_heightMatchesCard`: 7 dự án: `height(projects_grid_ad_0) == height(card 7)` (sai số 1 dp); `projectCardHeight(184.dp)` trong `AppTheme` bằng chiều cao đo của thẻ dự án rộng 184 dp (sai số 1 dp). `gridAd_ctaInsideCell`: chữ "Install" của ô nằm trọn trong khung ô (dùng cho điều kiện chuyển của mục 3.4). Câu thay khi chuyển sang dự phòng (chỉ khi đó): `height(projects_grid_ad_0) == 300 dp` và `top(card 7) == top(ad)` |
| Có cả ở bố cục gập (câu hỏi 15) | Compose UI test | `ProjectsAdsTest.gridAd_usesGridColumns`: hằng `PROJECT_GRID_COLUMNS` bằng số cột đo được (2 thẻ đầu cùng hàng, thẻ thứ 3 xuống hàng) và khe giả nhận `columns == PROJECT_GRID_COLUMNS`. Ảnh `_unfolded` và ba cột là của T-014 |
| Câu hỏi 24: không có luật trước nét vẽ đầu tiên | Unit test | `ProjectsAdsTest.noFirstStrokeRule`: quét import của mọi tệp `src/main` của `:feature:projects`: không tệp nào import `DeviceSettingsRepository` hay gọi `firstStrokeDrawn` (quét theo ký hiệu import và tên thuộc tính, không theo chuỗi con tự do) |
| Khe mặc định không vẽ gì khi không có `AdsController` (không làm lệch test sẵn có) | Compose UI test | `ProjectsAdsTest.defaultSlots_withoutController_noAdNodes`: `ProjectsScreen` không truyền `adSlots`, `List` 7 dự án: không có node `projects_grid_ad_*` hay `fake_bottom_bar`; `Empty`: node `projects_empty_ad` cao 0 dp |
| Giao diện màn trống có quảng cáo | Screenshot test | `ProjectsScreen_empty_light.png`, `ProjectsScreen_empty_dark.png` (chữ thiết kế là tiếng Anh, giữ khung `w411dp-h891dp-xxhdpi` như ảnh T-009) |
| Giao diện màn danh sách có thanh đáy | Screenshot test | `ProjectsScreen_list_light.png`, `ProjectsScreen_list_dark.png`; menu: `ProjectsScreen_menu_light.png`, `ProjectsScreen_menu_dark.png` |
| Lưới có ô quảng cáo (không có frame thiết kế) | Screenshot test | `ProjectsScreen_listGridAd_light.png` (8 dự án, cuộn tới ô quảng cáo bằng `gridState.scrollToItem(8)` trước khi chụp) |
| Màn hình thấp | Screenshot test khung `w360dp-h640dp-xxhdpi`, trạng thái nhiều nội dung nhất | `ProjectsScreen_empty_small.png` (ô quảng cáo ở cuối cột cuộn), `ProjectsScreen_list_small.png` (thanh đáy giả), `ProjectsScreen_listGridAd_small.png` (8 dự án, cuộn tới ô quảng cáo); kèm `ProjectsAdsTest.small_emptyAdReachable` (khung 360 × 640: `performScrollTo()` tới `projects_empty_ad` rồi nút "New animation", "Try a free lesson" vẫn `assertIsDisplayed` sau khi cuộn về) |
| Đúng thiết kế (khi có `design/screens/`) | Screenshot test cùng tên với từng ảnh thiết kế, ảnh so sánh `scripts/design-compare.sh projects`, rà soát độc lập theo mục 7 của mẫu thiết kế. Thanh điều hướng vẫn không có trong ảnh của feature (plan T-009, câu hỏi 1); khe đáy cao 106 dp theo `:core:ads` thay cho khung 72 dp của thiết kế (D-02) | `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark`, `ProjectsScreen_list_light`, `ProjectsScreen_list_dark`, `ProjectsScreen_menu_light`, `ProjectsScreen_menu_dark` |
| Ảnh thiết kế của ticket khác | Ghi chú | Ảnh thiết kế của ticket khác (T-011): `NewProjectScreen_default_light`, `NewProjectScreen_default_dark`; (T-013): `ProjectsScreen_restoring_light`, `ProjectsScreen_restoring_dark`, `ProjectsScreen_restore-error_light`, `ProjectsScreen_restore-error_dark`; (T-014): `ProjectsScreen_list_light_unfolded`, `ProjectsScreen_list_dark_unfolded` |
| Quảng cáo thật hiện đúng chỗ theo khóa trên staging | Kiểm tra thủ công (máy thật: Remote Config) | Trên Firebase staging bật `is_show_projects_empty_native`, `is_show_projects_bottom`, `is_show_projects_grid` (bản staging không bật sẵn các khóa này: `app/src/staging` không có dòng nào về `projects_`). Màn trống: ô native medium test dưới hai nút, không có thanh đáy. Tạo (hay khôi phục, khi có T-011, T-013) 7 dự án: thanh đáy native nhỏ trên thanh điều hướng, ô thẻ dọc sau dự án thứ 6, không có lớp native thu gọn; `adb logcat -v threadtime \| grep ' VGL\.Ads'` có dòng `Floating ads off on this screen: in-content ad on (projects_empty_native, projects_grid, restore_mrec)` mỗi lần vào màn (dòng do `InContentAds` ghi khi ít nhất một vị trí trong danh sách bật). Tắt cả ba khóa: không còn quảng cáo nào |
| Không có luật trước nét vẽ đầu tiên trên máy | Kiểm tra thủ công (máy thật: Remote Config) | Cài mới, chưa vẽ nét nào, khóa bật như trên: quảng cáo hiện ngay ở màn trống |

**Ngoại lệ viết test trước:** Phần nối `DefaultProjectsAdSlots` gọi hàm thật của `:core:ads` (`NativeAdSlot`, `BottomAdBarScaffold`, `rememberInListAds`, `itemsWithInListAds`): test JVM không có `AdsController`, nên phần quảng cáo thật chỉ kiểm thủ công trên staging (hai dòng cuối bảng); test `defaultSlots_withoutController_noAdNodes` chỉ kiểm phần nối không vẽ gì khi không có bộ điều khiển. Người duyệt phải đồng ý.

### 4.1. Chạy thử

Không cần (E dưới 2): plan dùng lại đúng các hàm công khai mà màn xem thử staging (`AdGalleryScreen`) và `SampleScreen` đã dùng và đã có test (`AdGalleryScreenTest`, `SampleScreenScreenshotTest`); không thêm thư viện, module hay quyền. Điểm còn chưa chắc (thẻ dọc ở chiều cao thẻ dự án) có phương án dự phòng ở mục 3.4 và test `gridAd_ctaInsideCell` làm điều kiện chuyển; người duyệt chọn trước ở câu hỏi 1.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** Quảng cáo (điểm chặn 1): thêm ba vị trí quảng cáo vào màn danh sách. Mức Cao, bắt buộc duyệt chéo; người phụ trách sản phẩm (anh Nhật) cần biết trước khi duyệt (risk-scoring.md, mục 3).

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 2 | Ba tệp code và bốn tệp test trong một module feature; đổi giao diện công khai của `ProjectsScreen` (tham số mới có mặc định) |
| B. Độ rõ của yêu cầu | 1 | Spec `confirmed`, vị trí và khóa rõ; thiết kế không có frame ô quảng cáo trong lưới nên chiều cao ô và khe đáy 106 dp so với 72 dp của thiết kế do AI chọn (câu hỏi 1) |
| C. Khả năng kiểm chứng tự động | 1 | Vị trí, điều kiện trạng thái và danh sách vị trí có test; quảng cáo thật theo khóa chỉ kiểm trên staging |
| D. Ảnh hưởng chéo | 2 | `ProjectsScreen.kt`, `ProjectCard.kt` cũng do T-013, T-014, T-056 sửa |
| E. Độ mới của kỹ thuật | 1 | Biến thể của mẫu có sẵn (`SampleScreen`, `AdGallerySlots` của màn xem thử); lần đầu một feature thật dùng `itemsWithInListAds` cho lưới và `InContentAds` |
| **Tổng** | 7 | **Mức: Cao** (chạm điểm chặn 1) |

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: chỉ dùng hàm công khai của `:core:ads`, không sửa. `ProjectsScreen` có tham số mới có mặc định, `AppNavHost` không đổi.
- Task khác trong sprint có thể xung đột:
  - T-013 (sao lưu, khôi phục): sửa `ProjectsScreen.kt` để thêm hộp thoại khôi phục và đặt khe `restore_mrec` (`AdSlot`); `InContentAds` của T-010 đã khai báo `restore_mrec`, T-013 không phải khai báo lại.
  - T-014 (gập mở ra): đổi `PROJECT_GRID_COLUMNS` thành số cột theo bề rộng cửa sổ và thêm ảnh `_unfolded`; ô quảng cáo theo cùng số cột (T-014 phụ thuộc T-010).
  - T-056 (nút quyền riêng tư): sửa thanh trên trong `ProjectsScreen.kt`, không đụng phần quảng cáo.
  - Điểm nên sửa còn để người quyết của T-012 (nhật ký T-012, PR #36): đang nhân bản mà có hai thông báo khác trong 4 giây thì thông báo thứ hai chờ tới khi nhân bản xong. T-010 dời `SnackbarHost` lên trên thanh đáy nhưng không đổi cơ chế xếp hàng thông báo của `ProjectsRoute`, nên không sửa điểm này (câu hỏi 4).
- Đề xuất thứ tự chạy: sau T-009, T-012 (đã merge). Trước T-013, T-014, T-056; cùng danh sách thì tuần tự (cùng sửa `ProjectsScreen.kt`).

## 7. Điều kiện dừng riêng

- Cần sửa `:core:ads` (ví dụ `itemsWithInListAds` không nhận được thứ plan cần, `BottomAdBarScaffold` không đặt được dưới thanh trên): dừng, vì là module của dự án mẫu (đề xuất về dự án mẫu theo `docs/ops/from-app.md`).
- Thêm vị trí, khóa Remote Config hay đổi `nativeEvery` của `projects_grid`: dừng (ngoài spec).
- Ảnh chuẩn ngoài 10 ảnh của mục 3.2 đổi (trừ nhiễu vẽ lệch 1 mỗi kênh, trả bằng `git checkout -- <ảnh>` như `CLAUDE.md`, mục Lệnh): dừng.

## 8. Câu hỏi cho người duyệt

1. **Chiều cao ô quảng cáo trong lưới.** Thiết kế không có frame ô quảng cáo trong lưới. Thẻ dọc mặc định cao 300 dp; thẻ dự án khoảng 166 dp. Đề xuất: ô quảng cáo cao bằng thẻ dự án cạnh nó (`adCellHeight`, cách ads-guide nêu cho lưới có thẻ của app), hàng lưới đều; media của quảng cáo khi đó nhỏ. Phương án khác (đã ghi làm dự phòng ở mục 3.4): ô 300 dp mặc định, thẻ dự án cùng hàng căn trên, để trống phía dưới.
2. **Khai báo `restore_mrec` trong `InContentAds` từ T-010.** Spec mục 5 ghi màn khai báo ba vị trí; hộp thoại khôi phục là T-013. Khai báo trước thì khi chỉ `is_show_restore_mrec` bật, lớp native thu gọn của thanh đáy đã bị chặn dù chưa có hộp thoại. Đề xuất: khai báo đủ ba như spec (danh sách cố định cho màn, T-013 không phải sửa). Phương án khác: T-010 khai báo hai vị trí, T-013 thêm `restore_mrec`.
3. **Khóa `native_every_projects_grid`.** Spec câu hỏi 15 chốt số cố định 6, không thêm khóa; nhưng `:core:ads` (T-007) tự sinh khóa này từ `nativeEvery = 6` và `rememberInListAds` đọc nó (mặc định 6, đã có trong `docs/remote-config-keys.md`). Đề xuất: giữ như T-007 (không đặt khóa trên Firebase thì luôn là 6), không sửa `:core:ads`. Phương án khác: ghi rõ vào spec là khóa có sẵn nhưng nhóm không dùng.
4. **Điểm nên sửa của T-012 về thông báo khi đang nhân bản.** T-010 sửa cùng `ProjectsScreen.kt` nhưng không đổi cách xếp hàng thông báo. Đề xuất: không gộp vào T-010 (khác phạm vi quảng cáo), để một ticket riêng hay plan T-012 phiên bản sau khi anh quyết. Phương án khác: cho T-010 làm phương án host riêng của mục 3.4 plan T-012 (cần thêm test của T-012 vào mục 4 của plan này).
5. **Thông báo "Coming soon" của mục Lessons ở `:app`.** `AppNavHost` hiện thông báo này ở `SnackbarHost` của `Scaffold`, ngay trên thanh điều hướng, tức đè lên thanh đáy quảng cáo của màn danh sách trong 4 giây khi người dùng chạm "Lessons" (tới khi có T-050). Đề xuất: chấp nhận tạm, T-050 bỏ thông báo này; ghi trong báo cáo. Phương án khác: T-010 thêm `AppNavHost.kt` vào mục 3.2 và ẩn thông báo đó khi đích là danh sách dự án (thông báo đưa vào màn danh sách qua `snackbarHostState` của `ProjectsRoute`).

## 9. Quyết định duyệt

- **Quyết định:** Duyệt / Dừng / Duyệt có điều kiện
- **Người duyệt:** <tên>, **duyệt chéo:** <tên, nếu rủi ro cao>
- **Ghi chú:** <điều kiện, yêu cầu sửa, trả lời câu hỏi ở mục 8>

## 10. Nhật ký thực hiện

<!-- AI ghi khi task đã có kết quả cuối (đã merge hoặc đã dừng), trên nhánh báo cáo của phiên (auto-run.md, bước 10). Mục mới thêm vào cuối, không sửa mục cũ. -->
