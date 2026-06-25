package dev.shiningpr1sm;

/**
 * A class to create and control threaded console activity indicators (spinners).
 * Runs completely in the background via a daemon thread with dynamic color-shifting frames.
 */
public class ConsoleSpinner implements Runnable {

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
        GROWING(new String[]{" ", "▃", "▄", "▅", "▆", "▇", "█", "▇", "▆", "▅", "▄", "▃"}, 90, ColorTheme.ORANGE_YELLOW);

        final String[] frames;
        final int delay;
        final ColorTheme defaultTheme;

        Style(String[] frames, int delay, ColorTheme defaultTheme) {
            this.frames = frames;
            this.delay = delay;
            this.defaultTheme = defaultTheme;
        }
    }

    private static final String RESET = "\u001B[0m";

    private final Style style;
    private final Position position;
    private final ColorTheme theme;

    private volatile boolean running = false;
    private Thread workerThread;
    private String message = "Loading...";

    /**
     * Constructs a spinner using default options:
     * CLASSIC layout, right-aligned positioning, and the STANDARD auto-color assignment.
     */
    public ConsoleSpinner() {
        this(Style.CLASSIC, Position.RIGHT, ColorTheme.STANDARD);
    }

    /**
     * Constructs a spinner with a customized animation frame layout.
     *
     * @param style The desired animation format selection (e.g., Style.TEXT_LOADING).
     */
    public ConsoleSpinner(Style style) {
        this(style, Position.RIGHT, ColorTheme.STANDARD);
    }

    /**
     * Constructs a fully tailored background console spinner.
     *
     * @param style    The animation structure frame configuration (CLASSIC, DOTS, TEXT_LOADING, GROWING).
     * @param position Layout placement indicating where the animation goes relative to the message (LEFT or RIGHT).
     * @param theme    The coloration environment ruleset (STANDARD assigns appropriate colors automatically).
     */
    public ConsoleSpinner(Style style, Position position, ColorTheme theme) {
        this.style = style;
        this.position = position;
        this.theme = theme;
    }

    /**
     * Launches the spinner animation sequence concurrently within a dedicated background thread.
     * This method instantly releases program control while tracking executes asynchronously.
     *
     * @param msg The status phrase coupled with the animation loop (e.g., "Establishing secure tunnel...").
     */
    public void start(String msg) {
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
    public void stop() {
        if (!running) return;

        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }

        System.out.print("\r" + " ".repeat(message.length() + 20) + "\r");
        System.out.flush();
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
            String colorCode = (activeTheme == ColorTheme.NONE) ? "" : getSpinnerColor(activeTheme, currentFrame, frames.length);

            String frameStr;
            if (style == Style.TEXT_LOADING) {
                frameStr = colorCode + frames[currentFrame] + RESET;
            } else {
                frameStr = "[" + colorCode + frames[currentFrame] + RESET + "]";
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

    private String getSpinnerColor(ColorTheme theme, int frameIndex, int totalFrames) {
        double ratio = (double) frameIndex / totalFrames;
        int r = 0, g = 0, b = 0;

        switch (theme) {
            case BLUE:
                g = (int) (100 + ratio * 155);
                b = 255;
                break;
            case GREEN:
                g = 255;
                r = (int) (ratio * 100);
                break;
            case PURPLE_PINK:
                r = (int) (150 + ratio * 105);
                b = (int) (255 - ratio * 50);
                break;
            case ORANGE_YELLOW:
                r = 255;
                g = (int) (80 + ratio * 150);
                break;
            default:
                return "";
        }

        return "\u001B[38;2;" + r + ";" + g + ";" + b + "m";
    }
}