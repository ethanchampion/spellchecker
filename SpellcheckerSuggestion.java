
// Import the required scanner and IO modules
import java.util.Scanner;
import java.io.*;

// Java class to spell check a file based on user input against a dictionary and give
// suggestions using Levenshtein distance from the word
class SpellcheckerSuggestion {
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

    static int computeLDistance(CharSequence lhs, CharSequence rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];
        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;
        for (int i = 1; i <= lhs.length(); i++) {
            for (int j = 1; j <= rhs.length(); j++) {
                boolean comparison = (lhs.charAt(i - 1) == rhs.charAt(j - 1));
                if (comparison == true)
                    distance[i][j] = Math.min(Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                            distance[i - 1][j - 1]);
                else
                    distance[i][j] = Math.min(Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                            distance[i - 1][j - 1] + 1);
            }
        }
        return distance[lhs.length()][rhs.length()];
    }

    static void createSuggestions(String word, String filePath) throws FileNotFoundException {
        System.out.println("The word: '" + word + "' was not found in dictionary.");
        String dictPath = System.getProperty("user.dir") + "\\Dictionary.txt";
        Scanner dictScan = new Scanner(new File(dictPath));
        String output = "";
        int count = 0;
        String[] wordList = new String[10];
        while (dictScan.hasNext()) {
            String dictWord = dictScan.next();
            int distance = computeLDistance(word, dictWord);
            if (distance == 1 && count < wordList.length)
                wordList[count++] = dictWord;
        }
        for (int i = 0; i < wordList.length; i++) {
            if (wordList[i] == null)
                continue;
            else
                output += "(" + i + ") " + wordList[i] + "\n";
        }
        System.out.println("Below are the suggestions for alternative words:");
        System.out.println(output);
        System.out.println("Input the number of the alternative or input -1 if none match:");
        int userInput = scan.nextInt();
        if (userInput == -1) {
            if (redirectUser(word)) {
                System.out.println("Enter what you would like it to be changed to: ");
                String newWord = scan.next().trim();
                try {
                    replaceWord(filePath, word, newWord);
                } catch (Exception IOException) {
                    System.out.println(IOException);
                }
            } else {
                try {
                    addToDict(dictPath, word);
                } catch (Exception IOException) {
                    System.out.println(IOException);
                }
            }
        } else {
            try {
                replaceWord(filePath, word, wordList[userInput]);
            } catch (Exception IOException) {
                System.out.println(IOException);
            }
        }
        dictScan.close();
    }

    // Procedure to check dictionary given the file path, throws exception if not
    // found
    static void checkDictionary(String filePath) throws IOException {
        // Creates the path to the file
        String path = System.getProperty("user.dir") + "\\" + filePath;
        // Creates a scanner for the file
        Scanner fileScan = new Scanner(new File(path));
        // Creates the path to the dictionary
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
            // If the word is not in the dictionary create suggestion
            if (wordFound == false)
                createSuggestions(userWord, filePath);
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