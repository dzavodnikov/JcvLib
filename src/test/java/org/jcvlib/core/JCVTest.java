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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import Jama.Matrix;

/**
 * Test class for points {@link JCV}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class JCVTest {

    @Test
    public void testJCV() {
        new JCV();
    }

    /**
     * Test method for: {@link JCV#getOS()}, {@link JCV#isLinux()}, {@link JCV#isWindows()}.
     */
    @Test
    public void testOS() {
        final String key = "os.name";

        final String valueLinux = "Linux";
        final String valueWindows = "Windows 2000";
        final String valueMac = "Mac OS X";

        // Save original value.
        final String value = System.getProperty(key);

        // Linux.
        System.setProperty(key, valueLinux);
        assertEquals(JCV.OS_LINUX, JCV.getOS());
        assertTrue(JCV.isLinux());
        assertFalse(JCV.isWindows());

        // Windows.
        System.setProperty(key, valueWindows);
        assertEquals(JCV.OS_WINDOWS, JCV.getOS());
        assertFalse(JCV.isLinux());
        assertTrue(JCV.isWindows());

        // MacOS.
        System.setProperty(key, valueMac);
        assertEquals(JCV.OS_UNSUPPORTED, JCV.getOS());
        assertFalse(JCV.isLinux());
        assertFalse(JCV.isWindows());

        // Restore original value.
        System.setProperty(key, value);
    }

    /**
     * Test method for: {@link JCV#getArch()}.
     */
    @Test
    public void testArch() {
        final String key = "os.arch";

        final String value32_1 = "x86";
        final String value32_2 = "i386";
        final String value64 = "x86_64";
        final String valueARM = "arm";

        // Save original value.
        final String value = System.getProperty(key);

        // x32.
        // 1.
        System.setProperty(key, value32_1);
        assertEquals(JCV.ARCH_32, JCV.getArch());
        // 2.
        System.setProperty(key, value32_2);
        assertEquals(JCV.ARCH_32, JCV.getArch());

        // x64.
        System.setProperty(key, value64);
        assertEquals(JCV.ARCH_64, JCV.getArch());

        // ARM.
        System.setProperty(key, valueARM);
        assertEquals(JCV.ARCH_UNSUPPORTED, JCV.getArch());
        assertFalse(JCV.isLinux());
        assertFalse(JCV.isWindows());

        // Restore original value.
        System.setProperty(key, value);
    }

    /**
     * Test method for: {@link JCV#equalValues(double, double)}, {@link JCV#equalValues(double, double, double)}.
     */
    @Test
    public void testEqualValues() {
        assertTrue(JCV.equalValues(JCV.PRECISION, JCV.PRECISION));
        assertFalse(JCV.equalValues(JCV.PRECISION * 10, JCV.PRECISION));
    }

    /**
     * Test method for: {@link JCV#round(double)}.
     */
    @Test
    public void testRound() {
        assertEquals(1, JCV.round(1.0));
        assertEquals(1, JCV.round(1.1));
        assertEquals(2, JCV.round(1.5));
        assertEquals(2, JCV.round(1.9));
        assertEquals(2, JCV.round(2.0));

        assertEquals(0, JCV.round(Color.MIN_VALUE));
        assertEquals(255, JCV.round(Color.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, JCV.round(Double.NEGATIVE_INFINITY));
        assertEquals(0, JCV.round(Double.NaN));
        assertEquals(Integer.MAX_VALUE, JCV.round(Double.POSITIVE_INFINITY));
    }

    /**
     * Test method for: {@link JCV#roundUp(double)}.
     */
    @Test
    public void testRoundUp() {
        assertEquals(1, JCV.roundUp(1.0));
        assertEquals(2, JCV.roundUp(1.1));
        assertEquals(2, JCV.roundUp(1.5));
        assertEquals(2, JCV.roundUp(1.9));
        assertEquals(2, JCV.roundUp(2.0));

        assertEquals(0, JCV.roundUp(Color.MIN_VALUE));
        assertEquals(255, JCV.roundUp(Color.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, JCV.roundUp(Double.NEGATIVE_INFINITY));
        assertEquals(0, JCV.roundUp(Double.NaN));
        assertEquals(Integer.MAX_VALUE, JCV.roundUp(Double.POSITIVE_INFINITY));
    }

    /**
     * Test method for: {@link JCV#roundDown(double)}.
     */
    @Test
    public void testRoundDown() {
        assertEquals(1, JCV.roundDown(1.0));
        assertEquals(1, JCV.roundDown(1.1));
        assertEquals(1, JCV.roundDown(1.5));
        assertEquals(1, JCV.roundDown(1.9));
        assertEquals(2, JCV.roundDown(2.0));

        assertEquals(0, JCV.roundDown(Color.MIN_VALUE));
        assertEquals(255, JCV.roundDown(Color.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, JCV.roundDown(Double.NEGATIVE_INFINITY));
        assertEquals(0, JCV.roundDown(Double.NaN));
        assertEquals(Integer.MAX_VALUE, JCV.roundDown(Double.POSITIVE_INFINITY));
    }

    /**
     * Test method for: {@link JCV#calculateCenter(int, int)}.
     */
    @Test
    public void testGetCenter() {
        assertTrue(JCV.calculateCenter(1, 1).equals(new Point(0, 0)));
        assertTrue(JCV.calculateCenter(2, 2).equals(new Point(0, 0)));
        assertTrue(JCV.calculateCenter(3, 3).equals(new Point(1, 1)));
        assertTrue(JCV.calculateCenter(3, 2).equals(new Point(1, 0)));
    }

    /**
     * Test method for: {@link JCV#verifyIsNotNull(Object)}.
     */
    @Test
    public void testVerifyIsNotNull() {
        JCV.verifyIsNotNull(1);

        // Incorrect source object.
        try {
            JCV.verifyIsNotNull(null);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link JCV#verifyIsSameSize(Image, Image)}.
     */
    @Test
    public void testVerifySameSizeImage() {
        final Image image1 = new Image(100, 100, 4);
        final Image image2 = new Image(100, 100, 6);
        final Image image3 = new Image(100, 101, 5);
        final Image image4 = new Image(101, 100, 6);

        JCV.verifyIsSameSize(image1, image2);

        try {
            JCV.verifyIsSameSize(image1, image3);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        try {
            JCV.verifyIsSameSize(image1, image4);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link JCV#verifyIsSameChannels(Image, Image)}.
     */
    @Test
    public void testVerifySameChannels() {
        final Image image1 = new Image(100, 100, 4);
        final Image image2 = new Image(100, 100, 4);
        final Image image3 = new Image(100, 100, 5);
        final Image image4 = new Image(100, 100, 3);

        JCV.verifyIsSameChannels(image1, image2);

        try {
            JCV.verifyIsSameChannels(image1, image3);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        try {
            JCV.verifyIsSameChannels(image1, image4);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link JCV#verifyNumOfChannels(Image, int)}.
     */
    @Test
    public void testVerifyNumOfChannels() {
        JCV.verifyNumOfChannels(new Image(100, 100, 4), 4);

        try {
            JCV.verifyNumOfChannels(new Image(100, 100, 4), 5);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        try {
            JCV.verifyNumOfChannels(new Image(100, 100, 4), -5);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link JCV#verifyIsSameSize(Matrix, Matrix)}.
     */
    @Test
    public void testVerifySameSizeMatrix() {
        final Matrix matrix1 = new Matrix(100, 100);
        final Matrix matrix2 = new Matrix(100, 100);
        final Matrix matrix3 = new Matrix(100, 101);
        final Matrix matrix4 = new Matrix(101, 100);

        JCV.verifyIsSameSize(matrix1, matrix2);

        try {
            JCV.verifyIsSameSize(matrix1, matrix3);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        try {
            JCV.verifyIsSameSize(matrix1, matrix4);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link JCV#verifyOddSize(int)}.
     */
    @Test
    public void testGetOddSize() {
        JCV.verifyOddSize(1);
        JCV.verifyOddSize(3);
        JCV.verifyOddSize(5);

        // Incorrect value.
        try {
            JCV.verifyOddSize(2);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }
}
