package edu.ap.herexamen_owen_heyrman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import edu.ap.herexamen_owen_heyrman.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up button click listeners
        binding.btnAddUser.setOnClickListener {
            navigateToFragment(AddUserFragment())
        }

        binding.btnAddExam.setOnClickListener {
            navigateToFragment(AddExamFragment())
        }

        binding.btnViewResultsByUser.setOnClickListener {
            navigateToFragment(ExamResultsFragment.newInstance("by_user"))
        }

        binding.btnViewResultsByExam.setOnClickListener {
            navigateToFragment(ExamResultsFragment.newInstance("by_exam"))
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }
}
