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

import java.util.ArrayList;
import java.util.List;

import org.jcvlib.core.Point;
import org.jcvlib.core.Size;

/**
 * Contains direction types.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public enum Direction {
    /**
     * Define 4 directions type.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Flood_fill">Flood fill -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    TYPE_4 {

        @Override
        public int getNumOfNeighbors() {
            return 4;
        }

        @Override
        public List<Point> getNeighbors(final Point point, final Size imageSize) {
            final List<Point> neighbors = new ArrayList<Point>();

            /*   3
             * 1 x 2
             *   4
             */
            if (point.getX() > 0) {
                // (x - 1, y)
                neighbors.add(new Point(point.getX() - 1, point.getY()));
            }
            if (point.getX() < imageSize.getWidth() - 1) {
                // (x + 1, y)
                neighbors.add(new Point(point.getX() + 1, point.getY()));
            }
            if (point.getY() > 0) {
                // (x, y - 1)
                neighbors.add(new Point(point.getX(), point.getY() - 1));
            }
            if (point.getY() < imageSize.getHeight() - 1) {
                // (x, y + 1)
                neighbors.add(new Point(point.getX(), point.getY() + 1));
            }

            return neighbors;
        }
    },

    /**
     * Define 8 directions type.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.wikipedia.org/wiki/Flood_fill">Flood fill -- Wikipedia</a>.</li>
     * </ol>
     * </p>
     */
    TYPE_8 {

        @Override
        public int getNumOfNeighbors() {
            return 8;
        }

        @Override
        public List<Point> getNeighbors(final Point point, final Size imageSize) {
            final List<Point> neighbors = new ArrayList<Point>();

            /* 1   3
             *   x
             * 2   4
             */
            if (point.getX() > 0 && point.getY() > 0) {
                // (x - 1, y - 1)
                neighbors.add(new Point(point.getX() - 1, point.getY() - 1));
            }
            if (point.getX() > 0 && point.getY() < imageSize.getHeight() - 1) {
                // (x - 1, y + 1)
                neighbors.add(new Point(point.getX() - 1, point.getY() + 1));
            }
            if (point.getX() < imageSize.getWidth() - 1 && point.getY() > 0) {
                // (x + 1, y - 1)
                neighbors.add(new Point(point.getX() + 1, point.getY() - 1));
            }
            if (point.getX() < imageSize.getWidth() - 1 && point.getY() < imageSize.getHeight() - 1) {
                // (x + 1, y + 1)
                neighbors.add(new Point(point.getX() + 1, point.getY() + 1));
            }

            // And add all neighbors from type 4.
            neighbors.addAll(TYPE_4.getNeighbors(point, imageSize));

            /* 1 7 3
             * 5 x 6
             * 2 8 4
             */
            return neighbors;
        }
    };

    public abstract int getNumOfNeighbors();

    public abstract List<Point> getNeighbors(final Point point, final Size imageSize);
}
