package com.enu.postsystem.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.enu.postsystem.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null


    val city_list = arrayOf("Тараз", "Шымкент", "Қызылорда", "Арал", "Қордай", "Мерке", "Шу")
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val materialDateBuilder: MaterialDatePicker.Builder<*> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTitleText("SELECT A DATE")
        val materialDatePicker = materialDateBuilder.build()

        binding.inputDate.setOnClickListener {
            materialDatePicker.show(
                parentFragmentManager,
                "MATERIAL_DATE_PICKER"
            )
        }

        materialDatePicker.addOnPositiveButtonClickListener {
            binding.inputDate.text = "Изменить дату"
            binding.showDate.setVisibility(View.VISIBLE)
            binding.showDate.setText(materialDatePicker.headerText)
        }
        if (binding.spinner != null){
            val adapter = context?.let { ArrayAdapter<String>(it, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, city_list) }
            binding.spinner.adapter = adapter
        }
        binding.btnPrice.setOnClickListener(View.OnClickListener {
            setPrice()
        })

        binding.btnDelivery.setOnClickListener(View.OnClickListener {
            val nameTovar: String = java.lang.String.valueOf(binding.tovarName.getText())
            val dateConf = java.lang.String.valueOf(binding.showDate.text)
            val phone: String = java.lang.String.valueOf(binding.phoneNumber.getText())
            val vesTovara: String = java.lang.String.valueOf(binding.tovarKG.getText())
            val textDelivery = java.lang.String.valueOf(binding.inputTextConf.text)
            val city:String = binding.spinner.selectedItem.toString()
            if (nameTovar == "") {
                binding.tovarName.setError("Введите название товара!")
            } else if (dateConf == "") {
                binding.inputDate.error = "Выберите дату!"
            } else if (vesTovara == "") {
                binding.tovarKG.setError("Введите все товара!")
            } else if (phone == "") {
                binding.phoneNumber.setError("Введите номер телефона!")
            } else if (textDelivery == "") {
                binding.inputTextConf.setError("Введите описание товара!")
            }  else {

                val price = getprice(vesTovera = vesTovara.toInt(), city)
                addDateFireStore(nameTovar, dateConf, phone, vesTovara, city, price, textDelivery)
            }
        })

        return root
    }

    fun setPrice() {
        val vesTovara: String = java.lang.String.valueOf(binding.tovarKG.getText())
        val city:String = binding.spinner.selectedItem.toString()
        if (vesTovara == "") {
            binding.tovarKG.setError("Введите все товара!")
        } else{
            val price = getprice(vesTovera = vesTovara.toInt(), city)
            binding.priceTxt.setText(price.toString() + " kzt")
        }
    }

    fun  getprice(vesTovera:Int, city:String):Int{
        var i = 1
        if (binding.spinner.selectedItem.toString()=="Тараз"){
            i = 4
        }else if (binding.spinner.selectedItem.toString()=="Қордай"){
            i =2
        }else if (binding.spinner.selectedItem.toString()=="Мерке"){
            i =3
        } else if (binding.spinner.selectedItem.toString()=="Шу"){
            i =3
        }else if (binding.spinner.selectedItem.toString()=="Шымкент"){
            i =6
        }else if (binding.spinner.selectedItem.toString()=="Қызылорда"){
            i =9
        }else if (binding.spinner.selectedItem.toString()=="Арал"){
            i =11
        }
        return (vesTovera * i)*100
    }

    fun addDateFireStore(
        name: String?,
        dateConf: String?,
        phone: String?,
        vesTovera: String,
        city: String?,
        price: Int,
        textDelivery: String
    ) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val dbConference: CollectionReference = db.collection("AtuPost")
        val data: DeliveryData =
            DeliveryData(name, dateConf, phone, vesTovera, city, price, textDelivery)
        dbConference.add(data).addOnSuccessListener(object : OnSuccessListener<DocumentReference?> {
            override fun onSuccess(documentReference: DocumentReference?) {

                binding.tovarName.setText("")
                binding.showDate.setText("")
                binding.inputTextConf.setText("")
                binding.tovarKG.setText("")
                binding.priceTxt.setText("0 kzt")
                binding.inputTextConf.setText("")
                binding.phoneNumber.setText("")

            }
        }).addOnFailureListener(object : OnFailureListener {
            override fun onFailure(e: Exception) {
                Toast.makeText(context, "Fail to add course "+e, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Toast.makeText(context,"You Selected:\n "+p0?.getItemAtPosition(p2),Toast.LENGTH_SHORT).show()

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}

