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
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for points {@link Region}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class RegionTest {

    public static final double PRECISION = 2.0;

    /**
     * Test method for: {@link Region}.
     */
    @Test
    public void testCerateException() {
        new Region(new Image(10, 10, 1), new Color(1, Color.MIN_VALUE));

        // No one color values.
        try {
            new Region(new Image(10, 10, 1), new Color(1, Color.MAX_VALUE));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect image.
        try {
            new Region(null, new Color(1, Color.MIN_VALUE));
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }

        // Incorrect color.
        try {
            new Region(new Image(10, 10, 1), null);
            fail("Not thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception message example:\n" + e.getMessage() + "\n");
        }
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test1() {
        final Image image = new Image(1, 1, 1);
        final int min = Math.min(image.getWidth(), image.getHeight());
        final int max = Math.min(image.getWidth(), image.getHeight());
        final Region region = new Region(image, new Color(1, Color.MIN_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getSize().calculateN(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        assertEquals(0.0, region.getEllipseMaxAxisOrientation(), JCV.PRECISION);
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test2() {
        final Image image = new Image(3, 1, 1);
        final int min = Math.min(image.getWidth(), image.getHeight());
        final int max = Math.max(image.getWidth(), image.getHeight());
        final Region region = new Region(image, new Color(1, Color.MIN_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getSize().calculateN(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        assertEquals(0.0, region.getEllipseMaxAxisOrientation(), JCV.PRECISION);
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test3() {
        final Image image = new Image(1, 3, 1);
        final int min = Math.min(image.getWidth(), image.getHeight());
        final int max = Math.max(image.getWidth(), image.getHeight());
        final Region region = new Region(image, new Color(1, Color.MIN_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getSize().calculateN(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        assertEquals(Math.PI / 2.0, region.getEllipseMaxAxisOrientation(), JCV.PRECISION);
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test4() {
        final Image image = new Image(5, 1, 1);
        final int min = Math.min(image.getWidth(), image.getHeight());
        final int max = Math.max(image.getWidth(), image.getHeight());
        final Region region = new Region(image, new Color(1, Color.MIN_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getSize().calculateN(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        assertEquals(0.0, region.getEllipseMaxAxisOrientation(), JCV.PRECISION);
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test5() {
        final Image image = new Image(1, 5, 1);
        final int min = Math.min(image.getWidth(), image.getHeight());
        final int max = Math.max(image.getWidth(), image.getHeight());
        final Region region = new Region(image, new Color(1, Color.MIN_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getSize().calculateN(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        assertEquals(Math.PI / 2.0, region.getEllipseMaxAxisOrientation(), JCV.PRECISION);
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test6() {
        final Image image = new Image(3, 3, 1);
        final int min = Math.min(image.getWidth(), image.getHeight());
        final int max = Math.max(image.getWidth(), image.getHeight());
        final Region region = new Region(image, new Color(1, Color.MIN_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getSize().calculateN(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        assertEquals(0.0, region.getEllipseMaxAxisOrientation(), JCV.PRECISION);
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test7() {
        final Image image = new Image(5, 3, 1);
        final int min = Math.min(image.getWidth(), image.getHeight());
        final int max = Math.max(image.getWidth(), image.getHeight());
        final Region region = new Region(image, new Color(1, Color.MIN_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getSize().calculateN(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        assertEquals(0.0, region.getEllipseMaxAxisOrientation(), JCV.PRECISION);
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test8() {
        final Image image = new Image(3, 5, 1);
        final int min = Math.min(image.getWidth(), image.getHeight());
        final int max = Math.max(image.getWidth(), image.getHeight());
        final Region region = new Region(image, new Color(1, Color.MIN_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getSize().calculateN(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        assertEquals(Math.PI / 2.0, region.getEllipseMaxAxisOrientation(), JCV.PRECISION);
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test9() {
        final Image image = new Image(9, 3, 1);
        final int min = Math.min(image.getWidth(), image.getHeight());
        final int max = Math.max(image.getWidth(), image.getHeight());
        final Region region = new Region(image, new Color(1, Color.MIN_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getSize().calculateN(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        assertEquals(0.0, region.getEllipseMaxAxisOrientation(), JCV.PRECISION);
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test10() {
        final Image image = new Image(3, 9, 1);
        final int min = Math.min(image.getWidth(), image.getHeight());
        final int max = Math.max(image.getWidth(), image.getHeight());
        final Region region = new Region(image, new Color(1, Color.MIN_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getSize().calculateN(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        assertEquals(Math.PI / 2.0, region.getEllipseMaxAxisOrientation(), JCV.PRECISION);
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test11() {
        final Image image = new Image(9, 9, 1);
        for (int i = 0; i < image.getWidth(); ++i) {
            image.set(i, i, 0, Color.MAX_VALUE);
        }
        final int min = 1;
        final int max = JCV.round(9.0 * Math.sqrt(2.0));
        final Region region = new Region(image, new Color(1, Color.MAX_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getWidth(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        //assertEquals(0.75 * Math.PI, region.getEllipseMaxAxisOrientation(), JCV.PRECISION); // TODO
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }

    /**
     * Test method for:
     * <ul>
     * <li>{@link Region#getAreaSize()}</li>
     * <li>{@link Region#getCentroid()}</li>
     * <li>{@link Region#getRegionX()}</li>
     * <li>{@link Region#getRegionY()}</li>
     * <li>{@link Region#getRegionWidth()}</li>
     * <li>{@link Region#getRegionHeight()}</li>
     * <li>{@link Region#getSecondOrderRowMoment()}</li>
     * <li>{@link Region#getSecondOrderMixedMoment()}</li>
     * <li>{@link Region#getSecondOrderColumnMoment()}</li>
     * <li>{@link Region#getEllipseMaxAxisOrientation()}</li>
     * <li>{@link Region#getEllipseMaxAxisLength()}</li>
     * <li>{@link Region#getEllipseMinAxisLength()}</li>
     * <li>{@link Region#toString()}</li>
     * </ul>
     */
    @Test
    public void test12() {
        final Image image = new Image(9, 9, 1);
        for (int i = 0; i < image.getWidth(); ++i) {
            image.set(i, image.getWidth() - i - 1, 0, Color.MAX_VALUE);
        }
        final int min = 1;
        final int max = JCV.round(9.0 * Math.sqrt(2.0));
        final Region region = new Region(image, new Color(1, Color.MAX_VALUE));
        System.out.println(region.toString());

        assertEquals(image.getWidth(), region.getAreaSize());

        final Point imageCenter = JCV.calculateCenter(image.getWidth(), image.getHeight());
        assertEquals(imageCenter.getX(), region.getCentroid().getX());
        assertEquals(imageCenter.getY(), region.getCentroid().getY());

        assertEquals(0, region.getRegionX());
        assertEquals(0, region.getRegionY());
        assertEquals(image.getSize().getWidth(), region.getRegionWidth());
        assertEquals(image.getSize().getHeight(), region.getRegionHeight());

        //assertEquals(0.25 * Math.PI, region.getEllipseMaxAxisOrientation(), JCV.PRECISION); // TODO
        assertEquals(max, region.getEllipseMaxAxisLength(), PRECISION);
        assertEquals(min, region.getEllipseMinAxisLength(), PRECISION);
    }
}
