package com.elseboot3909.gcrclient.utils

import com.elseboot3909.gcrclient.R
import java.util.*

class AccountUtils {

    companion object {

        private val random = Random()

        val dummyAvatars = arrayOf(
            R.drawable.ic_dummy_avatar_1,
            R.drawable.ic_dummy_avatar_2,
            R.drawable.ic_dummy_avatar_3,
            R.drawable.ic_dummy_avatar_4,
            R.drawable.ic_dummy_avatar_5
        )

        fun getRandomAvatar(): Int {
            return dummyAvatars[random.nextInt(dummyAvatars.size)]
        }

    }
}
