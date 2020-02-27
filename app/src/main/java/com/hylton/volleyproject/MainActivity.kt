package com.hylton.volleyproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var parseButton: Button
    private lateinit var requestQueue: RequestQueue

    var url: String = "https://api.myjson.com/bins/14yf50"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.name)
        parseButton = findViewById(R.id.parse_button)
        requestQueue = Volley.newRequestQueue(this)

        parseButton.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                returnArray(url)
                val mutableList =  returnArray(url)
                getResultFromApi(mutableList)
            }
        }

    }

    private suspend fun returnArray(url: String) : MutableList<String> {
        val arrayList = mutableListOf<String>()

        Log.d("Alele", "returnArray(url)")

        withContext(Dispatchers.IO){

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener { response ->
                    try {

                        Log.d("Alele", "Try block")
                        var jsonObject = response.getJSONObject("response").getJSONArray("docs").getJSONObject(0)

                        Log.d("Alele", jsonObject.toString())

                        var id = jsonObject.getString("id")
                        Log.d("Alele", id.toString())

                        var journal = jsonObject.getString("journal")
                        Log.d("Alele", journal)

                        arrayList.apply {
                            add(id)
                            add(journal)
                        }

                        Log.d("Alele", " id : ${arrayList[0]}")
                        Log.d("Alele", " journal : ${arrayList[1]}")

                    } catch (e: JSONException) {
                        Log.d("Alele", "catch block")
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    it.printStackTrace()
                    Log.d("Alele", it.printStackTrace().toString())
                })

            requestQueue.add(jsonObjectRequest)
        }

        Log.d("Alele", " id outside : ${arrayList[0]}")
        Log.d("Alele", " journal outside : ${arrayList[1]}")

        return arrayList
    }

    private suspend fun getResultFromApi(mutableList: MutableList<String>) {
        Log.d("Alele", "getResultFromApi()")

        var id = mutableList[0]
        var journal = mutableList[1]

        Log.d("Alele", " getResultFromApi() => id : $id")
        Log.d("Alele", " getResultFromApi() => journal : $journal")
    }

    private fun setNewText(input: String) {
        Log.d("Alele", "setNewText(input)")
        val newText = textView.text.toString() + "\n$input"
        textView.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) = withContext(Dispatchers.Main) {
        setNewText(input)
    }
}