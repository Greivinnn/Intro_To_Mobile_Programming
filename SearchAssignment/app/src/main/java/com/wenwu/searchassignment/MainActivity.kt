package com.wenwu.searchassignment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val joe = People("joe", 17, "male", "blue", 170, "Burnaby")
        val angela = People("angela", 18, "female", "brown", 165, "Vancouver")
        val fredo = People("fredo", 19, "male", "green", 180, "Richmond")
        val jp = People("jp", 20, "male", "brown", 175, "Surrey")

        val peopleArray = arrayOf(joe, angela, fredo, jp)

        val searchBox = findViewById<EditText>(R.id.searchEditText)
        val resultsView = findViewById<TextView>(R.id.resultsTextView)

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val results = searchPeople(peopleArray, query)
                displayResults(results, resultsView)
            }
        })
    }

    private fun searchPeople(people: Array<People>, query: String): List<People> {
        if (query.isEmpty()) return emptyList()

        return people.filter { person ->
            person.name.contains(query, ignoreCase = true) ||
                    person.address.contains(query, ignoreCase = true) ||
                    person.gender.contains(query, ignoreCase = true) ||
                    person.eyeColor.contains(query, ignoreCase = true) ||
                    person.age.toString().contains(query) ||
                    person.height.toString().contains(query)
        }
    }

    private fun displayResults(results: List<People>, view: TextView) {
        if (results.isEmpty()) {
            view.text = "No matches found"
            return
        }

        val sb = StringBuilder()
        for (person in results) {
            sb.appendLine("Name: ${person.name}")
            sb.appendLine("Age: ${person.age}")
            sb.appendLine("Gender: ${person.gender}")
            sb.appendLine("Eye Color: ${person.eyeColor}")
            sb.appendLine("Height: ${person.height} cm")
            sb.appendLine("Address: ${person.address}")
            sb.appendLine("---")
        }
        view.text = sb.toString()
    }
}