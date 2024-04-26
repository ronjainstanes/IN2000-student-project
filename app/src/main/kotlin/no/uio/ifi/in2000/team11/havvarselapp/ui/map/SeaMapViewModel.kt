package no.uio.ifi.in2000.team11.havvarselapp.ui.map

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.model.harbor.Harbor
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.Executors

data class MapUiState(
    /**
     * if there is a location-marker visible on the map
     */
    val markerVisible: Boolean = false
)

/**
 * The view model that handles UI logic related to the map.
 */
class SeaMapViewModel(application: Application) : AndroidViewModel(application) {
    // Using a single source of truth pattern for the map's UI state
    private val _mapUiState = MutableLiveData(MapUiState()).apply {
        observeForever { /* No need to do anything here as we are not observing this live data */ }
    }

    // Exposing an immutable version of the map's UI state through a read-only property
    val mapUiState: LiveData<MapUiState> get() = _mapUiState


        @SuppressLint("StaticFieldLeak")
        private val context = getApplication<Application>().applicationContext

    // Data for guest harbour markers on the map
    val harborData = MutableLiveData<List<Harbor>>().also {
        Executors.newSingleThreadExecutor().submit {
            suspend { loadGuestHarboursFromResources(context) }
        }
    }

    /**
     * Toggles whether a marker should be shown or hidden based on the current state.
     */
    fun toggleMarkerVisibility() {
        _mapUiState.value?.let { currentState ->
            _mapUiState.value = currentState.copy(markerVisible = !currentState.markerVisible)
        } ?: Unit
    }

    /**
     * Loads guest harbour data from resources into memory. This method runs on a background thread.
     */
    suspend fun loadGuestHarboursFromResources(context: Context): List<Harbor> = withContext(
        Dispatchers.IO) {
        val inputStream = context.resources.openRawResource(R.raw.guestharbors).use { stream ->
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.readBytes()
            buffer
        }

        parseJsonToHarbors(inputStream.decodeToString())
    }

    companion object {
        /**
         * Parses JSON string to list of [Harbor] objects.
         */
        private fun parseJsonToHarbors(jsonString: String): List<Harbor> {
            return try {
                val jsonArray = JSONArray(jsonString)
                List(jsonArray.length()) { i ->
                    jsonArray.getJSONObject(i)?.toHarbor()
                }.filterNotNull()
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}

private fun JSONObject.toHarbor(): Harbor? {
    return try {
        Harbor(
            id = getInt("id"),
            name = getString("name"),
            location = getJSONArray("location").let { arr ->
                doubleArrayOf(arr.getDouble(0), arr.getDouble(1)).takeIf { it[0] != 0.0 || it[1] != 0.0 }
            },
            description = getString("description")
        ).also {
            require(!it.location?.isNullOrEmpty()!!) { "Location must have nonzero coordinates" }
        }
    } catch (_: NullPointerException) {
        null
    }
}

fun DoubleArray.isNullOrEmpty(): Boolean = all { it == 0.0 }
