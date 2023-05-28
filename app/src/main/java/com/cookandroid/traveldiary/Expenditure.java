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

public class Expenditure extends Fragment {
    private ListView expendListView;
    private Button addBtn;
    private ExpenditureAdapter adapter;
    private List<ExpenditureEntry> entries;
    private static final String DIARY_FILE_NAME = "expenditure_entries.txt";

    // 추가된 변수들
    private EditText spendDateEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.expenditure, container, false);

        addBtn = v.findViewById(R.id.addBtn);
        expendListView = v.findViewById(R.id.expendListView);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExpendDialog(); // 일기 작성 창을 띄우는 메서드 호출
            }
        });

        entries = loadExpendEntries();
        adapter = new ExpenditureAdapter(getContext(), entries);
        expendListView.setAdapter(adapter);

        expendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 리스트의 요소를 클릭하면 수정할 수 있는 창을 띄움
                ExpenditureEntry selectedEntry = entries.get(position);
                showEditExpendDialog(selectedEntry, position);
            }
        });

        return v;
    }

    private void showExpendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.add_expenditure_dialog, null);
        builder.setView(dialogView);

        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        spendDateEditText = dialogView.findViewById(R.id.spendDateEditText); // 수정된 부분
        EditText spendEditText = dialogView.findViewById(R.id.spendEditText);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);

        AlertDialog dialog = builder.create();

        spendDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(); // 시작일 DatePickerDialog 표시
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String spendDate = spendDateEditText.getText().toString();
                String price = spendEditText.getText().toString();
                ExpenditureEntry entry = new ExpenditureEntry(title, spendDate, price);
                entries.add(entry);
                saveExpendEntries(entries);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showEditExpendDialog(ExpenditureEntry entry, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.edit_expenditure_dialog, null);
        builder.setView(dialogView);

        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        spendDateEditText = dialogView.findViewById(R.id.spendDateEditText); // 수정된 부분
        EditText spendEditText = dialogView.findViewById(R.id.spendEditText);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);
        Button deleteBtn = dialogView.findViewById(R.id.deleteBtn);

        // 수정된 부분 시작일과 종료일을 설정할 EditText와 관련 코드
        spendDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(); // 시작일 DatePickerDialog 표시
            }
        });

        titleEditText.setText(entry.getTitle());
        spendDateEditText.setText(entry.getSpendDate());
        spendEditText.setText(entry.getPrice());

        AlertDialog dialog = builder.create();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String spendDate = spendDateEditText.getText().toString();
                String price = spendEditText.getText().toString();
                entry.setTitle(title);
                entry.setPrice(spendDate);
                entry.setSpendDate(price); // 시작일 설정
                entries.set(position, entry);
                saveExpendEntries(entries);
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
                saveExpendEntries(entries);
            }
        });

        dialog.show();
    }

    // DatePickerDialog를 표시하는 메서드
    private void showDatePickerDialog() {
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
                spendDateEditText.setText(selectedDate);
            }
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    // saveScheduleEntries() 메서드 수정
    private void saveExpendEntries(List<ExpenditureEntry> entries) {
        FileOutputStream fos = null;
        BufferedWriter writer = null;

        try {
            fos = getContext().openFileOutput(DIARY_FILE_NAME, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(fos));

            for (ExpenditureEntry entry : entries) {
                String line = entry.getTitle() + "|" + entry.getSpendDate() + "|" + entry.getPrice();
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
    private List<ExpenditureEntry> loadExpendEntries() {
        List<ExpenditureEntry> entries = new ArrayList<>();
        FileInputStream fis = null;
        BufferedReader reader = null;

        try {
            fis = getContext().openFileInput(DIARY_FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String title = parts[0];
                    String date = parts[1];
                    String spend = parts[2];
                    ExpenditureEntry entry = new ExpenditureEntry(title, date, spend);
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
