package com.example.mainproject

import java.io.Serializable

class Platform (lat:Double,
               lng:Double,
               address:String,
               baseType:String,
               square:Double,
               boolIsIncreaseble:Boolean = false,
               boolWithRec:Boolean = false,
               boolWithFence:Boolean = false,
               fenceMat:String? = null,
               containersArray:Array<Container>? = arrayOf()
): Serializable {
    val Lat = lat
    val Lng = lng
    val Address = address
    val BaseType = baseType
    val Square = square
    val Boolisincreaseble = boolIsIncreaseble
    val Boolwithrec = boolWithRec
    val Boolwithfence = boolWithFence
    val Fencemat = fenceMat
    val Containersarray = containersArray
}