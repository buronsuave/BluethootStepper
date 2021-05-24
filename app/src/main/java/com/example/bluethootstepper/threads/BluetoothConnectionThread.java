package com.example.bluethootstepper.threads;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothConnectionThread extends Thread
{
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Context context;
    private Handler handler;

    public BluetoothConnectionThread(BluetoothSocket socket, Context context)
    {
        this.context = context;
        InputStream iStream = null;
        OutputStream oStream = null;
        try
        {
            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
        }
        catch (IOException ignored)
        {

        }

        inputStream = iStream;
        outputStream = oStream;
    }

    @Override
    public void run()
    {
        byte[] buffer = new byte[256];
        int bytes;

        while(true)
        {
            try
            {
                bytes = inputStream.read(buffer);
                String readMessage = new String(buffer, 0, bytes);

                int handlerState = 0;
                handler.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            }
            catch(IOException e)
            {
                break;
            }

        }

        super.run();
    }

    public void write(String input)
    {
        try
        {
            outputStream.write(input.getBytes());
        }
        catch(IOException e)
        {
            Toast.makeText(context, "Error creating thread", Toast.LENGTH_LONG).show();
        }
    }
}
