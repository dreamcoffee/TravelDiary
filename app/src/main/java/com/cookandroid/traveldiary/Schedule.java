package com.cookandroid.traveldiary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Schedule extends Fragment {
    private ListView diaryListView;
    private Button addBtn;
    private ScheduleAdapter adapter;
    private List<ScheduleEntry> entries;
    private static final String DIARY_FILE_NAME = "schedule_entries.txt";

    // 추가된 변수들
    private EditText startDateEditText;
    private EditText endDateEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.schedule, container, false);

        addBtn = v.findViewById(R.id.addBtn);
        diaryListView = v.findViewById(R.id.diaryListView);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScheduleDialog(); // 일기 작성 창을 띄우는 메서드 호출
            }
        });

        entries = loadScheduleEntries();
        adapter = new ScheduleAdapter(getContext(), entries);
        diaryListView.setAdapter(adapter);

        diaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 리스트의 요소를 클릭하면 수정할 수 있는 창을 띄움
                ScheduleEntry selectedEntry = entries.get(position);
                showEditScheduleDialog(selectedEntry, position);
            }
        });

        return v;
    }

    private void showScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.add_schedule_dialog, null);
        builder.setView(dialogView);

        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        EditText contentEditText = dialogView.findViewById(R.id.contentEditText);
        startDateEditText = dialogView.findViewById(R.id.startTravelDate); // 수정: 변수명 변경
        endDateEditText = dialogView.findViewById(R.id.endTravelDate); // 수정: 변수명 변경
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);

        AlertDialog dialog = builder.create();

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true); // 시작일 DatePickerDialog 표시
            }
        });
        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(false); // 종료일 DatePickerDialog 표시
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                String startDate = startDateEditText.getText().toString();
                String endDate = endDateEditText.getText().toString();
                ScheduleEntry entry = new ScheduleEntry(title, startDate, endDate, content);
                entries.add(entry);
                saveScheduleEntries(entries);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showEditScheduleDialog(ScheduleEntry entry, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.edit_schedule_dialog, null);
        builder.setView(dialogView);

        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        EditText contentEditText = dialogView.findViewById(R.id.contentEditText);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);
        Button deleteBtn = dialogView.findViewById(R.id.deleteBtn);

        // 추가된 부분 시작일과 종료일을 설정할 EditText와 관련 코드
        startDateEditText = dialogView.findViewById(R.id.startTravelDate); // 수정: 변수명 변경
        endDateEditText = dialogView.findViewById(R.id.endTravelDate); // 수정: 변수명 변경
        startDateEditText.setText(entry.getStartDate());
        endDateEditText.setText(entry.getEndDate());
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true); // 시작일 DatePickerDialog 표시
            }
        });
        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(false); // 종료일 DatePickerDialog 표시
            }
        });

        titleEditText.setText(entry.getTitle());
        contentEditText.setText(entry.getContent());

        AlertDialog dialog = builder.create();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                entry.setTitle(title);
                entry.setContent(content);
                entry.setStartDate(startDateEditText.getText().toString()); // 시작일 설정
                entry.setEndDate(endDateEditText.getText().toString()); // 종료일 설정
                entries.set(position, entry);
                saveScheduleEntries(entries);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entries.remove(position);
                adapter.notifyDataSetChanged();
                dialog.dismiss();

                // 파일 내용 변경
                saveScheduleEntries(entries);
            }
        });

        dialog.show();
    }

    // DatePickerDialog를 표시하는 메서드
    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // 날짜 선택 후 처리하는 로직 추가
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                calendar.set(year, month, dayOfMonth);
                String selectedDate = sdf.format(calendar.getTime());
                if (isStartDate) {
                    startDateEditText.setText(selectedDate);
                } else {
                    endDateEditText.setText(selectedDate);
                }
            }
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    // saveScheduleEntries() 메서드 수정
    private void saveScheduleEntries(List<ScheduleEntry> entries) {
        FileOutputStream fos = null;
        BufferedWriter writer = null;

        try {
            fos = getContext().openFileOutput(DIARY_FILE_NAME, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(fos));

            for (ScheduleEntry entry : entries) {
                String line = entry.getTitle() + "|" + entry.getStartDate() + "|" + entry.getEndDate() + "|"+ entry.getContent().replace("\n", "[br]");
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // loadScheduleEntries() 메서드 수정
    private List<ScheduleEntry> loadScheduleEntries() {
        List<ScheduleEntry> entries = new ArrayList<>();
        FileInputStream fis = null;
        BufferedReader reader = null;

        try {
            fis = getContext().openFileInput(DIARY_FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    String title = parts[0];
                    String startDate = parts[1];
                    String endDate = parts[2];
                    String content = parts[3].replace("[br]", "\n");
                    ScheduleEntry entry = new ScheduleEntry(title, startDate, endDate, content);
                    entries.add(entry);
                } else {
                    // 처리할 내용이 없을 경우
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entries;
    }
}