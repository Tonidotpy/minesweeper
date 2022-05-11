module com.tonidotpy.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.tonidotpy.minesweeper to javafx.fxml;
    exports com.tonidotpy.minesweeper;
}