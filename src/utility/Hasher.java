package utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Convenience wrapper for the MessageDigest utility.
 */
public enum Hasher
{
    MD5("MD5"),
    SHA_256("SHA-256"),
    SHA3_256("SHA3-256");

    private final String name;
    private MessageDigest generator = null;

    private Hasher(String name)
    {
        this.name = name;
        try
        {
            this.generator = MessageDigest.getInstance(name);
        }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
    }

    public byte[] digest(String message)
    {
        return generator.digest(message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String toString()
    {
        return name;
    }
}
