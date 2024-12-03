/*
 *  Course: TCSS143 - Fundamentals of Object-Oriented Programming-Theory
 *                    and Application
 *  Names:            Colby Jenkins, Keith Smith, Kevin Michalson, Marcus Meligro
 *  Instructor:       Wei Cai
 *  Assignment:       Team Project
 *  Due Date:         12/5/24
 *
 *  File Name:        Library.java
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Manages the collections of the songs. Provides functionality to load songs from a folder,
 * access songs by index or title, add songs to the library, and convert the library into a
 * format suitable for JTable.
 *
 * @version 28 November 2024
 */
public class Library {
    /**
     * Stores the songs in the library.
     */
    public ArrayList<Song> mySongLibrary;

    /**
     * Constructs a Library object by loading songs from the specified folder.
     *
     * @param theFolderPath the path to the folder containing the songs.
     */
    public Library(String theFolderPath) {
        // Initialize the song library by adding songs from the specified folder.
        mySongLibrary = addLibraryElements(theFolderPath);
    }

    /**
     * Adds songs from the specified folder to the library.
     *
     * @param theFolderPath the path to the folder containing the songs.
     * @return an ArrayList containing Song objects loaded from the folder.
     */
    private ArrayList<Song> addLibraryElements(String theFolderPath) {
        File myDirectory = new File(theFolderPath);
        ArrayList<Song> myResult = new ArrayList<>();

        // Check if the directory exists and is a directory.
        if (myDirectory.exists() && myDirectory.isDirectory()) {
            File[] myFiles = myDirectory.listFiles();
            int myFileCount;

            if (myFiles != null) {
                myFileCount = myFiles.length;
                System.out.println("Number of files in the directory: " + myFileCount);

                // Iterate through the files in the directory.
                for (File myFile : myFiles) {
                    // Create a Song object for each file and add it to the result list.
                    Song mySong = new Song(myFile.getPath());
                    myResult.add(mySong);
                }
            } else {
                myFileCount = 0;
                System.out.println("Error reading directory.");
            }
        }
        return myResult;
    }

    /**
     * Retrieves a song from the library by its index.
     *
     * @param theIndex the index of the song in the library.
     * @return the Song object at the specified index.
     */
    public Song getSong(int theIndex) {
        return mySongLibrary.get(theIndex);
    }

    /**
     * Retrieves a song from the library by its title.
     *
     * @param theSongTitle the title of the song to retrieve.
     * @return the Song object with the specified title, or null if not found.
     */
    public Song getSong(String theSongTitle) {
        Song myResult = null;

        // Iterate through the songs and find the one with the matching title.
        for (Song mySong : mySongLibrary) {
            if (mySong.getSongTitle().equals(theSongTitle)) {
                return mySong;
            }
        }
        return myResult;
    }

    /**
     * Adds a song to the library.
     *
     * @param theSong the Song object to add to the library.
     */
    public void addToLibrary(Song theSong) {
        mySongLibrary.add(theSong);
    }

    /**
     * Converts the song library to a 2D String array for use in a JTable.
     *
     * @return a 2D String array containing song information (index, title, artist, genre).
     */
    public String[][] toArray() {
        String[][] myResult = new String[mySongLibrary.size()][4];

        // Populate the 2D array with song information (index, title, artist, genre).
        for (int myIndex = 0; myIndex < mySongLibrary.size(); myIndex++) {
            myResult[myIndex][0] = myIndex + 1 +"";
            myResult[myIndex][1] = mySongLibrary.get(myIndex).getSongTitle();
            myResult[myIndex][2] = mySongLibrary.get(myIndex).getSongArtist();
            myResult[myIndex][3] = mySongLibrary.get(myIndex).getSongGenre();
        }
        return myResult;
    }

    /**
     * Provides a formatted string representation of the library contents.
     *
     * @return a string containing details of the library contents.
     */
    public String toString() {
        String myResult = "\n------- Library Contents -------\n";
        for (Song mySong : mySongLibrary) {
            myResult += String.format("Title: %2s" +
                            "\nArtist: %2s" +
                            "\nGenre: %2s" +
                            "\nFilePath: %2s\n\n",
                    mySong.getSongTitle(),
                    mySong.getSongArtist(),
                    mySong.getSongGenre(),
                    mySong.getFilePath());
        }
        myResult += "------------- End  -------------\n";

        return myResult;
    }

    /**
     * Sorts the library by song titles in case-insensitive order.
     */
    public void sortByTitle() {
        Collections.sort(mySongLibrary,
                Comparator.comparing(Song::getSongTitle, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Sorts the library by song artists in case-insensitive order.
     */
    public void sortByArtist() {
        Collections.sort(mySongLibrary,
                Comparator.comparing(Song::getSongArtist, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Sorts the library by song genres in case-insensitive order.
     */
    public void sortByGenre() {
        Collections.sort(mySongLibrary,
                Comparator.comparing(Song::getSongGenre, String.CASE_INSENSITIVE_ORDER));
    }
    /**
     * Searches the song library for songs that match the given search term.
     * Matches are scored based on the relevance to the song's title, artist, and genre.
     *
     * @param searchTerm The term to search for in the song library.
     * @return A list of songs sorted by relevance score, with the most relevant first.
     */
    public ArrayList<Song> searchSongs(String searchTerm) {
        ArrayList<Song> searchResults = new ArrayList<>();
        HashMap<Song, Integer> matchScores = new HashMap<>();


        // Normalize the search term for case-insensitive matching.
        searchTerm = searchTerm.toLowerCase();


        // Score each song based on matches with the search term.
        for (Song song : mySongLibrary) {
            int score = 0;


            // Check for matches in the song title (highest weight).
            if (song.getSongTitle().toLowerCase().contains(searchTerm)) {
                score += 100; // Base score for a title match.
                if (song.getSongTitle().toLowerCase().startsWith(searchTerm)) {
                    score += 50; // Bonus for matches starting with the term.
                }
            }


            // Check for matches in the artist name.
            if (song.getSongArtist().toLowerCase().contains(searchTerm)) {
                score += 75; // Base score for an artist match.
                if (song.getSongArtist().toLowerCase().startsWith(searchTerm)) {
                    score += 25; // Bonus for matches starting with the term.
                }
            }


            // Check for matches in the genre.
            if (song.getSongGenre().toLowerCase().contains(searchTerm)) {
                score += 50; // Base score for a genre match.
            }


            // Add to the match scores if the song has a positive score.
            if (score > 0) {
                matchScores.put(song, score);
            }
        }


        // Collect all matched songs into the results list.
        searchResults.addAll(matchScores.keySet());


        // Sort the results by score in descending order.
        searchResults.sort(new Comparator<Song>() {
            @Override
            public int compare(Song s1, Song s2) {
                return matchScores.get(s2).compareTo(matchScores.get(s1));
            }
        });


        return searchResults;
    }


}
