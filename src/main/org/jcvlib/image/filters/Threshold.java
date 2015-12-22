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

/**
 * Contains threshold methods.
 *
 * <p>
 * <h6>Links:</h6>
 * <ol>
 * <li><a href="http://en.wikipedia.org/wiki/Thresholding_(image_processing)">Thresholding (image processing) --
 * Wikipedia</a>.</li>
 * </ol>
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public enum Threshold {
    /**
     * Binary threshold.
     * 
     * <code><pre>
     * if src(x, y) <= threshold
     *      src(x, y) := 0
     * else
     *      src(x, y) := maxVal
     * </pre></code>
     */
    BINARY {

        @Override
        protected int run(final int value, final int threshold, final int maxVal) {
            if (value <= threshold) {
                return Color.MIN_VALUE;
            } else {
                return maxVal;
            }
        }
    },

    /**
     * Binary inverted threshold.
     * 
     * <code><pre>
     * if src(x, y) <= threshold
     *      src(x, y) := maxVal
     * else
     *      src(x, y) := 0
     * </pre></code>
     */
    BINARY_INV {

        @Override
        protected int run(final int value, final int threshold, final int maxVal) {
            if (value <= threshold) {
                return maxVal;
            } else {
                return Color.MIN_VALUE;
            }
        }
    },

    /**
     * Truncated threshold.
     *
     * <code><pre>
     * if src(x, y) <= threshold
     *      // Do nothing.
     * else
     *      src(x, y) := threshold
     * </pre></code>
     */
    TRUNC {

        @Override
        protected int run(final int value, final int threshold, final int maxVal) {
            if (value <= threshold) {
                return value;
            } else {
                return threshold;
            }
        }
    },

    /**
     * Threshold to zero.
     * 
     * <code><pre>
     * if src(x, y) <= threshold
     *      src(x, y) := 0
     * else
     *      // Do nothing.
     * </pre></code>
     */
    TO_ZERO {

        @Override
        protected int run(final int value, final int threshold, final int maxVal) {
            if (value <= threshold) {
                return Color.MIN_VALUE;
            } else {
                return value;
            }
        }
    },

    /**
     * Threshold to zero inverted.
     * 
     * <code><pre>
     * if src(x, y) <= threshold
     *      // Do nothing.
     * else
     *      src(x, y) := 0
     * </pre></code>
     */
    TO_ZERO_INV {

        @Override
        protected int run(final int value, final int threshold, final int maxVal) {
            if (value <= threshold) {
                return value;
            } else {
                return Color.MIN_VALUE;
            }
        }
    };

    protected abstract int run(final int value, final int threshold, final int maxVal);
}
