package com.example.bluetoothstepper;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bluetoothstepper.threads.BluetoothConnectionThread;

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

    private Button mAntiClockButton;
    private Button mClockButton;
    private Button mConnectButton;
    private Button mDisconnectButton;

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
            if (device.getName().equals("HC05"))
            {
                address = device.getAddress();
            }
        }

        Toast.makeText(this, address, Toast.LENGTH_SHORT).show();

        mConnectButton = findViewById(R.id.connectButton);
        mDisconnectButton = findViewById(R.id.disconnectButton);
        mClockButton = findViewById(R.id.rightButton);
        mAntiClockButton = findViewById(R.id.leftButton);

        mConnectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.this, "Connect click", Toast.LENGTH_LONG).show();
                activate = true;
                onResume();
            }
        });

        mAntiClockButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.this, "Anti clockwise click", Toast.LENGTH_LONG).show();
                connectionThread.write("0");
            }
        });

        mClockButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.this, "Clockwise click", Toast.LENGTH_LONG).show();
                connectionThread.write("1");
            }
        });

        mDisconnectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.this, "Disconnect click", Toast.LENGTH_LONG).show();

                try
                {
                    bluetoothSocket.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        checkBluetooth();
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