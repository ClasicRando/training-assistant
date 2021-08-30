package com.trainingassistant.ui.session

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.trainingassistant.R
import com.trainingassistant.databinding.FragmentSessionBinding

class SessionFragment : Fragment() {

    companion object {
        fun newInstance() = SessionFragment()
    }

    private val viewModel: SessionViewModel by viewModels()
    private lateinit var binding: FragmentSessionBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setSessionId(
            if (savedInstanceState != null)
                savedInstanceState.getString("id", "")
            else
                arguments?.getString("id", "") ?: ""
        )
        binding = FragmentSessionBinding.inflate(inflater, container, false)
        viewModel.session.observe(viewLifecycleOwner) { session ->
            binding.txtSessionClientName.text = session.client.name
            binding.txtSessionDayTime.text = session.date
            binding.txtSessionDuration.text = String.format(
                resources.getString(R.string.session_duration),
                session.duration
            )
            binding.etxtNotes.setText(session.notes)
            viewModel.adapter.notifyDataSetChanged()
        }
        viewModel.showProgress.observe(viewLifecycleOwner) {
            binding.prgSessionData.visibility = if(it) View.VISIBLE else View.GONE
            binding.etxtNotes.isEnabled = !it
            binding.rvSessionExercises.isEnabled = !it
        }
        binding.rvSessionExercises.adapter = viewModel.adapter
        viewModel.refreshSession()
        return binding.root
    }
}