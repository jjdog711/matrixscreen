# Matrix Digital Rain

An authentic Matrix digital rain effect for Android, built with Kotlin and Jetpack Compose. Features discrete character printing, real-time live settings, advanced color customization, Matrix-style splash screen, and movie-accurate visual effects.

## Features

### Authentic Matrix Digital Rain
- **Discrete Character Printing**: Characters print row-by-row at discrete positions, mimicking terminal-style output
- **Multi-level Visual Effects**: 4-tier brightness system with glow, jitter, flicker, and mutation effects
- **Authentic Speed Distribution**: Movie-accurate speed patterns - mostly contemplative with occasional fast bursts
- **Bright Trail System**: Random bright trail lengths (2-15 characters) behind the printing head
- **Efficient Grain Overlay**: High-performance tiled noise bitmap with subtle animated drift (4.5s X, 6.1s Y cycles)
- **Immersive Fullscreen**: Edge-to-edge display with hidden system bars
- **Hot-swap Settings**: All settings apply live without animation restart - only "Reset All" requires restart

### Live Settings System ⚡
- **Real-time Application**: All settings apply instantly without animation restart
- **Live Preview**: See changes immediately while editing in the overlay
- **Smooth Navigation**: Swipe gestures for seamless settings navigation
- **Smart Auto-confirm**: Regular symbol sets auto-confirm, custom sets require manual confirmation
- **No Restart Required**: Only "Reset All" requires restart - everything else applies live

### Matrix-Style Splash Screen
- **Blinking "MATRIX" Text**: Authentic Matrix-style loading screen with blinking animation
- **Gradient Background**: Deep green-black gradient for atmospheric effect
- **Loading Animation**: Animated dots with staggered timing
- **Auto-dismiss**: 3-second splash screen with smooth transition to main effect

### Modern Settings Architecture
- **6-Category Navigation**: Organized settings into Theme, Characters, Motion, Effects, Timing, and Background
- **Type-Safe Settings**: Full compile-time type safety with `SettingId<T>` constants
- **Domain Model**: Clean separation between UI and business logic
- **Registry System**: Extensible symbol sets and theme presets
- **Live Settings**: All changes apply instantly without animation restart

### Symbol Sets
- **Matrix Authentic** (default): Latin + half-width Katakana + Matrix glyphs (200+ characters)
- **Matrix Glitch**: Matrix Authentic + glitch symbols and custom glyphs (220+ characters)
- **Latin**: A-Z, a-z, 0-9 (62 characters)
- **Katakana**: Full-width Japanese Katakana (85 characters)
- **Numbers**: 0-9 only (10 characters)
- **Mixed**: Latin + Katakana combination (147 characters)
- **Binary**: 0 and 1 only (2 characters)
- **Hexadecimal**: 0-9, A-F (16 characters)
- **Custom**: User-created symbol sets with custom fonts and character weighting

### Custom Symbol Sets 🔥
- **Create & Edit**: Design your own symbol sets with any characters you want
- **Character Weighting**: Repeat characters to increase their frequency (e.g., "AAAABBBBCC" = 40% A, 40% B, 20% C)
- **Custom Fonts**: Choose from available fonts or use system fonts for your symbol sets
- **Live Preview**: See your characters rendered in real-time as you type
- **Save & Reuse**: Create multiple named custom sets and switch between them
- **Font Picker**: Horizontal scrollable font selection with live preview
- **Smart Toggle**: Intelligent custom set activation - no unnecessary navigation
- **Character Validation**: Real-time character count and validation (max 2000 characters)
- **Persistent Storage**: All custom sets saved automatically with DataStore
- **Keyboard Dismissal**: Tap anywhere outside text fields to dismiss keyboard
- **Intuitive Weighting**: Simply repeat characters in your input string to control their frequency

### Advanced Color System 🎨
- **Theme Presets**: 6 built-in themes (Matrix Green, Blue, Red, Purple, Orange, White)
- **Advanced Mode**: Full color customization with individual control over:
  - Rain head color (brightest characters)
  - Bright trail color (characters behind head)
  - Trail color (medium brightness)
  - Dim trail color (fading characters)
  - UI accent color (settings overlay)
  - Background color (canvas background)
  - UI overlay background
  - UI selection background
