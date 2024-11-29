/*
 *  Course: TCSS143 - Fundamentals of Object-Oriented Programming-Theory
 *                    and Application
 *  Names:            Colby Jenkins, Keith Smith, Kevin Michalson, Marcus Meligro
 *  Instructor:       Wei Cai
 *  Assignment:       Team Project
 *  Due Date:         12/5/24
 *
 *  File Name:        MusicPlayer.java
 */

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

/**
 * This file implements a MusicPlayer class that manages the playback of audio files,
 * supporting both individual songs and playlists.
 *
 *
 * @version 28 November 2024
 */
public class MusicPlayer extends PlaybackListener {
    // Synchronization object used for pausing and resuming playback.
    private static final Object playSignal = new Object();

    // Reference to the MusicPlayerGUI for updating the UI.
    private final MusicPlayerGUI musicPlayerGUI;

    // Currently playing song.
    private Song currentSong;

    // Getter method for the current song.
    public Song getCurrentSong(){
        return currentSong;
    }

    // Playlist of songs.
    private ArrayList<Song> playlist;

    // Index of the currently playing song in the playlist.
    private int currentPlaylistIndex;

    // AdvancedPlayer object for audio playback.
    private AdvancedPlayer advancedPlayer;

    // Flag indicating whether playback is paused.
    private boolean isPaused;

    // Flag indicating whether the current song has finished playing.
    private boolean songFinished;

    // Flags indicating whether the next or previous button was pressed.
    private boolean pressedNext, pressedPrev;

    // Current frame position in the song (used for pausing and resuming).
    private int currentFrame;

    // Setter method for the current frame.
    public void setCurrentFrame(int frame){
        currentFrame = frame;
    }

    // Current playback time in milliseconds (used for updating the slider).
    private int currentTimeInMilli;

    // Setter method for the current time in milliseconds.
    public void setCurrentTimeInMilli(int timeInMilli){
        currentTimeInMilli = timeInMilli;
    }

    // Constructor.
    public MusicPlayer(MusicPlayerGUI musicPlayerGUI){
        this.musicPlayerGUI = musicPlayerGUI;
    }

    // Loads a single song for playback.
    public void loadSong(Song song){
        currentSong = song;
        playlist = null; // Clear any existing playlist.

        // Stop the currently playing song if there is one.
        if(!songFinished)
            stopSong();

        // Play the loaded song.
        if(currentSong != null){
            currentFrame = 0; // Reset the frame position.
            currentTimeInMilli = 0; // Reset the current time.
            musicPlayerGUI.setPlaybackSliderValue(0); // Reset the playback slider in the GUI.
            playCurrentSong();
        }
    }

    // Loads a playlist of songs from a file.
    public void loadPlaylist(File playlistFile){
        playlist = new ArrayList<>();

        // Read song paths from the playlist file.
        try{
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String songPath;
            while((songPath = bufferedReader.readLine()) != null){
                Song song = new Song(songPath);
                playlist.add(song);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        // Start playing the first song in the playlist.
        if(playlist.size() > 0){
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMilli = 0;
            currentSong = playlist.get(0);
            currentFrame = 0;

            // Update the GUI.
            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);

            playCurrentSong();
        }
    }

    // Pauses the currently playing song.
    public void pauseSong(){
        if(advancedPlayer != null){
            isPaused = true;
            stopSong();
        }
    }

    // Stops the currently playing song.
    public void stopSong(){
        if(advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    // Plays the next song in the playlist.
    public void nextSong(){
        if(playlist == null) return; // Do nothing if there is no playlist.
        if(currentPlaylistIndex + 1 > playlist.size() - 1) return; // Do nothing if at the end of the playlist.

        pressedNext = true;

        if(!songFinished)
            stopSong();

        currentPlaylistIndex++;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMilli = 0;

        // Update the GUI.
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        playCurrentSong();
    }

    // Plays the previous song in the playlist.
    public void prevSong(){
        if(playlist == null) return; // Do nothing if there is no playlist.
        if(currentPlaylistIndex - 1 < 0) return; // Do nothing if at the beginning of the playlist.

        pressedPrev = true;

        if(!songFinished)
            stopSong();

        currentPlaylistIndex--;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMilli = 0;

        // Update the GUI.
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        playCurrentSong();
    }

    // Plays the current song.
    public void playCurrentSong(){
        if(currentSong == null) return;

        try{
            // Create an input stream for the MP3 file.
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            // Create a new AdvancedPlayer for playback.
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            // Updating song title and artist in the GUI.
            musicPlayerGUI.updateSongTitleAndArtist(getCurrentSong());

            // Start the music playback thread.
            startMusicThread();

            // Start the playback slider update thread.
            startPlaybackSliderThread();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // Thread for playing the music.
    private void startMusicThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(isPaused){
                        synchronized(playSignal){
                            isPaused = false;
                            playSignal.notify(); // Notify the playback slider thread to continue.
                        }

                        // Resume playback from the current frame.
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    }else{
                        // Start playback from the beginning.
                        advancedPlayer.play();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Thread for updating the playback slider.
    private void startPlaybackSliderThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isPaused){
                    try{
                        synchronized(playSignal){
                            playSignal.wait(); // Wait for the music thread to notify.
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                // Update the playback slider while the song is playing.
                while(!isPaused && !songFinished && !pressedNext && !pressedPrev){
                    try{
                        currentTimeInMilli++;
                        int calculatedFrame = (int) ((double) currentTimeInMilli * 2.08 * currentSong.getFrameRatePerMilliseconds());
                        musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);
                        Thread.sleep(1); // Sleep for 1 millisecond.
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // Setter method for the current song (used when selecting a song from the table).
    public  void setSelectedSong(Song song){
        this.currentSong = song;
        this.setCurrentFrame(0);
    }

    // Getter method for the current frame.
    public int getCurrentFrame() {
        return currentFrame;
    }

    // Called when playback starts.
    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Playback Started");
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    // Called when playback finishes.
    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Playback Finished");

        if(isPaused){
            // If paused, update the current frame based on the event.
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        }else{
            if(pressedNext || pressedPrev) return; // Do nothing if next or previous button was pressed.

            songFinished = true;

            if(playlist == null){
                // If no playlist, update the GUI to show the play button.
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            }else{
                if(currentPlaylistIndex == playlist.size() - 1){
                    // If it was the last song in the playlist, update the GUI to show the play button.
                    musicPlayerGUI.enablePlayButtonDisablePauseButton();
                }else{
                    // Otherwise, play the next song in the playlist.
                    nextSong();
                }
            }
        }
    }
}