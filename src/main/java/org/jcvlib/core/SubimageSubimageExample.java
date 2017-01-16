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

import java.io.File;
import java.io.IOException;

import org.jcvlib.gui.Window;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to work with sub-images of existing sub-images.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class SubimageSubimageExample {

    public static final String RESOURCES = "src" + File.separatorChar + "main" + File.separatorChar + "resources";
    public static final String IMAGES    = RESOURCES + File.separatorChar + "images";

    public static void main(final String[] args) throws IOException {
        // Read image.
        final Image image = ImageRW.read(IMAGES + File.separatorChar + "Lenna.bmp");

        // Get sub-image.
        final Image subImage = image.makeSubImage(100, 200, 400, 300);

        // Do some modifications with sub-image.
        for (int x = 0; x < 10; ++x) {
            for (int y = 0; y < 20; ++y) {
                // Draw red square on sub-image.
                subImage.set(x, y, 0, 255); // Red
                subImage.set(x, y, 1, 0); // Green
                subImage.set(x, y, 2, 0); // Blue
            }
        }

        // Get sub-image.
        final Image subSubImage = subImage.makeSubImage(50, 0, 300, 200);

        // Do some modifications with sub-sub-image.
        for (int x = 0; x < 20; ++x) {
            for (int y = 0; y < 10; ++y) {
                // Draw red square on sub-image.
                subSubImage.set(x, y, 0, 0); // Red
                subSubImage.set(x, y, 1, 255); // Green
                subSubImage.set(x, y, 2, 0); // Blue
            }
        }

        // Show window with images.
        Window.openAndShow(image, "Lenna");
        Window.openAndShow(subImage, "Lenna sub-image");
        Window.openAndShow(subSubImage, "Lenna sub-sub-image");
    }
}
