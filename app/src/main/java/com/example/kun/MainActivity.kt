package com.example.kun

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.kun.databinding.ActivityMainBinding
import kotlin.math.ceil
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private var interestRate : Double = 0.07
    private var precision : Int = 0
    private val com = 0.0025
    private val k = 1 / (1 - com).pow(2)

    private var inPrice : Double = 0.0

    private class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun update() {
        fun roundString(x : Double) : String {
            fun Double.format(digits: Int) = "%.${digits}f".format(this)
            return if (precision == 0) ceil(x).format(0) else (0.01F*ceil(100*x)).format(2)
         }
        fun sellPrice(price : Double, iRate : Double) : String {
            return roundString(k * price * (1 + iRate))
        }
        binding.MaxPrice.text = sellPrice(inPrice, interestRate)
        val alpha = interestRate + 0.005 - 0.0025.pow(2)
        binding.MinPrice.text = roundString(inPrice * (1 - alpha / (1 + interestRate)))
        val kappa = alpha / (2 - alpha + 2 * interestRate)
        binding.MinOkPrice.text = roundString(inPrice * (1 - kappa))
        binding.MaxOkPrice.text = roundString(inPrice * (1 + kappa))
        val count = binding.Counter.currentItem
        binding.TempPrice2.maxHeight = if (count < 1) 0 else binding.TempPrice1.height
        binding.TempPrice3.maxHeight = if (count < 2) 0 else binding.TempPrice1.height
        binding.TempPrice4.maxHeight = if (count < 3) 0 else binding.TempPrice1.height
        binding.TempPrice5.maxHeight = if (count < 4) 0 else binding.TempPrice1.height

        when (binding.TypeSelector.currentItem) {
            0 -> {
                var x0 = binding.MinPrice.text.toString().toDouble()
                var x1 = binding.MinOkPrice.text.toString().toDouble()
                val dx = (x1 - x0) / count
                when (binding.ModeSelector.currentItem) {
                    0 -> x0 = 2*x0 - x1
                    1 -> x0 = 1.5F*x0 - 0.5F*x1
                    else -> x0
                }
                binding.TempPrice1.text = roundString(x0)
                binding.TempPrice2.text = roundString(x0 + dx)
                binding.TempPrice3.text = roundString(x0 + 2*dx)
                binding.TempPrice4.text = roundString(x0 + 3*dx)
                binding.TempPrice5.text = roundString(x0 + 4*dx)
            }
            else -> {
                val dx : Double = when (binding.ModeSelector.currentItem) {
                    0 -> 0.2
                    1 -> 0.6
                    else -> 1.0
                }
                binding.TempPrice1.text = binding.MaxPrice.text
                binding.TempPrice2.text = sellPrice(inPrice, interestRate + dx)
                binding.TempPrice3.text = sellPrice(inPrice, interestRate + 2*dx)
                binding.TempPrice4.text = sellPrice(inPrice, interestRate + 3*dx)
                binding.TempPrice5.text = sellPrice(inPrice, interestRate + 4*dx)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val buyPrice : EditText = findViewById(R.id.BuyPrice)
        buyPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inPrice = if (start + count > 0) s.toString().toDouble() else 0.0
                precision = if (start + count > 0) if (s.toString().contains(".")) 2 else 0 else 0
                update()
            }
        })

        val intRate : EditText = findViewById(R.id.InterestRate)
        intRate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                interestRate = if (start + count > 0) 0.01 * s.toString().toDouble() else 0.07
                update()
            }
        })

        binding.ModeSelector.adapter = (object : RecyclerView.Adapter<PagerViewHolder>()  {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder =
                    PagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.selector_page, parent, false))

            override fun getItemCount(): Int = 3

            override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
                holder.itemView.findViewById<TextView>(R.id.SelectText).text = when (position) {
                    0 -> holder.itemView.context.getString(R.string.pessimistic)
                    1 -> holder.itemView.context.getString(R.string.conservative)
                    else -> holder.itemView.context.getString(R.string.optimistic)
                }
                holder.itemView.findViewById<TextView>(R.id.SelectText).setTextColor(holder.itemView.context.getColor(R.color.blue))
            }
        })
        binding.ModeSelector.offscreenPageLimit = 1
        binding.ModeSelector.setCurrentItem(1, false)
        binding.ModeSelector.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                update()
            }
        })

        binding.TypeSelector.adapter = (object : RecyclerView.Adapter<PagerViewHolder>()  {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder =
                    PagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.selector_page, parent, false))

            override fun getItemCount(): Int = 2

            override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
                holder.itemView.findViewById<TextView>(R.id.SelectText).text = when (position) {
                    0 -> holder.itemView.context.getString(R.string.BuyPrice).toLowerCase()
                    else -> holder.itemView.context.getString(R.string.SellPrice).toLowerCase()
                }
                holder.itemView.findViewById<TextView>(R.id.SelectText).setTextColor(holder.itemView.context.getColor(R.color.deep_blue))
            }
        })
        binding.TypeSelector.offscreenPageLimit = 1
        binding.TypeSelector.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                update()
            }
        })

        binding.Counter.adapter = (object : RecyclerView.Adapter<PagerViewHolder>()  {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder =
                    PagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.selector_page, parent, false))

            override fun getItemCount(): Int = 5

            override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
                holder.itemView.findViewById<TextView>(R.id.SelectText).text = (position + 1).toString()
                holder.itemView.findViewById<TextView>(R.id.SelectText).setTextColor(holder.itemView.context.getColor(R.color.red))
            }
        })
        binding.Counter.offscreenPageLimit = 1
        binding.Counter.currentItem = 2
        binding.Counter.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                update()
            }
        })

        update()
    }
}


/*
 class CollectionDemoFragment : Fragment() {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private lateinit var demoCollectionAdapter: DemoCollectionAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.collection_demo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        demoCollectionAdapter = DemoCollectionAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = demoCollectionAdapter
    }
}

* */