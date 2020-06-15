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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.smartTracker.R
import com.example.smartTracker.objects.C
import com.example.smartTracker.data.User

class RatingFragment : Fragment() {

    private lateinit var root : View
    private lateinit var ratingRecycler : RecyclerView
    private lateinit var refreshLayout : SwipeRefreshLayout

    private lateinit var adapter : RatingAdapter
    private lateinit var filter : IntentFilter
    private var receiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            val users = intent?.getParcelableArrayListExtra<User>(C.USERS)

            if(users != null){
                if(!this@RatingFragment::adapter.isInitialized){
                    adapter = RatingAdapter(users)
                    ratingRecycler.adapter = adapter
                }else{
                    adapter.users = users
                    adapter.notifyDataSetChanged()
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filter = IntentFilter(C.ACTION_RATING_SERVICE)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_rating, container, false)

        refreshLayout = root.findViewById(R.id.RatingRefreshLayout)
        ratingRecycler = root.findViewById(R.id.RatingRecycler)

        val ratingIntent = Intent(context, RatingService::class.java)

        refreshLayout.setOnRefreshListener {
            activity?.startService(ratingIntent)
            refreshLayout.isRefreshing = false
        }

        ratingRecycler.layoutManager = LinearLayoutManager(context)
        ratingRecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        activity?.startService(ratingIntent)
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

    private inner class RatingAdapter(var users : ArrayList<User>) : RecyclerView.Adapter<RatingAdapter.RatingHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingHolder {
            return RatingHolder(LayoutInflater.from(context).inflate(R.layout.item_rating, parent, false))
        }

        override fun onBindViewHolder(holder: RatingHolder, position: Int) {
            val user = users[holder.adapterPosition]

            holder.positionText.text = user.ratingPlace.toString()

            if(user.name != getString(R.string.you)){
                val wordToSpan = SpannableString("${user.name}#${user.userId}")
                val start = user.name.length
                val end = wordToSpan.length
                wordToSpan.setSpan(ForegroundColorSpan(Color.GRAY), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                holder.nameAndIdText.text = wordToSpan
            }else{
                val wordToSpan = SpannableString(user.name)
                wordToSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorPrimaryDark, null)), 0, user.name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                holder.nameAndIdText.text = wordToSpan
            }

            holder.ratingCountText.text = user.rating.toString()

            holder.itemView.setOnClickListener{
                if(user.name != getString(R.string.you)){
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra(C.USER, user)
                    startActivity(intent)
                    activity?.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                }
            }

        }

        override fun getItemCount(): Int {
            return users.size
        }

        private inner class RatingHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

            val positionText: TextView = itemView.findViewById(R.id.RatingPositionText)
            val nameAndIdText: TextView = itemView.findViewById(R.id.RatingNameIdText)
            val ratingCountText: TextView = itemView.findViewById(R.id.RatingCountText)

        }

    }

}