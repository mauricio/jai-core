/*
 * $RCSfile: SimpleCMYKColorSpace.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.2 $
 * $Date: 2006-02-08 21:14:55 $
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
        float C = colorvalue[0];
        float M = colorvalue[1];
        float Y = colorvalue[2];
        float K = colorvalue[3];

        float K1 = 1.0F - K;

        return new float[] {K1*(1.0F - C),
                            K1*(1.0F - M),
                            K1*(1.0F - Y)};
    }

    public float[] fromRGB(float[] rgbvalue) {
        float C = 1.0F - rgbvalue[0];
        float M = 1.0F - rgbvalue[1];
        float Y = 1.0F - rgbvalue[2];
        float K = Math.min(C, Math.min(M, Y));

        // If K == 1.0F, then C = M = Y = 1.0F.
        if(K != 1.0F) {
            float K1 = 1.0F - K;

            C = (C - K)/K1;
            M = (M - K)/K1;
            Y = (Y - K)/K1;
        }

        return new float[] {C, M, Y, K};
    }

    public float[] toCIEXYZ(float[] colorvalue) {
        return csRGB.toCIEXYZ(toRGB(colorvalue));
    }

    public float[] fromCIEXYZ(float[] xyzvalue) {
        return fromRGB(csRGB.fromCIEXYZ(xyzvalue));
    }
}
