package com.shorts.oscar.myapplication.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.shorts.oscar.myapplication.R
import com.shorts.oscar.myapplication.model.Photo

//Это основной адаптер для RecyclerView, который управляет отображением элементов списка.
//В конструкторе принимается список объектов Photo, который будет отображаться в RecyclerView.

class PhotoAdapter(var photos: List<Photo>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        // Создаем новый элемент списка, используя макет one_item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.one_item, parent, false)
        return PhotoViewHolder(view) // Возвращаем новый экземпляр PhotoViewHolder
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        // Получаем текущий объект Photo из списка по позиции
        val photo = photos[position]
        // Вызываем метод bind для установки данных в PhotoViewHolder
        holder.bind(photo)
    }

    override fun getItemCount(): Int {
        // Возвращаем общее количество элементов в списке
        return photos.size
    }

    // Внутренний класс PhotoViewHolder, расширяющий RecyclerView.ViewHolder
    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Инициализация View-компонентов из макета элемента списка
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val textViewId: TextView = itemView.findViewById(R.id.textViewId)
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)

        // Метод bind, который привязывает данные объекта Photo к View-компонентам
        @SuppressLint("CheckResult")
        fun bind(photo: Photo) {
            textViewId.text = photo.id.toString() // Установка ID в textViewId
            textViewTitle.text = photo.title // Установка заголовка в textViewTitle
            // Настройка опций загрузки изображения с помощью библиотеки Glide
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.progress_animation) // Установка placeholder-а
            Glide.with(itemView) // Инициализация Glide для текущего контекста itemView
                .setDefaultRequestOptions(requestOptions) // Установка опций запроса
                .load(photo.thumbnailUrl) // Загрузка изображения по URL-у
                .override(400, 400) // Установка размера изображения
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Стратегия кэширования
                .into(imageView) // Установка изображения в imageView
        }
    }
}