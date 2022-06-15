package com.elseboot3909.gcrclient.utils

class Constants {
    companion object {
        const val LOG_TAG = "GCRClient"

        const val SECURITY_KEY = "gcrclient"

        const val NAV_DRAWER_TIMEOUT: Long = 250

        const val DEFAULT_ANIMATION_TIME = 250

        const val SEARCH_ACQUIRED = 1
        const val CHANGE_STATE_CHANGED = 2
        const val ACCOUNT_SWITCHED = 3

        const val MAX_FETCHED_CHANGES = 25

        const val FILE_CHANGE_ID_KEY = "fileChangeID"
        const val FILE_PATCHSET_A_KEY = "filePatchSetA"
        const val FILE_PATCHSET_B_KEY = "filePatchSetB"
        const val FILE_NAME_KEY = "fileName"
    }
}
