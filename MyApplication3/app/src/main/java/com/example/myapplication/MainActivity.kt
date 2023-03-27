package com.example.myapplication
import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var timeSelected:Int=0
    private var timeCountDown:CountDownTimer?=null
    private var timeProgress:Int=0
    private var pauseOffSet:Long=0
    private var isStart=true
    private var safe :Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            setTime()
            safe=timeSelected
        }
        binding.btnPlayPause.setOnClickListener {
            starttimersetup()
        }
        binding.ibReset.setOnClickListener {
            resettime()
        }
        binding.tvAddTime.setOnClickListener {
            addextratime()
        }
    }


    private fun addextratime(){
        //val progressbar=binding.root.findViewById<ProgressBar>(R.id.pbTimer)
        if (timeSelected!=0){
            timeSelected+=10
            timeProgress+=10
            safe+=10
            //progressbar.max=timeSelected
            timepause()
            starttimer(pauseOffSet)
            Toast.makeText(this,"增加10秒了",Toast.LENGTH_SHORT).show()

        }
    }

    private fun resettime() {
        if (timeCountDown != null) {
            timeCountDown!!.cancel()
            timeProgress=0
            timeSelected=0
            pauseOffSet=0
            timeCountDown=null
            val startbtn=binding.root.findViewById<Button>(R.id.btnPlayPause)
            startbtn.text="開始"
            isStart=true
            //val progressbar=binding.root.findViewById<ProgressBar>(R.id.pbTimer)
            //progressbar.progress=0
            val timelefttv=binding.root.findViewById<TextView>(R.id.tv_Timeleft)
            timelefttv.text= "0"


        }
    }

    private fun timepause(){
        if (timeCountDown!=null)
        {
            timeCountDown!!.cancel()
        }


    }

    private fun starttimersetup(){
        val startbtn=binding.root.findViewById<Button>(R.id.btnPlayPause)
        if (timeSelected>timeProgress){
            if (isStart){
                startbtn.text="暫停"
                starttimer(pauseOffSet)
                isStart=false
            }
            else{
                isStart=true
                startbtn.text="繼續"
                timepause()
            }
        }
        else{
            Toast.makeText(this,"請輸入時間長短",Toast.LENGTH_SHORT).show()
        }

    }
    fun getSoundTitleAndUri(context: Context?, type: Int): Map<String, String>? {
        val manager = RingtoneManager(context)
        manager.setType(type)
        val cursor: Cursor = manager.cursor
        val titleAndUri: MutableMap<String, String> = HashMap()
        while (cursor.moveToNext()) {
            val title: String = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val id: String = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
            val uri: String = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
            titleAndUri[title] = "$uri/$id"
        }
        return titleAndUri
    }
    private fun starttimer(pauseOffSetL:Long){
        //val progressbar=binding.root.findViewById<ProgressBar>(R.id.pbTimer)
        //progressbar.progress=timeProgress
        var timeInMilliSeconds =(timeSelected)*1000.toLong()-pauseOffSetL*1000
        timeCountDown=object :CountDownTimer(
            timeInMilliSeconds,1000)
        {
            override fun onTick(p0: Long) {
                timeProgress++
                pauseOffSet=timeSelected.toLong()-p0/1000
                //progressbar.progress=timeSelected-timeProgress
                val timelefttv=binding.root.findViewById<TextView>(R.id.tv_Timeleft)
                timelefttv.text= (timeSelected-timeProgress).toString()
                if (timeProgress%safe==0){
                    println(timeSelected)
                    val mRingtone = RingtoneManager.getRingtone(this@MainActivity,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL));
                    mRingtone.play();

                    //mRingtone.stop()
                }
            }

            override fun onFinish() {
                if (timeSelected!=0) {
                    println(timeSelected)
                    timeSelected+=safe
                    //progressbar.max = timeSelected
                    timepause()
                    //this.start()
                    starttimer(pauseOffSet)
                    //Toast.makeText(this, "增加10秒了", Toast.LENGTH_SHORT).show()
                }
                //val mRingtone = RingtoneManager.getRingtone(this@MainActivity,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                //mRingtone.play();
                //this.cancel()
                //timeInMilliSeconds=save*1000.toLong()-pauseOffSetL*1000
                //this.start()
                //resettime()
                //Toast.makeText(this@MainActivity,"時間到!!",Toast.LENGTH_SHORT).show()
            }

        }.start()


    }

    private fun setTime(){
        val timedialog=Dialog(this)
        timedialog.setContentView(R.layout.add_dialog)
        val timeset=timedialog.findViewById<EditText>(R.id.etGetTime)
        val timelefttv=binding.root.findViewById<TextView>(R.id.tv_Timeleft)
        val btnstart=binding.root.findViewById<Button>(R.id.btnPlayPause)
        //val progressbar=binding.root.findViewById<ProgressBar>(R.id.pbTimer)
        timedialog.findViewById<Button>(R.id.btnOk).setOnClickListener {
            if ( timeset.text.isEmpty()){
                Toast.makeText(this,"請輸入時間長短",Toast.LENGTH_SHORT).show()
            }
            else{
                resettime()
                timelefttv.text=timeset.text
                btnstart.text="開始"
                timeSelected=timeset.text.toString().toInt()
                safe=timeSelected
                //progressbar.max=timeSelected
            }
            timedialog.dismiss()
        }
        timedialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timeCountDown!=null){
            timeCountDown?.cancel()
            timeProgress=0
        }
    }
}