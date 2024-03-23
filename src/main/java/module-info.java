module com.stepanew.goodmarksman {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires ch.qos.logback.core;
    requires org.slf4j;
    requires com.google.gson;

    opens com.stepanew.goodmarksman to javafx.fxml;
    exports com.stepanew.goodmarksman;
    exports com.stepanew.goodmarksman.models;
    opens com.stepanew.goodmarksman.models to javafx.fxml;
}

