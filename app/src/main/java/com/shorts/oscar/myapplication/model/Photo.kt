package com.shorts.oscar.myapplication.model

//Класс Photo представляет собой простой data class
//который используется для хранения информации о фотографиях:

data class Photo(
    val id: Int,             // Уникальный идентификатор фотографии
    val title: String,       // Заголовок фотографии
    val thumbnailUrl: String // URL миниатюры фотографии
)