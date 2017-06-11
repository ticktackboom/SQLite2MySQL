package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import transform.SqliteConvert;

public class Controller implements Initializable {

	/*
	 * Private attributes. Internal usage.
	 */

	private File fileChoosed;
	private boolean hasChanged;

	/*
	 * Window IDs.
	 */

	@FXML
	private TextField fileSelected;

	@FXML
	private TextArea outputText;

	/**
	 * 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fileChoosed = null;
		hasChanged = false;
	}

	/*
	 * Window actions.
	 */

	@FXML
	public void browseFile(ActionEvent e) {
		System.out.println(e.getSource().getClass().getSuperclass());
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose your sqlite file");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQLite files", "*.sqlite", "*.db"));
		if (fileChoosed != null && fileChoosed.exists()) {
			fileChooser.setInitialDirectory(new File(fileChoosed.getParent()));
		}
		fileChoosed = fileChooser.showOpenDialog(outputText.getScene().getWindow());
		if (fileChoosed != null) {
			fileSelected.setText(fileChoosed.toString());
		}
	}

	@FXML
	public void convertStart() {
		if (fileChoosed == null || !fileChoosed.exists())
			return;
		try {
			SqliteConvert conversion = new SqliteConvert(fileChoosed);
			outputText.setText(conversion.getMysqlSyntax());
		} catch (FileNotFoundException | SQLException e) {
			outputText.setText(e.getMessage());
		}
	}

	@FXML
	public void changed() {
	}

}
