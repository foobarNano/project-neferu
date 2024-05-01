package authorization;

import java.io.Serializable;

public class User implements Serializable
{
    public final String name;
    public final Permission[] permissions;

    User(String name, Permission... permissions)
    {
        this.name = name;
        this.permissions = permissions;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
