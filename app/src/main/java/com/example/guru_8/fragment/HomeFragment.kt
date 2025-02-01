package com.example.guru_8.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.guru_8.R


class HomeFragment : Fragment() {

    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = it.getString(ARG_DATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 선택된 날짜를 표시
        val dateTextView: TextView = view.findViewById(R.id.selectedDateTextView)
        dateTextView.text = "선택된 날짜: $selectedDate"

        return view
    }

    companion object {
        private const val ARG_DATE = "selected_date"

        fun newInstance(date: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_DATE, date)
            fragment.arguments = args
            return fragment
        }
    }
}