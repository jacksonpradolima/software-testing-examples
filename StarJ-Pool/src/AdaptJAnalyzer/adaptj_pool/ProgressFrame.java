/* ========================================================================== *
 *                                   AdaptJ                                   *
 *              A Dynamic Application Profiling Toolkit for Java              *
 *                                                                            *
 *  Copyright (C) 2003-2004 Bruno Dufour                                      *
 *                                                                            *
 *  This software is under (heavy) development. Please send bug reports,      *
 *  comments or suggestions to bdufou1@sable.mcgill.ca.                       *
 *                                                                            *
 *  This library is free software; you can redistribute it and/or             *
 *  modify it under the terms of the GNU Library General Public               *
 *  License as published by the Free Software Foundation; either              *
 *  version 2 of the License, or (at your option) any later version.          *
 *                                                                            *
 *  This library is distributed in the hope that it will be useful,           *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU         *
 *  Library General Public License for more details.                          *
 *                                                                            *
 *  You should have received a copy of the GNU Library General Public         *
 *  License along with this library; if not, write to the                     *
 *  Free Software Foundation, Inc., 59 Temple Place - Suite 330,              *
 *  Boston, MA 02111-1307, USA.                                               *
 * ========================================================================== */

package adaptj_pool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 

/**
 * A custom <code>Frame</code> which is used to display a graphical progress indicator while
 * a long task is being performed. Typically, AdaptJ uses the <code>ProgressFrame</code> class
 * to display the progress while events are being processed. 
 *
 * @see JFrame
 * @see adaptj_pool.Scene
 */

public class ProgressFrame extends JFrame implements Runnable, WindowListener {
    private static final int PROGRESS_BAR_MAX = 100;
    public static final int DEFAULT_TIMEOUT = 1000;

    private JProgressBar progressBar;
    private JLabel label;
    private Thread progressThread = null;
    private boolean mustStop = false;
    private int timeout;
    
    /**
     * Constructs a JFrame which contains a <code>JLabel</code> and a <code>JProgressBar</code>.
     * This is used to display the progress (in terms of processed events) in AdaptJ
     */
    public ProgressFrame() {
        this(DEFAULT_TIMEOUT);
    }

    /**
     * Constructs a JFrame which contains a <code>JLabel</code> and a <code>JProgressBar</code>
     * with a specified update interval. This is used to display the progress (in terms
     * of processed events) in AdaptJ.
     *
     *  @param timeout The amount of time (in milliseconds) between updates
     */
    public ProgressFrame(int timeout) {
        super("AdaptJ - Progress");

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        progressBar = new JProgressBar(0, PROGRESS_BAR_MAX);
        progressBar.setValue(0);

        label = new JLabel("Initializing...");

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(label, BorderLayout.NORTH);
        contentPane.add(progressBar, BorderLayout.CENTER);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        pack();
        setSize(250, 125);
        centerOnScreen();
        setVisible(true);

        this.timeout = timeout;
        progressThread = new Thread(this);
        progressThread.start();
    }

    public void run() {
        long maxEvent = Scene.v().getEventCount();
        long eventsPerTick = maxEvent / ((long) PROGRESS_BAR_MAX);
        setIndeterminate(maxEvent < 0L);
        
        /* Wait for something to do */
        while (!Scene.v().isProcessing()) {
            Thread.yield();
        }

        label.setText("Processing Events");
        
        while (true) {
            try {
                long currentEvent = Scene.v().getCurrentEvent();
                label.setText("Processing Events: " + currentEvent);
                if (currentEvent == maxEvent) {

                }
                if (maxEvent > 0L && eventsPerTick > 0L) {
                    progressBar.setValue((int)(currentEvent / eventsPerTick));
                }
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                /* Stop if processing is over */
                if (!Scene.v().isProcessing() || mustStop) {
                    break;
                }
            }
        }

        label.setText("Finalizing...");
    }

    public void stop() {
        if (progressThread != null) {
            mustStop = true;
            progressThread.interrupt();
            try {
                progressThread.join();
            } catch (InterruptedException e) {
                // ignore 
            }
        }
        progressThread = null;
        setVisible(false);
        dispose();
    }

    public void update() {
        if (progressThread != null) {
            progressThread.interrupt();
        }
    }

    /*
    private void stepValue() {
        progressBar.setValue(progressBar.getValue() + 1);
    }
    */

    public void setIndeterminate(boolean indeterminate) {
        progressBar.setIndeterminate(indeterminate);
        if (indeterminate) {
            progressBar.setString("N/A");
        } else {
            progressBar.setString(null);
        }
        progressBar.setStringPainted(true);
    }
    
    /** 
     * Centers the window on the screen.
     */
    public void centerOnScreen() {
        Rectangle screen = getGraphicsConfiguration().getBounds();
        Rectangle progFrame = getBounds();

        int newX = screen.x + (screen.width  - progFrame.width) / 2;
        int newY = screen.y + (screen.height - progFrame.height) / 2;

        if (newX < 0) {
            newX = 0;
        }

        if (newY < 0) {
            newY = 0;
        }

        setLocation(newX, newY);
    }
    
    /**
     * Gets the timeout interval between updates.
     *
     * @return The length of interval in milliseconds
     */
    public int getTimeout() {
        return timeout;
    }

    /* Window Listener Interface */
    public void windowActivated(WindowEvent e) {

    }

    public void windowClosed(WindowEvent e) {

    }
    
    public void windowClosing(WindowEvent e) {
        stop();
    }

    public void windowDeactivated(WindowEvent e) {

    }

    public void windowDeiconified(WindowEvent e) {
    
    }

    public void windowIconified(WindowEvent e) {
    
    }

    public void windowOpened(WindowEvent e) {

    }
}
