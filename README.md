# OpenArcade

![Version](https://img.shields.io/badge/version-1.0.2-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Android](https://img.shields.io/badge/Android-6.0%2B-brightgreen)
[![Donate via Liberapay](https://img.shields.io/liberapay/receives/kys0ff.svg?logo=liberapay)](https://liberapay.com/kys0ff/donate)

OpenArcade is a sophisticated, arcade-inspired game launcher meticulously crafted for Android. It
serves as a centralized command center for your entire mobile gaming collection, blending a refined
aesthetic with powerful customization and deep play-time insights.

## Recent Changes (1.0.2)

- **Fast Launch**: New "Skip Game Details" toggle for immediate gaming.
- **Tag Management**: Easier library organization via context menus.
- **Infrastructure**: Major architectural refactor to feature-based modules for better performance.
- **Licenses**: New dynamic open-source attribution screen.

## Core Features

- **Intelligent Game Discovery**: Seamlessly identifies and aggregates all installed titles into a
  unified, high-performance library.
- **Granular Categorization**: Organize your collection with precision using bespoke categories
  tailored to your gaming habits.
- **Advanced Play-time Analytics**: Gain profound insights into your gaming patterns with
  comprehensive, data-driven usage statistics.
- **Bespoke Personalization**: Complete creative control over your library, allowing you to redefine
  game titles and curate custom iconography.
- **Immersive Dynamic Theming**: An adaptive interface that extracts the essence of your game
  artwork to create a cohesive, color-aware visual experience.
- **High-Fidelity Navigation**: Experience fluid, cinematic transitions as you move between the
  library, deep-dive detail views, and system settings.

## Technical Architecture

OpenArcade leverages a cutting-edge technical stack to ensure performance, stability, and
maintainability:

- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Powers a fully declarative,
  reactive UI layer for maximum responsiveness.
- **[Koin](https://insert-koin.io/)** - Facilitates a lightweight and efficient dependency injection
  framework.
- **[Room](https://developer.android.com/training/data-storage/room)** - Provides a robust,
  ACID-compliant local database for managing extensive game data and analytics.
- **[Voyager](https://voyager.adriel.cafe/)** - Implements a pragmatic and flexible navigation
  architecture specifically designed for Compose.
- **[Coil](https://coil-kt.github.io/coil/)** - Ensures high-performance, lifecycle-aware image
  loading and caching.
- **[Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)** - Drives
  asynchronous operations and reactive data streams for a non-blocking user experience.
- **[Palette API](https://developer.android.com/develop/ui/views/graphics/palette)** - Delivers a
  modern design language with sophisticated color extraction for dynamic interface adaptation.

## Getting Started

### Prerequisites

- Android Studio Ladybug (or newer)
- Android SDK 37 (Target)
- Minimum Android Version: Android 6.0 (API 23)

### Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/kys0ff/OpenArcade.git
   ```
2. Open the project in Android Studio.
3. Synchronize the project with Gradle files.
4. Deploy the `app` module to a target device or emulator.

## Releases

- **[1.0.2](https://github.com/kys0ff/OpenArcade/releases/tag/v1.0.2)** - Architecture refactor, fast-launch settings, and enhanced tag management.
- **[1.0.0](https://github.com/kys0ff/OpenArcade/releases/tag/v1.0.0)** - The inaugural release
  featuring core library management, automated play-time tracking, and adaptive dynamic theming.

## Support & Donation

If you find OpenArcade useful, please consider supporting the project:

- **Liberapay**: [Donate to kys0ff](https://liberapay.com/kys0ff/donate)

## License

This project is licensed under the MIT License. Detailed terms can be found in
the [LICENSE](LICENSE) file.