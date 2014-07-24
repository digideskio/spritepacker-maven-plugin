package net.oneandone.maven.plugins.spritepacker.converters;

import com.google.common.base.Strings;
import net.oneandone.maven.plugins.spritepacker.ImagePacking;
import net.oneandone.maven.plugins.spritepacker.NamedImage;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.awt.Point;
import java.nio.file.Path;
import java.util.List;

/**
 * Converts ImagePacking to a CSS file, with the result that each icon gets its own class containing
 * background-position, width and height of the icon.
 *
 * @author mklein
 */
public class CssPackingConverter extends AbstractTextConverter {
    private final String cssPrefix;

    /**
     * Create a CSS converter with output file css and class prefix cssPrefix.
     * @param css       the output CSS file to write to
     * @param cssPrefix the CSS class prefix for each icon class
     */
    public CssPackingConverter(Path css, String cssPrefix) {
        super(css, "CSS");
        this.cssPrefix = fixFirstChar(sanitize(cssPrefix));
    }

    /**
     * Create output CSS string based on an ImagePacking.
     *
     * @param imageList     the list of images - must not be null
     * @param imagePacking  the ImagePacking to convert - must not be null
     * @param log           the log object to use
     * @return              String containing the CSS file contents
     * @throws MojoExecutionException
     */
    @Override
    protected String createOutput(List<NamedImage> imageList, ImagePacking imagePacking, Log log) throws MojoExecutionException {
        StringBuilder sb = new StringBuilder("/* this file is generated by the sprite packer. don't make any changes in here! */\n");
        for (NamedImage image : imageList) {
            String name = getCssClassName(image.getName());
            Point position = imagePacking.getPosition(image);
            String x = intToPixel(-position.x);
            String y = intToPixel(-position.y);
            String width = intToPixel(image.getWidth());
            String height = intToPixel(image.getHeight());
            sb.append(".").append(name).append("{background-position:").append(x).append(" ").append(y).append(";")
                    .append("width:").append(width).append(";height:").append(height).append(";}\n");
        }
        return sb.toString();
    }

    /**
     * Sanitize, prefix, and fix first character of a CSS class name
     *
     * @param name  the base CSS class name
     * @return      CSS class name that has been sanitized and prefixed, with first character fixed as needed.
     */
    private String getCssClassName(String name) {
        return Strings.isNullOrEmpty(cssPrefix) ? fixFirstChar(sanitize(name)) : cssPrefix + "-" + sanitize(name);
    }
}
