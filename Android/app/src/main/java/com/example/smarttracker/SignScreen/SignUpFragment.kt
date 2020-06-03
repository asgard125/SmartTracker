package com.example.smarttracker.SignScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smarttracker.R
import com.google.android.material.textfield.TextInputEditText

class SignUpFragment : Fragment(){

    private lateinit var root : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_sign_up, container, false)


        val nicknameText = root.findViewById<TextInputEditText>(R.id.NicknameSignUp)
        val emailText = root.findViewById<TextInputEditText>(R.id.EmailSignUp)
        val passwordText = root.findViewById<TextInputEditText>(R.id.PasswordSignUp)
        val passwordVerifyText = root.findViewById<TextInputEditText>(R.id.PasswordVerifySignUp)

        val signUpButton = root.findViewById<Button>(R.id.SignUpButton)
        signUpButton.setOnClickListener {
            val nickname = nicknameText.text.toString()
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            val password2 = passwordVerifyText.text.toString()
            if(password == password2){
                SignUpAsyncTask(context, activity!!.supportFragmentManager, (activity as FragmentCallback),    nickname, email, password).execute()
            }else{
                Toast.makeText(context, getString(R.string.verify_password_error), Toast.LENGTH_LONG).show()
            }
        }

        val backButton = root.findViewById<ImageButton>(R.id.BackButtonSignUp)
        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return root
    }

}