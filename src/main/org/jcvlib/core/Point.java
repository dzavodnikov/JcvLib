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

/**
 * Contain <code>(x, y)</code> position on image. This is <strong>immutable</strong> object.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Point {

    public final static int MIN_X_POSITION = 0;

    public final static int MIN_Y_POSITION = 0;

    private final int       x;

    private final int       y;

    /**
     * Create new point.
     */
    public Point(final int x, final int y) {
        if (x < MIN_X_POSITION) {
            throw new IllegalArgumentException(
                    "Value of 'x' (= " + x + ") must be more or equals than " + MIN_X_POSITION + " !");
        }
        if (y < MIN_Y_POSITION) {
            throw new IllegalArgumentException("Value of 'y' (= " + Integer.toString(y)
                    + ") must be more or equals than " + Integer.toString(MIN_Y_POSITION) + " !");
        }

        this.x = x;
        this.y = y;
    }

    /**
     * Create new point.
     */
    public Point(final Point point) {
        this(point.getX(), point.getY());
    }

    /**
     * Return X-value of pixel.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Return Y-value of pixel.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Return copy of current object.
     */
    public Point makeCopy() {
        return new Point(this);
    }

    /**
     * Return <code>true</code> if current point equivalent to object from parameter and <code>false</code> otherwise.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        Point point = null;

        if (object instanceof Point) {
            point = (Point) object;
        } else {
            return false;
        }

        if (point == this) {
            return true;
        }

        if (getX() == point.getX() && getY() == point.getY()) {
            return true;
        }

        return false;
    }

    /**
     * Return string with values of this point.
     */
    @Override
    public String toString() {
        return JCV.getPointString(this.x, this.y);
    }
}
