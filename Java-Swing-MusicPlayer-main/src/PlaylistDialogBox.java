import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class PlaylistDialogBox extends JDialog {

    private final MusicPlayerGUI musicPlayerGUI;

    // Store all of the paths to be written to a txt file (when we load a playlist)
    private ArrayList<Song> playlistLibrary;
    private PlaylistTableModel tableModel;

    public PlaylistDialogBox(ArrayList<Song> playlistLibrary, MusicPlayerGUI gui) {
        this.musicPlayerGUI = gui;
        this.playlistLibrary = playlistLibrary;

        // Configure dialog
        setTitle("Create Playlist");
        setSize(400, 400);
        setResizable(false);
        getContentPane().setBackground(MusicPlayerGUI.FRAME_COLOR);
        setLayout(null);
        setModal(true); // this property makes it so that the dialog has to be closed to give focus
        setLocationRelativeTo(musicPlayerGUI);

        addDialogComponents();
    }

    private void addDialogComponents() {
        tableModel = new PlaylistTableModel(playlistLibrary);
        JTable playlistTable = new JTable(tableModel);

        // Add a TableModelListener to handle checkbox changes
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // Handle changes to the checkbox state
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 0) { // Checkbox column
                    Boolean checked = (Boolean) tableModel.getValueAt(row, column);
                    System.out.println("Row " + row + " checkbox is " + (checked ? "selected" : "deselected"));
                }
            }
        });

        // Wrap the table in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(playlistTable);
        scrollPane.setBounds((int) (getWidth() * 0.025), 10, (int) (getWidth() * 0.95), (int) (getHeight() * 0.75));
        add(scrollPane);

        // Playlist name input field
        JTextField playlistNameBox = new JTextField("Playlist Name Here...");
        playlistNameBox.setBounds(10, (int) (getHeight() * 0.80), 200, 25);
        playlistNameBox.setFont(new Font("Dialog", Font.PLAIN, 14));
        add(playlistNameBox);

        // Save playlist button
        JButton savePlaylistButton = new JButton("Save");
        savePlaylistButton.setBounds(215, (int) (getHeight() * 0.80), 100, 25);
        savePlaylistButton.setFont(new Font("Dialog", Font.BOLD, 14));
        savePlaylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePlaylist(playlistNameBox.getText());
            }
        });
        add(savePlaylistButton);
    }

    private void savePlaylist(String playlistName) {
        ArrayList<Song> selectedSongs = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.isSelected(i)) {
                selectedSongs.add(playlistLibrary.get(i));
            }
        }

        if (selectedSongs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No songs selected for the playlist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Playlist");
        fileChooser.setCurrentDirectory(new File("src/assets/playlist"));
        fileChooser.setSelectedFile(new File(playlistName + ".txt")); // Default filename

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File playlistFile = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(playlistFile))) {
                for (Song song : selectedSongs) {
                    writer.write(song.getFilePath());
                    writer.newLine();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving playlist: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class PlaylistTableModel extends AbstractTableModel {
    private ArrayList<Song> songs;
    private ArrayList<Boolean> selected;

    public PlaylistTableModel(ArrayList<Song> songs) {
        this.songs = songs;
        this.selected = new ArrayList<>(Collections.nCopies(songs.size(), false)); // Initially all unselected
    }

    @Override
    public int getRowCount() {
        return songs.size();
    }

    @Override
    public int getColumnCount() {
        return 4; // Title, Artist, Genre, Checkbox
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return selected.get(rowIndex); // Checkbox value
        } else if (columnIndex == 1) {
            return songs.get(rowIndex).getSongTitle();
        } else if (columnIndex == 2) {
            return songs.get(rowIndex).getSongArtist();
        } else {
            return songs.get(rowIndex).getSongGenre();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class; // Checkbox column
        } else {
            return String.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Select"; // Or leave it blank ""
        } else if (column == 1) {
            return "Title";
        } else if (column == 2) {
            return "Artist";
        } else {
            return "Genre";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0; // Only the checkbox column is editable
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            selected.set(rowIndex, (Boolean) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public boolean isSelected(int rowIndex) {
        return selected.get(rowIndex);
    }
}