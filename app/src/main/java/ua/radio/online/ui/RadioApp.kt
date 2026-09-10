package ua.radio.online.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ua.radio.online.data.RadioStation
import ua.radio.online.data.StationsRepository
import ua.radio.online.data.TestStations
import ua.radio.online.player.PlayerController
import ua.radio.online.ui.components.MiniPlayer
import ua.radio.online.ui.screens.StationsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioApp() {
    val context = LocalContext.current
    val playerController = remember { PlayerController(context) }
    val repository = remember { StationsRepository() }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    var stations by remember { mutableStateOf<List<RadioStation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }

    fun refresh(showSpinner: Boolean = false) {
        scope.launch {
            if (showSpinner) isRefreshing = true
            val list = repository.fetchStations()
            stations = list
            isLoading = false
            isRefreshing = false
        }
    }

    DisposableEffect(Unit) {
        playerController.connect()
        onDispose { playerController.release() }
    }

    // Перше завантаження
    LaunchedEffect(Unit) {
        refresh()
    }

    // Оновлення при поверненні в додаток
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Автооновлення кожні 45 секунд, поки додаток відкритий
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(45_000)
            refresh()
        }
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
            if (isLoading && stations.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { refresh(showSpinner = true) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    StationsScreen(
                        stations = stations.ifEmpty { TestStations.list },
                        currentStationId = currentStation?.id,
                        isPlaying = isPlaying,
                        onPlay = { station -> playerController.play(station) },
                        onPause = { playerController.pause() }
                    )
                }
            }
        }
    }
}
