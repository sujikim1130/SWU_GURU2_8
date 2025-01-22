package com.example.mycalendar2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.mycalendar2.databinding.CalendarBinding

class Calendar_frag : Fragment() {

    private var _binding: CalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarBinding.inflate(inflater, container, false)
        val view = binding.root

        initRecyclerView()
        return view
    }

    private fun initRecyclerView() {
        // RecyclerView 초기화
        val recyclerView = binding.calRecycler
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = MonthAdapter()

        // 스냅 동작 추가
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        // 중앙에서 시작
        val position = Int.MAX_VALUE / 2
        recyclerView.scrollToPosition(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}