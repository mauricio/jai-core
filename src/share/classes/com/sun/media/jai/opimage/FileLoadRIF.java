/*
 * $RCSfile: FileLoadRIF.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 04:56:26 $
 * $State: Exp $
 */
package com.sun.media.jai.opimage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedImageAdapter;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.ImagingListener;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.util.ImageUtil;

/*
 * Package-scope class which merely adds a finalize() method to close
 * the associated stream.
 */
class StreamImage extends RenderedImageAdapter {
    private InputStream stream;

    /*
     * Create the object and cache the stream.
     */
    public StreamImage(RenderedImage image,
                       InputStream stream) {
        super(image);
        this.stream = stream;
        if(image instanceof OpImage) {
            // Set the properties related to TileCache key as used in
            // RenderedOp.
            setProperty("tile_cache_key", image);
            Object tileCache = ((OpImage)image).getTileCache();
            setProperty("tile_cache",
                        tileCache == null ?
                        java.awt.Image.UndefinedProperty : tileCache);
        }
    }

    /*
     * Close the stream.
     */
    protected void finalize() throws Throwable {
        stream.close();
        super.finalize();
    }
}

/**
 * @see javax.media.jai.operator.FileDescriptor
 *
 * @since EA3
 *
 */
public class FileLoadRIF implements RenderedImageFactory {

    /** Constructor. */
    public FileLoadRIF() {}

    /**
     * Creates an image from a String containing a file name.
     */
    public RenderedImage create(ParameterBlock args,
                                RenderingHints hints) {
        ImagingListener listener = ImageUtil.getImagingListener(hints);

        try {
            // Create a SeekableStream from the file name (first parameter).
            String fileName = (String)args.getObjectParameter(0);
            SeekableStream src = new FileSeekableStream(fileName);

            ImageDecodeParam param = null;
            if (args.getNumParameters() > 1) {
                param = (ImageDecodeParam)args.getObjectParameter(1);
            }

            ParameterBlock newArgs = new ParameterBlock();
            newArgs.add(src);
            newArgs.add(param);

            RenderingHints.Key key = JAI.KEY_OPERATION_BOUND;
            int bound = OpImage.OP_IO_BOUND;
            if (hints == null) {
                hints = new RenderingHints(key, new Integer(bound));
            } else if (!hints.containsKey(key)) {
                hints = (RenderingHints)hints.clone();
                hints.put(key, new Integer(bound));
            }

            // Get the registry from the hints, if any.
            // Don't check for null hints as it cannot be null here.
            OperationRegistry registry =
                (OperationRegistry)hints.get(JAI.KEY_OPERATION_REGISTRY);

            // Create the image using the most preferred RIF for "stream".
            RenderedImage image =
		RIFRegistry.create(registry, "stream", newArgs, hints);

            return image == null ? null : new StreamImage(image, src);

        } catch (FileNotFoundException e) {
            String message =
                JaiI18N.getString("FileLoadRIF0") + args.getObjectParameter(0);
            listener.errorOccurred(message, e, this, false);
//            e.printStackTrace();
            return null;
        } catch (Exception e) {
            String message = JaiI18N.getString("FileLoadRIF1");
            listener.errorOccurred(message, e, this, false);
//            e.printStackTrace();
            return null;
        }
    }
}
