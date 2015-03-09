package com.changami.app.lunchdecider.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.changami.app.lunchdecider.MySQLiteOpenHelper;
import com.changami.app.lunchdecider.R;
import com.changami.app.lunchdecider.data.LunchPointDao;

public class AddFragment extends Fragment {

    static SQLiteDatabase mydb;
    static LunchPointDao dao;

    @InjectView(R.id.point_name)
    EditText editText;

    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        ButterKnife.inject(this, view);

        editText.setText("");

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

    @OnClick(R.id.add_button)
    public void onClick() {
        if ("".equals(editText.getText().toString())) {
            Toast.makeText(getActivity().getApplicationContext(), "場所の名前を入力してください。", Toast.LENGTH_LONG).show();
            return;
        }

        dao.insert(editText.getText().toString());
        Toast.makeText(getActivity().getApplicationContext(), "「" + editText.getText().toString() + "」をDBに追加しました", Toast.LENGTH_LONG).show();
        editText.setText("");
    }

}
