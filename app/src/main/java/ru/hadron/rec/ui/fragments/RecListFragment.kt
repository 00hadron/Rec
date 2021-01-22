package ru.hadron.rec.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_rec_list.*
import ru.hadron.rec.R
import ru.hadron.rec.adapters.RecListAdapter

class RecListFragment : Fragment(R.layout.fragment_rec_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val adapter = RecListAdapter()
        rvRecList.adapter = adapter
        rvRecList.layoutManager = LinearLayoutManager(activity)
    }
}