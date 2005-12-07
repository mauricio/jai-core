/*
 * $RCSfile: SimpleCMYKColorSpace.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005-12-07 00:26:16 $
 * $State: Exp $
 */
package com.sun.media.jai.util;

import java.awt.color.ColorSpace;

public final class SimpleCMYKColorSpace extends ColorSpace {
    private ColorSpace csRGB;

    public SimpleCMYKColorSpace() {
        super(TYPE_CMYK, 4);
        csRGB = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
    }

    public boolean equals(Object o) {
        return o != null && o instanceof SimpleCMYKColorSpace;
    }

    public float[] toRGB(float[] colorvalue) {
        float K = colorvalue[3];
        float C = colorvalue[0] - K;
        float M = colorvalue[1] - K;
        float Y = colorvalue[2] - K;

        return new float[] {1.0F - C, 1.0F - M, 1.0F - Y};
    }

    public float[] fromRGB(float[] rgbvalue) {
        float C = 1.0F - rgbvalue[0];
        float M = 1.0F - rgbvalue[1];
        float Y = 1.0F - rgbvalue[2];
        float K = Math.min(C, Math.min(M, Y));

        return new float[] {C, M, Y, K};
    }

    public float[] toCIEXYZ(float[] colorvalue) {
        return csRGB.toCIEXYZ(toRGB(colorvalue));
    }

    public float[] fromCIEXYZ(float[] xyzvalue) {
        return fromRGB(csRGB.fromCIEXYZ(xyzvalue));
    }
}
