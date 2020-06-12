package com.example.smartTracker.mainScreen.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.smartTracker.R
import com.example.smartTracker.data.Goal
import com.example.smartTracker.data.Habit
import com.example.smartTracker.objects.C
import java.lang.StringBuilder

class ProfilePagerFragment : Fragment(){

    private lateinit var root : View
    private lateinit var recycler : RecyclerView

    companion object{

        const val HABITS = 1
        const val GOALS = 2
        const val FRAGMENT_TYPE = "FRAGMENT_TYPE"

        fun getNewHabitsInstance(position : Int, habits : ArrayList<Habit>) : Fragment{
            val args = bundleOf(FRAGMENT_TYPE to position, C.habits to habits)
            val fragment = ProfilePagerFragment()
            fragment.arguments = args
            return fragment
        }

        fun getNewGoalsInstance(position : Int, goals : ArrayList<Goal>) : Fragment{
            val args = bundleOf(FRAGMENT_TYPE to position, C.goals to goals)
            val fragment = ProfilePagerFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return when (arguments?.getInt(FRAGMENT_TYPE)) {
            HABITS -> {
                root = inflater.inflate(R.layout.single_recycler, container, false)
                val habits = arguments?.getParcelableArrayList<Habit>(C.habits)!!
                recycler = root.findViewById(R.id.SingleRecycler)
                recycler.layoutManager = LinearLayoutManager(context)
                recycler.adapter = HabitsRecycler(habits)
                root
            }
            GOALS -> {
                super.onCreateView(inflater, container, savedInstanceState)
            }
            else -> {
                super.onCreateView(inflater, container, savedInstanceState)
            }
        }
    }

    private inner class HabitsRecycler(val habits : ArrayList<Habit>) : RecyclerView.Adapter<HabitsRecycler.HabitHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitHolder {
            return HabitHolder(LayoutInflater.from(context).inflate(R.layout.item_profile_habit, parent, false))
        }

        override fun onBindViewHolder(holder: HabitHolder, position: Int) {

            val habit = habits[holder.adapterPosition]
            holder.bind(habit)

        }

        override fun getItemCount(): Int {
            return habits.size
        }

        private inner class HabitHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

            val cardView : CardView = itemView.findViewById(R.id.ProfileHabitItemCard)
            val itemTop : LinearLayout = itemView.findViewById(R.id.ProfileHabitTop)
            val expandablePart : LinearLayout = itemView.findViewById(R.id.ProfileHabitExpandablePart)
            val name : TextView = itemView.findViewById(R.id.ProfileHabitName)
            val reputation : TextView = itemView.findViewById(R.id.ProfileHabitReputation)
            val likeButton : ImageView = itemView.findViewById(R.id.ProfileHabitLikes)
            val dislikeButton : ImageView = itemView.findViewById(R.id.ProfileHabitDislikes)
            val description : TextView = itemView.findViewById(R.id.ProfileHabitDescription)
            val plusesText : TextView = itemView.findViewById(R.id.ProfileHabitPluses)
            val minusesText : TextView = itemView.findViewById(R.id.ProfileHabitMinuses)
            val dateText : TextView = itemView.findViewById(R.id.ProfileHabitDate)

            private var isExpanded = false

            fun bind(habit : Habit){

                itemTop.setOnClickListener{
                    expandablePart.visibility = if(isExpanded){
                        View.GONE
                    }else{
                        View.VISIBLE
                    }
                    isExpanded = !isExpanded
                }

                name.text = habit.name
                reputation.text = getString(R.string.reputation_pattern, habit.reputation)
                if(habit.isVoted){
                    if(habit.isLiked){
                        likeButton.setImageDrawable(resources.getDrawable(R.drawable.ic_like_green, null))
                    }else{
                        dislikeButton.setImageDrawable(resources.getDrawable(R.drawable.ic_dislike_red, null))
                    }
                }

                description.text = if(habit.description.isNotEmpty()){
                    habit.description
                }else{
                    getString(R.string.no_description)
                }

                plusesText.text = if(habit.pluses.isNotEmpty()){
                    val pluses = StringBuilder()

                    for(i in 0 until habit.pluses.size){
                        if(i != 0){
                            pluses.append("\n")
                        }
                        pluses.append(habit.pluses[i])
                    }
                    pluses.toString()
                }else{
                    getString(R.string.no_pluses)
                }

                minusesText.text = if(habit.minuses.isNotEmpty()){
                    val minuses = StringBuilder()
                    for(i in 0 until habit.minuses.size){
                        if(i != 0){
                            minuses.append("\n")
                        }
                        minuses.append(habit.minuses[i])
                    }

                    minuses.toString()
                }else{
                    getString(R.string.no_minuses)
                }


                val time = StringBuilder()
                for(i in 0 until habit.weekdays.size){
                    if(i != 0){
                        time.append(", ")
                    }
                    time.append(when(habit.weekdays[i]){
                        0 -> getString(R.string.monday)
                        1 -> getString(R.string.tuesday)
                        2 -> getString(R.string.wednesday)
                        3 -> getString(R.string.thursday)
                        4 -> getString(R.string.friday)
                        5 -> getString(R.string.saturday)
                        6 -> getString(R.string.sunday)
                        else -> "Error"
                    })
                }
                time.append(" - ${habit.notifyTime}")
                dateText.text = time.toString()

            }

        }

    }

}