- **Live Color Preview**: See color changes instantly in the animation
- **Color Picker Dialog**: Full HSV color picker with hex input
- **UI/Rain Color Linking**: Option to link UI colors to rain colors for cohesive themes
- **Color Resolution Pipeline**: Theme presets and advanced logic applied before rendering

### Custom Font Picker 🎨
- **Font Discovery**: Automatically scans `assets/fonts/` for available `.ttf` files
- **System Fonts**: Includes system monospace and default fonts as options
- **Live Preview**: See your characters rendered in the selected font immediately
- **Font Caching**: Optimized font loading with intelligent caching
- **Fallback Support**: Graceful fallback to system fonts if custom fonts fail
- **Display Names**: User-friendly font names (e.g., "Matrix Code NFI", "Space Grotesk", "Digital 7")
- **Performance Optimized**: Efficient font loading and memory management

## Default Settings

| Setting         | Default   | Range        | Description                   |
|-----------------|-----------|--------------|-------------------------------|
| Fall Speed      | 200%      | 50%-500%     | Base speed multiplier         |
| Font Size       | 14dp      | 8-24dp       | Character size                |
| Column Count    | 150       | 50-150       | Number of Matrix columns      |
| Target FPS      | 60fps     | 15-60fps     | Animation frame rate          |
| Line Spacing    | 90%       | 70%-120%     | Vertical spacing multiplier   |
| Trail Length    | 100 chars | 20-100 chars | Maximum trail length          |
| Bright Trail    | 15 chars  | 2-15 chars   | Bright characters behind head |
| Glow Intensity  | 200%      | 0%-200%      | Glow effect strength          |
| Jitter Amount   | 2px       | 0-3px        | Horizontal jitter movement    |
| Flicker Rate    | 20%       | 0%-20%       | Character flicker frequency   |
| Mutation Rate   | 8%        | 0%-10%       | Character change frequency    |
| Start Delay     | 0.01s     | 0-10s        | Column start delay            |
| Restart Delay   | 0.5s      | 0.5-5s       | Column restart delay          |
| Active %        | 40%       | 10%-80%      | Initial active columns        |
| Speed Variation | 1%        | 0-10%        | Runtime speed changes         |
| Grain Density   | 200       | 0-500        | Grain texture points          |
| Grain Opacity   | 3%        | 0%-10%       | Grain texture opacity         |

## Requirements

- **Android**: 5.0+ (API 21)
- **Java**: 17
- **Android Studio**: Koala (2024.1) or later
- **Gradle**: 8.11.1
- **Kotlin**: 1.9.22
- **Compose BOM**: 2024.10.01
- **Theme**: Material3 (no AppCompat dependency)
- **Build**: R8 minification and resource shrinking enabled for release builds

## How to Use

### Basic Usage
1. **Launch**: App starts with Matrix splash screen
2. **Settings**: Double-tap anywhere or swipe up to open settings
3. **Navigate**: Use the settings home screen to access different categories
4. **Live Preview**: All changes apply instantly to the background animation
5. **Confirm**: Regular settings auto-save, use Confirm/Cancel for custom symbol sets

### Advanced Color Customization
1. **Access**: Settings → Theme
2. **Presets**: Choose from 6 built-in theme presets
3. **Advanced Mode**: Toggle to Advanced for full color control
4. **Customize**: Adjust individual color components (head, trail, UI, background)
5. **Live Preview**: See changes instantly in the Matrix rain
6. **Color Picker**: Tap any color to open full HSV picker with hex input

### Custom Symbol Sets
1. **Access**: Settings → Characters → Toggle "Custom Symbol Sets"
2. **Create**: Tap "CREATE NEW SET" button
3. **Name**: Enter a descriptive name for your symbol set
4. **Characters**: Type any characters you want (max 512)
   - **Character Weighting**: Repeat characters to increase frequency
     - Example: `"AAAABBBBCC"` = 40% A, 40% B, 20% C
     - Example: `"LOVE"` = 25% each of L, O, V, E
     - Example: `"LOVELOVELO💀💀☠"` = Lots of L, O, V, E, occasional 💀, rare ☠
5. **Font**: Select from available fonts using the horizontal picker
6. **Preview**: See your characters rendered in real-time
7. **Save**: Tap "CREATE SET" to save
8. **Activate**: Select your custom set to make it active

