package com.example.aspirepath.ui.jobs

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.aspirepath.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.RangeSlider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.Locale

class JobFiltersBottomSheet : BottomSheetDialogFragment() {

    private lateinit var etDegree: TextInputEditText
    private lateinit var etUniversity: TextInputEditText
    private lateinit var chipGroupSkills: ChipGroup
    private lateinit var tilAddSkill: TextInputLayout
    private lateinit var etAddSkill: TextInputEditText
    private lateinit var actvExperience: AutoCompleteTextView
    private lateinit var sliderSalary: RangeSlider
    private lateinit var tvSalaryRange: TextView
    private lateinit var etLocation: TextInputEditText
    private lateinit var btnApplyFilters: MaterialButton
    private lateinit var btnClose: ImageButton

    interface OnJobFilterListener {
        fun onFiltersApplied(keywords: String, location: String, minSalary: Float, maxSalary: Float)
    }

    private var listener: OnJobFilterListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnJobFilterListener) {
            listener = context
        } else {
            // Optional: throw exception if parent must implement listener
            // throw RuntimeException("$context must implement OnJobFilterListener")
        }
    }

    // Allow setting listener manually (e.g. from fragment)
    fun setFilterListener(listener: OnJobFilterListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_job_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind Views
        etDegree = view.findViewById(R.id.etDegree)
        etUniversity = view.findViewById(R.id.etUniversity)
        chipGroupSkills = view.findViewById(R.id.chipGroupSkills)
        tilAddSkill = view.findViewById(R.id.tilAddSkill)
        etAddSkill = view.findViewById(R.id.etAddSkill)
        actvExperience = view.findViewById(R.id.actvExperience)
        sliderSalary = view.findViewById(R.id.sliderSalary)
        tvSalaryRange = view.findViewById(R.id.tvSalaryRange)
        etLocation = view.findViewById(R.id.etLocation)
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters)
        btnClose = view.findViewById(R.id.btnClose)

        // Setup Experience Dropdown
        val experienceOptions = arrayOf("Fresher", "1-2 Years", "3-5 Years", "5+ Years")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, experienceOptions)
        actvExperience.setAdapter(adapter)

        // Initialize slider with default values before setting listener
        sliderSalary.values = listOf(0.0f, 200000.0f)
        
        // Load Saved Preferences
        loadPreferences()

        // Close Button Logic
        btnClose.setOnClickListener {
            dismiss()
        }

        // Add Skill Logic
        tilAddSkill.setEndIconOnClickListener {
            addSkill()
        }
        
        etAddSkill.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || 
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                addSkill()
                true
            } else {
                false
            }
        }

        // Slider Logic
        sliderSalary.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            val min = values[0]
            val max = values[1]
            tvSalaryRange.text = "${formatCurrency(min)} - ${formatCurrency(max)}"
        }

        // Apply Button Logic
        btnApplyFilters.setOnClickListener {
            applyFilters()
        }
    }

    private fun addSkill() {
        val skillText = etAddSkill.text.toString().trim()
        if (skillText.isNotEmpty()) {
            addChip(skillText)
            etAddSkill.text?.clear()
        }
    }

    private fun addChip(text: String) {
        val chip = Chip(requireContext())
        chip.text = text
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            chipGroupSkills.removeView(chip)
        }
        chipGroupSkills.addView(chip)
    }

    private fun formatCurrency(amount: Float): String {
        return if (amount >= 100000) {
            "₹%.1fL".format(amount / 100000)
        } else if (amount >= 1000) {
            "₹%.0fk".format(amount / 1000)
        } else {
            "₹%.0f".format(amount)
        }
    }

    private fun loadPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("JobPrefs", Context.MODE_PRIVATE)
        
        etDegree.setText(sharedPreferences.getString("degree", ""))
        etUniversity.setText(sharedPreferences.getString("university", ""))
        etLocation.setText(sharedPreferences.getString("location", ""))
        
        // Load Experience
        val savedExperience = sharedPreferences.getString("experience", "Fresher")
        actvExperience.setText(savedExperience, false) // false avoids showing dropdown immediately

        // Load Skills
        val skillsString = sharedPreferences.getString("skills", "")
        if (!skillsString.isNullOrEmpty()) {
            val skills = skillsString.split(",")
            for (skill in skills) {
                if (skill.isNotBlank()) {
                    addChip(skill)
                }
            }
        }
        
        // Load Salary
        val minSalary = sharedPreferences.getFloat("minSalary", 0f)
        val maxSalary = sharedPreferences.getFloat("maxSalary", 200000f)
        sliderSalary.training_SetValues(minSalary, maxSalary)
        
        // Update Salary Text
        tvSalaryRange.text = "${formatCurrency(minSalary)} - ${formatCurrency(maxSalary)}"
    }
    
    // Extension function specifically for Material RangeSlider to set values correctly
    // RangeSlider expects a list of floats
    private fun RangeSlider.training_SetValues(min: Float, max: Float) {
         // Ensure min < max and within bounds
         val validMin = min.coerceAtLeast(valueFrom).coerceAtMost(valueTo)
         val validMax = max.coerceAtLeast(valueFrom).coerceAtMost(valueTo).coerceAtLeast(validMin)
         values = listOf(validMin, validMax)
    }

    private fun applyFilters() {
        val degree = etDegree.text.toString().trim()
        val university = etUniversity.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val experience = actvExperience.text.toString()
        
        // Validate Location
        if (location.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a preferred city", Toast.LENGTH_SHORT).show()
            etLocation.error = "Required"
            return
        }

        // Collect Skills
        val skills = mutableListOf<String>()
        for (i in 0 until chipGroupSkills.childCount) {
             val chip = chipGroupSkills.getChildAt(i) as? Chip
             chip?.text?.toString()?.let { skills.add(it) }
        }

        // Collect Salary
        val salaryValues = sliderSalary.values
        val minSalary = salaryValues[0]
        val maxSalary = salaryValues[1]

        // Save to SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("JobPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("degree", degree)
        editor.putString("university", university)
        editor.putString("location", location)
        editor.putString("experience", experience)
        editor.putString("skills", skills.joinToString(","))
        editor.putFloat("minSalary", minSalary)
        editor.putFloat("maxSalary", maxSalary)
        editor.apply()

        // Combine keywords logic
        // "Degree Skills Experience"
        val keywordsBuilder = StringBuilder()
        if (degree.isNotEmpty()) keywordsBuilder.append(degree).append(" ")
        if (skills.isNotEmpty()) keywordsBuilder.append(skills.joinToString(" ")).append(" ")
        if (experience != "Fresher") keywordsBuilder.append(experience).append(" ") // Fresher might not be a good keyword for all search engines, but keep if user insists. 
        // Actually, experience is often a filter, but prompt asked to combine into keyword string.
        
        val keywords = keywordsBuilder.toString().trim()

        // Notify Listener
        listener?.onFiltersApplied(keywords, location, minSalary, maxSalary)
        
        Toast.makeText(requireContext(), "Filters Applied", Toast.LENGTH_SHORT).show()
        dismiss()
    }
}
