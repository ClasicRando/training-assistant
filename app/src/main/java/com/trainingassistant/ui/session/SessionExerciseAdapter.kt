package com.trainingassistant.ui.session

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trainingassistant.data.model.SessionExercise
import com.trainingassistant.databinding.ExerciseSessionItemBinding
import com.trainingassistant.listen

class SessionExerciseAdapter(
    private val viewModel: SessionViewModel,
    private val clickEvent: (Int) -> Unit
) : RecyclerView.Adapter<SessionExerciseAdapter.SessionExerciseViewHolder>() {

    class SessionExerciseViewHolder(private val binding: ExerciseSessionItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(sessionExercise: SessionExercise) {
            binding.txtSessionExerciseDetails.text = sessionExercise.toString()
            binding.txtSessionExerciseComments.text = sessionExercise.commentsText
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SessionExerciseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ExerciseSessionItemBinding.inflate(inflater, parent, false)
        return SessionExerciseViewHolder(binding).listen {
            clickEvent(it)
        }
    }

    override fun onBindViewHolder(
        holder: SessionExerciseViewHolder,
        position: Int
    ) {
        holder.bind(viewModel.getExercise(position))
    }

    override fun getItemCount(): Int  = viewModel.exerciseCount
}