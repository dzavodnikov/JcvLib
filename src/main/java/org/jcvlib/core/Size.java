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

/**
 * Contain <code>[height, width]</code> size of image, rectangle or something else. This is <strong>immutable</strong>
 * object.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Size {

    public final static int MIN_WIDTH  = 1;

    public final static int MIN_HEIGHT = 1;

    private final int       width;

    private final int       height;

    /**
     * Create new size.
     */
    public Size(final int width, final int height) {
        if (width < MIN_WIDTH) {
            throw new IllegalArgumentException("Value of 'width' (= " + Integer.toString(width)
                    + ") must be more or equals than " + Integer.toString(MIN_WIDTH) + "!");
        }
        if (height < MIN_HEIGHT) {
            throw new IllegalArgumentException("Value of 'height' (= " + Integer.toString(height)
                    + ") must be more or equals than " + Integer.toString(MIN_HEIGHT) + "!");
        }

        this.width = width;
        this.height = height;
    }

    public Size(final Size size) {
        this(size.getWidth(), size.getHeight());
    }

    /**
     * Return width of saved size.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Return height of saved size.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Return number of elements (pixels, values and etc.) into current size (same as
     * <code>getWidth() * getHeight()</code>).
     */
    public int calculateN() {
        return getWidth() * getHeight();
    }

    /**
     * Return copy of current object.
     */
    public Size makeCopy() {
        return new Size(this);
    }

    /**
     * Return <code>true</code> if current size equivalent to object from parameter and <code>false</code> otherwise.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        Size size = null;

        if (object instanceof Size) {
            size = (Size) object;
        } else {
            return false;
        }

        if (size == this) {
            return true;
        }

        if (this.width == size.getWidth() && this.height == size.getHeight()) {
            return true;
        }

        return false;
    }

    /**
     * Return <code>true</code> if current size less or equals on width <strong>and</strong> height than given.
     */
    public boolean lessOrEqualsThan(final Size size) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(size);

        /*
         * Check.
         */
        return (getWidth() <= size.getWidth()) && (getHeight() <= size.getHeight());
    }

    /**
     * Return string with values of this size.
     */
    @Override
    public String toString() {
        return JCV.getSizeString(this.width, this.height);
    }
}
