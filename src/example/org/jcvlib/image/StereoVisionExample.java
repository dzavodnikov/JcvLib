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
import org.jcvlib.image.filters.Blur;
import org.jcvlib.image.filters.Filters;
import org.jcvlib.image.filters.Morphology;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to use stereo vision algorithms.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class StereoVisionExample {

    public static void main(final String[] args) throws IOException {
        // Read source image.
        final Image left = ImageRW.read("resources" + File.separatorChar + "Aloe-Left.png");
        final Image right = ImageRW.read("resources" + File.separatorChar + "Aloe-Right.png");

        //final Image grayLeft  = ColorConvert.fromRGBtoGray( left.getLayer(0, 3));
        //final Image grayRight = ColorConvert.fromRGBtoGray(right.getLayer(0, 3));

        // Find distance map.
        //Image map = Stereo.getMap(grayLeft, grayRight);
        Image map = Stereo.getMap(left, right);

        map = Filters.blur(map, new Size(5, 5), Blur.MEDIAN);
        //map = Filters.blur(map, new Size(5, 5), Blur.GAUSSIAN);

        map = Filters.morphology(map, new Size(5, 5), Morphology.CLOSE, 3);

        //map = Filters.blur(map, new Size(5, 5), Blur.GAUSSIAN);

        // Output results.
        Window.openAndShow(left, "Aloe Left Image");
        Window.openAndShow(right, "Aloe Right Image");
        Window.openAndShow(map, "Aloe Distance Map");
    }
}
