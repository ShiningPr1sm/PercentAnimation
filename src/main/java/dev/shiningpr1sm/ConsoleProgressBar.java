package dev.shiningpr1sm;

/**
 * A class designed to create and manage customizable terminal progress bars.
 * Supports distinct styling, flexible percentage placement, and ANSI True Color (RGB) gradients.
 *
 * <p>Usage example:
 * <pre>{@code
 * ConsoleProgressBar bar = ConsoleProgressBar.builder()
 *     .total(100)
 *     .barLength(30)
 *     .style(Style.BLOCKS)
 *     .position(Position.RIGHT)
 *     .theme(ColorTheme.STANDARD)
 *     .build();
 *
 * for (int i = 0; i <= 100; i++) {
 *     bar.update(i, "Downloading...");
 * }
 * bar.finish();
 * }</pre>
 */
public class ConsoleProgressBar implements AutoCloseable {

    /** Position of the percentage indicator relative to the progress bar. */
    public enum Position {
        /** Displays percentages to the left of the bar: {@code Message [50%] [████░░░░]} */
        LEFT,
        /** Displays percentages to the right of the bar: {@code Message [████░░░░] [50%]} */
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
        BLOCKS('\u2588', '\u2591'),
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

    private final int total;
    private final int barLength;
    private final Style style;
    private final Position position;
    private final ColorTheme theme;

    private String message = "Loading";
    private int lastPercentage = 0;
    private String lastRenderedLine = "";

    private ConsoleProgressBar(int total, int barLength, Style style, Position position, ColorTheme theme) {
        if (total <= 0) {
            throw new IllegalArgumentException("total must be greater than 0, got: " + total);
        }
        if (barLength <= 0) {
            throw new IllegalArgumentException("barLength must be greater than 0, got: " + barLength);
        }
        this.total = total;
        this.barLength = Math.max(barLength, 15);
        this.style = style;
        this.position = position;
        this.theme = theme;
    }

    /**
     * Creates a new builder for configuring a progress bar.
     *
     * @return a new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Updates the progress bar state and renders it onto the console with a custom tracking message.
     *
     * @param current The current step progress (internally capped to not exceed the total value).
     * @param msg     The status message displayed before the bar (e.g., "Downloading assets...").
     */
    public synchronized void update(int current, String msg) {
        this.message = msg;
        int validCurrent = Math.min(Math.max(current, 0), total);

        int percentage = (int) (((double) validCurrent / total) * 100);
        int filledLength = (int) (((double) validCurrent / total) * barLength);

        this.lastPercentage = percentage;

        ColorTheme activeTheme = resolveTheme();

        String barStr = renderBar(filledLength, activeTheme);
        String percentageStr = " " + percentage + "% ";
        String finalLine = renderLine(barStr, percentageStr.trim());

        this.lastRenderedLine = finalLine;

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
    public synchronized void finish() {
        System.out.println();
    }

    /**
     * Returns the current percentage value (0-100).
     *
     * @return the last rendered percentage.
     */
    public int getPercentage() {
        return lastPercentage;
    }

    @Override
    public void close() {
        finish();
    }

    private ColorTheme resolveTheme() {
        if (theme != ColorTheme.STANDARD) {
            return theme;
        }
        return AnsiColor.resolveProgressTheme(style);
    }

    private String renderBar(int filledLength, ColorTheme activeTheme) {
        boolean colored = activeTheme != ColorTheme.NONE && activeTheme != ColorTheme.STANDARD;

        if (style == Style.ARROW) {
            return renderArrow(filledLength, colored, activeTheme);
        }

        StringBuilder barBuilder = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                if (colored) barBuilder.append(AnsiColor.progressGradient(activeTheme, i, barLength));
                barBuilder.append(style.progressChar);
                if (colored) barBuilder.append(AnsiColor.RESET);
            } else {
                barBuilder.append(style.remainingChar);
            }
        }
        return barBuilder.toString();
    }

    private String renderArrow(int filledLength, boolean colored, ColorTheme activeTheme) {
        StringBuilder arrowBuilder = new StringBuilder();

        if (filledLength > 0) {
            for (int i = 0; i < filledLength - 1; i++) {
                if (colored) arrowBuilder.append(AnsiColor.progressGradient(activeTheme, i, barLength));
                arrowBuilder.append("-");
            }
            if (colored) arrowBuilder.append(AnsiColor.progressGradient(activeTheme, filledLength - 1, barLength));
            arrowBuilder.append(">");
            if (colored) arrowBuilder.append(AnsiColor.RESET);
            arrowBuilder.append(" ".repeat(barLength - filledLength));
        } else {
            arrowBuilder.append(">");
            arrowBuilder.append(" ".repeat(barLength - 1));
        }

        return arrowBuilder.toString();
    }

    private String renderLine(String barStr, String percentageStr) {
        return switch (position) {
            case LEFT -> message + " [" + percentageStr + "] [" + barStr + "]";
            case RIGHT -> message + " [" + barStr + "] [" + percentageStr + "]";
            case CENTER -> renderCenterLine(barStr, percentageStr);
        };
    }

    private String renderCenterLine(String barStr, String percentageStr) {
        int midPoint = barLength / 2;
        boolean colored = resolveTheme() != ColorTheme.NONE && resolveTheme() != ColorTheme.STANDARD;
        ColorTheme activeTheme = resolveTheme();
        int filledLength = (int) (((double) Math.min(lastPercentage, total) / total) * barLength);

        StringBuilder centerBar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i == midPoint) {
                centerBar.append(AnsiColor.RESET).append(" ").append(percentageStr).append(" ");
            }
            if (i < filledLength) {
                if (colored) centerBar.append(AnsiColor.progressGradient(activeTheme, i, barLength));
                centerBar.append(style.progressChar);
                if (colored) centerBar.append(AnsiColor.RESET);
            } else {
                centerBar.append(style.remainingChar);
            }
        }
        return message + " [" + centerBar + "]";
    }

    /**
     * Builder for constructing {@link ConsoleProgressBar} instances with a fluent API.
     */
    public static final class Builder {
        private int total = 100;
        private int barLength = 25;
        private Style style = Style.BLOCKS;
        private Position position = Position.RIGHT;
        private ColorTheme theme = ColorTheme.STANDARD;

        private Builder() {
        }

        public Builder total(int total) {
            this.total = total;
            return this;
        }

        public Builder barLength(int barLength) {
            this.barLength = barLength;
            return this;
        }

        public Builder style(Style style) {
            this.style = style;
            return this;
        }

        public Builder position(Position position) {
            this.position = position;
            return this;
        }

        public Builder theme(ColorTheme theme) {
            this.theme = theme;
            return this;
        }

        /**
         * Builds the progress bar with the configured settings.
         *
         * @return a new {@link ConsoleProgressBar} instance.
         * @throws IllegalArgumentException if total or barLength is less than or equal to 0.
         */
        public ConsoleProgressBar build() {
            return new ConsoleProgressBar(total, barLength, style, position, theme);
        }
    }
}
