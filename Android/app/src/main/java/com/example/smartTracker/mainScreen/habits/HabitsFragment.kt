package com.example.smartTracker.mainScreen.habits

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.smartTracker.data.Habit
import com.example.smartTracker.R
import com.example.smartTracker.objects.C
import com.example.smartTracker.objects.Database
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList

const val MAX_HABITS = 10

class HabitsFragment : Fragment(){

    private lateinit var root : View
    private lateinit var recycler : RecyclerView
    private lateinit var fab : FloatingActionButton
    private lateinit var refreshLayout : SwipeRefreshLayout

    private lateinit var adapter : HabitsAdapter

    private lateinit var notificationManager: HabitsNotificationManager

    private lateinit var filter : IntentFilter
    private var receiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                C.ACTION_HABITS_SERVICE ->{
                    var habits = intent.extras?.getParcelableArrayList<Habit>(C.habits)
                    if(habits == null){
                        habits = ArrayList()
                    }
                    if(!this@HabitsFragment::adapter.isInitialized){
                        adapter = HabitsAdapter(habits)
                        recycler.adapter = adapter
                    }else{
                        adapter.habits = habits
                        adapter.notifyDataSetChanged()
                    }
                    fab.isEnabled = true
                }
                C.ACTION_NEW_DAY_UPDATE_UI ->{
                    val requestHabitsIntent = Intent(context, HabitsService::class.java)
                    activity?.startService(requestHabitsIntent.putExtra(C.TASK_TYPE, C.GET_ALL_HABITS_TASK))
                }
                C.ACTION_HABIT_UNDO ->{
                    Toast.makeText(context, getString(R.string.edit_habit_error), Toast.LENGTH_LONG).show()
                    val oldHabit = intent.getParcelableExtra<Habit>(C.OLD_HABIT)!!
                    val updatedPosition = intent.getIntExtra(C.UPDATED_POSITION, -1)
                    adapter.habits[updatedPosition] = oldHabit
                    adapter.notifyItemChanged(updatedPosition)
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filter = IntentFilter(C.ACTION_HABITS_SERVICE)
        filter.addAction(C.ACTION_NEW_DAY_UPDATE_UI)
        filter.addAction(C.ACTION_HABIT_UNDO)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.single_fab_recycler, container, false)

        notificationManager = HabitsNotificationManager(context)

        recycler = root.findViewById(R.id.SingleFabRecycler)
        fab = root.findViewById(R.id.SingleFabRecyclerButton)
        refreshLayout = root.findViewById(R.id.HabitsRefreshLayout)

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val addNewHabitIntent = Intent(context, HabitsService::class.java)

        fab.isEnabled = false
        fab.setOnClickListener{
            if(adapter.habits.size <= MAX_HABITS){
                val newHabit = Database.HabitsModel.addDefaultHabitAndReturn()
                adapter.habits.add(newHabit)
                adapter.notifyItemInserted(adapter.habits.size)
                activity?.startService(addNewHabitIntent.putExtra(C.TASK_TYPE, C.ADD_DEFAULT_HABIT_TASK).putExtra(C.habit, newHabit))
            }else{
                Toast.makeText(context, getString(R.string.max_habits_error), Toast.LENGTH_SHORT).show()
            }
        }

        val requestHabitsIntent = Intent(context, HabitsService::class.java)

        refreshLayout.setOnRefreshListener {
            activity?.startService(requestHabitsIntent.putExtra(C.TASK_TYPE, C.GET_ALL_HABITS_TASK))
            fab.isEnabled = false
            refreshLayout.isRefreshing = false
        }

        activity?.startService(requestHabitsIntent.putExtra(C.TASK_TYPE, C.GET_ALL_HABITS_TASK))

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

    private inner class HabitsAdapter(var habits : ArrayList<Habit>) : RecyclerView.Adapter<HabitsAdapter.HabitsHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitsHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false)
            return HabitsHolder(view)
        }

        override fun onBindViewHolder(holder: HabitsHolder, position: Int) {
            val habit = habits[holder.adapterPosition]

            holder.title.text = habit.name

            val calendar = Calendar.getInstance()
            var weekday = calendar.get(Calendar.DAY_OF_WEEK)
            weekday = when(weekday){
                1 -> 6
                2 -> 0
                3 -> 1
                4 -> 2
                5 -> 3
                6 -> 4
                7 -> 5
                else -> -1
            }

            holder.isDoneCheckbox.isEnabled = !habit.isDone && habit.weekdays.contains(weekday)

            holder.isDoneCheckbox.isChecked = habit.isDone

            holder.isDoneCheckbox.setOnCheckedChangeListener { checkbox, isChecked ->
                if(isChecked){
                    Toast.makeText(context, getString(R.string.habit_completed), Toast.LENGTH_SHORT).show()
                    checkbox.isEnabled = false
                    notificationManager.deleteTodayAlarm(habit)
                    val completedIntent = Intent(context, HabitsService::class.java)
                    activity?.startService(completedIntent.putExtra(C.TASK_TYPE, C.COMPLETE_HABIT_TASK).putExtra(C.id, habit.id))
                }
                habit.isDone = isChecked
            }


            val type = if(habit.isPublic) getString(R.string.public_habit) else getString(R.string.private_habit)
            holder.info.text = getString(R.string.habit_info_pattern, habit.reputation, type)

            holder.itemView.setOnLongClickListener {
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.delete_habit_question))
                    .setPositiveButton(getString(R.string.delete)) { dialog, which ->
                        habits.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                        val intent = Intent(context, HabitsService::class.java)
                        activity?.startService(intent.putExtra(C.TASK_TYPE, C.DELETE_HABIT_TASK).putExtra(C.id, habit.id))
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
                true
            }

            holder.itemView.setOnClickListener{
                //start activity for result with habit's id parameter
                val intent = Intent(context, HabitActivity::class.java)
                val bundle = bundleOf("Habit" to habit, "Position" to holder.adapterPosition)
                intent.putExtras(bundle)
                startActivityForResult(intent, 1)
                activity?.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }

        }

        override fun getItemCount(): Int {
            return habits.size
        }

        inner class HabitsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val title = itemView.findViewById<TextView>(R.id.HabitItemName)
            val info = itemView.findViewById<TextView>(R.id.HabitItemInfo)
            val isDoneCheckbox = itemView.findViewById<CheckBox>(R.id.HabitItemCheckbox)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1 && data != null && resultCode == Activity.RESULT_OK){
            val needToUpdate = data.getBooleanExtra("needToUpdate", false)
            val updatedPosition = data.getIntExtra("Position", -1)
            if(updatedPosition != -1 && !needToUpdate) {
                val habit = data.getParcelableExtra<Habit>("Habit")
                adapter.habits[updatedPosition] = habit
                adapter.notifyItemChanged(updatedPosition)
            }
            if(needToUpdate){
                val requestHabitsIntent = Intent(context, HabitsService::class.java)
                activity?.startService(requestHabitsIntent.putExtra(C.TASK_TYPE, C.GET_ALL_HABITS_TASK))
            }
        }
    }

}