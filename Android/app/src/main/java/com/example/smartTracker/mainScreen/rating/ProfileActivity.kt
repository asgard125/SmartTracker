package com.example.smartTracker.mainScreen.rating

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.smartTracker.R
import com.example.smartTracker.data.Goal
import com.example.smartTracker.data.Habit
import com.example.smartTracker.data.User
import com.example.smartTracker.objects.C
import com.google.android.material.tabs.TabLayout

class ProfileActivity  : AppCompatActivity(){

    private lateinit var toolbar : Toolbar
    private lateinit var nameIdText : TextView
    private lateinit var ratingPlaceText : TextView
    private lateinit var ratingText : TextView
    private lateinit var pager : ViewPager
    private lateinit var tabLayout : TabLayout

    override fun onBackPressed() {
        finish()
    }

    private lateinit var filter : IntentFilter
    private var receiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            val habits = intent?.getParcelableArrayListExtra<Habit>(C.habits)!!
            val goals = intent.getParcelableArrayListExtra<Goal>(C.goals)!!
            pager.adapter = ProfilePagerAdapter(supportFragmentManager, habits, goals)
            pager.currentItem = 0
            tabLayout.setupWithViewPager(pager)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        filter = IntentFilter(C.ACTION_PROFILE_SERVICE)
        filter.addCategory(Intent.CATEGORY_DEFAULT)

        val user = intent.getParcelableExtra<User>(C.USER)!!

        toolbar = findViewById(R.id.ProfileToolbar)
        nameIdText = findViewById(R.id.ProfileNameId)
        ratingPlaceText = findViewById(R.id.ProfileRatingPlace)
        ratingText = findViewById(R.id.ProfileRatingCount)
        pager = findViewById(R.id.ProfileViewPager)
        tabLayout = findViewById(R.id.ProfileHabitTabLayout)

        toolbar.title = getString(R.string.profile)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val wordToSpan = SpannableString("${user.name}#${user.userId}")
        val start = user.name.length
        val end = wordToSpan.length
        wordToSpan.setSpan(ForegroundColorSpan(Color.GRAY), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        nameIdText.text = wordToSpan

        ratingPlaceText.text = getString(R.string.rating_place_pattern, user.ratingPlace)
        ratingText.text = getString(R.string.rating_pattern, user.rating)

        val intent = Intent(this, ProfileService::class.java)
        intent.putExtra(C.ID, user.userId)
        intent.putExtra(C.TASK_TYPE, C.GET_ALL_HABITS_TASK)
        startService(intent)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    private inner class ProfilePagerAdapter(fragmentManager : FragmentManager,
                                            var habits : ArrayList<Habit>, var goals : ArrayList<Goal>) : FragmentPagerAdapter(fragmentManager){

        val titles = arrayListOf(getString(R.string.habits), getString(R.string.goals))

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    ProfilePagerFragment.getNewHabitsInstance(ProfilePagerFragment.HABITS, habits)
                }
                1 -> {
                    ProfilePagerFragment.getNewGoalsInstance(ProfilePagerFragment.GOALS, goals)
                }
                else ->{
                    return Fragment()
                }
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

    }

}