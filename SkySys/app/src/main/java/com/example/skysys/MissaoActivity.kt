package com.example.skysys

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.skysys.dataClass.missao
import com.example.skysys.databinding.ActivityMissionBinding
import com.example.skysys.utils.realmIOSky

class MissaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMissionBinding

    var tempoVoo = 5

    fun enviar(mis : missao)
    {
        val bd = realmIOSky()

        bd.create(mis)
        bd.close()
    }
    
    fun initParametros(distancia: Double) {
        val segundos: Int = (distancia / 10).toInt()
        val minutos: Int = (segundos / 60).toInt()
        binding.TVoo.text = minutos.toString() + " m " + (segundos % 60).toString() + " s"
        binding.percorrer.text = String.format("%.2f", distancia)
        binding.Interesse.text = "0.5 ha"
        binding.tVAngulo.text = "150 graus"
        binding.tVAlturaDecolagem.text = "5 m"
        binding.tVAlturaVoo.text = "10 m"
        binding.DistanciaLinhas.text = "8 m"
        binding.litrosHec.text = "15 l"
        binding.tVVelocidade.text = "5.0 m/s"
    }

    fun mudarSeekBar(seekBar: SeekBar, textView: TextView) {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    tempoVoo = p1
                    val regex = textView.text.split(" ")
                    textView.text = p1.toString() + " " + regex[1]
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission)
        supportActionBar?.hide()
        binding = ActivityMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val distance = intent.getDoubleExtra("distancia", 0.0)

        initParametros(distance)
        with(binding) {
            mudarSeekBar(seekBarAngulo, tVAngulo)
            mudarSeekBar(seekBarAlturaDecolagem, tVAlturaDecolagem)
            mudarSeekBar(seekBarAlturaVoo, tVAlturaVoo)
            mudarSeekBar(seekBarDistanciaLinhas, DistanciaLinhas)
            mudarSeekBar(seekBarLitrosHec, litrosHec)
            mudarSeekBar(seekBarVelocidade, tVVelocidade)
            val segundos: Int = (distance / tempoVoo).toInt()
            val minutos: Int = (segundos / 60).toInt()
            binding.TVoo.text = minutos.toString() + " m " + (segundos % 60).toString() + " s"
        }

        binding.btEnviar.setOnClickListener{

            val mis = missao()
            mis.altura_Decolagem = binding.tVAlturaDecolagem.text.toString()
            mis.altura_Voo = binding.tVAlturaVoo.text.toString()
            mis.distancia = binding.DistanciaLinhas.text.toString()
            mis.velocidade = binding.tVVelocidade.text.toString()
            mis.tempo = binding.tVTempoVoo.text.toString()
            mis.altura_RTH = binding.litrosHec.text.toString()
            mis.nome = "Matheus222"

            enviar(mis)
            this.finish()
        }
    }
}