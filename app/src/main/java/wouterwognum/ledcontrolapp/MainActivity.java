package wouterwognum.ledcontrolapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
{
    public final static String DEVICE_NAME      = "Wouter's LED Thing";
    public final static String UUID_SERVICE     = "4fafc201-1234-459e-5678-c5c9c331914b";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Hashtable<BluetoothGattCharacteristic, String> BLEChar;
    private LinkedList<BluetoothGattCharacteristic> commandQueue;
    private Hashtable<BluetoothGattCharacteristic, String> previousCommand;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private final static int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private BluetoothGattService mService;
    private BluetoothAdapter bluetoothAdapter;
    private CircleImageView connectionStatus;

    private boolean waitForCallback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        checkRequiredPermissions();

        commandQueue = new LinkedList<BluetoothGattCharacteristic>();
        previousCommand = new Hashtable<BluetoothGattCharacteristic, String>();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionStatus = (CircleImageView) findViewById(R.id.connectionStatus);
        connectionStatus.setOnClickListener(new CircleImageView.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "clicked bt logo");
                scanLeDevice(true);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        // Ensures Bluetooth is available on the device and it is enabled. If not, displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void checkRequiredPermissions()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Needs permission to access location",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Needs permission to use / enable Bluetooth",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Needs permission to use / enable Bluetooth",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                mLEScanner = bluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
            scanLeDevice(true);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
        {
            scanLeDevice(false);
        }
    }

    @Override
    protected void onDestroy()
    {
        if (mGatt == null)
        {
            super.onDestroy();
            return;
        }
        mGatt.close();
        mGatt = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BT)
        {
            if (resultCode == Activity.RESULT_CANCELED)
            {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable)
    {
        if (enable)
        {
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            mLEScanner.startScan(filters, settings, mScanCallback);
        }
        else
        {
            mLEScanner.stopScan(mScanCallback);
        }
    }

    private ScanCallback mScanCallback = new ScanCallback()
    {

        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            try
            {
                if (result.getDevice().getName().equals(DEVICE_NAME))
                {
                    BluetoothDevice btDevice = result.getDevice();
                    connectToDevice(btDevice);
                }
            }
            catch (Exception e) {}
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results)
        {
            for (ScanResult sr : results)
            {
                try
                {
                    if (sr.getDevice().getName().equals(DEVICE_NAME))
                    {
                        BluetoothDevice btDevice = sr.getDevice();
                        connectToDevice(btDevice);
                    }
                }
                catch (Exception e) {}
            }
        }

        @Override
        public void onScanFailed(int errorCode)
        {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback()
            {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Log.i("onLeScan", device.toString());
                            connectToDevice(device);
                        }
                    });
                }
            };

    public void connectToDevice(BluetoothDevice device)
    {
        if (mGatt == null)
        {
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false); // will stop after first device detection
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState)
            {

                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();

                    // Set icon to connected
                    connectionStatus.setImageResource(R.drawable.bt_connected);
                    break;

                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");

                    // Set icon to connected
                    connectionStatus.setImageResource(R.drawable.bt_disconnected);
                    mGatt = null;
                    mService = null;
                    break;

                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            mService = gatt.getService(UUID.fromString(UUID_SERVICE));
            Log.i("service","Connected to service");

            for(BLEChars c : BLEChars.values())
            {
                c.setChar(mService.getCharacteristic(UUID.fromString(c.getUUID())));
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            try
            {
                if (status == BluetoothGatt.GATT_SUCCESS)
                {
                    Log.d("Sent BLE command succesfully", commandQueue.getFirst().getStringValue(0));
                    commandQueue.removeFirst();
                }
                else
                {
                    Log.d("Failed to send BLE command", commandQueue.getFirst().getStringValue(0));
                }
            }
            catch (Exception e)
            {
                Log.e("Error sending BLE command", e.toString());
            }

            waitForCallback = false;
            processNextCommand();
        }
    };

    public void processNextCommand()
    {
        try
        {
            if (!waitForCallback)
            {
                BluetoothGattCharacteristic c = null;

                if (commandQueue.size() > 0)
                {
                    while (commandQueue.size() > 0)
                    {
                        // Get first command from the queue
                        c = commandQueue.getFirst();

                        Log.d("Processing command",c.getStringValue(0));

                        // If value was previously sent, don't send it again.
                        if (previousCommand.containsKey(c) && previousCommand.get(c).equals(c.getStringValue(0)))
                        {
                            commandQueue.removeFirst();
                        }
                        else
                        {
                            break;
                        }
                    }
                }

                previousCommand.put(c, c.getStringValue(0));
                if (mGatt.writeCharacteristic(c)) waitForCallback = true;
                else Log.e("writeCharacteristic","Failed to write characteristic");
            }
        }
        catch (Exception e)
        {
            Log.e("Error processing command",e.toString());
        }
    }

    public void setBLEChar(BLEChars c, String value)
    {
        Log.d(c.name(), value);
        if (c.setCharValue(value))
        {
            commandQueue.add(c.getChar());
            processNextCommand();
        }
        else
        {
            Log.e("setCharValue","Failed to set value");
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    return new TabGeneral();

                case 1:
                    return new TabText();

                case 2:
                    return new TabEffects();

            }

            return null;
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }
    }
}
