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

import java.text.MessageFormat;

/**
 * This class contains all color values of some pixel.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Color {

    /**
     * Bounds of color values -- minimum value.
     */
    public static final int MIN_VALUE = 0;

    /**
     * Bounds of color value -- maximum value.
     */
    public static final int MAX_VALUE = 255;

    /**
     * Contains all channels of some pixel.
     */
    private final int[]     color;

    /**
     * Create empty color.
     */
    public Color(final int numOfChannels) {
        /*
         * Verify parameters.
         */
        if (numOfChannels <= 0) {
            throw new IllegalArgumentException(
                    String.format("{0} value (= {1}) must be more than 0!", int.class.getName(), numOfChannels));
        }

        /*
         * Create internal structure.
         */
        this.color = new int[numOfChannels];
    }

    /**
     * Create empty color and initialize all channels by given integer value (from interval <code>0..255</code>).
     */
    public Color(final int numOfChannels, final int value) {
        this(numOfChannels);

        fill(value);
    }

    /**
     * Create empty color and initialize it by given float-point color values (from interval <code>[0.0, 1.0]</code>).
     */
    public Color(final int[] colorValues) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(colorValues);
        if (colorValues.length == 0) {
            throw new IllegalArgumentException(
                    MessageFormat.format("{0} value have no elements!", int[].class.getName()));
        }

        /*
         * Create new object.
         */
        this.color = new int[colorValues.length];
        for (int channel = 0; channel < getNumOfChannels(); ++channel) {
            set(channel, colorValues[channel]);
        }
    }

    /**
     * Fill all channels by given value.
     */
    public void fill(final int value) {
        for (int channel = 0; channel < getNumOfChannels(); ++channel) {
            set(channel, value);
        }
    }

    /**
     * Return number channels of current color.
     */
    public int getNumOfChannels() {
        return this.color.length;
    }

    /**
     * Return value (from interval <code>[0.0, 255.0]</code>) of color from given channel number.
     */
    public int get(final int channel) {
        try {
            return this.color[channel];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(MessageFormat
                    .format("Value of \"channel\" (= {0}) must in interval 0..{1}!", channel, getNumOfChannels() - 1));
        }
    }

    /**
     * Set value (from interval <code>[0.0, 255.0]</code>) of color to given channel number.
     */
    public void set(final int channel, int value) {
        if (value < Color.MIN_VALUE) {
            value = Color.MIN_VALUE;
        }
        if (value > Color.MAX_VALUE) {
            value = Color.MAX_VALUE;
        }
        try {
            this.color[channel] = value;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(String.format("Value of \"channel\" (= %s) must in interval 0..%s!",
                    channel, getNumOfChannels() - 1));
        }
    }

    /**
     * Calculate normalize (all values between {@link Color#MIN_VALUE} and {@link Color#MAX_VALUE}) Euclidean distance
     * between 2 colors.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Euclidean_distance">Euclidean distance -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    public double euclidDist(final Color c) {
        /*
         * Verify parameters.
         */
        if (getNumOfChannels() != c.getNumOfChannels()) {
            throw new IllegalArgumentException(
                    "Given color must have same number of channels (= " + Integer.toString(c.getNumOfChannels()) + ") "
                            + "as currect color (= " + Integer.toString(getNumOfChannels()) + ")!");
        }

        /*
         * Perform operation.
         */
        // Calculate sum of squares.
        double result = 0.0;
        for (int channel = 0; channel < getNumOfChannels(); ++channel) {
            final double dist = get(channel) - c.get(channel);
            result += dist * dist;
        }

        return Math.sqrt(result) / Math.sqrt(getNumOfChannels());
    }

    /**
     * Return <code>true</code> if current color equivalent to object from parameter and <code>false</code> otherwise.
     * <p>
     * Uses {@link JCV#PRECISION} by default.
     * </p>
     */
    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        Color color = null;

        if (object instanceof Color) {
            color = (Color) object;
        } else {
            return false;
        }

        if (color == this) {
            return true;
        }

        if (color.getNumOfChannels() != getNumOfChannels()) {
            return false;
        }

        for (int channel = 0; channel < getNumOfChannels(); ++channel) {
            if (color.get(channel) != get(channel)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return copy of current color.
     * <p>
     * It will be a <strong>REAL COPY</strong> of current color!
     * </p>
     */
    public Color makeCopy() {
        final Color result = new Color(getNumOfChannels());

        for (int channel = 0; channel < getNumOfChannels(); ++channel) {
            result.set(channel, get(channel));
        }

        return result;
    }

    /**
     * Return string with values of this channel.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("{");
        for (int channel = 0; channel < getNumOfChannels(); ++channel) {
            sb.append(get(channel));

            if (channel < getNumOfChannels() - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");

        return sb.toString();
    }

    /**
     * Convert <code>int</code> to <code>unsigned byte</code>.
     * <p>
     * If value less <code>0</code> or great than <code>255</code> it will be truncated to corresponded correct value.
     * For example value <code>-1</code> will be replaced to <code>0</code>, value <code>256</code> will be replaced to
     * <code>255</code>.
     * </p>
     */
    public static byte IntToUByte(int value) {
        if (value < MIN_VALUE) {
            value = MIN_VALUE;
        }
        if (value > MAX_VALUE) {
            value = MAX_VALUE;
        }

        return (byte) value;
    }

    /**
     * Convert <code>unsigned byte</code> to <code>int</code>.
     */
    public static int UByteToInt(final byte value) {
        return value & 0xFF;
    }
}
