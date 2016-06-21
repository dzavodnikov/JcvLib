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

import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link Histogram} .
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class HistogramTest {

    // Model.
    private Histogram model;

    // Half-match.
    private Histogram hMatch;

    // Mismatch.
    private Histogram mMatch;

    private double    BHATTACHARYYA_PRECISION = 0.01;

    /**
     * Executes before creating instance of the class.
     */
    @Before
    public void setUp() throws Exception {
        final Image image = new Image(2, 1, 1);

        // Model.
        image.set(0, 0, 0, 255);
        image.set(1, 0, 0, 255);
        this.model = new Histogram(image, 2);

        // Half-match.
        image.set(0, 0, 0, 0);
        image.set(1, 0, 0, 255);
        this.hMatch = new Histogram(image, 2);

        // Mismatch.
        image.set(0, 0, 0, 0);
        image.set(1, 0, 0, 0);
        this.mMatch = new Histogram(image, 2);
    }

    private void checkNorm(Histogram hist) {
        double sum = 0.0;
        for (int i = 0; i < hist.getLength(); ++i) {
            sum += hist.get(i);
        }
        assertEquals(1.0, sum, JCV.PRECISION);
    }

    /**
     * Test method for: {@link Histogram}.
     */
    @Test
    public void testCalculateHistogram() {
        final Image imageCh1 = new Image(100, 100, 1);
        imageCh1.fill(new Color(1, Color.MAX_VALUE));

        final Image imageCh3 = new Image(10, 10, 3);
        imageCh3.fill(new Color(3, Color.MAX_VALUE));

        /*
         * First histogram.
         */
        final Histogram histCh1 = new Histogram(imageCh1);
        final Histogram histCh3 = new Histogram(imageCh3);

        // Check size.
        assertEquals(256, histCh1.getLength());
        assertEquals(256 * 256 * 256, histCh3.getLength());

        // Check normalize.
        this.checkNorm(histCh1);
        this.checkNorm(histCh3);

        // Check values.
        // Single-channel.
        for (int i = 0; i < histCh1.getLength() - 1; ++i) {
            assertEquals(0.0, histCh1.get(i), JCV.PRECISION);
        }
        assertEquals(1.0, histCh1.get(histCh1.getLength() - 1), JCV.PRECISION);
        // Multichannel.
        for (int i = 0; i < histCh3.getLength() - 1; ++i) {
            assertEquals(0.0, histCh3.get(i), JCV.PRECISION);
        }
        assertEquals(1.0, histCh3.get(histCh3.getLength() - 1), JCV.PRECISION);
    }

    /**
     * Test method for: {@link Histogram#compare(Histogram, int)}.
     */
    @Test
    public void testCorrel() {
        //        /*
        //         * See: http://easycalculation.com/statistics/learn-correlation.php
        //         */
        //        final Hist H1 = new Hist(new double[]{ 60.0, 61.0, 62.0, 63.0, 65.0 }, 1, 1);
        //        final Hist H2 = new Hist(new double[]{ 3.1, 3.6, 3.8, 4.0, 4.1 }, 1, 1);
        //        assertEquals(13.9 / 15.24336, H1.compare(H2, Hist.HISTOGRAM_COMPARE_CORREL), 0.0001);

        assertEquals(1.0, this.model.compare(this.model, Histogram.HISTOGRAM_COMPARE_CORREL), JCV.PRECISION);
        assertEquals(0.0, this.model.compare(this.hMatch, Histogram.HISTOGRAM_COMPARE_CORREL), JCV.PRECISION);
        assertEquals(-1.0, this.model.compare(this.mMatch, Histogram.HISTOGRAM_COMPARE_CORREL), JCV.PRECISION);
    }

    /**
     * Test method for: {@link Histogram#compare(Histogram, int)}.
     */
    @Test
    public void testChiSqr() {
        //        /*
        //         * See: http://www.slideshare.net/mhsgeography/chi-square-worked-example
        //         */
        //        final Hist H1 = new Hist(new double[]{ 10.0, 10.0, 10.0, 10.0, 10.0 }, 1, 1);
        //        final Hist H2 = new Hist(new double[]{  4.0,  6.0, 14.0, 10.0, 16.0 }, 1, 1);
        //        assertEquals(10.4, H1.compare(H2, Hist.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION_MAX);

        assertEquals(0.00, this.model.compare(this.model, Histogram.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION);
        assertEquals(0.25, this.model.compare(this.hMatch, Histogram.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION);
        assertEquals(1.00, this.model.compare(this.mMatch, Histogram.HISTOGRAM_COMPARE_CHISQR), JCV.PRECISION);
    }

    /**
     * Test method for: {@link Histogram#compare(Histogram, int)}.
     */
    @Test
    public void testIntersect() {
        assertEquals(1.0, this.model.compare(this.model, Histogram.HISTOGRAM_COMPARE_INTERSECT), JCV.PRECISION);
        assertEquals(0.5, this.model.compare(this.hMatch, Histogram.HISTOGRAM_COMPARE_INTERSECT), JCV.PRECISION);
        assertEquals(0.0, this.model.compare(this.mMatch, Histogram.HISTOGRAM_COMPARE_INTERSECT), JCV.PRECISION);
    }

    /**
     * Test method for: {@link Histogram#compare(Histogram, int)}.
     */
    @Test
    public void testBhattacharyya() {
        assertEquals(0.00, this.model.compare(this.model, Histogram.HISTOGRAM_COMPARE_BHATTACHARYYA), JCV.PRECISION);
        assertEquals(0.55, this.model.compare(this.hMatch, Histogram.HISTOGRAM_COMPARE_BHATTACHARYYA),
                BHATTACHARYYA_PRECISION);
        assertEquals(1.00, this.model.compare(this.mMatch, Histogram.HISTOGRAM_COMPARE_BHATTACHARYYA), JCV.PRECISION);
    }
}
