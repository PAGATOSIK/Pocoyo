<div align="center">

  <img src="https://raw.githubusercontent.com/PAGATOSIK/Pocoyo/main/src/main/resources/assets/pocou/icon.png" width="128" height="128" alt="PocoyoVisual Logo" />

# Pocoyo Visual (Mod-Client)

**A modern, lightweight, and customizable Visual Client Mod for Minecraft**

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.8-brightgreen.svg?style=for-the-badge&logo=minecraft)](https://minecraft.net)
[![Mod Loader](https://img.shields.io/badge/Modloader-Fabric-informational.svg?style=for-the-badge&logo=fabric)](https://fabricmc.net)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE.txt)

  ---

  <p align="center">
    <b>PocoyoVisual</b> enhances your Minecraft gameplay with slick HUD customization, smooth visual tweaks, and performance improvements — built from the ground up for <b>Fabric 1.21.8</b>.
  </p>

</div>

---

## Features

*  **Slick Custom HUD**: Fully customizable overlay for FPS, CPS, Ping, Direction, Armor status, and Key strokes.
* **FPS & Performance Optimization**: Fine-tuned rendering tweaks for higher frame rates and buttery smooth gameplay.
* **Visual Customization**: Custom crosshairs, particle effects, hit animations, and color schemes.
* **User-Friendly GUI**: In-game menu designed for seamless configuration without restarting your game.
*  **Fabric Native**: Extremely lightweight and fully compatible with most popular Fabric performance mods (Sodium, Lithium, FerriteCore, etc.).

---

## Prerequisites

Before installing, make sure you have the following installed:

* **Minecraft Java Edition** (`1.21.8`)
* **Fabric Loader** (`0.19.5`)
* **Java Development Kit (JDK 21)** (Required for building from source)

---

## Changelog

### Version 0.3
* **Bug Fixes**:
  * Fixed Cape module rendering to properly display custom cape texture
  * Fixed JumpCircle module texture path and rendering
  * Fixed rounded element anti-aliasing (AA_SAMPLES) for smoother visuals
  * Fixed ClickGUI theme buttons becoming unclickable after scrolling
  * Fixed HUD renderers to consistently use theme colors
* **New Features**:
  * Added WorldTime module for visual time manipulation
  * Added Combo counter with animated HUD display
  * Added EffectListRenderer for potion effects display
  * Merged theme and effect color selections into unified Theme category
  * Redesigned ClickGUI with improved layout and styling
* **Improvements**:
  * Performance optimizations for GUI rendering
  * Enhanced theme system with separate interface and effect colors
  * Improved scissor clipping for theme section
  * Better scroll offset handling in ClickGUI

### Version 0.2
* Initial release with basic visual modules and HUD system

---

##  Installation

1. Download and install [Fabric Loader](https://fabricmc.net/use/) for Minecraft **1.21.8**.
2. Download the latest release of **PocoyoVisual** from the [Releases](https://github.com/PAGATOSIK/Pocoyo/releases) tab.
3. Place the `.jar` file into your `.minecraft/mods` directory.
4. Launch Minecraft using the **Fabric** profile!

---

## Building from Source

If you want to build **PocoyoVisual** yourself, follow these steps:

### 1. Clone the Repository
```bash
git clone [https://github.com/PAGATOSIK/Pocoyo.git](https://github.com/PAGATOSIK/Pocoyo.git)
cd Pocoyo
