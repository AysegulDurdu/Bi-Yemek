package com.aysegul.biyemek.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aysegul.biyemek.view.DetayActivity
import com.aysegul.biyemek.R
import com.aysegul.biyemek.model.Yemekler
import com.squareup.picasso.Picasso

class YemekAdapter(private var mContext: Context,private var yemekListe:ArrayList<Yemekler>)
    :RecyclerView.Adapter<YemekAdapter.CardTasarimTutucu>() {

    inner class CardTasarimTutucu(tasarim:View):RecyclerView.ViewHolder(tasarim){
        var yemek_card:CardView
        var imageYemek:ImageView
        var textYemekAd:TextView
        var textFiyat:TextView

        init {
            yemek_card = tasarim.findViewById(R.id.sepet_card)
            imageYemek = tasarim.findViewById(R.id.imageYemek)
            textYemekAd = tasarim.findViewById(R.id.textYemekAd)
            textFiyat = tasarim.findViewById(R.id.textFiyat)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.yemek_listesi_tasarim,parent,false)
        return CardTasarimTutucu(tasarim)
    }

    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {
        val yemek = yemekListe.get(position)

        holder.textYemekAd.text = "${yemek.yemek_adi}"
        holder.textFiyat.text = "${yemek.yemek_fiyat}₺"

        holder.yemek_card.setOnClickListener {
            val intent = Intent(mContext, DetayActivity::class.java)
            intent.putExtra("yemekNesne",yemek)
            mContext.startActivity(intent)
        }

        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${yemek.yemek_resim_adi}"
        Picasso.get().load(url).into(holder.imageYemek)
    }

    override fun getItemCount(): Int {
        return yemekListe.size
    }

}