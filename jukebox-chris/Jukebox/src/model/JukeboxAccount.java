
/**
 * AUTHOR: Chris Castillo, Anisha Munjal 
 * FILE: JukeboxAccount.java 
 * ASSIGNMENT: The Cashless Jukebox
 * COURSE: CSC 335 Spring 2023
 * PURPOSE: This class implements the JukeboxAccount. This class contains
 * methods that allow students to create new account. It contains methods for
 * authenticating user credentials. The credentials are saved in a log file 
 * and retrieved to authenticate accounts.
 */

package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class JukeboxAccount {
	private final static String FILENAME = "credentials.ser";
	private static final int DAILY_SONG_LIMIT = 3;
	private LocalDate today;

	private boolean isAccountAuthenticated;
	private String userName;
	private HashMap<String, Credentials> credentials;
	private LocalDate lastLoginDate;
	private int numSongsLastPlayed;

	private LocalDate currentDate = LocalDate.now();
	private int numSongsPlayedToday = 0;

	/**
	 * Constructor for the JukeboxAccount class
	 * 
	 * @param name,       String student name
	 * @param password,   String password for the JukeboxAccount
	 * @param newAccount, boolean True when new account is to be created
	 */
	public JukeboxAccount(String name, String password, boolean newAccount) {
		this.userName = name;
		today = LocalDate.now();
		Alert alert = new Alert(AlertType.ERROR);

		// retrieve persistent account information from credentials file
		credentials = readCredentialsFromFile();

		if (newAccount == true) {
			// check if user already has an account
			if (credentials.containsKey(name)) {
				alert.setHeaderText("User " + name + " already exists!");
				alert.showAndWait();
			} else {
				Credentials userCredential = new Credentials();
				userCredential.setPassword(password);
				userCredential.setLastLoginDate(null);
				userCredential.setNumSongsLastPlayed(0);
				credentials.put(name, userCredential);

				// When making new account, account is authenticated
				isAccountAuthenticated = true;

				// store new account information to credentials file
				writeCredentialsToFile(credentials);
			}
		} else {
			// not a new account so verify credentials are valid
			if (credentials.containsKey(name)) {
				if (!credentials.get(name).getPassword().equals(password)) {
					alert.setHeaderText("Password invalid for user " + name);
					alert.showAndWait();

					isAccountAuthenticated = false;
				} else {
					isAccountAuthenticated = true;

					Credentials userCredential = credentials.get(name);
					lastLoginDate = userCredential.getLastLoginDate();
					numSongsLastPlayed = userCredential.getNumSongsLastPlayed();

					// user logged in for the first time after account created
					if (lastLoginDate == null) {
						lastLoginDate = currentDate;
					}
					checkLastLoginAndNumSongsPlayed();
				}
			} else {
				alert.setHeaderText("User " + name + " not found!");
				alert.showAndWait();
				isAccountAuthenticated = false;
			}
		}
	}
	
	
	/**
	 * Default constructor for JukeboxAccount class
	 */
	public JukeboxAccount() {
		credentials = readCredentialsFromFile();
	}

	/**
	 * Reset number of songs played for all user accounts
	 */
	public void resetNumSongsPlayedAllUsers() {

		// retrieve persistent account information from credentials file
		HashMap<String, Credentials> userCreds = readCredentialsFromFile();

		for (String key : userCreds.keySet()) {
			Credentials creds = userCreds.get(key);
			creds.setNumSongsLastPlayed(0);
			userCreds.put(key, creds);
		}

		writeCredentialsToFile(userCreds);
	}

	/**
	 * Get account authentication status
	 * 
	 * @return isAccountAuthenticated, boolean true if account is authenticated
	 */
	public boolean getAccountAuthenticationStatus() {
		return isAccountAuthenticated;
	}

	/**
	 * Add Song method for serialization. If user logs in today, will check how
	 * many songs they have played. If currentDate = Today, then add song like
	 * normal and increment numSongsPlayedToday. IF it turns 12am and is a new
	 * day, will reset currentDate to TODAY (12:01am) and numSongsPlayed today
	 * is set to 1 (since we are adding a song) and adds the song to both the
	 * playlist and observable list
	 * 
	 * @param song,           the Song to add to play list
	 * @param observableList, the ObservableList<Song>
	 * @return songAdded, boolean true when song is successfully added
	 */
	public boolean addSong(Song song, ObservableList<Song> observableList) {
		boolean songAdded = false;

		// If it is still the SAME DAY, check number of songs ALREADY played
		if (currentDate.equals(LocalDate.now())) {
			if (numSongsPlayedToday == DAILY_SONG_LIMIT) {
				return false;
			}

			// If < 3, increment number of songs played today
			numSongsPlayedToday++;

			// Adds song to playlist
			observableList.add(song);
			songAdded = true;

		} else {
			// Set currentDate to NEW DAY
			currentDate = LocalDate.now();

			// Since adding a song, implicit that this song being added is 1.
			numSongsPlayedToday = 1;

			// Adds song to playlist
			observableList.add(song);
			songAdded = true;
		}

		if (songAdded == true) {

			// numSongsPlayedToday has changed so serialize
			credentials = readCredentialsFromFile();

			Credentials userCredential = credentials.get(userName);
			userCredential.setLastLoginDate(currentDate);
			userCredential.setNumSongsLastPlayed(numSongsPlayedToday);
			credentials.put(userName, userCredential);

			// store new account information to credentials file
			writeCredentialsToFile(credentials);

		}

		return songAdded;
	}

	/**
	 * Pulls lastLoginDate and numSongsLastPlayed from serialized file. If the
	 * lastLoginDate equals to earlier that day, will set numSongsPlayedToday to
	 * numSongsLastPlayed. If LAST LOGIN was YESTERDAY (and before), No need to
	 * do anything
	 */
	private void checkLastLoginAndNumSongsPlayed() {
		// Checks if last login was still the same day
		if (lastLoginDate.equals(currentDate)) {
			// If user played 3 songs already, max is hit. 2 songs played, 1
			// song left.
			numSongsPlayedToday = numSongsLastPlayed;
		}
	}

	/**
	 * Returns number of songs user played today
	 * 
	 * @return numSongsPlayedToday, the integer number of songs played today
	 */
	public int getNumSongsPlayedToday() {
		return numSongsPlayedToday;
	}

	/**
	 * Read serialized credentials from file
	 * 
	 * @return HashMap<String, String>, the HashMap with credentials
	 */
	private HashMap<String, Credentials> readCredentialsFromFile() {
		HashMap<String, Credentials> readCredentials = new HashMap<>();

		try {
			FileInputStream rawBytes = new FileInputStream(FILENAME);

			// Read the file
			ObjectInputStream inFile = new ObjectInputStream(rawBytes);

			// Read one serialized object from file
			readCredentials = (HashMap<String, Credentials>) inFile
					.readObject();

			inFile.close();

		} catch (FileNotFoundException e) {
			// Exception can occur for the first time
		} catch (IOException e) {
			System.out.println("IO Exception");
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}

		return readCredentials;
	}

	/**
	 * Write serialized credentials to file
	 * 
	 * @param hmap, the HashMap<String, String> containing credentials
	 */
	private void writeCredentialsToFile(HashMap<String, Credentials> hmap) {
		try {

			// Write serialized object to file
			FileOutputStream bytesToDisk = new FileOutputStream(FILENAME);
			ObjectOutputStream outFile = new ObjectOutputStream(bytesToDisk);

			// Make the object persist so it can be read later
			outFile.writeObject(hmap);

			// close the output file
			outFile.close();

		} catch (IOException e) {
			System.out.println("Writing objects failed");
			e.printStackTrace();
		}
	}

	/**
	 * Get student name
	 * 
	 * @return userName, the String student user name
	 */
	public String getStudentName() {
		return userName;
	}
}