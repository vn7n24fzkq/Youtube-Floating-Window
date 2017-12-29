package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.awt.*;


public class FloatingWindow extends Application {
    private final static int BORDER_SIZE = 40;
    private WebView webview;
    private double height = 250, width = 400;
    private Stage decoStage, tranStage;
    private Scene playerScene;
    private Timeline cursorTimer;
    public long cursorTime = 250;

    public FloatingWindow(String title) {
        launch("FloatingWindow");
    }

    public FloatingWindow() {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initTranStage("");
        initDecoStage(primaryStage);

        decoStage.show();
    }

    private WebView generateWebView(String url) {
        webview = new WebView();
        webview.setPrefSize(width, height);
        webview.getEngine().load(url);
        webview.setVisible(true);


        //set mouse event
        webview.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                decoStage.setX(decoStage.getX());//set x y let decoStage show at correct position
                decoStage.setY(decoStage.getY());
                ////make decotage can show behind tranStage
                decoStage.setAlwaysOnTop(true);//set x y let decoStage show at correct position
                decoStage.show();
                decoStage.setAlwaysOnTop(false);
            }
        });
        webview.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                makeTimer(cursorTime);
            }
        });
        webview.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseButton.PRIMARY && !decoStage.isShowing()) {
                    decoStage.setX(decoStage.getX());//set x y let decoStage show at correct position
                    decoStage.setY(decoStage.getY());
                    decoStage.hide();
                    decoStage.show();
                }
            }
        });
        return webview;
    }

    public void fitTranStage() {
        if (decoStage.isShowing() == true) {
            tranStage.setX(decoStage.getX() + decoStage.getScene().getX());
            tranStage.setY(decoStage.getY() + decoStage.getScene().getY());
            tranStage.setHeight(decoStage.getScene().getHeight());
            tranStage.setWidth(decoStage.getScene().getWidth());
        }
    }

    public void initTranStage(String url) {
        if (tranStage != null)
            tranStage.impl_setPrimary(false);
        tranStage = new Stage();
        tranStage.initStyle(StageStyle.TRANSPARENT);
        tranStage.setAlwaysOnTop(true);
        tranStage.setScene(new Scene(generateWebView(url)));
        tranStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                closeTranStage();
            }
        });
    }

    public void initDecoStage(Stage primaryStage) {
        decoStage = primaryStage;

        playerScene = new Scene(initUserScene());
        decoStage.setScene(playerScene);
        decoStage.setHeight(height);
        decoStage.setWidth(width);

        //set listener let tranStage can auto size to same with decoStage
        decoStage.xProperty().addListener((obs, oldVal, newVal) -> {
            tranStage.setX(decoStage.getX() + decoStage.getScene().getX());
        });
        decoStage.yProperty().addListener((obs, oldVal, newVal) -> {
            tranStage.setY(decoStage.getY() + decoStage.getScene().getY());
        });
        decoStage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
            tranStage.setWidth(decoStage.getScene().getWidth());
        });

        decoStage.getScene().heightProperty().addListener((obs, oldVal, newVal) -> {
            tranStage.setHeight(decoStage.getScene().getHeight());
        });
        //close stage
        decoStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                if (tranStage.isShowing()) {
                    closeTranStage();
                    we.consume();
                } else {
                    Platform.exit();
                }
            }
        });
        decoStage.onShowingProperty().addListener((obs, oldVal, newVal) -> {
            decoStage.requestFocus();
        });
    }
    public Parent initUserScene() {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10;");
        // Set the border-style of the VBox
        root.setStyle("-fx-border-style: solid inside;");
        // Set the border-width of the VBox
        root.setStyle("-fx-border-width: 2;");
        // Set the border-insets of the VBox
        root.setStyle("-fx-border-insets: 5;");
        // Set the border-radius of the VBox
        root.setStyle("-fx-border-radius: 5;");
        // Set the size of the VBox

        Label label = new Label("Youtube URL");
        Button commit = new Button("commit");
        TextField urlFld = new TextField("put url in here");

        commit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                initTranStage(youtubeToEmbed(urlFld.getText()));
                showTranStage();
            }
        });
        // Add the children to the VBox
        root.getChildren().addAll(label, urlFld, commit);
        return root;
    }

    public void showTranStage() {
        tranStage.show();
        fitTranStage();
    }

    public void closeTranStage() {
        tranStage.hide();
        stopWebview();
    }

    private void stopWebview() {
        webview.getEngine().load(null);//make web engine stop
    }

    private void makeTimer(final long time) {
        if (cursorTimer != null) {
            cursorTimer.stop();
        }
        cursorTimer = new Timeline(new KeyFrame(
                Duration.millis(time),
                ae -> hideDecoStage()));
        cursorTimer.play();
    }

    private void hideDecoStage() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        //hide decoStage if cursor is out of border from BORDER_SIZE
        if (!(pointerInfo.getLocation().x < tranStage.getX() + tranStage.getWidth() + BORDER_SIZE
                && pointerInfo.getLocation().x > tranStage.getX() - BORDER_SIZE
                && pointerInfo.getLocation().y < tranStage.getY() + tranStage.getHeight() + BORDER_SIZE
                && pointerInfo.getLocation().y > tranStage.getY() - BORDER_SIZE)) {
            decoStage.setX(decoStage.getX());
            decoStage.setY(decoStage.getY());
            decoStage.hide();
            cursorTimer.stop();
        }
    }
    public String youtubeToEmbed(String url){//turn youtube url to embed url
        String embedUrl = url;
        if(url.contains("list")){
            embedUrl = url.replace("www.youtube.com/","www.youtube.com/embed/");
        }else if(url.contains("watch?v=")){
            embedUrl = url.replace("www.youtube.com/watch?v=", "www.youtube.com/embed/");
            if(embedUrl.contains("&"))
            embedUrl.substring(0,embedUrl.indexOf("&"));
        }else if(embedUrl.contains("youtu.be")){
            embedUrl = url.replace("youtu.be/","www.youtube.com/embed/");
        }
        return  embedUrl;
    }
}

