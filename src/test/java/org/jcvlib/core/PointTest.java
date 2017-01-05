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
package org.jcvlib.core;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for points {@link Point}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class PointTest {

    /**
     * Test method for: {@link Point}.
     */
    @Test
    public void testCerate() {
        new Point(0, 0);
    }

    /**
     * Test method for: {@link Point}.
     */
    @Test
    public void testCerateException() {
        // Incorrect Y position.
        try {
            new Point(-1, 0);
            Assert.fail("Not thrown IllegalArgumentException!");
        } catch (final IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect X position.
        try {
            new Point(0, -1);
            Assert.fail("Not thrown IllegalArgumentException!");
        } catch (final IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Point#getX()}, {@link Point#getY()}.
     */
    @Test
    public void testXY() {
        final Point point = new Point(1, 2);

        Assert.assertEquals(1, point.getX());
        Assert.assertEquals(2, point.getY());
    }

    /**
     * Test method for: {@link Point#toString()}.
     */
    @Test
    public void testToString() {
        final Point point = new Point(1, 2);

        System.out.println("Printing example:");
        System.out.println(point.toString());
    }

    /**
     * Test method for: {@link Point#equals(Object)}.
     */
    @Test
    public void testEquals() {
        final Point point1 = new Point(1, 2);
        Assert.assertTrue(point1.equals(point1));
        Assert.assertTrue(point1.equals(new Point(1, 2)));
        Assert.assertFalse(point1.equals(null));
        Assert.assertFalse(point1.equals(0));

        final Point point2 = new Point(2, 2);
        Assert.assertFalse(point1.equals(point2));

        final Point point3 = new Point(1, 3);
        Assert.assertFalse(point1.equals(point3));
    }
}
