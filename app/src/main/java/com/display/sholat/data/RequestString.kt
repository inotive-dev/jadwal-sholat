package com.display.sholat.data

import com.google.gson.Gson
import com.display.sholat.data.entity.Strings
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink

class RequestString(val data: Strings) : RequestBody() {
    override fun contentType(): MediaType {
        return "application/json".toMediaType()
    }

    override fun writeTo(sink: BufferedSink) {
        sink.write(Gson().toJson(data).toByteArray())
    }
}