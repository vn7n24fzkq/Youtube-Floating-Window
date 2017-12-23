package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class FloatingWindow extends Application {
    Stage stage;

    public FloatingWindow() {
        //     this("");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        WebView webview = new WebView();
        webview.setPrefSize(350, 250);
        webview.getEngine().load("https://www.youtube.com/embed/qag4ewos4TE");
        webview.setContextMenuEnabled(true);
        //    primaryStage.initStyle(StageStyle.UNDECORATED);
        webview.setVisible(true);


        Scene innerScene = new Scene(webview);
        primaryStage.setScene(innerScene);

//**set event
        final Delta dragDelta = new Delta();
        webview.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = primaryStage.getX() - mouseEvent.getScreenX();
                dragDelta.y = primaryStage.getY() - mouseEvent.getScreenY();
            }
        });
        webview.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isInMoveControlArea(mouseEvent.getScreenY())) {
                    mouseEvent.consume();
                }
            }
        });
        webview.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isInMoveControlArea(mouseEvent.getScreenY())) {
                    primaryStage.setX(mouseEvent.getScreenX() + dragDelta.x);
                    primaryStage.setY(mouseEvent.getScreenY() + dragDelta.y);
                }
            }
        });
        webview.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isInMoveControlArea(mouseEvent.getScreenY())) {
                    //  primaryStage.setX(mouseEvent.getScreenX() + dragDelta.x);
                    //  primaryStage.setY(mouseEvent.getScreenY() + dragDelta.y);
                }
                System.out.println(isInMoveControlArea(mouseEvent.getScreenY()));
                //  System.out.println("X:"+mouseEvent.getScreenX()+"|Y"+mouseEvent.getScreenY() );
            }
        });
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

    public FloatingWindow(String title) {
        launch("");
    }

    public boolean isInMoveControlArea(double y) {
        return y < (stage.getY() + Delta.moveControlSize);
    }
}

class Delta {
    double x, y;
    static double moveControlSize = 40;
}
