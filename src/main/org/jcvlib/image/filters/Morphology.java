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
package org.jcvlib.image.filters;

import org.jcvlib.core.Color;
import org.jcvlib.core.Extrapolation;
import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.jcvlib.core.KernelOperation;
import org.jcvlib.core.Size;
import org.jcvlib.image.Misc;

/**
 * Image morphology methods.
 *
 * <p>
 * <h6>Links:</h6>
 * <ol>
 * <li><a href="http://en.wikipedia.org/wiki/Mathematical_morphology">Mathematical morphology -- Wikipedia</a>.</li>
 * </ol>
 * </p>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public enum Morphology {
    /**
     * Dilation.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Dilation_(morphology)">Dilation (morphology) -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    DILATE {

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            final Image result = image.makeSame();

            image.noneLinearFilter(result, kernelSize.getWidth(), kernelSize.getHeight(),
                    JCV.calculateCenter(kernelSize.getWidth(), kernelSize.getHeight()), 1, extrapolation,
                    new KernelOperation() {

                @Override
                public void execute(final Image aperture, final Color max) {
                    // Initialize.
                    max.fill(Color.MIN_VALUE);

                    // Find maximum.
                    for (int x = 0; x < aperture.getWidth(); ++x) {
                        for (int y = 0; y < aperture.getHeight(); ++y) {
                            for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                                if (max.get(channel) < aperture.get(x, y, channel)) {
                                    max.set(channel, aperture.get(x, y, channel));
                                }
                            }
                        }
                    }
                }
            });

            return result;
        }
    },

    /**
     * Erosion.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Erosion_(morphology)">Erosion (morphology) -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    ERODE {

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            final Image result = image.makeSame();

            image.noneLinearFilter(result, kernelSize.getWidth(), kernelSize.getHeight(),
                    JCV.calculateCenter(kernelSize.getWidth(), kernelSize.getHeight()), 1, extrapolation,
                    new KernelOperation() {

                @Override
                public void execute(final Image aperture, final Color min) {
                    // Initialize.
                    min.fill(Color.MAX_VALUE);

                    // Find minimum.
                    for (int x = 0; x < aperture.getWidth(); ++x) {
                        for (int y = 0; y < aperture.getHeight(); ++y) {
                            for (int channel = 0; channel < aperture.getNumOfChannels(); ++channel) {
                                if (min.get(channel) > aperture.get(x, y, channel)) {
                                    min.set(channel, aperture.get(x, y, channel));
                                }
                            }
                        }
                    }
                }
            });

            return result;
        }
    },

    /**
     * Opening.
     *
     * <p>
     * <code>open(image, kernel) = dilate(erode(image, kernel), kernel)</code>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Opening_(morphology)">Opening (morphology) -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    OPEN {

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            return DILATE.run(ERODE.run(image, kernelSize, extrapolation), kernelSize, extrapolation);
        }
    },

    /**
     * Closing.
     *
     * <p>
     * <code>close(image, kernel) = erode(dilate(image, kernel), kernel)</code>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Closing_(morphology)">Closing (morphology) -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    CLOSE {

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            return ERODE.run(DILATE.run(image, kernelSize, extrapolation), kernelSize, extrapolation);
        }
    },

    /**
     * Morphological gradient.
     *
     * <p>
     * <code>morphologyGradient(image, kernel) = dilate(image, kernel) - erode(image, kernel)</code>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Morphological_Gradient">Morphological Gradient -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    GRADIENT {

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            return Misc.absDiff(DILATE.run(image, kernelSize, extrapolation),
                    ERODE.run(image, kernelSize, extrapolation));
        }
    },

    /**
     * White top-hat.
     *
     * <p>
     * <code>white_top_hat(image, kernel) = image - open(image, kernel)</code>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Top-hat_transform">Top-hat transform -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    WHITE_TOP_HAT {

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            return Misc.minus(image, OPEN.run(image, kernelSize, extrapolation));
        }
    },

    /**
     * Black top-hat.
     *
     * <p>
     * <code>black_top_hat(image, kernel) = close(image, kernel) - image</code>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="https://en.wikipedia.org/wiki/Top-hat_transform">Top-hat transform -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    BLACK_TOP_HAT {

        @Override
        protected Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation) {
            return Misc.minus(CLOSE.run(image, kernelSize, extrapolation), image);
        }
    };

    protected abstract Image run(final Image image, final Size kernelSize, final Extrapolation extrapolation);
}
