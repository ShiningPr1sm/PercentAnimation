package dev.shiningpr1sm;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleProgressBarTest {

    @Test
    void constructorThrowsOnZeroTotal() {
        assertThrows(IllegalArgumentException.class, () ->
                ConsoleProgressBar.builder().total(0).build());
    }

    @Test
    void constructorThrowsOnNegativeTotal() {
        assertThrows(IllegalArgumentException.class, () ->
                ConsoleProgressBar.builder().total(-5).build());
    }

    @Test
    void constructorThrowsOnZeroBarLength() {
        assertThrows(IllegalArgumentException.class, () ->
                ConsoleProgressBar.builder().total(100).barLength(0).build());
    }

    @Test
    void constructorForcesMinimumBarLength() {
        ConsoleProgressBar bar = ConsoleProgressBar.builder()
                .total(100)
                .barLength(5)
                .build();
        assertNotNull(bar);
    }

    @Test
    void builderDefaultsAreValid() {
        ConsoleProgressBar bar = ConsoleProgressBar.builder().build();
        assertNotNull(bar);
    }

    @Test
    void getPercentageReturnsZeroInitially() {
        ConsoleProgressBar bar = ConsoleProgressBar.builder().total(100).build();
        assertEquals(0, bar.getPercentage());
    }

    @Test
    void updateTracksPercentage() {
        ConsoleProgressBar bar = ConsoleProgressBar.builder().total(100).build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            bar.update(50, "test");
            assertEquals(50, bar.getPercentage());

            bar.update(100, "done");
            assertEquals(100, bar.getPercentage());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void updateCapsAtTotal() {
        ConsoleProgressBar bar = ConsoleProgressBar.builder().total(50).build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            bar.update(200, "overflow");
            assertEquals(100, bar.getPercentage());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void updateHandlesNegativeCurrent() {
        ConsoleProgressBar bar = ConsoleProgressBar.builder().total(100).build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            bar.update(-10, "negative");
            assertEquals(0, bar.getPercentage());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void updatePreservesLastMessage() {
        ConsoleProgressBar bar = ConsoleProgressBar.builder().total(100).build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            bar.update(10, "first message");
            bar.update(20);
            assertEquals(20, bar.getPercentage());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void finishDoesNotThrow() {
        ConsoleProgressBar bar = ConsoleProgressBar.builder().total(10).build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            assertDoesNotThrow(bar::finish);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void closeCallsFinish() {
        ConsoleProgressBar bar = ConsoleProgressBar.builder().total(10).build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            assertDoesNotThrow(bar::close);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void allStylesCanRender() {
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            for (ConsoleProgressBar.Style style : ConsoleProgressBar.Style.values()) {
                ConsoleProgressBar bar = ConsoleProgressBar.builder()
                        .total(10)
                        .style(style)
                        .build();
                bar.update(5, "test");
                bar.finish();
            }
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void allPositionsCanRender() {
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            for (ConsoleProgressBar.Position pos : ConsoleProgressBar.Position.values()) {
                ConsoleProgressBar bar = ConsoleProgressBar.builder()
                        .total(10)
                        .position(pos)
                        .build();
                bar.update(5, "test");
                bar.finish();
            }
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void allColorThemesCanRender() {
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            for (ConsoleProgressBar.ColorTheme theme : ConsoleProgressBar.ColorTheme.values()) {
                ConsoleProgressBar bar = ConsoleProgressBar.builder()
                        .total(10)
                        .theme(theme)
                        .build();
                bar.update(5, "test");
                bar.finish();
            }
        } finally {
            System.setOut(originalOut);
        }
    }
}
