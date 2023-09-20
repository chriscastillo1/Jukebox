package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;

import javafx.collections.ObservableList;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * 
 * @author Chris Castillo, Anisha Munjal
 *
 */
public class PlayList {

	private final static String FILENAME = "playlist.ser";
	private LinkedList<Song> list = new LinkedList<Song>();
	private Song currentSong;
	private Song pausedSong;
	private Media media;
	private MediaPlayer mediaPlayer;
	private ToggleButton playButton;

	/**
	 * Add songs to end of LinkedList. Ensures FIFO ORDER
	 * 
	 * @param songToAdd, the Song to add
	 */
	public void queueUpNextSong(Song songToAdd) {
		list.add(songToAdd);
	}

	/**
	 * Pause play list
	 */
	public void pause() {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			pausedSong = currentSong;
		}
	}

	/**
	 * Play song
	 * 
	 * @param song,           the Song to play
	 * @param observableList, the ObservableList<Song> of songs
	 */
	private void play(Song song, ObservableList<Song> observableList) {
		String path = song.getPath();

		File file = new File(path);
		URI uri = file.toURI();

		media = new Media(uri.toString());
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();

		mediaPlayer.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {

				mediaPlayer.stop();
				observableList.remove(0);
				if (!observableList.isEmpty()) {
					currentSong = observableList.get(0);

					// Adds 2 second delay between media songs
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					play(currentSong, observableList);
				} else {
					playButton.setText("PAUSED");
					playButton.setSelected(false);
				}
			}
		});
	}

	/**
	 * Start play list
	 * 
	 * @param observableList, Observable list of songs
	 * @param playButton,     the ToggleButton
	 */
	public void startPlayList(ObservableList<Song> observableList,
			ToggleButton playButton) {

		this.playButton = playButton;

		if (pausedSong != null) {
			currentSong = pausedSong;
			pausedSong = null;
		} else {
			currentSong = observableList.get(0);
		}
		play(currentSong, observableList);
	}

	/**
	 * Save play list to file
	 * 
	 * @param songList, the ObservableList<Song> to save to file
	 */
	public void savePlayListToSaveFile(ObservableList<Song> songList) {
		ArrayList<String> songPaths = new ArrayList<String>();

		for (Song song : songList) {
			songPaths.add(song.getPath());
		}

		try {
			FileOutputStream bytes = new FileOutputStream(FILENAME);
			ObjectOutputStream outFile = new ObjectOutputStream(bytes);

			outFile.writeObject(songPaths);
			outFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read saved play list from file
	 * 
	 * @return ArrayList<Song>, the array list of songs
	 */
	public ArrayList<Song> readPlayListFromSaveFile() {
		FileInputStream rawBytes;
		ArrayList<String> songPaths;
		ArrayList<Song> songs = new ArrayList<Song>();

		try {
			rawBytes = new FileInputStream(FILENAME);
			ObjectInputStream inFile = new ObjectInputStream(rawBytes);

			songPaths = (ArrayList<String>) inFile.readObject();

			inFile.close();

			for (String songPath : songPaths) {
				songs.add(SongList.findSong(songPath));
			}

			return songs;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// Returns null for compiling
		return null;
	}
}