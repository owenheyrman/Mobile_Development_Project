package edu.ap.herexamen_owen_heyrman

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val btnViewResultsByUser = findViewById<Button>(R.id.btnViewResultsByUser)
        val btnViewResultsByExam = findViewById<Button>(R.id.btnViewResultsByExam)

        btnViewResultsByUser.setOnClickListener {
            navigateToFragment(ExamResultsFragment.newInstance("by_user"))
        }

        btnViewResultsByExam.setOnClickListener {
            navigateToFragment(ExamResultsFragment.newInstance("by_exam"))
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
