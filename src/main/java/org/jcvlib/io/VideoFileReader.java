/*
 * Copyright (c) 2015-2017 JcvLib Team
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

import java.io.IOException;

import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.jcvlib.core.Size;
import org.jcvlib.image.TypeConvert;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

/**
 * This class allow read video from disk.
 * <p>
 * Video is a stream of images. Because of that we should have a object that will be save state to correct reading.
 * </p>
 * <p>
 * Example of usage: <code><pre>
 * ...
 * // String filePath = ...
 * VideoFileReader video = new VideoFileReader(filePath);
 * ...
 * Image image = null;
 * video.open();
 * if (video.isOpen()) {
 *      image = video.getImage();
 *      ...
 * }
 * ...
 * video.close();
 * ...
 * </pre></code>
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
/*
 * See:
 * * https://github.com/xuggle/xuggle-xuggler/blob/master/src/com/xuggle/xuggler/demos/DecodeAndPlayVideo.java
 * * http://wiki.xuggle.com/Tutorials
 * * http://stackoverflow.com/questions/9727590/what-codecs-does-xuggler-support
 */
public class VideoFileReader implements VideoReader, Runnable {

    /**
     * Tread for reading images from web-camera.
     */
    private Thread        t;

    /**
     * Object for reading video from file.
     */
    private IContainer    container;

    /**
     * Object for reading video from file.
     */
    private IStreamCoder  videoCoder;

    /**
     * Path to file with video.
     */
    private final String  filePath;

    /**
     * Variable to show that device is opened.
     */
    private boolean       isOpen;

    /**
     * Error message.
     */
    private String        errorMessage;

    /**
     * Common image buffer.
     */
    private IVideoPicture bufImg;

    /**
     * Size of getting frame from web-camera.
     */
    private Size          size;

    /**
     * Create new video reader with defined path to existing file.
     *
     * @param filePath
     *            Path to file.
     */
    public VideoFileReader(final String filePath) {
        /*
         * Verify parameters.
         */
        JCV.verifyIsNotNull(filePath);

        /*
         * Configure reader.
         */
        this.filePath = filePath;

        this.bufImg = null;
        this.size = null;

        this.isOpen = false;
        this.errorMessage = null;
    }

    private void generateError(final String errorMessage) throws IOException {
        this.errorMessage = errorMessage;
        checkErrors();
    }

    private void checkErrors() throws IOException {
        if (this.errorMessage != null) {
            throw new IOException(this.errorMessage);
        }
    }

    /**
     * Open file for reading video.
     */
    @Override
    public void open() throws IOException {
        final int maxWaitTime = 10000; // In milliseconds (10^{-3} seconds).
        final int waitStep = 10; // In milliseconds (10^{-3} seconds).
        int timeCounter;

        // Reopened if needed.
        if (isOpen()) {
            close();

            // Wait while video thread will be closed.
            timeCounter = 0;
            while (this.bufImg == null && timeCounter < maxWaitTime) {
                // Wait closed old video file.
                try {
                    Thread.sleep(waitStep);
                } catch (final InterruptedException e) {
                    generateError(e.getMessage());
                }

                // Calculate waiting time.
                timeCounter += waitStep;
            }

            // Generate an error!
            if (this.bufImg == null) {
                generateError("Can not closed video file that was opened!");
            }
        }

        try {
            this.t = new Thread(this);
            this.t.start();

            // Wait while web-camera device is opened.
            this.isOpen = true;
            timeCounter = 0;
            while (this.bufImg == null && isOpen() && timeCounter < maxWaitTime) {
                // Wait other thread.
                try {
                    Thread.sleep(waitStep);
                } catch (final InterruptedException e) {
                    generateError(e.getMessage());
                }

                // Calculate waiting time.
                timeCounter += waitStep;
            }
        } catch (final RuntimeException e) {
            generateError(e.getMessage());
        }

        // Generate an error!
        if (this.bufImg == null && isOpen()) {
            generateError("Can not open video file!");
        }

        // Initialize video size.
        this.size = getImage().getSize();
    }

