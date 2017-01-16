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
import java.util.LinkedList;
import java.util.List;

import org.jcvlib.core.Color;
import org.jcvlib.core.Image;
import org.jcvlib.core.Interpolation;
import org.jcvlib.core.Point;
import org.jcvlib.core.Size;
import org.jcvlib.gui.Window;
import org.jcvlib.image.geometry.Geometry;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to perspective transform works.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class PerspectiveTransformExample {

    public static final String RESOURCES = "src" + File.separatorChar + "main" + File.separatorChar + "resources";
    public static final String IMAGES    = RESOURCES + File.separatorChar + "images";

    public static void main(final String[] args) throws IOException {
        // Read image.
        final Image image = ImageRW.read(IMAGES + File.separatorChar + "Calibr.png");

        // Find transform.
        final List<Point> srcPoint = new LinkedList<>();
        srcPoint.add(new Point(49, 83));
        srcPoint.add(new Point(210, 66));
        srcPoint.add(new Point(238, 174));
        srcPoint.add(new Point(67, 207));

        final List<Point> dstPoint = new LinkedList<>();
        dstPoint.add(new Point(0, 0));
        dstPoint.add(new Point(400, 0));
        dstPoint.add(new Point(400, 300));
        dstPoint.add(new Point(0, 300));

        // Apply transform.
        final Image calibr = Geometry.wrapPerspectiveTransform(image,
                Geometry.getPerspectiveTransfrom(srcPoint, dstPoint), new Size(400, 300), Interpolation.BILINEAR,
                new Color(image.getNumOfChannels(), Color.MIN_VALUE));

        // Show windows with images.
        Window.openAndShow(image, "Source image");
        Window.openAndShow(calibr, "Transform image");
    }
}
