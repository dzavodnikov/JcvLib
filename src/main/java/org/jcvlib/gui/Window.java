/*
 * Copyright (c) 2017 JcvLib Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * This class is part of Java Computer Vision Library (JcvLib).
 */
package org.jcvlib.gui;

import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.jcvlib.core.Point;
import org.jcvlib.image.TypeConvert;
import org.jcvlib.io.ImageRW;

/**
 * Base class for all GUI functions.
 * <p>
 * Example of usage: <code><pre>
 * // Image image = ...
 * Window window = new Window("My Image");
 * window.show(image);
 * ...
 * // window.close();   // Needed if you want close window from your program.
 * </pre></code> or just: <code><pre>
 * // Image image = ...
 * Window.openAndShow(image, "My Image");
 * </pre></code>
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
/*
 * See:
 *      http://introcs.cs.princeton.edu/java/stdlib/Picture.java.html
 */
public class Window {

    /**
     * Contain links to all windows.
     */
    private static ArrayList<Window> allWindows             = new ArrayList<>();

    /**
     * Define char of last pressed keys in all windows.
     */
    private static int               allLastPressedKeyChar;

    /**
     * Frame of window.
     */
    private JFrame                   frame;

    /**
     * Component with image.
     */
    private ImageComponent           imageComponent;

    /**
     * Image to show.
     */
    private Image                    image;

    /**
     * Title of window.
     */
    private String                   title;

    /**
     * Define if current window is opened.
     */
    private boolean                  wasOpened;

    /**
     * Define char of last pressed keys in current window.
     */
    private int                      pressedKeyChar;

    /**
     * Mouse position.
     */
    private Point                    mousePos;

    /**
     * Toolbar information template.
     */
    private final static String      toolbarMessageTemplate = "Size: {0}    Ch: {2}";

    /**
     * Simplest way show image. As name of image will be used hash code of given image.
     */
    public static void openAndShow(final Image image) {
        Window.openAndShow(image, null);
    }

    /**
     * Simplest way show image.
     */
    public static void openAndShow(final Image image, final String title) {
        final Window localWind = new Window(title);
        localWind.show(image);
    }

    /**
     * Create new window with defined size. As name of image will be used hash code of given image.
     */
    public Window() {
        this(null);
    }

    /**
     * Create new window with defined size and given name of this window.
     */
    public Window(final String title) {
        this.wasOpened = false;
        this.title = title;

        // Add link to global list.
        synchronized (Window.allWindows) {
            Window.allWindows.add(this);
        }
    }

