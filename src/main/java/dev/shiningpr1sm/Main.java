package dev.shiningpr1sm;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int totalSteps = 100;

        ConsoleProgressBar blocksBar = new ConsoleProgressBar(
                totalSteps,
                30,
                ConsoleProgressBar.Style.BLOCKS,
                ConsoleProgressBar.Position.RIGHT,
                ConsoleProgressBar.ColorTheme.STANDARD
        );

        System.out.println("--- Blocks ---");
        for (int i = 0; i <= totalSteps; i++) {
            blocksBar.update(i, "");
            Thread.sleep(20);
        }
        blocksBar.finish();
        System.out.println();

        ConsoleProgressBar arrowBar = new ConsoleProgressBar(
                totalSteps,
                30,
                ConsoleProgressBar.Style.ARROW,
                ConsoleProgressBar.Position.LEFT,
                ConsoleProgressBar.ColorTheme.STANDARD
        );

        System.out.println("--- Arrows ---");
        for (int i = 0; i <= totalSteps; i++) {
            arrowBar.update(i, "");
            Thread.sleep(20);
        }
        arrowBar.finish();
        System.out.println();

        ConsoleProgressBar bubblesBar = new ConsoleProgressBar(
                totalSteps,
                30,
                ConsoleProgressBar.Style.BUBBLES,
                ConsoleProgressBar.Position.CENTER,
                ConsoleProgressBar.ColorTheme.STANDARD
        );

        System.out.println("--- Bubbles ---");
        for (int i = 0; i <= totalSteps; i++) {
            bubblesBar.update(i, "");
            Thread.sleep(60);
        }
        bubblesBar.finish();
        System.out.println();

        ConsoleProgressBar sticksBar = new ConsoleProgressBar(
                totalSteps,
                30,
                ConsoleProgressBar.Style.STICKS,
                ConsoleProgressBar.Position.CENTER,
                ConsoleProgressBar.ColorTheme.NONE
        );

        System.out.println("--- Sticks ---");
        for (int i = 0; i <= totalSteps; i++) {
            sticksBar.update(i, "");
            Thread.sleep(20);
        }
        sticksBar.finish();
    }
}