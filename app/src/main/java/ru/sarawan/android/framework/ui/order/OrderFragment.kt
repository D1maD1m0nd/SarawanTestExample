package ru.sarawan.android.framework.ui.order

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import dagger.android.support.AndroidSupportInjection
import retrofit2.HttpException
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentOrderBinding
import ru.sarawan.android.framework.ui.modals.SuccessOrderFragment
import ru.sarawan.android.framework.ui.order.viewModel.OrderViewModel
import ru.sarawan.android.framework.ui.profile.address_fragment.ProfileAddressFragment
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Order
import ru.sarawan.android.model.data.OrderApprove
import ru.sarawan.android.utils.exstentions.toFormatString
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
        initView()
    }

    private fun initView() = with(binding){
        addressButton.setOnClickListener {
            ProfileAddressFragment.newInstance(addressItem){}.show(childFragmentManager, null)
        }

        setOrderButton.setOnClickListener {
            addressItem?.let {
                viewModel.createOrder(it)
            }
        }

        swipeContainer.setOnRefreshListener(OnRefreshListener {
            //your method to refresh content
            viewModel.getAddress()
            swipeContainer.isRefreshing = false;
        })
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
                            binding.progressBar.visibility = View.GONE
                            item.orderName?.let {
                                val message = "Заказ №${it} оформлен"
                                SuccessOrderFragment
                                    .newInstance(message)
                                    .show(childFragmentManager, null)
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
                val error = state.error
                if(error is HttpException) {
                    when(error.code()) {
                        500 -> {

                        }
                    }

                }
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