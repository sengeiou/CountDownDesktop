package com.fax.showdt.activity;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ali.ha.fulltrace.logger.Logger;
import com.alibaba.sdk.android.push.common.util.JSONUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fax.showdt.AppContext;
import com.fax.showdt.BuildConfig;
import com.fax.showdt.ConstantString;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.WidgetConfig;
import com.fax.showdt.bean.WidgetShapeBean;
import com.fax.showdt.callback.WidgetEditProgressCallback;
import com.fax.showdt.callback.WidgetEditShapeCallback;
import com.fax.showdt.callback.WidgetEditVectorCallback;
import com.fax.showdt.callback.WidgetEditStickerCallback;
import com.fax.showdt.callback.WidgetEditTextCallback;
import com.fax.showdt.dialog.ios.interfaces.OnDialogButtonClickListener;
import com.fax.showdt.dialog.ios.interfaces.OnShowListener;
import com.fax.showdt.dialog.ios.util.BaseDialog;
import com.fax.showdt.dialog.ios.util.DialogSettings;
import com.fax.showdt.dialog.ios.util.view.BlurView;
import com.fax.showdt.dialog.ios.v3.FullScreenDialog;
import com.fax.showdt.dialog.ios.v3.MessageDialog;
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.dialog.ios.v3.WaitDialog;
import com.fax.showdt.fragment.WidgetProgressEditFragment;
import com.fax.showdt.fragment.WidgetShapeEditFragment;
import com.fax.showdt.fragment.WidgetVectorEditFragment;
import com.fax.showdt.fragment.WidgetStickerEditFragment;
import com.fax.showdt.fragment.WidgetTextEditFragment;
import com.fax.showdt.manager.location.LocationManager;
import com.fax.showdt.manager.musicPlug.KLWPSongUpdateManager;
import com.fax.showdt.manager.weather.WeatherManager;
import com.fax.showdt.manager.widget.CustomWidgetConfigConvertHelper;
import com.fax.showdt.manager.widget.CustomWidgetConfigDao;
import com.fax.showdt.manager.widget.WidgetContext;
import com.fax.showdt.manager.widget.WidgetDownloadManager;
import com.fax.showdt.manager.widget.WidgetSizeConfig;
import com.fax.showdt.service.NLService;
import com.fax.showdt.service.WidgetUpdateService;
import com.fax.showdt.utils.BitmapUtils;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.Constant;
import com.fax.showdt.utils.CustomPlugUtil;
import com.fax.showdt.utils.Environment;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.sticker.BitmapStickerIcon;
import com.fax.showdt.view.sticker.DeleteIconEvent;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.fax.showdt.view.sticker.ProgressSticker;
import com.fax.showdt.view.sticker.Sticker;
import com.fax.showdt.view.sticker.StickerView;
import com.fax.showdt.view.sticker.TextSticker;
import com.fax.showdt.view.sticker.ZoomIconEvent;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;
import com.fax.showdt.view.tab.AlphaTabView;
import com.fax.showdt.view.tab.AlphaTabsIndicator;
import com.fax.showdt.view.tab.OnTabChangedListner;
import com.gyf.barlibrary.ImmersionBar;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-9
 * Description:
 */
public class DiyWidgetMakeActivity extends TakePhotoBaseActivity implements View.OnClickListener {
    private final String KEY_RESTORE_DATA = "restore_data";
    public static final int EDIT_TEXT = 0;
    public static final int EDIT_STICKER = 1;
    public static final int EDIT_VECTOR = 2;
    public static final int EDIT_PROGRESS = 3;
    public static final int EDIT_SHAPE = 4;
    private AlphaTabView tabText, tabSticker, tabVector, tabProgress, tabShape;
    private StickerView mStickerView;
    private ImageView mStickerViewBg;
    private RelativeLayout mRLEditBody;
    private boolean mEditPaneShowing = false;
    private RecyclerView mRvEditBg;
    private Bitmap mEditBitmap, mSystemBgBitmap;
    private List<String> mEditBgList = new ArrayList<>();
    private CommonAdapter<String> mEditBgAdapter;
    private WidgetTextEditFragment mTextEditFragment;
    private WidgetStickerEditFragment mStickerEditFragment;
    private WidgetVectorEditFragment mVectorEditFragment;
    private WidgetProgressEditFragment mProgressEditFragment;
    private WidgetShapeEditFragment mShapeEditFragment;
    private LongSparseArray<Fragment> mFragments = new LongSparseArray<>();
    private Sticker mHandlingSticker;
    private LongSparseArray<Sticker> mStickerList;
    private FullScreenDialog mEditBgDialog;
    private boolean mIsGetSystemBgSuc = true;
    private CustomWidgetConfig customWidgetConfig;
    private UpdateLrcReceiver mUpdateLrcReceiver;
    private boolean selectBgFromAlbum = false;
    private boolean isFromSelection = false;


