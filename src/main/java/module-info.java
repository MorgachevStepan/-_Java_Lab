module com.stepanew.goodmarksman {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires ch.qos.logback.core;
    requires org.slf4j;

    opens com.stepanew.goodmarksman to javafx.fxml;
    exports com.stepanew.goodmarksman;
}

