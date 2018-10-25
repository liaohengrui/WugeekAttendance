package com.wugeek.wugeek;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dd.CircularProgressButton;
import com.qmuiteam.qmui.QMUILog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wugeek.wugeek.utils.MacUtils;

import java.io.IOException;
import java.util.logging.Logger;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Builder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {
    private String appId = "wx78fcb446852673c3";
    @BindView(R.id.tv_can_not_login)
    TextView tvCannotLogin;
    private Handler handler = null;
    private static final String TAG = "MainActivity";
    String mac = MacUtils.getNewMac();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.wx_login).setOnClickListener(this);
        findViewById(R.id.tv_register).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                final QMUITipDialog tipDialog;
                tipDialog = new QMUITipDialog.Builder(MainActivity.this)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord("正在加载")
                        .create();
                tipDialog.show();

                final Runnable runnableUi = new Runnable() {
                    @Override
                    public void run() {
                        tipDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, OnlineInfo.class);
                        finish();
                        startActivity(intent);
                    }
                };

                final Runnable loginError = new Runnable() {
                    @Override
                    public void run() {
                        tipDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "登入失败!密码或账户错误", Toast.LENGTH_LONG).show();
                    }
                };
                final String user_name = ((EditText) findViewById(R.id.et_name)).getText().toString();
                final String pass_word = ((EditText) findViewById(R.id.et_password)).getText().toString();
                final String json = " {\"account\": \"" + user_name + "\",\"password\": \"" + pass_word + "\" }";
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                String url = "http://api.wugeek.vczyh.com/auth/user/auth/account";
                OkHttpClient okHttpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)//默认就是GET请求，可以不写
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + e.getStackTrace());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String token = response.body().string();
                        Integer code = JSON.parseObject(token).getInteger("code");
                        token = JSON.parseObject(token).getString("data");
                        if (code == 1) {
                            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                            editor.putString("token", token);
                            editor.apply();
                            String macJson = " {\n" +
                                    "     \"mac\":\"" + mac + "\"\n" +
                                    " }";
                            RequestBody requestBody2 = RequestBody.create(MediaType.parse("application/json"), macJson);
                            Request request2 = new Request.Builder()
                                    .url("http://api.wugeek.vczyh.com/attendance/user/userInfo/alter")
                                    .addHeader("token", token)
                                    .post(requestBody2)//默认就是GET请求，可以不写
                                    .build();
                            OkHttpClient okHttpClient2 = new OkHttpClient();
                            Call call2 = okHttpClient2.newCall(request2);
                            call2.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Log.d(TAG, "onResponse: " + response.body().string());
                                    handler.post(runnableUi);
                                }
                            });

                        } else {
                            handler.post(loginError);
                        }
                    }
                });
                break;

            case R.id.wx_login:
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                IWXAPI api = WXAPIFactory.createWXAPI(MainActivity.this, appId, true);
                api.registerApp(appId);
                api.sendReq(req);
                break;

            case R.id.tv_register:
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                System.out.println("gg");
        }

    }
}