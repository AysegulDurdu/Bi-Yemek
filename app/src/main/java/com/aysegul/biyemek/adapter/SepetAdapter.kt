package com.aysegul.biyemek.adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aysegul.biyemek.R
import com.aysegul.biyemek.model.Sepet
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class SepetAdapter(private var mContext: Context, private var sepetListe:ArrayList<Sepet>)
    :RecyclerView.Adapter<SepetAdapter.CardTasarimTutucu>() {

    inner class CardTasarimTutucu(tasarim: View): RecyclerView.ViewHolder(tasarim){
        var textYemekAd: TextView
        var deleteYemek: ImageView
        var yemekResim: ImageView
        var minusImage : ImageView
        var adetText:TextView
        var plusImage:ImageView
        var textUrunFiyat:TextView

        init {
            textYemekAd = tasarim.findViewById(R.id.textYemekAd)
            deleteYemek = tasarim.findViewById(R.id.deleteYemek)
            yemekResim = tasarim.findViewById(R.id.yemekResim)
            minusImage = tasarim.findViewById(R.id.minusImage)
            adetText = tasarim.findViewById(R.id.adetText)
            plusImage = tasarim.findViewById(R.id.plusImage)
            textUrunFiyat = tasarim.findViewById(R.id.textUrunFiyat)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.sepetim_listesi_tasarim,parent,false)
        return CardTasarimTutucu(tasarim)
    }

    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {
        val sepet = sepetListe.get(position)
        holder.textYemekAd.text = "${sepet.yemek_adi}"
        holder.textUrunFiyat.text = "${sepet.yemek_fiyat}₺"

        var adet = sepet.yemek_siparis_adet
        holder.adetText.text = "${adet} Adet"
        holder.minusImage.setOnClickListener {
            if(adet>1){
                adet--
                holder.adetText.text = "${adet} Adet"
            }
        }
        holder.plusImage.setOnClickListener {
            adet++
            holder.adetText.text = "${adet} Adet"
        }

        holder.deleteYemek.setOnClickListener { view ->
            Snackbar.make(view,"${holder.textYemekAd.text} silinsin mi?", Snackbar.LENGTH_LONG)
                    .setAction("Evet"){
                        //Silme
                        val url = "http://kasimadalan.pe.hu/yemekler/delete_sepet_yemek.php"
                        val istek = object: StringRequest(Request.Method.POST,url, Response.Listener { cevap ->
                            Log.e("Sil Cevap",cevap)
                            sepetYemekAlma()
                        },Response.ErrorListener { Log.e("Sil","hata") }){
                            override fun getParams(): MutableMap<String, String> {
                                val params = HashMap<String,String>()
                                params["yemek_id"] = (sepet.yemek_id).toString()
                                return params
                            }
                        }
                        Volley.newRequestQueue(mContext).add(istek)

                        Snackbar.make(view,"${holder.textYemekAd.text} silindi", Snackbar.LENGTH_LONG).show()
                    }.show()

        }
        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${sepet.yemek_resim_adi}"
        Picasso.get().load(url).into(holder.yemekResim)
    }

    override fun getItemCount(): Int {
        return sepetListe.size
    }
    fun sepetYemekAlma(){
        val url = "http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"

        val istek= StringRequest(Request.Method.GET,url, Response.Listener { cevap ->
            // Log.e("Veri Okuma",cevap)

            try {

                val tempListe = ArrayList<Sepet>()

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

                    tempListe.add(sepet)
                }
                sepetListe = tempListe
                notifyDataSetChanged()

            }catch (e: JSONException){
                e.printStackTrace()
            }

        }, Response.ErrorListener { Log.e("Hata","Veri Okuma") })

        Volley.newRequestQueue(mContext).add(istek)
    }

}