### Font Management
- **Add Fonts**: Place `.ttf` files in `app/src/main/assets/fonts/`
- **Auto-Discovery**: App automatically detects and lists available fonts
- **System Fonts**: Includes system monospace and default as options
- **Live Preview**: See font changes immediately in the editor

## Build & Run

### From Android Studio
1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on device/emulator

### From Command Line
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (with R8 minification)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test

# Clean build
./gradlew clean
```

### APK Outputs
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk` (signed, ready to install)
- **Release APK**: `app/build/outputs/apk/release/app-release-unsigned.apk` (unsigned, requires signing for distribution)

## Performance

- **Target**: 60fps with frame rate limiting
- **Live Settings**: All settings apply without animation restart
- **Optimizations**: Pre-allocated Paint objects, viewport culling, lifecycle-aware animation
- **Memory**: Object pooling and efficient character management
- **Grain Overlay**: High-performance tiled bitmap with no per-frame Paint allocation
- **Hot-swap**: Animation loop never restarts - only reads fresh config values each frame
- **Release Builds**: R8 minification and resource shrinking for optimal APK size
- **Tips**: Use release builds for best performance, disable animations when debugging

## Technical Implementation

### Modern Architecture
- **Domain Model**: Clean separation between UI and business logic with `MatrixSettings`
- **Type Safety**: Full compile-time safety with `SettingId<T>` constants
- **Registry System**: Extensible symbol sets and theme presets
- **Color Resolution Pipeline**: Theme presets and advanced logic applied before rendering
- **Live Settings**: All settings apply instantly without animation restart

### Live Settings Architecture
- **Domain Model**: `MatrixSettings` with 32+ configurable parameters
- **Type-Safe Access**: `settings.get(SettingId)` and `settings.with(SettingId, value)`
- **No Restart Semantics**: Only "Reset All" requires restart, all other settings apply live
- **Smart Auto-confirm**: Regular symbol sets auto-save, custom sets require manual confirmation
- **Real-time FPS**: Target frame rate adjusts live without effect restart
- **Custom Set Switching**: Immediate character set updates when switching between custom symbol sets

### Custom Symbol Sets Architecture
- **Data Model**: `CustomSymbolSet` with `id`, `name`, `characters`, `fontFileName`
- **Persistence**: DataStore with JSON serialization using kotlinx.serialization
- **Character Weighting**: Raw string conversion to `CharArray` preserves repetitions
- **Font System**: `MatrixFontManager` with font discovery, caching, and fallback support

### Advanced Color System
- **Theme Presets**: 6 built-in themes with registry system
- **Color Resolution**: Pipeline applies presets, advanced logic, and UI/Rain linking
- **Live Application**: Color changes apply instantly to animation
- **Smart Generation**: Automatic color derivation when switching modes

### Key Components
- **SettingsNavGraph**: 6-category navigation (Theme, Characters, Motion, Effects, Timing, Background)
- **CustomSymbolSetsScreen**: Management UI with list, edit, delete functionality
- **CreateOrEditSymbolSetScreen**: Editor with live preview and font picker
- **MatrixFontManager**: Font discovery, loading, caching, and fallback system
- **NewSettingsViewModel**: Type-safe settings management with domain model
- **ResolveColors**: Color resolution pipeline for theme presets and advanced logic

### Font System
- **Discovery**: Async scanning of `assets/fonts/` directory
- **Loading**: Temporary file creation for Typeface loading
- **Caching**: In-memory cache to prevent repeated font loading
- **Fallback**: Custom font → Matrix font → System monospace chain

## Fonts & Assets

### Font System
The app features a comprehensive font system with automatic discovery and custom font support:

**Built-in Fonts**:
- **Matrix Code NFI**: Authentic Matrix-style font (if available)
- **System Monospace**: Clean, readable monospace font
- **System Default**: Standard system font
- **Custom Fonts**: Any `.ttf` files placed in `app/src/main/assets/fonts/`

**Font Discovery**: The app automatically scans the fonts directory and makes all available fonts selectable in the custom symbol set editor.

### Adding Custom Fonts
1. Place any `.ttf` font file in `app/src/main/assets/fonts/`
2. The app will automatically detect and list it in the font picker
3. Use the font in your custom symbol sets for personalized Matrix effects

