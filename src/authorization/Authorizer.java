package authorization;

import java.util.HashMap;

import javax.security.sasl.AuthenticationException;

import utility.Constants;
import utility.Serializer;

public final class Authorizer
{

    private static Authorizer authorizer;
    private static HashMap<Byte[], User> users;

    private Authorizer()
    {
        synchronize();
    }

    public static Authorizer getInstance()
    {
        if (authorizer == null) authorizer = new Authorizer();

        return authorizer;
    }

    /**
     * Loads the persistent user database or creates a new one if none can be retrieved.
     */
    public static void synchronize()
    {
        HashMap<Byte[], User> temp = Serializer.read_object(Constants.USERBASE_PATH);

        if (temp == null) temp = new HashMap<>();

        users = temp;
    }

    public User authorize(Byte[] hash) throws AuthenticationException
    {
        if (!users.containsKey(hash))
        {
            throw new AuthenticationException("Unable to authenticate client.");
        }

        return users.get(hash);
    }
}
