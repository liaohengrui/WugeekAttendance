package com.wugeek.wugeek.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wugeek.wugeek.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WuGeek";

    /**
     * 微信登录相关
     */
    private IWXAPI api;
    private String appId = "wx78fcb446852673c3";
    private String appSecret = "dacb76137f55e876344871c34c7aa7da";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过WXAPIFactory工厂获取IWXApI的示例
        api = WXAPIFactory.createWXAPI(this, appId, true);
        //将应用的appid注册到微信
        api.registerApp(appId);
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            boolean result = api.handleIntent(getIntent(), this);
            if (!result) {
                Log.d("ssssssss", "参数不合法，未被SDK处理，退出");
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {

        Log.d(TAG, "onResp----:" + baseResp.errCode);
        String code = ((SendAuth.Resp) baseResp).code;
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("ssssssss", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject json = (JSONObject) JSON.parse(response.body().string());
                String token = (String) json.get("access_token");
                String openID = (String) json.get("openid");
                String userInfo = getUserInfo(openID, token);
                Log.d(TAG, "用户信息: " + userInfo);
            }
        });


    }

    private String getUserInfo(String openID, String accessToken) throws IOException {
        final String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openID;

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
        Request request = new Request.Builder()
                .url(url)//请求接口。如果需要传参拼接到接口后面。
                .build();//创建Request 对象
        Response response = null;
        response = client.newCall(request).execute();//得到Response 对象
        return response.body().string();
    }


}
