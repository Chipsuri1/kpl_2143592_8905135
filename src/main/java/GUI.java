import base.Configuration;
import base.FileReader;
import corporateNetwork.AppForGUI;
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

public class GUI extends Application{
    private FileReader fileReader;
    private AppForGUI application;

    public GUI(){
        fileReader = new FileReader();
        application = new AppForGUI();
    }

    public void start(Stage primaryStage) {
        HSQLDB.instance.setupDatabase();
        application.setupData();
        application.setupData();
        application.setupAlgo();
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

        commandLineArea.setText("send message \"vaccine for covid is stored in building abc\" from branch_hkg to branch_wuh using shift and keyfile keyfile.json");

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

        commandLineArea.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            public void handle(javafx.scene.input.KeyEvent event) {
                //set debug mode
                //run
                //Show latest logfile
                switch (event.getCode()) {
                    case F3 -> {
                        if (Configuration.instance.debugMode) {
                            Configuration.instance.debugMode = false;
                        } else {
                            Configuration.instance.debugMode = true;
                        }
                        outputArea.setText("Debug mode is: " + Configuration.instance.debugMode);
                    }
                    case F5 -> {
                        String result = executeCommand(commandLineArea.getText());
                        if (result != null) {
                            outputArea.setText(result);
                        } else {
                            System.out.println("Result is null");
                        }
                    }
                    case F8 -> {
                        System.out.println("The latest logfile is showed in the output area");
                        outputArea.setText(fileReader.readLogFile());
                    }
                }
            }
        });

        hBox.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            public void handle(javafx.scene.input.KeyEvent event) {
                //set debug mode
                //run
                //Show latest logfile
                switch (event.getCode()) {
                    case F3 -> {
                        if (Configuration.instance.debugMode) {
                            Configuration.instance.debugMode = false;
                        } else {
                            Configuration.instance.debugMode = true;
                        }
                        outputArea.setText("Debug mode is: " + Configuration.instance.debugMode);
                    }
                    case F5 -> {
                        String result = executeCommand(commandLineArea.getText());
                        if (result != null) {
                            outputArea.setText(result);
                        } else {
                            outputArea.setText("");
                        }
                    }
                    case F8 -> {
                        System.out.println("The latest logfile is showed in the output area");
                        outputArea.setText(fileReader.readLogFile());
                    }
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