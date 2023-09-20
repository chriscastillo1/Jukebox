package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 * @author Chris Castillo, Anisha Munjal
 * 
 *         Creates an observableList of Song objects that exist in SongFiles
 */

public class SongList {

	private static ObservableList<Song> songList = FXCollections
			.observableArrayList(
					new Song("Caught a Pokémon!", "Game Freak", "00:05",
							"songfiles/Capture.mp3"),
					new Song("Danse Macabre - Violin Hook", "Kevin MacLeod",
							"00:34", "songfiles/DanseMacabreViolinHook.mp3"),
					new Song("Determined Tumbao 20", "FreePlay Music", "00:20",
							"songfiles/DeterminedTumbao.mp3"),
					new Song("Longing In Their Hearts", "Bonnie Raitt", "04:48",
							"songfiles/LongingInTheirHearts.mp3"),
					new Song("Loping Sting", "Kevin MacLeod", "00:04",
							"songfiles/LopingSting.mp3"),
					new Song("Swing Cheese 15", "FreePlay Music", "00:15",
							"songfiles/SwingCheese.mp3"),
					new Song("The Curtain Rises High Quality", "Kevin MacLeod",
							"00:28", "songfiles/TheCurtainRises.mp3"),
					new Song("Untameable Fire", "Pierre Langer", "04:41",
							"songfiles/UntameableFire.mp3"),
					new Song("Outer Banks", "prazkhanal", "04:02",
							"songfiles/outerbanks.mp3"),
					new Song("No Signal", "prazkhanal", "02:25",
							"songfiles/nosignal.mp3"),
					new Song("Energetic Upbeat", "yourtunes", "02:37",
							"songfiles/energeticupbeat.mp3"),
					new Song("Beyond Equations", "RomanSenykMusic", "03:49",
							"songfiles/beyondequation.mp3"),
					new Song("Moments", "Coma-Media", "02:19",
							"songfiles/moments.mp3"),
					new Song("Upbeat Funky Pop", "QubeSounds", "01:43",
							"songfiles/upbeat-funky-pop-118155.mp3"));

	/**
	 * Get observable song list
	 * 
	 * @return songList, the ObservableList<Song> list
	 */
	public static ObservableList<Song> getList() {
		return songList;
	}

	/**
	 * Find song at the specified song path
	 * 
	 * @param songPath, the String song path
	 * @return song, the Song
	 */
	public static Song findSong(String songPath) {
		for (Song song : songList) {
			if (songPath.equals(song.getPath())) {
				return song;
			}
		}
		// If songPath is invalid and not found
		return null;
	}
}