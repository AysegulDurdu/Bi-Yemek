package com.aysegul.biyemek.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aysegul.biyemek.R
import com.aysegul.biyemek.adapter.YemekAdapter
import com.aysegul.biyemek.model.Yemekler
import com.squareup.picasso.Picasso
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(),SearchView.OnQueryTextListener {

    private lateinit var yemekListe:ArrayList<Yemekler>
    private lateinit var adapter: YemekAdapter


    //carouselView
    val images = arrayOf(
            "http://kasimadalan.pe.hu/yemekler/resimler/makarna.png",
            "http://kasimadalan.pe.hu/yemekler/resimler/izgarasomon.png",
            "http://kasimadalan.pe.hu/yemekler/resimler/izgaratavuk.png",
            "http://kasimadalan.pe.hu/yemekler/resimler/pizza.png"
    )
    val imageListener = ImageListener{position, imageView ->
        Picasso.get().load(images[position]).into(imageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //carouselView
        carouselView.pageCount = images.size
        carouselView.setImageListener(imageListener)

        //toolbar
        toolbarMainAktivity.setTitle("Gerçek Lezzetleri Keşfet")
        setSupportActionBar(toolbarMainAktivity)

        //recycler
        yemekRv.setHasFixedSize(true)
        yemekRv.layoutManager = LinearLayoutManager(this)

        tumYemekler()

    }

    override fun onBackPressed() {
        val yeniIntent = Intent(Intent.ACTION_MAIN)
        yeniIntent.addCategory(Intent.CATEGORY_HOME)
        yeniIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(yeniIntent)
    }

    //Menu Bağlama
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.arama_menu,menu)
        val item = menu.findItem(R.id.action_ara)
        val searchView = item.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_sepet -> {
                startActivity(Intent(this@MainActivity, SepetActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        Log.e("Gönderilen arama sonucu",query)
        yemekAra(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        Log.e("Harf girildikce arama",newText)
        yemekAra(newText)
        return true
    }

    //TümYemekler
    fun tumYemekler(){
        val url = "http://kasimadalan.pe.hu/yemekler/tum_yemekler.php"

        val istek= StringRequest(Request.Method.GET,url, Response.Listener { cevap ->
            // Log.e("Veri Okuma",cevap)
            try {

                yemekListe = ArrayList()

                val jsonObj = JSONObject(cevap)
                val yemekler = jsonObj.getJSONArray("yemekler")

                for (i in 0 until yemekler.length()){

                    val y = yemekler.getJSONObject(i)

                    val yemek_id = y.getInt("yemek_id")
                    val yemek_adi = y.getString("yemek_adi")
                    val yemek_resim_adi = y.getString("yemek_resim_adi")
                    val yemek_fiyat = y.getInt("yemek_fiyat")

                    val yemek= Yemekler(yemek_id,yemek_adi,yemek_resim_adi,yemek_fiyat)

                    yemekListe.add(yemek)

                }
                adapter = YemekAdapter(this,yemekListe)
                yemekRv.adapter = adapter

            }catch (e: JSONException){
                e.printStackTrace()
            }
        }, Response.ErrorListener { Log.e("Hata","Veri Okuma") })

        Volley.newRequestQueue(this).add(istek)
    }

    //Yemek Ara
    fun yemekAra(aramaKelime:String){
        val url = "http://kasimadalan.pe.hu/yemekler/tum_yemekler_arama.php"

        val istek= object: StringRequest(Request.Method.POST,url,Response.Listener { cevap ->
            //Log.e("Veri Okuma",cevap)

            try {

                yemekListe = ArrayList()

                val jsonObj = JSONObject(cevap)
                val yemekler = jsonObj.getJSONArray("yemekler")
                for (i in 0 until yemekler.length()){
                    val y = yemekler.getJSONObject(i)

                    val yemek_id = y.getInt("yemek_id")
                    val yemek_adi = y.getString("yemek_adi")
                    val yemek_resim_adi = y.getString("yemek_resim_adi")
                    val yemek_fiyat = y.getInt("yemek_fiyat")

                    val yemek= Yemekler(yemek_id,yemek_adi,yemek_resim_adi,yemek_fiyat)

                    yemekListe.add(yemek)

                }
                adapter = YemekAdapter(this,yemekListe)
                yemekRv.adapter = adapter

            }catch (e: JSONException){
                e.printStackTrace()
            }
        },Response.ErrorListener { Log.e("Hata","Veri Okuma") }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String,String>()
                params["yemek_adi"] = aramaKelime
                return params
            }
        }
        Volley.newRequestQueue(this).add(istek)
    }
}