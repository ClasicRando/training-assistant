package com.trainingassistant.ui.client

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.trainingassistant.R
import com.trainingassistant.activity.SessionActivity
import com.trainingassistant.databinding.FragmentClientBinding
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ClientFragment : Fragment() {

    companion object {
        fun newInstance() = ClientFragment()
    }

    private val viewModel: ClientViewModel by viewModels()
    private lateinit var binding: FragmentClientBinding
    private val _zoneId = ZoneId.systemDefault()
    private lateinit var _dateFormat: DateTimeFormatter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _dateFormat = DateTimeFormatter.ofPattern(resources.getString(R.string.date_format))
        viewModel.setClientId(
            if (savedInstanceState != null)
                savedInstanceState.getString("id", "")
            else
                arguments?.getString("id", "") ?: ""
        )
        binding = FragmentClientBinding.inflate(inflater, container, false)
        viewModel.client.observe(viewLifecycleOwner) { client ->
            binding.txtClientName.text = client.name
            binding.txtClientStartDate.text = String.format(
                resources.getString(R.string.txt_client_start_date),
                client.startDateLocalText(_zoneId, _dateFormat)
            )
            binding.txtClientEndDate.text = String.format(
                resources.getString(R.string.txt_client_end_date),
                client.endDateLocalText(_zoneId, _dateFormat)
            )
            binding.txtClientBankedSessions.text = String.format(
                resources.getString(R.string.txt_client_banked_session),
                client.bankedSessions
            )
            binding.txtClientScheduleType.text = client.schedule.scheduleTypeText
            binding.txtClientScheduleDetails.text = client.schedule.scheduleDetails
            if (client.schedule.scheduleDetails.isEmpty()) {
                binding.txtClientScheduleDetails.visibility = View.GONE
            }
        }
        viewModel.sessionSelectedId.observe(viewLifecycleOwner) { id ->
            startActivity(
                Intent(context, SessionActivity::class.java)
                    .putExtra("id", id)
            )
        }
        viewModel.sessions.observe(viewLifecycleOwner) {
            viewModel.adapter.notifyDataSetChanged()
        }
        binding.rvClientSessions.adapter = viewModel.adapter
        binding.fabEditClient.setOnClickListener {
            val fragment = EditClientFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putString("id", viewModel.id)
                }
            }
            parentFragmentManager.beginTransaction()
                .addToBackStack("edit")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, fragment)
                .commit()
        }
        viewModel.refreshClient()
        return binding.root
    }

}