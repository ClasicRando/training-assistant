package com.trainingassistant.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trainingassistant.R
import com.trainingassistant.data.model.Session
import com.trainingassistant.databinding.ClientSessionItemBinding
import com.trainingassistant.getStringResource
import com.trainingassistant.listen


class ClientSessionAdapter(
    private val viewModel: ClientViewModel,
    private val clickEvent: (Int) -> Unit
) : RecyclerView.Adapter<ClientSessionAdapter.ClientSessionViewHolder>() {

    class ClientSessionViewHolder(private val binding: ClientSessionItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(session: Session) {
            binding.txtClientSessionDateTime.text = session.date
            binding.txtClientSessionDuration.text = String.format(
                getStringResource(R.string.session_duration),
                session.duration
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientSessionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ClientSessionItemBinding.inflate(inflater, parent, false)
        return ClientSessionViewHolder(binding).listen {
            clickEvent(it)
        }
    }

    override fun onBindViewHolder(holder: ClientSessionViewHolder, position: Int) {
        holder.bind(viewModel.getSession(position))
    }

    override fun getItemCount(): Int  = viewModel.sessionCount
}