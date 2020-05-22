package com.example.smarttracker.SignScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.smarttracker.R

class SignInFragment : Fragment(){

    private lateinit var root : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_sign_in, container, false)
        var signInButton = root.findViewById<Button>(R.id.SignInButton)
        signInButton.setOnClickListener { (activity as FragmentCallback).signIn() }
        var signUpButton = root.findViewById<Button>(R.id.SignUpButton)
        signUpButton.setOnClickListener { (activity as FragmentCallback).setSignUpFragment()}
        return root
    }

}