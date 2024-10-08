package com.github.spacemex;

import com.github.spacemex.menus.Menu;
import com.github.spacemex.unreal.JTextPaneLogHandler;
import com.github.spacemex.unreal.UnrealBuildPlugin;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.PrintStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class Main {
    private static final boolean DEBUG = true ;
    private static String enginePath;
    private static double engineVersion = 5.4;
    private static String dir;
    private static String builtDir;
    private static Logger logger;


    public static void main(String[] args) {
        logger = Logger.getLogger(Main.class.getName());


        UnrealBuildPlugin unreal = new UnrealBuildPlugin(logger);

        //Create Window
        JFrame frame = new JFrame("Unreal Engine Plugin Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(510,500);
        frame.setMinimumSize(new Dimension(510, 500));
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        //Initialize Menu
        Menu menu = new Menu();

        SimpleAttributeSet normalAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(normalAttr, Color.WHITE);

        SimpleAttributeSet errorAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(errorAttr, Color.RED);

        SimpleAttributeSet warningAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(warningAttr, Color.YELLOW);

        // Redirect logger.warn,logger.serve and logger.info to the logs JTextPane
        PrintStream outStream = new PrintStream(new JTextPaneOutputStream(menu.getLogs(), normalAttr));
        PrintStream errStream = new PrintStream(new JTextPaneOutputStream(menu.getLogs(), errorAttr));
        PrintStream warnStream = new PrintStream(new JTextPaneOutputStream(menu.getLogs(), warningAttr));

        JTextPaneLogHandler logHandler = new JTextPaneLogHandler(outStream, errStream, warnStream);
        logger.addHandler(logHandler);
        logger.addHandler(new ConsoleHandler()); // Adds logs to console

        if (DEBUG){
            logger.info("This Is An Info Message.");
            logger.warning("This Is A Warning Message.");
            logger.severe("This Is An Error Message.");
        }

        //Add the panel to the frame
        frame.setContentPane(menu.getPanel());

        menu.getEngineVersion().setText(String.valueOf(engineVersion));

        //Make the frame visible
        frame.setVisible(true);

        addDocumentListener(menu.getCustomEngine(), value -> enginePath = value);
        addDocumentListener(menu.getEngineVersion(), value -> {
            try {
                engineVersion = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                logger.severe("Invalid Engine Version: -> " + value);
            }
        });

        addDocumentListener(menu.getPluginFrom(), value -> dir = value);
        addDocumentListener(menu.getPluginOutput(), value -> builtDir = value);

        menu.getBuildButton().addActionListener(e -> {
            menu.ClearLogs();
            menu.setEnabled(false);
            new BuildWorker(unreal, enginePath, engineVersion, dir, builtDir, menu,logger).execute();
        });

    }
    private static void addDocumentListener(JTextField textField, ValueUpdater valueUpdater){
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChanged(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textChanged(e);
            }
            private void textChanged(DocumentEvent e) {
                String newValue = textField.getText();
                valueUpdater.update(newValue);
            }
        });
    }

    @FunctionalInterface
    interface ValueUpdater {
        void update(String value);
    }
}

class BuildWorker extends SwingWorker<Void, Void> {
    private final UnrealBuildPlugin unreal;
    private final String customEnginePath;
    private final double engineVersion;
    private final String dir;
    private final String builtDir;
    private final Menu menu;
    private final Logger logger;

    public BuildWorker(UnrealBuildPlugin unreal, String customEnginePath, double engineVersion,
                       String dir, String builtDir, Menu menu,Logger logger) {
        this.unreal = unreal;
        this.customEnginePath = customEnginePath;
        this.engineVersion = engineVersion;
        this.dir = dir;
        this.builtDir = builtDir;
        this.menu = menu;
        this.logger = logger;
    }

    @Override
    protected Void doInBackground() {
        logger.info("Build process started...");

        logger.info("Engine Version: " + engineVersion);

        if (customEnginePath != null && !customEnginePath.isEmpty()) {
            unreal.addCustomPath(customEnginePath);
            logger.info("Custom Engine Path: " + customEnginePath);
        }
        if (engineVersion > 0) {
            unreal.setEngineVersion(engineVersion);
            logger.info("Custom Engine Version: " + engineVersion);
        }
        unreal.initialize();
        if (dir != null && !dir.isEmpty() && !dir.equalsIgnoreCase("Plugin Directory/Repository: ")) {
            if (builtDir != null && !builtDir.isEmpty() && !builtDir.equalsIgnoreCase("Built Plugin Directory: ")) {
                unreal.start(dir, builtDir);
            } else {
                logger.severe("Built Plugin Directory is empty");
            }
        }
        return null;
    }

    @Override
    protected void done() {
        // Re-enable UI components if needed
        menu.setEnabled(true);

        // Optionally add additional steps to take after the task is done
        logger.info("Build Process Finished.");
    }
}
