package edu.ap.playtomicandroid

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class ProfileFragment : Fragment() {

    private lateinit var textViewName: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewName = view.findViewById(R.id.textViewName)
        val buttonEditProfile = view.findViewById<Button>(R.id.buttonEditProfile)

        buttonEditProfile.setOnClickListener {
            showEditUsernameDialog()
        }
    }

    private fun showEditUsernameDialog() {
        // Example: Using an AlertDialog to get user input
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Username")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val newUsername = input.text.toString()
            updateUsername(newUsername)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun updateUsername(username: String) {
        textViewName.text = username
        // Here you can also save the username to a database or shared preferences
    }
}

