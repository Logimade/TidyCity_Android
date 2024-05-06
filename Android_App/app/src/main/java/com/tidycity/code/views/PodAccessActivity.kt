package com.tidycity.code.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.tidycity.code.R

class PodAccessActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pod_access)

        tabLayout = findViewById(R.id.tabLayout)

        for (i in 1..4) {
            val tab = tabLayout.newTab()
            val tabItemView = LayoutInflater.from(this).inflate(R.layout.custom_tab_item, null)
            val tabTitle = tabItemView.findViewById<TextView>(R.id.tabTitle)

            when (i) {
                1 -> tabTitle.text = "All"
                2 -> tabTitle.text = "Allowed"
                3 -> tabTitle.text = "Pending"
                4 -> tabTitle.text = "Denied"
            }

            tab.customView = tabItemView
            tabLayout.addTab(tab)
        }


        // Define a TabLayout.OnTabSelectedListener
        val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Get the custom view of the selected tab
                val customView = tab?.customView
                customView?.let {
                    // Find the TextView inside the custom view
                    val tabTitle = it.findViewById<TextView>(R.id.tabTitle)
                    // Update the text color of the TextView
                    tabTitle.setTextColor(
                        ContextCompat.getColor(
                            this@PodAccessActivity,
                            R.color.purple_700
                        )
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Get the custom view of the unselected tab
                val customView = tab?.customView
                customView?.let {
                    // Find the TextView inside the custom view
                    val tabTitle = it.findViewById<TextView>(R.id.tabTitle)
                    // Update the text color of the TextView
                    tabTitle.setTextColor(
                        ContextCompat.getColor(
                            this@PodAccessActivity,
                            R.color.black
                        )
                    )
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No action needed when reselecting a tab
            }

        }
        // Set the tabSelectedListener to the TabLayout
        tabLayout.addOnTabSelectedListener(tabSelectedListener)
    }

}