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

import java.io.File;
import java.io.IOException;

import org.jcvlib.core.Image;
import org.jcvlib.core.Size;
import org.jcvlib.gui.Window;
import org.jcvlib.image.filters.Filters;
import org.jcvlib.image.filters.Morphology;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to use <a href="http://en.wikipedia.org/wiki/Mathematical_morphology">morphology
 * operations</a>.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ErodeDilateExample {

    public static void main(final String[] args) throws IOException {
        // Read source image.
        final Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");

        // Apply morphology operations.
        final Image erode = Filters.morphology(image, new Size(3, 3), Morphology.ERODE, 2);
        final Image dilate = Filters.morphology(image, new Size(3, 3), Morphology.DILATE, 2);
        final Image open = Filters.morphology(image, new Size(3, 3), Morphology.OPEN, 2);
        final Image close = Filters.morphology(image, new Size(3, 3), Morphology.CLOSE, 2);

        // Output results.
        Window.openAndShow(image, "Lenna");
        Window.openAndShow(erode, "Lenna Erode");
        Window.openAndShow(dilate, "Lenna Dilate");
        Window.openAndShow(open, "Lenna Open");
        Window.openAndShow(close, "Lenna Close");
    }
}
