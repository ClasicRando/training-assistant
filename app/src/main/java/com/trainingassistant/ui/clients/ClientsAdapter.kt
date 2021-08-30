package com.trainingassistant.ui.clients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trainingassistant.R
import com.trainingassistant.data.model.Client
import com.trainingassistant.databinding.ClientItemBinding
import com.trainingassistant.getStringResource
import com.trainingassistant.listen

class ClientsAdapter(
    private val viewModel: ClientsViewModel,
    private val clickEvent: (Int) -> Unit
) : RecyclerView.Adapter<ClientsAdapter.ClientsViewHolder>() {

    class ClientsViewHolder(private val binding: ClientItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(client: Client) {
            binding.txtClientItemName.text = client.name
            binding.txtClientItemIsActive.text = String.format(
                getStringResource(R.string.is_client_active),
                if (client.isActive) "Y" else "N"
            )
            binding.txtClientItemScheduleType.text = String.format(
                getStringResource(R.string.client_schedule_type),
                client.schedule.scheduleTypeText
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ClientItemBinding.inflate(inflater, parent, false)
        return ClientsViewHolder(binding).listen {
            clickEvent(it)
        }
    }

    override fun onBindViewHolder(holder: ClientsViewHolder, position: Int) {
        holder.bind(viewModel.getClient(position))
    }

    override fun getItemCount(): Int  = viewModel.clientCount
}