package com.github.spacemex;

import com.github.spacemex.menus.Menu;
import com.github.spacemex.unreal.JTextPaneLogHandler;
import com.github.spacemex.unreal.UnrealBuildPlugin;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.PrintStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class Main {
    private static final boolean DEBUG = true ;
    private static String enginePath;
    private static double engineVersion;
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

        //Make the frame visible
        frame.setVisible(true);



        logger.severe("This Is An Error Message.");

    }
}

class BuildWorker extends SwingWorker<Void, Void> {
    private final UnrealBuildPlugin unreal;
    private final String customEnginePath;
    private final double engineVersion;
    private final String dir;
    private final String builtDir;
    private final Menu menu;

    public BuildWorker(UnrealBuildPlugin unreal, String customEnginePath, double engineVersion,
                       String dir, String builtDir, Menu menu) {
        this.unreal = unreal;
        this.customEnginePath = customEnginePath;
        this.engineVersion = engineVersion;
        this.dir = dir;
        this.builtDir = builtDir;
        this.menu = menu;
    }

    @Override
    protected Void doInBackground() {
        System.out.println("Build process started...");
        System.out.println("Custom Engine Path: " + customEnginePath);
        System.out.println("Engine Version: " + engineVersion);

        if (customEnginePath != null && !customEnginePath.isEmpty() && !customEnginePath.equalsIgnoreCase("Custom Engine Directory: ")) {
            unreal.addCustomPath(customEnginePath);
        }
        if (engineVersion != 0.0) {
            unreal.setEngineVersion(engineVersion);
        }
        unreal.initialize();
        if (dir != null && !dir.isEmpty() && !dir.equalsIgnoreCase("Plugin Directory/Repository: ")) {
            if (builtDir != null && !builtDir.isEmpty() && !builtDir.equalsIgnoreCase("Built Plugin Directory: ")) {
                unreal.start(dir, builtDir);
            } else {
                System.err.println("Directory Must Not Be Empty");
            }
        }
        return null;
    }

    @Override
    protected void done() {
        // Re-enable UI components if needed
        menu.setEnabled(true);

        // Optionally add additional steps to take after the task is done
        System.out.println("Build process finished.");
    }
}