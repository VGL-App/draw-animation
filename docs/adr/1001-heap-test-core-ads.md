---
id: ADR-1001
title: App nâng heap của JVM test trong core/ads/build.gradle.kts, là ngoại lệ tạm của quy tắc app không sửa :core:ads, chờ dự án mẫu nhận thay đổi
status: proposed             # proposed | accepted | superseded
origin: app                  # template: ADR của dự án mẫu (mã dưới 1000) | app: ADR riêng của app (mã từ 1001)
date: 2026-11-23
deciders: [Nhật]
supersedes:                  # không thay ADR nào: ngoại lệ tạm, ADR-0030 vẫn hiệu lực cho mọi phần khác
superseded_by:
---

# ADR-1001: APP NÂNG HEAP CỦA JVM TEST TRONG CORE/ADS/BUILD.GRADLE.KTS, LÀ NGOẠI LỆ TẠM CỦA QUY TẮC APP KHÔNG SỬA :CORE:ADS, CHỜ DỰ ÁN MẪU NHẬN THAY ĐỔI

## Bối cảnh

App khai báo 22 vị trí quảng cáo trong `AdPlacement.kt`. `AdsController` dựng chỗ nạp sẵn cho mọi vị trí, nên các test của `:core:ads` tốn thêm bộ nhớ. Trên CI (`pr-check`, Java 17), `:core:ads:testDebugUnitTest` hết bộ nhớ (`OutOfMemoryError` khi dựng ảnh so sánh của screenshot test) với heap mặc định 512 MB của JVM test; trên máy dev với Java 21 thì qua.

`docs/ops/to-app.md` mục 2 và ADR-0030 xếp `:core:ads` (trừ `AdPlacement.kt`) vào phần của dự án mẫu, app không sửa. Chỗ đặt heap cho mọi module là convention plugin trong `build-logic/`, cũng của dự án mẫu. Không sửa thì mọi ticket thêm vị trí quảng cáo bị chặn ở CI, trong đó T-007 đang chặn 21 ticket.

Không ghi `supersedes` cho ADR-0030: app không chuyển ADR của dự án mẫu sang `superseded` được, và `scripts/template-sync.sh` xóa ADR của dự án mẫu đã `superseded`.

## Quyết định

1. App thêm vào `core/ads/build.gradle.kts` một khối đặt `maxHeapSize = "2g"` cho mọi task `Test` của module, không đổi gì khác của `:core:ads` ngoài `AdPlacement.kt`.
2. App mở issue `from-app` ở repo dự án mẫu, đề xuất dự án mẫu nhận thay đổi (ở `core/ads/build.gradle.kts`, hay chung cho mọi module ở `build-logic/`).
3. Ở đợt `template-sync` kế tiếp, khi dự án mẫu chưa nhận thay đổi thì giữ dòng của app (gộp tay khi xung đột); khi dự án mẫu đã nhận (cùng hay tốt hơn) thì lấy bản của mẫu và chuyển ADR này sang `superseded` bằng một ADR mới của app ghi việc đó.
4. Ngoại lệ khác ở phần của dự án mẫu cần ADR riêng của app; không mở rộng ADR này.

## Các phương án đã cân nhắc

| Phương án | Ưu điểm | Nhược điểm |
| --- | --- | --- |
| Sửa `core/ads/build.gradle.kts` (đã chọn, lời anh Nhật 2026-11-23) | Hẹp, chỉ một module; không chạm `build-logic/`; gỡ chặn ngay | Lệch dự án mẫu ở một file của mẫu; đợt đồng bộ có thể phải gộp tay |
| Sửa convention plugin trong `build-logic/` | Đúng gốc, áp cho mọi module | `build-logic/` thuộc mục "Không được làm", cần plan rủi ro cao riêng; lệch dự án mẫu rộng hơn |
| Sửa ở dự án mẫu trước rồi đồng bộ về | Sạch nhất về quy trình | Chậm, T-007 và 21 ticket chờ lâu |
| Đổi screenshot test của `:core:ads` cho tốn ít bộ nhớ | Không đổi cấu hình build | Sửa test của dự án mẫu, rộng và rủi ro hơn |

## Hệ quả

- **Tích cực:** test `:core:ads` qua trên CI Java 17 với 22 vị trí mới (chạy thử 2026-11-23, `pr-check` lần chạy 45087289839 xanh); T-007 và các ticket đặt quảng cáo không bị chặn.
- **Phải chấp nhận:** một file của dự án mẫu lệch bản mẫu tới khi mẫu nhận thay đổi; JVM test của `:core:ads` dùng tới 2 GB bộ nhớ trên máy chạy CI.
- **Việc AI phải tuân thủ:** trong `:core:ads`, ngoài `AdPlacement.kt` app chỉ được giữ khối `maxHeapSize` của task `Test` trong `core/ads/build.gradle.kts`; đợt `template-sync` giữ khối đó khi dự án mẫu chưa có thay đổi tương đương, lấy bản mẫu khi đã có; ngoại lệ khác ở phần của dự án mẫu cần ADR riêng của app
