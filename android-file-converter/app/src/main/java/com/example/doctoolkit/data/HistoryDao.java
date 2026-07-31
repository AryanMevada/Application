package com.example.doctoolkit.data;
import androidx.room.*; import java.util.*;
@Dao public interface HistoryDao { @Query("SELECT * FROM HistoryItem ORDER BY createdAt DESC LIMIT 10") List<HistoryItem> lastTen(); @Insert void insert(HistoryItem item); @Query("DELETE FROM HistoryItem WHERE id NOT IN (SELECT id FROM HistoryItem ORDER BY createdAt DESC LIMIT 10)") void trim(); }
