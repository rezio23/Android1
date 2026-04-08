package com.example.homework1apr

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val repo = FirebaseRepository()
    private val adapter = NotesAdapter(mutableListOf())

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSignOut = findViewById<Button>(R.id.btnSignOut)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val etNote = findViewById<EditText>(R.id.etNote)
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)

        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        btnSignOut.setOnClickListener {
            repo.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnAdd.setOnClickListener {
            val text = etNote.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            CoroutineScope(Dispatchers.IO).launch {
                val res = repo.addNote(text)
                withContext(Dispatchers.Main) {
                    if (res.isSuccess) {
                        etNote.text.clear()
                        loadNotes()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            res.exceptionOrNull()?.localizedMessage ?: "Add failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        loadNotes()
    }

    private fun loadNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            val res = repo.getNotes()
            withContext(Dispatchers.Main) {
                if (res.isSuccess) {
                    adapter.setItems(res.getOrDefault(emptyList()))
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        res.exceptionOrNull()?.localizedMessage ?: "Load failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

class NotesAdapter(private val items: MutableList<Note>) :
    RecyclerView.Adapter<NotesAdapter.VH>() {

    inner class VH(val v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val note = items[position]
        val tv = holder.v.findViewById<TextView>(R.id.tvNote)
        tv.text = note.text

        holder.v.setOnLongClickListener {
            val id = note.id
            if (id.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val repo = FirebaseRepository()
                    val res = repo.deleteNote(id)
                    withContext(Dispatchers.Main) {
                        if (res.isSuccess) {
                            items.removeAt(position)
                            notifyItemRemoved(position)
                        } else {
                            Toast.makeText(
                                holder.v.context,
                                res.exceptionOrNull()?.localizedMessage ?: "Delete failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            true
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<Note>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}