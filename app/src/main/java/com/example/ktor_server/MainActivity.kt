package com.example.ktor_server

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ktor_server.ui.theme.KtorserverTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KtorserverTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    Button(
        content = {
            Text(
                text = if (Server.instance.serverState.value.equals(Server.ServerState.STARTED)) "Отключить" else if (Server.instance.serverState.value.equals(
                        Server.ServerState.STOPPED
                    )
                ) "Поднять" else "Отключение..."
            )
        },
        onClick = {
            if (Server.instance.serverState.value.equals(Server.ServerState.STARTED)) {
                Server.instance.stopServer()
            } else if (Server.instance.serverState.value.equals(Server.ServerState.STOPPED)) {
                Server.instance.startServer(4321)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KtorserverTheme {
        Greeting()
    }
}