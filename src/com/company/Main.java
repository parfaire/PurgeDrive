package com.company;

public class Main {

    public static void main(String[] args) {
        //get the arguments
        //only 2 arguments available -(-h | -src)
        try {
            if (args.length > 0) {
                if (args[0].equals("-h")) {
                    System.out.println("Define the source folder to do purge.\n" +
                            "-s <source_target>\n" +
                            "ex: -s \"/PathA/PathB/PharmCIS\"\n" +
                            "target folder will be located in the same level hierarchy as source folder");
                } else if (args[0].equals("-s")) {
                    if (args.length > 2) {
                        System.out.println("More than 2 parameters detected, ignoring the rest - except the first two");
                    }
                    new Purge(args[1]);
                } else
                    System.out.println("Please give some arguments. -h for some help");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
