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
package org.jcvlib.io;

import java.io.IOException;

import org.jcvlib.core.Image;
import org.jcvlib.core.JCV;
import org.jcvlib.core.Size;
import org.jcvlib.image.TypeConvert;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IMetaData;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

/**
 * This class allow get images from web-cameras.
 *
 * <p>
 * Example of usage:
 * 
 * <code><pre>
 * ...
 * int numberOfWebCam = 0; // First web-camera.
 * int FPS = 30;
 * Size sizeOfGettingImage = new Size(320, 240);
 * // Initialize web-camera.
 * WebCamReader webCam = new WebCamReader(numberOfWebCam, FPS, sizeOfGettingImage);
 * ...
 * Image image = null;
 * webCam.open();
 * if (webCam.isOpen()) {
 *      image = webCam.getImage();
 *      ...
 * }
 * ...
 * webCam.close();
 * ...
 * </pre></code>
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
/*
 * Based on examples from:
 *      https://github.com/xuggle/xuggle-xuggler/blob/master/src/com/xuggle/xuggler/demos/DisplayWebcamVideo.java
 */
public class WebCamReader implements VideoReader, Runnable {

    /**
     * Tread for reading images from web-camera.
     */
    private Thread        t;

    /**
     * Object for reading video from web-cameras.
     */
    private IContainer    container;

    /**
     * Objects for reading video from web-cameras.
     */
    private IStreamCoder  videoCoder;

    /**
     * Name of OS-specific driver to read data from web-camera.
     */
    private String        driverName;

    /**
     * Define path to web-camera.
     */
    private String        deviceName;

    /**
     * Define number of device.
     */
    private int           numWebCam;

    /**
     * Variable to show that device is opened.
     */
    private boolean       isOpen;

    /**
     * Error message.
     */
    private String        errorMessage;

    /**
     * FPS (Frame Per Second).
     */
    private int           FPS;

    /**
     * Size of getting frame from web-camera.
     */
    private Size          size;

    /**
     * Common image buffer.
     */
    private IVideoPicture bufImg;

    /**
     * Open first web-camera (number 0) with 30 FPS and 320x240 picture size.
     */
    public WebCamReader() {
        this(0);
    }

    /**
     * Open web-camera with <code>30</code> FPS and <code>320x240</code> picture size.
     *
     * @param numWebCam
     *            Number of web-camera to open.
     */
    public WebCamReader(final int numWebCam) {
        this(numWebCam, 30, new Size(320, 240));
    }

    /**
     * Open web-camera with defined web-camera number, <a href="http://en.wikipedia.org/wiki/Frame_rate">FPS</a> and
     * size of getting image.
     *
     * @param numWebCam
     *            Number of web-camera to open.
     * @param FPS
     *            <a href="http://en.wikipedia.org/wiki/Frame_rate">FPS (Frames Per Second)</a>.
     * @param sizeOfImage
     *            Size of getting image
     */
    public WebCamReader(final int numWebCam, final int FPS, final Size sizeOfImage) {
        if (numWebCam < 0) {
            throw new IllegalArgumentException(
                    String.format("Parameter 'numWebCam' (=%s) must be more or equals 0!", numWebCam));
        }
        this.numWebCam = numWebCam;

        if (FPS < 0) {
            throw new IllegalArgumentException(String.format("Parameter 'FPS' (=%s) must be more or equals 0!", FPS));
        }
        this.FPS = FPS;

        JCV.verifyIsNotNull(sizeOfImage);
        this.size = sizeOfImage;

        if (JCV.isLinux()) {
            this.driverName = "video4linux2";
            this.deviceName = String.format("/dev/video%s", this.numWebCam);
        } else if (JCV.isWindows()) {
            this.driverName = "vfwcap";
            this.deviceName = Integer.toString(numWebCam);
        } else {
            throw new RuntimeException("Current operation system or architecture is not supported!");
        }

        this.bufImg = null;
    }

    private void generateError(final String errorMessage) throws IOException {
        this.errorMessage = errorMessage;
        checkErrors();
    }

    private void checkErrors() throws IOException {
        if (this.errorMessage != null) {
            this.close();
            throw new IOException(this.errorMessage);
        }
    }

    /**
     * Open current device.
     */
    @Override
    public void open() throws IOException {
        final int maxWaitTime = 10000;  // In milliseconds (10^{-3} seconds).
        final int waitStep = 10;        // In milliseconds (10^{-3} seconds).
        int timeCounter;

        // Reopened if needed.
        if (isOpen()) {
            close();

            // Wait while video thread will be closed.
            timeCounter = 0;
            while (this.bufImg == null && timeCounter < maxWaitTime) {
                // Wait closed camera.
                try {
                    Thread.sleep(waitStep);
                } catch (InterruptedException e) {
                    generateError(e.getMessage());
                }

                // Calculate waiting time.
                timeCounter += waitStep;
            }

            // Generate an error!
            if (this.bufImg == null) {
                generateError("Can not closed camera that was opened!");
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
                } catch (InterruptedException e) {
                    generateError(e.getMessage());
                }

                // Calculate waiting time.
                timeCounter += waitStep;
            }
        } catch (RuntimeException e) {
            generateError(e.getMessage());
        }

