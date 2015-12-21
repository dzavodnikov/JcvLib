/*
 * Copyright (c) 2012-2015 JcvLib Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
package org.jcvlib.video;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Size;
import org.jcvlib.gui.Window;
import org.jcvlib.image.ColorConvert;
import org.jcvlib.image.Misc;
import org.jcvlib.image.filters.Blur;
import org.jcvlib.image.filters.Filters;
import org.jcvlib.image.filters.Morphology;
import org.jcvlib.image.filters.Threshold;
import org.jcvlib.io.WebCamReader;

/**
 * This is example show how to use motion detector.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class MotionDetectorExample {

    public static void main(final String[] args) throws IOException {
        // Configure web-camera for reading.
        final int numberOfWebCam = 0;    // Cameras numbering with zero: 0 (first camera), 1 (second camera), ...
        final int fps = 30;
        final Size sizeOfGetImage = new Size(320, 240);

        // Parameters of image processing.
        final Size kernelSize = new Size(3, 3);
        final int motionThreshold = 1;

        // Motion History Image.
        final Image mhi = new Image(sizeOfGetImage.getWidth(), sizeOfGetImage.getHeight(), 1);
        mhi.fill(new Color(1, Color.MIN_VALUE));

        // Temporary images.
        Image previous = new Image(sizeOfGetImage.getWidth(), sizeOfGetImage.getHeight(), 1);
        previous.fill(new Color(1, Color.MIN_VALUE));

        // Open web-camera for reading. If we not opened this camera method #getImage() will return null.
        final WebCamReader webCam = new WebCamReader(numberOfWebCam, fps, sizeOfGetImage);
        webCam.open();

        // Create and open window to display video from web-camera.
        final Window window = new Window();

        // Start processing.
        while (window.getPressedKeyChar() != KeyEvent.VK_ESCAPE && webCam.isOpen()) {
            // 0. Get current image.
            Image current = ColorConvert.fromRGBtoGray(webCam.getImage());

            // 1. Blur image.
            Image blur = Filters.blur(current, kernelSize, Blur.GAUSSIAN);
            current = blur.makeCopy();

            // 2. Minus previous.
            blur = Misc.absDiff(blur, previous);

            // 3. Put blur current copy image to previous.
            previous = current.makeCopy();

            // 4. Threshold.
            Image thresh = Filters.threshold(blur, motionThreshold, Threshold.BINARY);

            // 5. Morphology close.
            Image close = Filters.morphology(thresh, kernelSize, Morphology.CLOSE);

            // 6. Morphology open.
            Image open = Filters.morphology(close, kernelSize, Morphology.OPEN);

            // 7. Update motion history image.
            VideoAnalysis.updateHistoryImage(mhi, open, 16.0);

            // Show motion history.
            window.show(mhi);
        }

        // Finished.
        window.close(); // Close opened windows!
        webCam.close(); // Every time close web-cameras after using!
    }
}
