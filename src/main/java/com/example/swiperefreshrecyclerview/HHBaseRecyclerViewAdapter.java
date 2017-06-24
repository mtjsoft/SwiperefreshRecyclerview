package com.example.swiperefreshrecyclerview;

/**
 * Created by Administrator on 2017/6/22 0022.
 */

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by android.mtj on 2017/4/1.
 */

public abstract class HHBaseRecyclerViewAdapter<T> extends
        RecyclerView.Adapter<HHBaseViewHolder> {

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();
    private Context context;
    private List<T> list;

    public HHBaseRecyclerViewAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public HHBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HHBaseViewHolder holder = null;
        if (mHeaderViews.get(viewType) != null) {
            holder = HHBaseViewHolder.createViewHolder(context,
                    mHeaderViews.get(viewType));
        } else if (mFootViews.get(viewType) != null) {
            holder = HHBaseViewHolder.createViewHolder(context,
                    mFootViews.get(viewType));
        } else {
            holder = HHBaseViewHolder.createViewHolder(context, parent,
                    getViewHolderLaoutId());
        }
        return holder;
    }

    /**
     * 获取ViewHolder layoutID
     *
     * @return
     */
    protected abstract int getViewHolderLaoutId();

    /**
     * 绑定ViewHolder
     */
    @Override
    public void onBindViewHolder(HHBaseViewHolder holder, int position) {
        if (!(isHeaderViewPos(position) || isFooterViewPos(position))) {
            bindViewHolderData(holder, position);
        }
    }

    /**
     * 子类实现绑定ViewHolder
     *
     * @param holder
     * @param position
     */
    protected abstract void bindViewHolderData(HHBaseViewHolder holder, int position);

    /**
     * 获取数据
     *
     * @return
     */
    public List<T> getListData() {
        return list;
    }

    /**
     * 获取总条目
     */
    @Override
    public int getItemCount() {
        return getHeadersCount() + getRealItemCount() + getFootersCount();
    }

    /**
     * 判断是否是头部
     *
     * @param position
     * @return
     */
    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    /**
     * 判断是否是底部
     *
     * @param position
     * @return
     */
    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    /**
     * 添加头部
     *
     * @param view
     */
    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    /**
     * 添加底部
     *
     * @param viewid
     */
    public void addFootView(View viewid) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, viewid);
    }

    /**
     * 获取数据条目
     *
     * @return
     */
    public int getRealItemCount() {
        return list.size();
    }

    /**
     * 获取头部条目
     *
     * @return
     */
    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    /**
     * 获取底部条目
     *
     * @return
     */
    public int getFootersCount() {
        return mFootViews.size();
    }

    /**
     * 移除第 i 个头部（0开始）
     *
     * @param i
     */
    public void removeHeaderView(int i) {
        if (mHeaderViews.get(BASE_ITEM_TYPE_HEADER + i) != null) {
            mHeaderViews.remove(BASE_ITEM_TYPE_HEADER + i);
        }
    }

    /**
     * 移除第 i 个底部（0开始）
     *
     * @param i
     */
    public void removeFooterView(int i) {
        if (mFootViews.get(BASE_ITEM_TYPE_FOOTER + i) != null) {
            mFootViews.remove(BASE_ITEM_TYPE_FOOTER + i);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount()
                    - getRealItemCount());
        }
        return position - getHeadersCount();
    }

    /**
     * GridLayoutManager 时，头部和底部都要占据整行
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView == null) {
            return;
        }
        final RecyclerView.LayoutManager layoutManager = recyclerView
                .getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager)
                    .setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return (isHeaderViewPos(position) || isFooterViewPos(position)) ? ((GridLayoutManager) layoutManager)
                                    .getSpanCount() : 1;
                        }
                    });
        }
    }

    /**
     * StaggeredGridLayoutManager 时，头部和底部都要占据整行
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(HHBaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }
}
