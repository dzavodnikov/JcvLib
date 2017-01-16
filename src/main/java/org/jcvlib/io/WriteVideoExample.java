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
package org.jcvlib.io;

import java.io.File;
import java.io.IOException;

import org.jcvlib.core.Image;

/**
 * This is example show how to write video into the file.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class WriteVideoExample {

    public static final String RESOURCES = "src" + File.separatorChar + "main" + File.separatorChar + "resources";
    public static final String VIDEOS    = RESOURCES + File.separatorChar + "videos";

    public static void main(final String[] args) throws IOException {
        // Define output video file
        final String filePath = VIDEOS + File.separatorChar + "Output.mp4";

        // Read source image.
        final Image image = ImageRW.read(VIDEOS + File.separatorChar + "RGB.png");

        final VideoFileWriter video = new VideoFileWriter(filePath, image.getSize(), 30, 10, 128);
        video.open();

        final int diag = Math.min(image.getWidth(), image.getHeight());
        for (int i = 0; i < diag; ++i) {
            // Draw red line.
            image.set(i, i, 0, 255);
            image.set(i, i, 1, 0);
            image.set(i, i, 2, 0);

            video.addImage(image);
        }
        System.out.println("Video was wrote on disk as: " + filePath);

        video.close();

        // Show result.
        ReadVideoExample.main(new String[] { filePath, filePath });
    }
}
