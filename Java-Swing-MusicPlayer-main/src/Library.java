import java.io.File;
import java.util.ArrayList;

public class Library {
    // ArrayList to store the songs in the library.
    public ArrayList<Song> songLibrary;

    // Constructor that takes the folder path containing the songs.
    public Library(String folderPath) {
        // Initialize the songLibrary by adding songs from the specified folder.
        songLibrary = addLibraryElements(folderPath);
    }

    // Private helper method to add songs from a folder to the library.
    private ArrayList<Song> addLibraryElements(String folderPath) {
        File directory = new File(folderPath);
        ArrayList<Song> result = new ArrayList<>();

        // Check if the directory exists and is a directory.
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            int fileCount;

            if (files != null) {
                fileCount = files.length;
                System.out.println("Number of files in the directory: " + fileCount);

                // Iterate through the files in the directory.
                for (File file : files) {
                    // Create a Song object for each file and add it to the result list.
                    Song song = new Song(file.getPath());
                    result.add(song);
                }
            } else {
                fileCount = 0;
                System.out.println("Error reading directory.");
            }
        }
        return result;
    }

    // Get a song from the library by its index.
    public Song getSong(int index) {
        return songLibrary.get(index);
    }

    // Get a song from the library by its title.
    public Song getSong(String songTitle) {
        Song result = null;

        // Iterate through the songs and find the one with the matching title.
        for (Song song : songLibrary) {
            if (song.getSongTitle().equals(songTitle)) {
                return song;
            }
        }
        return result;
    }

    // Add a song to the library.
    public void addToLibrary(Song song) {
        songLibrary.add(song);
    }

    // Convert the song library to a 2D String array for use in a JTable.
    public String[][] toArray() {
        String[][] result = new String[songLibrary.size()][4];

        // Populate the 2D array with song information (index, title, artist, genre).
        for (int i = 0; i < songLibrary.size(); i++) {
            result[i][0] = i + "";
            result[i][1] = songLibrary.get(i).getSongTitle();
            result[i][2] = songLibrary.get(i).getSongArtist();
            result[i][3] = songLibrary.get(i).getSongGenre();
        }
        return result;
    }

    // Override toString() method to provide a formatted string representation of the library contents.
    public String toString() {
        String result = "\n------- Library Contents -------\n";
        for (Song song : songLibrary) {
            result += String.format("Title: %2s" +
                    "\nArtist: %2s" +
                    "\nGenre: %2s" +
                    "\nFilePath: %2s\n\n", song.getSongTitle(), song.getSongArtist(), song.getSongGenre(), song.getFilePath());
        }
        result += "------------- End  -------------\n";

        return result;
    }
}