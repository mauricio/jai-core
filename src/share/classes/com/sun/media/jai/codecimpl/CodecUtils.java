/*
 * $RCSfile: CodecUtils.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 04:55:35 $
 * $State: Exp $
 */
package com.sun.media.jai.codecimpl;

import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;

/**
 * A class for utility functions for codecs.
 */
class CodecUtils {
    /**
     * Returns <code>true</code> if and only if <code>im</code>
     * has a <code>SinglePixelPackedSampleModel</code> with a
     * sample size of at most 8 bits for each of its bands.
     *
     * @param src The <code>RenderedImage</code> to test.
     * @return Whether the image is byte-packed.
     */
    static final boolean isPackedByteImage(RenderedImage im) {
        SampleModel imageSampleModel = im.getSampleModel();

        if(imageSampleModel instanceof SinglePixelPackedSampleModel) {
            for(int i = 0; i < imageSampleModel.getNumBands(); i++) {
                if(imageSampleModel.getSampleSize(i) > 8) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
