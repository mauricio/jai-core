/*
 * $RCSfile: MlibHistogramRIF.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 04:55:57 $
 * $State: Exp $
 */package com.sun.media.jai.mlib;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.JAI;
import javax.media.jai.ROI;
import javax.media.jai.util.ImagingListener;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.opimage.RIFUtil;

/**
 * A <code>RIF</code> supporting the "Histogram" operation in the
 * rendered image layer.
 *
 * @since EA2
 * @see javax.media.jai.operator.HistogramDescriptor
 * @see MlibHistogramOpImage
 */
public class MlibHistogramRIF implements RenderedImageFactory {

    /** Constructor. */
    public MlibHistogramRIF() {}

    /**
     * Creates a new instance of <code>MlibHistogramOpImage</code>
     * in the rendered layer. Any image layout information in
     * <code>RenderingHints</code> is ignored.
     * This method satisfies the implementation of RIF.
     */
    public RenderedImage create(ParameterBlock args,
                                RenderingHints hints) {

        // Return null of not mediaLib-compatible.
        if(!MediaLibAccessor.isMediaLibCompatible(args)) {
            return null;
        }

        // Return null if source data type is floating point.
        RenderedImage src = args.getRenderedSource(0);
        int dataType = src.getSampleModel().getDataType();
        if(dataType == DataBuffer.TYPE_FLOAT ||
           dataType == DataBuffer.TYPE_DOUBLE) {
            return null;
        }

        // Return null if ROI is non-null and not equals to source bounds.
        ROI roi = (ROI)args.getObjectParameter(0);
        if(roi != null &&
           !roi.equals(new Rectangle(src.getMinX(), src.getMinY(),
                                     src.getWidth(), src.getHeight()))) {
            return null;
        }

        // Get the non-ROI parameters.
        int xPeriod = args.getIntParameter(1);
        int yPeriod = args.getIntParameter(2);
        int[] numBins = (int[])args.getObjectParameter(3);
        double[] lowValueFP = (double[])args.getObjectParameter(4);
        double[] highValueFP = (double[])args.getObjectParameter(5);


        MlibHistogramOpImage op = null;
        try {
            op = new MlibHistogramOpImage(src,
                                          xPeriod, yPeriod,
                                          numBins, lowValueFP, highValueFP);
        } catch (Exception e) {
            ImagingListener listener = ImageUtil.getImagingListener(hints);
            String message = JaiI18N.getString("MlibHistogramRIF0");
            listener.errorOccurred(message, e, this, false);
        }

        return op;
    }
}
