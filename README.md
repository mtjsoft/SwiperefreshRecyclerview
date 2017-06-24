# SwiperefreshRecyclerview
支持下拉刷新、上拉加载的 Recyclerview，包括线性布局、网格布局和 流布局

## To get a Git project into your build:
## Step 1. Add the JitPack repository to your build file 
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
  compile 'com.github.mtjsoft:SwiperefreshRecyclerview:1.0.1'
}
```

## 使用 Demo

### 线性布局:

![线性布局](http://img.blog.csdn.net/20170624131953706?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjg3NzkwODM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### 网格布局:

![网格布局](http://img.blog.csdn.net/20170624132014859?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjg3NzkwODM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### 流式布局:

![流式布局](http://img.blog.csdn.net/20170624132037025?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjg3NzkwODM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### activity 继承 HHBaseListRecyclerViewActivity:

```
public class MainActivity extends HHBaseListRecyclerViewActivity<DataModel> {


    private int page_size = 30;//每页大小

    /**
     * 异步获取数据
     * @param pageIndex 页码
     * @param callback 异步获取数据后的回调  callback.onResponse(list); callback.onFailure(string);
     */
    @Override
    protected void getListDataInThread(int pageIndex, final NetCallBack<DataModel> callback) {
        //OkHttp get请求
        String url = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/" + page_size + "/" + pageIndex;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败回调
                callback.onFailure(e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                //解析json数据
                List<DataModel> modelList = new ArrayList<>();
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        DataModel model = new DataModel();
                        JSONObject jsonObject = (JSONObject) array.get(i);
                        model.setUrl(jsonObject.getString("url"));
                        modelList.add(model);
                    }
                    //成功回调
                    callback.onResponse(modelList);
                } catch (Exception e) {
                    //失败回调
                    callback.onFailure(e.getMessage().toString());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置recyclerview 的adapter
     */
    @Override
    protected HHBaseRecyclerViewAdapter<DataModel> instanceAdapter(List<DataModel> list) {
        return new MyAdapter(getBaseContext(), list);
    }

    /**
     * 设置item间隔
     *
     * @return
     */
    @Override
    protected int setItemDecoration() {
        return 10;
    }

    /**
     * 设置每页大小
     *
     * @return
     */
    @Override
    protected int setPageSize() {
        return page_size;
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

}
```
### adapter 继承 HHBaseRecyclerViewAdapter:
```
public class MyAdapter extends HHBaseRecyclerViewAdapter<DataModel> {
    private int width;
    private Context context;

    public MyAdapter(Context context, List<DataModel> list) {
        super(context, list);
        this.context = context;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
    }
    /**
     * 设置item布局
     */
    @Override
    protected int getViewHolderLaoutId() {
        return R.layout.item_image;
    }
    /**
     * 绑定数据
     */
    @Override
    protected void bindViewHolderData(HHBaseViewHolder holder, int position) {
    
        ImageView imageView = holder.getImageView(R.id.iv_imageview);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
        imageView.setLayoutParams(layoutParams);
        Glide.with(context).load(getListData().get(position).getUrl()).crossFade().into(imageView);
	
    }
}
```
### 用到的数据model
```
public class DataModel {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
```
### item_image.xml
```
<ImageView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/iv_imageview"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@color/colorAccent"
    android:scaleType="centerCrop" />
```
