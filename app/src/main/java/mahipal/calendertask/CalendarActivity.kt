package mahipal.calendertask

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.calender_view.*
import java.util.*
import kotlin.collections.ArrayList

class CalendarActivity : AppCompatActivity(), View.OnClickListener, GridCellAdapter.OnDateCallback, AdapterView.OnItemSelectedListener{

    private val LOG_TAG = CalendarActivity::class.java.simpleName

    private var adapter: GridCellAdapter? = null
    private var calendar: Calendar? = null
    private var month: Int = 0
    private var year: Int = 0
    private val dateFormatter = DateFormat()
    private val yearTemplate = "yyyy"
    private val monthTemplate = "MM"
    private val dayTemplate = "dd"
    private val isDoubleClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calender_view)

        prevMonth.setOnClickListener(this)
        nextMonth.setOnClickListener(this)
        spinner_currentYer.onItemSelectedListener = this
        spinner_currentMonth.onItemSelectedListener = this

        calendar = Calendar.getInstance(Locale.getDefault())
        calendar?.get(Calendar.MONTH)?.plus(1)?.let { month = it }
        calendar?.get(Calendar.YEAR)?.let { year = it }
        Log.d(LOG_TAG, "Calendar Instance:= " + "Month: " + month + " " + "Year: "
                + year)

        setYearSpinner()

        Log.d(LOG_TAG,"Year -----------> $year month ------------> $month")

        spinner_currentYer.setSelection(getIndexItem(spinner_currentYer, year.toString()))
        spinner_currentMonth.setSelection(month)

        adapter = GridCellAdapter(applicationContext, month, year, this)
        calendar_gridview.adapter = adapter

    }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        var year = ""
        if (spinner_currentYer.selectedItemPosition != 0) {
            year = spinner_currentYer.getItemAtPosition(spinner_currentYer.selectedItemPosition).toString()
        }
        val monthAtPosition = spinner_currentMonth.selectedItemPosition
        val month = spinner_currentMonth.getItemAtPosition(monthAtPosition).toString()

        when(adapterView?.id) {
            R.id.spinner_currentYer -> {
                if (monthAtPosition != 0 && year != "") {
                    setSelectedMonthAndYearOfDate(monthAtPosition, year.toInt())
                    val day = DateFormat.format(dayTemplate,calendar?.time)
                    setDate(day.toString(), month, year)
                }
            }

            R.id.spinner_currentMonth -> {
                if (monthAtPosition != 0 && year != "") {
                    setSelectedMonthAndYearOfDate(monthAtPosition, year.toInt())
                    val day = DateFormat.format(dayTemplate,calendar?.time)
                    setDate(day.toString(), month, year)
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun getIndexItem(spinner: Spinner, text: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(text,true)) {
                return i
            }
        }
        return 0
    }

    private fun setYearSpinner() {
        val years = ArrayList<String>()
        val thisYear = year

        years.add("Year")
        for (i in 1950..2034) {
            years.add(i.toString())
        }
        val adapter = ArrayAdapter<String>(this,R.layout.layout_spinner_item,R.id.tv_spinner_item,years)
        spinner_currentYer.adapter = adapter
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.prevMonth -> {
                if (month <= 1) {
                    month = 12
                    year--
                } else {
                    month--
                }
                Log.d(LOG_TAG, "Setting Prev Month in GridCellAdapter: " + "Month: "
                        + month + " Year: " + year)

                setGridCellAdapterToDate(month, year)
            }

            R.id.nextMonth -> {
                if (month > 11) {
                    month = 1
                    year++
                } else {
                    month++
                }
                Log.d(LOG_TAG, "Setting Next Month in GridCellAdapter: " + "Month: "
                        + month + " Year: " + year)

                setGridCellAdapterToDate(month, year)
            }
        }
    }

    private fun setDate(day: String,month: String,year: String){
        selectedDayMonthYear.text = "$day-$month-$year"
    }

    private fun setSelectedMonthAndYearOfDate(month: Int, year: Int) {
        adapter = GridCellAdapter(applicationContext, month, year, this)
        calendar_gridview.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    private fun setGridCellAdapterToDate(month: Int, year: Int) {
        adapter = GridCellAdapter(applicationContext, month, year, this)
        calendar?.set(year, month - 1, calendar?.get(Calendar.DAY_OF_MONTH)!!)

        val selectedYear = DateFormat.format(yearTemplate, calendar?.time)
        val day = DateFormat.format(dayTemplate,calendar?.time)

        val monthItem = spinner_currentMonth.getItemAtPosition(month).toString()
        setDate(day.toString(),monthItem,year.toString())

        spinner_currentMonth.setSelection(month)
        spinner_currentYer.setSelection(getIndexItem(spinner_currentYer, selectedYear.toString()))

        adapter?.notifyDataSetChanged()
        calendar_gridview.adapter = adapter
    }

    override fun onDateClick(date: String) {

        selectedDayMonthYear.text = date
    }
}
