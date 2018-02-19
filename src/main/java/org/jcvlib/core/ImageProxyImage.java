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
public class ImageProxyImage implements Image {

    public ImageProxyImage(final Image image, final int x, final int y, final int width, final int height,
            final int channelStart, final int numOfChannels) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getNumOfChannels() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getUnsafe(final int x, final int y, final int channel) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double get(final int x, final int y, final int channel) {
        // TODO Auto-generated method stub
        return 0;
    }
}
