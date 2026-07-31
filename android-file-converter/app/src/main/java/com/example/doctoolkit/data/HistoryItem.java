package com.example.doctoolkit.data;
import androidx.room.Entity; import androidx.room.PrimaryKey;
@Entity public class HistoryItem { @PrimaryKey(autoGenerate=true) public long id; public String tool; public String sourceName; public String outputName; public String outputPath; public long createdAt; public HistoryItem(String tool,String sourceName,String outputName,String outputPath,long createdAt){this.tool=tool;this.sourceName=sourceName;this.outputName=outputName;this.outputPath=outputPath;this.createdAt=createdAt;} }
