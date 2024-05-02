package ship.sensor;

import ship.ShipSector;

public abstract class Sensor<T extends Number> implements Runnable
{
    public final String NAME;
    public final ShipSector SECTOR;

    protected volatile T value;
    protected volatile boolean running;

    public Sensor(String name, ShipSector sector)
    {
        this.NAME = name;
        this.SECTOR = sector;
        this.running = false;
    }

    public void stop()
    {
        running = false;
    }

    public abstract T read();
    public abstract void calibrate();
}
