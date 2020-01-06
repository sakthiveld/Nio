package com.sakthiveld.nio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.zip.Deflater;

public class MainActivity extends AppCompatActivity {

    private static final String sHost = "uatstream.angelbroking.com";
    private static final int iPort = 8444;
    private static final String sStreamingRequest = "63=FT3.0|64=206|65=1|1=5$7=214599|1=5$7=214908|230=1";

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
        compresser.setInput("63=FT3.0|64=206|65=1|1=1$7=26000|230=1|".getBytes());
        compresser.finish();
        int prefixLength = compresser.deflate(send);
        compresser.end();
        byte start = 5;
        String convertValue = Integer.toString(prefixLength);
        String actualLength = getZeroString(convertValue.length()) + convertValue;
        byte[] actualBytes = new byte[1024];
        actualBytes[0] = start;

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

        @Override
        protected Boolean doInBackground(ByteBuffer... byteBuffers) {

            try {
                if(connected()) {
                    Log.e("Connection Report", "connected");

                    ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024);
                    byteBuffer1.put(byteReturn());
                    socketChannel.write(byteBuffer1);

                    socketChannel.write(byteBuffers);

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    Log.e("response", String.valueOf(socketChannel.read(byteBuffer)));


                } else {
                    socketChannel = SocketChannel.open();
                    socketChannel.connect(new InetSocketAddress("uatstream.angelbroking.com", 8444));
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



//63=FT3.0|64=206|65=1|1=1$7=15083|1=1$7=236|1=1$7=5900|1=1$7=16669|1=1$7=16675|1=1$7=317|1=1$7=10604|1=1$7=526|1=1$7=547|1=1$7=694|1=1$7=20374|1=1$7=881|1=1$7=910|1=1$7=4717|1=1$7=1232|1=1$7=7229|1=1$7=1330|1=1$7=1333|1=1$7=1348|1=1$7=1363|1=1$7=1394|1=1$7=4963|1=1$7=5258|1=1$7=29135|1=1$7=1594|1=1$7=1624|1=1$7=1660|1=1$7=11723|1=1$7=1922|1=1$7=11483|1=1$7=26000|1=3$7=19000|230=1|