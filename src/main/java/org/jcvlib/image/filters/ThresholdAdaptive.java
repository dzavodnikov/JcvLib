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

import Jama.Matrix;

/**
 * Contains adaptive threshold methods.
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
public enum ThresholdAdaptive {
    /**
     * Calculate mean value for all pixels from aperture of current pixel. All values have same weight.
     */
    MEAN {

        @Override
        protected Matrix getMatrixCoeff(final int size) {
            final Matrix coeff = new Matrix(size, size);
            coeff.setMatrix(0, size - 1, 0, size - 1, new Matrix(size, size, 1.0 / (size * size - 1)));
            return coeff;
        }

        @Override
        protected Threshold getThresholdMethod() {
            return Threshold.BINARY;
        }
    },

    /**
     * Calculate mean value for all pixels from aperture of current pixel. All values have same weight. Invert result.
     */
    MEAN_INV {

        @Override
        protected Matrix getMatrixCoeff(final int size) {
            return MEAN.getMatrixCoeff(size);
        }

        @Override
        protected Threshold getThresholdMethod() {
            return Threshold.BINARY_INV;
        }
    },

    /**
     * Calculate mean value for all pixels from aperture of current pixel. All values have weight from Gaussian matrix.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Gaussian_blur">Gaussian blur -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    GAUSSIAN {

        @Override
        protected Matrix getMatrixCoeff(final int size) {
            final Matrix coeff = new Matrix(size, size);
            final Matrix gaussianKernel = Filters.getGaussianKernel(size);
            coeff.setMatrix(0, size - 1, 0, size - 1, gaussianKernel.times(gaussianKernel.transpose()));
            return coeff;
        }

        @Override
        protected Threshold getThresholdMethod() {
            return Threshold.BINARY;
        }
    },

    /**
     * Calculate mean value for all pixels from aperture of current pixel. All values have weight from Gaussian matrix.
     * Invert result.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Gaussian_blur">Gaussian blur -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    GAUSSIAN_INV {

        @Override
        protected Matrix getMatrixCoeff(final int size) {
            return GAUSSIAN.getMatrixCoeff(size);
        }

        @Override
        public Threshold getThresholdMethod() {
            return Threshold.BINARY_INV;
        }
    };

    protected abstract Matrix getMatrixCoeff(final int size);

    protected abstract Threshold getThresholdMethod();
}
