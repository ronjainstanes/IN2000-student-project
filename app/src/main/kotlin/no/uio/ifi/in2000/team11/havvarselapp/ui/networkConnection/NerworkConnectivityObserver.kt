package no.uio.ifi.in2000.team11.havvarselapp.ui.networkConnection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Implementation of a Connectivity Observer.
 * Observes and responds when the app loses or gains internet access
 */
class NetworkConnectivityObserver (context: Context): ConnectivityObserver {

    //Initialize ConnectivityManager
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observe(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {

            //Create a NetworkCallback
            val callback = object : ConnectivityManager.NetworkCallback(){

                //Calls when network becomes available
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(ConnectivityObserver.Status.Available) }
                }

                //Calls when network is losing
                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(ConnectivityObserver.Status.Losing) }
                }

                //Calls when network is lost
                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(ConnectivityObserver.Status.Lost) }
                }

                //Calls when network is unavailable
                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(ConnectivityObserver.Status.Unavailable) }
                }
            }

            //Register the NetworkCallback
            connectivityManager.registerDefaultNetworkCallback(callback)

            //Unregister the NetworkCallback when no longer needed
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged() //Only emit distinct network status updates
    }
}