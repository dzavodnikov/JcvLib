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
 * Contain <code>(x, y)</code> positions on image.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class PointQueue {

    private final int[] x;
    private final int[] y;

    /**
     * Create new point queue.
     */
    public PointQueue(final int length) {
        this.x = new int[length];
        this.y = new int[length];
    }

    /**
     * Create new point queue.
     */
    private PointQueue(final int[] x, final int[] y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Return X-value of selected point.
     */
    public int getX(final int pos) {
        return this.x[pos];
    }

    /**
     * Set X-value to selected point.
     */
    public int setX(final int pos, final int val) {
        return this.x[pos] = val;
    }

    /**
     * Return Y-value of selected point.
     */
    public int getY(final int pos) {
        return this.y[pos];
    }

    /**
     * Set Y-value to selected point.
     */
    public int setY(final int pos, final int val) {
        return this.y[pos] = val;
    }

    public int getLenght() {
        return this.x.length;
    }

    /**
     * Return copy of current object.
     */
    public PointQueue makeCopy() {
        return new PointQueue(this.x, this.y);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < this.x.length; ++i) {
            result = (prime * result) + this.x[i];
            result = (prime * result) + this.y[i];
        }
        return result;
    }

    /**
     * Return <code>true</code> if current point equivalent to object from parameter and <code>false</code> otherwise.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        PointQueue pq = null;

        if (object instanceof PointQueue) {
            pq = (PointQueue) object;
        } else {
            return false;
        }

        if (pq == this) {
            return true;
        }

        if (getLenght() == pq.getLenght()) {
            for (int i = 0; i < getLenght(); ++i) {
                if (getX(i) != pq.getX(i) || getY(i) != pq.getY(i)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Return string with values of all contained points.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("{");

        final int lastElement = getLenght() - 1;

        for (int i = 0; i < lastElement; ++i) {
            sb.append(new Point(getX(i), getY(i)).toString());
        }

        if (lastElement >= 0) {
            sb.append(new Point(getX(lastElement), getY(lastElement)).toString());
        }

        sb.append("}");

        return sb.toString();
    }
}
