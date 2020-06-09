package com.example.smartTracker.signScreen

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartTracker.R
import com.example.smartTracker.objects.C
import com.google.android.material.textfield.TextInputEditText

class SignInFragment : Fragment(){

    private lateinit var root : View
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var loginText : TextInputEditText
    private lateinit var passwordText : TextInputEditText

    private var dialog : AlertDialog? = null

    private var login : String = ""
    private var password : String = ""

    private lateinit var filter : IntentFilter
    private var receiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            val signInStatus = intent?.getStringExtra(C.SIGN_IN_STATUS)

            dialog?.dismiss()
            val message = intent?.getStringExtra(C.MESSAGE)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            if(signInStatus == C.STATUS_OK){
                (activity as SignFragmentCallback).signIn()
            }
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_sign_in, container, false)

        filter = IntentFilter(C.ACTION_SIGN_SERVICE)
        filter.addCategory(Intent.CATEGORY_DEFAULT)

        loginText = root.findViewById(R.id.LoginSignIn)
        passwordText = root.findViewById(R.id.PasswordSignIn)
        signInButton = root.findViewById(R.id.SignInButton)
        signUpButton = root.findViewById(R.id.NavigationSignUpButton)

        signInButton.setOnClickListener {
            login = loginText.text.toString()
            password = passwordText.text.toString()

            val user = User(login = login, password = password)
            val intent = Intent(context, SignService::class.java)

            intent.putExtra(C.TASK_TYPE, C.SIGN_IN_TASK)
            intent.putExtra(C.USER, user)

            dialog = AlertDialog.Builder(context).setView(R.layout.dialog_loading).setCancelable(false).create()
            dialog?.show()
            dialog?.findViewById<TextView>(R.id.LoadingTitle)?.text = getString(R.string.loading)

            activity?.startService(intent)
        }

        signUpButton.setOnClickListener { (activity as SignFragmentCallback).setSignUpFragment() }

        return root
    }

    override fun onStart() {
        super.onStart()
        activity?.registerReceiver(receiver, filter)
        if(login != "" && password != ""){
            loginText.setText(login)
            passwordText.setText(password)
            login = ""
            password = ""
        }
    }

    override fun onStop() {
        activity?.unregisterReceiver(receiver)
        dialog?.dismiss()
        super.onStop()
    }

    fun setLoginAndPassword(login : String, password: String){
        this.login = login
        this.password = password
    }

}

