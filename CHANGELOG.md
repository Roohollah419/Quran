# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.3.0] - 2026-02-22

### Added
- Share functionality for individual ayahs to share through SMS, Bluetooth, or other installed apps
- Share icon (24dp filled) in ayah items positioned before comment and bookmark icons
- Share text includes Arabic text, English translation, ayah address (Surah name and ayah number), app name (Tilawah+), and GitHub repository link
- Android Intent chooser for selecting sharing method (SMS, Bluetooth, WhatsApp, email, etc.)
- Custom Arabic calligraphy font (`surah_names.ttf`) for displaying all 114 Surah names with beautiful glyphs
- SurahFontHelper utility class for managing surah font loading and unicode character mapping
- Unicode-based surah name display using Private Use Area characters (U+E900 to U+E972)

### Changed
- Updated ayah item layout to include share icon with proper constraint positioning
- Extended AyahAdapter to handle share icon click events with formatted share text
- Share icon uses primary color for filled appearance consistent with app theme
- Share text format now promotes the app with "Shared from Tilawah+" and GitHub link (https://github.com/Roohollah419/Quran)
- Bookmark and comment outline icons now use primary color (green) instead of text_secondary for better visibility in both light and dark themes
- All ayah action icons (share, comment, bookmark) now consistently use primary color regardless of their state
- All Arabic surah names throughout the app now display with custom calligraphy font for enhanced visual appeal
- Surah list, surah detail header, bookmarks, comments, and home screen now show decorative Arabic calligraphy for surah names
- Surah name font size increased by 1.5x compared to other text for better prominence and readability
- Share functionality continues to use plain text Arabic names for better compatibility across apps
- Comment dialog preserves plain text surah names for readability in dialog context

## [1.2.0] - 2026-02-22

### Added
- Bookmarks feature for saving favorite ayahs for quick access
- Bookmark icon (24dp outlined/filled) in ayah items that toggles between outline and filled state
- Bookmarks toolbar button (40dp filled icon) for quick access to all bookmarked ayahs
- Bookmarks list view showing all bookmarked ayahs with Arabic text, translation, and address
- Navigation from bookmarks list to specific ayah in surah detail view with automatic scrolling
- BookmarkManager utility class for managing bookmark storage using SharedPreferences
- Comments feature for adding personal notes to any ayah
- Comment icon (24dp outlined/filled) in ayah items that toggles between outline and filled state based on whether a comment exists
- Comment dialog for entering and editing comment text with Save and Delete buttons (max 500 characters)
- Comments toolbar button (40dp filled icon) positioned between bookmarks and settings for quick access to all comments
- Comments list view showing all commented ayahs with Arabic text, translation, address, and comment text
- Navigation from comments list to specific ayah in surah detail view with automatic scrolling
- Edit and delete functionality for comments directly from the comments list
- CommentManager utility class for managing comment storage using SharedPreferences with JSON serialization
- Theme-aware comment text display with subtle background and border styling
- Full test coverage for CommentManager and CommentsViewModel (80%+ line coverage maintained)

### Changed
- Updated ayah item layout to include bookmark and comment icons
- Extended AyahAdapter to handle bookmark and comment icon clicks and state updates
- Updated toolbar layout in activity_main.xml to include bookmarks and comments buttons
- Extended navigation graph with bookmarksFragment and commentsFragment destinations and navigation actions
- Updated ViewModelFactory to register BookmarksViewModel and CommentsViewModel for dependency injection

### Fixed
- Bookmark icon state synchronization in bookmark list view
- Navigation from bookmarked ayah to correct position in surah detail view

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
