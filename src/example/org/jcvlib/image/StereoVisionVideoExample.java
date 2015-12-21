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
package org.jcvlib.image;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.jcvlib.core.Image;
import org.jcvlib.core.Size;
import org.jcvlib.gui.Window;
import org.jcvlib.io.WebCamReader;

/**
 * This is example show how to use stereo vision algorithms.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class StereoVisionVideoExample {

    public static void main(final String[] args) throws IOException {
        // Configure web-camera for reading.
        final int fps = 30;
        final Size sizeOfGettingImage = new Size(320, 240);

        // Open web-camera for reading. If we not opened this camera method #getImage() will return null.
        final WebCamReader webCamLeft = new WebCamReader(0, fps, sizeOfGettingImage);
        webCamLeft.open();
        final Window windowLeft = new Window();

        final WebCamReader webCamRight = new WebCamReader(1, fps, sizeOfGettingImage);
        webCamRight.open();
        final Window windowRight = new Window();

        final Window windowMap = new Window();
        while (webCamLeft.isOpen() && webCamRight.isOpen() && windowLeft.getPressedKeyChar() != KeyEvent.VK_ESCAPE) {
            Image left = webCamLeft.getImage();
            Image right = webCamRight.getImage();

            // Find distance map.
            //final Image map = Stereo.getMap(grayLeft, grayRight);
            final Image map = Stereo.getMap(left, right);

            //map = Filters.blur(map, new Size(5, 5), Filters.BLUR_MEDIAN);
            //map = Filters.blur(map, new Size(5, 5), Filters.BLUR_GAUSSIAN);

            //map = Filters.morphology(map, new Size(5, 5), Filters.MORPHOLOGY_CLOSE, 3);
            //map = Filters.blur(map, new Size(5, 5), Filters.BLUR_GAUSSIAN);

            // Output results.
            windowLeft.show(left);
            windowRight.show(right);
            windowMap.show(map);
        }

        // Close opened windows!
        windowLeft.close();
        windowRight.close();
        windowMap.close();

        // Every time close web-cameras after using!
        webCamLeft.close();
        webCamRight.close();
    }
}
