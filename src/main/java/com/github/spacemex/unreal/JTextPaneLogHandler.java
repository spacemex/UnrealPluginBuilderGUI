package com.github.spacemex.unreal;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class JTextPaneLogHandler extends Handler {
    private final PrintStream outStream;
    private final PrintStream errStream;
    private final PrintStream warnStream;

    public JTextPaneLogHandler(PrintStream outStream, PrintStream errStream, PrintStream warnStream) {
        this.outStream = outStream;
        this.errStream = errStream;
        this.warnStream = warnStream;
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getLevel().equals(Level.SEVERE)) {
            errStream.println(record.getMessage());
        } else if (record.getLevel().equals(Level.WARNING)) {
            warnStream.println(record.getMessage());
        } else {
            outStream.println(record.getMessage());
        }
    }

    @Override
    public void flush() {
        outStream.flush();
        errStream.flush();
        warnStream.flush();
    }

    @Override
    public void close() throws SecurityException {
        outStream.close();
        errStream.close();
        warnStream.close();
    }
}