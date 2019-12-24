package com.fax.showdt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.R;
import com.fax.showdt.callback.WidgetEditStickerCallback;
import com.fax.showdt.fragment.widgetShapeEdit.WidgetShapeElementEditFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WidgetStickerEditFragment extends Fragment implements View.OnClickListener {
    private ImageView mIvLocal, mAdd,mConsume;
    private WidgetEditStickerCallback mCallback;
    private WidgetShapeElementEditFragment mStickerElementEditFragment;

    public WidgetStickerEditFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_sticker_edit_fragment, container, false);
        mIvLocal = view.findViewById(R.id.iv_local);
        mAdd = view.findViewById(R.id.iv_add);
        mConsume = view.findViewById(R.id.iv_consume);
        mIvLocal.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        mIvLocal.setOnClickListener(this);
        mConsume.setOnClickListener(this);
        initFragment();
        return view;
    }

    @Override
    public void onClick(View view) {
        int resId = view.getId();
        if (resId == R.id.iv_consume) {
            if (mCallback != null) {
                mCallback.closePanel();
            }
        }else if(resId == R.id.iv_add){
            if(mCallback != null){
                mCallback.onPickPhoto();
            }
        }
    }

    private void initFragment() {
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        mStickerElementEditFragment = new WidgetShapeElementEditFragment();
//        transaction.add(R.id.fl_sticker_edit_body, mStickerElementEditFragment);
//        transaction.commitAllowingStateLoss();
//        mStickerElementEditFragment.setWidgetShapeElementSelectedCallback(new WidgetEditStickerElementSelectedCallback() {
//            @Override
//            public void selectSticker(String path) {
//                mCallback.onAddSticker(path);
//            }
//        });
    }

    public void setWidgetEditStickerCallback(WidgetEditStickerCallback callback){
        this.mCallback = callback;
    }
}
