/*
 * $RCSfile: StreamDescriptor.java,v $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 04:57:44 $
 * $State: Exp $
 */
package javax.media.jai.operator;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.registry.RenderedRegistryMode;

/**
 * An <code>OperationDescriptor</code> describing the "Stream" operation.
 *
 * <p> The Stream operation produces an image by decoding data from a
 * <code>SeekableStream</code>.  The allowable formats are those
 * registered with the <code>com.sun.media.jai.codec.ImageCodec</code>
 * class.
 *
 * <p> The allowable formats are those registered with the
 * <code>com.sun.media.jai.codec.ImageCodec</code> class.
 *
 * <p> The second parameter contains an instance of
 * <code>ImageDecodeParam</code> to be used during the decoding.
 * It may be set to <code>null</code> in order to perform default
 * decoding, or equivalently may be omitted.
 *
 * <p><b> The classes in the <code>com.sun.media.jai.codec</code>
 * package are not a committed part of the JAI API.  Future releases
 * of JAI will make use of new classes in their place.  This
 * class will change accordingly.</b>
 * 
 * <p><table border=1>
 * <caption>Resource List</caption>
 * <tr><th>Name</th>        <th>Value</th></tr>
 * <tr><td>GlobalName</td>  <td>stream</td></tr>
 * <tr><td>LocalName</td>   <td>stream</td></tr>
 * <tr><td>Vendor</td>      <td>com.sun.media.jai</td></tr>
 * <tr><td>Description</td> <td>Reads an image from a SeekableStream.</td></tr>
 * <tr><td>DocURL</td>      <td>http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/StreamDescriptor.html</td></tr>
 * <tr><td>Version</td>     <td>1.0</td></tr>
 * <tr><td>arg0Desc</td>    <td>The SeekableStream to read from.</td></tr>
 * <tr><td>arg1Desc</td>    <td>The ImageDecodeParam to use.</td></tr>
 * </table></p>
 *
 * <p><table border=1>
 * <caption>Parameter List</caption>
 * <tr><th>Name</th>          <th>Class Type</th>
 *                            <th>Default Value</th></tr>
 * <tr><td>stream</td>        <td>com.sun.media.jai.codec.SeekableStream</td>
 *                            <td>NO_PARAMETER_DEFAULT</td>
 * <tr><td>param</td>         <td>com.sun.media.jai.codec.ImageDecodeParam</td>
 *                            <td>null</td>
 * </table></p>
 *
 * @see javax.media.jai.OperationDescriptor
 */
public class StreamDescriptor extends OperationDescriptorImpl {

    /**
     * The resource strings that provide the general documentation and
     * specify the parameter list for the "Stream" operation.
     */
    private static final String[][] resources = {
        {"GlobalName",  "Stream"},
        {"LocalName",   "Stream"},
        {"Vendor",      "com.sun.media.jai"},
        {"Description", JaiI18N.getString("StreamDescriptor0")},
        {"DocURL",      "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/StreamDescriptor.html"},
        {"Version",     JaiI18N.getString("DescriptorVersion")},
        {"arg0Desc",    JaiI18N.getString("StreamDescriptor1")},
        {"arg1Desc",    JaiI18N.getString("StreamDescriptor2")}
    };

    /** The parameter names for the "Stream" operation. */
    private static final String[] paramNames = {
        "stream", "param"
    };

    /** The parameter class types for the "Stream" operation. */
    private static final Class[] paramClasses = {
        com.sun.media.jai.codec.SeekableStream.class,
        com.sun.media.jai.codec.ImageDecodeParam.class
    };

    /** The parameter default values for the "Stream" operation. */
    private static final Object[] paramDefaults = {
        NO_PARAMETER_DEFAULT, null
    };

    /** Constructor. */
    public StreamDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }


    /**
     * Reads an image from a SeekableStream.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all
     * supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     *
     * @param stream The SeekableStream to read from.
     * @param param The ImageDecodeParam to use.
     * May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use.
     * May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>stream</code> is <code>null</code>.
     */
    public static RenderedOp create(SeekableStream stream,
                                    ImageDecodeParam param,
                                    RenderingHints hints)  {
        ParameterBlockJAI pb =
            new ParameterBlockJAI("Stream",
                                  RenderedRegistryMode.MODE_NAME);

        pb.setParameter("stream", stream);
        pb.setParameter("param", param);

        return JAI.create("Stream", pb, hints);
    }
}
