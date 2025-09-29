# Problem Statement 3: AI‑Generated Wellness Recommendation Board (Mobile - Android)

An Android app that generates personalized wellness tips using AI and displays them in a clean, card-based board.  
Users can explore AI-generated suggestions, view detailed step-by-step guidance, and save their favorites locally.  
Built with **Kotlin, Jetpack Compose, MVVM, Hilt**, and **Material 3**.

---

## 1) Project Setup & Demo

Mobile (Android):
- Requirements: Android Studio (Koala+), JDK 17+, Android SDK API 34.

- Steps to Run
  Clone the repository:  
   ```bash
   git clone https://github.com/princemaurya/plum_pm.git
   cd plum_pm
- Build:
  ```bash
  ./gradlew :app:assembleDebug
  # Windows:
  gradlew.bat :app:assembleDebug
  ```
- Install:
  ```bash
  adb install -r app/build/outputs/apk/debug/app-debug.apk
  ```
- Run: Launch from Android Studio on an emulator or device.

Demo: [https://drive.google.com/drive/folders/1Wb7zx9wkiTDPBLqVujBomttsOe0WRg7T?usp=sharing](https://drive.google.com/file/d/15rrJYms_yrzpyPjpAjFVKW73IZ3LV2jU/view?usp=drive_link)
- 

---

## 2) Problem Understanding

The task is to create a personalized wellness board powered by AI:
- Capture user profile inputs (age, gender, and wellness goals).
- Generate 5 short, engaging wellness tips as cards.
- Let users tap a card to view detailed explanations with step-by-step advice.
- Allow saving tips locally for quick access later.
- Provide a regenerate option for fresh AI suggestions.

Assumptions:
- AI is accessed via a REST API.
- Local persistence is handled with Room Database.
- Tips are short and lightweight for fast exploration.

---

## 3) AI Prompts & Iterations

### Integration at a glance
- **Method**: REST API via Retrofit (Gemini for tips/expansion; optional Google Cloud Translation for i18n).
- **Entry points**: `WellnessViewModel.generateNewTips()` and `WellnessViewModel.expandTip()` invoke `WellnessRepository`.

### Prompts used (from code)
- **Tips Generation Prompt** (`WellnessRepository.createTipsPrompt(userProfile)`)
  - Purpose: Generate 5 personalized tips for the user profile and locale.
  - Core constraints:
    - Localize values to display language; keep JSON keys in English.
    - Personalize with age, gender, goals, activity, diet, stress, work/motivation styles, favorites, etc.
  - Response schema:
    ```json
    {
      "tips": [
        {
          "id": "unique_id_1",
          "title": "Tip Title",
          "summary": "One line summary",
          "detailedExplanation": "Brief explanation",
          "stepByStepGuide": ["Step 1", "Step 2", "Step 3"],
          "category": "Category name",
          "icon": "emoji_icon"
        }
      ]
    }
    ```

- **Tip Expansion Prompt** (`WellnessRepository.createExpansionPrompt(tip, userProfile)`)
  - Purpose: Produce a detailed explanation and step-by-step guide for a chosen tip.
  - Response schema:
    ```json
    {
      "detailedExplanation": "Comprehensive and tailored explanation",
      "stepByStepGuide": ["Detailed step 1", "Detailed step 2", "Detailed step 3"]
    }
    ```

- **Translation (optional)**: Uses Google Cloud Translation REST API in `translateExistingTips(...)` to translate stored fields.

#### Exact prompts (verbatim from code)

- Tips generation (`createTipsPrompt`):
  ```text
  Generate 5 highly personalized wellness tips for a {age}-year-old {gender}{heightWeightInfo}{bmiInfo} 
  whose primary health goal is {primaryGoal}.
  
  IMPORTANT: Write all textual content in {DisplayLanguage}. Do not include any other language. Keep the JSON keys in English, but localize all values.
  
  User Profile Details:
  - Primary Goal: {primaryGoal}
  {secondaryGoalsLine}
  - Activity Level: {activityLevel}
  - Exercise Preferences: {exercisePreferences}
  - Daily Wellness Time: {dailyWellnessTime}
  - Dietary Preference: {dietaryPreference}
  {dietStyleLine}
  - Stress Level: {stressLevel}
  - Mindfulness Experience: {mindfulnessExperience}
  - Work Style: {workStyle}
  - Screen Time: {screenTime}
  - Motivation Style: {motivationStyle}
  {favoriteActivitiesLine}
  {additionalInfoLine}
  
  Please return the response in the following JSON format:
  {
    "tips": [
      {
        "id": "unique_id_1",
        "title": "Tip Title",
        "summary": "One line summary",
        "detailedExplanation": "Brief explanation",
        "stepByStepGuide": ["Step 1", "Step 2", "Step 3"],
        "category": "Category name",
        "icon": "emoji_icon"
      }
    ]
  }
  
  Make the tips:
  - Highly personalized based on their specific profile
  - Practical and actionable for their lifestyle and time constraints
  - Tailored to their activity level, dietary preferences, and work style
  - Appropriate for their stress level and mindfulness experience
  - Aligned with their motivation style ({motivationStyle})
  - Evidence-based wellness practices that are safe and effective
  - Consider their exercise preferences and favorite activities
  - Address their specific health goals and any secondary goals
  ```

- Tip expansion (`createExpansionPrompt`):
  ```text
  Expand this wellness tip with detailed information:
  
  Original tip: {tip.title}
  Summary: {tip.summary}
  
  User profile: {age}-year-old {gender}
  - Primary Goal: {primaryGoal}
  - Activity Level: {activityLevel}
  - Exercise Preferences: {exercisePreferences}
  - Daily Wellness Time: {dailyWellnessTime}
  - Dietary Preference: {dietaryPreference}
  - Stress Level: {stressLevel}
  - Work Style: {workStyle}
  - Motivation Style: {motivationStyle}
  {additionalInfoLine}
  
  IMPORTANT: Write all textual content in {DisplayLanguage}. Do not include any other language. Keep the JSON keys in English, but localize all values.
  
  Please provide a detailed explanation and step-by-step guide. Return in JSON format:
  {
    "detailedExplanation": "Comprehensive explanation of the tip with scientific backing, tailored to their specific profile",
    "stepByStepGuide": ["Detailed step 1", "Detailed step 2", "Detailed step 3"]
  }
  
  Make the explanation:
  - Comprehensive and evidence-based
  - Highly personalized for their specific profile
  - Actionable within their time constraints ({dailyWellnessTime})
  - Appropriate for their activity level and exercise preferences
  - Aligned with their motivation style and work environment
  - Consider their dietary preferences and stress level
  ```

### Prompt iterations for better results
- **Localization rule**: Enforced “values localized, keys in English” to stabilize parsing across languages.
- **Strict schemas**: Specified exact JSON formats for both generation and expansion to reduce parse errors.
- **Richer context**: Included more profile attributes (activity, diet, stress, work/motivation, favorites) for relevance.
- **Safety/actionability**: Asked for practical, safe, evidence-based guidance and time-bounded steps.
- **Graceful fallback**: On API/parse failure, repository returns mock data to keep the UX smooth.


### Translation (language switching)
- **Trigger**: Language change via `LanguageViewModel` (UI shows `isTranslating`, captures `translationError`).
- **Method/Endpoint**: REST via Retrofit (`TranslationApiService`) to Google Cloud Translation API (v2) using `ApiConfig.TRANSLATION_API_KEY`.
- **Request (per tip, batched fields)**:
  ```json
  {
    "q": ["title", "summary", "detailedExplanation", "step 1", "step 2", "step 3", "category"],
    "target": "<lang-code>",
    "format": "text"
  }
  ```
- **Response handling**: Maps `data.translations[].translatedText` back into the corresponding fields, decodes HTML entities, and persists via Room.
- **Flow**: Language change → `translateExistingTips(code)` → `TranslationApiService.translate(...)` → map & save → UI recomposes with translated content.

---

## 4) Architecture & Code Structure

Pattern: MVVM + Hilt DI + Jetpack Compose

### Navigation
- `MainActivity.kt` hosts Compose content and splash (system + in‑app overlay).
- `ui/components/AppNavigator` coordinates navigation between screens.

### Screens
- `ui/screen/wellnessboard/WellnessBoardScreen.kt`
  - Swipe-to-refresh (Accompanist) with custom indicator (arrow hint) and regenerate action.
  - Loading state with rotating phrases.
  - Tip cards: emoji, title, category, summary, favorite toggle.
- `ui/screen/favorites/FavoritesScreen.kt`
  - Lists saved tips; cleaned stats card (removed “Out of X total tips”).
- Tip detail screen
  - Expandable advice; extend within navigator for long-form content.

### ViewModels & State
- `WellnessViewModel`: exposes `uiState` (tips, loading, errors, favorites) and actions: `generateNewTips()`, `toggleFavorite(tip)`, `loadFavoriteTips()`.
- `LanguageViewModel`: manages current language, translating UI strings and coordinating translation flow.
- State via Kotlin Flows observed by Compose.

### Data Layer (Room)
- DAOs: `UserProfileDao`, `WellnessTipDao`.
- Entities/Models: `UserProfile`, `WellnessTip`.
- Persistence for tips and favorites; updates reflect immediately in UI via flows.

### Networking & AI
- Retrofit/OkHttp/Gson configured in Gradle.
- `WellnessRepository` orchestrates:
  - Gemini REST calls for tip generation and expansion.
  - Optional Google Cloud Translation REST API for language switching.
  - Business rules (preserve favorites on regeneration, graceful fallbacks).

### Theming & Resources
- Material 3 theme in `res/values/themes.xml`.
- Adaptive icons in `res/mipmap-anydpi-v26/ic_launcher*.xml` using `@drawable/logo`.



### 📊 Project Structure

```text
app/
├── data/
│   ├── api/           # API service interfaces (Gemini, Translation)
│   ├── database/      # Room database and DAOs
│   ├── model/         # Data models (UserProfile, WellnessTip, responses)
│   └── repository/    # WellnessRepository (AI + persistence orchestration)
├── di/                # Hilt modules and providers
├── navigation/        # Navigation setup (AppNavigator)
├── ui/
│   ├── screen/        # Compose screens (WellnessBoard, Favorites, etc.)
│   ├── theme/         # Material 3 theming (colors, typography)
│   └── viewmodel/     # ViewModels (WellnessViewModel, LanguageViewModel)
└── MainActivity.kt    # Single-activity host and splash behavior
```

---


## 6) Known Issues / Improvements

- **Error handling and offline behavior**
  - Good fallback to mock tips exists, but user feedback could be clearer on network/API failures.
  - Improvement: Add explicit error UI with retry and distinguish between offline vs API errors; cache last successful generation for offline reads.

- **Rate limiting and regenerate UX**
  - `generateNewTips()` can be triggered repeatedly (pull‑to‑refresh + menu). 
  - Improvement: Debounce/throttle calls; surface a cooldown indicator; handle 429/quotas gracefully.

- **Room migrations and test coverage**
  - Room DAOs exist; migration strategy isn’t documented and automated tests are absent.
  - Improvement: Add Room schema export, migration tests, and basic unit tests for `WellnessRepository`/`WellnessViewModel` (happy/error paths, favorites toggling).

- **Localization coverage**
  - `stringResourceLocalized(...)` is used, but some strings in UI are still hardcoded.
  - Improvement: Move all UI strings to `strings.xml`, add translations, and verify RTL support and font fallback.

- **UI consistency for pull‑to‑refresh**
  - Custom non‑spinner indicator was prototyped; currently the default spinner is restored in code while README mentions the custom indicator.
  - Improvement: Pick one approach; if custom, encapsulate and add tests/preview; if default, update README accordingly.

---


## 7) Bonus Work / Extra Features

- **Dark Mode**: Full Material 3 dark theme support with appropriate colors and contrast.
- **Edit User Info mid‑flow**: Access profile editing from the Sidebar at any time and change user info to optimize results.
- **Regenerate Tips Anytime**: Trigger `generateNewTips()` from pull‑to‑refresh or the regenerate action in UI to generate new tips.
- **Multilingual Support**: Change language of the application anytime without restarting the app.
