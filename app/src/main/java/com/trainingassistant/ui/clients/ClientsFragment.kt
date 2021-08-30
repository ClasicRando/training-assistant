package com.trainingassistant.ui.clients

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.trainingassistant.activity.ClientActivity
import com.trainingassistant.databinding.FragmentClientsBinding

class ClientsFragment : Fragment() {

    companion object {
        fun newInstance() = ClientsFragment()
    }

    private val viewModel: ClientsViewModel by viewModels { ClientsViewModelFactory(this) }
    private lateinit var binding: FragmentClientsBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientsBinding.inflate(inflater, container, false)
        viewModel.showProgress.observe(viewLifecycleOwner) { isProgress ->
            binding.srlClients.isRefreshing = isProgress
        }
        binding.srlClients.setOnRefreshListener {
            viewModel.refreshClients()
        }
        binding.rvClients.adapter = viewModel.adapter
        viewModel.selectedClientId.observe(viewLifecycleOwner) { id ->
            startActivity(
                Intent(context, ClientActivity::class.java)
                    .putExtra("id", id)
            )
        }
        viewModel.clients.observe(viewLifecycleOwner) {
            viewModel.adapter.notifyDataSetChanged()
        }
        if (viewModel.clientCount == 0) {
            viewModel.refreshClients()
        } else {
            (binding.rvClients.layoutManager as LinearLayoutManager)
                .scrollToPosition(viewModel.feedPosition)
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val layoutManager = binding.rvClients.layoutManager as LinearLayoutManager
        viewModel.saveFeedPosition(layoutManager.findFirstVisibleItemPosition())
    }
}