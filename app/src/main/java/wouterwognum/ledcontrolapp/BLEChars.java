package wouterwognum.ledcontrolapp;

import android.bluetooth.BluetoothGattCharacteristic;

public enum BLEChars
{
    Mode        ("b936aa79-9bf6-442b-b689-c3bde7a3afce"),
    Brightness  ("beb5483e-1234-4688-5678-ea07361b26a8"),
    TextTPC     ("4918adf4-1cb3-4503-994f-9402457b8e12"),
    TextBBC     ("6e90ed5e-6e35-4eff-bbbd-5d036907e7aa"),
    TextFAD     ("50a8b005-930b-4a73-8d0c-652c0dc81a3c"),
    TextEVR     ("12345678-930b-4a73-8d0c-652c0dc81a3c"),
    TextFFC     ("12345678-8888-4a73-8d0c-652c0dc81a3c"),
    TextPAL     ("12345678-9999-4a73-8d0c-652c0dc81a3c"),
    Text1       ("8faa743c-a767-4f44-996b-75b648680f20"),
    Text2       ("8faa743c-a767-4f44-996b-75b648680f21"),
    Effect      ("87654321-a767-1f4a-996b-15b648680f2a"),
    EffectSPD   ("8faa743c-a767-1f4a-996b-15b648680f2a"),
    EffectSCL   ("8faa743c-a767-3f4c-996b-35b648680f2c"),
    EffectFAD   ("12345678-a767-3f4c-996b-35b648680f2c"),
    EffectPAL   ("8faa743c-a767-4f4d-996b-45b648680f2d"),
    EffectCLL   ("8faa743c-a767-5f4e-996b-55b648680f2e");

    private String UUID;
    private BluetoothGattCharacteristic characteristic;

    BLEChars(String UUID)
    {
        this.UUID = UUID;
    }

    public void setChar(BluetoothGattCharacteristic c)
    {
        this.characteristic = c;
    }

    public BluetoothGattCharacteristic getChar()
    {
        return characteristic;
    }

    public boolean setCharValue(String value)
    {
        if (characteristic != null)
        {
            this.characteristic.setValue(value);
            return true;
        }
        else return false;

    }

    public String getUUID()
    {
        return UUID;
    }
}