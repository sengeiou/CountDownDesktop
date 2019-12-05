package com.fax.cddt.manager;

import android.text.TextUtils;

import com.fax.cddt.bean.CustomWidgetConfig;
import com.fax.cddt.utils.GsonUtils;
import com.fax.cddt.utils.SharedPreferenceUtil;

public class CommonConfigManager {

    private volatile static CommonConfigManager mInstance = null;

    private CommonConfigManager(){}
    public static CommonConfigManager getInstance(){
        if(mInstance== null){
            synchronized (CommonConfigManager.class){
                if(mInstance == null){
                    mInstance = new CommonConfigManager();
                }
            }
        }
        return mInstance;
    }


    public  void setWidgetConfig(CustomWidgetConfig config){
        SharedPreferenceUtil.getInstance().put("key_widget_config", GsonUtils.toJsonWithSerializeNulls(config));
    }

    public  CustomWidgetConfig getWidgetConfig(){
       String json =  (String)SharedPreferenceUtil.getInstance().get("key_widget_config","");
       if(!TextUtils.isEmpty(json)){
           return GsonUtils.parseJsonWithGson(json,CustomWidgetConfig.class);
       }
       return  null;
    }
}
