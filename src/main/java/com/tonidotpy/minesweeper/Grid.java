package com.tonidotpy.minesweeper;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Grid extends GridPane {
    private final int totRows, totCols;
    private final Cell[][] grid;

    private int totCellShown;
    private final int totMines;

    private final Game game;


    public Cell[][] getGrid() { return grid; }
    public int getTotRows()  { return totRows; }
    public int getTotCols()  { return totCols; }
    public int getTotMines() { return totMines; }
    public int getTotCellShown() { return totCellShown; }

    public boolean IsInBounds(int r, int c) { return (r >= 0 && r < totRows) && (c >= 0 && c < totCols); }



    public Grid(Game game, int totRows, int totCols, int totMines) {
        super();

        this.game = game;

        this.setAlignment(Pos.CENTER);

        this.totRows      = totRows;
        this.totCols      = totCols;
        // The first 9 cells at the beginning of the game cannot be mines
        this.totMines     = Math.max(1, Math.min(totMines, totRows * totCols - 9));
        this.totCellShown = 0;

        grid = new Cell[totRows][totCols];
        for (int r = 0; r < totRows; r++) {
            for (int c = 0; c < totCols; c++) {
                Cell cell = new Cell();
                final int row = r ,col = c;

                cell.setOnMousePressed(mouseEvent -> { if (cell.CanBeShown()) cell.setImage(ImageManager.getEmptyPressedImage()); });
                cell.setOnMouseReleased(mouseEvent -> { if (cell.CanBeShown()) cell.setImage(ImageManager.getEmptyImage()); } );
                cell.setOnMouseClicked(mouseEvent -> Click(mouseEvent, row, col));

                grid[r][c] = cell;
                this.add(cell, c, r);
            }
        }
    }



    private void Click(MouseEvent mouseEvent, int row, int col) {
        Cell cell = grid[row][col];
        switch (mouseEvent.getButton()) {
            case PRIMARY -> {
                if (cell.CanBeShown()) {
                    if (totCellShown == 0) {
                        Generate(row, col);
                    }

                    if (cell.isMine()) {
                        game.Lose(cell);
                        break;
                    }
                    else {
                        Propagate(row, col);
                    }
                }
                else if (cell.isShown() && cell.getTotNearMines() > 0) {
                    if (cell.getTotNearMines() > 0) {
                        if (!ShowObviousCells(row, col))
                            return;
                    }
                }
                if (totCellShown >= totRows * totCols - totMines) {
                    game.Win();
                }
            }
            case SECONDARY -> {
                if (!cell.isShown())
                    cell.ToggleState();
            }
        }
    }

    public void Generate(int row, int col) {
        this.setDisable(false);

        PlaceMines(row, col);
        CalculateNearMines();
    }
    private void PlaceMines(int row, int col) {
        ArrayList<Pair<Integer, Integer>> nums = new ArrayList<>();
        for (int r = 0; r < totRows; r++) {
            for (int c = 0; c < totCols; c++) {
                if (r < row - 1 || r > row + 1 || c < col - 1 || c > col + 1) {
                    nums.add(new Pair<>(r, c));
                }
            }
        }
        Collections.shuffle(nums);

        for (int i = 0; i < totMines; i++) {
            var index = nums.get(i);
            grid[index.getKey()][index.getValue()].setType(Cell.Type.MINE);
        }
    }
    private void CalculateNearMines() {
        for (int r = 0; r < totRows; r++) {
            for (int c = 0; c < totCols; c++) {
                Cell cell = grid[r][c];
                if (!cell.isMine()) {
                    int mines = CountAdjacentMines(r, c);
                    cell.setType(mines);
                }
            }
        }
    }

    private boolean ShowObviousCells(int row, int col) {
        Cell cell = grid[row][col];
        int flags = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (IsInBounds(r, c) && grid[r][c].isFlag()) {
                    flags++;
                }
            }
        }

        if (flags >= cell.getTotNearMines()) {
            Cell cur;
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if (IsInBounds(r, c) && (cur = grid[r][c]).CanBeShown()) {
                        cur.Show();
                        totCellShown++;


                        if (cur.getType() == Cell.Type.ZERO) {
                            Propagate(r, c);
                        }

                        if (cur.isMine()) {
                            game.Lose(cur);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    private int CountAdjacentMines(int row, int col) {
        int mines = 0;
        for (int r = row-1; r <= row+1; r++) {
            for (int c = col-1; c <= col+1; c++) {
                if (IsInBounds(r, c) && grid[r][c].isMine()) {
                    mines++;
                }
            }
        }
        return mines;
    }

    private void Propagate(int row, int col) {
        Stack<Pair<Integer, Integer>> trace = new Stack<>();
        trace.push(new Pair<>(row, col));
        while (!trace.empty()) {
            var index = trace.peek();
            trace.pop();

            int r = index.getKey(), c = index.getValue();
            Cell cell = grid[r][c];
            if (cell.CanBeShown()) {
                cell.Show();
                totCellShown++;
            }

            if (cell.getType() == Cell.Type.ZERO) {
                for (int i = r - 1; i <= r + 1; i++) {
                    for (int j = c - 1; j <= c + 1; j++) {
                        if (IsInBounds(i, j) && !grid[i][j].isShown()) {
                            trace.push(new Pair<>(i, j));
                        }
                    }
                }
            }
        }
    }

    public void ResizeCells(double width, double height) {
        double s = Math.min(width, height);
        double w = s / (double) totCols;
        double h = s / (double) totRows;

        for (var row : grid) {
            for (var col : row) {
                col.setFitWidth(w);
                col.setFitHeight(h);
                col.setPreserveRatio(true);
            }
        }
    }

    public void Reset() {
        totCellShown = 0;
        this.setDisable(false);
        for (var row : grid) {
            for (var col : row) {
                col.Reset();
            }
        }
    }
}
