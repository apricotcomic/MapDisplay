package com.example.nw0623.mapdisplay;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText editText;
    private DownloadTask task;
    private ShopSearch shopSearch;
    private TextView textView;
    private Bitmap bmp;

    String param0;
    String param1;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //画面項目の定義
        editText = findViewById(R.id.searchWord);
        imageView = findViewById(R.id.map);
        Button searchButton = findViewById(R.id.searchButton);
        textView = findViewById(R.id.apiResult);

        // MAPの格納フォルダ及びファイル名を指定
        param0 = "http://192.168.5.224:8080/dltest/05.jpg";

        //param0が外から入力されるつもりにしているのでIF文で判定
        //今は固定でparam0を指定してしまっているのでIF文の意味はない
        if(param0.length() != 0){
            task = new DownloadTask();
            // Listenerを設定
            task.setListener(createListener());
            // 非同期処理を開始
            task.execute(param0);
        }

        //検索ボタンをタップしたときの処理
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //店舗位置情報取得
                // 本来はeditTextに入力された内容で検索
                //結果がMAP上のアドレスで返ってくるつもり
                //今は何が入力されても同じ値しか返らない
                param1 = editText.getText().toString();

                try {
                    // APIのuriを設定
                    URL uri = new URL("http://192.168.5.224:8080/api/shopinfo");
                    shopSearch = new ShopSearch();

                    //onBackgroundの戻り値取得
                    shopSearch.setOnCallBack(new ShopSearch.CallBackTask(){

                        @Override
                        public void CallBack(String result) {
                            super.CallBack(result);

                            // JSONをパース
                            int addr_x = 0;
                            int addr_y = 0;
                            try {
                                JSONObject jsonObj = new JSONObject(result);
                                addr_x = jsonObj.getInt("addr_x");
                                addr_y = jsonObj.getInt("addr_y");

                            } catch (JSONException e){
                                e.printStackTrace();
                            }

                            // 取得したアドレスを返す
                            //本来は取得したアドレスからMAP上にピンを表示
                            StringBuilder buff = new StringBuilder();
                            buff.append(addr_x);
                            buff.append(",");
                            buff.append(addr_y);

                            textView.setText(buff);

                            //フロア画像を取得
                            //Drawable bmp = imageView.getDrawable();
                            //ピン画像を取得
                            Drawable pin = ResourcesCompat.getDrawable(getResources(),R.drawable.pin,null);
                            //ピンにGravity設定
                            pin.setBounds(350,550,0,0);

                            //LayerDrawableを生成
                            //Drawable[] layers = { bmp , pin };
                            //LayerDrawable layerDrawable = new LayerDrawable(layers);
                            //Viewにセットする
                            //imageView.setImageDrawable(layerDrawable);
                            imageView.setImageDrawable(pin);

                        }
                    });

                    // AsyncTaskを起動
                    shopSearch.execute(uri);

                } catch (Exception e) {
                    //エラー発生時
                    Toast.makeText(MainActivity.this, "JSONエラー", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        task.setListener(null);
        super.onDestroy();
    }

    //画像ダウンロードのListener
    private DownloadTask.Listener createListener() {
        return new DownloadTask.Listener() {
            @Override
            public void onSuccess(Bitmap bmp) {
                imageView.setImageBitmap(bmp);
            }
        };
    }
}
