/*
 *  Course:          TCSS143 - Fundamentals of Object-Oriented Programming-Theory
 *                   and Application
 *  Names:           Colby Jenkins, Keith Smith, Kevin Michalson, Marcus Meligro
 *  Instructor:      Wei Cai
 *  Assignment:      Team Project
 *  Due Date:        12/5/24
 *
 *  File Name:       App.java
 */

import javax.swing.*;

/**
 * This program initializes the music library and launches the MusicPlayerGUI.
 *
 * @version 28 November 2024
 */
public class App {

    /**
     * The entry point of the program. Initializes the music library and launches
     * the graphical user interface for the music player.
     *
     * @param theArgs the command-line arguments (not used).
     */
    public static void main(String[] theArgs) {
        // Initialize the music library with the path to the song directory.
        Library myLibrary = new Library("Java-Swing-MusicPlayer-main/src/assets/songs");

        // Use SwingUtilities.invokeLater to ensure GUI updates happen on the Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // Create and display the MusicPlayerGUI.
                new MusicPlayerGUI().setVisible(true);

                // (Commented out) Code for testing the Song class.
//                Song mySong = new Song("src/assets/Wind Riders - Asher Fulero.mp3");
//                System.out.println(mySong.getSongTitle());
//                System.out.println(mySong.getSongArtist());
            }
        });
    }
}
