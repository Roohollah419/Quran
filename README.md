# Quran Android Application

A modern Android application for reading and exploring the Holy Quran with a clean, user-friendly interface.

## About

This is a native Android application that provides access to the complete text of the Holy Quran in Arabic with English translations. The app features all 114 Surahs with 6,236 verses, offering a seamless reading experience with the authentic Uthmani Taha Naskh font, customizable themes, adjustable font sizes, and a clean, distraction-free interface designed for comfortable reading.

## Features

- **Complete Quran Text**: All 114 Surahs with full Arabic text and English translations
- **Uthmani Taha Naskh Font**: Authentic Uthman Taha Naskh typeface for beautiful Arabic text rendering
- **Bismillah Display**: Automatic Bismillah (بِسۡمِ ٱللَّهِ ٱلرَّحۡمَـٰنِ ٱلرَّحِیمِ) at the beginning of each Surah (except Surah 1 and 9)
- **Arabic Numerals**: Ayah numbers displayed in Arabic numerals (٠-٩) inline with the text
- **Compact Card Design**: Minimalist, flat ayah cards with optimal spacing for distraction-free reading
- **Random Ayah Display**: Discover a new verse every time you open the app
- **I'm Feeling Lucky**: Navigate to a random Surah for spontaneous exploration
- **Dark/Light Themes**: Switch between themes for comfortable reading in any lighting condition
- **Adjustable Font Sizes**: Four font size options (Small, Medium, Large, Extra Large) for optimal readability
- **Surah Name Display**: Toggle between English and Arabic names for Surahs in the list view
- **Collapsing Header**: Beautiful framed Surah header that collapses on scroll for maximum reading space
- **Offline Access**: All Quran data stored locally in Room database for instant access without internet
- **Clean Material Design**: Modern UI following Material Design guidelines with immediate settings application
- **Fragment-based Navigation**: Smooth transitions between screens
- **GitHub Integration**: Direct link to project repository in settings

## Screenshots

The app includes three main screens:
- **Home Screen**: Random Ayah display with quick navigation options
- **Surah List**: Browse all 114 Surahs with metadata (name in English/Arabic, verse count, revelation type)
- **Surah Detail**: Read complete Surahs with beautifully formatted verses in Uthmani Taha Naskh font, featuring:
  - Collapsing header with Surah name, ayah count, and revelation type icon
  - Bismillah at the beginning (for applicable Surahs)
  - Compact ayah cards with inline Arabic numerals
  - Minimal borders and optimal spacing for distraction-free reading

## Requirements

### Development Environment

- **Android Studio**: Arctic Fox (2020.3.1) or newer
- **JDK**: Java Development Kit 17 or higher
- **Gradle**: 8.2 (included via Gradle Wrapper)
- **Android Gradle Plugin**: 8.2.0

### Target Devices

- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Target SDK**: API 36 (Android 15)
- **Compile SDK**: API 36

## Project Structure

```
Quran/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/quran/
│   │   │   │   ├── ui/                    # UI layer (MVVM)
│   │   │   │   │   ├── base/              # Base classes
│   │   │   │   │   ├── home/              # Home screen
│   │   │   │   │   ├── surahlist/         # Surah list screen
│   │   │   │   │   ├── surahdetail/       # Surah detail screen
│   │   │   │   │   └── settings/          # Settings dialog
│   │   │   │   ├── data/                  # Data layer
│   │   │   │   │   ├── local/             # Room database
│   │   │   │   │   │   ├── dao/           # Data Access Objects
│   │   │   │   │   │   └── entity/        # Database entities
│   │   │   │   │   ├── model/             # Domain models
│   │   │   │   │   └── repository/        # Repository pattern
│   │   │   │   └── utils/                 # Utilities
│   │   │   ├── res/                       # Resources
│   │   │   │   ├── layout/                # XML layouts
│   │   │   │   ├── values/                # Strings, colors, themes
│   │   │   │   ├── navigation/            # Navigation graph
│   │   │   │   └── mipmap-*/              # App icons
│   │   │   ├── assets/                    # Quran JSON data
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle                   # App module build config
│   └── build.gradle                       # Root build config
├── gradle/                                # Gradle wrapper
├── gradlew.bat                           # Windows Gradle wrapper
├── settings.gradle                        # Project settings
└── README.md                             # This file
```

## Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture pattern with clean separation of concerns:

