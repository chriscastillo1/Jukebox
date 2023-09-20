package demoMediaPlayer;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.PlayList;
import model.Song;

/**
 * 
 * @author Chris Castillo, Anisha Munjal
 *
 */
public class PlayThreeSongs extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		PlayList playList = new PlayList();

		Song test1 = new Song("Caught a Pokémon!", "Game Freak", "00:05",
				"songfiles/Capture.mp3");
		Song test2 = new Song("Danse Macabre - Violin Hook", "Kevin MacLeod",
				"00:34", "songfiles/LopingSting.mp3");

		playList.queueUpNextSong(test1);
		playList.queueUpNextSong(test2);

		BorderPane pane = new BorderPane();
		pane.setCenter(new Label("Play three songs"));
		// Put the pane in a sized Scene and show the GUI
		Scene scene = new Scene(pane, 255, 85);
		stage.setScene(scene);
		// Don't forget to show the running app:
		stage.show();
	}

}
