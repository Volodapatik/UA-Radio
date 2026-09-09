package ua.radio.online.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ua.radio.online.data.TestStations
import ua.radio.online.player.PlayerController
import ua.radio.online.ui.components.MiniPlayer
import ua.radio.online.ui.screens.StationsScreen

@Composable
fun RadioApp() {
    val context = LocalContext.current
    val playerController = remember { PlayerController(context) }

    DisposableEffect(Unit) {
        playerController.connect()
        onDispose { playerController.release() }
    }

    val isPlaying by playerController.isPlaying.collectAsState()
    val currentStation by playerController.currentStation.collectAsState()

    Scaffold(
        bottomBar = {
            if (currentStation != null) {
                MiniPlayer(
                    station = currentStation!!,
                    isPlaying = isPlaying,
                    onPlayPause = { playerController.togglePlayPause() },
                    onStop = { playerController.stop() }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StationsScreen(
                stations = TestStations.list,
                currentStationId = currentStation?.id,
                isPlaying = isPlaying,
                onPlay = { station -> playerController.play(station) },
                onPause = { playerController.pause() }
            )
        }
    }
}
