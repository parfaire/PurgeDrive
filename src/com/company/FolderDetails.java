package com.company;

public class FolderDetails {
    int count;
    long size;

    public FolderDetails(){
        this.count=0;
        this.size=0;
    }

    public FolderDetails(int count,long size){
        this.count=count;
        this.size=size;
    }

    public void plus(FolderDetails folderDetails){
        count += folderDetails.getCount();
        size += folderDetails.getSize();
    }

    public void substract(FolderDetails folderDetails){
        count -= folderDetails.getCount();
        size -= folderDetails.getSize();
    }

    public void addOneForFolder(){ //plus 1 to count the folder itself
        count += 1;
    }

    public int getCount() {
        return count;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return  "Total Files:"+count+
                "\nSize:"+size+" bytes"+
                "\nSize in GB:"+size/1024/1024/1024;
    }
}
