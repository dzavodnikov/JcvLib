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

/**
 * Region is a structure that contains some characteristics of image region.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Region {

    private int         areaSize;

    private final int   regionX;
    private final int   regionY;
    private final int   regionWidth;
    private final int   regionHeight;

    private final Point centroid;

    private double      secondOrderRowMoment;

    private double      secondOrderMixedMoment;

    private double      secondOrderColumnMoment;

    private double      ellipseMaxAxisOrientation;

    private double      ellipseMaxAxisLength;

    private double      ellipseMinAxisLength;

    /**
     * Calculate some characteristics of filled region.
     *
     * @param regionImage
     *            Source image that will be processed.
     * @param targetColor
     *            Color that define region on source image.
     */
    public Region(final Image regionImage, final Color targetColor) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(regionImage);
        JCV.verifyIsNotNull(targetColor);

        /*
         * Calculate first-order values.
         */
        this.areaSize = 0;

        int regionMinX = 0;
        int regionMaxX = 0;
        int regionMinY = 0;
        int regionMaxY = 0;

        int centroidX = 0;
        int centroidY = 0;

        final Color color = new Color(regionImage.getNumOfChannels());

        for (int x = 0; x < regionImage.getWidth(); ++x) {
            for (int y = 0; y < regionImage.getHeight(); ++y) {
                regionImage.get(x, y, color);
                if (color.equals(targetColor)) {
                    ++this.areaSize;

                    centroidX += x;
                    centroidY += y;

                    if (x < regionMinX) {
                        regionMinX = x;
                    }
                    if (x > regionMaxX) {
                        regionMaxX = x;
                    }
                    if (y < regionMinY) {
                        regionMinY = y;
                    }
                    if (y > regionMaxY) {
                        regionMaxY = y;
                    }
                }
            }
        }
        if (this.areaSize == 0) {
            throw new IllegalArgumentException("Image 'regionImage' have no one point with color 'targetColor'!");
        }

        this.regionX = regionMinX;
        this.regionY = regionMinY;
        this.regionWidth = regionMaxX + 1;
        this.regionHeight = regionMaxY + 1;
        this.centroid = new Point(JCV.round(centroidX / this.areaSize), JCV.round(centroidY / this.areaSize));

        /*
         * Calculate second-order values.
         */
        this.secondOrderRowMoment = 0.0;
        this.secondOrderMixedMoment = 0.0;
        this.secondOrderColumnMoment = 0.0;
        for (int x = 0; x < regionImage.getWidth(); ++x) {
            for (int y = 0; y < regionImage.getHeight(); ++y) {
                regionImage.get(x, y, color);
                if (color.equals(targetColor)) {
                    this.secondOrderRowMoment += Math.pow(y - this.centroid.getY(), 2);
                    this.secondOrderMixedMoment += (x - this.centroid.getX()) * (y - this.centroid.getY());
                    this.secondOrderColumnMoment += Math.pow(x - this.centroid.getX(), 2);
                }
            }
        }
        this.secondOrderRowMoment /= this.areaSize;
        this.secondOrderMixedMoment /= this.areaSize;
        this.secondOrderColumnMoment /= this.areaSize;

        /*
         * Calculate ellipse axis orientation and minimum and maximum length.
         */
        final double pi2 = Math.PI;
        final double a = this.secondOrderRowMoment - this.secondOrderColumnMoment;
        final double b = this.secondOrderRowMoment + this.secondOrderColumnMoment;
        final double c = Math.sqrt(Math.pow(a, 2.0) + 4.0 * Math.pow(this.secondOrderMixedMoment, 2.0));
        if (JCV.equalValues(this.secondOrderMixedMoment, 0.0)) {
            if (this.secondOrderRowMoment > this.secondOrderColumnMoment) {
                this.ellipseMaxAxisOrientation = Math.PI;
                this.ellipseMaxAxisLength = 4.0 * Math.sqrt(this.secondOrderRowMoment);
                this.ellipseMinAxisLength = 4.0 * Math.sqrt(this.secondOrderColumnMoment);
            } else {
                this.ellipseMaxAxisOrientation = Math.PI / 2.0;
                this.ellipseMaxAxisLength = 4.0 * Math.sqrt(this.secondOrderColumnMoment);
                this.ellipseMinAxisLength = 4.0 * Math.sqrt(this.secondOrderRowMoment);
            }
        } else {
            this.ellipseMaxAxisLength = Math.sqrt(8.0 * (b + c));
            this.ellipseMinAxisLength = Math.sqrt(8.0 * (b - c));
            if (this.secondOrderRowMoment > this.secondOrderColumnMoment) {
                this.ellipseMaxAxisOrientation = 1.0 / Math.tan(-2.0 * this.secondOrderMixedMoment / (a + c));
            } else {
                this.ellipseMaxAxisOrientation = 1.0
                        / Math.tan(Math.sqrt(b + c) / (-2.0 * this.secondOrderMixedMoment));
            }
        }
        // Normalize orientation.
        this.ellipseMaxAxisOrientation -= JCV.roundDown(this.ellipseMaxAxisOrientation / pi2) * pi2;
        // Round to X-axis.
        this.ellipseMaxAxisOrientation = Math.abs(this.ellipseMaxAxisOrientation - Math.PI / 2.0);
    }

    /**
     * Return number of pixels into current region.
     */
    public int getAreaSize() {
        return this.areaSize;
    }

    /**
     * Return minimal region position.
     */
    public int getRegionX() {
        return this.regionX;
    }

    /**
     * Return minimal region position.
     */
    public int getRegionY() {
        return this.regionY;
    }

    /**
     * Return region size.
     */
    public int getRegionWidth() {
        return this.regionWidth;
    }

    /**
     * Return region size.
     */
    public int getRegionHeight() {
        return this.regionHeight;
    }

    /**
     * Return centroid (point on image) of current region.
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Centroid">Centroid -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    public Point getCentroid() {
        return this.centroid;
    }

    /**
     * Return second-order row (Y-axis) moment. Used into another characteristics.
     */
    public double getSecondOrderRowMoment() {
        return this.secondOrderRowMoment;
    }

    /**
     * Return second-order mixed (X- and Y-axis) moment. Used into another characteristics.
     */
    public double getSecondOrderMixedMoment() {
        return this.secondOrderMixedMoment;
    }

    /**
     * Return second-order column (X-axis) moment. Used into another characteristics.
     */
    public double getSecondOrderColumnMoment() {
        return this.secondOrderColumnMoment;
    }

    /**
     * Return max axis orientation ellipse (in radian) with regard to X-axis.
     */
    public double getEllipseMaxAxisOrientation() {
        return this.ellipseMaxAxisOrientation;
    }

    /**
     * Return length (in pixels) of maximal ellipse axis.
     */
    public double getEllipseMaxAxisLength() {
        return this.ellipseMaxAxisLength;
    }

    /**
     * Return length (in pixels) of minimal ellipse axis.
     */
    public double getEllipseMinAxisLength() {
        return this.ellipseMinAxisLength;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Area Size (in pixels):                     ");
        sb.append(getAreaSize());
        sb.append("\n");

        sb.append("Region Rectangle:                          ");
        sb.append(JCV.getRectangleString(getRegionX(), getRegionY(), getRegionWidth(), getRegionHeight()));
        sb.append("\n");

        sb.append("Centroid (point):                          ");
        sb.append(getCentroid());
        sb.append("\n");

        sb.append("Second-Order Row Moment (in pixels):       ");
        sb.append(getSecondOrderRowMoment());
        sb.append("\n");

        sb.append("Second-Order Mixed Moment (in pixels):     ");
        sb.append(getSecondOrderMixedMoment());
        sb.append("\n");

        sb.append("Second-Order Column Moment (in pixels):    ");
        sb.append(getSecondOrderColumnMoment());
        sb.append("\n");

        sb.append("Ellipse Max Axis Orientation (in radians): ");
        sb.append(getEllipseMaxAxisOrientation());
        sb.append("\n");

        sb.append("Ellipse Max Axis Length (in pixels):       ");
        sb.append(getEllipseMaxAxisLength());
        sb.append("\n");

        sb.append("Ellipse Min Axis Length (in pixels):       ");
        sb.append(getEllipseMinAxisLength());
        sb.append("\n");

        return sb.toString();
    }
}
