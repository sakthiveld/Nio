package com.sakthiveld.nio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    SocketChannel socketChannel;
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

    private void createSocket() {
        socketTask =  new SocketTask();
        socketTask.execute();
    }

    private boolean connected() {
//        if (socketChannel != null)
//            return socketChannel.isConnected();
//        else{
//            return false;
//        }
        return true;
    }

    class SocketTask extends AsyncTask<ByteBuffer, String, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(ByteBuffer... byteBuffers) {

            try {
                if(connected()) {
                    SocketChannel socketChannel = SocketChannel.open();
                    socketChannel.connect(new InetSocketAddress("uatstream.angelbroking.com", 8444));
                    socketChannel.configureBlocking(true);

                    String newData = "63=FT3.0|64=206|65=1|7=15083$1=1|7=236$1=1|7=5900$1=1|7=16669$1=1|7=16675$1=1|7=317$1=1|7=10604$1=1|230=1";
                    ByteBuffer bb = ByteBuffer.allocate(480);
                    bb.put(newData.getBytes());
                    socketChannel.write(bb);
                    socketChannel.finishConnect();
                    Log.e("request", bb.toString());

                    ByteBuffer bbb = ByteBuffer.allocate(84);
                    int result = socketChannel.read(bbb);

                    Log.e("response", String.valueOf(result));
                } else {
                    SocketNewTask socketNewTask = new SocketNewTask();
                    socketNewTask.execute();
                }
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "error"+e.toString(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    class SocketNewTask extends AsyncTask<ByteBuffer, String, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(ByteBuffer... byteBuffers) {
            try {
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.connect(new InetSocketAddress("uatstream.angelbroking.com", 8444));
                socketChannel.configureBlocking(true);
            } catch (IOException e) {
                e.printStackTrace();
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