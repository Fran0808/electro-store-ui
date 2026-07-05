package com.store.inventario.shared.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonFactory {

    public static <T> T toObject(Class<T> element, String value) {
        try {
            return new Gson().fromJson(value, element);
        } catch (JsonSyntaxException exception) {
            System.out.println("Parsing JSON: " + exception.getMessage());
            return null;
        }
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }


}
