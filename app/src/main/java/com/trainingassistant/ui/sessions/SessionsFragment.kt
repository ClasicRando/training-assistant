package com.trainingassistant.ui.sessions

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.trainingassistant.R
import com.trainingassistant.activity.SessionActivity
import com.trainingassistant.databinding.FragmentSessionsBinding
import java.time.format.DateTimeFormatter

class SessionsFragment : Fragment() {

    companion object {
        fun newInstance() = SessionsFragment()
    }

    private val viewModel: SessionsViewModel by viewModels { SessionsViewModelFactory(this) }
    private lateinit var binding: FragmentSessionsBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var _dateFormat: DateTimeFormatter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _dateFormat = DateTimeFormatter.ofPattern(resources.getString(R.string.date_format))
        binding = FragmentSessionsBinding.inflate(inflater, container, false)
        datePickerDialog = DatePickerDialog(requireContext()).apply {
            setOnDateSetListener { _, year, month, dayOfMonth ->
                viewModel.updateDate(year, month, dayOfMonth)
            }
        }
        binding.srlSessions.setOnRefreshListener {
            viewModel.refreshSessions()
        }
        binding.btnCurrentDate.setOnClickListener {
            datePickerDialog.show()
        }
        binding.rvSessions.adapter = viewModel.adapter
        viewModel.showProgress.observe(viewLifecycleOwner) { isProgress ->
            binding.srlSessions.isRefreshing = isProgress
        }
        viewModel.currentDate.observe(viewLifecycleOwner) { date ->
            binding.btnCurrentDate.text = date.format(_dateFormat)
        }
        viewModel.selectedSessionId.observe(viewLifecycleOwner) { id ->
            startActivity(
                Intent(context, SessionActivity::class.java)
                    .putExtra("id", id)
            )
        }
        viewModel.sessions.observe(viewLifecycleOwner) {
            viewModel.adapter.notifyDataSetChanged()
        }
        if (viewModel.sessionCount == 0) {
            viewModel.refreshSessions()
        } else {
            (binding.rvSessions.layoutManager as LinearLayoutManager)
                .scrollToPosition(viewModel.feedPosition)
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val layoutManager = binding.rvSessions.layoutManager as LinearLayoutManager
        viewModel.saveFeedPosition(layoutManager.findFirstVisibleItemPosition())
    }
}