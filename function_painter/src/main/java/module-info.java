module com.painter.function_painter {
    requires javafx.controls;
    requires javafx.fxml;
    requires jep.java;
    requires java.desktop;


    opens com.painter.function_painter to javafx.fxml;
    exports com.painter.function_painter;
}