package com.example.bluethootstepper;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.bluethootstepper.threads.BluetoothConnectionThread;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket bluetoothSocket = null;

    private static final int ENABLE_BLUETOOTH_INTENT_REQUEST = 1;
    private static final UUID BLUETOOTH_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address = null;
    private static boolean activate =  false;

    private static BluetoothConnectionThread connectionThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : devices)
        {
            if (device.getName().equals("HC-05"))
            {
                address = device.getAddress();
            }
        }


    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        return device.createRfcommSocketToServiceRecord(BLUETOOTH_MODULE_UUID);
    }

    private void checkBluetooth()
    {
        if (!bluetoothAdapter.isEnabled())
        {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, ENABLE_BLUETOOTH_INTENT_REQUEST);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(activate)
        {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            try
            {
                bluetoothSocket = createBluetoothSocket(device);
            }
            catch(IOException e)
            {
                Toast.makeText(getBaseContext(), "Something went wrong while creating the socket", Toast.LENGTH_LONG).show();
            }

            try
            {
                bluetoothSocket.connect();
            }
            catch(IOException e)
            {
                try
                {
                    bluetoothSocket.close();
                }
                catch(IOException ignored)
                {

                }
            }

            connectionThread = new BluetoothConnectionThread(bluetoothSocket, this);
            connectionThread.start();
        }
    }
}