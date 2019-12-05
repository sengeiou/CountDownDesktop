package com.fax.cddt.manager.widget;

import android.content.Context;
import android.graphics.Point;

import com.fax.cddt.bean.CustomWidgetConfig;
import com.fax.cddt.bean.DrawablePlugBean;
import com.fax.cddt.bean.LinePlugBean;
import com.fax.cddt.bean.PlugLocation;
import com.fax.cddt.bean.ProgressPlugBean;
import com.fax.cddt.bean.TextPlugBean;
import com.fax.cddt.utils.ViewUtils;
import com.fax.cddt.view.sticker.TextSticker;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-9-27
 * Description:下载了的桌面widget的json数据进行分辨率适配
 */
public class CustomWidgetScreenAdaptHelper {
    private CustomWidgetConfig mResultConfig;
    private Context mContext;
    private int mAdaptWidth,mAdaptHeight;


    public CustomWidgetScreenAdaptHelper(Context context) {
        this.mContext = context;
        mAdaptWidth =  WidgetConfig.getWidgetWidth();
        mAdaptHeight = WidgetConfig.getWidgetWidth();
    }

    public CustomWidgetConfig adaptConfig(@NonNull CustomWidgetConfig config) {

        mResultConfig = new CustomWidgetConfig();
        List<TextPlugBean> mTextList;
        List<LinePlugBean> mLineList;
        List<DrawablePlugBean> mDrawableList;
        List<ProgressPlugBean> mProgressList;
        mResultConfig.setId(config.getId());
        mResultConfig.setCoverUrl(config.getCoverUrl());
        mResultConfig.setTitle(config.getTitle());
        mResultConfig.setBaseOnWidthPx(mAdaptWidth);
        mResultConfig.setBaseOnHeightPx(mAdaptHeight);
        mResultConfig.setTextSize(ViewUtils.dp2px(TextSticker.DEFAULT_TEXT_SIZE));
        mResultConfig.setDefaultScale(TextSticker.DEFAULT_TEXT_SIZE);
        mResultConfig.setCreatedTime(System.currentTimeMillis());
        mResultConfig.setFromFeatured(true);
        mResultConfig.setDesc(config.getDesc());
        mResultConfig.setFontInfo(config.getFontInfo());
        mResultConfig.setOriginX(config.getOriginX());
        mResultConfig.setOriginY(config.getOriginY());
        mResultConfig.setForVip(config.isForVip());
        mResultConfig.setPreviewPath(config.getPreviewPath());
        mResultConfig.setVersion(config.getVersion());
        mTextList = config.getTextPlugList();
        mLineList = config.getLinePlugList();
        mDrawableList = config.getDrawablePlugList();
        mProgressList = config.getProgressPlugList();
        for (int i = 0; i < mTextList.size(); i++) {
            TextPlugBean bean = mTextList.get(i);
            initTextSticker(bean, config.getBaseOnWidthPx(), config.getBaseOnHeightPx());
        }
        for (int i = 0; i < mLineList.size(); i++) {
            LinePlugBean bean = mLineList.get(i);
            initLineSticker(bean, config.getBaseOnWidthPx(), config.getBaseOnHeightPx());
        }
        for (int i = 0; i < mDrawableList.size(); i++) {
            DrawablePlugBean bean = mDrawableList.get(i);
            initDrawableSticker(bean, config.getBaseOnWidthPx(), config.getBaseOnHeightPx());
        }
        for (int i = 0; i < mProgressList.size(); i++) {
            ProgressPlugBean bean = mProgressList.get(i);
            initProgressSticker(bean,config.getBaseOnWidthPx(),config.getBaseOnHeightPx());
        }
        mResultConfig.setLinePlugList(mLineList);
        mResultConfig.setTextPlugList(mTextList);
        mResultConfig.setDrawablePlugList(mDrawableList);
        mResultConfig.setProgressPlugList(mProgressList);
        return mResultConfig;

    }

