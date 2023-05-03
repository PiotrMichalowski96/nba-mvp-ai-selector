package pl.piter.commons.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

object JsonConverter {

    val objectMapper: ObjectMapper = jacksonObjectMapper()

    init {
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun readFileAsString(filePath: String) = File(filePath)
        .inputStream()
        .readBytes()
        .toString(Charsets.UTF_8)

    inline fun <reified T> readJsonFile(filePath: String): T {
        val json: String = readFileAsString(filePath)
        return objectMapper.readValue(json, T::class.java)
    }

}