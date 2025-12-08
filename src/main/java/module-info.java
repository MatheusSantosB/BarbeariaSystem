module com.barbearia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.barbearia to javafx.fxml;
    opens com.barbearia.controller to javafx.fxml;
    opens com.barbearia.model.entity to javafx.base;

    exports com.barbearia;
    exports com.barbearia.controller;
}