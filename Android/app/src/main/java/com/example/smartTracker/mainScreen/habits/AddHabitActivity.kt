package com.example.smartTracker.mainScreen.habits

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartTracker.data.Habit
import com.google.android.material.textfield.TextInputEditText

import com.example.smartTracker.R
import com.example.smartTracker.objects.C
import com.example.smartTracker.objects.Database
import kotlin.collections.ArrayList

class AddHabitActivity : AppCompatActivity() {

    private val PLUSES = 1
    private val MINUSES = -1
    private val COMMON_HOLDER = 2
    private val ADDING_HOLDER = 3

    private lateinit var toolbar: Toolbar
    private lateinit var nameText: TextInputEditText
    private lateinit var descriptionText: TextInputEditText
    private lateinit var plusesRecycler: RecyclerView
    private lateinit var minusesRecycler: RecyclerView
    private lateinit var timePicker: TimePicker
    private var toggleButtons = ArrayList<ToggleButton>()
    private lateinit var typeGroup: RadioGroup

    private lateinit var plusesAdapter: PlusesMinusesAdapter
    private lateinit var minusesAdapter: PlusesMinusesAdapter

    private var needToUpdateList = false
    private lateinit var filter : IntentFilter
    private val receiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == C.ACTION_NEW_DAY_UPDATE_UI){
                needToUpdateList = true
            }
        }

    }

    private var isMuted = false
    private lateinit var notificationManager: HabitsNotificationManager

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    override fun onBackPressed() {
        val newHabit = getNewHabit()
        val listener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                Dialog.BUTTON_POSITIVE -> {
                    addHabit(newHabit)
                    finish()
                }
                Dialog.BUTTON_NEGATIVE -> {
                    setResult(Activity.RESULT_OK, Intent().putExtra("needToUpdate", needToUpdateList))
                    finish()
                }
            }
        }
        val dialog = AlertDialog.Builder(this).setTitle(getString(R.string.create_new_habit))
                    .setPositiveButton(R.string.yes, listener)
                    .setNegativeButton(R.string.no, listener)
                    .setNeutralButton(R.string.cancel, listener)
                    .create()
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit)

        filter = IntentFilter(C.ACTION_NEW_DAY_UPDATE_UI)
        filter.addCategory(Intent.CATEGORY_DEFAULT)

        notificationManager = HabitsNotificationManager(baseContext)

        //Finding views
        toolbar = findViewById(R.id.HabitToolBar)
        nameText = findViewById(R.id.HabitName)
        descriptionText = findViewById(R.id.HabitDescription)
        plusesRecycler = findViewById(R.id.HabitPlusesRecycler)
        minusesRecycler = findViewById(R.id.HabitMinusesRecycler)

        timePicker = findViewById(R.id.NotificationTimePicker)

        toggleButtons.add(findViewById(R.id.HabitMondayButton))
        toggleButtons.add(findViewById(R.id.HabitTuesdayButton))
        toggleButtons.add(findViewById(R.id.HabitWednesdayButton))
        toggleButtons.add(findViewById(R.id.HabitThursdayButton))
        toggleButtons.add(findViewById(R.id.HabitFridayButton))
        toggleButtons.add(findViewById(R.id.HabitSaturdayButton))
        toggleButtons.add(findViewById(R.id.HabitSundayButton))

        typeGroup = findViewById(R.id.HabitTypeGroup)
        //Finding views

        toolbar.title = getString(R.string.habit)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        updateUI()

    }

    private fun updateUI(){
        nameText.setText(getString(R.string.new_habit))

        plusesRecycler.layoutManager = LinearLayoutManager(baseContext)
        minusesRecycler.layoutManager = LinearLayoutManager(baseContext)

        plusesAdapter = PlusesMinusesAdapter(ArrayList(), PLUSES)
        minusesAdapter = PlusesMinusesAdapter(ArrayList(), MINUSES)

        plusesRecycler.adapter = plusesAdapter
        minusesRecycler.adapter = minusesAdapter

        timePicker.setIs24HourView(true)
        timePicker.hour = 12
        timePicker.minute = 0

        for (day in 0..6) {
            toggleButtons[day].isChecked = day % 2 == 0
            toggleButtons[day].setOnCheckedChangeListener { buttonView, isChecked ->
                if(!isChecked){
                    var checkedButtonsCount = 0
                    for(button in toggleButtons) {
                        if (button.isChecked) {
                            checkedButtonsCount++
                        }
                    }
                    if(checkedButtonsCount == 0){
                        Toast.makeText(baseContext, getString(R.string.one_day_notification_error), Toast.LENGTH_LONG).show()
                        buttonView.isChecked = !isChecked
                    }
                }
            }
        }

        typeGroup.check(R.id.HabitPublicButton)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.habit_menu, menu)

        isMuted = false
        val drawable = getDrawable(R.drawable.ic_notifications)
        val title = getString(R.string.mute_habit)

        menu?.getItem(0)?.icon = drawable
        menu?.getItem(0)?.title = title

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.HabitSaveItem -> {
                val newHabit = getNewHabit()
                addHabit(newHabit)
                finish()
            }
            R.id.HabitMuteItem -> {
                isMuted = !isMuted
                val title = getString(if (isMuted) R.string.unmute_habit else R.string.mute_habit)
                val drawable = if (isMuted) {
                    getDrawable(R.drawable.ic_notifications_off)
                } else {
                    getDrawable(R.drawable.ic_notifications)
                }
                item.icon = drawable
                item.title = title
            }
            else -> throw Exception("There is no such type of menu item")
        }
        return true
    }

    private fun addHabit(newHabit: Habit) {

        if(!newHabit.isMuted){
            notificationManager.setNewAlarms(newHabit)
        }
        newHabit.id = Database.HabitsModel.addHabitAndReturnId(newHabit)

        intent.putExtras(bundleOf(C.habit to newHabit, "needToUpdate" to needToUpdateList))
        setResult(Activity.RESULT_OK, intent)
        val serviceIntent = Intent(baseContext, HabitsService::class.java)
        startService(serviceIntent.putExtra(C.habit, newHabit).putExtra(C.TASK_TYPE, C.ADD_HABIT_TASK))
    }

    private fun getNewHabit(): Habit {
        val name = nameText.text.toString()
        val description = descriptionText.text.toString()
        val isPublic = typeGroup.checkedRadioButtonId == R.id.HabitPublicButton
        val pluses = plusesAdapter.data
        val minuses = minusesAdapter.data

        var notificationTime = if(timePicker.hour < 10){
            "0${timePicker.hour}:"
        }else{
            "${timePicker.hour}:"
        }

        notificationTime += if(timePicker.minute < 10){
            "0${timePicker.minute}"
        }else{
            "${timePicker.minute}"
        }

        val weekDays = ArrayList<Int>()
        for (day in 0..6) {
            if (toggleButtons[day].isChecked) {
                weekDays.add(day)
            }
        }

        return Habit(
            name = name,
            description = description,
            pluses = pluses,
            minuses = minuses,
            notifyTime = notificationTime,
            weekdays = weekDays,
            isDone = false,
            isPublic = isPublic,
            isMuted = isMuted
        )
    }

    private inner class PlusesMinusesAdapter(var data: ArrayList<String>, var type: Int) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == COMMON_HOLDER) {
                PlusesMinusesHolder(
                    LayoutInflater.from(baseContext)
                        .inflate(R.layout.item_pluses_minuses, parent, false)
                )
            } else {
                AddingHolder(
                    LayoutInflater.from(baseContext).inflate(R.layout.adding_item, parent, false)
                )
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is PlusesMinusesHolder) {

                val plusOrMinusImage = getDrawable(
                    if (type == PLUSES) {
                        R.drawable.ic_plus
                    } else {
                        R.drawable.ic_minus
                    }
                )

                holder.image.setImageDrawable(plusOrMinusImage)

                holder.title.setText(data[holder.adapterPosition])
                holder.title.addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }


                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        data[holder.adapterPosition] = s.toString()
                    }

                })

                holder.deleteButton.setOnClickListener {
                    data.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }

            } else if (holder is AddingHolder) {

                holder.addingText.text = getString(
                    if (type == PLUSES) {
                        R.string.add_new_plus
                    } else {
                        R.string.add_new_minus
                    }
                )

                holder.itemView.setOnClickListener {
                    if (type == PLUSES) {
                        data.add(getString(R.string.new_plus))
                    } else {
                        data.add(getString(R.string.new_minus))
                    }
                    notifyItemInserted(data.size)
                }

            }
        }

        override fun getItemCount(): Int {
            return data.size + 1
        }

        override fun getItemViewType(position: Int): Int {
            return if (position != data.size) {
                COMMON_HOLDER
            } else {
                ADDING_HOLDER
            }
        }

        private inner class AddingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val addingText = itemView.findViewById<TextView>(R.id.AddItemText)

        }

        private inner class PlusesMinusesHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

            val image: ImageView = itemView.findViewById(R.id.PlusesMinusesImage)
            val title: EditText = itemView.findViewById(R.id.PlusesMinusesText)
            val deleteButton: ImageView = itemView.findViewById(R.id.PlusesMinusesDelete)

        }

    }

}