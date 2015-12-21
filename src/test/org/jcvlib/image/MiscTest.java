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
package org.jcvlib.image;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.ImageTest;
import org.jcvlib.core.Point;
import org.jcvlib.core.Region;
import org.junit.Test;

import Jama.Matrix;

/**
 * Test {@link Misc}.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class MiscTest {

    /**
     * Test method for: {@link Misc#floodFill(Image, Point, double, Color, Direction, FloodFillRange)}.
     */
    @Test
    public void testFloodFill() {
        /*
         * Ininitialize the image.
         */
        /*
         * +----------------+
         * |  0  1  2  3  4 |
         * |  5  2  7  8  3 |
         * | 10 11  3 13 14 |
         * +----------------+
         */
        final Image image = new Image(5, 3, 1);
        int color = 0;
        // Do not change the loop order!
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                image.set(x, y, 0, color);
                ++color;
            }
        }
        image.set(1, 1, 0, 2);
        image.set(2, 2, 0, 3);
        image.set(4, 0, 0, 5);
        image.set(4, 1, 0, 3);

        ImageTest.printImage(image, "Before:");

        /*
         * Test 1.
         */
        final Color fillColor1 = new Color(1, Color.MAX_VALUE);
        final Image result1 = Misc.floodFill(image.makeCopy(), new Point(0, 0), 3.0, fillColor1, Direction.TYPE_4,
                FloodFillRange.FIXED);
        ImageTest.printImage(result1, "After:");
        final Region region1 = new Region(result1, fillColor1);
        assertEquals(5, region1.getAreaSize());

        /*
         * Test 2.
         */
        final Color fillColor2 = new Color(1, Color.MAX_VALUE);
        final Image result2 = Misc.floodFill(image.makeCopy(), new Point(0, 0), 3.0, fillColor2, Direction.TYPE_8,
                FloodFillRange.FIXED);
        final Region region2 = new Region(result2, fillColor2);
        assertEquals(7, region2.getAreaSize());

        /*
         * Test 3.
         */
        final Color fillColor3 = new Color(1, Color.MAX_VALUE);
        final Image result3 = Misc.floodFill(image.makeCopy(), new Point(0, 0), 1.0, fillColor3, Direction.TYPE_4,
                FloodFillRange.NEIGHBOR);
        final Region region3 = new Region(result3, fillColor3);
        assertEquals(5, region3.getAreaSize());

        /*
         * Test 4.
         */
        final Color fillColor4 = new Color(1, Color.MAX_VALUE);
        final Image result4 = Misc.floodFill(image.makeCopy(), new Point(0, 0), 1.0, fillColor4, Direction.TYPE_8,
                FloodFillRange.NEIGHBOR);
        final Region region4 = new Region(result4, fillColor4);
        assertEquals(7, region4.getAreaSize());
    }

    /**
     * Test method for: {@link Misc#buildPyramidUp(Image)}, {@link Misc#buildPyramidDown(Image)}.
     */
    @Test
    public void testPyramid() {
        /*
         * +-------------+
         * |  0  1  2  3 |
         * |  4  5  6  7 |
         * |  8  9 10 11 |
         * | 12 13 14 15 |
         * +-------------+
         */
        final Image image = new Image(4, 4, 1);
        int val = Color.MIN_VALUE;
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                image.set(x, y, 0, val);

                ++val;
                if (val > Color.MAX_VALUE) {
                    val = Color.MIN_VALUE;
                }
            }
        }

        /*
         * +-------------------------+
         * |  0  0  1  1  2  2  3  3 |
         * |  0  0  1  1  2  2  3  3 |
         * |  4  4  5  5  6  6  7  7 |
         * |  4  4  5  5  6  6  7  7 |
         * |  8  8  9  9 10 10 11 11 |
         * |  8  8  9  9 10 10 11 11 |
         * | 12 12 13 13 14 14 15 15 |
         * | 12 12 13 13 14 14 15 15 |
         * +-------------------------+
         */
        final Image imageTwo = Misc.buildPyramidUp(image);

        //@formatter:off
        assertEquals( 8, imageTwo.getWidth());
        assertEquals( 8, imageTwo.getHeight());
        assertEquals( 1, imageTwo.getNumOfChannels());

        // Horizontal.
        assertEquals( 0, imageTwo.get(0, 0, 0));
        assertEquals( 0, imageTwo.get(1, 0, 0));
        assertEquals( 1, imageTwo.get(2, 0, 0));
        assertEquals( 1, imageTwo.get(3, 0, 0));
        assertEquals( 2, imageTwo.get(4, 0, 0));
        assertEquals( 2, imageTwo.get(5, 0, 0));
        assertEquals( 3, imageTwo.get(6, 0, 0));
        assertEquals( 3, imageTwo.get(7, 0, 0));

        // Vertical.
        assertEquals( 0, imageTwo.get(0, 0, 0));
        assertEquals( 0, imageTwo.get(0, 1, 0));
        assertEquals( 4, imageTwo.get(0, 2, 0));
        assertEquals( 4, imageTwo.get(0, 3, 0));
        assertEquals( 8, imageTwo.get(0, 4, 0));
        assertEquals( 8, imageTwo.get(0, 5, 0));
        assertEquals(12, imageTwo.get(0, 6, 0));
        assertEquals(12, imageTwo.get(0, 7, 0));

        // Diagonal.
        assertEquals( 0, imageTwo.get(0, 0, 0));
        assertEquals( 0, imageTwo.get(1, 1, 0));
        assertEquals( 5, imageTwo.get(2, 2, 0));
        assertEquals( 5, imageTwo.get(3, 3, 0));
        assertEquals(10, imageTwo.get(4, 4, 0));
        assertEquals(10, imageTwo.get(5, 5, 0));
        assertEquals(15, imageTwo.get(6, 6, 0));
        assertEquals(15, imageTwo.get(7, 7, 0));
        //@formatter:on

        /*
         * +-------+
         * |  0  2 |
         * |  8 10 |
         * +-------+
         *     |
         *     |
         * [Gaussian Blur]
         *     |
         *    \|/
         *     +
         * +-------+
         * |  1  3 |
         * |  8 10 |
         * +-------+
         */
        final Image imageHalf = Misc.buildPyramidDown(image);

        //@formatter:off
        assertEquals( 2, imageHalf.getWidth());
        assertEquals( 2, imageHalf.getHeight());
        assertEquals( 1, imageHalf.getNumOfChannels());

        assertEquals( 1, imageHalf.get(0, 0, 0));
        assertEquals( 3, imageHalf.get(1, 0, 0));
        assertEquals( 8, imageHalf.get(0, 1, 0));
        assertEquals(10, imageHalf.get(1, 1, 0));
        //@formatter:on
    }

    /**
     * Test method for: {@link Misc#sumArea(Image)}.
     */
    @Test
    public void testIntergralImage() {
        final Image image = new Image(1500, 1200, 3);
        image.fill(new Color(3, Color.MAX_VALUE));

        final List<Matrix> sumAreas = Misc.sumArea(image);

        // Check values.
        for (int channel = 0; channel < sumAreas.size(); ++channel) {
            for (int x = 0; x < image.getWidth(); ++x) {
                for (int y = 0; y < image.getHeight(); ++y) {
                    assertEquals((x + 1) * (y + 1) * Color.MAX_VALUE, sumAreas.get(channel).get(y, x), 1.0);
                }
            }
        }
    }
}
