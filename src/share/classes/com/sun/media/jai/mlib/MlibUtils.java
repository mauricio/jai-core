/*
 * $RCSfile: MlibUtils.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 04:56:08 $
 * $State: Exp $
 */
package com.sun.media.jai.mlib;

final class MlibUtils {
    /**
     * If constants array is less than numBands it is replaced
     * by an array of length numBands filled with constants[0].
     * Otherwise the input array is cloned.
     */
    static final int[] initConstants(int[] constants, int numBands) {
        int[] c = null;
        if (constants.length < numBands) {
            c = new int[numBands];
            for (int i = 0; i < numBands; i++) {
                c[i] = constants[0];
            }
        } else {
            c = (int[])constants.clone();
        }

        return c;
    }

    /**
     * If constants array is less than numBands it is replaced
     * by an array of length numBands filled with constants[0].
     * Otherwise the input array is cloned.
     */
    static final double[] initConstants(double[] constants, int numBands) {
        double[] c = null;
        if (constants.length < numBands) {
            c = new double[numBands];
            for (int i = 0; i < numBands; i++) {
                c[i] = constants[0];
            }
        } else {
            c = (double[])constants.clone();
        }

        return c;
    }
}
