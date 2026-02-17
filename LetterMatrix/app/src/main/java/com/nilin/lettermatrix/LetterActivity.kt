package com.nilin.lettermatrix

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LetterActivity : ComponentActivity() {

    private lateinit var sessionId: String
    private val apiService = ApiService.create()
    private var webSocketClient: WebSocketClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionId = intent.getStringExtra("SESSION_ID") ?: run {
            Toast.makeText(this, "Error: нет session ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            var roundData by remember { mutableStateOf<RoundData?>(null) }
            var currentMatrix by remember { mutableStateOf<List<String>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            var isSessionCompleted by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                loadRound { data, error ->
                    if (data != null) {
                        roundData = data
                        currentMatrix = data.mobile_matrix
                        connectWebSocket(
                            onRoundUpdated = { newMatrix ->
                                currentMatrix = newMatrix
                            },
                            onCompleted = {
                                isSessionCompleted = true
                            }
                        )
                    } else {
                        errorMessage = error
                        isLoading = false
                    }
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    webSocketClient?.disconnect()
                }
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when {
                        roundData != null -> GameScreen(
                            currentMatrix = currentMatrix,
                            onFinish = {
                                lifecycleScope.launch {
                                    try {
                                        apiService.stopSession(sessionId)
                                    } catch (e: Exception) {
                                        Log.e("LetterActivity", "Error stopping session", e)
                                    } finally {
                                        finish()
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    private fun connectWebSocket(
        onRoundUpdated: (List<String>) -> Unit,
        onCompleted: () -> Unit
    ) {
        webSocketClient = WebSocketClient(
            sessionId = sessionId,
            onRoundUpdated = onRoundUpdated,
            onSessionCompleted = onCompleted
        )
        webSocketClient?.connect()
    }

    private fun loadRound(onResult: (RoundData?, String?) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = apiService.getRound(sessionId)
                if (response.isSuccessful) {
                    onResult(response.body(), null)
                } else {
                    onResult(null, "Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(null, "Network error: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketClient?.disconnect()
    }
}

@Composable
fun GameScreen(
    currentMatrix: List<String>,
    onFinish: () -> Unit,
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            // Clean up when leaving the screen
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.wrapContentHeight()
        ) {
            items(currentMatrix) { letter ->
                LetterCard(letter = letter)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select letters on a the computer screen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton (
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stop session")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LetterCard(letter: String) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(2.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}