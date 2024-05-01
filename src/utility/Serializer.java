package utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class Serializer
{
    /**
     * Independent convenience function for saving persistent serializable objects.
     * Non-persistent stream assures resources are locked for the minimum amount of time.
     * @param object A serializable to be saved
     * @param filename Name of the target file
     * @return Whether the operation was successful
     */
    public static boolean write_object(Serializable object, String filename)
    {
        FileOutputStream stream = null;
        ObjectOutputStream out = null;

        try
        {
            stream = new FileOutputStream(filename);
            out = new ObjectOutputStream(stream);

            out.writeObject(object);
            return true;
        }
        catch (IOException e) { e.printStackTrace(); }
        finally
        {
            try
            {
                if (out != null) out.close();
                if (stream != null) stream.close();
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        return false;
    }

    /**
     * Independent convenience function for saving persistent serializable objects.
     * Non-persistent stream assures resources are locked for the minimum amount of time.
     * @param object A serializable to be saved
     * @param filename Name of the source file
     * @return Whether the operation was successful
     */
    /**
     * Independent convenience function for saving persistent serializable objects.
     * Non-persistent stream assures resources are locked for the minimum amount of time.
     * @param <T> Expected type of the object
     * @param filename Name of the source file with the serialized object
     * @return The deserialized object
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T read_object(String filename)
    {
        FileInputStream stream = null;
        ObjectInputStream in = null;

        try
        {
            stream = new FileInputStream(filename);
            in = new ObjectInputStream(stream);

            return (T) in.readObject();
        }
        catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        finally
        {
            try
            {
                if (in != null) in.close();
                if (stream != null) stream.close();
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        return null;
    }
}
