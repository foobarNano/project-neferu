package utility;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A static-like class for convenient access to system-wide constants.
 * <ul>
 *  <li>{@code final} assures the class cannot be inherited</li>
 *  <li>{@code private} constructor assures no dynamic instance can be created</li>
 * </ul>
 */
public final class Constants
{
    private Constants() {}

    private static final Properties P = init();

    private static Properties init()
    {
        Properties temp = new Properties();

        // Declare properties here
        
        try
        {
            temp.load(new FileInputStream("mainframe.config"));
        }
        catch (IOException e) { e.printStackTrace(); }
        
        return temp;
    }
}
