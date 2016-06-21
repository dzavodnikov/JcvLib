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
package org.jcvlib.image.geometry;

import org.jcvlib.core.Size;

import Jama.Matrix;

/**
 * <p>
 * <h6>Links:</h6>
 * <ol>
 * <li><a href="https://en.wikipedia.org/wiki/Smoothing">Smoothing -- Wikipedia</a>.</li>
 * </ol>
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public enum Reflection {
    /**
     * Horizontal reflection. <code><pre>
     * +-------+     +-------+
     * | 1 2 3 |     | 3 2 1 |
     * | 4 5 6 | --> | 6 5 4 |
     * | 7 8 9 |     | 9 8 7 |
     * +-------+     +-------+
     * </pre></code>
     */
    HORIZONTAL {

        @Override
        protected Matrix getMatrix(final Size imageSize) {
            return new Matrix(new double[][] { { -1.0, 0.0, imageSize.getWidth() - 1 }, { 0.0, 1.0, 0.0 } });
        }
    },

    /**
     * Vertical reflection. <code><pre>
     * +-------+     +-------+
     * | 1 2 3 |     | 7 8 9 |
     * | 4 5 6 | --> | 4 5 6 |
     * | 7 8 9 |     | 1 2 3 |
     * +-------+     +-------+
     * </pre></code>
     */
    VERTICAL {

        @Override
        protected Matrix getMatrix(final Size imageSize) {
            return new Matrix(new double[][] { { 1.0, 0.0, 0.0 }, { 0.0, -1.0, imageSize.getHeight() - 1 } });
        }
    },

    /**
     * Diagonal reflection. <code><pre>
     * +-------+     +-------+
     * | 1 2 3 |     | 9 8 7 |
     * | 4 5 6 | --> | 6 5 4 |
     * | 7 8 9 |     | 3 2 1 |
     * +-------+     +-------+
     * </pre></code>
     */
    DIAGONAL {

        @Override
        protected Matrix getMatrix(final Size imageSize) {
            return new Matrix(new double[][] { { -1.0, 0.0, imageSize.getWidth() - 1 },
                    { 0.0, -1.0, imageSize.getHeight() - 1 } });
        }
    };

    protected abstract Matrix getMatrix(final Size imageSize);
}
