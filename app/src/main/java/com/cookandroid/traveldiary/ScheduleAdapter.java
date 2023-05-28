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

public class ScheduleAdapter extends ArrayAdapter<ScheduleEntry> {

    public ScheduleAdapter(Context context, List<ScheduleEntry> entries) {
        super(context, 0, entries);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_entry, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView contentTextView = convertView.findViewById(R.id.contentTextView);
        TextView startDateTextView = convertView.findViewById(R.id.startDateTextView);
        TextView endDateTextView = convertView.findViewById(R.id.endDateTextView);

        ScheduleEntry entry = getItem(position);

        titleTextView.setText(entry.getTitle());
        contentTextView.setText(entry.getContent());
        startDateTextView.setText(entry.getStartDate());
        endDateTextView.setText(entry.getEndDate());

        return convertView;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < getCount()) {
            remove(getItem(position));
        }
    }
}
