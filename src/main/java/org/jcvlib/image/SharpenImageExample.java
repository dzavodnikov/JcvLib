/*
 * Copyright (c) 2012-2016 JcvLib Team
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

import org.jcvlib.core.Image;
import org.jcvlib.gui.Window;
import org.jcvlib.image.filters.Filters;
import org.jcvlib.image.filters.Sharpen;
import org.jcvlib.io.ImageRW;

/**
 * This is example show how to use sharpen filters.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class SharpenImageExample {

    public static final String RESOURCES = "src" + File.separatorChar + "main" + File.separatorChar + "resources";
    public static final String IMAGES    = RESOURCES + File.separatorChar + "images";

    public static void main(final String[] args) throws IOException {
        // Read image.
        final Image image = ImageRW.read(IMAGES + File.separatorChar + "Lenna.bmp");

        // Sharpen using Laplacian method.
        final Image laplacianSharpen = Filters.sharpen(image, Sharpen.LAPLACIAN);

        // Modern sharpen method using special matrix.
        final Image modernSharpen = Filters.sharpen(image, Sharpen.MODERN);

        // Show window with images.
        Window.openAndShow(image, "Lenna");
        Window.openAndShow(laplacianSharpen, "Lenna Laplacian Sharpen");
        Window.openAndShow(modernSharpen, "Lenna Modern Sharpen");
    }
}
