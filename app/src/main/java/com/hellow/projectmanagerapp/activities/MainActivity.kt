package com.hellow.projectmanagerapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.hellow.projectmanagerapp.R
import com.hellow.projectmanagerapp.adapter.BoardItemsAdapter
import com.hellow.projectmanagerapp.databinding.ActivityMainBinding
import com.hellow.projectmanagerapp.firebase.FirestoreClass
import com.hellow.projectmanagerapp.model.Board
import com.hellow.projectmanagerapp.model.User
import com.hellow.projectmanagerapp.utils.Constants

class MainActivity : BaseActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mUserName: String

    private lateinit var mSharedPreferences: SharedPreferences

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

         setContentView(binding.root)

         setupActionBar()

        binding.navView.setNavigationItemSelectedListener(this)

        mSharedPreferences =
            this.getSharedPreferences(Constants.PROJECT_MAN_PREFERENCES, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

        if (tokenUpdated) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this@MainActivity, true)
        } else {

            FirebaseMessaging.getInstance()
                .token.addOnSuccessListener(this@MainActivity) { instanceIdResult ->
                    updateFCMToken(instanceIdResult)
                }
        }

       binding.appBarMain.fabCreateBoard.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }

    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {

            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {

                startActivityForResult(
                    Intent(this@MainActivity, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )
            }

            R.id.nav_sign_out -> {

                FirebaseAuth.getInstance().signOut()

                mSharedPreferences.edit().clear().apply()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            showProgressDialog(resources.getString(R.string.please_wait))

            FirestoreClass().loadUserData(this@MainActivity)
        } else if (resultCode == Activity.RESULT_OK
            && requestCode == CREATE_BOARD_REQUEST_CODE
        ) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this@MainActivity)

        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    private fun updateFCMToken(token: String) {

        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this@MainActivity, userHashMap)
    }

    private fun setupActionBar() {

       setSupportActionBar(binding.appBarMain.toolbarMainActivity)
        binding.appBarMain.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        binding.appBarMain.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }

    }

    private fun toggleDrawer() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {

        mUserName = user.name

        val headerView = binding.navView.getHeaderView(0)

        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)

        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)

        navUsername.text = user.name

        if (readBoardsList) {
            FirestoreClass().getBoardsList(this@MainActivity)
        }

        if(mProgressDialog.isShowing){
            hideProgressDialog()
        }

    }
    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {


        if (boardsList.size > 0) {

            binding.appBarMain.mainContentLayout.mainContentXml.visibility = View.VISIBLE
            binding.appBarMain.mainContentLayout.tvNoBoardsAvailable.visibility = View.GONE

            binding.appBarMain.mainContentLayout.rvBoardsList.layoutManager = LinearLayoutManager(this@MainActivity)
            binding.appBarMain.mainContentLayout.rvBoardsList.setHasFixedSize(true)


            val adapter = BoardItemsAdapter(this@MainActivity, boardsList)
            binding.appBarMain.mainContentLayout.rvBoardsList.adapter = adapter // Attach the adapter to the recyclerView.

            adapter.setOnClickListener(object :
                BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })

            binding.appBarMain.mainContentLayout.rvBoardsList.visibility = View.VISIBLE
        } else {
            binding.appBarMain.mainContentLayout.mainContentXml.visibility = View.GONE
            binding.appBarMain.mainContentLayout.rvBoardsList.visibility = View.GONE
            binding.appBarMain.mainContentLayout.tvNoBoardsAvailable.visibility = View.VISIBLE
        }

        if(mProgressDialog.isShowing){
            hideProgressDialog()
        }
    }

    fun tokenUpdateSuccess() {

        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this@MainActivity, true)
    }

    companion object {

        const val MY_PROFILE_REQUEST_CODE: Int = 11

        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }
}