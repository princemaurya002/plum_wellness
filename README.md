# Plum Wellness App 🌟

A personalized wellness app that helps users get daily health tips tailored to their profile. The app uses AI to generate short, engaging tips, allows users to explore detailed advice, and gives them the option to save their favorite tips for later.

## 🌟 Features

### Profile Capture
- Users enter their basic details: age, gender, and health goal
- Profile is used to generate personalized tips
- Modern, animated UI with form validation

### Wellness Board (Main Screen)
- AI generates 5 short tips with titles, icons, and summaries
- Cards displayed in a scrollable feed with smooth animations
- Regenerate button to refresh tips with new suggestions
- Pull-to-refresh functionality

### Tip Details
- Detailed explanation when user taps a tip card
- AI expands tips into friendly article-style explanations
- Step-by-step guidance on how to follow each tip
- Beautiful, readable layout with proper typography

### Favorites System
- Save tips by tapping the heart icon
- Local storage using Room database
- Dedicated Favorites section with statistics
- Easy removal from favorites

## 🤖 AI Integration

The app integrates with Google's Gemini API to provide:

1. **Generate Tips**: AI creates 5 personalized wellness tips based on user profile
2. **Expand Tip**: AI provides detailed explanations and step-by-step guides
3. **Regenerate**: AI generates fresh tips when user wants more variety

**Note**: The Gemini API key is configured on the backend - users don't need to enter any API keys.

## 🛠️ Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **Navigation**: Navigation Compose
- **Animations**: Compose Animations
- **Image Loading**: Coil
- **API**: Google Gemini API

## 📱 Modern UI/UX Features

- **Material Design 3**: Latest Material Design components and theming
- **Smooth Animations**: Page transitions, card animations, and loading states
- **Responsive Design**: Works on different screen sizes
- **Dark/Light Theme**: System-based theme switching
- **Loading States**: Beautiful loading indicators and skeleton screens
- **Error Handling**: User-friendly error messages with retry options
- **Accessibility**: Proper content descriptions and semantic labels

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Kotlin 1.9.10 or later
- Android SDK 24+ (Android 7.0)
- OpenAI API Key

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd plum_pm
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project folder and select it

3. **Sync Project**
   - Android Studio will automatically sync the project
   - Wait for all dependencies to download

4. **Configure Gemini API Key**
   - Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
   - Create an account or sign in
   - Generate a new API key
   - Replace `YOUR_GEMINI_API_KEY_HERE` in `ApiConfig.kt` with your actual API key

5. **Run the App**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - The app will install and launch

### First Run

1. **Profile Setup**
   - Enter your name, age, gender, and health goal
   - Tap "Create My Profile"

2. **Generate Tips**
   - The app will automatically generate 5 personalized wellness tips
   - No API key input required from users

3. **Explore Tips**
   - Tap any tip card to see detailed explanations
   - Heart tips you like to save them as favorites
   - Use the regenerate button for new tips

## 🔧 Configuration

### API Key Storage
The Gemini API key is configured in `ApiConfig.kt`. For production use, consider:
- Storing the API key securely in Android Keystore
- Using environment variables or build config fields
- Implementing proper API key validation
- Using a backend service to proxy API calls

### Database
The app uses Room database for local storage:
- User profiles are stored locally
- Wellness tips are cached locally
- Favorites are stored persistently

### Network Configuration
- Internet permission is required
- API calls are made to Google's Gemini endpoints
- Proper error handling for network issues

## 🎨 Customization

### Themes
The app supports both light and dark themes:
- Colors are defined in `Color.kt`
- Typography is configured in `Type.kt`
- Material Design 3 theming is applied throughout

### Animations
Customize animations in:
- `WellnessNavigation.kt` for page transitions
- Individual screens for component animations
- Loading states and micro-interactions

### Content
- Health goals can be customized in `UserProfile.kt`
- Tip categories and icons can be modified
- AI prompts can be adjusted in `WellnessRepository.kt`

## 📊 App Architecture

```
app/
├── data/
│   ├── api/           # API service interfaces
│   ├── database/      # Room database and DAOs
│   ├── model/         # Data models
│   └── repository/    # Repository implementation
├── di/                # Dependency injection modules
├── navigation/        # Navigation setup
├── ui/
│   ├── screen/        # UI screens
│   ├── theme/         # App theming
│   └── viewmodel/     # ViewModels
└── MainActivity.kt    # Main activity
```

## 🔒 Security Considerations

- API keys are handled securely
- User data is stored locally on device
- Network requests use HTTPS
- No sensitive data is logged

## 🚀 Future Enhancements

- [ ] Push notifications for daily tips
- [ ] Tip sharing functionality
- [ ] Progress tracking
- [ ] Social features
- [ ] Offline mode
- [ ] Multiple languages support
- [ ] Widget support
- [ ] Wear OS companion app

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Google for providing the Gemini API
- Google for Jetpack Compose and Material Design
- Android community for excellent libraries and tools

---

**Note**: This app requires a Gemini API key to function. Make sure to keep your API key secure and never commit it to version control.
