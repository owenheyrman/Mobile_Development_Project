package edu.ap.herexamen_owen_heyrman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class UserListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val examId = intent.getStringExtra("EXAM_ID") ?: ""
        val examTitle = intent.getStringExtra("EXAM_TITLE") ?: ""

        if (savedInstanceState == null) {
            val fragment = UserListFragment().apply {
                arguments = Bundle().apply {
                    putString("EXAM_ID", examId)
                    putString("EXAM_TITLE", examTitle)
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
