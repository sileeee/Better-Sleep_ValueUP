package org.techtown.lampwitharduino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

public class Calendar extends AppCompatActivity {

    private static final String TAG = "Calendar";
    private CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                //year, month, date
                //i1+1, i2, i

                java.util.Calendar cal = java.util.Calendar.getInstance();
                int weekday = cal.get(java.util.Calendar.DAY_OF_WEEK);
                String DayOfWeek = "";

                switch (weekday){
                    case 1:
                        DayOfWeek = "일요일";
                        break;
                    case 2:
                        DayOfWeek = "월요일";
                        break;
                    case 3:
                        DayOfWeek = "화요일";
                        break;
                    case 4:
                        DayOfWeek = "수요일";
                        break;
                    case 5:
                        DayOfWeek = "목요일";
                        break;
                    case 6:
                        DayOfWeek = "금요일";
                        break;
                    case 7:
                        DayOfWeek = "토요일";
                        break;
                }

                String date = i + "년 " + (i1+1) + "월 " + i2 + "일 ";
                Log.d(TAG, "onSelectedDayChange : yyyy년 mm월 dd일 w요일: ");


                Intent intent = new Intent(Calendar.this, MainCalendar.class);
                intent.putExtra("date", date);
                startActivity(intent);


                //요일 가져오는거 다시 해보기
                weekday = cal.get(java.util.Calendar.DAY_OF_WEEK);
            }
        });
    }
}