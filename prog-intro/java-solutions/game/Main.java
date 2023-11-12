package game;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        final Game game = new Game(false, new RandomPlayer(), new HumanPlayer());
        System.out.println("Wins Amount");
        int playerToStart = 0;
        int wins = scan.nextInt();
        int[] winsCount = new int[2];
        int result;
        do {
            System.out.println("M N K");
            result = game.play(new MNKBoard(scan.nextInt(), scan.nextInt(), scan.nextInt(), playerToStart));
            playerToStart = (playerToStart + 1) % 2;
            System.out.println("Game result: " + result);
            if (result != 0) {
                winsCount[result - 1] += 1;
            }
            System.out.println("Overall result: First player - " + winsCount[0] + " , Second player - " + winsCount[1]);
            System.out.println("-------------------------");
        } while (winsCount[0] < wins && winsCount[1] < wins);
        scan.close();
    }
}
