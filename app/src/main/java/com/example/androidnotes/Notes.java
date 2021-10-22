package com.example.androidnotes;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Date;

public class Notes implements Serializable {
    private String title;
    private String description;
    private Date lastUpdateTime;

    public Notes(String title, String description, Date lastUpdateTime) {
        this.title = title;
        this.description = description;
        this.lastUpdateTime = lastUpdateTime;
    }

    public Notes() { }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("title").value(title);
            jsonWriter.name("description").value(description);
            jsonWriter.name("lastUpdateTime").value(lastUpdateTime.toString());
            jsonWriter.endObject();
            jsonWriter.close();
            return stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }
}
