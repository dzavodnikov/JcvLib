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
import org.jcvlib.image.filters.Blur;
import org.jcvlib.image.filters.Filters;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to use different smooth filters.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class SmoothImageExample {

    public static void main(final String[] args) throws IOException {
        // Read image.
        final Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");

        // Smooth algorithms.
        final Image box = Filters.blur(image, new Size(7, 7), Blur.BOX);
        final Image gauss = Filters.blur(image, new Size(11, 11), Blur.GAUSSIAN);
        final Image median = Filters.blur(image, new Size(7, 7), Blur.MEDIAN);
        final Image kuwahara = Filters.blur(image, new Size(9, 9), Blur.KUWAHARA);

        // Show window with images.
        Window.openAndShow(image, "Lenna");
        Window.openAndShow(box, "Lenna Box Blur");
        Window.openAndShow(gauss, "Lenna Gaussian Blur");
        Window.openAndShow(median, "Lenna Median Blur");
        Window.openAndShow(kuwahara, "Lenna Kuwahara Blur");
    }
}
