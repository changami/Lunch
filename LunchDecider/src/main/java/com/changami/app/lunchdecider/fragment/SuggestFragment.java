package com.changami.app.lunchdecider.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.changami.app.lunchdecider.MySQLiteOpenHelper;
import com.changami.app.lunchdecider.R;
import com.changami.app.lunchdecider.data.LunchPointDao;
import com.changami.app.lunchdecider.data.LunchPointEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SuggestFragment extends Fragment {

    static SQLiteDatabase mydb;
    static LunchPointDao dao;

    public static SuggestFragment newInstance() {
        return new SuggestFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggest, container, false);
        ButterKnife.inject(this, view);

        MySQLiteOpenHelper mHelper = new MySQLiteOpenHelper(getActivity().getApplicationContext());
        mydb = mHelper.getWritableDatabase();
        dao = new LunchPointDao(mydb);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.decide_button)
    public void onClick() {

        final List<String> records = new ArrayList<String>();

        List<LunchPointEntity> entities = dao.findAll();
        for (LunchPointEntity entity : entities) {
            records.add(entity.getPointName());
        }

        if (records.size() == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "おひるごはんたべるところがありません", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), records.get(new Random().nextInt(records.size())) + "に行きましょう", Toast.LENGTH_SHORT).show();
        }
    }

}