**Sample Fonts Included**:
- `matrix_code_nfi.ttf` - Matrix-style (default)
- `digital_7.ttf` - LED-style
- `orbitron.ttf` - Futuristic
- `cascadia_mono.ttf` - Clean monospace
- `jetbrains_mono_regular.ttf` - Developer-friendly monospace
- `jetbrains_mono_light.ttf` - Light weight variant
- `space_grotesk_regular.ttf` - Modern sans-serif
- `space_grotesk_medium.ttf` - Medium weight variant
- `space_grotesk_semibold.ttf` - Semi-bold variant

### Font Fallback System
- **Custom Symbol Sets**: Use selected custom font with fallback chain
- **Regular Symbol Sets**: Matrix Code NFI → Noto Sans JP → System monospace
- **Performance**: Intelligent font caching and efficient loading

**Legal Note**: Matrix Code NFI is generally free for non-commercial use but not suitable for commercial redistribution without proper licensing. For commercial releases, consider using open-source alternatives like Noto Sans JP + monospace fonts.

## Screenshots

*Screenshots will be added to `/docs/images/` directory. Use Android Studio's built-in screen recorder or device screenshot functionality to capture the Matrix effect.*

## Roadmap

- [x] Custom character set editor ✅
- [x] Custom font picker with font discovery ✅
- [x] Character weighting by repetition ✅
- [x] Live settings application ✅
- [x] Advanced color system ✅
- [x] Real-time FPS adjustment ✅
- [x] Hot-swap settings system ✅
- [x] Efficient grain overlay with tiled bitmap ✅
- [x] De-AppCompat theme migration ✅
- [x] R8 minification and resource shrinking ✅
- [x] Custom symbol set switching fix ✅
- [x] Space Grotesk font support ✅
- [x] Modern settings architecture ✅
- [x] Type-safe settings system ✅
- [x] Theme preset system ✅
- [x] Color resolution pipeline ✅
- [x] Registry system for extensibility ✅
- [ ] Add open-source font pack for commercial use
- [ ] Implement preset save/load functionality
- [ ] Add settings export/import feature
- [ ] Performance profiling for low-end devices
- [ ] Optional "rain density" control
- [ ] Background music integration
- [ ] Widget support for home screen
- [ ] Font import from device storage
- [ ] Custom symbol set sharing/export

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Issue Templates
Consider creating issue templates in `/.github/` for bug reports and feature requests.

## Changelog

### v1.1 (Latest)
- **Modern Settings Architecture**: Complete rewrite with 6-category navigation and type-safe settings
- **Domain Model**: Clean separation between UI and business logic with `MatrixSettings`
- **Type Safety**: Full compile-time safety with `SettingId<T>` constants and `WidgetSpec<T>`
- **Registry System**: Extensible symbol sets and theme presets with `SymbolSetRegistry` and `ThemePresetRegistry`
- **Color Resolution Pipeline**: Theme presets and advanced logic applied before rendering
- **Theme Presets**: 6 built-in themes (Matrix Green, Blue, Red, Purple, Orange, White)
- **UI/Rain Color Linking**: Option to link UI colors to rain colors for cohesive themes
- **Live Settings**: All settings apply instantly without animation restart
- **Custom Symbol Sets**: Create and edit your own symbol sets with any characters
- **Character Weighting**: Repeat characters to control their frequency in the Matrix rain
- **Custom Font Picker**: Choose from available fonts or system fonts for your symbol sets
- **Font Discovery**: Automatic scanning and listing of available `.ttf` fonts
- **Live Font Preview**: See your characters rendered in real-time with selected fonts
- **Smart Toggle**: Intelligent custom set activation without unnecessary navigation
- **Persistent Storage**: All custom sets saved automatically with DataStore
- **Enhanced UI**: Cyberpunk-themed font picker with horizontal scrolling
- **Performance**: Optimized font loading with intelligent caching
- **UX Improvements**: Keyboard dismissal on outside tap for better editing experience

### v1.0 (Initial Release)
- Initial release with authentic Matrix digital rain effect
- Matrix-style splash screen with blinking animation
- Real-time settings overlay with live preview
- 8 symbol sets including Matrix Authentic and Matrix Glitch
- 7 color themes with authentic Matrix green default
- Performance optimizations for 60fps targeting
- Hybrid font system with Matrix Code NFI support
- Comprehensive settings with 20+ customizable parameters

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Note**: This README reflects the current state of the codebase as of the latest analysis. All information is derived from actual source code and configuration files.