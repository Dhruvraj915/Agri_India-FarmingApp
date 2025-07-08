package com.project.farmingapp.view.yojna

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.farmingapp.R
import com.project.farmingapp.adapter.YojnaAdapter
import com.project.farmingapp.databinding.FragmentYojnaListBinding
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.viewmodel.YojnaViewModel

class YojnaListFragment : Fragment(), CellClickListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: YojnaViewModel
    private lateinit var adapter: YojnaAdapter
    private lateinit var yojnaFragment: YojnaFragment
    private var _binding: FragmentYojnaListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel = ViewModelProvider(requireActivity())[YojnaViewModel::class.java]
        viewModel.getAllYojna("yojnas")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYojnaListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Krishi Yojna"
        setHasOptionsMenu(true)

        viewModel.message3.observe(viewLifecycleOwner, Observer {
            Log.d("Art All Data", it[0].data.toString())

            adapter = YojnaAdapter(requireContext(), it, this)
            binding.rcyclrYojnaList.adapter = adapter
            binding.rcyclrYojnaList.layoutManager = LinearLayoutManager(requireContext())
        })
    }

    override fun onCellClickListener(name: String) {
        yojnaFragment = YojnaFragment()
        val bundle = Bundle().apply {
            putString("name", name)
        }
        yojnaFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, yojnaFragment, name)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setReorderingAllowed(true)
            .addToBackStack("yojnaListFrag")
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YojnaListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
