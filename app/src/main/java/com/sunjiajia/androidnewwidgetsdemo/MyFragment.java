/*
 *
 *  *
 *  *  *
 *  *  *  * ===================================
 *  *  *  * Copyright (c) 2016.
 *  *  *  * 作者：安卓猴
 *  *  *  * 微博：@安卓猴
 *  *  *  * 博客：http://sunjiajia.com
 *  *  *  * Github：https://github.com/opengit
 *  *  *  *
 *  *  *  * 注意**：如果您使用或者修改该代码，请务必保留此版权信息。
 *  *  *  * ===================================
 *  *  *
 *  *  *
 *  *
 *
 */

package com.sunjiajia.androidnewwidgetsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx.RxAdapter;
import com.sunjiajia.androidnewwidgetsdemo.adapter.MyRecyclerViewAdapter;
import com.sunjiajia.androidnewwidgetsdemo.adapter.MyStaggeredViewAdapter;
import com.sunjiajia.androidnewwidgetsdemo.utils.RopUtils;
import com.sunjiajia.androidnewwidgetsdemo.utils.SnackbarUtil;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Monkey on 2015/6/29.
 */
public class MyFragment extends Fragment
    implements SwipeRefreshLayout.OnRefreshListener, MyRecyclerViewAdapter.OnItemClickListener,
    MyStaggeredViewAdapter.OnItemClickListener {

  private View mView;
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private RecyclerView mRecyclerView;
  private RecyclerView.LayoutManager mLayoutManager;
  private MyRecyclerViewAdapter mRecyclerViewAdapter;
  private MyStaggeredViewAdapter mStaggeredAdapter;

  private static final int VERTICAL_LIST = 0;
  private static final int HORIZONTAL_LIST = 1;
  private static final int VERTICAL_GRID = 2;
  private static final int HORIZONTAL_GRID = 3;
  private static final int STAGGERED_GRID = 4;

  private static final int SPAN_COUNT = 2;
  private int flag = 0;
  List<String> ss;
  private List<ProductAllInfo> productinfo;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.frag_main, container, false);
    return mView;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.id_swiperefreshlayout);
    mRecyclerView = (RecyclerView) mView.findViewById(R.id.id_recyclerview);

    flag = (int) getArguments().get("flag");
    configRecyclerView();
    attemptLeaveMsg();

    // 刷新时，指示器旋转后变化的颜色
    mSwipeRefreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
    mSwipeRefreshLayout.setOnRefreshListener(this);
  }

  private void configRecyclerView() {

    switch (flag) {
      case VERTICAL_LIST:
        mLayoutManager =
            new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        break;
      case HORIZONTAL_LIST:
        mLayoutManager =
            new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        break;
      case VERTICAL_GRID:
        mLayoutManager =
            new GridLayoutManager(getActivity(), SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        break;
      case HORIZONTAL_GRID:
        mLayoutManager =
            new GridLayoutManager(getActivity(), SPAN_COUNT, GridLayoutManager.HORIZONTAL, false);
        break;
      case STAGGERED_GRID:
        mLayoutManager =
            new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        break;
    }

//    if (flag != STAGGERED_GRID) {
//      mRecyclerViewAdapter = new MyRecyclerViewAdapter(productinfo,getActivity());
//      mRecyclerViewAdapter.setOnItemClickListener(this);
//      mRecyclerView.setAdapter(mRecyclerViewAdapter);
//    } else {
//      mStaggeredAdapter = new MyStaggeredViewAdapter(getActivity());
//      mStaggeredAdapter.setOnItemClickListener(this);
//      mRecyclerView.setAdapter(mStaggeredAdapter);
//    }


  }
  private void attemptLeaveMsg() {
    Map<String, String> form = Maps.newHashMap();
    form.put("method", "rop.product.get");
    form.put("appKey", "00001");
    form.put("v", "1.0");
    form.put("format", "json");
    String sing = RopUtils.signString(form, "qwertyuiop");
    form.put("sign", sing);




    OkGo.post(new Urls().SERVER)
            .params(form)
            .getCall(StringConvert.create(), RxAdapter.<String>create())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
              productinfo = JSON.parseArray(s, ProductAllInfo.class);
              Log.e("result",s);

              if (productinfo != null) {
                if (productinfo.get(0).getResult().size() > 0) {
                  mRecyclerViewAdapter = new MyRecyclerViewAdapter(productinfo.get(0).getResult(), getActivity());
                  mRecyclerViewAdapter.setOnItemClickListener(this);
                  mRecyclerView.setAdapter(mRecyclerViewAdapter);
                  mRecyclerViewAdapter.notifyDataSetChanged();
                  mRecyclerView.setLayoutManager(mLayoutManager);
                }
              }

            });
//            .subscribe(new Action1<String>() {
//
//
//
//              @Override
//              public void call(String s) {
//                Log.e("result",s);
//              }
//            });

  }
  @Override public void onRefresh() {

    // 刷新时模拟数据的变化
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        mSwipeRefreshLayout.setRefreshing(false);
        int temp = (int) (Math.random() * 10);
        if (flag != STAGGERED_GRID) {
          mRecyclerViewAdapter.mDatas.add(0, "new" + temp);
          mRecyclerViewAdapter.notifyDataSetChanged();
        } else {
          mStaggeredAdapter.mDatas.add(0, "new" + temp);
          mStaggeredAdapter.mHeights.add(0, (int) (Math.random() * 300) + 200);
          mStaggeredAdapter.notifyDataSetChanged();
        }
      }
    }, 1000);
  }

  @Override
  public void onItemClick(View view, int position) {
//    SnackbarUtil.show(mRecyclerView, getString(R.string.item_clicked), 0);
    if (flag != STAGGERED_GRID) {
      Intent intent=new Intent(getActivity(),MovieActivity.class);
      intent.putExtra("a", mRecyclerViewAdapter.mDatas.get(position));


      startActivity(intent);
    } else {
      Intent intent=new Intent(getActivity(),PdfActivity.class);
      intent.putExtra("a", mStaggeredAdapter.mDatas.get(position));


      startActivity(intent);
    }

  }

  @Override public void onItemLongClick(View view, int position) {
    SnackbarUtil.show(mRecyclerView, getString(R.string.item_longclicked), 0);
    Intent intent=new Intent(getActivity(),MovieActivity.class);
  }
}
