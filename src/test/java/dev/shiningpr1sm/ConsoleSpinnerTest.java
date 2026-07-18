package dev.shiningpr1sm;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleSpinnerTest {

    @Test
    void builderDefaultsAreValid() {
        ConsoleSpinner spinner = ConsoleSpinner.builder()
                .style(ConsoleSpinner.Style.CLASSIC)
                .build();
        assertNotNull(spinner);
    }

    @Test
    void isRunningReturnsFalseInitially() {
        ConsoleSpinner spinner = ConsoleSpinner.builder()
                .style(ConsoleSpinner.Style.CLASSIC)
                .build();
        assertFalse(spinner.isRunning());
    }

    @Test
    void startSetsRunningTrue() {
        ConsoleSpinner spinner = ConsoleSpinner.builder()
                .style(ConsoleSpinner.Style.CLASSIC)
                .build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            spinner.start("loading...");
            assertTrue(spinner.isRunning());
            spinner.stop();
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void stopSetsRunningFalse() {
        ConsoleSpinner spinner = ConsoleSpinner.builder()
                .style(ConsoleSpinner.Style.CLASSIC)
                .build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            spinner.start("loading...");
            spinner.stop();
            assertFalse(spinner.isRunning());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void stopDoesNotThrowWhenNotRunning() {
        ConsoleSpinner spinner = ConsoleSpinner.builder()
                .style(ConsoleSpinner.Style.CLASSIC)
                .build();
        assertDoesNotThrow(spinner::stop);
    }

    @Test
    void startDoesNotThrowWhenAlreadyRunning() {
        ConsoleSpinner spinner = ConsoleSpinner.builder()
                .style(ConsoleSpinner.Style.CLASSIC)
                .build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            spinner.start("first");
            spinner.start("second");
            assertTrue(spinner.isRunning());
            spinner.stop();
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void setMessageUpdatesMessage() {
        ConsoleSpinner spinner = ConsoleSpinner.builder()
                .style(ConsoleSpinner.Style.CLASSIC)
                .build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            spinner.start("initial");
            spinner.setMessage("updated");
            assertTrue(spinner.isRunning());
            spinner.stop();
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void closeStopsSpinner() {
        ConsoleSpinner spinner = ConsoleSpinner.builder()
                .style(ConsoleSpinner.Style.CLASSIC)
                .build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            spinner.start("loading...");
            spinner.close();
            assertFalse(spinner.isRunning());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void allStylesCanStartAndStop() {
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            for (ConsoleSpinner.Style style : ConsoleSpinner.Style.values()) {
                ConsoleSpinner spinner = ConsoleSpinner.builder()
                        .style(style)
                        .build();
                spinner.start("test");
                assertTrue(spinner.isRunning());
                spinner.stop();
                assertFalse(spinner.isRunning());
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
            for (ConsoleSpinner.Position pos : ConsoleSpinner.Position.values()) {
                ConsoleSpinner spinner = ConsoleSpinner.builder()
                        .style(ConsoleSpinner.Style.CLASSIC)
                        .position(pos)
                        .build();
                spinner.start("test");
                spinner.stop();
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
            for (ConsoleSpinner.ColorTheme theme : ConsoleSpinner.ColorTheme.values()) {
                ConsoleSpinner spinner = ConsoleSpinner.builder()
                        .style(ConsoleSpinner.Style.CLASSIC)
                        .theme(theme)
                        .build();
                spinner.start("test");
                spinner.stop();
            }
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void doubleStopIsSafe() {
        ConsoleSpinner spinner = ConsoleSpinner.builder()
                .style(ConsoleSpinner.Style.CLASSIC)
                .build();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        try {
            spinner.start("test");
            spinner.stop();
            assertDoesNotThrow(spinner::stop);
        } finally {
            System.setOut(originalOut);
        }
    }
}
