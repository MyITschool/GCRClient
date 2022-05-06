package com.elseboot3909.GCRClient.UI.Main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elseboot3909.GCRClient.API.ChangesAPI
import com.elseboot3909.GCRClient.Adapter.ChangePreviewAdapter
import com.elseboot3909.GCRClient.Entities.ChangeInfo
import com.elseboot3909.GCRClient.R
import com.elseboot3909.GCRClient.UI.Change.ChangeActivity
import com.elseboot3909.GCRClient.UI.Search.SearchActivity
import com.elseboot3909.GCRClient.Utils.Constants
import com.elseboot3909.GCRClient.Utils.JsonUtils
import com.elseboot3909.GCRClient.Utils.NetManager
import com.elseboot3909.GCRClient.databinding.FragmentChangesListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class ChangesListFragment : Fragment() {

    private lateinit var binding: FragmentChangesListBinding
    private var changesList = ArrayList<ChangeInfo>()
    private var queryParams = ArrayList<String>()
    private val mGestureDetector = GestureDetector(context, object: GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent) : Boolean {
            return true
        }
    })
    private lateinit var changePreviewAdapter: ChangePreviewAdapter
    private var isFrozen = false

    private val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Constants.SEARCH_ACQUIRED -> {
                queryParams.clear()
                val search = result.data?.getStringExtra("search_string")
                if (search?.isNotEmpty() == true) {
                    queryParams.add(search)
                    binding.searchBar.setText(search)
                } else {
                    queryParams.add("status:open")
                    binding.searchBar.setText("")
                }
                val oldSize = changesList.size
                changesList.clear()
                changePreviewAdapter.notifyItemRangeRemoved(0, oldSize)
            }
            Constants.CHANGE_STATE_CHANGED -> {
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChangesListBinding.inflate(inflater, container, false)


        activity?.let {
            changePreviewAdapter = ChangePreviewAdapter(changesList, it)
        }
        binding.changesView.adapter = changePreviewAdapter
        binding.changesView.layoutManager = LinearLayoutManager(context)
        binding.changesView.isNestedScrollingEnabled = true

        binding.ctlMenu.setOnClickListener {
            activity?.findViewById<DrawerLayout>(R.id.serverNavDL)?.openDrawer(GravityCompat.START)
        }

        if (queryParams.isEmpty()) {
            queryParams.add("status:open")
        }
        binding.searchBar.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("search_string", binding.searchBar.text.toString().trim())
            activityResultLauncher.launch(intent)
        }

        if (changesList.isEmpty()) {
            getChangesList()
        }

        binding.changesView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)){
                    getChangesList()
                }
            }
        })

        binding.changesView.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                if (mGestureDetector.onTouchEvent(e) && child != null) {
                    val changeInfo = changesList[rv.getChildAdapterPosition(child)]
                    val intent = Intent(activity, ChangeActivity::class.java)
                    intent.putExtra("changeInfo", changeInfo as Serializable)
                    startActivity(intent)
                }
                return isFrozen
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) { }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) { }
        })

        binding.refreshChangesView.setOnRefreshListener {
            val oldSize = changesList.size
            changesList.clear()
            changePreviewAdapter.notifyItemRangeRemoved(0, oldSize)
            binding.refreshChangesView.isRefreshing = false
        }

        return binding.root
    }

    private fun getChangesList() {
        (activity as MainActivity).progressBarManager(true)
        isFrozen = true

        val oldSize = changesList.size

        val retrofit = NetManager.getRetrofitConfiguration(null, true)
        val changesAPI = retrofit.create(ChangesAPI::class.java)

        changesAPI.queryChanges(queryParams, 20, oldSize).enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null) {
                    changesList.addAll(Gson().fromJson(JsonUtils.trimJson(response.body()), object : TypeToken<ArrayList<ChangeInfo>>() {}.type))
                    changePreviewAdapter.notifyItemRangeChanged(oldSize, 20)
                } else {
                    Log.e(Constants.LOG_TAG, "onResponse: Not successful")
                }
                (activity as MainActivity).progressBarManager(false)
                isFrozen = false
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                (activity as MainActivity).progressBarManager(false)
                isFrozen = false
                Log.e(Constants.LOG_TAG, "onFailure: Not successful")
            }
        })
    }

}