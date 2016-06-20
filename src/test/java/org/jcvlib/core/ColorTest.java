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
package org.jcvlib.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for colors {@link Color}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ColorTest {

    /**
     * Test method for: {@link Color}.
     */
    @Test
    public void testCreate() {
        new Color(1);

        final int vlaue = 127;
        final Color color1 = new Color(3, vlaue);
        assertEquals(vlaue, color1.get(0), JCV.PRECISION);
        assertEquals(vlaue, color1.get(1), JCV.PRECISION);
        assertEquals(vlaue, color1.get(2), JCV.PRECISION);

        final int[] c = new int[]{ 32, 64, 128 };
        final Color color2 = new Color(c);
        assertEquals(c[0], color2.get(0), JCV.PRECISION);
        assertEquals(c[1], color2.get(1), JCV.PRECISION);
        assertEquals(c[2], color2.get(2), JCV.PRECISION);
    }

    /**
     * Test method for: {@link Color}.
     */
    @Test
    public void testCreateException() {
        // Incorrect number of Channels.
        try {
            new Color(0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect number of Channels.
        try {
            new Color(-1);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect source value.
        try {
            new Color(new int[]{});
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Color#toString()}.
     */
    @Test
    public void testToString() {
        final Color color = new Color(new int[]{ 10, 20, 30 });

        System.out.println("Printing example:");
        System.out.println(color.toString());
    }

    /**
     * Test method for: {@link Color#set(int, int)}, {@link Color#get(int)}.
     */
    @Test
    public void testSetGet() {
        final Color color = new Color(3);

        color.set(0, Color.MIN_VALUE + 0);
        color.set(1, Color.MIN_VALUE + 1);
        color.set(2, Color.MIN_VALUE + 2);

        assertEquals(Color.MIN_VALUE + 0, color.get(0));
        assertEquals(Color.MIN_VALUE + 1, color.get(1));
        assertEquals(Color.MIN_VALUE + 2, color.get(2));
    }

    /**
     * Test method for: {@link Color#set(int, int)}, {@link Color#get(int)}.
     */
    @Test
    public void testSetRound() {
        final Color color = new Color(2);

        color.set(0, Color.MAX_VALUE + 1);
        color.set(1, Color.MIN_VALUE - 1);

        assertEquals(Color.MAX_VALUE, color.get(0));
        assertEquals(Color.MIN_VALUE, color.get(1));
    }

    /**
     * Test method for: {@link Color#set(int, int)}, {@link Color#get(int)}.
     */
    @Test
    public void testGetSetException() {
        final Color color = new Color(5);

        // Set in incorrect position.
        try {
            color.set(5, Color.MIN_VALUE);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            color.set(-1, Color.MIN_VALUE);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Color#makeCopy()}, {@link Color#equals(Object)}.
     */
    @Test
    public void testCopyEquals() {
        final Color color1 = new Color(2, 32);
        assertTrue(color1.equals(color1));
        assertTrue(color1.equals(new Color(2, 32)));
        assertFalse(color1.equals(1));
        assertFalse(color1.equals(null));

        final Color color2 = color1.makeCopy();
        assertTrue(color1.equals(color2));

        color2.set(1, 64);
        assertFalse(color1.equals(color2));

        final Color color3 = new Color(3, 1);
        assertFalse(color1.equals(color3));
    }

    /**
     * Test method for: {@link Color#euclidDist(Color)}.
     */
    @Test
    public void testEuclidDist() {
        assertEquals(1.0, (new Color(new int[]{ 5, 5 })).euclidDist(new Color(new int[]{ 4, 6 })), JCV.PRECISION);

        final Color min = new Color(new int[]{ Color.MIN_VALUE, Color.MIN_VALUE });
        final Color max = new Color(new int[]{ Color.MAX_VALUE, Color.MAX_VALUE });
        assertEquals(Color.MAX_VALUE, min.euclidDist(max), JCV.PRECISION);
    }

    /**
     * Test method for: {@link Color#euclidDist(Color)}.
     */
    @Test
    public void testEuclidDistException() {
        try {
            (new Color(new int[]{ 1, 1 })).euclidDist(new Color(new int[]{ 4, 6, 8 }));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }
}