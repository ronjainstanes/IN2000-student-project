/*
package no.uio.ifi.in2000.team11.havvarselapp.ui.harbors

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team11.havvarselapp.R
import no.uio.ifi.in2000.team11.havvarselapp.model.harbor.Harbor
import org.json.JSONArray

class HarborViewModel:  ViewModel() {
    val harborData = MutableLiveData<List<Harbor>>()

    */
/**
     * Metode for å hente data fra selvskrevt JSON fil som ligger under "res" i "raw" mappen.
     *//*

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchHarborData(context: Context) {
        GlobalScope.launch {
            val harborList = mutableListOf<Harbor>()
            val inputStream = context.resources.openRawResource(R.raw.guestharbors)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            val jsonString = String(buffer)
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                harborList.add(
                    Harbor(
                        item.getInt("id"),
                        item.getString("name"),
                        item.getJSONArray("location").let { arrayOf(it.getDouble(0), it.getDouble(1)) },
                        item.getString("description")
                    )
                )
            }

            harborData.postValue(harborList)
        }
    }
}*/
