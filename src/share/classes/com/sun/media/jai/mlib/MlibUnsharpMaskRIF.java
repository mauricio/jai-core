/*
 * $RCSfile: MlibUnsharpMaskRIF.java,v $
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

import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;
import java.util.Map;

/**
 * A <code>RIF</code> supporting the "Convolve" operation in the
 * rendered image mode using MediaLib.
 *
 * @see javax.media.jai.operator.ConvolveDescriptor
 * @see MlibConvolveOpImage
 */
public class MlibUnsharpMaskRIF implements RenderedImageFactory {

    /** Constructor. */
    public MlibUnsharpMaskRIF() {}

    /**
     * Creates a new instance of <code>MlibConvolveOpImage</code> in
     * the rendered image mode.
     *
     * @param args  The source image and convolution kernel.
     * @param hints  May contain rendering hints and destination image layout.
     */
    public RenderedImage create(ParameterBlock args,
                                RenderingHints hints) {

        /* Get ImageLayout and TileCache from RenderingHints. */
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        

        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) ||
            !MediaLibAccessor.hasSameNumBands(args, layout)) {
            return null;
        }

        /* Get BorderExtender from hints if any. */
        BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);

        RenderedImage source = args.getRenderedSource(0);

	// map the input kernel + gain factor to an equivalent
	// convolution kernel and then do a normal convolve.
	KernelJAI unRotatedKernel =
		ImageUtil.getUnsharpMaskEquivalentKernel(
			(KernelJAI)args.getObjectParameter(0),
			args.getFloatParameter(1));

        KernelJAI kJAI = unRotatedKernel.getRotatedKernel();

        int kWidth = kJAI.getWidth();
        int kHeight = kJAI.getHeight();

        /* mediaLib does not handle kernels with either dimension < 2. */
        if (kWidth < 2 || kHeight < 2) {
            return null;
        }

        if (kJAI.isSeparable() && kWidth >= 3 && kWidth <= 7 &&  kWidth == kHeight) {
            return new MlibSeparableConvolveOpImage(source,
                                                    extender, hints, layout,
                                                    kJAI);
        } else if ((kWidth == 3 && kHeight == 3) ||
                   (kWidth == 5 && kHeight == 5)) {
            return new MlibConvolve3x3Or5x5OpImage(source,
                                                   extender, hints, layout,
                                                   kJAI);
        } else {
            return new MlibConvolveOpImage(source,
                                           extender, hints, layout,
                                           kJAI);
        }
    }
}
