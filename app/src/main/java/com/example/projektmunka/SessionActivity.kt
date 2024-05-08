package com.example.projektmunka

import android.os.Bundle
import android.widget.FrameLayout
import com.example.projektmunka.databinding.ActivitySessionBinding
import com.example.projektmunka.fragment.DataFragment
import com.example.projektmunka.fragment.FormFragment.CircularDiffFormFragment
import com.example.projektmunka.fragment.FormFragment.DiffFormFragment
import com.example.projektmunka.fragment.FormFragment.TimeDiffFormFragment
import com.example.projektmunka.fragment.FormFragment.CaloriesDiffFormFragment
import com.example.projektmunka.fragment.MapFragment
import com.example.projektmunka.fragment.ReportFragement
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionActivity : BaseActivity() {

    private lateinit var binding: ActivitySessionBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_session)

        // Initialize BottomSheet
        val bottomSheet: FrameLayout = findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        // Get the selected form type from the intent extras
        val selectedFormType = intent.getIntExtra("selected_form_type", 1)

        setFormContent(selectedFormType)

        // Display MapFragment by default
        supportFragmentManager.beginTransaction()
            .replace(com.google.android.material.R.id.container, MapFragment())
            .commit()
    }

    fun showDataFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.bottom_sheet, DataFragment())
            .commit()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun showReportFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ReportFragement())
            .addToBackStack(null)
            .commit()
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_session
    }

    private fun setFormContent(formType: Int) {
       val selectedFragment = when (formType) {
           0 -> CircularDiffFormFragment()
           1 -> DiffFormFragment()
           2 -> TimeDiffFormFragment()
           3 -> CaloriesDiffFormFragment()
           else -> CircularDiffFormFragment()
       }
        supportFragmentManager.beginTransaction()
            .replace(R.id.bottom_sheet, selectedFragment)
            .commit()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun startSession() {

    }
}