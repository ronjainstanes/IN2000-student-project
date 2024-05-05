package no.uio.ifi.in2000.team11.havvarselapp.ui.networkConnection

import kotlinx.coroutines.flow.Flow

/**
 * Observes and responds when the app loses or gains internet access
 */
interface ConnectivityObserver {

    fun observe():Flow<Status>

    enum class Status{
        Available, Unavailable, Losing, Lost
    }
}