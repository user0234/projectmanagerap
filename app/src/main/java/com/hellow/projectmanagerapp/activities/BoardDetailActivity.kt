package com.hellow.projectmanagerapp.activities

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.hellow.projectmanagerapp.R
import com.hellow.projectmanagerapp.databinding.ActivityBoardDetailBinding
import com.hellow.projectmanagerapp.databinding.ActivityCardDetailsBinding
import com.hellow.projectmanagerapp.model.Board
import com.hellow.projectmanagerapp.model.User
import com.hellow.projectmanagerapp.utils.Constants
import java.io.IOException


class BoardDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardDetailBinding

   private lateinit var mBoard: Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoard = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {
                intent.getParcelableExtra(Constants.BOARD_DETAIL, Board::class.java)!!
            } else {
                intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
            }
        }
        setupActionBar()
        setUserDataInUI(mBoard)

    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarBoardDetailActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarBoardDetailActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setUserDataInUI(board: Board) {


        Glide
            .with(this@BoardDetailActivity)
            .load(board.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivBoardImage)

        binding.tvBoardNameValue.text = board.name
        binding.tvMemberCountValue.text = board.assignedTo.size.toString()
        binding.tvBoardDetailsValue.text = board.description.toString()
    }
}