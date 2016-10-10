package qianfeng.a7_3volley;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private RequestQueue queue;
    private ImageView iv;
    private NetworkImageView networkImageView;
    private ImageLoader imageLoader;
    private ImageLoader.ImageListener imageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = ((TextView) findViewById(R.id.tv));

        iv = ((ImageView) findViewById(R.id.iv));

        networkImageView = (NetworkImageView) findViewById(R.id.networkimageview);


        // 一般这个是放在自定义的Application中初始化，然后提供方法暴露这个queue，
        // 然后通过getApplication()强转成我自定义的类型， 然后得到里面的queue实例
        queue = Volley.newRequestQueue(this); // 创建请求队列，


        networkImageView.setDefaultImageResId(R.mipmap.ic_launcher);
        networkImageView.setErrorImageResId(R.mipmap.ic_launcher);


        imageLoader = new ImageLoader(queue,new MyImageCache(this));
        imageListener = ImageLoader.getImageListener(iv, R.mipmap.ic_launcher,R.mipmap.ic_launcher);






    }

    public void stringRequest(View view) {

        //1.请求方式，默认为get请求
        //2.请求地址
        //3.请求成功时的回调
        //4.请求失败时的回调
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.baidu.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) { // 这个是在主线程中运行的，是可以在这里直接更新UI的
                tv.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            // POST请求通过重写getParams()方法来传递键值对数据,其他三种方式的POST请求，都可以在参数中直接写上去，然后在第一个参数中选择Method.POST就可以实现POST请求
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        // 把请求添加到队列中！！
        queue.add(stringRequest);

    }


    public void jsonObjectRequest(View view) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://www.tngou.net/api/cook/classify", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // 这里的response就是JSONObject类型，不用你new了，它帮你写了一行代码了 JSONObject response = new JSONObject(s);
                JSONArray tngou = null;
                StringBuffer sb = new StringBuffer();
                try {
                    tngou = response.getJSONArray("tngou");
                    for (int i = 0; i < tngou.length(); i++) {
                        JSONObject data = tngou.getJSONObject(i);
                        sb.append(data.getString("name")).append("\n");
                    }
                    tv.setText(sb.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonObjectRequest);
    }

    public void jsonArrayRequest(View view) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("http://api.iclient.ifeng.com/ClientNews?id=SYLB10,SYDT10,SYRECOMMEND&page=1&newShowType=1&province=%E5%B9%BF%E4%B8%9C%E7%9C%81&city=%E5%B9%BF%E5%B7%9E%E5%B8%82&district=%E5%A4%A9%E6%B2%B3%E5%8C%BA&gv=5.2.0&av=5.2.0&uid=868192023562255&deviceid=868192023562255&proid=ifengnews&os=android_21&df=androidphone&vt=5&screen=1080x1920&publishid=6001", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                StringBuffer result = new StringBuffer();
                try {
                    JSONArray item = response.getJSONObject(0).getJSONArray("item");
                    for (int i = 0; i < item.length(); i++) {
                        JSONObject data = item.getJSONObject(i);
                        result.append(data.getString("title")).append("\n");
                    }
                    tv.setText(result.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonArrayRequest);
    }


    public void imageRequest(View view) {

        ImageRequest imageRequest = new ImageRequest("http://fragment.firefoxchina.cn/res/img/logo_zww.png", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                iv.setImageBitmap(response);
            }
        }, 200, 200, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(imageRequest);

    }

    public void imageLoader(View view) {

        ImageLoader imageLoader = new ImageLoader(queue,new MyImageCache(this));
        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(iv,R.mipmap.ic_launcher,R.mipmap.ic_launcher);
        imageLoader.get("http://fragment.firefoxchina.cn/res/img/logo_zww.png",imageListener);

    }

    public void niv(View view) {
        networkImageView.setImageUrl("http://p2.ifengimg.com/ifengimcp/pic/20161010/d2e9ef1113d0a55335ad_size9_w207_h148.jpg",imageLoader);
    }
}
