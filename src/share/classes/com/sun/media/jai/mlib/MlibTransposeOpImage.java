/*
 * $RCSfile: MlibTransposeOpImage.java,v $
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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import java.util.Map;
import javax.media.jai.RasterAccessor;
import com.sun.medialib.mlib.*;
// import com.sun.media.jai.test.OpImageTester;

/**
 * An OpImage class to perform a transpose (flip) of an image.
 *
 * @since EA3
 *
 */
final class MlibTransposeOpImage extends GeometricOpImage {

    /** The Transpose type */
    protected int type;

    /**
     * Store source width & height
     */
    protected int src_width, src_height;

    protected Rectangle sourceBounds;

    private static ImageLayout layoutHelper(ImageLayout layout,
                                            RenderedImage source,
                                            int type) {
        ImageLayout newLayout;
        if (layout != null) {
            newLayout = (ImageLayout)layout.clone();
        } else {
            newLayout = new ImageLayout();
        }

        //
        // Forward map the source to get the destination (newLayout) area
        //
        int x1 = source.getMinX();
        int y1 = source.getMinY();
        int s_width = source.getWidth();
        int s_height = source.getHeight();
        int x2 = x1 + s_width - 1;
        int y2 = y1 + s_height - 1;

        // Layout coordinates
        int dx1 = 0;
        int dy1 = 0;
        int dx2 = 0;
        int dy2 = 0;

        switch (type) {
        case 0: // FLIP_VERTICAL
            dx1 = x1;
            dy1 = s_height - y2 - 1;
            dx2 = x2;
            dy2 = s_height - y1 - 1;
            break;

        case 1: // FLIP_HORIZONTAL
            dx1 = s_width - x2 - 1;
            dy1 = y1;
            dx2 = s_width - x1 - 1;
            dy2 = y2;
            break;

        case 2: // FLIP_DIAGONAL
            dx1 = y1;
            dy1 = x1;
            dx2 = y2;
            dy2 = x2;
            break;

        case 3: // FLIP_ANTIDIAGONAL
            dx1 = s_height - y2 - 1;
            dy1 = s_width - x2 - 1;
            dx2 = s_height - y1 - 1;
            dy2 = s_width - x1 - 1;
            break;

        case 4: // ROTATE_90
            dx1 = s_height - y2 - 1;
            dy1 = x1;
            dx2 = s_height - y1 - 1;
            dy2 = x2;
            break;

        case 5: // ROTATE_180
            dx1 = s_width - x2 - 1;
            dy1 = s_height - y2 - 1;
            dx2 = s_width - x1 - 1;
            dy2 = s_height - y1 - 1;
            break;

        case 6: // ROTATE_270
            dx1 = y1;
            dy1 = s_width - x2 - 1;
            dx2 = y2;
            dy2 = s_width - x1 - 1;
            break;
        }

        newLayout.setMinX(dx1);
        newLayout.setMinY(dy1);
        newLayout.setWidth(dx2 - dx1 + 1);
        newLayout.setHeight(dy2 - dy1 + 1);

        return newLayout;
    }

    /**
     * Constructs an TransposeOpImage from a RenderedImage source,
     * and Transpose type.  The image dimensions are determined by
     * forward-mapping the source bounds.
     * The tile grid layout, SampleModel, and ColorModel are specified
     * by the image source, possibly overridden by values from the
     * ImageLayout parameter.
     *
     * @param source a RenderedImage.

     *        or null.  If null, a default cache will be used.
     * @param layout an ImageLayout optionally containing the tile grid layout,
     *        SampleModel, and ColorModel, or null.
     * @param type the desired Tranpose type.
     */
    public MlibTransposeOpImage(RenderedImage source,
                                Map config,
                                ImageLayout layout,
                                int type) {
        super(vectorize(source),
              layoutHelper(layout,
                           source,
                           type),
              config,
              true,
              null,  // BorderExtender
              null,
              null); // Interpolation (superclass defaults to nearest neighbor)

        // store the Transpose type
        this.type = type;

        // Store the source width & height
        src_width = source.getWidth();
        src_height = source.getHeight();

        this.sourceBounds = new Rectangle(source.getMinX(),
                                          source.getMinY(),
                                          source.getWidth(),
                                          source.getHeight());
    }

