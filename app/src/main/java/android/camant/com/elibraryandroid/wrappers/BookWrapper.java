package android.camant.com.elibraryandroid.wrappers;

import java.util.ArrayList;

/**
 * Created by Institute on 1/23/2017.
 */

public class BookWrapper extends BaseWrapper {
    private ArrayList<BookRelatedWrapper> datas = new ArrayList<>();

    public ArrayList<BookRelatedWrapper> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<BookRelatedWrapper> datas) {
        this.datas = datas;
    }
}
