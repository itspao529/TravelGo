package com.udb.travelgo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ListenerRegistration
import com.udb.travelgo.R
import com.udb.travelgo.adapter.DestinationAdapter
import com.udb.travelgo.databinding.ActivityCatalogBinding
import com.udb.travelgo.model.Destination
import com.udb.travelgo.repository.DestinationRepository
import kotlinx.coroutines.launch

class CatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogBinding
    private lateinit var adapter: DestinationAdapter
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adapter = DestinationAdapter(
            onEdit = { destination ->
                val intent = Intent(this, AddEditDestinationActivity::class.java)
                intent.putExtra(AddEditDestinationActivity.EXTRA_DESTINATION_ID, destination.id)
                startActivity(intent)
            },
            onDelete = { destination -> confirmDelete(destination) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditDestinationActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onStart() {
        super.onStart()
        if (!DestinationRepository.isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        listenerRegistration = DestinationRepository.listenToDestinations(
            onChange = { list -> renderList(list) },
            onError = { e ->
                Toast.makeText(
                    this,
                    getString(R.string.error_load_failed, e.localizedMessage ?: ""),
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    override fun onStop() {
        super.onStop()
        listenerRegistration?.remove()
    }

    private fun renderList(list: List<Destination>) {
        adapter.submitList(list)
        binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun confirmDelete(destination: Destination) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(getString(R.string.dialog_delete_message, destination.name))
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                lifecycleScope.launch {
                    try {
                        DestinationRepository.deleteDestination(destination.id)
                        Toast.makeText(this@CatalogActivity, R.string.msg_deleted, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@CatalogActivity,
                            getString(R.string.error_delete_failed, e.localizedMessage ?: ""),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            DestinationRepository.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
