import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame {
    // Color and font configurations for the GUI.
    public static final Font FONT = new Font("Dialog",Font.PLAIN,18);
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color TEXT_COLOR_SECONDARY = Color.GRAY;

    // The underlying MusicPlayer object responsible for audio playback.
    private MusicPlayer musicPlayer;

    // JFileChooser for browsing and selecting MP3 files.
    private JFileChooser jFileChooser;

    // Library to hold all Song objects.
    private Library library;

    // GUI components for displaying song information and controlling playback.
    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;
    private JSlider playbackSlider;
    private JTable libraryTable;

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

        // Initialize the MusicPlayer and JFileChooser objects.
        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();

        // Set the default directory for the file chooser.
        jFileChooser.setCurrentDirectory(new File("Java-Swing-MusicPlayer-main/src/assets"));

        // Initialize the Library with the path to the song directory.
        library = new Library("Java-Swing-MusicPlayer-main/src/assets/songs");
        System.out.println(library.toString());

        // Filter the file chooser to display only MP3 files.
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        // Add the GUI components to the window.
        addGuiComponents();
    }

    private void addGuiComponents() {
        // Add a main panel to hold both search and sort panels in the NORTH.
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(null); // Set transparent background.
        add(northPanel, BorderLayout.NORTH);

        // Search Panel.
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(null); // Set transparent background.
        northPanel.add(searchPanel);

        JTextField searchBox = new JTextField(20);
        searchPanel.add(searchBox);

        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        // Sort Panel.
        JPanel sortPanel = new JPanel();
        sortPanel.setBackground(null); // Set transparent background.
        northPanel.add(sortPanel);

        String[] sortOptions = {"Title", "Artist", "Genre"};
        JComboBox<String> sortDropdown = new JComboBox<>(sortOptions);
        sortPanel.add(sortDropdown);

        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(e -> {
            String selectedOption = (String) sortDropdown.getSelectedItem();
            // Add your sorting logic here based on selectedOption.
            // For example:
            // if (selectedOption.equals("Title")) { library.sortByTitle(); }
            //library.sort();
            //updateTable();
        });
        sortPanel.add(sortButton);

        // Center Panel.
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        add(centerPanel, BorderLayout.CENTER);

        // Table to display the music library.
        String[] columnNames = {"", "Title", "Artist", "Genre"};
        String[][] tableData = library.toArray();

        libraryTable = new JTable(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable.
            }
        };
        libraryTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering.
        libraryTable.getTableHeader().setResizingAllowed(false);   // Prevent column resizing.

        JScrollPane scrollPane = new JScrollPane(libraryTable);
        libraryTable.getColumnModel().getColumn(0).setPreferredWidth(5); // Set preferred width for the first column.
        scrollPane.setPreferredSize(new Dimension(300, 100)); // Reduced height to 100.
        centerPanel.add(scrollPane);

        // Add MouseListener to handle song selection from the table.
        libraryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = libraryTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String tableSelection = (String) libraryTable.getValueAt(row, 1);
                    Song songSelection = library.getSong(tableSelection);
                    musicPlayer.setSelectedSong(songSelection);
                }
            }
        });

        // Song Information Panel.
        songTitle = new JLabel("Song Title");
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        songArtist = new JLabel("Artist");
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 18));
        songArtist.setForeground(TEXT_COLOR_SECONDARY);
        songArtist.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel songInfoPanel = new JPanel();
        songInfoPanel.setLayout(new BoxLayout(songInfoPanel, BoxLayout.Y_AXIS));
        songInfoPanel.setOpaque(false); // Make the panel transparent.
        songInfoPanel.add(songTitle);
        songInfoPanel.add(songArtist);
        centerPanel.setBackground(null); // Set transparent background.
        centerPanel.add(songInfoPanel);

        // Playback Slider.
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBackground(null); // Set transparent background.
        playbackSlider.setVisible(false);
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                musicPlayer.pauseSong(); // Pause playback when slider is pressed.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JSlider source = (JSlider) e.getSource();
                int frame = source.getValue();
                musicPlayer.setCurrentFrame(frame);
                musicPlayer.setCurrentTimeInMilli((int) (frame / (2.08 * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds())));
                musicPlayer.playCurrentSong(); // Resume playback when slider is released.
                enablePauseButtonDisablePlayButton();
            }
        });
        centerPanel.add(playbackSlider);

        // Add Playback Buttons to the GUI.
        addPlaybackBtns();
    }


    // Method to add playback control buttons to the GUI.
    private void addPlaybackBtns(){
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbackBtns.setBackground(null); // Set transparent background.

        // Previous button.
        JButton prevButton = new JButton(loadImage("Java-Swing-MusicPlayer-main/src/assets/previous.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null); // Set transparent background.
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.prevSong(); // Go to the previous song.
            }
        });
        playbackBtns.add(prevButton);

        // Play button.
        JButton playButton = new JButton(loadImage("Java-Swing-MusicPlayer-main/src/assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null); // Set transparent background.
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePauseButtonDisablePlayButton();
                musicPlayer.playCurrentSong(); // Start or resume playback.
            }
        });
        playbackBtns.add(playButton);

        // Pause button (initially invisible).
        JButton pauseButton = new JButton(loadImage("Java-Swing-MusicPlayer-main/src/assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null); // Set transparent background.
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePlayButtonDisablePauseButton();
                musicPlayer.pauseSong(); // Pause playback.
            }
        });
        playbackBtns.add(pauseButton);

        // Next button.
        JButton nextButton = new JButton(loadImage("Java-Swing-MusicPlayer-main/src/assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null); // Set transparent background.
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.nextSong(); // Go to the next song.
            }
        });
        playbackBtns.add(nextButton);

        // Add the playback buttons panel to the bottom (SOUTH) of the BorderLayout.
        add(playbackBtns, BorderLayout.SOUTH);
    }

    // Method to update the playback slider value from the MusicPlayer.
    public void setPlaybackSliderValue(int frame){
        playbackSlider.setValue(frame);
    }

    // Method to update the song title and artist labels.
    public void updateSongTitleAndArtist(Song song){
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    // Method to update the playback slider with information from the current song.
    public void updatePlaybackSlider(Song song){
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        // Create labels for the beginning and end of the slider.
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        JLabel labelBeginning = new JLabel("00:00");
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        labelBeginning.setForeground(TEXT_COLOR);

        JLabel labelEnd =  new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
        labelEnd.setForeground(TEXT_COLOR);

        labelTable.put(0, labelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    // Method to enable the pause button and disable the play button.
    public void enablePauseButtonDisablePlayButton(){
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);
        playButton.setVisible(false);
        playButton.setEnabled(false);
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    // Method to enable the play button and disable the pause button.
    public void enablePlayButtonDisablePauseButton(){
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);
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
            e.printStackTrace();
        }
        return null;
    }
}