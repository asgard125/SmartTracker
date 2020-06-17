package com.example.smartTracker.mainScreen.rating

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartTracker.R
import com.example.smartTracker.data.Habit
import com.example.smartTracker.data.User
import com.example.smartTracker.mainScreen.habits.HabitsService
import com.example.smartTracker.objects.C
import com.example.smartTracker.objects.Database
import java.lang.StringBuilder

open class HabitsAdapter(val habits : ArrayList<Habit>, val context : Context?) : RecyclerView.Adapter<HabitsAdapter.HabitHolder>(){

    private val isExpanded = ArrayList<Boolean>()

    init{
        for(i in 0 until habits.size){
            isExpanded.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitHolder {
        return HabitHolder(LayoutInflater.from(context).inflate(R.layout.item_profile_habit, parent, false))
    }

    override fun onBindViewHolder(holder: HabitHolder, position: Int) {

        val habit = habits[holder.adapterPosition]
        holder.bind(habit)

        holder.expandablePart.visibility = if(isExpanded[holder.adapterPosition]) View.VISIBLE else View.GONE

        holder.itemTop.setOnClickListener{
            isExpanded[holder.adapterPosition] = !isExpanded[holder.adapterPosition]
            notifyItemChanged(holder.adapterPosition)
        }

    }

    override fun getItemCount(): Int {
        return habits.size
    }

    open inner class HabitHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

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
        val copyButton : ImageView = itemView.findViewById(R.id.ProfileHabitCopyButton)
        val userButton : ImageView = itemView.findViewById(R.id.ProfileHabitUserButton)

        fun bind(habit : Habit){

            bindUserButton()

            name.text = habit.name
            reputation.text = context?.getString(R.string.reputation_pattern, habit.reputation)
            if(habit.isVoted){
                if(habit.voteType == Habit.POSITIVE){
                    likeButton.setImageDrawable(context?.getDrawable(R.drawable.ic_like_activated))
                }else if(habit.voteType == Habit.NEGATIVE){
                    dislikeButton.setImageDrawable(context?.getDrawable(R.drawable.ic_dislike_activated))
                }
            }

            likeButton.setOnClickListener{
                if(!habit.isVoted){
                    if(canVote(habit)) {
                        likeButton.setImageDrawable(context?.getDrawable(R.drawable.ic_like_activated))
                        habit.isVoted = true
                        habit.voteType = Habit.POSITIVE
                        habit.reputation++
                        reputation.text =
                            context?.getString(R.string.reputation_pattern, habit.reputation)
                        voteForHabit(Habit.POSITIVE, habit.serverId)
                    }
                }
                else{
                    Toast.makeText(context, context?.getString(R.string.vote_error), Toast.LENGTH_SHORT).show()
                }
            }

            dislikeButton.setOnClickListener{
                if(!habit.isVoted){
                    if(canVote(habit)) {
                        dislikeButton.setImageDrawable(context?.getDrawable(R.drawable.ic_dislike_activated))
                        habit.voteType = Habit.NEGATIVE
                        habit.isVoted = true
                        habit.reputation--
                        reputation.text = context?.getString(R.string.reputation_pattern, habit.reputation)
                        voteForHabit(Habit.NEGATIVE, habit.serverId)
                    }
                }else{
                    Toast.makeText(context, context?.getString(R.string.vote_error), Toast.LENGTH_SHORT).show()
                }
            }

            description.text = if(habit.description.isNotEmpty()){
                habit.description
            }else{
                context?.getString(R.string.no_description)
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
                context?.getString(R.string.no_pluses)
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
                context?.getString(R.string.no_minuses)
            }


            val time = StringBuilder()
            for(i in 0 until habit.weekdays.size){
                if(i != 0){
                    time.append(", ")
                }
                time.append(when(habit.weekdays[i]){
                    0 -> context?.getString(R.string.monday)
                    1 -> context?.getString(R.string.tuesday)
                    2 -> context?.getString(R.string.wednesday)
                    3 -> context?.getString(R.string.thursday)
                    4 -> context?.getString(R.string.friday)
                    5 -> context?.getString(R.string.saturday)
                    6 -> context?.getString(R.string.sunday)
                    else -> "Error"
                })
            }
            time.append(" - ${habit.notifyTime}")
            dateText.text = time.toString()

            copyButton.setOnClickListener{
                if(Database.HabitsModel.getCountOfHabits() < C.MAX_HABITS){
                    val copiedHabit = habit.copy(id = -1,
                        serverId = -1,
                        reputation = 0,
                        isVoted = false,
                        voteType = Habit.POSITIVE)
                    copiedHabit.id = Database.HabitsModel.addHabitAndReturnId(copiedHabit)
                    val addIntent = Intent(context, HabitsService::class.java).putExtra(C.habit, copiedHabit).putExtra(
                        C.TASK_TYPE, C.ADD_HABIT_TASK)
                    context?.startService(addIntent)
                }else{
                    Toast.makeText(context, context?.getString(R.string.max_habits_error), Toast.LENGTH_LONG).show()
                }
            }

        }

        private fun voteForHabit(voteType : String, serverId : Long){
            val intent = Intent(context, ProfileService::class.java)
            intent.putExtra(C.TASK_TYPE, C.VOTE_FOR_HABIT_TASK)
            intent.putExtra(C.VOTE_TYPE, voteType)
            intent.putExtra(C.serverId, serverId)
            context?.startService(intent)
        }

        open fun bindUserButton(){
            userButton.visibility = View.GONE
        }

        open fun canVote(habit : Habit) : Boolean{
            return true
        }

    }

}