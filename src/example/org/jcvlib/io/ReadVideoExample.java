/*
 * Copyright (c) 2012-2015 JcvLib Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import java.io.File;
import java.io.IOException;

import org.jcvlib.gui.Window;

/**
 * This is example show how to read and show video from file.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ReadVideoExample {

    public static void main(final String[] args) throws IOException {
        String filePath = "resources" + File.separatorChar + "Castle.mp4";
        String windowTitle = "Castle";

        // If you call this example with parameters -- use them.
        if (args.length > 0) {
            filePath = args[0];

            if (args.length > 1) {
                windowTitle = args[1];
            } else {
                windowTitle = null;
            }

        }

        // Create window to show images from video stream.
        final Window window = new Window(windowTitle);

        // Create video reader for get access to video-file.
        final VideoFileReader video = new VideoFileReader(filePath);
        video.open(); // Start reading.
        while (window.getPressedKeyChar() != KeyEvent.VK_ESCAPE) {
            // Get and show image.
            window.show(video.getImage());
        }
        // Close opened windows!
        window.close();

        // Close opened file.
        video.close();
    }
}
