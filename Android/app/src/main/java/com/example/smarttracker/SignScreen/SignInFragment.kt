package com.example.smarttracker.SignScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.smarttracker.R
import com.google.android.material.textfield.TextInputEditText

class SignInFragment : Fragment(){

    private lateinit var root : View
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var emailText : TextInputEditText
    private lateinit var passwordText : TextInputEditText
    private var email : String = ""
    private var password : String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_sign_in, container, false)

        getApiKey()?.let{
            (activity as FragmentCallback).signIn()
        }

        emailText = root.findViewById(R.id.EmailSignIn)
        passwordText = root.findViewById(R.id.PasswordSignIn)
        signInButton = root.findViewById(R.id.SignInButton)
        signUpButton = root.findViewById(R.id.NavigationSignUpButton)

        signInButton.setOnClickListener {
            email = emailText.text.toString()
            password = passwordText.text.toString()
            SignInAsyncTask(context, (activity as FragmentCallback)).execute(email, password)
        }

        signUpButton.setOnClickListener { (activity as FragmentCallback).setSignUpFragment() }

        return root
    }

    override fun onStart() {
        super.onStart()
        if(email != "" && password != ""){
            emailText.setText(email)
            passwordText.setText(password)
            email = ""
            password = ""
        }
    }

    private fun getApiKey() : String?{
        return null
    }

    fun setEmailAndPassword(email : String, password: String){
        this.email = email
        this.password = password
    }

}

