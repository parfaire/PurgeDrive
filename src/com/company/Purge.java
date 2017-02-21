package com.company;

import java.io.*;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Purge {
    private String[] regex;
    private static final int NUMBER_OF_FILTERS = 3;
    private File src,quarantine,changelog;
    private long matchingCount;
    private Pattern[] patterns;

    /**
     * Constructor to validate the source existence, initialise regex pattern and start the execution.
     * @param s source folder (absolute path+folder name), return error if path does not exist.
     */
    public Purge(String s) {
        src = Paths.get(s).toFile();
        if(src.exists()){
            matchingCount = 0;

            //initialise the pattern
            regex = new String[]{"\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]{1}[0-9]{1})\\.)(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){2}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b", //IP_ADDRESS_WITH_NO_SINGLE_DIGIT_AT_FIRST_0-255.
                                 "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$", //domain
                                 "(password :|password =|password:|password=)"}; //password
            patterns = new Pattern[NUMBER_OF_FILTERS];
            for(int i=0;i<NUMBER_OF_FILTERS;i++){
                patterns[i] = Pattern.compile(regex[i]);
            }

            //execute
            execute();

        }else{
            System.err.println("Source Path/Folder does not exist :(");
        }
    }

    /**
     * Execute the purge process - with time being recorded.
     * Firstly kicks off preparePurging() function then followed by purgeRecursively(src), and ended by printing the logs.
     */
    public void execute(){
        long startTime,endTime,duration;
        TwoFolderDetails details;
        try {
            //main execution
            startTime = System.nanoTime();
            preparePurging();
            details = purgeRecursively(src);
            details.addOneForFolder(); //+1 to count the root folder
            endTime = System.nanoTime();
            duration = (endTime - startTime) / 1000 / 1000 / 1000;  //divide 10^9 from nanosecond to second

            //print the logs
            System.out.println("===============================================\nNumber of files that gets quarantined:"
                                +matchingCount+"\n");
            System.out.println(details);
            System.out.println("\n\n**Duration: "+duration+" s");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Prepare the purging process. Creating quarantine folder and changelog file in the new folder.
     * Folder naming ex: Source folder = /home/Hello, will create /home/Hello_Purge
     * Also redirect the system output to changelog.
     * @throws IOException
     */
    private void preparePurging() throws IOException{
        quarantine = new File(src.getParent()+"/"+src.getName()+"_Purge/Quarantine");
        changelog = new File(src.getParent()+"/"+src.getName()+"_Purge/Purge-Changelog.txt");
        quarantine.mkdirs();
        changelog.createNewFile();
        System.setOut(new PrintStream(changelog)); //redirect set out to changelog file
    }

    /**
     * Recursively go through every single folder and file.
     * Finding the matches using regex pattern, either find 1 or 0 match.
     * Log the match, its position in line number, and the file path.
     * When it matches, move the file into quarantine.
     * @param src the source folder that needs to be purged.
     * @return TwoFolderDetails - the folder details (folder size,files count) of before-and-after purge.
     * @throws IOException
     */
    private TwoFolderDetails purgeRecursively(File src) throws IOException{
        TwoFolderDetails result = new TwoFolderDetails();
        TwoFolderDetails recursiveResult;
        FolderDetails fileDetails;
        BufferedReader reader;
        Matcher matcher;
        String lineText,lineTextMatch="",match="";
        long lineNumber;
        boolean flag;
        int i;

        //recursively calculate folder size and count the files
        for(File file : src.listFiles()){
            if(file.isDirectory()){
                recursiveResult = purgeRecursively(file);
                result.plus(recursiveResult);
                result.addOneForFolder();
            }else if(!file.isHidden()){ //only count the not hidden files - to ignore file system metadata file such as DS_STORE
                //update the details
                fileDetails = new FolderDetails(1,file.length());
                result.plus(new TwoFolderDetails(fileDetails,fileDetails));
                flag =true; //only need 1 match to quarantine the file

                //read the file
                lineNumber = 0;
                reader = new BufferedReader(new FileReader(file));
                while( (lineText = reader.readLine()) != null  && flag){
                    lineNumber++;
                    //find the match for every single filter
                    i=0;
                    while(i<NUMBER_OF_FILTERS && flag){
                        matcher = patterns[i].matcher(lineText);
                        // Find the match
                        while (matcher.find()) {
                            flag=false;
                            match = matcher.group();
                            lineTextMatch = lineText;
                        }
                        i++;
                    }
                }

                //if a match was found
                if(!flag){
                    // Get the matching string
                    matchingCount++;
                    System.out.println( matchingCount+".Match: " + match + "\nFrom:" + lineTextMatch +
                            "\nLocation:" + file.getAbsolutePath() +
                            "\nLine:" + lineNumber + "\n");
                    result.substractNew(fileDetails);
                    //move file to quarantine
                    moveToQuarantine(file);
                }
                reader.close();
            }
        }
        return result;
    }

    /**
     * Moving file to quarantine. It preserves the original folder structure into quarantine section.
     * @param sourceFile file that wants to be quarantined.
     */
    private void moveToQuarantine(File sourceFile){
        //sourcefile ex : /PharmCIS/A/B/a.txt
        String filePathName = sourceFile.getParent(); // /PharmCIS/A/B/
        String filePathWithoutRootName = filePathName.replace(src.getAbsolutePath(),""); // /A/B/
        String targetPathName = quarantine.getAbsolutePath()+filePathWithoutRootName; // /PharmCIS_Purge/Quarantine/A/B/

        //create in the quarantine the same folder structure
        File targetPath = new File(targetPathName);
        targetPath.mkdirs();
        sourceFile.renameTo(new File(targetPathName+"/"+sourceFile.getName()));
    }

}
