# 📱 Pixel Grain — Android App

> **Part of the [Pixel Grain](https://github.com/Divyansh0208) agritech platform.**
> Native Android app that classifies crop images on-device using a YOLOv8 ONNX model — no internet required for inference.

---

## ✨ Features

- 📷 Pick any image from gallery
- 🧠 On-device inference via ONNX Runtime (no internet needed)
- 🌾 Classifies 5 crops: **wheat, rice, maize, sugarcane, jute**
- ⚡ Result in under 1 second
- 📦 Lightweight — model is only 5.5MB

---

## 📊 Model Performance

| Metric | Value |
|--------|-------|
| Model | YOLOv8n-cls (ONNX) |
| Validation Accuracy | **100%** |
| Model Size | 5.5 MB |
| Runtime | onnxruntime-android 1.17.0 |
| Min SDK | API 24 (Android 7.0+) |

---

## 🗂️ Project Structure

```
pixel-grain-android/
├── app/
│   └── src/
│       └── main/
│           ├── assets/
│           │   └── best.onnx          ← YOLOv8 ONNX model
│           ├── java/com/example/cropidentifier/
│           │   └── MainActivity.kt    ← all inference logic
│           └── AndroidManifest.xml
├── app/build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── .gitignore
```

---

## ⚙️ Setup

### Requirements
- Android Studio (latest)
- Android SDK API 24+
- Internet for first Gradle sync (downloads dependencies)

### Steps
1. Clone the repo:
```bash
git clone https://github.com/Divyansh0208/pixel-grain-android.git
```
2. Open in Android Studio
3. Let Gradle sync complete
4. Run on emulator or physical device (API 24+)

---

## 🚀 How It Works

1. User taps **Select Crop Image**
2. Image decoded via Android `ImageDecoder` API
3. Bitmap resized to 224×224 and normalized (ImageNet mean/std)
4. Float tensor `[1, 3, 224, 224]` passed to `best.onnx`
5. ONNX Runtime runs inference locally
6. Softmax probabilities over 5 classes → top prediction displayed

---

## 🏷️ Classes & Label Map

| Label | Index |
|-------|-------|
| jute | 0 |
| maize | 1 |
| rice | 2 |
| sugarcane | 3 |
| wheat | 4 |

---

## 🛠️ Stack

| Tool | Purpose |
|------|---------|
| Kotlin | Primary language |
| Jetpack Compose | UI framework |
| onnxruntime-android 1.17.0 | On-device ML inference |
| Android ImageDecoder | Robust image loading |
| Gradle (Kotlin DSL) | Build system |

> ⚠️ **TFLite not used** — Python 3.14 (used for training) has no compatible TensorFlow wheels. ONNX Runtime is used instead and provides equivalent performance.

---

## 🔭 Roadmap

- [x] Phase 1 — Crop classifier (5 classes, on-device ONNX inference)
- [ ] Phase 2 — Grain segmentation (YOLOv8n-seg)
- [ ] Phase 2 — Adulteration detection (MobileNetV2)
- [ ] Phase 2 — ₹5 coin size calibration (OpenCV)
- [ ] Phase 3 — Marketplace UI + backend integration
- [ ] Phase 3 — Hindi language support

---

## 🧠 ML Training

The model was trained in a separate repo:
→ [pixel-grain-ml](https://github.com/Divyansh0208/pixel-grain-ml)

---

## 👤 Author

**Divyansh (Krrish)**
Built as part of the Pixel Grain agritech platform — Edge AI for farmers in Uttar Pradesh.
