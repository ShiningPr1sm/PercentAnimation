<div align="center">
  
# % / PercentAnimation

[![GitHub stars](https://img.shields.io/github/stars/ShiningPr1sm/PercentAnimation?style=flat-square)](https://github.com/ShiningPr1sm/PercentAnimation/stargazers)
[![GitHub last commit](https://img.shields.io/github/last-commit/ShiningPr1sm/PercentAnimation?label=last%20update&style=flat-square)](https://github.com/ShiningPr1sm/PercentAnimation/commits)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/ShiningPr1sm/PercentAnimation?label=version&style=flat-square)](https://github.com/ShiningPr1sm/PercentAnimation/releases)

<img width="800" height="450" alt="ezgif-6b80d9b8b98b32aa" src="https://github.com/user-attachments/assets/deffae28-84df-475d-8a44-10333d80f833" />
</div>

> A lightweight Java library for creating smooth, dynamic console animations like progress bars and spinners with ANSI color gradients.

---

## Overview
PercentAnimation solves the problem of boring, static terminal outputs in Java console applications. Instead of text walls, it provides a simple way to display real-time, visually appealing progress tracking. Built purely in Java without heavy external dependencies, it leverages ANSI escape codes to render smooth animations and vibrant color transitions directly in the terminal.

## Key Features
* **Dynamic Gradients**: Smooth ANSI color transitions for progress percentages and console output.
* **Console Spinners**: Easily manageable asynchronous loading animations for background tasks.
* **Zero Dependencies**: Lightweight footprint, making it effortless to integrate into any Java project.
* **Thread-Safe**: Safe to use from multiple threads.
* **AutoCloseable**: Both progress bars and spinners support try-with-resources.
* **Builder API**: Fluent, readable configuration.

## Getting Started

### Prerequisites
* **Java Development Kit**: JDK 21+
* **Dependency Manager**: Maven (for dependency declaration)

### Download
* You can download a `.jar` file directly from the Releases section, or include it via [Maven](https://central.sonatype.com/artifact/dev.shiningpr1sm/PercentAnimation):

```xml
<dependency>
    <groupId>dev.shiningpr1sm</groupId>
    <artifactId>PercentAnimation</artifactId>
    <version>1.0.2</version>
</dependency>
```

## Usage

### ConsoleProgressBar

```java
ConsoleProgressBar bar = ConsoleProgressBar.builder()
    .total(100)
    .barLength(30)
    .style(Style.BLOCKS)
    .position(Position.RIGHT)
    .theme(ColorTheme.STANDARD)
    .build();

for (int i = 0; i <= 100; i++) {
    bar.update(i, "Downloading...");
}
bar.finish();
```

#### Progress Bar Styles
| Style | Progress | Remaining | Description |
|-------|----------|-----------|-------------|
| `BLOCKS` | `█` | `░` | Solid blocks layout |
| `STICKS` | `\|` | `.` | Vertical ticks layout |
| `BUBBLES` | `O` | `o` | Bubble indicators layout |
| `ARROW` | `>` | `-` | Forward-pointing arrow layout |

#### Progress Bar Positions
| Position | Description |
|----------|-------------|
| `LEFT` | `Message [50%] [████░░░░]` |
| `RIGHT` | `Message [████░░░░] [50%]` |
| `CENTER` | `Message [████ 50% ░░░░]` |

#### Progress Bar Color Themes
| Theme | Description |
|-------|-------------|
| `STANDARD` | Auto-selects based on style |
| `BLUE_GRADIENT` | Deep blue to sky blue gradient |
| `GREEN_GRADIENT` | Forest green to lime green gradient |
| `PURPLE_PINK_GRADIENT` | Violet to neon pink gradient |
| `ORANGE_YELLOW_GRADIENT` | Orange to electric yellow gradient |
| `NONE` | No colors applied |

### ConsoleSpinner

```java
ConsoleSpinner spinner = ConsoleSpinner.builder()
    .style(Style.CLASSIC)
    .position(Position.RIGHT)
    .theme(ColorTheme.STANDARD)
    .build();

spinner.start("Loading configuration...");
// ... do work ...
spinner.stop();
```

#### Spinner Styles
| Style | Frames | Delay | Description |
|-------|--------|-------|-------------|
| `CLASSIC` | `\| / - \` | 100ms | Standard spinning line |
| `DOTS` | `. .. ...` | 250ms | Horizontal progression dots |
| `TEXT_LOADING` | `lOADING ... LOADING` | 120ms | Sequential letter illumination |
| `GROWING` | ` ▃▄▅▆▇█▇▆▅▄▃` | 90ms | Vertical scaling blocks |

#### Spinner Positions
| Position | Description                         |
|----------|-------------------------------------|
| `LEFT`   | `[\|] Loading system configuration` |
| `RIGHT`  | `Loading system configuration [\|]` |

#### Spinner Color Themes
| Theme | Description |
|-------|-------------|
| `STANDARD` | Auto-selects based on style |
| `BLUE` | Cyan shifting theme |
| `GREEN` | Lime green shifting theme |
| `PURPLE_PINK` | Violet to pink color wave |
| `ORANGE_YELLOW` | Orange to yellow color wave |
| `NONE` | No colors applied |

### Try-With-Resources

Both classes implement `AutoCloseable`:

```java
try (ConsoleProgressBar bar = ConsoleProgressBar.builder().total(100).build()) {
    for (int i = 0; i <= 100; i++) {
        bar.update(i, "Processing...");
    }
}
```

## Platform Compatibility
* **Linux/macOS**: Full ANSI support out of the box.
* **Windows 10+**: ANSI colors supported in Windows Terminal and modern PowerShell (Windows Terminal recommended).
* **Legacy Windows**: Colors will be disabled. Use `ColorTheme.NONE` for clean output.

## Acknowledgments
If you find a bug, error, or typo, please submit a report in the Issues section. Thank you very much for using this lib!
