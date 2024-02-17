module com.stepanew.goodmarksman {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;

    opens com.stepanew.goodmarksman to javafx.fxml;
    exports com.stepanew.goodmarksman;
}

