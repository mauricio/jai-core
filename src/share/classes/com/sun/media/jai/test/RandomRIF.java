/*
 * $RCSfile: RandomRIF.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 04:56:56 $
 * $State: Exp $
 */
package com.sun.media.jai.test;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;
import javax.media.jai.TileCache;

/**
 * @see RandomOpImage
 */
public class RandomRIF implements RenderedImageFactory {

    /** Constructor. */
    public RandomRIF() {}

    /**
     * Creates a new instance of RandomOpImage in the rendered layer.
     * This method satisfies the implementation of RIF.
     */
    public RenderedImage create(ParameterBlock paramBlock,
                                RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        
        // Get TileCache from renderHints if any.
        TileCache cache = RIFUtil.getTileCacheHint(renderHints);

        return new RandomOpImage(cache, OpImageTester.setLayout(layout));
    }
}
