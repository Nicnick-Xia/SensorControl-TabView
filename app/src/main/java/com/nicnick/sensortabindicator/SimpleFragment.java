package com.nicnick.sensortabindicator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author xiazicheng
 * @date 16/9/30.
 */

public class SimpleFragment extends Fragment {

    private String mTitle;
    public static final String BUNDLE_TITLE = "title";

    public static SimpleFragment newInstance(String title){
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE,title);

        SimpleFragment fragment = new SimpleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        if(bundle!=null){
            mTitle = bundle.getString(BUNDLE_TITLE);
        }

        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        TextView textView = (TextView) view.findViewById(R.id.id_text);

        textView.setText(mTitle);

        return view;
    }


}
