package com

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Main(
    val temp: Double
)

fun Application.configureRouting() {
    routing {
        get("/weather") {
            val city: String = "Helsinki"
            val weatherData = fetchWeather(city)

            if (weatherData != null) {
                call.respondText("Weather in $city: ${weatherData}")
            } else {
                call.respondText("Failed to fetch weather data")
            }
        }
    }
}

suspend fun fetchWeather(city: String): String? {
    val dotenv = Dotenv.load()
    val apiKey = dotenv["API_KEY"] ?: return null

    val client = HttpClient(CIO)

    return try{
        val response: HttpResponse = client.get("http://api.openweathermap.org/data/2.5/weather") {
            parameter("q", city)
            parameter("appid", apiKey)
            parameter("units", "metric")
        }
        response.bodyAsText()
    } catch(e: Exception) {
        println("Error fetching weather")
        null
    } finally {
        client.close()
    }
}