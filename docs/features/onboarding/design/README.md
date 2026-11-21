---
feature: onboarding
status: approved                # draft | approved
spec: features/onboarding/spec.md
tool: figma
link: https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O
designer: AI (Claude), anh Nhật duyệt
approved_by: Nhật
approved_at: 2026-11-20
---

# THIẾT KẾ: ONBOARDING

<!--
Quy trình: docs/ops/design-workflow.md. Quyết định: ADR-0008, ADR-0020, ADR-0021.
screens/ và context/ do scripts/figma-export.py sinh, không sửa tay. Thiết kế đổi thì chạy lại:
  scripts/figma-export.py "https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O/Frameloop?node-id=26-4695" onboarding
Màu và chữ trong context/ phần lớn ghi "ngoài hệ thống" vì :core:designsystem chưa đổi sang
thương hiệu Studio Night; plan đổi thương hiệu (docs/ops/new-app.md, mục 3) đưa các giá trị đó vào tokens.json.
Phiên tự chạy chỉ đọc thư mục này, không mở được Figma.
-->

## 1. Các màn hình và trạng thái

Kịch bản: theo [spec của feature](../spec.md).

Bổ sung ngày 2026-11-21 theo câu trả lời ở mục 9 của spec, anh Nhật duyệt ngày 2026-11-21 (chọn "Duyệt" trong phiên): `page-1-no-ad` (không có thẻ quảng cáo; nội dung trang căn giữa phần còn lại, nút vẫn ghim đáy). Các trang 2 đến 4 không có quảng cáo theo cùng cách.

| Màn hình (composable) | Trạng thái | Ảnh trong `screens/`, số liệu trong `context/` | Kịch bản |
| --- | --- | --- | --- |
| `OnboardingScreen` | page-1-no-ad, chế độ tối | `OnboardingScreen_page-1-no-ad_dark` | KB-4 |
| `OnboardingScreen` | page-1-no-ad | `OnboardingScreen_page-1-no-ad_light` | KB-4 |
| `OnboardingScreen` | page-1, chế độ tối | `OnboardingScreen_page-1_dark` | KB-1, KB-2, KB-3, KB-4, KB-6 |
| `OnboardingScreen` | page-1 | `OnboardingScreen_page-1_light` | KB-1, KB-2, KB-3, KB-4, KB-6 |
| `OnboardingScreen` | page-2, chế độ tối | `OnboardingScreen_page-2_dark` | KB-1, KB-2, KB-4, KB-6 |
| `OnboardingScreen` | page-2 | `OnboardingScreen_page-2_light` | KB-1, KB-2, KB-4, KB-6 |
| `OnboardingScreen` | page-3, chế độ tối | `OnboardingScreen_page-3_dark` | KB-1, KB-2, KB-4, KB-6 |
| `OnboardingScreen` | page-3 | `OnboardingScreen_page-3_light` | KB-1, KB-2, KB-4, KB-6 |
| `OnboardingScreen` | page-4, chế độ tối | `OnboardingScreen_page-4_dark` | KB-1, KB-2, KB-3, KB-4, KB-6 |
| `OnboardingScreen` | page-4 | `OnboardingScreen_page-4_light` | KB-1, KB-2, KB-3, KB-4, KB-6 |

## 2. Bảng chữ

Chữ tiếng Anh như trên thiết kế (bản đề xuất Q9: tiếng Anh). Lấy từ frame chế độ tối; frame sáng cùng chữ. Chữ trong ô quảng cáo là chữ giữ chỗ, không vào `strings.xml`.

| Màn hình, trạng thái | Vị trí (phần tử cha trong `context/`) | Chữ | Kiểu chữ (text style trên Figma) |
| --- | --- | --- | --- |
| `OnboardingScreen`, page-1-no-ad, chế độ tối | TopBar | 1 / 4 | labelLarge |
| `OnboardingScreen`, page-1-no-ad, chế độ tối | Page (pager) | Draw frame by frame | headlineSmall |
| `OnboardingScreen`, page-1-no-ad, chế độ tối | Page (pager) | Sketch each moment, then flip through the frames to see it move. | bodyLarge |
| `OnboardingScreen`, page-1-no-ad, chế độ tối | Button | Next | labelLarge |
| `OnboardingScreen`, page-1, chế độ tối | TopBar | 1 / 4 | labelLarge |
| `OnboardingScreen`, page-1, chế độ tối | Page (pager) | Draw frame by frame | headlineSmall |
| `OnboardingScreen`, page-1, chế độ tối | Page (pager) | Sketch each moment, then flip through the frames to see it move. | bodyLarge |
| `OnboardingScreen`, page-1, chế độ tối | Button | Next | labelLarge |
| `OnboardingScreen`, page-2, chế độ tối | TopBar | 2 / 4 | labelLarge |
| `OnboardingScreen`, page-2, chế độ tối | Page (pager) | See the frame before and after | headlineSmall |
| `OnboardingScreen`, page-2, chế độ tối | Page (pager) | Onion skin shows your last frames in red and the next ones in blue, so motion stays smooth. | bodyLarge |
| `OnboardingScreen`, page-2, chế độ tối | Button | Next | labelLarge |
| `OnboardingScreen`, page-3, chế độ tối | TopBar | 3 / 4 | labelLarge |
| `OnboardingScreen`, page-3, chế độ tối | Page (pager) | Add sound, share a video | headlineSmall |
| `OnboardingScreen`, page-3, chế độ tối | Page (pager) | Import your music, record your voice, and export MP4 or GIF in a few taps. | bodyLarge |
| `OnboardingScreen`, page-3, chế độ tối | Button | Next | labelLarge |
| `OnboardingScreen`, page-4, chế độ tối | TopBar | 4 / 4 | labelLarge |
| `OnboardingScreen`, page-4, chế độ tối | Page (pager) | Never lose your work | headlineSmall |
| `OnboardingScreen`, page-4, chế độ tối | Page (pager) | Frameloop saves as you draw and backs up a whole project to one file. | bodyLarge |
| `OnboardingScreen`, page-4, chế độ tối | Button | Start drawing | labelLarge |

