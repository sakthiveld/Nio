package com.sakthiveld.nio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.zip.Deflater;

public class MainActivity extends AppCompatActivity {

//    private static final String sHost = "uatstream.angelbroking.com";
//    private static final int iPort = 8444;
//    private static final String sStreamingRequest = "63=FT3.0|64=206|65=1|1=5$7=214599|1=5$7=214908|230=1";

    private static final String sHost = "mprod-stream2.angelbroking.com";
    private static final int iPort = 8443;
    private static final String sStreamingRequest = "63=FT3.0|64=206|65=1|1=5$7=213728|1=5$7=214023|1=5$7=214023|1=5$7=215546|230=1";

    SocketChannel socketChannel = null;
    SocketTask socketTask;
    RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler_view = findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setAdapter(recyclerViewAdapter);

        createSocket();

    }

    private ByteBuffer makeRequest(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        byte[] send = new byte[1024];
        Deflater compresser = new Deflater();
        compresser.setInput(sStreamingRequest.getBytes());
        compresser.finish();
        int prefixLength = compresser.deflate(send);
        compresser.end();
        byte start = 5;
        String convertValue = Integer.toString(prefixLength);
        String actualLength = getZeroString(convertValue.length()) + convertValue;
        byte[] actualBytes = new byte[1024];
        actualBytes[0] = start;
        System.arraycopy(actualLength.getBytes(), 0, actualBytes, 1, actualLength.length());
        if (prefixLength >= 0)
            System.arraycopy(send, 0, actualBytes, 6, prefixLength);


        //Log.e("byte", Arrays.toString(actualBytes));


        return ByteBuffer.wrap(actualBytes);

    }

    public static final int CASE_1 = 1;
    public static final int CASE_2 = 2;
    public static final int CASE_3 = 3;
    public static final int CASE_4 = 4;
    public static String getZeroString(int length) {
        switch (length) {
            case CASE_1:
                return "0000";
            case CASE_2:
                return "000";
            case CASE_3:
                return "00";
            case CASE_4:
                return "0";
        }
        return "";
    }

    private void createSocket() {
        socketTask =  new SocketTask();
        socketTask.execute(makeRequest());
    }

    private boolean connected() {
        if (socketChannel != null)
            return socketChannel.isConnected();
        else{
            return false;
        }
    }

    private byte[] byteReturn(){
        byte[] send = new byte[1024];
        Deflater compresser = new Deflater();
        compresser.setInput("HELLO".getBytes());
        compresser.finish();
        int prefixLength = compresser.deflate(send);
        compresser.end();
        byte start = 5;
        String convertValue = Integer.toString(prefixLength);
        String actualLength = getZeroString(convertValue.length()) + convertValue;
        byte[] actualBytes = new byte[1024];
        actualBytes[0] = start;

        return actualBytes;
    }

    class SocketTask extends AsyncTask<ByteBuffer, String, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(ByteBuffer... byteBuffers) {

            try {
                if(connected()) {
                    Log.e("Connection Report", "connected");

//                    ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024);
//                    byteBuffer1.put(byteReturn());
//                    socketChannel.write(byteBuffer1);

                    socketChannel.write(byteBuffers);
                    Log.e("request", Arrays.toString(byteBuffers[0].array()));

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    Log.e("response", String.valueOf(socketChannel.read(byteBuffer)));





                } else {
                    socketChannel = SocketChannel.open();
                    socketChannel.connect(new InetSocketAddress(sHost, iPort));
                    socketChannel.configureBlocking(true);
                    Log.e("Connection Report", "Not connected");
                    socketTask.cancel(true);
                    createSocket();
                }
            } catch (IOException e) {
                Log.e("Connection Report", e.toString());
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }




}