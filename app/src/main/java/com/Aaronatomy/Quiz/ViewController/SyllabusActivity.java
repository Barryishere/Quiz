package com.Aaronatomy.Quiz.ViewController;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.Aaronatomy.Quiz.Utility.TableItem;
import com.Aaronatomy.Quiz.Protocol.QNormalActivity;
import com.Aaronatomy.Quiz.R;
import com.Aaronatomy.Quiz.Utility.ColorGenerator;
import com.Aaronatomy.Quiz.Model.PeekTable;
import com.Aaronatomy.Quiz.Utility.QApplication;
import com.Aaronatomy.Quiz.Utility.SizeConvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SyllabusActivity extends QNormalActivity {
    public FrameLayout col1, col2, col3, col4, col5, col6, col7;
    public String[] semesterArray;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        setView();
    }

    private AdapterView.OnItemSelectedListener listenerSemester =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String year = list.get(i).split("第")[0];
                    String term = "";
                    if (list.get(i).contains("一"))
                        term = "1";
                    if (list.get(i).contains("二"))
                        term = "2";
                    col1.removeAllViews();
                    col2.removeAllViews();
                    col3.removeAllViews();
                    col4.removeAllViews();
                    col5.removeAllViews();
                    col6.removeAllViews();
                    col7.removeAllViews();
                    new TableLayer(SyllabusActivity.this).execute(year, term);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            };

    private void setView() {
        Spinner semester = findViewById(R.id.spinnerSemester);
        col1 = findViewById(R.id.col1);
        col2 = findViewById(R.id.col2);
        col3 = findViewById(R.id.col3);
        col4 = findViewById(R.id.col4);
        col5 = findViewById(R.id.col5);
        col6 = findViewById(R.id.col6);
        col7 = findViewById(R.id.col7);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this,
                R.layout.quiz_spinner_item);
        if (QApplication.getSemester() != null) {
            list = new ArrayList<>();
            semesterArray = QApplication.getSemester();
            for (String s : semesterArray) {
                list.add(s + "第一学期");
                list.add(s + "第二学期");
            }
            semesterAdapter.addAll(list);
        }

        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semester.setAdapter(semesterAdapter);
        semester.setOnItemSelectedListener(listenerSemester);
    }

    // 对课表界面进行布局
    static class TableLayer extends AsyncTask<String, Void, Void> {
        private ArrayList<TableItem> tableItems;
        private final WeakReference<SyllabusActivity> weakReference;

        TableLayer(SyllabusActivity syllabusActivity) {
            this.weakReference = new WeakReference<>(syllabusActivity);
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (strings.length == 2)
                tableItems = parse(strings[0], strings[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (tableItems != null)
                arrange(tableItems);
        }

        private ArrayList<TableItem> parse(String semester, String term) {
            ArrayList<TableItem> tableItems = new ArrayList<>();
            String htmlHolder = new PeekTable().startPeek(semester, term);
            Document document = Jsoup.parse(htmlHolder);
            Element table = document.getElementById("Table1");
            Elements cells = table.getElementsByTag("td");

            for (int i = 0; i < cells.size(); i++) {
                Element cell = cells.get(i);
                if (!cell.html().contains("<br>"))
                    cells.remove(cell);
            }

            for (Element useful : cells) {
                List<TextNode> data = useful.textNodes();
                if (data.size() == 4)
                    tableItems.add(new TableItem(
                            data.get(0).getWholeText(),
                            data.get(1).getWholeText(),
                            data.get(2).getWholeText(),
                            data.get(3).getWholeText()));
            }

            return tableItems;
        }

        private void arrange(ArrayList<TableItem> tableItems) {
            int offset;
            FrameLayout.LayoutParams params;
            SyllabusActivity activity = this.weakReference.get();
            int commonBlockHeight = SizeConvent.dp2PX(activity, 70);
            for (TableItem item : tableItems) {
                TextView textView = new TextView(activity);
                textView.setText(item.toString());
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(12);
                textView.setTextColor(activity.getResources().getColor(R.color.colorBlackTrans99));
                textView.setPadding(2, 2, 2, 2);
                textView.setBackgroundColor(new ColorGenerator(activity).generate());
                if (item.getDay() != null)
                    switch (item.getDay()) {
                        case DAY1:
                            activity.col1.addView(textView);
                            break;
                        case DAY2:
                            activity.col2.addView(textView);
                            break;
                        case DAY3:
                            activity.col3.addView(textView);
                            break;
                        case DAY4:
                            activity.col4.addView(textView);
                            break;
                        case DAY5:
                            activity.col5.addView(textView);
                            break;
                        case DAY6:
                            activity.col6.addView(textView);
                            break;
                        case DAY7:
                            activity.col7.addView(textView);
                            break;
                    }
                if (item.getPeriod() != null) {
                    offset = item.getPeriod()[0] - 1;
                    params = new FrameLayout.LayoutParams(textView.getLayoutParams());
                    params.topMargin = offset * commonBlockHeight;
                    params.height = (item.getPeriod().length * commonBlockHeight);
                    textView.setLayoutParams(params);
                }
            }
        }
    }
}
