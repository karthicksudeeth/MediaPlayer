package com.example.musicplayer

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
     var startTime=0.0
    var finalTime=0.0
    val frwdTime=10000
    val backwardTime=10000
    private val REQUEST_CODE_PICK_AUDIO=101;
    private var songuri: Uri?=null

    var handler= Handler()
    var mediaPlayer=MediaPlayer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val time:TextView=findViewById(R.id.tvTime)
        val seekbar:SeekBar=findViewById(R.id.seekBar)
        val btnPlay:Button=findViewById(R.id.btnPlay)
        val btnPause:Button=findViewById(R.id.btnPause)
        val btnfrwd:Button=findViewById(R.id.btnFrwd)
        val btnBack:Button=findViewById(R.id.btnBack)
        val btnSelectSong:Button=findViewById(R.id.btnSelectSong)

        btnSelectSong.setOnClickListener {

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "audio/*" // Filter for audio files
                startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO)
        }

        mediaPlayer=MediaPlayer.create(this,R.raw.song)

        btnPlay.setOnClickListener {
            mediaPlayer.start()

            finalTime= mediaPlayer.duration.toDouble()
            startTime=mediaPlayer.currentPosition.toDouble()

            seekbar.max=finalTime.toInt()
            seekbar.isClickable=false

            time.text=startTime.toString()
            println(mediaPlayer.currentPosition)
            seekbar.setProgress(startTime.toInt())


            handler.postDelayed(UpdateSongTime,100)
        }

//        title.text=""+resources.getIdentifier("song","raw",packageName)

        btnPause.setOnClickListener {
            mediaPlayer.pause()
        }

        btnfrwd.setOnClickListener {
            var temp=startTime
            if((temp+frwdTime)<=finalTime){
                startTime=startTime+frwdTime
                mediaPlayer.seekTo(startTime.toInt())
            }
            else{
                Toast.makeText(this,"Can't Jump forward",Toast.LENGTH_LONG).show()
            }


        }

        btnBack.setOnClickListener {
            var temp=startTime
            if((temp-backwardTime) >0){
                startTime=startTime-backwardTime
                mediaPlayer.seekTo(startTime.toInt())
            }
            else{
                Toast.makeText(this,"Can't jump backwards",Toast.LENGTH_LONG).show()
            }
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_AUDIO) {
            songuri = data?.data
            songuri?.let { uri ->
                mediaPlayer?.release()

                mediaPlayer = MediaPlayer.create(this, uri)
            }
            val title:TextView=findViewById(R.id.textView2)
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this, songuri)
            title.text = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)




        }
    }


    val UpdateSongTime:Runnable=object:Runnable{
        override fun run() {
            startTime=mediaPlayer.currentPosition.toDouble()
            finalTime=mediaPlayer.duration.toDouble()

            val time:TextView=findViewById(R.id.tvTime)
            time.text= String.format(
                "%d min , %d sec",
                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),

                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong())
                            - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            startTime.toLong()
                        )
                    ))

            val seekbar:SeekBar=findViewById(R.id.seekBar)
            seekbar.setProgress(startTime.toInt())

            handler.postDelayed(this, 100)

        }

    }
}