package com.wangly.linegraph;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.wangly.linegraph.view.MoveDemoView;
import com.wangly.linegraph.view.SegmentedGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public static boolean doubleClick;

    private List<String> mXanixValues = new ArrayList<String>();
    private List<Integer> mDate = new ArrayList<Integer>();

    @BindView(R.id.iv_screenSwitch)
    ImageView iv_screenSwitch;

    private MoveDemoView sportHorizontalView;

    private SegmentedGroup segmented2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initView();

    }
    private void initData() {
        String[] xTimes = getResources().getStringArray(R.array.TimeArray);
        for (String time : xTimes) {
            mXanixValues.add(time);
        }

        int resultArray[] = getResources().getIntArray(R.array.ResultArray);

        for (int result : resultArray) {
            mDate.add(result);
        }

    }

    private void initView() {
        List<String> mXSevenDatas = mXanixValues.subList(0, 7);
        List<Integer> mYSevenDatas = mDate.subList(0, 7);
        sportHorizontalView = (MoveDemoView) findViewById(R.id.moveView);
        segmented2 = (SegmentedGroup) findViewById(R.id.segmented2);
        sportHorizontalView.addXYDates(mXSevenDatas, mYSevenDatas, mYSevenDatas.size());


        segmented2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.tv_SevenData) {
                    List<String> mXSevenDatas = mXanixValues.subList(0, 7);
                    List<Integer> mYSevenDatas = mDate.subList(0, 7);
                    sportHorizontalView.addXYDates(mXSevenDatas, mYSevenDatas, mYSevenDatas.size());
                } else if (checkedId == R.id.tv_ThirtyData) {
                    List<String> mXThirtyDatas = mXanixValues.subList(0, 30);
                    List<Integer> mYThirtyDatas = mDate.subList(0, 30);
                    sportHorizontalView.addXYDates(mXThirtyDatas, mYThirtyDatas, mYThirtyDatas.size());
                }
            }
        });
    }


    @OnClick(R.id.iv_screenSwitch)
    public void switchScren(View view) {
        Toast.makeText(this, "执行了耶·····", Toast.LENGTH_LONG).show();
    }





}
