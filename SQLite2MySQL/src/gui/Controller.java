package gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class Controller implements Initializable {
	
	/*
	 * Private attributes. Internal usage.
	 */
	
	private File fileChoosed;
	
	/*
	 * Window IDs.
	 */
	
	@FXML
	private TextField fileSelected;
	
	/**
	 * 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * Window actions.
	 */
	
	@FXML
	public void browseFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose your sqlite file");
		fileChoosed = fileChooser.showOpenDialog(null);
		if (fileChoosed != null) {
			fileSelected.setText(fileChoosed.toString());
		}
	}

}
