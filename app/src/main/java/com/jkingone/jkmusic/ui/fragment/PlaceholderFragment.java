package com.jkingone.jkmusic.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.ui.activity.PlayActivity;

public class PlaceholderFragment extends LazyFragment {
    public static final String TAG = "PlaceholderFragment";

    private TextView mTextView;

    public static PlaceholderFragment newInstance(String... params) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        mTextView = view.findViewById(R.id.tv_placeholder);
        mTextView.setText(params[0]);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PlayActivity.class));
            }
        });
        Log.i(TAG, "onCreateView: " + params[0]);
        return view;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: " + params[0]);
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: " + params[0]);
    }

    /**
     * Called when all saved state has been restored into the view hierarchy
     * of the fragment.  This can be used to do initialization based on saved
     * state that you are letting the view hierarchy track itself, such as
     * whether check box widgets are currently checked.  This is called
     * after {@link #onActivityCreated(Bundle)} and before
     * {@link #onStart()}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.i(TAG, "onViewStateRestored: " + params[0]);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: " + params[0]);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: " + params[0]);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: " + params[0]);
    }

    /**
     * 与Activity#onStop()对应
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: " + params[0]);
    }

    /**
     * view从fragment中销毁
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: " + params[0]);
    }

    /**
     * fragment不能够再被使用
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: " + params[0]);
    }

    /**
     * 与activity不再有联系
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: " + params[0]);
    }
}
