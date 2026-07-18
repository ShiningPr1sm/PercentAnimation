package dev.shiningpr1sm;

/**
 * A class to create and control threaded console activity indicators (spinners).
 * Runs completely in the background via a daemon thread with dynamic color-shifting frames.
 *
 * <p>Usage example:
 * <pre>{@code
 * ConsoleSpinner spinner = ConsoleSpinner.builder()
 *     .style(Style.CLASSIC)
 *     .position(Position.RIGHT)
 *     .theme(ColorTheme.STANDARD)
 *     .build();
 *
 * spinner.start("Loading configuration...");
 * // ... do work ...
 * spinner.stop();
 * }</pre>
 */
public class ConsoleSpinner implements Runnable, AutoCloseable {

    /** Position of the visual spinner frame relative to the tracked message string. */
    public enum Position {
        /** Displays the spinner frame before the text message: {@code [|] Loading system configuration} */
        LEFT,
        /** Displays the spinner frame after the text message: {@code Loading system configuration [|]} */
        RIGHT
    }

    /** Color themes that utilize smooth dynamic RGB gradients. */
    public enum ColorTheme {
        /** Automatically determines the coloring based on the active animation style. */
        STANDARD,
        /** Bright sky blue / cyan shifting theme. */
        BLUE,
        /** Intense vivid lime green shifting theme. */
        GREEN,
        /** Neon cyberpunk violet-to-pink color wave. */
        PURPLE_PINK,
        /** Energetic fire orange-to-yellow color wave. */
        ORANGE_YELLOW,
        /** No coloring applied (uses raw terminal defaults). */
        NONE
    }

    /** Visual style frames and timing attributes for spinner animations. */
    public enum Style {
        /** Standard spinning line sequence (| / - \). */
        CLASSIC(new String[]{"|", "/", "-", "\\"}, 100, ColorTheme.BLUE),
        /** Horizontal progression dots (.  ..  ...). */
        DOTS(new String[]{".  ", ".. ", "...", "   "}, 250, ColorTheme.GREEN),
        /** Text sequence where individual letters illuminate sequentially. Perfect for legacy terminals like PowerShell. */
        TEXT_LOADING(new String[]{"lOADING", "LoADING", "LOaDING", "LOADiNG", "LOADInG", "LOADINg", "LOADING"}, 120, ColorTheme.PURPLE_PINK),
        /** Vertical scaling signal block structures. */
        GROWING(new String[]{" ", "\u2583", "\u2584", "\u2585", "\u2586", "\u2587", "\u2588", "\u2587", "\u2586", "\u2585", "\u2584", "\u2583"}, 90, ColorTheme.ORANGE_YELLOW);

        final String[] frames;
        final int delay;
        final ColorTheme defaultTheme;

        Style(String[] frames, int delay, ColorTheme defaultTheme) {
            this.frames = frames;
            this.delay = delay;
            this.defaultTheme = defaultTheme;
        }
    }

    private final Style style;
    private final Position position;
    private final ColorTheme theme;

    private volatile boolean running = false;
    private volatile String message = "Loading...";
    private Thread workerThread;
    private String lastRenderedLine = "";

    private ConsoleSpinner(Style style, Position position, ColorTheme theme) {
        if (style.frames.length == 0) {
            throw new IllegalArgumentException("Style frames must not be empty");
        }
        this.style = style;
        this.position = position;
        this.theme = theme;
    }

    /**
     * Creates a new builder for configuring a spinner.
     *
     * @return a new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Launches the spinner animation sequence concurrently within a dedicated background thread.
     * This method instantly releases program control while tracking executes asynchronously.
     *
     * @param msg The status phrase coupled with the animation loop (e.g., "Establishing secure tunnel...").
     */
    public synchronized void start(String msg) {
        if (running) return;

        this.message = msg;
        this.running = true;

        workerThread = new Thread(this);
        workerThread.setDaemon(true);
        workerThread.start();
    }

    /**
     * Terminates background thread processing and fully sanitizes the console row text
     * to prevent artifact collisions with forthcoming output streams.
     */
    public synchronized void stop() {
        if (!running) return;

        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }

        int clearLength = lastRenderedLine.length() + 5;
        System.out.print("\r" + " ".repeat(clearLength) + "\r");
        System.out.flush();
    }

    /**
     * Returns whether the spinner is currently running.
     *
     * @return {@code true} if the spinner animation is active.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Updates the spinner message while it is running.
     *
     * @param msg the new status message to display.
     */
    public void setMessage(String msg) {
        this.message = msg;
    }

    @Override
    public void run() {
        int currentFrame = 0;
        String[] frames = style.frames;

        ColorTheme activeTheme = this.theme;
        if (activeTheme == ColorTheme.STANDARD) {
            activeTheme = style.defaultTheme;
        }

        while (running) {
            String colorCode = (activeTheme == ColorTheme.NONE) ? "" : AnsiColor.spinnerGradient(activeTheme, currentFrame, frames.length);

            String frameStr;
            if (style == Style.TEXT_LOADING) {
                frameStr = colorCode + frames[currentFrame] + AnsiColor.RESET;
            } else {
                frameStr = "[" + colorCode + frames[currentFrame] + AnsiColor.RESET + "]";
            }

            String finalLine;
            if (position == Position.LEFT) {
                finalLine = frameStr + " " + message;
            } else {
                if (style == Style.TEXT_LOADING) {
                    finalLine = message + " -> " + frameStr;
                } else {
                    finalLine = message + " " + frameStr;
                }
            }

            this.lastRenderedLine = finalLine;

            System.out.print("\r" + finalLine);
            System.out.flush();

            currentFrame = (currentFrame + 1) % frames.length;

            try {
                Thread.sleep(style.delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void close() {
        stop();
    }

    /**
     * Builder for constructing {@link ConsoleSpinner} instances with a fluent API.
     */
    public static final class Builder {
        private Style style = Style.CLASSIC;
        private Position position = Position.RIGHT;
        private ColorTheme theme = ColorTheme.STANDARD;

        private Builder() {
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
         * Builds the spinner with the configured settings.
         *
         * @return a new {@link ConsoleSpinner} instance.
         */
        public ConsoleSpinner build() {
            return new ConsoleSpinner(style, position, theme);
        }
    }
}
