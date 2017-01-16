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
package org.jcvlib.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test main image classes {@link Extrapolation}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ExtrapolationTest {

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
     * Test method for: {@link Image#get(int, int, int, Extrapolation)} and {@link Extrapolation#ZERO}.
     */
    @Test
    public void testZero() {
        // A
        Assert.assertEquals(0, this.image.get(-1, -1, 0, Extrapolation.ZERO));
        Assert.assertEquals(0, this.image.get(-2, -2, 0, Extrapolation.ZERO));
        // B
        Assert.assertEquals(0, this.image.get(5, -1, 0, Extrapolation.ZERO));
        Assert.assertEquals(0, this.image.get(6, -2, 0, Extrapolation.ZERO));
        // C
        Assert.assertEquals(0, this.image.get(5, 3, 0, Extrapolation.ZERO));
        Assert.assertEquals(0, this.image.get(6, 4, 0, Extrapolation.ZERO));
        // D
        Assert.assertEquals(0, this.image.get(-1, 3, 0, Extrapolation.ZERO));
        Assert.assertEquals(0, this.image.get(-2, 4, 0, Extrapolation.ZERO));

        // A-B
        Assert.assertEquals(0, this.image.get(2, -1, 0, Extrapolation.ZERO));
        Assert.assertEquals(0, this.image.get(2, -2, 0, Extrapolation.ZERO));
        // B-C
        Assert.assertEquals(0, this.image.get(5, 1, 0, Extrapolation.ZERO));
        Assert.assertEquals(0, this.image.get(6, 1, 0, Extrapolation.ZERO));
        // C-D
        Assert.assertEquals(0, this.image.get(2, 3, 0, Extrapolation.ZERO));
        Assert.assertEquals(0, this.image.get(2, 4, 0, Extrapolation.ZERO));
        // D-A
        Assert.assertEquals(0, this.image.get(-1, 1, 0, Extrapolation.ZERO));
        Assert.assertEquals(0, this.image.get(-2, 1, 0, Extrapolation.ZERO));

        // Center
        Assert.assertEquals(8, this.image.get(2, 1, 0, Extrapolation.ZERO));
    }

    /**
     * Test method for: {@link Image#get(int, int, int, Extrapolation)} and {@link Extrapolation#REPLICATE}.
     */
    @Test
    public void testReplicate() {
        // A
        Assert.assertEquals(1, this.image.get(-1, -1, 0, Extrapolation.REPLICATE));
        Assert.assertEquals(1, this.image.get(-2, -2, 0, Extrapolation.REPLICATE));
        // B
        Assert.assertEquals(5, this.image.get(5, -1, 0, Extrapolation.REPLICATE));
        Assert.assertEquals(5, this.image.get(6, -2, 0, Extrapolation.REPLICATE));
        // C
        Assert.assertEquals(15, this.image.get(5, 3, 0, Extrapolation.REPLICATE));
        Assert.assertEquals(15, this.image.get(6, 4, 0, Extrapolation.REPLICATE));
        // D
        Assert.assertEquals(11, this.image.get(-1, 3, 0, Extrapolation.REPLICATE));
        Assert.assertEquals(11, this.image.get(-2, 4, 0, Extrapolation.REPLICATE));

        // A-B
        Assert.assertEquals(3, this.image.get(2, -1, 0, Extrapolation.REPLICATE));
        Assert.assertEquals(3, this.image.get(2, -2, 0, Extrapolation.REPLICATE));
        // B-C
        Assert.assertEquals(10, this.image.get(5, 1, 0, Extrapolation.REPLICATE));
        Assert.assertEquals(10, this.image.get(6, 1, 0, Extrapolation.REPLICATE));
        // C-D
        Assert.assertEquals(13, this.image.get(2, 3, 0, Extrapolation.REPLICATE));
        Assert.assertEquals(13, this.image.get(2, 4, 0, Extrapolation.REPLICATE));
        // D-A
        Assert.assertEquals(6, this.image.get(-1, 1, 0, Extrapolation.REPLICATE));
        Assert.assertEquals(6, this.image.get(-2, 1, 0, Extrapolation.REPLICATE));

        // Center
        Assert.assertEquals(8, this.image.get(2, 1, 0, Extrapolation.REFLECT));
    }

    /**
     * Test method for: {@link Image#get(int, int, int, Extrapolation)} and {@link Extrapolation#REFLECT}.
     */
    @Test
    public void testReflect() {
        // A
        Assert.assertEquals(1, this.image.get(-1, -1, 0, Extrapolation.REFLECT));
        Assert.assertEquals(7, this.image.get(-2, -2, 0, Extrapolation.REFLECT));
        // B
        Assert.assertEquals(5, this.image.get(5, -1, 0, Extrapolation.REFLECT));
        Assert.assertEquals(9, this.image.get(6, -2, 0, Extrapolation.REFLECT));
        // C
        Assert.assertEquals(15, this.image.get(5, 3, 0, Extrapolation.REFLECT));
        Assert.assertEquals(9, this.image.get(6, 4, 0, Extrapolation.REFLECT));
        // D
        Assert.assertEquals(11, this.image.get(-1, 3, 0, Extrapolation.REFLECT));
        Assert.assertEquals(7, this.image.get(-2, 4, 0, Extrapolation.REFLECT));

        // A-B
        Assert.assertEquals(3, this.image.get(2, -1, 0, Extrapolation.REFLECT));
        Assert.assertEquals(8, this.image.get(2, -2, 0, Extrapolation.REFLECT));
        // B-C
        Assert.assertEquals(10, this.image.get(5, 1, 0, Extrapolation.REFLECT));
        Assert.assertEquals(9, this.image.get(6, 1, 0, Extrapolation.REFLECT));
        // C-D
        Assert.assertEquals(13, this.image.get(2, 3, 0, Extrapolation.REFLECT));
        Assert.assertEquals(8, this.image.get(2, 4, 0, Extrapolation.REFLECT));
        // D-A
        Assert.assertEquals(6, this.image.get(-1, 1, 0, Extrapolation.REFLECT));
        Assert.assertEquals(7, this.image.get(-2, 1, 0, Extrapolation.REFLECT));

        // Center
        Assert.assertEquals(8, this.image.get(2, 1, 0, Extrapolation.REFLECT));
    }

    /**
     * Test method for: {@link Image#get(int, int, int, Extrapolation)} and {@link Extrapolation#WRAP}.
     */
    @Test
    public void testWrap() {
        // A
        Assert.assertEquals(15, this.image.get(-1, -1, 0, Extrapolation.WRAP));
        Assert.assertEquals(9, this.image.get(-2, -2, 0, Extrapolation.WRAP));
        // B
        Assert.assertEquals(11, this.image.get(5, -1, 0, Extrapolation.WRAP));
        Assert.assertEquals(7, this.image.get(6, -2, 0, Extrapolation.WRAP));
        // C
        Assert.assertEquals(1, this.image.get(5, 3, 0, Extrapolation.WRAP));
        Assert.assertEquals(7, this.image.get(6, 4, 0, Extrapolation.WRAP));
        // D
        Assert.assertEquals(5, this.image.get(-1, 3, 0, Extrapolation.WRAP));
        Assert.assertEquals(9, this.image.get(-2, 4, 0, Extrapolation.WRAP));

        // A-B
        Assert.assertEquals(13, this.image.get(2, -1, 0, Extrapolation.WRAP));
        Assert.assertEquals(8, this.image.get(2, -2, 0, Extrapolation.WRAP));
        // B-C
        Assert.assertEquals(6, this.image.get(5, 1, 0, Extrapolation.WRAP));
        Assert.assertEquals(7, this.image.get(6, 1, 0, Extrapolation.WRAP));
        // C-D
        Assert.assertEquals(3, this.image.get(2, 3, 0, Extrapolation.WRAP));
        Assert.assertEquals(8, this.image.get(2, 4, 0, Extrapolation.WRAP));
        // D-A
        Assert.assertEquals(10, this.image.get(-1, 1, 0, Extrapolation.WRAP));
        Assert.assertEquals(9, this.image.get(-2, 1, 0, Extrapolation.WRAP));

        // Center
        Assert.assertEquals(8, this.image.get(2, 1, 0, Extrapolation.WRAP));
    }
}
