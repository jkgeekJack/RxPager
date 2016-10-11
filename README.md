# RxPager
RxJava做的轮播图
RxJava有个Observable.interval的方法可以每个几秒执行一次，正好用于做一个轮播图
我们采用两边加一个的方法，尾部加原来的头部，头部加原来的尾部
然后监听切换页面时，如果是第一个的话就改为原来的最后一个，如果是最后一个的话就改为原来的第一个
![这里写图片描述](http://img.blog.csdn.net/20161011080629649)

##**首先导入依赖库**

```
compile 'io.reactivex:rxjava:1.0.14'
compile 'io.reactivex:rxandroid:1.0.1'
```

##**创建一个继承自ViewPager的类**
重写它的两个构造方法
```
public class MyRxPager extends ViewPager {
    public MyRxPager(Context context) {
        super(context);
    }
    public MyRxPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}

```
##**在这个ViewPager里写一个继承自PagerAdapter的内部类**
用于显示图片

```
private class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImgs.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
	        //用ImageView装这个图片
            ImageView view = new ImageView(mContext);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            view.setImageResource(mImgs.get(position));
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(view);
            return view;
        }
    }
```
##**在这个Viewpager里写一个OnPageChangeListener的实例**

```
private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                // 停下的时候
                case ViewPager.SCROLL_STATE_IDLE:
                    Log.d("page", getCurrentItem() + "");
                    //如果0就改为原来的最后一个
                    if (getCurrentItem() == 0) {
                        setCurrentItem(mImgs.size()-2, false);
                    //如果是后来的最后一个就改为1
                    } else if (getCurrentItem() == mImgs.size()-1) {
                        Toast.makeText(mContext, "" + getCurrentItem(), Toast.LENGTH_SHORT).show();
                        setCurrentItem(1, false);
                    }
                    mCurrentPage = getCurrentItem();
                    mIsTouch = false;
                    break;
                //用手滑动ViewPager的时候
                case ViewPager.SCROLL_STATE_DRAGGING:
                    mIsTouch = true;
                    break;
            }
        }
    };
```

##**然后再新建一个方法，传入所要展示的图片**

```
public void init(Context context, LinkedList<Integer> imgIds) {
        mContext = context;
        mImgs.clear();
        mImgs.addAll(imgIds);
        //尾部加原来的头部
        mImgs.add(imgIds.get(0));
        //头部加原来的尾部
        mCurrentPage = 1;
        setAdapter(new MyAdapter());
        setCurrentItem(mCurrentPage);
        Observable.interval(5, 5, TimeUnit.SECONDS)  // 5s的延迟，5s的循环时间
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        // 进行轮播操作
                        // 如果正在触摸就不执行自动轮播
                        if (!mIsTouch) {
                            mCurrentPage++;
                            setCurrentItem(mCurrentPage);
                        }
                    }
                });
        addOnPageChangeListener(mOnPageChangeListener);
    }
```
##**使用方法**

```
public class MainActivity extends AppCompatActivity {
    private MyRxPager mRxPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinkedList<Integer> mImgids=new LinkedList<Integer>();
        mImgids.add(R.drawable.bg2);
        mImgids.add(R.drawable.bg3);
        mImgids.add(R.drawable.bg4);
        mImgids.add(R.drawable.bg5);
        mImgids.add(R.drawable.bg6);
        mRxPager= (MyRxPager) findViewById(R.id.vp);
        mRxPager.init(this,mImgids);
    }
}
```

##**效果图**
![这里写图片描述](http://img.blog.csdn.net/20161011081633996)

我这里传入的是Drawable的图
你也可以修改一下传入Bitmap
甚至你可以传入url，然后PagerAdapter里的ImageView用Glide加载也行

##**[源码下载](https://github.com/jkgeekJack/RxPager)**

