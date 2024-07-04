package com.shorts.oscar.myapplication.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shorts.oscar.myapplication.api.PhotoService
import com.shorts.oscar.myapplication.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*ListViewModel является ViewModel, который связывает данные с пользовательским интерфейсом (View).
ViewModel сохраняет состояние данных между
конфигурациями активности или фрагмента и предоставляет методы для взаимодействия с данными.*/

class ListViewModel : ViewModel() {

    // Живые данные для списка фотографий
    private val _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> = _photos

    // Сервис для работы с API фотографий, инициализируем Retrofit
    private val photoService = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PhotoService::class.java)

    // Метод для загрузки списка фотографий
    fun loadPhotos() {
        viewModelScope.launch {
            try {
                // Выполнение запроса в фоновом потоке
                val photosList = withContext(Dispatchers.IO) {
                    photoService.getPhotos()
                }
                // Установка нового значения списка фотографий
                _photos.value = photosList
            } catch (e: Exception) {
                // Обработка ошибок, вывод в лог
                e.printStackTrace()
            }
        }
    }
}