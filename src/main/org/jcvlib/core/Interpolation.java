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
 * Contains interpolation methods.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public enum Interpolation {
    /**
     * Nearest neighbor interpolation.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Nearest-neighbor_interpolation">Nearest neighbor interpolation --
     * Wikipedia</a>.</li>
     * <li><a href="http://www.compuphase.com/graphic/scale.htm">Quick image scaling algorithms</a>.</li>
     * <li><a href="http://www.cambridgeincolour.com/tutorials/image-interpolation.htm">Understanding Digital Image
     * Interpolation</a>.</li>
     * </ol>
     * </p>
     */
    NEAREST_NEIGHBOR {

        @Override
        public double get(final Image image, final double x, final double y, final int channel) {
            return image.get(JCV.round(x), JCV.round(y), channel);
        }
    },

    /**
     * Bilinear interpolation.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Bilinear_interpolation">Bilinear interpolation -- Wikipedia</a>.</li>
     * <li><a href="http://www.compuphase.com/graphic/scale.htm">Quick image scaling algorithms</a>.</li>
     * <li><a href="http://www.cambridgeincolour.com/tutorials/image-interpolation.htm">Understanding Digital Image
     * Interpolation</a>.</li>
     * </ol>
     * </p>
     */
    BILINEAR {

        @Override
        public double get(final Image image, final double x, final double y, final int channel) {
            final int minX = JCV.roundDown(x);
            final int maxX = JCV.roundUp(x);
            final int minY = JCV.roundDown(y);
            final int maxY = JCV.roundUp(y);

            final double pX = x - minX;
            final double pY = y - minY;

            double sum = 0.0;
            sum += (1.0 - pX) * (1.0 - pY) * image.get(minX, minY, channel);
            sum += pX * (1.0 - pY) * image.get(maxX, minY, channel);
            sum += (1.0 - pX) * pY * image.get(minX, maxY, channel);
            sum += pX * pY * image.get(maxX, maxY, channel);

            return sum;
        }
    },

    /**
     * Bicubic interpolation.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Bicubic_interpolation">Bicubic interpolation -- Wikipedia</a>.</li>
     * <li><a href="http://www.compuphase.com/graphic/scale.htm">Quick image scaling algorithms</a>.</li>
     * <li><a href="http://www.cambridgeincolour.com/tutorials/image-interpolation.htm">Understanding Digital Image
     * Interpolation</a>.</li>
     * </ol>
     * </p>
     */
    BICUBIC {

        /**
         * Calculate cubic interpolation.
         *
         * @param p
         *            Number of values. Base on this values we will be calculate interpolation.
         * @param x
         *            Position that we want to interpolate.
         */
        private double cubicInterpolation(final double[] p, final double x) {
            /*
             * See:
             * * http://www.paulinternet.nl/?page=bicubic
             */
            return p[1] + 0.5 * x * (p[2] - p[0]
                    + x * (2.0 * p[0] - 5.0 * p[1] + 4.0 * p[2] - p[3] + x * (3.0 * (p[1] - p[2]) + p[3] - p[0])));
        }

        /**
         * Return nearest values to given position.
         */
        private double[] calculateNearPos(final Image image, final int x, final int y, final int channel) {
            return new double[]{ image.get(x - 1, y, channel, Extrapolation.REFLECT),
                    image.get(x, y, channel, Extrapolation.REFLECT),
                    image.get(x + 1, y, channel, Extrapolation.REFLECT),
                    image.get(x + 2, y, channel, Extrapolation.REFLECT) };
        }

        @Override
        public double get(final Image image, final double x, final double y, final int channel) {
            final int baseX = JCV.roundDown(x);
            final int baseY = JCV.roundDown(y);

            final double offsetX = x - baseX;
            final double offsetY = y - baseY;

            final double[] arr = new double[4];
            arr[0] = cubicInterpolation(calculateNearPos(image, baseX, baseY - 1, channel), offsetX);
            arr[1] = cubicInterpolation(calculateNearPos(image, baseX, baseY + 0, channel), offsetX);
            arr[2] = cubicInterpolation(calculateNearPos(image, baseX, baseY + 1, channel), offsetX);
            arr[3] = cubicInterpolation(calculateNearPos(image, baseX, baseY + 2, channel), offsetX);

            return cubicInterpolation(arr, offsetY);
        }
    };

    public abstract double get(final Image image, final double x, final double y, final int channel);
}
