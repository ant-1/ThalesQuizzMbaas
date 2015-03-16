package com.thales.thalesquizzmbaas.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.thales.thalesquizzmbaas.model.Quizz;
import com.thales.thalesquizzmbaas.ui.R;
import com.thales.thalesquizzmbaas.ui.listitems.QuizzLinearLayout;


public class ArrayQuizzAdapter extends ArrayAdapter<Quizz> {


    public ArrayQuizzAdapter(Context context, int resource) {
        super(context, resource);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.linearlayout_quizz, null);
        }
        Quizz quizz = getItem(position);
        QuizzLinearLayout quizzLinearLayout = (QuizzLinearLayout) convertView;
        quizzLinearLayout.setQuizz(quizz);
        return convertView;
    }
}
