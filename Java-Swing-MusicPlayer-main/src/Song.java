/*
 *  Course:          TCSS143 - Fundamentals of Object-Oriented Programming-Theory
 *                   and Application
 *  Names:           Colby Jenkins, Keith Smith, Kevin Michalson, Marcus Meligro
 *  Instructor:      Wei Cai
 *  Assignment:      Team Project
 *  Due Date:        12/5/24
 *
 *  File Name:       Song.java
 */

import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;

/**
 * Represents an MP3 song with metadata including title, artist, genre, and length.
 * Provides methods for retrieving song details and file information.
 *
 * @version 28 November 2024
 */
public class Song {

    /**
     * Stores metadata of the song (title, artist, length, genre, and file path).
     */
    private HashMap<String, String> mySongData = new HashMap<>();

    /**
     * Object for accessing MP3 file information using the mp3agic library.
     */
    private Mp3File myMp3File;

    /**
     * Frame rate per millisecond, calculated from the MP3 file data.
     */
    private double myFrameRatePerMilliseconds;

    /**
     * Constructs a Song object and extracts metadata from the given MP3 file path.
     *
     * @param theFilePath the file path of the MP3 file.
     */
    public Song(String theFilePath) {
        // Initialize keys for metadata.
        String songTitleKey = "songTitle";
        String songArtistKey = "songArtist";
        String songLengthKey = "songLength";
        String songGenreKey = "songGenre";

        // Store the file path in the song data map.
        mySongData.put("filePath", theFilePath);

        try {
            // Create an Mp3File object from the file path.
            myMp3File = new Mp3File(theFilePath);

            // Calculate the frame rate per millisecond.
            myFrameRatePerMilliseconds =
                    (double) myMp3File.getFrameCount() / myMp3File.getLengthInMilliseconds();

            // Convert the song length to a formatted string and store it.
            mySongData.put(songLengthKey, convertToSongLengthFormat());

            // Use jaudiotagger to read metadata from the MP3 file.
            AudioFile audioFile = AudioFileIO.read(new File(theFilePath));
            Tag tag = audioFile.getTag();

            if (tag != null) {
                // Store song title, artist, and genre from metadata.
                mySongData.put(songTitleKey, tag.getFirst(FieldKey.TITLE));
                mySongData.put(songArtistKey, tag.getFirst(FieldKey.ARTIST));
                mySongData.put(songGenreKey, tag.getFirst(FieldKey.GENRE));
            } else {
                // Default values if metadata is not found.
                mySongData.put(songTitleKey, "N/A");
                mySongData.put(songArtistKey, "N/A");
                mySongData.put(songGenreKey, "N/A");
            }
        } catch (Exception e) {
            System.out.println("ERROR: Input Files Not found");
            JOptionPane.showMessageDialog(null, "Error loading song data: " + e.getMessage(),"Error" ,JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Converts the song length to a formatted string (mm:ss).
     *
     * @return the formatted song length as a string.
     */
    private String convertToSongLengthFormat() {
        long minutes = myMp3File.getLengthInSeconds() / 60;
        long seconds = myMp3File.getLengthInSeconds() % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Retrieves the title of the song.
     *
     * @return the song title.
     */
    public String getSongTitle() {
        return mySongData.get("songTitle");
    }

    /**
     * Retrieves the artist of the song.
     *
     * @return the song artist.
     */
    public String getSongArtist() {
        return mySongData.get("songArtist");
    }

    /**
     * Retrieves the length of the song in mm:ss format.
     *
     * @return the song length.
     */
    public String getSongLength() {
        return mySongData.get("songLength");
    }

    /**
     * Retrieves the genre of the song.
     *
     * @return the song genre.
     */
    public String getSongGenre() {
        return mySongData.get("songGenre");
    }

    /**
     * Retrieves the file path of the song.
     *
     * @return the file path of the song.
     */
    public String getFilePath() {
        return mySongData.get("filePath");
    }

    /**
     * Retrieves the Mp3File object for the song.
     *
     * @return the Mp3File object.
     */
    public Mp3File getMp3File() {
        return myMp3File;
    }

    /**
     * Retrieves the frame rate per millisecond of the song.
     *
     * @return the frame rate per millisecond.
     */
    public double getFrameRatePerMilliseconds() {
        return myFrameRatePerMilliseconds;
    }
}
