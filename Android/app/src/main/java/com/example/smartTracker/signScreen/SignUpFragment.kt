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
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartTracker.R
import com.example.smartTracker.objects.C
import com.google.android.material.textfield.TextInputEditText

class SignUpFragment : Fragment(){

    private lateinit var root : View

    private lateinit var backButton : ImageButton
    private lateinit var nicknameText : TextInputEditText
    private lateinit var loginText : TextInputEditText
    private lateinit var passwordText : TextInputEditText
    private lateinit var passwordVerifyText : TextInputEditText
    private lateinit var signUpButton : Button

    private var dialog : AlertDialog? = null

    private lateinit var filter : IntentFilter
    private var receiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            val signUpStatus = intent?.getStringExtra(C.SIGN_UP_STATUS)

            dialog?.dismiss()
            val message = intent?.getStringExtra(C.MESSAGE)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            if(signUpStatus == C.STATUS_OK){
                val user = intent.getParcelableExtra<User>(C.USER)
                if(user != null) {
                    (activity as SignFragmentCallback).setSignInInfo(user.login, user.password)
                }

                activity?.supportFragmentManager?.popBackStack()
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_sign_up, container, false)

        filter = IntentFilter(C.ACTION_SIGN_SERVICE)
        filter.addCategory(Intent.CATEGORY_DEFAULT)

        backButton = root.findViewById(R.id.BackButtonSignUp)
        nicknameText = root.findViewById(R.id.NicknameSignUp)
        loginText = root.findViewById(R.id.LoginSignUp)
        passwordText = root.findViewById(R.id.PasswordSignUp)
        passwordVerifyText = root.findViewById(R.id.PasswordVerifySignUp)
        signUpButton = root.findViewById(R.id.SignUpButton)

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        signUpButton.setOnClickListener {
            val name = nicknameText.text.toString()
            val login = loginText.text.toString()
            val password = passwordText.text.toString()
            val password2 = passwordVerifyText.text.toString()
            if(password == password2){
                val user = User(name = name, login = login, password = password)
                val intent = Intent(context, SignService::class.java)

                intent.putExtra(C.TASK_TYPE, C.SIGN_UP_TASK)
                intent.putExtra(C.USER, user)

                dialog = AlertDialog.Builder(context).setView(R.layout.dialog_loading).setCancelable(false).create()
                dialog?.show()
                dialog?.findViewById<TextView>(R.id.LoadingTitle)?.text = getString(R.string.loading)

                activity?.startService(intent)
            }else{
                Toast.makeText(context, getString(R.string.verify_password_error), Toast.LENGTH_LONG).show()
            }
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        activity?.registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(receiver)
        dialog?.dismiss()
    }

}