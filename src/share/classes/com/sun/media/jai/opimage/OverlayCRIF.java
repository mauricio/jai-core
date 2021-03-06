/*
 * $RCSfile: OverlayCRIF.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 04:56:39 $
 * $State: Exp $
 */
package com.sun.media.jai.opimage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;
import java.util.Map;

/**
 * A <code>CRIF</code> supporting the "Overlay" operation in the rendered
 * and renderable image layers.
 *
 * @see javax.media.jai.operator.OverlayDescriptor
 * @see OverlayOpImage
 *
 */
public class OverlayCRIF extends CRIFImpl {

    /** Constructor. */
    public OverlayCRIF() {
        super("overlay");
    }

    /**
     * Creates a new instance of <code>OverlayOpImage</code>
     * in the rendered layer.
     *
     * @param args   The two source images.
     * @param hints  Optionally contains destination image layout.
     */
    public RenderedImage create(ParameterBlock args,
                                RenderingHints renderHints) {
        // Get ImageLayout from renderHints if any.
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        

        return new OverlayOpImage(args.getRenderedSource(0),
                                  args.getRenderedSource(1),
                                  renderHints,
                                  layout);
    }
}
