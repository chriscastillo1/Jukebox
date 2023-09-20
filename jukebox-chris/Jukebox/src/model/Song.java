package model;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;

/**
 * AUTHOR: Chris Castillo, Anisha Munjal
 */

public class Song implements Serializable {
	private final SimpleStringProperty title;
	private final SimpleStringProperty artist;
	private final SimpleStringProperty duration;
	private final SimpleStringProperty path;

	/**
	 * Constructor for the Song class
	 * 
	 * @param title,    String song title
	 * @param artist,   String artist name
	 * @param duration, String song duration
	 * @param path,     String path for the mp3 file
	 */
	public Song(String title, String artist, String duration, String path) {
		this.title = new SimpleStringProperty(title);
		this.artist = new SimpleStringProperty(artist);
		this.duration = new SimpleStringProperty(duration);
		this.path = new SimpleStringProperty(path);
	}

	/**
	 * Get song title
	 * 
	 * @return title, String song title
	 */
	public String getTitle() {
		return title.get();
	}

	/**
	 * Get song artist
	 * 
	 * @return artist, String song artist
	 */
	public String getArtist() {
		return artist.get();
	}

	/**
	 * Get song duration
	 * 
	 * @return duration, String song duration
	 */
	public String getDuration() {
		return duration.get();
	}

	/**
	 * Get song path
	 * 
	 * @return path, String song path
	 */
	public String getPath() {
		return path.get();
	}

	/**
	 * Get String representation of the Song Object
	 */
	@Override
	public String toString() {
		return "Title: " + title + "; Path: " + path;
	}
}