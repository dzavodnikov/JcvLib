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
package org.jcvlib.core;

import java.io.File;
import java.io.IOException;

import org.jcvlib.gui.Window;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to read and show images.
 *
 * @author Dmitriy Zavodnikov (d.zavodnikov@gmail.com)
 */
public class ReadAndShowImageExample {

    public static void main(final String[] args) throws IOException {
        // Read image.
        final Image image = ImageRW.read("resources" + File.separatorChar + "Lenna.jpg");

        // Show window with image.
        Window.openAndShow(image, "Lenna");
    }
}
