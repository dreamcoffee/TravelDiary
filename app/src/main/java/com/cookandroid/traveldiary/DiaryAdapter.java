package com.cookandroid.traveldiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DiaryAdapter extends ArrayAdapter<DiaryEntry> {

    public DiaryAdapter(Context context, List<DiaryEntry> entries) {
        super(context, 0, entries);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.diary_entry, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView contentTextView = convertView.findViewById(R.id.contentTextView);

        DiaryEntry entry = getItem(position);

        titleTextView.setText(entry.getTitle());
        dateTextView.setText(entry.getDate());
        contentTextView.setText(entry.getContent());

        return convertView;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < getCount()) {
            remove(getItem(position));
        }
    }
}
