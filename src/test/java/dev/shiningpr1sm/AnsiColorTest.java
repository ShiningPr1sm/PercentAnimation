package dev.shiningpr1sm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnsiColorTest {

    @Test
    void rgbReturnsValidAnsiCode() {
        String code = AnsiColor.rgb(255, 128, 0);
        assertEquals("\u001B[38;2;255;128;0m", code);
    }

    @Test
    void progressGradientReturnsNonEmptyForColoredThemes() {
        for (ConsoleProgressBar.ColorTheme theme : ConsoleProgressBar.ColorTheme.values()) {
            if (theme == ConsoleProgressBar.ColorTheme.NONE || theme == ConsoleProgressBar.ColorTheme.STANDARD) {
                assertEquals("", AnsiColor.progressGradient(theme, 5, 10));
            } else {
                String code = AnsiColor.progressGradient(theme, 5, 10);
                assertNotNull(code);
                assertFalse(code.isEmpty());
                assertTrue(code.startsWith("\u001B[38;2;"));
                assertTrue(code.endsWith("m"));
            }
        }
    }

    @Test
    void progressGradientRatioAffectsColor() {
        String start = AnsiColor.progressGradient(ConsoleProgressBar.ColorTheme.BLUE_GRADIENT, 0, 100);
        String end = AnsiColor.progressGradient(ConsoleProgressBar.ColorTheme.BLUE_GRADIENT, 99, 100);
        assertNotEquals(start, end);
    }

    @Test
    void spinnerGradientReturnsNonEmptyForColoredThemes() {
        for (ConsoleSpinner.ColorTheme theme : ConsoleSpinner.ColorTheme.values()) {
            if (theme == ConsoleSpinner.ColorTheme.NONE || theme == ConsoleSpinner.ColorTheme.STANDARD) {
                assertEquals("", AnsiColor.spinnerGradient(theme, 0, 4));
            } else {
                String code = AnsiColor.spinnerGradient(theme, 0, 4);
                assertNotNull(code);
                assertFalse(code.isEmpty());
                assertTrue(code.startsWith("\u001B[38;2;"));
                assertTrue(code.endsWith("m"));
            }
        }
    }

    @Test
    void resolveProgressThemeReturnsCorrectMapping() {
        assertEquals(ConsoleProgressBar.ColorTheme.BLUE_GRADIENT,
                AnsiColor.resolveProgressTheme(ConsoleProgressBar.Style.BUBBLES));
        assertEquals(ConsoleProgressBar.ColorTheme.GREEN_GRADIENT,
                AnsiColor.resolveProgressTheme(ConsoleProgressBar.Style.STICKS));
        assertEquals(ConsoleProgressBar.ColorTheme.PURPLE_PINK_GRADIENT,
                AnsiColor.resolveProgressTheme(ConsoleProgressBar.Style.BLOCKS));
        assertEquals(ConsoleProgressBar.ColorTheme.ORANGE_YELLOW_GRADIENT,
                AnsiColor.resolveProgressTheme(ConsoleProgressBar.Style.ARROW));
    }

    @Test
    void resetConstantIsValid() {
        assertEquals("\u001B[0m", AnsiColor.RESET);
    }

    @Test
    void rgbWithZeroValues() {
        assertEquals("\u001B[38;2;0;0;0m", AnsiColor.rgb(0, 0, 0));
    }

    @Test
    void rgbWithMaxValues() {
        assertEquals("\u001B[38;2;255;255;255m", AnsiColor.rgb(255, 255, 255));
    }
}
