package com.tonidotpy.minesweeper;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Game extends VBox {
    private MenuBar menuBar;
    private BorderPane controls;
    private ImageView[] scoreView;
    private ImageView generate;
    private ImageView[] timeView;
    private Grid grid;


    private final double menuHeightPercent = 0.05;
    private final double controlsHeightPercent = 0.07;
    private final double paddingPercent = 0.3;


    private int score;
    private final Timeline timeLine;
    private int time;


    public Game() {
        super();

        this.setCursor(Cursor.HAND);

        ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> Resize();
        this.widthProperty().addListener(resizeListener);
        this.heightProperty().addListener(resizeListener);

        this.score = 0;
        this.time  = 0;
        this.timeLine = new Timeline(
                new KeyFrame(Duration.seconds(1), actionEvent -> {
                    UpdateDigitsView(timeView, ++time);
                    if (time > 999)
                        Lose(null);
                })
        );
        this.timeLine.setCycleCount(Animation.INDEFINITE);
        this.timeLine.setAutoReverse(false);


        VBox.setVgrow(this, Priority.ALWAYS);
        this.setAlignment(Pos.TOP_CENTER);

        SetMenu();
        SetControls();
        SetGrid(15, 15, 50);

        SetScore();
        SetTime();
    }

    private void SetScore() {
        for (var row : grid.getGrid()) {
            for (var cell : row) {
                EventHandler<MouseEvent> eventHandler = mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                        if (cell.getState() == Cell.State.EMPTY)
                            score--;
                        else if (cell.isFlag())
                            score++;

                        UpdateDigitsView(scoreView, score);
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
            }
        }

        ResetScore();
    }
    public void ResetScore() {
        score = grid.getTotMines();
        UpdateDigitsView(scoreView, score);
    }
    private void SetTime() {
        for (var row : grid.getGrid()) {
            for (var cell : row) {
                EventHandler<MouseEvent> eventHandler = mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        if (timeLine.getStatus() != Animation.Status.RUNNING) {
                            UpdateDigitsView(timeView, time);
                            timeLine.play();
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
            }
        }
    }
    private void ResetTime() {
        timeLine.stop();
        UpdateDigitsView(timeView, time);
        time = 0;
    }
    private void UpdateDigitsView(ImageView[] view, int value) {
        int num = (value < 0) ? -value : value;

        int[] digits = {
            (num > 99) ? num / 100 : -1,
            (num > 9)  ? (num % 100) / 10  : -1,
            num % 10
        };

        for (int i = 0; i < 3; i++) {
            if (digits[i] < 0) {
                view[i].setImage(ImageManager.getDigitOffImage());
            }
            else {
                view[i].setImage(ImageManager.getDigitImage(digits[i]));
            }
        }

        if (value < -99) {
            for (var v : view) {
                v.setImage(ImageManager.getDigitMinusImage());
            }
        }
        else if (value < 0) {
            if (num > 9) {
                view[0].setImage(ImageManager.getDigitMinusImage());
            }
            else if (num > 0) {
                view[1].setImage(ImageManager.getDigitMinusImage());
            }
        }
    }


    public void Reset() {
        generate.setImage(prevSmileImage = ImageManager.getSmileImage());

        grid.Reset();
        ResetScore();
        ResetTime();
    }
    public void Resize() {
        ResizeMenu();
        ResizeControls();
        ResizeGrid();
    }


    private void SetMenu() {
        menuBar = new MenuBar();
        Menu options = new Menu("Options");
        MenuItem newGame = new MenuItem("New game");
        MenuItem quit    = new MenuItem("Exit");
        newGame.setOnAction(e -> Reset() );
        quit.setOnAction(e -> Platform.exit());

        options.getItems().add(newGame);
        options.getItems().add(quit);

        menuBar.getMenus().add(options);

        this.getChildren().add(menuBar);
    }
    // TODO: Resize menu
    public void ResizeMenu() {
        // menuBar.setPrefHeight(this.getHeight() * menuHeightPercent);
    }

    private Image prevSmileImage;
    private void SetControls() {
        scoreView = new ImageView[] {
            new ImageView(ImageManager.getDigitOffImage()),
            new ImageView(ImageManager.getDigitOffImage()),
            new ImageView(ImageManager.getDigitImage(0))
        };

        generate = new ImageView(prevSmileImage = ImageManager.getSmileImage());
        generate.setOnMouseClicked(mouseEvent -> Reset());
        generate.setOnMousePressed(mouseEvent -> generate.setImage(ImageManager.getSmilePressedImage()));
        generate.setOnMouseReleased(mouseEvent -> generate.setImage(prevSmileImage));

        timeView = new ImageView[] {
                new ImageView(ImageManager.getDigitOffImage()),
                new ImageView(ImageManager.getDigitOffImage()),
                new ImageView(ImageManager.getDigitImage(0))
        };

        controls = new BorderPane();
        HBox scoreContainer = new HBox(scoreView);
        controls.setLeft(scoreContainer);
        BorderPane.setAlignment(scoreContainer, Pos.CENTER);
        controls.setCenter(generate);
        BorderPane.setAlignment(generate, Pos.CENTER);
        HBox timeContainer = new HBox(timeView);
        controls.setRight(timeContainer);
        BorderPane.setAlignment(timeContainer, Pos.CENTER);

        this.getChildren().add(controls);
        ResizeControls();
    }
    public void ResizeControls() {
        double val = Math.min(this.getWidth(), this.getHeight()) * controlsHeightPercent;

        for (var view : timeView) {
            view.setFitHeight(val);
            view.setPreserveRatio(true);
        }

        generate.setFitHeight(val);
        generate.setPreserveRatio(true);

        for (var view : scoreView) {
            view.setFitHeight(val);
            view.setPreserveRatio(true);
        }

        double pad = val * paddingPercent;
        controls.setPadding(new Insets(pad, pad, pad, pad));
        controls.autosize();
    }
    private void SetGrid(int rows, int cols, int totMines) {
        this.getChildren().remove(grid);
        grid = new Grid(this, rows, cols, totMines);
        this.getChildren().add(grid);

        ResizeGrid();
    }
    public void ResizeGrid() {
        double gridHeight = this.getHeight() - menuBar.getHeight() - controls.getHeight();
        grid.resize(this.getWidth(), gridHeight);
        grid.ResizeCells(this.getWidth(), gridHeight);

        double pad = Math.min(this.getWidth(), this.getHeight()) * (controlsHeightPercent * paddingPercent);
        grid.setPadding(new Insets(0, pad, pad, pad));
    }

    public void Lose(Cell cell) {
        timeLine.stop();

        if (cell != null)
            cell.setType(Cell.Type.EXPLODED);

        for (var row : grid.getGrid()) {
            for (var col : row) {
                if (col.isMine()) {
                    col.Show();
                }
            }
        }
        grid.setDisable(true);

        generate.setImage(prevSmileImage = ImageManager.getDeadImage());

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sconfitta");
        alert.setHeaderText(null);
        alert.setContentText("Hai perso!");
        alert.showAndWait();
    }
    public void Win() {
        timeLine.stop();

        grid.setDisable(true);

        generate.setImage(prevSmileImage = ImageManager.getSwagImage());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Vittoria");
        alert.setHeaderText(null);
        alert.setContentText("Hai vinto!");
        alert.showAndWait();
    }
}
