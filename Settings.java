import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class Settings {
  int hourM; // morning hour placeholder
  int minuteM; // morning minute placeholder
  int hourN; // Night hour placeholder
  int minuteN; // Night minute placeholder
  File cfgPath = new File("wpcfg/config.txt"); // cfg path placeholder
  boolean cfgAvailable; // if cfg is available or not
  String fst_wp_path; // first wallpaper path
  String scd_wp_path; // second wallpaper path
  List<String> configLines = new ArrayList<>(); // config lines list

  Settings() {
    this.configLines = scanFile();
    this.cfgAvailable = cfgCheck(configLines);
  }

  List<String> scanFile(){
    List<String> freshLines = new ArrayList<>();
      
    // reading the config
    try (Scanner scanner = new Scanner(this.cfgPath)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (!line.isBlank()) {
          //System.out.println("[DEBUG]: "+ line);
          freshLines.add(line);
        }
      }

      //[DEBUG] to see inside of the List.
      //for(int i = 0; i < configLines.size(); i++){
      //  System.out.println("[INSIDE LIST] " + configLines.get(i));
      //}

    } catch (IOException e) {
      e.printStackTrace();
    }

    return freshLines;
  }

private void autoWpSetup() {
    try {
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("linux")) {
            System.out.println("Automatic background setup is currently supported only on Linux.");
            return;
        }

        String userHome = System.getProperty("user.home");
        File systemdDir = new File(userHome + "/.config/systemd/user");
        if (!systemdDir.exists()) {
            systemdDir.mkdirs();
        }

        File serviceFile = new File(systemdDir, "wpontime.service");
        try (PrintWriter writer = new PrintWriter(serviceFile)) {
            writer.println("[Unit]");
            writer.println();
            writer.println("[Service]");
            writer.println("Type=oneshot");
            writer.println("ExecStart=/usr/local/bin/wpontime");
        }

        File timerFile = new File(systemdDir, "wpontime.timer");
        try (PrintWriter writer = new PrintWriter(timerFile)) {
            writer.println("[Unit]");
            writer.println();
            writer.println("[Timer]");
            writer.println("OnActiveSec=1sec");
            writer.println("OnUnitActiveSec=1min");
            writer.println();
            writer.println("[Install]");
            writer.println("WantedBy=timers.target");
        }

        ProcessBuilder pbReload = new ProcessBuilder("systemctl", "--user", "daemon-reload");
        ProcessBuilder pbEnable = new ProcessBuilder("systemctl", "--user", "enable", "--now", "wpontime.timer");
        
        pbReload.start().waitFor();
        int exitCode = pbEnable.start().waitFor();

        if (exitCode == 0) {
            System.out.println("Successfully configured and started automatic background systemd timer.");
        } else {
            System.out.println("Files were created, but failed to start systemd timer automatically.");
        }

    } catch (Exception e) {
        System.out.println("Error setting up systemd automation: " + e.getMessage());
    }
}

  void setup(){
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter the starting hour for the first wallpaper period (2 digits)");
    System.out.print("-> ");
    this.hourN = Integer.parseInt(scanner.nextLine());
    System.out.println("Enter the starting minute for the first wallpaper period (2 digits)");
    System.out.print("-> ");
    this.minuteN = Integer.parseInt(scanner.nextLine());
    System.out.println("Enter the path of wallpaper that you want to use in this period.");
    System.out.print("-> ");
    this.fst_wp_path = scanner.nextLine();

    System.out.println("Enter the ending hour for the first wallpaper period (2 digits)");
    System.out.print("-> ");
    this.hourM = Integer.parseInt(scanner.nextLine());
    System.out.println("Enter the ending minute for the first wallpaper period (2 digits)");
    System.out.print("-> ");
    this.minuteM = Integer.parseInt(scanner.nextLine());
    System.out.println("Enter the path of wallpaper that you want to use after this period.");
    System.out.print("-> ");
    this.scd_wp_path = scanner.nextLine();

    List<String> hourupdatedList = updateHours(this.hourN, this.minuteN, this.hourM, this.minuteM);
    List<String> wpupdatedList = updateWPs(hourupdatedList);

    System.out.println("Do you want wpontime run itself automatically? (Y/n)");
    System.out.print("-> ");
    String automatic  = scanner.nextLine();
    if (automatic.isBlank()) {
      autoWpSetup();
    }else if(automatic.toLowerCase().equals("y")){
      autoWpSetup();
    }else if(automatic.toLowerCase().equals("n")){
      System.out.println("Automatic setup skipped by user.");
    }else{
      System.out.println("Invalid option");
      scanner.close();
      return;
    }

    overwrite(wpupdatedList);
    scanner.close();
  }

  void overwrite(List<String> lines){
try {
    // freshLines içindeki tüm güncel satırları alıp cfgPath'in üzerine yazar
    Files.write(cfgPath.toPath(), lines);
    System.out.println("[SUCCESS] File has been updated!");
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  List<String> updateWPs(List<String> freshLines){
    List<String> updatedLinesList = new ArrayList<>();

    for(int i = 0; i < freshLines.size(); i++){
      String currentLine = freshLines.get(i);
      if(freshLines.get(i).contains("[HOURN]")){
        updatedLinesList.add(freshLines.get(i));

        //System.out.println("[DEBUG] updatedLine: " + updatedLine);
      }else if(freshLines.get(i).contains("[MINUTEN]")){

        updatedLinesList.add(freshLines.get(i));

        //System.out.println("[DEBUG] updatedLine: " + updatedLine);

      }else if(freshLines.get(i).contains("[HOURM]")){

        updatedLinesList.add(freshLines.get(i));

        //System.out.println("[DEBUG] updatedLine: " + updatedLine);

      }else if(freshLines.get(i).contains("[MINUTEM]")){

        updatedLinesList.add(freshLines.get(i));

        //System.out.println("[DEBUG] updatedLine: " + updatedLine);


      }else if(freshLines.get(i).contains("[WP1]")){
        int start = currentLine.indexOf("{");
        int end = currentLine.indexOf("}");
        String updatedLine = currentLine.substring(0, start + 1) + this.fst_wp_path + currentLine.substring(end);
        updatedLinesList.add(updatedLine);
        //System.out.println("[DEBUG] updatedLine: " + updatedLine);
      }else if(freshLines.get(i).contains("[WP2]")){
        int start = currentLine.indexOf("{");
        int end = currentLine.indexOf("}");
        String updatedLine = currentLine.substring(0, start + 1) + this.scd_wp_path + currentLine.substring(end);
        updatedLinesList.add(updatedLine);
        //System.out.println("[DEBUG] updatedLine: " + updatedLine);
      }

    }

    for(int j = 0; j < updatedLinesList.size(); j++){
      System.out.println("[DEBUG] List containers: " + updatedLinesList.get(j));
    }
    System.out.println("[DEBUG] updatedList size: " + updatedLinesList.size());

    return updatedLinesList;
  }

  List<String> updateHours(int hourN, int minuteN, int hourM, int minuteM){
    //int hourM, int minuteM, int minuteN
    List<String> freshLines = scanFile();
    List<String> updatedLinesList = new ArrayList<>();

    for(int i = 0; i < freshLines.size(); i++){
      String currentLine = freshLines.get(i);
      if(freshLines.get(i).contains("[HOURN]")){
        int start = currentLine.indexOf("{");
        int end = currentLine.indexOf("}");
        String updatedLine = currentLine.substring(0, start + 1) + hourN + currentLine.substring(end);
        updatedLinesList.add(updatedLine);

        //System.out.println("[DEBUG] updatedLine: " + updatedLine);
      }else if(freshLines.get(i).contains("[MINUTEN]")){

        int start = currentLine.indexOf("{");
        int end = currentLine.indexOf("}");
        String updatedLine = currentLine.substring(0, start + 1) + minuteN + currentLine.substring(end);
        updatedLinesList.add(updatedLine);

        //System.out.println("[DEBUG] updatedLine: " + updatedLine);

      }else if(freshLines.get(i).contains("[HOURM]")){

        int start = currentLine.indexOf("{");
        int end = currentLine.indexOf("}");
        String updatedLine = currentLine.substring(0, start + 1) + hourM + currentLine.substring(end);
        updatedLinesList.add(updatedLine);

        //System.out.println("[DEBUG] updatedLine: " + updatedLine);

      }else if(freshLines.get(i).contains("[MINUTEM]")){

        int start = currentLine.indexOf("{");
        int end = currentLine.indexOf("}");
        String updatedLine = currentLine.substring(0, start + 1) + minuteM + currentLine.substring(end);
        updatedLinesList.add(updatedLine);

        //System.out.println("[DEBUG] updatedLine: " + updatedLine);


      }else if(freshLines.get(i).contains("[WP1]")){
        updatedLinesList.add(freshLines.get(i));
        //System.out.println("[DEBUG] updatedLine: " + updatedLine);
      }else if(freshLines.get(i).contains("[WP2]")){
        updatedLinesList.add(freshLines.get(i));
        //System.out.println("[DEBUG] updatedLine: " + updatedLine);
      }

    }

    return updatedLinesList;

    //for(int j = 0; j < updatedLinesList.size(); j++){
    //  System.out.println("[DEBUG] List containers: " + updatedLinesList.get(j));
    //}
    //System.out.println("[DEBUG] updatedList size: " + updatedLinesList.size());
  }

  void readcfg(List<String> lines){
    // [DEBUG] for if the file line amount matches with actual file line amount.
    // [DEBUG] System.out.println(lines.size());

    //[READ DEBUG]
    for(int i = 0; i < lines.size(); i++){
      //System.out.println("[READ DEBUG]: " + lines.get(i));
    }
    
    //reading everyline and takes the information from config file.
    for(int i = 0; i < lines.size(); i++){ 
      String currentLine = lines.get(i);
      if(!currentLine.isBlank()){ //to not get out of the for the for range
        int startIndex = currentLine.indexOf('[') + 1; //plus one cuz I want just the info's itself.
        int endIndex = currentLine.indexOf(']');
        String settingsType = currentLine.substring(startIndex, endIndex);
        //System.out.println("[DEBUG] SettingsType: " + settingsType);

        //whole cathegorizing stuff for infos
        if(settingsType.equals("HOURM")){
          int valueStart = currentLine.indexOf('{') + 1;
          int ValueEnd = currentLine.indexOf('}');
          String valueString = currentLine.substring(valueStart, ValueEnd);
          if(valueString.isBlank()){
            System.err.println("[ERROR] config file is corrupted!");
            System.exit(1);
          }
          int valueInt = Integer.parseInt(valueString);
          this.hourM = valueInt;
          //System.out.println("[DEBUG VALUEINT]: " + valueInt);
          //System.out.println("[DEBUG THIS HOURM]: " + this.hourM);

        }else if(settingsType.equals("HOURN")){
          int valueStart = currentLine.indexOf('{') + 1;
          int ValueEnd = currentLine.indexOf('}');
          String valueString = currentLine.substring(valueStart, ValueEnd);
          if(valueString.isBlank()){
            System.err.println("[ERROR] config file is corrupted!");
            System.exit(1);
          }

          int valueInt = Integer.parseInt(valueString);
          this.hourN = valueInt;
          //System.out.println("[DEBUG VALUEINT]: " + valueInt);
          //System.out.println("[DEBUG THIS HOURN]: " + this.hourN);

        }else if(settingsType.equals("MINUTEM")){
          int valueStart = currentLine.indexOf('{') + 1;
          int ValueEnd = currentLine.indexOf('}');
          String valueString = currentLine.substring(valueStart, ValueEnd);
          if(valueString.isBlank()){
            System.err.println("[ERROR] config file is corrupted!");
            System.exit(1);
          }

          int valueInt = Integer.parseInt(valueString);
          this.minuteM = valueInt;
          //System.out.println("[DEBUG VALUEINT]: " + valueInt);
          //System.out.println("[DEBUG THIS MINUTE]: " + this.minuteM);

        }else if(settingsType.equals("MINUTEN")){
          int valueStart = currentLine.indexOf('{') + 1;
          int ValueEnd = currentLine.indexOf('}');
          String valueString = currentLine.substring(valueStart, ValueEnd);
          if(valueString.isBlank()){
            System.err.println("[ERROR] config file is corrupted!");
            System.exit(1);
          }

          int valueInt = Integer.parseInt(valueString);
          this.minuteN = valueInt;
          //System.out.println("[DEBUG VALUEINT]: " + valueInt);
          //System.out.println("[DEBUG THIS MINUTE]: " + this.minuteN);

        } else if(settingsType.equals("WP1")) {
          int valueStart = currentLine.indexOf('{') + 1;
          int ValueEnd = currentLine.indexOf('}');
          String valueString = currentLine.substring(valueStart, ValueEnd);
          //System.out.println("[DEBUG VALUESTRING]: " + valueString);

          if(valueString.isBlank()){
            System.err.println("[ERROR] config file is corrupted!");
            System.exit(1);
          }

          this.fst_wp_path = valueString;
          //System.out.println("[DEBUG THIS WP1]: " + this.fst_wp_path);

        } else if(settingsType.equals("WP2")) {
          int valueStart = currentLine.indexOf('{') + 1;
          int ValueEnd = currentLine.indexOf('}');
          String valueString = currentLine.substring(valueStart, ValueEnd);

          if(valueString.isBlank()){
            System.err.println("[ERROR] config file is corrupted!");
            System.exit(1);
          }

          //System.out.println("[VALUESTRING]: " + valueString);
          this.scd_wp_path = valueString;
        }
      }
    }
  }


  boolean cfgCheck(List<String> lines) {
    if (cfgPath.exists()) {
      readcfg(lines);
      return true;
    }else{
      return false;
    }
  }

}
