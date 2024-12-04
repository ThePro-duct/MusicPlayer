/*
 *  Course: TCSS143 - Fundamentals of Object-Oriented Programming-Theory
 *                    and Application
 *  Names:            Colby Jenkins, Keith Smith, Kevin Michalson, Marcus Meligro
 *  Instructor:       Wei Cai
 *  Assignment:       Team Project
 *  Due Date:         12/5/24
 *
 *  File Name:        MusicPlayerGUI.java
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Initializes the GUI for the music player
 *
 *
 *
 * @version 28 November 2024
 */
public class MusicPlayerGUI extends JFrame {
    // Color and font configurations for the GUI.
    public static final Font FONT = new Font("Dialog",Font.PLAIN,18);
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color TEXT_COLOR_SECONDARY = Color.GRAY;

    private boolean wasSearched = false;
    // The underlying MusicPlayer object responsible for audio playback.
    private MusicPlayer myMusicPlayer;

    // JFileChooser for browsing and selecting MP3 files.
    private JFileChooser myJFileChooser;

    // Library to hold all Song objects.
    private Library myLibrary;
    private String[][] currentSongList;

    // GUI components for displaying song information and controlling playback.
    private JLabel mySongTitle, mySongArtist, myTableTitle;
    private JPanel myPlaybackBtns;
    private JSlider myPlaybackSlider;
    private JTable myLibraryTable;

    public MusicPlayerGUI(){
        // Calls JFrame constructor to initialize the GUI window with the title "Music Player".
        super("Music Player");

        // Set the initial size of the window.
        setSize(400, 600);

        // Terminate the application when the window is closed.
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Center the window on the screen.
        setLocationRelativeTo(null);

        // Prevent the window from being resized.
        setResizable(false);

        // Use BorderLayout for the main layout, allowing for precise component placement.
        setLayout(new BorderLayout());

        // Set the background color of the content pane.
        getContentPane().setBackground(FRAME_COLOR);

        setIconImage(loadImage("Java-Swing-MusicPlayer-main/src/assets/icon.png").getImage());

        // Initialize the MusicPlayer and JFileChooser objects.
        myMusicPlayer = new MusicPlayer(this);
        myJFileChooser = new JFileChooser();

        // Set the default directory for the file chooser.
        myJFileChooser.setCurrentDirectory(new File("Java-Swing-MusicPlayer-main/src/assets"));

        // Initialize the Library with the path to the song directory.
        myLibrary = new Library("Java-Swing-MusicPlayer-main/src/assets/songs");

        // Filter the file chooser to display only MP3 files.
        myJFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        // Add the GUI components to the window.
        addGuiComponents();
    }

