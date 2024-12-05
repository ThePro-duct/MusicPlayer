# Java Swing Music Player

This is a Java Swing-based music player application that provides a graphical user interface (GUI) for playing MP3 files, managing music libraries, and creating playlists.

## Features

### Library Management:
* Loads MP3 files from a specified directory.
* Displays song information in a table (title, artist, genre).
* Songs can be selected for playlist creation.
* Songs can be sorted in a library and sublibrary
* Allows users to create and save custom playlists by selecting songs.

### Playback Control:
* Play, pause, stop, next, and previous controls 
* Playback slider for seeking within a song.
* Displays the current song title and artist.

### Playlist Support:
* Create playlists by selecting songs.
* Save playlists as text files, containing paths to selected MP3 files.

## Implementation Details

The project utilizes several libraries to handle MP3 file decoding and metadata reading, along with Java Swing for the GUI.

* **JLayer:** For MP3 decoding and playback.
* **mp3agic:** For reading MP3 metadata (ID3 tags).
* **jaudiotagger:** For reading MP3 metadata (an alternative to mp3agic).

## Key Classes:

### **MusicPlayerGUI:**
* Creates and manages the user interface (UI).
* Handles playback controls and GUI updates.

### **MusicPlayer:**
* Manages audio playback using the JLayer library (play, pause, stop, next, previous).
* Interacts with Song objects to retrieve song data for playback.

### **Library:**
* Represents the music library, provides methods for loading and accessing songs.
* Allows adding and managing songs in the library.
* Allows sorting songs in the library

### **Song:**
* Represents an individual song, storing its metadata (title, artist, genre, length, file path).
* Extracts metadata from MP3 files using the mp3agic and jaudiotagger libraries.

### **PlaylistDialogBox:**
* A dialog for creating and saving playlists.
* Allows users to select songs from the library and save them to a text file.

### **PlaylistTableModel:**
* Custom TableModel for managing song selection in the playlist creation table.
* Tracks whether songs are selected for inclusion in a playlist.

### **App:**
* The main entry point of the application, starting the program.

## How to Run
1. Download the project code.
2. Ensure the libraries are properly part of the project dependencies.
3. Compile and run the `App` class to start the application.