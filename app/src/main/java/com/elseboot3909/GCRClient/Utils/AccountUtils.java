package com.elseboot3909.GCRClient.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.elseboot3909.GCRClient.Entities.AvatarInfo;
import com.elseboot3909.GCRClient.R;
import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Random;

public class AccountUtils {

    static final Random random = new Random();

    public static final int[] dummyAvatars = new int[]{R.drawable.ic_dummy_avatar_1, R.drawable.ic_dummy_avatar_2, R.drawable.ic_dummy_avatar_3, R.drawable.ic_dummy_avatar_4, R.drawable.ic_dummy_avatar_5};

    public static int getRandomAvatar() {
        return dummyAvatars[random.nextInt(dummyAvatars.length)];
    }

    public static final String[] dummyUsernames = new String[]{"John", "Laurel", "Luke", "Jack", "Dexter", "Henry", "Dale", "Elbert"};

    public static String getRandomUsername() {
        return dummyUsernames[random.nextInt(dummyUsernames.length)];
    }

    public static void setAvatarDrawable(AvatarInfo avatarInfo, View view)  {
        Picasso picasso = Picasso.get();
        picasso.load(avatarInfo.getUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap);
                roundedBitmapDrawable.setCircular(true);
                if (view instanceof Chip) {
                    ((Chip) view).setChipIcon(roundedBitmapDrawable);
                } else if (view instanceof ImageView) {
                    ((ImageView) view).setImageDrawable(roundedBitmapDrawable);
                } else {
                    Log.e(Constants.LOG_TAG, "(" + this.getClass() + ") setAvatarDrawable: Check type of view object!");
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) { }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }

}
