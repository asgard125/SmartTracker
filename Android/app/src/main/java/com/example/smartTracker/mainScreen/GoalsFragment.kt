package com.example.smartTracker.mainScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartTracker.R

class GoalsFragment : Fragment(){
    
    private lateinit var root : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.screen_in_progress, container, false)
        return root
    }
    
}