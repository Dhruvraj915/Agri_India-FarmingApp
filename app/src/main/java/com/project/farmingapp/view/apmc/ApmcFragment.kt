package com.project.farmingapp.view.apmc

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.farmingapp.adapter.ApmcAdapter
import com.project.farmingapp.databinding.FragmentApmcBinding
import com.project.farmingapp.model.APMCApi
import com.project.farmingapp.model.data.APMCCustomRecords
import com.project.farmingapp.model.data.APMCMain
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ApmcFragment : Fragment() {

    private var _binding: FragmentApmcBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: ApmcAdapter
    var indexSpinner1: Int? = null
    var indexSpinner2: Int? = null
    var someMap: Map<Any, Array<String>>? = null
    var states: Array<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApmcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressApmc.visibility = View.GONE
        binding.loadingTextAPMC.visibility = View.GONE

        (activity as AppCompatActivity).supportActionBar?.title = "APMC"

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.dateValueTextApmc.text = sdf.format(Date())

        states = arrayOf(
            "None", "Andhra Pradesh", "Chandigarh", "Chattisgarh", "Gujarat",
            "Hariyana", "Himachal Pradesh", "Jammu & Kashmir", "Jharkhand",
            "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Odisha",
            "Pudu Cherry", "Punjab", "Rajasthan", "Tamil Nadu", "Telangana",
            "Uttar Pradesh", "Uttarakhand", "West Bengal"
        )

        val districtInGujarat = arrayOf(
            "None", "Ahmedabad", "Amreli", "Anand", "Aravalli", "Banaskantha",
            "Bharuch", "Bhavnagar", "Botad", "Chhota Udepur", "Dahod", "Dangs",
            "Devbhoomi Dwarka", "Gandhinagar", "Gir Somnath", "Jamnagar",
            "Junagadh", "Kachchh", "Kheda", "Mahisagar", "Mehsana", "Morbi",
            "Narmada", "Navsari", "Panchmahal", "Patan", "Porbandar", "Rajkot",
            "Sabarkantha", "Surat", "Surendranagar", "Tapi", "Vadodara", "Valsad"
        )

        val districtInMaha = arrayOf(
            "None", "Ahmednagar", "Akola", "Amravati", "Aurangabad", "Beed",
            "Bhandara", "Buldhana", "Chandrapur", "Dhule", "Gadchiroli", "Gondia",
            "Hingoli", "Jalgaon", "Jalna", "Kolhapur", "Latur", "Mumbai City",
            "Mumbai Suburban", "Nagpur", "Nanded", "Nandurbar", "Nashik",
            "Osmanabad", "Palghar", "Parbhani", "Pune", "Raigad", "Ratnagiri",
            "Sangli", "Satara", "Sindhudurg", "Solapur", "Thane", "Wardha",
            "Washim", "Yavatmal"
        )

        val emptyDistricts = arrayOf("None")

        val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, states!!)
        binding.spinner1.adapter = aa

        someMap = mapOf(
            "Andhra Pradesh" to arrayOf("None", "Anantapur", "Chittoor", "East Godavari"),
            "Gujarat" to districtInGujarat,
            "Kerala" to arrayOf("None", "Ernakulam", "Kozhikode"),
            "Maharashtra" to districtInMaha,
            "Rajasthan" to arrayOf("None", "Jaipur", "Jodhpur"),
            "Uttar Pradesh" to arrayOf("None", "Lucknow", "Kanpur"),
            "West Bengal" to arrayOf("None", "Kolkata", "Howrah"),
            "Chandigarh" to emptyDistricts,
            "Chattisgarh" to emptyDistricts,
            "Hariyana" to emptyDistricts,
            "Himachal Pradesh" to emptyDistricts,
            "Jammu & Kashmir" to emptyDistricts,
            "Jharkhand" to emptyDistricts,
            "Karnataka" to emptyDistricts,
            "Madhya Pradesh" to emptyDistricts,
            "Odisha" to emptyDistricts,
            "Pudu Cherry" to emptyDistricts,
            "Punjab" to emptyDistricts,
            "Tamil Nadu" to emptyDistricts,
            "Telangana" to emptyDistricts,
            "Uttarakhand" to emptyDistricts
        )

        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    binding.textAPMCWarning.text = "Please Select State and District"
                    binding.recycleAPMC.visibility = View.GONE
                    binding.textAPMCWarning.visibility = View.VISIBLE
                } else {
                    val districts = someMap!![states!![position]] ?: emptyDistricts
                    val districtAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, districts)
                    binding.spinner2.adapter = districtAdapter
                    indexSpinner1 = position
                }
            }
        }

        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    binding.textAPMCWarning.text = "Please Select District"
                    binding.recycleAPMC.visibility = View.GONE
                    binding.textAPMCWarning.visibility = View.VISIBLE
                } else {
                    val selectedDistrict = someMap!![states!![indexSpinner1!!]]!![position]
                    getApmc(selectedDistrict)
                    indexSpinner2 = position
                    binding.progressApmc.visibility = View.VISIBLE
                    binding.loadingTextAPMC.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getApmc(district: String) {
        val call = APMCApi.apmcInstances.getSomeData(district)
        call.enqueue(object : Callback<APMCMain> {
            override fun onResponse(call: Call<APMCMain>, response: Response<APMCMain>) {
                val data = response.body()
                if (data != null) {
                    val formattedDate = data.updated_date.take(10).split("-").reversed().joinToString("/")
                    binding.dateValueTextApmc.text = formattedDate

                    if (data.records.isEmpty()) {
                        binding.textAPMCWarning.text = "No records found!"
                        binding.textAPMCWarning.visibility = View.VISIBLE
                        binding.recycleAPMC.visibility = View.GONE
                    } else {
                        val customRecords = ArrayList<APMCCustomRecords>()
                        var prev = data.records[0]
                        val list1 = mutableListOf(prev.commodity)
                        val list2 = mutableListOf(prev.min_price)
                        val list3 = mutableListOf(prev.max_price)

                        var previousRecord = APMCCustomRecords(prev.state, prev.district, prev.market, list1, list2, list3)

                        for (i in 1 until data.records.size) {
                            val current = data.records[i]
                            if (current.market == previousRecord.market) {
                                previousRecord.commodity.add(current.commodity)
                                previousRecord.min_price.add(current.min_price)
                                previousRecord.max_price.add(current.max_price)
                            } else {
                                customRecords.add(previousRecord)
                                previousRecord = APMCCustomRecords(current.state, current.district, current.market,
                                    mutableListOf(current.commodity), mutableListOf(current.min_price), mutableListOf(current.max_price))
                            }
                        }
                        customRecords.add(previousRecord)

                        adapter = ApmcAdapter(requireContext(), customRecords)
                        binding.recycleAPMC.adapter = adapter
                        binding.recycleAPMC.layoutManager = LinearLayoutManager(requireContext())
                        binding.recycleAPMC.visibility = View.VISIBLE
                        binding.textAPMCWarning.visibility = View.GONE
                    }
                }
                binding.progressApmc.visibility = View.GONE
                binding.loadingTextAPMC.visibility = View.GONE
            }

            override fun onFailure(call: Call<APMCMain>, t: Throwable) {
                Log.e("APMC", "API call failed", t)
                binding.progressApmc.visibility = View.GONE
                binding.loadingTextAPMC.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
