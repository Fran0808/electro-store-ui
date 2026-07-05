package com.store.inventario.security;

import com.store.inventario.module.auth.model.entity.CurrentUser;
import com.store.inventario.shared.utils.GsonFactory;
import com.store.inventario.shared.utils.PreferenceFactory;
import lombok.Getter;

public class SessionManager {

    private static SessionManager instance;
    private final String KEY_USER = "auth";
    private final String KEY_TOKEN = "token";

    @Getter
    private String token;
    @Getter
    private CurrentUser user;

    private SessionManager() {
        load();
    }

    public static synchronized SessionManager getInstance() {
        if (isNull()) instance = new SessionManager();
        return instance;
    }

    private void load() {
        this.token = PreferenceFactory.get(this.getClass(), KEY_TOKEN);
        this.user = GsonFactory.toObject(CurrentUser.class, PreferenceFactory.get(this.getClass(), KEY_USER));
    }

    public void save(String token, CurrentUser user, boolean isRemember) {
        this.token = token;
        this.user = user;
        if (isRemember) remember();
    }

    public void close() {
        PreferenceFactory.remove(this.getClass(), KEY_TOKEN);
        PreferenceFactory.remove(this.getClass(), KEY_USER);
        PreferenceFactory.flush(this.getClass());
    }

    private void remember() {
        PreferenceFactory.put(this.getClass(), KEY_TOKEN, this.token);
        PreferenceFactory.put(this.getClass(), KEY_USER, GsonFactory.toJson(this.user));
        PreferenceFactory.flush(this.getClass());
    }

    public boolean isAutenticado() {
        return token != null && user != null;
    }

    private static boolean isNull() {
        return instance == null;
    }

}