package com.example.tictactoe


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_start_menu.*


class StartMenu : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        startGame.setOnClickListener {
            this.findNavController().navigate(R.id.action_startMenu_to_game)
        }

        options.setOnClickListener {
            this.findNavController().navigate(R.id.action_startMenu_to_options)
        }
        super.onViewCreated(view, savedInstanceState)

        about.setOnClickListener {
            this.findNavController().navigate(R.id.action_startMenu_to_about)
        }
        super.onViewCreated(view, savedInstanceState)
    }

}
