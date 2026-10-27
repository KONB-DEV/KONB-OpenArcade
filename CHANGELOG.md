# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.2] - 2026-07-03

### Added
- "Skip Game Details" fast-launch toggle in settings.
- Direct game tag management from the library context menu.
- Dynamic Open-Source Licenses screen.
- GitHub and F-Droid specific build flavors.

### Changed
- Migrated architecture to a scalable feature-based layout.
- Synchronized launcher state initialization with AppViewModel for improved stability.
- Hoisted initial data load to global AppViewModel to optimize lifecycle scoping.
- Refactored utility objects into Koin-injected repositories.
- Improved UI responsiveness with immutable collections and optimized state streams.
- Replaced game visibility toggling with explicit removal for better clarity.

## [1.0.0] - 2026-06-25

### Added
- **Initial Release**
- Core launcher functionality with game detection.
- Game details screen with customizable metadata (titles, categories, icons).
- Play-time tracking and analytics dashboard.
- Dynamic color themes based on game artwork using Palette API.
- Search and filtering capabilities for the game library.
- Custom categories support for manual game organization.
- Background service for monitoring app usage and updating statistics.
- Dependency injection setup using Koin.
- Local persistence using Room Database.
- Navigation implementation using Voyager.
