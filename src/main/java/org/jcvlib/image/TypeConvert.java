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
import java.awt.image.WritableRaster;

import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;

/**
 * Contains methods to convert images from one type to another.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class TypeConvert {

    /**
     * Return number of channels in the specified BufferedImage type.
     */
    private static int detectNumOfChannelsByType(final int bufferedImageType) {
        /*
         * 14 types of BufferedImage:
         * http://docs.oracle.com/javase/6/docs/api/java/awt/image/BufferedImage.html
         * http://java.sun.com/developer/technicalArticles/GUI/java2d/java2dpart2.html
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
        switch (bufferedImageType) {
            // 1 channel.
            case BufferedImage.TYPE_BYTE_BINARY:
            case BufferedImage.TYPE_BYTE_GRAY:
            case BufferedImage.TYPE_USHORT_GRAY:
                return 1;

            // 3 channels.
            case BufferedImage.TYPE_3BYTE_BGR:
            case BufferedImage.TYPE_INT_BGR:
            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_USHORT_555_RGB:
            case BufferedImage.TYPE_USHORT_565_RGB:
                return 3;

            // 4 channels.
            case BufferedImage.TYPE_BYTE_INDEXED:
            case BufferedImage.TYPE_CUSTOM:
            case BufferedImage.TYPE_4BYTE_ABGR:
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
            case BufferedImage.TYPE_INT_ARGB_PRE:
                return 4;

            default:
                throw new IllegalArgumentException(
                        "BufferedImage have unsupported type " + Integer.toString(bufferedImageType) + "!");
        }
    }

    /**
     * Get pixel values from {@link BufferedImage} and put it in the given {@link Image} in specified position.
     *
     * @param image
     *            Source image.
     * @param x
     *            x-position of pixel.
     * @param y
     *            y-position of pixel.
     * @param pixel
     *            Packed values of color from {@link BufferedImage}. To correct interpret this value needed
     *            {@link BufferedImage} parameter.
     * @param bufferedImageType
     *            Type of {@link BufferedImage}. This value needed for correct interpret 'pixel' parameter.
     */
    private static void setPixel(final Image image, final int x, final int y, final int pixel,
            final int bufferedImageType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Perform operation.
         */
        /*
         * 14 types of BufferedImage:
         * http://docs.oracle.com/javase/6/docs/api/java/awt/image/BufferedImage.html
         * http://java.sun.com/developer/technicalArticles/GUI/java2d/java2dpart2.html
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
        switch (bufferedImageType) {
            // 1 channel.
            case BufferedImage.TYPE_BYTE_BINARY:
                image.set(x, y, 0, pixel * 255);

                break;

            // 1 channel.
            case BufferedImage.TYPE_BYTE_GRAY:
                image.set(x, y, 0, pixel);

                break;

            // 1 channel.
            case BufferedImage.TYPE_USHORT_GRAY:
                image.set(x, y, 0, JCV.round(org.jcvlib.core.Color.MAX_VALUE * pixel / 65535.0));

                break;

            // 3 channels.
            case BufferedImage.TYPE_3BYTE_BGR:
            case BufferedImage.TYPE_INT_BGR:
            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_USHORT_555_RGB:
            case BufferedImage.TYPE_USHORT_565_RGB:
                /*
                 * See:
                 * * http://www.devdaily.com/blog/post/java/getting-rgb-values-for-each-pixel-in-image-using-java-bufferedi
                 */
                image.set(x, y, 0, pixel >> 16 & 0xff); // Red
                image.set(x, y, 1, pixel >> 8 & 0xff); // Green
                image.set(x, y, 2, pixel >> 0 & 0xff); // Blue

                break;

            // 4 channels.
            case BufferedImage.TYPE_BYTE_INDEXED:
            case BufferedImage.TYPE_CUSTOM:
            case BufferedImage.TYPE_4BYTE_ABGR:
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
            case BufferedImage.TYPE_INT_ARGB_PRE:
                /*
                 * See:
                 * * http://www.devdaily.com/blog/post/java/getting-rgb-values-for-each-pixel-in-image-using-java-bufferedi
                 */
                image.set(x, y, 0, pixel >> 16 & 0xff); // Red
                image.set(x, y, 1, pixel >> 8 & 0xff); // Green
                image.set(x, y, 2, pixel >> 0 & 0xff); // Blue

                image.set(x, y, 3, pixel >> 24 & 0xff); // Alpha

                break;

            default:
                throw new IllegalArgumentException(
                        "BufferedImage have unsupported type " + Integer.toString(bufferedImageType) + "!");
        }
    }

    /**
     * Return integer value with packed pixels values from given {@link Image}.
     */
    private static int getPixel(final Image image, final int x, final int y) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Perform operation.
         */
        /*
         * 14 types of BufferedImage:
         * http://docs.oracle.com/javase/6/docs/api/java/awt/image/BufferedImage.html
         * http://java.sun.com/developer/technicalArticles/GUI/java2d/java2dpart2.html
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
        switch (image.getNumOfChannels()) {
            case 1:
                return image.get(x, y, 0);

            case 3:
                return new java.awt.Color(image.get(x, y, 0), image.get(x, y, 1), image.get(x, y, 2)).getRGB();

            case 4:
                return new java.awt.Color(image.get(x, y, 0), image.get(x, y, 1), image.get(x, y, 2),
                        image.get(x, y, 3)).getRGB();

            default:
                throw new IllegalArgumentException("To convert 'Image' to 'BufferedImage', source image should have "
                        + "1 (for Grayscale images), " + "3 (for RGB images) or " + "4 (for RGBA images) channels, "
                        + "but parameter 'image' have " + Integer.toString(image.getNumOfChannels()) + " channels!");
        }
    }

    /**
     * Convert from {@link BufferedImage} to {@link Image}.
     *
     * @param bufImg
     *            Source {@link BufferedImage}.
     * @return {@link Image} with 1 (for <i>Grayscale</i>), 3 (for <i>RGB</i>) or 4 (for <i>RGB</i> and <i>Alpha</i>)
     *         channels.
     */
    public static Image fromBufferedImage(final BufferedImage bufImg) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(bufImg);

        /*
         * Perform operation.
         */
        final Image result = new Image(bufImg.getWidth(), bufImg.getHeight(),
                TypeConvert.detectNumOfChannelsByType(bufImg.getType()));

        if (result.getNumOfChannels() == 1) {
            for (int x = 0; x < result.getWidth(); ++x) {
                for (int y = 0; y < result.getHeight(); ++y) {

                    TypeConvert.setPixel(result, x, y, bufImg.getRaster().getSample(x, y, 0), bufImg.getType());
                }
            }
        } else {
            for (int x = 0; x < result.getWidth(); ++x) {
                for (int y = 0; y < result.getHeight(); ++y) {
                    TypeConvert.setPixel(result, x, y, bufImg.getRGB(x, y), bufImg.getType());
                }
            }
        }

        return result;
    }

    /**
     * Convert from {@link Image} to {@link BufferedImage}.
     *
     * @param image
     *            Source {@link Image}.
     * @return This method can return {@link BufferedImage} image with 1 (for <i>Grayscale</i>), 3 (for <i>RGB</i>) or 4
     *         (for <i>RGB</i> and <i>Alpha</i>) channels.
     */
    public static BufferedImage toBufferedImage(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Perform operation.
         */
        /*
         * 14 types of BufferedImage:
         * http://docs.oracle.com/javase/6/docs/api/java/awt/image/BufferedImage.html
         * http://java.sun.com/developer/technicalArticles/GUI/java2d/java2dpart2.html
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
        BufferedImage bufImg = null;
        switch (image.getNumOfChannels()) {
            case 1:
                bufImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                final WritableRaster raster = bufImg.getRaster();

                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int x = 0; x < image.getWidth(); ++x) {
                        raster.setSample(x, y, 0, TypeConvert.getPixel(image, x, y));
                    }
                }

                return bufImg;

            case 3:
                bufImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int x = 0; x < image.getWidth(); ++x) {
                        bufImg.setRGB(x, y, TypeConvert.getPixel(image, x, y));
                    }
                }

                return bufImg;

            case 4:
                bufImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int x = 0; x < image.getWidth(); ++x) {
                        bufImg.setRGB(x, y, TypeConvert.getPixel(image, x, y));
                    }
                }

                return bufImg;

            default:
                throw new IllegalArgumentException("To convert 'Image' to 'BufferedImage', source image should have "
                        + "1 (for Grayscale images), " + "3 (for RGB images) or " + "4 (for RGBA images) channels, "
                        + "but parameter 'image' have " + Integer.toString(image.getNumOfChannels()) + " channels!");
        }
    }

    /**
     * Convert given {@link Image} to {@link Image} with new type.
     */
    public static Image toType(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Perform operation.
         */
        final Image result = new Image(image.getWidth(), image.getHeight(), image.getNumOfChannels());

        image.copyTo(result);

        return result;
    }
}
