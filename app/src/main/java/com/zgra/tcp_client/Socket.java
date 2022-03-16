package com.zgra.tcp_client;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


//TCPサーバー作成
final class TCP_Server extends Thread {

    //グローバル
    private final Handler mHandle = new Handler();
    private final Context conText;

    TCP_Server(Context context) {
        conText = context;
    }

    //サーバー開始
    public void run() {
        //結果表示用
        EditText ed = ((MainActivity) conText).findViewById(R.id.result_text);

        try (ServerSocket server = new ServerSocket(3123)) {
            while (true) {
                Socket socket = server.accept();

                // ソケット受信時処理
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                final StringBuilder messageBuilder = new StringBuilder();
                while ((message = in.readLine()) != null) {
                    messageBuilder.append(message);
                }

                //結果表示
                mHandle.post((() -> {
                    // 現在日時を取得
                    LocalDateTime nowDate = LocalDateTime.now();
                    System.out.println(nowDate);
                    // 表示形式を指定
                    DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    String formatNowDate = dtf1.format(nowDate);
                    //結果代入
                    ed.append(String.format("\n%s : %s", formatNowDate, messageBuilder));
                }));

                //最終保存
                FileOutputStream fileOutputstream = conText.openFileOutput("text.txt", Context.MODE_PRIVATE);
                fileOutputstream.write(messageBuilder.toString().getBytes());
            }
        } catch (IOException e) {
            mHandle.post((() -> Toast.makeText(conText, e.toString(), Toast.LENGTH_LONG)));
        }
    }
}


//TCP内容送信
final class TCP_Send extends Thread {

    //グローバル
    private final Handler mHandle = new Handler();
    private final Context conText;

    TCP_Send(Context context) {
        conText = context;
    }

    //TPC内容送信
    public void run() {

        //送信先ipとポート取得
        EditText ip_ = (EditText) ((MainActivity) conText).findViewById(R.id.IP_text);
        String ip = ip_.getText().toString();
        EditText port_ = (EditText) ((MainActivity) conText).findViewById(R.id.Port_text);
        int port = Integer.parseInt(port_.getText().toString());
        //送信内容取得
        EditText content = (EditText) ((MainActivity) conText).findViewById(R.id.send_text);

        try (Socket socket = new Socket()) {

            //接続
            InetSocketAddress address = new InetSocketAddress(ip, port);
            socket.connect(address, 3000);

            //内容送信
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            pw.println(String.format(Locale.JAPAN, "%s", content.getText().toString()));
        } catch (Exception e) {
            mHandle.post(() -> Toast.makeText(conText, e.toString(), Toast.LENGTH_LONG));
        } finally {
            //内容クリア
            content.setText("");
        }
    }

}