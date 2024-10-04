package com.github.spacemex;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.io.IOException;
import java.io.OutputStream;

public class JTextPaneOutputStream extends OutputStream {
    private final JTextPane textPane;
    private final AttributeSet attributeSet;

    public JTextPaneOutputStream(JTextPane textPane, AttributeSet attributeSet) {
        this.textPane = textPane;
        this.attributeSet = attributeSet;
    }

    @Override
    public void write(int b) throws IOException {
        String s = String.valueOf((char) b);
        appendToPane(s);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        String s = new String(b, off, len);
        appendToPane(s);
    }

    private void appendToPane(String s) {
        SwingUtilities.invokeLater(() -> {
            try {
                Document doc = textPane.getDocument();
                doc.insertString(doc.getLength(), s, attributeSet);
                textPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                System.err.println("BadLocationException: " + e);
            }
        });
    }
}
