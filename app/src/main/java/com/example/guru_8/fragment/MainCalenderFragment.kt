package com.example.guru_8.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.guru_8.FragmentActivity
import com.example.guru_8.R
import com.example.guru_8.data.DataBaseHelper
import com.example.guru_8.databinding.FragmentMainCalenderBinding

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

        val activity = requireActivity() as? FragmentActivity
        val selectedDate = activity?.getSelectedDate() ?: ""

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val newDate = "$year-${month + 1}-$dayOfMonth"
            activity?.setSelectedDate(newDate) // ✅ 날짜 저장
            navigateToFragment(ExpenseFragment.newInstance(newDate))
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("SpendingPrefs", 0)
        spendingLimit = sharedPreferences.getInt("spendingLimit", 0) // ✅ 한도 가져오기

        val dbManager = DataBaseHelper(requireContext())
        currentSpending = dbManager.getTotalSpending() // ✅ 현재 총 지출 가져오기

        updateSpendingInfo() // ✅ UI 업데이트
    }

    private fun updateSpendingInfo() {
        binding.currentSpendingTextView.text = "현재 지출: ${currentSpending}원 | 한도: ${spendingLimit}원"

        if (spendingLimit > 0) {
            var progress = (currentSpending.toDouble() / spendingLimit * 100).toInt()

            // ✅ 한도를 초과하면 Progress Bar는 100%로 고정
            if (progress > 100) {
                progress = 100
                binding.spendingProgressBar.progressDrawable =
                    resources.getDrawable(R.drawable.progress_bar_over_limit, null) // 초과 시 색상 변경 가능
            } else {
                binding.spendingProgressBar.progressDrawable =
                    resources.getDrawable(R.drawable.progress_bar_style, null) // 기본 색상
            }

            binding.spendingProgressBar.max = 100
            binding.spendingProgressBar.progress = progress
        } else {
            binding.spendingProgressBar.progress = 0
        }
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
        fun newInstance(selectedDate: String?): MainCalenderFragment {
            val fragment = MainCalenderFragment()
            val args = Bundle()
            args.putString("selectedDate", selectedDate) // ✅ String 타입 전달
            fragment.arguments = args
            return fragment
        }
    }

}