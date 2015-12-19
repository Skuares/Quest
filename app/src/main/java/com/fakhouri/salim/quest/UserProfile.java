package com.fakhouri.salim.quest;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by salim on 12/18/2015.
 */
public class UserProfile extends AppCompatActivity {

    private static int REQUEST_CODE= 1;

    public static boolean imageHasChanged = false;

    ImageView header;


    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    int mutedColor = R.attr.colorPrimary;

    private User mUser;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile);



        toolbar = (Toolbar)findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // ENSURE USER IS NOT NULL LATERZ
        mUser = MainActivity.myUser;
        collapsingToolbarLayout.setTitle(mUser.getUsername());

        header = (ImageView) findViewById(R.id.header);

        // get user image
        //String strImage = mUser.getUserImage();
        //Bitmap bitmap = mUser.stringToBitmap(strImage);

        if(MainActivity.userImageStatic != null){
            header.setImageBitmap(MainActivity.userImageStatic);
            // set up palette
            Palette.from(MainActivity.userImageStatic).generate(new Palette.PaletteAsyncListener() {
                @SuppressWarnings("ResourceType")
                @Override
                public void onGenerated(Palette palette) {

                    mutedColor = palette.getMutedColor(R.color.primary_500);
                    collapsingToolbarLayout.setContentScrimColor(mutedColor);
                    collapsingToolbarLayout.setStatusBarScrimColor(R.color.black_trans80);
                }
            });
        }else{
            String strImage = mUser.getUserImage();
            Bitmap bitmap = mUser.stringToBitmap(strImage);
            header.setImageBitmap(bitmap);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @SuppressWarnings("ResourceType")
                @Override
                public void onGenerated(Palette palette) {

                    mutedColor = palette.getMutedColor(R.color.primary_500);
                    collapsingToolbarLayout.setContentScrimColor(mutedColor);
                    collapsingToolbarLayout.setStatusBarScrimColor(R.color.black_trans80);
                }
            });

        }

        recyclerView = (RecyclerView)findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true); // only one view

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new UserAdapter(MainActivity.myUser);
        recyclerView.setAdapter(adapter);


        // set up the floating action button
        floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingPicture);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change picture
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                UserProfile.this.startActivityForResult(Intent.createChooser(intent, "Complete Action Using"), REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        adapter = new UserAdapter(MainActivity.myUser);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_edit:
                Intent edit = new Intent(UserProfile.this,EditProfile.class);
                startActivity(edit);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

                if (Build.VERSION.SDK_INT >= 19) {

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Uri selectedImage = data.getData();


                    InputStream input;
                    Bitmap bitmap;
                    String picturePath = getPath(UserProfile.this, selectedImage);
                    try {
                        input = UserProfile.this.getContentResolver().openInputStream(
                                selectedImage);
                        bitmap = BitmapFactory.decodeStream(input);
                        header.setImageBitmap(bitmap);

                        // save to firebase
                        saveImageToFirebase(picturePath);


                    } catch (FileNotFoundException e1) {
                        Toast.makeText(UserProfile.this, e1.getMessage(), Toast.LENGTH_LONG).show();

                    }
                } else {

                    DoTheJobInBackgroud hello = new DoTheJobInBackgroud();
                    hello.execute(data);
                }


            }


        } catch (Exception e) {
            Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void saveImageToFirebase(String path){

        if(path != null && mUser != null){
            // convert to bitmap
            Bitmap bitmap = BitmapFactory
                    .decodeFile(path);

            // call user method to setUserImage
            mUser.setUserImage(bitmap,MainActivity.userRef);

            // let main activity know that image has changed
            imageHasChanged = true;

        }else{

            Toast.makeText(UserProfile.this,"Image Could not be saved",Toast.LENGTH_LONG).show();
        }


    }

    private class DoTheJobInBackgroud extends AsyncTask<Intent, Void, String> {

        Intent data;

        @Override
        protected String doInBackground(Intent... params) {
            // get the image from data
            data = params[0];
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};


            //Log.d("hello", "Android Version is 18 below");
            Cursor cursor = UserProfile.this.getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String picturePath = cursor.getString(columnIndex);

                cursor.close();

                return picturePath;
            }

            return null;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(s);
                header.setImageBitmap(bitmap);
                saveImageToFirebase(s);


            } else {
                Toast.makeText(UserProfile.this, "path is wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }



    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && com.fakhouri.salim.quest.DocumentsContract.isDocumentUri(uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = com.fakhouri.salim.quest.DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = com.fakhouri.salim.quest.DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = com.fakhouri.salim.quest.DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
