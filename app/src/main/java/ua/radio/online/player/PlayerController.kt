package ua.radio.online.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ua.radio.online.data.RadioStation

class PlayerController(private val context: Context) {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentStation = MutableStateFlow<RadioStation?>(null)
    val currentStation: StateFlow<RadioStation?> = _currentStation.asStateFlow()

    fun connect() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, RadioPlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            controller = controllerFuture?.get()
            controller?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }
            })
        }, MoreExecutors.directExecutor())
    }

    fun play(station: RadioStation) {
        val mediaItem = RadioPlaybackService.createMediaItem(
            stationId = station.id,
            name = station.name,
            streamUrl = station.streamUrl
        )
        controller?.setMediaItem(mediaItem)
        controller?.prepare()
        controller?.play()
        _currentStation.value = station
    }

    fun pause() {
        controller?.pause()
    }

    fun resume() {
        controller?.play()
    }

    fun stop() {
        controller?.stop()
        _currentStation.value = null
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else resume()
    }

    fun release() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controller = null
    }
}
