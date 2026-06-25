package dev.shiningpr1sm;

/**
 * A class designed to create and manage customizable terminal progress bars.
 * Supports distinct styling, flexible percentage placement, and ANSI True Color (RGB) gradients.
 */
public class ConsoleProgressBar {

    /** Position of the percentage indicator relative to the progress bar. */
    public enum Position {
        /** Displays percentages to the left of the bar: {@code Message [50%] [████░░░░]} */
        LEFT,
        /** Displays percentages to the right of the bar: {@code Message [████░░░░] 50%} */
        RIGHT,
        /** Embeds percentages directly into the center of the bar: {@code Message [████ 50% ░░░░]} */
        CENTER
    }

    /** Color themes that utilize smooth dynamic RGB gradients. */
    public enum ColorTheme {
        /** Automatically selects the best visual theme based on the bar's style. */
        STANDARD,
        /** Smooth deep blue to vibrant sky blue gradient. */
        BLUE_GRADIENT,
        /** Rich forest green to bright lime green gradient. */
        GREEN_GRADIENT,
        /** Cyberpunk-inspired deep violet to neon pink gradient. */
        PURPLE_PINK_GRADIENT,
        /** Fiery rust orange to bright electric yellow gradient. */
        ORANGE_YELLOW_GRADIENT,
        /** No colors applied (uses default terminal text color). */
        NONE
    }

    /** Visual character styles for rendering the progress bar. */
    public enum Style {
        /** Solid blocks layout (█ and ░). */
        BLOCKS('█', '░'),
        /** Vertical ticks layout (| and .). */
        STICKS('|', '.'),
        /** Bubble indicators layout (O and o). */
        BUBBLES('O', 'o'),
        /** Forward-pointing arrow indicator layout (> and -). */
        ARROW('>', '-');

        final char progressChar;
        final char remainingChar;

        Style(char progressChar, char remainingChar) {
            this.progressChar = progressChar;
            this.remainingChar = remainingChar;
        }
    }

    private static final String RESET = "\u001B[0m";

    private final int total;
    private final int barLength;
    private final Style style;
    private final Position position;
    private final ColorTheme theme;
    private String message = "Loading";

    /**
     * Constructs a progress bar with default configurations:
     * BLOCKS style, right-aligned percentages, and the STANDARD color theme.
     *
     * @param total The total number of steps required to reach 100% (e.g., 100 for basic percentages or total file size).
     */
    public ConsoleProgressBar(int total) {
        this(total, 25, Style.BLOCKS, Position.RIGHT, ColorTheme.STANDARD);
    }

    /**
     * Constructs a fully customized progress bar.
     *
     * @param total      The total number of steps to complete the tracking (100 is highly recommended for basic loading).
     * @param barLength  The visual width of the progress bar in characters (minimum forced value is 15).
     * @param style      The visual character layout (BLOCKS, STICKS, BUBBLES, or ARROW).
     * @param position   Where to display the percentage text (LEFT, RIGHT, or CENTER).
     * @param theme      The color gradient preset (use STANDARD to map colors automatically to styles).
     */
    public ConsoleProgressBar(int total, int barLength, Style style, Position position, ColorTheme theme) {
        this.total = total;
        this.barLength = Math.max(barLength, 15);
        this.style = style;
        this.position = position;
        this.theme = theme;
    }

