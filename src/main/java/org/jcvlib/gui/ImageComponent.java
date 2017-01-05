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
package org.jcvlib.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.jcvlib.core.JCV;

/**
 * Internal class for display image.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
class ImageComponent extends JComponent {

    private static final long serialVersionUID = 1L;

    private BufferedImage     bufImg;

    private final double      defaultScale     = 1.0;

    private double            scale;

    public ImageComponent(final BufferedImage image) {
        this.bufImg = image;
        setScale(this.defaultScale);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        // Update image.
        if (this.bufImg != null) {
            final Dimension dim = getPreferredSize();
            g.drawImage(this.bufImg, 0, 0, dim.width, dim.height, this);
        }
    }

    /**
     * Use default sale into current image.
     */
    public void setDefaultScale() {
        setScale(this.defaultScale);
    }

    /**
     * Return current scale for contained image.
     */
    public double getScale() {
        return this.scale;
    }

    /**
     * Set new value of scale for contained image.
     */
    public void setScale(final double scale) {
        this.scale = scale;

        // Resize image.
        if (this.bufImg != null) {
            setPreferredSize(new Dimension(JCV.round(this.scale * this.bufImg.getWidth()),
                    JCV.round(this.scale * this.bufImg.getHeight())));
            revalidate();
            repaint();
        }
    }

    /**
     * Update contained image.
     */
    public void setBufferedImage(final BufferedImage bufImg) {
        this.bufImg = bufImg;
    }
}
