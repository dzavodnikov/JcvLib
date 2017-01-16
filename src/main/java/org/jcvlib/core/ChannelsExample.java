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
 * This is example show how to work with image channels.
 * <p>
 * Images represents as multilayer arrays. Standard image is 3 (<a href="http://en.wikipedia.org/wiki/RGB">RGB</a> --
 * Read, Green and Blue) or 4 (<a href="http://en.wikipedia.org/wiki/RGB">RGB</a> +
 * <a href="http://en.wikipedia.org/wiki/Alpha_compositing">Alpha</a>) channels.
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ChannelsExample {

    public static final String RESOURCES = "src" + File.separatorChar + "main" + File.separatorChar + "resources";
    public static final String IMAGES    = RESOURCES + File.separatorChar + "images";

    public static void main(final String[] args) throws IOException {
        // Read image.
        final Image image = ImageRW.read(IMAGES + File.separatorChar + "RGB.png");
        Window.openAndShow(image, "Original RGB Image");

        // Get layer -- new image.
        final Image imageRedChannel = image.makeChannel(0);
        Window.openAndShow(imageRedChannel, "Only Red Channel");

        final Image imageGreenChannel = image.makeChannel(1);
        Window.openAndShow(imageGreenChannel, "Only Green Channel");

        final Image imageBlueChannel = image.makeChannel(2);
        Window.openAndShow(imageBlueChannel, "Only Blue Channel");
    }
}
