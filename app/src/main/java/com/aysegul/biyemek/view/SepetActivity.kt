package com.aysegul.biyemek.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aysegul.biyemek.R
import com.aysegul.biyemek.adapter.SepetAdapter
import com.aysegul.biyemek.model.Sepet
import kotlinx.android.synthetic.main.activity_detay.*
import kotlinx.android.synthetic.main.activity_sepet.*
import kotlinx.android.synthetic.main.sepetim_listesi_tasarim.*
import org.json.JSONException
import org.json.JSONObject

class SepetActivity : AppCompatActivity() {

    private lateinit var sepetListe:ArrayList<Sepet>
    private lateinit var adapter: SepetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sepet)

        //toolbar
        toolbarSepetActivity.setTitle("Sepetim")
        setSupportActionBar(toolbarSepetActivity)

        sepetRv.setHasFixedSize(true)
        sepetRv.layoutManager = LinearLayoutManager(this)

        sepetYemekAlma()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_home -> {
                startActivity(Intent(this@SepetActivity, MainActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun sepetYemekAlma(){
        val url = "http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"

        val istek= StringRequest(Request.Method.GET,url, Response.Listener { cevap ->
           // Log.e("Veri Okuma",cevap)

            try {

                sepetListe = ArrayList()

                val jsonObj = JSONObject(cevap)
                val sepet_yemekler = jsonObj.getJSONArray("sepet_yemekler")
                for (i in 0 until sepet_yemekler.length()){
                    val s = sepet_yemekler.getJSONObject(i)

                    val yemek_id = s.getInt("yemek_id")
                    val yemek_adi = s.getString("yemek_adi")
                    val yemek_resim_adi = s.getString("yemek_resim_adi")
                    val yemek_fiyat = s.getInt("yemek_fiyat")
                    val yemek_siparis_adet = s.getInt("yemek_siparis_adet")

                    val sepet= Sepet(yemek_id,yemek_adi,yemek_resim_adi,yemek_fiyat,yemek_siparis_adet)

                    sepetListe.add(sepet)

                }
                adapter = SepetAdapter(this,sepetListe)

                sepetRv.adapter = adapter
                if(sepetListe.size == 0){
                    sepetRv.visibility = View.GONE
                    textSepet.visibility = View.VISIBLE

                }else{
                    sepetRv.visibility = View.VISIBLE
                    textSepet.visibility = View.GONE
                }

            }catch (e: JSONException){
                e.printStackTrace()
            }
        }, Response.ErrorListener { Log.e("Hata","Veri Okuma") })

        Volley.newRequestQueue(this).add(istek)
    }

}