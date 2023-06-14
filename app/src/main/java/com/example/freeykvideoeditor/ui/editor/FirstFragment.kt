package com.example.freeykvideoeditor.ui.editor

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.navigation.fragment.findNavController
import com.example.freeykvideoeditor.MainActivity
import com.example.freeykvideoeditor.R
import com.example.freeykvideoeditor.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                val imagePaths = arrayListOf<Uri>()
                val outputVideoPath = "path/to/output.mp4"
                val musicPath = "path/to/music.mp3"
                Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
                uris.forEach {
                    imagePaths.addAll(listOf(it))
                }
                val ffmpegCommand =
                    "ffmpeg -y -loop 1 -framerate 1 -t ${imagePaths.size} -i %s -i $musicPath -vf \"scale='min(iw, ih)':'min(iw, ih)':force_original_aspect_ratio=decrease,pad=ceil(iw/2)*2:ceil(ih/2)*2\" -c:v libx264 -pix_fmt yuv420p -shortest -strict -2 -c:a aac -b:a 192k -r 30 -shortest $outputVideoPath"
                Log.d("PhotoPicker", "Number of items selected: $imagePaths")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        (activity as MainActivity).testing()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addResources.setOnClickListener {
            if (isPhotoPickerAvailable()) {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}