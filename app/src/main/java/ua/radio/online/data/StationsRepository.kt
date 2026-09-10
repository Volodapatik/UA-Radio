package ua.radio.online.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

class StationsRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun fetchStations(): List<RadioStation> = withContext(Dispatchers.IO) {
        try {
            val url = URL("${SupabaseConfig.URL}/rest/v1/stations?is_active=eq.true&order=sort_order.asc")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SupabaseConfig.ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
                setRequestProperty("Accept", "application/json"
                connectTimeout = 15000
                readTimeout = 15000
            }

            val code = connection.responseCode
            if (code !in 200..299) {
                connection.disconnect()
                return@withContext TestStations.list
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val stations = json.decodeFromString<List<RadioStation>>(body)
            if (stations.isEmpty()) TestStations.list else stations
        } catch (e: Exception) {
            e.printStackTrace()
            TestStations.list
        }
    }
}
