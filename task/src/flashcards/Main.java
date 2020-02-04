package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class DefData {
    String def;
    int errors;

    public DefData(String def, int errors) {
        this.def = def;
        this.errors = errors;
    }
}

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static LinkedHashMap<String, DefData> cardsDefs = new LinkedHashMap<>();
    static List<String> log = new ArrayList<>();
    static String importFileName;
    static String exportFileName;

    private static void print(String str) {
        log.add(str);
        System.out.println(str);
    }

    private static String read() {
        String str = scanner.nextLine();
        log.add(str);
        return str;
    }

    public static String getKeyByFirstValue(LinkedHashMap<String, DefData> hashMap, String value) {
        Object[] defData = hashMap.values().toArray();

        for (int i = 0; i < defData.length; i++) {
            if (((DefData) defData[i]).def.equals(value)) {
                return (String) hashMap.keySet().toArray()[i];
            }
        }

        return null;
    }

    public static void add() {
        print("The card:");
        String card = read();

        DefData loggedDef = cardsDefs.get(card);
        if (loggedDef != null) {
            print("The card \"" + card + "\" already exists.\n");
            return;
        }

        print("The definition of the card:");
        String def = read();

        String cardByDef = getKeyByFirstValue(cardsDefs, def);
        if (cardByDef != null) {
            print("The definition \"" + def + "\" already exists.\n");
            return;
        }

        cardsDefs.put(card, new DefData(def, 0));
        print("The pair (\"" + card + "\":\"" + def + "\") has been added.\n");
    }

    public static void remove() {
        print("The card:");
        String card = read();

        if (cardsDefs.get(card) == null) {
            print("Can't remove \"" + card + "\": there is no such card.\n");
        } else {
            cardsDefs.remove(card);
            print("The card has been removed.\n");
        }

    }

    public static void imprt() {
        print("File name:");
        String fileName = read();
        doImport(fileName);
    }

    public static void doImport(String fileName){
        File file = new File(fileName);
        int loadedNum = 0;
        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNext()) {
                String card = fileScanner.nextLine();
                String def = fileScanner.nextLine();
                int errs = Integer.parseInt(fileScanner.nextLine());
                cardsDefs.put(card, new DefData(def, errs));
                loadedNum++;
            }
        } catch (FileNotFoundException e) {
            print("File not found.\n");
            return;
        }

        print(loadedNum + " cards have been loaded.\n");
    }

    public static void export() {
        print("File name:");
        String fileName = read();
        doExport(fileName);
    }

    public static void doExport(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(fileName, false)) {
            for (Map.Entry<String,DefData> entry : cardsDefs.entrySet()) {
                writer.write(entry.getKey());
                writer.write('\n');
                writer.write(entry.getValue().def);
                writer.write('\n');
                writer.write(String.valueOf(entry.getValue().errors));
                writer.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        print(cardsDefs.size() + " cards have been saved.\n");
    }

    public static void ask() {
        print("How many times to ask?");
        int timesToAks = Integer.parseInt(read());

        Random random = new Random();
        for (int i = 0; i < timesToAks; i++) {
            List<String> keysAsArray = new ArrayList<String>(cardsDefs.keySet());
            String card = keysAsArray.get(random.nextInt(keysAsArray.size()));
            String def = cardsDefs.get(card).def;

            print("Print the definition of \"" + card + "\":");
            String inputDef = read();

            String actualCard = (String) getKeyByFirstValue(cardsDefs, inputDef);
            if (def.equals(inputDef)) {
                print("Correct answer.\n");
            } else if (actualCard != null) {
                cardsDefs.get(card).errors++;
                print("Wrong answer. The correct one is \"" + def + "\"," +
                        " you've just written the definition of \"" + actualCard + "\".\n");
            } else {
                cardsDefs.get(card).errors++;
                print("Wrong answer. The correct one is \"" + def + "\".\n");
            }
        }
    }

    public static void hardestCard() {
        int maxErrors = 0;
        for (Map.Entry<String,DefData> entry : cardsDefs.entrySet()) {
            int errors = entry.getValue().errors;
            if (errors > maxErrors) {
                maxErrors = errors;
            }
        }

        List<String> hardestCards = new ArrayList<>();
        for (Map.Entry<String,DefData> entry : cardsDefs.entrySet()) {
            int errors = entry.getValue().errors;
            if (errors == maxErrors) {
                hardestCards.add(entry.getKey());
            }
        }

        if (maxErrors == 0) {
            print("There are no cards with errors.\n");
        } else if (hardestCards.size() == 1) {
            print("The hardest card is \"" + hardestCards.get(0) + "\". You have " + maxErrors + " errors answering it.\n");
        } else {
            StringBuilder hardestCardsStr = new StringBuilder();
            for (int i = 0; i < hardestCards.size(); i++) {
                hardestCardsStr.append("\"").append(hardestCards.get(i)).append("\"");
                if (i < hardestCards.size() - 1) {
                    hardestCardsStr.append(", ");
                }
            }
            print("The hardest cards are " + hardestCardsStr + ". You have " + maxErrors + " errors answering them.\n");
        }
    }

    public static void resetStats() {
        for (Map.Entry<String,DefData> entry : cardsDefs.entrySet()) {
            entry.getValue().errors = 0;
        }
        System.out.println("Card statistics has been reset.\n");
    }

    public static void log() {
        print("File name:");
        String fileName = read();

        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(fileName, false)) {
            for (String logLine : log) {
                writer.write(logLine);
                writer.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("The log has been saved.\n");
    }

    private static void start() {
        if (importFileName != null){
            doImport(importFileName);
        }
    }

    public static void exit() {
        print("Bue bye!");
        if (exportFileName != null){
            doExport(exportFileName);
        }
        System.exit(0);
    }

    static void parseCommandLineArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-import")){
                importFileName = args[i + 1];
            } else if (args[i].equals("-export")) {
                exportFileName = args[i + 1];
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLineArgs(args);
        start();
        while (true) {
            print("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String command = read();
            switch (command) {
                case "add":
                    add();
                    break;
                case "remove":
                    remove();
                    break;
                case "import":
                    imprt();
                    break;
                case "export":
                    export();
                    break;
                case "ask":
                    ask();
                    break;
                case "hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                case "log":
                    log();
                    break;
                case "exit":
                    exit();
                    break;
            }
        }
    }
}
