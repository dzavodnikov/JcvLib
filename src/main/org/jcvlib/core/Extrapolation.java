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

/**
 * Contains extrapolation methods.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public enum Extrapolation {

    /**
     * <code>00000|abcdefgh|00000</code> (image boundaries are denoted with "<code>|</code>").
     */
    ZERO {

        @Override
        public int get(final Image image, final int x, final int y, final int channel) {
            if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
                return Color.MIN_VALUE;
            } else {
                return image.getUnsafe(x, y, channel);
            }
        }
    },

    /**
     * <code>aaaaaa|abcdefgh|hhhhhhh</code> (image boundaries are denoted with "<code>|</code>").
     */
    REPLICATE {

        @Override
        protected int translatePosition(final int xy, final int wh) {
            if (xy < 0) {
                return 0;
            }

            if (xy >= wh) {
                return wh - 1;
            }

            return super.translatePosition(xy, wh);
        }
    },

    /**
     * <code>fedcba|abcdefgh|hgfedcb</code> (image boundaries are denoted with "<code>|</code>").
     */
    REFLECT {

        @Override
        protected int translatePosition(final int xy, final int wh) {
            if (xy < 0) {
                return -xy - 1;
            }

            if (xy >= wh) {
                return wh - (xy - wh) - 1;
            }

            return super.translatePosition(xy, wh);
        }
    },

    /**
     * <code>cdefgh|abcdefgh|abcdefg</code> (image boundaries are denoted with "<code>|</code>").
     */
    WRAP {

        @Override
        protected int translatePosition(final int xy, final int wh) {
            if (xy < 0) {
                return wh + xy;
            }

            if (xy >= wh) {
                return xy - wh;
            }

            return super.translatePosition(xy, wh);
        }
    };

    /**
     * Translate position to release extrapolation on image borders.
     *
     * @param xy
     *            X or Y values.
     * @param wh
     *            Width or Height of image.
     * @return Position that are should be used for extrapolation.
     */
    protected int translatePosition(final int xy, final int wh) {
        return xy;
    }

    public int get(final Image image, final int x, final int y, final int channel) {
        return image.getUnsafe(translatePosition(x, image.getWidth()), translatePosition(y, image.getHeight()),
                channel);
    }
}
