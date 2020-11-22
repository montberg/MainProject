package com.example.mainproject


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import java.io.StringReader
import java.net.URL

lateinit var ArrayOfPlatforms:Array<Platform>
class ListActivity() : OptionsMenu(), DataBase {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)
        val platformlistview = findViewById<ListView>(R.id.platformList)
        val adapter = PlatformAdapter(this)
        platformlistview.adapter = adapter
        platformlistview.setOnItemClickListener { parent, view, position, id ->
            val a = adapter.getItem(position)!!
            val redactor = Intent(this, Redactor::class.java)
            redactor.putExtra("Platform", a)
            startActivity(redactor)
        }
    }
}
private class PlatformAdapter(ctx: Context) : ArrayAdapter<Platform>(ctx, android.R.layout.simple_list_item_2, ArrayOfPlatforms){
    override fun getView(position: Int, ConvertView: View?, parent: ViewGroup): View {
        var convertView: View? = ConvertView
        val platform: Platform? = getItem(position)
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, null)
        }
        if (platform != null) {
            (convertView!!.findViewById<View>(android.R.id.text1) as TextView).text = platform.Address
        }
        if (convertView != null) {
            if (platform != null) {
                (convertView.findViewById<View>(android.R.id.text2) as TextView).text = "${platform.Containersarray?.size.toString()} контейнера|ов"
            }
        }
        return convertView!!
    }
}