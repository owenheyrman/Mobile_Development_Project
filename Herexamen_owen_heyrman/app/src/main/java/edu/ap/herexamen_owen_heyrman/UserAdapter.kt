package edu.ap.herexamen_owen_heyrman

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.ap.herexamen_owen_heyrman.databinding.ItemUserBinding

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = ""
)

//class UserAdapter(context: Context, private val users: List<User>) : ArrayAdapter<User>(context, 0, users) {
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val user = getItem(position)
//        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)

//        val tvName = view.findViewById<TextView>(R.id.tvUserName)
//        tvName.text = "${user?.firstName} ${user?.lastName}"
//
//        return view
//    }
//}

class UserAdapter(
    private val onUserSelected: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvUserName.text = "${user.firstName} ${user.lastName}"
            binding.root.setOnClickListener {
                onUserSelected(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}