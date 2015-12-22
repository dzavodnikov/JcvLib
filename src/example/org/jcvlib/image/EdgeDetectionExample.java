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
import org.jcvlib.gui.Window;
import org.jcvlib.image.filters.EdgeDetect;
import org.jcvlib.image.filters.Filters;
import org.jcvlib.image.filters.Threshold;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to use <a href="http://en.wikipedia.org/wiki/Edge_detection">edge detection</a> operators.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class EdgeDetectionExample {

    public static void main(final String[] args) throws IOException {
        // Read source image.
        final Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");

        // Convert from RGB color image to Gray-scale color image.
        final Image gray = ColorConvert.fromRGBtoGray(image.makeLayer(0, 3));

        // We just copy existing image to output results from other methods.
        final Image roberts = Filters.edgeDetection(gray, EdgeDetect.ROBERTS);
        final Image prewitt = Filters.edgeDetection(gray, EdgeDetect.PREWITT);
        final Image sobel = Filters.edgeDetection(gray, EdgeDetect.SOBEL);
        final Image scharr = Filters.edgeDetection(gray, EdgeDetect.SCHARR);

        // We can apply threshold to existing images with detected edges.
        final Image sobelT = Filters.threshold(sobel, 96, Threshold.BINARY);

        // Output results.
        Window.openAndShow(gray, "Lenna Gary");
        Window.openAndShow(roberts, "Lenna Roberts");
        Window.openAndShow(prewitt, "Lenna Prewitt");
        Window.openAndShow(sobel, "Lenna Sobel");
        Window.openAndShow(scharr, "Lenna Scharr");
        Window.openAndShow(sobelT, "Lenna Sobel Threshold");
    }
}
