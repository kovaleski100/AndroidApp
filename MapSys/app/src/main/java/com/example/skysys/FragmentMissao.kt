package com.example.skysys

import android.app.Notification.EXTRA_TEXT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.skysys.databinding.ActivityMissionBinding
import com.example.skysys.databinding.SecondfragmentBinding

private val EXTRA_TEXT = "texto"
private val EXTRA_BG_COLOR = "corBg"
private val EXTRA_TEXT_COLOR = "corTexto"

class FragmentMissao: Fragment() {

    private var _binding: SecondfragmentBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SecondfragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

    }
}

fun newInstace(
    text:String, background: String, textColor: String):FragmentMissao
{
    val params = Bundle()
    params.putString(EXTRA_TEXT, text)
    params.putString(EXTRA_BG_COLOR, background)
    params.putString(EXTRA_TEXT_COLOR, textColor)

    val slf = FragmentMissao()
    slf.arguments = params

    return slf
}