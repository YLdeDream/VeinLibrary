package com.shangzuo.veindemo

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class NetUtils {


    fun main() {
        val urlString = "https://example.com/api/data"
        val params = mapOf(
            "param1" to "value1",
            "param2" to "value2"
        )
        val response = sendGetRequest(urlString, params)
        Log.e("NetUtils", "main: ")
        println(response)
    }
    companion object{
        fun sendGetRequest(urlString: String, params: Map<String, String>): String {
            val urlBuilder = StringBuilder(urlString)
            if (params.isNotEmpty()) {
                urlBuilder.append("?")
                for ((key, value) in params) {
                    urlBuilder.append(URLEncoder.encode(key, "UTF-8"))
                    urlBuilder.append("=")
                    urlBuilder.append(URLEncoder.encode(value, "UTF-8"))
                    urlBuilder.append("&")
                }
                urlBuilder.deleteCharAt(urlBuilder.length - 1) // 移除末尾的 "&"
            }

            val url = URL(urlBuilder.toString())
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            Log.e("NetUtils", "Sending GET request to URL: $urlString")
            Log.e("NetUtils", "Response Code: $responseCode")

            val response = StringBuilder()
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                var inputLine: String?
                while (reader.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                reader.close()
            } else {
                Log.e("NetUtils", "GET request failed")
            }
            connection.disconnect()
            return response.toString()
        }

        fun sendPostRequest(urlString: String, jsonBody: String): String {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.doOutput = true

            try {
                val outputStream = connection.outputStream
                outputStream.write(jsonBody.toByteArray(Charsets.UTF_8))
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                Log.e("NetUtils", "Sending POST request to URL: $urlString")
                Log.e("NetUtils", "Response Code: $responseCode")

                val response = StringBuilder()
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    var inputLine: String?
                    while (reader.readLine().also { inputLine = it } != null) {
                        response.append(inputLine)
                    }
                    reader.close()
                } else {
                    Log.e("NetUtils", "POST request failed")
                }
                return response.toString()
            } finally {
                connection.disconnect()
            }
        }
    }

}