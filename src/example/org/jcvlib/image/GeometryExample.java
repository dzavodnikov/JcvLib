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

import org.jcvlib.core.Image;
import org.jcvlib.core.Size;
import org.jcvlib.gui.Window;
import org.jcvlib.image.geometry.Geometry;
import org.jcvlib.image.geometry.Reflection;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to use geometry transformations.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class GeometryExample {

    public static void main(final String[] args) throws IOException {
        // Read image.
        final Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");

        // Resize image.
        final Image reflectD = Geometry.reflect(image, Reflection.DIAGONAL);
        final Image resize = Geometry.resize(image, new Size(800, 800));
        final Image rotate45 = Geometry.rotate(image, 45.0);

        // Show windows with images.
        Window.openAndShow(image, "Lenna");
        Window.openAndShow(reflectD, "Lenna diagonal");
        Window.openAndShow(resize, "Lenna resized");
        Window.openAndShow(rotate45, "Lenna rotate 45");
    }
}
