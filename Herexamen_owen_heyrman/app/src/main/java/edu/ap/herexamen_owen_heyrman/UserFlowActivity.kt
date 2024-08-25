// UserFlowActivity.kt
package edu.ap.herexamen_owen_heyrman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.ap.herexamen_owen_heyrman.databinding.ActivityUserFlowBinding


class SharedViewModel : ViewModel() {
    val selectedExam = MutableLiveData<Exam>()
    val selectedUser = MutableLiveData<User>()
}
class UserFlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserFlowBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initially display the ExamListFragment
        if (savedInstanceState == null) {
            navigateToExamListFragment()
        }
    }

    public fun navigateToExamListFragment() {
        replaceFragment(ExamListFragment())
    }

    public fun navigateToUserListFragment() {
        val fragment = UserListFragment()
        replaceFragment(fragment)
    }
    public fun navigateToExamDetailsFragment(){
        val fragment = ExamDetailsFragment()
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)  // Add to back stack to allow navigation back
            .commit()
    }
}
