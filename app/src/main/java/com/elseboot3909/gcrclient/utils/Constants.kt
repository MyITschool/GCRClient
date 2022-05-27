package com.elseboot3909.gcrclient.utils

class Constants {
    companion object {
        const val LOG_TAG = "GCRClient"

        const val SERVERS_DATA = "servers_data"
        const val SELECTED_SERVER = "selected_server"

        const val NAV_DRAWER_TIMEOUT: Long = 250

        const val EMPTY_COMMAND = -1
        const val SEARCH_ACQUIRED = 0
        const val CHANGE_STATE_CHANGED = 1
        const val ACCOUNT_SWITCHED = 2

        const val SEARCH_STRING_KEY = "strSearch"
        const val SEARCH_PROJECTS_KEY = "projectsSearch"

        const val FILE_CHANGE_ID_KEY = "fileChangeID"
        const val FILE_PATCHSET_A_KEY = "filePatchSetA"
        const val FILE_PATCHSET_B_KEY = "filePatchSetB"
        const val FILE_NAME_KEY = "fileName"
    }
}