## 3. Ánh xạ sang hệ thống thiết kế

Vai trò màu theo `tokens.json` (tên vai trò Material 3); giá trị Studio Night nằm trong biến Figma collection "Frameloop" (trang Design system).

| Phần tử trên thiết kế (component Figma) | Thành phần trong code | Màu (vai trò) | Kiểu chữ | Khoảng cách |
| --- | --- | --- | --- | --- |
| Button | `PrimaryButton` (Style=Primary); `FilledTonalButton`, `OutlinedButton`, `TextButton` của Material 3 (Style=Tonal, Outlined, Text), truyền `colors` rõ | `primary`/`onPrimary`, `surfaceContainerHigh`/`onSurface`, `outline`, chữ `primary` | `labelLarge` | theo `context/` |
| Icon | `Icon(Icons.Rounded.<Tên>)` (material-icons-extended, bộ Rounded); tên ligature trong thiết kế là tên Material Symbols, đổi sang tên `Icons.Rounded` tương ứng | theo phần tử cha | - | theo `context/` |
| NativeAdMedium (trong khung `native_onboarding`) | `NativeStepAd(placement, step)` của `:core:ads`, khung cao `NativeStepAdDefaults.height()` | do `:core:ads` vẽ | - | cách nút Next ít nhất 24 dp |
| StatusBar | Thanh trạng thái của hệ thống, không vẽ; màn hình dùng `WindowInsets.statusBars` | - | - | theo `context/` |

- **Thành phần mới cần thêm vào hệ thống thiết kế:** Không có.

## 4. Luồng thao tác

Mở app lần đầu: Loading > hộp thoại đồng ý quảng cáo > `OnboardingScreen` trang 1 > bấm Next > trang 2 > Next > trang 3 > Next > trang 4 > bấm "Start drawing" > `ProjectsScreen` (trống). Không có Skip (Q19). Back về trang trước; ở trang 1 thì thoát app. Tắt app giữa chừng thì lần mở sau hiện lại từ trang 1, không còn quảng cáo.

## 5. Vị trí quảng cáo

`native_onboarding`: thẻ native 250 dp (`NativeStepAd`, đặt ngoài pager), chỉ ở lần mở đầu, đổi quảng cáo mỗi trang; khung cao theo `NativeStepAdDefaults.height()`; nút Next ghim đáy, cách thẻ ít nhất 24 dp ở mọi cỡ chữ; sang trang không chờ quảng cáo. CTA quảng cáo viền xanh dương, khác nút xanh chanh của app (bản đề xuất mục 9.1).

## 6. Chuyển sang Compose

- **Phải giống bản mẫu:** bố cục, thứ tự thành phần, chữ (mục 2), vai trò màu và kiểu chữ (mục 3).
- **Được phép khác:** cách vẽ chữ và bóng của hệ thống, hiệu ứng chuyển động, chênh lệch nhỏ theo lưới 4 dp, icon `Icons.Rounded` thay cho phông Material Symbols; hình minh họa vẽ bằng Compose Canvas theo số liệu `context/`.

## 7. Tiêu chí khi duyệt

- [ ] Dùng đúng màu, chữ, khoảng cách, thành phần của hệ thống thiết kế.
- [ ] Đủ các trạng thái trong mục 1, kể cả chế độ tối.
- [ ] Chữ đúng bảng chữ, không bị cắt hay tràn.
- [ ] Bố cục và thứ tự thành phần giống bản mẫu; lệch về vị trí không quá một bước lưới 4 dp.
- [ ] Vùng chạm đủ lớn (ít nhất 48 dp), chữ dễ đọc.
- [ ] Dùng được ở màn hình thấp (360 x 640): nội dung cuộn được, chữ không chồng nhau, nút chính không bị che hay cắt.
- [ ] Vùng quảng cáo tách bạch, không gây bấm nhầm.
- [ ] Giao diện và nội dung không được thiết kế để thu hút chủ yếu trẻ em.

## 8. Kết quả đối chiếu

<!-- AI dán review/summary.md sau mỗi lần chạy scripts/design-compare.sh, kèm nhận xét của rà soát độc lập. -->
