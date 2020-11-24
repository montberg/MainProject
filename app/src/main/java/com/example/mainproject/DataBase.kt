package com.example.mainproject

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.lang.reflect.Executable
import java.nio.charset.Charset
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*
import kotlin.coroutines.coroutineContext

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
    fun insertDataToTable(platform:Platform) {
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        var lastID = 0
        val statement: Statement = connection.createStatement()
        var strSQL = "INSERT INTO platform(lat, lng, address, basetype, square, boolisinc, boolwithrec, boolwithfence, fencemat, containeramount) " +
                "VALUES(${platform.Lat}, ${platform.Lng}, '${platform.Address}', '${platform.BaseType}', ${platform.Square}, ${platform.Boolisincreaseble}," +
                " ${platform.Boolwithrec}, ${platform.Boolwithfence}, '${platform.Fencemat}', ${platform.Containersarray?.size}) returning id;"
            val dataBaseResponse: ResultSet = statement.executeQuery(strSQL)
            while(dataBaseResponse.next()) {
                lastID = dataBaseResponse.getInt(1)
            }
        if(platform.Containersarray!!.isNotEmpty()) {
            platform.Containersarray.forEach { c ->
                strSQL = "insert into container(rubbishtype, volume, parent_id) values('${c.RubbishType}', ${c.Volume}, ${lastID});"
                statement.execute(strSQL)
            }
        }
            connection.close()
    }
    fun deletePlatform(id:Int){
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val statement: Statement = connection.createStatement()
        val deleteQuery = "delete from platform where id = $id"
        statement.execute(deleteQuery)
        connection.close()
    }
    fun insertDataToTable(platform:Platform, id:Int) {
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val lastID:Int = id
        val statement: Statement = connection.createStatement()
        var strSQL = "UPDATE OR INSERT INTO platform(id, lat, lng, address, basetype, square, boolisinc, boolwithrec, boolwithfence, fencemat, containeramount) " +
                "VALUES(${lastID}, ${platform.Lat}, ${platform.Lng}, '${platform.Address}', '${platform.BaseType}', ${platform.Square}, ${platform.Boolisincreaseble}," +
                " ${platform.Boolwithrec}, ${platform.Boolwithfence}, '${platform.Fencemat}', ${platform.Containersarray?.size}) MATCHING(id);"
        statement.execute(strSQL)
        strSQL = "delete from container where parent_id = $lastID;"
        statement.execute(strSQL)
        platform.Containersarray?.forEach { c ->
            strSQL = "insert into container(rubbishtype, volume, parent_id) values('${c.RubbishType}', ${c.Volume}, ${lastID});"
            statement.execute(strSQL)
        }
        connection.close()
    }
    fun getPlatform(): Array<Platform> {
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val statement: Statement = connection.createStatement()
        val getPlatforms = "select*from platform"
        val response:ResultSet = statement.executeQuery(getPlatforms)
        val PlatformArray:MutableList<Platform> = arrayListOf()
        while(response.next()){
            val id = response.getInt(1)
            val Lat = response.getFloat(2)
            val Lng =  response.getFloat(3)
            val Address = response.getString(4)
            val BaseType = response.getString(5)
            val Square = response.getString(6)
            val Boolisincreaseble = response.getBoolean(7)
            val Boolwithrec = response.getBoolean(8)
            val Boolwithfence = response.getBoolean(9)
            val Fencemat = response.getString(10)

            val getContainersQuery = "select * from container where parent_id = '${id}'"
            val connection2: Connection = DriverManager.getConnection(URL, getConnectionProperties())
            val statement2: Statement = connection2.createStatement()
            val containersListResponse = statement2.executeQuery(getContainersQuery)
            val tempContainerList:MutableList<Container> = arrayListOf()
            while(containersListResponse.next()) {
                val rubbishtype = containersListResponse.getString(2)
                val volume = containersListResponse.getString(3)
                val container = Container(rubbishtype, volume.toDouble())
                tempContainerList.add(container)
            }
            connection2.close()
            val tempPlatform = Platform(id,Lat.toDouble(), Lng.toDouble(), Address.toString(), BaseType.toString(), Square.toDouble(), Boolisincreaseble, Boolwithrec, Boolwithfence, Fencemat.toString(), tempContainerList.toTypedArray())
            PlatformArray.add(tempPlatform)
        }
        connection.close()
        return PlatformArray.toTypedArray()
    }
}