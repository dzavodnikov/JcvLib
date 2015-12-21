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
package org.jcvlib.core;

import java.text.MessageFormat;

import Jama.Matrix;

/**
 * Contains common useful constants and methods.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class JCV {

    public static final String LIB_NAME         = "JcvLib";

    public static final int    LIB_VER_MAJOR    = 5;

    public static final int    LIB_VER_MINOR    = 1;

    public static final int    LIB_VER_PATCH    = 1;

    public static final String LIB_VER          = Integer.toString(LIB_VER_MAJOR) + "."
            + Integer.toString(LIB_VER_MINOR) + "." + Integer.toString(LIB_VER_PATCH);

    /**
     * <code>10<sup>-13</sup></code>.
     */
    public static final double PRECISION        = 0.000_000_000_000_1;

    /**
     * Constant to define OS Linux.
     */
    public static final String OS_LINUX         = "Linux";

    /**
     * Constant to define OS Windows.
     */
    public static final String OS_WINDOWS       = "Windows";

    /**
     * Constant to define unsupported OS.
     */
    public static final String OS_UNSUPPORTED   = "UnsupportedOS";

    /**
     * Constant to define 32-bit architecture.
     */
    public static final String ARCH_32          = "32";

    /**
     * Constant to define 64-bit architecture.
     */
    public static final String ARCH_64          = "64";

    /**
     * Constant to define unsupported architecture.
     */
    public static final String ARCH_UNSUPPORTED = "UnsupportedArch";

    /**
     * Get constant that identify current operation system.
     *
     * @return Current operation system identifier. See <code>JCV.OS_*</code>.
     */
    public static String getOS() {
        /*
         * See: * http://lopica.sourceforge.net/os.html
         */
        final String os = System.getProperty("os.name");

        if (os.indexOf("Linux") >= 0) {
            return JCV.OS_LINUX;
        } else if (os.indexOf("Windows") >= 0) {
            return JCV.OS_WINDOWS;
        } else {
            return JCV.OS_UNSUPPORTED;
        }
    }

    /**
     * Get constant that identify current <strong>JVM</strong> architecture.
     *
     * <p>
     * It means, that on 64-bit architecture can be installed 32-bit JVM. In this case 32-bit constant will be returned.
     * It will be correct because we can run only 32-bit native code on 32-bit JCV.
     * </p>
     *
     * @return Current architecture identifier. See <code>JCV.ARCH_*</code>.
     */
    public static String getArch() {
        /*
         * See: * http://lopica.sourceforge.net/os.html
         */
        final String arch = System.getProperty("os.arch");

        if (arch.indexOf("64") >= 0) {
            return JCV.ARCH_64;
        } else if (arch.indexOf("86") >= 0) {
            return JCV.ARCH_32;
        } else {
            return JCV.ARCH_UNSUPPORTED;
        }
    }

    /**
     * Check that current operation system is a Linux and running on supported architecture.
     *
     * @return If current operation system is <b>Linux</b> return <code>true</code> and <code>false</code> otherwise.
     */
    public static boolean isLinux() {
        return !JCV.getArch().equals(JCV.ARCH_UNSUPPORTED) && JCV.getOS().equals(JCV.OS_LINUX);
    }

    /**
     * Check that current operation system is a Windows and running on supported architecture.
     *
     * @return If current operation system is <b>Windows</b> return <code>true</code> and <code>false</code> otherwise.
     */
    public static boolean isWindows() {
        return !JCV.getArch().equals(JCV.ARCH_UNSUPPORTED) && JCV.getOS().equals(JCV.OS_WINDOWS);
    }

    /**
     * Correct compare 2 float-point numbers (with default {@link JCV#PRECISION} precision).
     *
     * <code><pre>
     * if num1 == num2
     *      return true
     * otherwise
     *      return false
     * </pre></code>
     * 
     * Useful for compare values of 2 float-point colors.
     */
    public static boolean equalValues(final double num1, final double num2) {
        return JCV.equalValues(num1, num2, JCV.PRECISION);
    }

    /**
     * Correct compare 2 float-point numbers with defined precision.
     *
     * <code><pre>
     * if num1 == num2
     *      return True
     * otherwise
     *      return False
     * </pre></code>
     * 
     * Useful for compare values of 2 float-point colors.
     */
    public static boolean equalValues(final double num1, final double num2, final double precision) {
        if (Math.abs(num1 - num2) <= precision) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Round value to nearest integer.
     *
     * <p>
     * For example:
     * <ul>
     * <li><code>1.0 --> 1</code></li>
     * <li><code>1.1 --> 1</code></li>
     * <li><code>1.5 --> 2</code></li>
     * <li><code>1.9 --> 2</code></li>
     * <li><code>2.0 --> 2</code></li>
     * </ul>
     * </p>
     */
    public static int round(final double value) {
        if (value == Double.POSITIVE_INFINITY) {
            return Integer.MAX_VALUE;
        }
        if (value == Double.NEGATIVE_INFINITY) {
            return Integer.MIN_VALUE;
        }
        return (int) Math.round(value);
    }

    /**
     * Round value to greater integer.
     *
     * <p>
     * For example:
     * <ul>
     * <li><code>1.0 --> 1</code></li>
     * <li><code>1.1 --> 2</code></li>
     * <li><code>1.5 --> 2</code></li>
     * <li><code>1.9 --> 2</code></li>
     * <li><code>2.0 --> 2</code></li>
     * </ul>
     * </p>
     */
    public static int roundUp(final double value) {
        return (int) Math.ceil(value);
    }

    /**
     * Round value to less integer.
     *
     * <p>
     * For example:
     * <ul>
     * <li><code>1.0 --> 1</code></li>
     * <li><code>1.1 --> 1</code></li>
     * <li><code>1.5 --> 1</code></li>
     * <li><code>1.9 --> 1</code></li>
     * <li><code>2.0 --> 2</code></li>
     * </ul>
     * </p>
     */
    public static int roundDown(final double value) {
        return (int) Math.floor(value);
    }

    /**
     * Return center of element with current image.
     *
     * <p>
     * For example if we have size <code>[3 x 2]</code> this method return point <code>(1, 0)</code> (<strong>not
     * <code>(1, 1)</code>!</strong>). This method round down to odd size and then found a center!
     * </p>
     */
    public static Point calculateCenter(final int width, final int height) {
        int x = width / 2;
        if (width % 2 == 0) {
            --x;
        }

        int y = height / 2;
        if (height % 2 == 0) {
            --y;
        }

        return new Point(x, y);
    }

    /**
     * Verify if given object is not <code>null</code> and generate {@link IllegalArgumentException} otherwise.
     *
     * @param obj
     *            Object to verification.
     */
    public static void verifyIsNotNull(final Object obj) {
        /*
         * Apply checking.
         */
        if (obj == null) {
            throw new IllegalArgumentException("Given object is null!");
        }
    }

    /**
     * Verify if 2 images have same size and generate {@link IllegalArgumentException} otherwise.
     *
     * @param image1
     *            First image.
     * @param image2
     *            Second image.
     */
    public static void verifyIsSameSize(final Image image1, final Image image2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image1);
        JCV.verifyIsNotNull(image2);

        /*
         * Apply checking.
         */
        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
            throw new IllegalArgumentException(
                    MessageFormat.format("First {0} size (= {1}) must be the same as second {0} size (= {2})!",
                            Image.class.getName(), JCV.getSizeString(image1.getWidth(), image1.getHeight()),
                            JCV.getSizeString(image2.getWidth(), image2.getHeight())));
        }
    }

    /**
     * Verify if 2 images have same number of channels and generate {@link IllegalArgumentException} otherwise.
     *
     * @param image1
     *            First image.
     * @param image2
     *            Second image.
     */
    public static void verifyIsSameChannels(final Image image1, final Image image2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image1);
        JCV.verifyIsNotNull(image2);

        /*
         * Apply checking.
         */
        if (image1.getNumOfChannels() != image2.getNumOfChannels()) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "First {0} number of channels (= {1}) must be same as second {0} number of channels (= {2})!",
                    Image.class.getName(), image1.getNumOfChannels(), image2.getNumOfChannels()));
        }
    }

    /**
     * Verify if image have defined number of channels and generate {@link IllegalArgumentException} otherwise.
     *
     * @param image
     *            Source image.
     * @param numOfChannels
     *            Required number of channels.
     */
    public static void verifyNumOfChannels(final Image image, final int numOfChannels) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Apply checking.
         */
        if (numOfChannels <= 0) {
            throw new IllegalArgumentException(MessageFormat.format("{0} must be more than 0!", int.class.getName()));
        }
        if (image.getNumOfChannels() != numOfChannels) {
            throw new IllegalArgumentException(MessageFormat.format("{0} have {1} channels, but should have {2}!",
                    Image.class.getName(), image.getNumOfChannels(), numOfChannels));
        }
    }

    /**
     * Verify if 2 images have same size and generate {@link IllegalArgumentException} otherwise.
     *
     * @param mat1
     *            First matrix.
     * @param mat2
     *            Second matrix.
     */
    public static void verifyIsSameSize(final Matrix mat1, final Matrix mat2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(mat1);
        JCV.verifyIsNotNull(mat2);

        /*
         * Apply checking.
         */
        if (mat1.getColumnDimension() != mat2.getColumnDimension()) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "First {0} column number (= {1}) must be the same as second {0} column number (= {2})!",
                    mat1.getColumnDimension(), mat2.getColumnDimension()));
        }
        if (mat1.getRowDimension() != mat2.getRowDimension()) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "First {0} row number (= {1}) must be the same as second {0} row number (= {2})!",
                    mat1.getRowDimension(), mat2.getRowDimension()));
        }
    }

    /**
     * Verify that given size is odd.
     */
    public static void verifyOddSize(final int size) {
        /*
         * Apply checking.
         */
        if (size <= 0) {
            throw new IllegalArgumentException(
                    MessageFormat.format("{0} value (= {1}) must be more than 0!", int.class.getName(), size));
        }
        if (size % 2 == 0) {
            throw new IllegalArgumentException(
                    MessageFormat.format("{0} value (= {1}) must be odd (1, 3, 5, ...)!", int.class.getName(), size));
        }
    }

    /**
     * Create string representation of {@link Matrix} defined by given parameters.
     */
    public static String getMatrixString(final Matrix M) {
        final StringBuilder sb = new StringBuilder();

        for (int row = 0; row < M.getRowDimension(); ++row) {
            sb.append("|");
            for (int col = 0; col < M.getColumnDimension(); ++col) {
                sb.append(M.get(row, col));
                sb.append(" ");
            }
            sb.append("|\n");
        }

        return sb.toString();
    }

    /**
     * Create string representation of size defined by given parameters.
     */
    public static String getPointString(final int x, final int y) {
        final StringBuilder sb = new StringBuilder();

        sb.append("(");
        sb.append(x);
        sb.append(", ");
        sb.append(y);
        sb.append(")");

        return sb.toString();
    }

    /**
     * Create string representation of size defined by given parameters.
     */
    public static String getSizeString(final int width, final int height) {
        final StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(width);
        sb.append(" x ");
        sb.append(height);
        sb.append("]");

        return sb.toString();
    }

    /**
     * Create string representation of rectangle defined by given parameters.
     */
    public static String getRectangleString(final int x, final int y, final int width, final int height) {
        final StringBuilder sb = new StringBuilder();

        sb.append("{");
        sb.append(getPointString(x, y));
        sb.append(", ");
        sb.append(getSizeString(width, height));
        sb.append("}");

        return sb.toString();
    }
}
