package server;

import java.util.function.Function;

public abstract class ServerFunction implements Function<String[], String>
{
    private final Permission[] permissions;

    public ServerFunction(String description, Permission... permissions)
    {
        this.permissions = permissions;
    }

    public boolean authorize(Permission[] permissions)
    {
        // TODO: Implement a multi-level permission system
        return true;
    }

    public abstract String apply(String[] args);
}
