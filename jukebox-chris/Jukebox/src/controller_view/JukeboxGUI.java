package controller_view;

import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.JukeboxAccount;
import model.PlayList;
import model.Song;
import model.SongList;

/**
 * AUTHOR: Chris Castillo, Anisha Munjal
 */
public class JukeboxGUI extends Application {
	// Starts the Jukebox Application
	public static void main(String[] args) {
		launch(args);
	}

	private JukeboxAccount currentUserLoggedIn;

	private LoginCreateAccountPane loginPane;

	// Central Playlist for Jukebox
	private PlayList playList = new PlayList();

	// Pane styles and layouts for GUI
	private BorderPane outside;

	private ObservableList<Song> observableList;

	// Views for PlayList and SongList
	private GridPane listContainer;
	private TableView<Song> songListView;
	private TableView<Song> playListView;

	// Container and Button for Logging in / Logging Out and PLAY button
	private GridPane loginCreateContainer;
	private Button loginCreate;
	private Button logout;
	private ToggleButton playButton = new ToggleButton("PAUSED");

	private Stage primaryStage;

	// Buttons for Adding Song and Label for Song Counter
	private HBox addSongNumSongContainer = new HBox(10);
	private Button addSong = new Button("Add Song");
	private Label numSongsAdded = new Label("Num Songs Added Today: ");

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		startNewPlayListOrReadSave();
		initializeGUI(primaryStage);
		initializeLoginCreate();

		initializeListViews();
		setupPlayListView();
		setupSongListView();

		initializeAddSongNumSong();

		initializeLogout();

		registerHandlers();

		// Setups the scene and stage of the main GUI
		Scene scene = new Scene(outside, 600, 900);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();

		// Auto starts if PlayList at startup is NOT empty
		if (!observableList.isEmpty()) {
			playButton.fire();
		}

