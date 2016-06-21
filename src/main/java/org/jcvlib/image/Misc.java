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
package org.jcvlib.image;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.jcvlib.core.Point;
import org.jcvlib.core.Size;
import org.jcvlib.image.filters.Blur;
import org.jcvlib.image.filters.Filters;
import org.jcvlib.parallel.ChannelsLoop;
import org.jcvlib.parallel.Parallel;
import org.jcvlib.parallel.PixelsLoop;

import Jama.Matrix;

/**
 * Contains miscellaneous image processing methods.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Misc {

    /**
     * Flood fill algorithm for fill some region.
     * <p>
     * This region is detected by start position and distance between color of start point and neighbors of this point.
     * </p>
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Flood_fill">Flood fill -- Wikipedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/Euclidean_distance">Euclidean distance -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param seed
     *            Start point.
     * @param distance
     *            Distance between start color and color all others neighbor points. Uses for compare Euclidean distance
     *            between two pixels. Maximum value for image with 3 channels is <code>442</code>.
     * @param fillColor
     *            Fill color value.
     * @param directionType
     *            Type of direction: 4 or 8. See <code>Misc.DIRECTIONS_TYPE_*</code>.
     * @param rangeType
     *            Define how to calculate difference: calculate difference between the current pixel and seed pixel or
     *            difference between neighbor pixels (i.e. the range is floating). See
     *            <code>Misc.FLOOD_FILL_RANGE_*</code>.
     * @return Single-channel {@link Image} that contains filled region (with {@link Color#MAX_VALUE} on filled points).
     */
    public static Image floodFill(final Image image, final Point seed, final double distance, final Color fillColor,
            final Direction directionType, final FloodFillRange rangeType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(seed);
        JCV.verifyIsNotNull(fillColor);

        /*
         * Perform operation.
         */
        final Image regionImage = new Image(image.getWidth(), image.getHeight(), 1);
        regionImage.fill(new Color(1, Color.MIN_VALUE));

        final List<Point> pointList = new ArrayList<Point>();
        pointList.add(seed);

        final Color seedColor = new Color(image.getNumOfChannels());
        image.get(seed, seedColor);

        final List<Color> colorList = new ArrayList<Color>();
        colorList.add(seedColor);

        final Color statFillColor = new Color(1, Color.MAX_VALUE);

        // Main loop.
        while (!pointList.isEmpty()) {
            final int lastIndex = pointList.size() - 1;

            final Point point = pointList.get(lastIndex);
            pointList.remove(lastIndex);

            final Color color = colorList.get(lastIndex);
            colorList.remove(lastIndex);

            Color sourceColor;
            if (rangeType.equals(FloodFillRange.FIXED)) {
                sourceColor = seedColor;
            } else {
                sourceColor = color;
            }

            // Process neighbors.
            final Color neighborColor = new Color(image.getNumOfChannels());
            for (Point neighborPoint : directionType.getNeighbors(point, image.getSize())) {
                image.get(neighborPoint, neighborColor);

                if (!neighborColor.equals(fillColor) && sourceColor.euclidDist(neighborColor) <= distance) {
                    pointList.add(neighborPoint);
                    colorList.add(neighborColor.makeCopy());

                    image.set(neighborPoint, fillColor);
                    regionImage.set(neighborPoint, statFillColor);
                }
            }
        }

        return regionImage;
    }

    /**
     * Same as {@link #floodFill(Image, Point, double, Color, Direction, FloodFillRange)}, but as default direction type
     * uses {@link Direction#TYPE_8} and as range type uses {@link FloodFillRange#FIXED}.
     */
    public static Image floodFill(final Image image, final Point seed, final double distance, final Color fillColor) {
        return Misc.floodFill(image, seed, distance, fillColor, Direction.TYPE_8, FloodFillRange.FIXED);
    }

    /**
     * Calculate summed area table.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Summed_area_table">Summed area table -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    public static List<Matrix> sumArea(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Perform operation.
         */
        final List<Matrix> result = new LinkedList<Matrix>();
        for (int i = 0; i < image.getNumOfChannels(); ++i) {
            result.add(new Matrix(image.getHeight(), image.getWidth()));
        }

        Parallel.channels(image, new ChannelsLoop() {

            @Override
            public void execute(final int channel) {
                for (int x = 0; x < image.getWidth(); ++x) {
                    for (int y = 0; y < image.getHeight(); ++y) {
                        double left = Color.MIN_VALUE;
                        if (x > 0) {
                            left = result.get(channel).get(y, x - 1);
                        }

                        double top = Color.MIN_VALUE;
                        if (y > 0) {
                            top = result.get(channel).get(y - 1, x);
                        }

                        double topLeft = Color.MIN_VALUE;
                        if (x > 0 && y > 0) {
                            topLeft = result.get(channel).get(y - 1, x - 1);
                        }

                        result.get(channel).set(y, x, left + top - topLeft + image.get(x, y, channel));
                    }
                }
            }
        });

        return result;
    }

    /**
     * Upsamples image.
     * <p>
     * This algorithm blur image first (with kernel <code>[5 x 5]</code>) and duplicate each row and each column then.
     * Used into {@link #buildPyramidGauss(Image, Size)}.
     * </p>
     *
     * @param image
     *            Source image.
     * @return Upsample image.
     */
    public static Image buildPyramidUp(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Perform operation.
         */
        final Image result = new Image(2 * image.getWidth(), 2 * image.getHeight(), image.getNumOfChannels());

        Parallel.pixels(Filters.blur(image, new Size(5, 5), Blur.GAUSSIAN), new PixelsLoop() {

            @Override
            public void execute(int x, int y, final int worker) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    result.set(2 * x, 2 * y, channel, image.get(x, y, channel));
                    result.set(2 * x + 1, 2 * y, channel, image.get(x, y, channel));
                    result.set(2 * x, 2 * y + 1, channel, image.get(x, y, channel));
                    result.set(2 * x + 1, 2 * y + 1, channel, image.get(x, y, channel));
                }
            }
        });

        return result;
    }

    /**
     * Downsamples image.
     * <p>
     * This algorithm blur image first (with kernel <code>[5 x 5]</code>) and remove each odd row and column then. Used
     * into {@link #buildPyramidLaplace(Image, Size)}.
     * </p>
     *
     * @param image
     *            Source image.
     * @return Downsample image.
     */
    public static Image buildPyramidDown(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        if (image.getWidth() < 2 || image.getHeight() < 2) {
            return image;
        }

        /*
         * Perform operation.
         */
        final Image blurImage = Filters.blur(image, new Size(5, 5), Blur.GAUSSIAN);
        final Image result = new Image(JCV.roundDown(image.getWidth() / 2.0), JCV.roundDown(image.getHeight() / 2.0),
                image.getNumOfChannels());

        Parallel.pixels(result, new PixelsLoop() {

            @Override
            public void execute(int x, int y, final int worker) {
                for (int channel = 0; channel < blurImage.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, blurImage.get(2 * x, 2 * y, channel));
                }
            }
        });

        return result;
    }

    /**
     * Create the Gaussian pyramid for an image.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Gaussian_pyramid">Gaussian pyramid -- Wikipedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/Pyramid_(image_processing)">Pyramid (image processing) -- Wikipedia</a>
     * .</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param minSize
     *            Minimal image size into created pyramid.
     * @return Pyramid of images.
     */
    public static List<Image> buildPyramidGauss(final Image image, final Size minSize) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(minSize);

        /*
         * Perform operation.
         */
        final List<Image> result = new LinkedList<Image>();

        Image current = image;
        while (current.getWidth() > minSize.getWidth() && current.getWidth() > minSize.getHeight()) {
            result.add(current);
            current = buildPyramidDown(current);
        }

        return result;
    }

    /**
     * Same as {@link #buildPyramidGauss(Image, Size)} but use size <code>[16 x 16]</code> as default minimal image
     * size.
     */
    public static List<Image> buildPyramidGauss(final Image image) {
        return buildPyramidGauss(image, new Size(16, 16));
    }

    /**
     * Create the Laplacian pyramid for an image.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Laplacian_pyramid">Laplacian pyramid -- Wikipedia</a>.</li>
     * <li><a href="http://en.wikipedia.org/wiki/Pyramid_(image_processing)">Pyramid (image processing) -- Wikipedia</a>
     * .</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param maxSize
     *            Maximal image size into created pyramid.
     * @return Pyramid of images.
     */
    public static List<Image> buildPyramidLaplace(final Image image, final Size maxSize) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(maxSize);

        /*
         * Perform operation.
         */
        final List<Image> result = new LinkedList<Image>();

        Image current = image;
        while (maxSize.getWidth() > current.getWidth() && maxSize.getHeight() > current.getHeight()) {
            current = buildPyramidUp(current);
            result.add(current);
        }

        return result;
    }

    /**
     * Put one image to another (using Alpha channel).
     * <p>
     * The images must have the same channels size. The last channel uses as Alpha channel.
     * </p>
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Alpha_compositing">Alpha channel -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param baseImage
     *            This image will be changed. <strong>This image should have 3 or 4 channels!</strong>
     * @param injectPosition
     *            Position where will be injected <code>injectImage</code> on <code>baseImage</code>.
     * @param injectImage
     *            This image injected to <code>baseImage</code> image. <strong>This image should have 3 or 4
     *            channels!</strong>
     * @return Image with combination of baseImage and injectImage. That image have same size and type as baseImage and
     *         have 3 channels.
     */
    public static Image injectImage(final Image baseImage, final Point injectPosition, final Image injectImage) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(baseImage);
        JCV.verifyIsNotNull(injectPosition);
        JCV.verifyIsNotNull(injectImage);

        // Verify images.
        if (baseImage.getNumOfChannels() < 3 || baseImage.getNumOfChannels() > 4) {
            throw new IllegalArgumentException(String.format("Channel number of 'baseImage' (= %s) must be 3 or 4!",
                    baseImage.getNumOfChannels()));
        }
        if (injectImage.getNumOfChannels() < 3 || injectImage.getNumOfChannels() > 4) {
            throw new IllegalArgumentException(String.format("Channel number of 'injectImage' (= %s) must be 3 or 4!",
                    injectImage.getNumOfChannels()));
        }

        /*
         * Perform operation.
         */
        final Image result = baseImage.makeCopy();

        final Image baseImageSub = result.makeSubImage(injectPosition.getX(), injectPosition.getY(),
                Math.min(injectImage.getWidth(), baseImage.getWidth() - injectPosition.getX()),
                Math.min(injectImage.getHeight(), baseImage.getHeight() - injectPosition.getY()));
        final Image injectImageSub = injectImage.makeSubImage(0, 0, baseImageSub.getWidth(), baseImageSub.getHeight());

        // Inject images.
        Parallel.pixels(baseImageSub, new PixelsLoop() {

            @Override
            public void execute(final int x, final int y, final int worker) {
                for (int channel = 0; channel < 3; ++channel) {
                    double alpha1;
                    if (injectImageSub.getNumOfChannels() == 3) {
                        alpha1 = 1.0;
                    } else {
                        alpha1 = injectImageSub.get(x, y, 3) / Color.MAX_VALUE;
                    }

                    double alpha2;
                    if (baseImageSub.getNumOfChannels() == 3) {
                        alpha2 = 1.0;
                    } else {
                        alpha2 = baseImageSub.get(x, y, 3) / Color.MAX_VALUE;
                    }

                    double value = alpha1 * injectImageSub.get(x, y, channel)
                            + alpha2 * baseImageSub.get(x, y, channel) * (1.0 - alpha1);
                    if (value > Color.MAX_VALUE) {
                        value = Color.MAX_VALUE;
                    }

                    baseImageSub.set(x, y, channel, JCV.round(value));
                }
            }
        });

        return result;
    }

    /**
     * This method <strong>sum</strong> 2 given images. Analog <strong>union</strong> for binary images and sets.
     * <p>
     * If value of color will be more than <code>{@link Color#MAX_VALUE}</code> this color value set
     * <code>{@link Color#MAX_VALUE}</code>.
     * </p>
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Union_(set_theory)">Union (set theory) -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    public static Image sum(final Image image1, final Image image2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(image1, image2);
        JCV.verifyIsSameChannels(image1, image2);

        /*
         * Perform operation.
         */
        final Image result = image1.makeSame();

        Parallel.pixels(result, new PixelsLoop() {

            @Override
            public void execute(final int x, final int y, final int worker) {
                for (int channel = 0; channel < result.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, image1.get(x, y, channel) + image2.get(x, y, channel));
                }
            }
        });

        return result;
    }

    /**
     * Absolute value of difference between 2 images.
     */
    public static Image absDiff(final Image image1, final Image image2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(image1, image2);
        JCV.verifyIsSameChannels(image1, image2);

        /*
         * Perform operation.
         */
        final Image result = image1.makeSame();

        Parallel.pixels(image1, new PixelsLoop() {

            @Override
            public void execute(final int x, final int y, final int worker) {
                for (int channel = 0; channel < image1.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, Math.abs(image1.get(x, y, channel) - image2.get(x, y, channel)));
                }
            }
        });

        return result;
    }

    /**
     * First image <strong>minus</strong> second image. Analog <strong>complement</strong> for sets.
     * <p>
     * If value of color will be less than <code>{@link Color#MIN_VALUE}</code> this color value set
     * <code>{@link Color#MIN_VALUE}</code>.
     * </p>
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Complement_(set_theory)">Complement (set theory) -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    public static Image minus(final Image image1, final Image image2) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsSameSize(image1, image2);
        JCV.verifyIsSameChannels(image1, image2);

        /*
         * Perform operation.
         */
        final Image result = image1.makeSame();

        Parallel.pixels(result, new PixelsLoop() {

            @Override
            public void execute(final int x, final int y, final int worker) {
                for (int channel = 0; channel < image1.getNumOfChannels(); ++channel) {
                    result.set(x, y, channel, image1.get(x, y, channel) - image2.get(x, y, channel));
                }
            }
        });

        return result;
    }

    /**
     * Return arithmetic mean of colors in current image.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Arithmetic_mean">Arithmetic mean -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    public static Color calculateMean(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Perform operation.
         */
        // Initialize.
        final double[] sum = new double[image.getNumOfChannels()];
        for (int channel = 0; channel < sum.length; ++channel) {
            sum[channel] = 0.0;
        }

        // Sum all values.
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
                    sum[channel] += image.get(x, y, channel);
                }
            }
        }

        // Calculate average.
        final Color mean = new Color(image.getNumOfChannels());
        for (int channel = 0; channel < sum.length; ++channel) {
            mean.set(channel, JCV.round(sum[channel] / (image.getWidth() * image.getHeight())));
        }

        return mean;
    }
}
