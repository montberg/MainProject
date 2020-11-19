package com.example.mainproject


import android.os.Bundle
import android.widget.*

import java.io.StringReader
import java.net.URL


class ListActivity : OptionsMenu() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)
    }
}