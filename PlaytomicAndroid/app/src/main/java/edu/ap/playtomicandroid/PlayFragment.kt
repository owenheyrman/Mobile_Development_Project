package edu.ap.playtomicandroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button



class PlayFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_play, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assuming your button has the ID book_court_button
        val bookCourtButton = view.findViewById<Button>(R.id.book_court_button)
        //bookCourtButton.setOnClickListener {
        //    openBookCourtFragment()
        //}
    }

    private fun openBookCourtFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, BookCourtFragment())
        transaction.addToBackStack(null) // Optional, but useful for enabling navigation back to the previous fragment
        transaction.commit()
    }

}
