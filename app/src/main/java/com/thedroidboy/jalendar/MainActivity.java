package com.thedroidboy.jalendar;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.thedroidboy.jalendar.adapters.PagerAdapterMonth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapterMonth(getSupportFragmentManager()));
        viewPager.setCurrentItem(PagerAdapterMonth.INITIAL_OFFSET);
        viewPager.setOffscreenPageLimit(3);
        new Thread(() -> {
            for (int i = PagerAdapterMonth.INITIAL_OFFSET - 10; i < PagerAdapterMonth.INITIAL_OFFSET + 10; i++){
                JewCalendarPool.obtain(i);
            }
        }).start();
    }

    /*
     RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MonthVM monthVM = ViewModelProviders.of(this).get(MonthVM.class);
        final CalendarRecyclerAdapter recyclerAdapter = new CalendarRecyclerAdapter();
        monthVM.init();
        monthVM.getMonthList().observe(this, recyclerAdapter::setList);
        recyclerView.setAdapter(recyclerAdapter);
        LinearSnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                return targetPosition;
            }
        };
        snapHelper.attachToRecyclerView(recyclerView);
    * */
}
