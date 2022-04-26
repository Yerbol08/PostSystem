package com.enu.postsystem.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.enu.postsystem.databinding.FragmentDashboardBinding
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private var adapter:OrderAdapter?= null
    var data = arrayListOf<OrderItems>()
    var db = FirebaseFirestore.getInstance()
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.recyclerViewDashboard.setHasFixedSize(true)
        adapter = context?.let { OrderAdapter(data, it) }
        binding.recyclerViewDashboard.adapter = adapter
        showOrder()
        return root
    }

    fun showOrder(){
        db.collection("AtuPost").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val data1 = OrderItems(document["name"].toString(),
                            document["dateConf"].toString(),
                            document["phone"].toString(),
                            document["vesTovera"].toString(),
                            document["city"].toString(),
                            document["price"].toString(),
                            document["textDelivery"].toString())
                        data.add(data1)
                    }
                    adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Error getting documents.", Toast.LENGTH_LONG).show()
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}