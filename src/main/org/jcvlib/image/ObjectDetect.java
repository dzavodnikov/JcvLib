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
package org.jcvlib.image;

import org.jcvlib.core.Color;
import org.jcvlib.core.Extrapolation;
import org.jcvlib.core.Histogram;
import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.jcvlib.core.KernelOperation;
import org.jcvlib.core.Point;

/**
 * Detect objects on given image.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ObjectDetect {

    /**
     * Match template using <a href="http://en.wikipedia.org/wiki/Euclidean_distance">Euclidean distance</a> between
     * template and sub-image.
     *
     * <p>
     * <strong>Attention! Extremely slow method! Use small images (resize big images) or another methods!</strong>
     * </p>
     *
     * @param image
     *            Source image where we try to find template.
     * @param template
     *            Image with object that we want to find.
     * @return Result image where we will be save result. <strong>Should have same size and 1 channel!</strong> This
     *         image contains average Euclidean distance in normalize form (all values between {@link Color#MIN_VALUE}
     *         and {@link Color#MAX_VALUE}) between template and all sub-images.
     */
    public static Image matchTempleteEuclid(final Image image, final Image template) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(template);

        /*
         * Perform operation.
         */
        final Image result = new Image(image.getWidth(), image.getHeight(), 1);

        image.noneLinearFilter(result, template.getWidth(), template.getHeight(), new Point(0, 0), 1,
                Extrapolation.ZERO, new KernelOperation() {

                    @Override
                    public void execute(final Image aperture, final Color result) {
                        double resultValue = 0.0;
                        final Color apertureColor = new Color(aperture.getNumOfChannels());
                        final Color templateColor = new Color(template.getNumOfChannels());
                        for (int x = 0; x < aperture.getWidth(); ++x) {
                            for (int y = 0; y < aperture.getHeight(); ++y) {
                                aperture.get(x, y, apertureColor);
                                template.get(x, y, templateColor);
                                resultValue += apertureColor.euclidDist(templateColor);
                            }
                        }

                        result.fill(JCV
                                .round(Color.MAX_VALUE - resultValue / (aperture.getWidth() * aperture.getHeight())));
                    }
                });

        return result;
    }

    /**
     * Match template using <a href="http://en.wikipedia.org/wiki/Image_histogram">image histogram</a> between template
     * and sub-image.
     *
     * <p>
     * <strong>Attention! Extremely slow method! Use small images (resize big images) or another methods!</strong>
     * </p>
     *
     * @param image
     *            Source image where we try to find template.
     * @param template
     *            Image with object that we want to find.
     * @param compareType
     *            Type of compare histograms. Use <code>Hist.HISTOGRAM_COMPARE_*</code>.
     * @return Result image where we will be save result. <strong>Should have same size and 1 channel!</strong> This
     *         image contains distance in normalize form (all values between {@link Color#MIN_VALUE} and
     *         {@link Color#MAX_VALUE}) between template and all sub-images.
     */
    public static Image matchTempleteHist(final Image image, final Image template, final int compareType) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(template);

        /*
         * Perform operation.
         */
        double scale = 1.0;
        double offset = 0.0;

        final Histogram templateHist = new Histogram(template);

        switch (compareType) {
            case Histogram.HISTOGRAM_COMPARE_CORREL:
                scale = 0.5 * Color.MAX_VALUE;
                offset = 0.5 * Color.MAX_VALUE;
                break;

            case Histogram.HISTOGRAM_COMPARE_CHISQR:
                scale = -1.0 * Color.MAX_VALUE;
                offset = Color.MAX_VALUE;
                break;

            case Histogram.HISTOGRAM_COMPARE_INTERSECT:
                scale = Color.MAX_VALUE;
                offset = 0.0;
                break;

            case Histogram.HISTOGRAM_COMPARE_BHATTACHARYYA:
                scale = -1.0 * Color.MAX_VALUE;
                offset = Color.MAX_VALUE;
                break;

            default:
                throw new IllegalArgumentException("Unknown compare type! Use 'JCV.HISTOGRAM_COMPARE_*' values!");
        }

        final double proxyScale = scale;
        final double proxyOffset = offset;

        final Image result = new Image(image.getWidth(), image.getHeight(), 1);

        image.noneLinearFilter(result, template.getWidth(), template.getHeight(), new Point(0, 0), 1,
                Extrapolation.ZERO, new KernelOperation() {

                    @Override
                    public void execute(final Image aperture, final Color result) {
                        result.fill(JCV.round(
                                proxyScale * templateHist.compare(new Histogram(aperture), compareType) + proxyOffset));
                    }
                });

        return result;
    }
}
