# Railway 部署指南（使用 Dockerfile）

## 安全部署到 Railway

### 1. 環境變數設定
在 Railway 專案的 Variables 頁面中設定以下環境變數：

```
MONGODB_URI=mongodb+srv://Sights:SummerPractice@sights.lpcnzhu.mongodb.net/?retryWrites=true&w=majority&appName=Sights
MONGODB_DATABASE=Sights
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
PORT=8080
```

### 2. 部署步驟

1. **連接 GitHub 倉庫**
   - 在 Railway 控制台中選擇 "Deploy from GitHub repo"
   - 選擇你的後端專案倉庫

2. **Railway 自動檢測 Dockerfile**
   - Railway 會自動檢測到專案根目錄的 Dockerfile
   - 使用 Docker 構建和部署應用程式

3. **設定環境變數**
   - 進入專案的 Variables 頁面
   - 添加上述所有環境變數
   - 注意：CORS_ALLOWED_ORIGINS 要設定為你前端的實際域名

### 3. Dockerfile 優化

我們的 Dockerfile 已經針對 Railway 進行了優化：
- ✅ 使用 JDK 21 slim 映像檔減少大小
- ✅ 設定適當的 JVM 記憶體限制（-Xmx512m）
- ✅ 支援動態 PORT 環境變數
- ✅ 包含 .dockerignore 以加速構建

### 4. 安全注意事項

- ✅ 敏感資訊已移至環境變數
- ✅ `.env` 檔案已加入 `.gitignore` 和 `.dockerignore`
- ✅ 原始碼中不包含任何密碼或連線字串
- ✅ CORS 設定可根據部署環境調整

### 4. 部署後設定

1. **更新 CORS 設定**
   - 取得 Railway 分配的域名
   - 更新前端專案中的 API 基礎 URL
   - 在 Railway 環境變數中更新 `CORS_ALLOWED_ORIGINS`

2. **測試連線**
   - 確認後端服務正常啟動
   - 測試 MongoDB 連線
   - 測試 API 端點

### 5. 本地開發

```bash
# 使用本地環境變數
source .env
./gradlew bootRun
```

或者在 IDE 中設定環境變數。

### 6. 常見問題

- **CORS 錯誤**: 確認 `CORS_ALLOWED_ORIGINS` 包含正確的前端域名
- **MongoDB 連線失敗**: 檢查 `MONGODB_URI` 是否正確設定
- **Port 衝突**: Railway 會自動分配 PORT，確保應用程式使用 `${PORT}` 環境變數
