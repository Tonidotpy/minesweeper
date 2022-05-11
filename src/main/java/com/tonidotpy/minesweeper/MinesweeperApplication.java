package com.tonidotpy.minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MinesweeperApplication extends Application {
    private Scene scene;
    private Game game;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Minesweeper");

        game = new Game();
        scene = new Scene(game);
        stage.setScene(scene);
        stage.show();

        game.Resize();
    }

    public static void main(String[] args) {
        launch();
    }
}