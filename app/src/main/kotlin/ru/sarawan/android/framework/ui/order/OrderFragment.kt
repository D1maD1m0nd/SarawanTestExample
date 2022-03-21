package ru.sarawan.android.framework.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import retrofit2.HttpException
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentOrderBinding
import ru.sarawan.android.framework.ui.order.viewModel.OrderViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Order
import ru.sarawan.android.model.data.OrderApprove
import ru.sarawan.android.utils.exstentions.toFormatString
import javax.inject.Inject

class OrderFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by lazy {
        viewModelFactory.get().create(OrderViewModel::class.java)
    }

    private var addressItem: AddressItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

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

    private fun initView() = with(binding) {
        addressButton.setOnClickListener {
            val action = OrderFragmentDirections.actionOrderFragmentToProfileAddressFragment(
                city = addressItem?.city,
                street = addressItem?.street,
                house = addressItem?.house,
                roomNumber = addressItem?.roomNumber
            )
            findNavController().navigate(action)
        }

        setOrderButton.setOnClickListener { addressItem?.let { viewModel.createOrder(it) } }

        swipeContainer.setOnRefreshListener {
            viewModel.getAddress()
            swipeContainer.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setState(state: AppState<*>) {
        when (state) {
            is AppState.Success -> {
                when (val item = state.data) {
                    is String -> binding.addressButton.text = item

                    is AddressItem -> {
                        binding.progressBar.visibility = View.GONE
                        addressItem = AddressItem(idAddressOrder = item.id)
                        addressItem?.let { viewModel.getOrder(it) }
                        viewModel.getFormatAddress(item)
                    }

                    is Order -> setOrderData(item)

                    is OrderApprove -> {
                        binding.progressBar.visibility = View.GONE
                        item.orderName.let {
                            val message = "Заказ №${it} оформлен"
                            val action = OrderFragmentDirections
                                .actionOrderFragmentToSuccessOrderDialogFragment(message)
                            findNavController().navigate(action)
                        }
                    }

                    else -> throw RuntimeException("Wrong AppState type $state")
                }
            }

            is AppState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.contentOrderLayout.visibility = View.GONE
            }

            is AppState.Error -> {
                binding.progressBar.visibility = View.GONE
                val error = state.error
                if (error is HttpException) {
                    when (error.code()) {
                        500 -> Toast.makeText(context, "Ошибка сервера ${error.message()}", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(context, "Error ${error.message()}", Toast.LENGTH_SHORT).show();
                }
            }

            else -> throw RuntimeException("Wrong AppState type $state")
        }
    }

    private fun setOrderData(order: Order) = with(binding) {
        diliveryPriceValueTextView.text =
            order.paymentAmount.toFormatString(getString(R.string.rub_symbol))
        costValueTextView.text = order.basketSumm.toFormatString(getString(R.string.rub_symbol))
        resultValuePaymentTextView.text = order.paymentAmount.plus(order.basketSumm)
            .toFormatString(getString(R.string.rub_symbol))
        contentOrderLayout.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }
}