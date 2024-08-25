package edu.ap.herexamen_owen_heyrman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class ExamDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_details)

        val examId = intent.getStringExtra("EXAM_ID") ?: ""
        val userId = intent.getStringExtra("USER_ID") ?: ""
        val userName = intent.getStringExtra("USER_NAME") ?: ""

        if (savedInstanceState == null) {
            val fragment = ExamDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("EXAM_ID", examId)
                    putString("USER_ID", userId)
                    putString("USER_NAME", userName)
                }
            }
            replaceFragment(fragment)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
