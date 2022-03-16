package com.zgra.tcp_client;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //グローバル

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ipアドレス取得
        Context context = getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
        int ip_ = info.getIpAddress();

        //表示
        String Ipaddress = String.format(Locale.JAPAN, "%02d.%02d.%02d.%02d", (ip_) & 0xff, (ip_ >> 8) & 0xff, (ip_ >> 16) & 0xff, (ip_ >> 24) & 0xff);
        TextView tv = (TextView) findViewById(R.id.ipshow_text);
        tv.setText(String.format(Locale.JAPAN, "アドレス\n%s:%d", Ipaddress, 3123));
        EditText ed = (EditText) findViewById(R.id.IP_text);
        ed.setText(Ipaddress);
    }

    //送信ボタン処理
    public void tpc_send_event(View view) {
        Toast.makeText(MainActivity.this, "送信中です。", Toast.LENGTH_SHORT).show();

        //サーバー開始
        TCP_Send th2 = new TCP_Send(this);
        th2.start();
    }

    //サーバー開始
    public void tcp_server_event(View view) {
        Toast.makeText(getApplicationContext(), "サーバー開始しました。", Toast.LENGTH_SHORT).show();
        //ボタン無効化
        Button btn0 = findViewById(R.id.tcp_server);
        btn0.setEnabled(false);
        //内容代入
        EditText ed = findViewById(R.id.result_text);
        ed.setText("サーバーを開始しています...\n");

        //サーバー開始
        TCP_Server th1 = new TCP_Server(this);
        th1.start();
    }


    //受信内容コピー
    public void copy_text(View view) {
        //ファイル読み込み
        FileInputStream fis = null;
        try {
            fis = openFileInput("text.txt");
        } catch (FileNotFoundException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        //あったら
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        //読み込み
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        } finally {
            String contents = stringBuilder.toString();
            //クリップボードコピー
            ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData myClip = ClipData.newPlainText("", contents);
            myClipboard.setPrimaryClip(myClip);
        }
    }
}