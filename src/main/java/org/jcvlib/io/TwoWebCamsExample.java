/*
 * Copyright (c) 2012-2016 JcvLib Team
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
package org.jcvlib.io;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.jcvlib.core.Size;
import org.jcvlib.gui.Window;

/**
 * This is example show how to read images from 2 web-cameras for stereoscopic applications.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class TwoWebCamsExample {

    public static void main(final String[] args) {
        // Configure web-cameras.
        final int fps = 30;
        final Size sizeOfGettingImage = new Size(320, 240);

        // Create objects for reading information from web-cameras.
        final WebCamReader webCam1 = new WebCamReader(0, fps, sizeOfGettingImage);
        final WebCamReader webCam2 = new WebCamReader(1, fps, sizeOfGettingImage);

        // Create windows to show video from web-cameras.
        final Window window1;
        final Window window2;

        /*
         * Every time use this construction if you want to control open/close web-camera!
         */
        try {
            // Open web-cameras.
            webCam1.open();
            webCam2.open();

            // Create windows to show video from web-cameras.
            window1 = new Window();
            window2 = new Window();

            /*
             * Reading data from web-cameras until this web-cameras (if web-camera is closed method #getImage() return null)
             * and windows are opened.
             */
            while (webCam1.isOpen() && webCam2.isOpen() && Window.getLastPressedKeyChar() != KeyEvent.VK_ESCAPE) {
                // Reading images from web-cameras and output it to window.
                window1.show(webCam1.getImage());
                window2.show(webCam2.getImage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // Close all opened windows.
        Window.closeAll();

        /*
         * Close all opened web-cameras. If one camera have a problem (throw exception) the
         * second camera will be working. Because of that we should close all cameras after
         * catch-construction.
         *
         * Every time close web-cameras after using!
         */
        webCam1.close();
        webCam2.close();
    }
}
