
/**
 * AUTHOR: Chris Castillo, Anisha Munjal 
 * FILE: Credentials.java 
 * ASSIGNMENT: The Cashless Jukebox
 * COURSE: CSC 335 Spring 2023
 * PURPOSE: This class creates a Credentials object that contains user password,
 * user last login date and number of songs played by user on last login date.
 */
package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Credentials implements Serializable {
	private String password;
	private LocalDate lastLoginDate;
	private int numSongsLastPlayed;

	/**
	 * Get user password
	 * 
	 * @return String, the user password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Get user last login date
	 * 
	 * @return lastLoginDate, the last login date for the user
	 */
	public LocalDate getLastLoginDate() {
		return lastLoginDate;
	}

	/**
	 * Get number of songs played by the user
	 * 
	 * @return numSongsLastPlayed, integer number of songs last played by user
	 */
	public int getNumSongsLastPlayed() {
		return numSongsLastPlayed;
	}

	/**
	 * Set user password
	 * 
	 * @param password, the String password for the user account
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Set last login date for the user account
	 * 
	 * @param lastLoginDate, last login date for the user
	 */
	public void setLastLoginDate(LocalDate lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	/**
	 * Set number of songs last played by user
	 * 
	 * @param numSongsLastPlayed, the integer number of songs last played
	 */
	public void setNumSongsLastPlayed(int numSongsLastPlayed) {
		this.numSongsLastPlayed = numSongsLastPlayed;
	}
}