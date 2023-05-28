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

public class ExpenditureAdapter extends ArrayAdapter<ExpenditureEntry> {

    public ExpenditureAdapter(Context context, List<ExpenditureEntry> entries) {
        super(context, 0, entries);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expenditure_entry, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView spendDateTextView = convertView.findViewById(R.id.spendDateTextView);
        TextView spendTextView = convertView.findViewById(R.id.spendTextView);

        ExpenditureEntry entry = getItem(position);

        titleTextView.setText(entry.getTitle());
        spendDateTextView.setText(entry.getSpendDate());
        spendTextView.setText(entry.getPrice());

        return convertView;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < getCount()) {
            remove(getItem(position));
        }
    }
}
