package com.lauwba.surelabs.mishopcustomer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputLayout
import android.text.TextWatcher
import android.widget.EditText
import android.text.Editable
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    lateinit var next : FloatingActionButton
    lateinit var nama : EditText
    lateinit var email: EditText
    lateinit var password : EditText
    lateinit var confirmPass : EditText
    lateinit var nomorTelepon : EditText
    lateinit var alamat : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        next = findViewById(R.id.next)
        nama = findViewById(R.id.nama)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        confirmPass = findViewById(R.id.confirm)
        nomorTelepon = findViewById(R.id.nomorTelepon)
        alamat = findViewById(R.id.alamat)

        confirmPass.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(s!!.equals(password.text.toString())){
                    Toast.makeText(this@MainActivity, "Password Tidak cocok", Toast.LENGTH_SHORT).show()
                }
            }

        })

        next.setOnClickListener{
            var i = Intent(this@MainActivity, DetailRegistrasi::class.java)
            startActivity(i)
        }
    }
}
