package com.jkgeekjack.rxpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2016/10/10.
 */
public class MyRxPager extends ViewPager {
    private Context mContext;
    //是否在触摸
    public boolean mIsTouch = false;
    private int mCurrentPage;
    private LinkedList<Integer> mImgs = new LinkedList<Integer>();
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
                    //如果0就改为原来的最后一个
                    if (getCurrentItem() == 0) {
                        setCurrentItem(mImgs.size()-2, false);
                    //如果是后来的最后一个就改为1
                    } else if (getCurrentItem() == mImgs.size()-1) {
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

    private Disposable mDisposable;

    public MyRxPager(Context context) {
        super(context);
    }

    public MyRxPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context, LinkedList<Integer> imgIds) {
        mContext = context;
        mImgs.clear();
        mImgs.addAll(imgIds);
        //尾部加原来的头部
        mImgs.add(imgIds.get(0));
        //头部加原来的尾部
        mImgs.addFirst(imgIds.get(imgIds.size() - 1));
        mCurrentPage = 1;
        setAdapter(new MyAdapter());
        setCurrentItem(mCurrentPage);
        mDisposable = Observable.interval(5, 5, TimeUnit.SECONDS)  // 5s的延迟，5s的循环时间
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
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

    /**
     * 取消自动轮播
     */
    public void stopInterval() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
