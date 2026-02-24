# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.4.1] - 2026-02-24

### Fixed
- Image preview orientation in edit mode now correctly displays landscape images
- ImageEditorActivity now uses EXIF-aware image loading to match final output orientation
- Preview and final saved image now show consistent orientation for all image types

## [1.4.0] - 2026-02-23

### Added
- Create Image feature for sharing ayahs as beautiful images with custom backgrounds
- New image creation icon (24dp) in ayah items for generating shareable images positioned after share icon
- Image picker with Camera and Gallery options for selecting background images
- ImageEditorActivity screen with draggable text positioning for customizing image layout
- Semi-transparent text background (50% black, 16dp rounded corners) for readability on any image background
- Automatic save to Pictures/Quran directory with MediaStore API for proper gallery integration
- Share functionality for generated images with Android share dialog
- Arabic text overlay using custom calligraphy font (uthmantaha.ttf) for authentic Quran text rendering
- Surah name and ayah number display on images below Arabic text
- FileProvider configuration for camera photo capture on Android 7+ (prevents FileUriExposedException)
- PermissionHelper utility for camera and storage permissions across different Android versions (API 23-34+)
- ImageOverlayHelper utility for Canvas-based image generation with text overlay
- Camera permission (android.permission.CAMERA) for taking photos with camera
- Read Media Images permission (android.permission.READ_MEDIA_IMAGES) for accessing gallery photos (Android 13+)
- Read External Storage permission (android.permission.READ_EXTERNAL_STORAGE) for accessing gallery photos (Android 12 and below)
- Write External Storage permission (android.permission.WRITE_EXTERNAL_STORAGE) for saving images (Android 9 and below)
- Scoped storage support for Android 10+ using MediaStore API with proper IS_PENDING flag handling
- Image scaling to max 2048x2048 pixels to prevent OutOfMemoryError with large images
- Progress indicator during image generation with AsyncTask background processing
- Text box padding (24dp) and positioning constraints to keep text within image bounds
- Multi-line text support with StaticLayout for proper Arabic text wrapping
- Maximum text width set to 80% of image width for proper readability

### Changed
- Updated ayah item layout to include create image icon positioned after share icon and before comment icon
- Extended AyahAdapter with OnCreateImageClickListener callback interface for image creation
- SurahDetailFragment now handles image picker and editor activity launches with ActivityResultLauncher
- Share icon constraint updated to position before create image icon instead of comment icon
- Comment icon constraint updated to position after create image icon instead of share icon

### Fixed
- Permission flow bug where camera/gallery would not launch immediately after granting permission on first attempt
- Replaced deprecated permission callback pattern with modern ActivityResultLauncher for reliable permission handling
- Image rotation bug where camera photos with EXIF orientation metadata would be misaligned with text overlay
- Implemented native ExifInterface (API 24+) to properly read and apply image rotation before text overlay
- Text overlay now correctly aligns with rotated camera images in all orientations (portrait, landscape, and flipped)

### Added Permissions
- CAMERA - For taking photos with device camera
- READ_MEDIA_IMAGES - For accessing gallery photos (Android 13+)
- READ_EXTERNAL_STORAGE - For accessing gallery photos (Android 12 and below)
- WRITE_EXTERNAL_STORAGE - For saving images (Android 9 and below)

## [1.3.0] - 2026-02-22

### Added
- Share functionality for individual ayahs to share through SMS, Bluetooth, or other installed apps
- Share icon (24dp filled) in ayah items positioned before comment and bookmark icons
- Share text includes Arabic text, English translation, ayah address (Surah name and ayah number), app name (Tilawah+), and GitHub repository link
- Android Intent chooser for selecting sharing method (SMS, Bluetooth, WhatsApp, email, etc.)
- Custom Arabic calligraphy font (`surah_names.ttf`) for displaying all 114 Surah names with beautiful glyphs
- SurahFontHelper utility class for managing surah font loading and unicode character mapping
- Unicode-based surah name display using Private Use Area characters (U+E900 to U+E972)
- Telegram-style overscroll navigation in Surah detail view for quick navigation between Surahs
- Pull down at bottom of Surah to navigate to next Surah (works for Surahs 1-113)
- Pull up at top of Surah to navigate to previous Surah (works for Surahs 2-114)
- Pull-to-reveal mechanism: page physically moves up/down during overscroll, revealing indicator in the space created
- Visual feedback indicators showing progress toward threshold (150dp) in revealed space
- Percentage display during overscroll with confirmation message when threshold is reached
- Haptic feedback vibration when navigation threshold is reached
- Smooth spring-back animation when releasing before threshold or canceling
- RecyclerView translation with background color to create physical pull effect
- Directional slide animations: next Surah slides up from bottom, previous Surah slides down from top
- Two separate navigation actions with matching animations for intuitive directional transitions

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
- Bookmark and comment views now show only surah name in address (ayah number removed to avoid redundancy since it appears in the ayah text)
- Comment dialog toolbar now has 32dp top margin to prevent title from overlapping with Android status bar icons
- Comment dialog button layout now has 32dp bottom margin to prevent save button from overlapping with Android navigation buttons
- Comment dialog title (Add Comment/Edit Comment) is now hidden for a cleaner interface
- Comment dialog toolbar background is now transparent with no elevation/shadow
- Comment dialog close button now uses primary color (green) for better visibility and consistency
- Bookmarks view title is now hidden for a cleaner, more spacious layout
- Comments view title is now hidden for a cleaner, more spacious layout
- RecyclerView content in bookmarks and comments now starts from the top of the screen

### Fixed
- Navigation bug where clicking comment icon from bookmarks view (and vice versa) did nothing
- Added missing navigation actions between bookmarksFragment and commentsFragment in nav_graph.xml
- Toolbar buttons now work correctly from all fragments including bookmarks and comments views
- Overscroll navigation bug where consecutive navigation in the same direction would alternate between two surahs
- surahNumber variable now updates when new surah data loads, ensuring correct sequential navigation
- Overscroll navigation race condition where navigation would sometimes fail to trigger
- Added overscroll direction tracking to prevent RecyclerView state changes from interfering with navigation
- Boundary checks now prevent overscroll indicators from appearing at first/last Surah

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
