package utility;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
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
    
    private static final String DATA_PATH = P.getProperty("DATA_PATH");

    public static final Charset CHARSET = Charset.forName(P.getProperty("CHARSET"));
    public static final String USERBASE_PATH = String.format("%s%s", DATA_PATH, P.getProperty("USERBASE_FILE"));

    private static Properties init()
    {
        Properties temp = new Properties();
        
        try
        {
            temp.load(new FileInputStream("mainframe.config"));
        }
        catch (IOException e) { e.printStackTrace(); }
        
        return temp;
    }
}
