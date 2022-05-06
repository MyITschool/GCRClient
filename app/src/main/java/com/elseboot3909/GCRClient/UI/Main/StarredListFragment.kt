package com.elseboot3909.GCRClient.UI.Main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elseboot3909.GCRClient.API.AccountAPI
import com.elseboot3909.GCRClient.Adapter.ChangePreviewAdapter
import com.elseboot3909.GCRClient.Entities.ChangeInfo
import com.elseboot3909.GCRClient.R
import com.elseboot3909.GCRClient.UI.Change.ChangeActivity
import com.elseboot3909.GCRClient.Utils.Constants
import com.elseboot3909.GCRClient.Utils.JsonUtils
import com.elseboot3909.GCRClient.Utils.NetManager
import com.elseboot3909.GCRClient.databinding.FragmentStarredListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StarredListFragment : Fragment() {

    private lateinit var binding: FragmentStarredListBinding
    private var changesList = ArrayList<ChangeInfo>()
    private lateinit var changePreviewAdapter: ChangePreviewAdapter
    private val mGestureDetector = GestureDetector(context, object: GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent) : Boolean {
            return true
        }
    })
    private var starredCount = -1
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStarredListBinding.inflate(inflater, container, false)

        binding.ctlMenu.setOnClickListener {
            activity?.findViewById<DrawerLayout>(R.id.serverNavDL)?.openDrawer(GravityCompat.START)
        }

        if (starredCount < 0) {
            starredCount = 0
            getChangesList()
        }
        updateTotalCount(starredCount)

        activity?.let {
            changePreviewAdapter = ChangePreviewAdapter(changesList, it)
        }
        binding.changesView.adapter = changePreviewAdapter
        binding.changesView.layoutManager = LinearLayoutManager(context)


        binding.changesView.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                if (mGestureDetector.onTouchEvent(e) && child != null) {
                    val changeInfo = changesList[rv.getChildAdapterPosition(child)]
                    val intent = Intent(activity, ChangeActivity::class.java)
                    intent.putExtra("changeInfo", changeInfo)
                    startActivity(intent)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) { }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) { }
        })

        binding.refreshChangesView.setOnRefreshListener {
            val size = changesList.size
            changesList.clear()
            changePreviewAdapter.notifyItemRangeRemoved(0, size)
            updateTotalCount(0)
            getChangesList()
            binding.refreshChangesView.isRefreshing = false
        }

        return binding.root
    }

    private fun getChangesList() {
        if (!isLoading) {
            isLoading = true
        } else {
            return
        }
        (activity as MainActivity).progressBarManager(true)

        val retrofit = NetManager.getRetrofitConfiguration(null, true)
        val accountAPI = retrofit.create(AccountAPI::class.java)

        accountAPI.getStarredChanges().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null) {
                    changesList.addAll(Gson().fromJson(JsonUtils.trimJson(response.body()), object : TypeToken<ArrayList<ChangeInfo>>() {}.type))
                    updateTotalCount(changesList.size)
                    changePreviewAdapter.notifyItemRangeChanged(0, changesList.size)
                } else {
                    Log.e(Constants.LOG_TAG, "onResponse: Not successful")
                }
                isLoading = false
                (activity as MainActivity).progressBarManager(false)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                isLoading = false
                (activity as MainActivity).progressBarManager(false)
                Log.e(Constants.LOG_TAG, "onFailure: Not successful")
            }
        })
    }

    private fun updateTotalCount(count: Int) {
        starredCount = count
        binding.totalCount.text = getString(R.string.total_starred_changes, starredCount)
    }

}