package com.nilin.lettermatrix

import android.util.Log
import okhttp3.*
import org.json.JSONObject

class WebSocketClient(
    private val sessionId: String,
    private val onRoundUpdated: (List<String>) -> Unit,
    private val onSessionCompleted: () -> Unit
) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()

    fun connect() {
        val url = "ws://192.168.1.137:8000/ws/session/$sessionId/"

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connected to session $sessionId")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received: $text")
                handleMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closing: $reason")
            }
        })
    }

    private fun handleMessage(message: String) {
        try {
            val json = JSONObject(message)
            val type = json.getString("type")

            when (type) {
                "connected" -> {
                    Log.d("WebSocket", "Successfully connected")
                }
                "round_updated" -> {
                    val mobileMatrix = json.getJSONArray("mobile_matrix")
                    val letters = mutableListOf<String>()
                    for (i in 0 until mobileMatrix.length()) {
                        letters.add(mobileMatrix.getString(i))
                    }
                    onRoundUpdated(letters)
                }
                "session_completed" -> {
                    onSessionCompleted()
                }
            }
        } catch (e: Exception) {
            Log.e("WebSocket", "Error parsing message: ${e.message}")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnecting")
    }
}