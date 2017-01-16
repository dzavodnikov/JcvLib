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
package org.jcvlib.gui;

import java.awt.Dimension;

import javax.swing.JLabel;

/**
 * Internal class for status bar on image window.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class StatusBar extends JLabel {

    private static final long serialVersionUID = 1L;

    public StatusBar() {
        super();

        super.setPreferredSize(new Dimension(100, 16));
    }

    /**
     * Update string into the status bar.
     *
     * @param message
     *            New message into the status bar.
     */
    public void setMessage(final String message) {
        setText(message);
    }
}
