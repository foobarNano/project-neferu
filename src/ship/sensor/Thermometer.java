package ship.sensor;

import ship.ShipSector;

public class Thermometer extends Sensor<Double>
{
    public Thermometer(String name, ShipSector sector)
    {
        super(name, sector);
        calibrate();
    }

    @Override
    public Double read()
    {
        return value;
    }

    @Override
    public void calibrate()
    {
        value = 20.0;
    }

    public void run()
    {
        running = true;

        while (running)
        {
            value += ((Math.random() - 0.5) / 10);
        }
    }
    
}