		saveAndExitConfirmation(primaryStage);
	}

	private void registerHandlers() {
		loginCreate.setOnAction((event) -> {
			Alert alert = new Alert(AlertType.NONE);
			Stage stage = new Stage();
			loginPane = new LoginCreateAccountPane(stage);

			alert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

			alert.getDialogPane().setContent(loginPane);
			alert.getDialogPane().setPadding(new Insets(10));

			alert.setHeaderText("Login / Create Account Then Hit Close / Exit");
			Optional<ButtonType> result = alert.showAndWait();

			// If user just exits without logging in, DO NOTHING
			if (result.get() != ButtonType.CLOSE) {
				return;
			}

			alert.close();
			currentUserLoggedIn = loginPane.getCurrentUserLoggedIn();

			if (currentUserLoggedIn != null) {
				primaryStage.setTitle("Jukebox: Welcome "
						+ currentUserLoggedIn.getStudentName() + "!");

				// Sets Add Song button to Visible ONCE user is logged in
				addSongNumSongContainer.setVisible(true);

				// Hides login / create button
				loginCreate.setVisible(false);

				// Shows Logout button
				logout.setVisible(true);

				// Lets user interact with playbutton
				// playButton.setMouseTransparent(false);

				numSongsAdded.setText("Num Songs Added Today: "
						+ currentUserLoggedIn.getNumSongsPlayedToday());
			}
		});

		playButton.setOnAction((event) -> {
			boolean isSelected = playButton.isSelected();

			// If there are no songs in playlist, do NOTHING
			if (observableList.isEmpty()) {
				playButton.setText("PAUSED");
				playButton.setSelected(false);
				return;
			}

			// Highlights current song playing in GRAY
			playListView.getSelectionModel().select(0);

			if (isSelected) {
				playButton.setText("PLAYING");
				playList.startPlayList(observableList, playButton);

			} else {
				playList.pause();
				playButton.setText("PAUSED");

			}
		});

		logout.setOnAction((event) -> {
			currentUserLoggedIn = null;

			// Resets Title to Jukebox
			primaryStage.setTitle("Jukebox");

			// Shows Login / Create Account Button
			loginCreate.setVisible(true);

			// Hides logout button
			logout.setVisible(false);

			// Hides Add Song button
			addSongNumSongContainer.setVisible(false);

			// Play Button is Hidden so YOU MUST BE LOGGED IN
			playButton.setMouseTransparent(true);

			// Resets Num Songs
			numSongsAdded.setText("Num Songs Added Today: ");
		});

		addSong.setOnAction((event) -> {
			// If user tries to add more than 3 songs, show alert
			if (currentUserLoggedIn.getNumSongsPlayedToday() == 3) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Max Songs Added For Today");
				alert.showAndWait();
			}

			// Selects first song in playlist
			Song song = songListView.getSelectionModel().getSelectedItem();
			if (song != null) {
				currentUserLoggedIn.addSong(song, observableList);

				if (!playButton.isSelected()) {
					playButton.fire();
				}

				// Updates number of songs played by user
				numSongsAdded.setText("Num Songs Added Today: "
						+ currentUserLoggedIn.getNumSongsPlayedToday());
			}
		});
	}

	/**
	 * When starting Jukebox, allows to start Fresh or Read from existing
	 * playlist
	 */
	private void startNewPlayListOrReadSave() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText("Click OK to start with a saved PlayList");
		alert.setContentText("Click CANCEL to start with an empty PlayList");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ButtonType.OK) {
			// Reads PlayList from Save
			ArrayList<Song> list = playList.readPlayListFromSaveFile();
			observableList = FXCollections.observableArrayList(list);

		} else {
			// Starts with fresh observableList
			observableList = FXCollections.observableArrayList();
			
			// Reset number of songs played for all users
			JukeboxAccount jukeboxAccount = new JukeboxAccount();
			jukeboxAccount.resetNumSongsPlayedAllUsers();
		}
	}

	/**
	 * Initializes the start of the JukeboxGUI Sets Stage Title and Initializes
	 * Outside Pane
	 */
	private void initializeGUI(Stage stage) {
		stage.setTitle("Jukebox");
		stage.initStyle(StageStyle.DECORATED);
		outside = new BorderPane();
	}

	/**
	 * Adds the Button Login / Create and sets the outside container to TOP
	 */
	private void initializeLoginCreate() {
		// Makes a HBox to make formatting look nicer. HBox is the container
		// for the button.
		// The Login / Create button is added to HBox, which is added to Top
		loginCreateContainer = new GridPane();
		loginCreateContainer.setPadding(new Insets(10, 10, 10, 70));

		loginCreate = new Button("Login / Create");

		loginCreateContainer.add(loginCreate, 0, 0);

		// ONLY LOGGED IN CAN USER USE PLAY BUTTON
		playButton.setMinSize(70, 20);
		playButton.setMouseTransparent(true);

		loginCreateContainer.add(playButton, 1, 0);

		// Sets gap between Login / Create button and PLAY / PAUSE Button
		loginCreateContainer.setHgap(298);

		outside.setTop(loginCreateContainer);
	}

	private void initializeListViews() {
		listContainer = new GridPane();
		listContainer.setAlignment(Pos.CENTER);
		listContainer.setPadding(new Insets(0, 10, 10, 10));
	}

	/**
	 * Creates a ListView of Songs that are currently in the playlist
	 */
	@SuppressWarnings("unchecked")
	private void setupPlayListView() {
		playListView = new TableView<Song>();

		playListView.setMouseTransparent(true);

		// Creates Column for SongTitle and retrieves ObservableList of songs
		TableColumn<Song, String> songTitle = new TableColumn<Song, String>(
				"Title");
		songTitle.setMinWidth(258);
		songTitle.setMaxWidth(258);
		songTitle.setCellValueFactory(
				new PropertyValueFactory<Song, String>("title"));

		// Creates Column for SongArtist and retrieves ObservableList of songs
		TableColumn<Song, String> songArtist = new TableColumn<Song, String>(
				"Artist");
		songArtist.setMinWidth(120);
		songArtist.setMaxWidth(120);
		songArtist.setCellValueFactory(
				new PropertyValueFactory<Song, String>("artist"));

		// Creates Column for SongArtist and retrieves ObservableList of songs
		TableColumn<Song, String> songTime = new TableColumn<Song, String>(
				"Time");
		songTime.setMinWidth(50);
		songTime.setMaxWidth(50);
		songTime.setCellValueFactory(
				new PropertyValueFactory<Song, String>("duration"));

		// Sets the observableList (SongList returns) and adds to TableView
		playListView.setItems(observableList);
		playListView.getColumns().addAll(songTitle, songArtist, songTime);

		listContainer.add(playListView, 1, 0);
	}

	/**
	 * Sets up the TableView for list of ALL songs available to the user Each
	 * column can be sorted ascending / descending
	 */
	@SuppressWarnings("unchecked")
	private void setupSongListView() {
		songListView = new TableView<Song>();

		// Creates Column for SongTitle and retrieves ObservableList of songs
		TableColumn<Song, String> songTitle = new TableColumn<Song, String>(
				"Title");
		songTitle.setMinWidth(258);
		songTitle.setMaxWidth(258);
		songTitle.setCellValueFactory(
				new PropertyValueFactory<Song, String>("title"));

		// Creates Column for SongArtist and retrieves ObservableList of songs
		TableColumn<Song, String> songArtist = new TableColumn<Song, String>(
				"Artist");
		songArtist.setMinWidth(120);
		songArtist.setMaxWidth(120);
		songArtist.setCellValueFactory(
				new PropertyValueFactory<Song, String>("artist"));

		// Creates Column for SongArtist and retrieves ObservableList of songs
		TableColumn<Song, String> songTime = new TableColumn<Song, String>(
				"Time");
		songTime.setMinWidth(50);
		songTime.setMaxWidth(50);
		songTime.setCellValueFactory(
				new PropertyValueFactory<Song, String>("duration"));

		// Sets the observableList (SongList returns) and adds to TableView
		songListView.setItems(SongList.getList());
		songListView.getColumns().addAll(songTitle, songArtist, songTime);

		listContainer.add(songListView, 1, 2);
	}

	/**
	 * Initialized Add Song and song counter for GUI. BUT MUST BE LOGGED IN TO
	 * SEE IT
	 */
	private void initializeAddSongNumSong() {
		addSongNumSongContainer.getChildren().addAll(addSong, numSongsAdded);
		// ONLY USERS LOGGED IN CAN ADD SONGS TO PLAYLIST. INCREMENT COUNT
		addSongNumSongContainer.setVisible(false);
		addSongNumSongContainer.setAlignment(Pos.CENTER_LEFT);
		addSongNumSongContainer.setPadding(new Insets(10, 10, 10, 0));

		listContainer.add(addSongNumSongContainer, 1, 1);

		outside.setCenter(listContainer);
	}

	/**
	 * Creates a logout button for users
	 */
	private void initializeLogout() {
		HBox logoutContainer = new HBox();
		logout = new Button("Logout");
		logout.setMinSize(160, 20);

		// NOTE: UPON INITIALIZING, LOGOUT BUTTON IS NOT VISIBLE
		// ONLY WHEN USER LOGS IN IS IT VISIBLE
		logout.setVisible(false);

		logoutContainer.getChildren().add(logout);
		logoutContainer.setAlignment(Pos.CENTER);
		logoutContainer.setPadding(new Insets(0, 0, 10, 0));

		outside.setBottom(logoutContainer);
	}

	/**
	 * Shows a Pop-up of alert saving playlist and exiting
	 */
	private void saveAndExitConfirmation(Stage stage) {
		stage.setOnCloseRequest((event) -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Save And Exit");
			alert.setHeaderText("Save PlayList");
			alert.setContentText("Press Cancel to Discard Changes");

			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == ButtonType.OK) {
				playList.savePlayListToSaveFile(observableList);
			}

			Platform.exit();
			System.exit(0);
		});
	}
}