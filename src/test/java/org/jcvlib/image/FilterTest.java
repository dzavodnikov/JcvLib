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
package org.jcvlib.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jcvlib.core.Color;
import org.jcvlib.core.Extrapolation;
import org.jcvlib.core.Image;
import org.jcvlib.core.ImageTest;
import org.jcvlib.core.JCV;
import org.jcvlib.image.filters.Filters;
import org.junit.Test;

import Jama.Matrix;

/**
 * Test class for colors {@link Filters}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class FilterTest {

    /**
     * Test method for: {@link Filters#linearFilter(Image, Matrix, double, double, Extrapolation)}.
     */
    @Test
    public void testLinearFilter() {
        /*
         * Source image.
         */
        final Image image = new Image(5, 5, 1);
        image.fill(new Color(1, Color.MIN_VALUE));
        /*
         * A   0 1 2 3 4   B
         *   +-----------+
         * 0 | 1 0 0 0 0 |
         * 1 | 0 0 0 0 0 |
         * 2 | 0 0 0 0 0 |
         * 3 | 0 0 0 0 0 |
         * 4 | 0 0 0 0 0 |
         *   +-----------+
         * D               C
         */
        image.set(0, 0, 0, 1);

        ImageTest.printImage(image, "Image after linear filter applying:");

        /*
         * Kernel.
         */
        //@formatter:off
        final Matrix kernel = new Matrix(new double[][]{ 
            {  0,  0,  0,  0,  0 }, 
            {  0, 10,  0,  0,  0 }, 
            {  0,  0,  0,  0,  0 },
            {  0,  0,  0,  0,  0 }, 
            {  0,  0,  0,  0,  0 }
        });
        //@formatter:on

        final Image result = Filters.linearFilter(image, kernel, 2, 3, Extrapolation.REFLECT);

        // Check values.
        /*
         * A   0 1 2 3 4   B
         *   +-----------+
         * 0 | 3 3 3 3 3 |
         * 1 | 3 3 3 3 3 |
         * 2 | 3 3 3 3 3 |
         * 3 | 3 3 3 3 3 |
         * 4 | 3 3 3 8 3 |
         *   +-----------+
         * D               C
         */
        ImageTest.printImage(result, "Image after linear filter applying:");

        // Check values.
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                if (x < 2 && y < 2) {
                    assertEquals(8, result.get(x, y, 0));
                } else {
                    assertEquals(3, result.get(x, y, 0));
                }
            }
        }
    }

    /**
     * Test method for: {@link Filters#getGaussianKernel(int, double)}.
     */
    @Test
    public void testGaussKernel() {
        /*
         * Use values from: http://www.embege.com/gauss/
         */
        //@formatter:off
        final double[] kernelTest = new double[]{ 
                0.054_488_684_549_644_33, 
                0.244_201_342_003_233_46,
                0.402_619_946_894_244_35, 
                0.244_201_342_003_233_46, 
                0.054_488_684_549_644_33
            };
        //@formatter:on

        final Matrix kernel = Filters.getGaussianKernel(5, 1.0);

        assertEquals(kernelTest.length, kernel.getRowDimension());
        assertEquals(1, kernel.getColumnDimension());

        double sumTest = 0.0;
        double sum = 0.0;
        for (int i = 0; i < kernelTest.length; ++i) {
            sumTest += kernelTest[i];
            sum += kernel.get(i, 0);
        }
        assertEquals(1.0, sumTest, JCV.PRECISION);
        assertEquals(1.0, sum, JCV.PRECISION);

        // Check values.
        for (int i = 0; i < kernelTest.length; ++i) {
            assertEquals(kernelTest[i], kernel.get(i, 0), JCV.PRECISION);
        }
    }

    /**
     * Test method for: {@link Filters#getGaussianKernel(int, double)}.
     */
    @Test
    public void testGaussKernelException() {
        // Incorrect kernel size (more than 0).
        try {
            Filters.getGaussianKernel(0, 1.0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect kernel size (should be odd: 1, 3, 5, ...).
        try {
            Filters.getGaussianKernel(2, 1.0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Filters#getSigma(int)}, {@link Filters#getKernelSize(double)}.
     */
    @Test
    public void testSigmaAndKernelSize() {
        final double sigma1 = 1.5;
        final int kernelSize1 = 9;

        assertEquals(kernelSize1, Filters.getKernelSize(sigma1));
        assertEquals(sigma1, Filters.getSigma(kernelSize1), JCV.PRECISION);

        final double sigma2 = 1.0;
        final int kernelSize2 = 6;

        assertEquals(kernelSize2 + 1, Filters.getKernelSize(sigma2), JCV.PRECISION);
        assertEquals(sigma2, Filters.getSigma(kernelSize2), JCV.PRECISION);
    }
}
