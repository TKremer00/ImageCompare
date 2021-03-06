package views;

import compareGui.DetailsGui;
import compareGui.DirInputGui;
import compareGui.FileInputGui;
import compareGui.InputMethodGui;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sample.FileHandler;
import sample.Popup;

public class CompareGui extends GridPane {

    private FileHandler fileHandler;

    private DirInputGui dirInputGui;
    private FileInputGui fileInputGui;
    private DetailsGui detailsGui;
    private InputMethodGui inputMethodGui;
    public static StringProperty log = new SimpleStringProperty();

    private ProgressBar progressBar;
    private TextArea output;

    CompareGui() {
        GridPane p = this;
        p.setHgap(10);

        fileHandler = new FileHandler();

        inputMethodGui = new InputMethodGui();
        inputMethodGui.setPrefHeight(80);
        inputMethodGui.setMinWidth(250);

        dirInputGui = new DirInputGui();
        dirInputGui.setPrefHeight(dirInputGui.getSize());

        fileInputGui = new FileInputGui();
        fileInputGui.setPrefHeight(165);

        detailsGui = new DetailsGui();
        detailsGui.setPrefHeight(70);

        output = new TextArea();
        output.setPrefHeight(200);
        output.setPrefWidth(500);
        output.setEditable(true);
        output.setFont(new Font(14));
        //output.textProperty().bind(FileHandler.log);

        p.add(output,1,0,1,5);
        p.add(inputMethodGui,0,0);

        p.add(dirInputGui,0,1);

        p.add(detailsGui,0,2);

        Button btnRun = new Button("Run");
        p.add(btnRun,0,3);

        //Spacing
        p.add(new Text(" "),0,4);

        progressBar = new ProgressBar(0);
        progressBar.setTranslateX(5);
        progressBar.setMaxWidth(230);
        p.add(progressBar,0,5);

        setHalignment(btnRun, HPos.RIGHT);
        setHalignment(progressBar,HPos.CENTER);

        //Event listener Toggle group*/
        inputMethodGui.getSelectInput().selectedToggleProperty().addListener(event -> {
            if(inputMethodGui.getRbDir().isSelected()){
                p.getChildren().remove(fileInputGui);
                p.add(dirInputGui,0,1);
            }else if(inputMethodGui.getRbFile().isSelected()) {
                p.getChildren().remove(dirInputGui);
                p.add(fileInputGui,0,1);
            }
        });

        btnRun.setOnAction(event -> {

            fileHandler.setFileExtension(detailsGui.getSelectedItem());
            String message;

            if(inputMethodGui.getRbFile().isSelected()) {
                message = (fileHandler.compare2Images(fileInputGui.getFile1String(),fileInputGui.getFile2String()) ? "Images are the same" : "Images aren't the same");
                Popup.popup(message);

            } else if (inputMethodGui.getRbDir().isSelected()) {
                fileHandler.compareImages(dirInputGui.getDirString(), dirInputGui.getMethod());

                // When boolean finished is true check if deleted is checked
                fileHandler.finishedCompareProperty().addListener((observableMain, oldValueMain, newValueMain) -> {
                    if(newValueMain) {
                        String text = "Done checking images open txt to see";
                        if(dirInputGui.getRbDelete()) {
                            text = (fileHandler.deleteSameImages(dirInputGui.getDirString()) ? "Double images removed" : "Deleting images failed");
                        }
                        Popup.popup(text);
                    }
                });
            }
        });

        //Set progress bar
        fileHandler.progressProperty().addListener((observable, oldValue, newValue) -> {
            double value = newValue.doubleValue();
            progressBar.setProgress((value == fileHandler.getTotalImages() - 1) ? 1 : ((1 / (float)fileHandler.getTotalImages()) * value));
        });
    }

    public void AddToLog(String text){
        Platform.runLater(() -> output.setText(text + "\n" + output.getText()));
    }
}
