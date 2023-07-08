// Import the required scanner and IO modules
import java.util.Scanner;
import java.io.*;

// Java class to spell check a file based on user input against a dictionary
public class Spellchecker {
    // Create a global static scanner that will ask the user for input
    static Scanner scan = new Scanner(System.in);

    // A procedure to add to the dictionary file, throws exception if Dictionary not
    // found
    static void addToDict(String dictPath, String userWord) throws IOException {
        // Creates a file writer of the dictionary file in append mode
        FileWriter updater = new FileWriter(new File(dictPath), true);
        // Append the word and save changes
        updater.append("\n" + userWord).flush();
        // Close the file
        updater.close();
    }

    // Procedure to replace userWord with newWord in filePath, throws errors if
    // fails
    static void replaceWord(String filePath, String userWord, String newWord)
            throws FileNotFoundException, IOException {
        // Create a file given the path
        File file = new File(filePath);
        // Create a buffered reader using a file reader of the file
        BufferedReader reader = new BufferedReader(new FileReader(file));
        // Create empty strings
        String line = "", newText = "";
        // Set the line to the next line and if its not null
        while ((line = reader.readLine()) != null) {
            // Create an array from the line split with spaces
            String[] words = line.split("\\s+");
            // Create an empty string for the correct line
            String newLine = "";
            // For each word in the line
            for (int i = 0; i < words.length; i++) {
                // If the word is the same as the one to replace, replace it in the new line
                if (words[i].equals(userWord))
                    newLine += newWord + " ";
                // Otherwise use the old word
                else
                    newLine += words[i] + " ";
            }
            newText += newLine + "\r\n"; // Add a new line for each line
        }
        reader.close(); // Close the reader
        // Create a file, write to the file and close it
        FileWriter updater = new FileWriter(file, false);
        updater.write(newText);
        updater.flush();
        updater.close();
    }

    // Function to redirect the user
    static boolean redirectUser(String userWord) {
        // Output the options to the user
        System.out.println("The word: '" + userWord
                + "' was not found in dictionary.\nWould you like to (a)dd to dictionary or (c)hange: ");
        // Save their decision
        String decision = scan.next().trim();
        // Return the corresponding true/false
        if (decision.equals("a") || decision.equals("A"))
            return false;
        else
            return true;
    }

    // Procedure to check dictionary against words in file and throws IOException if
    // file not found
    static void checkDictionary(String filePath) throws IOException {
        // Create the path to file
        String path = System.getProperty("user.dir") + "\\" + filePath;
        // Create a scanner to the file (cannot use main scanner)
        Scanner fileScan = new Scanner(new File(path));
        // Create the path to the dictionary
        String dictPath = System.getProperty("user.dir") + "\\Dictionary.txt";
        // While there is a word in the file
        while (fileScan.hasNext()) {
            // Save the word
            String userWord = fileScan.next();
            // Create a separate scanner to the dictionary
            Scanner dictScan = new Scanner(new File(dictPath));
            // Set the current word to not found
            boolean wordFound = false;
            // While there is a word in the dictionary
            while (dictScan.hasNext()) {
                // Save the word
                String dictWord = dictScan.next();
                // If the words are the same set the word to found
                if (userWord.equals(dictWord))
                    wordFound = true;
            }
            // If the word was not in the dictionary
            if (wordFound == false) {
                // Store the output of the redirect (which option the user chooses)
                boolean changeWord = redirectUser(userWord);
                // If they want to change the word
                if (changeWord) {
                    // Output the options to the user
                    System.out.println("Enter what you would like it to be changed to: ");
                    // Replace the word with the user's input by calling replaceWord procedure
                    replaceWord(filePath, userWord, scan.next().trim());
                } else
                    addToDict(dictPath, userWord); // If they want to add to dictionary call addToDict
            }
            dictScan.close(); // Close the dictionary scanner
        }
        fileScan.close(); // Close the file scanner
    }

    // Main Method
    public static void main(String[] args) {
        // Ask user to input name of file
        System.out.println("Enter the name of the file to spellcheck: ");
        // Obtain the input and trim extra white-spaces
        String file = scan.next().trim();
        try {
            // Try checking dictionary given the file to check
            checkDictionary(file);
            // If successful then output the file is checked
            System.out.println("The file is now spell checked and saved.");
        } catch (Exception IOException) {
            // If an IOException occurs output the exception
            System.out.println(IOException);
        } finally {
            // Regardless of outcomes close the scanner to save memory
            scan.close();
        }
    }
}