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
 * Test class for points {@link Size}.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class SizeTest {

    /**
     * Test method for: {@link Size}.
     */
    @Test
    public void testCerateException() {
        new Size(1, 1);

        // Incorrect Height.
        try {
            new Size(0, 1);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect Width.
        try {
            new Size(1, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Size#toString()}.
     */
    @Test
    public void testToString() {
        final Size size = new Size(1, 2);

        System.out.println("Printing example:");
        System.out.println(size.toString());
    }

    /**
     * Test method for: {@link Size#getWidth()}, {@link Size#getHeight()}.
     */
    @Test
    public void testWidthHeight() {
        final Size size = new Size(1, 2);

        assertEquals(1, size.getWidth());
        assertEquals(2, size.getHeight());
    }

    /**
     * Test method for: {@link Size#calculateN()}.
     */
    @Test
    public void testCalculateN() {
        assertEquals(6, (new Size(3, 2)).calculateN());
    }

    /**
     * Test method for: {@link Size#lessOrEqualsThan(Size)}.
     */
    @Test
    public void testLessOrEquals() {
        final Size size1 = new Size(6, 7);
        assertTrue(size1.lessOrEqualsThan(size1));

        final Size size2 = new Size(5, 5);
        assertTrue(size2.lessOrEqualsThan(size1));

        final Size size3 = new Size(6, 8);
        assertFalse(size3.lessOrEqualsThan(size1));

        final Size size4 = new Size(7, 7);
        assertFalse(size4.lessOrEqualsThan(size1));
    }

    /**
     * Test method for: {@link Size#lessOrEqualsThan(Size)}.
     */
    @Test
    public void testLessOrEqualsException() {
        // Incorrect source object.
        try {
            (new Size(3, 2)).lessOrEqualsThan(null);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Size#equals(Object)}.
     */
    @Test
    public void testEquals() {
        final Size size1 = new Size(1, 2);
        assertTrue(size1.equals(size1));
        assertTrue(size1.equals(new Size(1, 2)));
        assertFalse(size1.equals(null));
        assertFalse(size1.equals(0));

        final Size size2 = new Size(2, 2);
        assertFalse(size1.equals(size2));

        final Size size3 = new Size(1, 3);
        assertFalse(size1.equals(size3));
    }
}
