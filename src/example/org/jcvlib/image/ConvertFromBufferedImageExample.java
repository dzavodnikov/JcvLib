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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.jcvlib.core.Image;
import org.jcvlib.gui.Window;

/**
 * This is example show how to using methods for convert images to or from other data types.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ConvertFromBufferedImageExample {

    public static void main(final String[] args) {
        // Dimensions of the image.
        final int width = 400;
        final int height = 400;

        // Let's create a BufferedImage for a binary image.
        final BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        // We need its raster to set the pixel's values.
        final WritableRaster raster = bi.getRaster();

        /*
         * Put the pixels on the raster. Note that only values 0 and 1 are used for the pixels.
         * You could even use other values: in this type of image, even values are black and odd
         * values are white.
         */
        for (int h = 0; h < height; ++h) {
            for (int w = 0; w < width; ++w) {
                if (((h / 50) + (w / 50)) % 2 == 0) {
                    // Checkerboard pattern.
                    raster.setSample(w, h, 0, 0);
                } else {
                    raster.setSample(w, h, 0, 1);
                }
            }
        }

        // Create Image8I based on BufferedImage.
        final Image image = TypeConvert.fromBufferedImage(bi);

        // Show result.
        Window.openAndShow(image);
    }
}
