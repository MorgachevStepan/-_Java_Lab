module com.stepanew.goodmarksman {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires org.slf4j;
    requires com.google.gson;
    requires org.hibernate.orm.core;
    requires org.postgresql.jdbc;
    requires java.persistence;
    requires java.naming;
    requires java.sql;

    opens com.stepanew.goodmarksman to javafx.fxml, org.postgresql.jdbc;
    opens com.stepanew.goodmarksman.models to javafx.fxml, com.google.gson;
    opens com.stepanew.goodmarksman.server to javafx.fxml, com.google.gson, org.postgresql.jdbc;
    opens com.stepanew.goodmarksman.server.response to com.google.gson;
    opens com.stepanew.goodmarksman.store to java.persistence, org.hibernate.orm.core;

    exports com.stepanew.goodmarksman;
    exports com.stepanew.goodmarksman.models;
    exports com.stepanew.goodmarksman.server;
    exports com.stepanew.goodmarksman.server.response;
    exports com.stepanew.goodmarksman.store;
}