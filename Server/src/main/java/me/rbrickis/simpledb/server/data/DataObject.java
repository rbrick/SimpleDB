package me.rbrickis.simpledb.server.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Ryan on 2/20/2015
 * <p/>
 * Project: SimpleDB
 */
@Getter
@AllArgsConstructor
public class DataObject {
    String key;
    String value;

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
