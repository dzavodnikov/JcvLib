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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.image.geometry.Geometry;
import org.jcvlib.image.geometry.Reflection;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link Geometry}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class GeomTest {

    private Image image;

    /**
     * Executes before creating instance of the class.
     */
    @Before
    public void setUp() throws Exception {
        this.image = new Image(150, 120, 3);

        // Do not change the loop order!
        for (int channel = 0; channel < this.image.getNumOfChannels(); ++channel) {
            int val = Color.MIN_VALUE;
            for (int y = 0; y < this.image.getHeight(); ++y) {
                for (int x = 0; x < this.image.getWidth(); ++x) {
                    this.image.set(x, y, channel, val);

                    ++val;
                    if (val > Color.MAX_VALUE) {
                        val = Color.MIN_VALUE;
                    }
                }
            }
        }
    }

    /**
     * Test method for: {@link Geometry#reflect(Image, Reflection)}.
     */
    @Test
    public void testMirroringHorizontal() {
        final Image mirror = Geometry.reflect(this.image, Reflection.HORIZONTAL);

        assertTrue(this.image.getSize().equals(mirror.getSize()));

        // Check values.
        for (int x = 0; x < mirror.getWidth(); ++x) {
            for (int y = 0; y < mirror.getHeight(); ++y) {
                for (int channel = 0; channel < this.image.getNumOfChannels(); ++channel) {
                    int answer = this.image.get(mirror.getWidth() - x - 1, y, channel);
                    int result = mirror.get(x, y, channel);

                    assertEquals(answer, result);
                }
            }
        }
    }

    /**
     * Test method for: {@link Geometry#reflect(Image, Reflection)}.
     */
    @Test
    public void testMirroringVertical() {
        final Image mirror = Geometry.reflect(this.image, Reflection.VERTICAL);

        assertTrue(this.image.getSize().equals(mirror.getSize()));

        // Check values.
        for (int x = 0; x < mirror.getWidth(); ++x) {
            for (int y = 0; y < mirror.getHeight(); ++y) {
                for (int channel = 0; channel < this.image.getNumOfChannels(); ++channel) {
                    final int answer = this.image.get(x, mirror.getHeight() - y - 1, channel);
                    final int result = mirror.get(x, y, channel);

                    assertEquals(answer, result);
                }
            }
        }
    }

    /**
     * Test method for: {@link Geometry#reflect(Image, Reflection)}.
     */
    @Test
    public void testMirroringDiagonal() {
        final Image mirror = Geometry.reflect(this.image, Reflection.DIAGONAL);

        assertTrue(this.image.getSize().equals(mirror.getSize()));

        // Check values.
        for (int x = 0; x < mirror.getWidth(); ++x) {
            for (int y = 0; y < mirror.getHeight(); ++y) {
                for (int channel = 0; channel < this.image.getNumOfChannels(); ++channel) {
                    final int answer = this.image.get(mirror.getWidth() - x - 1, mirror.getHeight() - y - 1, channel);
                    final int result = mirror.get(x, y, channel);

                    assertEquals(answer, result);
                }
            }
        }
    }

    /**
     * Test method for: {@link Geometry#getPerspectiveTransfrom(List, List)}.
     */
    @Test
    public void testGetPerspectiveTransformException() {
        final List<Point> pointsIncorrect = new LinkedList<Point>();
        //@formatter:off
        pointsIncorrect.add(new Point(  0,   0));
        pointsIncorrect.add(new Point( 10,  10));
        pointsIncorrect.add(new Point( 20,  20));
        pointsIncorrect.add(new Point( 67, 207));
        //@formatter:on

        final List<Point> pointsCorrect = new LinkedList<Point>();
        //@formatter:off
        pointsCorrect.add(new Point( 49,  83));
        pointsCorrect.add(new Point(210,  66));
        pointsCorrect.add(new Point(238, 174));
        pointsCorrect.add(new Point( 67, 207));
        //@formatter:on

        try {
            Geometry.getPerspectiveTransfrom(pointsIncorrect, pointsCorrect);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }
}
