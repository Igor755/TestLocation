package com.shorts.oscar.myapplication.presentation.ui.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shorts.oscar.myapplication.databinding.FragmentListBinding
import com.shorts.oscar.myapplication.presentation.adapter.PhotoAdapter

// Класс экрана списка фотографий
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private lateinit var adapter: PhotoAdapter
    private val binding get() = _binding!!

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val listViewModel = ViewModelProvider(this)[ListViewModel::class.java]
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PhotoAdapter(emptyList()) // Пустой список изначально
        binding.recyclerView.adapter = adapter
        listViewModel.photos.observe(viewLifecycleOwner) { photos ->
            adapter.photos = photos // Обновляем данные в адаптере
            adapter.notifyDataSetChanged() // Сообщаем адаптеру, что данные изменились
        }
        listViewModel.loadPhotos()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}