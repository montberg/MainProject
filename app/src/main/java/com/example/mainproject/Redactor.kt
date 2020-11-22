package com.example.mainproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
lateinit var listofcontainers: Array<Container>
class Redactor : AppCompatActivity() {
    lateinit var txtTipMusora : AutoCompleteTextView
    lateinit var txtObjemKonteinerov : EditText
    lateinit var txtAddress: AutoCompleteTextView
    lateinit var txtTipOsnovania : AutoCompleteTextView
    lateinit var txtPloshad : EditText
    lateinit var checkUvelichitPloshad : CheckBox
    lateinit var checkSReconstrukcjei : CheckBox
    lateinit var checkOgrazhdenie : CheckBox
    lateinit var txtMaterialOgrazhdenia : AutoCompleteTextView
    lateinit var btnAddContainer : Button
    lateinit var containerListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redactor)
        findAllElements()
        val platform = intent.getSerializableExtra("Platform") as Platform
        listofcontainers = platform.Containersarray!!
        txtAddress.setText(platform.Address)
        txtTipOsnovania.setText(platform.BaseType)
        txtPloshad.setText(platform.Square.toString())
        checkUvelichitPloshad.isChecked = platform.Boolisincreaseble
        checkSReconstrukcjei.isChecked = platform.Boolwithrec
        checkOgrazhdenie.isChecked = platform.Boolwithfence
        txtMaterialOgrazhdenia.setText(platform.Fencemat)
        val adapter = ContainerAdapter(this)
        containerListView.adapter = adapter

        if(checkUvelichitPloshad.isChecked) checkSReconstrukcjei.visibility = View.VISIBLE
        else checkSReconstrukcjei.visibility = View.GONE

        if(checkOgrazhdenie.isChecked) txtMaterialOgrazhdenia.visibility = View.VISIBLE
        else txtMaterialOgrazhdenia.visibility = View.GONE

        checkUvelichitPloshad.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) showHide(checkSReconstrukcjei)
            else showHide(checkSReconstrukcjei)
            checkSReconstrukcjei.isChecked = false
        }
        checkOgrazhdenie.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) showHide(txtMaterialOgrazhdenia)
            else showHide(txtMaterialOgrazhdenia)
            txtMaterialOgrazhdenia.text = null
        }
    }
    class ContainerAdapter(ctx: Context) : ArrayAdapter<Container>(ctx, android.R.layout.simple_list_item_2, listofcontainers){
        override fun getView(position: Int, ConvertView: View?, parent: ViewGroup): View {
            var convertView = ConvertView
            val container: Container? = getItem(position)
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, null)
            }
            (convertView!!.findViewById<View>(android.R.id.text1) as TextView).text = container!!.RubbishType
            (convertView.findViewById<View>(android.R.id.text2) as TextView).text = container.Volume.toString()
            return convertView
        }
    }
    private fun showHide(view: View) {
        if (view.visibility == View.VISIBLE){
            view.visibility = View.GONE
        } else{
            view.visibility = View.VISIBLE
        }
    }
    private fun findAllElements(){
        txtAddress = findViewById(R.id.chooseAddress)
        txtTipOsnovania = findViewById(R.id.autoTipOsnovania)
        txtPloshad = findViewById(R.id.txtPloshad)
        checkUvelichitPloshad = findViewById(R.id.checkUvelichitPloshad)
        checkSReconstrukcjei = findViewById(R.id.checkSReconstrukcjei)
        checkOgrazhdenie = findViewById(R.id.checkOgrazhdenie)
        txtMaterialOgrazhdenia = findViewById(R.id.autoOgrazhdenie)
        txtTipMusora = findViewById(R.id.autoTipMusora)
        txtObjemKonteinerov = findViewById(R.id.autoObjemKonteinerov)
        btnAddContainer = findViewById(R.id.addContainer)
        containerListView = findViewById(R.id.containerListView)
    }
}