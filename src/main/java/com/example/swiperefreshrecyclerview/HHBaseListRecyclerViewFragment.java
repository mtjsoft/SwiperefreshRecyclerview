package com.example.swiperefreshrecyclerview;

/**
 * Created by Administrator on 2017/6/22 0022.
 */

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swiperefreshrecyclerview.imp.HHBaseOnClickListener;
import com.example.swiperefreshrecyclerview.imp.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android.mtj on 2017/6/10.
 */

public abstract class HHBaseListRecyclerViewFragment<T> extends
        Fragment implements HHBaseOnClickListener {
    // 获取listview显示数据的发送消息的what
    public static final int GET_LIST_DATA = 0;
    private int mark = 2;// 【0：LinearLayoutManager 1：GridLayoutManager 2：StaggeredGridLayoutManager】
    private int pager = 1;//页码
    private int pager_size = 30;//每页大小
    private int mLastVisibleItem;//最后一个可见行
    private List<T> list = new ArrayList<>();
    private List<T> temp = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView tipTextView;
    private HHBaseRecyclerViewAdapter<T> adapter;
    //
    private int count = 2;// 列数，默认为每行2列
    private View footView;//上拉加载更多布局
    //
    private boolean isSwipeRefresh = false;//是否是下拉刷新
    //
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    //
    private boolean load_more = true;// 是否允许加载更多功能
    private boolean refresh = true;// 是否允许下拉功能
    private boolean is_move = false;// 是否允许拖拽功能


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.activity_recycleview, null);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        tipTextView = (TextView) view.findViewById(R.id.tv_tip);
        initValues();
        onPageLoad();
        initListeners();
        return view;
    }

    public void onPageLoad() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        getListData();
    }

    public void initValues() {
        // TODO Auto-generated method stub
        if (setCount() > 0) {
            count = setCount();
        }
        mark = setLayoutManagerType();
        pager_size = setPageSize();
        linearLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), count);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(count,
                StaggeredGridLayoutManager.VERTICAL);

        if (setItemDecoration() >= 0) {
            // 设置间隔
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.left = setItemDecoration() / 2;
                    outRect.right = setItemDecoration() / 2;
                    outRect.top = setItemDecoration();
                }
            });
            recyclerView.setPadding(setItemDecoration() / 2, 0, setItemDecoration() / 2, 0);
        }
        //改变加载显示的颜色
