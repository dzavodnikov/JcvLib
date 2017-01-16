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
package org.jcvlib.image;

import java.io.File;
import java.io.IOException;

import org.jcvlib.core.Image;
import org.jcvlib.gui.Window;
import org.jcvlib.image.filters.Filters;
import org.jcvlib.image.filters.Threshold;
import org.jcvlib.image.filters.ThresholdAdaptive;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to work with threshold filter.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ThresholdExample {

    public static final String RESOURCES = "src" + File.separatorChar + "main" + File.separatorChar + "resources";
    public static final String IMAGES    = RESOURCES + File.separatorChar + "images";

    public static void main(final String[] args) throws IOException {
        // Read image.
        final Image image = ImageRW.read(IMAGES + File.separatorChar + "Lenna.bmp");

        // To using threshold image should have only 1 channel.
        final Image imageGray = ColorConvert.fromRGBtoGray(image);

        // Apply threshold filter.
        final Image imageThreshold = Filters.threshold(imageGray, 127, Threshold.BINARY);
        final Image imageAdaptiveT = Filters.adapriveThreshold(imageGray, 7, ThresholdAdaptive.GAUSSIAN_INV, 0);

        // Show window with images.
        Window.openAndShow(image, "Lenna");
        Window.openAndShow(imageGray, "Lenna Gary");
        Window.openAndShow(imageThreshold, "Lenna Threshold");
        Window.openAndShow(imageAdaptiveT, "Lenna Adaptive Threshold");
    }
}
