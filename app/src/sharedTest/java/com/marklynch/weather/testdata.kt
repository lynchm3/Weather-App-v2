package com.marklynch.weather

var testLon = -122.08
var testLat = 37.42
var testLocationName = "Test Location"

fun generateGetWeatherResponse() = """{
   "coord":{
      "lon":$testLon,
      "lat":$testLat
   },
   "weather":[
      {
         "id":800,
         "main":"Clear",
         "description":"clear sky",
         "icon":"01d"
      }
   ],
   "base":"stations",
   "main":{
      "temp":298.24,
      "pressure":1017,
	  "humidity":47,
	  "tempMin":294.82,
	  "tempMax":301.48
   },
   "visibility":16093,
   "wind":{
	  "speed":2.1,
	  "deg":340
   },
   "clouds":{
      "all":1
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