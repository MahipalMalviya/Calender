package mahipal.calendertask

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class GridCellAdapter(val context: Context?, val month: Int?, val year: Int?, val callback: OnDateCallback?) : BaseAdapter() {

    private val LOG_TAG = GridCellAdapter::class.java.simpleName
    private val list = ArrayList<String>()

    private val DAY_OFFSET = 1
    private val weekdays = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    private val months = arrayOf("January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December")
    private val daysOfMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    private var daysInMonth: Int = 0
    private var currentDayOfMonth: Int = 0
    private var currentWeekDay: Int = 0

    private var calendarCell: Button? = null
    private var textDay: TextView? = null

    private var eventsPerMonthMap: HashMap<String, Int>? = null
    private var dateFormatter = SimpleDateFormat("dd-MMM-yyyy", Locale.US)

    init {
        val calendar = Calendar.getInstance()
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH))
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK))

        // Print Month
        printMonth(month ?: 0, year ?: 0)

        // Find Number of Events
        eventsPerMonthMap = findNumberOfEventsPerMonth(year ?: 0, month ?: 0)
    }

    interface OnDateCallback {
        fun onDateClick(date: String)
    }

    private fun getMonthAsString(i: Int): String {
        return months[i]
    }

    private fun getWeekDayAsString(i: Int): String {
        return weekdays[i]
    }

    private fun getNumberOfDaysOfMonth(i: Int): Int {
        return daysOfMonth[i]
    }

    override fun getItem(position: Int): String {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    private fun printMonth(mm: Int?, yy: Int?) {
        Log.d(LOG_TAG, "==> printMonth: mm: $mm yy: $yy")
        var trailingSpaces = 0
        var daysInPrevMonth = 0
        var prevMonth = 0
        var prevYear = 0
        var nextMonth = 0
        var nextYear = 0

        val currentMonth = mm?.minus(1)?.let { it } ?: 0
        val currentMonthName = getMonthAsString(currentMonth)
        daysInMonth = getNumberOfDaysOfMonth(currentMonth)

        Log.d(LOG_TAG, "Current Month: " + " " + currentMonthName + " having "
                + daysInMonth + " days.")

        val cal = GregorianCalendar(yy ?: 0, currentMonth, 1)
        Log.d(LOG_TAG, "Gregorian Calendar:= " + cal.time.toString())

        when (currentMonth) {
            11 -> {
                prevMonth = currentMonth - 1
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth)
                nextMonth = 0
                yy?.let { prevYear = it }
                yy?.plus(1)?.let { nextYear = it }
                Log.d(LOG_TAG, "*->PrevYear: " + prevYear + " PrevMonth:"
                        + prevMonth + " NextMonth: " + nextMonth
                        + " NextYear: " + nextYear)
            }
            0 -> {
                prevMonth = 11
                yy?.minus(1)?.let { prevYear = it }
                yy?.let { nextYear = it }
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth)
                nextMonth = 1
                Log.d(LOG_TAG, "**--> PrevYear: " + prevYear + " PrevMonth:"
                        + prevMonth + " NextMonth: " + nextMonth
                        + " NextYear: " + nextYear)
            }
            else -> {
                prevMonth = currentMonth - 1
                nextMonth = currentMonth + 1
                yy?.let { nextYear = it }
                yy?.let { prevYear = it }

                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth)
                Log.d(LOG_TAG, "***---> PrevYear: " + prevYear + " PrevMonth:"
                        + prevMonth + " NextMonth: " + nextMonth
                        + " NextYear: " + nextYear)
            }
        }

        val currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1
        trailingSpaces = currentWeekDay

        Log.d(LOG_TAG, "Week Day:" + currentWeekDay + " is "
                + getWeekDayAsString(currentWeekDay))
        Log.d(LOG_TAG, "No. Trailing space to Add: $trailingSpaces")
        Log.d(LOG_TAG, "No. of Days in Previous Month: $daysInPrevMonth")

        if (cal.isLeapYear(cal.get(Calendar.YEAR)))
            if (mm == 2)
                ++daysInMonth
            else if (mm == 3)
                ++daysInPrevMonth

        // Trailing Month days
        for (i in 0 until trailingSpaces) {
            Log.d(LOG_TAG,
                    "PREV MONTH:= "
                            + prevMonth
                            + " => "
                            + getMonthAsString(prevMonth)
                            + " "
                            + (daysInPrevMonth - trailingSpaces + DAY_OFFSET + i).toString())
            list.add(((((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i)).toString()
                    + "-GREY"
                    + "-"
                    + getMonthAsString(prevMonth)
                    + "-"
                    + prevYear))
        }

        // Current Month Days
        for (i in 1..daysInMonth) {
            Log.d(currentMonthName, ((i).toString() + " "
                    + getMonthAsString(currentMonth) + " " + yy))
            if (i == getCurrentDayOfMonth()) {
                list.add(((i).toString() + "-BLUE" + "-"
                        + getMonthAsString(currentMonth) + "-" + yy))
            } else {
                list.add(((i).toString() + "-WHITE" + "-"
                        + getMonthAsString(currentMonth) + "-" + yy))
            }
        }

        // Leading Month days
        for (i in 0 until list.size % 7) {
            Log.d(LOG_TAG, "NEXT MONTH:= " + getMonthAsString(nextMonth))
            list.add(((i + 1).toString() + "-GREY" + "-"
                    + getMonthAsString(nextMonth) + "-" + nextYear))
        }
    }

    private fun findNumberOfEventsPerMonth(year: Int,
                                           month: Int): HashMap<String, Int> {
        return HashMap()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        var row: View? = convertView
        if (row == null) {
            row = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_cell, parent, false)
        }

        calendarCell = row?.findViewById(R.id.btn_calendar_day_gridcell)
        textDay = row?.findViewById(R.id.txt_num_events_per_day)
        // Get a reference to the Day gridcell

        calendarCell?.setOnClickListener({
            Log.e(LOG_TAG, "position of item ----> $position")

            callback?.onDateClick(it?.tag as String)
        })

        // ACCOUNT FOR SPACING

        Log.d(LOG_TAG, "Current Day: " + getCurrentDayOfMonth())
        val day_color = list[position].split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val theday = day_color[0]
        val themonth = day_color[2]
        val theyear = day_color[3]
        if (eventsPerMonthMap?.isNotEmpty()!! && eventsPerMonthMap != null) {
            if (eventsPerMonthMap?.containsKey(theday)!!) {
                val numEvents = eventsPerMonthMap!![theday] as Int
                textDay?.text = numEvents.toString()
            }
        }

        // Set the Day GridCell
        calendarCell?.text = theday
        calendarCell?.tag = "$theday-$themonth-$theyear"
        Log.d(LOG_TAG, "Setting GridCell " + theday + "-" + themonth + "-"
                + theyear)

        if (day_color[1] == "GREY") {
            context?.resources?.getColor(R.color.lightgray)?.let { calendarCell?.setTextColor(it) }
        }
        if (day_color[1] == "WHITE") {
            context?.resources?.getColor(R.color.lightgray02)?.let { calendarCell?.setTextColor(it) }
        }
        if (day_color[1] == "BLUE") {
            context?.resources?.getColor(R.color.orrange)?.let { calendarCell?.setTextColor(it) }
        }
        return row
    }

    private fun getCurrentDayOfMonth(): Int {
        return currentDayOfMonth
    }

    private fun setCurrentDayOfMonth(currentDayOfMonth: Int) {
        this.currentDayOfMonth = currentDayOfMonth
    }

    private fun setCurrentWeekDay(currentWeekDay: Int) {
        this.currentWeekDay = currentWeekDay
    }

    fun getCurrentWeekDay(): Int {
        return currentWeekDay
    }


}