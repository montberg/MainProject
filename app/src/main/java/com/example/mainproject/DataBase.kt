package com.example.mainproject

import android.util.Log
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

var URL = "jdbc:firebirdsql://192.168.0.105:3050/e:/data/database.fdb" //URL до бд в виде jdbc:firebirdsql://<IP-адрес либо сайт>:<порт>/<полный путь до БД>

interface DataBase {
    fun getConnectionProperties():Properties{
        Class.forName("org.firebirdsql.jdbc.FBDriver")
        val props = Properties()
        props.setProperty("user", "SYSDBA")
        props.setProperty("password", "masterkey")
        props.setProperty("encoding", "WIN1251")
        return props
    }
    fun checkUser(login: String, password: String): Boolean {
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val query = "select * from users where login='$login'"
        val statement: Statement = connection.createStatement()
        val dataBaseResponse: ResultSet = statement.executeQuery(query)
        while(dataBaseResponse.next()) {
            try {
                if (dataBaseResponse.getString("login") == login && dataBaseResponse.getString("password")  == password) return true
            } catch (e: Exception) {
                return false
            }
        }
        connection.close()
        return false
    }
    fun getListValues(query:String):Array<String?>{
        val values:MutableList<String> = mutableListOf()
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val statement: Statement = connection.createStatement()
        //val query = "select type from basetype;"
        val dataBaseResponse: ResultSet = statement.executeQuery(query)
        while(dataBaseResponse.next()){
            val temp = dataBaseResponse.getBytes("type")
            values.add(String(temp, Charset.forName("CP866")))
        }
        return values.toTypedArray()
    }
}