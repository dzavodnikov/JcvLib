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
package org.jcvlib.image.geometry;

import java.util.List;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Interpolation;
import org.jcvlib.core.JCV;
import org.jcvlib.core.Point;
import org.jcvlib.core.Size;
import org.jcvlib.parallel.Parallel;
import org.jcvlib.parallel.PixelsLoop;

import Jama.Matrix;

/**
 * This is a base class for geometry transformations.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Geometry {

    /**
     * Wrap perspective transformation matrix to image.
     *
     * <p>
     * Apply perspective transformation represented as a matrix:
     *
     * <code><pre>
     * | t<sub>i</sub> * x'<sub>i</sub> |   | p<sub>11</sub>  p<sub>12</sub>  p<sub>13</sub> |   | x<sub>i</sub> |
     * | t<sub>i</sub> * y'<sub>i</sub> | = | p<sub>21</sub>  p<sub>22</sub>  p<sub>23</sub> | * | y<sub>i</sub> |
     * | t<sub>i</sub>       |   | p<sub>31</sub>  p<sub>32</sub>  p<sub>33</sub> |   | 1  |
     * </pre></code>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://xenia.media.mit.edu/~cwren/interpolator/">Perspective Transform Estimation</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param P
     *            Perspective matrix with added offset vector (with size <code>[3x3]</code>).
     * @param newSize
     *            Size of result image.
     * @param interpolation
     *            Interpolation method.
     * @param fillColor
     *            Color to fill field near the image.
     * @return Result of image transformation.
     */
    public static Image wrapPerspectiveTransform(final Image image, final Matrix P, final Size newSize,
            final Interpolation interpolation, final Color fillColor) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(P);
        if (P.getRowDimension() != 3 || P.getColumnDimension() != 3) {
            throw new IllegalArgumentException(String.format("Matrix 'P' sould have size [3x3], but have [%sx%s]!",
                    P.getRowDimension(), P.getColumnDimension()));
        }

        /*
         * Perform operation.
         */
        final Image result = new Image(newSize.getWidth(), newSize.getHeight(), image.getNumOfChannels());
        final Matrix invP = P.inverse();

        Parallel.pixels(result, new PixelsLoop() {

            @Override
            public void execute(final int x, final int y, final int worker) {
                for (int channel = 0; channel < result.getNumOfChannels(); ++channel) {
                    final double t = invP.get(2, 0) * x + invP.get(2, 1) * y + invP.get(2, 2);
                    final double nx = (invP.get(0, 0) * x + invP.get(0, 1) * y + invP.get(0, 2)) / t;
                    final double ny = (invP.get(1, 0) * x + invP.get(1, 1) * y + invP.get(1, 2)) / t;

                    double value;
                    if (nx < 0 || nx > image.getWidth() - 1 || ny < 0 || ny > image.getHeight() - 1) {
                        value = fillColor.get(channel);
                    } else {
                        value = image.get(nx, ny, channel, interpolation);
                    }
                    result.set(x, y, channel, JCV.round(value));
                }
            }
        });

        return result;
    }

    /**
     * Calculate perspective transformation matrix from 4 pairs of the corresponding points.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://xenia.media.mit.edu/~cwren/interpolator/">Perspective Transform Estimation</a>.</li>
     * </ol>
     * </p>
     *
     * @param srcPoint
     *            4 source points.
     * @param dstPoint
     *            4 target points.
     * @return Matrix that apply transformation image.
     */
    public static Matrix getPerspectiveTransfrom(final List<Point> srcPoint, final List<Point> dstPoint) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(srcPoint);
        if (srcPoint.size() != 4) {
            throw new IllegalArgumentException(
                    String.format("List 'srcPoint' should have 4 elements, but it have %s!", srcPoint.size()));
        }

        JCV.verifyIsNotNull(dstPoint);
        if (dstPoint.size() != 4) {
            throw new IllegalArgumentException(
                    String.format("List 'dstPoint' should have 4 elements, but it have %s!", dstPoint.size()));
        }

        /*
         * Perform operation.
         */
        final Matrix X = new Matrix(4, 3);
        for (int i = 0; i < X.getColumnDimension(); ++i) {
            X.set(i, 0, srcPoint.get(i).getX());
            X.set(i, 1, srcPoint.get(i).getY());
            X.set(i, 2, 1.0);
        }

        final Matrix Y = new Matrix(4, 3);
        for (int i = 0; i < X.getColumnDimension(); ++i) {
            Y.set(i, 0, dstPoint.get(i).getX());
            Y.set(i, 1, dstPoint.get(i).getY());
            Y.set(i, 2, 1.0);
        }

        // Create transformation matrix and generate error if points are incorrect.
        try {
            return X.solve(Y).transpose();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Wrap affine transformation matrix to image.
     *
     * <p>
     * Apply perspective transformation represented as a matrix:
     *
     * <code><pre>
     * | x'<sub>i</sub>|   | a<sub>11</sub>  a<sub>12</sub>  a<sub>13</sub> |   | x<sub>i</sub> |
     * | y'<sub>i</sub>| = | a<sub>21</sub>  a<sub>22</sub>  a<sub>23</sub> | * | y<sub>i</sub> |
     *                            | 1  |
     * </pre></code>
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Affine_transformation">Affine transformation -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param A
     *            Affine matrix with added offset vector (with size <code>[2x3]</code>).
     * @param newSize
     *            Size of result image.
     * @param interpolation
     *            Interpolation method.
     * @param fillColor
     *            Color to fill field near the image.
     * @return Result of image transformation.
     */
    public static Image wrapAffineTransform(final Image image, final Matrix A, final Size newSize,
            final Interpolation interpolation, final Color fillColor) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        JCV.verifyIsNotNull(A);
        if (A.getRowDimension() != 2 || A.getColumnDimension() != 3) {
            throw new IllegalArgumentException(String.format("Matrix 'A' sould have size [2x3], but have [%sx%s]!",
                    A.getRowDimension(), A.getColumnDimension()));
        }

        /*
         * Perform operation.
         */
        // Create perspective matrix from affine matrix.
        final Matrix P = new Matrix(3, 3);
        P.setMatrix(0, 1, 0, 2, A);
        P.setMatrix(2, 2, 0, 2, new Matrix(new double[][]{ { 0.0, 0.0, 1.0 } }));

        return Geometry.wrapPerspectiveTransform(image, P, newSize, interpolation, fillColor);
    }

    /**
     * Calculate affine transformation matrix from 3 pairs of the corresponding points.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Affine_transformation">Affine transformation -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param srcPoint
     *            3 source points.
     * @param dstPoint
     *            3 target points.
     * @return Matrix that apply transformation image.
     */
    public static Matrix getAffineTransfrom(final List<Point> srcPoint, final List<Point> dstPoint) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(srcPoint);
        if (srcPoint.size() != 3) {
            throw new IllegalArgumentException(
                    String.format("List 'srcPoint' should have 3 elements, but it have %s!", srcPoint.size()));
        }

        JCV.verifyIsNotNull(dstPoint);
        if (dstPoint.size() != 3) {
            throw new IllegalArgumentException(
                    String.format("List 'dstPoint' should have 3 elements, but it have %s!", dstPoint.size()));
        }

        /*
         * Perform operation.
         */
        final Matrix X = new Matrix(3, 3);
        for (int i = 0; i < X.getColumnDimension(); ++i) {
            X.set(i, 0, srcPoint.get(i).getX());
            X.set(i, 1, srcPoint.get(i).getY());
            X.set(i, 2, 1.0);
        }

        final Matrix Y = new Matrix(3, 2);
        for (int i = 0; i < X.getColumnDimension(); ++i) {
            Y.set(i, 0, dstPoint.get(i).getX());
            Y.set(i, 1, dstPoint.get(i).getY());
        }

        // Create transformation matrix and generate error if points are incorrect.
        try {
            return X.solve(Y).transpose();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Reflect image according to selected type.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Mirror_image">Mirror image -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     *
     * @param image
     *            Source image.
     * @param reflectionMethod
     *            Reflection method.
     * @return Image with same size, number of channels and type as source image.
     */
    public static Image reflect(final Image image, final Reflection reflectionMethod) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(reflectionMethod);

        /*
         * Perform operation.
         */
        return Geometry.wrapAffineTransform(image, reflectionMethod.getMatrix(image.getSize()), image.getSize(),
                Interpolation.NEAREST_NEIGHBOR, new Color(image.getNumOfChannels(), Color.MIN_VALUE));
    }

    /**
     * Scale image.
     *
     * @param image
     *            Source image.
     * @param scale
     *            Scale parameter.
     * @param interpolation
     *            Interpolation method.
     * @param fillColor
     *            Color to fill field near the image.
     * @return Image with new size and same number of channels and same type as source image.
     */
    public static Image scale(final Image image, final double scale, final Interpolation interpolation,
            final Color fillColor) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        if (scale <= 0.0) {
            throw new IllegalArgumentException("Parameter 'scale' must be more than 0!");
        }

        /*
         * Perform operation.
         */
        final int newWidth = JCV.round(image.getWidth() * scale);
        final int newHeight = JCV.round(image.getHeight() * scale);
        if (newWidth < 1 || newHeight < 1) {
            throw new IllegalArgumentException("Parameter 'scale' is too small!");
        }
        final Size newSize = new Size(newWidth, newHeight);

        return Geometry.resize(image, newSize, interpolation, fillColor);
    }

    /**
     * Scale image.
     *
     * <p>
     * Use <code>Image.INTERPOLATION_BILINEAR</code> and {@link Color#MIN_VALUE} as default.
     * </p>
     *
     * @param image
     *            Source image.
     * @param scale
     *            Scale parameter.
     * @return Image with new size and same number of channels and same type as source image.
     */
    public static Image scale(final Image image, final double scale) {
        return Geometry.scale(image, scale, Interpolation.BILINEAR,
                new Color(image.getNumOfChannels(), Color.MIN_VALUE));
    }

    /**
     * Resize image.
     *
     * @param image
     *            Source image.
     * @param newSize
     *            New image size.
     * @param interpolation
     *            Interpolation method.
     * @param fillColor
     *            Color to fill field near the image.
     * @return Image with new size and same number of channels and same type as source image.
     */
    public static Image resize(final Image image, final Size newSize, final Interpolation interpolation,
            final Color fillColor) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(newSize);

        /*
         * Perform operation.
         */
        final double scaleX = (double) newSize.getWidth() / (double) image.getWidth();
        final double scaleY = (double) newSize.getHeight() / (double) image.getHeight();

        final Matrix A = new Matrix(new double[][]{ { scaleX, 0.0, 0.0 }, { 0.0, scaleY, 0.0 } });

        return Geometry.wrapAffineTransform(image, A, newSize, interpolation, fillColor);
    }

    /**
     * Resize image.
     *
     * <p>
     * Use <code>Image.INTERPOLATION_BILINEAR</code> and {@link Color#MIN_VALUE} as default.
     * </p>
     *
     * @param image
     *            Source image.
     * @param newSize
     *            New image size.
     * @return Image with new size and same number of channels and same type as source image.
     */
    public static Image resize(final Image image, final Size newSize) {
        return Geometry.resize(image, newSize, Interpolation.BILINEAR,
                new Color(image.getNumOfChannels(), Color.MIN_VALUE));
    }

    /**
     * Rotate image on some degree.
     *
     * @param image
     *            Source image.
     * @param angle
     *            Rotation angle in degrees.
     * @param centerOfRotation
     *            Center of rotation.
     * @param interpolation
     *            Interpolation method.
     * @param fillColor
     *            Color to fill field near the image.
     * @return Rotated image.
     */
    public static Image rotate(final Image image, final double angle, final Point centerOfRotation,
            final Interpolation interpolation, final Color fillColor) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(centerOfRotation);

        /*
         * Perform operation.
         */
        // Correct angle.
        final double rad = (angle / 180.0) * Math.PI;

        /*
         * Create transformation matrix.
         */
        // Rotate kernel.
        //@formatter:off
        final Matrix R = new Matrix(new double[][]{ 
                {  Math.cos(rad), -Math.sin(rad) }, 
                {  Math.sin(rad),  Math.cos(rad) } 
            });
        //@formatter:on

        // Center of shift.
        //@formatter:off
        final Matrix s = new Matrix(new double[][]{ 
                { centerOfRotation.getX() }, 
                { centerOfRotation.getY() }
            });
        //@formatter:on

        // Configure shift values.
        final Matrix b = R.times(s);

        // Create affine matrix.
        final Matrix A = new Matrix(2, 3);
        A.setMatrix(0, 1, 0, 1, R);
        A.setMatrix(0, 1, 2, 2, b);

        // Find shift of result and new size of image.
        final double[] x = new double[4];
        final double[] y = new double[4];

        /*
         * A          B
         *  +--------+
         *  |        |
         *  |        |
         *  |        |
         *  +--------+
         * D          C
         */
        // A (0, 0)
        x[0] = A.get(0, 0) * 0 + A.get(0, 1) * 0 + A.get(0, 2);
        y[0] = A.get(1, 0) * 0 + A.get(1, 1) * 0 + A.get(1, 2);

        // B (width - 1, 0)
        x[1] = A.get(0, 0) * (image.getWidth() - 1) + A.get(0, 1) * 0 + A.get(0, 2);
        y[1] = A.get(1, 0) * (image.getWidth() - 1) + A.get(1, 1) * 0 + A.get(1, 2);

        // C (width - 1, height - 1)
        x[2] = A.get(0, 0) * (image.getWidth() - 1) + A.get(0, 1) * (image.getHeight() - 1) + A.get(0, 2);
        y[2] = A.get(1, 0) * (image.getWidth() - 1) + A.get(1, 1) * (image.getHeight() - 1) + A.get(1, 2);

        // D (0, height - 1)
        x[3] = A.get(0, 0) * 0 + A.get(0, 1) * (image.getHeight() - 1) + A.get(0, 2);
        y[3] = A.get(1, 0) * 0 + A.get(1, 1) * (image.getHeight() - 1) + A.get(1, 2);

        // Find min/max.
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < 4; ++i) {
            if (minX > x[i]) {
                minX = x[i];
            }
            if (minY > y[i]) {
                minY = y[i];
            }

            if (maxX < x[i]) {
                maxX = x[i];
            }
            if (maxY < y[i]) {
                maxY = y[i];
            }
        }

        // Add shift to affine matrix.
        A.set(0, 2, A.get(0, 2) - minX);
        A.set(1, 2, A.get(1, 2) - minY);

        // Create new size for result image.
        final Size newSize = new Size(JCV.roundUp(maxX - minX), JCV.roundUp(maxY - minY));

        return Geometry.wrapAffineTransform(image, A, newSize, interpolation, fillColor);
    }

    /**
     * Rotate image on some degree.
     *
     * <p>
     * Use center of image as center of rotation, <code>Image.INTERPOLATION_BILINEAR</code> and {@link Color#MIN_VALUE}
     * as default.
     * </p>
     *
     * @param image
     *            Source image.
     * @param angle
     *            Rotation angle in degrees.
     * @return Rotated image.
     */
    public static Image rotate(final Image image, final double angle) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);

        /*
         * Perform operation.
         */
        return Geometry.rotate(image, angle, JCV.calculateCenter(image.getWidth(), image.getHeight()),
                Interpolation.BILINEAR, new Color(image.getNumOfChannels(), Color.MIN_VALUE));
    }
}
