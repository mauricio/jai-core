/*
 * $RCSfile: JPEGImageEncoder.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 04:55:37 $
 * $State: Exp $
 */
package com.sun.media.jai.codecimpl;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.JPEGEncodeParam;
//
// Need these classes since we are currently using the
// Java2D JpegEncoder for our Jpeg Implementation.
//
import com.sun.image.codec.jpeg.JPEGQTable;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.util.ImagingException;

/**
 * An ImageEncoder for the JPEG (JFIF) file format.
 *
 * The common cases of single band grayscale and three or four band RGB images
 * are handled so as to minimize the amount of information required of the
 * programmer. See the comments pertaining to the constructor and the
 * <code>writeToStream()</code> method for more detailed information.
 *
 * @since EA2
 */
public class JPEGImageEncoder extends ImageEncoderImpl {

    private JPEGEncodeParam jaiEP = null;

    public JPEGImageEncoder(OutputStream output,
                            ImageEncodeParam param) {
        super(output, param);
        if (param != null) {
            jaiEP = (JPEGEncodeParam)param;
        }
    }

    //
    // Go through the settable encoding parameters and see
    // if any of them have been set. If so, transfer then to the
    // com.sun.image.codec.jpeg.JPEGEncodeParam object.
    //
    static void modifyEncodeParam(JPEGEncodeParam jaiEP,
          com.sun.image.codec.jpeg.JPEGEncodeParam j2dEP,
                                               int nbands) {

        int val;
        int[] qTab;
        for(int i=0; i<nbands; i++) {
            //
            // If subsampling factors were set, apply them
            //
            val = jaiEP.getHorizontalSubsampling(i);
            j2dEP.setHorizontalSubsampling(i, val);

            val = jaiEP.getVerticalSubsampling(i);
            j2dEP.setVerticalSubsampling(i, val);

            //
            // If new Q factors were supplied, apply them
            //
            if (jaiEP.isQTableSet(i)) {
                qTab = jaiEP.getQTable(i);
                val = jaiEP.getQTableSlot(i);
                j2dEP.setQTableComponentMapping(i, val);
                j2dEP.setQTable(val, new JPEGQTable(qTab));
            }
        }

        // Apply new quality, if set
        if (jaiEP.isQualitySet()) {
            float fval = jaiEP.getQuality();
            j2dEP.setQuality(fval, true);
        }

        // Apply new restart interval, if set
        val = jaiEP.getRestartInterval();
        j2dEP.setRestartInterval(val);

        // Write a tables-only abbreviated JPEG file
        if (jaiEP.getWriteTablesOnly() == true) {
            j2dEP.setImageInfoValid(false);
            j2dEP.setTableInfoValid(true);
        }

        // Write an image-only abbreviated JPEG file
        if (jaiEP.getWriteImageOnly() == true) {
            j2dEP.setTableInfoValid(false);
            j2dEP.setImageInfoValid(true);
        }

        // Write the JFIF (APP0) marker
        if (jaiEP.getWriteJFIFHeader() == false) {
            j2dEP.setMarkerData(
              com.sun.image.codec.jpeg.JPEGDecodeParam.APP0_MARKER, null);
        }

    }

    /**
     * Encodes a RenderedImage and writes the output to the
     * OutputStream associated with this ImageEncoder.
     */
    public void encode(RenderedImage im) throws IOException {
        //
        // Check data type and band count compatibility.
        // This implementation handles only 1 and 3 band source images.
        //
        SampleModel sampleModel = im.getSampleModel();
        ColorModel  colorModel  = im.getColorModel();

        // Must be a 1 or 3 band BYTE image
        int numBands  = colorModel.getNumColorComponents();
        int transType = sampleModel.getTransferType();
        if (((transType != DataBuffer.TYPE_BYTE) &&
             !CodecUtils.isPackedByteImage(im)) ||
            ((numBands != 1) && (numBands != 3) )) {
            throw new RuntimeException(JaiI18N.getString("JPEGImageEncoder0"));
        }

        // Must be GRAY or RGB
        int cspaceType = colorModel.getColorSpace().getType();
        if (cspaceType != ColorSpace.TYPE_GRAY &&
            cspaceType != ColorSpace.TYPE_RGB) {
            throw new Error(JaiI18N.getString("JPEGImageEncoder1"));
        }

        //
        // Create a BufferedImage to be encoded.
        // The JPEG interfaces really need a whole image.
        //
        WritableRaster wRas;
        BufferedImage bi;

        //
        // Get a contiguous raster. Jpeg compression can't work
        // on tiled data in most cases.
        // Also need to be sure that the raster doesn't have a
        // non-zero origin, since BufferedImage won't accept that.
        // (Bug ID 4253990)
        //

	//Fix 4694162: JPEGImageEncoder throws ClassCastException
	Raster ras = im.getData();
	if (ras instanceof WritableRaster)
	    wRas = (WritableRaster)ras;
	else
	    wRas = Raster.createWritableRaster(ras.getSampleModel(),
			ras.getDataBuffer(),
			new Point(ras.getSampleModelTranslateX(),
				  ras.getSampleModelTranslateY()));
        if (wRas.getMinX() != 0 || wRas.getMinY() != 0) {
            wRas = wRas.createWritableTranslatedChild(0, 0);
        }

        // First create the Java2D encodeParam based on the BufferedImage
        com.sun.image.codec.jpeg.JPEGEncodeParam j2dEP = null;
        if (colorModel instanceof IndexColorModel) {
            //
            // Need to expand the indexed data to components.
            // The convertToIntDiscrete method is used to perform this.
            //
            IndexColorModel icm = (IndexColorModel)colorModel;
            bi = icm.convertToIntDiscrete(wRas, false);
            j2dEP = com.sun.image.codec.jpeg.JPEGCodec.getDefaultJPEGEncodeParam(bi);
        } else {
            bi = new BufferedImage(colorModel, wRas, false, null);
            j2dEP = com.sun.image.codec.jpeg.JPEGCodec.getDefaultJPEGEncodeParam(bi);
        }


        // Now modify the Java2D encodeParam based on the options set
        // in the JAI encodeParam object.
        if (jaiEP != null) {
            modifyEncodeParam(jaiEP, j2dEP, numBands);
        }

        // Now create the encoder with the modified Java2D encodeParam
        com.sun.image.codec.jpeg.JPEGImageEncoder encoder;
        encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(
                    output, j2dEP);

        try {
          // Write the image data.
            encoder.encode(bi);
        } catch(IOException e) {
            String message = JaiI18N.getString("JPEGImageEncoder2");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e),
                                   this, false);
//            throw new RuntimeException(e.getMessage());
        }

    }

}
