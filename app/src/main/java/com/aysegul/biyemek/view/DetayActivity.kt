package com.aysegul.biyemek.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aysegul.biyemek.R
import com.aysegul.biyemek.model.Yemekler
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detay.*

class DetayActivity : AppCompatActivity() {

    var adet = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detay)

        val yemek = intent.getSerializableExtra("yemekNesne") as Yemekler
        adetText.text = "${adet} Adet"
        textUrunAd.text = yemek.yemek_adi
        textUrunFiyat.text = "${(yemek.yemek_fiyat)}₺"

        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${yemek.yemek_resim_adi}"
        Picasso.get().load(url).into(imageYemekDetay)

        toolbarDetayActivity.title = "Yemek Detay"
        setSupportActionBar(toolbarDetayActivity)

        minusImage.setOnClickListener {
            if(adet>1){
                adet--
                adetText.text = "${adet} Adet"
                textUrunFiyat.text = "${ adet * yemek.yemek_fiyat}₺"
            }
        }
        plusImage.setOnClickListener {
            adet++
            adetText.text = "${adet} Adet"
            textUrunFiyat.text = "${ adet * yemek.yemek_fiyat}₺"

        }

        buttonSepetEkle.setOnClickListener {
            sepetEkle(yemek.yemek_id,yemek.yemek_adi,yemek.yemek_resim_adi,yemek.yemek_fiyat,adet)
            finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu)
        menuInflater.inflate(R.menu.sepet_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_home -> {
                startActivity(Intent(this@DetayActivity, MainActivity::class.java))
                return true
            }
            R.id.action_sepet -> {
                startActivity(Intent(this@DetayActivity, SepetActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //Sepet Ekle
    fun sepetEkle(yemek_id:Int,yemek_adi:String,yemek_resim_adi:String,yemek_fiyat:Int,yemek_siparis_adet:Int){

        val url = "http://kasimadalan.pe.hu/yemekler/insert_sepet_yemek.php"

        val istek = object: StringRequest(Request.Method.POST,url, Response.Listener { cevap ->
            Log.e("Ekle Cevap",cevap)

            startActivity(Intent(this@DetayActivity, SepetActivity::class.java))

        }, Response.ErrorListener { Log.e("Ekle Cevap","hata") }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String,String>()
                params["yemek_id"] = yemek_id.toString()
                params["yemek_adi"]=yemek_adi
                params["yemek_resim_adi"]=yemek_resim_adi
                params["yemek_fiyat"]=yemek_fiyat.toString()
                params["yemek_siparis_adet"]=yemek_siparis_adet.toString()
                return params
            }
        }
        Volley.newRequestQueue(this).add(istek)
    }

}