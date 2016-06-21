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
package org.jcvlib.parallel;

import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.jparfor.JLoop;
import org.jparfor.JParFor;

/**
 * Class for parallelization image processing algorithms.
 * <p>
 * Example: <code><pre>
 * // Image image = ...
 * Parallel.channels(image, new ChannelsLoopI() {
 *         {@literal @}Override
 *         public void execute(int channel) {
 *             // Do something.
 *         }
 *     });
 * </pre></code> or <code><pre>
 * // Image image = ...
 * Parallel.pixels(image, new JcvPixelsLoopI() {
 *          {@literal @}Override
 *          public void execute(int x, int y) {
 *              // Do something.
 *          }
 *      });
 * </pre></code>
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class Parallel {

    /**
     * Minimal image size to parallelization by default: 160 x 120 = 19 200 elements).
     */
    public static final int MIN_SIZE_DEFAULT = 160 * 120;

    private static int      currentMinSize   = MIN_SIZE_DEFAULT;

    /**
     * Return minimal size for parallelization.
     */
    public static int getMinSize() {
        return currentMinSize;
    }

    /**
     * Set min size for parallelization.
     */
    public static void setMinSize(final int minSize) {
        currentMinSize = minSize;
    }

    /**
     * Return number of worker that will be used.
     */
    public static int getNumOfWorkers() {
        return JParFor.getMaxWorkers();
    }

    /**
     * Set number of workers that will be used.
     */
    public static void setNumOfWorkers(final int maxWork) {
        JParFor.setMaxWorkers(maxWork);
    }

    /**
     * Parallel processing channels of image.
     */
    public static void channels(final Image image, final ChannelsLoop runner) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(runner);

        /*
         * Perform operation.
         */
        JParFor.setMinIterations(
                JCV.roundUp((double) Parallel.getMinSize() / (double) (image.getWidth() * image.getHeight())));
        JParFor.exec(image.getNumOfChannels(), new JLoop() {

            @Override
            public void execute(final int channel, final int nThread) {
                runner.execute(channel);
            }
        });
    }

    /**
     * Parallel processing pixels of image.
     *
     * @param image
     *            Source image.
     * @param runner
     *            Object to process image on each loop step.
     */
    public static void pixels(final Image image, final PixelsLoop runner) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(image);
        JCV.verifyIsNotNull(runner);

        /*
         * Perform operation.
         */
        JParFor.setMinIterations(JCV.roundUp(Parallel.getMinSize() / image.getWidth() + 1.0));
        JParFor.exec(image.getHeight(), new JLoop() {

            @Override
            public void execute(final int y, final int nThread) {
                for (int x = 0; x < image.getWidth(); ++x) {
                    runner.execute(x, y, nThread);
                }
            }
        });
    }
}
