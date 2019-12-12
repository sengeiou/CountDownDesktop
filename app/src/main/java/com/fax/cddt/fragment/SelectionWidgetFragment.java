package com.fax.cddt.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.fax.cddt.R;
import com.fax.cddt.adapter.CommonAdapter;
import com.fax.cddt.adapter.ViewHolder;
import com.fax.cddt.bean.TestBen;
import com.fax.cddt.bean.TestData;
import com.fax.cddt.utils.GlideUtils;
import com.fax.cddt.utils.GsonUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;

public class SelectionWidgetFragment extends Fragment {

    private RecyclerView mRv;
    private ProgressBar mProgressBar;
    private CommonAdapter<TestBen> mAdapter;
    private List<TestBen> mData = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.selection_widget_fragment,container,false);
        mRv = view.findViewById(R.id.rv);
        mProgressBar = view.findViewById(R.id.progress_circular);
        initRecyclerView();
        reqHomeWidgetData();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initRecyclerView(){
        mAdapter = new CommonAdapter<TestBen>(getActivity(),R.layout.widget_show_item,mData) {
            @Override
            protected void convert(ViewHolder holder, TestBen testBen, int position) {
                ImageView ivWidget = holder.getView(R.id.iv_widget);
                GlideUtils.loadRoundCircleImage(getActivity(),testBen.getUrl(),ivWidget, 10);
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        mRv.setLayoutManager(gridLayoutManager);
        mRv.setAdapter(mAdapter);
    }

    private void reqHomeWidgetData(){
        OkHttpUtils.get().url("http://q2cjxj02l.bkt.clouddn.com/test1.txt").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(mProgressBar != null){
                    mProgressBar.setVisibility(View.GONE);
                }
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                if(mProgressBar != null){
                    mProgressBar.setVisibility(View.GONE);
                }
                Log.i("test_result;",response);

                if(response != null){
                    TestData testData = GsonUtils.parseJsonWithGson(response,TestData.class);
                    mData.clear();
                    mData.addAll(testData.getList());
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }




}
