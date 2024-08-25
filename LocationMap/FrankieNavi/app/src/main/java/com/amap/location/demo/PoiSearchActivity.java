package com.amap.location.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItemV2;
import com.amap.api.services.poisearch.PoiResultV2;
import com.amap.api.services.poisearch.PoiSearchV2;

import java.util.ArrayList;


public class PoiSearchActivity extends AppCompatActivity implements PoiSearchV2.OnPoiSearchListener {
    private EditText ed;
    private PoiSearchV2 poiSearch;
    private PoiSearchV2.Query query;
    private String cityCode="028";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location);
        ed=findViewById(R.id.ed);
    }

    public void search(View view) throws AMapException {
        String keyword=ed.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)){
            Toast.makeText(this,"plz input place",Toast.LENGTH_LONG).show();
            return;
        }

        query = new PoiSearchV2.Query(
                keyword,
                "",
                cityCode);
        // "公共设施|商务住宅"
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        poiSearch = new PoiSearchV2(this, query);
        poiSearch.setOnPoiSearchListener(this);//
        poiSearch.searchPOIAsyn();// 开始搜索
    }

    @Override
    public void onPoiSearched(PoiResultV2 result, int code) {
        if (code == 0) {
            assert result != null;
            if (result.getQuery() != null) {
                ArrayList<PoiItemV2> list = result.getPois();
                for (int i=0;i<list.size();i++){
                    Log.e("PoiSearchActivity","onPoiSearched ="+list.get(i).getTitle());
                }

            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItemV2 result, int code) {

    }
}
