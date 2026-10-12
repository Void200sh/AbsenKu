package com.absenku.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.absenku.data.AttendanceRecord
import com.absenku.databinding.ActivityMainBinding
import com.absenku.ui.checkin.CheckInActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter()
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.todayRecords.observe(this) { records ->
            adapter.submitList(records)
            binding.llEmptyState.visibility = if (records.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            binding.rvHistory.visibility = if (records.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
        }

        viewModel.greetingPrefix.observe(this) { prefix ->
            // Toolbar subtitle could be updated here if needed
        }

        viewModel.userName.observe(this) { name ->
            // Could update a user name text view if we had one
        }

        viewModel.todayStats.observe(this) { stats ->
            // binding.tvTodayStats.text = stats
        }

        viewModel.monthStats.observe(this) { stats ->
            // binding.tvMonthStats.text = stats
        }
    }

    private fun setupClickListeners() {
        binding.btnCheckIn.setOnClickListener {
            val intent = Intent(this, CheckInActivity::class.java)
            startActivity(intent)
        }

        binding.tvSeeAll.setOnClickListener {
            // TODO: Open full history screen
            Toast.makeText(this, "Full history coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

class HistoryAdapter : androidx.recyclerview.widget.ListAdapter<AttendanceRecord, HistoryAdapter.ViewHolder>(
    androidx.recyclerview.widget.DiffUtil.ItemCallback<AttendanceRecord> {
        override fun areItemsTheSame(oldItem: AttendanceRecord, newItem: AttendanceRecord): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AttendanceRecord, newItem: AttendanceRecord): Boolean =
            oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val binding = com.absenku.databinding.ItemHistoryBinding.inflate(
            android.view.LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: com.absenku.databinding.ItemHistoryBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(record: AttendanceRecord) {
            binding.tvTitle.text = record.type.replace("_", " ").capitalize()
            val date = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(record.timestamp))
            val location = record.location ?: "Lokasi tidak diketahui"
            binding.tvMeta.text = "$date • $location"

            val (bgRes, textRes) = when (record.type) {
                "check_in" -> com.absenku.R.drawable.bg_status_hadir to com.absenku.R.color.status_hadir_text
                "check_out" -> com.absenku.R.drawable.bg_status_hadir to com.absenku.R.color.status_hadir_text
                else -> com.absenku.R.drawable.bg_status_belum to com.absenku.R.color.status_belum_text
            }
            binding.tvStatus.setBackgroundResource(bgRes)
            binding.tvStatus.setTextColor(binding.root.context.getColor(textRes))
            binding.tvStatus.text = when (record.type) {
                "check_in" -> "Hadir"
                "check_out" -> "Selesai"
                else -> "Belum"
            }
        }
    }
}