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
package org.jcvlib.image;

import java.io.File;
import java.io.IOException;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Point;
import org.jcvlib.core.Region;
import org.jcvlib.gui.Window;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to using function to <a href="http://en.wikipedia.org/wiki/Flood_fill">fill</a> some region.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class FloodFillExample {

    public static final String RESOURCES = "src" + File.separatorChar + "main" + File.separatorChar + "resources";
    public static final String IMAGES    = RESOURCES + File.separatorChar + "images";

    public static void main(final String[] args) throws IOException {
        // Read image.
        final Image imageLenna = ImageRW.read(IMAGES + File.separatorChar + "Lenna.bmp");

        // Fill region by green color.
        final Color color = new Color(new int[] { 0, 255, 0 });
        // Seed
        final Point seed = new Point(0, 0);
        // Distance between colors.
        final double dist = 6.0;

        // Apply flood fill algorithm.
        final Image lennaFillImage = Misc.floodFill(imageLenna, seed, dist, color, Direction.TYPE_8,
                FloodFillRange.NEIGHBOR);

        // Output region characteristics.
        final Region lennaFillRegion = new Region(lennaFillImage, new Color(1, Color.MAX_VALUE));
        System.out.println(lennaFillRegion.toString());

        // Show window with image after fill region.
        Window.openAndShow(imageLenna, "Lenna with fill region");
        Window.openAndShow(lennaFillImage, "Lenna fill region");
    }
}
