package com.skuares.studio.quest;

import android.graphics.Bitmap;
import android.util.Base64;

import com.shaded.fasterxml.jackson.annotation.JsonProperty;

import java.io.ByteArrayOutputStream;

/**
 * Created by salim on 1/23/2016.
 */
public class QPicture {

    private String owner;
    private String stringPicture;


    public QPicture() {
    }



    // retrieve the picture data
    public QPicture(@JsonProperty("owner") String owner,
                    @JsonProperty("stringPicture") String stringPicture
                                                ){

        this.owner = owner;
        this.stringPicture = stringPicture;
    }


    public String getOwner() {
        return owner;
    }

    public String getStringPicture() {
        return stringPicture;
    }

    public static String bitmapToString(Bitmap bitmap){


        // output stream to write for when using compress
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        bitmap.recycle();
        // convert the output stream to byte array
        byte[] bytes = outputStream.toByteArray();
        // convert byte[] to string
        String stringImage = Base64.encodeToString(bytes, Base64.DEFAULT);

        return stringImage;
    }
}
