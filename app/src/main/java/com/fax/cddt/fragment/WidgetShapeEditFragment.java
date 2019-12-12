package com.fax.cddt.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.fax.cddt.R;
import com.fax.cddt.bean.WidgetShapeBean;
import com.fax.cddt.callback.WidgetEditShapeCallback;
import com.fax.cddt.callback.WidgetEditShapeElementSelectedCallback;
import com.fax.cddt.callback.WidgetEditTextCallback;
import com.fax.cddt.callback.WidgetEditTextElementSelectedCallback;
import com.fax.cddt.dialog.WidgetTextInputDialog;
import com.fax.cddt.fragment.widgetShapeEdit.WidgetShapeElementEditFragment;
import com.fax.cddt.fragment.widgetTextEdit.WidgetTextElementEditFragment;
import com.fax.cddt.fragment.widgetTextEdit.WidgetTextFontEditFragment;
import com.fax.cddt.utils.CustomPlugUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class WidgetShapeEditFragment extends Fragment implements View.OnClickListener {
    private ImageView mIvLocal, mIvColor;
    private Context mContext;
    private WidgetShapeElementEditFragment mShapeElementEditFragment;
    private WidgetEditShapeCallback mWidgetEditShapeCallback;


    public WidgetShapeEditFragment(Context context) {
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_shape_edit_fragment, container, false);
        mIvLocal = view.findViewById(R.id.iv_local);
        mIvColor = view.findViewById(R.id.iv_color);
        mIvLocal.setOnClickListener(this);
        mIvColor.setOnClickListener(this);
        initFragment();
        return view;
    }

    @Override
    public void onClick(View view) {

    }

    private void initFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        mShapeElementEditFragment = new WidgetShapeElementEditFragment(mContext);
        transaction.add(R.id.fl_shape_edit_body, mShapeElementEditFragment);
        transaction.commitAllowingStateLoss();
        mShapeElementEditFragment.setWidgetEditShapeElementSelectedCallback(new WidgetEditShapeElementSelectedCallback() {
            @Override
            public void selectShapeElement(WidgetShapeBean widgetShapeBean) {
                mWidgetEditShapeCallback.onAddShapeSticker(widgetShapeBean);
            }
        });
    }
    public void setWidgetEditShapeCallback(WidgetEditShapeCallback callback) {
        this.mWidgetEditShapeCallback = callback;
    }
//    public void showInputDialog(int maxLength) {
//        final String str =mTextSticker.getText();
//        final SpannableStringBuilder content = CustomPlugUtil.getSpannableStrFromSigns(str, mContext);
//        final WidgetTextInputDialog textInputDialog = new WidgetTextInputDialog(getActivity(), maxLength);
//        FragmentManager fragmentManager = getChildFragmentManager();
//        textInputDialog.show(fragmentManager, "textInputDialog");
//        textInputDialog.setOnFontEditListener(new WidgetTextInputDialog.OnFontEditListener() {
//            @Override
//            public void onShow() {
//                textInputDialog.showSoftInput();
//                final EditText mEtInput = textInputDialog.getEditText();
//                mEtInput.setText(content);
//                mEtInput.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        String text = mEtInput.getText().toString().trim();
//                        if (mTextSticker != null) {
//                            mTextSticker.setText(text);
//                        }
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//
//                    }
//                });
//            }
//        });
//
//    }
}
