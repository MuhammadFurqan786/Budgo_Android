package com.sokoldev.budgo.patient.ui.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.viewmodels.JobViewModel
import com.sokoldev.budgo.common.data.local.AppDatabase
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.GPSTracker
import com.sokoldev.budgo.common.utils.Global
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.FragmentCartBinding
import com.sokoldev.budgo.patient.adapter.CartAdapter
import com.sokoldev.budgo.patient.models.request.PlaceOrderRequest
import com.sokoldev.budgo.patient.models.request.Product
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JobViewModel by viewModels()
    private lateinit var cartViewModel: CartViewModel
    private lateinit var adapter: CartAdapter
    private var amount = "0"
    private var clientSecret = ""
    private var customerId = ""
    private var ephemeralKey = ""
    private var latitude = ""
    private var longitude = ""

    private lateinit var helper: PreferenceHelper
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var publishableKey: String
    private var isRead:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root
        helper = PreferenceHelper.getPref(requireContext())
        getLatLon()
        val cartDao = AppDatabase.getDatabase(requireContext()).cartDao()
        cartViewModel =
            ViewModelProvider(this, CartViewModelFactory(cartDao))[CartViewModel::class.java]

        val intent = arguments
        if (intent != null) {
            isRead = intent.getBoolean(PreferenceKeys.IS_READ)
        }


        cartViewModel.getCartItems()
        initObserver(cartViewModel)


        paymentSheet = PaymentSheet(this) { paymentSheetResult ->
            onPaymentSheetResult(paymentSheetResult)
        }

        binding.checkoutButton.setOnClickListener {
            if (isRead) {
                Log.d("CartFragment", "Checkout button clicked, amount: $amount")
                if (amount != "0") {
                    helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN, "")?.let { token ->
                        Log.d("CartFragment", "Fetching order token with user token: $token")
                        viewModel.getOrderToken(token, amount)
                    }
                }
            }else{
                findNavController().navigate(R.id.action_navigation_cart_to_cancellationFragment)
            }
        }

        return root
    }

    @SuppressLint("SetTextI18n")
    private fun initObserver(cartViewModel: CartViewModel) {
        adapter = CartAdapter(emptyList(), cartViewModel)
        binding.recyclerviewCart.adapter = adapter

        cartViewModel.listCart.observe(viewLifecycleOwner) { cartItems ->
            Log.d("CartFragment", "Cart items updated: ${cartItems.size} items")
            if (!cartItems.isNullOrEmpty()) {
                adapter.updateCartItems(cartItems)
                val totalPrice = cartItems.sumOf { it.productPrice * it.quantity }
                binding.total.text = "$$totalPrice"
                amount = totalPrice.toString()

                binding.recyclerviewCart.visibility = View.VISIBLE
                binding.linearCheckout.visibility = View.VISIBLE
                binding.imageEmptyCart.visibility = View.GONE
                binding.textEmptyCart.visibility = View.GONE
            } else {
                binding.recyclerviewCart.visibility = View.GONE
                binding.linearCheckout.visibility = View.GONE
                binding.imageEmptyCart.visibility = View.VISIBLE
                binding.textEmptyCart.visibility = View.VISIBLE
            }
        }

        viewModel.paymentToken.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    clientSecret = response.data.client_secret ?: ""
                    customerId = response.data.customer ?: ""
                    ephemeralKey = response.data.ephemeral_key ?: ""

                    Log.d("CartFragment", "Payment token received: $clientSecret")
                    Log.d("CartFragment", "Customer ID: $customerId, Ephemeral Key: $ephemeralKey")

                    publishableKey = response.data.publishable_key ?: ""

                    // Initialize Stripe Payment Configuration
                    PaymentConfiguration.init(requireContext(), publishableKey)

                    presentPaymentSheet()
                }

                is ApiResponse.Error -> {
                    binding.loadingView.visibility = View.GONE
                    Log.e("CartFragment", "Error fetching payment token: ${response.errorMessage}")
                    Global.showMessage(binding.root, response.errorMessage, 2000)
                }

                ApiResponse.Loading -> {
                    binding.loadingView.visibility = View.VISIBLE
                    Log.d("CartFragment", "Fetching payment token...")
                }
            }
        }

        cartViewModel.placeOrderResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Success -> {
                    binding.loadingView.visibility = View.GONE
                    Global.showMessage(binding.root, response.data.message, 2000)
                    cartViewModel.clearCart()
                }

                is ApiResponse.Error -> {
                    binding.loadingView.visibility = View.GONE
                    Log.e("CartFragment", "Error placing order: ${response.errorMessage}")
                    Global.showMessage(binding.root, response.errorMessage, 2000)
                }

                ApiResponse.Loading -> {
                    binding.loadingView.visibility = View.VISIBLE
                    Log.d("CartFragment", "Placing order...")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun presentPaymentSheet() {
        if (clientSecret.isEmpty() || customerId.isEmpty() || ephemeralKey.isEmpty()) {
            Log.e(
                "CartFragment",
                "Payment sheet setup error: missing clientSecret, customerId, or ephemeralKey"
            )
            Toast.makeText(requireContext(), "Payment not set up properly", Toast.LENGTH_SHORT)
                .show()
            return
        }

        Log.d("CartFragment", "Presenting payment sheet with clientSecret: $clientSecret")

        val customerConfig = PaymentSheet.CustomerConfiguration(
            id = customerId,
            ephemeralKeySecret = ephemeralKey
        )

        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "My Merchant",
                customer = customerConfig,
                allowsDelayedPaymentMethods = true
            )
        )
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Log.d("CartFragment", "Payment canceled")

                Toast.makeText(requireContext(), "Payment Canceled", Toast.LENGTH_SHORT).show()
            }

            is PaymentSheetResult.Failed -> {
                Log.e("CartFragment", "Payment failed: ${paymentSheetResult.error}")
                Toast.makeText(
                    requireContext(),
                    "Payment Failed: ${paymentSheetResult.error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            is PaymentSheetResult.Completed -> {
                Log.d("CartFragment", "Payment successful!")
                placeOrder()
                Toast.makeText(requireContext(), "Payment Successful!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun placeOrder() {
        val list = adapter.getCartItems()
        val arrayList = ArrayList<Product>()
        for (item in list) {
            arrayList.add(Product(item.productPrice, item.productId, item.quantity))
        }
        val request = PlaceOrderRequest(
            amount,
            helper.getCurrentUser()?.name ?: "",
            latitude,
            longitude,
            arrayList
        )
        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)
            ?.let { cartViewModel.placeOrder(it, request) }
    }


    private fun getLatLon() {
        val gpsTracker = GPSTracker(requireContext())

        if (gpsTracker.isGPSTrackingEnabled) {
            latitude = gpsTracker.getLatitude().toString()
            longitude = gpsTracker.getLongitude().toString()

            helper.saveStringValue(PreferenceKeys.KEY_LATITUDE, latitude)
            helper.saveStringValue(PreferenceKeys.KEY_LONGITUDE, longitude)

        } else {
            gpsTracker.showSettingsAlert()
        }
    }

}
