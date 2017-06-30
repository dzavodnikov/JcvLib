/*
 * Copyright (c) 2017 JcvLib Team
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

import org.jcvlib.parallel.Parallel;
import org.jparfor.JParFor;

import Jama.Matrix;

/**
 * Main class for all images that used into the JcvLib.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Image {

    private final byte[] source;
    private final int    sourceWidth;
    private final int    sourceHeight;
    private final int    sourceNumOfChannels;

    /*
     * Will be used for sub-image moving.
     */
    private int          subImageX;
    private int          subImageY;
    private final int    subImageWidth;
    private final int    subImageHeight;

    private final int    subImageLayerStart;
    private final int    subImageLayerLength;

    /**
     * Create new empty image.
     *
     * @param width
     *            Width of image (in pixels).
     * @param height
     *            Height of image (in pixels).
     * @param numOfChannels
     *            Number of channels in current image.
     */
    public Image(final int width, final int height, final int numOfChannels) {
        /*
         * Verify parameters.
         */
        if (numOfChannels <= 0) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Value of \"numOfChannels\" (= {0}) must be more than 0!", numOfChannels));
        }

        /*
         * Create a new object.
         */
        this.sourceWidth = width;
        this.sourceHeight = height;
        this.sourceNumOfChannels = numOfChannels;
        this.source = new byte[this.sourceWidth * this.sourceHeight * this.sourceNumOfChannels];

        this.subImageX = 0;
        this.subImageY = 0;
        this.subImageWidth = this.sourceWidth;
        this.subImageHeight = this.sourceHeight;
        this.subImageLayerStart = 0;
        this.subImageLayerLength = this.sourceNumOfChannels;
    }

    private Image(final byte[] source, final int sourceWidth, final int sourceHeight, final int sourceNumOfChannels,
            final int subImageX, final int subImageY, final int subImageWidth, final int subImageHeight,
            final int subImageLayerStart, final int subImageLayerLength) {
        this.source = source;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.sourceNumOfChannels = sourceNumOfChannels;

        this.subImageX = subImageX;
        this.subImageY = subImageY;
        this.subImageWidth = subImageWidth;
        this.subImageHeight = subImageHeight;
        this.subImageLayerStart = subImageLayerStart;
        this.subImageLayerLength = subImageLayerLength;
    }

    /**
     * Return width of image (in pixels).
     */
    public int getWidth() {
        return this.subImageWidth;
    }

    /**
     * Return height of image (in pixels).
     */
    public int getHeight() {
        return this.subImageHeight;
    }

    /**
     * Return size of image (in pixels).
     */
    public Size getSize() {
        return new Size(getWidth(), getHeight());
    }

    /**
     * Return number of channels in current image.
     */
    public int getNumOfChannels() {
        return this.subImageLayerLength;
    }

    /**
     * Verify given point.
     */
    private void verifyPoint(final double x, final double y, final int width, final int height) {
        if (x < 0 || x >= width) {
            throw new IllegalArgumentException(
                    String.format("Value of \"x\" (= %s) must in interval 0..%s!", x, width - 1));
        }

        if (y < 0 || y >= height) {
            throw new IllegalArgumentException(
                    String.format("Value of \"y\" (= %s) must in interval 0..%s!", y, height - 1));
        }
    }

    /**
     * Verify given point.
     */
    private void verifyPoint(final double x, final double y) {
        verifyPoint(x, y, getWidth(), getHeight());
    }

    private void verifyChannel(final int channel) {
        if (channel < 0 || channel >= getNumOfChannels()) {
            throw new IllegalArgumentException(MessageFormat
                    .format("Value of \"channel\" (= {0}) must in interval 0..{1}!", channel, getNumOfChannels() - 1));
        }
    }

    private void verifyChannel(final Color color) {
        JCV.verifyIsNotNull(color);

        if (color.getNumOfChannels() != getNumOfChannels()) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "Parameter \"color\" must have same number of channels (= {0}) as image (= {1})!",
                    color.getNumOfChannels(), getNumOfChannels()));
        }
    }

    /**
     * Return position in source array for given point and channel.
     */
    private int calculateArrayPosition(final int x, final int y, final int channel) {
        return this.sourceNumOfChannels * (this.sourceHeight * x + y) + channel;
    }

    private void calculateImagePosition(final int arrayPosition, final int[] vals) {
        // X.
        vals[0] = arrayPosition / (getHeight() * getNumOfChannels());
        // Y.
        final int t = arrayPosition - getNumOfChannels() * getHeight() * vals[0];
        vals[1] = t / getNumOfChannels();
        // Channel.
        vals[2] = t % getNumOfChannels();
    }

    /**
     * Same as {@link #get(int, int, int)}, but not check position of color value. Useful for group operations.
     */
    public int getUnsafe(final int x, final int y, final int channel) {
        return Color.UByteToInt(this.source[calculateArrayPosition(this.subImageX + x, this.subImageY + y,
                this.subImageLayerStart + channel)]);
    }

    /**
     * Return integer value in interval <code>[0, 255]</code> of selected channel from selected pixel.
     */
    public int get(final int x, final int y, final int channel) {
        /*
         * Verify parameters.
         */
        verifyPoint(x, y);
        verifyChannel(channel);

        /*
         * Return value.
         */
        return getUnsafe(x, y, channel);
    }

    public int get(final Point pos, final int channel) {
        return get(pos.getX(), pos.getY(), channel);
    }

    /**
     * Same as {@link #get(int, int, Color)}, but not check position and color object. Useful for group operations.
     */
    public void getUnsafe(final int x, final int y, final Color color) {
        for (int channel = 0; channel < getNumOfChannels(); ++channel) {
            color.set(channel, getUnsafe(x, y, channel));
        }
    }

    /**
     * Return color from selected pixel.
     */
    public void get(final int x, final int y, final Color color) {
        /*
         * Verify parameters.
         */
        verifyPoint(x, y);
        verifyChannel(color);

        /*
         * Copy value.
         */
        getUnsafe(x, y, color);
    }

    public void get(final Point pos, final Color color) {
        get(pos.getX(), pos.getY(), color);
    }

    /**
     * Same as {@link #set(int, int, int, int)}, but not check position of color value. Useful for group operations.
     */
    public void setUnsafe(final int x, final int y, final int channel, final int value) {
        this.source[calculateArrayPosition(this.subImageX + x, this.subImageY + y,
                this.subImageLayerStart + channel)] = Color.IntToUByte(value);
    }

    /**
     * Set integer value in interval <code>[0, 255]</code> to selected channel from selected pixel.
     * <p>
     * If value less minimal or great than maximal allowed value it will be truncated to corresponded correct value. For
     * example value <code>-1</code> will be replaced to <code>0</code>, value <code>256</code> will be replaced to
     * <code>255</code>.
     * </p>
     */
    public void set(final int x, final int y, final int channel, final int value) {
        /*
         * Verify parameters.
         */
        verifyPoint(x, y);
        verifyChannel(channel);

        /*
         * Set value.
         */
        setUnsafe(x, y, channel, value);
    }

    public void set(final Point pos, final int channel, final int value) {
        set(pos.getX(), pos.getY(), channel, value);
    }

    /**
     * Same as {@link #set(int, int, Color)}, but not check position and color object. Useful for group operations.
     */
    public void setUnsafe(final int x, final int y, final Color color) {
        for (int channel = 0; channel < getNumOfChannels(); ++channel) {
            setUnsafe(x, y, channel, color.get(channel));
        }
    }

    /**
     * Set color to selected pixel.
     */
    public void set(final int x, final int y, final Color color) {
        /*
         * Verify parameters.
         */
        verifyPoint(x, y);
        verifyChannel(color);

        /*
         * Copy value.
         */
        setUnsafe(x, y, color);
    }

    public void set(final Point pos, final Color color) {
        set(pos.getX(), pos.getY(), color);
    }

    /**
     * Extrapolate image uses selected extrapolation type. Uses into filters.
     *
     * @param x
     *            X-position.
     * @param y
     *            Y-position.
     * @param channel
     *            Number of channel.
     * @param extrapolation
     *            Extrapolation method.
     * @return Values of color from current image using extrapolation. It means, that you can using negative values of
     *         <code>x</code> and <code>y</code> or values more than <code>width</code> and <code>height</code>.
     */
    public int get(final int x, final int y, final int channel, final Extrapolation extrapolation) {
        /*
         * Verify parameters.
         */
        verifyChannel(channel);
        JCV.verifyIsNotNull(extrapolation);

        /*
         * Return value.
         */
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return extrapolation.get(this, x, y, channel);
        } else {
            return get(x, y, channel);
        }
    }

    /**
     * Interpolate image uses selected interpolation type. Uses into geometry transformations.
     *
     * @param x
     *            X-position.
     * @param y
     *            Y-position.
     * @param channel
     *            Number of channel.
     * @param interpolation
     *            Interpolation method.
     * @return Values of color from current image using interpolation. It means, that you can using fractional pixel
     *         position values of <code>x</code> and <code>y</code> result will be interpolate.
     */
    public double get(final double x, final double y, final int channel, final Interpolation interpolation) {
        /*
         * Verify parameters.
         */
        verifyPoint(x, y);
        verifyChannel(channel);
        JCV.verifyIsNotNull(interpolation);

        /*
         * Get value.
         */
        return interpolation.get(this, x, y, channel);
    }

    public void foreach(final ParallelValueOperation runner) {
        JParFor.exec(getSize().calculateN() * getNumOfChannels(), (arrayPosition, nThread) -> {
            final int[] xyc = new int[3];
            calculateImagePosition(arrayPosition, xyc);
            set(xyc[0], xyc[1], xyc[2], runner.execute(get(xyc[0], xyc[1], xyc[2])));
        });
    }

    public void noneLinearFilter(final Image result, final int width, final int height, final Point anchor,
            final Extrapolation extrapolation, final KernelOperation operator) {
        // Create extend image.
        final Image sourceExtend = new Image(getWidth() + width - 1, getHeight() + height - 1, getNumOfChannels());

        // Fill extend image.
        Parallel.pixels(sourceExtend, (x, y, worker) -> {
            for (int channel = 0; channel < sourceExtend.getNumOfChannels(); ++channel) {
                sourceExtend.setUnsafe(x, y, channel,
                        get(x - anchor.getX(), y - anchor.getY(), channel, extrapolation));
            }
        });

        // Initialize apertures.
        final Image[] apertures = new Image[Parallel.getNumOfWorkers()];
        for (int i = 0; i < apertures.length; ++i) {
            apertures[i] = sourceExtend.makeSubImage(0, 0, width, height);
        }
        // Initialize colors.
        final Color[] colors = new Color[Parallel.getNumOfWorkers()];
        for (int i = 0; i < apertures.length; ++i) {
            colors[i] = new Color(result.getNumOfChannels());
        }

        // Run operator for each pixel from extended image.
        Parallel.pixels(this, (x, y, worker) -> {
            final Image aperture = apertures[worker];
            final Color color = colors[worker];

            // Update position.
            aperture.subImageX = x;
            aperture.subImageY = y;

            // Execute.
            operator.execute(aperture, color);
            result.set(x, y, color);
        });
    }

    /**
     * Nonlinear filter.
     * <p>
     * For each pixel on this image apply given operator. This operator get kernel values and put result into current
     * pixel. For example, we have kernel size <code>[4 x 4]</code> and anchor <code>(1, 1)</code> for each pixel on
     * image we select a field: <code><pre>
     *     0 1 2 3
     *   +---------+
     * 0 | o o o o |
     * 1 | o x o o |
     * 2 | o o o o |
     * 3 | o o o o |
     *   +---------+
     * </pre></code> And put this values into operator. Result of this operator set to current pixel.
     * </p>
     * <p>
     * Some filters need kernel with only odd sizes and anchor only in the center (for example, Gaussian blur).
     * </p>
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Nonlinear_filter">Nonlinear filter -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param result
     *            Result image.
     * @param width
     *            Width of kernel that will be used for image operation.
     * @param height
     *            Height of kernel that will be used for image operation.
     * @param anchor
     *            Anchor of the kernel that contain the relative position of a filtered point within the kernel.
     * @param iterations
     *            Number of iteration this filter to source image.
     * @param extrapolation
     *            Extrapolation method.
     * @param operator
     *            Operation that should get kernel on each step.
     */
    public void noneLinearFilter(final Image result, final int width, final int height, final Point anchor,
            final int iterations, final Extrapolation extrapolation, final KernelOperation operator) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(this, result);
        JCV.verifyIsNotNull(anchor);
        verifyPoint(anchor.getX(), anchor.getY(), width, height);
        if (iterations <= 0) {
            throw new IllegalArgumentException("Number of iterations should be more than 0!");
        }
        JCV.verifyIsNotNull(operator);

        /*
         * Perform transformation.
         */
        Image currentSource = makeCopy();
        Image currentResult = result;
        Image temp;
        for (int i = 0; i < iterations; ++i) {
            // Perform.
            currentSource.noneLinearFilter(currentResult, width, height, anchor, extrapolation, operator);

            // Switch.
            temp = currentSource;
            currentSource = currentResult;
            currentResult = temp;
        }

        currentSource.copyTo(result);
    }

    /**
     * Fill current image by given color (each pixel will be have defined color).
     */
    public void fill(final Color color) {
        /*
         * Verify parameters.
         */
        verifyChannel(color);

        /*
         * Set values.
         */
        Parallel.pixels(this, (x, y, worker) -> {
            for (int channel = 0; channel < getNumOfChannels(); ++channel) {
                set(x, y, channel, color.get(channel));
            }
        });
    }

    /**
     * This method <strong>multiply</strong> current image on given number.
     * <p>
     * If value of color will be more than <code>{@link Color#MAX_VALUE}</code> this color value set
     * <code>{@link Color#MAX_VALUE}</code>. If value of color will be less than <code>{@link Color#MIN_VALUE}</code>
     * this color value set <code>{@link Color#MIN_VALUE}</code>.
     * </p>
     */
    public void mult(final double c) {
        /*
         * Verify parameters.
         */
        if (c < 0.0) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Parameter \"c\" (= {0}) must be more or equal 0.0!", c));
        }

        /*
         * Perform operation.
         */
        foreach(value -> JCV.round(c * value));
    }

    /**
     * Convolve current image with given matrix.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Convolution">Convolution -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @return Result of convolution for each channel.
     */
    public double[] convolve(final Matrix kernel) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(kernel);
        if (getWidth() != kernel.getColumnDimension() || getHeight() != kernel.getRowDimension()) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Kernel should have same size (= {0}) as image (= {0})!", getSize(),
                            JCV.getSizeString(kernel.getColumnDimension(), kernel.getRowDimension())));
        }

        /*
         * Perform operation.
         */
        final double result[] = new double[getNumOfChannels()];

        for (int channel = 0; channel < getNumOfChannels(); ++channel) {
            for (int x = 0; x < kernel.getColumnDimension(); ++x) {
                for (int y = 0; y < kernel.getRowDimension(); ++y) {
                    result[channel] += kernel.get(y, x) * get(x, y, channel);
                }
            }
        }

        return result;
    }

    /**
     * Create <strong>empty</strong> image with size, number of channels and source type as in given image.
     * <p>
     * <strong>Values from given image will be not copied!</strong>
     * </p>
     */
    public Image makeSame() {
        return new Image(getWidth(), getHeight(), getNumOfChannels());
    }

    /**
     * Copy values from current to given image. <strong>Given image should have SAME size as current image.</strong>
     */
    public void copyTo(final Image target) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(this, target);
        if (target.getNumOfChannels() != getNumOfChannels()) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "Given image should have a same number of channels (= {0}) as current image (= {1})!",
                    target.getNumOfChannels(), getNumOfChannels()));
        }

        /*
         * Copy values.
         */
        Parallel.pixels(this, (x, y, worker) -> {
            for (int channel = 0; channel < getNumOfChannels(); ++channel) {
                target.setUnsafe(x, y, channel, getUnsafe(x, y, channel));
            }
        });
    }

    /**
     * Return copy of current image. It will be a <strong>REAL COPY</strong> of current image or sub-image!
     */
    public Image makeCopy() {
        final Image copy = makeSame();

        copyTo(copy);

        return copy;
    }

    /**
     * Return sub-image based on current source. It is <strong>NOT COPY</strong> current image.
     */
    public Image makeSubImage(final int x, final int y, final int width, final int height) {
        /*
         * Verify parameters.
         */
        if (getWidth() < x + width || getHeight() < y + height) {
            throw new IllegalArgumentException("Can not create sub-image with defined size!");
        }

        /*
         * Create new object.
         */
        return new Image(this.source, this.sourceWidth, this.sourceHeight, this.sourceNumOfChannels, this.subImageX + x,
                this.subImageY + y, width, height, this.subImageLayerStart, this.subImageLayerLength);
    }

    /**
     * Select from multichannel image from current image.
     * <p>
     * Returned image will be have <strong>same size</strong> but this new image contains only <code>sizeLayer</code>
     * channels that got from <code>startChannel</code> to <code>startChannel + sizeLayer</code>. It is <strong>NOT
     * COPY</strong> of current image -- this is <strong>SAME</strong> channels from current image!
     * </p>
     */
    public Image makeLayer(final int startChannel, final int sizeLayer) {
        /*
         * Verify parameters.
         */
        verifyChannel(startChannel);
        if (sizeLayer < 1) {
            throw new IllegalArgumentException(
                    "Value of 'sizeChannels' (= " + sizeLayer + ") must more or equals than 1!");
        }
        if (startChannel + sizeLayer > getNumOfChannels()) {
            throw new IllegalArgumentException("Value of 'startChannel + sizeChannels' (= " + (startChannel + sizeLayer)
                    + ") " + "must be less or equals than " + getNumOfChannels() + "!");
        }

        /*
         * Create new object.
         */
        return new Image(this.source, this.sourceWidth, this.sourceHeight, this.sourceNumOfChannels, this.subImageX,
                this.subImageY, this.subImageWidth, this.sourceHeight, startChannel, sizeLayer);
    }

    /**
     * Select single channel image based on channel with number <code>channelNumber</code> from current multichannel
     * image.
     * <p>
     * This is analog of <code>split</code> function from other computer vision libraries. You can get access to
     * channels as an elements of a list. It is <strong>NOT COPY</strong> current image -- this is <strong>SAME</strong>
     * channel from image!
     * </p>
     */
    public Image makeChannel(final int numChannel) {
        return makeLayer(numChannel, 1);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (int x = 0; x < getWidth(); ++x) {
            for (int y = 0; y < getHeight(); ++y) {
                for (int channel = 0; channel < getNumOfChannels(); ++channel) {
                    result = (prime * result) + get(x, y, channel);
                }
            }
        }
        return result;
    }

    /**
     * Return <code>true</code> if current image equivalent to object from parameter and <code>false</code> otherwise.
     * <p>
     * Uses {@link JCV#PRECISION} by default.
     * </p>
     */
    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        Image image = null;

        if (object instanceof Image) {
            image = (Image) object;
        } else {
            return false;
        }

        if (image == this) {
            return true;
        }

        // Compare size.
        if (getWidth() != image.getWidth()) {
            return false;
        }
        if (getHeight() != image.getHeight()) {
            return false;
        }
        if (getNumOfChannels() != image.getNumOfChannels()) {
            return false;
        }

        // Compare values.
        for (int x = 0; x < getWidth(); ++x) {
            for (int y = 0; y < getHeight(); ++y) {
                for (int channel = 0; channel < getNumOfChannels(); ++channel) {
                    if (get(x, y, channel) != image.get(x, y, channel)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Return string with some internal information about this channel.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Image:");
        sb.append("\n");

        sb.append("    Hash:            ");
        sb.append(hashCode());
        sb.append("\n");

        sb.append("    Source size:     ");
        sb.append(new Size(this.sourceWidth, this.sourceHeight));
        sb.append("\n");

        sb.append("    Current image:   ");
        sb.append(JCV.getRectangleString(this.subImageX, this.subImageY, this.subImageWidth, this.subImageHeight));
        sb.append("\n");

        sb.append("    Start channel:   ");
        sb.append(this.subImageLayerStart);
        sb.append("\n");

        sb.append("    Size layer:      ");
        sb.append(this.subImageLayerLength);
        sb.append("\n");

        return sb.toString();
    }
}
