package com.sokoldev.budgo.patient.ui.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.local.AppDatabase
import com.sokoldev.budgo.common.data.local.CartItem
import com.sokoldev.budgo.common.data.models.response.Product
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.databinding.ActivityProductDetailsBinding
import com.sokoldev.budgo.patient.ui.cart.CartViewModel
import com.sokoldev.budgo.patient.ui.cart.CartViewModelFactory
import com.sokoldev.budgo.patient.ui.home.HomeActivity

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private var quantity: Int = 1
    private lateinit var cartViewModel: CartViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val product: Product? = intent.getParcelableExtra("product")

        val cartDao = AppDatabase.getDatabase(this).cartDao()
        cartViewModel =
            ViewModelProvider(this, CartViewModelFactory(cartDao))[CartViewModel::class.java]

        binding.apply {
            if (product != null) {
                product.productName.let { productName.text = it }
                product.productPrice.let { productPrice.text = "$$it" }
                product.productDescription.let { productDesc.text = it }
                product.temperature.let { tValue.text = "Weight : $it" }
                product.condition.let { cValue.text = "THC : $it" }
                product.productImage?.let {
                    loadImageWithBaseUrlFallback(productImage,it)
                }
                product.productName.let { productName.text = it }
            }
        }

        // Set click listener for plus button
        binding.buttonPlus.setOnClickListener {
            quantity++
            binding.quantityText.text = quantity.toString() // Update quantity display
        }

        binding.buttonMinus.setOnClickListener {
            if (quantity > 1) { // Ensure quantity doesn't go below 1
                quantity--
                binding.quantityText.text = quantity.toString() // Update quantity display
            }
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.addToCart.setOnClickListener {

            val cart = product?.let { it1 ->
                it1.id?.let { it2 ->
                    product.productPrice?.let { it3 ->
                        CartItem(
                            it2,
                            quantity,
                            product.productImage!!,
                            product.productName!!,
                            it3.toInt()
                        )
                    }
                }
            }
            if (cart != null) {
                cartViewModel.addToCart(cart)
            } // Add to cart with current quantity
            // Optionally, show a message or update UI to indicate success
            Global.showMessage(binding.root.rootView, "Product Added to Cart")

            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("destination", R.id.navigation_cart)
            startActivity(intent)
        }
    }
    private fun loadImageWithBaseUrlFallback(imageView: ImageView, originalUrl: String) {
        // Sanitize the original URL by replacing spaces
        val safeOriginalUrl = originalUrl.replace(" ", "%20")

        // Replace base URL and sanitize the fallback as well
        val modifiedUrl = safeOriginalUrl.replace(
            "https://budgo.net/budgo/public/",
            "https://admin.budgo.net/"
        )

        // Build the fallback request with the safe original URL
        val fallbackRequest = Glide.with(imageView.context)
            .load(safeOriginalUrl)

        // Start primary request with fallback
        Glide.with(imageView.context)
            .load(modifiedUrl)
            .error(fallbackRequest) // Try this if the primary fails
            .into(imageView)
    }


}