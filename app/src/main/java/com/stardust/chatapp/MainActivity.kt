package com.stardust.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.stardust.chatapp.Fragments.ChatFragment
import com.stardust.chatapp.Fragments.SearchFragment
import com.stardust.chatapp.Fragments.SettingsFragment
import com.stardust.chatapp.Model.Chat
import com.stardust.chatapp.Model.Users
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var databaseRef: DatabaseReference ?= null
    var firebaseUser: FirebaseUser ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val toolbarMain: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbarMain)
        supportActionBar!!.title = ""

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)

        /*var viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        viewPagerAdapter.addFragment(ChatFragment())
        viewPagerAdapter.addFragment(SearchFragment())
        viewPagerAdapter.addFragment(SettingsFragment())

        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout,viewPager){
            tab, position ->
            if(position == 0){
                tab.text = "Chat"
            }
            if(position == 1){
                tab.text = "Search"
            }
            if(position == 2){
                tab.text = "Settings"
            }
        }.attach()*/

        val databaseChatsRef = FirebaseDatabase.getInstance().reference.child("Chats")

        databaseChatsRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
                viewPagerAdapter.addFragment(ChatFragment())
                viewPagerAdapter.addFragment(SearchFragment())
                viewPagerAdapter.addFragment(SettingsFragment())
                viewPager.adapter = viewPagerAdapter


                var unseenMessagesCount = 0

                for(datasnapshot in p0.children){

                    val chat = datasnapshot.getValue(Chat::class.java)
                    if(chat!!.getReceiver().equals(firebaseUser!!.uid) && chat.getMessageSeen() == false){

                        unseenMessagesCount += 1
                    }
                }

                if(unseenMessagesCount == 0){
                    TabLayoutMediator(tabLayout,viewPager){
                            tab, position ->
                        if(position == 0){
                            tab.text = "Chat"
                        }
                        if(position == 1){
                            tab.text = "Search"
                        }
                        if(position == 2){
                            tab.text = "Settings"
                        }
                    }.attach()
                }
                else{
                    TabLayoutMediator(tabLayout,viewPager){
                            tab, position ->
                        if(position == 0){
                            tab.text = "Chat ($unseenMessagesCount)"
                        }
                        if(position == 1){
                            tab.text = "Search"
                        }
                        if(position == 2){
                            tab.text = "Settings"
                        }
                    }.attach()
                }
            }

        })



        //Load Username and Profile Picture
        databaseRef!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){

                    val user: Users? = p0.getValue(Users::class.java)
                    profile_name.text = user!!.getUsername()
                    Picasso.get().load(user.getProfilePicture()).into(profile_circleImageView)

                }
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_logout -> {

                //When Logout is selected SignOut from Firebase and show Login Page
                FirebaseAuth.getInstance().signOut()
                val gotoLoginActivity = Intent(this, LoginActivity::class.java)
                gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(gotoLoginActivity)
                finish()

                return true
            }
        }
        return false
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle){

        private val fragments: ArrayList<Fragment> = ArrayList()

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment:Fragment){
            fragments.add(fragment)
        }

    }

    private fun availabilityStatus(status: String){

        val userDatabaseRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap.put("Status", status)

        userDatabaseRef.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()

        availabilityStatus("Online")
    }

    override fun onPause() {
        super.onPause()

        availabilityStatus("Offline")
    }

}