//        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.YELLOW);
        //设置LayoutManager
        switch (mark) {
            case 0:
                recyclerView.setLayoutManager(linearLayoutManager);
                break;
            case 1:
                recyclerView.setLayoutManager(gridLayoutManager);
                break;
            case 2:
                recyclerView
                        .setLayoutManager(staggeredGridLayoutManager);
                break;
        }
    }

    public void initListeners() {
        // TODO Auto-generated method stub
        if (refresh) {
            swipeRefreshLayout
                    .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            isSwipeRefresh = true;
                            if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(true);
                            }
                            pager = 1;
                            getListData();
                        }
                    });
        }
        if (load_more) {
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                                mLastVisibleItem = ((LinearLayoutManager) recyclerView
                                        .getLayoutManager())
                                        .findLastVisibleItemPosition();
                            } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                                View view = recyclerView.getLayoutManager()
                                        .getChildAt(
                                                recyclerView.getLayoutManager()
                                                        .getChildCount() - 1);
                                mLastVisibleItem = recyclerView
                                        .getLayoutManager().getPosition(view);
                            } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                                int[] lastPositions = ((StaggeredGridLayoutManager) recyclerView
                                        .getLayoutManager())
                                        .findLastVisibleItemPositions(null);
                                mLastVisibleItem = findMax(lastPositions);
                            }
                        }

                        @Override
                        public void onScrollStateChanged(
                                RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (adapter != null && newState == RecyclerView.SCROLL_STATE_IDLE
                                    && mLastVisibleItem >= adapter
                                    .getItemCount() - 1
                                    && pager_size == temp.size()) {
                                pager++;
                                getListData();
                            }
                        }
                    });
        }
    }

    /**
     * 取出最大值
     *
     * @param positions
     * @return
     */
    private int findMax(int[] positions) {
        int max = positions[0];
        for (int value : positions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 获取listView显示的数据
     */
    private void getListData() {
        tipTextView.setVisibility(View.GONE);
        getListDataInThread(pager, new NetCallBack<T>() {
            @Override
            public void onFailure(String error) {
                temp = null;
                Message message = new Message();
                message.what = GET_LIST_DATA;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(List list) {
                temp = list;
                Message message = new Message();
                message.what = GET_LIST_DATA;
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 获取请求参数
     *
     * @param pageIndex 当前获取的是第几页的数据
     * @return
     */
    protected abstract void getListDataInThread(int pageIndex, NetCallBack<T> callBack);

    /**
     * 实例化一个Adapter
     *
     * @param list listView显示的数据的集合
     * @return
     */
    protected abstract HHBaseRecyclerViewAdapter<T> instanceAdapter(List<T> list);

    /**
     * 设置item装饰间距
     *
     * @return
     */
    protected abstract int setItemDecoration();

    /**
     * 获取当前页每页获取的数据的大小
     *
     * @return
     */
    protected abstract int setPageSize();

    /**
     * 设置LayoutManager类型，默认2
     * 【
     * 0：LinearLayoutManager ，
     * 1：GridLayoutManager，
     * 2：StaggeredGridLayoutManager
     * 】
     * 设置1、2时，需用getCount（）方法，设置列数，默认2
     *
     * @return
     */
    protected abstract int setLayoutManagerType();

    /**
     * 设置每行列数，默认2
     */
    protected abstract int setCount();


    /**
     * 设置是否下拉刷新
     */

    public void setIsRefresh(Boolean refresh) {
        this.refresh = refresh;
    }

    /**
     * 设置是否拖拽
     */

    public void setIsMove(Boolean is_move) {
        this.is_move = is_move;
    }

    /**
     * 设置是否加载更多
     *
     * @param load_more true是
     */
    public void setIsLoadMore(Boolean load_more) {
        this.load_more = load_more;
    }

    /**
     * 设置底部加载更多
     *
     * @param footView
     */
    public void setFootView(View footView) {
        this.footView = footView;
    }

    /**
     * 返回列表数据
     *
     * @return
     */
    public List<T> getPagerListData() {
        return list;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * 下载刷新的时候执行的代码
     */
    public void onRefresh() {
        pager = 1;
        getListData();
    }

    /**
     * 设置页码
     *
     * @param pageIndex
     */
    public void setPageIndex(int pageIndex) {
        this.pager = pageIndex;
    }

    /**
     * 当前页码
     *
     * @return
     */
    public int getPageIndex() {
        return this.pager;
    }

    /**
     * 异步请求数据回掉
     *
     * @param <T>
     */
    public interface NetCallBack<T> {
        void onFailure(String error);

        void onResponse(List<T> list);
    }

    /**
     * 设置数据
     */
    private void setData() {
        if ((temp == null || temp.size() != pager_size) && footView != null && load_more) {
            adapter.removeFooterView(0);
            adapter.notifyDataSetChanged();
        }
        if (temp == null) {
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            //加载失败
            tipTextView.setVisibility(View.VISIBLE);
            tipTextView.setText(getString(R.string.load_fa));
        } else if (temp.size() == 0) {
            if (pager == 1) {
                if (list == null) {
                    list = new ArrayList<>();
                } else {
                    list.clear();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                //暂无数据
                tipTextView.setVisibility(View.VISIBLE);
                tipTextView.setText(getString(R.string.no_data));
            } else {
                //暂无数据提示
                Toast.makeText(getContext(), R.string.no_data, Toast.LENGTH_LONG).show();
            }
        } else {
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (pager == 1) {
                if (list != null && list.size() > 0) {
                    list.clear();
                }
                list.addAll(temp);
                if (isSwipeRefresh && adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = instanceAdapter(list);
                    recyclerView.setAdapter(adapter);
                }
                if (temp.size() == pager_size && adapter.getFootersCount() == 0 && load_more) {
                    if (footView == null) {
                        footView = View.inflate(getContext(), R.layout.hh_include_footer,
                                null);
                    }
                    adapter.addFootView(footView);
                }
            } else {
                list.addAll(temp);
                adapter.notifyDataSetChanged();
            }
            if (is_move) {
                //添加ItemTouchHelper
                //先实例化Callback
                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
                //用Callback构造ItemtouchHelper
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                //调用ItemTouchHelper的attachToRecyclerView方法建立联系
                touchHelper.attachToRecyclerView(recyclerView);
            }
            adapter.setItemClick(this);
            isSwipeRefresh = false;
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == GET_LIST_DATA) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                setData();
            }
        }
    };


}
