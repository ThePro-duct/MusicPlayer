import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.util.HashMap;

// Class representing an MP3 song.
public class Song {
    // HashMap to store song metadata (title, artist, length, genre, file path).
    private HashMap<String, String> songData = new HashMap<>();

    // Mp3File object from the mp3agic library for accessing MP3 file information.
    private Mp3File mp3File;

    // Frame rate per millisecond, calculated from the MP3 file data.
    private double frameRatePerMilliseconds;

    // Constructor that takes the file path of the MP3 file.
    public Song(String filePath){
        // Initialize string references for clarity (though not strictly necessary).
        String songTitle, songArtist, songLength, songGenre;
        songTitle = "songTitle";
        songArtist = "songArtist";
        songLength = "songLength";
        songGenre = "songGenre";

        // Store the file path in the songData HashMap.
        songData.put("filePath", filePath);

        try{
            // Create an Mp3File object from the file path.
            mp3File = new Mp3File(filePath);

            // Calculate the frame rate per millisecond.
            frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();

            // Convert the song length to a formatted string and store it.
            songData.put("songLength", convertToSongLengthFormat());

            // Use jaudiotagger to read metadata from the MP3 file.
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            Tag tag =  audioFile.getTag();

            if(tag != null){
                // Store the song title, artist, and genre from the metadata.
                songData.put("songTitle", tag.getFirst(FieldKey.TITLE));
                songData.put("songArtist", tag.getFirst(FieldKey.ARTIST));
                songData.put("songGenre",tag.getFirst(FieldKey.GENRE));
            }else{
                // If metadata is not found, set default values.
                songData.put(songTitle,"N/A");
                songData.put(songArtist,"N/A");
                songData.put(songGenre, "N/A"); // It's good to have a default for genre as well.
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // Private helper method to convert the song length to a formatted string (mm:ss).
    private String convertToSongLengthFormat(){
        long minutes = mp3File.getLengthInSeconds() / 60;
        long seconds = mp3File.getLengthInSeconds() % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        return formattedTime;
    }

    // Getters for the song data.
    public String getSongTitle() {
        return songData.get("songTitle");
    }

    public String getSongArtist() {
        return songData.get("songArtist");
    }

    public String getSongLength() {
        return songData.get("songLength");
    }

    public String getSongGenre() {
        return songData.get("songGenre");
    }

    public String getFilePath() {
        return songData.get("filePath");
    }

    public Mp3File getMp3File(){return mp3File;}

    public double getFrameRatePerMilliseconds(){return frameRatePerMilliseconds;}
}