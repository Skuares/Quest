package com.skuares.studio.quest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by salim on 12/22/2015.
 */
public class LoadImageFromString {

    private Context context;

    Bitmap placeholder;

    public LoadImageFromString(Context context) {
        this.context = context;
        placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder);
    }

    public void loadBitmapFromString(String path, ImageView imageView) {
        if (cancelPotentialWork(path, imageView)) {
            final StringWorkerTask task = new StringWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(placeholder, task);

            imageView.setImageDrawable(asyncDrawable);


            /*
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                 reqW = imageView.getHeight();
                 reqH = imageView.getWidth();
            }
            */
            task.execute(path);



        }
    }

    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final StringWorkerTask stringWorkerTask = getStringWorkerTask(imageView);

        if (stringWorkerTask != null) {
            final String bitmapData = stringWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || bitmapData != data) {
                // Cancel previous task
                stringWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    class StringWorkerTask extends AsyncTask<Object, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;
        private String data = null;

        public StringWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Object... params) {
            data = (String)params[0];

            return decodeBitmapFromString(data);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (isCancelled()) {
                bitmap = null;
            }
            

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final StringWorkerTask bitmapWorkerTask =
                        getStringWorkerTask(imageView);
                if (imageView != null && this == bitmapWorkerTask ) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<StringWorkerTask> stringWorkerTaskReference;

        public AsyncDrawable(Bitmap bitmap,// placeholder
                             StringWorkerTask stringWorkerTask) {
            super(bitmap);
            stringWorkerTaskReference =
                    new WeakReference<StringWorkerTask>(stringWorkerTask);
        }

        public StringWorkerTask getStringWorkerTask() {
            return stringWorkerTaskReference.get();
        }
    }


    private static StringWorkerTask getStringWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getStringWorkerTask();
            }
        }
        return null;
    }

    public static Bitmap decodeBitmapFromString(String stringImage){


        Bitmap bitmap = null;

        // decode the string
        try {
            byte[] bytes = Base64.decode(stringImage, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        }catch (Exception e){

            Log.e("ErrorImage", e.getMessage());
            return null;
        }
    }
}
