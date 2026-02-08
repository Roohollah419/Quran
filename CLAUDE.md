# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application project named "Quran". The project was converted from a simple Java console application to an Android app that can be deployed to mobile devices.

## Build and Run Commands

**Build the project:**
```bash
gradlew.bat build
```

**Build APK:**
```bash
gradlew.bat assembleDebug
```

**Install on connected device:**
```bash
gradlew.bat installDebug
```

**Run from Android Studio:**
- Open the project in Android Studio
- Click the Run button or press Shift+F10
- Select your connected device or emulator
- The app will build, install, and launch automatically

## Project Structure

- `app/` - Main Android application module
  - `src/main/java/com/example/quran/` - Java source code
    - `MainActivity.java` - Main activity and entry point
  - `src/main/res/` - Android resources
    - `layout/` - UI layout XML files
    - `values/` - String resources, colors, styles
    - `mipmap-*/` - App launcher icons
  - `src/main/AndroidManifest.xml` - App manifest
  - `build.gradle` - App module build configuration
- `gradle/` - Gradle wrapper files
- `build.gradle` - Root build configuration
- `settings.gradle` - Project settings
- `src/Main.java` - Original console app (legacy, can be removed)

## Key Technical Details

- **Android SDK**: compileSdk 34, targetSdk 34, minSdk 24 (Android 7.0+)
- **Java Version**: Java 17 (sourceCompatibility and targetCompatibility)
- **Package Name**: com.example.quran
- **Build System**: Gradle 8.2 with Android Gradle Plugin 8.2.0
- **Dependencies**:
  - AndroidX AppCompat 1.6.1
  - Material Components 1.11.0
  - ConstraintLayout 2.1.4

## Deployment to Physical Device

1. **Enable Developer Options** on your phone:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times

2. **Enable USB Debugging**:
   - Go to Settings → Developer Options
   - Turn on "USB Debugging"

3. **Connect and Deploy**:
   - Connect phone via USB
   - Approve USB debugging prompt on phone
   - Run the app from Android Studio

## Architecture

The app uses standard Android Activity lifecycle with:
- **MainActivity**: Entry point that displays welcome message and demo content
- **activity_main.xml**: Main layout using ConstraintLayout with TextViews
- Material Design theme for consistent UI styling
