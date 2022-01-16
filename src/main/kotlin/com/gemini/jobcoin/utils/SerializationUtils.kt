package com.gemini.jobcoin.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.lang.reflect.Type

/**
 * Utility functions for serializing and deserializing objects
 */
object SerializationUtils {

    val mapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    /**
     * Takes JSON string [value] and returns a deserialized map with keys of type [T] and values of type [F]
     */
    fun <T, F> readMapFromString(value: String): Map<T, F> {
        return mapper.readValue(value)
    }

    /**
     * Takes JsonNode [json] and returns a deserialized map with keys of type [T] and values of type [F]
     */
    fun <T, F> readMapFromJsonObject(json: JsonNode): Map<T, F> {
        return mapper.convertValue(json)
    }

    /**
     * Takes JSON string [value] and returns an generic JsonNode
     */
    fun readTreeFromString(value: String): JsonNode {
        return mapper.readTree(value)
    }

    /**
     * Takes JSON string [value] and returns an object of type [T].
     */
    fun <T> readValueFromString(value: String, type: Class<T>): T {
        return mapper.readValue(value, type)
    }

    /**
     * Takes JsonElement [value] and returns an object of type [T].
     */
    fun <T> readValueFromJsonNode(value: JsonNode, type: Class<T>): T {
        return mapper.treeToValue(value, type)
    }

    /**
     * Takes JsonElement [value] and returns an object of type represented by [Type].
     */
    fun <T> readValueFromJsonNode(value: JsonNode, typeRef: TypeReference<T>): T {
        return mapper.convertValue(value, typeRef)
    }

    /**
     * Takes any [value] of type [T] and returns a json [String].
     */
    fun <T> writeValueToString(value: T): String {
        return mapper.writeValueAsString(value)
    }

    /**
     * Takes any [value] of type [T] and returns a [JsonNode].
     */
    fun <T> writeValueToJsonNode(value: T): JsonNode {
        return mapper.valueToTree(value)
    }
}
