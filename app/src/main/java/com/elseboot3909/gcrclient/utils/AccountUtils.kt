package com.elseboot3909.gcrclient.utils

import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.AccountInfo
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

        fun getShowedName(account: AccountInfo): String {
            return when {
                account.username.isNotEmpty() -> account.username
                account.name.isNotEmpty() -> account.name
                account.email.isNotEmpty() -> account.email
                else -> account._account_id.toString()
            }
        }

    }
}
