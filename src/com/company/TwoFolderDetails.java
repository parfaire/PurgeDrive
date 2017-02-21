package com.company;

public class TwoFolderDetails {
    private FolderDetails newFolderDetails;
    private FolderDetails oldFolderDetails;

    public TwoFolderDetails(){
        newFolderDetails = new FolderDetails();
        oldFolderDetails = new FolderDetails();
    }

    public TwoFolderDetails(FolderDetails newFolderDetails, FolderDetails oldFolderDetails){
        this.newFolderDetails = newFolderDetails;
        this.oldFolderDetails = oldFolderDetails;
    }

    public FolderDetails getNewFolderDetails() {
        return newFolderDetails;
    }

    public FolderDetails getOldFolderDetails() {
        return oldFolderDetails;
    }

    public void plus(TwoFolderDetails twoFolderDetails){
        newFolderDetails.plus(twoFolderDetails.getNewFolderDetails());
        oldFolderDetails.plus(twoFolderDetails.getOldFolderDetails());
    }

    public void substractNew(FolderDetails fd){
        newFolderDetails.substract(fd);
    }

    public void addOneForFolder(){
        newFolderDetails.addOneForFolder();
        oldFolderDetails.addOneForFolder();
    }

    @Override
    public String toString() {
        return  "--Before Purge--\n"+oldFolderDetails.toString()+
                "\n\n==After Purge==\n"+newFolderDetails.toString();
    }
}