- **UI Layer**: Activities, Fragments, and Adapters
- **ViewModel Layer**: Manages UI state and business logic
- **Repository Layer**: Single source of truth for data operations
- **Data Layer**: Room database with DAOs and entities

### Key Technologies

- **Room Database**: Local SQLite database wrapper for efficient data storage
- **LiveData**: Observable data holder for reactive UI updates
- **Navigation Component**: Fragment navigation and argument passing
- **Material Components**: Modern UI components following Material Design
- **CoordinatorLayout & AppBarLayout**: Collapsing header with smooth scroll behavior
- **RecyclerView**: Efficient list rendering for Surahs and Ayahs
- **Custom Fonts**: Uthmani Taha Naskh font for authentic Quranic text rendering
- **Gson**: JSON parsing for initial data loading

## Building the Application

### 1. Clone the Repository

```bash
git clone [text](https://github.com/Roohollah419/Quran.git)
cd Quran
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Click **File > Open**
3. Navigate to the project directory and select it
4. Wait for Gradle sync to complete

### 3. Build from Command Line

**On Windows:**
```bash
gradlew.bat clean build
```

**On macOS/Linux:**
```bash
./gradlew clean build
```

This will compile the project and run all checks.

### 4. Generate APK

**Debug APK:**
```bash
gradlew.bat assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

**Release APK:**
```bash
gradlew.bat assembleRelease
```

## Running the Application

### Run on Physical Device

1. **Enable Developer Options** on your Android device:
   - Go to **Settings > About Phone**
   - Tap **Build Number** 7 times
   - Developer Options will be enabled

2. **Enable USB Debugging**:
   - Go to **Settings > Developer Options**
   - Turn on **USB Debugging**

3. **Connect and Deploy**:
   - Connect your device via USB cable
   - Approve the USB debugging prompt on your device
   - In Android Studio, click the **Run** button (or press **Shift+F10**)
   - Select your device from the list
   - The app will build, install, and launch automatically

### Run on Emulator

1. Open **AVD Manager** in Android Studio
2. Create a new virtual device (API 24+)
3. Launch the emulator
4. Click **Run** button in Android Studio
5. Select the emulator from the list

### Install from Command Line

```bash
gradlew.bat installDebug
```

This will install the debug APK on any connected device or running emulator.

## Data Source

The complete Quran data is loaded from `quran.json` file stored in the `assets` folder. On first launch, the app:
1. Parses the JSON file containing all 114 Surahs and 6,236 verses
2. Populates the Room database with the data
3. Uses the database for all subsequent queries for optimal performance

## Configuration

### Settings

Users can customize the app through the Settings dialog (accessible via gear icon):

- **Theme**: Toggle between Light and Dark modes
- **Surah Name**: Choose between English or Arabic names for Surahs in the list view
- **Font Size**: Choose from 4 sizes (Small, Medium, Large, Extra Large)
- **Version Info**: Display current app version with direct link to GitHub repository

All settings are applied immediately without requiring confirmation buttons. Settings are persisted using SharedPreferences and applied across all screens.

## Dependencies

```gradle
// Core Android
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'com.google.android.material:material:1.11.0'

// Room Database
implementation 'androidx.room:room-runtime:2.6.1'
annotationProcessor 'androidx.room:room-compiler:2.6.1'

// Lifecycle (ViewModel & LiveData)
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'

// Navigation Component
implementation 'androidx.navigation:navigation-fragment:2.7.6'
implementation 'androidx.navigation:navigation-ui:2.7.6'

// UI Components
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.cardview:cardview:1.0.0'

// JSON Parsing
implementation 'com.google.code.gson:gson:2.10.1'
```

## App Size

- **APK Size**: ~13 MB
  - JSON Data: 1.4 MB
  - Libraries: ~8 MB
  - Code & Resources: ~3.6 MB

## Version

- **Version Name**: 1.0
- **Version Code**: 1
- **Package Name**: com.example.quran

## Future Enhancements

Potential features for future releases:
- Multiple language translations (Urdu, French, etc.)
- Arabic transliteration (Roman script)
- Audio recitation with verse highlighting
- Bookmarks and favorites system
- Advanced search functionality (by keyword, Surah, Juz)
- Verse sharing with custom graphics
- Reading progress tracking and statistics
- Tafsir (commentary) integration
- Night reading mode with reduced blue light
- Widget support for home screen

## License

This project is for educational and personal use.

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## Contact

For questions or suggestions, please open an issue in the repository.

---

**Built with ❤️ for the Muslim community**
