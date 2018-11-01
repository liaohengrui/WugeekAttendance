package com.wugeek.wugeek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.wugeek.wugeek.bean.RegisterInfo;
import com.wugeek.wugeek.utils.MacUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity implements Button.OnClickListener {
    private static final String TAG = "RegisterActivity";
    Handler handler;
    QMUITipDialog errorDialog;
    QMUITipDialog okDialog;
    int time = 60;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final SharedPreferences.Editor editor = getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.check_code_button).setOnClickListener(this);
        handler = new Handler();

    }

    @Override
    public void onClick(View v) {

        String password = ((EditText) findViewById(R.id.text_password)).getText().toString();
        String userName = ((EditText) findViewById(R.id.text_userid)).getText().toString();
        String phone = ((EditText) findViewById(R.id.text_phone)).getText().toString();
        String code = ((EditText) findViewById(R.id.text_check_code)).getText().toString();
        String rePassword = ((EditText) findViewById(R.id.text_repassword)).getText().toString();
        errorDialog = new QMUITipDialog.Builder(RegisterActivity.this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("请检查填写信息")
                .create();

        okDialog = new QMUITipDialog.Builder(RegisterActivity.this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在加载")
                .create();


        switch (v.getId()) {
            case R.id.button2:
                if (code.isEmpty() || phone.isEmpty() || userName.isEmpty() || password.isEmpty() || rePassword.isEmpty()
                        || !rePassword.equals(password)) {
                    handler.post(runnableErrorUi(false));
                } else {
                    String url2 = "http://api.wugeek.vczyh.com/auth/user/register/account2";
                    String json2 = "{\n" +
                            "  \"username\": \"" + userName + "\",\n" +
                            "  \"password\": \"" + password + "\",\n" +
                            "  \"phone\":\"" + phone + "\",\n" +
                            "  \"code\": \"" + code + "\"\n" +
                            "}";
                    Log.d(TAG, "onClick: " + json2);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json2);
                    OkHttpClient okHttpClient = new OkHttpClient();


                    final Request request = new Request.Builder()
                            .url(url2)
                            .post(requestBody)//默认就是GET请求，可以不写
                            .build();

                    final Runnable finnish = new Runnable() {
                        @Override
                        public void run() {
                            okDialog.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    okDialog.dismiss();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            }, 4000);
                        }
                    };


                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: " + e.getStackTrace());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String token = response.body().string();
                            Log.d(TAG, "onResponse: " + token);
                            Integer code = JSON.parseObject(token).getInteger("code");
                            if (code == 1) {
                                handler.post(finnish);

                            } else {
                                handler.post(runnableErrorUi(false));
                            }

                        }
                    });
                }
                break;
            case R.id.check_code_button:

                Log.d(TAG, "onClick: check_code_button");
                String url1 = "http://api.wugeek.vczyh.com/auth/user/register/account2";
                String json = "{\n" +
                        "  \"username\":\"" + userName + "\",\n" +
                        "  \"password\":\"" + password + "\",\n" +
                        "  \"phone\":\"" + phone + "\"\n" +
                        "}";
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                OkHttpClient okHttpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(url1)
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
                        if (code == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Button button = findViewById(R.id.check_code_button);
                                    button.setEnabled(false);
                                    button.setBackgroundResource(R.drawable.check_border2);
                                    /** 倒计时60秒，一次1秒 */
                                    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            // TODO Auto-generated method stub
                                            button.setText(millisUntilFinished / 1000 + "秒");
                                        }

                                        @Override
                                        public void onFinish() {
                                            button.setText("发送验证码");
                                            button.setEnabled(true);
                                        }
                                    }.start();
                                }
                            });
                        } else {
                            handler.post(runnableErrorUi(false));
                        }
                    }
                });
                break;
            default:
        }
    }

    private Runnable runnableErrorUi(boolean isOk) {
        Runnable runnableErrorUi = new Runnable() {
            @Override
            public void run() {
                errorDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorDialog.dismiss();
                    }
                }, 2000);
            }
        };
        Runnable runnableOkUi = new Runnable() {
            @Override
            public void run() {
                okDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        okDialog.dismiss();
                    }
                }, 4000);
            }
        };
        return isOk ? runnableOkUi : runnableErrorUi;
    }
}
