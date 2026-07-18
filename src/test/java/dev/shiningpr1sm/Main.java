package dev.shiningpr1sm;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int totalSteps = 100;

        ConsoleProgressBar blocksBar = ConsoleProgressBar.builder()
                .total(totalSteps)
                .barLength(30)
                .style(ConsoleProgressBar.Style.BLOCKS)
                .position(ConsoleProgressBar.Position.RIGHT)
                .theme(ConsoleProgressBar.ColorTheme.STANDARD)
                .build();

        System.out.println("--- Blocks ---");
        for (int i = 0; i <= totalSteps; i++) {
            blocksBar.update(i, "");
            Thread.sleep(20);
        }
        blocksBar.finish();
        System.out.println();

        ConsoleProgressBar arrowBar = ConsoleProgressBar.builder()
                .total(totalSteps)
                .barLength(30)
                .style(ConsoleProgressBar.Style.ARROW)
                .position(ConsoleProgressBar.Position.LEFT)
                .theme(ConsoleProgressBar.ColorTheme.STANDARD)
                .build();

        System.out.println("--- Arrows ---");
        for (int i = 0; i <= totalSteps; i++) {
            arrowBar.update(i, "");
            Thread.sleep(20);
        }
        arrowBar.finish();
        System.out.println();

        ConsoleProgressBar bubblesBar = ConsoleProgressBar.builder()
                .total(totalSteps)
                .barLength(30)
                .style(ConsoleProgressBar.Style.BUBBLES)
                .position(ConsoleProgressBar.Position.CENTER)
                .theme(ConsoleProgressBar.ColorTheme.STANDARD)
                .build();

        System.out.println("--- Bubbles ---");
        for (int i = 0; i <= totalSteps; i++) {
            bubblesBar.update(i, "");
            Thread.sleep(60);
        }
        bubblesBar.finish();
        System.out.println();

        ConsoleProgressBar sticksBar = ConsoleProgressBar.builder()
                .total(totalSteps)
                .barLength(30)
                .style(ConsoleProgressBar.Style.STICKS)
                .position(ConsoleProgressBar.Position.CENTER)
                .theme(ConsoleProgressBar.ColorTheme.NONE)
                .build();

        System.out.println("--- Sticks ---");
        for (int i = 0; i <= totalSteps; i++) {
            sticksBar.update(i, "");
            Thread.sleep(20);
        }
        sticksBar.finish();
    }
}
