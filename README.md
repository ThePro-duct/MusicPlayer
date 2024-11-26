# Java Swing Music Player

This is a simple music player application built using Java Swing. It provides a graphical user interface for playing MP3 files, managing a music library, and controlling playback.

## Features

* **Library Management:**
    * Loads MP3 files from a specified directory.
    * Displays song information in a table (title, artist, genre).
    * Allows sorting of songs by title, artist, or genre (not yet implemented).
    * Provides a search functionality (not yet implemented).
* **Playback Control:**
    * Play, pause, stop, next, and previous controls.
    * Playback slider for seeking within a song.
    * Displays current song title and artist.
* **Playlist Support:**
    * Ability to load and play playlists (not yet fully implemented).


## Implementation Details

The project uses the following libraries:

* **JLayer:** For MP3 decoding and playback.
* **mp3agic:** For reading MP3 metadata (ID3 tags).
* **jaudiotagger:**  An alternative library for reading MP3 metadata.
* **Swing:** For the graphical user interface.

**Key Classes:**

* **`MusicPlayerGUI`:**  Handles the graphical user interface and user interaction.
* **`MusicPlayer`:**  Manages the audio playback using the JLayer library.
* **`Library`:**  Represents the music library and provides methods for loading, accessing, and managing songs.
* **`Song`:**  Represents an individual song, storing its metadata and providing access to file information.
* **`App`:**  Contains the `main()` method, the entry point of the application.

**Code Structure:**

The code is organized into these main classes, each with specific responsibilities. The `MusicPlayerGUI` class creates the user interface, while the `MusicPlayer` class handles the actual audio playback. The `Library` class manages the collection of songs, and the `Song` class represents an individual song with its metadata. The `App` class starts the application.


## How to Run

1. **Make sure you have Java installed.**
2. **Download the project code.**
3. **Include the required libraries (JLayer, mp3agic, jaudiotagger) in your project's classpath.**
4. **Compile and run the `App` class.**


## Future Improvements

* **Implement sorting functionality.**
* **Implement search functionality.**
* **Add playlist creation and saving.**
* **Improve the user interface with more features and better design.**
* **Add error handling and user feedback.**


## Contributing

Contributions are welcome! Feel free to submit pull requests for bug fixes, new features, or improvements to the code.
