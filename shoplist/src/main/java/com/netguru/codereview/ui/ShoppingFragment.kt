package com.netguru.codereview.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.netguru.codereview.network.model.ShopListItemResponse
import com.netguru.codereview.network.model.ShopListResponse
import com.netguru.codereview.shoplist.R
import com.netguru.codereview.ui.model.ShopList
import javax.inject.Inject

class ShoppingFragment : Fragment(R.layout.main_fragment) {

    @Inject
    private var viewModel: ShoppingViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ShoppingViewModel::class.java)

        viewModel!!.shopLists.observe(this, { lists ->
            val progressBar = view.findViewById<ProgressBar>(R.id.message)
            val latestIcon = view.findViewById<ImageView>(R.id.latest_list_icon)

            val shopLists = lists.also {
                latestIcon?.load(it.first().iconUrl)
            }

            progressBar?.isVisible = false

            Log.i("LOGTAG", "LOLOLOL Is it done already?")


            // Display the list in recyclerview
            // adapter.submitList(shopLists)
        })
        viewModel!!.events().observe(this, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
    }
}
