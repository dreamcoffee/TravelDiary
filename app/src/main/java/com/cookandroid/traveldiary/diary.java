package com.cookandroid.traveldiary;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class diary extends Fragment{
    private ListView diaryListView;
    private Button addBtn;
    private DiaryAdapter adapter;
    private List<DiaryEntry> entries;
    private static final String DIARY_FILE_NAME = "diary_entries.txt";
    private int currentDay = 1; // 현재 일기의 번호를 나타내는 변수
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.diary, container, false);
        View main = inflater.inflate(R.layout.activity_main, container, false);

        addBtn = v.findViewById(R.id.addBtn);
        diaryListView = v.findViewById(R.id.diaryListView);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiaryDialog();
            }
        });

        entries = loadDiaryEntries(); // 저장된 일기 데이터를 로드
        adapter = new DiaryAdapter(getContext(), entries);
        diaryListView.setAdapter(adapter);

        return v;
    }

    private void showDiaryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.add_diary_dialog, null);
        builder.setView(dialogView);
        builder.setTitle(currentDay + "일차");

        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        EditText contentEditText = dialogView.findViewById(R.id.contentEditText);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);

        // 현재 날짜를 구하는 코드 (예: "2023-05-27")
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // 날짜를 표시할 TextView 설정
        TextView dateTextView = dialogView.findViewById(R.id.dateTextView);
        dateTextView.setText(currentDate);

        AlertDialog dialog = builder.create();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                DiaryEntry entry = new DiaryEntry(title, currentDate, content);
                entries.add(entry);
                adapter.notifyDataSetChanged();
                currentDay++; // 다음 일기를 위해 일기 번호 증가
                saveDiaryEntries(entries); // 일기 데이터 저장
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private List<DiaryEntry> loadDiaryEntries() {
        List<DiaryEntry> entries = new ArrayList<>();

        FileInputStream fis = null;
        BufferedReader br = null;

        try {
            fis = getContext().openFileInput(DIARY_FILE_NAME);
            br = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = br.readLine()) != null) {
                // 저장된 데이터를 원하는 형식으로 변환하여 객체로 구성
                String[] entryData = line.split("\\|");
                if (entryData.length == 3) {
                    String title = entryData[0];
                    String date = entryData[1];
                    String content = entryData[2];
                    DiaryEntry entry = new DiaryEntry(title, date, content);
                    entries.add(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entries;
    }

    private void saveDiaryEntries(List<DiaryEntry> entries) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;

        try {
            fos = getContext().openFileOutput(DIARY_FILE_NAME, Context.MODE_PRIVATE);
            bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (DiaryEntry entry : entries) {
                // 일기 데이터를 원하는 형식으로 변환하여 저장
                String entryString = entry.getTitle() + "|" + entry.getDate() + "|" + entry.getContent();
                bw.write(entryString);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
