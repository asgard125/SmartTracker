package com.example.smartTracker.mainScreen.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartTracker.R
import com.example.smartTracker.data.Goal
import com.example.smartTracker.data.Habit
import com.example.smartTracker.objects.C

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
                recycler.adapter = HabitsAdapter(habits, context)
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

}