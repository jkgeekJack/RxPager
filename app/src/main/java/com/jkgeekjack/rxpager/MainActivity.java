package com.jkgeekjack.rxpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.LinkedList;

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
