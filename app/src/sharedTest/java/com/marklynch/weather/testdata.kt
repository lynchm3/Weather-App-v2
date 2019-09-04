package com.marklynch.weather

import kotlin.random.Random

var testLon = -122.08
var testLat = 37.42
var testLocationName = "Test Location"
var testDescription = "Test Description"
var testTemperature = Random.nextDouble()
var testHumidity = Random.nextDouble()
var testTemperatureMin = Random.nextDouble()
var testTemperatureMax = Random.nextDouble()
var testWindSpeed = Random.nextDouble()
var testWindDeg = Random.nextDouble()
var testCloudiness = Random.nextDouble()

fun generateGetWeatherResponse() = """{
   "coord":{
      "lon":$testLon,
      "lat":$testLat
   },
   "weather":[
      {
         "id":800,
         "main":"Clear",
         "description":"$testDescription",
         "icon":"01d"
      }
   ],
   "base":"stations",
   "main":{
      "temp":$testTemperature,
      "pressure":1017,
	  "humidity":$testHumidity,
	  "tempMin":$testTemperatureMin,
	  "tempMax":$testTemperatureMax
   },
   "visibility":16093,
   "wind":{
	  "speed":$testWindSpeed,
	  "deg":$testWindDeg
   },
   "clouds":{
      "all":$testCloudiness
   },
   "dt":1563212122,
   "sys":{
      "type":1,
      "id":5122,
      "message":0.0134,
      "country":"US",
      "sunrise":1563195547,
      "sunset":1563247741
   },
   "timezone":-25200,
   "id":5375480,
   "name":"$testLocationName",
   "cod":200
}"""