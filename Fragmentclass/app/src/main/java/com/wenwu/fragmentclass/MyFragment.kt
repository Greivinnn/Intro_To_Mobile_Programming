package com.wenwu.fragmentclass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


interface FragmentListener
{
    fun removeButtonClicked()
}

class MyFragment : Fragment()
{
    // assign this variable to whatever is listener for
    // this instance of the fragment
    lateinit var listener: FragmentListener

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val myFragView = inflater.inflate(R.layout.my_fragment_layout,
            container,
            false)

        val deleteButton = myFragView.findViewById<Button>(R.id.fragmentButton_id)
        deleteButton.setOnClickListener {
            listener.removeButtonClicked()

        }

        return myFragView
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)

        if (context is FragmentListener)
            listener = context
    }

    override fun onDetach()
    {
        super.onDetach()
    }
}