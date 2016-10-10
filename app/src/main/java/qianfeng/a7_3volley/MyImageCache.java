package qianfeng.a7_3volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2016/10/10 0010.
 */
public class MyImageCache implements ImageLoader.ImageCache {
    // 在这里实现图片的三级缓存机制
    // 不过在这里只需要两层就够了，因为如果在SDCard中找不到，它会自己去url那里网络下载，这步是不用你自己做的，待会系统会调用putBitmap(),存储进SDCard中

    private Context context;
    private LruCache<String, Bitmap> lruCache;

    public MyImageCache(Context context) {
        this.context = context;
        long maxMemory = Runtime.getRuntime().maxMemory();
        long maxSize = maxMemory / 8;
        lruCache = new LruCache<String, Bitmap>((int) maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount(); // 得到Bitmap的大小
            }
        };

    }

    @Override
    public Bitmap getBitmap(String url) { // 这里是找图片
        Bitmap bitmap = lruCache.get(getUrl(url));
        if (bitmap == null) {
            // 去SDCard中找
            bitmap = findBitmapInSDCard(getUrl(url));
            if(bitmap != null) // 在SDCard中找得到的话，就存进集合中
            {
                putBitmap(url,bitmap); // 存进集合中
            }
        }
        return bitmap;
    }

    private Bitmap findBitmapInSDCard(String url) {
         return BitmapFactory.decodeFile(new File(context.getExternalCacheDir(), url).getAbsolutePath());
    }


    private String getUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length());
    }


    @Override
    public void putBitmap(String url, Bitmap bitmap) { // 这里是存储图片
        // 存储进集合，如果集合有了，就存储进SDCard中
        lruCache.put(getUrl(url),bitmap);

        // 如果是存储数据话，存了进集合中了，那就检查 sdcard 中有没有存储这条数据，如果没有的话，那就存储进 sdCard 中
        Bitmap bitmapInSDCard = findBitmapInSDCard(getUrl(url));
        if(bitmapInSDCard == null)
        {
            saveBitmap2Sdcard(getUrl(url),bitmap);// 存储的这张图片来源于集合里，因为这种情况下，从SDCard中读回来的图片是空的，不能作为存储实体。
        }

    }

    private void saveBitmap2Sdcard(String url, Bitmap bitmapInSDCard) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(context.getExternalCacheDir(),url));
            if(url.toLowerCase().endsWith(".png"))
            {
                bitmapInSDCard.compress(Bitmap.CompressFormat.PNG,100,fos);
            }else
            {
                bitmapInSDCard.compress(Bitmap.CompressFormat.JPEG,100,fos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