    /**
     * Forward map the source Rectangle.
     */
    protected Rectangle forwardMapRect(Rectangle sourceRect,
                                       int sourceIndex) {
        return mapRect(sourceRect, sourceBounds, type, true);
    }

    /**
     * Backward map the destination Rectangle.
     */
    protected Rectangle backwardMapRect(Rectangle destRect,
                                        int sourceIndex) {
        //
        // Backward map the destination to get the source Rectangle
        //
        int x1 = destRect.x;
        int y1 = destRect.y;
        int x2 = x1 + destRect.width - 1;
        int y2 = y1 + destRect.height - 1;

        int sx1 = 0;
        int sy1 = 0;
        int sx2 = 0;
        int sy2 = 0;

        switch (type) {
        case 0: // FLIP_VERTICAL
            sx1 = x1;
            sy1 = src_height - y2 - 1;
            sx2 = x2;
            sy2 = src_height - y1 - 1;
            break;

        case 1: // FLIP_HORIZONTAL
            sx1 = src_width - x2 - 1;
            sy1 = y1;
            sx2 = src_width - x1 - 1;
            sy2 = y2;
            break;

        case 2: // FLIP_DIAGONAL
            sx1 = y1;
            sy1 = x1;
            sx2 = y2;
            sy2 = x2;
            break;

        case 3: // FLIP_ANTIDIAGONAL
            sx1 = src_width - y2 - 1;
            sy1 = src_height - x2 - 1;
            sx2 = src_width - y1 - 1;
            sy2 = src_height - x1 - 1;
            break;

        case 4: // ROTATE_90
            sx1 = y1;
            sy1 = src_height - x2 - 1;
            sx2 = y2;
            sy2 = src_height - x1 - 1;
            break;

        case 5: // ROTATE_180
            sx1 = src_width - x2 - 1;
            sy1 = src_height - y2 - 1;
            sx2 = src_width - x1 - 1;
            sy2 = src_height - y1 - 1;
            break;

        case 6: // ROTATE_270
            sx1 = src_width - y2 - 1;
            sy1 = x1;
            sx2 = src_width - y1 - 1;
            sy2 = x2;
            break;
        }

        return new Rectangle(sx1,
                             sy1,
                             (sx2 - sx1 + 1),
                             (sy2 - sy1 + 1));
    }

    /**
     * Map a point according to the transposition type.
     * If <code>mapForwards</code> is <code>true</code>,
     * the point is considered to lie in the source image and
     * is mapping into the destination space.  Otherwise,
     * the point lies in the destination and is mapped
     * into the source space.
     *
     * <p> In either case, the bounds of the source image
     * must be supplied.  The bounds are given by the indices
     * of the upper left and lower right pixels, i.e.,
     * maxX = minX + width - 1 and similarly for maxY.
     */
    // COPIED WHOLESALE FROM com.sun.media.jai.opimage.TransposeOpImage.
    protected static void mapPoint(int[] pt,
                                   int minX, int minY,
                                   int maxX, int maxY,
                                   int type,
                                   boolean mapForwards) {
        int sx = pt[0];
        int sy = pt[1];
        int dx = -1;
        int dy = -1;

        switch (type) {
        case 0: // FLIP_VERTICAL
            dx = sx;
            dy = minY + maxY - sy;
            break;

        case 1: // FLIP_HORIZONTAL
            dx = minX + maxX - sx;
            dy = sy;
            break;

        case 2: // FLIP_DIAGONAL
            dx = minX - minY + sy;
            dy = minY - minX + sx;
            break;

        case 3: // FLIP_ANTIDIAGONAL
            if (mapForwards) {
                dx = minX + maxY - sy;
                dy = minY + maxX - sx;
            } else {
                dx = minY + maxX - sy;
                dy = minX + maxY - sx;
            }
            break;

        case 4: // ROTATE_90
            if (mapForwards) {
                dx = minX + maxY - sy;
                dy = minY - minX + sx;
            } else {
                dx = minX - minY + sy;
                dy = minX + maxY - sx;
            }
            break;

        case 5: // ROTATE_180
            dx = minX + maxX - sx;
            dy = minY + maxY - sy;
            break;

        case 6: // ROTATE_270
            if (mapForwards) {
                dx = minX - minY + sy;
                dy = maxX + minY - sx;
            } else {
                dx = maxX + minY - sy;
                dy = minY - minX + sx;
            }
            break;
        }

        pt[0] = dx;
        pt[1] = dy;
    }

