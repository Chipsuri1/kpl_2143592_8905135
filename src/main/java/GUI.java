import base.Configuration;
import base.FileReader;
import corporateNetwork.CorporateNetwork;
import entitys.HSQLDB;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.A;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI extends Application{
    private CorporateNetwork corporateNetwork;
    private FileReader fileReader;

    private App application;

    public GUI(){
        corporateNetwork = new CorporateNetwork();
        fileReader = new FileReader();
        application = new App();
    }

    public void start(Stage primaryStage) {
        //Using Mülli db zeug
        HSQLDB.instance.setupDatabase();
        primaryStage.setTitle("MSA | Mergentheim/Mosbach Security Agency");

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.setSpacing(10);
        hBox.setStyle("-fx-background-color: #336699;");

        Button executeButton = new Button("Execute");
        executeButton.setPrefSize(100, 20);

        Button closeButton = new Button("Close");
        closeButton.setPrefSize(100, 20);

        TextArea commandLineArea = new TextArea();
        commandLineArea.setWrapText(true);

        TextArea outputArea = new TextArea();
        outputArea.setWrapText(true);
        outputArea.setEditable(false);

        executeButton.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
                String result = executeCommand(commandLineArea.getText());
                if(result != null){
                    outputArea.setText(result);
                }else{
                    System.out.println("Result is null");
                }
            }
        });

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                System.out.println("[close] pressed");
                HSQLDB.instance.shutdown();
                System.exit(0);
            }
        });

        hBox.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            public void handle(javafx.scene.input.KeyEvent event) {
                switch (event.getCode()) {
                    case F3:
                        //set debug mode
                        System.out.println("F3 is pressed");
                        if(Configuration.instance.debugMode){
                            Configuration.instance.debugMode = false;
                        }else {
                            Configuration.instance.debugMode = true;
                        }
                        break;
                    case F5:
                        //run
                        String result = executeCommand(commandLineArea.getText());
                        if(result != null){
                            outputArea.setText(result);
                        }else{
                            System.out.println("Result is null");
                        }
                        break;
                    case F8:
                        //Show latest logfile
                        System.out.println("The latest logfile is showed in the output area");
                        outputArea.setText(fileReader.readLogFile());
                        break;
                }
            }
        });

        hBox.getChildren().addAll(executeButton, closeButton);

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        vbox.getChildren().addAll(hBox, commandLineArea, outputArea);

        Scene scene = new Scene(vbox, 950, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private String executeCommand(String input){
        String result = application.executeCommands(input);
        System.out.println("Command is executed");
        return result;
    }

}