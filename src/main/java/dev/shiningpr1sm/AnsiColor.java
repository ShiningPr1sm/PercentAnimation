package dev.shiningpr1sm;

/**
 * Internal utility class for generating ANSI True Color (RGB) escape codes.
 * Used by {@link ConsoleProgressBar} and {@link ConsoleSpinner} for gradient rendering.
 */
final class AnsiColor {

    static final String RESET = "\u001B[0m";

    private AnsiColor() {
    }

    static String rgb(int r, int g, int b) {
        return "\u001B[38;2;" + r + ";" + g + ";" + b + "m";
    }

    static String progressGradient(ConsoleProgressBar.ColorTheme theme, int index, int totalLength) {
        double ratio = (double) index / totalLength;
        return switch (theme) {
            case BLUE_GRADIENT -> rgb(0, (int) (70 + ratio * 185), (int) (200 + ratio * 55));
            case GREEN_GRADIENT -> rgb((int) (ratio * 50), (int) (100 + ratio * 120), (int) (ratio * 50));
            case PURPLE_PINK_GRADIENT -> rgb((int) (110 + ratio * 145), 0, (int) (180 - ratio * 30));
            case ORANGE_YELLOW_GRADIENT -> rgb((int) (200 + ratio * 55), (int) (50 + ratio * 160), 0);
            case STANDARD, NONE -> "";
        };
    }

    static String spinnerGradient(ConsoleSpinner.ColorTheme theme, int frameIndex, int totalFrames) {
        double ratio = (double) frameIndex / totalFrames;
        return switch (theme) {
            case BLUE -> rgb(0, (int) (100 + ratio * 155), 255);
            case GREEN -> rgb((int) (ratio * 100), 255, 0);
            case PURPLE_PINK -> rgb((int) (150 + ratio * 105), 0, (int) (255 - ratio * 50));
            case ORANGE_YELLOW -> rgb(255, (int) (80 + ratio * 150), 0);
            case STANDARD, NONE -> "";
        };
    }

    static ConsoleProgressBar.ColorTheme resolveProgressTheme(ConsoleProgressBar.Style style) {
        return switch (style) {
            case BUBBLES -> ConsoleProgressBar.ColorTheme.BLUE_GRADIENT;
            case STICKS -> ConsoleProgressBar.ColorTheme.GREEN_GRADIENT;
            case BLOCKS -> ConsoleProgressBar.ColorTheme.PURPLE_PINK_GRADIENT;
            case ARROW -> ConsoleProgressBar.ColorTheme.ORANGE_YELLOW_GRADIENT;
        };
    }
}
