# SwiperefreshRecyclerview
## 支持下拉刷新、上拉加载的 Recyclerview，包括线性布局、网格布局和 流布局。
## 只需要在adapter中绑定数据，其他的交给我，就是这么6。

### To get a Git project into your build:
### Step 1. Add the JitPack repository to your build file 
### Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}      
```
## Step 2. Add the dependency
```
dependencies {     
  compile 'com.github.mtjsoft:SwiperefreshRecyclerview:1.1.0'
}
```
## GitHub 源码: [SwiperefreshRecyclerview源码](https://github.com/mtjsoft/SwiperefreshRecyclerview)

## 使用 Demo: [SwiperefreshRecyclerviewDemo](https://github.com/mtjsoft/SwiperefreshRecyclerviewDemo)

### 线性布局:

![线性布局](http://img.blog.csdn.net/20170624131953706?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjg3NzkwODM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### 网格布局:

![网格布局](http://img.blog.csdn.net/20170624132014859?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjg3NzkwODM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### 流式布局:

![流式布局](http://img.blog.csdn.net/20170624132037025?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjg3NzkwODM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## 1、activity 继承 HHBaseListRecyclerViewActivity: 
 ①getListDataInThread()异步获取数据。
 ②setLayoutManagerType()方法即可实现  线性布局、网格布局 和 流布局。

```
/**
     * 异步获取数据
     *
     * @param pageIndex 页码
     * @param callBack  异步获取数据后的回调 
     * callback.onResponse(list);
     * callback.onFailure(string);
     */
    @Override
    protected void getListDataInThread(int pageIndex, final  NetCallBack<DataModel> callBack) {
    }
    /**
     * 设置recyclerview 的adapter
     */
    @Override
    protected HHBaseRecyclerViewAdapter<DataModel> instanceAdapter(List<DataModel> list) {
       //return 自己的adapter
        return new MyAdapterDemo(getContext(), list);
    }
    /**
     * 设置item之间的距离
     *
     * @return
     */
    @Override
    protected int setItemDecoration() {
        return 10;
    }

    /**
     * 设置每页获取数据的大小
     *
     * @return
     */
    @Override
    protected int setPageSize() {
        return 30;
    }

    /**
     * 设置LayoutManager类型，默认2
     * 【
     * 0：LinearLayoutManager ，
     * 1：GridLayoutManager，
     * 2：StaggeredGridLayoutManager
     * 】
     * 设置1、2时，setCount（）方法，设置列数，默认2
     *
     * @return
     */
    @Override
    protected int setLayoutManagerType() {
        return 2;
    }

    /**
     * 设置每行列数，默认2
     */
    @Override
    protected int setCount() {
        return 2;
    }
```

## 2、Fragment 继承 HHBaseListRecyclerViewFragment ，使用方法与 Activity 继承HHBaseListRecyclerViewActivity的一致。
**用fragment来实现，可以嵌套在任意activity里使用。**


## 3、adapter 继承 HHBaseRecyclerViewAdapter : 
通过下面两个方法就可以实现view复用，数据绑定，简单高效
```
    /**
     * 设置item布局
     */
    @Override
    protected int getViewHolderLaoutId() {
        return R.layout.item;//自己的item.xml
    }
    /**
     * 绑定数据
     */
    @Override
    protected void bindViewHolderData(HHBaseViewHolder holder, int position) {
    //通过holder得到控件，通过position得到对应数据，进行数据绑定
    }
```


## 4、activity  和 fragment 中可以自主设置以下等方法。

```
    /**
     * 设置是否下拉刷新
     */
    setIsRefresh(Boolean refresh)

    /**
     * 设置是否加载更多
     *
     * @param load_more true是
     */
    setIsLoadMore(Boolean load_more)


    /**
     * 设置底部加载更多的loading布局，不设置时，使用默认布局
     *
     * @param footView
     */
    setFootView(View footView)

    /**
     * 获取列表数据
     */
    getPagerListData()

    /**
     * 获取recyclerview
     */
    getRecyclerView()

    /**
     * 刷新页面数据
     */
    onRefresh()
    
    /**
     * 设置页码
     *
     * @param pageIndex
     */
    setPageIndex(int pageIndex)

    /**
     * 当前页码
     */
    getPageIndex()
```
