/*
 * Copyright (c) 2015-2017 JcvLib Team
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
import org.jcvlib.core.Image;
import org.jcvlib.image.Misc;

import Jama.Matrix;

/**
 * Image sharpen methods.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public enum Sharpen {
    /**
     * Sharpen image using Discrete Laplace operator.
     * <p>
     * Algorithm:
     * <ol>
     * <li>Apply discrete Laplace operator to source image.</li>
     * <li>Summarize source image with image from step 1.</li>
     * </ol>
     * </p>
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Discrete_Laplace_operator">Discrete Laplace operator -- Wikipedia</a>.
     * </li>
     * </ol>
     * </p>
     */
    LAPLACIAN {

        @Override
        protected Image run(final Image image, final Extrapolation extrapolationMethod) {
            return Misc.sum(Filters.laplacian(image), image);
        }
    },

    /**
     * Sharpen image using common-used sharpen matrix.
     */
    MODERN {

        @Override
        protected Image run(final Image image, final Extrapolation extrapolationMethod) {
            //@formatter:off
            final Matrix modernSharpen = new Matrix(new double[][]{
                    {  0.0, -1.0,  0.0 },
                    { -1.0,  5.0, -1.0 },
                    {  0.0, -1.0,  0.0 }
                });
            //@formatter:on
            final double div = 1.0;
            final double offset = Color.MIN_VALUE;

            return Filters.linearFilter(image, modernSharpen, div, offset, Extrapolation.REFLECT);
        }
    };

    protected abstract Image run(final Image image, final Extrapolation extrapolationMethod);
}
