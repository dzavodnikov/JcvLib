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
package org.jcvlib.core;

/**
 * Interface for implement linear and nonlinear operations.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public interface KernelOperation {

    /**
     * @param aperture
     *            Apply some operations based on this image.
     * @param result
     *            Save result in this object.
     */
    public void execute(Image aperture, Color result);
}
