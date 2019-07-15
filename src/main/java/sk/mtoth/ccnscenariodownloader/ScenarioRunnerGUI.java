package sk.mtoth.ccnscenariodownloader;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class ScenarioRunnerGUI extends JFrame implements ActionListener {

    protected static final String textFieldString = "URL to scenarios:";
    //    private static final String urlEnterAction = "URLEnteredAction";
    private static final String startDownloadAction = "StartDownloadAction";
    private static final String chooseLocationAction = "ChooseLocationAction";
    private static final int INPUT_COLUMNS = 40;
//    private String scenariosUrl = "http://ccnapoleonics.net/Maps/";
    static String scenariosUrl = "https://www.commandsandcolors.net/napoleonics/maps/scenario-list.html";
    static String downloadLocation = System.getProperty("user.home") + File.separator + "CCNScenarios";

    private JFormattedTextField urlTextField;
    private JFormattedTextField locationTextField;
    private JButton startButton;
    private JButton dirLocationButton;

    public ScenarioRunnerGUI() {
        setLookAndFeel();
        JFrame jFrame = buildFrame();

        DefaultFormatter formatter = new DefaultFormatter();
        formatter.setOverwriteMode(false);

        JPanel urlPanel = new JPanel(new GridLayout(1, 2, 5, 10));
        urlTextField = new JFormattedTextField(formatter);
        urlTextField.setText(scenariosUrl);
        urlTextField.setHorizontalAlignment(JTextField.LEFT); // ??
        urlTextField.setColumns(INPUT_COLUMNS);
        urlTextField.setInputVerifier(new urlInputVerifier());


        //Create some labels for the fields.
        JLabel textFieldLabel = new JLabel(textFieldString, SwingConstants.CENTER);
        textFieldLabel.setLabelFor(urlTextField);
        urlPanel.add(textFieldLabel);
        urlPanel.add(urlTextField);

        JPanel locationPanel = new JPanel(new GridLayout(1, 2, 5, 10));
        locationTextField = new JFormattedTextField(formatter);
        locationTextField.setText(downloadLocation);
        locationTextField.setColumns(INPUT_COLUMNS);
        locationTextField.setInputVerifier(new dirInputVerifier());
        dirLocationButton = new JButton("Choose download directory");
        dirLocationButton.setSize(new Dimension(80, 20));
        dirLocationButton.setEnabled(true);
        dirLocationButton.setActionCommand(chooseLocationAction);
        dirLocationButton.addActionListener(this);

        locationPanel.add(dirLocationButton);
        locationPanel.add(locationTextField);
//        locationPanel.setSize(new Dimension(300, 200));

        startButton = new JButton("Download!");
        startButton.setActionCommand(startDownloadAction);
        startButton.addActionListener(this);
        startButton.setForeground(Color.RED);
        startButton.setBorderPainted(true);
        startButton.setSize(80, 20);
        startButton.setMaximumSize(new Dimension(100, 20));


        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 5, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder("Configuration")));
        mainPanel.add(urlPanel);
        mainPanel.add(locationPanel);
        mainPanel.add(startButton, BorderLayout.SOUTH);

        jFrame.add(mainPanel);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
            System.err.println(e.getMessage());
        }
    }

    private JFrame buildFrame() {
        JFrame jFrame = new JFrame();
//        JMenuBar jMenuBar = new JMenuBar();
//        JMenu jMenuFile = new JMenu();
//        jMenuFile.setText("File");
//        jMenuBar.add(jMenuFile);
//        jFrame.setJMenuBar(jMenuBar);

        jFrame.setTitle("CCN Scenario Downloader");
        jFrame.setIconImage(this.getCCNImage());

        jFrame.setResizable(true);
        jFrame.setMinimumSize(new Dimension(400, 200));
        jFrame.setSize(new Dimension(500, 200));
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        return jFrame;
    }

    private Image getCCNImage() {
        BufferedImage bufferedImage = null;
        InputStream imageUrl;
        try {
            imageUrl = ScenarioRunnerGUI.class.getClassLoader().getResourceAsStream("napoleonics.png");
            System.out.println(imageUrl);
            if (imageUrl != null) {
                bufferedImage = ImageIO.read(imageUrl);
            } else {
                System.out.println("image is null");
            }
            if (imageUrl != null) imageUrl.close();
        } catch (IOException e) {
            System.err.println("Unable to read given file!");
            e.printStackTrace();
        }
        return bufferedImage;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        System.out.println("action=" + event.getActionCommand());
        if (startDownloadAction.equals(event.getActionCommand())) {
            if (urlTextField.getInputVerifier().verify(urlTextField) &&
                    locationTextField.getInputVerifier().verify(locationTextField)) {
                downloadLocation = locationTextField.getText();
                scenariosUrl = urlTextField.getText();
                ScenarioRunner.getOrUpdateScenarios(scenariosUrl);
            } else {
                startButton.setEnabled(false);
            }
        } else if (chooseLocationAction.equals(event.getActionCommand())) {
            openFileChooser();
        } else {
            System.err.println("Unknown action!");
        }
    }

    private File openFileChooser() {
        File file = null;
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(downloadLocation));
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int choseFile = jfc.showOpenDialog(dirLocationButton);
        if (choseFile == JFileChooser.APPROVE_OPTION) {
            file = jfc.getSelectedFile();
            System.out.println(file);
            locationTextField.setText(file.toString());
        }
        return file;
    }

    private class urlInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            boolean toReturn;
            String urlText = ((JFormattedTextField) input).getText();
            try {
                if (urlText != null) {
                    // Just checking for proper URL
                    new URL(urlText);
                    scenariosUrl = urlText;
                    System.out.println(scenariosUrl);
                    toReturn = true;
                } else {
                    System.err.println("Provided URL is empty");
                    toReturn = false;
                }
            } catch (MalformedURLException e) {
                toReturn = false;
            }
            if (toReturn) {
                urlTextField.setBackground(Color.WHITE);
                startButton.setEnabled(true);
            } else {
                urlTextField.setBackground(Color.RED);
            }
            return toReturn;
        }
    }

    private class dirInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            boolean isValid = false;
            JFormattedTextField inputField = ((JFormattedTextField) input);
            String downloadLocationInput = inputField.getText();
            if (downloadLocationInput == null || downloadLocationInput.equals("")) {
                inputField.setText(downloadLocation);
                inputField.setForeground(Color.GREEN);
            } else {
                inputField.setForeground(Color.BLACK);
                File dir = new File(downloadLocation);
                if (dir.exists() && dir.isDirectory() && dir.canWrite())
                    isValid = true;
                else if (!dir.exists() && dir.canWrite()) {
                    System.out.println("Creating directory " + dir.toString());
                    isValid = dir.mkdirs();
                } else {
                    isValid = false;
                }

                if (!isValid) {
                    locationTextField.setBackground(Color.RED);
                    startButton.setEnabled(false);
                } else {
                    locationTextField.setBackground(Color.WHITE);
                }
            }
            return isValid;
        }
    }
}