    @IntDef({EDIT_TEXT, EDIT_STICKER, EDIT_VECTOR, EDIT_SHAPE, EDIT_PROGRESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EditType {
    }

    public static void startSelf(Context context, String json, boolean isFromSelection) {
        Intent intent = new Intent(context, DiyWidgetMakeActivity.class);
        intent.putExtra(ConstantString.widget_make_data, json);
        intent.putExtra(ConstantString.widget_make_data_is_from_selection, isFromSelection);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diy_widget_make_activity);
        WeatherManager.getInstance().starGetWeather();
        initStatusBar();
        intervalRefreshStickerView();
        initData(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当服务开启后通知 通知监听器停止刷新歌曲信息
        Intent intent = new Intent();
        intent.putExtra("switch_flag", false);
        intent.setAction(NLService.NOTIFY_REFRESH_AUDIO_INFO);
        sendBroadcast(intent);
        LocationManager.getInstance().stopLocation();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mTextEditFragment == null && fragment instanceof WidgetTextEditFragment) {
            Logger.e("onAttachFragment mTextEditFragment");
            mTextEditFragment = (WidgetTextEditFragment) fragment;
            transaction.remove(mTextEditFragment);
        } else if (mVectorEditFragment == null && fragment instanceof WidgetVectorEditFragment) {
            Logger.e("onAttachFragment mVectorEditFragment");
            mVectorEditFragment = (WidgetVectorEditFragment) fragment;
            transaction.remove(mVectorEditFragment);
        } else if (mStickerEditFragment == null && fragment instanceof WidgetStickerEditFragment) {
            Logger.e("onAttachFragment mStickerEditFragment");
            mStickerEditFragment = (WidgetStickerEditFragment) fragment;
            transaction.remove(mStickerEditFragment);
        } else if (mProgressEditFragment == null && fragment instanceof WidgetProgressEditFragment) {
            Logger.e("onAttachFragment mProgressEditFragment");
            mProgressEditFragment = (WidgetProgressEditFragment) fragment;
            transaction.remove(mProgressEditFragment);
        } else if (mShapeEditFragment == null && fragment instanceof WidgetShapeEditFragment) {
            Logger.e("onAttachFragment mShapeEditFragment");
            mShapeEditFragment = (WidgetShapeEditFragment) fragment;
            transaction.remove(mShapeEditFragment);
        }
        transaction.commitAllowingStateLoss();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CustomWidgetConfig config = new CustomWidgetConfigConvertHelper().saveConfig(customWidgetConfig, mStickerList);
        customWidgetConfig.setBaseOnHeightPx(mStickerView.getHeight());
        customWidgetConfig.setBaseOnWidthPx(mStickerView.getWidth());
        outState.putString(KEY_RESTORE_DATA, config.toJSONString());
        Log.i("test_save_state:", config.toJSONString());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setEditBodySlideOutAnimation();
    }

    @Override
    protected void initView() {
        super.initView();
        mStickerView = findViewById(R.id.sticker_view);
        mStickerViewBg = findViewById(R.id.iv_select_bg);
        mRLEditBody = findViewById(R.id.rl_edit_body);
        tabText = findViewById(R.id.tv_text);
        tabProgress = findViewById(R.id.tv_progress);
        tabVector = findViewById(R.id.tv_vector);
        tabSticker = findViewById(R.id.tv_sticker);
        tabShape = findViewById(R.id.tv_shape);
        tabText.setOnClickListener(this);
        tabProgress.setOnClickListener(this);
        tabVector.setOnClickListener(this);
        tabSticker.setOnClickListener(this);
        tabShape.setOnClickListener(this);
        AlphaTabsIndicator indicator = findViewById(R.id.alphaIndicator);
        indicator.setOnTabChangedListner(new OnTabChangedListner() {
            @Override
            public void onTabSelected(int tabNum) {
                switch (tabNum) {
                    case 0: {
                        TextSticker textSticker = new TextSticker(System.currentTimeMillis());
                        textSticker.setTextColor("#FFFFFF");
                        textSticker.setFontPath("fonts/xindixiaowanzi.ttf");
                        mTextEditFragment.setWidgetEditTextSticker(textSticker);
                        mStickerView.addSticker(textSticker, Sticker.Position.TOP);
                        switchToOneFragment(EDIT_TEXT);
                        break;
                    }
                    case 1: {
                        switchToOneFragment(EDIT_STICKER);
                        break;
                    }
                    case 2: {
                        switchToOneFragment(EDIT_VECTOR);
                        break;
                    }
                    case 3: {
                        ProgressSticker progressSticker = new ProgressSticker(System.currentTimeMillis());
                        progressSticker.resize(ViewUtils.dpToPx(150f, DiyWidgetMakeActivity.this), ViewUtils.dpToPx(10f, DiyWidgetMakeActivity.this));
                        progressSticker.setPercent(0.7f);
                        progressSticker.setProgressType(ProgressSticker.HORIZONTAL);
                        progressSticker.setDrawType(ProgressSticker.SOLID);
                        mStickerView.addSticker(progressSticker, Sticker.Position.TOP);
                        switchToOneFragment(EDIT_PROGRESS);
                        mProgressEditFragment.setProgressSticker(progressSticker);
                        break;
                    }
                    case 4: {
                        DrawableSticker drawableSticker = new DrawableSticker(null, System.currentTimeMillis());
                        drawableSticker.setmPicType(DrawableSticker.SHAPE);
                        mStickerView.addSticker(drawableSticker, Sticker.Position.CENTER);
                        switchToOneFragment(EDIT_SHAPE);
                        mShapeEditFragment.setDrawableSticker(drawableSticker);
                        break;
                    }

                }
            }
        });
    }

    private void initData(Bundle savedInstanceState) {
        mStickerList = new LongSparseArray<>();
        Intent intent = getIntent();
        String json = "";
        isFromSelection = intent.getBooleanExtra(ConstantString.widget_make_data_is_from_selection, false);
        if (!isFromSelection) {
            json = intent.getStringExtra(ConstantString.widget_make_data);
        }

        if (!TextUtils.isEmpty(json)) {
            customWidgetConfig = GsonUtils.parseJsonWithGson(json, CustomWidgetConfig.class);
        }
        if (savedInstanceState != null) {
            String saveJson = savedInstanceState.getString(KEY_RESTORE_DATA);
            Log.i("test_save_state:", "初始化：" + saveJson);
            if (!TextUtils.isEmpty(saveJson)) {
                customWidgetConfig = GsonUtils.parseJsonWithGson(saveJson, CustomWidgetConfig.class);
            }
        }
        if (customWidgetConfig == null) {
            customWidgetConfig = new CustomWidgetConfig();
        }
        initStickers();
        initAllEditFragments();
        initStickerView();
        initStickerViewBg();
        initBlurView();
        if (isFromSelection) {
            String widgetJson = intent.getStringExtra(ConstantString.widget_make_data);
            WidgetConfig config = GsonUtils.parseJsonWithGson(widgetJson, WidgetConfig.class);
            if (Integer.valueOf(config.getSupportVersion()) > ConstantString.WIDGET_SUPPORT_VERSION) {
                showUpdateVersionDialog(config);
            } else {
                downloadWidget(config);
            }
        }
    }

    private void showUpdateVersionDialog(final WidgetConfig widgetConfig) {
        MessageDialog.show(this, "提示", "你的版本过低,需要更新最新版本才能正常加载插件", "立即更新", "忽略")
                .setCancelable(false)
                .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        CommonUtils.goAppMarket(BuildConfig.APP_ID);
                        return false;
                    }
                }).setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
            @Override
            public boolean onClick(BaseDialog baseDialog, View v) {
                downloadWidget(widgetConfig);
                return false;
            }
        });
    }

    private void downloadWidget(final WidgetConfig widgetConfig) {
        WidgetDownloadManager.getInstance().startDownloadWidget(widgetConfig, new WidgetDownloadManager.DownloadWidgetCallback() {
            @Override
            public void downloadStart() {
                WaitDialog.show(DiyWidgetMakeActivity.this, "素材整合中...");
            }

            @Override
            public void downloadSuc() {
                WaitDialog.show(DiyWidgetMakeActivity.this, "整合完成", TipDialog.TYPE.SUCCESS);
                customWidgetConfig = GsonUtils.parseJsonWithGson(widgetConfig.getConfig(), CustomWidgetConfig.class);
                Log.i("test_config:", customWidgetConfig.toJSONString());
                initStickers();
                initStickerViewBg();
                initBlurView();
            }

            @Override
            public void downloadProgress(int progress) {

            }

            @Override
            public void downloadFail(String errorMsg) {
                WaitDialog.show(DiyWidgetMakeActivity.this, "整合失败", TipDialog.TYPE.ERROR);
            }
        });
    }

    private void initStickers() {
        if (customWidgetConfig != null) {
            CustomWidgetConfigConvertHelper mHelper = new CustomWidgetConfigConvertHelper();
            mStickerList.clear();
            mHelper.initAllStickerPlugs(mStickerView, customWidgetConfig, mStickerList);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUpdateLrcReceiver = new UpdateLrcReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KLWPSongUpdateManager.ACTION_UPDATE_MEDIA_INFO);
        registerReceiver(mUpdateLrcReceiver, intentFilter);
        //当服务开启后通知 通知监听器刷新歌曲信息
        Intent intent = new Intent();
        intent.putExtra("switch_flag", true);
        intent.setAction(NLService.NOTIFY_REFRESH_AUDIO_INFO);
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.tv_text) {
            TextSticker textSticker = new TextSticker(System.currentTimeMillis());
            textSticker.setTextColor("#FFFFFF");
            textSticker.setFontPath("fonts/xindixiaowanzi.ttf");
            mTextEditFragment.setWidgetEditTextSticker(textSticker);
            mStickerView.addSticker(textSticker, Sticker.Position.TOP);
            switchToOneFragment(EDIT_TEXT);
        } else if (resId == R.id.tv_sticker) {
            switchToOneFragment(EDIT_STICKER);
        } else if (resId == R.id.tv_vector) {
            switchToOneFragment(EDIT_SHAPE);
        } else if (resId == R.id.tv_progress) {
            ProgressSticker progressSticker = new ProgressSticker(System.currentTimeMillis());
            progressSticker.resize(ViewUtils.dpToPx(150f, this), ViewUtils.dpToPx(10f, this));
            progressSticker.setPercent(0.7f);
            progressSticker.setProgressType(ProgressSticker.HORIZONTAL);
            progressSticker.setDrawType(ProgressSticker.SOLID);
            mStickerView.addSticker(progressSticker, Sticker.Position.TOP);
            switchToOneFragment(EDIT_PROGRESS);
            mProgressEditFragment.setProgressSticker(progressSticker);
        } else if (resId == R.id.tv_shape) {
            DrawableSticker drawableSticker = new DrawableSticker(null, System.currentTimeMillis());
            drawableSticker.setmPicType(DrawableSticker.SHAPE);
            mStickerView.addSticker(drawableSticker, Sticker.Position.CENTER);
            switchToOneFragment(EDIT_PROGRESS);
            mShapeEditFragment.setDrawableSticker(drawableSticker);
        } else if (resId == R.id.iv_back) {
            if (mStickerList.isEmpty()) {
                finish();
                return;
            }
            MessageDialog.show(this, "提示", "确定要退出编辑吗？")
                    .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                        @Override
                        public boolean onClick(BaseDialog baseDialog, View v) {
                            finish();
                            return false;
                        }
                    });
        } else if (resId == R.id.iv_change_bg) {
            showEditBgPanel();
        } else if (resId == R.id.iv_save) {
            saveConfig();
        } else if(resId == R.id.iv_guide){
            Intent intent = new Intent(DiyWidgetMakeActivity.this, WebActivity.class);
            intent.putExtra(WebActivity.URL_KEY, Constant.WIDGET_GUIDE);
            startActivity(intent);
        }
    }

    /**
     * 初始化状态栏
     */
    private void initStatusBar() {
        ImmersionBar.hideStatusBar(getWindow());
    }

    private void initBlurView() {
        BlurView blurView = new BlurView(this, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        RelativeLayout rootView = findViewById(R.id.rootView);
        rootView.setBackground(new BitmapDrawable(getResources(), mEditBitmap));
        rootView.addView(blurView, 0, params);
    }

    private void initStickerView() {
        FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams) mStickerView.getLayoutParams();
        params.width = WidgetSizeConfig.getWidgetWidth();
        params.height = WidgetSizeConfig.getWidget4X4Height();
        mStickerView.setLayoutParams(params);
        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.sticker_ic_close_icon),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.sticker_ic_scale_icon),
                BitmapStickerIcon.RIGHT_BOTTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());
        mStickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon));
        mStickerView.setConstrained(true);
        mStickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                mHandlingSticker = sticker;
                mStickerList.put(sticker.getId(), sticker);
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                Log.i("test_click:", "click");

            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                mStickerList.delete(sticker.getId());
                if (mEditPaneShowing) {
                    setEditBodySlideOutAnimation();
                    mEditPaneShowing = false;
                }
                mHandlingSticker = null;
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                mHandlingSticker = sticker;
            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {
                Log.i("test_click:", "touch");
                mHandlingSticker = mStickerView.getCurrentSticker();
                if (sticker instanceof TextSticker) {
                    mTextEditFragment.setWidgetEditTextSticker((TextSticker) sticker);
                    switchToOneFragment(EDIT_TEXT);
                } else if (sticker instanceof DrawableSticker) {
                    if (((DrawableSticker) sticker).getmPicType() == DrawableSticker.SVG) {
                        switchToOneFragment(EDIT_VECTOR);
                        mVectorEditFragment.setWidgetEditShapeSticker((DrawableSticker) sticker);
                    } else if (((DrawableSticker) sticker).getmPicType() == DrawableSticker.SDCARD) {
                        switchToOneFragment(EDIT_STICKER);
                        mStickerEditFragment.setDrawableSticker((DrawableSticker) sticker);
                    } else if (((DrawableSticker) sticker).getmPicType() == DrawableSticker.SHAPE) {
                        switchToOneFragment(EDIT_SHAPE);
                        mShapeEditFragment.setDrawableSticker((DrawableSticker) sticker);
                    }
                } else if (sticker instanceof ProgressSticker) {
                    mProgressEditFragment.setProgressSticker((ProgressSticker) sticker);
                    switchToOneFragment(EDIT_PROGRESS);
                }
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                if (sticker instanceof TextSticker) {
                    mTextEditFragment.showInputDialog(100);
                }
            }

            @Override
            public void onStickerNoTouched() {
                if (mEditPaneShowing) {
                    setEditBodySlideOutAnimation();
                    mEditPaneShowing = false;
                }
                mHandlingSticker = null;
            }

            @Override
            public void onClickedBindAppIcon(@NonNull Sticker sticker) {

            }

            @Override
            public void onCopySticker(@NonNull Sticker sticker) {

            }

            @Override
            public void onUnlock() {

            }
        });
    }


    private Bitmap getSystemBitmap() {
        Drawable drawable = WallpaperManager.getInstance(this).getDrawable();
        return BitmapUtils.drawableToBitmap(drawable);
    }

    private Bitmap clipWidgetSizeBitmap(Bitmap bitmap) {
        Bitmap resultBitmap = null;

        try {
            float w = bitmap.getWidth();
            float h = bitmap.getHeight();
            Log.i("test_size:", "height:" + bitmap.getHeight() + " width:" + bitmap.getWidth());
            int cropWidth = (int) (w * WidgetSizeConfig.WIDGET_MAX_WIDTH_RATIO);// 裁切后所取的正方形区域边长
            int x = (int) ((w - cropWidth) * 1.0f / 2);
            int marginTop = ViewUtils.dpToPx(60f, this);
            if ((w < h || w == h) && w > h - marginTop) {
                marginTop = (int) (h - w);
            } else if (w > h) {
                cropWidth = (int) (cropWidth / 2f);
            }
            resultBitmap = Bitmap.createBitmap(bitmap, x, marginTop, cropWidth, cropWidth, null, false);
        } catch (Exception e) {
            resultBitmap = null;
        }
        return resultBitmap;
    }

    private void initStickerViewBg() {
        Bitmap bitmap = clipWidgetSizeBitmap(getSystemBitmap());
        if (bitmap != null) {
            mIsGetSystemBgSuc = true;
            mEditBitmap = bitmap;
            mSystemBgBitmap = bitmap;
        } else {
            mIsGetSystemBgSuc = false;
            mEditBitmap = CommonUtils.getAssetPic(this, "file:///android_asset/widgetBg/template00.png");
        }
        if (customWidgetConfig != null) {
            if (!TextUtils.isEmpty(customWidgetConfig.getBgPath()) && FileExUtils.exists(customWidgetConfig.getBgPath())) {
                Log.i("test_config_bgPath:", customWidgetConfig.getBgPath());
                mEditBitmap = BitmapUtils.decodeFile(customWidgetConfig.getBgPath());
            }
        }
//        if(isFromSelection){
//            Glide.with(this)
//                    .load(customWidgetConfig.getBgPath())
//                    .into(new CustomTarget<Drawable>() {
//                        @Override
//                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                            mStickerViewBg.setImageDrawable(resource);
//                            mEditBitmap = BitmapUtils.drawableToBitmap(resource);
//                            initBlurView();
//                        }
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                        }
//                    });
//        }
        mStickerViewBg.setImageBitmap(mEditBitmap);

    }

    private void intervalRefreshStickerView() {
        addDisponsable(Observable.interval(30, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mStickerView.postInvalidate();
                    }
                }));
    }

    private void initAllEditFragments() {
        mTextEditFragment = new WidgetTextEditFragment();
        mStickerEditFragment = new WidgetStickerEditFragment();
        mVectorEditFragment = new WidgetVectorEditFragment();
        mProgressEditFragment = new WidgetProgressEditFragment();
        mShapeEditFragment = new WidgetShapeEditFragment();
        mTextEditFragment.setWidgetEditTextCallback(new WidgetEditTextCallback() {
            @Override
            public void onAddSticker() {
                TextSticker textSticker = new TextSticker(System.currentTimeMillis());
                textSticker.setTextColor("#FFFFFF");
                textSticker.setFontPath("fonts/xindixiaowanzi.ttf");
                mTextEditFragment.setWidgetEditTextSticker(textSticker);
                mStickerView.addSticker(textSticker, Sticker.Position.TOP);

            }

            @Override
            public void closePanel() {
                setEditBodySlideOutAnimation();
                mStickerView.clearCurrentSticker();
            }
        });
        mStickerEditFragment.setWidgetEditStickerCallback(new WidgetEditStickerCallback() {
            @Override
            public void onAddSticker(String path) {
                Drawable drawable = new BitmapDrawable(getResources(), CommonUtils.getAssetPic(DiyWidgetMakeActivity.this, path));
                DrawableSticker drawableSticker = new DrawableSticker(drawable, System.currentTimeMillis());
                drawableSticker.setmPicType(DrawableSticker.ASSET);
                drawableSticker.setDrawablePath(path);
                mStickerView.addSticker(drawableSticker, Sticker.Position.CENTER);
            }

            @Override
            public void closePanel() {
                setEditBodySlideOutAnimation();
                mStickerView.clearCurrentSticker();
            }

            @Override
            public void onPickPhoto() {
                startCropOneImg(1, 1);
            }
        });
        mVectorEditFragment.setWidgetEditShapeCallback(new WidgetEditVectorCallback() {
            @Override
            public void onAddVectorSticker(WidgetShapeBean widgetShapeBean) {
                try {
                    SVG svg = new SVGBuilder().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN))
                            .readFromAsset(getAssets(), widgetShapeBean.getSvgPath()).build();
                    PictureDrawable drawable = svg.getDrawable();
                    DrawableSticker drawableSticker = new DrawableSticker(drawable, System.currentTimeMillis());
                    drawableSticker.setmPicType(DrawableSticker.SVG);
                    drawableSticker.setDrawablePath(widgetShapeBean.getSvgPath());
                    mStickerView.addSticker(drawableSticker, Sticker.Position.CENTER);
                    Log.i("test_add_sticker:", "添加DrawSticker成功");
                    mVectorEditFragment.setWidgetEditShapeSticker(drawableSticker);
                } catch (IOException e) {

                }

            }

            @Override
            public void closePanel() {
                setEditBodySlideOutAnimation();
                mStickerView.clearCurrentSticker();
            }
        });

        mProgressEditFragment.setWidgetEditProgressCallback(new WidgetEditProgressCallback() {
            @Override
            public void onAddProgressSticker() {
                ProgressSticker progressSticker = new ProgressSticker(System.currentTimeMillis());
                progressSticker.resize(ViewUtils.dpToPx(150f, DiyWidgetMakeActivity.this), ViewUtils.dpToPx(10f, DiyWidgetMakeActivity.this));
                progressSticker.setPercent(0.7f);
                progressSticker.setProgressType(ProgressSticker.HORIZONTAL);
                progressSticker.setDrawType(ProgressSticker.SOLID);
                mStickerView.addSticker(progressSticker, Sticker.Position.TOP);
                switchToOneFragment(EDIT_PROGRESS);
                mProgressEditFragment.setProgressSticker(progressSticker);
            }

            @Override
            public void closePanel() {
                setEditBodySlideOutAnimation();
                mStickerView.clearCurrentSticker();
            }
        });

        mShapeEditFragment.setWidgetEditShapeCallback(new WidgetEditShapeCallback() {
            @Override
            public void onAddShapeSticker() {
                DrawableSticker drawableSticker = new DrawableSticker(null, System.currentTimeMillis());
                drawableSticker.setmPicType(DrawableSticker.SHAPE);
                mStickerView.addSticker(drawableSticker, Sticker.Position.CENTER);
                switchToOneFragment(EDIT_SHAPE);
                mShapeEditFragment.setDrawableSticker(drawableSticker);
            }

            @Override
            public void closePanel() {
                setEditBodySlideOutAnimation();
                mStickerView.clearCurrentSticker();
            }
        });

    }

    private void setEditBodySlideInAnimation() {
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        mRLEditBody.startAnimation(ctrlAnimation);
    }

    private void setEditBodySlideOutAnimation() {
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        ctrlAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRLEditBody.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRLEditBody.startAnimation(ctrlAnimation);
    }

    private void switchToOneFragment(@EditType int editType) {

        if (!mEditPaneShowing) {
            setEditBodySlideInAnimation();
        }
        mEditPaneShowing = true;
        Log.i("test_show:", "visible" + mRLEditBody.getHeight());
        mRLEditBody.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (editType) {
            case EDIT_TEXT: {
                if (!mFragments.containsKey(EDIT_TEXT)) {
                    mFragments.put(EDIT_TEXT, mTextEditFragment);
                    transaction.add(R.id.rl_edit_body, mTextEditFragment);
                    transaction.commitAllowingStateLoss();
                }
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                for (int i = 0; i < mFragments.size(); i++) {
                    long key = mFragments.keyAt(i);
                    Fragment fragment = mFragments.get(key);
                    if (fragment == mTextEditFragment) {
                        transaction1.show(fragment);
                    } else {
                        transaction1.hide(fragment);
                    }
                }

                transaction1.commitAllowingStateLoss();
                break;
            }
            case EDIT_STICKER: {
                if (!mFragments.containsKey(EDIT_STICKER)) {
                    mFragments.put(EDIT_STICKER, mStickerEditFragment);
                    transaction.add(R.id.rl_edit_body, mStickerEditFragment);
                    transaction.commitAllowingStateLoss();
                }
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                for (int i = 0; i < mFragments.size(); i++) {
                    long key = mFragments.keyAt(i);
                    Fragment fragment = mFragments.get(key);
                    if (fragment == mStickerEditFragment) {
                        transaction1.show(fragment);
                    } else {
                        transaction1.hide(fragment);
                    }
                }
                transaction1.commitAllowingStateLoss();
                break;
            }
            case EDIT_VECTOR: {
                if (!mFragments.containsKey(EDIT_VECTOR)) {
                    mFragments.put(EDIT_VECTOR, mVectorEditFragment);
                    transaction.add(R.id.rl_edit_body, mVectorEditFragment);
                    transaction.commitAllowingStateLoss();
                }
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                for (int i = 0; i < mFragments.size(); i++) {
                    long key = mFragments.keyAt(i);
                    Fragment fragment = mFragments.get(key);
                    if (fragment == mVectorEditFragment) {
                        transaction1.show(fragment);
                    } else {
                        transaction1.hide(fragment);
                    }
                }
                transaction1.commitAllowingStateLoss();
                break;
            }
            case EDIT_PROGRESS: {
                if (!mFragments.containsKey(EDIT_PROGRESS)) {
                    mFragments.put(EDIT_PROGRESS, mProgressEditFragment);
                    transaction.add(R.id.rl_edit_body, mProgressEditFragment);
                    transaction.commitAllowingStateLoss();
                }
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                for (int i = 0; i < mFragments.size(); i++) {
                    long key = mFragments.keyAt(i);
                    Fragment fragment = mFragments.get(key);
                    if (fragment == mProgressEditFragment) {
                        transaction1.show(fragment);
                    } else {
                        transaction1.hide(fragment);
                    }
                }
                transaction1.commitAllowingStateLoss();
                break;
            }
            case EDIT_SHAPE: {
                if (!mFragments.containsKey(EDIT_SHAPE)) {
                    mFragments.put(EDIT_SHAPE, mShapeEditFragment);
                    transaction.add(R.id.rl_edit_body, mShapeEditFragment);
                    transaction.commitAllowingStateLoss();
                }
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                for (int i = 0; i < mFragments.size(); i++) {
                    long key = mFragments.keyAt(i);
                    Fragment fragment = mFragments.get(key);
                    if (fragment == mShapeEditFragment) {
                        transaction1.show(fragment);
                    } else {
                        transaction1.hide(fragment);
                    }
                }
                transaction1.commitAllowingStateLoss();
                break;
            }
        }

    }

    private void showEditBgPanel() {
        FullScreenDialog.show(this, R.layout.widget_edit_bg_selector_panel, new FullScreenDialog.OnBindView() {
            @Override
            public void onBind(final FullScreenDialog dialog, View rootView) {
                Log.i("test_dialog:", "initEditBgPanel");
                mRvEditBg = rootView.findViewById(R.id.rv);
                ImageView mIvClose = rootView.findViewById(R.id.iv_close);
                mEditBgDialog = dialog;
                mIvClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.doDismiss();
                    }
                });
            }
        }).setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(BaseDialog dialog) {
                Log.i("test_dialog:", "initEditBgPanel");
                initEditBgPanel();
            }
        });
    }

    private void showEditDialog() {
        MessageDialog.show(this, "提示", "确定要退出编辑吗？")
                .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        finish();
                        return false;
                    }
                });
    }

    private void initEditBgPanel() {
        mEditBgList.clear();
        if (mIsGetSystemBgSuc) {
            mEditBgList.add(0, "");
        }
        mEditBgList.add(0, "");
        Resources res = AppContext.get().getResources();
        String[] strs = res.getStringArray(R.array.widget_edit_bg);
        List<String> tempList = Arrays.asList(strs);
        mEditBgList.addAll(tempList);
        Log.i("test_dialog:", "mEditBgList.size:" + mEditBgList.size());
        mEditBgAdapter = new CommonAdapter<String>(this, R.layout.widget_make_edit_bg_item, mEditBgList) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                ImageView mIv = holder.getView(R.id.iv_item);
                if (position == 0) {
                    mIv.setImageResource(R.drawable.widget_bg_add_icon);
                } else if (position == 1 && mIsGetSystemBgSuc) {
                    mIv.setImageBitmap(mSystemBgBitmap);
                } else {
                    GlideUtils.loadImage(DiyWidgetMakeActivity.this, "file:///android_asset/" + s, mIv);
                }
            }
        };
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        mRvEditBg.setLayoutManager(manager);
        mEditBgAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (position == 0) {
                    selectBgFromAlbum = true;
                    startCropOneImg(1, 1);
                } else if (position == 1 && mIsGetSystemBgSuc) {
                    mStickerViewBg.setImageBitmap(mSystemBgBitmap);
                    RelativeLayout rootView = findViewById(R.id.rootView);
                    rootView.setBackground(new BitmapDrawable(getResources(), mEditBitmap));
                } else {
                    mEditBitmap = CommonUtils.getAssetPic(DiyWidgetMakeActivity.this, mEditBgList.get(position));
                    mStickerViewBg.setImageBitmap(mEditBitmap);
                    RelativeLayout rootView = findViewById(R.id.rootView);
                    rootView.setBackground(new BitmapDrawable(getResources(), mEditBitmap));

                }
                if (mEditBgDialog != null) {
                    mEditBgDialog.doDismiss();
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mRvEditBg.setAdapter(mEditBgAdapter);
    }

    private void sendConfigChangedBroadcast() {
        Intent intent = new Intent();
        intent.setAction(WidgetUpdateService.WIDGET_CONFIG_CHANGED);
        sendBroadcast(intent);
    }

    private void saveConfig() {
        final Bitmap bgBitmap = BitmapUtils.viewToBitmap(mStickerViewBg);
        mStickerView.setShowGrid(false);
        mStickerView.requestLayout();
        mStickerView.clearCurrentSticker();
        final Bitmap stickerBitmap = BitmapUtils.getScreenShotsBitmap(mStickerView);
        final String coverFileName = "widget_cover" + System.currentTimeMillis() + ".png";
        final String bgFileName = "widget_bg" + System.currentTimeMillis() + ".png";
        final String coverDir = Environment.getHomeDir() + File.separator + Constant.WIDGET_DATA_DIR + File.separator + "widget_screenshot";
        final String bgDir = Environment.getHomeDir() + File.separator + Constant.WIDGET_DATA_DIR + File.separator + "widget_bg";
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                BitmapUtils.saveFile(stickerBitmap, coverFileName, coverDir);
                BitmapUtils.saveFile(bgBitmap, bgFileName, bgDir);
                customWidgetConfig = new CustomWidgetConfig();
                if (FileExUtils.exists(customWidgetConfig.getCoverUrl())) {
                    FileExUtils.deleteSingleFile(customWidgetConfig.getCoverUrl());
                }
                if (FileExUtils.exists(customWidgetConfig.getBgPath())) {
                    FileExUtils.deleteSingleFile(customWidgetConfig.getBgPath());
                }
                customWidgetConfig.setCoverUrl(coverDir + File.separator + coverFileName);
                customWidgetConfig.setBgPath(bgDir + File.separator + bgFileName);
                customWidgetConfig.setId(System.currentTimeMillis());
                customWidgetConfig.setBaseOnHeightPx(mStickerView.getHeight());
                customWidgetConfig.setBaseOnWidthPx(mStickerView.getWidth());
                CustomWidgetConfigConvertHelper mHelper = new CustomWidgetConfigConvertHelper();
                CustomWidgetConfig newConfig = mHelper.saveConfig(customWidgetConfig, mStickerList);
                Log.i("test_widget_config:", "保存到数据库：" + newConfig.toJSONString());
                CustomWidgetConfigDao.getInstance(DiyWidgetMakeActivity.this).insert(newConfig);
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        DialogSettings.tipTheme = DialogSettings.THEME.LIGHT;
                        WaitDialog.show(DiyWidgetMakeActivity.this, "加工数据中...");
                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        TipDialog.show(DiyWidgetMakeActivity.this, "保存成功！", TipDialog.TYPE.SUCCESS);
                        sendConfigChangedBroadcast();
                        DiyWidgetMakeActivity.this.finish();
                        Log.i("test_config:", GsonUtils.toJsonWithSerializeNulls(customWidgetConfig));
                        Log.i("test_config:", "保存成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        TipDialog.show(DiyWidgetMakeActivity.this, "保存失败！", TipDialog.TYPE.ERROR);
                        e.printStackTrace();
                        Log.i("test_config:", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEditPaneShowing) {
                mEditPaneShowing = false;
                setEditBodySlideOutAnimation();
            } else {
                if (mStickerList.isEmpty()) {
                    finish();
                } else {
                    showEditDialog();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void cropSuc(String path) {
        super.cropSuc(path);
        if (selectBgFromAlbum) {
            selectBgFromAlbum = false;
            mEditBitmap = BitmapUtils.decodeFile(path);
            mStickerViewBg.setImageBitmap(mEditBitmap);
            RelativeLayout rootView = findViewById(R.id.rootView);
            rootView.setBackground(new BitmapDrawable(getResources(), mEditBitmap));
        } else {
            Drawable drawable = new BitmapDrawable(getResources(), path);
            DrawableSticker drawableSticker = new DrawableSticker(drawable, System.currentTimeMillis());
            drawableSticker.setmPicType(DrawableSticker.SDCARD);
            drawableSticker.setDrawablePath(path);
            mStickerView.addSticker(drawableSticker, Sticker.Position.CENTER);
            if (mStickerEditFragment != null) {
                mStickerEditFragment.setDrawableSticker(drawableSticker);
            }
        }
    }

    @Override
    public void cropFail(Throwable throwable) {
        throwable.printStackTrace();
        Log.i("crop_fail:", "msg:" + throwable.getMessage());
        super.cropFail(throwable);
    }

    /**
     * 接收第三方播放器播放时的歌词
     */
    class UpdateLrcReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (KLWPSongUpdateManager.ACTION_UPDATE_MEDIA_INFO.equals(action)) {
                CustomPlugUtil.lrc = intent.getStringExtra(KLWPSongUpdateManager.LRC_KEY);
                CustomPlugUtil.album = intent.getStringExtra(KLWPSongUpdateManager.ALBUM_KEY);
                CustomPlugUtil.singerName = intent.getStringExtra(KLWPSongUpdateManager.SINGER_KEY);
                CustomPlugUtil.songName = intent.getStringExtra(KLWPSongUpdateManager.SONGNAME_KEY);
                CustomPlugUtil.duration = intent.getLongExtra(KLWPSongUpdateManager.DURATION_KEY, 0L);
                CustomPlugUtil.currentDuration = intent.getLongExtra(KLWPSongUpdateManager.CURRENT_DURATION_KEY, 0L);
                Log.i("test_song:", "lrc:" + intent.getStringExtra(KLWPSongUpdateManager.LRC_KEY));
                Log.i("test_song:", "album:" + CustomPlugUtil.album);
                Log.i("test_song:", "singerName:" + CustomPlugUtil.singerName);
                Log.i("test_song:", "songName:" + CustomPlugUtil.songName);
                Log.i("test_song:", "duration:" + CustomPlugUtil.duration);
                Log.i("test_song:", "currentDuration:" + CustomPlugUtil.currentDuration);
            }
        }

    }
}
