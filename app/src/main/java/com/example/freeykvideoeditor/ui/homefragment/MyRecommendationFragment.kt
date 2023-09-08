package com.example.freeykvideoeditor.ui.homefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.freeykvideoeditor.databinding.FragmentMyRecommantionBinding
import com.example.freeykvideoeditor.ui.ffmpeg.tools.LogsDbHelper


class MyRecommendationFragment : Fragment() {
    private var _binding:FragmentMyRecommantionBinding?=null
    private val binding by lazy { _binding!! }
    private val logsDbHelper by lazy { LogsDbHelper(requireContext().applicationContext) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyRecommantionBinding.inflate(inflater,container,false)
        binding.simpleText.text = logsDbHelper.getLogs().toString()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}