package com.example.vnus.ui.auth.notificacao;

import java.util.List;
import java.util.Map;

public class OneSignalNotification {
    String app_id;
    Map<String, String> contents;
    Map<String, String> headings;
    List<Map<String, Object>> filters;

    public OneSignalNotification(String app_id, Map<String, String> contents, Map<String, String> headings, List<Map<String, Object>> filters) {
        this.app_id = app_id;
        this.contents = contents;
        this.headings = headings;
        this.filters = filters;
    }
}