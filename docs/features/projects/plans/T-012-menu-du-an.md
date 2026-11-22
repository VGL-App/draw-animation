---
ticket: T-012
title: Menu dự án - mở, đổi tên, nhân bản, xóa
status: draft                # draft | approved | stopped | done
version: 1                   # tăng khi plan được sửa lại sau phản hồi hoặc sau khi dừng
risk_score: 8                # tổng điểm theo risk-scoring.md, để trống nếu chạm điểm chặn
risk_level: medium           # low | medium | high
token_estimate: 15.2M        # tổng token dự kiến, ví dụ 20M; số gợi ý: scripts/token-usage.py estimate (docs/ops/token-usage.md)
blocking_points: []          # các điểm chặn bị chạm, ví dụ [ads, sdk]
run_order:                   # AI điền khi lập danh sách chạy
reviewer:                    # người duyệt
cross_reviewer:              # bắt buộc khi risk_level là high
approved_at:
lane:                        # để trống: phiên tự chạy; quick: đường làm nhanh có người theo dõi (ADR-0048)
---

# PLAN T-012: MENU DỰ ÁN - MỞ, ĐỔI TÊN, NHÂN BẢN, XÓA

## 1. Tóm tắt

Thêm vào `:feature:projects` (module do plan T-009 tạo) menu dự án mở bằng nhấn giữ thẻ: `ModalBottomSheet` của Material 3 với đầu menu (ảnh thu nhỏ, tên, "<n> frames · <fps> fps · edited <thời điểm>") và năm mục theo thiết kế; hộp thoại đổi tên và hộp thoại xác nhận xóa là `AlertDialog` chuẩn; nhân bản hiện thông báo ngắn "đang nhân bản" và chặn bấm lại tới khi xong. Mọi thao tác đi qua `ProjectRepository` có sẵn của `:core:project` (`rename`, `duplicate`, `delete` của T-002), không sửa module core. Kiểm chứng bằng unit test ViewModel, test giao diện Robolectric (mở và đóng menu bằng nút quay lại, chạm ngoài, kéo xuống), screenshot test cùng tên ảnh thiết kế `ProjectsScreen_menu_*`.

