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

import java.awt.image.BufferedImage;

import org.jcvlib.core.Image;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link ColorConvert} to from/to {@link BufferedImage}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class TypeConvertTest {

    /*
     * 14 types of BufferedImage:
     *  * http://docs.oracle.com/javase/6/docs/api/java/awt/image/BufferedImage.html
     *  * http://java.sun.com/developer/technicalArticles/GUI/java2d/java2dpart2.html
     *
     * With 1 channel:
     * ~~~~~~~~~~~~~~~
     * + TYPE_BYTE_BINARY 1 bit per pixel, 8 pixels to a byte.
     * + TYPE_BYTE_GRAY 8-bit gray value for each pixel.
     * + TYPE_USHORT_GRAY 16-bit gray values for each pixel.
     *
     * With 3 channels:
     * ~~~~~~~~~~~~~~~~
     * + TYPE_3BYTE_BGR Blue, green, and red values stored, 1 byte each.
     * + TYPE_INT_BGR 8-bit blue, green, and red pixel values stored in a 32-bit integer.
     * + TYPE_INT_RGB 8-bit red, green, and blue values stored in a 32-bit integer.
     * + TYPE_USHORT_555_RGB 5-bit red, green, and blue values packed into 16 bits.
     * + TYPE_USHORT_565_RGB 5-bit red and blue values, 6-bit green values packed into 16 bits.
     *
     * With 4 channels:
     * ~~~~~~~~~~~~~~~~
     * + TYPE_BYTE_INDEXED 8-bit pixel value that references a color index table.
     * + TYPE_CUSTOM Image type is not recognized so it must be a customized image.
     * + TYPE_4BYTE_ABGR Alpha, blue, green, and red values stored, 1 byte each.
     * + TYPE_4BYTE_ABGR_PRE Alpha and premultiplied blue, green, and red values stored, 1 byte each.
     * + TYPE_INT_ARGB 8-bit alpha, red, green, and blue values stored in a 32-bit integer.
     * + TYPE_INT_ARGB_PRE 8-bit alpha and premultiplied red, green, and blue values stored in a 32-bit integer.
     */
    private final int width  = 2500;

    private final int height = 2500;

    /**
     * Test method for: {@link TypeConvert#fromBufferedImage(BufferedImage)}, {@link TypeConvert#toBufferedImage(Image)}
     * .
     */
    @Test
    public void testBinary() {
        // Create.
        final BufferedImage bufImg = new BufferedImage(this.width, this.height, BufferedImage.TYPE_BYTE_BINARY);

        // Initialize.
        int value = 0;
        for (int y = 0; y < bufImg.getHeight(); ++y) {
            for (int x = 0; x < bufImg.getWidth(); ++x) {
                bufImg.getRaster().setSample(x, y, 0, value);

                if (value == 0) {
                    value = 1;
                } else {
                    value = 0;
                }
            }
        }

        // Check values.
        try {
            final Image image = TypeConvert.fromBufferedImage(bufImg);
            Assert.assertEquals(1, image.getNumOfChannels());

            for (int y = 0; y < image.getHeight(); ++y) {
                for (int x = 0; x < image.getWidth(); ++x) {
                    Assert.assertEquals(image.get(x, y, 0), bufImg.getRaster().getSample(x, y, 0) * 255);
                }
            }
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test method for: {@link TypeConvert#fromBufferedImage(BufferedImage)}, {@link TypeConvert#toBufferedImage(Image)}
     * .
     */
    @Test
    public void testGray() {
        testGrayscaleImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_BYTE_GRAY), 255);
        testGrayscaleImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_USHORT_GRAY), 65535);
    }

    /**
     * Test method for: {@link TypeConvert#fromBufferedImage(BufferedImage)}, {@link TypeConvert#toBufferedImage(Image)}
     * .
     */
    @Test
    public void testColorChannels3() {
        testMultichannelImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_3BYTE_BGR), false);
        testMultichannelImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_BGR), false);
        testMultichannelImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB), false);
        testMultichannelImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_USHORT_555_RGB), false);
        testMultichannelImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_USHORT_565_RGB), false);
    }

    /**
     * Test method for: {@link TypeConvert#fromBufferedImage(BufferedImage)}, {@link TypeConvert#toBufferedImage(Image)}
     * .
     */
    @Test
    public void testColorChannels4() {
        testMultichannelImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_BYTE_INDEXED), true);
        testMultichannelImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_4BYTE_ABGR), true);
        testMultichannelImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_4BYTE_ABGR_PRE), true);
        testMultichannelImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB), true);
        testMultichannelImage(new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB_PRE), true);
    }

    /**
     * This method initialize {@link BufferedImage} with 1 channel, convert it to {@link Image} and compare values of
     * this images.
     *
     * @param bufImg
     *            {@link BufferedImage} with predefined type.
     * @param maxValue
     *            Max value to using {@link BufferedImage} with different range for color.
     */
    private void testGrayscaleImage(final BufferedImage bufImg, final int maxValue) {
        // Initialize.
        int color = 0;
        for (int y = 0; y < bufImg.getHeight(); ++y) {
            for (int x = 0; x < bufImg.getWidth(); ++x) {
                bufImg.getRaster().setSample(x, y, 0, color);

                ++color;
                if (color > maxValue) {
                    color = 0;
                }
            }
        }

        // Check values.
        try {
            final Image image = TypeConvert.fromBufferedImage(bufImg);
            Assert.assertEquals(1, image.getNumOfChannels());

            for (int y = 0; y < image.getHeight(); ++y) {
                for (int x = 0; x < image.getWidth(); ++x) {
                    Assert.assertEquals(image.get(x, y, 0) / org.jcvlib.core.Color.MAX_VALUE,
                            (double) bufImg.getRaster().getSample(x, y, 0) / (double) maxValue,
                            org.jcvlib.core.Color.MAX_VALUE);
                }
            }
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Initialize given {@link BufferedImage} by different values.
     *
     * @param bufImg
     *            Source {@link BufferedImage} for initializing.
     * @param hasAlpha
     *            Is given image should support alpha-channel.
     */
    private void initializeMultichannelImage(final BufferedImage bufImg, final boolean hasAlpha) {
        int R = 0;
        int G = 0;
        int B = 0;
        int A = 0;
        for (int y = 0; y < bufImg.getHeight(); ++y) {
            for (int x = 0; x < bufImg.getWidth(); ++x) {
                java.awt.Color rgb;
                if (hasAlpha) {
                    rgb = new java.awt.Color(R, G, B, A);
                } else {
                    rgb = new java.awt.Color(R, G, B);
                }
                bufImg.setRGB(x, y, rgb.getRGB());

                // Next color.
                ++R;
                if (R > 255) {
                    R = 0;
                    ++G;
                    if (G > 255) {
                        G = 0;
                        ++B;
                        if (B > 255) {
                            B = 0;
                        }
                    }
                }
                // Alpha channel is independent from colors.
                ++A;
                if (A > 255) {
                    A = 0;
                }
            }
        }
    }

    /**
     * This method compare {@link BufferedImage} and {@link JcvImage64F}.
     */
    private void compareMultichannelImage(final BufferedImage bufImg, final boolean hasAlpha, final Image image) {
        try {
            if (hasAlpha) {
                Assert.assertEquals(4, image.getNumOfChannels());
            } else {
                Assert.assertEquals(3, image.getNumOfChannels());
            }

            // Check values.
            for (int y = 0; y < bufImg.getHeight(); ++y) {
                for (int x = 0; x < bufImg.getWidth(); ++x) {
                    /*
                     * http://docs.oracle.com/javase/6/docs/api/java/awt/Color.html
                     */
                    final java.awt.Color rgb = new java.awt.Color(bufImg.getRGB(x, y), hasAlpha);

                    // Verify convert.
                    Assert.assertEquals(image.get(x, y, 0), rgb.getRed());
                    Assert.assertEquals(image.get(x, y, 1), rgb.getGreen());
                    Assert.assertEquals(image.get(x, y, 2), rgb.getBlue());
                    if (hasAlpha) {
                        Assert.assertEquals(image.get(x, y, 3), rgb.getAlpha());
                    }
                }
            }
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * This method initialize {@link BufferedImage} with 3 or 4 channels, convert it to {@link JcvImage64F} and compare
     * values of this images (testing {@link ColorConvert#fromBufferedImage(BufferedImage)}). Then this method convert
     * {@link JcvImage64F} back to {@link BufferedImage} and compare this {@link BufferedImage} with {@link JcvImage64F}
     * (testing {@link ColorConvert#toBufferedImage(JcvImage64F)}).
     *
     * @param bufImg
     *            {@link BufferedImage} with predefined type.
     * @param hasAlpha
     *            Define using 3 or 4 channels.
     */
    private void testMultichannelImage(final BufferedImage bufImg1, final boolean hasAlpha) {
        initializeMultichannelImage(bufImg1, hasAlpha);

        // Check values.
        try {
            // Convert. We testing this method.
            final Image image = TypeConvert.fromBufferedImage(bufImg1);
            final BufferedImage bufImg2 = TypeConvert.toBufferedImage(image);

            // Compare results.
            compareMultichannelImage(bufImg1, hasAlpha, image);
            compareMultichannelImage(bufImg2, hasAlpha, image);
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
