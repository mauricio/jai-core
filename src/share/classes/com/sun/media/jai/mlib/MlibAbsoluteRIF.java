/*
 * $RCSfile: MlibAbsoluteRIF.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 04:55:47 $
 * $State: Exp $
 */
package com.sun.media.jai.mlib;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;
import java.util.Map;
import com.sun.media.jai.opimage.RIFUtil;

/**
 * A <code>RIF</code> supporting the "Absolute" operation in the
 * rendered image mode using MediaLib.
 *
 * @see javax.media.jai.operator.AbsoluteDescriptor
 * @see MlibAbsoluteOpImage
 *
 */
public class MlibAbsoluteRIF implements RenderedImageFactory {

    /** Constructor. */
    public MlibAbsoluteRIF() {}

    /**
     * Creates a new instance of <code>MlibAbsoluteOpImage</code> in
     * the rendered image mode.
     *
     * @param args  The source image.
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

	return new MlibAbsoluteOpImage(args.getRenderedSource(0),
                                       hints, layout);
    }
}
