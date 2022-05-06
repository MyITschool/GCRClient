package com.elseboot3909.GCRClient.Utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.elseboot3909.GCRClient.Entities.AvatarInfo
import com.elseboot3909.GCRClient.R
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.util.*

class AccountUtils {

    companion object {

        private val random = Random()

        private val dummyAvatars = arrayOf(R.drawable.ic_dummy_avatar_1, R.drawable.ic_dummy_avatar_2, R.drawable.ic_dummy_avatar_3, R.drawable.ic_dummy_avatar_4, R.drawable.ic_dummy_avatar_5)

        fun getRandomAvatar() : Int {
            return dummyAvatars[random.nextInt(dummyAvatars.size)]
        }

        private val dummyUsernames = arrayOf("John", "Laurel", "Luke", "Jack", "Dexter", "Henry", "Dale", "Elbert")

        fun getRandomUsername() : String {
            return dummyUsernames[random.nextInt(dummyUsernames.size)]
        }

        fun setAvatarDrawable(avatarInfo: AvatarInfo , view: View)
        {
            val picasso = Picasso.get()
            picasso.load(avatarInfo.url).into(object: Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap)
                    roundedBitmapDrawable.isCircular = true
                    if (view is Chip) {
                        view.chipIcon = roundedBitmapDrawable
                    } else if (view is ImageView) {
                        view.setImageDrawable(roundedBitmapDrawable)
                    }
                }

                override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) { }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) { }

            })
        }
    }
}
