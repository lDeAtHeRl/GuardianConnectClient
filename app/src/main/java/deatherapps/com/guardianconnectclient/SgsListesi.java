package deatherapps.com.guardianconnectclient;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

import static deatherapps.com.guardianconnectclient.GCService.SG;
import static deatherapps.com.guardianconnectclient.GCService.SGSDATETIMEList;
import static deatherapps.com.guardianconnectclient.GCService.SGSList;
import static deatherapps.com.guardianconnectclient.GCService.sgsArray;


public class SgsListesi extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    public int UST_LIMIT;
    public int ALT_LIMIT;
    public int OLCUM;

    SharedPreferences preferences;

    public static LineChart mChart;
    private Menu menu;
    LineData data;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sgs_layout);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        UST_LIMIT = preferences.getInt("ust", 0);
        ALT_LIMIT = preferences.getInt("alt", 0);
        OLCUM = preferences.getInt("olcum", 0);

        TextView sggoster = findViewById(R.id.sgs_tv);

        if(Integer.parseInt(SG)>=UST_LIMIT) {
            sggoster.setTextColor(Color.RED);
        }else  if(Integer.parseInt(SG)<=ALT_LIMIT){
            sggoster.setTextColor(Color.RED);
        }else{
            sggoster.setTextColor(Color.CYAN);
        }
        sggoster.setText(SG);


        getSupportActionBar().setTitle("Ölçüm Listesi");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChart =findViewById(R.id.chart);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);






        // add data
        if (sgsArray.length()!=0) {
            setData();
        }else{
            Toast.makeText(this, "Diyabetliyiz.biz ( GC )\nÖlçümler Yüklenemedi", Toast.LENGTH_LONG).show();
        }

//        TekrarBagla();
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
//        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextColor(Color.WHITE);
        // no description text
        mChart.setDescription("Diyabetliyiz.biz");
//        mChart.setDescriptionColor(Color.CYAN);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setScaleXEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setScaleYEnabled(false);
        mChart.setBackgroundColor(R.color.colorPrimaryDark);



        LimitLine upper_limit = new LimitLine(UST_LIMIT, UST_LIMIT+" Üst Limit");
        upper_limit.setLineWidth(1f);
        upper_limit.setLineColor(Color.RED);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        upper_limit.setTextColor(Color.YELLOW);
        upper_limit.setTextSize(8f);

        LimitLine lower_limit = new LimitLine(ALT_LIMIT, ALT_LIMIT+" Alt Limit");
        lower_limit.setLineWidth(1f);
        lower_limit.setLineColor(Color.RED);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextColor(Color.YELLOW);
        lower_limit.setTextSize(8f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        rightAxis.addLimitLine(upper_limit);
        rightAxis.addLimitLine(lower_limit);
        rightAxis.setTextSize(10f);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setAxisMaxValue(500f);
        rightAxis.setAxisMinValue(0f);
        rightAxis.setYOffset(0f);
        rightAxis.enableGridDashedLine(10f, 1f, 0f);

        XAxis x = mChart.getXAxis();
        x.setTextSize(15f);
        x.setTextColor(Color.WHITE);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(upper_limit);
        leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaxValue(500f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setYOffset(0f);
        leftAxis.enableGridDashedLine(10f, 1f, 0f);
        leftAxis.setDrawZeroLine(true);

        mChart.getAxisLeft().setEnabled(true);
        mChart.getAxisRight().setEnabled(true);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);


//        mChart.getViewPortHandler().setMaximumScaleY(10000f);
//        mChart.getViewPortHandler().setMaximumScaleX(10000f);

//        mChart.animateX(0, Easing.EasingOption.EaseInOutQuart);
        mChart.setVisibleXRange(10,5);

        //  dont forget to refresh the drawing
        mChart.notifyDataSetChanged();
        mChart.invalidate();
        mChart.moveViewToX(sgsArray.length());
    }

    private ArrayList<String> setXAxisValues(){
        ArrayList<String> xVals = new ArrayList<String>();
        for (int s = 0; s < sgsArray.length(); s++) {
            xVals.add(SGSDATETIMEList.get(s));
        }
        return xVals;
    }

    private ArrayList<Entry> setYAxisValues(){
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int s = 0; s < sgsArray.length(); s++) {
            yVals.add(new Entry(Integer.valueOf(SGSList.get(s)), (s)));
        }
        return yVals;
    }

    public void setData() {
        ArrayList<String> xVals = setXAxisValues();

        ArrayList<Entry> yVals = setYAxisValues();

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "Ölçüm");
        set1.setFillAlpha(0);
        set1.setFillColor(Color.GREEN);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setDrawCubic(true);
//        set1.setColors(ColorTemplate.JOYFUL_COLORS);
//        set1.setColor(Color.BLACK);
        set1.setValueTextColor(Color.WHITE);
        set1.setCircleColor(Color.RED);
        set1.setLineWidth(1f);
        set1.setCircleRadius(5f);
        set1.setDrawCircleHole(true);
        set1.setValueTextSize(15f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        data = new LineData(xVals, dataSets);


        // set data
        mChart.setData(data);
        mChart.invalidate();
        mChart.moveViewToX(sgsArray.length());
    }



    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture
                                            lastPerformedGesture) {

//        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }


    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture
                                          lastPerformedGesture) {

//        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
//        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
//            mChart.highlightValues(null);
    }


    public void onChartLongPressed(MotionEvent me) {
//        Log.i("LongPress", "Chart longpressed.");
    }


    public void onChartDoubleTapped(MotionEvent me) {
//        Log.i("DoubleTap", "Chart double-tapped.");
    }


    public void onChartSingleTapped(MotionEvent me) {
//        Log.i("SingleTap", "Chart single-tapped.");
    }


    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
//        Log.i("Fling", "Chart flinged. VeloX: "
//                + velocityX + ", VeloY: " + velocityY);
    }


    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
//        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }


    public void onChartTranslate(MotionEvent me, float dX, float dY) {
//        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//
//        Log.i("Entry selected", e.toString());
//        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleXIndex()
//                + ", high: " + mChart.getHighestVisibleXIndex());
//
//        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin()
//                + ", xmax: " + mChart.getXChartMax()
//                + ", ymin: " + mChart.getYChartMin()
//                + ", ymax: " + mChart.getYChartMax());
    }

    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mChart.clear();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    this.finishAffinity();
                }
                return true;
            case R.id.action_kucult:
                mChart.zoomOut();
                mChart.setVisibleXRange(1,1000);
                return true;
            case R.id.action_buyut:
                mChart.setVisibleXRange(10,5);
                mChart.moveViewToX(sgsArray.length());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.olcum_listesi_actions, menu);
        this.menu = menu;
        return true;
    }


}
