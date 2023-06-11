package com.hellow.projectmanagerapp.activities

 import android.app.Activity
import android.app.Dialog
 import android.os.Build
 import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellow.projectmanagerapp.R
import com.hellow.projectmanagerapp.adapter.MemberListItemsAdapter
import com.hellow.projectmanagerapp.databinding.ActivityMembersBinding
import com.hellow.projectmanagerapp.databinding.DialogSearchMemberBinding
import com.hellow.projectmanagerapp.firebase.FirestoreClass
import com.hellow.projectmanagerapp.model.Board
import com.hellow.projectmanagerapp.model.User
import com.hellow.projectmanagerapp.utils.Constants
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board

    private lateinit var mAssignedMembersList: ArrayList<User>

    private var anyChangesDone: Boolean = false

    private lateinit var binding: ActivityMembersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {
                intent.getParcelableExtra(Constants.BOARD_DETAIL, Board::class.java)!!
            } else {
                intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
            }
        }

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@MembersActivity,
            mBoardDetails.assignedTo
        )
    }

    override fun onBackPressed() {
        if (anyChangesDone) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarMembersActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarMembersActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_add_member -> {

                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupMembersList(list: ArrayList<User>) {

        mAssignedMembersList = list

        hideProgressDialog()

        binding.rvMembersList.layoutManager = LinearLayoutManager(this@MembersActivity)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this@MembersActivity, list)
        binding.rvMembersList.adapter = adapter
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(this)

        val binding = DialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvAdd.setOnClickListener(View.OnClickListener {

            val email = binding.etEmailSearchMember.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()

                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this@MembersActivity, email)
            } else {
                showErrorSnackBar("Please enter members email address.")
            }
        })
        binding.tvCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        dialog.show()
    }

    fun memberDetails(user: User) {

        mBoardDetails.assignedTo.add(user.id)

        FirestoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, user)
    }

    fun memberAssignSuccess(user: User) {

        hideProgressDialog()

        mAssignedMembersList.add(user)

        anyChangesDone = true

        setupMembersList(mAssignedMembersList)

        sendNotificationToUserAsyncTask(mBoardDetails.name, user.fcmToken)

    }

    private fun sendNotificationToUserAsyncTask(boardName: String, token: String) {

        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        showProgressDialog(resources.getString(R.string.please_wait))

        executor.execute {

            var result: String
        var connection: HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL) // Base Url
                connection = url.openConnection() as HttpURLConnection

                connection.doOutput = true
                connection.doInput = true


                connection.instanceFollowRedirects = false


                connection.requestMethod = "POST"


                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )
                connection.useCaches = false


                val wr = DataOutputStream(connection.outputStream)


                val jsonRequest = JSONObject()


                val dataObject = JSONObject()

                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the Board $boardName")

                dataObject.put(
                    Constants.FCM_KEY_MESSAGE,
                    "You have been assigned to the new board by ${mAssignedMembersList[0].name}"
                )


                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)


                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult: Int =
                    connection.responseCode

                if (httpResult == HttpURLConnection.HTTP_OK) {


                    val inputStream = connection.inputStream


                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    try {

                        while (reader.readLine().also { line = it } != null) {
                            sb.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {

                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                } else {

                    result = connection.responseMessage
                }

            } catch (e: SocketTimeoutException) {
                result = "Connection Timeout"
            } catch (e: Exception) {
                result = "Error : " + e.message
            } finally {
                connection?.disconnect()
            }


            handler.post(Runnable {
                hideProgressDialog()

                Log.e("JSON Response Result", result)
            })
        }

    }

}


