package com.example.countriesrvapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

interface RowListener {
    fun onRowClicked(index: Int)
}

class HomeActivity : AppCompatActivity(), RowListener {

    lateinit var selectedCountryTextView: TextView

    override fun onRowClicked(index: Int) {
        selectedCountryTextView.text = AppData.countries[index]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_layout)

        selectedCountryTextView = findViewById(R.id.selectedCountryTextView)

        val countriesRV = findViewById<RecyclerView>(R.id.countriesRV_id)
        countriesRV.layoutManager = LinearLayoutManager(this)
        countriesRV.adapter = CountriesAdapter(this)
    }
}

class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.countryTextView_id)
}

class CountriesAdapter(val listener: RowListener) : RecyclerView.Adapter<CountryViewHolder>() {

    override fun getItemCount(): Int {
        return AppData.countries.count()
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): CountryViewHolder {
        val rowView = LayoutInflater.from(container.context)
            .inflate(R.layout.country_row, container, false)

        return CountryViewHolder(rowView)
    }

    override fun onBindViewHolder(viewHolder: CountryViewHolder, index: Int) {
        viewHolder.textView.text = AppData.countries[index]

        viewHolder.textView.setOnClickListener {
            listener.onRowClicked(index)
        }
    }
}