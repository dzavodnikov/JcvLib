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
package org.jcvlib.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.jcvlib.image.TypeConvert;

import com.sixlegs.png.PngImage;

/**
 * Class for Input/Output images.
 *
 * <p>
 * <strong>Remember, that to reading/writing images you should specified file extension to define file format for
 * saving!</strong>
 * </p>
 *
 * <p>
 * Example of usage:
 * 
 * <code><pre>
 * ...
 * // Read image.
 * Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");
 * ...
 * // Write image. File format detected by file extension.
 * ImageRW.write(image, "resources" + File.separatorChar + "Lenna.bmp");
 * ...
 * </pre></code>
 * 
 * or
 * 
 * <code><pre>
 * ...
 * // Read image.
 * Image image = ImageRW.read(new File("resources" + File.separatorChar + "Lenna.bmp"));
 * ...
 * // Write image.
 * ImageRW.write(image, new File("resources" + File.separatorChar + "Lenna.bmp"), "BMP");
 * ...
 * </pre></code>
 * </p>
 *
 * <p>
 * Supported file formats:
 * <ul>
 * <li>JPEG</li>
 * <li>PNG (Portable Network Graphics)</li>
 * <li>BMP (BitMaP)</li>
 * </ul>
 * </p>
 *
 * <p>
 * <h6>Links:</h6>
 * <ol>
 * <li><a href="http://en.wikipedia.org/wiki/JPEG">JPEG -- Wikiedia</a>.</li>
 * <li><a href="http://en.wikipedia.org/wiki/Portable_Network_Graphics">PNG (Portable Network Graphics) -- Wikiedia</a>.
 * </li>
 * <li><a href="http://en.wikipedia.org/wiki/BMP_file_format">BMP (BitMaP) -- Wikiedia</a>.</li>
 * <li><a href="http://people.sc.fsu.edu/~jburkardt/data/data.html">Examples of files in various formats</a>.</li>
 * </ol>
 * </p>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ImageRW {

    /**
     * Write image from disk.
     *
     * <p>
     * Supported file formats:
     * <ul>
     * <li>JPEG</li>
     * <li>PNG (Portable Network Graphics)</li>
     * <li>BMP (BitMaP)</li>
     * </ul>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/JPEG">JPEG -- Wikiedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/Portable_Network_Graphics">PNG (Portable Network Graphics) --
     * Wikiedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/BMP_file_format">BMP (BitMaP) -- Wikiedia</a>.</li>
     * <li><a href="http://people.sc.fsu.edu/~jburkardt/data/data.html">Examples of files in various formats</a>.</li>
     * </ol>
     * </p>
     */
    public static Image read(final File imageFile) throws IOException {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(imageFile);

        /*
         * Perform operation.
         */
        final String fileFormat = imageFile.getName().substring(imageFile.getName().lastIndexOf('.') + 1);

        BufferedImage bufImg = null;
        if (fileFormat.equalsIgnoreCase("png")) {
            /*
             * Using Sixlegs Java PNG Decoder (http://code.google.com/p/javapng/) for reading PNG files,
             * because standard implementation of PNG decoder generate "OutOfMemoryError: Java heap space"
             * on reading big PNG files.
             */
            bufImg = (new PngImage()).read(imageFile);
        } else {
            /*
             * Using standard Java library for other file image formats.
             */
            bufImg = ImageIO.read(imageFile);
        }

        return TypeConvert.fromBufferedImage(bufImg);
    }

    /**
     * Write image from disk.
     *
     * <p>
     * Supported file formats:
     * <ul>
     * <li>JPEG</li>
     * <li>PNG (Portable Network Graphics)</li>
     * <li>BMP (BitMaP)</li>
     * </ul>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/JPEG">JPEG -- Wikiedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/Portable_Network_Graphics">PNG (Portable Network Graphics) --
     * Wikiedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/BMP_file_format">BMP (BitMaP) -- Wikiedia</a>.</li>
     * <li><a href="http://people.sc.fsu.edu/~jburkardt/data/data.html">Examples of files in various formats</a>.</li>
     * </ol>
     * </p>
     */
    public static Image read(final String fileName) throws IOException {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(fileName);

        /*
         * Read image.
         */
        return ImageRW.read(new File(fileName));
    }

    /**
     * Write current image into file. Type of image detected by file extension.
     *
     * <p>
     * Supported file formats:
     * <ul>
     * <li>JPEG</li>
     * <li>PNG (Portable Network Graphics)</li>
     * <li>BMP (BitMaP)</li>
     * </ul>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/JPEG">JPEG -- Wikiedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/Portable_Network_Graphics">PNG (Portable Network Graphics) --
     * Wikiedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/BMP_file_format">BMP (BitMaP) -- Wikiedia</a>.</li>
     * <li><a href="http://people.sc.fsu.edu/~jburkardt/data/data.html">Examples of files in various formats</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Saving image.
     * @param fileImage
     *            {@link File} variable for saving.
     * @param fileFormat
     *            File extension to define saving image format.
     */
    public static void write(final Image image, final File fileImage, final String fileFormat) throws IOException {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(fileImage);
        JCV.verifyIsNotNull(fileFormat);

        /*
         * Write image.
         */
        ImageIO.write(TypeConvert.toBufferedImage(image), fileFormat, fileImage);
    }

    /**
     * Write current image into file. Type of image detected by file extension.
     *
     * <p>
     * Supported file formats:
     * <ul>
     * <li>JPEG</li>
     * <li>PNG (Portable Network Graphics)</li>
     * <li>BMP (BitMaP)</li>
     * </ul>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/JPEG">JPEG -- Wikiedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/Portable_Network_Graphics">PNG (Portable Network Graphics) --
     * Wikiedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/BMP_file_format">BMP (BitMaP) -- Wikiedia</a>.</li>
     * <li><a href="http://people.sc.fsu.edu/~jburkardt/data/data.html">Examples of files in various formats</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Saving image.
     * @param fileName
     *            Name of file for saving. Must contain file extension.
     */
    public static void write(final Image image, final String fileName) throws IOException {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(fileName);

        /*
         * Write image.
         */
        final String fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        ImageRW.write(image, new File(fileName), fileFormat);
    }
}
