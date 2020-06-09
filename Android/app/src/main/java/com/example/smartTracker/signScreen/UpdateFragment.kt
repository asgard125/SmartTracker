package com.example.smartTracker.signScreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.smartTracker.R
import com.example.smartTracker.objects.C

class UpdateFragment : Fragment(){

    private lateinit var root : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_update, container, false)

        val updateText : TextView = root.findViewById(R.id.UpdateText)
        val wordToSpan = SpannableString(getString(R.string.update_text))
        val clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(C.OUT_SITE))
                startActivity(browserIntent)
            }
        }
        wordToSpan.setSpan(clickableSpan, 50,78, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        updateText.text = wordToSpan
        updateText.movementMethod = LinkMovementMethod.getInstance()

        return root
    }

}