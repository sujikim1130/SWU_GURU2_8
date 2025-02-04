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

/**
 * 메인 캘린더 화면을 표시하는 프래그먼트
 */
class MainCalenderFragment : Fragment() {

    private var _binding: FragmentMainCalenderBinding? = null // View Binding 객체
    private val binding get() = _binding!!

    private var spendingLimit: Int = 0 // 설정된 지출 한도
    private var currentSpending: Int = 0 // 현재까지 지출한 금액

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainCalenderBinding.inflate(inflater, container, false)

        val activity = requireActivity() as? FragmentActivity
        val selectedDate = activity?.getSelectedDate() ?: ""

        // 캘린더에서 날짜를 선택하면 해당 날짜의 지출 화면으로 이동
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val newDate = "$year-${month + 1}-$dayOfMonth"
            activity?.setSelectedDate(newDate)
            navigateToFragment(ExpenseFragment.newInstance(newDate))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("SpendingPrefs", 0)
        spendingLimit = sharedPreferences.getInt("spendingLimit", 0) // 한도 불러오기

        val dbManager = DataBaseHelper(requireContext())
        currentSpending = dbManager.getTotalSpending() // 현재 총 지출 가져오기

        updateSpendingInfo()
    }

    /**
     * 현재 지출과 한도를 UI에 업데이트하는 함수
     */
    private fun updateSpendingInfo() {
        binding.currentSpendingTextView.text = "현재 지출: ${currentSpending}원 | 한도: ${spendingLimit}원"

        if (spendingLimit > 0) {
            var progress = (currentSpending.toDouble() / spendingLimit * 100).toInt()

            if (progress > 100) {
                progress = 100
                binding.spendingProgressBar.progressDrawable =
                    resources.getDrawable(R.drawable.progress_bar_over_limit, null) // 초과 시 색상 변경
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

    /**
     * 프래그먼트를 전환하는 함수
     */
    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, fragment)  // ExpenseFragment로 이동
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }

    companion object {
        /**
         * 새로운 MainCalenderFragment 인스턴스를 반환하는 함수
         */
        fun newInstance(selectedDate: String?): MainCalenderFragment {
            val fragment = MainCalenderFragment()
            val args = Bundle()
            args.putString("selectedDate", selectedDate)
            fragment.arguments = args
            return fragment
        }
    }
}