    /**
     * Set current window is visible.
     */
    private void open() {
        this.wasOpened = true;

        // Create the GUI for viewing the image if needed.
        this.frame = new JFrame();

        // Allow change size of window.
        this.frame.setResizable(true);

        // Add listener for correct close windows.
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                close(); // Close current window.
            }
        });

        // Create listener for close all open windows on press <Esc> button.
        this.frame.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(final KeyEvent event) {
                // Do nothing.
            }

            @Override
            public void keyReleased(final KeyEvent event) {
                setKeyChar(event.getKeyChar());

                // Listener for close all windows by press 'Esc' button.
                if (Window.getLastPressedKeyChar() == KeyEvent.VK_ESCAPE) {
                    Window.closeAll();
                }
            }

            @Override
            public void keyTyped(final KeyEvent event) {
                // Do nothing.
            }
        });

        // Set title to window.
        if (this.title == null) {
            this.title = Integer.toString(this.image.hashCode());
        }
        this.frame.setTitle(this.title);

        // Create panel with image.
        this.imageComponent = new ImageComponent(TypeConvert.toBufferedImage(this.image));
        final JScrollPane panel = new JScrollPane(this.imageComponent);
        this.frame.add(panel);

        // Create menu bar.
        final JMenuBar menuBar = new JMenuBar();
        this.frame.setJMenuBar(menuBar);

        createFileMenu(menuBar);
        createResizeMenu(menuBar);

        // Create status bar.
        createStatusBar();

        // Set objects onto canvas.
        this.frame.pack();
        this.frame.setVisible(true);
    }

    private void setKeyChar(final int keyChar) {
        this.pressedKeyChar = keyChar;
        Window.allLastPressedKeyChar = keyChar;
    }

    /**
     * Show or update image in current window.
     */
    public void show(final Image image) {
        this.image = image;

        // Create window if needed.
        if (!this.wasOpened) {
            open();
        }

        this.imageComponent.setBufferedImage(TypeConvert.toBufferedImage(this.image));
        this.frame.repaint();
    }

    /**
     * Close current window if it was opened. Same as click on close window button.
     */
    public void close() {
        this.wasOpened = false;

        /*
         * See:
         * * http://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe
         */
        this.frame.dispose();
    }

    /**
     * Close all opened windows. Same as press <strong>Esc</strong> key.
     */
    public static synchronized void closeAll() {
        if (Window.allWindows != null) {
            for (final Window window : Window.allWindows) {
                window.close();
            }
        }
    }

    /**
     * Return last key char of pressed in current window.
     */
    public int getPressedKeyChar() {
        return this.pressedKeyChar;
    }

    /**
     * Return mouse point on image.
     */
    public Point getMousePoint() {
        return this.mousePos;
    }

    /**
     * Return last key char of last pressed key in some opened window in current application.
     * <p>
     * If opened only one window result will be same as {@link #getPressedKeyChar()}.
     * </p>
     */
    public static int getLastPressedKeyChar() {
        return Window.allLastPressedKeyChar;
    }

    private void createFileMenu(final JMenuBar menuBar) {
        // Create menu 'File'.
        final JMenu fileMenu = new JMenu(" File   ");
        menuBar.add(fileMenu);

        /*
         * Add menu 'Save...' menu item.
         */
        final JMenuItem saveMenuItem = new JMenuItem(" Save...    ");
        fileMenu.add(saveMenuItem);

        saveMenuItem.addActionListener(action -> {
            final FileDialog chooser = new FileDialog(Window.this.frame, "Select path and name to save file",
                    FileDialog.SAVE);

            chooser.setVisible(true);
            if (chooser.getFile() != null) {
                try {
                    // Verify file name: name should include file name and extension.
                    String fileName;
                    String fileFormat;

                    if (chooser.getFile().lastIndexOf('.') >= 0) {
                        fileName = chooser.getFile().substring(chooser.getFile().lastIndexOf(File.separatorChar) + 1,
                                chooser.getFile().lastIndexOf('.'));
                        fileFormat = chooser.getFile().substring(chooser.getFile().lastIndexOf('.') + 1).toLowerCase();
                    } else {
                        fileName = chooser.getFile();
                        // File format is not specified -- PNG is default.
                        fileFormat = "png";
                        System.out.println("You not specified file format. By default, will be use PNG format.");
                    }

                    // Set new name title of window according to file name to saving image.
                    Window.this.frame.setTitle(fileName);

                    // Write image to file.
                    final String fullFilePath = chooser.getDirectory() + File.separator + fileName + "." + fileFormat;
                    ImageRW.write(Window.this.image, fullFilePath);
                } catch (final Exception e) {
                    e.printStackTrace();
                    System.err.println("Can not save image: " + e.getMessage());
                }
            }
        });
        // Add hot-key <Ctrl>+<S> to call 'File' -> 'Save...' menu item.
        saveMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        /*
         * Add menu 'Quit...' menu item.
         */
        final JMenuItem quitMenuItem = new JMenuItem(" Quit       ");
        fileMenu.add(quitMenuItem);

        quitMenuItem.addActionListener(action -> Window.this.frame.dispose());
        // Add hot-key <Ctrl>+<Q> to call 'File' -> 'Quite' menu item.
        quitMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    private void createResizeMenu(final JMenuBar menuBar) {
        // Create menu 'Resize'.
        final JMenu resizeMenu = new JMenu(" Resize ");
        menuBar.add(resizeMenu);

        /*
         * Add menu 'Zoom +' menu item.
         */
        final JMenuItem zoomPlusMenuItem = new JMenuItem(" Zoom +     ");
        resizeMenu.add(zoomPlusMenuItem);

        zoomPlusMenuItem.addActionListener(
                action -> Window.this.imageComponent.setScale(Window.this.imageComponent.getScale() + 0.1f));
        // Add hot-key <Ctrl>+<+> to call 'Resize' -> 'Zoom +' menu item.
        zoomPlusMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        /*
         * Add menu 'Zoom --' menu item.
         */
        final JMenuItem zoomMinusMenuItem = new JMenuItem(" Zoom --    ");
        resizeMenu.add(zoomMinusMenuItem);

        zoomMinusMenuItem.addActionListener(
                action -> Window.this.imageComponent.setScale(Window.this.imageComponent.getScale() - 0.1f));
        // Add hot-key <Ctrl>+<-> to call 'Resize' -> 'Zoom --' menu item.
        zoomMinusMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        /*
         * Add menu 'Zoom 0' menu item.
         */
        final JMenuItem zoomDefaultMenuItem = new JMenuItem(" Zoom 0      ");
        resizeMenu.add(zoomDefaultMenuItem);

        zoomDefaultMenuItem.addActionListener(action -> Window.this.imageComponent.setDefaultScale());
        // Add hot-key <Ctrl>+<0> to call 'Resize' -> 'Zoom 0' menu item.
        zoomDefaultMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    private void createStatusBar() {
        final StatusBar statusBar = new StatusBar();
        this.frame.add(statusBar, java.awt.BorderLayout.SOUTH);

        final String baseMsg = MessageFormat.format(Window.toolbarMessageTemplate,
                JCV.getSizeString(this.image.getWidth(), this.image.getHeight()), this.image.getNumOfChannels());
        // Set status bar message.
        statusBar.setMessage(baseMsg);
        this.imageComponent.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(final MouseEvent event) {
                // Do nothing.
                System.out.println(event.getX() + ", " + event.getY());
            }

            @Override
            public void mouseMoved(final MouseEvent event) {
                // Get mouse position.
                final int x = event.getX();
                final int y = event.getY();

                // Create output info.
                if (x < Window.this.image.getWidth() && y < Window.this.image.getHeight()) {
                    Window.this.mousePos = new Point(x, y);
                    final StringBuilder sb = new StringBuilder();

                    // Output position.
                    sb.append("    Point");
                    sb.append(Window.this.mousePos.toString());

                    // Get color value.
                    sb.append(" = Color");
                    final Color color = new Color(Window.this.image.getNumOfChannels());
                    Window.this.image.get(Window.this.mousePos, color);
                    sb.append(color.toString());

                    // Update status bar message.
                    statusBar.setMessage(baseMsg + sb.toString());
                }
            }
        });
    }
}
