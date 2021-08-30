package com.trainingassistant.ui.sessions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trainingassistant.data.model.Session
import com.trainingassistant.databinding.SessionItemBinding
import com.trainingassistant.listen

class SessionsAdapter(
    private val viewModel: SessionsViewModel,
    private val clickEvent: (Int) -> Unit
) : RecyclerView.Adapter<SessionsAdapter.SessionsViewHolder>() {

    class SessionsViewHolder(private val binding: SessionItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(session: Session) {
            binding.txtSessionItemName.text = session.client.name
            binding.txtSessionItemTime.text = session.itemTimeText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SessionItemBinding.inflate(inflater, parent, false)
        return SessionsViewHolder(binding).listen {
            clickEvent(it)
        }
    }

    override fun onBindViewHolder(holder: SessionsViewHolder, position: Int) {
        holder.bind(viewModel.getSession(position))
    }

    override fun getItemCount(): Int  = viewModel.sessionCount
}