    // COPIED WHOLESALE FROM com.sun.media.jai.opimage.TransposeOpImage.
    private static Rectangle mapRect(Rectangle rect,
                                     Rectangle sourceBounds,
                                     int type,
                                     boolean mapForwards) {
        int sMinX = sourceBounds.x;
        int sMinY = sourceBounds.y;
        int sMaxX = sMinX + sourceBounds.width - 1;
        int sMaxY = sMinY + sourceBounds.height - 1;
        int dMinX, dMinY, dMaxX, dMaxY;

        int[] pt = new int[2];
        pt[0] = rect.x;
        pt[1] = rect.y;
        mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
        dMinX = dMaxX = pt[0];
        dMinY = dMaxY = pt[1];

        pt[0] = rect.x + rect.width - 1;
        pt[1] = rect.y;
        mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
        dMinX = Math.min(dMinX, pt[0]);
        dMinY = Math.min(dMinY, pt[1]);
        dMaxX = Math.max(dMaxX, pt[0]);
        dMaxY = Math.max(dMaxY, pt[1]);

        pt[0] = rect.x;
        pt[1] = rect.y + rect.height - 1;
        mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
        dMinX = Math.min(dMinX, pt[0]);
        dMinY = Math.min(dMinY, pt[1]);
        dMaxX = Math.max(dMaxX, pt[0]);
        dMaxY = Math.max(dMaxY, pt[1]);

        pt[0] = rect.x + rect.width - 1;
        pt[1] = rect.y + rect.height - 1;
        mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
        dMinX = Math.min(dMinX, pt[0]);
        dMinY = Math.min(dMinY, pt[1]);
        dMaxX = Math.max(dMaxX, pt[0]);
        dMaxY = Math.max(dMaxY, pt[1]);

        return new Rectangle(dMinX, dMinY,
                             dMaxX - dMinX + 1,
                             dMaxY - dMinY + 1);
    }

    public Raster computeTile(int tileX, int tileY) {
        //
        // Create a new WritableRaster to represent this tile.
        //
        Point org = new Point(tileXToX(tileX), tileYToY(tileY));
        WritableRaster dest = createWritableRaster(sampleModel, org);

        //
        // Clip output rectangle to image bounds.
        //
        Rectangle rect = new Rectangle(org.x, org.y,
                                       sampleModel.getWidth(),
                                       sampleModel.getHeight());

        Rectangle destRect = rect.intersection(getBounds());
        Rectangle srcRect =  mapDestRect(destRect, 0);

        //
        // Get source
        //
        PlanarImage src = getSourceImage(0);

        Raster[] sources = new Raster[1];

        sources[0] = src.getData(srcRect);

        computeRect(sources, dest, destRect);

        // Recycle the source tile
        if(src.overlapsMultipleTiles(srcRect)) {
            recycleTile(sources[0]);
        }

        return dest;
    }

