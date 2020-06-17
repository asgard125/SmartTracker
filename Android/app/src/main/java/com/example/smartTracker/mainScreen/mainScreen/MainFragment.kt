package com.example.smartTracker.mainScreen.mainScreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartTracker.R
import com.example.smartTracker.data.Habit
import com.example.smartTracker.data.User
import com.example.smartTracker.mainScreen.habits.HabitsService
import com.example.smartTracker.mainScreen.rating.HabitsAdapter
import com.example.smartTracker.mainScreen.rating.ProfileActivity
import com.example.smartTracker.objects.C

class MainFragment : Fragment() {

    private lateinit var root : View
    private lateinit var helloUserText : TextView
    private lateinit var leaderboardTitle : TextView
    private lateinit var leaderboardSpinner : Spinner
    private lateinit var leaderboardRecycler : RecyclerView

    private var currentUserId : Long = -1

    private lateinit var filter : IntentFilter
    private var receiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            val sortType = intent?.getIntExtra(C.sortType, -1)
            if(sortType == MainScreenService.BEST_HABITS || sortType == MainScreenService.NEWEST_HABITS){
                val habits = intent.getParcelableArrayListExtra<Habit>(C.habits)
                val users = ArrayList<User>()
                for(i in 0 until habits.size){
                    users.add(User())
                }
                habitsAdapter = HabitsLeaderboardAdapter(users, habits,  context)
                leaderboardRecycler.adapter = habitsAdapter
            }
        }

    }

    private lateinit var habitsAdapter : HabitsLeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filter = IntentFilter(C.ACTION_MAIN_LEADERBOARD)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_main, container, false)

        helloUserText = root.findViewById(R.id.HelloUserText)
        leaderboardTitle = root.findViewById(R.id.MainLeaderboardTitle)
        leaderboardSpinner = root.findViewById(R.id.MainLeaderboardSpinner)
        leaderboardRecycler = root.findViewById(R.id.MainLeaderboardRecycler)


        val preferences = context?.getSharedPreferences(C.MAIN_PREFERENCES, Context.MODE_PRIVATE)
        val userName = preferences?.getString(C.NAME, getString(R.string.user))
        currentUserId = preferences?.getLong(C.ID, -1)!!
        helloUserText.text = getString(R.string.hello_user_pattern, userName)

        leaderboardSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                startLeaderboardService(position)
                leaderboardTitle.text = getString(when(position){
                    MainScreenService.BEST_HABITS -> R.string.best_habits
                    MainScreenService.NEWEST_HABITS -> R.string.newest_habits
                    else -> -1
                })
            }

        }

        leaderboardRecycler.layoutManager = LinearLayoutManager(context)

        return root
    }

    override fun onStart() {
        super.onStart()
        activity?.registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(receiver)
    }

    private fun startLeaderboardService(sortType : Int){
        val intent = Intent(context, MainScreenService::class.java)
        intent.putExtra(C.sortType, sortType)
        intent.putExtra(C.TASK_TYPE, C.GET_LEADERBOARD_TASK)
        activity?.startService(intent)
    }

    private inner class HabitsLeaderboardAdapter(val users : ArrayList<User>, habits : ArrayList<Habit>, context : Context?) : HabitsAdapter(habits, context){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitHolder {
            return HabitsLeaderboardHolder(LayoutInflater.from(context).inflate(R.layout.item_profile_habit, parent, false))
        }

        private inner class HabitsLeaderboardHolder(itemView : View): HabitsAdapter.HabitHolder(itemView){

            override fun bindUserButton() {
                userButton.visibility = View.GONE
                userButton.setOnClickListener{
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra(C.USER, users[adapterPosition])
                    startActivity(intent)
                    activity?.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                }
            }

            override fun canVote(habit : Habit): Boolean {
                return if(currentUserId == habit.userId){
                    Toast.makeText(context, getString(R.string.vote_for_your_habit_error), Toast.LENGTH_LONG).show()
                    false
                }else{
                    true
                }
            }

        }

    }

}