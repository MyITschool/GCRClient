package com.elseboot3909.gcrclient.credentials

import androidx.datastore.core.Serializer
import com.elseboot3909.gcrclient.ServersList
import java.io.InputStream
import java.io.OutputStream

object ServersListSerializer : Serializer<ServersList> {
    override val defaultValue: ServersList = ServersList.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): ServersList = ServersList.parseFrom(input)
    override suspend fun writeTo(t: ServersList, output: OutputStream) = t.writeTo(output)
}