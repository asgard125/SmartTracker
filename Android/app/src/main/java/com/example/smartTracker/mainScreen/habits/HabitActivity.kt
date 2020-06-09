package com.example.smartTracker.mainScreen.habits

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
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
import kotlin.collections.ArrayList

class HabitActivity : AppCompatActivity() {

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

    private lateinit var habit: Habit
    private var updatedPosition: Int = 0

    private lateinit var model : HabitsModel

    private var isMuted = false
    private lateinit var notificationManager: HabitsNotificationManager

    override fun onBackPressed() {
        val newHabit = getNewHabit()
        if (newHabit != habit) {
            val listener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    Dialog.BUTTON_POSITIVE -> {
                        saveResult(newHabit)
                        notificationManager.updateHabitAlarms(habit, newHabit)
                        finish()
                    }
                    Dialog.BUTTON_NEGATIVE -> {
                        notificationManager.updateHabitAlarms(habit, newHabit)
                        finish()
                    }
                }
            }
            val dialog =
                AlertDialog.Builder(this).setTitle(getString(R.string.save_changes_question))
                    .setPositiveButton(R.string.yes, listener)
                    .setNegativeButton(R.string.no, listener)
                    .setNeutralButton(R.string.cancel, listener)
                    .create()
            dialog.show()
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit)

        model = HabitsModel(baseContext)

        notificationManager = HabitsNotificationManager(baseContext)

        updatedPosition = intent.extras!!.getInt("Position")
        habit = intent.extras!!.getParcelable<Habit>("Habit")!!

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

        nameText.setText(habit.name)
        descriptionText.setText(habit.description)

        plusesRecycler.layoutManager = LinearLayoutManager(baseContext)
        minusesRecycler.layoutManager = LinearLayoutManager(baseContext)

        /*Removes lags from transition between list screen and edit screen, by delaying inflating recycler view's items
        Handler().postDelayed({
            plusesAdapter = PlusesMinusesAdapter(ArrayList(habit.pluses), PLUSES)
            minusesAdapter = PlusesMinusesAdapter(ArrayList(habit.minuses), MINUSES)

            plusesRecycler.adapter = plusesAdapter
            minusesRecycler.adapter = minusesAdapter

        }, 1000)
        Removes lags from transition from list screen to edit screen, by delaying inflating recycler view*/

        plusesAdapter = PlusesMinusesAdapter(ArrayList(habit.pluses), PLUSES)
        minusesAdapter = PlusesMinusesAdapter(ArrayList(habit.minuses), MINUSES)

        plusesRecycler.adapter = plusesAdapter
        minusesRecycler.adapter = minusesAdapter

        timePicker.setIs24HourView(true)
        timePicker.hour = (habit.notifyTime[0].toString() + habit.notifyTime[1]).toInt()
        timePicker.minute =
            (habit.notifyTime[3].toString() + habit.notifyTime[4]).toInt()

        for (day in 0..6) {
            toggleButtons[day].isChecked = habit.weekdays.contains(day)
        }

        if (habit.isPublic) {
            typeGroup.check(R.id.HabitPublicButton)
        } else {
            typeGroup.check(R.id.HabitPrivateButton)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.habit_menu, menu)

        isMuted = habit.isMuted
        val drawable =
            getDrawable(if (isMuted) R.drawable.ic_notifications_off else R.drawable.ic_notifications)
        val title = getString(if (isMuted) R.string.unmute_habit else R.string.mute_habit)

        menu?.getItem(0)?.icon = drawable
        menu?.getItem(0)?.title = title

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.HabitSaveItem -> {
                val newHabit = getNewHabit()
                if (habit != newHabit) {
                    saveResult(newHabit)
                }
                notificationManager.updateHabitAlarms(habit, newHabit)
                finish()
            }
            R.id.HabitMuteItem -> {
                isMuted = !isMuted
                val title = getString(if (isMuted) R.string.unmute_habit else R.string.mute_habit)
                val drawable = if (isMuted) {
                    notificationManager.unmuteHabit(habit)
                    getDrawable(R.drawable.ic_notifications_off)
                } else {
                    notificationManager.muteHabit(habit)
                    getDrawable(R.drawable.ic_notifications)
                }
                item.icon = drawable
                item.title = title
            }
            else -> throw Exception("There is no such type of menu item")
        }
        return true
    }

    private fun saveResult(newHabit: Habit) {
        intent.putExtras(bundleOf("Habit" to newHabit, "Position" to updatedPosition))
        setResult(Activity.RESULT_OK, intent)
        val intent = Intent(baseContext, HabitsService::class.java)
        startService(intent.putExtra(C.TASK_TYPE, C.UPDATE_HABIT_TASK).putExtra(C.habit, newHabit))
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

        return habit.copy(
            name = name,
            description = description,
            pluses = pluses,
            minuses = minuses,
            notifyTime = notificationTime,
            weekdays = weekDays,
            isDone = habit.isDone,
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