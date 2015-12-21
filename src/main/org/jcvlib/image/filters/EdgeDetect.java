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

import Jama.Matrix;

/**
 * Edge detection methods.
 *
 * <p>
 * <h6>Links:</h6>
 * <ol>
 * <li><a href="http://en.wikipedia.org/wiki/Edge_detection">Edge detection -- Wikipedia</a>.</li>
 * </ol>
 * </p>
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public enum EdgeDetect {
    /**
     * Roberts cross.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Roberts_Cross">Roberts Cross -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    ROBERTS {

        @Override
        protected Matrix getMatrixKernelX() {
            //@formatter:off
            return new Matrix(new double[][]{ 
                {  1.0,  0.0,  0.0 }, 
                {  0.0, -1.0,  0.0 }, 
                {  0.0,  0.0,  0.0 }
            });
            //@formatter:on
        }

        @Override
        protected Matrix getMatrixKernelY() {
            //@formatter:off
            return new Matrix(new double[][]{ 
                {  0.0,  0.0,  0.0 }, 
                {  0.0,  0.0, -1.0 }, 
                {  0.0,  1.0,  0.0 }
            });
            //@formatter:on
        }
    },

    /**
     * Prewitt operator.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Prewitt_operator">Prewitt operator -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    PREWITT {

        @Override
        protected Matrix getMatrixKernelX() {
            //@formatter:off
            return new Matrix(new double[][]{ 
                { -1.0,  0.0,  1.0 }, 
                { -1.0,  0.0,  1.0 }, 
                { -1.0,  0.0,  1.0 }
            });
            //@formatter:on
        }

        @Override
        protected Matrix getMatrixKernelY() {
            //@formatter:off
            return new Matrix(new double[][]{ 
                {  1.0,  1.0,  1.0 }, 
                {  0.0,  0.0,  0.0 }, 
                { -1.0, -1.0, -1.0 }
            });
            //@formatter:on
        }
    },

    /**
     * Sobel operator.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Sobel_operator">Sobel operator -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    SOBEL {

        @Override
        protected Matrix getMatrixKernelX() {
            //@formatter:off
            return new Matrix(new double[][]{ 
                { -1.0,  0.0,  1.0 }, 
                { -2.0,  0.0,  2.0 }, 
                { -1.0,  0.0,  1.0 }
            });
            //@formatter:on
        }

        @Override
        protected Matrix getMatrixKernelY() {
            //@formatter:off
            return new Matrix(new double[][]{
                { -1.0, -2.0, -1.0 }, 
                {  0.0,  0.0,  0.0 }, 
                {  1.0,  2.0,  1.0 }
            });
            //@formatter:on
        }
    },

    /**
     * Scharr operator.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Sobel_operator#Alternative_operators">Sobel operator.
     * Alternative_operators -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    SCHARR {

        @Override
        protected Matrix getMatrixKernelX() {
            //@formatter:off
            return new Matrix(new double[][]{ 
                {   3.0,   0.0,  -3.0 }, 
                {  10.0,   0.0, -10.0 }, 
                {   3.0,   0.0,  -3.0 }
            });
            //@formatter:on
        }

        @Override
        protected Matrix getMatrixKernelY() {
            //@formatter:off
            return new Matrix(new double[][]{ 
                {   3.0,  10.0,   3.0 }, 
                {   0.0,   0.0,   0.0 }, 
                {  -3.0, -10.0,  -3.0 }
            });
            //@formatter:on
        }
    },

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
     */
    LAPLACIAN {

        @Override
        protected Matrix getMatrixKernelX() {
            //@formatter:off
            return new Matrix(new double[][]{ 
                {   0.0,   1.0,   0.0 }, 
                {   1.0,  -4.0,   1.0 }, 
                {   0.0,   1.0,   0.0 }
            });
            //@formatter:on
        }

        @Override
        protected Matrix getMatrixKernelY() {
            return LAPLACIAN.getMatrixKernelX();
        }
    };

    protected abstract Matrix getMatrixKernelX();

    protected abstract Matrix getMatrixKernelY();
}
