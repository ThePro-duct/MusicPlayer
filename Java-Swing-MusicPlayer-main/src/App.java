import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Initialize the music library with the path to the song directory.
        Library library = new Library("Java-Swing-MusicPlayer-main/src/assets/songs");

        // Use SwingUtilities.invokeLater to ensure GUI updates happen on the Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                // Create and display the MusicPlayerGUI.
                new MusicPlayerGUI().setVisible(true);

                // (Commented out) Code for testing the Song class.
//                Song song = new Song("src/assets/Wind Riders - Asher Fulero.mp3");
//                System.out.println(song.getSongTitle());
//                System.out.println(song.getSongArtist());
            }
        });

    }
}