    /**
     * Updates the progress bar state and renders it onto the console with a custom tracking message.
     *
     * @param current The current step progress (internally capped to not exceed the total value).
     * @param msg     The status message displayed before the bar (e.g., "Downloading assets...").
     */
    public void update(int current, String msg) {
        this.message = msg;
        int validCurrent = Math.min(current, total);

        int percentage = (int) (((double) validCurrent / total) * 100);
        int filledLength = (int) (((double) validCurrent / total) * barLength);

        ColorTheme activeTheme = this.theme;
        if (activeTheme == ColorTheme.STANDARD) {
            if (this.style == Style.BUBBLES) {
                activeTheme = ColorTheme.BLUE_GRADIENT;
            } else if (this.style == Style.STICKS) {
                activeTheme = ColorTheme.GREEN_GRADIENT;
            } else if (this.style == Style.BLOCKS) {
                activeTheme = ColorTheme.PURPLE_PINK_GRADIENT;
            } else if (this.style == Style.ARROW) {
                activeTheme = ColorTheme.ORANGE_YELLOW_GRADIENT;
            } else {
                activeTheme = ColorTheme.NONE;
            }
        }

        String barStr;

        if (style == Style.ARROW) {
            StringBuilder arrowBuilder = new StringBuilder();
            if (filledLength > 0) {
                for (int i = 0; i < filledLength - 1; i++) {
                    if (activeTheme != ColorTheme.NONE) arrowBuilder.append(getGradientColor(activeTheme, i, barLength));
                    arrowBuilder.append("-");
                }
                if (activeTheme != ColorTheme.NONE) arrowBuilder.append(getGradientColor(activeTheme, filledLength - 1, barLength));
                arrowBuilder.append(">");
                if (activeTheme != ColorTheme.NONE) arrowBuilder.append(RESET);

                arrowBuilder.append(" ".repeat(barLength - filledLength));
            } else {
                if (activeTheme != ColorTheme.NONE) arrowBuilder.append(getGradientColor(activeTheme, 0, barLength));
                arrowBuilder.append(">");
                if (activeTheme != ColorTheme.NONE) arrowBuilder.append(RESET);
                arrowBuilder.append(" ".repeat(barLength - 1));
            }
            barStr = arrowBuilder.toString();
        } else {
            StringBuilder barBuilder = new StringBuilder();
            for (int i = 0; i < barLength; i++) {
                if (i < filledLength) {
                    if (activeTheme != ColorTheme.NONE) barBuilder.append(getGradientColor(activeTheme, i, barLength));
                    barBuilder.append(style.progressChar);
                    if (activeTheme != ColorTheme.NONE) barBuilder.append(RESET);
                } else {
                    barBuilder.append(style.remainingChar);
                }
            }
            barStr = barBuilder.toString();
        }

        String percentageStr = " " + percentage + "% ";
        String finalLine = "";

        switch (position) {
            case LEFT:
                finalLine = message + " [" + percentageStr.trim() + "] [" + barStr + "]";
                break;

            case RIGHT:
                finalLine = message + " [" + barStr + "]" + percentageStr;
                break;

            case CENTER:
                int midPoint = barLength / 2;
                StringBuilder centerBar = new StringBuilder();
                for (int i = 0; i < barLength; i++) {
                    if (i == midPoint) {
                        centerBar.append(RESET).append(percentageStr);
                    }
                    if (i < filledLength) {
                        if (activeTheme != ColorTheme.NONE) centerBar.append(getGradientColor(activeTheme, i, barLength));
                        centerBar.append(style.progressChar);
                        if (activeTheme != ColorTheme.NONE) centerBar.append(RESET);
                    } else {
                        centerBar.append(style.remainingChar);
                    }
                }
                finalLine = message + " [" + centerBar + "]";
                break;
        }

        System.out.print("\r" + finalLine);
        System.out.flush();
    }

    /**
     * Updates the progress bar state using the last specified text message.
     *
     * @param current The current step progress.
     */
    public void update(int current) {
        update(current, this.message);
    }

    /**
     * Finalizes the progress bar rendering by shifting the console caret to a new line.
     * Always execute this when the operation completes to ensure subsequent log statements don't corrupt the layout.
     */
    public void finish() {
        System.out.println();
    }

    private String getGradientColor(ColorTheme theme, int index, int totalLength) {
        double ratio = (double) index / totalLength;
        int r = 0, g = 0, b = 0;

        if (theme == ColorTheme.BLUE_GRADIENT) {
            r = 0;
            g = (int) (70 + ratio * 185);
            b = (int) (200 + ratio * 55);
            return "\u001B[38;2;" + r + ";" + g + ";" + b + "m";
        } else if (theme == ColorTheme.GREEN_GRADIENT) {
            r = (int) (0 + ratio * 50);
            g = (int) (100 + ratio * 120);
            b = (int) (0 + ratio * 50);
            return "\u001B[38;2;" + r + ";" + g + ";" + b + "m";
        } else if (theme == ColorTheme.PURPLE_PINK_GRADIENT) {
            r = (int) (110 + ratio * 145);
            g = 0;
            b = (int) (180 - ratio * 30);
            return "\u001B[38;2;" + r + ";" + g + ";" + b + "m";
        } else if (theme == ColorTheme.ORANGE_YELLOW_GRADIENT) {
            r = (int) (200 + ratio * 55);
            g = (int) (50 + ratio * 160);
            b = 0;
            return "\u001B[38;2;" + r + ";" + g + ";" + b + "m";
        }
        return "";
    }
}