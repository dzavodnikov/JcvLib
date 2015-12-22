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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jcvlib.core.Image;
import org.jcvlib.gui.Window;
import org.jcvlib.io.ImageRW;

/**
 * This is example explain how to use <a href="http://en.wikipedia.org/wiki/Pyramid_(image_processing)">pyramid in image
 * processing</a>.
 *
 * <p>
 * We use <a href="http://www.zennaro.net/projects/compvis/gp/gp.htm">Gaussian pyramid</a> for our algorithms.
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class PyramidsExample {

    public static void main(final String[] args) throws IOException {
        // Load image.
        final Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");

        // Show original image.
        Window.openAndShow(image, "Lenna");

        // Create Gaussian pyramid.
        final List<Image> pyramid = Misc.buildPyramidGauss(image);

        // Show images from pyramid.
        for (int i = 0; i < pyramid.size(); ++i) {
            Window.openAndShow(pyramid.get(i), "Lenna -- Gaussian pyramid image #" + Integer.toString(i));
        }
    }
}
