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
package org.jcvlib.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test main image classes {@link Interpolation}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class InterpolationTest {

    private Image image;

    /**
     * Executes before creating instance of the class.
     */
    @Before
    public void setUp() {
        this.image = new Image(5, 3, 1);

        /*
         * A    0  1  2  3  4   B
         *   +----------------+
         * 0 |  1  2  3  4  5 |
         * 1 |  6  7  8  9 10 |
         * 2 | 11 12 13 14 15 |
         *   +----------------+
         * D                    C
         */
        int color = 1;
        // Do not change the loop order!
        for (int y = 0; y < this.image.getHeight(); ++y) {
            for (int x = 0; x < this.image.getWidth(); ++x) {
                this.image.set(x, y, 0, color);

                ++color;
            }
        }
    }

    /**
     * Test method for: {@link Image#get(double, double, int, Interpolation)} and {@link Interpolation#NEAREST_NEIGHBOR}
     * .
     */
    @Test
    public void testNearest() {
        assertEquals(7.0, this.image.get(1.1, 1.1, 0, Interpolation.NEAREST_NEIGHBOR), JCV.PRECISION);
    }

    /**
     * Test method for: {@link Image#get(double, double, int, Interpolation)} and {@link Interpolation#BILINEAR}.
     */
    @Test
    public void testBilinear() {
        assertEquals(7.0, this.image.get(1.0, 1.0, 0, Interpolation.BILINEAR), JCV.PRECISION);
        assertEquals(8.0, this.image.get(2.0, 1.0, 0, Interpolation.BILINEAR), JCV.PRECISION);
        assertEquals(12.0, this.image.get(1.0, 2.0, 0, Interpolation.BILINEAR), JCV.PRECISION);
        assertEquals(13.0, this.image.get(2.0, 2.0, 0, Interpolation.BILINEAR), JCV.PRECISION);

        assertEquals(7.5, this.image.get(1.5, 1.0, 0, Interpolation.BILINEAR), JCV.PRECISION);
        assertEquals(7.4, this.image.get(1.4, 1.0, 0, Interpolation.BILINEAR), JCV.PRECISION);
        assertEquals(9.5, this.image.get(1.0, 1.5, 0, Interpolation.BILINEAR), JCV.PRECISION);
        assertEquals(9.0, this.image.get(1.0, 1.4, 0, Interpolation.BILINEAR), JCV.PRECISION);
        assertEquals(10.0, this.image.get(1.5, 1.5, 0, Interpolation.BILINEAR), JCV.PRECISION);
    }

    /**
     * Test method for: {@link Image#get(double, double, int, Interpolation)} and {@link Interpolation#BICUBIC}.
     */
    @Test
    public void testBicubic() {
        assertEquals(7.5000, this.image.get(1.5, 1.0, 0, Interpolation.BICUBIC), JCV.PRECISION);
        assertEquals(10.3125, this.image.get(1.5, 1.5, 0, Interpolation.BICUBIC), JCV.PRECISION);
    }
}
