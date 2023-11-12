package game;

import java.util.Arrays;
import java.util.Map;

public class MNKBoard implements Board, Position {
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.'
    );

    private final Cell[][] cells;
    private final int m;
    private final int n;
    private final int k;
    private int playersTurn;
    private int cellAmount;
    private Cell turn;

    public MNKBoard(int m, int n, int k, int playersTurn) {
        this.m = m;
        this.n = n;
        this.k = k;
        this.playersTurn = playersTurn;
        this.cells = new Cell[n][m];
        this.cellAmount = m * n;
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        if (playersTurn == 0) {
            turn = Cell.X;
        } else {
            turn = Cell.O;
        }
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public Cell getCell() {
        return turn;
    }

    @Override
    public boolean isValid(final Move move) { 
        return 0 <= move.getRow() && move.getRow() < n
                && 0 <= move.getColumn() && move.getColumn() < m
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();
    }

    @Override
    public Result makeMove(final Move move) {
        if (playersTurn > 0) {
            playersTurn -= 1;
            return Result.UNKNOWN;
        }
        
        if (!isValid(move)) {
            return Result.LOSE;
        }

        int row = move.getRow();
        int column = move.getColumn();
        cells[row][column] = move.getValue();
        cellAmount -= 1;
        
        int inDiag1 = 0;
        int inDiag2 = 0;
        int inRow = 0;
        int inColumn = 0;
        for (int i = -k + 1; i < k; i++) {
            boolean correctRow = 0 <= row + i && row + i < n;
            boolean correctColumn = 0 <= column + i && column + i < m;
            boolean correctDiagRight = correctRow && correctColumn;
            boolean correctDiagLeft = correctRow && 0 <= column - i && column - i < m;
            if (correctRow && cells[row + i][column] == turn) {
                inRow += 1;
            }
            if (correctColumn && cells[row][column + i] == turn) {
                inColumn += 1;
            }
            if (correctDiagRight && cells[row + i][column + i] == turn) {
                inDiag1 += 1;
            }
            if (correctDiagLeft && cells[row + i][column - i] == turn) {
                inDiag2 += 1;
            }
        }

        if (inDiag1 >= k || inDiag2 >= k || inRow >= k || inColumn >= k) {
            return Result.WIN;
        }

        if (cellAmount == 0) {
            return Result.DRAW;
        }

        turn = turn == Cell.X ? Cell.O : Cell.X;
        return Result.UNKNOWN;
    }

    @Override
    public Cell getCell(final int r, final int c) {
        return cells[r][c];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(" ");
        for (int i = 0; i < m; i++) {
            sb.append(i);
        }
        for (int col = 0; col < n; col++) {
            sb.append(System.lineSeparator());
            sb.append(col);
            for (int row = 0; row < m; row++) {
                sb.append(SYMBOLS.get(cells[col][row]));
            }
        }
        return sb.toString();
    }

    @Override
    public int getRow () {
        return n;
    }

    @Override
    public int getColumn () {
        return m;
    }
}
