package com.shorts.oscar.myapplication.api

import com.shorts.oscar.myapplication.data.Photo
import retrofit2.http.GET

/*Интерфейс PhotoService для работы с удалённым API,
используя библиотеку Retrofit для запросов HTTP.
метод getPhotos, который использует аннотацию
@GET для выполнения GET-запроса к эндпоинту "photos".*/

interface PhotoService {
    @GET("photos")
    suspend fun getPhotos(): List<Photo>
}