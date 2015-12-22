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
import org.jcvlib.core.Point;
import org.jcvlib.gui.Window;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to inject one image to another.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class InjectImageExample {

    public static void main(final String[] args) throws IOException {
        // Read image.
        final Image baseImage = ImageRW.read("resources" + File.separatorChar + "Lenna.bmp");
        final Image injectImage = ImageRW.read("resources" + File.separatorChar + "JcvLibLogo.png");

        // Inject image.
        final Image result = Misc.injectImage(baseImage,          // Source image.
                new Point(30, 0),   // Injected point.
                injectImage         // Injected image.
        );

        // Show window with images.
        Window.openAndShow(baseImage, "Base image");
        Window.openAndShow(injectImage, "Inject image");
        Window.openAndShow(result, "Image after inject");
    }
}