    /**
     * Check if current file is opened.
     */
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    /**
     * Run thread to read data from video-file.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        try {
            /*
             * 1. Let's make sure that we can actually convert video pixel formats.
             */
            if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
                throw new RuntimeException(
                        "You must install the GPL version of Xuggler (with \"IVideoResampler\" support)!");
            }

            /*
             * 2. Create a Xuggler container object.
             */
            this.container = IContainer.make();

            /*
             * 3. Open up the container.
             */
            if (this.container.open(this.filePath, IContainer.Type.READ, null) < 0) {
                throw new RuntimeException(String.format("Could not open file: %s", this.filePath));
            }

            /*
             * 4. Find first video stream.
             */
            // Query how many streams the call to open found.
            final int numStreams = this.container.getNumStreams();
            // Iterate through the streams to find the first video stream.
            int videoStreamId = -1;
            this.videoCoder = null;
            for (int i = 0; i < numStreams; ++i) {
                // Find the stream object.
                final IStream stream = this.container.getStream(i);

                // Get the pre-configured decoder that can decode this stream.
                final IStreamCoder coder = stream.getStreamCoder();
                if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                    videoStreamId = i;
                    this.videoCoder = coder;
                    break;
                }
            }

            /*
             * 5. Verify video stream.
             */
            // Check if we found video stream.
            if (videoStreamId == -1) {
                throw new RuntimeException(
                        String.format("Could not find video stream in container: %s", this.filePath));
            }
            // Try to open up our decoder so it can do work.
            if (this.videoCoder.open() < 0) {
                throw new RuntimeException(
                        String.format("Could not open video decoder for container: %s", this.filePath));
            }

            /*
             * 6. Check color scheme. If needed create color scheme convertor.
             */
            IVideoResampler resampler = null;
            if (this.videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
                // If this stream is not in BGR24, we're going to need to convert it.
                resampler = IVideoResampler.make(this.videoCoder.getWidth(), this.videoCoder.getHeight(),
                        IPixelFormat.Type.BGR24, this.videoCoder.getWidth(), this.videoCoder.getHeight(),
                        this.videoCoder.getPixelType());
                if (resampler == null) {
                    throw new RuntimeException(
                            String.format("Could not create color space resampler for: %s", this.filePath));
                }
            }

            /*
             * 7. Now, we start walking through the container looking at each packet.
             */
            final IPacket packet = IPacket.make();
            long firstTimestampInStream = Global.NO_PTS;
            long systemClockStartTime = 0;
            while (this.container.readNextPacket(packet) >= 0 && this.isOpen) {
                /*
                 * 8. Check the packet: if it belongs to our video stream.
                 */
                if (packet.getStreamIndex() == videoStreamId) {
                    /*
                     * 9. We allocate a new picture to get the data out of Xuggler.
                     */
                    final IVideoPicture picture = IVideoPicture.make(this.videoCoder.getPixelType(),
                            this.videoCoder.getWidth(), this.videoCoder.getHeight());
                    int offset = 0;
                    while (offset < packet.getSize()) {
                        /*
                         * 10. Decode the video, checking for any errors.
                         */
                        final int bytesDecoded = this.videoCoder.decodeVideo(picture, packet, offset);
                        if (bytesDecoded < 0) {
                            throw new RuntimeException(String.format("Got error decoding video in: %s", this.filePath));
                        }

                        offset += bytesDecoded;
                    }

                    /*
                     * 11. Check the current picture.
                     *
                     * Some decoders will consume data in a packet, but will not be able to
                     * construct a full video picture yet. Therefore you should always check
                     * if you got a complete picture from the decoder.
                     */
                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
                        /*
                         * 12. Convert color packet into BGR24 color scheme if needed.
                         *
                         * If the resampler is not null, that means we didn't get the video
                         * in BGR24 format and need to convert it into BGR24 format.
                         */
                        if (resampler != null) {
                            newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(),
                                    picture.getHeight());
                            if (resampler.resample(newPic, picture) < 0) {
                                throw new RuntimeException(
                                        String.format("Could not resample video from: %s", this.filePath));
                            }
                        }

                        /*
                         * 13. Check color scheme.
                         */
                        if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                            throw new RuntimeException(
                                    String.format("Could not decode video as BGR 24 bit data in: %s", this.filePath));
                        }

                        /*
                         * 14. Wait to out video file with selected rate.
                         */
                        /*
                         * We could just display the images as quickly as we decode them, but it
                         * turns out we can decode a lot faster than you think.
                         *
                         * So instead, the following code does a poor-man's version of trying to
                         * match up the frame-rate requested for each 'IVideoPicture' with the system
                         * clock time on your computer.
                         *
                         * Remember that all Xuggler 'IAudioSamples' and 'IVideoPicture' objects
                         * always give timestamps in Microseconds, relative to the first decoded item.
                         * If instead you used the packet timestamps, they can be in different units
                         * depending on your 'IContainer', and IStream and things can get hairy quickly.
                         */
                        if (firstTimestampInStream == Global.NO_PTS) {
                            // This is our first time through.
                            firstTimestampInStream = picture.getTimeStamp();
                            /*
                             * Get the starting clock time.
                             * So, we can hold up frames until the right time in milliseconds (10^{-3} seconds).
                             */
                            systemClockStartTime = System.currentTimeMillis();
                        } else {
                            // In milliseconds (10^{-3} seconds).
                            final long systemClockCurrentTime = System.currentTimeMillis();
                            // In milliseconds (10^{-3} seconds).
                            final long mSecClockTimeSinceStartOfVideo = systemClockCurrentTime - systemClockStartTime;

                            /*
                             * Compute how long for this frame since the first frame in the stream.
                             * Remember that IVideoPicture and IAudioSamples timestamps are always in MICROSECONDS,
                             * so we divide by 1000 to get milliseconds (10^{-3} seconds).
                             */
                            final long mSecStreamTimeSinceStartOfVideo = (picture.getTimeStamp()
                                    - firstTimestampInStream) / 1000;
                            // And we give ourselves 50 ms of tolerance.
                            final long mSecTolerance = 50;
                            // In milliseconds (10^{-3} seconds).
                            final long mSecToSleep = mSecStreamTimeSinceStartOfVideo
                                    - (mSecClockTimeSinceStartOfVideo + mSecTolerance);
                            if (mSecToSleep > 0) {
                                try {
                                    Thread.sleep(mSecToSleep);
                                } catch (final InterruptedException e) {
                                    /*
                                     * We might get this when the user closes the dialog box, so just return from the method.
                                     */
                                    return;
                                }
                            }
                        }

                        /*
                         * Copy link to created object.
                         */
                        this.bufImg = newPic;
                    }
                }
            }
        } catch (final RuntimeException e) {
            this.errorMessage = e.getMessage();
        }

        // Current stream is closed.
        this.isOpen = false;

        /*
         * 17. Close.
         *
         * Technically since we're exiting anyway, these will be cleaned up by the garbage collector...
         * But because we're nice people and want to be invited places for Christmas, we're going to show how to clean up.
         */
        if (this.container != null) {
            this.container.close();
            this.container = null;
        }

        if (this.videoCoder != null) {
            this.videoCoder.close();
            this.videoCoder = null;
        }
    }

    /**
     * Return image from video stream.
     */
    @SuppressWarnings("deprecation")
    @Override
    public Image getImage() throws IOException {
        checkErrors();

        /*
         * 16. Convert the BGR24 to an Java BufferedImage.
         */
        return TypeConvert.fromBufferedImage(Utils.videoPictureToImage(this.bufImg));
    }

    /**
     * Return size of image that get from camera.
     */
    public Size getSize() throws IOException {
        return this.size;
    }

    /**
     * Close current device and stop reading data from it.
     */
    @Override
    public void close() {
        this.isOpen = false;
    }
}