    /**
     * 初始化文字插件
     *
     * @param bean
     * @param baseOnWidth
     * @param baseOnHeight
     */
    private void initTextSticker(TextPlugBean bean, int baseOnWidth, int baseOnHeight) {
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        plugLocation.setX(targetPoint.x);
        plugLocation.setY(targetPoint.y);
        bean.setLocation(plugLocation);
        float adaptHeightRatio = getHeightRatio(baseOnHeight);
        float adaptWidthRatio = getWidthRatio(baseOnWidth);
        float width = bean.getRight() - bean.getLeft();
        float height = bean.getBottom() - bean.getTop();
        bean.setLeft((plugLocation.getX() - (width / 2) * adaptWidthRatio));
        bean.setRight((plugLocation.getX() + (width / 2) * adaptWidthRatio));
        bean.setTop((plugLocation.getY() - (height / 2) * adaptHeightRatio));
        bean.setBottom((plugLocation.getY() + (height / 2) * adaptHeightRatio));
        bean.setText(bean.getText());
        bean.setAlignment(null);
    }

    /**
     * 初始化线条插件
     *
     * @param bean
     * @param baseOnWidth
     * @param baseOnHeight
     */
    private void initLineSticker(LinePlugBean bean, int baseOnWidth, int baseOnHeight) {
        float ratio, targetLength;
        if (bean.getStyle() == 2) {
            float ratioWidth = getWidthRatio(baseOnWidth);
            targetLength = bean.getWidth() * ratioWidth;
        } else {
            float heightRatio = getHeightRatio(baseOnHeight);
            targetLength = bean.getWidth() * heightRatio;
        }
        ratio = targetLength * 1.0f / mAdaptWidth;
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        plugLocation.setX(targetPoint.x);
        plugLocation.setY(targetPoint.y);
        bean.setLocation(plugLocation);
        bean.setScale(ratio);
        bean.setSize(targetLength);
        bean.setWidth((int) targetLength);
        bean.setLeft(plugLocation.getX() - bean.getWidth() / 2f);
        bean.setRight(plugLocation.getX() + bean.getWidth() / 2f);
        bean.setTop(plugLocation.getY() - bean.getHeight() / 2f);
        bean.setBottom(plugLocation.getY() + bean.getHeight() / 2f);

    }

    private void initDrawableSticker(DrawablePlugBean bean, int baseOnWidth, int baseOnHeight) {
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        plugLocation.setX(targetPoint.x);
        plugLocation.setY(targetPoint.y);
        bean.setLocation(plugLocation);
        float adaptRatio = getWidthRatio(baseOnWidth);
        bean.setScale(bean.getScale() * adaptRatio);
        bean.setLeft(plugLocation.getX() - bean.getWidth() * bean.getScale() / 2);
        bean.setRight(plugLocation.getX() + bean.getWidth() * bean.getScale() / 2);
        bean.setTop(plugLocation.getY() - bean.getHeight() * bean.getScale() / 2);
        bean.setBottom(plugLocation.getY() + bean.getHeight() * bean.getScale() / 2);
    }

    private void initProgressSticker(ProgressPlugBean bean, int baseOnWidth, int baseOnHeight) {
        float ratio, targetLength;
        float ratioWidth = getWidthRatio(baseOnWidth);
        targetLength = bean.getWidth() * ratioWidth;
        ratio = targetLength * 1.0f / mAdaptWidth;
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        plugLocation.setX(targetPoint.x);
        plugLocation.setY(targetPoint.y);
        bean.setLocation(plugLocation);
        bean.setScale(ratio);
        bean.setSize(targetLength);
        bean.setWidth((int) targetLength);
        bean.setLeft(plugLocation.getX() - bean.getWidth() / 2f);
        bean.setRight(plugLocation.getX() + bean.getWidth() / 2f);
        bean.setTop(plugLocation.getY() - bean.getHeight() / 2f);
        bean.setBottom(plugLocation.getY() + bean.getHeight() / 2f);
    }

    private Point reSizeWidthAndHeight(float x, float y, int baseWidth, int baseHeight) {
        float widthRatio = getWidthRatio(baseWidth);
        float heightRatio = getHeightRatio(baseHeight);
        int targetX = (int) (x * widthRatio);
        int targetY = (int) (y * heightRatio);
        return new Point(targetX, targetY);
    }

    private float getWidthRatio(int baseWidth) {
        return mAdaptWidth * 1.0f / baseWidth;
    }

    private float getHeightRatio(int baseHeight) {
        return mAdaptHeight * 1.0f / baseHeight;
    }


}
