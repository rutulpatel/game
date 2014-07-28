package com.rutulpatel.game101;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by rutul on 7/26/14.
 */
public class RenderViewTest extends Activity {

    class RenderView extends View {
        Paint paint;
        Typeface font;
        Bitmap bob565;
        Bitmap bob4444;
        Rect dst = new Rect();
        Rect bounds = new Rect();

        public RenderView(Context context){
            super(context);
            paint = new Paint();
            font = Typeface.createFromAsset(context.getAssets(), "fonts/helvetica.ttf");
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open("images/logo.png");
                bob565 = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                Log.d("Bitmap text", "bob565 format: " + bob565.getConfig());

                inputStream = assetManager.open("images/logo.png");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Config.ARGB_4444;
                bob4444 = BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();
                Log.d("Bitmap Text", "bob4444 format: " + bob4444.getConfig());
            } catch (IOException e) {

            } finally {

            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            dst.set(50, 50, 350, 350);
            canvas.drawBitmap(bob565, null, dst, null);
            canvas.drawBitmap(bob4444, 100, 100, null);

            //text
            paint.setColor(Color.BLUE);
            paint.setTypeface(font);
            paint.setTextSize(50);
            paint.setTextAlign(Align.CENTER);
            canvas.drawText(
                    "Rutul Patel",
                    canvas.getWidth() / 2,
                    canvas.getHeight() / 2,
                    paint);

            String name = "Rutul Patel";
            paint.setColor(Color.RED);
            paint.setTextAlign(Align.LEFT);
            paint.getTextBounds(name, 0, name.length(), bounds);
            canvas.drawText(
                    name,
                    canvas.getWidth() - bounds.width(),
                    canvas.getHeight() / 2 + 200,
                    paint);
            invalidate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new RenderView(this));
    }
}
