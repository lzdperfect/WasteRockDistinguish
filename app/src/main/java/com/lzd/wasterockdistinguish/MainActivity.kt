package com.lzd.wasterockdistinguish

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lzd.network.utils.HttpUtils
import com.lzd.network.utils.NetWorkUtil
import com.lzd.wasterockdistinguish.R
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(),RadioGroup.OnCheckedChangeListener {
    private lateinit var iv:ImageView
    private lateinit var tv:TextView
    private lateinit var error:TextView
    private lateinit var totalTv:TextView
    private lateinit var coalTv:TextView
    private lateinit var rockTv:TextView
    private lateinit var rg:RadioGroup
    private lateinit var rb1:RadioButton
    private lateinit var rb2:RadioButton
    private lateinit var spinner: Spinner
    private var isThirdChoosed = true
    private var bitmapFile:Bitmap? = null
    private var name: String? = null
    private var picturePath:String? = null
    private lateinit var list:ArrayList<Map<String,Int>>
    private var startTime:Long? = 0
    private var showData = ""
    companion object{
        private const val REQUEST_GET_PICTURE_FROM_LIBRARY1 = 3
        private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE1 = 4
        private const val ROCK_65_fourth = "033025008108602521048021210690232165002821574020016110240089006180754047808260551068806760478049605810584120806941110057011560634191610781678091218040999076011240524083606470982121811701112106611681122"
        private const val ROCK_76_fourth = "031023008102401520932008209800118121003061172026811930291111604741082043811010459143207061366063414020668107809580722066809150825127812941212123812501263125412961054110611461193152414441338124614311345"
        private const val ROCK_65_third = "033025008362084349071356077550094525067537080297206251159275184229225159165194195403231370190385211639359459304601333253375175279216327406390371355389374"
        private const val ROCK_76_third = "031023008341051311027327039403102391089398097372158361146367153477235455211467223359319241223305275426431404413417421418432351369382398508481446415477448"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list = ArrayList()
        iv = findViewById(R.id.imageView)
        tv = findViewById(R.id.textView2)
        totalTv = findViewById(R.id.total)
        coalTv = findViewById(R.id.coal)
        rockTv = findViewById(R.id.rock)
        error = findViewById(R.id.textView3)
        rg = findViewById(R.id.rg)
        rb1 = findViewById(R.id.rb1)
        rb2 = findViewById(R.id.rb2)
        spinner = findViewById(R.id.spinner)
        totalTv.text = String.format(getString(R.string.total),0)
        coalTv.text = String.format(getString(R.string.coalnum),0)
        rockTv.text = String.format(getString(R.string.rocknum),0)
        tv.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE1
                )
            } else {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(
                    intent,
                    REQUEST_GET_PICTURE_FROM_LIBRARY1
                )
            }
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            if (name != null && bitmapFile !== null){
                commitPic()
            }else{
                Toast.makeText(this@MainActivity,"请选择要展示的图片",Toast.LENGTH_SHORT).show()
            }
        }
       rg.setOnCheckedChangeListener(this)

        var list = listOf("3位坐标","4位坐标")
        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                isThirdChoosed = position== 0
            }
        }
    }

    /**
     * 提交图片
     */
    private fun commitPic(){
        var bytes = getBytesByFile(picturePath!!)
        val fileBody: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), bytes)
        startTime = System.currentTimeMillis()
        Log.i("updatePic,StartTime:",getTime(startTime!!))
        ShowDialog.getInstance()!!.getDialog(this)
        var retrofit = HttpUtils.getInstance()?.getRetrofit(Config.PICBASEURL)
        var picInterface = retrofit?.create(PicInterface::class.java)
        NetWorkUtil.getInstance()!!.setSubscribe(picInterface!!.upLoadPicFile(name!!,fileBody),object :
            SingleObserver<PicBean> {
            override fun onSuccess(t: PicBean) {
                ShowDialog.getInstance()!!.dismissDialog()
                if (t.status_code == 200){
                    //view的显隐控制
                    error.visibility = View.INVISIBLE
                    iv.visibility = View.VISIBLE
                    val endTime = System.currentTimeMillis()
                    Log.i("updatePic,EndTime:",getTime(endTime))
                    Log.i("updatePic,UseTime:",(endTime-startTime!!).toString())
                    Log.i("updatePic,result:",t.coordinates)
                    val data= t.coordinates.replace(" ","");
                    drawRectangles(data,bitmapFile!!,name!!)
                }else{
                    //失败提示----view的显隐控制
                    error.visibility = View.VISIBLE
                    iv.visibility = View.INVISIBLE
                    error.text = t.error
                }
            }

            override fun onSubscribe(d: Disposable) {
                Log.i("updatePic,result:","")
            }

            override fun onError(e: Throwable) {
                //失败提示----view的显隐控制
                ShowDialog.getInstance()!!.dismissDialog()
                error.visibility = View.VISIBLE
                iv.visibility = View.INVISIBLE
                error.text = e.message
            }

        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE1) { //读写内存卡成功
            if (grantResults.isNotEmpty() && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) { //选取照片
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(
                    intent,
                    REQUEST_GET_PICTURE_FROM_LIBRARY1
                )
            } else {
                Toast.makeText(this, "READ EXTERNAL STORAGE PERMISSION DENIED", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_GET_PICTURE_FROM_LIBRARY1 ->{
                if (data == null || resultCode != RESULT_OK) {
                    Toast.makeText(this, "选取失败请重试", Toast.LENGTH_SHORT).show()
                    return
                }
                val selectedImage = data.data
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(
                    selectedImage!!,
                    filePathColumn, null, null, null
                )
                if (cursor != null) {
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    picturePath = cursor.getString(columnIndex)
                    var nameList = picturePath!!.split("/")
                    name = nameList[nameList.size-1]
                    tv.text = name
                    cursor.close()
                    bitmapFile = BitmapFactory.decodeFile(picturePath)
//                    showData = if (isThirdChoosed){
//                        ROCK_65_third
//                    }else{
//                        ROCK_65_fourth
//                    }
//                    drawRectangles(showData,bitmapFile!!,name!!)
                }else{
                    Toast.makeText(this,"选取失败",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 绘制矩形
     */
    private fun drawRectangles(data:String,imageBitmap: Bitmap, name:String) {
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0
        var mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        var canvas: Canvas = Canvas(mutableBitmap);
        var paint = Paint();
        //解析data
        getData(data,mutableBitmap.width,mutableBitmap.height)
        for (i in 0 until list.size) {
            left = list[i]["left"] ?: error("")
            top = list[i]["top"] ?: error("")
            right = list[i]["right"] ?: error("")
            bottom = list[i]["bottom"] ?: error("")
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE //不填充
            paint.strokeWidth = 8f
            //线的宽度

            var rect = Rect(left,top,right,bottom);
            canvas.drawRect(rect, paint)
        }
        var finalBitmap = if (mutableBitmap.width > mutableBitmap.height){
             createPhotos(mutableBitmap)
        }else{
            mutableBitmap
        }
        iv.setImageBitmap(finalBitmap);//img: 定义在xml布局中的ImagView控件
    }

    /**
     * 判断请求的返回值坐标点位数
     */
    private fun getData(data:String,width:Int,height:Int){
        if (isThirdChoosed){
            getThirdData(data,width,height)
        }else{
            getFourthData(data,width,height)
        }
    }

    /**
     * 返回4位坐标系数据源算法
     */
    private fun getFourthData(data:String,width:Int,height:Int){
        list.clear()
        var total = data.substring(0,3).toInt()
        var coal = data.substring(3,6).toInt()
        var rock = data.substring(6,9).toInt()
        totalTv.text = String.format(getString(R.string.total),total)
        coalTv.text = String.format(getString(R.string.coalnum),coal)
        rockTv.text = String.format(getString(R.string.rocknum),rock)
        var length = rock.toInt()
        for (i in 0 until length){
            var map = HashMap<String,Int>()
            var left = data.substring(9+(i*24),13+(i*24)).toInt()
            var top = data.substring(13+(i*24),17+(i*24)).toInt()
            var right = data.substring(17+(i*24),21+(i*24)).toInt()
            var bottom = data.substring(21+(i*24),25+(i*24)).toInt()
            map["left"] = width-left
            map["top"] =  height-top
            map["right"] = width-right
            map["bottom"] = height-bottom
            list.add(map)
        }
    }
    /**
     * 返回3位坐标系数据源算法
     */
    private fun getThirdData(data:String,width:Int,height:Int){
        list.clear()
        var total = data.substring(0,3).toInt()
        var coal = data.substring(3,6).toInt()
        var rock = data.substring(6,9).toInt()
        totalTv.text = String.format(getString(R.string.total),total)
        coalTv.text = String.format(getString(R.string.coalnum),coal)
        rockTv.text = String.format(getString(R.string.rocknum),rock)
        var length = rock.toInt()
        for (i in 0 until length){
            var map = HashMap<String,Int>()
            var left = data.substring(9+(i*18),12+(i*18)).toInt()
            var top = data.substring(12+(i*18),15+(i*18)).toInt()
            var right = data.substring(15+(i*18),18+(i*18)).toInt()
            var bottom = data.substring(18+(i*18),21+(i*18)).toInt()
            map["left"] = width - (left * 3)
            map["top"] =  height - (top * 3)
            map["right"] = width - (right * 3)
            map["bottom"] = height - (bottom * 3)
            list.add(map)
        }
    }

    /**
     * bitmap旋转90度
     */
    private fun createPhotos(bitmap: Bitmap): Bitmap {
        var bitmap = bitmap
        val m = Matrix()
        try {
            m.setRotate(
                90f,
                (bitmap.width / 2).toFloat(),
                (bitmap.height / 2).toFloat()
            ) //90就是我们需要选择的90度
            val bmp2 = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                m,
                true
            )
            bitmap.recycle()
            bitmap = bmp2
        } catch (ex: Exception) {
            print("创建图片失败！$ex")
        }
        return bitmap
    }

    /**
     * file转byte
     */
    private fun getBytesByFile(filePath: String): ByteArray? {
        try {
            val file = File(filePath)
            //获取输入流
            val fis = FileInputStream(file)
            //新的 byte 数组输出流，缓冲区容量1024byte
            val bos = ByteArrayOutputStream(1024)
            //缓存
            val b = ByteArray(1024)
            var n: Int
            while (fis.read(b).also { n = it } != -1) {
                bos.write(b, 0, n)
            }
            fis.close()
            //改变为byte[]
            val data: ByteArray = bos.toByteArray()
            //
            bos.close()
            return data
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取当前系统时间
     */
    private fun getTime(time:Long):String{
        var simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.CHINA);// HH:mm:ss
        //获取当前时间
        var date = Date(time)
        return simpleDateFormat.format(date)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            R.id.rb1 ->{
                isThirdChoosed = true
                showData =
                    ROCK_65_third

            }
            R.id.rb2 ->{
                isThirdChoosed = false
                showData =
                    ROCK_65_fourth
            }
        }
        if (name != null && bitmapFile !== null){
            drawRectangles(showData,bitmapFile!!,name!!)
        }
    }
}
