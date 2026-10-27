# ColorOS 17 & One UI 9 Liquid Glass Overhaul

A comprehensive visual and interactive reimagining of OpenArcade into a flagship liquid glassmorphic gaming dashboard, blending ColorOS 17's fluid aquamorphic lighting with One UI 9's refined squircle bento ergonomics and dynamic palette refraction.

## User Review & Critical Decisions

> [!IMPORTANT]
> The design direction has been tailored to your confirmed preferences:
> - **Aquamorphic Fluid Theme**: ColorOS 17 inspired ambient atmospheric mesh glows and aquatic refractions over a deep dark canvas.
> - **Dynamic Glass Palette**: Every card and widget dynamically absorbs the extracted vibrant color profile of the individual game/app icon.
> - **Fluid Spring Physics**: Bouncy organic spring animations and specular rim lighting that shines across glass bevels on touch.
> - **Scrollbar Configuration**: Scrollbars are disabled by default and the scrollbar toggle is hidden from the Settings menu.

---

## 1. Overview & Core Concept

OpenArcade is an on-device game launcher and telemetry center for Android. While the existing feature set (game library scanning, usage telemetry, tagging, filtering, renaming, sorting, and launching) is fully intact, the entire presentation layer will be rebuilt to look and feel like a modern flagship operating system UI:

- **Liquid Glass Materials**: Custom composable glass surfaces with multi-stop refractive specular borders, translucent frosted backdrops, and subtle top-edge rim lighting.
- **Ambient Mesh Atmosphere**: Subtle dynamic background luminescence that shifts and breathes behind the content, creating a sense of physical light passing through colored liquid glass.
- **One UI 9 Bento Hierarchy**: Generously rounded continuous squircle containers (24dp to 32dp), glanceable metric widgets, and tactile spring-loaded interactions.

---

## 2. User Experience & Visual Design

### Visual Identity & Theme

```
┌────────────────────────────────────────────────────────────────────────┐
│  COLOROS 17 & ONE UI 9 LIQUID GLASS DESIGN SYSTEM                      │
├────────────────────────────────────────────────────────────────────────┤
│  Canvas:        Deep Midnight Obsidian (#0B0F19 ~ #07090E)             │
│  Ambient Glow:  Electric Cyan (#00E5FF), Sapphire (#3D7BFF),           │
│                 Emerald Aurora (#00F5A0), Radiant Orchid (#B388FF)     │
│  Glass Surface: Translucent frosted acrylic (8% to 14% white/color)    │
│  Specular Rim:  Linear bevel gradient: Top-Left 80% opacity shine to   │
│                 bottom-right 5% soft dissipation                       │
│  Squircles:     Continuous curved corners: 24dp (cards) / 32dp (hero)  │
│  Typography:    Modern high-contrast geometric headers with rounded    │
│                 subtitles and neon-accented telemetry pills            │
└────────────────────────────────────────────────────────────────────────┘
```

### Component Transformations

1. **Ambient Caustic Background**:
   - A smooth background canvas rendering floating, softly blurred radial light blooms that interact harmoniously with the dark backdrop.
2. **Liquid Glass Cards (`LiquidGlassCard`)**:
   - Dual-layer glass structure:
     - Base: Frosted translucent container with subtle inner tint.
     - Specular Edge: Multi-stop linear border highlighting the top-left edge as if illuminated by an overhead light source.
     - Top Sheen: A faint horizontal light reflection line along the top interior edge simulating physical glass thickness.
     - Dynamic Adaptation: When displaying a game, the glass tint and specular reflections shift to match that game's palette.
3. **Hero Banner & Carousel**:
   - Translucent floating capsule with panoramic blurred backdrop glow, fluid pagination pills, and a floating glass action button with tactile spring bounce.
4. **Interactive Filter & Search Pod**:
   - ColorOS inspired floating search capsule with integrated glass filter pills that smoothly expand and glow on selection.
5. **System & Analytics Bento Widgets**:
   - Rounded bento tiles displaying battery level, memory, and play-time statistics with glowing circular progress rings and gradient charts.
6. **Game Library Grid Cards**:
   - Squircle glass cards featuring icon pop-out depth, subtle hover/press scale spring (0.95f scaling), and glowing status badges.
7. **Settings Screen Reorganization**:
   - Clean One UI bento groupings. The "Show Scrollbar" option is removed from the visible UI and defaults to disabled across the entire app.

---

## 3. Key Product Decisions & Trade-Offs

- **Pure Compose Shader & Canvas Effects vs. Heavy Native Blurs**:
  - *Chosen Approach*: Implement frosted glass and liquid highlights using Compose's native `Brush`, `drawWithContent`, `Modifier.drawBehind`, and layered alpha composites.
  - *Why*: Ensures 60/120fps fluid scrolling performance on all Android devices and emulators without triggering expensive hardware GPU render-to-texture pipeline stalls.
- **Scrollbar Default & Visibility**:
  - *Chosen Approach*: Change `_showScrollbar` default to `false` in `ArcadePreferences` and remove the setting row from `SettingsScreen`.
  - *Why*: Directly aligns with user requirements while keeping the underlying preference capability intact if ever needed.
- **Zero Feature Loss Guarantee**:
  - All existing features (game launching, tag dialogs, renaming, custom icons, sort options, section toggles, system stats, usage analytics) remain 100% operational with identical data flows and ViewModel contracts.

---

## 4. Technical Architecture & Component Structure

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           UI HIERARCHY                                  │
└─────────────────────────────────────────────────────────────────────────┘
                                   │
               ┌───────────────────┴───────────────────┐
               ▼                                       ▼
    ┌─────────────────────┐                 ┌─────────────────────┐
    │ AmbientGlassCanvas  │                 │ LiquidGlassCard     │
    │ (Mesh glow & light) │                 │ (Specular rim light)│
    └─────────────────────┘                 └─────────────────────┘
               │                                       │
     ┌─────────┴─────────┬───────────────────┬─────────┴─────────┐
     ▼                   ▼                   ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│  HeroBanner  │   │  FilterPods  │   │  BentoTiles  │   │ GameGridCard │
│  (Carousel)  │   │  (Squircles) │   │ (Analytics)  │   │  (Adaptive)  │
└──────────────┘   └──────────────┘   └──────────────┘   └──────────────┘
```

### Planned File Adjustments

- **`core/ui/theme/Color.kt` & `Theme.kt`**: ColorOS 17 aquamorphic neon and obsidian palette tokens.
- **`core/ui/components/ArcadeCard.kt` -> `LiquidGlassCard.kt`**: Specular border gradients, inner liquid sheen, press scale springs.
- **`core/ui/components/ArcadeToggle.kt` & `ArcadeButton.kt`**: Liquid capsule switches and fluid pill buttons.
- **`core/data/local/preferences/ArcadePreferences.kt`**: Set default `_showScrollbar` to `false`.
- **`features/settings/ui/SettingsScreen.kt`**: Remove scrollbar setting row and apply One UI 9 bento styling.
- **`features/games_launcher/ui/`**: Apply liquid glass styling to Hero Banner, Library Grid, Filter Chips, System Status, and Analytics widgets.
