# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1] - 2026-02-21

### Added
- Consistent toolbar at the top of all pages for settings and future action buttons
- Smooth slide-up/slide-down animations for all navigation transitions (Skip, I'm Feeling Lucky, Settings, Surah navigation)
- Dialog animations for settings panel

### Changed
- Increased Arabic text size in Ayah detail cards from 20sp to 24sp base size
- Updated Surah List header to use surface color instead of primary green color, matching app theme
- Reduced Surah List header height from 52dp to 12dp for a more compact design
- Redesigned Surah Detail header to match Surah List row style exactly (same layout, height, and appearance)
- Updated Surah Detail header and list item font sizes to be consistent (18sp for names, 16sp for numbers)
- Adjusted toolbar spacing with 24dp top margin to prevent overlap with status bar
- Reduced toolbar height from 56dp to 40dp for a more compact appearance

### Fixed
- Settings dialog crash caused by missing color resource reference (replaced removed orange color with primary color)
- Removed elevation/shadow from settings dialog in both light and dark themes

### Removed
- Unused image resource `surah_frame.png` (179KB)
- Unused color resources: `orange`, `orange_light`
- Unused string resources: `welcome_message`, `total_surahs`, `view_surahs`, `apply`, `cancel`, `loading`, `error`

### Optimized
- Enabled resource shrinking and code minification for release builds
- Reduced APK size through removal of unused resources

## [1.0] - Initial Release

### Added
- Native Android Quran reading application
- Access to all 114 Surahs with 6,236 verses
- Arabic text with English translations
- Offline access through local Room database
- Light and dark theme support
- Font size customization (S, M, L, XL)
- Language selection (English/Arabic)
- Random Ayah display on home screen
- Surah list with search and filtering
- Detailed Surah view with all verses
- Material Design UI with green theme
- GitHub repository link in settings
