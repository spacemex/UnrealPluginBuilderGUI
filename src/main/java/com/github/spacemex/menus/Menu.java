package com.github.spacemex.menus;

import javax.swing.*;

public class Menu {
    private JPanel mainPanel;
    private JToolBar engineBar;
    private JScrollPane logScrollPane;
    private JToolBar settingsBar;
    private JTextField customEngine;
    private JTextField engineVersion;
    private JTextField pluginFrom;
    private JTextField pluginOutput;
    private JButton buildButton;
    private JTextPane logs;

    public void setEnabled(boolean enabled){
        this.mainPanel.setEnabled(enabled);
        this.customEngine.setEnabled(enabled);
        this.engineVersion.setEnabled(enabled);
        this.pluginFrom.setEnabled(enabled);
        this.pluginOutput.setEnabled(enabled);
        this.buildButton.setEnabled(enabled);
    }
    public void ClearLogs(){
        this.logs.setText("");
    }

    public JPanel getPanel(){
        return this.mainPanel;
    }

    public JTextField getCustomEngine(){
        return this.customEngine;
    }
    public JTextField getEngineVersion(){
        return this.engineVersion;
    }
    public JTextField getPluginFrom(){
        return this.pluginFrom;
    }
    public JTextField getPluginOutput(){
        return this.pluginOutput;
    }
    public JButton getBuildButton(){
        return this.buildButton;
    }
    public JTextPane getLogs(){
        return this.logs;
    }
}
