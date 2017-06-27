package com.example.swiperefreshrecyclerview.imp;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.swiperefreshrecyclerview.HHBaseRecyclerViewAdapter;

import java.util.Collections;

/**
 * Created by Administrator on 2017/6/26 0026.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private HHBaseRecyclerViewAdapter mAdapter;

    public SimpleItemTouchHelperCallback(HHBaseRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager ||
                recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    /**
     * 重写拖拽不可用
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 交换位置
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //得到当拖拽的viewHolder的Position
        int fromPosition = viewHolder.getAdapterPosition();
        //拿到当前拖拽到的item的viewHolder
        int toPosition = target.getAdapterPosition();
        Collections.swap(mAdapter.getListData(), fromPosition, toPosition);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }

//    /**
//     * 长按选中Item的时候开始调用
//     *
//     * @param viewHolder
//     * @param actionState
//     */
//    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
//        }
//        super.onSelectedChanged(viewHolder, actionState);
//    }
//
//    /**
//     * 手指松开的时候还原
//     *
//     * @param recyclerView
//     * @param viewHolder
//     */
//    @Override
//    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
//        viewHolder.itemView.setBackgroundColor(0);
//    }
}