    private void addGuiComponents() {
        // Add a main panel to hold both search and sort panels in the NORTH.
        JPanel theNorthPanel = new JPanel();
        theNorthPanel.setLayout(new BoxLayout(theNorthPanel, BoxLayout.Y_AXIS));
        theNorthPanel.setBackground(null); // Set transparent background.
        add(theNorthPanel, BorderLayout.NORTH);

        // Search Panel.
        JPanel theSearchPanel = new JPanel();
        theSearchPanel.setBackground(null); // Set transparent background.
        theNorthPanel.add(theSearchPanel);

        JTextField theSearchBox = new JTextField(20);
        theSearchPanel.add(theSearchBox);

        JButton theSearchButton = new JButton("Search");
        theSearchButton.addActionListener(e -> {
            if(theSearchBox.getText() != null && !(theSearchBox.getText().equals(""))){
                wasSearched = true;
                ArrayList<Song> searchTable = myLibrary.searchOrder(theSearchBox.getText());
                updateTable(searchTable);
            }else{
                wasSearched = false;
                updateTable();
            }

        });
        theSearchPanel.add(theSearchButton);


        // Create a JMenuBar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar); // Add the menu bar to the frame



        // --- Sort Submenu ---
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenu sortMenu = new JMenu("Sort");
        menuBar.add(sortMenu);

        // Sort options (using ActionListeners for brevity)
        JMenuItem titleSortItem = new JMenuItem("Title");
        titleSortItem.addActionListener(e -> {
            myLibrary.sortByTitle(wasSearched);
            updateTable(wasSearched);
        });
        sortMenu.add(titleSortItem);

        JMenuItem artistSortItem = new JMenuItem("Artist");
        artistSortItem.addActionListener(e -> {
            myLibrary.sortByArtist(wasSearched);
            updateTable(wasSearched);
        });
        sortMenu.add(artistSortItem);

        JMenuItem genreSortItem = new JMenuItem("Genre");
        genreSortItem.addActionListener(e -> {
            myLibrary.sortByGenre(wasSearched);
            updateTable(wasSearched);
        });
        sortMenu.add(genreSortItem);

        // --- Playlist Submenu ---
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        JMenuItem createPlaylistItem = new JMenuItem("Create Playlist");
        createPlaylistItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(wasSearched){
                    PlaylistDialogBox playlistBox = new PlaylistDialogBox(myLibrary.updatedSongLibrary, MusicPlayerGUI.this);
                    playlistBox.setVisible(true);
                }else{
                    PlaylistDialogBox playlistBox = new PlaylistDialogBox(myLibrary.mySongLibrary, MusicPlayerGUI.this);
                    playlistBox.setVisible(true);
                }
            }
        });
        playlistMenu.add(createPlaylistItem);

        JMenuItem loadPlaylistItem = new JMenuItem("Load Playlist");
        loadPlaylistItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Load Playlist");
            fileChooser.setCurrentDirectory(new File("Java-Swing-MusicPlayer-main/src/assets/playlist"));

            int userSelection = fileChooser.showOpenDialog(MusicPlayerGUI.this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Get the selected file
                File playlistFile = fileChooser.getSelectedFile();

                // Load the playlist and update the table
                Library newLibrary = new Library("Playlist Name",playlistFile.toString());
                myMusicPlayer.loadPlaylist(playlistFile);
                updateTable(newLibrary.mySongLibrary); // Assuming getCurrentSong returns the first song of the playlist
            }
        });
        playlistMenu.add(loadPlaylistItem);


        // Center Panel.
        JPanel theCenterPanel = new JPanel();
        theCenterPanel.setLayout(new BoxLayout(theCenterPanel, BoxLayout.Y_AXIS));
        add(theCenterPanel, BorderLayout.CENTER);

        // Table to display the music myLibrary.
        myTableTitle = new JLabel("Library");
        myTableTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        myTableTitle.setForeground(TEXT_COLOR);
        myTableTitle.setHorizontalAlignment(JLabel.LEFT); // Left align the label
        myTableTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        theCenterPanel.add(myTableTitle);

        String[] theColumnNames = {"", "Title", "Artist", "Genre"};
        String[][] theTableData = myLibrary.toArray();

        myLibraryTable = new JTable(theTableData, theColumnNames) {
            @Override
            public boolean isCellEditable(int theRow, int column) {
                return false; // Make table cells non-editable.
            }
        };
        myLibraryTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering.
        myLibraryTable.getTableHeader().setResizingAllowed(false);   // Prevent column resizing.

        JScrollPane theScrollPane = new JScrollPane(myLibraryTable);
        myLibraryTable.getColumnModel().getColumn(0).setPreferredWidth(5); // Set preferred width for the first column.
        theScrollPane.setPreferredSize(new Dimension(300, 100)); // Reduced height to 100.
        theCenterPanel.add(theScrollPane);

        // Add MouseListener to handle song selection from the table.
        myLibraryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int theRow = myLibraryTable.rowAtPoint(e.getPoint());
                if (theRow >= 0) {
                    String theTableSelection = (String) myLibraryTable.getValueAt(theRow, 1);
                    Song theSongSelection = myLibrary.getSong(theTableSelection);
                    myMusicPlayer.setSelectedSong(theSongSelection);
                }
            }
        });

        // Song Information Panel.
        mySongTitle = new JLabel("Song Title");
        mySongTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        mySongTitle.setForeground(TEXT_COLOR);
        mySongTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        mySongArtist = new JLabel("Artist");
        mySongArtist.setFont(new Font("Dialog", Font.PLAIN, 18));
        mySongArtist.setForeground(TEXT_COLOR_SECONDARY);
        mySongArtist.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel songInfoPanel = new JPanel();
        songInfoPanel.setLayout(new BoxLayout(songInfoPanel, BoxLayout.Y_AXIS));
        songInfoPanel.setOpaque(false); // Make the panel transparent.
        songInfoPanel.add(mySongTitle);
        songInfoPanel.add(mySongArtist);
        theCenterPanel.setBackground(null); // Set transparent background.
        theCenterPanel.add(songInfoPanel);

        // Playback Slider.
        myPlaybackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        myPlaybackSlider.setBackground(null); // Set transparent background.
        myPlaybackSlider.setVisible(false);
        myPlaybackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                myMusicPlayer.pauseSong(); // Pause playback when slider is pressed.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JSlider theSource = (JSlider) e.getSource();
                int theFrame = theSource.getValue();
                myMusicPlayer.setCurrentFrame(theFrame);
                myMusicPlayer.setCurrentTimeInMilli((int) (theFrame / (2.08 * myMusicPlayer.getCurrentSong().getFrameRatePerMilliseconds())));
                myMusicPlayer.playCurrentSong(); // Resume playback when slider is released.
                enablePauseButtonDisablePlayButton();
            }
        });
        theCenterPanel.add(myPlaybackSlider);

        // Add Playback Buttons to the GUI.
        addPlaybackBtns();
    }


    // Method to add playback control buttons to the GUI.
    private void addPlaybackBtns(){
        myPlaybackBtns = new JPanel();
        myPlaybackBtns.setBounds(0, 435, getWidth() - 10, 80);
        myPlaybackBtns.setBackground(null); // Set transparent background.

        // Previous button.
        JButton prevButton = new JButton(loadImage("Java-Swing-MusicPlayer-main/src/assets/previous.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null); // Set transparent background.
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myMusicPlayer.prevSong(); // Go to the previous song.
            }
        });
        myPlaybackBtns.add(prevButton);

        // Play button.
        JButton playButton = new JButton(loadImage("Java-Swing-MusicPlayer-main/src/assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null); // Set transparent background.
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePauseButtonDisablePlayButton();
                myMusicPlayer.playCurrentSong(); // Start or resume playback.
            }
        });
        myPlaybackBtns.add(playButton);

        // Pause button (initially invisible).
        JButton pauseButton = new JButton(loadImage("Java-Swing-MusicPlayer-main/src/assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null); // Set transparent background.
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePlayButtonDisablePauseButton();
                myMusicPlayer.pauseSong(); // Pause playback.
            }
        });
        myPlaybackBtns.add(pauseButton);

        // Next button.
        JButton nextButton = new JButton(loadImage("Java-Swing-MusicPlayer-main/src/assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null); // Set transparent background.
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myMusicPlayer.nextSong(); // Go to the next song.
            }
        });
        myPlaybackBtns.add(nextButton);

        // Add the playback buttons panel to the bottom (SOUTH) of the BorderLayout.
        add(myPlaybackBtns, BorderLayout.SOUTH);
    }

    // Method to update the playback slider value from the MusicPlayer.
    public void setPlaybackSliderValue(int theFrame){
        myPlaybackSlider.setValue(theFrame);
    }

    // Method to update the song title and artist labels.
    public void updateSongTitleAndArtist(Song song){
        mySongTitle.setText(song.getSongTitle());
        mySongArtist.setText(song.getSongArtist());
    }

    // Method to update the playback slider with information from the current song.
    public void updatePlaybackSlider(Song song){
        myPlaybackSlider.setMaximum(song.getMp3File().getFrameCount());

        // Create labels for the beginning and end of the slider.
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        JLabel theLabelBeginning = new JLabel("00:00");
        theLabelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        theLabelBeginning.setForeground(TEXT_COLOR);

        JLabel theLabelEnd =  new JLabel(song.getSongLength());
        theLabelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
        theLabelEnd.setForeground(TEXT_COLOR);

        labelTable.put(0, theLabelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(), theLabelEnd);

        myPlaybackSlider.setLabelTable(labelTable);
        myPlaybackSlider.setPaintLabels(true);
    }

    // Method to enable the pause button and disable the play button.
    public void enablePauseButtonDisablePlayButton(){
        JButton playButton = (JButton) myPlaybackBtns.getComponent(1);
        JButton pauseButton = (JButton) myPlaybackBtns.getComponent(2);
        playButton.setVisible(false);
        playButton.setEnabled(false);
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    // Method to enable the play button and disable the pause button.
    public void enablePlayButtonDisablePauseButton(){
        JButton playButton = (JButton) myPlaybackBtns.getComponent(1);
        JButton pauseButton = (JButton) myPlaybackBtns.getComponent(2);
        playButton.setVisible(true);
        playButton.setEnabled(true);
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    // Method to load an image from a file path and return an ImageIcon.
    private ImageIcon loadImage(String imagePath){
        try{
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Error: Could not load image.","Error",JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // Update the table with the current library
    private void updateTable() {
        String[][] theTableData = myLibrary.toArray();
        updateTable(theTableData);
        currentSongList = theTableData;
    }


    // Update the table with search results
    private void updateTable(boolean wasSearched) {
        String[][] theTableData = myLibrary.toArray(wasSearched);
        updateTable(theTableData);
        currentSongList = theTableData;
    }

    // Update the table with a specific list of songs
    private void updateTable(ArrayList<Song> songList) {
        String[][] theTableData = new String[songList.size()][4];
        for (int i = 0; i < songList.size(); i++) {theTableData[i][0] = i + 1 + "";
            theTableData[i][1] = songList.get(i).getSongTitle();
            theTableData[i][2] = songList.get(i).getSongArtist();
            theTableData[i][3] = songList.get(i).getSongGenre();
        }
        updateTable(theTableData);
        currentSongList = theTableData;
    }

    // Common method to update the JTable with given data
    private void updateTable(String[][] theTableData) {
        String[] theColumnNames = {"", "Title", "Artist", "Genre"};

        // Create a new TableModel and update the JTable.
        myLibraryTable.setModel(new javax.swing.table.DefaultTableModel(theTableData, theColumnNames) {
            @Override
            public boolean isCellEditable(int theRow, int column) {
                return false;
            }
        });

        // Adjust column widths.
        myLibraryTable.getColumnModel().getColumn(0).setPreferredWidth(5);
    }
}