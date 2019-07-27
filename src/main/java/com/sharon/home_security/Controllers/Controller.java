package com.sharon.home_security.Controllers;

import com.sharon.home_security.Model.OpenCV;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private Button cameraButton;
    @FXML private ImageView originalFrame;
    @FXML private CheckBox haarClassifier;
    @FXML private CheckBox lbpClassifier;

    private boolean active;

    private OpenCV openCV;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        openCV = new OpenCV();
        active=false;
    }

    public void startCamera(ActionEvent event){
        if(!active) this.cameraButton.setText("Stop Capture");
        else this.cameraButton.setText("Start Capture");
        openCV.startCamera(this::updateImage);
    }

    @FXML
    protected void haarSelected(ActionEvent event)
    {
        // check whether the lpb checkbox is selected and deselect it
        if (this.lbpClassifier.isSelected())
            this.lbpClassifier.setSelected(false);
        openCV.setClassifier(1);
    }

    @FXML
    protected void lbpSelected(ActionEvent event)
    {
        // check whether the haar checkbox is selected and deselect it
        if (this.haarClassifier.isSelected())
            this.haarClassifier.setSelected(false);

        openCV.setClassifier(0);
    }

    private void updateImage(Image image){
        Platform.runLater(()-> originalFrame.imageProperty().set(image));
    }




}
