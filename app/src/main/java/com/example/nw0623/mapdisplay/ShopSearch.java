package com.example.nw0623.mapdisplay;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShopSearch extends AsyncTask<URL,Void,String > {

    private String result;
    private CallBackTask callbacktask;

    //コンストラクター
    /*public ShopSearch(TextView tv) {
        super();
        this.tv = tv;
    }*/

    //doInBackgroud:非同期処理を行う
    //AsyncTaskの第一引数を受け取って処理を行う
    @Override
    protected String doInBackground(URL... urls) {
        HttpURLConnection con = null;
        // URLの作成
        URL url = urls[0];

        try {
            // 接続用HttpURLConnectionオブジェクト作成
            con = (HttpURLConnection) url.openConnection();
            // リクエストメソッドの設定
            con.setRequestMethod("GET");
            // リダイレクトを自動で許可しない設定
            con.setInstanceFollowRedirects(false);
            // タイムアウト時間の設定
            con.setConnectTimeout(5000);
            // URL接続からデータを読み取る場合はtrue
            con.setDoInput(true);
            // URL接続にデータを書き込む場合はtrue
            // →これを書くとRequestMethodがPOSTに変わってしまうので、書いてはいけない事になった
            //con.setDoOutput(true);

            // 接続
            con.connect();

            //APIで渡されたデータ(json）を取得
            BufferedInputStream inputStream = new BufferedInputStream(con.getInputStream());
            ByteArrayOutputStream responseArray = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];

            // 取得したデータをバッファからメモリにセット
            int length;
            while((length = inputStream.read(buff)) != -1) {
                if(length > 0) {
                    responseArray.write(buff, 0, length);
                }
            }
            // 取得データをString変数にセット
            result = new String(responseArray.toByteArray());

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                // コネクションを切断
                con.disconnect();
            }
        }
        // 取得した結果を返す
        // この値がonPostExecuteに引き渡される
        return result;
    }

    // 非同期処理の後処理
    // AsyncTaskの第三引数を受け取って処理を行う
    // doInbackground終了後UIスレッドから勝手に呼ばれる（自力で呼んではいけない）
    public void onPostExecute(String ret) {
        super.onPostExecute(ret);
        callbacktask.CallBack(ret);

    }

    public void setOnCallBack(CallBackTask _cbj) {
        callbacktask = _cbj;
    }

    public static class CallBackTask {
        public void CallBack(String ret){

        }
    }
}
