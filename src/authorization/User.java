package authorization;

import java.io.Serializable;

public class User implements Serializable
{
    final String name;
    final Permission[] permissions;

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