        // Generate an error!
        if (this.bufImg == null && this.isOpen()) {
            generateError("Can not open camera!");
        }
    }

    /**
     * Check if current device is opened.
     */
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    /**
     * Run thread to read data from web-camera.
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
                        "You must install the GPL version of Xuggler (with 'IVideoResampler' support)!");
            }

            /*
             * 2. Create a Xuggler container object.
             */
            this.container = IContainer.make();

            /*
             * 3. Tell Xuggler about the device format.
             */
            final IContainerFormat format = IContainerFormat.make();
            if (format.setInputFormat(this.driverName) < 0) {
                throw new RuntimeException(String.format("Couldn't open web-camera device: %s", this.driverName));
            }

            /*
             * 4. Configure device.
             *
             * Devices, unlike most files, need to have parameters set in order for Xuggler
             * to know how to configure them, for a webcam, these parameters make sense.
             */
            final IMetaData params = IMetaData.make();
            params.setValue("framerate", String.format("%s/1", this.FPS));
            params.setValue("video_size", String.format("%sx%s", this.size.getWidth(), this.size.getHeight()));

            /*
             * 5. Open up the container.
             */
            final int retval = this.container.open(this.deviceName, IContainer.Type.READ, format, false, true, params,
                    null);
            if (retval < 0) {
                /*
                 * This little trick converts the non friendly integer return value into a
                 * slightly more friendly object to get a human-readable error name.
                 */
                close();

                final IError error = IError.make(retval);
                throw new RuntimeException(
                        String.format("Could not open file: %s; Error: %s", this.deviceName, error.getDescription()));
            }

            /*
             * 6. Find first video stream.
             */
            // Query how many streams the call to open found.
            final int numStreams = this.container.getNumStreams();
            // Iterate through the streams to find the first video stream.
            int videoStreamId = -1;
            this.videoCoder = null;
            for (int i = 0; i < numStreams; i++) {
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
             * 7. Verify video stream.
             */
            // Check if we found video stream.
            if (videoStreamId == -1) {
                throw new RuntimeException(
                        String.format("Could not find video stream in container: %s", this.deviceName));
            }
            // Try to open up our decoder so it can do work.
            if (this.videoCoder.open() < 0) {
                throw new RuntimeException(
                        String.format("Could not open video decoder for container: %s", this.deviceName));
            }

            /*
             * 8. Check color scheme. If needed create color scheme convertor.
             */
            IVideoResampler resampler = null;
            if (this.videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
                // If this stream is not in BGR24, we're going to need to convert it.
                resampler = IVideoResampler.make(this.videoCoder.getWidth(), this.videoCoder.getHeight(),
                        IPixelFormat.Type.BGR24, this.videoCoder.getWidth(), this.videoCoder.getHeight(),
                        this.videoCoder.getPixelType());
                if (resampler == null) {
                    throw new RuntimeException(
                            String.format("Could not create color space resampler for: %s", this.deviceName));
                }
            }

            /*
             * 9. Now, we start walking through the container looking at each packet.
             */
            final IPacket packet = IPacket.make();
            while (this.container.readNextPacket(packet) >= 0 && this.isOpen) {
                /*
                 * 10. Check the packet: if it belongs to our video stream.
                 */
                if (packet.getStreamIndex() == videoStreamId) {
                    /*
                     * 11. We allocate a new picture to get the data out of Xuggler.
                     */
                    final IVideoPicture picture = IVideoPicture.make(this.videoCoder.getPixelType(),
                            this.videoCoder.getWidth(), this.videoCoder.getHeight());
                    int offset = 0;
                    while (offset < packet.getSize()) {
                        /*
                         * 12. Decode the video, checking for any errors.
                         */
                        final int bytesDecoded = this.videoCoder.decodeVideo(picture, packet, offset);
                        if (bytesDecoded < 0) {
                            throw new RuntimeException(
                                    String.format("Got error decoding video in: %s", this.deviceName));
                        }

                        offset += bytesDecoded;
                    }

                    /*
                     * 13. Check the current picture.
                     *
                     * Some decoders will consume data in a packet, but will not be able to
                     * construct a full video picture yet. Therefore you should always check
                     * if you got a complete picture from the decoder.
                     */
                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
                        /*
                         * 14. Convert color packet into BGR24 color scheme if needed.
                         *
                         * If the resampler is not null, that means we didn't get the video
                         * in BGR24 format and need to convert it into BGR24 format.
                         */
                        if (resampler != null) {
                            newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(),
                                    picture.getHeight());
                            if (resampler.resample(newPic, picture) < 0) {
                                throw new RuntimeException(
                                        String.format("Could not resample video from: %s", this.deviceName));
                            }
                        }

                        /*
                         * 15. Check color scheme.
                         */
                        if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                            throw new RuntimeException(
                                    String.format("Could not decode video as BGR 24 bit data in: %s", this.deviceName));
                        }

                        /*
                         * Copy link to created object.
                         */
                        this.bufImg = newPic;
                    }
                }
            }
        } catch (RuntimeException e) {
            this.errorMessage = e.getMessage();
        }

        // Current stream is closed.
        this.isOpen = false;

        /*
         * 17. Close.
         *
         * Technically since we're exiting anyway, these will be cleaned up by the garbage collector...
         * But because we're nice people and want to be invited places for Christmas, we're going to
         * show how to clean up.
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
     * Get current image from web-camera.
     */
    @SuppressWarnings("deprecation")
    @Override
    public Image getImage() throws IOException {
        checkErrors();

        /*
         * 16. Convert the BGR24 to BufferedImage and then to Image.
         */
        return TypeConvert.fromBufferedImage(Utils.videoPictureToImage(this.bufImg));
    }

    /**
     * Return size of image that get from web-camera.
     */
    public Size getSize() {
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
