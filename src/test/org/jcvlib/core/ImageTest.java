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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jcvlib.parallel.Parallel;
import org.junit.Test;

import Jama.Matrix;
import junit.framework.TestSuite;

/**
 * Test main image class {@link Image}.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ImageTest extends TestSuite {

    /**
     * Print first channel of given image with given comment.
     */
    public static void printImage(Image image, String comment) {
        System.out.println(comment);
        // Do not change the loop order!
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                System.out.print(String.format("%3d ", image.get(x, y, 0)));
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Test method for: {@link Image}.
     */
    @Test
    public void testCreateException() {
        // Incorrect Channel value.
        try {
            new Image(300, 200, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            new Image(300, 200, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect Channel value.
        try {
            new Image(300, 200, -1);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            new Image(300, 200, -1);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#getWidth()}, {@link Image#getHeight()}, {@link Image#getNumOfChannels()},
     * {@link Image#getSize()}}.
     */
    @Test
    public void testSize() {
        final Image image = new Image(300, 200, 4);

        assertEquals(300, image.getWidth());
        assertEquals(200, image.getHeight());

        assertEquals(300, image.getSize().getWidth());
        assertEquals(200, image.getSize().getHeight());

        assertEquals(4, image.getNumOfChannels());
    }

    /**
     * Test method for: {@link Image#toString()}.
     */
    @Test
    public void testToString() {
        System.out.println("Printing example image:");
        System.out.println(new Image(300, 200, 4).toString());
    }

    /**
     * Test method for: {@link Image#get(int, int, int)}, {@link Image#set(int, int, int, int)}.
     */
    @Test
    public void testSetGet() {
        final Image image = new Image(800, 600, 4);
        int value;

        // Test set method.
        value = Color.MIN_VALUE;
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    image.set(x, y, channel, value);

                    // Next value.
                    ++value;
                    if (value > Color.MAX_VALUE) {
                        value = Color.MIN_VALUE;
                    }
                }
            }
        }

        // Test get method.
        value = Color.MIN_VALUE;
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    assertEquals(value, image.get(x, y, channel));

                    // Next value.
                    ++value;
                    if (value > Color.MAX_VALUE) {
                        value = Color.MIN_VALUE;
                    }
                }
            }
        }
    }

    /**
     * Test method for: {@link Image#set(int, int, int, int)}.
     */
    @Test
    public void testSetRound() {
        final Image image = new Image(800, 600, 4);

        image.set(0, 0, 0, Color.MIN_VALUE - 1);
        assertEquals(Color.MIN_VALUE, image.get(0, 0, 0));

        image.set(1, 1, 1, Color.MAX_VALUE + 1);
        assertEquals(Color.MAX_VALUE, image.get(1, 1, 1));
    }

    /**
     * Test method for internal methods for calculate positions.
     */
    @Test
    public void testPositionMethods() {
        final Image image = new Image(120, 90, 5);
        try {
            final Method calculateArrayPosition = Image.class.getDeclaredMethod("calculateArrayPosition", int.class,
                    int.class, int.class);
            calculateArrayPosition.setAccessible(true);

            final Method calculateImagePosition = Image.class.getDeclaredMethod("calculateImagePosition", int.class,
                    int[].class);
            calculateImagePosition.setAccessible(true);

            for (int x = 0; x < image.getWidth(); ++x) {
                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                        try {
                            final int pos = (int) calculateArrayPosition.invoke(image, x, y, channel);

                            final int[] xyc = new int[3];
                            calculateImagePosition.invoke(image, pos, xyc);

                            assertEquals(x, xyc[0]);
                            assertEquals(y, xyc[1]);
                            assertEquals(channel, xyc[2]);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for: {@link Image#get(Point, Color)}, {@link Image#set(Point, Color)}.
     */
    @Test
    public void testSetGetPointAndColor() {
        final int val1 = 32;
        final Image image = new Image(100, 100, 4);
        final Color resColor = new Color(image.getNumOfChannels());

        image.fill(new Color(4, val1));
        image.get(10, 10, resColor);
        assertTrue(resColor.equals(new Color(new int[]{ val1, val1, val1, val1 })));

        final int val2 = 64;
        final Color srcColor = new Color(new int[]{ val2, val2, val2, val2 });
        final Point pos = new Point(10, 10);
        image.set(pos, srcColor);

        image.get(pos, resColor);
        assertTrue(resColor.equals(srcColor));
    }

    /**
     * Test method for: {@link Image#get(int, int, int)}, {@link Image#set(int, int, int, int)}.
     */
    @Test
    public void testSetGetException() {
        final Image image = new Image(300, 200, 4);

        // Get on incorrect Height.
        try {
            image.get(300, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.get(-1, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Get on incorrect Width.
        try {
            image.get(0, 200, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.get(0, -1, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Set on incorrect Channel.
        try {
            image.get(0, 0, 4);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.get(0, 0, -1);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Set on incorrect Height.
        try {
            image.set(300, 0, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.set(-1, 0, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Set on incorrect Width.
        try {
            image.set(0, 200, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.set(0, -1, 0, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Set on incorrect Channel.
        try {
            image.set(0, 0, 4, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.set(0, 0, -1, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#noneLinearFilter(Image, int, int, Point, int, Extrapolation, KernelOperation)}.
     */
    @Test
    public void testNonlinearFilter1() {
        final Image image = new Image(5, 5, 1);
        image.fill(new Color(1, Color.MIN_VALUE));
        int counter;

        // Initialize test image.
        counter = 1;
        // Do not change the loop order!
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                image.set(x, y, 0, counter);
                ++counter;
            }
        }

        printImage(image, "Image before any filter applying:");

        /* Anchor in a kernel:
         *     0 1 2 3 4
         *   +-----------+
         * 0 | o o o o o |
         * 1 | o o o o o |
         * 2 | o o o x o |
         * 3 | o o o o o |
         *   +-----------+
         */
        final Point anchor = new Point(3, 2);

        final Image result = new Image(5, 5, 1);
        Parallel.setNumOfWorkers(1);
        image.noneLinearFilter(result, 5, 4, anchor, 3, Extrapolation.ZERO, new KernelOperation() {

            @Override
            public void execute(final Image aperture, final Color result) {
                // Nothing changing.
                aperture.get(anchor, result);
            }
        });

        printImage(result, "Image after nonlinear filter applying:");

        // Check values.
        counter = 1;
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                assertEquals(counter, result.get(x, y, 0));
                ++counter;
            }
        }
    }

    /**
     * Test method for: {@link Image#noneLinearFilter(Image, int, int, Point, int, Extrapolation, KernelOperation)}.
     */
    @Test
    public void testNonlinearFilter2() {
        final Image image = new Image(5, 5, 1);
        image.fill(new Color(1, Color.MIN_VALUE));
        /*
         * A   0 1 2 3 4   B
         *   +-----------+
         * 0 | 0 1 0 0 0 |
         * 1 | 0 0 0 0 0 |
         * 2 | 0 0 0 0 0 |
         * 3 | 0 0 0 0 0 |
         * 4 | 0 0 0 0 0 |
         *   +-----------+
         * D               C
         */
        image.set(1, 0, 0, 1);
        printImage(image, "Image before any filter applying:");

        /*
         *     0 1 2 3 4
         *   +-----------+
         * 0 | o o o o o |
         * 1 | o o o o o |
         * 2 | o o o x o |
         * 3 | o o o o o |
         *   +-----------+
         */
        final Point anchor = new Point(3, 2);

        final Image result = new Image(5, 5, 1);
        image.noneLinearFilter(result, 5, 4, anchor, 2, Extrapolation.ZERO, new KernelOperation() {

            @Override
            public void execute(final Image aperture, final Color result) {
                /*
                 *     0 1 2 3 4
                 *   +-----------+
                 * 0 | o o f o o |
                 * 1 | o o o o o |
                 * 2 | o o o t o |
                 * 3 | o o o o o |
                 *   +-----------+
                 *      f --> t
                 */
                aperture.get(2, 0, result);
            }
        });

        // Check values.
        /*
         * A   0 1 2 3 4   B
         *   +-----------+
         * 0 | 0 0 0 0 0 |
         * 1 | 0 0 0 0 0 |
         * 2 | 0 0 0 0 0 |
         * 3 | 0 0 0 0 0 |
         * 4 | 0 0 0 1 0 |
         *   +-----------+
         * D               C
         */
        printImage(result, "Image after nonlinear filter applying:");

        // Check values.
        for (int y = 0; y < result.getHeight(); ++y) {
            for (int x = 0; x < result.getWidth(); ++x) {
                if (x == 3 && y == 4) {
                    assertEquals(1, result.get(x, y, 0));
                } else {
                    assertEquals(0, result.get(x, y, 0));
                }
            }
        }
    }

    /**
     * Test method for: {@link Image#noneLinearFilter(Image, int, int, Point, int, Extrapolation, KernelOperation)}.
     */
    @Test
    public void testNonlinearFilterException() {
        final KernelOperation op = new KernelOperation() {

            @Override
            public void execute(final Image aperture, final Color result) {
                result.fill(0);
            }
        };

        // Incorrect anchor X position.
        try {
            (new Image(100, 100, 3)).noneLinearFilter(new Image(100, 100, 3), 5, 5, new Point(5, 1), 1,
                    Extrapolation.ZERO, op);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect anchor Y position.
        try {
            (new Image(100, 100, 3)).noneLinearFilter(new Image(100, 100, 3), 5, 5, new Point(1, 5), 1,
                    Extrapolation.ZERO, op);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect iterations.
        try {
            (new Image(100, 100, 3)).noneLinearFilter(new Image(100, 100, 3), 5, 5, new Point(1, 1), -1,
                    Extrapolation.ZERO, op);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#fill(Color)}.
     */
    @Test
    public void testFill() {
        final Image image = new Image(300, 200, 4);
        image.fill(new Color(new int[]{ 0, 1, 2, 3 }));

        try {
            image.fill(null);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.fill(new Color(new int[]{ 0, 1, 2 }));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#fill(Color)}.
     */
    @Test
    public void testFillException() {
        final Image image = new Image(300, 200, 4);
        image.fill(new Color(new int[]{ 0, 1, 2, 3 }));

        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    assertEquals(channel, image.get(x, y, channel));
                }
            }
        }
    }

    /**
     * Test method for: {@link Image#mult(double)}.
     */
    @Test
    public void testMult() {
        final Image image = new Image(300, 200, 4);
        image.fill(new Color(new int[]{ 0, 1, 2, 3 }));

        image.mult(2.0);

        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    assertEquals(JCV.round(channel * 2.0), image.get(x, y, channel));
                }
            }
        }
    }

    /**
     * Test method for: {@link Image#mult(double)}.
     */
    @Test
    public void testMultException() {
        final Image image = new Image(300, 200, 4);

        try {
            image.mult(-1.0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#convolve(Matrix)}.
     */
    @Test
    public void testConvolve() {
        final Image image = new Image(3, 2, 1);
        image.fill(new Color(new int[]{ 1 }));

        final Matrix kernel = new Matrix(2, 3);
        for (int i = 0; i < kernel.getRowDimension(); ++i) {
            for (int j = 0; j < kernel.getColumnDimension(); ++j) {
                kernel.set(i, j, 2.0);
            }
        }

        final double[] c = image.convolve(kernel);
        assertEquals(1, c.length);
        assertEquals(12.0, c[0], JCV.PRECISION);
    }

    /**
     * Test method for: {@link Image#convolve(Matrix)}.
     */
    @Test
    public void testConvolveException() {
        final Image image = new Image(3, 2, 1);

        image.convolve(new Matrix(2, 3));
        try {
            image.convolve(new Matrix(3, 3));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
        try {
            image.convolve(new Matrix(2, 4));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#makeCopy()}, {@link Image#copyTo(Image)}, {@link Image#equals(Object)}.
     */
    @Test
    public void testCopyCloneEquals() {
        final Image image = new Image(100, 100, 4);
        image.fill(new Color(new int[]{ 1, 2, 3, 4 }));

        assertTrue(image.equals(image));
        assertTrue(image.equals(image.makeCopy()));
        assertFalse(image.equals(null));
        assertFalse(image.equals(image.makeSubImage(10, 10, 10, 10)));
        assertFalse(image.equals(image.makeLayer(1, 2)));
        assertFalse(image.equals(0));

        final Image copy = image.makeCopy();
        copy.set(0, 0, 0, 5);
        assertFalse(image.equals(copy));

        final Image image1 = new Image(100, 100, 5);
        assertFalse(image.equals(image1));
    }

    /**
     * Test method for: {@link Image#copyTo(Image)}.
     */
    @Test
    public void testCopyTo() {
        final Image image = new Image(100, 100, 4);
        final Image copy = image.makeCopy();
        copy.set(10, 10, 2, 1);
        assertFalse(image.equals(copy));

        image.copyTo(copy);
        assertTrue(image.equals(copy));
    }

    /**
     * Test method for: {@link Image#copyTo(Image)}.
     */
    @Test
    public void testCopyToException() {
        final Image image = new Image(100, 100, 3);
        final Image subImg = image.makeSubImage(10, 10, 20, 20);
        try {
            image.copyTo(subImg);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        final Image layer = image.makeLayer(0, 2);
        try {
            image.copyTo(layer);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#makeSubImage(int, int, int, int)}.
     */
    @Test
    public void testMakeSubImage() {
        final Image image = new Image(100, 100, 4);
        final Image subImage = image.makeSubImage(80, 80, 20, 20);
        final Image subSubImage = subImage.makeSubImage(10, 10, 10, 10);

        assertEquals(subImage.getHeight(), 20);
        assertEquals(subImage.getWidth(), 20);

        assertEquals(subSubImage.getHeight(), 10);
        assertEquals(subSubImage.getWidth(), 10);

        /*
         * Verify access to pixels of sub-images.
         */
        final int value = 5;

        // Set pixels values.
        System.out.println(subSubImage);
        for (int y = 0; y < subSubImage.getHeight(); ++y) {
            for (int x = 0; x < subSubImage.getWidth(); ++x) {
                for (int channel = 0; channel < 4; ++channel) {
                    subSubImage.set(y, x, channel, value);
                }
            }
        }

        // Get and check pixels values.
        for (int y = 90; y < 100; ++y) {
            for (int x = 90; x < 100; ++x) {
                for (int channel = 0; channel < 4; ++channel) {
                    assertEquals(value, image.get(y, x, channel), JCV.PRECISION);
                }
            }
        }
    }

    /**
     * Test method for: {@link Image#makeSubImage(int, int, int, int)}.
     */
    @Test
    public void testMakeSubImageException() {
        final Image image = new Image(100, 100, 4);
        image.makeSubImage(80, 80, 20, 20);

        // Incorrect Width.
        try {
            image.makeSubImage(80, 80, 20, 21);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect Height.
        try {
            image.makeSubImage(80, 80, 21, 20);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for: {@link Image#makeLayer(int, int)}, {@link Image#makeChannel(int)}.
     */
    @Test
    public void testMakeLayerAndChannel() {
        final Image image = new Image(100, 100, 4);
        int value;

        int startChannel = 1;
        int sizeLayer = 2;
        int numChannel = 3;

        final Image layer = image.makeLayer(startChannel, sizeLayer);
        final Image channel = image.makeChannel(numChannel);

        /*
         * Check size.
         */
        assertEquals(image.getHeight(), layer.getHeight());
        assertEquals(image.getWidth(), layer.getWidth());
        assertEquals(sizeLayer, layer.getNumOfChannels());

        assertEquals(image.getHeight(), channel.getHeight());
        assertEquals(image.getWidth(), channel.getWidth());
        assertEquals(1, channel.getNumOfChannels());

        /*
         * Check, that we have same source.
         */
        // Set values.
        value = Color.MIN_VALUE;
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                for (int ch = 0; ch < image.getNumOfChannels(); ++ch) {
                    image.set(y, x, ch, value);

                    // Next value.
                    ++value;
                    if (value > Color.MAX_VALUE) {
                        value = Color.MIN_VALUE;
                    }
                }
            }
        }
        // Check values.
        for (int y = 0; y < layer.getHeight(); ++y) {
            for (int x = 0; x < layer.getWidth(); ++x) {
                // Check single channel.
                assertEquals(image.get(y, x, numChannel), channel.get(y, x, 0), JCV.PRECISION);

                // Check layer.
                for (int ch = 0; ch < layer.getNumOfChannels(); ++ch) {
                    assertEquals(image.get(y, x, ch + startChannel), layer.get(y, x, ch), JCV.PRECISION);
                }
            }
        }
    }

    /**
     * Test method for: {@link Image#makeLayer(int, int)}, {@link Image#makeChannel(int)}.
     */
    @Test
    public void testMakeLayerAndChannelException() {
        final Image image = new Image(100, 100, 4);

        // Incorrect Channel.
        try {
            image.makeChannel(-1);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect Channel.
        try {
            image.makeChannel(4);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect start position.
        try {
            image.makeLayer(-1, 2);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect start length.
        try {
            image.makeLayer(0, 5);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect start length.
        try {
            image.makeLayer(2, 3);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect start length.
        try {
            image.makeLayer(2, 0);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }
}
