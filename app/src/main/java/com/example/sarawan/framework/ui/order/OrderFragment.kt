package com.example.sarawan.framework.ui.order

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.sarawan.R
import com.example.sarawan.databinding.FragmentOrderBinding
import com.example.sarawan.framework.ui.basket.viewModel.BasketViewModel
import com.example.sarawan.framework.ui.order.viewModel.OrderViewModel
import com.example.sarawan.model.data.AddressItem
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Order
import com.example.sarawan.model.data.OrderApprove
import com.example.sarawan.utils.exstentions.toFormatString
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class OrderFragment : Fragment() {
    private var _binding : FragmentOrderBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: OrderViewModel by lazy {
        viewModelFactory.create(OrderViewModel::class.java)
    }

    private var addressItem : AddressItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState: AppState<*> ->
            setState(appState)
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAddress()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setState(state: AppState<*>) {
        when(state) {
            is AppState.Success -> {
                val data = state.data
                if(data.isNotEmpty()) {
                    when(val item = data.first()) {
                        is AddressItem -> {
                            addressItem = AddressItem(idAddressOrder = item.id)
                            addressItem?.let {
                                viewModel.getOrder(it)
                            }
                            binding.addressButton.text = formatAddress(item)
                        }

                        is Order -> {
                            setOrderData(item)
                        }

                        is OrderApprove -> {
                            item.orderName?.let {
                                val message = "Заказ №${it} оформлен"
                            }
                        }
                    }
                }
            }

            is AppState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.contentOrderLayout.visibility = View.GONE
            }

            is AppState.Error -> {
                binding.progressBar.visibility = View.GONE
            }

            is AppState.Empty -> {

            }
        }
    }
    private fun formatAddress(address: AddressItem): String {
        val city = address.city
        val street = address.street
        val house = address.house
        val roomNum = address.roomNumber
        return "$city, ул $street, д $house, кв $roomNum"
    }

    private fun setOrderData(order : Order) = with(binding) {
        diliveryPriceValueTextView.text = order
                                            .paymentAmount
                                            ?.toFormatString(getString(R.string.rub_symbol))
                                            ?: DEFAULT_VALUE
        costValueTextView.text = order
                                    .basketSumm
                                    ?.toFormatString(getString(R.string.rub_symbol))
                                    ?: DEFAULT_VALUE
        resultValuePaymentTextView.text = order
                                            .paymentAmount
                                            ?.plus(order.basketSumm ?: 0.0)
                                            ?.toFormatString(getString(R.string.rub_symbol))
                                            ?: DEFAULT_VALUE
        contentOrderLayout.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private companion object {
        const val DEFAULT_VALUE = "0.0 ₽"
    }
}