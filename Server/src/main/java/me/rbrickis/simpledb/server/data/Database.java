package me.rbrickis.simpledb.server.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan on 2/20/2015
 * <p/>
 * Project: SimpleDB
 */
public class Database {
    static List<DataObject> data = new ArrayList<DataObject>();

    public static void insert(DataObject object) {
         data.add(object);
    }

    public static DataObject get(String key) {
        DataObject object = null;
        for (DataObject obj : data) {
            if (obj.getKey().equals(key)) {
                object = obj;
            }
        }
        return object;
    }


    public static void save() {
        File file = new File("data.txt");
    }


}
