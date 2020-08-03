package com.kishorejethava.cameraxcropping.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.kishorejethava.cameraxcropping.R
import kotlinx.android.synthetic.main.fragment_select.*

class SelectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_select, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCircular.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                .navigate(SelectFragmentDirections.selectToPermission())

        }
        btnSquare.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                .navigate(SelectFragmentDirections.selectToPermission())

        }
        btnRectangle.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                .navigate(SelectFragmentDirections.selectToPermission())

        }
    }
}