**Phụ thuộc plan T-009 chưa merge.** Plan này xây trên tên và file mà plan T-009 phiên bản 1 (PR nháp #26, nhánh `claude/plan-T-009-man-danh-sach-du-an`, bản đọc ngày 2026-11-22) sẽ tạo: `ProjectsViewModel`, `ProjectsUiState` (`Empty`, `List`, `Loading`, `Disabled`), `ProjectCardUi(id, name, frameCount, fps, updatedAtMillis, readable)`, `ProjectsEffect` (`OpenProject`, `ShowCannotOpen`, `ShowComingSoon`), `ProjectsRepository`/`DefaultProjectsRepository`, `FakeProjectsRepository`, `ProjectsRoute` với callback `null` nghĩa là "Coming soon", `ProjectsTestTags`. T-009 đổi thì plan này phải xem lại (mục 6, mục 8 câu 1).

## 2. Hiểu yêu cầu

Ticket xử lý các kịch bản sau trong `features/projects/spec.md`:

| Kịch bản | Tiêu chí chấp nhận | Cách hiểu của AI |
| --- | --- | --- |
| KB-4 | Nhấn giữ thẻ: menu trượt lên từ đáy, nền tối; đầu menu ảnh thu nhỏ, tên, "<số khung> frames · <tốc độ> fps · edited <thời điểm>"; mục "Open", "Rename", "Duplicate", "Back up to a file" (chú thích ".frameloop"), "Delete" (màu báo lỗi), đúng thiết kế | `ProjectCard` thêm `onLongClick` (`combinedClickable`); ViewModel đặt `ProjectsOverlay.Menu(card)`; màn vẽ `ModalBottomSheet` (`skipPartiallyExpanded = true`, `containerColor = surfaceContainerHigh`, `scrimColor = scrim` độ mờ 0.55 như thiết kế). "edited" theo hàm thuần `editedAt` (câu hỏi 3) |
| KB-4 | Bấm "Open" thì mở trong màn vẽ | Đóng menu rồi đi cùng đường với chạm thẻ của T-009 (`OpenProject` khi cờ `EDITOR` bật, `ShowComingSoon` khi tắt, theo spec KB-2 dòng "Open" trong menu) |
| KB-4 | Chạm ngoài, kéo xuống, nút quay lại thì menu đóng, danh sách giữ nguyên | Cả ba gọi `onDismissRequest` của `ModalBottomSheet` (đã chạy thử, mục 4.1) → `dismissOverlay()`; danh sách không đổi vì không gọi kho |
| KB-4 (đặc biệt) | Menu mở thì không có quảng cáo trong menu | Nội dung menu không có `AdSlot`/`NativeAdSlot`; test quét import của tệp menu và hộp thoại. Thanh đáy native của T-010 nằm ở cửa sổ activity, dưới lớp nền tối của cửa sổ menu |
| KB-5 | Bấm "Rename": hộp thoại tiêu đề "Rename", "Save", "Cancel", ô nhập có sẵn tên hiện tại, bàn phím hiện | `ProjectsOverlay.Rename(card)`; `AlertDialog` với `OutlinedTextField` nhận `FocusRequester`, `requestFocus()` trong `LaunchedEffect` (ô có tiêu điểm thì bàn phím mềm hiện; test kiểm tiêu điểm, máy thật kiểm bàn phím). Con trỏ đặt cuối tên (`TextFieldValue` với `TextRange(name.length)`) |
| KB-5 | Xác nhận thì thẻ hiện tên mới ngay; mở lại app vẫn giữ | `ProjectsRepository.rename` → `ProjectRepository.rename` ghi `project.json` và phát lại danh sách (T-002); tên lưu trên đĩa nên mở lại app vẫn giữ (test của T-002) |
| KB-5 | Hủy thì tên giữ nguyên | "Cancel", chạm ngoài, quay lại: `dismissOverlay()`, không gọi kho |
| KB-5 (đặc biệt) | Ô trống hay chỉ khoảng trắng thì nút xác nhận tắt | `Save` `enabled = text.isNotBlank()`; ViewModel cũng bỏ qua tên trống (không gọi `rename`, vì `ProjectNames.normalize` ném `IllegalArgumentException` với tên trống) |
| KB-5 (đặc biệt) | Trùng tên dự án khác vẫn cho lưu | Không kiểm trùng; kho không kiểm trùng (T-002) |
| KB-5 (ngầm, câu hỏi 5) | Tên tối đa 40 ký tự | Ô nhập cắt ở `ProjectNames.MAX_NAME_LENGTH` điểm mã (code point) bằng `takeCodePoints` riêng của feature, cùng cách đếm với kho; kho vẫn `normalize` |
| KB-6 | Nhân bản: bản sao đủ khung, layer, âm thanh, khung hình, độ phân giải, tốc độ, nền ở đầu danh sách, tên "<tên> copy" (lần sau "copy 2"), cắt tên gốc khi quá 40 ký tự; gốc không đổi | `ProjectsRepository.duplicate` → `ProjectRepository.duplicate` (T-002 làm và đã có test cho mọi điều này); ViewModel chỉ gọi và báo kết quả |
| KB-6 | Vẽ trên bản sao thì gốc không đổi và ngược lại | Bản sao có mã mới và tệp riêng (T-002, `duplicate_copiesEveryReferencedFile`, `duplicate_sourceUnchanged`); không có gì mới ở T-012 |
| KB-6 (đặc biệt) | Hết chỗ: báo lỗi dễ hiểu, không để bản sao dở | Kho trả `Failure(NO_SPACE)` và đã xóa bản dở (T-002); ViewModel phát thông báo "Not enough space on your phone. Free up some space and try again." (chữ của câu hỏi 12, câu hỏi 2 của plan) |
| KB-6 (đặc biệt) | Dự án lớn: có dấu hiệu đang làm; không bấm nhân bản lần nữa được tới khi xong | `duplicatingId: ProjectId?` trong `ProjectsMenuState`; khi khác `null`: thông báo ngắn không tự ẩn "Duplicating “<tên>”…" (spec mục 4, "đang nhân bản" là thông báo ngắn chuẩn) và mục "Duplicate" của mọi menu `enabled = false`; `onDuplicate` của ViewModel bỏ qua khi đang nhân bản |
| KB-7 | Bấm "Delete": hộp thoại "Delete “<tên>”?", "This can't be undone.", "Delete", "Cancel"; đã xóa không hoàn tác | `ProjectsOverlay.ConfirmDelete(card)`; `AlertDialog`; nút "Delete" màu `error` (truyền `colors` rõ cho `TextButton`) |
| KB-7 | Xác nhận: thẻ biến mất, dữ liệu xóa khỏi máy, mở lại không thấy | `ProjectsRepository.delete` → `ProjectRepository.delete` xóa thư mục và phát lại danh sách (T-002) |
| KB-7 | Hủy thì giữ nguyên | `dismissOverlay()`, không gọi kho |
| KB-7 | Xóa dự án cuối thì về trạng thái trống | Danh sách rỗng phát lại thì `ProjectsViewModel` của T-009 ra `Empty` (T-009 đã có test với kho giả); T-012 thêm một test đi trọn đường xóa |
| KB-2 (đặc biệt, phần T-012) | Dự án hỏng: menu chỉ còn "Delete"; app không tự xóa | `ProjectMenuContent` với `card.readable == false` chỉ có mục "Delete"; đầu menu có tên (nếu đọc được), ảnh trống, không có dòng số khung. Xóa chỉ khi người dùng xác nhận |
| KB-12 (phần T-012) | Gập, mở máy giữ menu hay hộp thoại đang mở | `ProjectsOverlay` nằm trong `ProjectsViewModel` (sống qua đổi cấu hình); chữ đang gõ trong hộp đổi tên giữ bằng `rememberSaveable` |

Thiết kế tham chiếu: `features/projects/design/README.md`, các ảnh trong `design/screens/`: `ProjectsScreen_menu_light`, `ProjectsScreen_menu_dark` (số liệu ở `design/context/ProjectsScreen_menu_*.md`). Hộp thoại đổi tên, xác nhận xóa và thông báo ngắn không có frame riêng (spec mục 4, câu hỏi 2): dùng `AlertDialog` và `Snackbar` chuẩn.

ADR liên quan: ADR-0001 (feature chỉ phụ thuộc core), ADR-0003 (cờ `EDITOR` theo dõi bằng `Flow`, đã có ở T-009), ADR-0008 (hệ thống thiết kế), ADR-0019 (log), ADR-0020 (screenshot cùng tên ảnh thiết kế), ADR-0031 (câu khẳng định cũ), ADR-0045 (quảng cáo: không chạm).

## 3. Giải pháp

### 3.1. Cách làm

Gói `com.vgl.draw.animation.feature.projects` (của T-009). Mọi tên của T-009 dưới đây lấy theo plan T-009 phiên bản 1.

**Dữ liệu.** `data/ProjectsRepository.kt` thêm ba hàm, `DefaultProjectsRepository` chuyển thẳng cho `ProjectRepository` của `:core:project` (đã tiêm ở T-009 cho `items`), trả nguyên `ProjectResult`:

```kotlin
suspend fun rename(id: ProjectId, name: String): ProjectResult<Project>
suspend fun duplicate(id: ProjectId): ProjectResult<Project>
suspend fun delete(id: ProjectId): ProjectResult<Unit>
```

Không bắt `IOException`: kho của T-002 đã đổi mọi lỗi ghi thành `ProjectResult.Failure`. `rename` với tên trống ném `IllegalArgumentException` (lỗi lập trình), nên ViewModel không bao giờ gọi với tên trống.

**Trạng thái** (thêm vào `ProjectsUiState.kt`):

```kotlin
sealed interface ProjectsOverlay {
    data object None : ProjectsOverlay
    data class Menu(val card: ProjectCardUi) : ProjectsOverlay
    data class Rename(val card: ProjectCardUi) : ProjectsOverlay
    data class ConfirmDelete(val card: ProjectCardUi) : ProjectsOverlay
}

data class ProjectsMenuState(
    val overlay: ProjectsOverlay = ProjectsOverlay.None,
    val duplicatingName: String? = null, // non-null while a duplicate runs
    val busy: Boolean = false,           // a rename or delete is being written
)

enum class ProjectsMessage { RENAME_FAILED, DELETE_FAILED, DUPLICATE_FAILED, NO_SPACE }
```

`ProjectsEffect` (T-009) thêm `data class ShowMessage(val message: ProjectsMessage)`. Thẻ hỏng có `name == null` thì đầu menu và hộp xác nhận xóa dùng cùng chữ thay thế mà thẻ của T-009 dùng cho tên không đọc được.

**ViewModel.** `ProjectsViewModel` (T-009) thêm `menuState: StateFlow<ProjectsMenuState>` (một `MutableStateFlow` riêng, không gộp vào `combine` của `uiState`, để mở menu không phát lại danh sách) và các hàm:

- `onProjectLongClick(card)`: `overlay = Menu(card)`. Bỏ qua khi `overlay` khác `None`.
- `onMenuOpen()`: đọc thẻ của `Menu`, `overlay = None`, rồi gọi đúng nhánh của `onProjectClick(card)` (T-009) để phát `OpenProject` hay `ShowComingSoon` hay `ShowCannotOpen`; cờ `EDITOR` đọc ngay lúc bấm từ giá trị đang theo dõi (không đọc một lần lúc mở menu).
- `onMenuRename()`: `overlay = Rename(card)` (chỉ với thẻ `readable`).
- `onRenameConfirm(name: String)`: `name.isBlank()` hay `busy` thì bỏ qua. `busy = true`; `ProjectNames.normalize(name) == card.name` thì không ghi; ngược lại gọi `repository.rename(card.id, name)`. `Success`: `overlay = None`. `Failure`: `overlay = None` và phát `ShowMessage(NO_SPACE)` khi `NO_SPACE`, `ShowMessage(RENAME_FAILED)` với lỗi khác (`WRITE_FAILED`, `NOT_FOUND`, `UNREADABLE`). `busy = false` trong `finally`.
- `onMenuDuplicate()`: `overlay = None`; `duplicatingName != null` thì bỏ qua (không gọi kho lần hai). Đặt `duplicatingName = card.name`, gọi `repository.duplicate(card.id)` trong `viewModelScope`; `Failure(NO_SPACE)` thì `ShowMessage(NO_SPACE)`, lỗi khác `ShowMessage(DUPLICATE_FAILED)`; `Success` không báo gì (bản sao đứng đầu danh sách là dấu hiệu). `duplicatingName = null` trong `finally`.
- `onMenuDelete()`: `overlay = ConfirmDelete(card)` (cả thẻ hỏng).
- `onDeleteConfirm()`: `busy` thì bỏ qua. `busy = true`, `repository.delete(card.id)`; `Success`: `overlay = None`; `Failure`: `overlay = None`, `ShowMessage(DELETE_FAILED)` (`NO_SPACE` không xảy ra khi xóa, vẫn ánh xạ như lỗi khác). `busy = false` trong `finally`.
- `onMenuBackUp()`: `overlay = None`; Route gọi callback `onBackUpProject(id)` khi có, `null` thì "Coming soon" như mẫu của T-009 (T-013 nối).
- `dismissOverlay()`: `overlay = None` (bỏ qua khi `busy`, để hộp thoại không đóng giữa lúc ghi).

Thẻ trong `overlay` là bản chụp lúc mở; danh sách phát lại mà không còn thẻ cùng `id` (bị xóa từ chỗ khác) thì `overlay = None` (một `collect` của `repository.items` trong `init`, chỉ so `id`). Log (thẻ `Projects`, ADR-0019): mở menu, đổi tên xong/lỗi (loại lỗi), nhân bản bắt đầu/xong/lỗi, xóa xong/lỗi; không ghi tên dự án hay mã. Không ghi sự kiện phân tích (spec câu hỏi 17).

**Thời điểm "edited".** `EditedAt.kt`: hàm thuần `editedAt(updatedAtMillis: Long, nowMillis: Long, zone: ZoneId): EditedAt` với `sealed interface EditedAt { Today; Yesterday; data class OnDate(val date: LocalDate, val sameYear: Boolean) }` (cùng ngày lịch theo `zone` là `Today`; hôm trước là `Yesterday`; còn lại `OnDate`; thời điểm sau `now` vì đồng hồ lệch coi là `Today`). Chữ: "edited today", "edited yesterday", "edited %1$s" với ngày định dạng bằng `DateUtils.formatDateTime(context, millis, FORMAT_SHOW_DATE or FORMAT_ABBREV_MONTH or (khác năm thì FORMAT_SHOW_YEAR, cùng năm thì FORMAT_NO_YEAR))` (API có từ API 3, theo ngôn ngữ máy). `java.time` có từ API 26 (= `minSdk`). `ProjectMenuContent` nhận `nowMillis: Long`; `ProjectsRoute` truyền `System.currentTimeMillis()` lấy một lần khi menu mở (`remember(overlay)`), screenshot truyền số cố định. Dòng meta: `plurals` `projects_card_frames` của T-009 nối thêm " · " và chữ "edited ...", tức chuỗi mới `projects_menu_meta` = `%1$s · %2$s`.

**Menu.** `ProjectMenuSheet.kt`:

- `ProjectMenuContent(card, thumbnail: ImageBitmap?, nowMillis, duplicateEnabled, onOpen, onRename, onDuplicate, onBackUp, onDelete)`: cột không trạng thái, số liệu theo `context/ProjectsScreen_menu_*.md`: tay kéo 32 × 4 bo 2 màu `outline` trong dải cao 24; đầu menu cao 56, lề trái/phải 24, lề dưới 8, ảnh 48 × 48 bo 8 nền `canvas` (ảnh thu nhỏ từ `thumbnails` của T-009, `ContentScale.Crop`), cách chữ 12; tên `titleMedium` `onSurface` một dòng cắt "…"; meta `bodySmall` `onSurfaceVariant` một dòng cắt "…". Năm mục `ListItem` cao 56, lề 16, icon 24 cách chữ 16, `colors = ListItemDefaults.colors(containerColor = Color.Transparent)`, chữ `bodyLarge` `onSurface`; "Back up to a file" có `trailingContent` ".frameloop" `bodyMedium` `onSurfaceVariant`; "Delete" icon và chữ `error`. Lề dưới cột 16 cộng `WindowInsets.navigationBars`. Thẻ `readable = false`: chỉ đầu menu (không dòng meta) và mục "Delete". Mỗi mục `Modifier.clickable(role = Role.Button)`, `testTag` `project_menu_open`, `project_menu_rename`, `project_menu_duplicate`, `project_menu_backup`, `project_menu_delete`; cột gốc `project_menu`.
- `ProjectMenuSheet(…)`: `ModalBottomSheet(onDismissRequest, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = surfaceContainerHigh, scrimColor = scrim.copy(alpha = MENU_SCRIM_ALPHA), dragHandle = null) { ProjectMenuContent(…) }` (tay kéo vẽ trong `ProjectMenuContent` cho đúng số liệu và để screenshot vẽ được trong một cửa sổ). `MENU_SCRIM_ALPHA = 0.55f` là hằng của file (độ mờ của thiết kế, như `SCRIM_ALPHA` của `NetworkDialog`). Bấm một mục: ViewModel đặt `overlay` mới; sheet rời cây ngay (không chờ hiệu ứng ẩn), chấp nhận mất hiệu ứng trượt xuống khi chọn mục.
- `@OptIn(ExperimentalMaterial3Api::class)` cho `ModalBottomSheet` (Material 3 1.3.2).

**Hộp thoại.** `ProjectDialogs.kt`:

- `RenameProjectDialog(initialName, busy, onConfirm: (String) -> Unit, onDismiss)`: `AlertDialog(containerColor = surfaceContainerHigh)`, tiêu đề "Rename" (`titleLarge`), `OutlinedTextField` một dòng (`singleLine`, `ImeAction.Done` gọi `onConfirm` khi chữ không trống), giá trị `rememberSaveable(stateSaver = TextFieldValue.Saver)` khởi tạo bằng tên hiện tại, con trỏ ở cuối; mỗi lần đổi cắt ở 40 điểm mã. `confirmButton` "Save" `enabled = text.isNotBlank() && !busy`; `dismissButton` "Cancel". Cả hai `TextButton` truyền `colors = ButtonDefaults.textButtonColors(contentColor = primary)` rõ (nút chữ trên nền khác màu, `CLAUDE.md` bước 10). `testTag`: `rename_dialog`, `rename_field`, `rename_save`, `rename_cancel`.
- `DeleteProjectDialog(name, busy, onConfirm, onDismiss)`: tiêu đề "Delete “%1$s”?" (`titleLarge`, tối đa 2 dòng, cắt "…"), lời "This can't be undone." (`bodyMedium` `onSurfaceVariant`), "Delete" `TextButton` `contentColor = error`, "Cancel" `contentColor = primary`, `enabled = !busy`. `testTag`: `delete_dialog`, `delete_confirm`, `delete_cancel`.

**Màn hình.** `ProjectsScreen.kt` (T-009): `ProjectsScreen` nhận thêm `menuState: ProjectsMenuState`, `nowMillis: Long` và các callback `onProjectLongClick`, `onMenuOpen`, `onMenuRename`, `onMenuDuplicate`, `onMenuBackUp`, `onMenuDelete`, `onRenameConfirm`, `onDeleteConfirm`, `onDismissOverlay` (mặc định rỗng, để test có sẵn của T-009 gọi `ProjectsScreen` không đổi). Theo `menuState.overlay` vẽ `ProjectMenuSheet`, `RenameProjectDialog` hay `DeleteProjectDialog` sau nội dung chính. `duplicatingName != null` thì `LaunchedEffect(duplicatingName)` hiện `snackbarHostState.showSnackbar("Duplicating “…”…", duration = Indefinite)` và hủy khi về `null` (thông báo ngắn chuẩn, không có vòng chờ quay để screenshot không chạy mãi). `ProjectsRoute` thu `menuState`, nhận thêm `onBackUpProject: ((ProjectId) -> Unit)? = null`, hiện `ShowMessage` bằng `SnackbarHost` có sẵn của T-009 (chữ theo `ProjectsMessage`). `ProjectCard.kt` (T-009): `Modifier.clickable` của thẻ đổi thành `combinedClickable(onClick, onLongClick, onLongClickLabel = "Project options")` (nhãn cho trợ năng).

**Điều hướng.** `navigation/ProjectsNavigation.kt` (T-009): `projectsScreen(…)` thêm `onBackUpProject: ((ProjectId) -> Unit)? = null`, chuyển cho `ProjectsRoute`. Giá trị mặc định `null` nên `AppNavHost` không đổi ở T-012.

**Icon.** `Icons.Rounded.Edit` ("Open", thiết kế dùng `edit`) và `Icons.Rounded.Delete` có trong bộ icon lõi đi kèm `material3` 1.3.2 (đã xem ở plan T-009, mục 4.1). `drive_file_rename_outline`, `content_copy`, `save_alt` không có, làm vector drawable như T-009 (`viewportWidth/Height = 960`, `<group android:translateY="960">`, `fillColor` `@android:color/white`), `pathData` từ SVG Material Symbols Rounded (Apache 2.0) tải ngày 2026-11-22 tại `https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/<tên>/default/24px.svg` (HTTP 200), dán sẵn vì phiên không gọi được máy chủ ngoài:

- `drive_file_rename_outline` (`ic_menu_rename.xml`):
  `m400-120 160-160h240q33 0 56.5 23.5T880-200q0 33-23.5 56.5T800-120H400Zm-240-80h57l391-391-57-57-391 391v57Zm-80 40v-97q0-16 6-30.5t17-25.5l505-504q12-11 26.5-17t30.5-6q16 0 31 6t26 18l55 56q12 11 17.5 26t5.5 30q0 16-5.5 30.5T777-647L273-143q-11 11-25.5 17t-30.5 6h-97q-17 0-28.5-11.5T80-160Zm640-544-56-56 56 56Zm-141 85-28-29 57 57-29-28Z`
- `content_copy` (`ic_menu_duplicate.xml`):
  `M360-240q-33 0-56.5-23.5T280-320v-480q0-33 23.5-56.5T360-880h360q33 0 56.5 23.5T800-800v480q0 33-23.5 56.5T720-240H360Zm0-80h360v-480H360v480ZM200-80q-33 0-56.5-23.5T120-160v-520q0-17 11.5-28.5T160-720q17 0 28.5 11.5T200-680v520h400q17 0 28.5 11.5T640-120q0 17-11.5 28.5T600-80H200Zm160-240v-480 480Z`
- `save_alt` (`ic_menu_backup.xml`):
  `M465-339.5q-7-2.5-13-8.5L308-492q-12-12-11.5-28t11.5-28q12-12 28.5-12.5T365-549l75 75v-286q0-17 11.5-28.5T480-800q17 0 28.5 11.5T520-760v286l75-75q12-12 28.5-11.5T652-548q11 12 11.5 28T652-492L508-348q-6 6-13 8.5t-15 2.5q-8 0-15-2.5ZM240-160q-33 0-56.5-23.5T160-240v-80q0-17 11.5-28.5T200-360q17 0 28.5 11.5T240-320v80h480v-80q0-17 11.5-28.5T760-360q17 0 28.5 11.5T800-320v80q0 33-23.5 56.5T720-160H240Z`

**Chữ** (`values/strings.xml` của feature, bản Việt ở `values-vi/`, câu hỏi 5): `projects_menu_open` "Open", `projects_menu_rename` "Rename", `projects_menu_duplicate` "Duplicate", `projects_menu_backup` "Back up to a file", `projects_menu_backup_extension` ".frameloop" (`translatable="false"`), `projects_menu_delete` "Delete", `projects_menu_meta` "%1$s · %2$s" (`translatable="false"`), `projects_menu_edited_today` "edited today", `projects_menu_edited_yesterday` "edited yesterday", `projects_menu_edited_on` "edited %1$s", `projects_card_long_click_label` "Project options", `projects_rename_title` "Rename", `projects_rename_save` "Save", `projects_rename_cancel` "Cancel", `projects_delete_title` "Delete “%1$s”?", `projects_delete_message` "This can't be undone.", `projects_delete_confirm` "Delete", `projects_delete_cancel` "Cancel", `projects_duplicating` "Duplicating “%1$s”…", `projects_message_no_space` "Not enough space on your phone. Free up some space and try again.", `projects_message_rename_failed` "Couldn't rename the project. Try again.", `projects_message_duplicate_failed` "Couldn't duplicate the project. Try again.", `projects_message_delete_failed` "Couldn't delete the project. Try again." (ba chữ lỗi cuối spec chưa có, câu hỏi 2).

### 3.2. Danh sách file

Các tệp ghi "Sửa" dưới `feature/projects/` do plan T-009 tạo; trên nhánh chính hôm nay chưa có (mục 8, câu 1).

| Thao tác | Đường dẫn | Nội dung thay đổi |
| --- | --- | --- |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsUiState.kt` | `ProjectsOverlay`, `ProjectsMenuState`, `ProjectsMessage`, `ProjectsEffect.ShowMessage` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsViewModel.kt` | `menuState` và các hàm của mục 3.1 |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectsScreen.kt` | Tham số mới của `ProjectsScreen`/`ProjectsRoute`, vẽ menu và hộp thoại, thông báo "đang nhân bản" và `ShowMessage`, tag mới trong `ProjectsTestTags` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectCard.kt` | `combinedClickable` có `onLongClick` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectMenuSheet.kt` | `ProjectMenuContent`, `ProjectMenuSheet` |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectDialogs.kt` | `RenameProjectDialog`, `DeleteProjectDialog`, cắt tên ở 40 điểm mã |
| Tạo | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/EditedAt.kt` | `EditedAt`, `editedAt(...)`, hàm chữ `editedLabel(...)` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/ProjectsRepository.kt` | `rename`, `duplicate`, `delete` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultProjectsRepository.kt` | Chuyển ba hàm cho `ProjectRepository` |
| Sửa | `feature/projects/src/main/kotlin/com/vgl/draw/animation/feature/projects/navigation/ProjectsNavigation.kt` | Tham số `onBackUpProject` mặc định `null` |
| Sửa | `feature/projects/src/main/res/values/strings.xml` | Chữ của mục 3.1 |
| Sửa | `feature/projects/src/main/res/values-vi/strings.xml` | Bản tiếng Việt cùng tên (câu hỏi 5) |
| Tạo | `feature/projects/src/main/res/drawable/ic_menu_rename.xml` | Vector `drive_file_rename_outline` |
| Tạo | `feature/projects/src/main/res/drawable/ic_menu_duplicate.xml` | Vector `content_copy` |
| Tạo | `feature/projects/src/main/res/drawable/ic_menu_backup.xml` | Vector `save_alt` |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/FakeProjectsRepository.kt` | Ghi lại lời gọi `rename`/`duplicate`/`delete`, kết quả đặt trước, cổng `CompletableDeferred` để giữ `duplicate`/`rename`/`delete` đang chạy; `delete` và `duplicate` thành công thì đổi danh sách như kho thật |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectMenuViewModelTest.kt` | Unit test mục 4 (phần ViewModel) |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/EditedAtTest.kt` | Unit test của `editedAt` |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectMenuTest.kt` | Test giao diện Robolectric mục 4 (menu, hộp thoại, đóng bằng ba cách, nhấn giữ thẻ) |
| Tạo | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/ProjectMenuScreenshotTest.kt` | Screenshot test mục 4 |
| Sửa | `feature/projects/src/test/kotlin/com/vgl/draw/animation/feature/projects/data/DefaultProjectsRepositoryTest.kt` | Ba test chuyển tiếp (kết quả và mã dự án đi nguyên) |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_menu_light.png` | Ảnh chuẩn mới, cùng tên ảnh thiết kế |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_menu_dark.png` | Ảnh chuẩn mới, cùng tên ảnh thiết kế |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_menu_small.png` | Ảnh chuẩn mới, khung 360 × 640, tên dài |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_menuUnreadable_light.png` | Ảnh chuẩn mới, menu của thẻ hỏng |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_rename_light.png` | Ảnh chuẩn mới, hộp thoại đổi tên |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_rename_dark.png` | Ảnh chuẩn mới |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_delete_light.png` | Ảnh chuẩn mới, hộp thoại xác nhận xóa |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_delete_dark.png` | Ảnh chuẩn mới |
| Tạo | `feature/projects/src/test/screenshots/ProjectsScreen_duplicating_light.png` | Ảnh chuẩn mới, thông báo "đang nhân bản" |

Nơi đọc các file plan sửa: mọi tệp "Sửa" ở trên chỉ có trong `:feature:projects` (T-009 tạo), không script hay workflow nào đọc chúng theo tên (`grep -rn "feature/projects" scripts/ .github/` trên nhánh chính chỉ có thể khớp các mẫu đường dẫn chung `feature/**`). Ảnh chuẩn của T-009 (`ProjectsScreen_list_*`, `ProjectsScreen_empty_*`) không đổi vì menu chỉ vẽ khi `overlay` khác `None` và thẻ không đổi hình (chỉ đổi cách bắt chạm); nếu ảnh nào của T-009 lệch thì đó là lỗi, không ghi lại.

### 3.3. Cờ tính năng, quảng cáo, dữ liệu, quyền, thư viện

- Cờ tính năng: Không thêm. "Open" dùng cờ `EDITOR` mà `ProjectsViewModel` của T-009 đã theo dõi; cả menu nằm sau cờ `PROJECTS` của T-009. Staging bật sẵn `EDITOR` (`StagingFeatureFlagsModule`), nên nhánh cờ tắt kiểm bằng test.
- Quảng cáo: Không chạm. Menu, hộp thoại đổi tên, hộp thoại xóa không có quảng cáo (spec mục 5).
- Sự kiện phân tích: Không chạm (spec câu hỏi 17: không có sự kiện cho đổi tên, nhân bản, xóa).
- File R2: Không chạm.
- **Quyền truy cập:** Không thêm. Đổi tên, nhân bản, xóa chỉ ghi bộ nhớ trong của app qua `ProjectRepository`.
- **Thư viện và SDK:** Không thêm. `ModalBottomSheet`, `rememberModalBottomSheetState`, `AlertDialog`, `ListItem`, `OutlinedTextField` thuộc `androidx-compose-material3` (bản 1.3.2, đã có trên classpath của feature qua plugin `template.android.feature`); `combinedClickable` thuộc `androidx.compose.foundation` (phụ thuộc bắc cầu của material3); `Icons.Rounded.Edit`, `Icons.Rounded.Delete` thuộc bộ icon lõi đi kèm `material3`; `DateUtils` của Android; `java.time` (API 26). Cả bốn lớp Material 3 đã biên dịch và chạy trong test tạm của mục 4.1. `:feature:projects` đã phụ thuộc `:core:project` (T-009).

### 3.4. Phương án dự phòng

- **Điểm chưa chắc:** screenshot của menu và hộp thoại. `ModalBottomSheet` và `AlertDialog` mở cửa sổ riêng (test tạm thấy hai gốc ngữ nghĩa), nên chụp theo khối nội dung của Roborazzi (dạng lambda) chỉ thấy cửa sổ của activity.
- **Phương án chính:** như `NetworkLostScreenScreenshotTest`: test screenshot vẽ trong một cửa sổ `ProjectsScreen` (trạng thái `List`, bốn dự án của bảng chữ thiết kế) cộng một `Box` nền `scrim` độ mờ 0.55 và `ProjectMenuContent` căn đáy trên nền `surfaceContainerHigh`; hộp thoại vẽ bằng nội dung của chúng (`RenameDialogCard`, `DeleteDialogCard`: phần thân không có cửa sổ, `AlertDialog` gọi lại chúng qua tham số `title`/`text`/nút), căn giữa trên nền tối. Cửa sổ thật của sheet và hộp thoại kiểm bằng `ProjectMenuTest`.
- **Điều kiện chuyển:** ảnh do phương án chính vẽ lệch rõ với thiết kế ở phần menu vì nội dung không giống sheet thật (ví dụ thiếu bo góc trên của sheet), sau 2 lần sửa.
- **Cách làm thay:** chụp cả màn bằng `captureScreenRoboImage("src/test/screenshots/<Tên>.png")` của Roborazzi (chụp mọi cửa sổ) trong lớp test riêng có `createAndroidComposeRule<ComponentActivity>()`, dựng `ProjectsScreen` với `menuState` mở sẵn. Tên ảnh, khung và mục 3.2 giữ nguyên; test ở mục 4 giữ nguyên.

### 3.5. Hành vi đã có bị đổi

`scripts/existing-tests.py` chạy trên plan này không thấy test có sẵn nào (mọi tệp main của mục 3.2 chưa có trên nhánh chính; xem mục 4.1). Các hành vi do T-009 thêm mà plan này đổi, với test T-009 sẽ viết (theo mục 4 của plan T-009):

| Hành vi cũ | Hành vi mới (câu của mục 3.1) | Test có sẵn khẳng định hành vi cũ | Giữ, hay câu khẳng định mới |
| --- | --- | --- | --- |
| Thẻ chỉ nhận chạm (`clickable`) | Thẻ nhận chạm và nhấn giữ (`combinedClickable`); chạm vẫn gọi `onProjectClick` | `ProjectsScreenTest` của T-009 (các test chạm thẻ), `ProjectsViewModelTest.clickReadable_*`, `clickUnreadable_emitsCannotOpen` | Giữ: chạm vẫn qua `onClick`; `ProjectMenuTest.cardTap_stillOpens_longPressOpensMenu` thêm đối chứng nhấn giữ không gọi `onProjectClick` |
| `ProjectsScreen` không có `menuState` | Thêm tham số có giá trị mặc định | `ProjectsScreenTest`, `ProjectsScreenScreenshotTest` của T-009 | Giữ: gọi không truyền tham số mới vẫn biên dịch và vẽ như cũ (mặc định `ProjectsMenuState()`); ảnh chuẩn của T-009 không đổi |
| `ProjectsRepository` có `items`, `thumbnail` | Thêm `rename`, `duplicate`, `delete` | `DefaultProjectsRepositoryTest` của T-009 | Giữ; `FakeProjectsRepository` thêm ba hàm (cập nhật dữ liệu giả, ADR-0029) |

## 4. Kiểm thử

Dữ liệu dùng chung: thẻ `walk` (`ProjectCardUi(id = "walk", name = "Walk cycle", frameCount = 24, fps = 12, updatedAtMillis = NOW - 1h, readable = true)`), thẻ hỏng `broken` (`name = "Old test"`, `readable = false`), `NOW` cố định 2026-11-22 15:00 theo `ZoneId.of("UTC")`. Luồng thu trong `backgroundScope` chờ bằng `runCurrent()`, không `advanceUntilIdle()` (bài học của T-002).

| Tiêu chí chấp nhận | Loại test | Tên test hoặc cách kiểm tra |
| --- | --- | --- |
| KB-4, nhấn giữ mở menu | Unit test, Compose UI test | `ProjectMenuViewModelTest.longClick_opensMenuForThatCard` (`overlay == Menu(walk)`); `longClick_whileOverlayOpen_ignored`; `ProjectMenuTest.cardTap_stillOpens_longPressOpensMenu` (`ProjectsScreen` với `List` và trạng thái giữ trong test: `longClick()` trên `project_card_walk` gọi `onProjectLongClick(walk)` một lần, `onProjectClick` 0 lần; `performClick()` gọi `onProjectClick` một lần, `onProjectLongClick` không thêm) |
| KB-4, menu trượt từ đáy, nền tối | Compose UI test | `ProjectMenuTest.menuSheet_isAtBottom` (`@GraphicsMode(NATIVE)`, khung `w411dp-h891dp-xxhdpi`, `ProjectMenuSheet` mở: chờ `waitUntil(3_000)` có `project_menu`; `boundsInWindow.bottom` của `project_menu` bằng chiều cao cửa sổ trừ lề thanh hệ thống, lệch tối đa 1 px; `top` ở nửa dưới màn) |
| KB-4, nội dung menu đúng | Compose UI test | `ProjectMenuTest.menu_showsHeaderAndFiveItems`: có "Walk cycle", "24 frames · 12 fps · edited today", "Open", "Rename", "Duplicate", "Back up to a file", ".frameloop", "Delete" theo đúng thứ tự từ trên xuống (so `top`); `ProjectMenuTest.deleteItem_usesErrorColor` (`@GraphicsMode(NATIVE)`: vẽ `ShadowDialog.getLatestDialog().window.decorView` (cửa sổ của sheet, cách `NetworkDialogHostTest` lấy cửa sổ hộp thoại) ra `Bitmap`, điểm ảnh giữa nét icon "Delete" bằng `colorScheme.error`, điểm ảnh giữa nét icon "Open" bằng `onSurface`, sai số mỗi kênh 8) |
| KB-4, "edited <thời điểm>" | Unit test | `EditedAtTest`: cùng ngày 00:00 và 23:59 → `Today`; hôm trước 23:59 → `Yesterday`; hai ngày trước → `OnDate(sameYear = true)`; năm trước → `OnDate(sameYear = false)`; `updatedAt` sau `now` 5 phút → `Today`; ranh giới ngày theo `zone` (cùng thời điểm, `UTC` là `Today`, `Asia/Ho_Chi_Minh` là `Yesterday` khi `now` = 2026-11-22 00:30 giờ Việt Nam và `updatedAt` = 23:30 hôm trước giờ Việt Nam) |
| KB-4, "Open" mở màn vẽ | Unit test | `ProjectMenuViewModelTest.menuOpen_editorOn_emitsOpenProjectAndCloses`; `menuOpen_editorOff_emitsComingSoon` (spec KB-2: cờ màn vẽ tắt, lối "Open"); `menuOpen_editorTurnsOffWhileMenuOpen_emitsComingSoon` (cờ bật lúc mở menu, tắt trước khi bấm: kiểm ngay lúc bấm) |
| KB-4, chạm ngoài, kéo xuống, quay lại thì đóng | Compose UI test | `ProjectMenuTest.back_closesMenu` (`Espresso.pressBack()`), `tapOutside_closesMenu` (chạm gốc ngữ nghĩa của cửa sổ sheet ở `(centerX, 20 px)`, vùng nền tối), `swipeDown_closesMenu` (`swipeDown(startY = top, endY = top + 2000f)` trên `project_menu_open`): mỗi test `onDismissOverlay` gọi đúng 1 lần, sau đó không còn node `project_menu`, `onRename`/`onDelete`/`onOpen` 0 lần. Cả ba cách đã chạy thử với `ModalBottomSheet` (mục 4.1) |
| KB-4, đóng thì danh sách giữ nguyên | Unit test | `ProjectMenuViewModelTest.dismiss_closesWithoutRepositoryCalls`: sau `dismissOverlay()` `overlay == None`, kho giả 0 lời gọi ghi, `uiState` vẫn là cùng danh sách |
| KB-4 (đặc biệt), không quảng cáo trong menu | Unit test (quét mã nguồn theo import) | `ProjectMenuTest.menuFiles_importNothingFromCoreAds`: đọc `src/main/kotlin/com/vgl/draw/animation/feature/projects/ProjectMenuSheet.kt` và `ProjectDialogs.kt` (đường dẫn tương đối từ thư mục module, như các test quét tệp có sẵn), không dòng `import` nào bắt đầu bằng `com.vgl.draw.animation.core.ads`; đối chứng trong cùng test: hàm quét trả có khi đưa một chuỗi mẫu `import com.vgl.draw.animation.core.ads.AdSlot`. Quét theo import, không theo chuỗi con, nên chú thích nhắc quảng cáo không làm test đỏ |
| KB-5, mở hộp đổi tên | Unit test, Compose UI test | `ProjectMenuViewModelTest.menuRename_opensRenameDialog`; `ProjectMenuTest.renameDialog_showsTitleButtonsAndCurrentName_focused`: có "Rename", "Save", "Cancel"; `rename_field` có chữ "Walk cycle" và `assertIsFocused()` (đã chạy thử, mục 4.1) |
| KB-5, xác nhận đổi tên | Unit test | `ProjectMenuViewModelTest.renameConfirm_callsRepositoryAndCloses` (kho giả nhận `("walk", "Walk loop")`, `overlay == None`); `renameConfirm_listShowsNewName` (kho giả đổi danh sách khi `rename` thành công; thẻ đầu tiên của `uiState` có tên "Walk loop"); `renameConfirm_sameName_noWrite` (đối chứng: " Walk cycle " → 0 lời gọi `rename`, `overlay == None`; "Walk cycle 2" → 1 lời gọi) |
| KB-5, mở lại app giữ tên | Unit test (có sẵn) | `DefaultProjectRepositoryTest` của T-002 (đổi tên rồi đọc lại từ kho mới cùng thư mục); T-012 thêm `DefaultProjectsRepositoryTest.rename_delegatesToProjectRepository` (mã và tên đi nguyên, `Success` trả nguyên) |
| KB-5, hủy | Unit test, Compose UI test | `ProjectMenuViewModelTest.renameCancel_noWrite`; `ProjectMenuTest.renameCancel_callsDismiss` |
| KB-5 (đặc biệt), trống hay khoảng trắng thì tắt nút | Compose UI test, Unit test | `ProjectMenuTest.renameSave_disabledWhenBlank` (xóa hết → `rename_save` `assertIsNotEnabled()`; "   " → vẫn tắt; "a" → `assertIsEnabled()`); `ProjectMenuViewModelTest.renameConfirm_blank_ignored` (0 lời gọi, không ném) |
| KB-5 (đặc biệt), trùng tên vẫn lưu | Unit test | `ProjectMenuViewModelTest.renameConfirm_duplicateName_stillSaves` (danh sách có "Bouncing ball"; đổi "Walk cycle" thành "Bouncing ball" → 1 lời gọi `rename`) |
| Tên tối đa 40 ký tự | Compose UI test | `ProjectMenuTest.renameField_cutsAt40CodePoints`: `performTextReplacement` 45 chữ "a" → ô còn 40; 39 chữ "a" cộng hai emoji "🎬🎬" → ô còn 39 "a" cộng một emoji (không cắt đôi cặp thay thế) |
| KB-5, lỗi ghi | Unit test | `ProjectMenuViewModelTest.renameFailure_closesAndEmitsMessage` (bảng: `WRITE_FAILED`, `NOT_FOUND`, `UNREADABLE` → `RENAME_FAILED`; `NO_SPACE` → `NO_SPACE`) |
| KB-5, Save hai lần nhanh | Unit test | `ProjectMenuViewModelTest.renameConfirm_twiceWhileWriting_writesOnce` (cổng giữ `rename` đang chạy; gọi `onRenameConfirm` hai lần; mở cổng; 1 lời gọi) |
| KB-6, nhân bản | Unit test | `ProjectMenuViewModelTest.menuDuplicate_callsRepositoryAndCloses`; `duplicateSuccess_copyFirstInList` (kho giả thêm "Walk cycle copy" ở đầu; `uiState` có thẻ đó đầu tiên); đủ khung, layer, tên "copy", "copy 2", cắt 40 ký tự, gốc không đổi: test có sẵn của T-002 (`DefaultProjectRepositoryTest.duplicate_*`), T-012 thêm `DefaultProjectsRepositoryTest.duplicate_delegatesToProjectRepository` |
| KB-6, bản sao và gốc tách nhau | Unit test (có sẵn) | `DefaultProjectRepositoryTest.duplicate_*` của T-002 (tệp ô chép sang mã mới); T-012 không đổi kho |
| KB-6 (đặc biệt), hết chỗ | Unit test | `ProjectMenuViewModelTest.duplicateNoSpace_emitsNoSpaceMessage`; `duplicateWriteFailed_emitsDuplicateFailed`; không để bản dở: test có sẵn của T-002 (`duplicate_noSpace_leavesNothing`) |
| KB-6 (đặc biệt), dấu hiệu đang làm, không bấm lại được | Unit test, Compose UI test, Screenshot test | `ProjectMenuViewModelTest.duplicate_inProgress_setsDuplicatingAndBlocksSecond` (cổng giữ `duplicate`: `duplicatingName == "Walk cycle"`; mở menu lần nữa, `onMenuDuplicate()` → kho vẫn 1 lời gọi; mở cổng → `duplicatingName == null`); `duplicate_failure_clearsDuplicating`; `ProjectMenuTest.duplicating_showsSnackbarAndDisablesItem` (`menuState.duplicatingName = "Walk cycle"`: có chữ "Duplicating “Walk cycle”…"; menu mở: `project_menu_duplicate` `assertIsNotEnabled()`; đối chứng `duplicatingName = null`: `assertIsEnabled()` và không có chữ "Duplicating"); ảnh `ProjectsScreen_duplicating_light.png` |
| KB-7, hộp xác nhận | Unit test, Compose UI test | `ProjectMenuViewModelTest.menuDelete_opensConfirm`; `ProjectMenuTest.deleteDialog_showsTexts`: "Delete “Walk cycle”?", "This can't be undone.", "Delete", "Cancel"; `deleteDialog_confirmUsesErrorColor` (`@GraphicsMode(NATIVE)`, vẽ `ShadowDialog.getLatestDialog().window.decorView` ra `Bitmap`, điểm ảnh giữa nét chữ "D" của nút `delete_confirm` gần `error` hơn `primary`, và nút `delete_cancel` gần `primary` hơn `error`) |
| KB-7, xác nhận xóa | Unit test | `ProjectMenuViewModelTest.deleteConfirm_callsRepositoryAndCardGone` (kho giả bỏ thẻ khỏi danh sách; `uiState` không còn `walk`; `overlay == None`); xóa khỏi máy, mở lại không thấy: test có sẵn của T-002 (`delete_*`); `DefaultProjectsRepositoryTest.delete_delegatesToProjectRepository` |
| KB-7, hủy | Unit test, Compose UI test | `ProjectMenuViewModelTest.deleteCancel_noWrite`; `ProjectMenuTest.deleteCancel_callsDismiss` |
| KB-7, xóa dự án cuối về trống | Unit test | `ProjectMenuViewModelTest.deleteLastProject_givesEmpty` (kho giả một dự án; mở menu, xóa, xác nhận; `uiState is ProjectsUiState.Empty`) |
| KB-7, lỗi xóa, bấm hai lần | Unit test | `deleteFailure_closesAndEmitsDeleteFailed`; `deleteConfirm_twiceWhileWriting_deletesOnce`; `dismiss_whileBusy_ignored` (cổng giữ `delete`, `dismissOverlay()` → `overlay` vẫn `ConfirmDelete`) |
| KB-2 (đặc biệt), dự án hỏng chỉ còn "Delete" | Compose UI test, Unit test, Screenshot test | `ProjectMenuTest.unreadableMenu_onlyDelete` (có "Old test" và "Delete"; không có "Open", "Rename", "Duplicate", "Back up to a file", không có chữ "frames"); `ProjectMenuViewModelTest.unreadable_deleteConfirmed_deletes` (xóa được thẻ hỏng sau xác nhận) và `unreadable_neverDeletedWithoutConfirm` (mở menu, đóng: 0 lời gọi `delete`); ảnh `ProjectsScreen_menuUnreadable_light.png` |
| Thẻ trong menu bị xóa từ nơi khác | Unit test | `ProjectMenuViewModelTest.cardRemovedFromList_closesOverlay` (menu của `walk` mở; kho giả phát danh sách không có `walk`; `runCurrent()`; `overlay == None`); đối chứng: phát danh sách vẫn có `walk` với `updatedAtMillis` khác → menu vẫn mở |
| "Back up to a file" trước T-013 | Compose UI test | `ProjectMenuTest.backUp_withoutDestination_showsComingSoon` (`ProjectsRoute` với `onBackUpProject = null` và ViewModel dựng tay với kho giả như `ProjectsRouteTest` của T-009: bấm `project_menu_backup` → menu đóng, snackbar "Coming soon"); `backUp_withDestination_callsWithId` (callback nhận `walk`) |
| KB-12, giữ menu, hộp thoại, chữ đang gõ | Unit test, Compose UI test | Menu và hộp thoại nằm trong ViewModel nên sống qua đổi cấu hình: `ProjectMenuViewModelTest.overlay_isViewModelState` (không phụ thuộc `uiState` phát lại: phát lại danh sách có `walk` thì `overlay` giữ). `ProjectMenuTest.renameText_survivesRestore`: `StateRestorationTester`, gõ "Walk lo", `emulateSavedInstanceStateRestore()`, `rename_field` vẫn "Walk lo"; đối chứng trước khi gõ là "Walk cycle" |
| Giao diện menu đúng thiết kế | Screenshot test | `ProjectsScreen_menu_light.png`, `ProjectsScreen_menu_dark.png`: `@Config(qualifiers = "en-w411dp-h891dp-xxhdpi")`, bốn dự án của bảng chữ thiết kế (`Bouncing ball` 48/12, `Walk cycle` 24/12, `Logo sting` 90/24, `Blink test` 16/8), menu của "Walk cycle", `nowMillis` cùng ngày (ra "edited today"), ảnh thu nhỏ giả màu đặc; vẽ trong một cửa sổ (mục 3.4) |
| Màn hình thấp | Screenshot test khung `w360dp-h640dp-xxhdpi`, trạng thái nhiều nội dung nhất | `ProjectsScreen_menu_small.png`: menu đủ năm mục của thẻ tên dài 40 ký tự; `ProjectMenuTest.small_menuFits` (`@Config(qualifiers = "w360dp-h640dp-xxhdpi")`, `@GraphicsMode(NATIVE)`: `project_menu_delete` nằm trọn trong cửa sổ, `top` của `project_menu` ≥ 0; tên trong đầu menu `lineCount == 1` và `isLineEllipsized(0)` qua `GetTextLayoutResult`) |
| Hộp thoại | Screenshot test | `ProjectsScreen_rename_light.png`, `ProjectsScreen_rename_dark.png`, `ProjectsScreen_delete_light.png`, `ProjectsScreen_delete_dark.png` (khung `w411dp-h891dp-xxhdpi`, chữ tiếng Anh) |
| Đúng thiết kế (khi có `design/screens/`) | Screenshot test cùng tên với từng ảnh thiết kế, ảnh so sánh `scripts/design-compare.sh projects`, rà soát độc lập theo mục 7 của mẫu thiết kế. Ảnh của T-012 không có thanh điều hướng (ở `:app`, T-009) và quảng cáo thanh đáy (T-010): phần đó lệch so với thiết kế như ảnh của T-009 (plan T-009, câu hỏi 1) | `ProjectsScreen_menu_light`, `ProjectsScreen_menu_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-012 | Ảnh thiết kế của ticket khác (T-009): `ProjectsScreen_empty_light`, `ProjectsScreen_empty_dark`, `ProjectsScreen_list_light`, `ProjectsScreen_list_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-012 | Ảnh thiết kế của ticket khác (T-011): `NewProjectScreen_default_light`, `NewProjectScreen_default_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-012 | Ảnh thiết kế của ticket khác (T-013): `ProjectsScreen_restoring_light`, `ProjectsScreen_restoring_dark`, `ProjectsScreen_restore-error_light`, `ProjectsScreen_restore-error_dark` |
| Ảnh thiết kế của ticket khác | Không thuộc T-012 | Ảnh thiết kế của ticket khác (T-014): `ProjectsScreen_list_light_unfolded`, `ProjectsScreen_list_dark_unfolded` |
| Chữ đúng bảng chữ của thiết kế | Compose UI test | `ProjectMenuTest.menu_showsHeaderAndFiveItems` (chữ nguyên văn của bảng chữ, mục `ProjectsScreen, menu`); hộp thoại theo chữ của spec câu hỏi 2 trong `renameDialog_*`, `deleteDialog_showsTexts` |
| Bản tiếng Việt | Compose UI test | `ProjectMenuTest.vietnamese_menuMetaKeepsNumbers` (`@Config(qualifiers = "vi")`): dòng meta có "24" và "12" đúng chỗ khung và tốc độ; tiêu đề hộp xóa chứa tên dự án |
| Nút chữ trong hộp thoại đúng màu | Compose UI test | `deleteDialog_confirmUsesErrorColor` (trên), `ProjectMenuTest.renameDialog_buttonsUsePrimary` (cách đo như trên: chữ "Save" khi bật gần `primary`) |
| Bàn phím mềm hiện khi mở hộp đổi tên; nhấn giữ thẻ trên máy | Kiểm tra thủ công (máy thật: khác, Robolectric không có bàn phím mềm; emulator chưa có dự án để nhấn giữ vì màn Tạo dự án là T-011 và cú pháp kịch bản không có nhấn giữ) | Sau khi có T-011 trên staging: tạo dự án, nhấn giữ thẻ, menu trượt lên; "Rename" → bàn phím hiện, ô có tên hiện tại; đổi tên, đóng và mở lại app thấy tên mới; "Duplicate" một dự án nhiều khung thấy thông báo "Duplicating…"; "Delete" → hộp xác nhận, xóa dự án cuối thấy màn trống |

**Ngoại lệ viết test trước:** ba vector drawable, `strings.xml` (tài nguyên; chữ được kiểm qua các test chữ ở trên), tham số `onBackUpProject` của `ProjectsNavigation.kt` (một dòng nối, kiểm gián tiếp qua `ProjectsRoute`).

Thứ tự commit: bước 3 commit các tệp test, phần thêm của `FakeProjectsRepository.kt` và khung tối thiểu để biên dịch (các lớp trạng thái mới, chữ ký ba hàm của `ProjectsRepository` và `DefaultProjectsRepository` với `TODO()`, chữ ký `ProjectMenuContent`/`ProjectMenuSheet`/hai hộp thoại/`editedAt` trả rỗng, tham số mới của `ProjectsScreen`/`ProjectsRoute`, các hàm rỗng của ViewModel); bước 4 commit phần code. Ảnh chuẩn ghi ở bước 4 bằng `./gradlew :feature:projects:recordRoborazziDebug :feature:projects:testDebugUnitTest --rerun`, rồi `git status --short -- '*/src/test/screenshots/*'` chỉ được thấy các ảnh của mục 3.2; ảnh khác (của T-009) đổi thì trả bằng `git checkout -- <ảnh>`, và ảnh của T-009 lệch thật thì đó là lỗi của thay đổi, sửa code.

### 4.1. Chạy thử

- **Đã chạy thử:** ngày 2026-11-22, worktree tạm từ `origin/main` (`b5a33cf`), test tạm không commit `feature/sample/src/test/.../TrialMenuTest.kt` (`:feature:sample` vì `:feature:projects` chưa có trên nhánh chính), cùng classpath Material 3 1.3.2 mà feature nào cũng có: (1) `combinedClickable` nhận `longClick()` của `performTouchInput`: `onLongClick` 1 lần, `onClick` 0 lần; (2) `ModalBottomSheet` (`skipPartiallyExpanded = true`) mở là cửa sổ riêng (cây có 2 gốc ngữ nghĩa), node "Open" trong sheet tìm được bằng `onNodeWithText`; `Espresso.pressBack()` gọi `onDismissRequest` 1 lần và node biến mất; chạm gốc của cửa sổ sheet ở `(centerX, 20 px)` gọi `onDismissRequest` 1 lần; `swipeDown(startY = top, endY = top + 2000f)` trên mục của sheet gọi `onDismissRequest` 1 lần; (3) `AlertDialog` có `OutlinedTextField` với `FocusRequester.requestFocus()` trong `LaunchedEffect`: `assertIsFocused()` qua, `onNode(isDialog())` có node. Kết quả: 4 test, 0 lỗi.
- **Lệnh:** `./gradlew :feature:sample:testDebugUnitTest --tests '*TrialMenuTest*'` (qua, 4/4). `scripts/check.sh` trên cùng worktree có test tạm: CHECK_RESULT_PLACEHOLDER. Trên nhánh có code liên quan (`:feature:projects` của T-009 cộng code của T-012): Chạy thử: chưa chạy xong (module `:feature:projects` chưa có trên nhánh chính vì plan T-009 chưa merge; câu hỏi 6).
- **Kết quả:** ba điểm mới của kỹ thuật (nhấn giữ, sheet đóng bằng ba cách, ô nhập có tiêu điểm trong hộp thoại) chạy được trên Robolectric của dự án. Chưa thử: screenshot của sheet và hộp thoại (phương án chính và dự phòng ở mục 3.4), đo màu điểm ảnh trên cửa sổ của sheet hay hộp thoại.

## 5. Chấm rủi ro

**Điểm chặn bị chạm:** Không có. Không sửa module core (chỉ gọi `ProjectRepository` có sẵn), không quảng cáo, không sự kiện, không quyền, không thư viện, không cấu hình build.

| Tiêu chí | Điểm (0 đến 3) | Lý do |
| --- | --- | --- |
| A. Phạm vi thay đổi | 2 | Nhiều phần trong một module feature (ViewModel, màn, thẻ, repository, điều hướng), đổi giao diện công khai của `ProjectsRepository`, `ProjectsScreen`, `ProjectsRoute`, `projectsScreen`; không chạm module khác |
| B. Độ rõ của yêu cầu | 1 | Spec đã xác nhận, có thiết kế menu và chữ hộp thoại (câu hỏi 2); AI tự quyết chữ lỗi đổi tên, nhân bản, xóa, cách ghi "edited <thời điểm>", mục "Back up to a file" trước T-013 (mục 8) |
| C. Khả năng kiểm chứng tự động | 1 | Mọi tiêu chí có test JVM; bàn phím mềm và cảm giác nhấn giữ kiểm bằng mắt trên máy thật |
| D. Ảnh hưởng chéo | 2 | Sửa các tệp T-009 tạo; T-010, T-013, T-014 cũng sửa `ProjectsScreen.kt`, `ProjectsViewModel.kt` |
| E. Độ mới của kỹ thuật | 2 | `ModalBottomSheet`, `AlertDialog`, `combinedClickable` lần đầu dùng trong dự án (phổ biến, đã chạy thử trên Robolectric, mục 4.1); screenshot của cửa sổ riêng có dự phòng ở mục 3.4 |
| **Tổng** | 8 | **Mức: Trung bình** |

Ticket ghi rủi ro sơ bộ `low`; chấm theo thang được 8 (Trung bình) vì kỹ thuật mới và vì xây trên tệp của một plan chưa merge.

## 6. Ảnh hưởng chéo

- File hoặc thành phần dùng chung bị chạm: chỉ các tệp của `:feature:projects`. Không chạm `:app` (`AppNavHost` không đổi nhờ tham số mặc định `null`), không chạm `:core:*`.
- **Phụ thuộc plan T-009 phiên bản 1 (PR nháp #26, chưa merge).** Mọi tệp "Sửa" của mục 3.2 do T-009 tạo; tên lớp, tham số, tag và cách báo "Coming soon" lấy từ plan T-009 bản đọc ngày 2026-11-22. T-009 đổi (sửa sau duyệt, hay phiên chạy lệch tên khi làm) thì plan T-012 phải xem lại mục 3.1, 3.2, 3.5 và 4 trước khi duyệt hay trước khi chạy; `plan-check.py` lúc bắt đầu task sẽ báo tệp "Sửa" chưa có nếu T-009 chưa merge.
- Task khác có thể xung đột:
  - T-009: chạy trước, bắt buộc (`depends_on`).
  - T-010 (quảng cáo ở danh sách): sửa `ProjectsScreen.kt` (bọc `InContentAds`, thanh đáy, ô lưới). Lớp nổi của quảng cáo (thanh đáy thu gọn) nằm ở cửa sổ activity nên ở dưới menu; T-010 không được thêm quảng cáo vào `ProjectMenuContent`. Xếp tuần tự với T-012, thứ tự nào cũng được.
  - T-013 (sao lưu, khôi phục): dùng mục "Back up to a file" và callback `onBackUpProject` của plan này; T-013 phụ thuộc T-012 nên chạy sau, nối callback trong `AppNavHost` hay trong feature.
  - T-014 (máy gập): menu và hộp thoại như điện thoại (spec câu hỏi 21); sửa `ProjectsScreen.kt`, xếp tuần tự.
  - T-018 (màn vẽ): "Open" dùng đúng đường của chạm thẻ, không thêm gì.
- Đề xuất thứ tự chạy: sau T-009; trước T-013. Cùng danh sách với T-010, T-014 thì tuần tự (`run-check.py` báo cùng sửa tệp).

## 7. Điều kiện dừng riêng

- Cần sửa `ProjectRepository`, `ProjectNames` hay tệp khác của `:core:project` để đổi tên, nhân bản, xóa chạy đúng: dừng (điểm chặn 7 mà plan chưa nêu; T-002 đã có đủ hàm).
- Tên lớp, tham số hay tag của T-009 trên nhánh chính khác plan T-009 bản mà plan này dựa vào đến mức phải đổi câu khẳng định của mục 4: dừng, để người xem lại plan.

## 8. Câu hỏi cho người duyệt

1. **Plan dựa vào plan T-009 chưa merge.** Plan T-012 phiên bản 1 dựa vào plan T-009 phiên bản 1 (PR nháp #26). Duyệt T-012 trước khi T-009 merge thì `plan-check.py` báo lỗi các tệp "Sửa" chưa có (lỗi chỉ hết khi T-009 merge; người không gỡ được bằng cách sửa plan). Đề xuất: duyệt T-012 sau khi T-009 merge, hay duyệt cùng đợt nhưng xếp T-012 sau T-009 trong cùng danh sách chạy; T-009 đổi tên hay cấu trúc thì sửa T-012 thành phiên bản 2. Kết quả `plan-check.py` hôm nay: 11 lỗi "plan sửa hoặc xóa, nhưng file chưa có trên nhánh chính", đúng 11 tệp "Sửa" của mục 3.2 do T-009 tạo; không lỗi nào khác.
2. **Chữ lỗi chưa có trong spec.** Spec câu hỏi 2 chỉ có chữ cho hộp thoại và sao lưu. Đề xuất: hết chỗ khi nhân bản (và khi đổi tên) dùng chữ của câu hỏi 12 "Not enough space on your phone. Free up some space and try again."; lỗi khác "Couldn't rename the project. Try again.", "Couldn't duplicate the project. Try again.", "Couldn't delete the project. Try again." (thông báo ngắn tự ẩn, không nút "Retry"). Nhân bản xong không báo gì, vì bản sao hiện ở đầu danh sách. Chữ dấu hiệu đang làm: "Duplicating “<tên>”…" (thông báo ngắn không tự ẩn, tắt khi xong).
3. **Cách ghi "edited <thời điểm>".** Thiết kế chỉ có "edited today". Đề xuất: "edited today", "edited yesterday", còn lại ngày ngắn theo ngôn ngữ máy ("edited Nov 3"; khác năm thì có năm, "edited Nov 3, 2025"), tính theo múi giờ của máy.
4. **Mục "Back up to a file" trước T-013.** Ticket loại trừ sao lưu. Đề xuất: mục hiện đúng thiết kế, bấm thì đóng menu và hiện "Coming soon" tới khi T-013 nối callback (cách T-009 làm với các đích chưa có). Phương án khác: ẩn mục tới T-013 (ảnh `ProjectsScreen_menu_*` lệch thiết kế).
5. **Bản tiếng Việt** của chữ mới, theo câu hỏi 4 của plan T-009 (đề xuất có `values-vi/`): "Mở", "Đổi tên", "Nhân bản", "Sao lưu ra file", "Xóa", "Lưu", "Hủy", "Xóa “%1$s”?", "Không hoàn tác được.", "Đang nhân bản “%1$s”…", "sửa hôm nay", "sửa hôm qua", "sửa %1$s". T-009 chọn chỉ tiếng Anh thì T-012 cũng bỏ `values-vi/` và test tiếng Việt.
6. **Chạy thử chưa xong trên nhánh có code liên quan.** Ba kỹ thuật mới đã chạy thử trên `:feature:sample` (mục 4.1); `scripts/check.sh` trên nhánh có `:feature:projects` chưa chạy được vì module chưa có. Đề xuất: người lập danh sách chạy chạy lại phần chạy thử sau khi T-009 merge (dán `TrialMenuTest` vào `:feature:projects`, `scripts/check.sh`), hay chấp nhận kết quả trên `:feature:sample` vì cùng classpath Material 3.
7. **Screenshot của menu và hộp thoại vẽ trong một cửa sổ** (mục 3.4), như `NetworkLostScreenScreenshotTest`: ảnh không phải cửa sổ thật của `ModalBottomSheet`/`AlertDialog` (cửa sổ thật kiểm bằng test giao diện). Anh đồng ý, hay muốn dùng ngay phương án dự phòng `captureScreenRoboImage` (chưa có tiền lệ trong dự án)?
8. **Đổi tên không đổi chỗ trong danh sách.** Kho giữ `updatedAtMillis` khi đổi tên (T-002, câu hỏi 2 của plan T-002), nên thẻ đổi tên không lên đầu. Spec KB-2 chỉ nói "sửa gần nhất đứng đầu". Đề xuất: giữ như T-002.
9. Plan chưa qua rà soát độc lập (phiên con soạn plan chỉ làm bước 1 tới 5 của `/write-plan`; phiên chính giao rà soát sau).

## 9. Quyết định duyệt

## 10. Nhật ký thực hiện
