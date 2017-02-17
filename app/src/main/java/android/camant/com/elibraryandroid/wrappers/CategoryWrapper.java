package android.camant.com.elibraryandroid.wrappers;

import java.util.ArrayList;

/**
 * Created by Institute on 1/23/2017.
 */

public class CategoryWrapper extends BaseWrapper {
    private ArrayList<CategoryRelatedWrapper> datas = new ArrayList<>();

    public ArrayList<CategoryRelatedWrapper> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<CategoryRelatedWrapper> datas) {
        this.datas = datas;
    }
}
