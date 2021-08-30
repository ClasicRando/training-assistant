package com.trainingassistant.ui.client

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.trainingassistant.R
import com.trainingassistant.databinding.FragmentEditClientBinding
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EditClientFragment : Fragment() {

    companion object {
        fun newInstance() = EditClientFragment()
    }

    private val viewModel: EditClientViewModel by viewModels()
    private val _zoneId = ZoneId.systemDefault()
    private lateinit var binding: FragmentEditClientBinding
    private lateinit var _dateFormat: DateTimeFormatter
    private lateinit var startDatePicker: DatePickerDialog
    private lateinit var endDatePicker: DatePickerDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        startDatePicker = DatePickerDialog(requireContext()).apply {
            setOnDateSetListener { _, year, month, day ->
                viewModel.updateStartDate(
                    LocalDate.of(year, month, day)
                        .atStartOfDay(_zoneId)
                        .toEpochSecond()
                )
            }
        }
        endDatePicker = DatePickerDialog(requireContext()).apply {
            setOnDateSetListener { _, year, month, day ->
                viewModel.updateEndDate(
                    LocalDate.of(year, month, day)
                        .atStartOfDay(_zoneId)
                        .toEpochSecond()
                )
            }
        }
        _dateFormat = DateTimeFormatter.ofPattern(resources.getString(R.string.date_format))
        viewModel.setClientId(
            if (savedInstanceState != null)
                savedInstanceState.getString("id", "")
            else
                arguments?.getString("id", "") ?: ""
        )
        binding = FragmentEditClientBinding.inflate(inflater, container, false)
        viewModel.client.observe(viewLifecycleOwner) { client ->
            binding.etxtEditClientName.setText(client.name)
            binding.btnEditClientStartDate.text = client.startDateLocalText(_zoneId, _dateFormat)
            binding.btnEditClientEndDate.text = client.endDateLocalText(_zoneId, _dateFormat)
            binding.etxtEditClientBankedSessions.setText(client.bankedSessions.toString())
        }
        binding.btnEditClientStartDate.setOnClickListener { button ->
            if (viewModel.client.value == null) {
                Snackbar.make(button, "Client not found", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.client.value?.startDateLocal(_zoneId)?.let { date ->
                startDatePicker.updateDate(date.year, date.monthValue, date.dayOfMonth)
            }
            startDatePicker.show()
        }
        binding.btnEditClientEndDate.setOnClickListener { button ->
            if (viewModel.client.value == null) {
                Snackbar.make(button, "Client not found", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.client.value?.endDateLocal(_zoneId)?.let { date ->
                endDatePicker.updateDate(date.year, date.monthValue, date.dayOfMonth)
            }
            endDatePicker.show()
        }
        viewModel.refreshClient()
        return binding.root
    }

}