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

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link ColorConvert} to convert color schemas.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ColorConvertTest {

    private int   width;

    private int   height;

    private int   numOfChannels;

    private Image imageRGB;

    /**
     * Executes before creating instance of the class.
     */
    @Before
    public void setUp() throws Exception {
        this.width = 5;
        this.height = 4;

        // Returned image should be contain only 3 channels.
        this.numOfChannels = 3;

        // Create image.
        this.imageRGB = new Image(this.width, this.height, this.numOfChannels);

        // Initialize image.
        int value = JCV.round(Color.MIN_VALUE);
        for (int x = 0; x < this.imageRGB.getWidth(); ++x) {
            for (int y = 0; y < this.imageRGB.getHeight(); ++y) {
                for (int channel = 0; channel < this.imageRGB.getNumOfChannels(); ++channel) {
                    this.imageRGB.set(x, y, channel, value);

                    ++value;
                    if (value > Color.MAX_VALUE) {
                        value = JCV.round(Color.MIN_VALUE);
                    }
                }
            }
        }
    }

    /**
     * Test method for: {@link ColorConvert#fromRGBtoGray(Image)}, {@link ColorConvert#fromGrayToRGB(Image)}.
     */
    @Test
    public void testRGBtoGrayToRGB() {
        final Image imgGray = ColorConvert.fromRGBtoGray(this.imageRGB);
        Assert.assertFalse(imgGray.equals(this.imageRGB));

        final Image imgRGB = ColorConvert.fromGrayToRGB(imgGray);
        for (int channel = 0; channel < imgRGB.getNumOfChannels(); ++channel) {
            Assert.assertTrue(imgRGB.makeChannel(channel).equals(imgGray));
        }
    }

    /**
     * Test method for: {@link ColorConvert#fromRGBtoHSL(Image)}, {@link ColorConvert#fromHSLtoRGB(Image)}.
     */
    @Test
    public void testRGBtoHSLtoRGB() {
        final Image imgHLS = ColorConvert.fromRGBtoHSL(this.imageRGB);
        Assert.assertFalse(imgHLS.equals(this.imageRGB));

        final Image imgRGB = ColorConvert.fromHSLtoRGB(imgHLS);
        Assert.assertTrue(imgRGB.equals(this.imageRGB));
    }

    /**
     * Test method for: {@link ColorConvert#fromRGBtoHSV(Image)}, {@link ColorConvert#fromHSVtoRGB(Image)}.
     */
    @Test
    public void testRGBtoHSVtoRGB() {
        final Image imgHSV = ColorConvert.fromRGBtoHSV(this.imageRGB);
        Assert.assertFalse(imgHSV.equals(this.imageRGB));

        final Image imgRGB = ColorConvert.fromHSVtoRGB(imgHSV);
        Assert.assertTrue(imgRGB.equals(this.imageRGB));
    }
}