    protected void computeRect(Raster[] sources,
                               WritableRaster dest,
                               Rectangle destRect) {
        Raster source = sources[0];

        //
        // Get the minX, minY, width & height of sources raster
        //
        int x1 = source.getMinX();
        int y1 = source.getMinY();
        int src_width = source.getWidth();
        int src_height = source.getHeight();

        Rectangle srcRect = new Rectangle(x1, y1, src_width, src_height);

        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);

        MediaLibAccessor srcAccessor =
            new MediaLibAccessor(source,srcRect,formatTag);
        MediaLibAccessor dstAccessor =
            new MediaLibAccessor(dest,destRect,formatTag);
        int numBands = getSampleModel().getNumBands();

	mediaLibImage srcML[], dstML[];

        switch (dstAccessor.getDataType()) {
        case DataBuffer.TYPE_BYTE:
        case DataBuffer.TYPE_USHORT:
        case DataBuffer.TYPE_SHORT:
        case DataBuffer.TYPE_INT:
	    srcML = srcAccessor.getMediaLibImages();
	    dstML = dstAccessor.getMediaLibImages();
            switch (type) {
            case 0: // FLIP_VERTICAL
                Image.FlipX(dstML[0], srcML[0]);
                break;

            case 1: // FLIP_HORIZONTAL
                Image.FlipY(dstML[0], srcML[0]);
                break;

            case 2: // FLIP_DIAGONAL
                Image.FlipMainDiag(dstML[0], srcML[0]);
                break;

            case 3: // FLIP_ANTIDIAGONAL
                Image.FlipAntiDiag(dstML[0], srcML[0]);
                break;

            case 4: // ROTATE_90
                Image.Rotate90(dstML[0], srcML[0]);
                break;

            case 5: // ROTATE_180
                Image.Rotate180(dstML[0], srcML[0]);
                break;

            case 6: // ROTATE_270
                Image.Rotate270(dstML[0], srcML[0]);
                break;
            }
            break;

        case DataBuffer.TYPE_FLOAT:
        case DataBuffer.TYPE_DOUBLE:
	    srcML = srcAccessor.getMediaLibImages();
	    dstML = dstAccessor.getMediaLibImages();
            switch (type) {
            case 0: // FLIP_VERTICAL
                Image.FlipX_Fp(dstML[0], srcML[0]);
                break;

            case 1: // FLIP_HORIZONTAL
                Image.FlipY_Fp(dstML[0], srcML[0]);
                break;

            case 2: // FLIP_DIAGONAL
                Image.FlipMainDiag_Fp(dstML[0], srcML[0]);
                break;

            case 3: // FLIP_ANTIDIAGONAL
                Image.FlipAntiDiag_Fp(dstML[0], srcML[0]);
                break;

            case 4: // ROTATE_90
                Image.Rotate90_Fp(dstML[0], srcML[0]);
                break;

            case 5: // ROTATE_180
                Image.Rotate180_Fp(dstML[0], srcML[0]);
                break;

            case 6: // ROTATE_270
                Image.Rotate270_Fp(dstML[0], srcML[0]);
                break;
            }
            break;

        default:
            throw new RuntimeException(JaiI18N.getString("Generic2"));
        }

        //
        // If the RasterAccessor object set up a temporary buffer for the
        // op to write to, tell the RasterAccessor to write that data
        // to the raster, that we're done with it.
        //
        if (dstAccessor.isDataCopy()) {
            dstAccessor.copyDataToRaster();
        }
    }

//     public static OpImage createTestImage(OpImageTester oit) {
//         int type = 1;
//         return new MlibTransposeOpImage(oit.getSource(), null,
//                                         new ImageLayout(oit.getSource()),
//                                         type);
//     }

//     public static void main (String args[]) {
//         String classname = "com.sun.media.jai.mlib.MlibTransposeOpImage";
//         OpImageTester.performDiagnostics(classname,args);
//     }
}
