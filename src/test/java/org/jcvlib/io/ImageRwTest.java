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

import java.io.File;
import java.io.IOException;

import org.jcvlib.core.Color;
import org.jcvlib.core.Histogram;
import org.jcvlib.core.Image;
import org.jcvlib.image.ColorConvert;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link ImageRW}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ImageRwTest {

    /**
     * Test method for: {@link ImageRW#write(Image, String)}.
     */
    @Test
    public void testBMP() {
        final Image image = ImageRwTest.init(1500, 1200, 3);
        ImageRwTest.writeAndReadTest(image, "BMP");
        ImageRwTest.writeAndReadHistogramTest(image, "BMP");
    }

    /**
     * Test method for: {@link ImageRW#write(Image, String)}.
     */
    @Test
    public void testJPG() {
        final Image image = ImageRwTest.init(1500, 1200, 3);
        ImageRwTest.writeAndReadHistogramTest(image, "JPG");
    }

    /**
     * Test method for: {@link ImageRW#write(Image, String)}.
     */
    @Test
    public void testPNG() {
        final Image image = ImageRwTest.init(1500, 1200, 4);
        ImageRwTest.writeAndReadTest(image, "PNG");
        ImageRwTest.writeAndReadHistogramTest(image, "PNG");
    }

    /**
     * Create and initialize new {@link JcvImage64F}.
     */
    private static Image init(final int width, final int height, final int numOfChannels) {
        final Image image = new Image(width, height, numOfChannels);

        int value = Color.MIN_VALUE;
        for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int x = 0; x < image.getWidth(); ++x) {
                    image.set(x, y, channel, value);

                    ++value;
                    if (value > Color.MAX_VALUE) {
                        value = Color.MIN_VALUE;
                    }
                }
            }
        }

        return image;
    }

    /**
     * Write, read and compare images.
     *
     * @param fileExtension
     *            File extension for temporary saving.
     */
    private static void writeAndReadTest(final Image image, final String fileExtension) {
        try {
            final String imagePath = "Test." + fileExtension;
            ImageRW.write(image, imagePath);
            final Image newImage = ImageRW.read(imagePath);

            Assert.assertTrue(image.equals(newImage));

            // Remove temp file.
            new File(imagePath).delete();
        } catch (final IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Write, read and compare histogram images.
     *
     * @param fileExtension
     *            File extension for temporary saving.
     */
    private static void writeAndReadHistogramTest(final Image image, final String fileExtension) {
        try {
            final String imagePath = "Test." + fileExtension;
            ImageRW.write(image, imagePath);
            final Image newImage = ImageRW.read(imagePath);

            final Image imageBW = ColorConvert.fromRGBtoGray(image.makeLayer(0, 3));
            final Histogram imageBWHist = new Histogram(imageBW);

            final Image newImageBW = ColorConvert.fromRGBtoGray(newImage.makeLayer(0, 3));
            final Histogram newImageBWHist = new Histogram(newImageBW);

            Assert.assertEquals(1.0, imageBWHist.compare(newImageBWHist, Histogram.HISTOGRAM_COMPARE_CORREL), 0.1);
            Assert.assertEquals(0.0, imageBWHist.compare(newImageBWHist, Histogram.HISTOGRAM_COMPARE_CHISQR), 0.1);
            Assert.assertEquals(1.0, imageBWHist.compare(newImageBWHist, Histogram.HISTOGRAM_COMPARE_INTERSECT), 0.5);
            Assert.assertEquals(0.0, imageBWHist.compare(newImageBWHist, Histogram.HISTOGRAM_COMPARE_BHATTACHARYYA),
                    0.5);

            // Remove temp file.
            new File(imagePath).delete();
        } catch (final IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
