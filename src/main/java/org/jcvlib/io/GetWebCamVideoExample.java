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
package org.jcvlib.io;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.jcvlib.core.Size;
import org.jcvlib.gui.Window;

/**
 * This is example show how to read images from web-camera.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class GetWebCamVideoExample {

    public static void main(final String[] args) throws IOException {
        // Configure web-camera for reading.
        final int numberOfWebCam = 0; // Cameras numbering with zero: 0 (first camera), 1 (second camera), ...
        final int fps = 30;
        final Size sizeOfGettingImage = new Size(320, 240);

        // Open web-camera for reading. If we not opened this camera method #getImage() will return null.
        final WebCamReader webCam = new WebCamReader(numberOfWebCam, fps, sizeOfGettingImage);
        webCam.open();

        // Create and open window to display video from web-camera.
        final Window window = new Window();

        // Reading images from web-camera wile window and web-camera are opened.
        while (window.getPressedKeyChar() != KeyEvent.VK_ESCAPE && webCam.isOpen()) {
            window.show(webCam.getImage());
        }
        // Close opened windows!
        window.close();

        // Every time close web-cameras after using!
        webCam.close();
    }
}
