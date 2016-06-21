/*
 * Copyright (c) 2012-2016 JcvLib Team
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
package org.jcvlib.image.filters;

import org.jcvlib.core.Color;
import org.jcvlib.core.Extrapolation;
import org.jcvlib.core.Histogram;
import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.jcvlib.core.KernelOperation;
import org.jcvlib.core.ParallelValueOperation;
import org.jcvlib.core.Point;
import org.jcvlib.core.Size;

import Jama.Matrix;

/**
 * Contains base filters.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Filters {

    /**
     * Coefficient that uses for select kernel size from sigma and back (= 6.0).
     */
    private static final double sigmaSizeCoeff = 6.0;

    /**
     * Convolves an image with the kernel. Common-used method for apply linear matrix filter.
     *
     * <p>
     * For example, we have matrix kernel:
     *
     * <code><pre>
     *     0 1 2
     *   +-------+
     * 0 | 1 1 1 |
     * 1 | 1 1 1 |
     * 2 | 1 1 1 |
     *   +-------+
     * </pre></code>
     *
     * with <code>div = 9</code> and <code>offset = 0</code>.
     * </p>
     *
     * <p>
     * For each pixels we select aperture (using extrapolation on the image borders):
     *
     * <code><pre>
     *     0 1 2
     *   +-------+
     * 0 | o o o |
     * 1 | o x o |
     * 2 | o o o |
     *   +-------+
     * </pre></code>
     *
     * and multiply each value from aperture to corresponding value from kernel. For example value in position
     * <code>(0, 1)</code> from aperture will be multiply to value <code>(0, 1)</code> from kernel.
     * </p>
     *
     * <p>
     * Then all multiplied values are summarized, divided to <code>div</code> value and to the result added
     * <code>offset</code> value.
     * </p>
     *
     * <p>
     * Result of this operations saved into output image into corresponding position.
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Linear_filter">Linear filter -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param kernel
     *            Kernel to perform convolution. <strong>Should have odd size both all dimensions (1, 3, 5,
     *            ...)!</strong>
     * @param div
     *            Coefficient to division.
     * @param offset
     *            Value to offset the result.
     * @param extrapolation
     *            Extrapolation method.
     * @return Image with result of applying linear filter. Have same size, number of channels and type as a source
     *         image.
     */
    public static Image linearFilter(final Image image, final Matrix kernel, final double div, final double offset,
            final Extrapolation extrapolation) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(kernel);
        JCV.verifyOddSize(kernel.getColumnDimension());
        JCV.verifyOddSize(kernel.getRowDimension());

        /*
         * Perform transformation.
         */
        final Image result = image.makeSame();

        image.noneLinearFilter(result, kernel.getColumnDimension(), kernel.getRowDimension(),
                JCV.calculateCenter(kernel.getColumnDimension(), kernel.getRowDimension()), 1, extrapolation,
                new KernelOperation() {

                    @Override
                    public void execute(final Image aperture, final Color result) {
                        final double[] sum = aperture.convolve(kernel);
                        for (int channel = 0; channel < result.getNumOfChannels(); ++channel) {
                            result.set(channel, JCV.round(sum[channel] / div + offset));
                        }
                    }
                });

        return result;
    }

    /**
     * Implement separable filter (2 consistent linear transformations with first and second filter).
     *
     * @param image
     *            Source image.
     * @param kernelFirst
     *            Kernel to perform convolution at <strong>first step</strong>. <strong>Should have odd size for all
     *            dimensions (1, 3, 5, ...)!</strong>
     * @param kernelSecond
     *            Kernel to perform convolution at <strong>second step</strong>. <strong>Should have odd size for all
     *            dimensions (1, 3, 5, ...)!</strong>
     * @param div
     *            Coefficient to division.
     * @param offset
     *            Value to offset the result.
     * @param extrapolation
     *            Extrapolation method.
     * @return Image with result of applying separable filter. Have same size, number of channels and type as a source
     *         image.
     */
    public static Image separableFilter(final Image image, final Matrix kernelFirst, final Matrix kernelSecond,
            final double div, final double offset, final Extrapolation extrapolation) {
        // First iteration.
        final Image result = linearFilter(image, kernelFirst, div, offset, extrapolation);

        // Second iteration.
        return linearFilter(result, kernelSecond, div, offset, extrapolation);
    }

    /**
     * Threshold filter.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Thresholding_(image_processing)">Thresholding (image processing) --
     * Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param threshold
     *            Threshold value.
     * @param thresholdMethod
     *            Threshold method.
     * @param maxVal
     *            If current color value more than threshold, set this value.
     * @return Image with result of applying threshold filter. Have same size, number of channels and type as a source
     *         image.
     */
    public static Image threshold(final Image image, final int threshold, final Threshold thresholdMethod,
            final int maxVal) {
        /*
         * Verify parameters.
         */
        if (threshold < Color.MIN_VALUE || threshold > Color.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Parameter 'threshold' (=" + Double.toString(threshold) + ") must be in interval ["
                            + Double.toString(Color.MIN_VALUE) + ", " + Double.toString(Color.MAX_VALUE) + "]!");
        }

        if (maxVal < Color.MIN_VALUE || maxVal > Color.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Parameter 'max' (=" + Double.toString(maxVal) + ") must be in interval ["
                            + Double.toString(Color.MIN_VALUE) + ", " + Double.toString(Color.MAX_VALUE) + "]!");
        }

        /*
         * Perform transformation.
         */
        final Image result = image.makeSame();

        result.foreach(new ParallelValueOperation() {

            @Override
            public int execute(int value) {
                return thresholdMethod.run(value, threshold, maxVal);
            }
        });

        return result;
    }

    /**
     * Same as {@link #threshold(Image, int, Threshold, int)}, but uses {@link Color#MAX_VALUE} as default maximal
     * value.
     */
    public static Image threshold(final Image image, final int threshold, final Threshold thresholdMethod) {
        return threshold(image, threshold, thresholdMethod, Color.MAX_VALUE);
    }

    /**
     * Adaptive threshold.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://homepages.inf.ed.ac.uk/rbf/HIPR2/adpthrsh.htm">Adaptive Thresholding -- HIPR2</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param blockSize
     *            Size of block for detection threshold value for current pixel.
     * @param thresholdMethod
     *            Adaptive threshold method.
     * @param maxVal
     *            If current color value more than threshold, set this value.
     * @param C
     *            Constant subtracted from the mean or weighted mean. Should be in interval <code>0..255</code>.
     * @return Image with result of applying adaptive threshold filter. Have same size, number of channels and type as a
     *         source image.
     */
    public static Image adapriveThreshold(final Image image, final int blockSize,
            final ThresholdAdaptive thresholdMethod, final int C, final int maxVal) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        if (C < Color.MIN_VALUE || C > Color.MAX_VALUE) {
            throw new IllegalArgumentException(String.format("Parameter 'C' (=%s) should be in interval 0..255!", C));
        }

        /*
         * Perform transformation.
         */
        final Image result = image.makeSame();

        final Point apertureCenter = JCV.calculateCenter(blockSize, blockSize);
        image.noneLinearFilter(result, blockSize, blockSize, apertureCenter, 1, Extrapolation.REPLICATE,
                new KernelOperation() {

                    @Override
                    public void execute(final Image aperture, final Color result) {
                        for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                            /*
                             * Find threshold value.
                             */
                            int sum = 0;
                            for (int x = 0; x < aperture.getWidth(); ++x) {
                                for (int y = 0; y < aperture.getHeight(); ++y) {
                                    sum += aperture.get(x, y, channel)
                                            * thresholdMethod.getMatrixCoeff(blockSize).get(y, x);
                                }
                            }

                            /*
                             * Apply threshold.
                             */
                            int threshold = sum - C;
                            if (threshold < Color.MIN_VALUE) {
                                threshold = Color.MIN_VALUE;
                            }
                            if (threshold > Color.MAX_VALUE) {
                                threshold = Color.MAX_VALUE;
                            }

                            final int val = aperture.get(apertureCenter.getX(), apertureCenter.getY(), channel);

                            result.set(channel, thresholdMethod.getThresholdMethod().run(val, threshold, maxVal));
                        }
                    }
                });

        return result;
    }

    /**
     * Same as {@link #adapriveThreshold(Image, int, ThresholdAdaptive, int, int)}, but uses {@link Color#MAX_VALUE} as
     * default maximal value.
     */
    public static Image adapriveThreshold(final Image image, final int blockSize,
            final ThresholdAdaptive thresholdMethod, final int C) {
        return adapriveThreshold(image, blockSize, thresholdMethod, C, Color.MAX_VALUE);
    }

    /**
     * Calculate value for threshold by Otsu method.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Otsu's_method">Otsu's method -- Wikipedia</a>.</li>
     * <li><a href="http://web-ext.u-aizu.ac.jp/course/bmclass/documents/otsu1979.pdf">Otsu H. -- A Threshold Selection
     * Method from Gray-Level Histograms. 1979</a>.</li>
     * </ol>
     * </p>
     */
    public static int calculateOtsuThresholdValue(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Perform transformation.
         */
        final Histogram hist = new Histogram(image);

        // Initialize q1(t).
        final double[] q1 = new double[256];
        q1[0] = hist.get(0);
        for (int i = 1; i < q1.length; ++i) {
            q1[i] = q1[i - 1] + hist.get(i);
        }

        // Initialize mu1(t).
        final double[] mu1 = new double[256];
        mu1[0] = 0;
        for (int i = 1; i < mu1.length; ++i) {
            mu1[i] = (q1[i - 1] * mu1[i - 1] + i * hist.get(i)) / q1[i];
        }

        // Initialize mu.
        double mu = 0.0;
        for (int i = 0; i < hist.getLength(); ++i) {
            mu += i * hist.get(i);
        }

        // Initialize mu2(t).
        final double[] mu2 = new double[256];
        for (int i = 0; i < mu2.length; ++i) {
            mu2[i] = (mu - q1[i] * mu1[i]) / (1.0 - q1[i]);
        }

        double[] sb = new double[256];
        for (int i = 0; i < sb.length; ++i) {
            sb[i] = q1[i] * (1.0 - q1[i]) * Math.pow(mu1[i] - mu2[i], 2);
        }

        double max = sb[0];
        int t = 0;
        for (int i = 1; i < sb.length; ++i) {
            if (max < sb[i]) {
                max = sb[i];
                t = i;
            }
        }

        return t;
    }

    /**
     * Gradient filter by X and Y dimensions.
     *
     * <p>
     * This method is useful for creation own gradient methods.
     * </p>
     *
     * <p>
     * Algorithm:
     * <ol>
     * <li>Convolve aperture with kernel <code>derivativeX</code> and save result as <code>Gx</code>.</li>
     * <li>Convolve aperture with kernel <code>derivativeY</code> and save result as <code>Gy</code>.</li>
     * <li>Calculate gradient by formula <code>G = SQRT(Gx<sup>2</sup> + Gy<sup>2</sup>)</code>.</li>
     * </ol>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Image_gradient">Image gradient -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param derivativeX
     *            Derivative values for X-dimension. <strong>Should have same size as <code>derivativeY</code></strong>.
     * @param derivativeY
     *            Derivative values for Y-dimension. <strong>Should have same size as <code>derivativeX</code></strong>.
     *            Needed to normalize values of different derivatives.
     * @param scale
     *            Scale parameter.
     * @param extrapolation
     *            Extrapolation method.
     * @return Image with result of applying gradient filter. Have same size, number of channels and type as a source
     *         image.
     */
    public static Image gradientFilter(final Image image, final Matrix derivativeX, final Matrix derivativeY,
            final double scale, final Extrapolation extrapolation) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsSameSize(derivativeX, derivativeY);
        JCV.verifyOddSize(derivativeX.getColumnDimension());
        JCV.verifyOddSize(derivativeX.getRowDimension());

        /*
         * Perform transformation.
         */
        final Image result = image.makeSame();

        image.noneLinearFilter(result, derivativeX.getColumnDimension(), derivativeX.getRowDimension(),
                JCV.calculateCenter(derivativeX.getColumnDimension(), derivativeX.getRowDimension()), 1, extrapolation,
                new KernelOperation() {

                    @Override
                    public void execute(final Image aperture, final Color result) {
                        final double[] Gx = aperture.convolve(derivativeX);
                        final double[] Gy = aperture.convolve(derivativeY);

                        for (int channel = 0; channel < result.getNumOfChannels(); ++channel) {
                            // Calculate 'G' and multiply to scale parameter.
                            result.set(channel, JCV
                                    .round(scale * Math.sqrt(Gx[channel] * Gx[channel] + Gy[channel] * Gy[channel])));
                        }
                    }
                });

        return result;
    }

    /**
     * Edge detection algorithms.
     *
     * <p>
     * Based on {@link #gradientFilter(Image, Matrix, Matrix, double, Extrapolation)}.
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Edge_detection">Edge detection -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param edgeDetectionMethod
     *            Edge detection method.
     * @param scale
     *            Scale parameter for values in result image.
     * @param extrapolation
     *            Extrapolation method.
     * @return Image with result of applying detecting edges filter. Have same size, number of channels and type as a
     *         source image.
     */
    public static Image edgeDetection(final Image image, final EdgeDetect edgeDetectionMethod, final double scale,
            final Extrapolation extrapolation) {
        return gradientFilter(image, edgeDetectionMethod.getMatrixKernelX(), edgeDetectionMethod.getMatrixKernelY(),
                scale, extrapolation);
    }

    /**
     * Same {@link #edgeDetection(Image, EdgeDetect, double, Extrapolation)}, but uses <code>1.0</code> as default scale
     * and {@link Extrapolation#REFLECT} as default extrapolation method.
     */
    public static Image edgeDetection(final Image image, final EdgeDetect edgeDetectionMethod) {
        return edgeDetection(image, edgeDetectionMethod, 1.0, Extrapolation.REFLECT);
    }

    /**
     * Same {@link #edgeDetection(Image, EdgeDetect, double, Extrapolation)}, but uses {@link EdgeDetect#SOBEL} as
     * default edge detection method, <code>1.0</code> as default scale and {@link Extrapolation#REFLECT} as default
     * extrapolation method.
     */
    public static Image edgeDetection(final Image image) {
        return edgeDetection(image, EdgeDetect.SOBEL, 1.0, Extrapolation.REFLECT);
    }

    /**
     * Discrete Laplace operator.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Discrete_Laplace_operator">Discrete Laplace operator -- Wikipedia</a>.
     * </li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param extrapolation
     *            Extrapolation method.
     * @return Image with result of applying Laplace operator. Have same size, number of channels and type as a source
     *         image.
     */
    public static Image laplacian(final Image image, final Extrapolation extrapolation) {
        return linearFilter(image, EdgeDetect.LAPLACIAN.getMatrixKernelX(), -1.0, Color.MIN_VALUE, extrapolation);
    }

    /**
     * Discrete Laplace operator.
     *
     * <p>
     * Use {@link Extrapolation#REPLICATE} by default.
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Discrete_Laplace_operator">Discrete Laplace operator -- Wikipedia</a>.
     * </li>
     * </ol>
     * </p>
     */
    public static Image laplacian(final Image image) {
        return laplacian(image, Extrapolation.REPLICATE);
    }

    /**
     * Invert values into image: each value V invert to <code>({@link Color#MAX_VALUE} - V)</code>.
     */
    public static Image invert(final Image image) {
        final Matrix invertKernel = new Matrix(new double[][]{ { -1.0 } });
        final double div = 1.0;
        final double offset = Color.MAX_VALUE;

        return linearFilter(image, invertKernel, div, offset, Extrapolation.REFLECT);
    }

    /**
     * Release Gaussian kernel for Gaussian filter as a <strong>vector with size <code>(width, 1)</code></strong>.
     *
     * <p>
     * Used formula:
     * 
     * <code><pre>
     * G(x) = alpha * exp{-(x<sup>2</sup>) / (2 * sigma<sup>2</sup>)}
     * </pre></code>
     * 
     * where <code>alpha</code> is a scalable parameter to <code>sum(G(x, y)) == 1.0</code>.
     * </p>
     *
     * <p>
     * Uses into {@link #gaussianBlur(Image, Size, double, double, Extrapolation)}.
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Gaussian_filter">Gaussian filter -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    public static Matrix getGaussianKernel(final int kernelSize, final double sigma2) {
        /*
         * Verify parameters.
         */
        if (kernelSize == 0 || kernelSize % 2 == 0) {
            throw new IllegalArgumentException("Parameter 'kernelSize' must have width (= "
                    + Integer.toString(kernelSize) + ") is odd value and be more than 0 (1, 3, 5, ...)!");
        }

        /*
         * Create matrix.
         */
        Matrix kernel = new Matrix(kernelSize, 1);

        // Calculate values.
        final int kernelHalfSize = (kernelSize - 1) / 2;
        for (int i = -kernelHalfSize; i <= kernelHalfSize; ++i) {
            kernel.set(i + kernelHalfSize, 0, Math.exp(-(i * i) / (2.0 * sigma2)));
        }

        // Scale.
        double sum = 0.0;
        for (int i = 0; i < kernel.getRowDimension(); ++i) {
            sum += kernel.get(i, 0);
        }
        kernel = kernel.times(1.0 / sum);

        return kernel;
    }

    /**
     * Same as {@link #getGaussianKernel(int, double)}, but uses {@link #getSigma(int)} as default
     * <code>sigma<sup>2</sup></code> value.
     */
    public static Matrix getGaussianKernel(final int kernelSize) {
        return getGaussianKernel(kernelSize, getSigma(kernelSize));
    }

    /**
     * Return size of one dimension base on <code>sigma</code> value.
     *
     * <p>
     * Uses into {@link #gaussianBlur(Image, Size, double, double, Extrapolation)}.
     * </p>
     */
    public static int getKernelSize(final double sigma) {
        int size = JCV.roundDown(sigmaSizeCoeff * sigma);
        if (size % 2 == 0) {
            ++size;
        }

        return size;
    }

    /**
     * Return <code>sigma</code> base on size value of one dimension for Gaussian blur.
     */
    public static double getSigma(final int size) {
        return size / sigmaSizeCoeff;
    }

    /**
     * Gaussian blur.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Gaussian_blur">Gaussian blur -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <strong>Should have odd size for both dimensions (1, 3, 5,
     *            ...)!</strong>
     * @param sigmaX
     *            Sigma value by X dimension (see formula).
     * @param sigmaY
     *            Sigma value by Y dimension (see formula).
     * @param extrapolationMethod
     *            Extrapolation method.
     * @return Image with result of applying Gaussian blur filter. Have same size, number of channels and type as a
     *         source image.
     */
    public static Image gaussianBlur(final Image image, final Size kernelSize, final double sigmaX, final double sigmaY,
            final Extrapolation extrapolationMethod) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(kernelSize);

        /*
         * Perform transformation.
         */
        final Matrix gaussianKernelX = getGaussianKernel(kernelSize.getWidth(), sigmaX);
        final Matrix gaussianKernelY = getGaussianKernel(kernelSize.getHeight(), sigmaY).transpose();
        final double div = 1.0;
        final double offset = Color.MIN_VALUE;

        return separableFilter(image, gaussianKernelX, gaussianKernelY, div, offset, extrapolationMethod);
    }

    /**
     * Blur image.
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <strong>Should have odd size for both dimensions (1, 3, 5,
     *            ...)!</strong>
     * @param blurMethod
     *            Blur method.
     * @param extrapolationMethod
     *            Extrapolation method.
     * @return Image with result of applying blur filter. Have same size, number of channels and type as a source image.
     */
    public static Image blur(final Image image, final Size kernelSize, final Blur blurMethod,
            final Extrapolation extrapolationMethod) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(kernelSize);
        JCV.verifyOddSize(kernelSize.getWidth());
        JCV.verifyOddSize(kernelSize.getHeight());
        JCV.verifyIsNotNull(blurMethod);
        JCV.verifyIsNotNull(extrapolationMethod);

        /*
         * Run.
         */
        return blurMethod.run(image, kernelSize, extrapolationMethod);
    }

    /**
     * Same as {@link #blur(Image, Size, Blur, Extrapolation)}, but using {@link Extrapolation#REPLICATE} as default
     * extrapolation method.
     */
    public static Image blur(final Image image, final Size kernelSize, final Blur blurMethod) {
        return blur(image, kernelSize, blurMethod, Extrapolation.REPLICATE);
    }

    /**
     * Same as {@link #blur(Image, Size, Blur)}, but using {@link Blur#GAUSSIAN} as default blur type and
     * {@link Extrapolation#REPLICATE} as default extrapolation method.
     */
    public static Image blur(final Image image, final Size kernelSize) {
        return blur(image, kernelSize, Blur.GAUSSIAN, Extrapolation.REPLICATE);
    }

    /**
     * Sharpen image.
     *
     * @param image
     *            Source image.
     * @param sharpenMethod
     *            Sharpen method.
     * @param extrapolationMethod
     *            Extrapolation method.
     * @return Image with result of applying sharpen filter. Have same size, number of channels and type as a source
     *         image.
     */
    public static Image sharpen(final Image image, final Sharpen sharpenMethod,
            final Extrapolation extrapolationMethod) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(sharpenMethod);
        JCV.verifyIsNotNull(extrapolationMethod);

        /*
         * Run.
         */
        return sharpenMethod.run(image, extrapolationMethod);
    }

    /**
     * Same as {@link #sharpen(Image, Sharpen, Extrapolation)}, but uses {@link Extrapolation#REPLICATE} as default
     * extrapolation method.
     */
    public static Image sharpen(final Image image, final Sharpen sharpenMethod) {
        return sharpen(image, sharpenMethod, Extrapolation.REPLICATE);
    }

    /**
     * Same as {@link #sharpen(Image, Sharpen, Extrapolation)}, but uses {@link Sharpen#MODERN} as default sharpen type
     * and {@link Extrapolation#REPLICATE} as default extrapolation method.
     */
    public static Image sharpen(final Image source) {
        return sharpen(source, Sharpen.MODERN, Extrapolation.REPLICATE);
    }

    /**
     * Morphology transformation.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Mathematical_morphology">Mathematical morphology -- Wikipedia</a>.</li>
     * <li><a href="http://haralick.org/journals/04767941.pdf">Haralick R. M., Sternberg S., Zhuang X. -- Image Analysis
     * Using Mathematical Morphology. 1987</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param kernelSize
     *            Size of kernel for applying filter. <strong>Should have odd size for both dimensions (1, 3, 5,
     *            ...)!</strong>
     * @param morphologyMethod
     *            Morphology method.
     * @param iterations
     *            Number of applying this filter to source image.
     * @param extrapolationMethod
     *            Extrapolation method.
     * @return Image with result of applying morphology filter. Have same size, number of channels and type as a source
     *         image.
     */
    public static Image morphology(final Image image, final Size kernelSize, final Morphology morphologyMethod,
            final int iterations, final Extrapolation extrapolationMethod) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyOddSize(kernelSize.getWidth());
        JCV.verifyOddSize(kernelSize.getHeight());
        JCV.verifyIsNotNull(morphologyMethod);
        if (iterations <= 0) {
            throw new IllegalArgumentException("Number of iterations should be more than 0!");
        }
        JCV.verifyIsNotNull(extrapolationMethod);

        /*
         * Perform transformation.
         */
        Image result = image;

        for (int i = 0; i < iterations; ++i) {
            result = morphologyMethod.run(result, kernelSize, extrapolationMethod);
        }

        return result;
    }

    /**
     * Same as {@link #morphology(Image, Size, Morphology, int, Extrapolation)}, but use {@link Extrapolation#REPLICATE}
     * as default extrapolation method.
     */
    public static Image morphology(final Image image, final Size kernelSize, final Morphology morphologyMethod,
            final int iterations) {
        return morphology(image, kernelSize, morphologyMethod, iterations, Extrapolation.REPLICATE);
    }

    /**
     * Same as {@link #morphology(Image, Size, Morphology, int, Extrapolation)}, but use <code>1</code> as default
     * iteration and {@link Extrapolation#REPLICATE} as default extrapolation method.
     */
    public static Image morphology(final Image image, final Size kernelSize, final Morphology morphologyMethod) {
        return morphology(image, kernelSize, morphologyMethod, 1, Extrapolation.REPLICATE);
    }
}
