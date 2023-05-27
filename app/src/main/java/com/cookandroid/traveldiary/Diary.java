package com.cookandroid.traveldiary;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class Diary extends Fragment{
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

        addBtn = v.findViewById(R.id.addBtn);
        diaryListView = v.findViewById(R.id.diaryListView);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiaryDialog(); // 일기 작성 창을 띄우는 메서드 호출
            }
        });

        entries = loadDiaryEntries();
        adapter = new DiaryAdapter(getContext(), entries);
        diaryListView.setAdapter(adapter);

        diaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 리스트의 요소를 클릭하면 수정할 수 있는 창을 띄움
                DiaryEntry selectedEntry = entries.get(position);
                showEditDiaryDialog(selectedEntry, position);
            }
        });

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

    private void showEditDiaryDialog(DiaryEntry entry, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.edit_diary_dialog, null);
        builder.setView(dialogView);

        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        EditText contentEditText = dialogView.findViewById(R.id.contentEditText);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);
        Button deleteBtn = dialogView.findViewById(R.id.deleteBtn);

        titleEditText.setText(entry.getTitle());
        contentEditText.setText(entry.getContent());

        AlertDialog dialog = builder.create();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = titleEditText.getText().toString();
                String newContent = contentEditText.getText().toString();
                DiaryEntry updatedEntry = new DiaryEntry(newTitle, entry.getDate(), newContent);
                entries.set(position, updatedEntry);
                adapter.notifyDataSetChanged();
                dialog.dismiss();

                // 파일 내용 변경
                saveDiaryEntries(entries);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entries.remove(position);
                adapter.notifyDataSetChanged();
                dialog.dismiss();

                // 파일 내용 변경
                saveDiaryEntries(entries);
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
