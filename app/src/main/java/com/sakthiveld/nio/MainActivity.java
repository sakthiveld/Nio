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
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private static final String sHost = "uatstream.angelbroking.com";
    private static final int iPort = 8444;
//    private static final String sStreamingRequest = "63=FT3.0|64=206|65=1|1=5$7=214599|1=5$7=214908|230=1";

//    private static final String sHost = "mprod-stream2.angelbroking.com";
//    private static final int iPort = 8443;
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

//                    while (true) {
//                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//                        socketChannel.read(byteBuffer);
//                        Log.e("response", String.valueOf(socketChannel.read(byteBuffer)));
//                    }

                    while (true) {
                        //                       ByteBuffer allDataBuffer = ByteBuffer.allocate(1);
//                        socketChannel.read(allDataBuffer);
//                        int allDataSize = socketChannel.read(allDataBuffer);
//                        Log.e("size", String.valueOf(allDataSize));

                        //ByteBuffer[] allDataBuffer = new ByteBuffer[3];

                        ByteBuffer firstDataBuffer = ByteBuffer.allocate(1);
                        socketChannel.read(firstDataBuffer);
                        ByteBuffer secondDataBuffer = ByteBuffer.allocate(1);
                        socketChannel.read(secondDataBuffer);
                        ByteBuffer thirdDataBuffer = ByteBuffer.allocate(1);
                        socketChannel.read(thirdDataBuffer);

                        int lengthToRead = firstDataBuffer.get(0);
                        int isResponseCompressed = thirdDataBuffer.get(0);

                        //Log.e("Response----->>", "Length-"+lengthToRead+"       compresss-"+isResponseCompressed);

                        ByteBuffer dataByteBuffer = ByteBuffer.allocate(lengthToRead);
                        socketChannel.read(dataByteBuffer);

                        //Log.e("data byte buffer", Arrays.toString(dataByteBuffer.array()));

                        byte[] body = dataByteBuffer.array();

                        //Log.e("data byte", Arrays.toString(body));

                        process(body ,isResponseCompressed ,lengthToRead);

                    }

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

    public static int toUnsignedInt(short x) {
        return ((int) x) & 0xffff;
    }
    public static int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }
    public static long toUnsignedLong(int x) {
        return ((long) x) & 0xffffffffL;
    }

    private static void process(byte[] receiveData, int compressFlag, int iSize) {

        byte[] result = null;
        int unCompressedLength = 0;
        if(compressFlag == 1) {
            Log.e("Compress", "enabled");
            Inflater decompresser = new Inflater();
            decompresser.setInput(receiveData, 0, iSize);
            try {
                int uncompressedBufferLen = iSize * 20;
                result = new byte[uncompressedBufferLen];
                unCompressedLength = decompresser.inflate(result);
                System.out.println(Arrays.toString(result));
                if ((uncompressedBufferLen == unCompressedLength) && (!decompresser.finished())) {
                    result = new byte[uncompressedBufferLen];
                    unCompressedLength = decompresser.inflate(result);
                }
                decompresser.end();

            } catch (DataFormatException e) {
                e.printStackTrace();
            }
        }else {
            result = receiveData;
            unCompressedLength = result.length;
            Log.e("Compress length", "  "+unCompressedLength);
        }
        int read = 0;
        while (read<unCompressedLength) {
            int transCode = result[read];
            //Log.e("transCode", "  "+transCode);

            int size = getSizeForTransCode(transCode);
            if(size<=0)
                return;
            byte[] temp = new byte[size];
            System.arraycopy(result, read+1, temp, 0, temp.length);
            StreamingPacket reciev = new StreamingPacket();
            reciev.createFromBytes(temp, transCode);
            read = read+1+size;
            System.out.println(reciev.toString());
        }
    }

    public static int getSizeForTransCode(int transCode){
        switch (transCode){
            case AppConstant.QUOTE_1:
                return AppConstant.QUOTE_1_SIZE;
            case AppConstant.QUOTE_1_NCDEX:
                return AppConstant.QUOTE_1_NCDEX_SIZE;
            case AppConstant.QUOTE_2:
                return AppConstant.QUOTE_2_SIZE;
            case AppConstant.QUOTE_2_NCDEX:
                return AppConstant.QUOTE_2_NCDEX_SIZE;
            case AppConstant.QUOTE_3:
                return AppConstant.QUOTE_3_SIZE;
            case AppConstant.QUOTE_3_NCDEX:
                return AppConstant.QUOTE_3_NCDEX_SIZE;
        }
        return 0;
    }

}