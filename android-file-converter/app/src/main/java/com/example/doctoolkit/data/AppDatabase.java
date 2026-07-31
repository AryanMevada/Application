package com.example.doctoolkit.data;
import android.content.Context; import androidx.room.*;
@Database(entities={HistoryItem.class}, version=1) public abstract class AppDatabase extends RoomDatabase { public abstract HistoryDao historyDao(); private static volatile AppDatabase db; public static AppDatabase get(Context c){ if(db==null) synchronized(AppDatabase.class){ if(db==null) db= Room.databaseBuilder(c.getApplicationContext(), AppDatabase.class, "doc_toolkit.db").build(); } return db; } }
