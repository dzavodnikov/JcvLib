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

import org.jcvlib.parallel.Parallel;
import org.jcvlib.parallel.PixelsLoop;

/**
 * This class contains methods for manipulate image histograms.
 * <p>
 * <h6>Links:</h6>
 * <ol>
 * <li><a href="http://en.wikipedia.org/wiki/Image_histogram">Image histogram -- Wikipedia</a>.</li>
 * </ol>
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Histogram {

    /**
     * Correlation comparison.
     * <p>
     * Return values in interval <code>[-1.0, 1.0]</code>
     * </p>
     * <p>
     * Examples:
     * <ul>
     * <li>Exact match -- <code>1.0</code></li>
     * <li>Half match -- <code>0.0</code></li>
     * <li>Total mismatch -- <code>-1.0</code></li>
     * </ul>
     * </p>
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Correlation_and_dependence">Correlation and dependence -- Wikipedia</a>
     * .</li>
     * </ol>
     * </p>
     */
    public static final int HISTOGRAM_COMPARE_CORREL        = 0;

    /**
     * Chi-Square comparison.
     * <p>
     * Return values in interval <code>[0.0, 1.0]</code>
     * </p>
     * <p>
     * Examples:
     * <ul>
     * <li>Exact match -- <code>0.0</code></li>
     * <li>Half match -- <code>0.25</code></li>
     * <li>Total mismatch -- <code>1.0</code></li>
     * </ul>
     * </p>
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Pearson's_chi-squared_test">Pearson's chi-squared test -- Wikipedia</a>
     * .</li>
     * </ol>
     * </p>
     */
    public static final int HISTOGRAM_COMPARE_CHISQR        = 1;

    /**
     * Intersection comparison.
     * <p>
     * Return values in interval <code>[0.0, 1.0]</code>.
     * </p>
     * <p>
     * Examples:
     * <ul>
     * <li>Exact match -- <code>1.0</code></li>
     * <li>Half match -- <code>0.5</code></li>
     * <li>Total mismatch -- <code>0.0</code></li>
     * </ul>
     * </p>
     * <code><pre>
     * d(H<sub>1</sub>, H<sub>2</sub>) = Sum<sub>i</sub>( Min(H<sub>1</sub>(i), H<sub>2</sub>(i)) )
     * </pre></code>
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Intersection_(set_theory)">Intersection (set theory) -- Wikipedia</a>.
     * </li>
     * </ol>
     * </p>
     */
    public static final int HISTOGRAM_COMPARE_INTERSECT     = 2;

    /**
     * Bhattacharyya comparison.
     * <p>
     * Return values in interval <code>[0.0, 1.0]</code>
     * </p>
     * <p>
     * Examples:
     * <ul>
     * <li>Exact match -- <code>0.0</code></li>
     * <li>Half match -- <code>0.55</code></li>
     * <li>Total mismatch -- <code>1.0</code></li>
     * </ul>
     * </p>
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Bhattacharyya_distance">Bhattacharyya distance -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    public static final int HISTOGRAM_COMPARE_BHATTACHARYYA = 3;

    private final double[]  histogram;

    private final int       size;

    private final int       channels;

    private final double    blob;

    private int calcLength() {
        int res = 1;
        for (int i = 0; i < this.channels; ++i) {
            res *= this.size;
        }
        return res;
    }

    private int calcPos(final Image image, final int x, final int y) {
        double base = 1.0;
        int val = 0;
        for (int channel = 0; channel < image.getNumOfChannels(); ++channel) {
            val += JCV.roundDown(image.get(x, y, channel) * base / this.blob);
            base *= this.size;
        }
        return val;
    }

    /**
     * Create image histogram.
     *
     * @param image
     *            Source image.
     * @param size
     *            Size of histogram per channel.
     */
    public Histogram(final Image image, final int size) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        if (size <= 0) {
            throw new IllegalArgumentException("Value of \"size\" should be more than 0!");
        }

        /*
         * Perform operation.
         */
        this.size = size;
        this.channels = image.getNumOfChannels();
        this.blob = (1.0 + Color.MAX_VALUE) / this.size;

        // Initialize histogram.
        this.histogram = new double[this.calcLength()];
        for (int i = 0; i < this.histogram.length; ++i) {
            this.histogram[i] = 0.0;
        }

        // Calculate.
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                this.histogram[this.calcPos(image, x, y)] += 1.0;
            }
        }
        this.normalize();
    }

    /**
     * Create image histogram. Use <code>256</code> elements per channel by default.
     */
    public Histogram(final Image image) {
        this(image, JCV.roundDown(Color.MAX_VALUE) + 1);
    }

    /**
     * @return Number of channels source image.
     */
    public int getChannels() {
        return this.channels;
    }

    /**
     * @return Length of current histogram.
     */
    public int getLength() {
        return this.histogram.length;
    }

    /**
     * Return value of selected histogram bin.
     */
    public double get(int bin) {
        return this.histogram[bin];
    }

    private void normalize() {
        double sum = 0.0;
        for (int i = 0; i < this.histogram.length; ++i) {
            sum += this.histogram[i];
        }

        for (int i = 0; i < this.histogram.length; ++i) {
            this.histogram[i] /= sum;
        }
    }

    /**
     * Return variance of current histogram.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Variance">Variance -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    public double calculateVariance() {
        final double average = 1.0 / this.getLength();
        double sum = 0.0;
        for (int i = 0; i < this.getLength(); ++i) {
            sum += Math.pow(this.get(i) - average, 2);
        }
        return sum;
    }

    // TODO
    public Histogram resize(final int newSize) {
        return null;
    }

    /**
     * Compare current histogram.
     * <p>
     * <strong>Histograms must have the same size!</strong>
     * </p>
     *
     * @param hist
     *            Second histogram to compare.
     * @param compareType
     *            Type of compare histograms. Use <code>Hist.HISTOGRAM_COMPARE_*</code>.
     */
    public double compare(final Histogram hist, final int compareType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(hist);
        if (this.getLength() != hist.getLength()) {
            throw new IllegalArgumentException("Length of current histogram (= " + Integer.toString(this.getLength())
                    + ") and length of given histogram (= " + Integer.toString(hist.getLength())
                    + ") is not the same!");
        }

        /*
         * Perform operation.
         */
        final double average = 1.0 / this.getLength();
        double result = 0.0;
        double num = 0.0;
        double denSq = 0.0;
        switch (compareType) {
            case Histogram.HISTOGRAM_COMPARE_CORREL:
                // Calculate numerator and denominator.
                for (int i = 0; i < this.getLength(); ++i) {
                    num += (this.get(i) - average) * (hist.get(i) - average);
                }

                denSq = Math.sqrt(this.calculateVariance() * hist.calculateVariance());
                if (!JCV.equalValues(denSq, 0.0, JCV.PRECISION) && !Double.isNaN(denSq)) {
                    result = num / denSq;
                }

                break;

            case Histogram.HISTOGRAM_COMPARE_CHISQR:
                for (int i = 0; i < this.getLength(); ++i) {
                    if (this.get(i) > 0) {
                        double diff = this.get(i) - hist.get(i);
                        result += (diff * diff) / this.get(i);
                    }
                }

                break;

            case Histogram.HISTOGRAM_COMPARE_INTERSECT:
                for (int i = 0; i < this.getLength(); ++i) {
                    result += Math.min(this.get(i), hist.get(i));
                }

                break;

            case Histogram.HISTOGRAM_COMPARE_BHATTACHARYYA:
                denSq = average * this.getLength();

                for (int i = 0; i < this.getLength(); ++i) {
                    num += Math.sqrt(this.get(i) * hist.get(i));
                }

                final double diff = 1.0 - num / denSq;
                if (diff < 0.0) {
                    result = 0.0;
                } else {
                    result = Math.sqrt(diff);
                }

                break;

            default:
                throw new IllegalArgumentException(
                        "Unknown compare type! Use \"Histogram.HISTOGRAM_COMPARE_*\" values!");
        }

        return result;
    }

    /**
     * Threshold to zero. <code><pre>
     * if hist(i) <= threshold
     *      hist(i) := 0
     * else
     *      // Do nothing.
     * </pre></code>
     */
    public void threshold(final double threshold) {
        /*
         * Verify parameters.
         */
        if (threshold < 0.0 || threshold > 1.0) {
            throw new IllegalArgumentException(
                    "Parameter \"threshold\" (= " + Double.toString(threshold) + ") should be in interval [0.0, 1.0]!");
        }

        /*
         * Perform operation.
         */
        for (int i = 0; i < this.getLength(); ++i) {
            if (this.get(i) < threshold) {
                this.histogram[i] = 0.0;
            }
        }
        this.normalize();
    }

    public Image selectPixels(final Image image) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Perform operation.
         */
        final Image result = new Image(image.getWidth(), image.getHeight(), 1);
        Parallel.pixels(image, new PixelsLoop() {

            @Override
            public void execute(final int x, final int y, final int worker) {
                final int val;
                if (histogram[calcPos(image, x, y)] > 0.0) {
                    // TODO
                    val = Color.MAX_VALUE;
                } else {
                    val = Color.MIN_VALUE;
                }
                result.set(x, y, 0, val);
            }
        });
        return result;
    }
}
