package com.example.mainproject


import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.*
import kotlin.collections.ArrayList


var ArrayOfPlatforms:MutableList<PlatformInfo> = mutableListOf()
class ListActivity : OptionsMenu(), DataBase, SwipeRefreshLayout.OnRefreshListener {

    lateinit var adapter:PlatformAdapter
    lateinit var platformlistview:RecyclerView
    lateinit var txtFind:EditText
    lateinit var mSwipeRefresh:SwipeRefreshLayout
    lateinit var filteredList:MutableList<PlatformInfo>
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)
        ArrayOfPlatforms = getPlatform()
        txtFind = findViewById(R.id.txtFind)
        platformlistview = findViewById(R.id.platformList)
        adapter = PlatformAdapter(ArrayOfPlatforms)
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        platformlistview.layoutManager = layoutManager
        platformlistview.itemAnimator = DefaultItemAnimator()
        platformlistview.adapter = adapter

        mSwipeRefresh =  findViewById(R.id.swipe)
        mSwipeRefresh.setOnRefreshListener(this)

        txtFind.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString()!=""){
                    filteredList = ArrayList()
                    for(item in ArrayOfPlatforms) {
                        if(item.Address.toLowerCase(Locale.ROOT).replace(",", "").replace(" улица", "").contains(s.toString().toLowerCase(Locale.ROOT).replace(",", "")))
                            filteredList.add(item)
                        setupRecyclerView(filteredList)
                    }
                }else {
                    setupRecyclerView(ArrayOfPlatforms)
                }
            }
            private fun setupRecyclerView(list: MutableList<PlatformInfo>) {
                adapter = PlatformAdapter(list)
                //setting up layout manager to recyclerView
                platformlistview.layoutManager = LinearLayoutManager(applicationContext)
                platformlistview.setHasFixedSize(true)

                //setting adapter to recyclerView
                platformlistview.adapter = adapter
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
    override fun onResume() {
        ArrayOfPlatforms = getPlatform()
        adapter.notifyDataSetChanged()
        super.onResume()
    }

    override fun onRefresh() {
        Handler().postDelayed({ //Останавливаем обновление:
            val arrayOfPlatforms = getPlatform()
            ArrayOfPlatforms = arrayOfPlatforms
            val Adapter = PlatformAdapter(arrayOfPlatforms)
            platformlistview.adapter = Adapter
            Adapter.notifyDataSetChanged()
            mSwipeRefresh.isRefreshing = false
        }, 1000)
    }
}