package com.nixbug.entebus.utils

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class NullOnEmptyConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {

        val gsonConverter: Converter<ResponseBody, Any> =
            retrofit.nextResponseBodyConverter(this, type, annotations)

        return object : Converter<ResponseBody, Any?> {
            override fun convert(body: ResponseBody): Any? {
                return if (body.contentLength() == 0L) {
                    null
                } else {
                    gsonConverter.convert(body)
                }
            }
        }
    }
}
