/*
 * Copyright (c) 2012-2018 JcvLib Team
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
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ColorRaw implements Color {

    private final double[] colors;

    public ColorRaw(final int numOfChannels) {
        // TODO: Verify.
        this.colors = new double[numOfChannels];
    }

    @Override
    public int getNumOfChannels() {
        return this.colors.length;
    }

    @Override
    public double getUnsafe(final int numOfChannel) {
        return this.colors[numOfChannel];
    }

    @Override
    public double get(final int numOfChannel) {

        return getUnsafe(numOfChannel);
    }
}
