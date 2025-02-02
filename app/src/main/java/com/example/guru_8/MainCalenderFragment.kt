package com.example.guru_8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.guru_8.databinding.FragmentMainCalenderBinding
import com.example.guru_8.fragment.ExpenseFragment

class MainCalenderFragment : Fragment() {

    private var _binding: FragmentMainCalenderBinding? = null
    private val binding get() = _binding!!

    private var spendingLimit: Int = 0
    private var currentSpending: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainCalenderBinding.inflate(inflater, container, false)

        // arguments에서 데이터 가져오기
        arguments?.let {
            spendingLimit = it.getInt("spendingLimit", 0)
            currentSpending = it.getInt("currentSpending", 0)
        }

        // ProgressBar 및 텍스트 업데이트
        updateProgressBar()

        // ✅ 달력에서 날짜 선택 시 `ExpenseFragment`로 이동
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            navigateToFragment(ExpenseFragment.newInstance(selectedDate)) // ✅ 추가된 부분
        }

        return binding.root
    }

    private fun updateProgressBar() {
        if (spendingLimit > 0) {
            val progress = (currentSpending.toDouble() / spendingLimit * 100).toInt()
            binding.spendingProgressBar.max = 100
            binding.spendingProgressBar.progress = progress
        }

        // 지출 상태 업데이트
        binding.currentSpendingTextView.text = getString(
            R.string.current_spending_status,
            currentSpending,
            spendingLimit
        )
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, fragment)  // ✅ fragment_container 안에 `ExpenseFragment` 표시
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }

    companion object {
        fun newInstance(spendingLimit: Int, currentSpending: Int) = MainCalenderFragment().apply {
            arguments = Bundle().apply {
                putInt("spendingLimit", spendingLimit)
                putInt("currentSpending", currentSpending)
            }
        }
    }
}