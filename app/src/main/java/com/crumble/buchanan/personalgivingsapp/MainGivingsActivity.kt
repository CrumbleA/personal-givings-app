package com.crumble.buchanan.personalgivingsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_givings.*

class MainGivingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_givings)

        val fwoNumberString = intent.getStringExtra("email")?.substring(0, 3)
        val churchCodeString = intent.getStringExtra("email")?.split('@')?.get(1)?.split('.')?.get(0)
        println(fwoNumberString)
        val fwoNumber = fwoNumberString?.toInt()

        fwoNumberTitleTextView.text = getString(R.string.fwo_number_title, fwoNumber.toString().padStart(3, '0'))

        val firebaseDatabase = Firebase.database

        println("${churchCodeString}/users/${Firebase.auth.currentUser!!.uid}")

        val myRef = firebaseDatabase.getReference("${churchCodeString}/users/${Firebase.auth.currentUser!!.uid}")
        myRef.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(dataSnaphot: DataSnapshot) {
                val user = dataSnaphot.getValue<User>()

                fwoAmountTextView.text = getString(R.string.amount, user?.fwo_givings ?: "0.00")
                developmentAmountTextView.text = getString(R.string.amount, user?.development_givings ?: "0.00")
                unitedAppealAmountTextView.text = getString(R.string.amount, user?.united_appeal_givings ?: "0.00")
                worldDevelopmentAmountTextView.text = getString(R.string.amount, user?.world_development_givings ?: "0.00")
                lambegAppealAmountTextView.text = getString(R.string.amount, user?.lambeg_appeal_givings ?: "0.00")
                totalAmountTextView.text = getString(R.string.amount, user?.total_givings ?: "0.00")

                if (user == null) {
                    fwoAmountTextView.text = "unknown"
                    developmentAmountTextView.text = "unknown"
                    unitedAppealAmountTextView.text = "unknown"
                    worldDevelopmentAmountTextView.text = "unknown"
                    lambegAppealAmountTextView.text = "unknown"
                    totalAmountTextView.text = "unknown"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("firebaseError", "loadPost:onCancelled", error.toException())
            }
        })

        val dateRef = firebaseDatabase.getReference("${churchCodeString}/date")
        dateRef.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dateObject = dataSnapshot.getValue<JSONDate>()

                dateTextView.text = getString(R.string.date_text, dateObject?.dateStart, dateObject?.dateEnd)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("firebaseError", "loadPost:onCancelled", error.toException())
            }
        })
        
        signOutButton.setOnClickListener { 
            Firebase.auth.signOut()
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

data class User (
    var fwo_number: String? = null,
    var fwo_givings: String? = null,
    var development_givings: String? = null,
    var united_appeal_givings: String? = null,
    var world_development_givings: String? = null,
    var lambeg_appeal_givings: String? = null,
    var total_givings: String? = null
)

data class JSONDate (
    var dateStart: String? = null,
    var dateEnd: String? = null
)