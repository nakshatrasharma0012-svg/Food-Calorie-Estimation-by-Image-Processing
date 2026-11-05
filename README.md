🍛 FoodCalorie AI — Food Calorie Estimation by Image Processing

An Android app that estimates calories and nutrition of Indian food from a single photo, backed by a Google Colab–trained multi-model AI pipeline and an optional Gemini Vision API mode.
✨ Features
📸 Capture a meal with the camera or pick an image from the gallery
🤖 AI analysis: calories, carbs, protein, fat, fibre, sugar, vitamins, serving size, health insights
🔀 Selectable backend: Direct Gemini Vision API or Custom Colab / Ngrok backend
🛟 Fallback chain: custom backend → Gemini → offline visual analysis
🗂️ Local history and ❤️ favourites (Room / SQLite)
📊 Home dashboard with today's intake
👤 Register / login with USER and ADMIN roles
🛠️ Admin dashboard: manage users, roles and all food logs

🧱 Tech Stack
Layer	Technology
Android UI	Kotlin, Jetpack Compose, Material 3
State	MainViewModel + StateFlow
Local DB	Room over SQLite (food_calorie_database, v3)
Networking	OkHttp
AI backend	Google Colab (Python), exposed via Ngrok; Gemini Vision API
ML	TensorFlow / Keras, scikit-learn (MobileNetV2, ResNet50V2, Custom CNN, Random Forest)
Tools	Android Studio, Pixel 9 emulator

SDK: minSdk 24 · targetSdk 35 · compileSdk 35 · package com.example.food



🏗️ Architecture
User ─► Compose UI ─► MainViewModel ─► FoodAnalyzerService (OkHttp)
                           │                    ├─► Colab backend  (/analyze, /predict)
                           │                    ├─► Gemini Vision API
                           │                    └─► Offline fallback
                           └─► Room DB (users, food_records)

ML pipeline (Colab): image → resize 224×224 → MobileNetV2 / ResNet50V2 / Custom CNN / Random Forest → weighted ensemble (0.40 / 0.35 / 0.10 / 0.15) → nutrition CSV lookup → JSON response.

Diagrams (ER, use case, DFD 0/1/2, architecture, Gantt, SDLC) are in /docs.

📁 Project Structure
Food/
├── app/src/main/java/com/example/food/
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── local/        AppDatabase, User, FoodRecord, UserDao, FoodRecordDao
│   │   ├── model/        FoodAnalysisResult
│   │   ├── remote/       FoodAnalyzerService
│   │   └── repository/   PreferencesRepository
│   └── ui/
│       ├── components/   BottomNavBar
│       ├── screens/      Splash, Auth, Home, Camera, AnalyzingOverlay,
│       │                 Result, History, Favorites, Settings, AdminDashboard
│       ├── theme/
│       └── viewmodel/    MainViewModel
├── build.gradle.kts
└── settings.gradle.kts
notebook/
└── Indian_Food_MultiModel_Nutrition_System.ipynb

🚀 Getting Started
1. Run the Android app
Clone the repo

git clone https://github.com/nakshatrasharma0012-svg/Food-Calorie-Estimation-by-Image-Processing).git


Open the Food folder in Android Studio.
Let Gradle sync, then run on an emulator or device (Android 7.0+).
On the login screen use the demo accounts:
Admin: admin@food.com
User: user@food.com
2. Choose an AI backend (Profile → Settings)

Option A: Gemini Vision API

Get a free key from Google AI Studio.
Paste it in Settings → Gemini API Key.

Option B: Custom Colab / Ngrok backend

Open the notebook in Google Colab.
Put your Indian food image dataset (class sub-folders) in Google Drive and update the dataset path.
Run all cells to train and evaluate the models.
Start the inference service and copy the public Ngrok URL.
Paste the URL in Settings → Custom Colab / Ngrok Backend. The URL changes each time the tunnel restarts.

🔐 Never commit API keys or Ngrok URLs. Keep them in app settings only.

🧠 Model Training (Colab notebook)
Input size: 224×224 RGB, with data augmentation
Split: 80% train / 10% validation / 10% test
Models: MobileNetV2, ResNet50V2 (transfer learning), 3-block Custom CNN, Random Forest on deep embeddings
Callbacks: EarlyStopping (restore best weights)
Evaluation: accuracy, confusion matrix, precision / recall / F1 per class
Nutrition values: matched from a nutrition CSV and scaled by quantity

Honest note: the prototype run found only 7 images, so the 100% accuracy shown in its confusion matrices is not representative. A larger labelled Indian food dataset is needed for real evaluation.

Related research

An image-only calorie regression study on Nutrition5k (frozen EfficientNet-B0 → PCA-128 → five regressors → split-conformal intervals) is described in the project's IEEE-style paper. Best model PCA-HGB: MAE 86.4 kcal, RMSE 109.4 kcal, R² 0.748 on 105 held-out dishes. Conformal intervals reached 94.3–98.1% coverage at 90% nominal.

🗄️ Database

Room database food_calorie_database with tables:

Table	Key fields
users	id, name, email, password, role (USER / ADMIN), createdAt
food_records	id, foodName, calories, carbs, protein, fat, fiber, sugar, vitamins, quantity, healthInsightsJson, imagePath, timestamp, isFavorite
📸 Screenshots
Splash	Login	Home	Result
docs/screens/splash.png	docs/screens/login.png	docs/screens/home.png	docs/screens/result.png

(Add your screenshots to docs/screens/ and replace the paths with ![alt](path) images.)

⚠️ Limitations
Portion size, hidden ingredients and oil are not detectable from one photo
Classifier results come from a tiny prototype dataset
Some test cases (camera permission, backend failure, restart persistence, invalid response) still need to be executed and recorded
food_records has no userId column yet, so scans are not linked to users by foreign key
🔮 Future Scope
Verified nutrition database and a real labelled Indian food dataset
Better portion estimation (depth, multiple views, reference objects)
Secure credential storage, authentication for the backend, cloud sync
Weekly / daily charts, meal categories, multi-language support
