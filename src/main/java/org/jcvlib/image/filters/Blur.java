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
package org.jcvlib.image.filters;

import java.util.Arrays;

import org.jcvlib.core.Color;
import org.jcvlib.core.Extrapolation;
import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.jcvlib.core.Point;
import org.jcvlib.core.Size;
import org.jcvlib.image.Misc;
import org.jcvlib.parallel.Parallel;

import Jama.Matrix;

/**
 * Smoothing methods.
 * <p>
 * <h6>Links:</h6>
 * <ol>
 * <li><a href="https://en.wikipedia.org/wiki/Smoothing">Smoothing -- Wikipedia</a>.</li>
 * </ol>
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public enum Blur {
    /**
     * Box blur.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Box_blur">Box blur -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    BOX {

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            // Create a kernel.
            final Matrix box = new Matrix(kernelSize.getHeight(), kernelSize.getWidth());
            for (int x = 0; x < box.getColumnDimension(); ++x) {
                for (int y = 0; y < box.getRowDimension(); ++y) {
                    box.set(y, x, 1.0);
                }
            }
            final double div = kernelSize.calculateN();
            final double offset = Color.MIN_VALUE;

            // Apply filter.
            return Filters.linearFilter(image, box, div, offset, extrapolation);
        }
    },

    /**
     * Gaussian blur.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Gaussian_blur">Gaussian blur -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    GAUSSIAN {

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            return Filters.gaussianBlur(image, kernelSize, Filters.getSigma(kernelSize.getWidth()),
                    Filters.getSigma(kernelSize.getHeight()), extrapolation);
        }
    },

    /**
     * Median filter.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Median_filter">Median filter -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    MEDIAN {

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            final Point kernelCenter = JCV.calculateCenter(kernelSize.getWidth(), kernelSize.getHeight());
            final Image result = image.makeSame();

            Parallel.pixels(image, (x, y, worker) -> {
                final int shiftX = x - kernelCenter.getX();
                final int shiftY = y - kernelCenter.getY();

                // Copy content into temporary array.
                final int[] tempArr = new int[kernelSize.calculateN()];
                for (int lx = 0; lx < kernelSize.getWidth(); ++lx) {
                    for (int ly = 0; ly < kernelSize.getHeight(); ++ly) {
                        for (int channel1 = 0; channel1 < image.getNumOfChannels(); ++channel1) {
                            tempArr[lx * kernelSize.getHeight() + ly] = image.get(shiftX + lx, shiftY + ly, channel1,
                                    extrapolation);
                        }
                    }
                }

                // Sort temporary array.
                Arrays.sort(tempArr);

                // Return middle element.
                for (int channel2 = 0; channel2 < image.getNumOfChannels(); ++channel2) {
                    result.set(x, y, channel2, tempArr[(tempArr.length - 1) / 2]);
                }
            });

            return result;
        }
    },

    /**
     * Kuwahara blur.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://rsbweb.nih.gov/ij/plugins/kuwahara.html">Kuwahara Filter</a>.</li>
     * </ol>
     * </p>
     */
    KUWAHARA {

        /**
         * Calculate variance of sub-rectangle of kernel.
         * <p>
         * Used into {@link #kuwaharaBlur(Image, Size, int)}.
         * </p>
         */
        private double[] variance(final Image aperture, final Color average) {
            final double[] result = new double[aperture.getNumOfChannels()];
            for (int i = 0; i < aperture.getNumOfChannels(); ++i) {
                result[i] = 0.0;
            }

            for (int x = 0; x < aperture.getWidth(); ++x) {
                for (int y = 0; y < aperture.getHeight(); ++y) {
                    for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                        final double value = average.get(channel) - aperture.get(x, y, channel);
                        result[channel] += value * value;
                    }
                }
            }

            return result;
        }

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            if (kernelSize.calculateN() > 9) {
                final Point kernelCenter = JCV.calculateCenter(kernelSize.getWidth(), kernelSize.getHeight());
                final Image result = image.makeSame();

                image.noneLinearFilter(result, kernelSize.getWidth(), kernelSize.getHeight(), kernelCenter, 1,
                        extrapolation, (aperture, result1) -> {
                            final Image[] windows = new Image[4];

                            final Color[] mean = new Color[windows.length];
                            final double[][] variance = new double[windows.length][aperture.getNumOfChannels()];

                            final Size windowSize = new Size(kernelCenter.getX(), kernelCenter.getY());

                            // Create sub-images.
                            windows[0] = aperture.makeSubImage(0, 0, windowSize.getWidth(), windowSize.getHeight());
                            windows[1] = aperture.makeSubImage(kernelCenter.getX() + 1, 0, windowSize.getWidth(),
                                    windowSize.getHeight());
                            windows[2] = aperture.makeSubImage(0, kernelCenter.getY() + 1, windowSize.getWidth(),
                                    windowSize.getHeight());
                            windows[3] = aperture.makeSubImage(kernelCenter.getX() + 1, kernelCenter.getY() + 1,
                                    windowSize.getWidth(), windowSize.getHeight());

                            // Calculate average and variance.
                            for (int i1 = 0; i1 < windows.length; ++i1) {
                                mean[i1] = Misc.calculateMean(windows[i1]);
                                variance[i1] = variance(windows[i1], mean[i1]);
                            }

                            // Found min of variance.
                            for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                                int minPos = 0;
                                double minVal = variance[minPos][channel];
                                for (int i2 = 1; i2 < 4; ++i2) {
                                    if (variance[i2][channel] < minVal) {
                                        minVal = variance[i2][channel];
                                        minPos = i2;
                                    }
                                }

                                // Different values for different channels.
                                result1.set(channel, mean[minPos].get(channel));
                            }
                        });

                return result;
            } else {
                return image;
            }
        }
    };

    protected abstract Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation);
}
